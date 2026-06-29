package javax.bluetooth;

import java.io.IOException;

public class BluetoothConnectionException extends IOException {
   private int _error;
   public static final int UNKNOWN_PSM = 1;
   public static final int SECURITY_BLOCK = 2;
   public static final int NO_RESOURCES = 3;
   public static final int FAILED_NOINFO = 4;
   public static final int TIMEOUT = 5;
   public static final int UNACCEPTABLE_PARAMS = 6;

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
