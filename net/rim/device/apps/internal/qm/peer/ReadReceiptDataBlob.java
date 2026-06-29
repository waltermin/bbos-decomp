package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.DataBuffer;

final class ReadReceiptDataBlob extends PeerDataBlob {
   private int _id;
   private String _convId;

   public ReadReceiptDataBlob() {
   }

   public ReadReceiptDataBlob(String convId, int id) {
      this._id = id;
      this._convId = convId;
   }

   public final int getMsgId() {
      return this._id;
   }

   public final String getConvId() {
      return this._convId;
   }

   @Override
   public final int getType() {
      return 8;
   }

   @Override
   public final void pickle(DataBuffer db) {
      db.writeByte(8);
      db.writeCompressedInt(4);
      db.writeInt(this._id);
      db.writeUTF(this._convId);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      this._id = db.readInt();
      this._convId = db.readUTF();
   }
}
