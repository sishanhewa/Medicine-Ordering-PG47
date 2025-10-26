-- Create Payments table for Finance Management
-- This script should be run manually on the database

-- Check if Payments table exists, if not create it
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Payments' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Payments (
        id BIGINT IDENTITY PRIMARY KEY,
        patientName NVARCHAR(100) NOT NULL,
        medicineName NVARCHAR(100) NOT NULL,
        amount DECIMAL(12,2) NOT NULL,
        status NVARCHAR(20) DEFAULT 'PENDING',
        paymentDate DATETIME2 DEFAULT GETDATE(),
        notes NVARCHAR(500)
    );
    
    PRINT 'Payments table created successfully.';
END
ELSE
BEGIN
    PRINT 'Payments table already exists.';
END

-- Verify table creation
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Payments' 
ORDER BY ORDINAL_POSITION;

-- Add some sample data for testing
IF NOT EXISTS (SELECT 1 FROM dbo.Payments)
BEGIN
    INSERT INTO dbo.Payments (patientName, medicineName, amount, status, paymentDate, notes) VALUES
    ('John Doe', 'Aspirin 100mg', 25.50, 'PENDING', GETDATE(), 'Monthly prescription refill'),
    ('Jane Smith', 'Paracetamol 500mg', 15.75, 'APPROVED', GETDATE() - 1, 'Pain relief medication'),
    ('Bob Johnson', 'Vitamin D3', 45.00, 'REJECTED', GETDATE() - 2, 'Insurance coverage issue'),
    ('Alice Brown', 'Metformin 500mg', 30.25, 'PENDING', GETDATE(), 'Diabetes management'),
    ('Charlie Wilson', 'Lisinopril 10mg', 22.80, 'APPROVED', GETDATE() - 1, 'Blood pressure medication');
    
    PRINT 'Sample payment data inserted successfully.';
END
ELSE
BEGIN
    PRINT 'Payments table already contains data.';
END

-- Show current data
SELECT COUNT(*) as 'Total Payments' FROM dbo.Payments;
SELECT status, COUNT(*) as 'Count' FROM dbo.Payments GROUP BY status;



