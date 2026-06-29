package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.calendar.caldb.CalendarFolder;
import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;

public final class ViewCalendarPopupScreen$CalendarFolderController {
   private CalendarKey _calendarKey;
   private String _calendarName;
   private boolean _isVisible;

   public ViewCalendarPopupScreen$CalendarFolderController(CalendarKey calendarKey) {
      this._calendarKey = calendarKey;
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(this._calendarKey.getCalendarServiceID());
      if (calendarService != null) {
         CalendarFolder calendarFolder = calendarService.getCalendarFolder(this._calendarKey.getCalendarFolderID());
         this._calendarName = ((StringBuffer)(new Object())).append(calendarService.getServiceName()).append(calendarFolder.getFolderNameSuffix()).toString();
         this._isVisible = CalendarOptions.getOptions().isShowAppointments(this._calendarKey);
      } else {
         this._calendarName = null;
      }
   }

   public final boolean isValid() {
      return this._calendarName != null;
   }

   public final CalendarKey getCalendarKey() {
      return this._calendarKey;
   }

   public final Field getField(RadioButtonGroup radioGroup) {
      HorizontalFieldManager line = (HorizontalFieldManager)(new Object());
      line.setTag(ViewCalendarPopupScreen.FOLDER_TEXT_TAG);
      RadioButtonField radioButton = new ViewCalendarPopupScreen$CalendarFolderController$1(this, this._calendarName, radioGroup, false);
      radioButton.setCookie(this);
      int color = CalendarOptions.getOptions().getCalendarColour(this._calendarKey);
      line.add(radioButton);
      line.add(new ViewCalendarPopupScreen$ColorSwatchField(radioButton.getPreferredHeight() - 2, color));
      return line;
   }

   public final boolean isVisible() {
      return this._isVisible;
   }

   public final void setVisible(boolean isVisible) {
      if (this.isValid() && this._isVisible != isVisible) {
         this._isVisible = isVisible;
         CalendarOptions.getOptions().setShowAppointments(this._calendarKey, isVisible);
      }
   }
}
