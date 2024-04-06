package uk.co.ltheobald.trymsk;

import com.pulumi.aws.*;

class KafkaStack extends Stack {
    public static void main(String[] args) {
        
/*
         Cluster kafkaCluster = new Cluster("my-kafka-cluster", new ClusterArgs()
            .setBrokerNodeGroupInfo(new ClusterArgs.BrokerNodeGroupInfoArgs()
                .setAzDistribution("DEFAULT")
                .setInstanceType("kafka.t3.small")
                .setEbsVolumeSize(1))
            .setKafkaVersion("3.5.1")
            .setNumberOfBrokerNodes(3));
*/
        //this.export("kafkaClusterArn", kafkaCluster.getArn());
        //this.export("kafkaClusterZookeeperConnectString", kafkaCluster.getZookeeperConnectString());
    }
}