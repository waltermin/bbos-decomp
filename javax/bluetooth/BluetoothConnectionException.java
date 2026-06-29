package javax.bluetooth;

import java.io.IOException;

public class BluetoothConnectionException extends IOException {
   private int _error;
   public static final int UNKNOWN_PSM;
   public static final int SECURITY_BLOCK;
   public static final int NO_RESOURCES;
   public static final int FAILED_NOINFO;
   public static final int TIMEOUT;
   public static final int UNACCEPTABLE_PARAMS;

   public BluetoothConnectionException(int error) {
      this.checkError(error);
      this._error = error;
   }

   public BluetoothConnectionException(int error, String msg) {
      super(msg);
      this.checkError(error);
      this._error = error;
   }

   private void checkError(int error) {
      switch (error) {
         case 0:
         default:
            throw new Object();
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
      }
   }

   public int getStatus() {
      return this._error;
   }
}
