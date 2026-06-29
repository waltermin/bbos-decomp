package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class PasswordKeyBlob extends PeerDataBlob {
   private String _key;
   private static final int KEY = 1;

   public PasswordKeyBlob() {
   }

   public final void setData(String key) {
      this._key = key;
   }

   public final String getKey() {
      return this._key;
   }

   @Override
   public final int getType() {
      return 21;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = new DataBuffer();
      this.addStringToDataBuffer(db2, 1, this._key);
      ConverterUtilities.writeEmptyField(db2, 0);
      db2.trim();
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      int type;
      while (db.available() > 2 && (type = ConverterUtilities.getType(db, true)) != 0) {
         switch (type) {
            case 1:
               this._key = ConverterUtilities.readString(db);
               break;
            default:
               ConverterUtilities.skipField(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
