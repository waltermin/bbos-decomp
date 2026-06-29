package net.rim.device.apps.internal.activation;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.system.NvStore;

final class ActivationServiceImpl$ServerLongTermKeyStore extends Hashtable {
   ActivationServiceImpl$ServerLongTermKeyStore() {
      this.initalize();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final boolean initalize() {
      synchronized (this) {
         boolean var6 = false /* VF: Semaphore variable */;

         boolean var10000;
         try {
            var6 = true;
            byte[] e = NvStore.readData(20);
            if (e != null) {
               this.restore(e);
            }

            var10000 = true;
            var6 = false;
         } finally {
            if (var6) {
               ActivationServiceImpl.logEvent(1314276683, 2);
               NvStore.deleteData(20);
               return false;
            }
         }

         return var10000;
      }
   }

   private final byte[] serialize() {
      synchronized (this) {
         DataBuffer dataBuffer = (DataBuffer)(new Object());
         Enumeration keys = this.keys();
         Enumeration values = this.elements();

         while (keys.hasMoreElements()) {
            String uid = (String)keys.nextElement();
            byte[] serverLongTermKey = (byte[])values.nextElement();
            dataBuffer.writeByteArray(uid.getBytes());
            dataBuffer.writeByteArray(serverLongTermKey);
         }

         dataBuffer.trim();
         return dataBuffer.toArray();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void restore(byte[] inputBytes) {
      synchronized (this) {
         this.clear();
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            Object e = new Object(inputBytes, 0, inputBytes.length, true);

            while (!((DataBuffer)e).eof()) {
               byte[] uidBytes = ((DataBuffer)e).readByteArray();
               byte[] serverLongTermKey = ((DataBuffer)e).readByteArray();
               this.put(new Object(uidBytes), serverLongTermKey);
            }

            var9 = false;
         } finally {
            if (var9) {
               ActivationServiceImpl.logEvent(1314276691, 2);
               NvStore.deleteData(20);
               return;
            }
         }
      }
   }

   final void commit() {
      synchronized (this) {
         byte[] data = this.serialize();
         NvStore.writeData(20, data);
      }
   }
}
