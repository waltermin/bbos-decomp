package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.ui.SelfDrawingListField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneInfoOption$CallTimerList extends SelfDrawingListField {
   private int[] _timers;
   private StringBuffer _buf = (StringBuffer)(new Object());

   public PhoneInfoOption$CallTimerList(int[] timers) {
      super(timers.length, 0);
      this._timers = timers;
   }

   public final int getSelectedCallTimer() {
      return this._timers[this.getSelectedIndex()];
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int labelResource = -1;
      switch (this._timers[index]) {
         case -1:
            break;
         case 0:
         default:
            labelResource = 164;
            break;
         case 1:
            labelResource = 166;
            break;
         case 2:
            labelResource = 312;
            break;
         case 3:
            labelResource = 208;
      }

      this._buf.setLength(0);
      if (labelResource >= 0) {
         this._buf.append(PhoneResources.getString(labelResource));
      }

      int x = 0;
      int labelLength = this._buf.length();
      DateTimeUtilities.formatElapsedTime(CallTimers.getCallTimers().getTimer(this._timers[index]), this._buf, false);
      int timerWidth = graphics.drawText(this._buf, labelLength, this._buf.length() - labelLength, x, y, 5, width);
      graphics.drawText(this._buf, 0, labelLength, x, y, 64, width - timerWidth - 3);
   }
}
