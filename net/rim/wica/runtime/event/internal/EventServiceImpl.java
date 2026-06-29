package net.rim.wica.runtime.event.internal;

import java.util.Vector;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.logging.Logger;

public class EventServiceImpl implements EventService {
   private IntHashtable _listeners = new IntHashtable(10);

   public void dispatchEvent(int event, int eventParam, Object data) {
      this.dispatchEvent(null, event, eventParam, data);
   }

   @Override
   public void dispatchEvent(int event, Object data) {
      this.dispatchEvent(null, event, -1, data);
   }

   @Override
   public void dispatchEvent(Object sender, int event) {
      this.dispatchEvent(sender, event, -1, null);
   }

   @Override
   public void dispatchEvent(Object sender, int event, int eventParam) {
      this.dispatchEvent(sender, event, eventParam, null);
   }

   @Override
   public void dispatchEvent(Object sender, int event, Object data) {
      this.dispatchEvent(sender, event, -1, data);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void dispatchEvent(Object sender, int event, int eventParam, Object data) {
      Vector tempListeners = (Vector)this._listeners.get(event);
      if (tempListeners != null) {
         int size = tempListeners.size();

         for (int i = 0; i < size; i++) {
            EventListener listener = (EventListener)tempListeners.elementAt(i);

            try {
               listener.handleEvent(sender, event, eventParam, data);
            } catch (Throwable var11) {
               Logger.log("EventListener " + listener.getClass() + " threw " + e + " on event " + event + " sent from " + sender.getClass(), 3);
               continue;
            }
         }
      }
   }

   @Override
   public void addListener(int event, EventListener listener) {
      this.addListener(new int[]{event}, listener);
   }

   @Override
   public void addListener(int[] events, EventListener listener) {
      if (listener == null) {
         throw new NullPointerException("listener");
      }

      for (int i = 0; i < events.length; i++) {
         Vector listeners = (Vector)this._listeners.get(events[i]);
         listeners = ListenerUtilities.fastAddListener(listeners, listener);
         this._listeners.put(events[i], listeners);
      }
   }

   @Override
   public void removeListener(int event, EventListener listener) {
      this.removeListener(new int[]{event}, listener);
   }

   @Override
   public void removeListener(int[] events, EventListener listener) {
      if (listener == null) {
         throw new NullPointerException("listener");
      }

      for (int i = 0; i < events.length; i++) {
         Vector listeners = (Vector)this._listeners.get(events[i]);
         if (listeners != null) {
            listeners = ListenerUtilities.removeListener(listeners, listener);
            if (listeners == null) {
               this._listeners.remove(events[i]);
            } else {
               this._listeners.put(events[i], listeners);
            }
         }
      }
   }

   @Override
   public void removeListener(EventListener listener) {
      if (listener == null) {
         throw new NullPointerException("listener");
      }

      IntEnumeration e = this._listeners.keys();

      while (e.hasMoreElements()) {
         this.removeListener(e.nextElement(), listener);
      }
   }
}
