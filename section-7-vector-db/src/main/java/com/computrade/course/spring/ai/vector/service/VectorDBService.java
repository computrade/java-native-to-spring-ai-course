package com.computrade.course.spring.ai.vector.service;

import com.opencsv.CSVReaderHeaderAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.document.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDBService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @Value("classpath:courses_dataset.csv")
    private Resource csvResource;

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
}
