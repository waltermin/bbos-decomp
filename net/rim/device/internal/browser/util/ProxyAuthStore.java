package net.rim.device.internal.browser.util;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.NvStore;

public final class ProxyAuthStore {
   private static final int VERSION_1;
   public static final int REALM;
   public static final int USERNAME;
   public static final int PASSWORD;

   private ProxyAuthStore() {
   }

   public static final void addConnection(String uid, String[] fields) {
      if (uid != null && fields != null && fields.length == 3 && fields[0] != null && fields[1] != null && fields[2] != null) {
         Hashtable data = readTable();
         data.put(uid, fields);
         writeTable(data);
      } else {
         throw new Object();
      }
   }

   public static final String[] getConnection(String uid) {
      if (uid == null) {
         throw new Object();
      }

      Hashtable data = readTable();
      return (Object[])data.get(uid);
   }

   private static final Hashtable readTable() {
      Hashtable nvStore = (Hashtable)(new Object());
      byte[] nvStoreData = NvStore.readData(35);
      if (nvStoreData != null && nvStoreData.length > 0) {
         DataBuffer buffer = (DataBuffer)(new Object(nvStoreData, 0, nvStoreData.length, true));

         try {
            byte version = buffer.readByte();

            while (buffer.available() > 0) {
               String id = buffer.readUTF();
               String[] data = new Object[3];
               data[0] = buffer.readUTF();
               data[1] = buffer.readUTF();
               data[2] = buffer.readUTF();
               nvStore.put(id, data);
            }
         } finally {
            return nvStore;
         }
      }

      return nvStore;
   }

   private static final void writeTable(Hashtable htData) {
      DataBuffer buffer = (DataBuffer)(new Object());
      if (htData != null) {
         try {
            buffer.writeByte(1);
            Enumeration elements = htData.keys();

            while (elements.hasMoreElements()) {
               String key = (String)elements.nextElement();
               String[] data = (Object[])htData.get(key);
               buffer.writeUTF(key);
               buffer.writeUTF(data[0]);
               buffer.writeUTF(data[1]);
               buffer.writeUTF(data[2]);
            }
         } finally {
            ;
         }
      }

      NvStore.writeData(35, buffer.getArray(), buffer.getArrayStart(), buffer.getLength());
   }
}
