package net.rim.device.cldc.io.devicehttp;

import java.io.InputStream;

public final class ChunkedInputStream extends InputStream {
   private LengthControlledInputStream _currentChunk;
   private InputStream _in;
   private boolean _closed;
   private boolean _closeUnderlying;
   private boolean _eof;

   public ChunkedInputStream(InputStream underlying, boolean closeUnderlying) {
      this._in = underlying;
      this._closeUnderlying = closeUnderlying;
   }

   @Override
   public final synchronized int read() {
      if (this._closed) {
         throw new Object();
      } else {
         return (this._currentChunk == null || this._currentChunk.getLength() <= 0) && !this.readNextChunk() ? -1 : this._currentChunk.read();
      }
   }

   @Override
   public final synchronized int read(byte[] b, int offset, int length) {
      if (this._closed) {
         throw new Object();
      }

      int bytesRead = 0;

      while (length > 0) {
         if ((this._currentChunk == null || this._currentChunk.getLength() <= 0) && !this.readNextChunk()) {
            if (bytesRead > 0) {
               return bytesRead;
            }

            return -1;
         }

         int numRead = this._currentChunk.read(b, offset, length);
         if (numRead < 0) {
            return numRead;
         }

         offset += numRead;
         length -= numRead;
         bytesRead += numRead;
      }

      return bytesRead > 0 ? bytesRead : -1;
   }

   private final boolean readNextChunk() {
      if (this._eof) {
         return false;
      }

      int byteRead = this._in.read();
      int currentChunkLength = 0;
      boolean readValue = false;

      while (byteRead != -1) {
         if (byteRead >= 48 && byteRead <= 57) {
            currentChunkLength = currentChunkLength * 16 + byteRead - 48;
            readValue = true;
         } else if (byteRead >= 65 && byteRead <= 70) {
            currentChunkLength = currentChunkLength * 16 + byteRead - 55;
            readValue = true;
         } else if (byteRead >= 97 && byteRead <= 102) {
            currentChunkLength = currentChunkLength * 16 + byteRead - 87;
            readValue = true;
         } else if (readValue || byteRead != 13 && byteRead != 10) {
            break;
         }

         byteRead = this._in.read();
      }

      this.skipToNextLine(byteRead);
      if (currentChunkLength != 0) {
         this._currentChunk = new LengthControlledInputStream(this._in, currentChunkLength, false, false);
         return true;
      }

      while (this.skipToNextLine(this._in.read())) {
      }

      this._eof = true;
      return false;
   }

   public final boolean eof() {
      return this._eof;
   }

   @Override
   public final int available() {
      return this._in.available();
   }

   private final boolean skipToNextLine(int byteRead) {
      boolean lineHasContent = false;
      boolean crRead = false;

      while (byteRead >= 0) {
         if (byteRead == 13) {
            crRead = true;
         } else {
            if (byteRead == 10 && crRead) {
               return lineHasContent;
            }

            lineHasContent = true;
         }

         byteRead = this._in.read();
      }

      return lineHasContent;
   }

   @Override
   public final void close() {
      this._closed = true;
      if (this._closeUnderlying) {
         this._in.close();
      }
   }
}
