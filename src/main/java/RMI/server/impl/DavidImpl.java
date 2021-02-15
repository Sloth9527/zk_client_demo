package RMI.server.impl;

import RMI.server.David;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DavidImpl extends UnicastRemoteObject implements David {
  public DavidImpl() throws RemoteException {}

  @Override
  public String sayHello(String name) {
    return String.format("Hello %s, I'm David. Please make me into a real life boy", name);
  }
}
