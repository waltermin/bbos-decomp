package net.rim.device.apps.api.transmission.rim;

import net.rim.device.api.util.DataBuffer;

public class MoreMessageCompleteTransmission extends RIMMessagingTransmission {
   private int _contentId;
   private int _refId;

   public int getContentId() {
      return this._contentId;
   }

   public int getCMimeRefId() {
      return this._refId;
   }

   public boolean markMessageComplete() {
      return this._contentId == Integer.MIN_VALUE;
   }

   @Override
   public void read(DataBuffer packetDataBuffer) {
      CMIMEParameters params = new CMIMEParameters(2, 2);
      params.read(packetDataBuffer, (byte)0);
      this._contentId = CMIMEUtilities.getGMEInteger(params.get((byte)2), 0, Integer.MIN_VALUE);
      this._refId = CMIMEUtilities.getGMEInteger(params.get((byte)1), 0, Integer.MIN_VALUE);
   }
}
