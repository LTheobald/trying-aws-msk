package uk.co.ltheobald.trymsk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pulumi.Pulumi;
import com.pulumi.awsnative.ec2.Subnet;
import com.pulumi.awsnative.ec2.SubnetArgs;
import com.pulumi.awsnative.ec2.Vpc;
import com.pulumi.awsnative.ec2.VpcArgs;
import com.pulumi.awsnative.msk.Cluster;
import com.pulumi.awsnative.msk.ClusterArgs;
import com.pulumi.awsnative.msk.inputs.ClusterBrokerNodeGroupInfoArgs;
import com.pulumi.core.Output;
import com.pulumi.resources.CustomResourceOptions;

public class KafkaStack {
  // TODO Fetch from the config
  private static final String AWS_REGION = "eu-west-1";

  public static void main(String... args) {
    Pulumi.run(ctx -> {
      Vpc vpc = new Vpc("vpc", VpcArgs.builder().cidrBlock("10.0.0.0/16").build());

      // Create 3 Subnets for the VPC
      CustomResourceOptions dependsOnVpc = CustomResourceOptions.builder().dependsOn(vpc).build();
      List<String> availabilityZones = Arrays.asList(AWS_REGION + "a", AWS_REGION + "b",
          AWS_REGION + "c");
      List<Subnet> subnets = new ArrayList<>();
      List<Output<String>> subnetIds = new ArrayList<>();
      for (int i = 0; i < 3; i++) {
        Subnet current = new Subnet(String.format("my-subnet-%d", i), SubnetArgs.builder()
            .vpcId(vpc.id())
            .cidrBlock(String.format("10.0.%d.0/24", i))
            .availabilityZone(availabilityZones.get(i))
            .build(), dependsOnVpc);
        subnets.add(current);
        subnetIds.add(current.id());
      }

      CustomResourceOptions dependsOnSubnets = CustomResourceOptions.builder()
          .dependsOn(subnets.get(2)).build();
      Cluster kafkaCluster = new Cluster("my-msk-cluster",
          ClusterArgs.builder().brokerNodeGroupInfo(
              ClusterBrokerNodeGroupInfoArgs.builder()
                  .brokerAzDistribution("DEFAULT")
                  .clientSubnets(Output.all(subnetIds))
                  .instanceType("kafka.t3.small").build())
              .kafkaVersion("3.5.1")
              .numberOfBrokerNodes(3)
              .build(),
          dependsOnSubnets);

      ctx.export("kafkaClusterArn", kafkaCluster.arn());
      ctx.export("kafkaClusterName", kafkaCluster.clusterName());
    });
  }
}