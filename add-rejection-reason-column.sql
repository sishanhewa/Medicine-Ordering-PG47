-- Manual script to add rejectionReason column to existing Prescriptions table
-- Run this in SQL Server Management Studio if the migration doesn't work

USE MedicineDB;
GO

-- Add rejectionReason column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason')
BEGIN
    ALTER TABLE dbo.Prescriptions ADD rejectionReason NVARCHAR(500) NULL;
    PRINT 'rejectionReason column added successfully';
END
ELSE
BEGIN
    PRINT 'rejectionReason column already exists';
END
GO
