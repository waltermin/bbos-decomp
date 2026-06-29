package net.rim.plazmic.internal.mediaengine.event;

import net.rim.plazmic.internal.mediaengine.util.Array;
import net.rim.plazmic.internal.mediaengine.util.Heap;

class EventHeap extends Heap {
   private Event _event = new Event();

   public EventHeap(Array h) {
      super(h);
   }

   public void remove(int event, int eventParam) {
      int count = 0;

      while (count < super._end) {
         super._heap.get(this._event, count);
         if (this._event._event == event && this._event._eventParam == eventParam) {
            super._end--;
            if (count < super._end) {
               super._heap.swap(super._end, count);
            }

            super._heap.clear(super._end);
            this.fixDown(count);
         } else {
            count++;
         }
      }
   }

   public boolean hasEvent(int event) {
      boolean found = false;

      for (int count = 0; !found && count < super._end; count++) {
         super._heap.get(this._event, count);
         if (this._event._event == event) {
            found = true;
         }
      }

      return found;
   }
}
