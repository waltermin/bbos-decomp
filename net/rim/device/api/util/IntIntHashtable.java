package net.rim.device.api.util;

public class IntIntHashtable implements Persistable {
   private int[] _key;
   private int[] _value;
   private byte[] _occupied;
   private int _numberOfKeys;
   private int _threshold;
   static final byte OCCUPIED_NO;
   static final byte OCCUPIED_YES;
   static final byte OCCUPIED_NIL;
   private static final int _loadFactorMul;
   private static final int _loadFactorRShift;

   public IntIntHashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new int[initialCapacity];
      this._value = new int[initialCapacity];
      this._occupied = new byte[initialCapacity];
      this._threshold = initialCapacity * 3 >> 2;
   }

   public IntIntHashtable() {
      this(11);
   }

   public int size() {
      return this._numberOfKeys;
   }

   public boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public synchronized IntEnumeration keys() {
      return new IntIntHashtableEnumerator(this._key, this._occupied);
   }

   public synchronized IntEnumeration elements() {
      return new IntIntHashtableEnumerator(this._value, this._occupied);
   }

   public synchronized boolean contains(int value) {
      byte[] occupied = this._occupied;
      int[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (occupied[len] == 1 && values[len] == value) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean containsKey(int key) {
      int index = this.find(key);
      return this._occupied[index] == 1;
   }

   public synchronized void clear() {
      int len = this._key.length;

      while (--len >= 0) {
         this._occupied[len] = 0;
      }

      this._numberOfKeys = 0;
   }

   public synchronized int remove(int key) {
      int index = this.find(key);
      if (this._occupied[index] != 1) {
         return -1;
      } else {
         int result = this._value[index];
         this._numberOfKeys--;
         if (this._numberOfKeys == 0) {
            this._occupied[index] = 0;
            return result;
         } else {
            this._occupied[index] = 2;
            return result;
         }
      }
   }

   public synchronized int get(int key) {
      int index = this.find(key);
      return this._occupied[index] != 1 ? -1 : this._value[index];
   }

   protected void rehash() {
      int[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      int[] values = this._value;
      int[] newkey = new int[newlen];
      int[] newvalue = new int[newlen];
      byte[] occupied = this._occupied;
      byte[] newoccupied = new byte[newlen];
      this._key = newkey;
      this._value = newvalue;
      this._occupied = newoccupied;
      this._threshold = newlen * 3 >> 2;

      while (--len >= 0) {
         if (occupied[len] == 1) {
            int key = keys[len];
            int index = this.find(key);
            newkey[index] = key;
            newvalue[index] = values[len];
            newoccupied[index] = 1;
         }
      }
   }

   public synchronized int put(int key, int value) {
      if (this._numberOfKeys + 1 > this._threshold) {
         this.rehash();
      }

      int index = this.find(key);
      int result;
      if (this._occupied[index] == 1) {
         result = this._value[index];
      } else {
         this._numberOfKeys++;
         result = -1;
      }

      this._occupied[index] = 1;
      this._key[index] = key;
      this._value[index] = value;
      return result;
   }

   private int find(int key) {
      byte[] occupied = this._occupied;
      int[] keys = this._key;
      int hashcode = key;
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = modulus;
      int foundgap = -1;
      int count = 0;

      label52:
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
