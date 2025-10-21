-- Migration script to fix Prescriptions table structure
-- This handles existing tables that might have the old column names

-- Add orderId column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'orderId')
BEGIN
    ALTER TABLE dbo.Prescriptions ADD orderId INT NULL;
END

-- Rename uploadedAt to uploadDate if uploadedAt exists and uploadDate doesn't
IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'uploadedAt')
   AND NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'uploadDate')
BEGIN
    EXEC sp_rename 'dbo.Prescriptions.uploadedAt', 'uploadDate', 'COLUMN';
END

-- Add rejectionReason column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Prescriptions') AND name = 'rejectionReason')
BEGIN
    ALTER TABLE dbo.Prescriptions ADD rejectionReason NVARCHAR(500) NULL;
END

-- Update status values to match the new format
UPDATE dbo.Prescriptions SET status = 'PENDING' WHERE status = 'Pending' OR status = 'pending';
UPDATE dbo.Prescriptions SET status = 'APPROVED' WHERE status = 'Approved' OR status = 'approved';
UPDATE dbo.Prescriptions SET status = 'REJECTED' WHERE status = 'Rejected' OR status = 'rejected';
