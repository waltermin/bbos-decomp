package net.rim.plazmic.mediaengine.io;

public class LoadingStatus {
   private String _source;
   private long _readBytes;
   private long _totalBytes;
   private String _contentType;
   private int _status;
   private int _code = 0;
   protected String _message;
   public static final int LOADING_STARTED = 1;
   public static final int LOADING_READING = 2;
   public static final int LOADING_FINISHED = 3;
   public static final int LOADING_FAILED = 4;
   public static final int LOADING_INSUFFICIENT_SPACE = 5;
   private static final int LOADING_UNKNOWN = 0;

   public final long getReadBytes() {
      return this._readBytes;
   }

   public final long getTotalBytes() {
      return this._totalBytes;
   }

   public final String getContentType() {
      return this._contentType;
   }

   public final String getSource() {
      return this._source;
   }

   public final int getStatus() {
      return this._status;
   }

   public final int getCode() {
      return this._code;
   }

   public final String getMessage() {
      return this._message;
   }

   public final void setStatus(int status) {
      this._status = status;
   }

   public final void setReadBytes(long size) {
      this._readBytes = size;
   }

   public final void setContentType(String type) {
      this._contentType = type;
   }

   public final void setCode(int code) {
      this._code = code;
   }

   public final void setMessage(String msg) {
      this._message = msg;
   }

   public final void setTotalBytes(long size) {
      this._totalBytes = size;
   }

   public final void init(String source, String type, int status) {
      this._source = source;
      this._contentType = type;
      this._status = status;
      this._readBytes = 0;
      this._totalBytes = 0;
      this._code = 0;
      this._message = null;
   }

   public final void clear() {
      this._source = null;
      this._status = 0;
      this._readBytes = 0;
      this._totalBytes = 0;
      this._contentType = null;
      this._code = 0;
      this._message = null;
   }
}
