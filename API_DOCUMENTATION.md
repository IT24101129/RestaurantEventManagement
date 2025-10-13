# 🍽️ Restaurant Management System - API Documentation

## 📋 Overview

This document provides comprehensive API documentation for the Restaurant Management System, including all endpoints, request/response formats, and integration points.

## 🔐 Authentication

All API endpoints require authentication except for public routes. The system uses Spring Security with form-based authentication.

### Public Endpoints
- `GET /` - Home page
- `GET /login` - Login page
- `POST /login` - Login processing
- `GET /register` - Registration page
- `POST /register` - User registration

## 🎭 Role-Based Access Control

### Manager (`MANAGER`)
- Full system access
- All management functions
- User management
- System configuration

### Head Chef (`CHEF`)
- Kitchen operations
- Order management
- Task assignment
- Inventory management

### Banquet Hall Supervisor (`BANQUET_SUPERVISOR`)
- Event management
- Resource allocation
- Hall management
- Staff assignment

### Customer Relations Officer (`CUSTOMER_RELATIONS_OFFICER`)
- Feedback management
- Customer communication
- Satisfaction reporting
- Issue resolution

### Kitchen Staff (`KITCHEN_STAFF`)
- Task execution
- Order preparation
- Status updates

### Front-end Staff (`FRONT_END_STAFF`)
- Order processing
- Customer service
- Order management

## 📊 API Endpoints by Module

### 1. Manager Module (`/manager/**`)

#### Dashboard
```http
GET /manager/dashboard
```
**Description**: Manager dashboard with system overview
**Access**: Manager only
**Response**: HTML dashboard with system statistics

#### User Management
```http
GET /manager/users
POST /manager/users
PUT /manager/users/{id}
DELETE /manager/users/{id}
```
**Description**: User management operations
**Access**: Manager only

### 2. Kitchen Module (`/kitchen/**`)

#### Kitchen Dashboard
```http
GET /kitchen/dashboard
```
**Description**: Real-time kitchen dashboard
**Access**: Chef, Kitchen Staff
**Response**: HTML dashboard with orders and tasks

#### Order Management
```http
GET /kitchen/api/orders
POST /kitchen/orders/{orderId}/status
```
**Description**: Order management and status updates
**Access**: Chef, Kitchen Staff

#### Task Management
```http
POST /kitchen/tasks/{taskId}/assign
POST /kitchen/tasks/{taskId}/status
GET /kitchen/tasks/pending
```
**Description**: Kitchen task management
**Access**: Chef, Kitchen Staff

#### Inventory Management
```http
GET /kitchen/api/inventory
POST /kitchen/inventory/check-low-stock
```
**Description**: Inventory management and alerts
**Access**: Chef

### 3. Event Management Module (`/events/**`)

#### Event Booking
```http
GET /events/booking
POST /events/booking
GET /events/bookings
```
**Description**: Event booking and management
**Access**: All authenticated users

#### Event Management
```http
POST /events/booking/{id}/status
GET /events/availability
```
**Description**: Event status and availability management
**Access**: All authenticated users

### 4. Banquet Hall Module (`/banquet/**`)

#### Banquet Dashboard
```http
GET /banquet/dashboard
```
**Description**: Banquet hall management dashboard
**Access**: Banquet Hall Supervisor, Manager
**Response**: HTML dashboard with events and resources

#### Event Management
```http
GET /banquet/api/events
POST /banquet/assign-staff
POST /banquet/allocate-equipment
POST /banquet/confirm-schedule/{bookingId}
```
**Description**: Event management and resource allocation
**Access**: Banquet Hall Supervisor, Manager

#### Staff Assignment
```http
GET /banquet/available-staff
GET /banquet/booking/{bookingId}/staff-assignments
```
**Description**: Staff assignment management
**Access**: Banquet Hall Supervisor, Manager

#### Equipment Management
```http
GET /banquet/booking/{bookingId}/equipment-allocations
```
**Description**: Equipment allocation management
**Access**: Banquet Hall Supervisor, Manager

