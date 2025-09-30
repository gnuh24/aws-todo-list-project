#!/bin/bash
set -e

CONTAINER_NAME="mysql:latest-test"
MYSQL_ROOT_PASSWORD="0204"
MYSQL_DATABASE="aws_todolist_database"

echo "🚀 Pulling MySQL image..."
docker pull mysql:latest

echo "🛑 Removing old container if exists..."
docker rm -f $CONTAINER_NAME 2>/dev/null || true

echo "📦 Starting new MySQL container..."
docker run -d \
  --name $CONTAINER_NAME \
  -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD \
  -e MYSQL_DATABASE=$MYSQL_DATABASE \
  -p 3306:3306 \
  -v $(pwd)/init.sql:/docker-entrypoint-initdb.d/init.sql \
  mysql:latest

echo "✅ MySQL container '$CONTAINER_NAME' is up and running!"
echo "   - Host: localhost"
echo "   - Port: 3306"
echo "   - User: root"
echo "   - Password: $MYSQL_ROOT_PASSWORD"
echo "   - Database: $MYSQL_DATABASE"
