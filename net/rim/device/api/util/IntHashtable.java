package net.rim.device.api.util;

import java.util.Enumeration;

public class IntHashtable implements Persistable {
   private int[] _key;
   private Object[] _value;
   private Object _empty;
   private int _numberOfKeys;
   private int _threshold;
   private static final int _loadFactorMul = 3;
   private static final int _loadFactorRShift = 2;

   public IntHashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new int[initialCapacity];
      this._value = new Object[initialCapacity];
      this._empty = new Object();
      this._threshold = initialCapacity * 3 >> 2;
   }

   public IntHashtable() {
      this(11);
   }

   public int size() {
      return this._numberOfKeys;
   }

   public boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public synchronized IntEnumeration keys() {
      return new HashtableIntEnumerator(this._key, this._value, this._empty);
   }

   public synchronized Enumeration elements() {
      return new HashtableObjectEnumerator(this._value, this._empty);
   }

   public synchronized boolean contains(Object value) {
      Object empty = this._empty;
      Object[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (values[len] != null && values[len] != empty && values[len].equals(value)) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean containsKey(int key) {
      int index = this.find(key);
      return this._value[index] != null && this._value[index] != this._empty;
   }

   public synchronized void removeValue(Object value) {
      Object[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (values[len] == value) {
            values[len] = this._empty;
            this._numberOfKeys--;
         }
      }
   }

   public synchronized void clear() {
      int len = this._key.length;

      while (--len >= 0) {
         this._value[len] = null;
      }

      this._numberOfKeys = 0;
   }

   public synchronized int keysToArray(int[] array) {
      int index = 0;
      int count = this._value.length;
      Object empty = this._empty;

      for (int i = 0; i < count; i++) {
         Object value = this._value[i];
         if (value != null && value != empty) {
            array[index++] = this._key[i];
         }
      }

      return this._numberOfKeys;
   }

   public synchronized Object remove(int key) {
      int index = this.find(key);
      if (this._value[index] != null && this._value[index] != this._empty) {
         Object result = this._value[index];
         this._numberOfKeys--;
         if (this._numberOfKeys == 0) {
            this._value[index] = null;
            return result;
         } else {
            this._value[index] = this._empty;
            return result;
         }
      } else {
         return null;
      }
   }

   public synchronized Object get(int key) {
      int index = this.find(key);
      return this._value[index] == this._empty ? null : this._value[index];
   }

   protected void rehash() {
      int[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      Object[] values = this._value;
      int[] newkey = new int[newlen];
      Object[] newvalue = new Object[newlen];
      Object empty = this._empty;
      this._key = newkey;
      this._value = newvalue;
      this._threshold = newlen * 3 >> 2;

      while (--len >= 0) {
         if (values[len] != null && values[len] != empty) {
            int key = keys[len];
            int index = this.find(key);
            newkey[index] = key;
            newvalue[index] = values[len];
            values[len] = null;
         }
      }
   }

   public synchronized Object put(int key, Object value) {
      if (value == null) {
         throw new NullPointerException();
      }

      if (this._numberOfKeys + 1 > this._threshold) {
         this.rehash();
      }

      int index = this.find(key);
      Object result;
      if (this._value[index] != null && this._value[index] != this._empty) {
         result = this._value[index];
      } else {
         this._numberOfKeys++;
         result = null;
      }

      this._key[index] = key;
      this._value[index] = value;
      return result;
   }

   private int find(int key) {
      Object empty = this._empty;
      int[] keys = this._key;
      Object[] values = this._value;
      int hashcode = key;
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = modulus;
      int foundgap = -1;
      int count = 0;

      label52:
      while (true) {
         int i = h1;

         while (values[i] != null) {
            if (values[i] != empty) {
               if (keys[i] == key) {
                  return i;
               }
            } else if (foundgap == -1) {
               foundgap = i;
            }

            i += h2;
            if (i >= modulus) {
               if (h2 >= modulus) {
                  if (modulus > 3) {
                     h2 = (hashcode >>> 1) % (modulus - 1) + 1;
                  } else {
                     h2 = (hashcode >>> 1) % modulus;
                  }

                  i = i - modulus + h2;
                  if (i < modulus) {
                     i += modulus;
                  }
               }

               i -= modulus;
            }

            count++;
            if (i == h1) {
               if (++h1 >= modulus) {
                  h1 = 0;
               }

               if (count >= modulus) {
                  return foundgap;
               }
               continue label52;
            }
         }

         if (foundgap != -1) {
            return foundgap;
         }

         return i;
      }
   }
}
