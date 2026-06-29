package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.RecordBaseSyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public class Record extends RecordBaseSyncCommand {
   public Record() {
      this.setTag(6);
   }

   @Override
   public void readParametersFrom(DataBuffer din) {
      try {
         while (din.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            if (!super.readParameterValueFrom(xTag, din)) {
               TypeLengthEncoding.skipValue(din);
            }
         }
      } finally {
         return;
      }
   }
}
