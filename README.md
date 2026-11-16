# Smart Building Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An integrated building management platform featuring HVAC control, lighting automation, access control, energy monitoring, and occupancy analytics.

## Features

### 🌡️ HVAC Control
- Temperature and humidity management
- Zone-based climate control with PID loops
- BACnet/IP and Modbus protocol support
- Automated mode selection (heating/cooling)

### 💡 Lighting Automation
- Brightness and color temperature control
- Scene management (256 scenes per zone)
- Daylight harvesting for energy savings
- DALI-2 and DMX512 protocol support

### 🚪 Access Control
- Multi-credential support (card, PIN, biometric, mobile)
- Anti-passback and two-factor authentication
- Real-time door monitoring
- Emergency lockdown capability

### ⚡ Energy Monitoring
- Real-time power consumption tracking
- Peak demand management
- Power quality analysis
- Carbon footprint calculation

### 🔌 IoT Integration
- MQTT broker for device communication
- Support for multiple protocols
- Real-time telemetry processing
- Command and control interface

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                  │
│  │   Web    │  │  Mobile  │  │  Admin   │                  │
│  │Dashboard │  │   App    │  │ Console  │                  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘                  │
└───────┼─────────────┼─────────────┼─────────────────────────┘
        │             │             │
┌───────▼─────────────▼─────────────▼─────────────────────────┐
│                     API Gateway                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  REST API  │  WebSocket  │  Authentication           │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                   Business Logic Layer                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │   HVAC   │  │ Lighting │  │  Access  │  │  Energy  │    │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │    │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘    │
└───────┼─────────────┼─────────────┼─────────────┼───────────┘
        │             │             │             │
┌───────▼─────────────▼─────────────▼─────────────▼───────────┐
│                   Integration Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │  BACnet  │  │  Modbus  │  │   MQTT   │  │   IoT    │    │
│  │ Adapter  │  │ Adapter  │  │  Broker  │  │ Gateway  │    │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 15+
- MQTT Broker (Mosquitto)
- Maven 3.9+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/kamel-pidima/smart-building-bms.git
cd smart-building-bms
```

2. Configure the database:
```bash
createdb smartbuilding
```

3. Set environment variables:
```bash
export DB_USERNAME=smartbuilding
export DB_PASSWORD=smartbuilding
export MQTT_BROKER_URL=tcp://localhost:1883
```

4. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

5. Access the API documentation:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### HVAC Control
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/hvac/buildings/{id}/controllers` | List controllers |
| GET | `/api/v1/hvac/zones/{id}/controller` | Get zone controller |
| POST | `/api/v1/hvac/zones/{id}/temperature` | Set temperature |
| POST | `/api/v1/hvac/controllers/{id}/mode` | Set operating mode |

### Lighting Control
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/lighting/zones/{id}/brightness` | Set brightness |
| POST | `/api/v1/lighting/zones/{id}/scene` | Activate scene |
| POST | `/api/v1/lighting/zones/{id}/daylight-harvesting` | Configure DLH |

### Access Control
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/access/{id}/unlock` | Unlock door |
| POST | `/api/v1/access/buildings/{id}/lockdown` | Emergency lockdown |
| GET | `/api/v1/access/logs` | Access audit logs |

### Energy Monitoring
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/energy/buildings/{id}/summary` | Energy summary |
| GET | `/api/v1/energy/buildings/{id}/consumption` | Current consumption |
| GET | `/api/v1/energy/buildings/{id}/carbon` | Carbon footprint |

## Project Structure

```
src/
├── main/
│   ├── java/com/smartbuilding/
│   │   ├── SmartBuildingApplication.java
│   │   ├── common/          # Shared entities and utilities
│   │   ├── hvac/            # HVAC module
│   │   ├── lighting/        # Lighting module
│   │   ├── access/          # Access control module
│   │   ├── energy/          # Energy monitoring module
│   │   ├── gateway/         # IoT gateway (MQTT)
│   │   └── api/             # REST API controllers
│   └── resources/
│       └── application.yml
└── test/
    └── java/com/smartbuilding/
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Related Documentation

- [System Requirements Specification](docs/SRS.md)
- [Software Design Document](docs/SDD.md)
- [API Reference](docs/API.md)
- [Integration Guide](docs/INTEGRATION.md)
