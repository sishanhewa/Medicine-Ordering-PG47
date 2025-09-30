package com.example.medicineordering.repository;

import com.example.medicineordering.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DriverRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public DriverRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Driver> findAll() {
        return jdbc.query("SELECT * FROM Drivers ORDER BY name",
                new BeanPropertyRowMapper<>(Driver.class));
    }

    public List<Driver> findAvailableDrivers() {
        return jdbc.query("SELECT * FROM Drivers WHERE available = 1 ORDER BY name",
                new BeanPropertyRowMapper<>(Driver.class));
    }

    public Optional<Driver> findById(int id) {
        String sql = "SELECT * FROM Drivers WHERE id = ?";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Driver.class), id)
                .stream().findFirst();
    }

    public void save(Driver d) {
        String sql = "INSERT INTO Drivers(name,email,phone,vehicleType,licensePlate,serviceArea,available) " +
                "VALUES (?,?,?,?,?,?,?)";
        jdbc.update(sql, d.getName(), d.getEmail(), d.getPhone(),
                d.getVehicleType(), d.getLicensePlate(), d.getServiceArea(), d.isAvailable());
    }

    public void deleteById(int id) {
        jdbc.update("DELETE FROM Drivers WHERE id = ?", id);
    }

    public int countActiveDeliveries(int driverId) {
        Integer c = jdbc.queryForObject(
                "SELECT COUNT(*) FROM Deliveries WHERE driverId = ? AND status IN ('Assigned','Picked_Up','In_Transit')",
                Integer.class, driverId);
        return c == null ? 0 : c;
    }

    public void setAvailability(int driverId, boolean available) {
        jdbc.update("UPDATE Drivers SET available = ? WHERE id = ?", available, driverId);
    }
}
