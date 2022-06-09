#!/usr/bin/bash
# script to find interface of the given docker network
usage() {
    echo "usage: ./find-interface.sh <docker_network_name>"
}

if [ $# -lt 1 ]; then
    usage
    exit 1
fi

gateway_ip=$(docker network inspect -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}' $1)
interface=$(ip addr | grep $gateway_ip | sed -E "s/\s+/\t/g" | cut -f8)
echo "$interface"