### 5. Customer Relations Module (`/customer-relations/**`)

#### Customer Relations Dashboard
```http
GET /customer-relations/dashboard
```
**Description**: Feedback management dashboard
**Access**: Customer Relations Officer, Manager
**Response**: HTML dashboard with feedback and statistics

#### Feedback Management
```http
GET /customer-relations/api/pending-feedback
GET /customer-relations/api/low-rating-feedback
GET /customer-relations/api/escalated-feedback
GET /customer-relations/api/anonymous-feedback
```
**Description**: Feedback retrieval and management
**Access**: Customer Relations Officer, Manager

#### Response Management
```http
POST /customer-relations/feedback/{id}/respond
POST /customer-relations/feedback/{id}/handle-low-rating
POST /customer-relations/feedback/{id}/send-promotional-offer
POST /customer-relations/feedback/{id}/handle-anonymous
POST /customer-relations/feedback/{id}/handle-escalation
```
**Description**: Feedback response and handling
**Access**: Customer Relations Officer, Manager

#### Follow-up Management
```http
POST /customer-relations/feedback/{id}/schedule-follow-up
```
**Description**: Follow-up reminder scheduling
**Access**: Customer Relations Officer, Manager

#### Reporting
```http
POST /customer-relations/reports/generate
GET /customer-relations/api/statistics
```
**Description**: Satisfaction reporting and analytics
**Access**: Customer Relations Officer, Manager

### 6. Reservation Module (`/reservations/**`)

#### Reservation Management
```http
GET /reservations/dashboard
POST /reservations
GET /reservations/api/availability
POST /reservations/{id}/status
```
**Description**: Table reservation management
**Access**: All authenticated users

### 7. Schedule Module (`/schedule/**`)

#### Staff Scheduling
```http
GET /schedule/dashboard
POST /schedule/assign
GET /schedule/api/availability
```
**Description**: Staff scheduling management
**Access**: Manager, Schedule Manager

## 📝 Request/Response Formats

### Standard Response Format

#### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data
  }
}
```

#### Error Response
```json
{
  "success": false,
  "error": "Error message describing what went wrong",
  "code": "ERROR_CODE"
}
```

### Common Request Formats

#### Order Status Update
```json
{
  "status": "IN_PREPARATION",
  "notes": "Optional notes"
}
```

#### Task Assignment
```json
{
  "staffId": 123,
  "assignedBy": "chef@restaurant.com",
  "notes": "Priority task"
}
```

#### Feedback Response
```json
{
  "responseType": "ACKNOWLEDGMENT",
  "responseText": "Thank you for your feedback...",
  "isInternal": false,
  "escalationNotes": null,
  "followUpRequired": false,
  "followUpDate": null
}
```

#### Event Staff Assignment
```json
{
  "staffId": 456,
  "role": "Server",
  "assignedHours": 8,
  "startTime": "18:00",
  "endTime": "02:00"
}
```

#### Equipment Allocation
```json
{
  "equipmentName": "Sound System",
  "equipmentType": "Audio",
  "quantity": 2,
  "unitCost": 50.00
}
```

## 🔄 Real-time Updates

### WebSocket Endpoints (Future Enhancement)
```javascript
// Real-time order updates
ws://localhost:8080/ws/orders
// Real-time task updates
ws://localhost:8080/ws/tasks
// Real-time feedback updates
ws://localhost:8080/ws/feedback
```

### AJAX Polling (Current Implementation)
```javascript
// Poll every 5 seconds for kitchen updates
setInterval(refreshKitchenData, 5000);

// Poll every 30 seconds for feedback updates
setInterval(refreshFeedbackData, 30000);

