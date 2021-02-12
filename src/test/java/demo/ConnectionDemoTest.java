package demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConnectionDemoTest {
  private ConnectionDemo ccDemo;

  @BeforeEach
  void setUp() {
    this.ccDemo = new ConnectionDemo();
    try {
      this.ccDemo.connect();
      ;
    } catch (IOException e) {
      e.printStackTrace();
      ;
    }
  }

  @AfterEach
  void tearDown() {
    this.ccDemo.close();
    ;
  }

  @Test
  void createNodeTest() {
    String path = "/zk001";
    String result = this.ccDemo.createNode(path, "zk001-data".getBytes());

    assertEquals(path, result);
  }

  @Test
  void deleteNodeTest() {
    String path = "/zk002";
    this.ccDemo.createNode(path, "delete data".getBytes());

    this.ccDemo.deleteNode(path);

    Boolean result = this.ccDemo.isExistNode(path);

    assertFalse(result);
  }

  @Test
  void getNodeDataTest() {
    String path = "/zk003";
    String data = "zk003-data";

    this.ccDemo.createNode(path, data.getBytes());

    String result = this.ccDemo.getNodeData(path);

    assertEquals(data, result);
  }

  @Test
  void setNodeDataTest() {
    String path = "/zk004";
    String data = "zk004-data";
    String update_data = "zk004-data-update";

    this.ccDemo.createNode(path, data.getBytes());
    this.ccDemo.setNodeData(path, update_data.getBytes());

    String result = this.ccDemo.getNodeData(path);

    assertEquals(update_data, result);
  }

  @Test
  void isExistNodeTest() {
    String path = "/zk005";
    String data = "zk005-data";

    this.ccDemo.createNode(path, data.getBytes());

    Boolean result = this.ccDemo.isExistNode(path);

    assertTrue(result);
  }

  @Test
  void setNodeWatchTest() {
    String path = "/zk005";
    String data = "zk005-data";
    final String[] update_data = {"zk005-data-update"};
    final int[] result = {0};

    this.ccDemo.createNode(path, data.getBytes());

    Watcher watcher =
        new Watcher() {
          @Override
          public void process(WatchedEvent watchedEvent) {
            result[0] = result[0] + 1;
          }
        };
    this.ccDemo.setNodeWatch(path, watcher);
    this.ccDemo.setNodeData(path, update_data[0].getBytes());
    this.ccDemo.deleteNode(path);

    assertEquals(1, result[0]);
  }
}
