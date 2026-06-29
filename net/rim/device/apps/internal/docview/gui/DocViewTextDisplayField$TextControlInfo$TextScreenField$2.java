package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.component.ActiveRichTextField$RegionQueue;

class DocViewTextDisplayField$TextControlInfo$TextScreenField$2 implements Runnable {
   private final String val$text;
   private final ActiveRichTextField$RegionQueue val$rq;
   private final DocViewTextDisplayField$TextControlInfo$TextScreenField this$2;

   DocViewTextDisplayField$TextControlInfo$TextScreenField$2(
      DocViewTextDisplayField$TextControlInfo$TextScreenField _1, String _2, ActiveRichTextField$RegionQueue _3
   ) {
      this.this$2 = _1;
      this.val$text = _2;
      this.val$rq = _3;
   }

   @Override
   public void run() {
      this.this$2.super_setText(this.val$text, this.val$rq);
   }
}
