CREATE TABLE jobs (
                      id UUID PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      cron_expression VARCHAR(100) NOT NULL,
                      status VARCHAR(20) NOT NULL,
                      job_type VARCHAR(20) NOT NULL,
                      payload JSONB NOT NULL,
                      next_fire_at TIMESTAMP NOT NULL,
                      last_fire_at TIMESTAMP,
                      retry_count INT NOT NULL DEFAULT 0,
                      max_retries INT NOT NULL DEFAULT 3,
                      created_at TIMESTAMP NOT NULL,
                      updated_at TIMESTAMP NOT NULL
);