package net.rim.device.api.gps;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class FledgeEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      switch (event) {
         case 5635:
            GPSRegistry gpsRegistry = (GPSRegistry)listener;
            gpsRegistry.setSimulateGPSPuck(subMessage != 0);
      }
   }
}
