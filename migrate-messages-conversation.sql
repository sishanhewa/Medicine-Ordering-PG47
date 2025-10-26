-- Migration script to add conversation support to Messages table
-- Run this if the Messages table already exists

-- Add conversationId column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Messages') AND name = 'conversationId')
BEGIN
    ALTER TABLE dbo.Messages ADD conversationId BIGINT NULL;
    PRINT 'Added conversationId column to Messages table';
END
ELSE
BEGIN
    PRINT 'conversationId column already exists in Messages table';
END
GO

-- Add parentMessageId column if it doesn't exist
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.Messages') AND name = 'parentMessageId')
BEGIN
    ALTER TABLE dbo.Messages ADD parentMessageId BIGINT NULL;
    PRINT 'Added parentMessageId column to Messages table';
END
ELSE
BEGIN
    PRINT 'parentMessageId column already exists in Messages table';
END
GO

-- Update existing messages to have their own conversationId (same as their id)
UPDATE dbo.Messages 
SET conversationId = id 
WHERE conversationId IS NULL;
PRINT 'Updated existing messages with conversationId';

-- Add foreign key constraints (optional, for data integrity)
-- ALTER TABLE dbo.Messages ADD CONSTRAINT FK_Messages_Parent 
--     FOREIGN KEY (parentMessageId) REFERENCES dbo.Messages(id);
-- ALTER TABLE dbo.Messages ADD CONSTRAINT FK_Messages_Conversation 
--     FOREIGN KEY (conversationId) REFERENCES dbo.Messages(id);

PRINT 'Migration completed successfully';




