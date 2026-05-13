docker start bonusroll-db || docker run -d --name bonusroll-db -v postgres_data:/var/lib/postgresql -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=bonusroll -p 5432:5432 postgres
