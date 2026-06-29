package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.transmission.EventLoggerEvents;
import net.rim.device.apps.api.utility.serialization.SerializationException;

public class RIMMessagingTransmission implements CMIMEConstants, EventLoggerEvents, Persistable {
   public void read(DataBuffer packetDataBuffer) throws SerializationException {
      throw new SerializationException();
   }

   public DataBuffer write() throws SerializationException {
      throw new SerializationException();
   }
}
