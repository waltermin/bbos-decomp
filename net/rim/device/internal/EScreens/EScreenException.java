package net.rim.device.internal.EScreens;

public final class EScreenException extends RuntimeException {
   private int _code;

   public EScreenException(String msg) {
   }

   public EScreenException(int code) {
      this._code = code;
   }

   public final int getCode() {
      return this._code;
   }

   @Override
   public final String toString() {
      StringBuffer strBuf = new StringBuffer();
      String message = this.getMessage();
      strBuf.append(this.getClass().getName());
      if (message != null) {
         strBuf.append(": ");
         strBuf.append(message);
      }

      strBuf.append(',');
      strBuf.append(this.translateErrorCode(this._code));
      return strBuf.toString();
   }

   private final String translateErrorCode(int code) {
      switch (code) {
         case -7:
            return " EScreen error: " + code;
         case -6:
            return " Bad user data";
         case -5:
            return " Null pointer";
         case -4:
            return " Bad action ID";
         case -3:
            return " Bad item ID";
         case -2:
            return " Bad screen ID";
         case -1:
            return " General error";
         case 0:
         default:
            return " OK";
      }
   }
}
