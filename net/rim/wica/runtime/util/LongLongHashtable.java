package net.rim.wica.runtime.util;

import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.Persistable;

public class LongLongHashtable implements Persistable {
   private long[] _key;
   private long[] _value;
   private byte[] _occupied;
   private int _numberOfKeys;
   private int _threshold;
   static final byte OCCUPIED_NO = 0;
   static final byte OCCUPIED_YES = 1;
   static final byte OCCUPIED_NIL = 2;
   private static final int _loadFactor = 75;

   public LongLongHashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new Object();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new long[initialCapacity];
      this._value = new long[initialCapacity];
      this._occupied = new byte[initialCapacity];
      this._threshold = initialCapacity * 75 / 100;
   }

   public LongLongHashtable() {
      this(10);
   }

   public int size() {
      return this._numberOfKeys;
   }

   public boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public synchronized LongEnumeration keys() {
      return new LongLongHashtableEnumerator(this._key, this._occupied);
   }

   public synchronized LongEnumeration elements() {
      return new LongLongHashtableEnumerator(this._value, this._occupied);
   }

   public synchronized boolean contains(long value) {
      byte[] occupied = this._occupied;
      long[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (occupied[len] == 1 && values[len] == value) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean containsKey(long key) {
      int index = find(this._key, this._occupied, key);
      return this._occupied[index] == 1;
   }

   public synchronized void clear() {
      int len = this._key.length;

      while (--len >= 0) {
         this._occupied[len] = 0;
      }

      this._numberOfKeys = 0;
   }

   public synchronized long remove(long key) {
      int index = find(this._key, this._occupied, key);
      if (this._occupied[index] != 1) {
         return 0;
      }

      long result = this._value[index];
      this._occupied[index] = 2;
      this._numberOfKeys--;
      return result;
   }

   public synchronized boolean removeValue(long value, boolean removeAll) {
      byte[] occupied = this._occupied;
      long[] values = this._value;
      int len = values.length;
      int initialNumKeys = this._numberOfKeys;

      while (--len >= 0) {
         if (occupied[len] == 1 && values[len] == value) {
            this._occupied[len] = 2;
            this._numberOfKeys--;
            if (!removeAll) {
               return true;
            }
         }
      }

      return initialNumKeys - this._numberOfKeys > 0;
   }

   public synchronized long get(long key) {
      int index = find(this._key, this._occupied, key);
      return this._occupied[index] != 1 ? 0 : this._value[index];
   }

   protected void rehash() {
      long[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      long[] values = this._value;
      long[] newkey = new long[newlen];
      long[] newvalue = new long[newlen];
      byte[] occupied = this._occupied;
      byte[] newoccupied = new byte[newlen];

      while (--len >= 0) {
         if (occupied[len] == 1) {
            int index = find(newkey, newoccupied, keys[len]);
            newkey[index] = keys[len];
            newvalue[index] = values[len];
            newoccupied[index] = 1;
         }
      }

      this._key = newkey;
      this._value = newvalue;
      this._occupied = newoccupied;
      this._threshold = newlen * 75 / 100;
   }

   public synchronized long put(long key, long value) {
      if (this._numberOfKeys + 1 > this._threshold) {
         this.rehash();
      }

      int index = find(this._key, this._occupied, key);
      long result;
      if (this._occupied[index] == 1) {
         result = this._value[index];
      } else {
         this._numberOfKeys++;
         result = 0;
      }

      this._occupied[index] = 1;
      this._key[index] = key;
      this._value[index] = value;
      return result;
   }

   private static int find(long[] keys, byte[] occupied, long key) {
      int hashcode = (int)(key ^ key >> 32);
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = (hashcode << 1) + 1;
      int foundgap = -1;
      int count = 0;

      label36:
      while (true) {
         int i = h1;

         while (occupied[i] != 0) {
            if (occupied[i] != 2) {
               if (keys[i] == key) {
                  return i;
               }
            } else if (foundgap == -1) {
               foundgap = i;
            }

            i = (i + h2) % modulus;
            if (i < 0) {
               i += modulus;
            }

            count++;
            if (i == h1) {
               h1 = (h1 + 1) % modulus;
               if (count >= modulus) {
                  return foundgap;
               }
               continue label36;
            }
         }

         if (foundgap != -1) {
            return foundgap;
         }

         return i;
      }
   }
}
