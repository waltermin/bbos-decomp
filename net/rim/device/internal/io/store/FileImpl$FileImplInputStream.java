package net.rim.device.internal.io.store;

import java.io.InputStream;

class FileImpl$FileImplInputStream extends InputStream {
   private int _offset;
   private int _markOffset;
   private boolean _closed;
   private final FileImpl this$0;

   public FileImpl$FileImplInputStream(FileImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public void mark(int readlimit) {
      this._markOffset = this._offset;
   }

   @Override
   public void reset() {
      this._offset = this._markOffset;
   }

   @Override
   public int available() {
      return this.this$0._contentLength - this._offset;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public void close() {
      this._closed = true;
   }

   @Override
   public int read() {
      if (this._closed) {
         throw new Object("Stream closed");
      }

      synchronized (this.this$0._contentLock) {
         return this.this$0._content != null && this._offset < this.this$0._contentLength ? this.this$0._content[this._offset++] & 0xFF : -1;
      }
   }

   @Override
   public long skip(long nBytes) {
      synchronized (this.this$0._contentLock) {
         if (this._offset + nBytes >= this.this$0._contentLength) {
            long result = this.this$0._contentLength - this._offset;
            this._offset = this.this$0._contentLength;
            return result;
         } else {
            this._offset = (int)(this._offset + nBytes);
            return nBytes;
         }
      }
   }

   @Override
   public int read(byte[] b, int off, int len) {
      if (this._closed) {
         throw new Object("Stream closed");
      }

      if (off >= 0 && len >= 0 && off + len <= b.length) {
         synchronized (this.this$0._contentLock) {
            if (this.this$0._contentLength == this._offset) {
               return -1;
            }

            int remaining = this.this$0._contentLength - this._offset;
            if (len > remaining) {
               len = remaining;
            }

            System.arraycopy(this.this$0._content, this._offset, b, off, len);
            this._offset += len;
            return len;
         }
      } else {
         throw new Object();
      }
   }
}
