CREATE TABLE IF NOT EXISTS spring_ai_chat_memory (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    conversation_id varchar(255) NOT NULL,
    sequence_id int4 NOT NULL,
    content text NULL,
    type varchar(255) NOT NULL,
    "timestamp" timestamp NOT NULL,
    CONSTRAINT spring_ai_chat_memory_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_spring_ai_chat_memory_conversation_id ON spring_ai_chat_memory (conversation_id);