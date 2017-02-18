# Apache Spark and Kafka integration and Spark Analytics over DataFrame.
This example shows how to send processing results from Spark Streaming to Apache Kafka in reliable way. 
It will help you to understand the complete flow of spark streaming and analytics.

# Quickstart guide
Step 1: Download the code

Download the 0.10.1.0 release and un-tar it.
> tar -xzf kafka_2.11-0.10.1.0.tgz
> cd kafka_2.11-0.10.1.0

Step 2: Start the server

Kafka uses ZooKeeper so you need to first start a ZooKeeper server if you don't already have one. 
You can use the convenience script packaged with kafka to get a quick-and-dirty single-node ZooKeeper instance.

> bin/zookeeper-server-start.sh config/zookeeper.properties
[2013-04-22 15:01:37,495] INFO Reading configuration from: config/zookeeper.properties (org.apache.zookeeper.server.quorum.QuorumPeerConfig)
...
Now start the Kafka server:

> bin/kafka-server-start.sh config/server.properties
[2013-04-22 15:01:47,028] INFO Verifying properties (kafka.utils.VerifiableProperties)
[2013-04-22 15:01:47,051] INFO Property socket.send.buffer.bytes is overridden to 1048576 (kafka.utils.VerifiableProperties)
...
Step 3: Create a topic

Let's create a topic named "test" with a single partition and only one replica:

> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
We can now see that topic if we run the list topic command:

> bin/kafka-topics.sh --list --zookeeper localhost:2181
test

Step 4: Send some messages

Kafka comes with a command line client that will take input from a file or from standard input and 
send it out as messages to the Kafka cluster. By default, each line will be sent as a separate message.

Run the producer and then type a few messages into the console to send to the server.

> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
This is a message
This is another message

Step 5: Start a consumer

Kafka also has a command line consumer that will dump out messages to standard output.

> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
This is a message
This is another message

Now create a new topic with a replication factor of three:

> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
Okay but now that we have a cluster how can we know which broker is doing what? To see that run the "describe topics" command:

> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic	PartitionCount:1	ReplicationFactor:3	Configs:
	Topic: my-replicated-topic	Partition: 0	Leader: 1	Replicas: 1,2,0	Isr: 1,2,0
Here is an explanation of output. The first line gives a summary of all the partitions, each additional line gives information about one partition. Since we have only one partition for this topic there is only one line.

"leader" is the node responsible for all reads and writes for the given partition. 
Each node will be the leader for a randomly selected portion of the partitions.
"replicas" is the list of nodes that replicate the log for this partition regardless of whether they are the leader or 
even if they are currently alive.
"isr" is the set of "in-sync" replicas. This is the subset of the replicas list that is currently alive and caught-up to the leader.

We can run the same command on the original topic we created to see where it is:
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic test
Topic:test	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: test	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
So there is no surprise there—the original topic has no replicas and is on server 0, the only server in our cluster when we created it.

Let's publish a few messages to our new topic:
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic
...
my test message 1
my test message 2
^C

Now let's consume these messages:

> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
^C

# Spark helping commands
To Run Producer(KafkaProducer)
java -cp /home/riveriq/Projects/SparkKafkaIntegration/SparkKafkaIntegration-jar-with-dependencies.jar:/etc/kafka_2.10-0.8.2.0/libs/* com.riveriq.kafkaproducer.KafkaProducer JSON

To Run comsumer(SparkKafkaIntegration)
./spark-submit --class "com.riveriq.sparkkafkaintegration.SparkKafkaIntegration" \
--master local[2] \
/home/riveriq/Projects/SparkKafkaIntegration/SparkKafkaIntegration-jar-with-dependencies.jar localhost:2181 test-consumer-group kafkatest 1
