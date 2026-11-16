# AGENTS.md - Computrade AI Agent Guidelines

> [!IMPORTANT]
> **NO TESTING REQUIRED**: There are no tests for this project. Do not run, create, or update tests.

## Architecture Overview
Native to Spring AI is the demo code for the Spring AI course.
It is a multi-module Gradle project (Java 25) built on Spring Boot 4.1.0 and Spring AI 2.0.0.
Each submodule is a standalone, runnable Spring Boot application for one course section.
See `README.md` for the full student-facing guide.

## Project Structure
Submodules are registered in `settings.gradle` and inherit shared configuration from the root `build.gradle`.
Each one adds its own provider and feature starters in its own `build.gradle`.

### Submodules
| Module | Topic |
|---|---|
| `section-2-chat-gemini` | Basic chat with Google Gemini |
| `section-2-chat-gemini-advanced` | Chat options and request parameters |
| `section-2-chat-gemini-system-prompt` | System prompts |
| `section-2-chat-gemini-stream` | Streaming responses (WebFlux) |
| `section-3-chat-advisors` | Custom advisors and guardrails (SafeGuard, role guard) |
| `section-4-chat-structured-output` | Structured output with Bean Validation |
| `section-5-chat-ollama` | Local models with Ollama |
| `section-6-chat-memory` | Chat memory (JDBC / PostgreSQL) |
| `section-7-vector-db` | RAG with pgvector and Tika PDF ingestion |
| `section-8-tools` | Tool calling (Finnhub stock market, tax calculator) |
| `section-9-mcp-server-stdio` | MCP server over stdio |
| `section-9-mcp-server-http` | MCP server over HTTP (WebMVC) |
| `section-9-mcp-server-async` | Async MCP server (WebFlux) |
| `section-9-mcp-client` | MCP client |
| `section-9-mcp-client-async` | Async MCP client (WebFlux) |
| `section-9-mcp-client-final` | MCP client that exposes MCP resources as tools |
| `section-10-observability` | Observability with Actuator, Micrometer, Prometheus and Grafana |

### Package layout
Code lives under `com.computrade.course.spring.ai` with the sub-packages `config`, `controller`, `service`, `model` and, where needed, `advisor`.
Prompt templates live in `src/main/resources/prompts/`.

## Core Dependencies
- **Spring AI BOM**: `org.springframework.ai:spring-ai-bom:2.0.0`
- **Applied to all subprojects** (root `build.gradle`):
  - Web stack: `spring-boot-starter-webflux` when the module name contains `stream` or `async`. Otherwise `spring-boot-starter-webmvc` and `springdoc-openapi-starter-webmvc-ui` (Swagger UI).
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator`
  - `org.projectlombok:lombok`
  - `com.opencsv:opencsv`
- **Added per module**: model starters (mainly `spring-ai-starter-model-google-genai`; Ollama in section 5), plus feature starters such as pgvector, JDBC chat memory, Tika, MCP client and MCP server.

## Configuration
- API keys go in `gradle.properties` (copy it from `gradle.properties.example`; never commit it).
- `bootRun` passes the keys to the app as `GEMINI_API_KEY`, `OPENAI_API_KEY`, `ANTHROPIC_API_KEY` and `FINNHUB_API_KEY`.
- Never hard-code API keys or passwords in source or YAML files.

## Infrastructure
- Root `compose.yaml`: PostgreSQL 17 with pgvector, plus pgAdmin.
- `section-10-observability/compose.yml`: Prometheus and Grafana (Grafana on port 3005).

## Conventions
- When adding a new section module, register it in `settings.gradle` and add it to this file and to `README.md`.
