package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;

final class ViewCalendarPopupScreen$CalendarFolderController$1 extends RadioButtonField {
   private final ViewCalendarPopupScreen$CalendarFolderController this$0;

   ViewCalendarPopupScreen$CalendarFolderController$1(ViewCalendarPopupScreen$CalendarFolderController _1, String x0, RadioButtonGroup x1, boolean x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   protected final void layout(int width, int height) {
      super.layout(width - this.getFont().getHeight(), height);
   }
}
