CREATE TABLE dbo.Drivers (
                             id INT IDENTITY PRIMARY KEY,
                             name NVARCHAR(100) NOT NULL,
                             email NVARCHAR(100) NOT NULL UNIQUE,
                             phone NVARCHAR(20) NOT NULL,
                             vehicleType NVARCHAR(50),
                             licensePlate NVARCHAR(20),
                             serviceArea NVARCHAR(100),
                             available BIT DEFAULT 1
);

CREATE TABLE dbo.Orders (
                            id INT IDENTITY PRIMARY KEY,
                            orderNumber NVARCHAR(50) NOT NULL,
                            customerName NVARCHAR(100) NOT NULL,
                            deliveryAddress NVARCHAR(255) NOT NULL,
                            deliveryWindow NVARCHAR(50) NULL,
                            weight DECIMAL(5,2) NULL,
                            status NVARCHAR(20) DEFAULT 'Pending'
);

CREATE TABLE dbo.Deliveries (
                                id INT IDENTITY PRIMARY KEY,
                                orderId INT NOT NULL FOREIGN KEY REFERENCES dbo.Orders(id),
                                driverId INT NOT NULL FOREIGN KEY REFERENCES dbo.Drivers(id),
                                status NVARCHAR(20) DEFAULT 'Assigned',
                                eta NVARCHAR(50) NULL,
                                notes NVARCHAR(255) NULL
);
