package net.rim.device.api.crypto.certificate.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.util.Arrays;

final class ProviderCompressionTable {
   private Vector _hashes = (Vector)(new Object());
   private Vector _values = (Vector)(new Object());

   public ProviderCompressionTable() {
   }

   public final int addTableEntry(byte[] data) {
      int index = this.matchEntry(data);
      if (index != -1) {
         return index;
      }

      if (this._hashes.size() == 32767) {
         throw new Object();
      }

      this._hashes.addElement(new Object(HashCodeCalculator.getCRC32(data)));
      this._values.addElement(data);
      return this._hashes.size() - 1;
   }

   public final byte[] getTableEntry(int index) {
      return (byte[])this._values.elementAt(index);
   }

   private final int matchEntry(byte[] data) {
      int index = -1;
      int last = this._hashes.size() - 1;
      Integer hash = (Integer)(new Object(HashCodeCalculator.getCRC32(data)));

      while (index < last) {
         index = this._hashes.indexOf(hash, index + 1);
         if (index == -1) {
            break;
         }

         if (Arrays.equals((byte[])this._values.elementAt(index), data)) {
            return index;
         }
      }

      return -1;
   }

   public final int size() {
      return this._hashes.size();
   }

   public final void serialize(DataOutputStream out) {
      int size = this._values.size();
      out.writeShort((short)size);

      for (int i = 0; i < size; i++) {
         byte[] data = (byte[])this._values.elementAt(i);
         out.writeShort((short)data.length);
         out.write(data);
      }
   }

   public final void unSerialize(DataInputStream in) {
      int size = in.readShort() & '\uffff';

      for (int i = 0; i < size; i++) {
         byte[] value = new byte[in.readShort() & 65535];
         in.read(value);
         this.addTableEntry(value);
      }
   }
}
