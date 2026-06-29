package net.rim.wica.transport.util;

public class DataException extends Exception {
   private int _errorCode;
   public static final int FORMAT_ERROR;
   public static final int EOD_ERROR;

   public DataException(int errorCode) {
      this._errorCode = errorCode;
   }

   public DataException(int errorCode, String s) {
      super(s);
      this._errorCode = errorCode;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object("Error Code: "))).append(String.valueOf(this._errorCode)).append(" ").append(super.toString()).toString();
   }

   public int getErrorCode() {
      return this._errorCode;
   }
}
