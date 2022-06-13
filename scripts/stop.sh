#!/usr/bin/bash
# stops all components

# set project root directory
if [ ! $DIPLOMSKI_HOME ]; then
    echo "DIPLOMSKI_HOME not set, assuming $PWD is DIPLOMSKI_HOME."
    root=$PWD
else
    root=$DIPLOMSKI_HOME
fi
# load environment variables
source "$root/.env"

# stop generator
cd "$root/generator"
docker-compose --env-file "$root/.env" down || exit 1
echo "Generator is stopped."
# stop the labeler
docker stop $LABELER_NAME
docker rm $LABELER_NAME
# stop infrastructure
cd "$root/infrastructure"
docker-compose --env-file "$root/.env" down || exit 1
echo "Infrastructure is down."
