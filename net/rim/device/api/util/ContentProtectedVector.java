package net.rim.device.api.util;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

public class ContentProtectedVector extends Vector implements Persistable, PersistentContentListener {
   boolean _protected;

   public ContentProtectedVector(int initialCapacity, int capacityIncrement, boolean protect) {
      super(initialCapacity, capacityIncrement);
      if (protect) {
         this.reCrypt();
      }
   }

   public ContentProtectedVector(int initialCapacity, int capacityIncrement) {
      super(initialCapacity, capacityIncrement);
      this.reCrypt();
   }

   public ContentProtectedVector(int initialCapacity) {
      super(initialCapacity);
      this.reCrypt();
   }

   public ContentProtectedVector(boolean protect) {
      if (protect) {
         this.reCrypt();
      }
   }

   public ContentProtectedVector() {
      this(true);
   }

   @Override
   public synchronized void copyInto(Object[] anArray) {
      super.copyInto(anArray);
      if (this._protected) {
         for (int i = super.elementCount - 1; i >= 0; i--) {
            anArray[i] = PersistentContent.decode(anArray[i]);
         }
      }
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
   public synchronized int indexOf(Object elem, int index) {
      if (!this._protected) {
         return super.indexOf(elem, index);
      }

      if (elem == null) {
         for (int i = index; i < super.elementCount; i++) {
            if (super.elementData[i] == null) {
               return i;
            }
         }
      } else {
         for (int i = index; i < super.elementCount; i++) {
            if (elem.equals(PersistentContent.decode(super.elementData[i]))) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public synchronized int lastIndexOf(Object elem, int index) {
      if (!this._protected) {
         return super.lastIndexOf(elem, index);
      }

      if (index >= super.elementCount) {
         throw new ArrayIndexOutOfBoundsException(index + " >= " + super.elementCount);
      }

      if (elem == null) {
         for (int i = index; i >= 0; i--) {
            if (super.elementData[i] == null) {
               return i;
            }
         }
      } else {
         for (int i = index; i >= 0; i--) {
            if (elem.equals(PersistentContent.decode(super.elementData[i]))) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public synchronized Object elementAt(int index) {
      return this._protected ? PersistentContent.decode(super.elementAt(index)) : super.elementAt(index);
   }

   @Override
   public synchronized Object firstElement() {
      return this._protected ? PersistentContent.decode(super.firstElement()) : super.firstElement();
   }

   @Override
   public synchronized Object lastElement() {
      return this._protected ? PersistentContent.decode(super.lastElement()) : super.lastElement();
   }

   @Override
   public synchronized void setElementAt(Object obj, int index) {
      if (this._protected) {
         obj = PersistentContent.encodeObject(obj);
      }

      super.setElementAt(obj, index);
   }

   @Override
   public synchronized void insertElementAt(Object obj, int index) {
      if (this._protected) {
         obj = PersistentContent.encodeObject(obj);
      }

      super.insertElementAt(obj, index);
   }

   @Override
   public synchronized void addElement(Object obj) {
      if (this._protected) {
         obj = PersistentContent.encodeObject(obj);
      }

      super.addElement(obj);
   }

   public synchronized boolean isProtected() {
      return this._protected;
   }

   public synchronized boolean checkCrypt() {
      if (!this._protected) {
         return false;
      }

      for (int i = 0; i < super.elementCount; i++) {
         if (!PersistentContent.checkEncoding(super.elementData[i])) {
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

      for (int i = 0; i < super.elementCount; i++) {
         super.elementData[i] = PersistentContent.encodeObject(super.elementData[i]);
      }

      PersistentContent.addWeakListener(this);
   }

   private void reCrypt2() {
      for (int i = 0; i < super.elementCount; i++) {
         super.elementData[i] = PersistentContent.reEncode(super.elementData[i]);
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
