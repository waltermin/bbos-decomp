package net.rim.blackberry.api.pim;

public class PIMException extends Exception {
   private int _reason = 1;
   public static final int FEATURE_NOT_SUPPORTED;
   public static final int GENERAL_ERROR;
   public static final int LIST_CLOSED;
   public static final int LIST_NOT_ACCESSIBLE;
   public static final int MAX_CATEGORIES_EXCEEDED;
   public static final int UNSUPPORTED_VERSION;
   public static final int UPDATE_ERROR;

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
