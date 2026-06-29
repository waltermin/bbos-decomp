package net.rim.device.api.system;

import net.rim.device.api.listener.Event;
import net.rim.device.api.listener.EventListenerManager;

final class PersistentContent$Listeners$EventLauncher implements Runnable {
   EventListenerManager _listeners;
   Event _event;
   boolean _broadcast;

   PersistentContent$Listeners$EventLauncher(EventListenerManager listeners, Event event, boolean broadcast) {
      this._listeners = listeners;
      this._event = event;
      this._broadcast = broadcast;
   }

   @Override
   public final void run() {
      if (this._broadcast) {
         this._listeners.update(this._event);
      } else {
         this._listeners.update(null, this._event);
      }
   }
}
