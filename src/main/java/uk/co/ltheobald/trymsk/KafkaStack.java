package uk.co.ltheobald.trymsk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.pulumi.Pulumi;
import com.pulumi.awsnative.ec2.Subnet;
import com.pulumi.awsnative.ec2.SubnetArgs;
import com.pulumi.awsnative.ec2.Vpc;
import com.pulumi.awsnative.ec2.VpcArgs;
import com.pulumi.awsnative.msk.Cluster;
import com.pulumi.awsnative.msk.ClusterArgs;
import com.pulumi.awsnative.msk.inputs.ClusterBrokerNodeGroupInfoArgs;

public class KafkaStack {
    // TODO Fetch from the config
    private static final String AWS_REGION = "eu-west-1";

    public static void main(String... args) {
        Pulumi.run(ctx -> {
            Vpc vpc = new Vpc("vpc", VpcArgs.builder().cidrBlock("10.0.0.0/16").build());
            
            // Create 3 Subnets for the VPC
            List<String> availabilityZones = Arrays.asList(AWS_REGION+"a", AWS_REGION+"b", AWS_REGION+"c");
            List<Subnet> subnets = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                subnets.add(new Subnet(String.format("my-subnet-%d", i), SubnetArgs.builder()
                    .vpcId(vpc.id())
                    .cidrBlock(String.format("10.0.%d.0/24", i))
                    .availabilityZone(availabilityZones.get(i))
                    .build()));git add .
                    clear
                    
            }
            List<String> subnetIds = subnets.stream()
                .map(subnet -> subnet.id().toString())
                .collect(Collectors.toList());

            Cluster kafkaCluster = new Cluster("my-msk-cluster",
                    ClusterArgs.builder().brokerNodeGroupInfo(
                            ClusterBrokerNodeGroupInfoArgs.builder()
                                    .brokerAzDistribution("DEFAULT")
                                    .clientSubnets(subnetIds)
                                    .instanceType("kafka.t5.small").build())
                            .kafkaVersion("3.5.1")
                            .numberOfBrokerNodes(3)
                            .build());

            ctx.export("kafkaClusterArn", kafkaCluster.arn());
            ctx.export("kafkaClusterName", kafkaCluster.clusterName());
        });
    }
}