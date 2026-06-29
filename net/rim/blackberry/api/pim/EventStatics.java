package net.rim.blackberry.api.pim;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;

final class EventStatics {
   protected static Factory _eventFactory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(-1986287563994289176L);
   protected static CalDB _cal = CalendarProxy.getInstance().getCalendarDatabase();
}
