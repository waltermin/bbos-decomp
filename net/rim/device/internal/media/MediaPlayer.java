package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;

public class MediaPlayer implements MediaEventListener {
   private Object _lock;
   private Object _myLock = new Object();
   private MediaStreamingManager$StreamingSession _streamingSession;
   private int _mediaHandle;
   private final String _mimeType;
   private boolean _mediaLoaded;
   private int _streams;
   private int _width;
   private int _height;
   private byte[] _screenBitmap;
   private Application _app;
   private MediaEventListener _listener;
   private static String MIME_TYPE_AMR = "audio/amr";
   public static final int MEDIA_STREAM_AUDIO;
   public static final int MEDIA_STREAM_VIDEO;
   public static final int MEDIA_SUCCESS;
   public static final int MEDIA_ERROR_BUSY;
   public static final int MEDIA_ERROR_PARAM;
   public static final int MEDIA_ERROR_MEMORY;
   public static final int MEDIA_ERROR_NEED_MORE_DATA;
   public static final int MEDIA_ERROR_UNSPECIFIED;
   public static final int MEDIA_ERROR_FORMAT;

   boolean isAlive() {
      return this._mediaHandle != -1;
   }

   boolean hasVideo() {
      return (this._streams & 2) != 0;
   }

   void play() {
      int status = 0;
      synchronized (this._lock) {
         if (this._mediaHandle == -1) {
            return;
         }

         status = MediaNatives.play0(this._mediaHandle);
      }

      this.checkStatus(status);
   }

   void pause() {
      int status = 0;
      synchronized (this._myLock) {
         if (this._mediaHandle == -1) {
            return;
         }

         status = MediaNatives.pause0(this._mediaHandle, this._screenBitmap);
         if ((this._streams & 2) != 0 && this._screenBitmap != null) {
            try {
               this._myLock.wait(2000);
            } catch (Throwable t) {
               System.out.println("Exception in pause wait");
            }
         }
      }

      this.checkStatus(status);
   }

   Bitmap getPauseBitmap() {
      synchronized (this._lock) {
         if (this._mediaHandle != -1 && this._screenBitmap != null) {
            Bitmap var10000;
            try {
               var10000 = new Bitmap(197, this._width, this._height).setRGB565(this._screenBitmap, 0, this._width << 1, 0, 0, this._width, this._height);
            } catch (Exception e) {
               return null;
            }

            return var10000;
         } else {
            return null;
         }
      }
   }

   void seek(int milliseconds) {
      int status = 0;
      synchronized (this._lock) {
         if (this._mediaHandle == -1) {
            return;
         }

         status = MediaNatives.seek0(this._mediaHandle, milliseconds);
      }

      this.checkStatus(status);
   }

   void seekComplete() {
      int status = 0;
      synchronized (this._lock) {
         if (this._mediaHandle == -1) {
            return;
         }

         this._streamingSession.flushBuffer();
         status = MediaNatives.seekComplete0(this._mediaHandle);
      }

      this.checkStatus(status);
   }

   void signifyNoMoreData() {
      int status = 0;
      synchronized (this._lock) {
         if (this._mediaHandle == -1) {
            return;
         }

         status = MediaNatives.signifyNoMoreData0(this._mediaHandle);
      }

      this.checkStatus(status);
   }

   void unload() {
      synchronized (this._lock) {
         if (this._streamingSession != null) {
            try {
               this._streamingSession.unregisterMediaPlayer(this);
            } catch (IllegalStateException var4) {
            }
         }

         this._streamingSession = null;
         this._listener = null;
         int handle = this._mediaHandle;
         if (handle != -1) {
            MediaNatives.unload0(handle, true);
            this._mediaHandle = -1;
            if (this._app != null) {
               MediaNatives.removeListener(this._app, this);
               this._app = null;
            }
         }
      }
   }

   int getLength() {
      return MediaNatives.getLength0(this._mediaHandle);
   }

   boolean isSeekable() {
      return MediaNatives.isSeekable0(this._mediaHandle);
   }

   int getPlayableStreams() {
      return MediaNatives.getPlayableStreams0(this._mediaHandle);
   }

   void resize(int width, int height) {
      this._width = width;
      this._height = height;
      Array.resize(this._screenBitmap, width * height << 1);
      MediaNatives.resize0(this._mediaHandle, width, height);
      this.parametersChanged();
   }

   void relocate(int x, int y) {
      MediaNatives.relocate0(this._mediaHandle, x, y);
      this.parametersChanged();
   }

   void resizeAndRelocate(int x, int y, int width, int height) {
      this._width = width;
      this._height = height;
      Array.resize(this._screenBitmap, width * height << 1);
      MediaNatives.resizeAndRelocate0(this._mediaHandle, x, y, width, height);
      this.parametersChanged();
   }

   int getContentWidth() {
      return MediaNatives.getContentWidth0(this._mediaHandle);
   }

   int getContentHeight() {
      return MediaNatives.getContentHeight0(this._mediaHandle);
   }

   void passCredentials(String credentials) {
      if (this._mediaHandle != -1) {
         MediaNatives.passCredentials0(this._mediaHandle, credentials);
      }
   }

   public void addListener(MediaEventListener listener) {
      synchronized (this._lock) {
         this._listener = listener;
      }
   }

   public void removeListener(MediaEventListener listener) {
      synchronized (this._lock) {
         if (this._listener == listener) {
            this._listener = null;
         }
      }
   }

