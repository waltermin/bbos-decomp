package net.rim.device.apps.internal.explorer.file;

final class MediaFolderListener$1 implements Runnable {
   private final MediaFolderListener this$0;

   MediaFolderListener$1(MediaFolderListener _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      ExploreManager manager = this.this$0.getManager();
      if (manager != null) {
         manager.refreshView();
      }
   }
}
