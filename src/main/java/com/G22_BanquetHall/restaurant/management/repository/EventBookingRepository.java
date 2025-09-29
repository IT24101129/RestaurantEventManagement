package com.G22_BanquetHall.restaurant.management.repository;

import com.G22_BanquetHall.restaurant.management.model.EventBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class EventBookingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<EventBooking> rowMapper = new RowMapper<EventBooking>() {
        @Override
        public EventBooking mapRow(ResultSet rs, int rowNum) throws SQLException {
            EventBooking booking = new EventBooking();
            booking.setEventId(rs.getLong("EventID"));
            booking.setType(rs.getString("Type"));
            booking.setNumGuests(rs.getInt("NumGuests"));
            booking.setTotalCost(rs.getDouble("TotalCost"));
            booking.setRequirements(rs.getString("Requirements"));
            booking.setSpecialNotes(rs.getString("SpecialNotes"));
            booking.setStatus(rs.getString("Status"));
            booking.setHallId(rs.getLong("HallID"));
            booking.setCoordinatorUserId(rs.getLong("CoordinatorUserID"));
            booking.setScheduleId(rs.getLong("ScheduleID"));

            // Handle null timestamps safely
            java.sql.Timestamp startTimestamp = rs.getTimestamp("StartTime");
            booking.setStartTime(startTimestamp != null ? startTimestamp.toLocalDateTime() : null);

            java.sql.Timestamp endTimestamp = rs.getTimestamp("EndTime");
            booking.setEndTime(endTimestamp != null ? endTimestamp.toLocalDateTime() : null);

            return booking;
        }
    };

    public boolean isValidHallId(Long hallId) {
        if (hallId == null || hallId <= 0) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM BanquetHall WHERE HallID = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hallId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, Object>> getAvailableHalls() {
        String sql = "SELECT HallID, PackageName, Capacity FROM BanquetHall ORDER BY PackageName";
        return jdbcTemplate.queryForList(sql);
    }

    public EventBooking save(EventBooking booking) {
        // Validate HallID exists before inserting
        if (!isValidHallId(booking.getHallId())) {
            throw new IllegalArgumentException("Invalid HallID: " + booking.getHallId() + ". Hall does not exist.");
        }

        // Validate required fields
        if (booking.getType() == null || booking.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Event type is required");
        }

        // Insert into Event
        String eventSql = "INSERT INTO [Event] ([Type], NumGuests, TotalCost, Requirements, SpecialNotes, [Status], CoordinatorUserID, HallID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(eventSql,
                booking.getType(),
                booking.getNumGuests(),
                booking.getTotalCost(),
                booking.getRequirements(),
                booking.getSpecialNotes(),
                booking.getStatus(),
                booking.getCoordinatorUserId(),
                booking.getHallId());

        // Get the last inserted EventID
        Long eventId = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Long.class);
        booking.setEventId(eventId);

        // Insert into Schedule
        String scheduleSql = "INSERT INTO Schedule ([Type], StartTime, EndTime, HallID, Availability) VALUES (?, ?, ?, ?, 'Booked')";
        jdbcTemplate.update(scheduleSql,
                booking.getType(),
                booking.getStartTime() != null ? java.sql.Timestamp.valueOf(booking.getStartTime()) : null,
                booking.getEndTime() != null ? java.sql.Timestamp.valueOf(booking.getEndTime()) : null,
                booking.getHallId());

        // Get the last inserted ScheduleID
        Long scheduleId = jdbcTemplate.queryForObject("SELECT SCOPE_IDENTITY()", Long.class);
        booking.setScheduleId(scheduleId);

        return booking;
    }

    public List<EventBooking> findAll() {
        String sql = "SELECT e.*, s.ScheduleID, s.StartTime, s.EndTime " +
                "FROM [Event] e " +
                "LEFT JOIN Schedule s ON e.HallID = s.HallID AND s.Availability = 'Booked' " +
                "ORDER BY s.StartTime DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public EventBooking findById(Long id) {
        String sql = "SELECT e.*, s.ScheduleID, s.StartTime, s.EndTime " +
                "FROM [Event] e " +
                "LEFT JOIN Schedule s ON e.HallID = s.HallID AND s.Availability = 'Booked' " +
                "WHERE e.EventID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<EventBooking> findPendingBookings() {
        String sql = "SELECT e.*, s.ScheduleID, s.StartTime, s.EndTime " +
                "FROM [Event] e " +
                "LEFT JOIN Schedule s ON e.HallID = s.HallID AND s.Availability = 'Booked' " +
                "WHERE e.[Status] = 'PENDING' ORDER BY s.StartTime ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<EventBooking> findUpcomingEvents() {
        String sql = "SELECT e.*, s.ScheduleID, s.StartTime, s.EndTime " +
                "FROM [Event] e " +
                "LEFT JOIN Schedule s ON e.HallID = s.HallID AND s.Availability = 'Booked' " +
                "WHERE e.[Status] = 'APPROVED' AND s.StartTime > ? ORDER BY s.StartTime ASC";
        return jdbcTemplate.query(sql, rowMapper, LocalDateTime.now());
    }

    public void approveBooking(Long id) {
        String sql = "UPDATE [Event] SET [Status] = 'APPROVED' WHERE EventID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void rejectBooking(Long id) {
        String sql = "UPDATE [Event] SET [Status] = 'REJECTED' WHERE EventID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void assignResources(Long id, String staff, String equipment) {
        // Placeholder: Update logic to use Event_Equipment or a new table
    }

    public boolean hasConflict(Long hallId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        String sql = "SELECT COUNT(*) FROM Schedule " +
                "WHERE HallID = ? AND Availability = 'Booked' " +
                "AND ((StartTime BETWEEN ? AND ?) OR (EndTime BETWEEN ? AND ?)) " +
                "AND ScheduleID != ?";
        if (excludeBookingId == null) {
            sql = "SELECT COUNT(*) FROM Schedule " +
                    "WHERE HallID = ? AND Availability = 'Booked' " +
                    "AND ((StartTime BETWEEN ? AND ?) OR (EndTime BETWEEN ? AND ?))";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hallId, start, end, start, end);
            return count != null && count > 0;
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hallId, start, end, start, end, excludeBookingId);
        return count != null && count > 0;
    }
}