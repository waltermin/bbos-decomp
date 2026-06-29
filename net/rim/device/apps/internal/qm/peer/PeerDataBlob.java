package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

class PeerDataBlob implements Persistable {
   static final int TYPE_SIMPLE_MSG;
   static final int TYPE_NOTIFICATION;
   static final int TYPE_PIN_UPDATE;
   static final int TYPE_INVITE_ACCEPTED;
   static final int TYPE_USER_INFO;
   static final int TYPE_READ_RECEIPT;
   static final int TYPE_CONV_INVITE;
   static final int TYPE_CONV_ACCEPT;
   static final int TYPE_CONV_JOIN;
   static final int TYPE_CONV_REM;
   static final int TYPE_CHG_INFO;
   static final int TYPE_DELETE_CONTACT;
   static final int TYPE_PASSWORD_KEY;
   static final int TYPE_FILE_TRANSFER;
   static final int TYPE_VERIFY_HASH;
   static final int TYPE_SESSION;

   int getType() {
      throw null;
   }

   int getId() {
      return -1;
   }

   void pickle(DataBuffer _1) {
      throw null;
   }

   void unPickle(DataBuffer _1, int _2) {
      throw null;
   }

   void appendDataBuffer(DataBuffer db, DataBuffer appendMe) {
      int length = appendMe.getLength();
      appendMe.setPosition(0);
      db.writeByte(this.getType());
      db.writeCompressedInt(length);
      db.write(appendMe, length);
   }

   void addStringToDataBuffer(DataBuffer db, int type, String s) {
      if (s != null) {
         ConverterUtilities.writeStringSmart(db, type, s);
      }
   }
}
