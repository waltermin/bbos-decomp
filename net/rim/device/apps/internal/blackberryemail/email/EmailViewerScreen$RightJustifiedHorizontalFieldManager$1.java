package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;

class EmailViewerScreen$RightJustifiedHorizontalFieldManager$1 extends VerticalFieldManager {
   private final EmailViewerScreen$RightJustifiedHorizontalFieldManager this$0;

   EmailViewerScreen$RightJustifiedHorizontalFieldManager$1(EmailViewerScreen$RightJustifiedHorizontalFieldManager _1) {
      this.this$0 = _1;
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this.this$0._rightJustifiedField != null) {
         width -= this.this$0._rightJustifiedField.getPreferredWidth();
      }

      super.sublayout(width, height);
      if (this.this$0._rightJustifiedField != null) {
         XYRect myRect = this.getContentRect();
         int minHeight = this.this$0._rightJustifiedField.getPreferredHeight();
         if (myRect.height < minHeight) {
            XYRect footerRect = this.this$0._footerManager.getExtent();
            this.setPositionChild(this.this$0._footerManager, footerRect.x, minHeight - footerRect.height);
            this.setExtent(myRect.width, minHeight);
         }
      }
   }
}
