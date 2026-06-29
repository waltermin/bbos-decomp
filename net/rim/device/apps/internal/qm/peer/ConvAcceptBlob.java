package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class ConvAcceptBlob extends PeerDataBlob {
   private String _convId;
   private String _contactName;
   private static final int CONV_ID = 1;
   private static final int CONTACT_NAME = 2;

   public ConvAcceptBlob() {
   }

   public ConvAcceptBlob(String convId, String contactName) {
      this._convId = convId;
      this._contactName = contactName;
   }

   public final String getConversationId() {
      return this._convId;
   }

   public final String getContactName() {
      return this._contactName;
   }

   @Override
   public final int getType() {
      return 16;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.addStringToDataBuffer(db2, 1, this._convId);
      this.addStringToDataBuffer(db2, 2, this._contactName);
      ConverterUtilities.writeEmptyField(db2, 0);
      db2.trim();
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      int type;
      while (db.available() > 2 && (type = ConverterUtilities.getType(db, true)) != 0) {
         switch (type) {
            case 0:
               ConverterUtilities.skipField(db);
               break;
            case 1:
            default:
               this._convId = ConverterUtilities.readString(db);
               break;
            case 2:
               this._contactName = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
