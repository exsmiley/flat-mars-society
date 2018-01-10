docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
sudo sh run.sh