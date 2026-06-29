package net.rim.device.internal.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.Control;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public class DataSourceImpl extends DataSource implements SourceStream {
   String _locator;
   boolean _connected;
   boolean _started;
   InputStream _is;
   String _contentType;
   long _contentLength = -1;
   long _position;
   int _seekType = 0;
   public static final String MIME_AUDIO_MIDI = "audio/midi";
   public static final String MIME_AUDIO_X_MIDI = "audio/x-midi";
   public static final String MIME_AUDIO_MID = "audio/mid";
   public static final String MIME_AUDIO_X_NOKIA_RINGTONE = "audio/x-nokia-ringtone";
   public static final String MIME_AUDIO_MP3 = "audio/mpeg";
   public static final String MIME_AUDIO_WAV = "audio/x-wav";
   public static final String MIME_AUDIO_AMR = "audio/amr";
   public static final String MIME_AUDIO_RAW_PCM = "audio/basic";
   public static final String MIME_IMAGE_GIF = "image/gif";
   public static final String MIME_IMAGE_PNG = "image/png";
   public static final String MIME_MEDIA_PME = "application/x-vnd.rim.pme";
   public static final String MIME_MEDIA_PMB = "application/x-vnd.rim.pme.b";

   public DataSourceImpl(String locator) {
      super(null);
      this._locator = locator;
   }

   @Override
   public String getContentType() {
      if (this._connected) {
         return this._contentType;
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   @Override
   public void connect() throws IOException {
      if (!this._connected) {
         if (this._is != null) {
            this._connected = true;
         } else {
            throw new IOException("No InputStream provided. Use setInputStream()");
         }
      }
   }

   @Override
   public void disconnect() {
      try {
         this.stop();
      } catch (Exception var2) {
      }

      this._connected = false;
   }

   @Override
   public void start() throws IOException {
      if (this._connected) {
         if (!this._started) {
            if (this._is != null) {
               if (this._seekType == 2) {
                  this._is.mark(Integer.MAX_VALUE);
               }

               this._started = true;
            } else {
               throw new IOException("No InputStream set. Use setInputStream()");
            }
         }
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   @Override
   public void stop() {
      if (this._started && this._connected) {
         if (this._is != null) {
            try {
               this._is.close();
            } catch (IOException var2) {
            }
         }

         this._started = false;
      }
   }

   @Override
   public SourceStream[] getStreams() {
      return new SourceStream[]{this};
   }

   public InputStream getInputStream() {
      return this._connected && this._started ? this._is : null;
   }

   public void setInputStream(InputStream is) {
      this._is = is;
      if (this._is.markSupported()) {
         this._seekType = 2;
      }

      this._connected = true;
   }

   public void setContentType(String ct) {
      this._contentType = ct;
   }

   @Override
   public ContentDescriptor getContentDescriptor() {
      return new ContentDescriptor(this._contentType);
   }

   @Override
   public long getContentLength() {
      return this._contentLength;
   }

   @Override
   public int read(byte[] b, int off, int len) {
      int numBytes = this._is.read(b, off, len);
      if (numBytes != -1) {
         this._position += numBytes;
      }

      return numBytes;
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

      if (this._seekType == 0) {
         return this._position;
      }

      if (this._seekType == 1 && where != 0) {
         return this._position;
      }

      if (where > this._contentLength && this._contentLength > 0) {
         where = this._contentLength;
      }

      if (where < this._position) {
         this._is.reset();
         this._is.mark(Integer.MAX_VALUE);
         this._position = this._is.skip(where);
      } else {
         this._position = this._position + this._is.skip(where - this._position);
      }

      return this._position;
   }

   @Override
   public long tell() {
      return this._position;
   }

   @Override
   public int getSeekType() {
      return this._seekType;
   }

   @Override
   public Control[] getControls() {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }

   @Override
   public Control getControl(String controlType) {
      if (!this._connected) {
         throw new IllegalStateException();
      } else {
         return null;
      }
   }
}
