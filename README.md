# Diplomski
Samo neki notes di si pratim kaj radim sad i di stojim

## 1) Generiranje
### Tehnologije za proucit
- [Locust](https://locust.io/)
- [DroopeScan](https://github.com/SamJoan/droopescan)

### Notes
OK jebeš magicwand, nemremo nikaj s njim jer je prelos, napravit cemo sve svoje lol.



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


Sad smo na tom da preko kafke prebacimo labele ko je napadac a ko nije, ali nije to nuzno,
mozemo samo iskopirat to u sami container jel tako?

### TODO
- [x] dodat labeliranje
- [x] probat novi [cicflowmeter](https://github.com/datthinh1801/cicflowmeter/tree/main/src/cicflowmeter) za citanje direkt s interface-a
- [ ] dokerizirat customizirani cicflowmeter
- [ ] mozda dodat i pretvaranje u json
- [x] podesit skriptu `generate-labels.sh` da printa usage i da fakat zapisuje di treba
- [ ] poslat labele na kafku i citat ih s kafke
- [ ] napisat skriptu koja pokrece sve to: prvo Zookeeper, Kafku, CICFlowMeter i TCPDump, onda Generator i skriptu `generate-labels.sh`, onda tek Labeler, onda tek Spremator
- [ ] napisat skriptu koja zaustavlja sve: prvo Generator, onda TCPDump pa CICFlowMeter, onda Labeler, pa Spremator, pa Kafku i Zookeeper

## 3) Spremanje
### Tehnologije za proucit
- [Kafka Connect](https://docs.confluent.io/platform/current/connect/index.html)

### TODO
- [ ] Zapakirat spremator u Docker container? Stavit onaj console consumer u nj i pretad ostale stvari preko env varijabli, volume stavimo tu di je

## Pokretanje
1) pokrenut infrastructure.yml koji pokrece kafku i cicflowmeter
2) pokrenut generator.yml i odma uz njega kreirat labele i poslat ih na kafku ***
3) pokrenut labeler (npr docker image)
4) pokrenut consumera koji to zapisuje na disk

## Pisanje rada
Ako će faliti sadržaja:
- možemo dodati bazu podataka koju koristi Wordpress u schemu
- možemo dodati Zookeeper kojeg Kafka koristi u schemu
- možemo napisat više o značajkama CICFlowMeter-a
- možemo dodati cijeli apendix za pokretanje i u nj stavit npr skriptu za pokretanje i za zaustavljanje

*** nek labeler cita od kud je stao, nije bitno di je, sve dok ne dobi npr "DONE" il tak nes sta mu govori da je gotovo, onda cak mozemo i labeler ubacit u infrastructure.yml
