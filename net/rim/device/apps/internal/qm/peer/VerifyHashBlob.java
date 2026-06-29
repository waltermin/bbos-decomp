package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class VerifyHashBlob extends PeerDataBlob {
   private boolean _request;
   private String _message;
   private String _hash;
   private String _displayName;
   private int _cookie;
   private static final int REQUEST = 1;
   private static final int MESSAGE = 2;
   private static final int HASH = 3;
   private static final int DISPLAY_NAME = 4;
   private static final int COOKIE = 5;

   public VerifyHashBlob() {
      this(false, null, null, 0);
   }

   public VerifyHashBlob(boolean request, String message, String hash, int cookie) {
      this._request = request;
      this._message = message;
      this._hash = hash;
      PeerApplication.getInstance();
      this._displayName = PeerApplication.getSession().getDisplayName();
      this._cookie = cookie;
   }

   public final boolean isRequest() {
      return this._request;
   }

   public final String getMessage() {
      return this._message;
   }

   public final String getHash() {
      return this._hash;
   }

   public final String getDisplayName() {
      return this._displayName;
   }

   public final int getCookie() {
      return this._cookie;
   }

   @Override
   public final int getType() {
      return 23;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      ConverterUtilities.writeInt(db2, 1, this._request ? 1 : 0);
      this.addStringToDataBuffer(db2, 2, this._message);
      this.addStringToDataBuffer(db2, 3, this._hash);
      this.addStringToDataBuffer(db2, 4, this._displayName);
      ConverterUtilities.writeInt(db2, 5, this._cookie);
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
               this._request = ConverterUtilities.readInt(db) != 0;
               break;
            case 2:
               this._message = ConverterUtilities.readString(db);
               break;
            case 3:
               this._hash = ConverterUtilities.readString(db);
               break;
            case 4:
               this._displayName = ConverterUtilities.readString(db);
               break;
            case 5:
               this._cookie = ConverterUtilities.readInt(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
