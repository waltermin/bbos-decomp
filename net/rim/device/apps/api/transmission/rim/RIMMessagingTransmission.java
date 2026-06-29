package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.EventLoggerEvents;

public class RIMMessagingTransmission implements CMIMEConstants, EventLoggerEvents, Persistable {
   public void read(DataBuffer packetDataBuffer) {
      throw new Object();
   }

   public DataBuffer write() {
      throw new Object();
   }
}
