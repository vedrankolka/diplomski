package main

import (
	"fmt"
	"log"
	"os"

	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/joho/godotenv"
	"github.com/magiconair/properties"
)

var topicKey = "FLOW_CSV_LABELED"
var outputKey = "OUTPUT_FILE"

func main() {
	if len(os.Args) < 2 {
		fmt.Println("The program expects an argument: <path_to_properties_file> [<path_to_env_file>]")
		os.Exit(1)
	}
	// load consumer properties
	props := properties.MustLoadFile(os.Args[1], properties.UTF8)
	// load .env file if given
	if len(os.Args) >= 3 {
		godotenv.Load(os.Args[2])
	}

	outputFileName := getFromEnv(outputKey)

	file, err := os.OpenFile(outputFileName, os.O_APPEND|os.O_WRONLY, 0644)

	if err != nil {
		if os.IsNotExist(err) {
			// if error is err.NotExists, create the file
			file, err = os.Create(outputFileName)
			if err != nil {
				log.Fatalf("Could not create output file: %v\n", err)
			}
		} else {
			// else it's a different error
			log.Fatalf("Could not opet file %s: %v\n", outputFileName, err)
		}
	}

	defer file.Close()

	consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": props.MustGetString("bootstrap.servers"),
		"group.id":          props.MustGetString("group.id"),
		"auto.offset.reset": props.MustGetString("auto.offset.reset"),
	})

	if err != nil {
		log.Fatalf("Could not connect to Kafka :%v\n", err)
	}

	defer consumer.Close()

	consumer.SubscribeTopics([]string{getFromEnv(topicKey)}, nil)

	// TODO u jednoj dretvi zapocet ovo slusanje a u drugoj cekat CTRL+C brijem?
	// u svakom slucaju treba nekak docekat ctrl c
	// TODO napravit ispis npr broja obrađenih poruka
	counter := 0
	// save cursor position
	// fmt.Print("\033[s")
	for {
		msg, err := consumer.ReadMessage(-1)
		if err == nil {
			n, err := file.WriteString(string(msg.Value) + "\n")
			if n != len(msg.Value)+1 || err != nil {
				log.Printf("Could not write message %q: %v\n", string(msg.Value), err)
			} else {
				counter++
				// reset cursor position, write line and move cursor up
				fmt.Print("\033[G\033[K")
				fmt.Printf("Read %d messages.\n", counter)
				fmt.Print("\033[A")
			}

			// fmt.Printf("Message on %s: %s\n", msg.TopicPartition, string(msg.Value))
		} else {
			// The client will automatically try to recover from all errors.
			log.Printf("Consumer error: %v (%v)\n", err, msg)
		}
	}
}

func getFromEnv(key string) string {
	value := os.Getenv(key)
	if value == "" {
		log.Fatalf("No environment variable with name " + key)
	}

	return value
}
