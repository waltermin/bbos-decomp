package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.transmission.Parameters;

public final class RIMMessagingDeliveryToAddress extends RIMMessagingTransmission {
   private Parameters _parameters = new Parameters(5, 8);

   public final String[][] getAddresses() {
      return CMIMEUtilities.decodeAddresses(this._parameters.get((byte)64));
   }

   public final int getReferenceIdentifier() {
      return CMIMEUtilities.decodeInteger(this._parameters.getFirst((byte)48));
   }

   @Override
   public final void read(DataBuffer packetDataBuffer) {
      this._parameters.read(packetDataBuffer);
   }
}
