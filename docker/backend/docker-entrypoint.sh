#!/bin/bash

echo "spring.jpa.hibernate.ddl-auto=create-drop" > application.properties
echo "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect" >> application.properties
echo "spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" >> application.properties
echo "spring.jpa.show-sql=true" >> application.properties

echo "spring.datasource.username=$POSTGRES_USER" >> application.properties
echo "spring.datasource.password=$POSTGRES_PASSWORD" >> application.properties

echo "server.port=8808" >> application.properties


echo "spring.datasource.driver-class-name=org.postgresql.Driver" >> application.properties
echo "Waiting for the PostgreSQL server to start on $DB_HOST:$DB_PORT"

while ! nc -z "$DB_HOST" "$DB_PORT"; do
  sleep 0.2
done

echo "PostgreSQL Server has started"

java -jar app.jar