import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;


//ServerInterface extends Remote interface which serves to identify interfaces whose methods may be
// invoked from a non-local virtual machine.
public interface ServerInterface extends Remote {
  //A method to handle remote PUT requests. Takes key and value as arguments.
  //If key doesn't exist a new entry is made in the store else older key is overwritten.
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  Response put(String key, String value) throws RemoteException, ServerNotActiveException;

  //A method to handle remote GET requests. Takes key as argument.
  //If key doesn't exist a 404 error message is sent to client else value of the key is sent with status 200.
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  Response get(String key) throws RemoteException, ServerNotActiveException;

  //A method to handle remote DELETE requests. Takes key as argument.
  //If key doesn't exist a 404 error message is sent to client else the entry with given key is deleted.
  //Throws ServerNotActiveException if no remote method invocation is being processed in the current thread.
  //RemoteException is thrown if client tries to invoke but invocation is failed due to Unmarshalling or malformed packets.
  Response delete(String key) throws RemoteException, ServerNotActiveException;
}
