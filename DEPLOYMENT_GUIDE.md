# 🚀 Restaurant Management System - Deployment Guide

## 📋 Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Database**: SQL Server 2019 or higher
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Storage**: Minimum 10GB free space
- **OS**: Windows 10/11, Linux (Ubuntu 20.04+), or macOS

### Software Dependencies
- **Maven**: 3.8+ (for building)
- **Git**: 2.30+ (for version control)
- **SMTP Server**: For email notifications (Gmail, Outlook, or custom SMTP)

## 🛠️ Installation Steps

### 1. Clone the Repository
```bash
git clone <repository-url>
cd restaurant-management-system
```

### 2. Database Setup

#### SQL Server Configuration
```sql
-- Create database
CREATE DATABASE RestaurantManagement;

-- Create login
CREATE LOGIN restaurant_user WITH PASSWORD = 'YourSecurePassword123!';

-- Create user and grant permissions
USE RestaurantManagement;
CREATE USER restaurant_user FOR LOGIN restaurant_user;
ALTER ROLE db_owner ADD MEMBER restaurant_user;
```

#### Run Database Schema
```bash
# The schema.sql file will be automatically executed on first startup
# Or manually run:
sqlcmd -S localhost -d RestaurantManagement -i src/main/resources/schema.sql
```

### 3. Application Configuration

#### Create `application.properties`
```properties
# Database Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=RestaurantManagement;encrypt=true;trustServerCertificate=true
spring.datasource.username=restaurant_user
spring.datasource.password=YourSecurePassword123!
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.format_sql=true

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Application Configuration
server.port=8080
server.servlet.context-path=/
spring.application.name=restaurant-management-system

# Logging Configuration
logging.level.com.restaurant=INFO
logging.level.org.springframework.security=DEBUG
logging.file.name=logs/restaurant-system.log

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Session Configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=false
```

#### Create `application-prod.properties` (Production)
```properties
# Production Database
spring.datasource.url=jdbc:sqlserver://prod-server:1433;databaseName=RestaurantManagement;encrypt=true;trustServerCertificate=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Production Email
spring.mail.host=${SMTP_HOST}
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}

# Production Logging
logging.level.com.restaurant=WARN
logging.level.org.springframework.security=WARN
logging.file.name=/var/log/restaurant-system/application.log

# Security
server.servlet.session.cookie.secure=true
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
```

### 4. Build the Application

#### Using Maven
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Skip tests (if needed)
mvn package -DskipTests
```

#### Using Maven Wrapper (if available)
```bash
# Windows
./mvnw.cmd clean package

# Linux/macOS
./mvnw clean package
```

### 5. Run the Application

#### Development Mode
```bash
# Using Maven
mvn spring-boot:run

# Using JAR file
java -jar target/restaurant-management-system-1.0.0.jar

# With specific profile
java -jar target/restaurant-management-system-1.0.0.jar --spring.profiles.active=prod
```

#### Production Mode
```bash
# Set environment variables
export DB_USERNAME=restaurant_user
export DB_PASSWORD=YourSecurePassword123!
export SMTP_HOST=smtp.gmail.com
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password

# Run with production profile
java -jar target/restaurant-management-system-1.0.0.jar --spring.profiles.active=prod
```

## 🔧 Configuration Details

### Database Configuration

#### SQL Server Connection String
```properties
# Basic connection
jdbc:sqlserver://localhost:1433;databaseName=RestaurantManagement

# With encryption
jdbc:sqlserver://localhost:1433;databaseName=RestaurantManagement;encrypt=true;trustServerCertificate=true

# With authentication
jdbc:sqlserver://localhost:1433;databaseName=RestaurantManagement;user=restaurant_user;password=YourSecurePassword123!
```

#### Connection Pool Settings
```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

### Email Configuration

#### Gmail Setup
1. Enable 2-Factor Authentication
2. Generate App Password
3. Use app password in configuration

#### Custom SMTP Setup
```properties
spring.mail.host=your-smtp-server.com
spring.mail.port=587
spring.mail.username=your-username
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=your-smtp-server.com
```

### Security Configuration

#### Password Encryption
```properties
# BCrypt configuration
spring.security.bcrypt.strength=12
```

#### Session Management
```properties
# Session timeout (30 minutes)
server.servlet.session.timeout=30m

# Session cookie security
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

## 🐳 Docker Deployment

### Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/restaurant-management-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Create docker-compose.yml
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_USERNAME=restaurant_user
      - DB_PASSWORD=YourSecurePassword123!
      - SMTP_HOST=smtp.gmail.com
      - SMTP_USERNAME=your-email@gmail.com
      - SMTP_PASSWORD=your-app-password
    depends_on:
      - database
    networks:
      - restaurant-network

  database:
    image: mcr.microsoft.com/mssql/server:2019-latest
    environment:
      - ACCEPT_EULA=Y
      - SA_PASSWORD=YourSecurePassword123!
      - MSSQL_PID=Express
    ports:
      - "1433:1433"
    volumes:
      - sqlserver_data:/var/opt/mssql
    networks:
      - restaurant-network

volumes:
  sqlserver_data:

networks:
  restaurant-network:
    driver: bridge
```

