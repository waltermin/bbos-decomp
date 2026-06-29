package java.util;

public class Hashtable {
   private Object[] _key;
   private int[] _hash;
   private Object[] _value;
   private Object _empty;
   private int _numberOfKeys;
   private int _threshold;
   private static final int _loadFactorMul;
   private static final int _loadFactorRShift;

   public Hashtable(int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._key = new Object[initialCapacity];
      this._hash = new int[initialCapacity];
      this._value = new Object[initialCapacity];
      this._empty = new Object();
      this._threshold = initialCapacity * 3 >> 2;
   }

   public Hashtable() {
      this(11);
   }

   public int size() {
      return this._numberOfKeys;
   }

   public boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public synchronized Enumeration keys() {
      return new HashtableEnumerator(this._key, this._empty);
   }

   public synchronized Enumeration elements() {
      return new HashtableEnumerator(this._value, this._empty);
   }

   public synchronized boolean contains(Object value) {
      if (value == null) {
         throw new NullPointerException();
      }

      Object empty = this._empty;
      Object[] keys = this._key;
      Object[] values = this._value;
      int len = values.length;

      while (--len >= 0) {
         if (keys[len] != null && keys[len] != empty && values[len].equals(value)) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean containsKey(Object key) {
      int index = this.find(key, key.hashCode());
      return this._key[index] != null && this._key[index] != this._empty;
   }

   public synchronized void clear() {
      for (int len = this._key.length; --len >= 0; this._key[len] = null) {
         this._value[len] = null;
      }

      this._numberOfKeys = 0;
   }

   @Override
   public synchronized String toString() {
      StringBuffer buf = new StringBuffer().append('{');
      Object empty = this._empty;
      boolean gotone = false;
      int len = this._key.length;

      for (int i = 0; i < len; i++) {
         Object key = this._key[i];
         if (key != null && key != empty) {
            if (gotone) {
               buf.append(", ");
            }

            buf.append(key.toString()).append('=').append(this._value[i].toString());
            gotone = true;
         }
      }

      return buf.append('}').toString();
   }

   public synchronized Object remove(Object key) {
      int index = this.find(key, key.hashCode());
      if (this._key[index] != null && this._key[index] != this._empty) {
         Object result = this._value[index];
         this._numberOfKeys--;
         if (this._numberOfKeys == 0) {
            this._key[index] = null;
            this._value[index] = null;
            return result;
         } else {
            this._key[index] = this._empty;
            this._value[index] = null;
            return result;
         }
      } else {
         return null;
      }
   }

   public synchronized Object get(Object key) {
      int index = this.find(key, key.hashCode());
      return this._value[index];
   }

   protected void rehash() {
      Object[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      Object[] values = this._value;
      int[] hashes = this._hash;
      int[] newhash = new int[newlen];
      Object[] newkey = new Object[newlen];
      Object[] newvalue = new Object[newlen];
      Object empty = this._empty;
      this._key = newkey;
      this._hash = newhash;
      this._value = newvalue;
      this._threshold = newlen * 3 >> 2;

      while (--len >= 0) {
         if (keys[len] != null && keys[len] != empty) {
            Object key = keys[len];
            int hashcode = hashes[len];
            int index = this.find(key, hashcode);
            newhash[index] = hashcode;
            newkey[index] = key;
            newvalue[index] = values[len];
            values[len] = null;
            keys[len] = null;
         }
      }
   }

   public synchronized Object put(Object key, Object value) {
      if (value == null) {
         throw new NullPointerException();
      }

      int hashcode = key.hashCode();
      int index = this.find(key, hashcode);
      if (this._key[index] == null || this._key[index] == this._empty) {
         this._numberOfKeys++;
      }

      if (this._numberOfKeys > this._threshold) {
         this.rehash();
         index = this.find(key, hashcode);
      }

      Object result = this._value[index];
      this._key[index] = key;
      this._hash[index] = hashcode;
      this._value[index] = value;
      return result;
   }

   private int find(Object key, int hashcode) {
      Object empty = this._empty;
      int[] hashes = this._hash;
      Object[] keys = this._key;
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = modulus;
      int foundgap = -1;
      int count = 0;

      label55:
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
               continue label55;
            }
         }

         if (foundgap != -1) {
            return foundgap;
         }

         return i;
      }
   }
}
