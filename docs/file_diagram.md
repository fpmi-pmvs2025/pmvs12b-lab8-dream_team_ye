---
layout: default
title: Диаграмма файлов приложения
nav_order: 5
---

# Диаграмма файлов приложения

## Общая архитектурная структура

Приложение разработано с использованием принципов Clean Architecture, что позволяет обеспечить четкое разделение ответственности и повысить тестируемость кода.

```
     UI (Presentation Layer)
     ↑        ↓
     ↑    ViewModel
     ↑        ↓
 Domain Layer (Use Cases)
     ↑        ↓
     ↑   Repositories
     ↑        ↓
   Data Layer (API + DB)
```

## Взаимодействие основных компонентов

```mermaid
graph TD
    A[MainActivity] --> B[AppNavHost]
    B --> C[DashboardScreen]
    B --> D[PortfolioScreen]
    B --> E[ProfileScreen]
    
    C --> F[DashboardViewModel]
    D --> G[PortfolioViewModel]
    E --> H[ProfileViewModel]
    
    F --> I[GetCryptocurrenciesUseCase]
    G --> J[GetPortfolioUseCase]
    G --> K[AddCoinUseCase]
    G --> L[RemoveCoinUseCase]
    
    I --> M[CryptocurrencyRepository]
    J --> N[PortfolioRepository]
    K --> N
    L --> N
    
    M --> O[CryptocurrencyApi]
    M --> P[CryptocurrencyDao]
    N --> Q[PortfolioDao]
    
    O --> R[(Remote Server)]
    P --> S[(Local Database)]
    Q --> S
```

## Диаграмма структуры UI компонентов

```mermaid
graph TD
    A[MainActivity] --> B[CryptoTrackerApp]
    B --> C[NavigationBar]
    B --> D[AppNavHost]
    
    D --> E[DashboardScreen]
    D --> F[PortfolioScreen]
    D --> G[ProfileScreen]
    
    E --> H[CryptoList]
    H --> I[CryptoListItem]
    
    F --> J[PortfolioList]
    J --> K[PortfolioItem]
    
    E --> L[SearchBar]
    E --> M[FilterOptions]
```

## Диаграмма базы данных

```mermaid
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

## Диаграмма потока данных

```mermaid
sequenceDiagram
    participant User
    participant UI
    participant ViewModel
    participant UseCase
    participant Repository
    participant LocalDB
    participant API
    
    User->>UI: Открывает дашборд
    UI->>ViewModel: Запрашивает данные
    ViewModel->>UseCase: Вызывает getTopCryptocurrencies()
    UseCase->>Repository: Запрашивает данные
    Repository->>LocalDB: Проверяет кэш
    alt Данные актуальны
        LocalDB->>Repository: Возвращает данные из кэша
    else Данные устарели или отсутствуют
        Repository->>API: Запрашивает свежие данные
        API->>Repository: Возвращает данные
        Repository->>LocalDB: Обновляет кэш
    end
    Repository->>UseCase: Возвращает данные
    UseCase->>ViewModel: Возвращает данные
    ViewModel->>UI: Обновляет UI
    UI->>User: Отображает данные
```

## Модель внедрения зависимостей

```mermaid
graph TD
    A[AppModule] --> B[RepositoryModule]
    A --> C[DatabaseModule]
    A --> D[NetworkModule]
    A --> E[ViewModelModule]
    
    B --> F[CryptocurrencyRepository]
    B --> G[PortfolioRepository]
    B --> H[UserRepository]
    
    C --> I[AppDatabase]
    I --> J[CryptocurrencyDao]
    I --> K[PortfolioDao]
    I --> L[UserDao]
    
    D --> M[CryptocurrencyApiService]
    D --> N[AuthApiService]
    
    E --> O[Provides ViewModels]
``` 