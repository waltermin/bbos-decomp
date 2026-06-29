package net.rim.plazmic.mediaengine;

public class MediaException extends Exception {
   private int _code = -1;
   private Object _data;
   public static final int UPGRADE_PLAYER = 1;
   public static final int UPGRADE_MEDIA = 2;
   public static final int UNSUPPORTED_MEDIA = 11;
   public static final int INVALID_HEADER = 3;
   public static final int OUT_OF_BOUNDS = 4;
   public static final int INTERRUPTED_DOWNLOAD = 5;
   public static final int REQUEST_TIMED_OUT = 7;
   public static final int UNSUPPORTED_TYPE = 8;
   public static final int INSUFFICIENT_SPACE = 9;
   public static final int CHECKSUM_MISMATCH = 10;
   public static final int INVALID_SUB_PME = 11;
   private static final int DEFAULT_ERROR_CODE = -1;

   public MediaException() {
      this(-1, null, null);
   }

   public MediaException(String msg) {
      this(-1, msg, null);
   }

   public MediaException(int code) {
      this(code, null, null);
   }

   public MediaException(int code, String msg) {
      this(code, msg, null);
   }

   public MediaException(int code, String msg, Object data) {
      super(msg);
      this._code = code;
      this._data = data;
   }

   public int getCode() {
      return this._code;
   }

   public Object getData() {
      return this._data;
   }
}
