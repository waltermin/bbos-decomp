package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.internal.i18n.CommonResource;

final class CloseCalendarVerb extends CalendarViewerVerb {
   private CalendarActions _calActions;

   public CloseCalendarVerb(CalendarActions calActions) {
      super(CommonResource.getBundle(), 9, 268501008, 0);
      this._calActions = calActions;
   }

   @Override
   public final Object invoke(Object parameter) {
      this._calActions.close();
      return null;
   }
}
