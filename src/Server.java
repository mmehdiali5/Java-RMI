import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


//Server class extends UnicastRemoteObject class to make Server class Remote. Also Server Interface
//extends Remote class which serves to identify interfaces whose methods may be invoked from a non-local virtual machine
public class Server extends UnicastRemoteObject implements ServerInterface {
  //The store which application uses to manage data. This store stores String Key, value pairs.
  private HashMap<String, String> store;


  public Server() throws RemoteException {
    super();
    store = new HashMap<>();
  }

  // A function to get current time in yyyy-MM-dd HH:mm:ss:SSS in a String to be used in log
  private static String getCurrentTime() {
    LocalDateTime currentDateTime = LocalDateTime.now();

    // Create a DateTimeFormatter to format the output (optional)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    // Format and print the current date and time
    return currentDateTime.format(formatter);
  }

  //A method to handle remote PUT requests. Takes key and value as arguments.
  //If key doesn't exist a new entry is made in the store else older key is overwritten.
  //Note that method is marked as synchronized to make it thread-safe and avoid concurrent read/writes
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  @Override
  public synchronized Response put(String key, String value) throws RemoteException, ServerNotActiveException {
    store.put(key, value);
    String clientHost = RemoteServer.getClientHost();

    Response res = new Response("200", "PUT Request successful with key = " + key + " and value = "
            + value);
    System.out.println(getCurrentTime() + " Received PUT REQUEST" + " FROM " + clientHost +
            " with key = " + key + " and value = " + value + " " + res);
    return res;

    //To mimic Malformed Response
    //return (Response) new Object();
  }

  //A method to handle remote GET requests. Takes key as argument.
  //If key doesn't exist a 404 error message is sent to client else value of the key is sent with status 200.
  //Note that method is marked as synchronized to make it thread-safe and avoid concurrent read/writes
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  @Override
  public synchronized Response get(String key) throws RemoteException, ServerNotActiveException {
    String clientHost = RemoteServer.getClientHost();
    Response res = null;
    if (!store.containsKey(key)) {
      res = new Response("404", "Key = " + key + " not found");
    } else {
      res = new Response("200", "GET Request Successful with key = " + key + ", value = " + store.get(key));
    }
    System.out.println(getCurrentTime() + " Received GET REQUEST " + " FROM " + clientHost
            + " with key = " + key + " " + res);
    return res;
  }

  //A method to handle remote DELETE requests. Takes key as argument.
  //If key doesn't exist a 404 error message is sent to client else the entry with given key is deleted.
  //Note that method is marked as synchronized to make it thread-safe and avoid concurrent read/writes
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  @Override
  public synchronized Response delete(String key) throws RemoteException, ServerNotActiveException {
    String clientHost = RemoteServer.getClientHost();
    Response res = null;
    if (!store.containsKey(key)) {
      res = new Response("404", "Key = " + key + " not found");
    } else {
      store.remove(key);
      res = new Response("200", "DELETE Request successful with key = " + key);
    }
    System.out.println(getCurrentTime() + " Received DELETE REQUEST " + " FROM " + clientHost +
            " with key = " + key + " " + res);
    return res;
  }

  public static void main(String[] args) {
    try {
      Server server = new Server();
//      int PORT = 32000;
//      String name = "Server";

      if (args.length < 2) {
        System.err.println("Please enter PORT and name for the server");
        System.exit(0);
      }
      int PORT = Integer.parseInt(args[0]);
      String name = args[1];

      //Creates registry on local host with given port.
      Registry registry = LocateRegistry.createRegistry(PORT);
      //Associate name to remote reference.
      registry.rebind(name, server);
      //Once registered and get bound, server starts to listen on given port.
      System.out.println("Server started...");
    } catch (Exception e) {
      System.err.println("Unable to Register or bind the server to the given PORT or name");
    }
  }
}
