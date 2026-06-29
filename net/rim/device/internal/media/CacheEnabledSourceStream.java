package net.rim.device.internal.media;

import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.SourceStream;

public class CacheEnabledSourceStream implements SourceStream {
   private SourceStream _stream;
   private int _seekType;
   private byte[] _cache;
   private long _position;
   private long _streamPosition;
   private static final int USE_DEFAULT_VALUE = -1;
   private static final long MAX_CACHE_SIZE = 1048576L;

   public CacheEnabledSourceStream(SourceStream source) {
      this._stream = source;
      this._seekType = -1;
      if (this._stream.getSeekType() == 0 || this._stream.getSeekType() == 1) {
         long length = this._stream.getContentLength();
         if (length > 0 && length <= 1048576) {
            this._cache = new byte[(int)length];
            this._seekType = 2;
            this._position = 0;
            this._streamPosition = 0;
         }
      }
   }

   @Override
   public int read(byte[] b, int off, int len) {
      if (this._seekType == -1) {
         return this._stream.read(b, off, len);
      }

      int bytesRead = 0;
      if (this._position < this._streamPosition) {
         int toRead = (int)Math.min(this._streamPosition - this._position, len);
         System.arraycopy(this._cache, (int)this._position, b, off, toRead);
         bytesRead += toRead;
         this._position += toRead;
         off += toRead;
      }

      if (bytesRead < len) {
         int toRead = len - bytesRead;
         int num = this._stream.read(b, off, toRead);
         if (num == -1) {
            if (bytesRead == 0) {
               return num;
            }

            return bytesRead;
         }

         System.arraycopy(b, off, this._cache, (int)this._position, num);
         this._position += num;
         this._streamPosition += num;
         bytesRead += num;
      }

      return bytesRead;
   }

   @Override
   public long seek(long where) {
      if (this._seekType == -1) {
         return this._stream.seek(where);
      }

      if (where < 0) {
         where = 0;
      }

      if (where > this._stream.getContentLength()) {
         where = this._stream.getContentLength();
      }

      if (where <= this._streamPosition) {
         this._position = where;
      } else {
         int toRead = (int)(where - this._streamPosition);
         int num = this._stream.read(this._cache, (int)this._position, toRead);
         this._position += num;
         this._streamPosition += num;
      }

      return this._position;
   }

   @Override
   public ContentDescriptor getContentDescriptor() {
      return this._stream.getContentDescriptor();
   }

   @Override
   public long getContentLength() {
      return this._stream.getContentLength();
   }

   @Override
   public int getSeekType() {
      return this._seekType == -1 ? this._stream.getSeekType() : this._seekType;
   }

   @Override
   public int getTransferSize() {
      return this._stream.getTransferSize();
   }

   @Override
   public long tell() {
      return this._seekType == -1 ? this._stream.tell() : this._position;
   }

   @Override
   public Control getControl(String controlType) {
      return this._stream.getControl(controlType);
   }

   @Override
   public Control[] getControls() {
      return this._stream.getControls();
   }
}
