package net.rim.device.apps.api.calendar.controller;

import java.util.TimeZone;

public interface DurationParts {
   boolean hasParts();

   Object getNextFromTime(long var1, TimeZone var3);

   Object getPrevFromTime(long var1, TimeZone var3);

   Object getNext(Object var1);

   Object getPrev(Object var1);

   int compare(Object var1, Object var2);
}
