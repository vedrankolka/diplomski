# Diplomski
Samo neki notes di si pratim kaj radim sad i di stojim

## 1) Generiranje
### Tehnologije za proucit
- [Locust](https://locust.io/)
- [DroopeScan](https://github.com/SamJoan/droopescan)
- [MagicWand](https://github.com/twosixlabs/magicwand-datatool)
- [Selenium with Python](https://selenium-python.readthedocs.io/index.html)
- Neki BrowsingBot da radi benigni promet
### Notes
OK jebeš magicwand, nemremo nikaj s njim jer je prelos, napravit cemo sve svoje lol

Evo [tutorial](https://faun.pub/snooping-on-container-traffic-in-docker-compose-d34764a01276) za snimanje prometa. 

### TODO
- [x] vidit jel se moze integrirat Droopescan u Magicwand
- [x] probat pokrenut Droopescan sa docker-compose tak da u command citmao env vars `"$URL"` i to
- [x] dodat random sleep prije pokretanja Droopescana
- [x] dodat neki drugi napad ([flightsim](https://github.com/alphasoc/flightsim))
- [x] dodat content na stranicu
- [x] spojit FileStreamConnector da cita `.pcap` od `tcpdumpa`
- [x] prebacit sam network unutar dockera, dakle <b>ne na hostu</b>
- [x] dodat `.env` file u kojem pisu network name i output file i da onda to citaju svi kuis

## 2) Obrada
### Tehnologije za proucit
- [Kafka Streams API](https://kafka.apache.org/documentation/streams/)
- [CIC FlowMeter](https://www.unb.ca/cic/research/applications.html#CICFlowMeter)
- [Zeek](https://docs.zeek.org/en/master/)

### Notes
- koristit Kafka Streams za pretvaranje `.pcap` u `.csv` pomoću CIC FlowMetera ili Zeeka
1) predamo path do direktorija generatora (tam je docker-compose.yml)
2) pomoću `docker-compose ps -q` dodemo do ID-jeva containera
3) ```docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}},{{index .Config.Labels "hr.fer.diplomski.kolka.attack.label"}}' <ID>``` daje ipv4,isAttack
Ako nema labelu za attack mozemo zanemarit valjda??? ili pretpostavit da je `benign`
4) na temelju procitanih parova citamo s kafke i dodajemo labele heheee

### TODO
- [ ] dodat labeliranje
- [x] probat novi [cicflowmeter](https://github.com/datthinh1801/cicflowmeter/tree/main/src/cicflowmeter) za citanje direkt s interface-a
- [ ] mozda dokerizirat customizirani cicflowmeter
- [ ] mozda dodat i pretvaranje u json

## 3) Spremanje
### Tehnologije za proucit
- [Kafka Connect](https://docs.confluent.io/platform/current/connect/index.html)

### TODO
- [ ] local files sink
- [ ] hdfs sink
