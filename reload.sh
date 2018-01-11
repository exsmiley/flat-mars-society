docker stop $(docker ps -aq)
echo 'y' | docker system prune -a
sudo sh run.sh