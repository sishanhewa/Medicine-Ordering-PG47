-- Drop tables first to ensure a clean start. Dependent tables must be dropped first.
DROP TABLE IF EXISTS dbo.OrderItems;
DROP TABLE IF EXISTS dbo.Deliveries;
DROP TABLE IF EXISTS dbo.Prescriptions;
DROP TABLE IF EXISTS dbo.Carts;
DROP TABLE IF EXISTS dbo.ContactInquiries;
DROP TABLE IF EXISTS dbo.Orders;
DROP TABLE IF EXISTS dbo.Medicines;
DROP TABLE IF EXISTS dbo.Customers;
DROP TABLE IF EXISTS dbo.Drivers;
DROP TABLE IF EXISTS dbo.Users;


-- Table 1: Users (For authentication and authorization)
CREATE TABLE dbo.Users (
    id INT IDENTITY PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    passwordHash NVARCHAR(255) NOT NULL,
    role NVARCHAR(50) NOT NULL, -- Customer, Admin, DeliveryManager, DeliveryPersonnel, Pharmacist, CustomerSupport, FinanceManager
    email NVARCHAR(100),
    fullName NVARCHAR(100),
    phone NVARCHAR(20),
    isActive BIT DEFAULT 1,
    createdAt DATETIME2 DEFAULT GETDATE()
);

-- Table 2: Drivers
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

-- Table 3: Medicines (For product listing)
CREATE TABLE dbo.Medicines (
                               id INT IDENTITY PRIMARY KEY,
                               name NVARCHAR(100) NOT NULL,
                               category NVARCHAR(50) NULL,
                               description NVARCHAR(500),
                               price DECIMAL(10, 2) NOT NULL,
                               stockLevel INT NOT NULL,
                               requiresPrescription BIT DEFAULT 0,
                               imageUrl NVARCHAR(500) NULL
);

-- Table 4: Orders
CREATE TABLE dbo.Orders (
                            id INT IDENTITY PRIMARY KEY,
                            orderNumber NVARCHAR(50) NOT NULL,
                            customerName NVARCHAR(100) NOT NULL,
                            deliveryAddress NVARCHAR(255) NOT NULL,
                            deliveryWindow NVARCHAR(50) NULL,
                            weight DECIMAL(5,2) NULL,
                            status NVARCHAR(20) DEFAULT 'Pending'
);

-- Table 5: Deliveries (Linking Orders and Drivers)
CREATE TABLE dbo.Deliveries (
                                id INT IDENTITY PRIMARY KEY,
                                orderId INT NOT NULL FOREIGN KEY REFERENCES dbo.Orders(id),
                                driverId INT NOT NULL FOREIGN KEY REFERENCES dbo.Drivers(id),
                                status NVARCHAR(20) DEFAULT 'Assigned',
                                eta NVARCHAR(50) NULL,
                                notes NVARCHAR(255) NULL
);

-- Table 6: Customers (basic customer profile details for orders, carts, and prescriptions)
CREATE TABLE dbo.Customers (
    id INT PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    phone NVARCHAR(20) NULL,
    password NVARCHAR(100) NULL,
    address NVARCHAR(255) NULL
);

-- Table 7: Carts (simple cart lines linking a customer to a medicine)
CREATE TABLE dbo.Carts (
    id INT IDENTITY PRIMARY KEY,
    customerId INT NOT NULL FOREIGN KEY REFERENCES dbo.Customers(id),
    medicineId INT NOT NULL FOREIGN KEY REFERENCES dbo.Medicines(id),
    quantity INT NOT NULL
);

-- Table 8: Prescriptions (uploaded file metadata linked to order or customer)
CREATE TABLE dbo.Prescriptions (
    id INT IDENTITY PRIMARY KEY,
    orderId INT NULL FOREIGN KEY REFERENCES dbo.Orders(id),
    customerId INT NOT NULL FOREIGN KEY REFERENCES dbo.Customers(id),
    fileName NVARCHAR(255) NOT NULL,
    filePath NVARCHAR(500) NOT NULL,
    uploadDate DATETIME DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'Uploaded'
);

