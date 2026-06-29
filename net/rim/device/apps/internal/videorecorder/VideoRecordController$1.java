package net.rim.device.apps.internal.videorecorder;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.io.file.FileHandleProvider;
import net.rim.device.internal.io.file.FileUtilities;

final class VideoRecordController$1 extends Thread {
   private final VideoRecordController this$0;

   VideoRecordController$1(VideoRecordController _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      label27:
      try {
         var3 = true;
         this.this$0._tempFile.rename(((StringBuffer)(new Object())).append(FileUtilities.getName(this.this$0._filename)).append(".lock").toString());
         this.this$0._tempIS = this.this$0._tempFile.openInputStream();
         this.this$0._finalFile = (FileConnection)Connector.open(this.this$0._filename);
         this.this$0._finalFile.create();
         this.this$0._tempOS = this.this$0._finalFile.openOutputStream();
         var3 = false;
      } finally {
         if (var3) {
            System.out.println("Video final file failed");
            break label27;
         }
      }

      if (this.this$0._tempIS instanceof Object && this.this$0._tempOS instanceof Object) {
         Camera.transcodeVideoFile(((FileHandleProvider)this.this$0._tempIS).getFileHandle(), ((FileHandleProvider)this.this$0._tempOS).getFileHandle());
      }
   }
}
