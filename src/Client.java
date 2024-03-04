import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//A client program to invoke remote methods on the server.
public class Client {

  //A helper function to get the current time.
  private static String getCurrentTime() {
    LocalDateTime currentDateTime = LocalDateTime.now();

    // Create a DateTimeFormatter to format the output (optional)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    // Format and print the current date and time
    return currentDateTime.format(formatter);
  }

  //A generic function to remotely invoke methods on the server. It checks the type of request and then sends
  //the respective request to the server by invoking the respective remote method.
  private static void sendRequest(String type, String key, String value, ServerInterface server)
          throws ServerNotActiveException {
    Response res = null;
    try {
      switch (type) {
        case "put":
          res = server.put(key, value);
          break;
        case "get":
          res = server.get(key);
          break;
        case "del":
          res = server.delete(key);
          break;
      }
      if (res != null) {
        System.out.println(getCurrentTime() + " " + res);
      } else {
        System.out.println("Please enter valid request type");
      }
    }
    //If the client is unable to communicate with the server a ServerNotActiveException is thrown which
    //is handled by the main method and prints the error message.
    catch (ConnectException e) {
      throw new ServerNotActiveException();
    }
    //While invoking the remote method if any exception occurs due to Unmarshalling etc. a remote exception is thrown
    //which is handled by the client by displaying the respective error message.
    catch (RemoteException e) {
      System.out.println(getCurrentTime() + " " + e.getMessage());
    }
    //If client is unable to cast the response object it means the response got malformed on the way to client
    //This is handled by displaying proper error message to the client log.
    catch (ClassCastException e) {
      System.out.println(getCurrentTime() + " Received malformed response from the server");
    }
  }

  //An interactive runUI function which keeps running till the user decides to close the client.
  private static void runUI(ServerInterface server) throws ServerNotActiveException {
    boolean stop = false;
    while (!stop) {
      System.out.println();
      System.out.println("Choose From Following Options:\n1) PUT\n2) GET\n3) DELETE\n4) CLOSE CLIENT\n");
      Scanner scanner = new Scanner(System.in);

      // Read the user's input as a String
      String option = scanner.nextLine();
      String key = "";
      String value = "";
      switch (option) {
        case "1":
          System.out.print("Enter Key: ");
          key = scanner.nextLine();
          System.out.print("Enter Value: ");
          value = scanner.nextLine();
          sendRequest("put", key, value, server);
          break;
        case "2":
          System.out.print("Enter Key: ");
          key = scanner.nextLine();
          sendRequest("get", key, "", server);
          break;
        case "3":
          System.out.print("Enter Key: ");
          key = scanner.nextLine();
          sendRequest("del", key, "", server);
          break;
        case "4":
          System.out.println("Client Closed");
          stop = true;
          break;
        default:
          System.out.println("Please enter valid Input");
          break;
      }

    }
  }


  public static void main(String[] args) {
//    String address = "localhost";
//    int PORT = 32000;
//    String name = "Server";

    if(args.length<3){
      System.err.println("Please enter address, PORT and name of the server");
      System.exit(0);
    }
    String address=args[0];
    int PORT = Integer.parseInt(args[1]);
    String name = args[2];

    try {
      //Get the remote server reference using LocateRegistry.getRegistry
      Registry registry = LocateRegistry.getRegistry(address, PORT);
      //Get the remote reference bound to the specified name in this registry
      ServerInterface server = (ServerInterface) registry.lookup(name);

      try {

        //Pre-populate the store
        prepopulate(server);

        runUI(server);
      }
      //If server is not available then exception is handled by displaying proper error message.
      catch (ServerNotActiveException e) {
        System.err.println("Unable to connect to the Server.");
      }

    }
    //If unable to get the reference of the remote object the exception is handled by displaying proper error message.
    catch (NotBoundException | RemoteException e) {
      System.err.println("Unable to connect to the Server. Please recheck address, PORT and name of the server" +
              " and try again.");
    }

  }

  //A private function to pre-populate the store.
  private static void prepopulate(ServerInterface server) throws ServerNotActiveException {
    //Pre-Populate
    sendRequest("put", "key1", "value1", server);
    sendRequest("put", "key2", "value2", server);
    sendRequest("put", "key3", "value3", server);


    sendRequest("put", "key4", "value1", server);
    sendRequest("get", "key4", "", server);
    sendRequest("del", "key4", "", server);

    //Valid Delete
    sendRequest("del", "key1", "", server);
    //Invalid GET
    sendRequest("get", "key1", "", server);


    sendRequest("put", "key5", "value5", server);
    sendRequest("get", "key5", "", server);

    //Update value and get new value
    sendRequest("put", "key5", "value6", server);
    sendRequest("get", "key5", "", server);

    sendRequest("put", "key7", "value7", server);
    sendRequest("put", "key8", "value8", server);
    sendRequest("del", "key8", "", server);
    //Invalid GET
    sendRequest("get", "key8", "", server);


    sendRequest("del", "key7", "", server);
    //Invalid GET
    sendRequest("get", "key7", "", server);

    //Invalid Delete
    sendRequest("del", "INVALID", "", server);
  }

}