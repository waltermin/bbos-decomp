package net.rim.device.api.io;

import java.io.IOException;

public class IOTransmitterException extends IOException {
   public IOTransmitterException() {
   }

   public IOTransmitterException(String msg) {
      super(msg);
   }
}
