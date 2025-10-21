package com.example.medicineordering.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DriverExtrasRepository {
    @Autowired private JdbcTemplate jdbc;

    public void upsertShiftTime(int driverId, String shiftTime) {
        jdbc.update("""
            MERGE DriverProfiles AS T
            USING (SELECT ? AS driverId, ? AS shiftTime) AS S
            ON T.driverId = S.driverId
            WHEN MATCHED THEN UPDATE SET shiftTime=S.shiftTime
            WHEN NOT MATCHED THEN INSERT(driverId,shiftTime) VALUES(S.driverId,S.shiftTime);
        """, driverId, shiftTime);
    }

    public String findShiftTime(int driverId) {
        try {
            return jdbc.queryForObject("SELECT shiftTime FROM DriverProfiles WHERE driverId=?", String.class, driverId);
        } catch (Exception e) { return null; }
    }
}