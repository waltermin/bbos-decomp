package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.wica.common.metadata.component.DataComponentDef;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.component.CompositeKey;
import net.rim.wica.runtime.metadata.component.KeyDataCollection;

public final class DataKeyHashtable {
   private KeyDataCollection _collection;
   private Wiclet _wiclet;
   private int[] _key;
   private int[] _hash;
   private byte[] _occupied;
   private int _numberOfKeys;
   private int _threshold;
   private IntIntHashtable _keyToIndex;
   private IntHashtable _handleToKeyValue;
   private boolean _isSingleDataKey;
   static final byte OCCUPIED_NO;
   static final byte OCCUPIED_YES;
   static final byte OCCUPIED_NIL;
   private static final int _loadFactor;

   public DataKeyHashtable(Wiclet wiclet, KeyDataCollection c, int initialCapacity) {
      if (initialCapacity < 0) {
         throw new Object();
      }

      if (initialCapacity < 1) {
         initialCapacity = 1;
      }

      this._wiclet = wiclet;
      this._collection = c;
      int[] keyFields = c.getDef().getKeyFields();
      this._isSingleDataKey = keyFields.length == 1 && c.getDef().getFieldType(keyFields[0]) == 6;
      if (this._isSingleDataKey) {
         this._handleToKeyValue = (IntHashtable)(new Object(initialCapacity));
      }

      this._key = new int[initialCapacity];
      this._hash = new int[initialCapacity];
      this._occupied = new byte[initialCapacity];
      this._threshold = initialCapacity * 75 / 100;
      this._keyToIndex = (IntIntHashtable)(new Object(initialCapacity));
   }

   public final int size() {
      return this._numberOfKeys;
   }

   public final boolean isEmpty() {
      return this._numberOfKeys == 0;
   }

   public final synchronized boolean containsKey(Object key) {
      int index = this.find(key, key.hashCode());
      return this._occupied[index] == 1;
   }

   public final synchronized boolean contains(int handle) {
      return this._keyToIndex.contains(handle);
   }

   public final synchronized int remove(int handle) {
      int index = this._keyToIndex.get(handle);
      if (index != -1) {
         int result = this._key[index];
         this._keyToIndex.remove(handle);
         if (this._handleToKeyValue != null) {
            this._handleToKeyValue.remove(handle);
         }

         this._occupied[index] = 2;
         this._numberOfKeys--;
         return result;
      } else {
         return -1;
      }
   }

   public final Object getKey(int handle) {
      if (this._handleToKeyValue != null && this._handleToKeyValue.containsKey(handle)) {
         return this._handleToKeyValue.get(handle);
      }

      DataComponentDef defs = this._collection.getDef();
      int[] keyFields = defs.getKeyFields();
      long dataHandle = (long)defs.getId() << 32 | handle;
      if (keyFields.length == 1) {
         int keyField = defs.getKeyFields()[0];
         if (defs.getFieldType(keyField) == 6) {
            dataHandle = this._collection.getReferenceField(dataHandle, keyField);
            if (dataHandle == -1) {
               throw new Object("Invalid data key");
            }

            KeyDataCollection dc = (KeyDataCollection)this._wiclet.getDataCollection(defs.getFieldReferenceType(keyField));
            return dc.getPKey(dataHandle);
         } else {
            return this._collection.getFieldValueAsObject(dataHandle, keyField);
         }
      } else {
         int numberOfKeys = keyFields.length;
         CompositeKey cKey = new CompositeKey(numberOfKeys);

         for (int i = 0; i < numberOfKeys; i++) {
            cKey.setPart(i, this._collection.getFieldValueAsObject(dataHandle, keyFields[i]));
         }

         return cKey;
      }
   }

   public final synchronized void clear() {
      int len = this._key.length;

      while (--len >= 0) {
         this._occupied[len] = 0;
      }

      this._keyToIndex.clear();
      if (this._handleToKeyValue != null) {
         this._handleToKeyValue.clear();
      }

      this._numberOfKeys = 0;
   }

   public final synchronized int remove(Object key) {
      int index = this.find(key, key.hashCode());
      if (this._occupied[index] != 1) {
         return -1;
      }

      int handle = this._key[index];
      this._keyToIndex.remove(handle);
      if (this._handleToKeyValue != null) {
         this._handleToKeyValue.remove(handle);
      }

      this._occupied[index] = 2;
      this._numberOfKeys--;
      return handle;
   }

   public final synchronized int get(Object key) {
      int index = this.find(key, key.hashCode());
      return this._occupied[index] != 1 ? -1 : this._key[index];
   }

   public final synchronized IntEnumeration elements() {
      return new IntIntHashtableEnumerator(this._key, this._occupied);
   }

   protected final void rehash() {
      int[] keys = this._key;
      int len = keys.length;
      int newlen = (len << 1) + 1;
      int[] hashes = this._hash;
      int[] newhash = new int[newlen];
      int[] newkey = new int[newlen];
      byte[] occupied = this._occupied;
      byte[] newoccupied = new byte[newlen];
      this._key = newkey;
      this._hash = newhash;
      this._occupied = newoccupied;
      this._threshold = newlen * 75 / 100;

      while (--len >= 0) {
         if (occupied[len] == 1) {
            int key = keys[len];
            int hashcode = hashes[len];
            int index = this.find(key, hashcode);
            newkey[index] = key;
            newhash[index] = hashcode;
            newoccupied[index] = 1;
            this._keyToIndex.put(key, index);
         }
      }
   }

   public final synchronized int put(Object key, int handle) {
      if (this._numberOfKeys + 1 > this._threshold) {
         this.rehash();
      }

      int hashcode = key.hashCode();
      int index = this.find(key, hashcode);
      int result;
      if (this._occupied[index] == 1) {
         result = this._key[index];
      } else {
         this._numberOfKeys++;
         result = -1;
      }

      this._occupied[index] = 1;
      this._key[index] = handle;
      this._hash[index] = hashcode;
      this._keyToIndex.put(handle, index);
      if (this._handleToKeyValue != null) {
         this._handleToKeyValue.put(handle, key);
      }

      return result;
   }

   private final int find(Object key, int hashcode) {
      byte[] occupied = this._occupied;
      int[] hashes = this._hash;
      int[] keys = this._key;
      int modulus = keys.length;
      int h1 = (hashcode & 2147483647) % modulus;
      int h2 = (hashcode << 1) + 1;
      int foundgap = -1;
      int count = 0;

      label38:
      while (true) {
         int i = h1;

         while (occupied[i] != 0) {
            if (occupied[i] != 2) {
               if (hashes[i] == hashcode && this.equalKey(keys[i], key)) {
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

   private final int find(int key, int hashcode) {
      byte[] occupied = this._occupied;
      int[] keys = this._key;
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

   private final boolean equalKey(int keyHandle, Object key) {
      if (this._isSingleDataKey) {
         return key.equals(this.getKey(keyHandle));
      }

      if (!(key instanceof CompositeKey)) {
         int var9 = this._collection.getDef().getKeyFields()[0];
         long var10 = (long)this._collection.getDef().getId() << 32 | keyHandle;
         return this._collection.equalsField(var10, var9, key);
      }

      CompositeKey cKey = (CompositeKey)key;
      int[] fields = this._collection.getDef().getKeyFields();
      int numberOfFields = fields.length;
      long dataHandle = (long)this._collection.getDef().getId() << 32 | keyHandle;

      for (int i = 0; i < numberOfFields; i++) {
         if (!this._collection.equalsField(dataHandle, fields[i], cKey.getPart(i))) {
            return false;
         }
      }

      return true;
   }
}
