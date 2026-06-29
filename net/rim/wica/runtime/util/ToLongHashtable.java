package net.rim.wica.runtime.util;

public class ToLongHashtable {
   private Object[] _key;
   private int[] _hash;
   private long[] _value;
   private Object _empty;
   private int _numberOfKeys;
   private int _threshold;
   private static final int _loadFactor;

   public ToLongHashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new Object();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new Object[initialCapacity];
      this._hash = new int[initialCapacity];
      this._value = new long[initialCapacity];
      this._empty = new Object();
      this._threshold = initialCapacity * 75 / 100;
   }

   public ToLongHashtable() {
      this(10);
   }

   public int size() {
      return this._numberOfKeys;
   }

   public boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public synchronized boolean contains(int value) {
      Object empty = this._empty;
      Object[] keys = this._key;
      long[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (keys[len] != null && keys[len] != empty && values[len] == value) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean containsKey(Object key) {
      int index = find(this._hash, this._key, this._empty, key);
      return this._key[index] != null && this._key[index] != this._empty;
   }

   public synchronized void clear() {
      int len = this._key.length;

      while (--len >= 0) {
         this._key[len] = null;
      }

      this._numberOfKeys = 0;
   }

   public synchronized long remove(Object key) {
      int index = find(this._hash, this._key, this._empty, key);
      if (this._key[index] != null && this._key[index] != this._empty) {
         long result = this._value[index];
         this._key[index] = this._empty;
         this._numberOfKeys--;
         return result;
      } else {
         return 0;
      }
   }

   public synchronized long get(Object key) {
      int index = find(this._hash, this._key, this._empty, key);
      return this._key[index] != null && this._key[index] != this._empty ? this._value[index] : 0;
   }

   protected void rehash() {
      Object[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      long[] values = this._value;
      int[] hashes = this._hash;
      int[] newhash = new int[newlen];
      Object[] newkey = new Object[newlen];
      long[] newvalue = new long[newlen];
      Object empty = this._empty;

      while (--len >= 0) {
         if (keys[len] != null && keys[len] != empty) {
            int index = find(newhash, newkey, empty, keys[len]);
            newhash[index] = hashes[len];
            newkey[index] = keys[len];
            newvalue[index] = values[len];
            keys[len] = null;
         }
      }

      this._key = newkey;
      this._hash = newhash;
      this._value = newvalue;
      this._threshold = newlen * 75 / 100;
   }

   public synchronized long put(Object key, long value) {
      if (this._numberOfKeys + 1 > this._threshold) {
         this.rehash();
      }

      int index = find(this._hash, this._key, this._empty, key);
      long result;
      if (this._key[index] != null && this._key[index] != this._empty) {
         result = this._value[index];
      } else {
         this._numberOfKeys++;
         result = 0;
      }

      this._key[index] = key;
      this._hash[index] = key.hashCode();
      this._value[index] = value;
      return result;
   }

   private static int find(int[] hashes, Object[] keys, Object empty, Object key) {
      int hashcode = key.hashCode();
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = (hashcode << 1) + 1;
      int foundgap = -1;
      int count = 0;

      label38:
      while (true) {
         int i = h1;

         while (keys[i] != null) {
            if (keys[i] != empty) {
               if (hashes[i] == hashcode && keys[i].equals(key)) {
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
               continue label38;
            }
         }

         if (foundgap != -1) {
            return foundgap;
         }

         return i;
      }
   }
}
