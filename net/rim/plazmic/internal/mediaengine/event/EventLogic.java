package net.rim.plazmic.internal.mediaengine.event;

public interface EventLogic {
   void addEventDependancy(Event var1, Event var2, long var3);

   void removeEventDependancy(Event var1, Event var2);

   Object getDependentEvents(Event var1);
}
