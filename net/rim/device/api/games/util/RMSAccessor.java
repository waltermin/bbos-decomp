package net.rim.device.api.games.util;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.util.Arrays;

public final class RMSAccessor {
   private static int start = 0;
   private static byte[] rytp = new byte[]{32, 21, 6, 115};
   private static byte[] hrew = new byte[]{37, 63, 98, 5};
   private static byte[] ghnm = new byte[]{24, 12, 126, 65};
   private static byte[] qmsp = new byte[]{103, 55, 8, 85};

   public static final boolean save(byte[] data, String name) {
      label60:
      try {
         RecordStore.deleteRecordStore(name);
      } finally {
         break label60;
      }

      if (data == null) {
         return true;
      }

      byte[] key = new byte[0];

      for (int i = 3; i >= 0; i--) {
         Arrays.insertAt(key, hrew[i], 0);
         Arrays.insertAt(key, rytp[i], 0);
         Arrays.insertAt(key, ghnm[i], 0);
         Arrays.insertAt(key, qmsp[i], 0);
      }

      try {
         HMAC hmac = new HMAC(new HMACKey(key), new MD5Digest());
         hmac.update(data);
         byte[] mac = hmac.getMAC();
         RecordStore rms = RecordStore.openRecordStore(name, true);
         rms.addRecord(data, start, data.length);
         rms.addRecord(mac, 0, mac.length);
         rms.closeRecordStore();
         return true;
      } finally {
         ;
      }
   }

   public static final byte[] restore(String name) {
      byte[] result = null;

      try {
         RecordStore rms = RecordStore.openRecordStore(name, true);
         RecordEnumeration enumeration = rms.enumerateRecords(null, null, false);
         if (enumeration.numRecords() == 0) {
            rms.closeRecordStore();
            enumeration.destroy();
            return null;
         }

         result = enumeration.nextRecord();
         byte[] mac = enumeration.nextRecord();
         rms.closeRecordStore();
         enumeration.destroy();
         byte[] key = new byte[0];

         for (int i = 3; i >= 0; i--) {
            Arrays.insertAt(key, hrew[i], 0);
            Arrays.insertAt(key, rytp[i], 0);
            Arrays.insertAt(key, ghnm[i], 0);
            Arrays.insertAt(key, qmsp[i], 0);
         }

         HMAC hmac = new HMAC(new HMACKey(key), new MD5Digest());
         hmac.update(result);
         byte[] mac2 = hmac.getMAC();
         return Arrays.equals(mac, mac2) ? result : null;
      } finally {
         return result != null ? result : null;
      }
   }
}
