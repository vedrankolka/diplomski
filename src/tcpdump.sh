#!/usr/bin/bash
# Script to start capturing packets on the given docker network
function usage {
    echo "usage: ./tcpdump.sh <docker_network_name> <output_file_name>"
    echo "    docker_network_name = name of docker network on which to capture packets"
    echo "    output_file_name    = file name where to write captured data"
}

if [ $# -lt 2 ]; then
    usage
    exit 1
fi
# read the arguments
network_name=$1
out=$2
# get gateway ip and interface name
gateway_ip=$(docker network inspect -f '{{range .IPAM.Config}}{{.Gateway}}{{end}}' $network_name)
interface=$(ip addr | grep $gateway_ip | sed -E "s/\s+/\t/g" | cut -f8)

echo "network_name = $network_name"
echo "out = $out"
echo "gateway_ip = $gateway_ip"
echo "interface = $interface"

tcpdump -i "$interface" -w "$out"
