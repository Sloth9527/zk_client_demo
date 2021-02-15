package RMI.common;

import java.io.IOException;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public interface ZKClient {
  String ZK_SERVER_CLUSTER = "0.0.0.0:2181,0.0.0.0:2182,0.0.0.0:2183";
  int SESSION_TIMEOUT = 5000;
  String ZK_REGISTRY_PATH = "/registry";
  String ZK_PROVIDER_PATH = ZK_REGISTRY_PATH + "/provider";

  ZooKeeper connectCluster() throws IOException, InterruptedException;

  void closeCluster() throws InterruptedException;

  void createRegistry() throws KeeperException, InterruptedException;

  Boolean isRegistryExist() throws KeeperException, InterruptedException;

  String createProvider(String url) throws KeeperException, InterruptedException;

  List<String> getProviderList(Watcher watcher) throws KeeperException, InterruptedException;

  byte[] getProviderData(String providerName) throws KeeperException, InterruptedException;
}
