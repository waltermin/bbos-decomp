package javax.bluetooth;

import java.util.Vector;
import net.rim.device.api.util.Arrays;

public class DataElement {
   private int _type;
   private Object _value;
   public static final int NULL = 0;
   public static final int U_INT_1 = 8;
   public static final int U_INT_2 = 9;
   public static final int U_INT_4 = 10;
   public static final int U_INT_8 = 11;
   public static final int U_INT_16 = 12;
   public static final int INT_1 = 16;
   public static final int INT_2 = 17;
   public static final int INT_4 = 18;
   public static final int INT_8 = 19;
   public static final int INT_16 = 20;
   public static final int URL = 64;
   public static final int UUID = 24;
   public static final int BOOL = 40;
   public static final int STRING = 32;
   public static final int DATSEQ = 48;
   public static final int DATALT = 56;

   public DataElement(int valueType) {
      this._type = valueType;
      switch (valueType) {
         case 0:
            return;
         case 48:
         case 56:
            this._value = new Vector();
            return;
         default:
            throw new IllegalArgumentException();
      }
   }

   public DataElement(boolean bool) {
      this._type = 40;
      this._value = new Boolean(bool);
   }

   public DataElement(int valueType, long value) {
      long low = 0;
      long high = 0;
      boolean checkValue = true;
      switch (valueType) {
         case 7:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            throw new IllegalArgumentException("Illegal valueType");
         case 8:
         default:
            high = 255;
            break;
         case 9:
            high = 65535;
            break;
         case 10:
            high = 4294967295L;
            break;
         case 16:
            low = -128;
            high = 127;
            break;
         case 17:
            low = -32768;
            high = 32767;
            break;
         case 18:
            low = Integer.MIN_VALUE;
            high = Integer.MAX_VALUE;
            break;
         case 19:
            checkValue = false;
      }

      if (!checkValue || value >= low && value <= high) {
         this._type = valueType;
         this._value = new Long(value);
      } else {
         throw new IllegalArgumentException("Illegal value");
      }
   }

   public DataElement(int valueType, Object value) {
      this.validate(valueType, value);
      this._type = valueType;
      this._value = value;
   }

   public void addElement(DataElement elem) {
      if (elem == null) {
         throw new NullPointerException();
      }

      switch (this._type) {
         case 48:
         case 56:
            Vector v = (Vector)this._value;
            v.addElement(elem);
            return;
         default:
            throw new ClassCastException();
      }
   }

   public void insertElementAt(DataElement elem, int index) {
      if (elem == null) {
         throw new NullPointerException();
      }

      switch (this._type) {
         case 48:
         case 56:
            Vector v = (Vector)this._value;
            v.insertElementAt(elem, index);
            return;
         default:
            throw new ClassCastException();
      }
   }

   public int getSize() {
      switch (this._type) {
         case 48:
         case 56:
            Vector v = (Vector)this._value;
            return v.size();
         default:
            throw new ClassCastException();
      }
   }

   public boolean removeElement(DataElement elem) {
      if (elem == null) {
         throw new NullPointerException();
      }

      switch (this._type) {
         case 48:
         case 56:
            Vector v = (Vector)this._value;
            return v.removeElement(elem);
         default:
            throw new ClassCastException();
      }
   }

   public int getDataType() {
      return this._type;
   }

   public long getLong() {
      switch (this._type) {
         case 7:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            throw new ClassCastException();
         case 8:
         case 9:
         case 10:
         case 16:
         case 17:
         case 18:
         case 19:
         default:
            Long l = (Long)this._value;
            return l;
      }
   }

   public boolean getBoolean() {
      if (this._type == 40) {
         Boolean b = (Boolean)this._value;
         return b;
      } else {
         throw new ClassCastException();
      }
   }

   public Object getValue() {
      switch (this._type) {
         case 11:
         case 12:
         case 20:
            return Arrays.copy((byte[])this._value);
         case 24:
         case 32:
         case 64:
            return this._value;
         case 48:
         case 56:
            Vector v = (Vector)this._value;
            return v.elements();
         default:
            throw new ClassCastException();
      }
   }

   private void validate(int valueType, Object value) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      switch (valueType) {
         case 11:
            this.checkByteArrayLength(value, 8);
            return;
         case 12:
            this.checkByteArrayLength(value, 16);
            return;
         case 20:
            this.checkByteArrayLength(value, 16);
            return;
         case 24:
            if (value instanceof UUID) {
               return;
            }
            break;
         case 32:
         case 64:
            if (value instanceof String) {
               return;
            }
      }

      throw new IllegalArgumentException();
   }

   private void checkByteArrayLength(Object value, int length) {
      if (value instanceof byte[]) {
         byte[] b = (byte[])value;
         if (b.length == length) {
            return;
         }
      }

      throw new IllegalArgumentException();
   }
}
