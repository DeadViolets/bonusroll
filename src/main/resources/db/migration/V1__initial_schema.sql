CREATE TABLE items (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    icon TEXT NOT NULL
);

CREATE INDEX idx_item_name
ON items(name);
