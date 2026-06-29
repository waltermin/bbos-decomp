package net.rim.device.internal.system;

import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.util.Persistable;

public final class ApplicationRegistryHashtable implements Persistable {
   private long[] _key;
   private Object[] _value;
   private boolean[] _protect;
   private Object _empty;
   private int _numberOfKeys;
   private int _threshold;
   private static final int _loadFactorMul;
   private static final int _loadFactorRShift;

   public ApplicationRegistryHashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new long[initialCapacity];
      this._value = new Object[initialCapacity];
      this._protect = new boolean[initialCapacity];
      this._empty = new Object();
      this._threshold = initialCapacity * 3 >> 2;
   }

   public final synchronized Object remove(long key, boolean protect) {
      int index = this.find(key);
      if (this._value[index] != null && this._value[index] != this._empty) {
         Object result = this._value[index];
         if (!protect && this._protect[index]) {
            throw new ControlledAccessException();
         }

         this._numberOfKeys--;
         if (this._numberOfKeys == 0) {
            this._value[index] = null;
         } else {
            this._value[index] = this._empty;
         }

         this._protect[index] = false;
         return result;
      } else {
         return null;
      }
   }

   public final synchronized Object get(long key, boolean protect) {
      int index = this.find(key);
      if (this._value[index] == this._empty) {
         return null;
      } else if (!protect && this._protect[index]) {
         throw new ControlledAccessException();
      } else {
         return this._value[index];
      }
   }

   protected final void rehash() {
      long[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      Object[] values = this._value;
      boolean[] protects = this._protect;
      long[] newkey = new long[newlen];
      Object[] newvalue = new Object[newlen];
      boolean[] newprotect = new boolean[newlen];
      Object empty = this._empty;
      this._key = newkey;
      this._value = newvalue;
      this._protect = newprotect;
      this._threshold = newlen * 3 >> 2;

      while (--len >= 0) {
         if (values[len] != null && values[len] != empty) {
            long key = keys[len];
            int index = this.find(key);
            newkey[index] = key;
            newvalue[index] = values[len];
            newprotect[index] = protects[len];
            values[len] = null;
            protects[len] = false;
         }
      }
   }

   public final synchronized Object put(long key, Object value, boolean protect) {
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
      this._protect[index] = protect;
      return result;
   }

   private final int find(long key) {
      Object empty = this._empty;
      long[] keys = this._key;
      Object[] values = this._value;
      int hashcode = (int)(key ^ key >> 32);
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
