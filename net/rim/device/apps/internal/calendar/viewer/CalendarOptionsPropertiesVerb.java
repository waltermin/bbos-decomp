package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.apps.api.calendar.caldb.CalendarKey;
import net.rim.device.internal.i18n.CommonResource;

final class CalendarOptionsPropertiesVerb extends CalendarViewerVerb {
   private CalendarKey _calendarKey;
   private CalendarOptionsPropertiesScreen$ChangeListener _changeListener;

   public CalendarOptionsPropertiesVerb(CalendarKey key, CalendarOptionsPropertiesScreen$ChangeListener changeListener) {
      super(CommonResource.getBundle(), 20, 16986368, 0);
      this._calendarKey = key;
      this._changeListener = changeListener;
   }

   @Override
   public final Object invoke(Object parameter) {
      CalendarOptionsPropertiesScreen.showEditPropertiesScreen(this._calendarKey, this._changeListener);
      return null;
   }
}