### Deploy with Docker
```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 🌐 Web Server Configuration

### Nginx Configuration
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static resources
    location /static/ {
        alias /path/to/static/files/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
}
```

### Apache Configuration
```apache
<VirtualHost *:80>
    ServerName your-domain.com
    DocumentRoot /var/www/html

    ProxyPreserveHost On
    ProxyPass / http://localhost:8080/
    ProxyPassReverse / http://localhost:8080/

    # Security headers
    Header always set X-Frame-Options DENY
    Header always set X-Content-Type-Options nosniff
    Header always set X-XSS-Protection "1; mode=block"
</VirtualHost>
```

## 📊 Monitoring and Logging

### Application Monitoring
```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

### Log Configuration
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/restaurant-system.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/restaurant-system.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

## 🔒 Security Hardening

### SSL/TLS Configuration
```properties
# SSL Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-keystore-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

### Security Headers
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            );
        return http.build();
    }
}
```

## 🚀 Production Deployment

### Environment Setup
```bash
# Create production user
sudo useradd -m -s /bin/bash restaurant
sudo usermod -aG sudo restaurant

# Create application directory
sudo mkdir -p /opt/restaurant-management
sudo chown restaurant:restaurant /opt/restaurant-management

# Create log directory
sudo mkdir -p /var/log/restaurant-system
sudo chown restaurant:restaurant /var/log/restaurant-system
```

### Systemd Service
```ini
# /etc/systemd/system/restaurant-management.service
[Unit]
Description=Restaurant Management System
After=network.target

[Service]
Type=simple
User=restaurant
Group=restaurant
WorkingDirectory=/opt/restaurant-management
ExecStart=/usr/bin/java -jar restaurant-management-system-1.0.0.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Service Management
```bash
# Enable and start service
sudo systemctl enable restaurant-management
sudo systemctl start restaurant-management

# Check status
sudo systemctl status restaurant-management

# View logs
sudo journalctl -u restaurant-management -f
```

## 🔧 Troubleshooting

### Common Issues

#### Database Connection Issues
```bash
# Check database connectivity
telnet localhost 1433

# Test connection string
sqlcmd -S localhost -U restaurant_user -P YourSecurePassword123! -d RestaurantManagement
```

#### Port Already in Use
```bash
# Find process using port 8080
sudo netstat -tulpn | grep :8080

# Kill process
sudo kill -9 <PID>
```

#### Memory Issues
```bash
# Increase heap size
java -Xmx2g -Xms1g -jar restaurant-management-system-1.0.0.jar
```

#### Email Issues
```bash
# Test SMTP connection
telnet smtp.gmail.com 587

# Check email configuration
curl -X POST http://localhost:8080/test-email
```

### Log Analysis
```bash
# View application logs
tail -f logs/restaurant-system.log

# Search for errors
grep -i error logs/restaurant-system.log

# Monitor real-time logs
tail -f logs/restaurant-system.log | grep -i "ERROR\|WARN"
```

## 📈 Performance Optimization

### JVM Tuning
```bash
# Production JVM settings
java -server \
  -Xms2g \
  -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -jar restaurant-management-system-1.0.0.jar
```

### Database Optimization
```sql
-- Create indexes for better performance
CREATE INDEX IX_orders_status ON orders(status);
CREATE INDEX IX_orders_created_at ON orders(created_at);
CREATE INDEX IX_kitchen_tasks_status ON kitchen_tasks(status);
CREATE INDEX IX_customer_feedback_rating ON customer_feedback(rating);
CREATE INDEX IX_customer_feedback_status ON customer_feedback(status);
```

## 🎯 Post-Deployment Checklist

- [ ] Application starts successfully
- [ ] Database connection established
- [ ] All API endpoints responding
- [ ] Email notifications working
- [ ] User authentication functional
- [ ] All user roles can access their dashboards
- [ ] Real-time updates working
- [ ] Logging configured properly
- [ ] Security headers in place
- [ ] SSL certificate installed (if applicable)
- [ ] Monitoring set up
- [ ] Backup procedures in place
- [ ] Performance testing completed

## 📞 Support

For additional support or issues:
1. Check the logs in `/var/log/restaurant-system/`
2. Review the troubleshooting section
3. Check the system status with `systemctl status restaurant-management`
4. Contact the development team with specific error messages

---

## 🎉 Conclusion

This deployment guide provides comprehensive instructions for setting up the Restaurant Management System in various environments. Follow the steps carefully and refer to the troubleshooting section if you encounter any issues.

The system is designed to be scalable, secure, and maintainable, with proper monitoring and logging capabilities for production use.
