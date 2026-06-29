package net.rim.device.apps.internal.browser.plugin.media.field;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.internal.media.HTTPBufferingManager;

final class MediaBrowserField$PlaylistThread extends Thread {
   private boolean _runToNextItem;
   private final MediaBrowserField this$0;

   private MediaBrowserField$PlaylistThread(MediaBrowserField _1) {
      this.this$0 = _1;
   }

   public final synchronized void nextItem() {
      this._runToNextItem = true;
      this.notify();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (!this.this$0._closed) {
         boolean skipPlay = false;
         if (!this.this$0._repeat && this.this$0._playlistCurrentIndex >= this.this$0._playlistOrder.length) {
            synchronized (this) {
               this._runToNextItem = false;
            }
         } else {
            label541:
            try {
               if (this.this$0._repeat && this.this$0._playlistCurrentIndex >= this.this$0._playlistOrder.length) {
                  if (this.this$0._shuffled) {
                     this.this$0._playlistCurrentIndex = -1;
                     this.this$0.setShuffledPlaylistOrder();
                  }

                  this.this$0._playlistCurrentIndex = 0;
               }

               int playIndex = this.this$0._playlistOrder[this.this$0._playlistCurrentIndex];
               ((PlaylistField)this.this$0._playlistField).setCurrentItem(playIndex);
               MediaBrowserField.access$2408(this.this$0);
               synchronized (this) {
                  this._runToNextItem = false;
               }

               String url = this.this$0._playlist.getUrl(playIndex);
               if (url == null) {
                  continue;
               }

               url = this.this$0._browserContent.resolveUrl(url);
               InputConnection inputConnection = this.this$0.requestResource(url);
               if (inputConnection == null) {
                  continue;
               }

               boolean fileBased = false;
               if (!(inputConnection instanceof HttpConnection)) {
                  if (inputConnection instanceof FileConnection) {
                     fileBased = true;
                     if (!((FileConnection)inputConnection).exists()) {
                        if (!this.this$0._trackForward && this.this$0._playlistCurrentIndex != 1) {
                           MediaBrowserField.access$2420(this.this$0, 2);
                        } else {
                           this.this$0._trackForward = true;
                        }

                        inputConnection.close();
                        continue;
                     }
                  }
               } else {
                  HttpConnection conn = (HttpConnection)inputConnection;
                  if (conn.getResponseCode() != 200) {
                     conn.close();
                     continue;
                  }
               }

               String contentType = RendererControl.getContentType(inputConnection);
               int mediaType = MIMETypeAssociations.getMediaTypeFromMIMEType(contentType);
               if (this.this$0._closed
                  || contentType.equals("audio/x-mpegurl")
                  || contentType.equals("audio/x-scpls")
                  || mediaType != 2 && mediaType != 7 && mediaType != 3) {
                  inputConnection.close();
                  continue;
               }

               InputStream in = null;

               try {
                  if (!this.this$0._visible && contentType.startsWith("video")) {
                     synchronized (this) {
                        this._runToNextItem = true;
                     }

                     skipPlay = true;
                  } else {
                     in = RendererControl.getInputStreamFromContentEncoding(inputConnection, inputConnection.openInputStream());
                     HTTPBufferingManager bufferManager = null;
                     if (inputConnection instanceof HttpConnection) {
                        bufferManager = new HTTPBufferingManager((HttpConnection)inputConnection, in, this.this$0);
                     }

                     this.this$0
                        .initDataSource(
                           inputConnection,
                           in,
                           RendererControl.getContentLength(inputConnection),
                           contentType,
                           fileBased,
                           RendererControl.getUrl(inputConnection),
                           bufferManager
                        );
                     this.this$0.initContent();
                  }
               } catch (Throwable var51) {
                  label560: {
                     if (in != null) {
                        label502:
                        try {
                           in.close();
                        } finally {
                           break label502;
                        }
                     }

                     if (this.this$0._singleFilePlayback) {
                        Application.getApplication().invokeAndWait(new MediaBrowserField$PlaylistThread$1(this));
                     }

                     QuincyUtil.sendQuincy(t, false);
                     if (!this.this$0._singleFilePlayback) {
                        continue;
                     }

                     skipPlay = true;
                     this._runToNextItem = false;
                     break label560;
                  }
               }

               this.this$0._activeResourceRequest = null;
               if (!skipPlay) {
                  this.this$0.play(false);
               }
            } finally {
               break label541;
            }
         }

         synchronized (this) {
            if (!this._runToNextItem) {
               try {
                  this.wait();
               } finally {
                  continue;
               }
            }
         }
      }
   }

   MediaBrowserField$PlaylistThread(MediaBrowserField x0, MediaBrowserField$1 x1) {
      this(x0);
   }
}
