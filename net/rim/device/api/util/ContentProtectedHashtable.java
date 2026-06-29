package net.rim.device.api.util;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

public class ContentProtectedHashtable extends Hashtable implements Persistable, PersistentContentListener {
   private boolean _protected;

   public ContentProtectedHashtable(boolean protect) {
      if (protect) {
         this.reCrypt();
      }
   }

   public ContentProtectedHashtable() {
      this(true);
   }

   public ContentProtectedHashtable(int initialCapacity) {
      this(initialCapacity, true);
   }

   public ContentProtectedHashtable(int initialCapacity, boolean protect) {
      super(initialCapacity);
      if (protect) {
         this.reCrypt();
      }
   }

   public ContentProtectedHashtable(Hashtable hashtable, boolean protect) {
      if (hashtable == null) {
         throw new IllegalArgumentException();
      }

      Enumeration enumeration = hashtable.keys();

      while (enumeration.hasMoreElements()) {
         Object key = enumeration.nextElement();
         this.put(key, hashtable.get(key));
      }

      if (protect) {
         this.reCrypt();
      }
   }

   @Override
   public synchronized boolean contains(Object value) {
      if (!this._protected) {
         return super.contains(value);
      }

      if (value == null) {
         throw new NullPointerException();
      }

      Enumeration enumeration = super.elements();

      while (enumeration.hasMoreElements()) {
         Object obj = PersistentContent.decode(enumeration.nextElement());
         if (obj.equals(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public synchronized Enumeration elements() {
      Enumeration enumeration = super.elements();
      if (this._protected) {
         enumeration = new ContentProtectedEnumeration(enumeration);
      }

      return enumeration;
   }

   @Override
   public synchronized Object get(Object key) {
      Object value = super.get(key);
      if (this._protected) {
         value = PersistentContent.decode(value);
      }

      return value;
   }

   @Override
   public synchronized Object put(Object key, Object value) {
      if (this._protected) {
         value = PersistentContent.encodeObject(value);
      }

      Object returnValue = super.put(key, value);
      if (this._protected) {
         returnValue = PersistentContent.decode(returnValue);
      }

      return returnValue;
   }

   public synchronized boolean isProtected() {
      return this._protected;
   }

   public synchronized boolean checkCrypt() {
      if (!this._protected) {
         return false;
      }

      Enumeration enumeration = this.keys();

      while (enumeration.hasMoreElements()) {
         Object key = enumeration.nextElement();
         if (!PersistentContent.checkEncoding(super.get(key))) {
            return false;
         }
      }

      return true;
   }

   public synchronized void reCrypt() {
      if (!this._protected) {
         this.transitionToProtected();
      } else {
         this.reCrypt2();
      }
   }

   private void transitionToProtected() {
      this._protected = true;
      Enumeration enumeration = this.keys();

      while (enumeration.hasMoreElements()) {
         Object key = enumeration.nextElement();
         super.put(key, PersistentContent.encodeObject(super.get(key)));
      }

      PersistentContent.addWeakListener(this);
   }

   private void reCrypt2() {
      Enumeration enumeration = this.keys();

      while (enumeration.hasMoreElements()) {
         Object key = enumeration.nextElement();
         super.put(key, PersistentContent.reEncode(super.get(key)));
      }
   }

   @Override
   public synchronized void persistentContentModeChanged(int generation) {
      this.reCrypt2();
   }

   @Override
   public synchronized void persistentContentStateChanged(int state) {
   }
}
