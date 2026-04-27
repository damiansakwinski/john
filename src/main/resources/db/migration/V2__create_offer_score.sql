CREATE TABLE IF NOT EXISTS john_offer_score (
  offer_id INTEGER PRIMARY KEY,
  main_photo_has_face BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
