package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.component.Dialog;

class DocViewImageDisplayField$2 implements Runnable {
   private final DocViewImageDisplayField this$0;

   DocViewImageDisplayField$2(DocViewImageDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Dialog.alert(DocViewDisplayField._resources.getString(80));
   }
}
