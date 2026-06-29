package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class UpdateSyncConfiguration extends SyncConfiguration {
   public UpdateSyncConfiguration() {
      this.setTag(18);
   }

   @Override
   public final void writeParametersTo(DataBuffer dout) {
      TypeLengthEncoding.writeBytes(dout, 65, super._configuration);
   }

   @Override
   public final void readParametersFrom(DataBuffer din) {
      try {
         while (din.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            if (xTag == 65) {
               TypeLengthEncoding.readBytes(din, super._configuration);
            } else {
               TypeLengthEncoding.skipValue(din);
            }
         }
      } finally {
         return;
      }
   }
}
