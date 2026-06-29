package net.rim.device.internal.system;

public final class DefaultManuallySelectedNetIdReader implements SIMCardEfTask {
   public final void setManuallySelectedNetId(int networkId) {
      if (RadioInternal.getManuallySelectedNetworkID() == -1) {
         RadioInternal.setManuallySelectedNetworkID(networkId);
      }
   }

   @Override
   public final void doWork(SIMCardEfHandler efHandler) {
      int result = efHandler.infoRequest(17);
      if (result == 0) {
         byte[] buffer = new byte[efHandler.getFileSize()];
         int code = efHandler.readRequest(0, buffer);
         if (code == 0) {
            if (buffer.length >= 7) {
               int byte1 = buffer[4];
               int byte2 = buffer[5];
               int byte3 = buffer[6];
               if ((byte1 & byte2 & byte3) == 255) {
                  return;
               }

               int mcc = (byte1 & 15) << 8;
               mcc |= byte1 & 240;
               mcc |= byte2 & 15;
               int mnc;
               if ((byte2 & 240) == 240) {
                  mnc = (byte3 & 15) << 4;
                  mnc |= (byte3 & 240) >> 4;
               } else {
                  mnc = (byte3 & 15) << 8;
                  mnc |= byte3 & 240;
                  mnc |= (byte2 & 240) >> 4;
               }

               this.setManuallySelectedNetId(mnc << 16 | mcc);
            }
         }
      }
   }

   public DefaultManuallySelectedNetIdReader() {
      RadioInternal.setManuallySelectedNetworkID(-1);
   }
}
