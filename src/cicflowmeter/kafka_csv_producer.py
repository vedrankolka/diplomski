from kafka import KafkaProducer


class KafkaCSVProducer:

    def __init__(self, topic, broker):
        self.topic = topic
        self.broker = broker
        self.producer = KafkaProducer(bootstrap_servers=[broker], retries=5)

    def writerow(self, values):
        line = ",".join([str(v) for v in values])
        future = self.producer.send(self.topic, key=None, value=bytes(line, 'utf-8'))
        future.get(10)
