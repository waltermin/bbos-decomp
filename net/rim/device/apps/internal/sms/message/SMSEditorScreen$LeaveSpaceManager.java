package net.rim.device.apps.internal.sms.message;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.container.VerticalFieldManager;

class SMSEditorScreen$LeaveSpaceManager extends VerticalFieldManager {
   public SMSEditorScreen$LeaveSpaceManager() {
   }

   @Override
   protected void sublayout(int width, int height) {
      StringBuffer buf = new StringBuffer();
      Font currentFont = this.getFont();
      int widestValue = 0;

      for (char i = '0'; i < ':'; i++) {
         buf.setLength(0);
         buf.append(i);
         int charwidth = currentFont.getAdvance(buf, 0, buf.length());
         if (charwidth > widestValue) {
            widestValue = charwidth;
         }
      }

      int spaceToLeave = widestValue * 4 + currentFont.getAdvance(buf, 0, buf.length());
      super.sublayout(width - spaceToLeave, height);
      this.setExtent(width - spaceToLeave, this.getHeight());
   }
}