-- Table 9: OrderItems (track what medicines were in each order)
CREATE TABLE dbo.OrderItems (
    id INT IDENTITY PRIMARY KEY,
    orderId INT NOT NULL FOREIGN KEY REFERENCES dbo.Orders(id) ON DELETE CASCADE,
    medicineId INT NOT NULL FOREIGN KEY REFERENCES dbo.Medicines(id),
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- Table 10: ContactInquiries (customer support inquiries)
CREATE TABLE dbo.ContactInquiries (
    id INT IDENTITY PRIMARY KEY,
    customerName NVARCHAR(100) NOT NULL,
    customerEmail NVARCHAR(100) NOT NULL,
    customerPhone NVARCHAR(20),
    subject NVARCHAR(200) NOT NULL,
    message NVARCHAR(1000) NOT NULL,
    status NVARCHAR(20) DEFAULT 'New',
    inquiryDate DATETIME2 DEFAULT GETDATE(),
    priority NVARCHAR(20) DEFAULT 'Medium'
);


-- medicines for UI testing with diverse products and unique image URLs
INSERT INTO dbo.Medicines (name, category, description, price, stockLevel, requiresPrescription, imageUrl) VALUES
-- Pain Relief Medicines
('Panadol 500mg', 'Pain Relief', 'Pain reliever and fever reducer', 120.00, 100, 0, 'https://www.grocerylanka.com/cdn/shop/products/Panadol-_Paracetamol-500mg_--Sri-Lanka_1024x1024.jpg?v=1643375348'),
('Ibuprofen 400mg', 'Pain Relief', 'Anti-inflammatory pain reliever', 140.00, 90, 0, 'https://pharmacyservice.co.uk/wp-content/uploads/pharmacy_mentor/importImages/ibuprofen-400mg-96-coated-tablets-2.jpeg'),
('Aspirin 100mg', 'Pain Relief', 'Cardiovascular protection and pain relief', 85.00, 75, 0, 'https://www.pharmaciedesteinfort.com/media/catalog/product/cache/e34e4c303aca0a6b6a6aff8f2907f7d5/a/s/aspirine-100-mg-30-comprimes-0002.jpg'),
('Voltaren Gel', 'Pain Relief', 'Topical pain relief gel for muscle pain', 320.00, 45, 0, 'https://i-cf65.ch-static.com/content/dam/cf-consumer-healthcare/voltaren-delta/en_GB/desktop/products/teasers/updated/30783_001_VOLTAROL_BACK_MUSCLE_100G_CARTON_2365x2365_300DPI.png?auto=format'),

-- Antibiotics
('Amoxicillin 500mg', 'Antibiotic', 'Antibiotic capsules (requires prescription)', 450.00, 60, 1, 'https://cpimg.tistatic.com/08525953/b/4/Amoxicillin-.jpg'),
('Azithromycin 250mg', 'Antibiotic', 'Broad spectrum antibiotic (requires prescription)', 380.00, 40, 1, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5UVPxrCPfaSFZqVQHxAFCHTZMSpgxHmswTQ&s'),
('Ciprofloxacin 500mg', 'Antibiotic', 'Fluoroquinolone antibiotic (requires prescription)', 520.00, 35, 1, 'https://healthplusnigeria.com/cdn/shop/files/Ciprotab_20Ciprofloxacin_20500mg_20Tabs_20x14_a3523601-b194-4c80-af63-45ed6670fd78_grande.webp?v=1757093097'),

-- Allergy Medicines
('Cetirizine 10mg', 'Allergy', 'Antihistamine for allergy relief', 200.00, 80, 0, 'https://medino-product.imgix.net/teva-cetirizine-10mg-hay-fever-allergy-relief-30-tablets-b9ce9411.png?h=350&w=350&fit=fill&bg=FFF&auto=format&q=90'),
('Loratadine 10mg', 'Allergy', 'Non-drowsy antihistamine', 160.00, 110, 0, 'https://cdn.sanity.io/images/zbeduy22/production/512ad5933600d843ab7bd88181355e8b84024162-600x600.jpg'),
('Fexofenadine 120mg', 'Allergy', '24-hour allergy relief', 280.00, 65, 0, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTlZDCZTOQAeBEAzQ1nnnTzN6wzmSmEyOpN4Q&s'),

-- Vitamins & Supplements
('Vitamin C 1000mg', 'Vitamins', 'Immune system support and antioxidant', 180.00, 150, 0, 'https://shopxonline.lk/cdn/shop/files/acorbic-c-1000mg-vitamin-c-supplement-508757.jpg?v=1725942125'),
('Vitamin D3 2000IU', 'Vitamins', 'Bone health and immune support', 250.00, 85, 0, 'https://i0.wp.com/libertystore.lk/wp-content/uploads/2021/04/Liberty-Store.lk-2024-08-08T203723.944.png?fit=1080%2C1080&ssl=1'),
('Multivitamin Complex', 'Vitamins', 'Complete daily vitamin supplement', 320.00, 70, 0, 'https://www.sportsnutritionhub.lk/cdn/shop/files/Multivitamin_Complex_Capsules_webp.jpg?v=1760208882&width=2048'),
('Omega-3 Fish Oil', 'Vitamins', 'Heart and brain health supplement', 450.00, 55, 0, 'https://m.media-amazon.com/images/I/71ZBnxvlvCL._AC_SL1500_.jpg'),

-- Digestive Health
('Omeprazole 20mg', 'Digestive', 'Stomach acid reducer (requires prescription)', 220.00, 70, 1, 'https://www.biovea.com/ca/en/images/products/xlrg/1978_z.jpg'),
('Probiotics 50 Billion', 'Digestive', 'Gut health and digestive support', 380.00, 45, 0, 'https://m.media-amazon.com/images/I/71uHfRTn6RL._AC_UF1000,1000_QL80_.jpg'),
('Digestive Enzymes', 'Digestive', 'Natural digestive enzyme supplement', 290.00, 60, 0, 'https://m.media-amazon.com/images/I/8180gAIXo-L._AC_UF894,1000_QL80_.jpg'),

-- Diabetes Management
('Metformin 500mg', 'Diabetes', 'Blood sugar control (requires prescription)', 300.00, 50, 1, 'https://images.apollo247.in/pub/media/catalog/product/o/k/oka0007_3.jpg?tr=q-80,f-webp,w-400,dpr-3,c-at_max%20400w'),
('Glucose Test Strips', 'Diabetes', 'Blood glucose monitoring strips', 180.00, 120, 0, 'https://images-cdn.ubuy.co.in/6519d45e5117e11f324f2c54-blood-glucose-test-strips-for-diabetes.jpg'),

-- Rehydration & First Aid
('ORS Powder', 'Rehydration', 'Oral rehydration salts sachet', 90.00, 200, 0, 'https://www.grocerylanka.com/cdn/shop/files/rn-image_picker_lib_temp_6ea48b55-9621-41ba-92b2-2a3313a800be_700x700.jpg?v=1751296912'),
('Electrolyte Tablets', 'Rehydration', 'Sports hydration and electrolyte replacement', 150.00, 85, 0, 'https://m.media-amazon.com/images/I/81qjVIWun+L._AC_UF1000,1000_QL80_.jpg'),

-- Topical Products
('Antiseptic Cream', 'First Aid', 'Antibacterial wound healing cream', 120.00, 95, 0, 'https://healwell-homeo.com/cdn/shop/files/Antiseptic.jpg?v=1722318520'),
('Hydrocortisone Cream', 'First Aid', 'Anti-inflammatory skin cream', 180.00, 70, 0, 'https://pics.walgreens.com/prodimg/595116/900.jpg'),
('Burn Relief Gel', 'First Aid', 'Cooling gel for minor burns and sunburn', 220.00, 55, 0, 'https://i5.walmartimages.com/seo/Equate-Burn-Relief-Gel-with-Lidocaine-8-oz_5369673c-4e71-4d52-abe1-0e1977bf9452.95f66f35ccc0680dd134e2c747f6f37b.jpeg'),

-- Respiratory Health
('Inhaler Spacer', 'Respiratory', 'Asthma inhaler spacer device', 350.00, 30, 0, 'https://files.nationalasthma.org.au/images/_fit770/child-spacer-puffer-device.png'),
('Cough Syrup', 'Respiratory', 'Natural honey-based cough relief', 160.00, 80, 0, 'https://images.ctfassets.net/sabbecbbwaz3/SUANq01Uei9mbWy71UDmI/8fdceca9370a6d11e0c9615bb301b88e/Vicks_AU_Cough_2in1_Syrup_front.png');

-- Sample Contact Inquiries for testing
INSERT INTO dbo.ContactInquiries (customerName, customerEmail, customerPhone, subject, message, status, inquiryDate, priority) VALUES
('John Smith', 'demo@crystalcare.com', '+94 77 123 4567', 'Order Delivery Issue', 'My order #12345 was supposed to be delivered yesterday but I haven''t received it yet. Can you please check the status?', 'New', '2024-01-15 10:30:00', 'High'),
('John Smith', 'demo@crystalcare.com', '+94 77 123 4567', 'Medicine Availability', 'I need to know if you have Paracetamol 500mg tablets in stock. I need 2 strips urgently.', 'In Progress', '2024-01-14 14:20:00', 'Medium'),
('John Smith', 'demo@crystalcare.com', '+94 77 123 4567', 'Prescription Upload Problem', 'I tried to upload my prescription but it keeps showing an error. The file is a PDF and under 5MB.', 'Resolved', '2024-01-13 09:15:00', 'Low'),
('Sarah Johnson', 'sarah.johnson@email.com', '+94 71 987 6543', 'Account Information', 'I want to update my delivery address. How can I change it in my profile?', 'New', '2024-01-16 16:45:00', 'Medium'),
('Mike Wilson', 'mike.wilson@email.com', '+94 70 555 1234', 'Refund Request', 'I received the wrong medicine in my order. I ordered Aspirin but got Ibuprofen instead. Can I get a refund?', 'In Progress', '2024-01-15 11:30:00', 'High');

