package net.rim.wica.transport.util;

public class DataException extends Exception {
   private int _errorCode;
   public static final int FORMAT_ERROR = 1;
   public static final int EOD_ERROR = 2;

   public DataException(int errorCode) {
      this._errorCode = errorCode;
   }

   public DataException(int errorCode, String s) {
      super(s);
      this._errorCode = errorCode;
   }

   @Override
   public String toString() {
      return "Error Code: " + String.valueOf(this._errorCode) + " " + super.toString();
   }

   public int getErrorCode() {
      return this._errorCode;
   }
}
