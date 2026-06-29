package net.rim.device.apps.internal.explorer.file;

final class FileExplorerApp$1 implements Runnable {
   private final FileExplorerApp this$0;

   FileExplorerApp$1(FileExplorerApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._mediaDialog != null) {
         this.this$0.pushScreen(this.this$0._mediaDialog);
      }
   }
}
