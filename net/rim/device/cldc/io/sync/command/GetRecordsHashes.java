package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntVector;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class GetRecordsHashes extends SyncCommand {
   private IntVector _groupIds;

   public GetRecordsHashes() {
      this.setTag(16);
      this._groupIds = new IntVector(0);
   }

   @Override
   public final void readParametersFrom(DataBuffer dins) {
      try {
         while (dins.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(dins);
            switch (xTag) {
               case 112:
                  int xGroupId = TypeLengthEncoding.readInt(dins);
                  this._groupIds.addElement(xGroupId);
                  break;
               default:
                  TypeLengthEncoding.skipValue(dins);
            }
         }
      } finally {
         return;
      }
   }

   public final IntVector getGroupsIds() {
      return this._groupIds;
   }

   @Override
   public final void reset() {
      super.reset();
      this._groupIds.setSize(0);
   }
}
