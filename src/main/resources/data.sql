-- Update existing test users with correct password hash
-- Password for all test users is: password123

-- Update existing users with correct password hash
UPDATE dbo.Users SET passwordHash = '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i' WHERE username = 'customer1';
UPDATE dbo.Users SET passwordHash = '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i' WHERE username = 'manager1';
UPDATE dbo.Users SET passwordHash = '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i' WHERE username = 'driver1';

-- Insert test users for login testing (only if they don't exist)
-- Test Customer (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'customer1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('customer1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Customer', 'customer1@test.com', 'John Customer', '+94 77 123 4567', 1, GETDATE());
END

-- Test Manager (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'manager1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('manager1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'DeliveryManager', 'manager1@test.com', 'Jane Manager', '+94 77 234 5678', 1, GETDATE());
END

-- Test Driver (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'driver1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('driver1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'DeliveryPersonnel', 'driver1@test.com', 'Bob Driver', '+94 77 345 6789', 1, GETDATE());
END

-- Create corresponding Customer records (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Customers WHERE id = 1)
BEGIN
    INSERT INTO dbo.Customers (id, name, email, phone, password, address) VALUES
    (1, 'John Customer', 'customer1@test.com', '+94 77 123 4567', '', '123 Test Street, Colombo 05, Sri Lanka');
END

-- Update existing drivers with passwords (if they don't have them)
UPDATE dbo.Drivers SET passwordHash = '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i' 
WHERE passwordHash IS NULL OR passwordHash = '' OR passwordHash = 'NULL';

-- Add test driver if not exists
IF NOT EXISTS (SELECT 1 FROM dbo.Drivers WHERE email = 'testdriver@test.com')
BEGIN
    INSERT INTO dbo.Drivers (name, email, phone, passwordHash, vehicleType, licensePlate, serviceArea, available) VALUES
    ('Test Driver', 'testdriver@test.com', '+94 77 999 9999', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Van', 'TEST-001', 'Colombo', 1);
END

-- Test Pharmacist (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'pharmacist1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('pharmacist1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'Pharmacist', 'pharmacist1@test.com', 'Dr. Sarah Pharmacist', '+94 77 555 1234', 1, GETDATE());
END

-- Test Customer Support (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'support1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('support1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'CustomerSupport', 'support@crystalcare.com', 'Customer Support Team', '+94 77 123 4567', 1, GETDATE());
END

-- Test Admin (insert only if not exists)
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'admin1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('admin1', '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i', 'ADMIN', 'admin@crystalcare.com', 'System Administrator', '+94 77 000 0000', 1, GETDATE());
END

-- Add test prescriptions (only if they don't exist)
IF NOT EXISTS (SELECT 1 FROM dbo.Prescriptions WHERE customerId = 1)
BEGIN
    INSERT INTO dbo.Prescriptions (orderId, customerId, fileName, filePath, uploadDate, status) VALUES
    (1003, 1, 'prescription_001.pdf', '/uploads/prescriptions/prescription_001.pdf', GETDATE(), 'APPROVED'),
    (NULL, 1, 'prescription_002.jpg', '/uploads/prescriptions/prescription_002.jpg', GETDATE() - 1, 'PENDING');
END
ELSE
BEGIN
    -- Update existing prescription to link to order 1003
    UPDATE dbo.Prescriptions SET orderId = 1003, status = 'APPROVED' WHERE customerId = 1 AND orderId IS NULL;
END

-- Add FinanceManager user
IF NOT EXISTS (SELECT 1 FROM dbo.Users WHERE username = 'finance1')
BEGIN
    INSERT INTO dbo.Users (username, passwordHash, role, email, fullName, phone, isActive, createdAt) VALUES
    ('finance1', '$2a$10$5yBzQ2jOGVyH6Hc1yqodGuqWz6BIB58Ru2Qs8rV2SljxURG7Ys9nm', 'FinanceManager', 'finance@crystalcare.com', 'Finance Manager', '+94 77 123 4567', 1, GETDATE());
END
