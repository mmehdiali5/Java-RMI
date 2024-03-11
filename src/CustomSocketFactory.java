import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;


//  CustomSocketFactory is a custom implementation of RMIClientSocketFactory
//  that creates sockets with a specified timeout value for RMI client connections.
//  This factory allows setting a timeout on the sockets used by RMI clients
//  to control the duration for establishing connections.

public class CustomSocketFactory implements RMIClientSocketFactory {

  private final int timeout;

  public CustomSocketFactory(int timeout) {
    this.timeout = timeout;
  }

  @Override
  public Socket createSocket(String host, int port) throws IOException {
    Socket socket = new Socket(host, port);
    socket.setSoTimeout(timeout); // Set timeout
    return socket;
  }
}
