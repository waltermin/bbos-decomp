package java.rmi;

import java.io.IOException;

public class RemoteException extends IOException {
   public Throwable detail;

   public RemoteException() {
   }

   public RemoteException(String s) {
      super(s);
   }

   public RemoteException(String s, Throwable throwable) {
      super(s);
      this.detail = throwable;
   }

   @Override
   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "\n Nested Exception: " + this.detail.toString();
   }
}
