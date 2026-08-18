# 🚀 Stock Market MCP Server Architecture

Welcome to the **Stock Market Model Context Protocol (MCP) Server**, built with **Spring AI 2.0** and **Java 25**.

---

## 🏗 Overview & Capabilities

This server exposes real-time market metrics, context resources, and prompt to LLM Clients (such as Spring AI MCP Client ) via the **Model Context Protocol (MCP)**.

---

## 🛠 Features & Capabilities

### 1. 🔧 Tools (`@Tool`)
Dynamic functions executed on demand by the LLM:
* `fetchStockPrice`: Fetches real-time stock quotes (current price, high, low, previous close).
* `exportCompanyNews`: Retrieves latest news articles for a specific ticker over a 30-day window.

### 2. 📑 Resources (`stock://...`)
Exposed data streams and context files:
* `stock://docs/architecture.md`: *(This File)* Serves system architecture and capability documentation.

### 3. 💬 Prompts (`McpSchema.Prompt`)
Pre-defined prompt templates to guide AI analysis:
* `general-market-overview`: Zero-argument prompt template for high-level market evaluations.

---

## ⚙ Technology Stack

| Component | Technology                  |
| :--- |:----------------------------|
| **Framework** | Spring Boot / Spring AI 2.0 |
| **Server Engine** | Sync Transport (STDIO)      |
| **JDK Version** | Java 25                     |
| **Build Tool** | Gradle 9.0                  |
| **External API** | Finnhub Financial API       |