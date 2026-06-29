package net.rim.device.api.ui;

import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;

class MediaController$1 extends MenuItem {
   private final MediaController this$0;

   MediaController$1(MediaController _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      FocusInteractor cia = ((MediaField)this.this$0.getManager())._focusInteractor;
      if (cia != null) {
         cia.setFocusToItem(cia.getItemInFocus());
         cia.activateItemInFocus();
      }
   }
}
