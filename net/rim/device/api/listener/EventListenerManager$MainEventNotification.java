package net.rim.device.api.listener;

import net.rim.vm.WeakReference;

final class EventListenerManager$MainEventNotification extends EventListenerManager$EventNotification {
   EventListenerManager$MainEventNotification(WeakReference applicationWR, Object listener, Event event) {
      super(applicationWR, listener, event);
   }

   @Override
   final Thread invoke() {
      return super._listener == null ? null : super._event.updateEventListener(super._listener);
   }
}
