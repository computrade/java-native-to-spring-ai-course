package com.computrade.course.spring.ai.vector.service;

import com.computrade.course.spring.ai.vector.util.CustomRegexDocumentSplitter;
import com.opencsv.CSVReaderHeaderAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDBService {

    String SOURCE_KEY_WORD = "source";

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    @Value("classpath:data/courses_dataset.csv")
    private Resource csvResource;

    @Value("classpath:data/spring_ai_course_syllabus.pdf")
    private Resource pdfResource;

    public Map<String,EmbeddingResponse> embed(String prompt) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(prompt));
        return Map.of("embedding", embeddingResponse);
    }

    public void ingestCoursesToVectorStore() {
        List<Document> documents = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8));
             CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(bufferedReader)) {

            Map<String, String> values;
            while ((values = csvReader.readMap()) != null) {
                String id = values.get("ID");
                String courseName = values.get("Course Name");
                String category = values.get("Category");
                String description = values.get("Description");
                String rating = values.get("Rating");
                String noOfStudents = values.get("No of Students");
                String totalHours = values.get("Total Course Hours");

                // Create content for the embedding
                String content = String.format("Course: %s. Category: %s. Description: %s",
                        courseName, category, description);

                // Create metadata for the Vector DB.
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(SOURCE_KEY_WORD, Objects.requireNonNull(csvResource.getFilename()));
                metadata.put("course_id", id);
                metadata.put("category", category);
                metadata.put("rating", Double.parseDouble(rating));
                metadata.put("students", Integer.parseInt(noOfStudents));
                metadata.put("hours", Double.parseDouble(totalHours));

                Document document = new Document(content, metadata);
                documents.add(document);
            }

            // Push all documents to the Vector Store.
            if (!documents.isEmpty()) {
                vectorStore.accept(documents);
                log.info("Successfully ingested {} courses using OpenCSV.", documents.size());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV and upload to vector store via OpenCSV", e);
        }
    }

    public String chat(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }

    public String chatRag(String convId, String prompt) {

        SearchRequest searchRequest = SearchRequest.builder().query(prompt).topK(3).similarityThreshold(0.7).build();
        return getChatResponse(convId, prompt, searchRequest);
    }

    public String ingestLongPdf() {
        try {
           log.info("Starting PDF ingestion process...");
            TikaDocumentReader pdfReader = new TikaDocumentReader(pdfResource);
            List<Document> rawDocuments = pdfReader.get();

            log.info("Successfully read {} raw pages/paragraphs from PDF.", rawDocuments.size());
            //TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(100).build(); // split into chunks.

            // Split into chunks with prior doc knowledge
            CustomRegexDocumentSplitter splitter = new CustomRegexDocumentSplitter("(?=Module \\d+:)");

            List<Document> splitDocuments = splitter.split(rawDocuments);
            log.info("Split into {} smaller semantic chunks.", splitDocuments.size());

            // Add useful metadata - to know what is the source of the Chunk
            for (int i = 0; i < splitDocuments.size(); i++) {
                Document doc = splitDocuments.get(i);
                doc.getMetadata().put(SOURCE_KEY_WORD, Objects.requireNonNull(pdfResource.getFilename()));
                doc.getMetadata().put("chunk_index", i);
            }

            // upload to Vectore Store with smaller Batches to avoid Netword and DB overload.
            int batchSize = 100;
            List<Document> batch = new ArrayList<>();
            for (int i = 0; i < splitDocuments.size(); i++) {
                Document doc = splitDocuments.get(i);
                batch.add(doc);
                // When we get to the Batch size or end of list - push to the DB
                if (batch.size() == batchSize || i == splitDocuments.size() - 1) {
                    log.info("Ingested batch of {} chunks into the Vector Store...", batch.size());
                    vectorStore.accept(batch);
                    batch.clear(); // clear the Batch for the next iteration
                }
            }

           return "All PDF data has been successfully vectorized and stored!";

        } catch (Exception e) {
            throw new RuntimeException("Failed to process and upload long PDF to vector store", e);
        }
    }

    // Filtered Semantic Search
    public String queryMyCourse(String convId, String prompt) {
        // Use Spring AI FilterExpressionBuilder to build filter expression
        FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
        // For this method - the source must be equal to the pdfSource file name
        Filter.Expression filterExpression = filterBuilder
                .eq(SOURCE_KEY_WORD, Objects.requireNonNull(pdfResource.getFilename()))
                .build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3) // 3 most relevant.
                .filterExpression(filterExpression) // Filter magic happens here.
                .build();


        return getChatResponse(convId, prompt, searchRequest);
    }


    // Filtered Semantic Search
    public String queryAllCourse(String convId, String prompt) {

        FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();
        Filter.Expression filterExpression = filterBuilder
                .eq(SOURCE_KEY_WORD, Objects.requireNonNull(csvResource.getFilename()))
                .build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3)
                .filterExpression(filterExpression) // Filter magic happens here.
                .build();


        return getChatResponse(convId, prompt, searchRequest);
    }


    @Nullable
    private String getChatResponse(String convId, String prompt, SearchRequest searchRequest) {
        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);
        String similarDocsContent = similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String response = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(courseSystemPrompt).param("courses", similarDocsContent))
                .user(prompt)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }
}
