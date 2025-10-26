-- Create Customer Support Tables
-- Run this script in SQL Server Management Studio or Azure Data Studio
-- Connect to your MedicineDB database and execute this script

USE MedicineDB;
GO

-- Create Messages table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Messages' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Messages (
        id BIGINT IDENTITY PRIMARY KEY,
        content NVARCHAR(MAX) NOT NULL,
        sender NVARCHAR(255) NOT NULL,
        receiver NVARCHAR(255) NOT NULL,
        timestamp DATETIME2 DEFAULT GETDATE(),
        status NVARCHAR(50) DEFAULT 'unread',
        archived BIT DEFAULT 0,
        conversationId BIGINT NULL,
        parentMessageId BIGINT NULL
    );
    PRINT 'Messages table created successfully';
END
ELSE
BEGIN
    PRINT 'Messages table already exists';
END
GO

-- Create Notifications table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Notifications' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Notifications (
        id BIGINT IDENTITY PRIMARY KEY,
        type NVARCHAR(100) NOT NULL,
        content NVARCHAR(MAX) NOT NULL,
        recipient NVARCHAR(255) NOT NULL,
        timestamp DATETIME2 DEFAULT GETDATE()
    );
    PRINT 'Notifications table created successfully';
END
ELSE
BEGIN
    PRINT 'Notifications table already exists';
END
GO

-- Create Issues table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Issues' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.Issues (
        id BIGINT IDENTITY PRIMARY KEY,
        status NVARCHAR(50) NOT NULL,
        relatedMessageId BIGINT,
        archived BIT DEFAULT 0,
        createdAt DATETIME2 DEFAULT GETDATE()
    );
    PRINT 'Issues table created successfully';
END
ELSE
BEGIN
    PRINT 'Issues table already exists';
END
GO

-- Insert some test data
INSERT INTO dbo.Messages (content, sender, receiver, timestamp, status, archived) VALUES
('Hello, I need help with my order', 'customer1@test.com', 'support@crystalcare.com', GETDATE(), 'unread', 0),
('My prescription was rejected, can you help?', 'customer1@test.com', 'support@crystalcare.com', GETDATE() - 1, 'read', 0);

INSERT INTO dbo.Notifications (type, content, recipient, timestamp) VALUES
('order_status', 'Your order has been processed successfully', 'customer1@test.com', GETDATE()),
('general', 'Welcome to Crystal Care! We are here to help with your medicine needs.', 'customer1@test.com', GETDATE() - 1);

PRINT 'Customer support tables and test data created successfully!';
