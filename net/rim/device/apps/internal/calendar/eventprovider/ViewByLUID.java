package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.verb.Verb;

public class ViewByLUID extends Verb {
   private static long GUID = -2616944461649776979L;

   public static void register() {
      ViewByLUID me = null;
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         me = (ViewByLUID)reg.get(GUID);
         if (me == null) {
            me = new ViewByLUID();
            reg.put(GUID, me);
         }
      }
   }

   public static ViewByLUID getInstance() {
      ViewByLUID me = null;
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         return (ViewByLUID)reg.get(GUID);
      }
   }

   public ViewByLUID() {
      super(1126655);
   }

   @Override
   public String toString() {
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      return rb.getString(636);
   }

   @Override
   public Object invoke(Object parameter) {
      Event[] events = CalendarServiceManager.getInstance().getEvents();
      ViewByLUID$UidComparator uidComparator = new ViewByLUID$UidComparator();
      Arrays.sort(events, uidComparator);
      ViewByLUID$CalendarEventListScreen listScreen = new ViewByLUID$CalendarEventListScreen(events);
      UiApplication uiApp = UiApplication.getUiApplication();
      uiApp.pushScreen(listScreen);
      return null;
   }
}
