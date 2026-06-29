package net.rim.device.api.listener;

import net.rim.vm.WeakReference;

final class EventListenerManager$PreEventNotification extends EventListenerManager$EventNotification {
   EventListenerManager$PreEventNotification(WeakReference applicationWR, Event event) {
      super(applicationWR, null, event);
   }

   @Override
   final Thread invoke() {
      return super._event.preUpdateEventListener();
   }
}
