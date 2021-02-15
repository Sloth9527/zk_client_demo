package RMI.common.impl;

import RMI.common.Provider;
import RMI.common.ZKClient;
import demo.ConnectionDemo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderImpl implements Provider {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionDemo.class);

  @Override
  public void publish(Remote remote, String host, int port)
      throws IOException, KeeperException, InterruptedException {
    String url = this.registry(remote, host, port);
    if (url != null) {
      ZKClient zk = new ZKClientImpl();
      zk.createRegistry();
      zk.createProvider(url);
      LOGGER.info("publish successful");
    }
  }

  private String registry(Remote remote, String host, int port)
      throws RemoteException, MalformedURLException {
    String url = String.format("rmi://%s:%d/%s", host, port, remote.getClass().getName());
    LocateRegistry.createRegistry(port);
    Naming.rebind(url, remote);

    LOGGER.info("LocateRegistry bind : {}", url);

    return url;
  }
}
