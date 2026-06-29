package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.container.FullScreen;

class TraceBackDialog$TraceBackScreen extends FullScreen {
   private final TraceBackDialog this$0;

   TraceBackDialog$TraceBackScreen(TraceBackDialog _1) {
      super(281474976710656L);
      this.this$0 = _1;
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this.this$0._app.popScreen(this);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }
}
