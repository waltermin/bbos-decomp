package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class NotificationDataBlob extends PeerDataBlob {
   private int _notifyCode;
   private String _message;
   private String _from;
   private static final int NOTIF_CODE = 1;
   private static final int NOTIF_MSG = 2;
   private static final int FROM_ID = 3;
   public static final int NOTIF_UNKNOWN = -1;
   public static final int NOTIF_UNAVAILABLE = 0;
   public static final int NOTIF_AVAILABLE = 1;
   public static final int NOTIF_BUSY = 2;
   public static final int NOTIF_THREAD_READ = 3;
   public static final int NOTIF_TYPING = 4;
   public static final int NOTIF_NOT_TYPING = 5;
   public static final int NO_PRESENCE_SUPPORT = 6;
   public static final int READ_ONLY_PRESENCE_SUPPORT = 7;

   public NotificationDataBlob() {
      this(-1, null);
   }

   public NotificationDataBlob(int code, String message) {
      this._notifyCode = code;
      this._message = message;
   }

   public final void setData(int code, String message) {
      this._notifyCode = code;
      this._message = message;
   }

   public final String getMessage() {
      return this._message;
   }

   public final int getNotificationCode() {
      return this._notifyCode;
   }

   public final String getFromId() {
      return this._from;
   }

   @Override
   public final int getType() {
      return 2;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      ConverterUtilities.writeInt(db2, 1, this._notifyCode);
      this.addStringToDataBuffer(db2, 2, this._message);
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
               this._notifyCode = ConverterUtilities.readInt(db);
               break;
            case 2:
               this._message = ConverterUtilities.readString(db);
               break;
            case 3:
               this._from = ConverterUtilities.readString(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
