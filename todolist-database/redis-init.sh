#!/bin/sh

# Chờ Redis khởi động
until redis-cli ping 2>/dev/null | grep -q PONG; do
  echo "Waiting for Redis..."
  sleep 1
done

echo "Redis ready. Initializing default keys..."

redis-cli SET email_exist:admin@gmail.com 1
redis-cli SET email_exist:user1@gmail.com 1
redis-cli SET email_exist:user2@gmail.com 1

echo "Redis initialization done!"
