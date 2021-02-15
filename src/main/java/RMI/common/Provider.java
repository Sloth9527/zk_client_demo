package RMI.common;

import java.io.IOException;
import java.rmi.Remote;
import org.apache.zookeeper.KeeperException;

public interface Provider {
  void publish(Remote remote, String host, int port)
      throws IOException, KeeperException, InterruptedException;
}