   boolean isSinkSupported(int sink) {
      if (!this._mediaLoaded) {
         throw new IllegalStateException("Media must be loaded first");
      } else if (!this.isQualcommHardware()) {
         return true;
      } else {
         return !MIME_TYPE_AMR.equals(this._mimeType) ? true : sink != 5;
      }
   }

   @Override
   public void mediaStopped(int handle) {
      if (this._mediaLoaded && handle == this._mediaHandle) {
         this._mediaHandle = -1;
         if (this._app != null) {
            MediaNatives.removeListener(this._app, this);
            this._app = null;
         }

         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaStopped(handle);
         }
      }
   }

   @Override
   public void mediaPauseComplete(int handle) {
      if (handle == this._mediaHandle) {
         synchronized (this._myLock) {
            if (this._screenBitmap != null) {
               try {
                  MediaNatives.populatePauseBitmap0(this._screenBitmap);
               } catch (IllegalArgumentException iae) {
                  this._myLock.notifyAll();
                  return;
               }
            }

            this._myLock.notifyAll();
         }

         MediaEventListener listener = this._listener;
         if (listener != null) {
            this._listener.mediaPauseComplete(handle);
         }
      }
   }

   @Override
   public void mediaParametersChangedComplete(int handle) {
      if (handle == this._mediaHandle) {
         synchronized (this._myLock) {
            this._myLock.notifyAll();
         }
      }
   }

   @Override
   public void mediaSeek(int handle, int offset) {
      if (handle == this._mediaHandle) {
         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaSeek(handle, offset);
         }
      }
   }

   @Override
   public void mediaError(int handle, int errorCode) {
      if (handle == this._mediaHandle) {
         synchronized (this._myLock) {
            this._myLock.notifyAll();
         }

         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaError(handle, errorCode);
         }
      }
   }

   @Override
   public void mediaLoaded(int handle) {
      if (handle == this._mediaHandle) {
         this._mediaLoaded = true;
         int streams = this.getPlayableStreams();
         if (streams != -1 && (streams & 2) == 0) {
            this._streams = streams;
            this._screenBitmap = null;
         }

         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaLoaded(handle);
         }
      }
   }

   @Override
   public void mediaStatusUpdate(int handle, int milliseconds) {
      if (handle == this._mediaHandle) {
         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaStatusUpdate(handle, milliseconds);
         }
      }
   }

   @Override
   public void mediaAuthenticationRequired(int handle, Object uri, Object header) {
      if (handle == this._mediaHandle) {
         MediaEventListener listener = this._listener;
         if (listener != null) {
            listener.mediaAuthenticationRequired(handle, uri, header);
         }
      }
   }

   private void initializePlayer(MediaStreamingManager$StreamingSession streamingSession, int mediaHandle, int streams, int width, int height) {
      this._streamingSession = streamingSession;
      this._mediaHandle = mediaHandle;
      this._streams = streams;
      this._width = width;
      this._height = height;
      streamingSession.registerMediaPlayer(this);
   }

   private MediaPlayer(Object lock, String mimeType) {
      this._lock = lock;
      this._mimeType = mimeType;
      this._mediaLoaded = false;
   }

   private boolean isQualcommHardware() {
      return RadioInfo.getNetworkType() == 4;
   }

   private void checkStatus(int status) {
      if (status != 0) {
         MediaEventListener listener = this._listener;
         int handle = this._mediaHandle;
         if (listener != null && handle != -1) {
            listener.mediaError(handle, status);
         }
      }
   }

   static MediaPlayer initialize(
      MediaEventListener listener,
      int streams,
      MediaStreamingManager$StreamingSession streamingSession,
      int x,
      int y,
      int width,
      int height,
      int contentLength,
      boolean seekable,
      int interruptible,
      int fileHandle,
      String url,
      String userAgent,
      int apnId,
      String mimeType
   ) {
      if (x >= 0 && y >= 0 && width >= 0 && height >= 0) {
         MediaPlayer player = new MediaPlayer(streamingSession, mimeType);
         player.addListener(listener);
         if (player._app != null) {
            MediaNatives.removeListener(player._app, player);
         }

         player._app = Proxy.getInstance();
         MediaNatives.addListener(player._app, player);
         int streamingHandle = streamingSession.getHandle();
         int audioChannel = streamingSession.getChannel();
         long result = -1;
         if (fileHandle >= 0) {
            result = MediaNatives.initFile0(streams, audioChannel, fileHandle, x, y, width, height, contentLength, interruptible);
         } else if (url != null) {
            result = MediaNatives.initURL0(url, userAgent, apnId, streams, audioChannel, x, y, width, height, contentLength, interruptible);
         } else {
            result = MediaNatives.init0(streams, streamingHandle, audioChannel, x, y, width, height, contentLength, seekable, interruptible);
         }

         int status = (int)result;

         try {
            if (status == 0) {
               int mediaHandle = (int)(result >> 32);
               player.initializePlayer(streamingSession, mediaHandle, streams, width, height);
               if ((streams & 2) != 0) {
                  player._screenBitmap = new byte[width * height << 1];
               }

               return player;
            }
         } catch (IllegalStateException var22) {
         }

         if (player._app != null) {
            MediaNatives.removeListener(player._app, player);
         }

         player._app = null;
         player._listener = null;
         MediaPlayer var23 = null;
         return null;
      } else {
         return null;
      }
   }

   private void parametersChanged() {
      synchronized (this._myLock) {
         MediaNatives.parametersChanged0(this._mediaHandle);

         try {
            this._myLock.wait(1000);
         } catch (Exception var4) {
         }
      }
   }
}
