-- Fix driver passwords - run this to update existing drivers
-- This script ensures all drivers have proper password hashes

-- First, ensure the passwordHash column exists
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Drivers') AND name = 'passwordHash')
BEGIN
    ALTER TABLE dbo.Drivers ADD passwordHash NVARCHAR(255);
END

-- Update all drivers with the correct password hash
UPDATE dbo.Drivers 
SET passwordHash = '$2a$10$Yww/JGiV0cklkas.JQ/W7uZdDYsRJaj5gEOveSvD9KuAR3R/AiF/i' 
WHERE passwordHash IS NULL OR passwordHash = '' OR passwordHash = 'NULL' OR passwordHash = '';

-- Verify the update
SELECT id, name, email, passwordHash FROM dbo.Drivers;




