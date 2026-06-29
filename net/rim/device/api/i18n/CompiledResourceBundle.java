package net.rim.device.api.i18n;

import java.io.UnsupportedEncodingException;
import net.rim.device.api.util.Arrays;

public class CompiledResourceBundle extends ResourceBundle {
   private final long _bundleId;
   private final int[] _ids;
   private final short[] _offsets;
   private final byte[] _data;
   private int _offset;
   private static String[] ENCODINGS = new String[]{"ISO8859_1", "UTF8"};

   protected CompiledResourceBundle(Locale locale, long bundleId, int[] ids, short[] offsets, byte[] data) {
      super(locale);
      this._bundleId = bundleId;
      this._ids = ids;
      this._offsets = offsets;
      this._data = data;
   }

   @Override
   long getId() {
      return this._bundleId;
   }

   @Override
   protected Object handleGetObject(int key) {
      int index = Arrays.binarySearch(this._ids, key);
      Object result = null;
      if (index >= 0) {
         int offset = this._offsets[index] & '\uffff';
         synchronized (this) {
            this._offset = offset;
            return this.read();
         }
      } else {
         return result;
      }
   }

   private Object read() {
      Object result = null;
      byte type = this.readTag();
      switch (type) {
         case 91:
            byte elementType = this.readTag();
            short nelem = this.readLength();
            if (elementType == 115) {
               String[] strings = new String[nelem];

               for (int lv = 0; lv < nelem; lv++) {
                  strings[lv] = this.readPartString();
               }

               result = strings;
            }
         default:
            return result;
         case 115:
            return this.readPartString();
      }
   }

   private short readLength() {
      short length = (short)(this._data[this._offset] << 8 | 0xFF & this._data[this._offset + 1]);
      this._offset += 2;
      return length;
   }

   private String readPartString() {
      short length = this.readLength();
      byte encoding = this._data[this._offset];
      this._offset++;

      String result;
      try {
         result = new String(this._data, this._offset, length, ENCODINGS[encoding]);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e.toString());
      }

      this._offset += length;
      return result;
   }

   private byte readTag() {
      byte tag = this._data[this._offset];
      this._offset++;
      return tag;
   }
}
