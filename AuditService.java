package com.resturant.restaurantapp.service;

import com.resturant.restaurantapp.model.Event;
import com.resturant.restaurantapp.model.EventAudit;
import com.resturant.restaurantapp.repository.EventAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private EventAuditRepository eventAuditRepository;

    @Transactional
    public void logEventCreation(Event event, String changedBy) {
        EventAudit audit = new EventAudit();
        audit.setEventId(event.getEventId());
        audit.setEventPrimaryId(event.getId());
        audit.setChangeType(EventAudit.ChangeType.INSERT);
        audit.setChangedBy(changedBy);
        audit.setChangedAt(LocalDateTime.now());
        audit.setFieldName("EVENT_CREATED");
        audit.setOldValue(null);
        audit.setNewValue("Event created with ID: " + event.getEventId());
        
        eventAuditRepository.save(audit);
    }

    @Transactional
    public void logEventUpdate(Event oldEvent, Event newEvent, String changedBy) {
        List<EventAudit> auditEntries = new ArrayList<>();
        
        // Compare all fields and log changes
        Field[] fields = Event.class.getDeclaredFields();
        
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object oldValue = field.get(oldEvent);
                Object newValue = field.get(newEvent);
                
                // Skip certain fields that shouldn't be audited
                if (shouldSkipField(field.getName())) {
                    continue;
                }
                
                // Check if value actually changed
                if (!areEqual(oldValue, newValue)) {
                    EventAudit audit = new EventAudit();
                    audit.setEventId(newEvent.getEventId());
                    audit.setEventPrimaryId(newEvent.getId());
                    audit.setChangeType(EventAudit.ChangeType.UPDATE);
                    audit.setChangedBy(changedBy);
                    audit.setChangedAt(LocalDateTime.now());
                    audit.setFieldName(field.getName());
                    audit.setOldValue(convertToString(oldValue));
                    audit.setNewValue(convertToString(newValue));
                    
                    auditEntries.add(audit);
                }
            } catch (IllegalAccessException e) {
                System.err.println("Error accessing field " + field.getName() + ": " + e.getMessage());
            }
        }
        
        // Save all audit entries
        if (!auditEntries.isEmpty()) {
            eventAuditRepository.saveAll(auditEntries);
        }
    }

    @Transactional
    public void logEventDeletion(Event event, String changedBy) {
        EventAudit audit = new EventAudit();
        audit.setEventId(event.getEventId());
        audit.setEventPrimaryId(event.getId());
        audit.setChangeType(EventAudit.ChangeType.DELETE);
        audit.setChangedBy(changedBy);
        audit.setChangedAt(LocalDateTime.now());
        audit.setFieldName("EVENT_DELETED");
        audit.setOldValue("Event with ID: " + event.getEventId());
        audit.setNewValue(null);
        
        eventAuditRepository.save(audit);
    }

    public List<EventAudit> getAuditHistoryByEventId(String eventId) {
        return eventAuditRepository.findByEventIdOrderByChangedAtDesc(eventId);
    }

    public List<EventAudit> getAuditHistoryByEventPrimaryId(Long eventPrimaryId) {
        return eventAuditRepository.findByEventPrimaryIdOrderByChangedAtDesc(eventPrimaryId);
    }

    public List<EventAudit> getAllAuditHistory() {
        return eventAuditRepository.findAllOrderByChangedAtDesc();
    }

    public List<EventAudit> getAuditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return eventAuditRepository.findByDateRange(startDate, endDate);
    }

    public List<EventAudit> getAuditHistoryByUser(String changedBy) {
        return eventAuditRepository.findByChangedByOrderByChangedAtDesc(changedBy);
    }

    private boolean shouldSkipField(String fieldName) {
        // Skip fields that shouldn't be audited
        return fieldName.equals("id") || 
               fieldName.equals("serialVersionUID") ||
               fieldName.equals("$jacocoData");
    }

    private boolean areEqual(Object oldValue, Object newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }
        if (oldValue == null || newValue == null) {
            return false;
        }
        return oldValue.equals(newValue);
    }

    private String convertToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return value.toString();
        }
        return value.toString();
    }
}

