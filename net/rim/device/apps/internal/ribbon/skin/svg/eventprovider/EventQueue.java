package net.rim.device.apps.internal.ribbon.skin.svg.eventprovider;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ToIntHashtable;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.vm.Array;

class EventQueue {
   private int _numEventTypes;
   private String[] _queuedEvents = new String[0];
   private int[] _queuedEventsOrder = new int[0];
   private ToIntHashtable _supportedEvents = new ToIntHashtable(20);

   public void registerEventPair(String eventId1, String eventId2) {
      this._supportedEvents.put(eventId1, this._numEventTypes);
      this._supportedEvents.put(eventId2, this._numEventTypes);
      this._numEventTypes++;
      Array.resize(this._queuedEvents, this._numEventTypes);
   }

   public void queueEvent(String eventId) {
      int eventIdx = this._supportedEvents.get(eventId);
      if (eventIdx != -1) {
         synchronized (this._queuedEventsOrder) {
            this._queuedEvents[eventIdx] = eventId;
            if (Arrays.contains(this._queuedEventsOrder, eventIdx)) {
               Arrays.remove(this._queuedEventsOrder, eventIdx);
            }

            Arrays.add(this._queuedEventsOrder, eventIdx);
         }
      }
   }

   public void triggerEvents(ModelInteractorImpl model) {
      synchronized (this._queuedEventsOrder) {
         int len = this._queuedEventsOrder.length;

         for (int i = 0; i < len; i++) {
            int eventIdx = this._queuedEventsOrder[i];
            String eventId = this._queuedEvents[eventIdx];
            if (eventId != null) {
               int handle = model.getHandle(eventId);
               model.trigger(107, handle, null);
               this._queuedEvents[eventIdx] = null;
            }
         }

         Array.resize(this._queuedEventsOrder, 0);
      }
   }
}
