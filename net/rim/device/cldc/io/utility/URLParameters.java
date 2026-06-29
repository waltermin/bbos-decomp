package net.rim.device.cldc.io.utility;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;

public class URLParameters {
   private Vector _keys = new Vector();
   private Vector _values = new Vector();

   public boolean containParameter(String key) {
      int index = this.searchForIndex(key);
      return index >= 0;
   }

   public Vector getKeys() {
      return this._keys;
   }

   public Vector getValues() {
      return this._values;
   }

   public String getValue(String key) {
      String value = null;
      int index = this.searchForIndex(key);
      if (index >= 0) {
         value = (String)this._values.elementAt(index);
      }

      return value;
   }

   public String remove(String key) {
      String value = null;
      int index = this.searchForIndex(key);
      if (index >= 0) {
         value = (String)this._values.elementAt(index);
         this._keys.removeElementAt(index);
         this._values.removeElementAt(index);
      }

      return value;
   }

   private int searchForIndex(String key) {
      int vectorSize = this._keys.size();
      int position = 0;

      while (position < vectorSize && !StringUtilities.strEqualIgnoreCase(key, (String)this._keys.elementAt(position), 1701707776)) {
         position++;
      }

      return position < vectorSize ? position : -1;
   }

   public void setParameter(String name, String value) {
      if (name != null) {
         this._keys.addElement(name.trim());
         this._values.addElement(value == null ? "" : value.trim());
      }
   }

   @Override
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      int size = this._keys.size();

      for (int i = 0; i < size; i++) {
         buffer.append(';').append(this._keys.elementAt(i)).append('=').append(this._values.elementAt(i));
      }

      return buffer.toString();
   }
}
