package net.rim.device.api.listener;

public interface Event {
   Thread preUpdateEventListener();

   Thread updateEventListener(Object var1);

   Thread postUpdateEventListener();
}
