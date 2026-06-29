package net.rim.device.api.ui;

import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;

class MediaField$MFFocusChangeListener implements FocusChangeListener {
   private final MediaField this$0;

   MediaField$MFFocusChangeListener(MediaField _1) {
      this.this$0 = _1;
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (eventType == 2 && field == this.this$0) {
         FocusInteractor cia = this.this$0._focusInteractor;
         if (cia != null) {
            Field focus = this.this$0.getFieldWithFocus();
            if (this.this$0.isForeignObject(focus)) {
               return;
            }

            if (focus != this.this$0._controller && cia.hasFocus()) {
               cia.setFocusToItem(-1);
            }
         }
      }
   }
}
