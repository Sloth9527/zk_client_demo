package RMI.server;

import RMI.common.Provider;
import RMI.common.impl.ProviderImpl;
import RMI.server.impl.DavidImpl;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class ServerApp {
  public static void main(String[] args) throws Exception {
    Options options = new Options();

    options.addRequiredOption("h", "host", true, "host name");
    options.addRequiredOption("p", "port", true, "port");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    David david = new DavidImpl();
    Provider provider = new ProviderImpl();
    int port = Integer.parseInt(cmd.getOptionValue("p"));
    provider.publish(david, cmd.getOptionValue("h"), port);

    Thread.sleep(Long.MAX_VALUE);
  }
}
