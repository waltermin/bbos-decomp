package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class ConvJoinBlob extends PeerDataBlob {
   private String _convId;
   private String _contactId;
   private String _contactName;
   private static final int CONV_ID = 1;
   private static final int CONTACT_ID = 2;
   private static final int CONTACT_NAME = 3;

   public ConvJoinBlob() {
   }

   public ConvJoinBlob(String convId, String contactId, String contactName) {
      this._convId = convId;
      this._contactId = contactId;
      this._contactName = contactName;
   }

   public final String getConversationId() {
      return this._convId;
   }

   public final String getContactId() {
      return this._contactId;
   }

   public final String getContactName() {
      return this._contactName;
   }

   @Override
   public final int getType() {
      return 17;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.addStringToDataBuffer(db2, 1, this._convId);
      this.addStringToDataBuffer(db2, 2, this._contactId);
      this.addStringToDataBuffer(db2, 3, this._contactName);
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
               this._contactId = ConverterUtilities.readString(db);
               break;
            case 3:
               this._contactName = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
