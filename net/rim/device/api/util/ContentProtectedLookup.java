package net.rim.device.api.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

public class ContentProtectedLookup implements PersistentContentListener, Persistable {
   private Vector _keys;
   private Vector _values;

   public ContentProtectedLookup(int initialCapacity) {
      this._keys = new Vector(initialCapacity);
      this._values = new Vector(initialCapacity);
      PersistentContent.addWeakListener(this);
   }

   public ContentProtectedLookup() {
      this(10);
   }

   public ContentProtectedLookup(Hashtable hashtable) {
      this(hashtable.size());
      Enumeration e = hashtable.keys();

      while (e.hasMoreElements()) {
         Object key = e.nextElement();
         this.put(key, hashtable.get(key));
      }

      PersistentContent.addWeakListener(this);
   }

   public void clear() {
      synchronized (this._keys) {
         this._keys = new Vector();
         this._values = new Vector();
      }
   }

   public int size() {
      synchronized (this._keys) {
         return this._keys.size();
      }
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public int indexOfKey(Object key) {
      return this.indexOfKey(key, 0);
   }

   public int indexOfKey(Object key, int index) {
      synchronized (this._keys) {
         int size = this.size();

         for (int i = index; i < size; i++) {
            Object current = PersistentContent.decode(this._keys.elementAt(i));
            if (current.equals(key)) {
               return i;
            }
         }

         return -1;
      }
   }

   public int indexOfValue(Object value) {
      return this.indexOfValue(value, 0);
   }

   public int indexOfValue(Object value, int index) {
      synchronized (this._keys) {
         int size = this.size();

         for (int i = index; i < size; i++) {
            Object current = PersistentContent.decode(this._values.elementAt(i));
            if (current.equals(value)) {
               return i;
            }
         }

         return -1;
      }
   }

   public boolean containsKey(Object key) {
      return this.indexOfKey(key) >= 0;
   }

   public boolean containsValue(Object value) {
      return this.indexOfValue(value) >= 0;
   }

   public void put(Object key, Object value) {
      synchronized (this._keys) {
         int i = this.indexOfKey(key);
         if (i >= 0) {
            this._values.setElementAt(PersistentContent.encodeObject(value), i);
         } else {
            this._keys.addElement(PersistentContent.encodeObject(key));
            this._values.addElement(PersistentContent.encodeObject(value));
         }
      }
   }

   public Object get(Object key) {
      synchronized (this._keys) {
         return PersistentContent.decode(this._values.elementAt(this.indexOfKey(key)));
      }
   }

   public Object keyAt(int i) {
      synchronized (this._keys) {
         return PersistentContent.decode(this._keys.elementAt(i));
      }
   }

   public Object valueAt(int i) {
      synchronized (this._keys) {
         return PersistentContent.decode(this._values.elementAt(i));
      }
   }

   public void removeKey(Object key) {
      synchronized (this._keys) {
         int i = this.indexOfKey(key);
         if (i >= 0) {
            this.removeAt(i);
         }
      }
   }

   public void removeValue(Object value) {
      synchronized (this._keys) {
         int i = 0;

         while (true) {
            i = this.indexOfValue(value, i);
            if (i < 0) {
               return;
            }

            this.removeAt(i);
         }
      }
   }

   public void removeAt(int i) {
      synchronized (this._keys) {
         this._keys.removeElementAt(i);
         this._values.removeElementAt(i);
      }
   }

   public Enumeration keys() {
      synchronized (this._keys) {
         return new ContentProtectedLookup$MyEnumeration(this._keys.elements());
      }
   }

   public Enumeration values() {
      synchronized (this._keys) {
         return new ContentProtectedLookup$MyEnumeration(this._values.elements());
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      synchronized (this._keys) {
         for (int i = this.size() - 1; i >= 0; i--) {
            this._keys.setElementAt(PersistentContent.reEncode(this._keys.elementAt(i)), i);
            this._values.setElementAt(PersistentContent.reEncode(this._values.elementAt(i)), i);
         }
      }
   }
}
