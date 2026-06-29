package net.rim.device.apps.internal.videorecorder;

import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.io.file.FileUtilities;

final class VideoRecorderScreen$1 implements Runnable {
   private final VideoRecorderScreen this$0;

   VideoRecorderScreen$1(VideoRecorderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      String filename = this.this$0._vrc.getVideoFileName(true);
      Verb renameVerb = ExplorerServices.getRenameVerb(FileUtilities.getDisplayName(filename), null);
      if (renameVerb != null) {
         Object obj = renameVerb.invoke(filename);
         if (obj instanceof Object) {
            String newname = (String)obj;
            this.this$0._vrc.setVideoFileName(newname);
            this.this$0.updateFileName(FileUtilities.getDisplayBaseName(newname).toCharArray());
         }
      }
   }
}
