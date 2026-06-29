package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class ChangeInfoBlob extends PeerDataBlob {
   private String _contactId;
   private String _contactName;
   private String _oldPin;
   private static final int CONTACT_ID = 1;
   private static final int CONTACT_NAME = 2;
   private static final int OLD_PIN = 3;

   public ChangeInfoBlob() {
   }

   public final void setData(String contactId, String contactName, String oldPin) {
      this._contactId = contactId;
      this._contactName = contactName;
      this._oldPin = oldPin;
   }

   public final String getContactName() {
      return this._contactName;
   }

   public final String getOldPin() {
      return this._oldPin;
   }

   @Override
   public final int getType() {
      return 19;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.addStringToDataBuffer(db2, 1, this._contactId);
      this.addStringToDataBuffer(db2, 2, this._contactName);
      this.addStringToDataBuffer(db2, 3, this._oldPin);
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
               this._contactId = ConverterUtilities.readString(db);
               break;
            case 2:
               this._contactName = ConverterUtilities.readString(db);
               break;
            case 3:
               this._oldPin = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
