CREATE UNIQUE INDEX idx_event_watermark_aggregate_event
  ON john_event_watermark (aggregate_id, event_type);
