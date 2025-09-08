# 🌤️ ClimaTempo API

[![Java](https://img.shields.io/badge/Java-21-red)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](LICENSE)

API de clima que integra dados meteorológicos externos e gera **insights inteligentes** como conforto térmico, horários recomendados, hidratação e até recomendações para obras ao ar livre.

---

## 📌 Problema de Negócio Resolvido
Dados climáticos crus (ex: 28°C, 60% de umidade) **não trazem valor de negócio direto**.  
O ClimaTempo transforma esses dados em **informações úteis e contextuais**:

- É confortável ou não?  
- Preciso me hidratar mais?  
- Quais horários do dia são mais adequados para atividades externas?  
- É um bom dia para execução de obras?  

---

## 🏗️ Arquitetura Escolhida e Justificativa
A arquitetura segue o padrão **Layered Architecture (Arquitetura em Camadas)**:

- **Controller** → Endpoints REST (`/clima`, `/clima/historico`, `/clima/obra`).  
- **Service** → Processamento de dados e geração de insights.  
- **Repository** → Persistência (Spring Data JPA + H2/MySQL).  
- **DTOs** → Transferência de dados entre camadas.  

**Justificativa**:
- Escalável, clara e de fácil manutenção.  
- Separação de responsabilidades.  
- Fácil integração futura com outras APIs ou bancos de dados.  
- Flexível para evolução do negócio.  
- Estrutura em camadas permite futura evolução para **arquitetura hexagonal**, isolando melhor o domínio de integrações externas.  

---

## 🤖 Algoritmos e Lógicas de Negócio
- **Consulta Climática**: consome API externa e normaliza dados.  
- **Insights Gerados**:
  - Conforto térmico (`temp` vs `feels_like`).  
  - Hidratação (baseado na umidade).  
  - Vento (classificação por intensidade).  
  - Horários recomendados (faixas de maior conforto).  
  - Obra (score baseado em temperatura, vento, umidade e chuva).  
- **Histórico**: todas as consultas são salvas em banco para análises futuras.  

---

## ⚙️ Decisões Técnicas e Trade-offs
- **Spring Boot** pela produtividade e ecossistema.  
- **H2** para ambiente local e testes (trade-off: dados voláteis).  
- **MySQL** como alternativa para produção (persistência real).  
- **DTOs** para desacoplamento, mas aumentam verbosidade.  
- **Integração com APIs externas** garante dados em tempo real, mas cria dependência de disponibilidade.  

---

## 💻 Como Executar Localmente

### Pré-requisitos
- Java 21+  
- Maven 3.9+  

### Configuração & Execução
```bash
# Clonar e entrar no projeto
git clone https://github.com/seu-usuario/climatempo-api.git
cd climatempo-api

# Configurar sua API Key do OpenWeather em src/main/resources/application.properties
openweather.api.key=SUA_CHAVE_AQUI
spring.datasource.url=jdbc:h2:mem:climatempo
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# Rodar
mvn spring-boot:run

# Base URL
http://localhost:8080
```

---

## 🧪 Exemplos de Uso

### 🔎 Consultar clima de uma cidade
```bash
GET http://localhost:8080/api/clima/Palmas
```
**Resposta**:
```json
{
  "cidade": "📍 Palmas",
  "climaAtual": "🌡️ 25.0°C | 🤔 Sensação: 25.0°C | 💧 Umidade: 30%",
  "insights": "😌 Conforto: Confortável | 🥤 Hidratação: Moderada | 🌬️ Vento: Normal | ⏰ Horários: 08:00–10:00"
}
```

---

### 📜 Consultar histórico
```bash
GET http://localhost:8080/clima/historico/{cidadeID}
```
**Resposta**:
```json
[
  {
    "id": 1,
    "cidade": {
      "id": 1,
      "nome": "Curitiba"
    },
    "observadoEm": "2025-09-07T21:00:01.128852",
    "tempC": 12.92,
    "sensacaoTerm": 12.67,
    "umidade": 92
  }
]
```

---

### 🧱 Viabilidade da Obra (multi-dias)
```bash
GET http://localhost:8080/api/clima/avaliarobra/{cidadeID}
```

**Resposta**:
```json
[
  {
    "dia": "2025-09-08",
    "bom": true,
    "score": 80,
    "motivos": [
      "Umidade muito alta."
    ],
    "metricas": {
      "tempMin": 12.87,
      "tempMax": 22.57,
      "tempDia": 15.97875,
      "umidade": 86,
      "ventoMaxKmH": 14.076,
      "probChuva": 0,
      "chuvaMm": 0
    }
  },
  {
    "dia": "2025-09-09",
    "bom": false,
    "score": 40,
    "motivos": [
      "Alta probabilidade de chuva.",
      "Chuva acumulada significativa."
    ],
    "metricas": {
      "tempMin": 13.69,
      "tempMax": 23.72,
      "tempDia": 16.88125,
      "umidade": 74,
      "ventoMaxKmH": 23.256,
      "probChuva": 1,
      "chuvaMm": 2.16
    }
  },
  {
    "dia": "2025-09-10",
    "bom": true,
    "score": 100,
    "motivos": [
      "Condições gerais favoráveis."
    ],
    "metricas": {
      "tempMin": 10.33,
      "tempMax": 26.31,
      "tempDia": 16.565,
      "umidade": 65,
      "ventoMaxKmH": 12.78,
      "probChuva": 0,
      "chuvaMm": 0
    }
  },
  {
    "dia": "2025-09-11",
    "bom": true,
    "score": 100,
    "motivos": [
      "Condições gerais favoráveis."
    ],
    "metricas": {
      "tempMin": 11.11,
      "tempMax": 28.84,
      "tempDia": 18.5425,
      "umidade": 63,
      "ventoMaxKmH": 12.708,
      "probChuva": 0,
      "chuvaMm": 0
    }
  },
  {
    "dia": "2025-09-12",
    "bom": true,
    "score": 75,
    "motivos": [
      "Temperatura pouco adequada para obra."
    ],
    "metricas": {
      "tempMin": 10.2,
      "tempMax": 20.47,
      "tempDia": 14.12,
      "umidade": 69,
      "ventoMaxKmH": 14.544,
      "probChuva": 0,
      "chuvaMm": 0
    }
  }
]
```

---

## 📜 Licença
Este projeto está licenciado sob a [MIT License](LICENSE).
