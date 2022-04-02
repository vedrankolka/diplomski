# Diplomski
Samo neki notes di si pratim kaj radim sad i di stojim

## 1) Generiranje
### Tehnologije za proucit
- [Locust](https://locust.io/)
- [DroopeScan](https://github.com/SamJoan/droopescan)
- [MagicWand](https://github.com/twosixlabs/magicwand-datatool)
- [Selenium with Python](https://selenium-python.readthedocs.io/index.html)
### Notes
OK jebeš magicwand, nemremo nikaj s njim jer je prelos, napravit cemo sve svoje lol

### TODO
- [x] vidit jel se moze integrirat Droopescan u Magicwand
- [x] probat pokrenut Droopescan sa docker-compose tak da u command citmao env vars `"$URL"` i to
- [ ] dodat random sleep prije pokretanja Droopescana
- [ ] dodat neki drugi napad (mozda synflood)
- [ ] dodat content na stranicu
- [ ] spojit FileStreamConnector da cita `.pcap` od `tcpdumpa`

## 2) Obrada
### Tehnologije za proucit
- [Kafka Streams API](https://kafka.apache.org/documentation/streams/)
- [CIC FlowMeter](https://www.unb.ca/cic/research/applications.html#CICFlowMeter)
- [Zeek](https://docs.zeek.org/en/master/)

### Notes
- koristit Kafka Streams za pretvaranje `.pcap` u `.csv` pomoću CIC FlowMetera ili Zeeka
- mozda koristit `magicwand covert` za pretvaranje hmmm?

## 3) Spremanje
### Tehnologije za proucit
- [Kafka Connect](https://docs.confluent.io/platform/current/connect/index.html)

### TODO
- [ ] local files sink
- [ ] hdfs sink
