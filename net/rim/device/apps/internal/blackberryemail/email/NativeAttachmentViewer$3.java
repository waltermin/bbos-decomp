package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;

class NativeAttachmentViewer$3 implements Runnable {
   private final BrowserContent val$content;
   private final Field val$contentField;
   private final NativeAttachmentViewer this$0;

   NativeAttachmentViewer$3(NativeAttachmentViewer _1, BrowserContent _2, Field _3) {
      this.this$0 = _1;
      this.val$content = _2;
      this.val$contentField = _3;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         this.val$content.finishLoading();
      } catch (Throwable var3) {
         this.this$0.logError(error);
         Application.getApplication().invokeLater(new NativeAttachmentViewer$3$1(this));
         return;
      }
   }
}
