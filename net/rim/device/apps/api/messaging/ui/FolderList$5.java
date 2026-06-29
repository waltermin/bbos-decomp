package net.rim.device.apps.api.messaging.ui;

class FolderList$5 implements Runnable {
   private final long val$folderLUID;
   private final FolderList this$0;

   FolderList$5(FolderList _1, long _2) {
      this.this$0 = _1;
      this.val$folderLUID = _2;
   }

   @Override
   public void run() {
      this.this$0.handleRemoveFolder(this.val$folderLUID);
   }
}
