package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.internal.i18n.CommonResource;

final class CalendarOptionsVerb extends CalendarViewerVerb {
   public CalendarOptionsVerb() {
      super(CommonResource.getBundle(), 20, 16986368, 0);
   }

   @Override
   public final Object invoke(Object parameter) {
      CalendarOptionsEntryScreen.showEditOptionsScreen();
      return null;
   }
}
