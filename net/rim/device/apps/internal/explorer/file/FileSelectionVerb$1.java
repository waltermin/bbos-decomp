package net.rim.device.apps.internal.explorer.file;

final class FileSelectionVerb$1 implements Runnable {
   private final FileSelectionVerb this$0;

   FileSelectionVerb$1(FileSelectionVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._popupExplorer.close();
   }
}
