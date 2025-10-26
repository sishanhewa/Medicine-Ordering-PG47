-- Fix FinanceManager password hash
UPDATE dbo.Users 
SET passwordHash = '$2a$10$5yBzQ2jOGVyH6Hc1yqodGuqWz6BIB58Ru2Qs8rV2SljxURG7Ys9nm'
WHERE username = 'finance1' AND role = 'FinanceManager';

-- Verify the update
SELECT username, role, email, fullName FROM dbo.Users WHERE username = 'finance1';


