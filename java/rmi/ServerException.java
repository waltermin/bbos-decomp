package java.rmi;

public class ServerException extends RemoteException {
   public ServerException(String message) {
   }

   public ServerException(String message, Exception exception) {
   }
}
