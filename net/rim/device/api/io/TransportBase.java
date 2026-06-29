package net.rim.device.api.io;

import net.rim.device.internal.io.TrafficLogger;

public class TransportBase {
   protected TrafficLogger _tLogger;

   public void init() {
      throw null;
   }

   public void setTrafficLogger(TrafficLogger logger) {
      this._tLogger = logger;
   }
}
