import json
import psycopg

DB_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "dbname": "bonusroll",
    "user": "postgres",
    "password": "postgres",
}

with open("../data/equippable-items-current-expansion.json") as f:
    items = json.load(f)

with psycopg.connect(**DB_CONFIG) as conn:
    with conn.cursor() as cursor:
        cursor.executemany(
            """
            INSERT INTO items (id, name, icon)
            VALUES (%s, %s, %s)
            """,
            [
                (
                    item["id"],
                    item["name"],
                    item["icon"]
                )
                for item in items
            ]
        )

    conn.commit()
