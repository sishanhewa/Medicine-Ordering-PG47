package com.example.medicineordering.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DeliveryExtrasRepository {

    private final JdbcTemplate jdbc;

    @Autowired
    public DeliveryExtrasRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** ------------------------- READS (for driver dashboard) ------------------------- */

    /**
     * Assigned list for a driver.
     * Returns ALL fields the driver UI expects:
     *  - id (alias of delivery id), deliveryId (kept too), orderNumber, customerName, deliveryAddress
     *  - deliveryWindow, weight, itemCount, priority, customerPhone, assignedTime
     */
    public List<Map<String,Object>> findAssignedForDriver(int driverId) {
        String sql = """
            SELECT
                d.id                                         AS id,             -- <— for template: delivery.id
                d.id                                         AS deliveryId,
                o.orderNumber,
                o.customerName,
                o.deliveryAddress,
                o.deliveryWindow,
                COALESCE(TRY_CAST(o.weight AS float), 0)     AS weight,
                1                                            AS itemCount,
                'normal'                                     AS priority,
                ''                                           AS customerPhone,   -- use your real column if you have it
                CONVERT(varchar(5),
                    COALESCE(t.assignedTime, SYSUTCDATETIME()), 108) AS assignedTime
            FROM dbo.Deliveries d
            JOIN dbo.Orders o            ON o.id = d.orderId
            LEFT JOIN dbo.DeliveryTimeline t ON t.deliveryId = d.id
            WHERE d.driverId = ? AND d.status = 'Assigned'
            ORDER BY d.id DESC
            """;
        return jdbc.queryForList(sql, driverId);
    }

    /**
     * In-progress list for a driver.
     * Returns: id, deliveryId, orderNumber, customerName, deliveryAddress, eta, pickedUpTime, inTransitTime, priority, customerPhone
     */
    public List<Map<String,Object>> findInProgressForDriver(int driverId) {
        String sql = """
            SELECT
                d.id                                         AS id,             -- <— for template: delivery.id
                d.id                                         AS deliveryId,
                o.orderNumber,
                o.customerName,
                o.deliveryAddress,
                COALESCE(t.eta, '')                          AS eta,
                CONVERT(varchar(5), t.pickedUpTime, 108)     AS pickedUpTime,
                CONVERT(varchar(5), t.inTransitTime, 108)    AS inTransitTime,
                'normal'                                     AS priority,
                ''                                           AS customerPhone   -- use your real column if you have it
            FROM dbo.Deliveries d
            JOIN dbo.Orders o            ON o.id = d.orderId
            LEFT JOIN dbo.DeliveryTimeline t ON t.deliveryId = d.id
            WHERE d.driverId = ? AND d.status IN ('picked_up','in_transit')
            ORDER BY d.id DESC
            """;
        return jdbc.queryForList(sql, driverId);
    }

    /**
     * Completed today list for a driver.
     * Returns: id, deliveryId, orderNumber, customerName, deliveryAddress, completedTime, priority, customerPhone
     */
    public List<Map<String,Object>> findCompletedTodayForDriver(int driverId) {
        String sql = """
            SELECT
                d.id                                         AS id,             -- <— for template: delivery.id
                d.id                                         AS deliveryId,
                o.orderNumber,
                o.customerName,
                o.deliveryAddress,
                CONVERT(varchar(5), t.completedTime, 108)    AS completedTime,
                'normal'                                     AS priority,
                ''                                           AS customerPhone   -- use your real column if you have it
            FROM dbo.Deliveries d
            JOIN dbo.Orders o            ON o.id = d.orderId
            LEFT JOIN dbo.DeliveryTimeline t ON t.deliveryId = d.id
            WHERE d.driverId = ? AND d.status = 'delivered'
              AND CONVERT(date, t.completedTime) = CONVERT(date, SYSUTCDATETIME())
            ORDER BY t.completedTime DESC
            """;
        return jdbc.queryForList(sql, driverId);
    }

    /** ------------------------- MUTATIONS (driver actions) ------------------------- */

    public void ensureTimelineRow(int deliveryId) {
        String merge = """
            MERGE dbo.DeliveryTimeline AS T
            USING (SELECT ? AS deliveryId) AS S
            ON T.deliveryId = S.deliveryId
            WHEN NOT MATCHED THEN
                INSERT (deliveryId, assignedTime) VALUES (S.deliveryId, SYSUTCDATETIME());
        """;
        jdbc.update(merge, deliveryId);
    }

    /** sets pickedUpTime and inTransitTime if missing */
    public void markPickedUpAndInTransit(int deliveryId) {
        ensureTimelineRow(deliveryId);
        jdbc.update(
                "UPDATE dbo.DeliveryTimeline SET pickedUpTime = COALESCE(pickedUpTime, SYSUTCDATETIME()) WHERE deliveryId=?",
                deliveryId
        );
        jdbc.update(
                "UPDATE dbo.DeliveryTimeline SET inTransitTime = COALESCE(inTransitTime, SYSUTCDATETIME()) WHERE deliveryId=?",
                deliveryId
        );
    }

    public void setEta(int deliveryId, String eta) {
        ensureTimelineRow(deliveryId);
        jdbc.update("UPDATE dbo.DeliveryTimeline SET eta=? WHERE deliveryId=?", eta, deliveryId);
    }

    public void markCompleted(int deliveryId) {
        ensureTimelineRow(deliveryId);
        jdbc.update(
                "UPDATE dbo.DeliveryTimeline SET completedTime = COALESCE(completedTime, SYSUTCDATETIME()) WHERE deliveryId=?",
                deliveryId
        );
    }

    public void upsertProof(int deliveryId, String recipientName, String notes, String photoUrl, String signaturePath) {
        String merge = """
            MERGE dbo.DeliveryProofs AS T
            USING (SELECT ? AS deliveryId) AS S
            ON T.deliveryId = S.deliveryId
            WHEN MATCHED THEN
              UPDATE SET recipientName=?, notes=?, proofPhotoUrl=?, signaturePath=?, createdAt=SYSUTCDATETIME()
            WHEN NOT MATCHED THEN
              INSERT(deliveryId, recipientName, notes, proofPhotoUrl, signaturePath)
              VALUES(?, ?, ?, ?, ?);
        """;
        jdbc.update(merge,
                deliveryId,
                recipientName, notes, photoUrl, signaturePath,
                deliveryId, recipientName, notes, photoUrl, signaturePath);
    }

    public void insertIssue(int deliveryId, String issueType, String description, String photoUrl, String action) {
        jdbc.update(
                "INSERT INTO dbo.DeliveryIssues(deliveryId, issueType, description, photoUrl, actionTaken) VALUES (?,?,?,?,?)",
                deliveryId, issueType, description, photoUrl, action
        );
    }
}