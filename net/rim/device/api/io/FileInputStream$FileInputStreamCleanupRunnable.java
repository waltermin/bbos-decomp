package net.rim.device.api.io;

import net.rim.device.internal.io.file.FileSystem;

final class FileInputStream$FileInputStreamCleanupRunnable implements Runnable {
   private final FileInputStream this$0;

   FileInputStream$FileInputStreamCleanupRunnable(FileInputStream _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._handle != -1) {
         FileSystem.close(this.this$0._handle);
         this.this$0._handle = -1;
      }
   }
}
