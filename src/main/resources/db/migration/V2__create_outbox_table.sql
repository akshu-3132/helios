CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               job_id UUID NOT NULL REFERENCES jobs(id),
                               status VARCHAR(20) NOT NULL,
                               retry_count INT NOT NULL DEFAULT 0,
                               created_at TIMESTAMP NOT NULL,
                               sent_at TIMESTAMP
);