package net.rim.device.api.listener;

import net.rim.vm.WeakReference;

final class EventListenerManager$PostEventNotification extends EventListenerManager$EventNotification {
   EventListenerManager$PostEventNotification(WeakReference applicationWR, Event event) {
      super(applicationWR, null, event);
   }

   @Override
   final Thread invoke() {
      return super._event.postUpdateEventListener();
   }
}
