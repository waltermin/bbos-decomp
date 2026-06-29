package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;

final class SessionBlob extends PeerDataBlob {
   private int _type;
   private int _id;
   private String _application;
   private String _message;
   public static final int TYPE_REQUEST = 0;
   public static final int TYPE_ACCEPT = 1;
   public static final int TYPE_DENY = 2;
   public static final int TYPE_END = 3;
   private static final int TYPE = 1;
   private static final int APP = 2;
   private static final int MSG = 3;
   private static final int ID = 4;

   public SessionBlob() {
   }

   public SessionBlob(int type, String application, String message) {
      this._type = type;
      this._id = this.hashCode();
      this._application = application;
      this._message = message;
   }

   public final void setId(int id) {
      this._id = id;
   }

   @Override
   public final int getId() {
      return this._id;
   }

   public final int getSessionType() {
      return this._type;
   }

   public final String getApplication() {
      return this._application;
   }

   public final String getMessage() {
      return this._message;
   }

   @Override
   public final int getType() {
      return 24;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object());
      ConverterUtilities.writeInt(db2, 1, this._type);
      ConverterUtilities.writeInt(db2, 4, this._id);
      if (this._application != null) {
         this.addStringToDataBuffer(db2, 2, this._application);
      }

      if (this._message != null) {
         this.addStringToDataBuffer(db2, 3, this._message);
      }

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
               this._type = ConverterUtilities.readInt(db);
               break;
            case 2:
            default:
               this._application = ConverterUtilities.readString(db);
               break;
            case 3:
               this._message = ConverterUtilities.readString(db);
               break;
            case 4:
               this._id = ConverterUtilities.readInt(db);
         }
      }

      ConverterUtilities.skipField(db);
   }
}
