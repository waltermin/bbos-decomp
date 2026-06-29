package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

class PeerDataBlob implements Persistable {
   static final int TYPE_SIMPLE_MSG = 1;
   static final int TYPE_NOTIFICATION = 2;
   static final int TYPE_PIN_UPDATE = 5;
   static final int TYPE_INVITE_ACCEPTED = 6;
   static final int TYPE_USER_INFO = 7;
   static final int TYPE_READ_RECEIPT = 8;
   static final int TYPE_CONV_INVITE = 9;
   static final int TYPE_CONV_ACCEPT = 16;
   static final int TYPE_CONV_JOIN = 17;
   static final int TYPE_CONV_REM = 18;
   static final int TYPE_CHG_INFO = 19;
   static final int TYPE_DELETE_CONTACT = 20;
   static final int TYPE_PASSWORD_KEY = 21;
   static final int TYPE_FILE_TRANSFER = 22;
   static final int TYPE_VERIFY_HASH = 23;
   static final int TYPE_SESSION = 24;

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
