#!/usr/bin/bash
# uses docker and docker-compose commands to find out ip addresses of containers and if they are malicious or not
# expects two arguments: project directory containing the docker-compose.yml and path where labels are written
usage() {
    echo "usage: ./generate-labels.sh <project_dir> <labels_path> [env_file_path]"
    echo "    project_dir   - Directory of the generator project"
    echo "    labels_path   - Path to file where labels are written"
    echo "    env_file_path - Path to env file for docker-compose configuration (optional)"
}

if [ $# -lt 2 ]; then
    usage
    exit 1
fi

HEADER="ipv4,attack"

workdir=$PWD

project_dir="$1"
labels_path="$2"
env_file_path="$3"

cd "$project_dir" || exit 1

if [ -z "$env_file_path" ]; then
    containers=$(docker-compose ps -q)
else
    containers=$(docker-compose --env-file $env_file_path ps -q)
fi

if [ "$containers" = "" ]; then
    echo "No containers found in project $project_dir"
else
    echo "$HEADER" > "$labels_path"
    for container in $containers; do
        ipv4=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$container")
        attack=$(docker inspect -f '{{index .Config.Labels "hr.fer.diplomski.kolka.attack.label"}}' "$container")
        echo "$ipv4,$attack" >> "$labels_path"
    done
fi

cd "$workdir" || exit 1
