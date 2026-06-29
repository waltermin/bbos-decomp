package net.rim.device.api.crypto.encoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.NoSuchAlgorithmException;

final class Utility {
   static final boolean registerAlgorithms(Hashtable registry, Object coder, String baseAlgorithm, String[] subAlgorithms) {
      if (registry != null && coder != null && baseAlgorithm != null && subAlgorithms != null) {
         int numSubAlgorithms = subAlgorithms.length;
         Hashtable table = (Hashtable)registry.get(baseAlgorithm);
         if (table == null) {
            table = new Hashtable(numSubAlgorithms * 3 / 2);
            registry.put(baseAlgorithm, table);
         }

         boolean added = false;

         for (int i = 0; i < numSubAlgorithms; i++) {
            String subAlgorithm = subAlgorithms[i];
            Object found = table.get(subAlgorithm);
            if (found == null) {
               table.put(subAlgorithm, coder);
               added = true;
            }
         }

         return added;
      } else {
         throw new IllegalArgumentException();
      }
   }

   static final Object getCoder(Hashtable hashTable, String encodingAlgorithm, String algorithm) throws NoSuchAlgorithmException {
      if (hashTable != null && encodingAlgorithm != null && algorithm != null) {
         Hashtable find = (Hashtable)hashTable.get(encodingAlgorithm);
         if (find != null) {
            Object coder = find.get(algorithm);
            if (coder != null) {
               return coder;
            }
         }

         throw new NoSuchAlgorithmException(encodingAlgorithm + ' ' + algorithm);
      } else {
         throw new IllegalArgumentException();
      }
   }

   static final byte[] readData(DataInputStream input) {
      if (input == null) {
         throw new IllegalArgumentException();
      }

      int length = input.readInt();
      byte[] data = new byte[length];
      input.readFully(data);
      return data;
   }

   static final void writeData(byte[] data, DataOutputStream output) {
      if (output == null) {
         throw new IllegalArgumentException();
      }

      if (data == null) {
         output.writeInt(0);
      } else {
         output.writeInt(data.length);
         output.write(data);
      }
   }
}
