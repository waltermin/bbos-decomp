package net.rim.device.apps.internal.ribbon.skin.svg;

class MediaLayout$2 implements Runnable {
   private final String val$localUrl;
   private final int val$localFocusId;
   private final boolean val$localPushHistory;
   private final MediaLayout this$0;

   MediaLayout$2(MediaLayout _1, String _2, int _3, boolean _4) {
      this.this$0 = _1;
      this.val$localUrl = _2;
      this.val$localFocusId = _3;
      this.val$localPushHistory = _4;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         Object e = this.this$0._mediaLoader.createMedia(this.val$localUrl);
         this.this$0._player.setMedia(e);
         if (this.val$localFocusId != -1) {
            this.this$0.focusHotspot(this.val$localFocusId);
         }

         if (this.val$localPushHistory) {
            this.this$0.pushHistory(this.val$localUrl);
            return;
         }

         var3 = false;
      } finally {
         if (var3) {
            System.err.println(((StringBuffer)(new Object("Exception thrown while loading "))).append(this.val$localUrl).toString());
            return;
         }
      }
   }
}
