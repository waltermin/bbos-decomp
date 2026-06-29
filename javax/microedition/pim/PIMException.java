package javax.microedition.pim;

public class PIMException extends Exception {
   private int _reason = 1;
   public static final int FEATURE_NOT_SUPPORTED = 0;
   public static final int GENERAL_ERROR = 1;
   public static final int LIST_CLOSED = 2;
   public static final int LIST_NOT_ACCESSIBLE = 3;
   public static final int MAX_CATEGORIES_EXCEEDED = 4;
   public static final int UNSUPPORTED_VERSION = 5;
   public static final int UPDATE_ERROR = 6;

   public PIMException() {
   }

   public PIMException(String detailMessage) {
      super(detailMessage);
   }

   public PIMException(String detailMessage, int reason) {
      super(detailMessage);
      this._reason = reason;
   }

   public int getReason() {
      return this._reason;
   }
}
