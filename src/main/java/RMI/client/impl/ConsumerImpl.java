package RMI.client.impl;

import RMI.client.Consumer;
import RMI.common.ZKClient;
import RMI.common.impl.ZKClientImpl;
import demo.ConnectionDemo;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerImpl implements Consumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDemo.class);

  @Setter private ZKClient client = null;
  @Getter @Setter private List<String> registryList = new ArrayList<>();

  public ZKClient getClient() {
    if (this.client == null) {
      this.setClient(new ZKClientImpl());
    }
    return this.client;
  }

  @Override
  public <T extends Remote> T lookup() {
    T server = null;
    String url = this.getUrl();

    LOGGER.info("get url: {}", url);

    if (url != null) {
      server = this.getServer(url);
    }

    return server;
  }

  @Override
  public String getUrl() {
    int size = this.getRegistryList().size();
    if (size == 0) {
      return null;
    } else if (size == 1) {
      return this.getRegistryList().get(0);
    }

    return this.getRegistryList().get(ThreadLocalRandom.current().nextInt(size));
  }

  @Override
  public <T extends Remote> T getServer(String url) {
    T remote = null;

    try {
      remote = (T) Naming.lookup(url);
      LOGGER.info("get server successful");
    } catch (Exception e) {
      LOGGER.error("get server failed");
      if (this.getRegistryList().size() != 0) {
        url = this.getUrl();
        LOGGER.info("start reload server by {}", url);
        return this.getServer(url);
      } else {
        LOGGER.error("registry list is empty");
      }
    }
    return remote;
  }

  @Override
  public void updateRegistryList(List<String> providerList)
      throws KeeperException, InterruptedException {

    LOGGER.info("start update registry list.");
    this.setRegistryList(new ArrayList<>());

    for (String provider : providerList) {
      byte[] data = this.getClient().getProviderData(provider);
      this.getRegistryList().add(new String(data));
    }
    LOGGER.info("update registry list successful.");
  }

  @Override
  public void watchRegistry() throws KeeperException, InterruptedException {
    LOGGER.info("start watch registry.");
    Watcher watcher =
        watchedEvent -> {
          try {
            LOGGER.info("registry changed.");
            ConsumerImpl.this.watchRegistry();
          } catch (Exception e) {
            LOGGER.error(e.getMessage());
          }
        };
    List<String> providerList = this.getClient().getProviderList(watcher);

    this.updateRegistryList(providerList);
  }
}
