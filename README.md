# Java Native to Spring AI Course

A comprehensive multi-module repository containing complete code examples, section-by-section projects, and practical applications for the **"Java Native to Spring AI"** course.

Built with **Java 25**, **Spring Boot 4.1.0**, and **Spring AI 2.0.0**, demonstrating modern enterprise AI integration patterns ranging from foundational prompt engineering to advanced RAG, Tool Calling, Model Context Protocol (MCP), and Observability.

---

## Table of Contents
- [Tech Stack & Architecture](#tech-stack--architecture)
- [Prerequisites](#prerequisites)
- [Getting Started & Configuration](#getting-started--configuration)
  - [1. API Keys Configuration](#1-api-keys-configuration)
  - [2. Start Infrastructure with Docker Compose](#2-start-infrastructure-with-docker-compose)
- [Repository Structure & Section Roadmap](#repository-structure--section-roadmap)
  - [Section 2: Google Gemini Chat & Streaming](#section-2-google-gemini-chat--streaming)
  - [Section 3: Chat Advisors & Guardrails](#section-3-chat-advisors--guardrails)
  - [Section 4: Structured Output](#section-4-structured-output)
  - [Section 5: Local Models with Ollama](#section-5-local-models-with-ollama)
  - [Section 6: Conversational Memory](#section-6-conversational-memory)
  - [Section 7: Vector Databases & RAG](#section-7-vector-databases--rag)
  - [Section 8: Tools & Function Calling](#section-8-tools--function-calling)
  - [Section 9: Model Context Protocol (MCP)](#section-9-model-context-protocol-mcp)
  - [Section 10: AI Observability & Monitoring](#section-10-ai-observability--monitoring)
- [Running the Applications](#running-the-applications)
- [Interactive API Documentation (Swagger UI)](#interactive-api-documentation-swagger-ui)
- [Infrastructure & Services Overview](#infrastructure--services-overview)

---

## Tech Stack & Architecture

- **Java**: Java 25 (Language Toolchain configured)
- **Framework**: Spring Boot `4.1.0`
- **AI Framework**: Spring AI `2.0.0` (`spring-ai-bom`)
- **Build Tool**: Gradle Multi-Project (`settings.gradle` / `build.gradle`)
- **AI Providers Supported**:
  - Google Gemini (`spring-ai-starter-model-google-genai`)
  - OpenAI (`spring-ai-starter-model-openai`)
  - Anthropic Claude (`spring-ai-starter-model-anthropic`)
  - Ollama (`spring-ai-starter-model-ollama`)
- **Vector Database**: PostgreSQL 17 with `pgvector` (`pgvector/pgvector:pg17`)
- **MCP**: Spring AI Model Context Protocol (Stdio, Sync HTTP, and Async WebFlux transports)
- **Documentation**: SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui:2.8.5`)
- **Observability**: Spring Boot Actuator, Micrometer, Prometheus, Grafana

---

## Prerequisites

Before running the projects, ensure you have the following installed:

1. **Java 25 SDK** (e.g. OpenJDK 25, Temurin 25)
2. **Docker & Docker Compose** (for database, vector store, and observability stack)
3. **AI Model API Keys**:
   - Google Gemini API Key ([Google AI Studio](https://aistudio.google.com/))
   - *(Optional)* Finnhub API Key for financial tools demos ([Finnhub](https://finnhub.io/))
   - *(Optional)* OpenAI / Anthropic API keys for alternate provider demos
4. *(Optional)* **Ollama** installed locally if testing Section 5 (`http://localhost:11434`)

---

## Getting Started & Configuration

### 1. API Keys Configuration

Copy the example properties file in the root directory:

```bash
# On Linux / macOS
cp gradle.properties.example gradle.properties

# On Windows (PowerShell)
Copy-Item gradle.properties.example gradle.properties
```

Edit `gradle.properties` and provide your keys:

```properties
geminiApiKey=YOUR_GEMINI_API_KEY
openaiApiKey=YOUR_OPENAI_API_KEY
anthropicApiKey=YOUR_ANTHROPIC_API_KEY
finnhubApiKey=YOUR_FINNHUB_API_KEY
gitHubToken=YOUR_GITHUB_TOKEN
```

> **Note**: `gradle.properties` is passed automatically to the `bootRun` tasks as environment variables (`GEMINI_API_KEY`, `FINNHUB_API_KEY`, etc.) and is ignored by git to protect credentials.

### 2. Start Infrastructure with Docker Compose

Start the PostgreSQL instance equipped with the `pgvector` extension and pgAdmin:

```bash
docker compose up -d
```

| Service | Host Port | Credentials / Details    |
| :--- | :--- |:-------------------------|
| **PostgreSQL (pgvector)** | `localhost:5432` | DB: `springai_db` |
| **pgAdmin 4** | `localhost:5050` | Email: `admin@admin.com` |

---

## Repository Structure & Section Roadmap

Each subproject represents an isolated module focused on a core capability of Spring AI:

```
java-native-to-spring-ai-course/
├── section-2-chat-gemini                    # Basic ChatClient & ChatModel with Gemini
├── section-2-chat-gemini-advanced           # Advanced parameters (temperature, topP, tokens)
├── section-2-chat-gemini-system-prompt      # System prompts & PromptTemplate (.st files)
├── section-2-chat-gemini-stream             # Reactive streaming with WebFlux (Flux<String>)
├── section-3-chat-advisors                  # Request/Response Advisors & Custom Guardrails
├── section-4-chat-structured-output         # Strongly-typed Java Records & Output Converters
├── section-5-chat-ollama                    # Local model execution using Ollama
├── section-6-chat-memory                    # Conversational memory (In-Memory & JDBC stores)
├── section-7-vector-db                      # RAG, PGVector embeddings & Similarity Search
├── section-8-tools                          # Function / Tool Calling with REST API integrations
├── section-9-mcp-server-stdio               # Model Context Protocol: Server via Stdio
├── section-9-mcp-server-http                # Model Context Protocol: Server via HTTP (port 6061)
├── section-9-mcp-server-async               # Model Context Protocol: Async Server (port 6062)
├── section-9-mcp-client                     # Model Context Protocol: Sync Client
├── section-9-mcp-client-async               # Model Context Protocol: Reactive Client
├── section-9-mcp-client-final               # Model Context Protocol: Full Client Demo
└── section-10-observability                 # Actuator metrics, Micrometer, Prometheus & Grafana
```

### Section 2: Google Gemini Chat & Streaming
- **`section-2-chat-gemini`**: Initializes `ChatClient` using Spring Boot auto-configuration with Google Gemini (`gemini-3.5-flash`).
- **`section-2-chat-gemini-advanced`**: Configures runtime chat options such as temperature, `topP`, `topK`, and maximum token limits.
- **`section-2-chat-gemini-system-prompt`**: Uses system prompts and StringTemplate files (`.st`) for template-based prompt engineering.
- **`section-2-chat-gemini-stream`**: Leverages Spring WebFlux and `Flux<String>` to stream real-time token responses to the client.

### Section 3: Chat Advisors & Guardrails
- **`section-3-chat-advisors`**: Demonstrates the Spring AI Advisor API. Covers pre-built advisors like `SimpleLoggerAdvisor` and custom advisors for input validation, role-based safety checks (`RoleGuardAdvisor`), and content moderation (`CaseInsensitiveSafeGuardAdvisor`).

### Section 4: Structured Output
- **`section-4-chat-structured-output`**: Explores schema-driven structured responses. Converts unstructured LLM output directly into Java Records, POJOs, Lists, and Maps using `BeanOutputConverter`, `MapOutputConverter`, and `ListOutputConverter`.

### Section 5: Local Models with Ollama
- **`section-5-chat-ollama`**: Demonstrates privacy-first, on-premise AI deployments using local LLMs (e.g. Llama 3.2, Gemma) managed by Ollama without relying on cloud APIs.

### Section 6: Conversational Memory
- **`section-6-chat-memory`**: Manages multi-turn conversation history using `MessageChatMemoryAdvisor` and `PromptChatMemoryAdvisor`. Supports conversation IDs with in-memory storage and persistent JDBC-backed repositories.

### Section 7: Vector Databases & RAG
- **`section-7-vector-db`**: Implements Retrieval-Augmented Generation (RAG):
  - Document reading & chunking (`TokenTextSplitter`).
  - Embeddings generation via Google GenAI (`gemini-embedding-2`, 1536 dimensions).
  - Storage & vector indexing with PostgreSQL `pgvector` (HNSW indexing, Cosine Distance).
  - Augmenting chat prompts with contextual search results.

### Section 8: Tools & Function Calling
- **`section-8-tools`**: Integrates external tools into the LLM conversation loop. Uses Spring AI tool callbacks to allow the model to autonomously invoke external services (such as the Finnhub stock market REST API) to fetch real-time data.

### Section 9: Model Context Protocol (MCP)
- **`section-9-mcp-server-stdio`**: Implements an MCP server exposing custom tools over standard input/output (Stdio).
- **`section-9-mcp-server-http`**: Implements a synchronous MCP server over Streamable HTTP transport (runs on port `6061`).
- **`section-9-mcp-server-async`**: Reactive MCP server implementation using Spring WebFlux (runs on port `6062`).
- **`section-9-mcp-client` / `section-9-mcp-client-async` / `section-9-mcp-client-final`**: MCP client applications connecting to MCP servers, discovering tool capabilities, and routing model tool invocations through the protocol.

### Section 10: AI Observability & Monitoring
- **`section-10-observability`**: Complete observability integration for production AI workloads:
  - Spring Boot Actuator & Micrometer Observations tracking prompt tokens, completion tokens, and latency.
  - Dedicated Docker Compose stack (`section-10-observability/compose.yml`) running Prometheus (`:9090`) and Grafana (`:3005`).

---

## Running the Applications

To run any section module, use the Gradle wrapper from the root folder:

### Linux / macOS
```bash
./gradlew :section-2-chat-gemini:bootRun
./gradlew :section-4-chat-structured-output:bootRun
./gradlew :section-7-vector-db:bootRun
./gradlew :section-8-tools:bootRun
```

### Windows (PowerShell / CMD)
```powershell
.\gradlew.bat :section-2-chat-gemini:bootRun
.\gradlew.bat :section-4-chat-structured-output:bootRun
.\gradlew.bat :section-7-vector-db:bootRun
.\gradlew.bat :section-8-tools:bootRun
```

---

## Interactive API Documentation (Swagger UI)

All Spring WebMVC modules include SpringDoc OpenAPI. Once a service is running, navigate to Swagger UI in your browser to inspect and trigger endpoints interactively:

```
http://localhost:8080/swagger-ui/index.html
```

*(For custom port modules such as `section-9-mcp-server-http`, access via `http://localhost:6061/swagger-ui/index.html`)*.

---

## Infrastructure & Services Overview

### Core Stack (Root `compose.yaml`)
- **PostgreSQL Vector Store**: `localhost:5432` 
- **pgAdmin Management UI**: `http://localhost:5050` 

### Observability Stack (`section-10-observability/compose.yml`)
To launch Prometheus and Grafana for Section 10:
```bash
cd section-10-observability
docker compose up -d
```
- **Prometheus UI**: `http://localhost:9090`
- **Grafana Dashboards**: `http://localhost:3005`
