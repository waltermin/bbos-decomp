package net.rim.device.apps.internal.profiles;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

final class NotificationsEngineImpl$EventQueue {
   private NotificationsEngineImpl$Event[] _queue;
   private final NotificationsEngineImpl this$0;

   NotificationsEngineImpl$EventQueue(NotificationsEngineImpl _1) {
      this.this$0 = _1;
      this._queue = new NotificationsEngineImpl$Event[0];
   }

   final int getEventCountBySourceID(long sourceId) {
      int result = 0;

      for (int i = 0; i < this._queue.length; i++) {
         NotificationsEngineImpl$Event e = this._queue[i];
         if (e != null && e.belongsToApplication(sourceId)) {
            result++;
         }
      }

      return result;
   }

   final void getEventReferencesBySourceID(long sourceId, Object[] objectlist) {
      for (int i = 0; i < this._queue.length; i++) {
         NotificationsEngineImpl$Event e = this._queue[i];
         if (e != null && e.belongsToApplication(sourceId)) {
            Array.resize(objectlist, objectlist.length + 1);
            objectlist[objectlist.length - 1] = e.getEventReference();
         }
      }
   }

   final void getEventIdsBySourceID(long sourceId, long[] eventIdList) {
      for (int i = 0; i < this._queue.length; i++) {
         NotificationsEngineImpl$Event e = this._queue[i];
         if (e != null && e.belongsToApplication(sourceId)) {
            Array.resize(eventIdList, eventIdList.length + 1);
            eventIdList[eventIdList.length - 1] = e.getEventId();
         }
      }
   }

   private final void remove(int eventIndex) {
      NotificationsEngineImpl$Event event = this._queue[eventIndex];
      Arrays.removeAt(this._queue, eventIndex);
      if (event.equals(this.this$0._negotiatedEvent)) {
         this.this$0.deferNegotiatedEvent();
      }

      event.clear();
   }

   final void remove(NotificationsEngineImpl$Event event) {
      int eventIndex = Arrays.getIndex(this._queue, event);
      if (eventIndex != -1) {
         this.remove(eventIndex);
      }
   }

   final void removeAll(long appId) {
      for (int i = this._queue.length - 1; i >= 0; i--) {
         if (this._queue[i].belongsToApplication(appId)) {
            this.remove(i);
         }
      }
   }

   final void removeAllExpired(long currentTime, long lastOutOfHolsterTime, boolean inHolster) {
      for (int i = this._queue.length - 1; i >= 0; i--) {
         if (!this._queue[i].isValid(currentTime, lastOutOfHolsterTime, inHolster)) {
            this.remove(i);
         }
      }
   }

   final void removeFirst(long appId, long eventId, Object eventReference) {
      int numEvents = this._queue.length;

      for (int i = 0; i < numEvents; i++) {
         if (this._queue[i].equals(appId, eventId, eventReference)) {
            this.remove(i);
            return;
         }
      }
   }

   final void insert(NotificationsEngineImpl$Event event) {
      Arrays.insertAt(this._queue, event, 0);
   }

   final NotificationsEngineImpl$Event getFirst() {
      return this._queue.length == 0 ? null : this._queue[0];
   }
}
