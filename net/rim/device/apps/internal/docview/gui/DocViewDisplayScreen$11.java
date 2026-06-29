package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class DocViewDisplayScreen$11 implements Runnable {
   private final Screen val$myScreen;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$11(DocViewDisplayScreen _1, Screen _2) {
      this.this$0 = _1;
      this.val$myScreen = _2;
   }

   @Override
   public void run() {
      if (DocViewDisplayScreen.access$700(this.this$0).isForeground() && this.val$myScreen.isDisplayed()
         || this.this$0._displayField != null && this.this$0._displayField.getScreen() != null && this.this$0._displayField.getScreen().isDisplayed()) {
         Status.show(EmailResources.getString(1003), Bitmap.getPredefinedBitmap(0), 1000);
      }
   }
}
