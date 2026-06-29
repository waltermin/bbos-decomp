package net.rim.device.apps.api.calendar.caldb;

import java.util.Hashtable;
import net.rim.vm.Persistable;

final class CalendarOptions$PersistedCalendarOptions implements Persistable {
   int _firstDayOfWeek = 0;
   int _dayStartMillis = 32400000;
   int _dayEndMillis = 61200000;
   long _reminderMillis = CalendarOptions.REMINDER_CHOICES[4];
   boolean _confirmDelete = true;
   int _initialView = 0;
   boolean _keystrokesTriggerQuickInput = true;
   boolean _displayViewUnitsInMenu = true;
   long _snoozeMillis = 300000;
   boolean _doSpellCheck = false;
   short _keepAppointmentsDuration = CalendarOptions.KEEP_APPOINTMENTS_DURATION_CHOICES[2];
   boolean _tasksInView = false;
   boolean _multiDatabaseSupport = false;
   boolean _useLegacyAgendaView = false;
   int _timeToShowFreeBlocks = 15;
   boolean _showEndTime = true;
   Hashtable _calendarPropertyTable = new Hashtable(3);
}
