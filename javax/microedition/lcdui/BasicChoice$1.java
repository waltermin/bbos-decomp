package javax.microedition.lcdui;

import net.rim.device.api.ui.container.VerticalFieldManager;

class BasicChoice$1 extends VerticalFieldManager {
   private final BasicChoice this$0;

   BasicChoice$1(BasicChoice _1, long x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      this.this$0._onScreen = true;
      if (this.this$0._currentlySelectedIndex >= 0 && (this.this$0._type == 3 || this.this$0._type == 4) && this.this$0.size() > 0) {
         this.this$0.setSelectedIndex(this.this$0._currentlySelectedIndex, true);
      }
   }

   @Override
   protected void onUndisplay() {
      super.onUndisplay();
      this.this$0._onScreen = false;
      if (this.this$0._type != 4) {
         this.this$0._currentlySelectedIndex = this.getFieldWithFocusIndex();
      } else {
         this.this$0._currentlySelectedIndex = this.this$0.getSelectedIndex();
      }
   }
}
