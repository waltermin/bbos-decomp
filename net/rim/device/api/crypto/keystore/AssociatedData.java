package net.rim.device.api.crypto.keystore;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class AssociatedData implements Persistable {
   private long _association;
   private byte[][][] _data;
   public static final long EMAIL;
   public static final long NAME;
   public static final long DESKTOP_COOKIE;
   public static final long ISSUER;
   public static final long SERIAL_NUMBER;
   public static final long SMART_CARD_KEY;
   public static final long HISTORICAL_KEY_ID;
   public static final long PGP_KEY_ID;
   public static final long PGP_UNIVERSAL_CACHED_KEY_INFO;

   public AssociatedData(long association, byte[] data) {
      this._association = association;
      if (data == null) {
         throw new Object();
      }

      this._data = new byte[1][data.length][];
      System.arraycopy(data, 0, this._data[0], 0, data.length);
   }

   public AssociatedData(long association, byte[][][] data) {
      this._association = association;
      if (data == null) {
         throw new Object();
      }

      this._data = new byte[data.length][0][];

      for (int i = 0; i < data.length; i++) {
         Array.resize(this._data[i], data[i].length);
         System.arraycopy(data[i], 0, this._data[i], 0, data[i].length);
      }
   }

   public long getAssociation() {
      return this._association;
   }

   public byte[][][] getData() {
      byte[][][] data = new byte[this._data.length][0][];

      for (int i = 0; i < this._data.length; i++) {
         Array.resize(data[i], this._data[i].length);
         System.arraycopy(this._data[i], 0, data[i], 0, this._data[i].length);
      }

      return data;
   }
}
