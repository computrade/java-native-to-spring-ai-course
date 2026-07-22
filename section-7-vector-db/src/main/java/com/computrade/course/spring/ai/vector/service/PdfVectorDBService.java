package com.computrade.course.spring.ai.vector.service;

import com.computrade.course.spring.ai.vector.util.CustomRegexDocumentSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfVectorDBService {

    String SOURCE_KEY_WORD = "source";

    private final ChatClient chatClient;

    private final VectorStore pdfVectorStore;

    @Value("classpath:systemPromptForCourse.st")
    private Resource courseSystemPrompt;

    @Value("classpath:data/spring_ai_course_syllabus.pdf")
    private Resource pdfResource;

    public String chatPdfRag(String convId, String prompt) {

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
            pdfVectorStore.accept(splitDocuments);

            return "All PDF data has been successfully vectorized and stored!";

        } catch (Exception e) {
            throw new RuntimeException("Failed to process and upload long PDF to vector store", e);
        }
    }


    @Nullable
    private String getChatResponse(String convId, String prompt, SearchRequest searchRequest) {
        List<Document> similarDocs = pdfVectorStore.similaritySearch(searchRequest);
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
