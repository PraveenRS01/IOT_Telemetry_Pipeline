# IoT Telemetry Pipeline - Event-Driven Microservices

A comprehensive event-driven microservices architecture for processing IoT telemetry data with real-time analytics, cognitive load monitoring, and scalable data processing capabilities.

## System Architecture

```
IoT Device Simulator → Ingestion Service → Kafka → Stream Processor → Time Series Service
                                                      ↓                    ↓
                                              Real-time Analytics    InfluxDB + PostgreSQL
```

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0, Spring WebFlux, Spring Data JPA
- **Message Queue**: Apache Kafka 3.6.0
- **Databases**: InfluxDB 2.7 (Time Series), PostgreSQL 15 (Analytics)
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven (Multi-module project)
- **Monitoring**: Spring Actuator, Custom Metrics

## Project Structure

```
Microservices_proj/
├── iot-data-simulator/          # Cognitive load telemetry generator
├── iot-ingestion-service/       # REST API for data ingestion
├── iot-stream-processor/        # Real-time Kafka stream processing
├── iot-time-series-service/     # Time series and analytics storage
├── docker-compose.yml           # Infrastructure orchestration
├── sql/init.sql                 # Database schema initialization
└── scripts/                     # Service management scripts
```

## Microservices Overview

### 1. IoT Data Simulator (Port 8081)
**Purpose**: Generates realistic cognitive load telemetry data simulating user behavior patterns.

**Key Features**:
- Simulates user interactions across multiple features (search, dashboard, product_details)
- Generates metrics: response time, click count, error rates, session duration, scroll depth
- Scheduled data generation every 5 seconds
- HTTP-based data transmission with retry mechanisms
- Real-time statistics and health monitoring

**Technologies**: Spring Boot, Spring WebFlux, WebClient, Scheduled Tasks

### 2. Ingestion Service (Port 8082)
**Purpose**: Centralized data ingestion point that receives, validates, and routes telemetry data.

**Key Features**:
- REST API endpoint for telemetry data ingestion
- Data validation and persistence to H2 database
- Kafka producer for real-time data streaming
- CORS support for cross-origin requests
- Comprehensive error handling and logging

**Technologies**: Spring Boot, Spring Data JPA, Spring Kafka, H2 Database

### 3. Stream Processor (Port 8083)
**Purpose**: Real-time stream processing engine for analytics and anomaly detection.

**Key Features**:
- Kafka consumer for real-time data processing
- Engagement score calculation based on user behavior patterns
- Anomaly detection for performance issues and error spikes
- Real-time dashboard updates simulation
- HTTP integration with time-series service
- Comprehensive metrics tracking and statistics

**Technologies**: Spring Boot, Spring Kafka, WebClient, Jackson JSON Processing

### 4. Time Series Service (Port 8084)
**Purpose**: Dual-database architecture for time-series storage and analytics aggregation.

**Key Features**:
- InfluxDB integration for high-frequency time-series data
- PostgreSQL integration for structured analytics data
- User engagement summary aggregation
- Feature-based analytics and reporting
- RESTful APIs for data retrieval and visualization
- Automatic schema management and data validation

**Technologies**: Spring Boot, InfluxDB Client, Spring Data JPA, PostgreSQL

## Data Flow Architecture

### Real-Time Processing Pipeline
1. **Data Generation**: Simulator generates cognitive load metrics every 5 seconds
2. **Data Ingestion**: HTTP POST to ingestion service with retry mechanisms
3. **Data Persistence**: Raw data stored in H2 database for audit trail
4. **Event Streaming**: Data published to Kafka topic for real-time processing
5. **Stream Processing**: Consumer calculates engagement scores and detects anomalies
6. **Data Storage**: Processed data sent to time-series service via HTTP
7. **Dual Storage**: Data stored in both InfluxDB (time-series) and PostgreSQL (analytics)

### Cognitive Load Metrics
- **Response Time**: Page load and interaction response times
- **Click Patterns**: Click count and time between interactions
- **Error Tracking**: Error count and error rate calculations
- **Session Analytics**: Session duration and usage frequency
- **Engagement Scoring**: Composite score based on user behavior patterns
- **Anomaly Detection**: Threshold-based detection for performance issues

## Infrastructure Components

### Apache Kafka
- **Broker**: Single-node Kafka cluster with Zookeeper
- **Topics**: `telemetry-data` for real-time data streaming
- **Configuration**: Auto-topic creation, replication factor 1
- **Port**: 9092 (Kafka), 2181 (Zookeeper)

### InfluxDB
- **Version**: 2.7 with setup mode enabled
- **Organization**: `iot-telemetry`
- **Bucket**: `telemetry-data`
- **Authentication**: Token-based authentication
- **Port**: 8086

### PostgreSQL
- **Version**: 15
- **Database**: `telemetry_analytics`
- **Schema**: Automated initialization with `init.sql`
- **Port**: 5432

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

### Quick Start
1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd Microservices_proj
   ```

2. **Start Infrastructure**
   ```bash
   docker-compose up -d
   ```

3. **Build and Start Services**
   ```bash
   ./start-all-services.sh
   ```

4. **Verify System Health**
   ```bash
   ./test-new-architecture.sh
   ```

### Service Endpoints

| Service | Health Check | Statistics | Data Endpoints |
|---------|-------------|------------|----------------|
| Simulator | `GET /api/v1/simulator/health` | `GET /api/v1/simulator/stats` | `POST /api/v1/simulator/generate` |
| Ingestion | `GET /api/v1/telemetry/health` | `GET /api/v1/telemetry/stats` | `POST /api/v1/telemetry/ingest` |
| Stream Processor | `GET /api/v1/stream-processor/health` | `GET /api/v1/stream-processor/stats` | `GET /api/v1/stream-processor/engagement/{userId}` |
| Time Series | `GET /api/v1/timeseries/health` | `GET /api/v1/timeseries/stats` | `GET /api/v1/timeseries/analytics` |

## Key Features

### Real-Time Data Processing
- Event-driven microservices architecture with Apache Kafka
- Real-time stream processing with engagement score calculation
- Anomaly detection for performance monitoring
- Cognitive load telemetry simulation and analysis

### Data Storage and Analytics
- Dual-database architecture (InfluxDB for time-series, PostgreSQL for analytics)
- User engagement summary aggregation
- Feature-based analytics and reporting
- Automated schema management and data validation

### Infrastructure and Deployment
- Docker Compose orchestration for local development
- Spring Batch jobs for nightly data aggregation
- Comprehensive monitoring and health checks
- Scalable microservices architecture

## Monitoring and Observability

### Health Monitoring
- Spring Actuator endpoints for all services
- Custom health checks for database connections
- Service dependency monitoring
- Real-time metrics collection

### Logging and Debugging
- Structured logging with correlation IDs
- Service-specific log files in `logs/` directory
- Error tracking and retry mechanism logging
- Performance metrics and statistics

### Testing Strategy
- Integration testing with Docker Compose
- End-to-end pipeline validation
- Error scenario testing and recovery
- Performance benchmarking scripts