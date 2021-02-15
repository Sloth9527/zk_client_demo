package RMI.client;

import RMI.client.impl.ConsumerImpl;
import RMI.server.David;

public class ClientApp {
  public static void main(String[] args) throws Exception {
    Consumer consumer = new ConsumerImpl();
    consumer.watchRegistry();

    David david = consumer.lookup();
    String response = david.sayHello("jk");
    System.out.println(response);
  }
}
