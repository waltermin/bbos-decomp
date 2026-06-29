package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.util.DataBuffer;

public final class PushProperty {
   private PushProperty() {
   }

   public static final void serialize(DataBuffer dataBuffer) {
      dataBuffer.writeBoolean(true);
   }

   public static final boolean initialize(DataBuffer dataBuffer, int version) {
      try {
         dataBuffer.readCompressedInt();
         return true;
      } finally {
         ;
      }
   }
}
