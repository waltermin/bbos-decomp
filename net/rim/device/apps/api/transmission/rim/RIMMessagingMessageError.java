package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;

public final class RIMMessagingMessageError extends RIMMessagingTransmission {
   private Parameters _parameters = new Parameters(5, 8);

   public final int getErrorCode() {
      return CMIMEUtilities.getGMEInteger(this._parameters.get((byte)48), 1);
   }

   public final Object getText() {
      try {
         return CMIMEUtilities.getTextObject(this._parameters.getFirst((byte)64));
      } finally {
         EventLogger.logEvent(3020044433160143544L, 1382380389, 2);
         return null;
      }
   }

   public final int getReferenceIdentifier() {
      return CMIMEUtilities.getGMEInteger(this._parameters.get((byte)48), 0);
   }

   @Override
   public final void read(DataBuffer packetDataBuffer) {
      this._parameters.read(packetDataBuffer);
   }
}
