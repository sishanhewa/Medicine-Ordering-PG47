-- Create AdminUsers table for IT Admin panel
-- This table is separate from the main Users table to avoid conflicts

-- Create AdminUsers table if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'AdminUsers' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
    CREATE TABLE dbo.AdminUsers (
        id BIGINT IDENTITY PRIMARY KEY,
        username NVARCHAR(255) NOT NULL UNIQUE,
        passwordHash NVARCHAR(255) NOT NULL,
        fullName NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL UNIQUE,
        phone NVARCHAR(50),
        role NVARCHAR(50) NOT NULL,
        active BIT DEFAULT 1,
        createdAt DATETIME2 DEFAULT GETDATE(),
        updatedAt DATETIME2 DEFAULT GETDATE()
    );
    PRINT 'AdminUsers table created successfully';
END
ELSE
BEGIN
    PRINT 'AdminUsers table already exists';
END
GO

-- Insert test admin user if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM dbo.AdminUsers WHERE username = 'admin1')
BEGIN
    INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active) VALUES
    ('admin1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'System Administrator', 'admin@crystalcare.com', '+94 77 000 0000', 'ADMIN', 1);
    PRINT 'Test admin user created';
END
ELSE
BEGIN
    PRINT 'Test admin user already exists';
END
GO

-- Insert some test users for different roles
IF NOT EXISTS (SELECT 1 FROM dbo.AdminUsers WHERE username = 'delivery_manager1')
BEGIN
    INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active) VALUES
    ('delivery_manager1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'John Delivery Manager', 'delivery@crystalcare.com', '+94 77 111 1111', 'DELIVERY_MANAGER', 1);
    PRINT 'Test delivery manager created';
END

IF NOT EXISTS (SELECT 1 FROM dbo.AdminUsers WHERE username = 'pharmacist1')
BEGIN
    INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active) VALUES
    ('pharmacist1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Dr. Sarah Pharmacist', 'pharmacist@crystalcare.com', '+94 77 222 2222', 'PHARMACIST', 1);
    PRINT 'Test pharmacist created';
END

IF NOT EXISTS (SELECT 1 FROM dbo.AdminUsers WHERE username = 'finance_manager1')
BEGIN
    INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active) VALUES
    ('finance_manager1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Mike Finance Manager', 'finance@crystalcare.com', '+94 77 333 3333', 'FINANCE_MANAGER', 1);
    PRINT 'Test finance manager created';
END

IF NOT EXISTS (SELECT 1 FROM dbo.AdminUsers WHERE username = 'customer1')
BEGIN
    INSERT INTO dbo.AdminUsers (username, passwordHash, fullName, email, phone, role, active) VALUES
    ('customer1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Jane Customer', 'customer@crystalcare.com', '+94 77 444 4444', 'CUSTOMER', 1);
    PRINT 'Test customer created';
END

PRINT 'Admin users setup completed successfully';
GO



