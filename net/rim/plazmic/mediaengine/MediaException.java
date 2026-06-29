package net.rim.plazmic.mediaengine;

public class MediaException extends Exception {
   private int _code = -1;
   private Object _data;
   public static final int UPGRADE_PLAYER;
   public static final int UPGRADE_MEDIA;
   public static final int UNSUPPORTED_MEDIA;
   public static final int INVALID_HEADER;
   public static final int OUT_OF_BOUNDS;
   public static final int INTERRUPTED_DOWNLOAD;
   public static final int REQUEST_TIMED_OUT;
   public static final int UNSUPPORTED_TYPE;
   public static final int INSUFFICIENT_SPACE;
   public static final int CHECKSUM_MISMATCH;
   public static final int INVALID_SUB_PME;
   private static final int DEFAULT_ERROR_CODE;

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
