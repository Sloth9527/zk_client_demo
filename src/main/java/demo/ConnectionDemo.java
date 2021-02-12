package demo;

import java.io.IOException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionDemo {
  // session的时效时间
  private static final int SESSION_TIMEOUT = 3000;
  // 创建Logger对象
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDemo.class);
  // zookeeper连接对象
  public ZooKeeper zookeeper;

  // server ips
  private static final String SERVER_IPS = "0.0.0.0:2181,0.0.0.0:2182,0.0.0.0:2183";

  private Watcher watcher =
      new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
          LOGGER.info("process:" + watchedEvent.getType());
        }
      };

  public void connect() throws IOException {
    this.zookeeper = new ZooKeeper(SERVER_IPS, SESSION_TIMEOUT, this.watcher);
  }

  public void close() {
    if (this.zookeeper != null) {
      try {
        this.zookeeper.close();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public String createNode(String path, byte[] date) {
    String result = null;
    try {
      result = this.zookeeper.create(path, date, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
      Thread.sleep(30000);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("create node success, result={}", result);

    return result;
  }

  public void deleteNode(String path) {
    try {
      this.zookeeper.delete(path, -1);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
  }

  public String getNodeData(String path) {
    String result = null;
    try {
      byte[] data = this.zookeeper.getData(path, null, null);
      result = new String(data);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("{} node data: {}", path, result);

    return result;
  }

  public String setNodeData(String path, byte[] data) {
    Stat result = null;
    try {
      result = this.zookeeper.setData(path, data, -1);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("{} node data:{}", path, result.toString());
    return result.toString();
  }

  public void setNodeWatch(String path, Watcher watcher) {
    try {
      this.zookeeper.exists(path, watcher);
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("{} node is watched", path);
  }

  public Boolean isExistNode(String path) {
    Boolean result = false;
    try {
      Stat data = this.zookeeper.exists(path, null);
      result = data != null;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    LOGGER.info("{} node {}", path, result ? "is existed" : "not found");

    return result;
  }
}
