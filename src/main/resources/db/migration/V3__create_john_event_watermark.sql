CREATE TABLE IF NOT EXISTS john_event_watermark (
  aggregate_id INTEGER PRIMARY KEY,
  event_type VARCHAR(64) NOT NULL,
  last_event_id INTEGER NOT NULL
);

