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
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
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

    @Value("classpath:courses_dataset.csv")
    private Resource csvResource;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    @Value("classpath:spring_ai_course_syllabus.pdf")
    private Resource pdfResource;
    

    public Map<String,EmbeddingResponse> embed(String prompt) {
        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(prompt));
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

                // יצירת תוכן ה-טקסט עבור ה-Embedding
                String content = String.format("Course: %s. Category: %s. Description: %s",
                        courseName, category, description);

                // בניית המטא-דאטה עבור ה-Vector DB
                Map<String, Object> metadata = new HashMap<>();
                metadata.put(SOURCE_KEY_WORD, Objects.requireNonNull(csvResource.getFilename()));
                metadata.put("course_id", id);
                metadata.put("category", category);
                metadata.put("rating", Double.parseDouble(rating));
                metadata.put("students", Integer.parseInt(noOfStudents));
                metadata.put("hours", Double.parseDouble(totalHours));

                // יצירת אובייקט ה-Document של Spring AI
                Document document = new Document(content, metadata);
                documents.add(document);
            }

            // דחיפת כל ה-Documents ל-Vector Store בבת אחת
            if (!documents.isEmpty()) {
                vectorStore.accept(documents);
                log.info("Successfully ingested {} courses using OpenCSV.", documents.size());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV and upload to vector store via OpenCSV", e);
        }
    }




    public String chat(String prompt) {

            String response = chatClient.prompt().user(prompt)
                    // Conversation id must be set to something.
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"default"))
                    .call()
                    .content();

            return response;
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
            CustomRegexDocumentSplitter splitter = new CustomRegexDocumentSplitter("(?=Module \\d+:)");

            List<Document> splitDocuments = splitter.split(rawDocuments);
            log.info("Split into {} smaller semantic chunks.", splitDocuments.size());

            // 4. העלאה ל-Vector Store ב-Batches (מניעת עומס על ה-Network וה-DB)
            int batchSize = 100;
            List<Document> batch = new ArrayList<>();

            for (int i = 0; i < splitDocuments.size(); i++) {
                Document doc = splitDocuments.get(i);

                // הוספת מטא-דאטה שימושי כדי לדעת מאיזה מקור הגיע ה-Chunk
                doc.getMetadata().put(SOURCE_KEY_WORD, Objects.requireNonNull(pdfResource.getFilename()));
                doc.getMetadata().put("chunk_index", i);

                batch.add(doc);

                // ברגע שהגענו לגודל ה-Batch או לסוף הרשימה - דוחפים ל-DB
                if (batch.size() == batchSize || i == splitDocuments.size() - 1) {
                    vectorStore.accept(batch);
                    log.info("Ingested batch of {} chunks into the Vector Store...", batch.size());
                    batch.clear(); // ריקון ה-Batch לקראת הסיבוב הבא
                }
            }

           return "All PDF data has been successfully vectorized and stored!";

        } catch (Exception e) {
            throw new RuntimeException("Failed to process and upload long PDF to vector store", e);
        }
    }

    // Filtered Semantic Search
    public String queryMyCourse(String convId, String prompt) {
        // 1. שימוש ב-Builder הייעודי של Spring AI לבניית ביטויי סינון
        FilterExpressionBuilder filterBuilder = new FilterExpressionBuilder();

        // 2. הגדרת תנאי סינון: השדה 'source' במטא-דאטה חייב להיות שווה בדיוק לשם קובץ הסילבוס
        Filter.Expression filterExpression = filterBuilder
                .eq(SOURCE_KEY_WORD, Objects.requireNonNull(pdfResource.getFilename()))
                .build();

        SearchRequest searchRequest = SearchRequest.builder()
                .query(prompt)
                .topK(3) // מספר ה-Chunks הכי רלוונטיים שנרצה לשלוף
                .filterExpression(filterExpression) // כאן קורה הקסם של הסינון
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
