package uk.co.ltheobald.trymsk;

import com.pulumi.Pulumi;
import com.pulumi.awsnative.ec2.Subnet;
import com.pulumi.awsnative.ec2.SubnetArgs;
import com.pulumi.awsnative.ec2.Vpc;
import com.pulumi.awsnative.msk.Cluster;
import com.pulumi.awsnative.msk.ClusterArgs;
import com.pulumi.awsnative.msk.inputs.ClusterBrokerNodeGroupInfoArgs;
import com.pulumi.resources.CustomResourceOptions;
import com.pulumi.resources.ResourceOptions;

public class KafkaStack {
    private static final String VPC_ID = "vpc";
    private static final String AWS_REGION = "eu-west-1";

    public static void main(String... args) {
        Pulumi.run(ctx -> {
            Vpc vpc = new Vpc(VPC_ID);
            Subnet subnetA = new Subnet("subnetA",
                    SubnetArgs.builder().availabilityZone(AWS_REGION + "a").vpcId(VPC_ID).build(),
                    CustomResourceOptions.builder().dependsOn(vpc).build());
            Subnet subnetB = new Subnet("subnetB",
                    SubnetArgs.builder().availabilityZone(AWS_REGION + "b").vpcId(VPC_ID).build(),
                    CustomResourceOptions.builder().dependsOn(vpc).build());
            Subnet subnetC = new Subnet("subnetC",
                    SubnetArgs.builder().availabilityZone(AWS_REGION + "c").vpcId(VPC_ID).build(),
                    CustomResourceOptions.builder().dependsOn(vpc).build());

            Cluster kafkaCluster = new Cluster("my-msk-cluster",
                    ClusterArgs.builder().brokerNodeGroupInfo(
                            ClusterBrokerNodeGroupInfoArgs.builder()
                                    .brokerAzDistribution("DEFAULT")
                                    .clientSubnets(subnetA.id().toString(), subnetB.id().toString(), subnetC.id().toString())
                                    .instanceType("kafka.t5.small").build())
                            .kafkaVersion("3.5.1")
                            .numberOfBrokerNodes(3)
                            .build());

            ctx.export("kafkaClusterArn", kafkaCluster.arn());
        });
    }
}