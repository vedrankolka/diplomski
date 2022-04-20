

class KafkaCSVProducer:
    # TODO actually implement this
    def __init__(self, topic, broker):
        self.topic = topic
        self.broker = broker

    def writerow(self, values):
        line = ",".join([str(v) for v in values])
        print(f'broker={self.broker} topic={self.topic} line="{line}"')
