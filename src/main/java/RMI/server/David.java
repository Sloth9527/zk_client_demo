package RMI.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface David extends Remote {
  String sayHello(String name) throws RemoteException;
}
