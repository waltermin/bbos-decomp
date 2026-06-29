package net.rim.device.apps.api.calendar.controller;

import java.util.TimeZone;

public interface Duration {
   long getStart(TimeZone var1);

   long getDuration(TimeZone var1);

   boolean isAllDay();
}
