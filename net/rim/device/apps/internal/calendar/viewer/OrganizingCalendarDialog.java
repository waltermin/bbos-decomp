package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

final class OrganizingCalendarDialog extends Dialog {
   CalendarApp _calendarApplication;
   int _pushRequests = 0;
   private static ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");

   OrganizingCalendarDialog(CalendarApp calendarApplication) {
      super(_rb.getString(99), null, null, 0, null);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_hourglass"));
      this._calendarApplication = calendarApplication;
   }

   final synchronized void push() {
      this._pushRequests++;
      if (this._pushRequests == 1) {
         this._calendarApplication.pushScreen(this);
         this._calendarApplication.doPainting();
      }
   }

   final synchronized void pop(int amount) {
      if (amount > 0) {
         this._pushRequests -= amount;
      } else {
         this._pushRequests--;
      }

      if (this._pushRequests <= 0) {
         label31:
         try {
            if (this.getUiEngine() == this._calendarApplication.getActiveScreen().getUiEngine()) {
               this._calendarApplication.popScreen(this);
            }
         } finally {
            break label31;
         }

         this._pushRequests = 0;
      }
   }
}
