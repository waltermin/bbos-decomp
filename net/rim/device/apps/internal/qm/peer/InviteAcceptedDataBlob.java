package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class InviteAcceptedDataBlob extends PeerDataBlob {
   private String _cookie;
   private String _userName;
   private String _contactId;
   private String _key;
   private String _originalContactInfo;
   private static final int COOKIE;
   private static final int USERNAME;
   private static final int CONTACT_ID;
   private static final int KEY;
   private static final int ORIGINAL_CONTACT_INFO;

   public InviteAcceptedDataBlob() {
      this(null, null, null, null, null);
   }

   public InviteAcceptedDataBlob(String cookie, String contactId, String userName, String key, String originalContactInfo) {
      this._cookie = cookie;
      this._contactId = contactId;
      this._userName = userName;
      this._key = key;
      this._originalContactInfo = originalContactInfo;
   }

   public final String getContactId() {
      return this._contactId;
   }

   public final String getUserName() {
      return this._userName;
   }

   public final String getKey() {
      return this._key;
   }

   public final String getOrigContactInfo() {
      return this._originalContactInfo;
   }

   @Override
   public final int getType() {
      return 6;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      this.addStringToDataBuffer(db2, 1, this._cookie);
      this.addStringToDataBuffer(db2, 3, this._contactId);
      this.addStringToDataBuffer(db2, 2, this._userName);
      this.addStringToDataBuffer(db2, 4, this._key);
      this.addStringToDataBuffer(db2, 6, this._originalContactInfo);
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
            case 5:
               ConverterUtilities.skipField(db);
               break;
            case 1:
            default:
               this._cookie = ConverterUtilities.readString(db);
               break;
            case 2:
               this._userName = ConverterUtilities.readString(db);
               break;
            case 3:
               this._contactId = ConverterUtilities.readString(db);
               break;
            case 4:
               this._key = ConverterUtilities.readString(db);
               break;
            case 6:
               this._originalContactInfo = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
