package net.rim.plazmic.mediaengine;

class MediaManager$Downloader extends Thread {
   private boolean _running;
   private String _requestedURL;
   private String _currentURL;
   private final MediaManager this$0;

   MediaManager$Downloader(MediaManager _1) {
      this.this$0 = _1;
      this._running = true;
   }

   private synchronized void loadMedia(String url) {
      this.this$0.cancel();
      this._requestedURL = url;
      this.notify();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (this._running) {
         synchronized (this) {
            label159:
            try {
               if (this._requestedURL == null) {
                  this.wait();
                  if (!this._running) {
                     return;
                  }
               }

               this._currentURL = this._requestedURL;
               this._requestedURL = null;
            } catch (Throwable var20) {
               this.this$0._platform.logDebug(this, 22, -1, e);
               break label159;
            }
         }

         if (this._currentURL != null) {
            boolean var9 = false /* VF: Semaphore variable */;

            label150: {
               try {
                  label148:
                  try {
                     var9 = true;
                     Object e = this.this$0.createMedia(this._currentURL);
                     if (this._requestedURL == null) {
                        this.this$0.fireMediaEvent(12, -1, e);
                        var9 = false;
                     } else {
                        var9 = false;
                     }
                     break label150;
                  } catch (Throwable var18) {
                     this.this$0._platform.logDebug(this, 22, -1, e);
                     if (this._requestedURL == null) {
                        this.this$0.fireMediaEvent(13, -1, this._currentURL);
                        var9 = false;
                     } else {
                        var9 = false;
                     }
                     break label148;
                  }
               } finally {
                  if (var9) {
                     this._currentURL = null;
                  }
               }

               this._currentURL = null;
               continue;
            }

            this._currentURL = null;
         }
      }
   }
}
