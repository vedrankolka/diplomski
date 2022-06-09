#!/usr/bin/bash
# stops all components

# set project root directory
if [ ! $DIPLOMSKI_HOME ]; then
    echo "DIPLOMSKI_HOME not set, assuming $PWD is DIPLOMSKI_HOME."
    root=$PWD
else
    root=$DIPLOMSKI_HOME
fi


cd "$root/src/generator"
docker-compose --env-file "$root/.env" down || exit 1
echo "Generator is stopped."

cd "$root"/src/infrastructure
docker-compose --env-file "$root/.env" down || exit 1
echo "Infrastructure is down."
