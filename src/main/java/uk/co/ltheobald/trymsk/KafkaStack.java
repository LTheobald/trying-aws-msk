package uk.co.ltheobald.trymsk;

import com.pulumi.Pulumi;
import com.pulumi.Stack;
import com.pulumi.awsnative.msk.Cluster;
import com.pulumi.awsnative.msk.ClusterArgs;
import com.pulumi.awsnative.msk.inputs.ClusterBrokerNodeGroupInfoArgs;

public class KafkaStack extends Stack {
    public static void main(String... args) {
        Pulumi.run(ctx -> {
            Cluster kafkaCluster = new Cluster("my-msk-cluster",
                    ClusterArgs.builder().brokerNodeGroupInfo(
                            ClusterBrokerNodeGroupInfoArgs.builder()
                                    .brokerAzDistribution("DEFAULT")
                                    .instanceType("kafka.t5.small").build())
                            .kafkaVersion("3.5.1")
                            .numberOfBrokerNodes(3)
                            .build());

            ctx.export("kafkaClusterArn", kafkaCluster.arn());
        });
    }
}