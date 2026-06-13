# AGENTS.md - Computrade AI Agent Guidelines

> [!IMPORTANT]
> **NO TESTING REQUIRED**: There are no tests for this project. Do not run, create, or update tests.

## Architecture Overview
Native to Spring AI is a code for Spring AI course demos. 
It is a multi-module Spring Boot 4.1.0 application (Java 25) with Spring AI 2.0.0 and Docker Compose support.

## Project Structure
The project is organized into submodules, each representing a specific AI provider or interaction type. All submodules inherit common configurations and dependencies from the root `build.gradle`.

### Submodules:
- `section-2-chat-gemini`: Chat interactions using Google Gemini.
- `section-2-chat-openai`: Chat interactions using OpenAI.
- `section-2-chat-ollama`: Chat interactions using local models via Ollama.
- `section-2-chat-cluade`: Chat interactions using Anthropic Claude.
- `section-2-chat-multi`: Unified module supporting multiple AI profiles.

## Core Dependencies
- **Spring AI BOM**: `org.springframework.ai:spring-ai-bom:2.0.0`
- **Common Starters** (applied to all subprojects):
  - `org.springframework.boot:spring-boot-starter-webmvc`
  - `org.springframework.ai:spring-ai-starter-model-openai`
  - `org.springframework.ai:spring-ai-starter-model-google-genai`
  - `org.springframework.ai:spring-ai-starter-model-anthropic`
  - `org.springframework.ai:spring-ai-starter-model-ollama`
  - `org.projectlombok:lombok`
  - `org.springframework.boot:spring-boot-devtools`
