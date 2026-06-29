package java.lang;

import net.rim.vm.TraceBack;

public class Throwable {
   private String detailMessage;
   private Object backtrace;

   public Throwable() {
   }

   public Throwable(String message) {
      this.detailMessage = message;
   }

   public String getMessage() {
      return this.detailMessage;
   }

   @Override
   public String toString() {
      String s = this.getClass().getName();
      String message = this.getMessage();
      return message != null ? s + ": " + message : s;
   }

   public void printStackTrace() {
      if (this.backtrace != null) {
         TraceBack.printStackTrace(System.err, this.backtrace);
      } else {
         System.err.println("No stack trace");
      }
   }
}
