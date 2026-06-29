package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.calendar.eventdb.CalDBFactory;
import net.rim.device.apps.internal.calendar.eventdb.CalendarProxyImpl;
import net.rim.device.apps.internal.calendar.meeting.PackageManager;
import net.rim.device.internal.system.InternalServices;

class Registration {
   public static void libMain(String[] args) {
      CalendarProxy calProxy = CalendarProxyImpl.init();
      UnreadCountManager.setUnreadCountVisible(8, false);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      CalDBFactory calDBFactory = new CalDBFactory();
      ar.put(1210326705763766088L, calDBFactory);
      EventFactory eventFac = new EventFactory();
      ar.put(-1986287563994289176L, eventFac);
      calProxy.addToRepository(-2932280743217917193L, new NewEventInPlaceVerb());
      Verb newEventVerb = new NewEventVerb();
      calProxy.addToRepository(-2786162410658704605L, newEventVerb);
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      rb = rb;
      HotKeys.registerHotKey(4, !InternalServices.isReducedFormFactor() ? rb.getString(122).charAt(0) : 'O', newEventVerb, true);
      PackageManager.registerOnceOnSystemStart();
      NotificationsManager.registerSource(2666833733215697856L, new Registration$1(), 1);
   }
}
