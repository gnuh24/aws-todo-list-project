#!/bin/sh

# Start Redis server in background
redis-server &
sleep 2  # đợi Redis khởi động

# Tạo các key email_exist:<email>
redis-cli SET email_exist:admin@gmail.com 1
redis-cli SET email_exist:user1@gmail.com 1
redis-cli SET email_exist:user2@gmail.com 1

# Giữ Redis process chạy
wait
