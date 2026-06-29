package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.DataBuffer;

final class DeleteContactBlob extends PeerDataBlob {
   public DeleteContactBlob() {
   }

   @Override
   public final int getType() {
      return 20;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
   }
}
