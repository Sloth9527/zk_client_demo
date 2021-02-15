package RMI.client;

import java.rmi.Remote;
import java.util.List;
import org.apache.zookeeper.KeeperException;

public interface Consumer {
  <T extends Remote> T lookup();

  <T extends Remote> T getServer(String url);

  String getUrl();

  void updateRegistryList(List<String> providerList) throws KeeperException, InterruptedException;

  void watchRegistry() throws KeeperException, InterruptedException;
}
