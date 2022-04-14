#!/usr/bin/bash
# Script to start capturing packets on the given docker network
function usage {
    echo "usage: ./tcpdump.sh <path_to_env_file>"
    echo "    path_to_env_file = path to file which defines the following env vars:"
    echo "    NETWORK_NAME     = name of docker network on which to capture packets"
    echo "    OUTPUT_FILE      = file name where to write captured data"
}

if [ $# -lt 1 ]; then
    usage
    exit 1
fi
# read the arguments
source $1
network_name=$NETWORK_NAME
out=$OUTPUT_FILE
# get gateway ip and interface name
gateway_ip=$(docker network inspect -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}' $network_name)
interface=$(ip addr | grep $gateway_ip | sed -E "s/\s+/\t/g" | cut -f8)

echo "network_name = $network_name"
echo "out = $out"
echo "gateway_ip = $gateway_ip"
echo "interface = $interface"

tcpdump -i "$interface" -w "$out"
