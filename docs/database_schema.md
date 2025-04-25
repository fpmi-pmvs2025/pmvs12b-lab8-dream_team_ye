---
layout: page
title: Схема базы данных
---

# Схема базы данных

Приложение использует SQLite базу данных через библиотеку Room для локального хранения данных.

## Основные таблицы

## Tables

### 1. CRYPTOCURRENCY
- `id` (string, PK): Unique identifier for the cryptocurrency
- `name` (string): Name of the cryptocurrency
- `symbol` (string): Ticker symbol
- `price` (float): Current price
- `marketCap` (float): Market capitalization
- `volume24h` (float): 24-hour trading volume
- `percentChange24h` (float): 24-hour price change in percent
- `imageUrl` (string): URL to the cryptocurrency's image
- `lastUpdated` (long): Timestamp of the last update

### 2. USER
- `id` (string, PK): Unique identifier for the user
- `name` (string): User's name
- `email` (string): User's email
- `profilePicUrl` (string): URL to the user's profile picture

### 3. PORTFOLIO
- `userId` (string, FK): References `USER.id`
- `cryptocurrencyId` (string, FK): References `CRYPTOCURRENCY.id`
- `amount` (float): Amount of cryptocurrency owned
- `purchasePrice` (float): Price at which the cryptocurrency was purchased
- `purchaseDate` (long): Timestamp of the purchase

## Relationships
- A `USER` **has** many `PORTFOLIO` entries
- A `CRYPTOCURRENCY` can be **included in** many `PORTFOLIO` entries

## Entity-Relationship Diagram

```
erDiagram
    CRYPTOCURRENCY {
        string id PK
        string name
        string symbol
        float price
        float marketCap
        float volume24h
        float percentChange24h
        string imageUrl
        long lastUpdated
    }
    
    PORTFOLIO {
        string userId FK
        string cryptocurrencyId FK
        float amount
        float purchasePrice
        long purchaseDate
    }
    
    USER {
        string id PK
        string name
        string email
        string profilePicUrl
    }
    
    USER ||--o{ PORTFOLIO : "has"
    CRYPTOCURRENCY ||--o{ PORTFOLIO : "included_in"
```