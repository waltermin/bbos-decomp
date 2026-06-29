package net.rim.device.api.crypto.keystore;

import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class AssociatedData implements Persistable {
   private long _association;
   private byte[][] _data;
   public static final long EMAIL = -1124699153917633064L;
   public static final long NAME = -5599182711442550284L;
   public static final long DESKTOP_COOKIE = -3741488786487467288L;
   public static final long ISSUER = 5689852616259641725L;
   public static final long SERIAL_NUMBER = 7970222113131699770L;
   public static final long SMART_CARD_KEY = -4699629744920546763L;
   public static final long HISTORICAL_KEY_ID = 3198502480206239397L;
   public static final long PGP_KEY_ID = 3622586747345475248L;
   public static final long PGP_UNIVERSAL_CACHED_KEY_INFO = -7361299091238025133L;

   public AssociatedData(long association, byte[] data) {
      this._association = association;
      if (data == null) {
         throw new Object();
      }

      this._data = new byte[1][data.length];
      System.arraycopy(data, 0, this._data[0], 0, data.length);
   }

   public AssociatedData(long association, byte[][] data) {
      this._association = association;
      if (data == null) {
         throw new Object();
      }

      this._data = new byte[data.length][0];

      for (int i = 0; i < data.length; i++) {
         Array.resize(this._data[i], data[i].length);
         System.arraycopy(data[i], 0, this._data[i], 0, data[i].length);
      }
   }

   public long getAssociation() {
      return this._association;
   }

   public byte[][] getData() {
      byte[][] data = new byte[this._data.length][0];

      for (int i = 0; i < this._data.length; i++) {
         Array.resize(data[i], this._data[i].length);
         System.arraycopy(this._data[i], 0, data[i], 0, this._data[i].length);
      }

      return data;
   }
}
