redis-server &

until redis-cli ping 2>/dev/null | grep -q PONG; do
  echo "Waiting for Redis..."
  sleep 1
done

redis-cli SET email_exist:admin@gmail.com 1
redis-cli SET email_exist:user1@gmail.com 1
redis-cli SET email_exist:user2@gmail.com 1
redis-cli SET email_exist:user3@gmail.com 1
redis-cli SET email_exist:user4@gmail.com 1
redis-cli SET email_exist:user5@gmail.com 1

redis-cli SET banlist:accountId:user5@gmail.com 1

wait
