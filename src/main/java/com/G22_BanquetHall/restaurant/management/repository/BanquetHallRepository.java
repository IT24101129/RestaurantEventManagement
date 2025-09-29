package com.G22_BanquetHall.restaurant.management.repository;



import com.G22_BanquetHall.restaurant.management.model.BanquetHall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



    @Repository
    public class BanquetHallRepository {
        @Autowired
        private JdbcTemplate jdbcTemplate;

        private final RowMapper<BanquetHall> rowMapper = new RowMapper<BanquetHall>() {
            @Override
            public BanquetHall mapRow(ResultSet rs, int rowNum) throws SQLException {
                BanquetHall hall = new BanquetHall();
                hall.setHallId(rs.getLong("HallID"));
                hall.setPackageName(rs.getString("PackageName"));
                hall.setCapacity(rs.getInt("Capacity"));
                hall.setFacilities(rs.getString("Facilities"));
                hall.setSupervisorUserId(rs.getLong("SupervisorUserID"));
                return hall;
            }
        };

        public List<BanquetHall> findAll() {
            String sql = "SELECT * FROM BanquetHall";
            return jdbcTemplate.query(sql, rowMapper);
        }

        public BanquetHall findById(Long id) {
            String sql = "SELECT * FROM BanquetHall WHERE HallID = ?";
            try {
                return jdbcTemplate.queryForObject(sql, rowMapper, id);
            } catch (Exception e) {
                return null;
            }
        }

        public void updateAvailability(Long id, Boolean available) {
            // Note: 'available' is not a column in BanquetHall; you might need to update based on Schedule
            String sql = "UPDATE BanquetHall SET /* Add relevant column */ = ? WHERE HallID = ?";
            jdbcTemplate.update(sql, available, id); // Adjust logic as needed
        }
    }

    ;

