package net.rim.plazmic.internal.mediaengine.event;

import net.rim.device.api.util.IntHashtable;
import net.rim.plazmic.internal.mediaengine.service.EventResolver;
import net.rim.plazmic.internal.mediaengine.util.ArrayList;

public class EventResolverImpl implements EventResolver {
   private EventLogic _logic;
   private Event _event = new Event();
   private EventEngine _engine;
   private IntHashtable _listenerTable = new IntHashtable();

   @Override
   public Object getEventLogic() {
      return this._logic;
   }

   @Override
   public void setEventLogic(Object data) {
      if (data != null && !(data instanceof EventLogic)) {
         throw new IllegalArgumentException();
      }

      this._logic = (EventLogic)data;
   }

   @Override
   public void mediaEvent(Object sender, int type, int source, Object receivedEvent) {
      if (this._logic != null && this._engine != null) {
         ArrayList eventList = (ArrayList)this._logic.getDependentEvents((Event)receivedEvent);
         if (eventList != null) {
            for (int i = 0; i < eventList.size(); i++) {
               eventList.get(this._event, i);
               this._event._time = ((Event)receivedEvent)._time + this._event._time;
               this._event._sender = this;
               this._event._listener = this.resolveListener(this._event._event);
               this._engine.postEvent(this._event, true);
               this._event.clear();
            }
         }
      }
   }

   @Override
   public void setEventListener(int typeOfEvent, Object listener) {
      this._listenerTable.put(typeOfEvent, listener);
   }

   @Override
   public void removeAllEventListeners() {
      this._listenerTable.clear();
   }

   private Object resolveListener(int type) {
      return this._listenerTable.get(type);
   }

   @Override
   public void setEngine(EventEngine engine) {
      this._engine = engine;
   }
}
