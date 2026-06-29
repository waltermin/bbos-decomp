package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.browser.field.BrowserContent;

class DocViewSoundDisplayField$1 extends Thread {
   private final BrowserContent val$content;
   private final DocViewSoundDisplayField this$0;

   DocViewSoundDisplayField$1(DocViewSoundDisplayField _1, BrowserContent _2) {
      this.this$0 = _1;
      this.val$content = _2;
   }

   @Override
   public void run() {
      try {
         this.val$content.finishLoading();
      } finally {
         return;
      }
   }
}
