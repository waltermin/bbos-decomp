package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.Persistable;

final class CalendarOptions$CalendarPropertyValue implements Persistable {
   long _calendarServiceID;
   long _calendarFolderID;
   boolean _allowWirelessSync;
   int _calendarColour = 255;
   boolean _displayReminders = true;
   boolean _showAppointments = true;
}
