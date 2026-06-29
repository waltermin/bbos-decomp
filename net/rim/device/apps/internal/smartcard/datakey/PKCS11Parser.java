package net.rim.device.apps.internal.smartcard.datakey;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;

class PKCS11Parser implements DatakeyPKCS11Constants {
   public static IntHashtable getAttributes(byte[] data) {
      if (data != null && data.length != 0) {
         DataBuffer buffer = new DataBuffer(false);
         buffer.setData(data, 0, data.length);
         IntHashtable attributes = new IntHashtable();

         try {
            while (!buffer.eof()) {
               int type = buffer.readInt();
               short length = buffer.readShort();
               byte[] value = new byte[length];
               buffer.readFully(value);
               attributes.put(type, new PKCS11Attribute(value));
            }
         } finally {
            return attributes;
         }

         return attributes;
      } else {
         return null;
      }
   }
}
