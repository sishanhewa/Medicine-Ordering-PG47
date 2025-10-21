-- Schema file that preserves existing data
-- Only creates tables if they don't exist

-- Table 1: Users (For authentication and authorization)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Users' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Users (
    id INT IDENTITY PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    passwordHash NVARCHAR(255) NOT NULL,
    role NVARCHAR(50) NOT NULL,
    email NVARCHAR(100),
    fullName NVARCHAR(100),
    phone NVARCHAR(20),
    isActive BIT DEFAULT 1,
    createdAt DATETIME2 DEFAULT GETDATE()
);
END

-- Table 2: Drivers
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Drivers' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Drivers (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone NVARCHAR(20) NOT NULL,
    passwordHash NVARCHAR(255) NOT NULL,
    vehicleType NVARCHAR(50),
    licensePlate NVARCHAR(20),
    serviceArea NVARCHAR(100),
    available BIT DEFAULT 1,
    createdAt DATETIME2 DEFAULT GETDATE()
);
END
ELSE
BEGIN
    -- Add passwordHash column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Drivers') AND name = 'passwordHash')
    BEGIN
        ALTER TABLE dbo.Drivers ADD passwordHash NVARCHAR(255) NOT NULL DEFAULT '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i';
    END
    
    -- Add createdAt column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Drivers') AND name = 'createdAt')
    BEGIN
        ALTER TABLE dbo.Drivers ADD createdAt DATETIME2 DEFAULT GETDATE();
    END
END

-- Table 3: Medicines (For product listing)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Medicines' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Medicines (
    id INT IDENTITY PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    stockLevel INT DEFAULT 0,
    category NVARCHAR(50),
    requiresPrescription BIT DEFAULT 0,
    imageUrl NVARCHAR(255),
    createdAt DATETIME2 DEFAULT GETDATE()
);
END

-- Table 4: Customers
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Customers' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Customers (
    id INT PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone NVARCHAR(20),
    password NVARCHAR(255),
    address NVARCHAR(500),
    createdAt DATETIME2 DEFAULT GETDATE()
);
END

-- Table 5: Orders
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Orders' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Orders (
    id INT IDENTITY PRIMARY KEY,
    orderNumber NVARCHAR(50) NOT NULL UNIQUE,
    customerName NVARCHAR(100) NOT NULL,
    deliveryAddress NVARCHAR(500) NOT NULL,
    deliveryWindow NVARCHAR(100),
    weight DECIMAL(8,2) DEFAULT 0,
    status NVARCHAR(50) DEFAULT 'Pending',
    orderDate DATETIME2 DEFAULT GETDATE(),
    itemCount INT DEFAULT 0
);
END

-- Table 6: Carts
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Carts' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Carts (
    id INT IDENTITY PRIMARY KEY,
    customerId INT NOT NULL,
    medicineId INT NOT NULL,
    quantity INT NOT NULL,
    medicineName NVARCHAR(100),
    price DECIMAL(10,2),
    category NVARCHAR(50),
    stockLevel INT,
    requiresPrescription BIT DEFAULT 0,
    createdAt DATETIME2 DEFAULT GETDATE()
);
END

-- Table 7: Prescriptions
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Prescriptions' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Prescriptions (
    id INT IDENTITY PRIMARY KEY,
    orderId INT,
    customerId INT NOT NULL,
    fileName NVARCHAR(255) NOT NULL,
    filePath NVARCHAR(500) NOT NULL,
    fileSize BIGINT,
    uploadDate DATETIME2 DEFAULT GETDATE(),
    status NVARCHAR(50) DEFAULT 'PENDING',
    rejectionReason NVARCHAR(500) NULL
);
END

-- Table 8: OrderItems
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'OrderItems' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.OrderItems (
    id INT IDENTITY PRIMARY KEY,
    orderId INT NOT NULL,
    medicineId INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
END

-- Table 9: ContactInquiries
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'ContactInquiries' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.ContactInquiries (
    id INT IDENTITY PRIMARY KEY,
    customerName NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    phone NVARCHAR(20),
    subject NVARCHAR(200) NOT NULL,
    message NVARCHAR(1000) NOT NULL,
    status NVARCHAR(50) DEFAULT 'New',
    createdAt DATETIME2 DEFAULT GETDATE(),
    priority NVARCHAR(20) DEFAULT 'Medium'
);
END

-- Table 10: Deliveries
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'Deliveries' AND schema_id = SCHEMA_ID('dbo'))
BEGIN
CREATE TABLE dbo.Deliveries (
    id INT IDENTITY PRIMARY KEY,
    orderId INT NOT NULL,
    driverId INT,
    status NVARCHAR(50) DEFAULT 'Pending',
    assignedAt DATETIME2,
    pickedUpAt DATETIME2,
    deliveredAt DATETIME2,
    notes NVARCHAR(500)
);
END