// Poll every 10 seconds for banquet updates
setInterval(refreshBanquetData, 10000);
```

## 📊 Data Models

### Order Model
```json
{
  "id": 1,
  "customerName": "John Doe",
  "status": "PENDING",
  "totalAmount": 45.50,
  "createdAt": "2023-12-01T10:30:00",
  "items": [
    {
      "id": 1,
      "itemName": "Pizza Margherita",
      "quantity": 2,
      "price": 15.00
    }
  ]
}
```

### Kitchen Task Model
```json
{
  "id": 1,
  "orderId": 1,
  "taskName": "Prepare Pizza Margherita",
  "status": "ASSIGNED",
  "assignedTo": "chef@restaurant.com",
  "priority": "HIGH",
  "estimatedTime": 15
}
```

### Event Booking Model
```json
{
  "id": 1,
  "eventName": "Wedding Reception",
  "clientName": "Jane Smith",
  "eventDate": "2023-12-15",
  "startTime": "18:00",
  "endTime": "23:00",
  "guestCount": 100,
  "status": "PENDING"
}
```

### Customer Feedback Model
```json
{
  "id": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "rating": 4,
  "feedbackType": "SERVICE",
  "comment": "Great service, friendly staff",
  "status": "PENDING",
  "priority": "MEDIUM",
  "isAnonymous": false
}
```

## 🔧 Error Handling

### HTTP Status Codes
- `200 OK` - Successful request
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Common Error Scenarios
1. **Authentication Errors**
   - Invalid credentials
   - Session expired
   - Insufficient permissions

2. **Validation Errors**
   - Missing required fields
   - Invalid data format
   - Business rule violations

3. **System Errors**
   - Database connection issues
   - External service failures
   - Internal processing errors

## 🚀 Integration Examples

### Frontend Integration
```javascript
// Kitchen dashboard real-time updates
function refreshKitchenData() {
    fetch('/kitchen/api/orders')
        .then(response => response.json())
        .then(data => {
            updateOrderDisplay(data.orders);
            updateTaskDisplay(data.tasks);
        })
        .catch(error => console.error('Error:', error));
}

// Feedback response submission
function submitFeedbackResponse(feedbackId, responseData) {
    fetch(`/customer-relations/feedback/${feedbackId}/respond`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(responseData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showSuccessMessage(data.message);
            refreshFeedbackData();
        } else {
            showErrorMessage(data.error);
        }
    });
}
```

### Backend Service Integration
```java
// Kitchen service integration
@Autowired
private KitchenService kitchenService;

@Autowired
private NotificationService notificationService;

public void processOrder(Order order) {
    // Process order
    Order processedOrder = kitchenService.processOrder(order);
    
    // Create tasks
    List<KitchenTask> tasks = kitchenService.createTasksForOrder(processedOrder);
    
    // Send notifications
    notificationService.sendOrderReadyNotification(processedOrder);
}
```

## 📈 Performance Considerations

### Caching Strategy
- Database query caching
- Session data caching
- Static resource caching

### Database Optimization
- Proper indexing
- Query optimization
- Connection pooling

### Async Processing
- Email notifications
- Report generation
- Background tasks

## 🔒 Security Considerations

### Input Validation
- Server-side validation
- SQL injection prevention
- XSS protection

### Authentication
- Secure session management
- Password encryption
- Role-based access control

### Data Protection
- Sensitive data encryption
- Audit logging
- Access monitoring

## 📱 Mobile API Support

### Responsive Design
- Mobile-optimized endpoints
- Touch-friendly interfaces
- Progressive Web App support

### API Versioning
- Version 1.0 (Current)
- Future versioning strategy
- Backward compatibility

## 🧪 Testing

### API Testing
- Unit tests for all endpoints
- Integration tests
- Performance tests

### Test Data
- Sample data sets
- Test user accounts
- Mock external services

## 📚 Additional Resources

### Documentation
- System overview
- User guides
- Developer documentation

### Support
- Error reporting
- Feature requests
- Technical support

---

## 🎯 Conclusion

This API documentation provides comprehensive coverage of all endpoints and functionalities in the Restaurant Management System. The modular design allows for easy integration and extension, while the comprehensive error handling and security measures ensure reliable operation.

For additional support or questions, please refer to the system overview documentation or contact the development team.
