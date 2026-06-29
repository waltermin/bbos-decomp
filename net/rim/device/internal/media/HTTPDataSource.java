package net.rim.device.internal.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;
import net.rim.device.api.system.Application;

public class HTTPDataSource extends DataSource implements SourceStream {
   private String _contentType;
   private long _pos;
   private InputStream _inputStream;
   private boolean _started;
   private HTTPBufferingManager _buffer;
   private HTTPBufferingCallback _callback;
   private String _contentUrl;
   private long _estimatedTime = -1;

   public void setEstimatedTime(long time) {
      this._estimatedTime = time;
      if (this._buffer != null) {
         this._buffer.setEstimatedTime(time);
      }
   }

   public HTTPBufferingManager getBufferingManager() {
      return this._buffer;
   }

   @Override
   public long getContentLength() {
      return this._buffer != null ? this._buffer.getStreamLength() : -1;
   }

   @Override
   public int read(byte[] b, int off, int len) {
      int numRead = this._inputStream.read(b, off, len);
      if (numRead > 0) {
         this._pos += numRead;
      }

      return numRead;
   }

   @Override
   public int getTransferSize() {
      return -1;
   }

   @Override
   public long seek(long where) {
      if (where < 0) {
         where = 0;
      }

      if (this._buffer != null && this._buffer.bufferWillContainAllContent()) {
         if (where >= this._pos) {
            this._pos = this._pos + this._inputStream.skip(where - this._pos);
         } else {
            this._inputStream.reset();
            this._inputStream.mark(Integer.MAX_VALUE);
            this._pos = this._inputStream.skip(where);
         }

         return this._pos;
      } else {
         if (where != 0) {
            return this._pos;
         }

         if (Application.isEventDispatchThread()) {
            throw new IOException("Seek called on event dispatch thread");
         }

         this.disconnect();

         try {
            this.connect();
            this.start();
         } catch (IOException ioe) {
            this.disconnect();
            throw ioe;
         }

         return this._pos;
      }
   }

   @Override
   public long tell() {
      return this._pos;
   }

   @Override
   public int getSeekType() {
      return this._buffer != null && this._buffer.bufferContainsAllContent() ? 2 : 1;
   }

   @Override
   public ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor(this._contentType);
   }

   @Override
   public void connect() {
      if (this._buffer == null) {
         if (Application.isEventDispatchThread()) {
            throw new IOException("Connect called on event dispatch thread");
         }

         InputConnection input = this._callback.requestResource(this._contentUrl);
         if (input == null) {
            throw new IOException();
         }

         if (!(input instanceof HttpConnection)) {
            try {
               input.close();
            } catch (IOException var4) {
            }

            throw new IOException("Not a HTTP Connection");
         } else {
            HttpConnection httpInput = (HttpConnection)input;
            if (httpInput.getResponseCode() != 200) {
               try {
                  input.close();
               } catch (IOException var5) {
               }

               throw new IOException("Bad response");
            } else {
               this._buffer = new HTTPBufferingManager(httpInput, httpInput.openInputStream(), this._callback);
               if (this._estimatedTime != -1) {
                  this._buffer.setEstimatedTime(this._estimatedTime);
               }
            }
         }
      }
   }

   public HTTPDataSource(HTTPBufferingCallback callback, HTTPBufferingManager buffer, String contentUrl, String contentType) {
      super("");
      this._contentUrl = contentUrl;
      this._contentType = contentType;
      this._buffer = buffer;
      this._callback = callback;
   }

   @Override
   public void disconnect() {
      if (this._buffer != null) {
         if (this._started) {
            try {
               this.stop();
            } catch (IOException var2) {
            }
         }

         this._pos = 0;
         this._buffer.shutdown();
         this._buffer = null;
      }
   }

   @Override
   public void start() {
      if (this._buffer != null) {
         if (!this._started) {
            this._buffer.start();
            this._inputStream = this._buffer.getInputStream();
         }
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   @Override
   public void stop() {
      if (this._started && this._buffer != null) {
         if (this._inputStream != null) {
            try {
               this._inputStream.close();
            } catch (IOException var2) {
            }
         }

         this._inputStream = null;
         this._started = false;
      }
   }

   @Override
   public SourceStream[] getStreams() {
      return new SourceStream[]{this};
   }

   @Override
   public Control[] getControls() {
      if (this._buffer == null) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public Control getControl(String controlType) {
      if (this._buffer == null) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public String getContentType() {
      return this._contentType;
   }
}
