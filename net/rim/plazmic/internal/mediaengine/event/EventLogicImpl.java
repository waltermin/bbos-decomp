package net.rim.plazmic.internal.mediaengine.event;

import net.rim.device.api.util.LongHashtable;
import net.rim.plazmic.internal.mediaengine.util.ArrayList;

public class EventLogicImpl implements EventLogic {
   private LongHashtable _eventMap = (LongHashtable)(new Object());
   private static final int DEFAULT_EVENT_LIST_LENGTH = 3;
   private static final int DEFAULT_EVENT_LIST_INCREMENT = 3;

   protected long getKey(Event triggerEvent) {
      return (long)triggerEvent._eventParam << 32 | triggerEvent._event;
   }

   public boolean isEmpty() {
      return this._eventMap.isEmpty();
   }

   @Override
   public Object getDependentEvents(Event triggerEvent) {
      return this._eventMap.get(this.getKey(triggerEvent));
   }

   @Override
   public void addEventDependancy(Event triggerEvent, Event resultingEvent, long timeOffsetMillis) {
      ArrayList eventList = (ArrayList)this.getDependentEvents(triggerEvent);
      if (eventList == null) {
         eventList = this.createEventList();
         this.addToEventMap(triggerEvent, eventList);
      }

      resultingEvent._time = timeOffsetMillis;
      eventList.add(resultingEvent);
   }

   @Override
   public void removeEventDependancy(Event triggerEvent, Event resultingEvent) {
      throw new Object("Not Implemented Yet!");
   }

   private void addToEventMap(Event triggerEvent, Object eventList) {
      this._eventMap.put(this.getKey(triggerEvent), eventList);
   }

   private ArrayList createEventList() {
      return new ArrayList(new EventArray(), 3, 3);
   }
}
