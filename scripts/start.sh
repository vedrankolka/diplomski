#!/usr/bin/bash
# starts all components

find_interface() {
    if [ $# -lt 1 ]; then
        >&2 echo "No network name given."
        return 1
    fi

    gateway_ip=$(docker network inspect -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}' $1)
    interface=$(ip addr | grep $gateway_ip | sed -E "s/\s+/\t/g" | cut -f8)
    return 0
}

stop_infrastructure() {
    cd "$root"/infrastructure
    docker-compose --env-file "$root/.env" down
}

# set project root directory
if [ ! $DIPLOMSKI_HOME ]; then
    echo "DIPLOMSKI_HOME not set, assuming $PWD is DIPLOMSKI_HOME."
    root=$PWD
else
    root=$DIPLOMSKI_HOME
fi
# load environment variables
source "$root/.env"
find_interface $NETWORK_NAME
export INTERFACE=$interface
# start zookeeper and kafka
cd "$root"/infrastructure
docker-compose --env-file "$root/.env" up -d || exit 1
# these depend on Kafka, so try to start them a few times and wait inbetween tries
running_count=0
min_running_count=3
for i in `seq $MAX_TRIES`
do
    is_running=$(docker ps --filter "name=infrastructure_cicflowmeter_1" --filter "status=running" --quiet)
    if [ -z $is_running ]; then
        echo "[$i] Failed to start CICFlowMeter"
        running_count=0
    else
        # check if it is running after a second
        running_count=$(($running_count + 1))
        if [ $running_count -ge $min_running_count ]; then
            echo "CICFlowMeter has been running for $min_running_count cycles. Continuing."
            break
        fi
    fi
    # give it time to crash/start
    sleep $RETRY_TIME
done

# if the last try was not successful, bounce out!
if [ $i -ge $MAX_TRIES ] && [ $running_count -lt $min_running_count ]; then
    echo "Goddamnit! We boutta bounce!"
    stop_infrastructure
    exit 1
fi
# Else we good.
echo "Infrastructure is up."
# only now can the generator be started
cd "$root/generator"
docker-compose --env-file "$root/.env" up -d || (stop_infrastructure || exit 1)
echo "Generator is running."
# generate labels and run the labeler
$root/scripts/generate-labels.sh "$root/generator/" "$root/labeler/src/main/resources/labels.csv" "$root/.env"
docker run -d --name $LABELER_NAME --network host \
--volume $root/labeler/src/main/resources:/resources \
vk50630/labeler:1.0 /resources/application.properties /resources/labels.csv
echo "Labeler is running."