package net.rim.device.apps.internal.browser.plugin.media.util;

import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public final class MediaDataSource extends DataSource implements SourceStream {
   private String _contentType;
   private long _contentLength;
   private long _pos;
   private InputStream _inputStream;
   private boolean _connected;

   public final void close() {
      try {
         this._inputStream.close();
      } finally {
         return;
      }
   }

   @Override
   public final long getContentLength() {
      return this._contentLength;
   }

   @Override
   public final int read(byte[] b, int off, int len) {
      int numRead = this._inputStream.read(b, off, len);
      if (numRead > 0) {
         this._pos += numRead;
      }

      return numRead;
   }

   @Override
   public final int getTransferSize() {
      return -1;
   }

   @Override
   public final long seek(long where) {
      if (this._inputStream.markSupported()) {
         if (where >= this._pos) {
            this._pos = this._pos + this._inputStream.skip(where - this._pos);
         } else {
            this._inputStream.reset();
            this._inputStream.mark(Integer.MAX_VALUE);
            this._pos = this._inputStream.skip(where);
         }
      }

      return this._pos;
   }

   @Override
   public final long tell() {
      return this._pos;
   }

   @Override
   public final int getSeekType() {
      return this._inputStream.markSupported() ? 2 : 0;
   }

   @Override
   public final ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor(this._contentType);
   }

   @Override
   public final String getContentType() {
      return this._contentType;
   }

   @Override
   public final void connect() {
      this._connected = true;
   }

   @Override
   public final void disconnect() {
      this._connected = false;
   }

   @Override
   public final void start() {
   }

   @Override
   public final void stop() {
      if (this._inputStream.markSupported()) {
         this._inputStream.reset();
         this._inputStream.mark(Integer.MAX_VALUE);
         this._pos = 0;
      }
   }

   @Override
   public final SourceStream[] getStreams() {
      return new SourceStream[]{this};
   }

   @Override
   public final Control[] getControls() {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public final Control getControl(String controlType) {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   public MediaDataSource(InputStream in, long contentLength, String contentType) {
      super("");
      this._contentType = contentType;
      this._contentLength = contentLength;
      this._inputStream = in;
   }
}
