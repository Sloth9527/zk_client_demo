package RMI.common.impl;

import RMI.common.ZKClient;
import demo.ConnectionDemo;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKClientImpl implements ZKClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDemo.class);
  @Setter private ZooKeeper zk;
  @Getter private CountDownLatch latch = new CountDownLatch(1);

  public ZooKeeper getZk() {
    if (this.zk == null) {
      try {
        this.setZk(this.connectCluster());
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.error(e.getMessage());
      }
    }
    return this.zk;
  }

  @Override
  public ZooKeeper connectCluster() throws IOException {
    if (this.zk != null) {
      return this.getZk();
    }
    LOGGER.info("start connect cluster : {}", ZK_SERVER_CLUSTER);
    return new ZooKeeper(
        ZK_SERVER_CLUSTER,
        SESSION_TIMEOUT,
        watchedEvent -> {
          if (watchedEvent.getState() == KeeperState.SyncConnected) {
            LOGGER.info("connect cluster successful.");
            ZKClientImpl.this.getLatch().countDown();
          }
        });
  }

  @Override
  public void closeCluster() throws InterruptedException {
    if (this.getZk() != null) {
      this.zk.close();
    }
  }

  @Override
  public void createRegistry() throws KeeperException, InterruptedException {
    if (!this.isRegistryExist()) {
      this.getZk()
          .create(
              ZK_REGISTRY_PATH,
              "provider registry".getBytes(),
              Ids.OPEN_ACL_UNSAFE,
              CreateMode.PERSISTENT);
      LOGGER.info("create registry successful");
    }
  }

  @Override
  public Boolean isRegistryExist() throws KeeperException, InterruptedException {
    Stat data = this.getZk().exists(ZK_REGISTRY_PATH, false);
    return data != null;
  }

  @Override
  public String createProvider(String url) throws KeeperException, InterruptedException {
    String res =
        this.getZk()
            .create(
                ZK_PROVIDER_PATH,
                url.getBytes(),
                Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);

    LOGGER.info("create provider successful");
    LOGGER.info("providerPath: {}", res);

    return res;
  }

  @Override
  public byte[] getProviderData(String providerName) throws KeeperException, InterruptedException {
    String path = String.format("%s/%s", ZK_REGISTRY_PATH, providerName);
    return this.getZk().getData(path, null, null);
  }

  @Override
  public List<String> getProviderList(Watcher watcher)
      throws KeeperException, InterruptedException {
    return this.getZk().getChildren(ZK_REGISTRY_PATH, watcher);
  }
}
