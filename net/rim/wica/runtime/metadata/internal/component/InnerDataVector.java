package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.util.LongVector;

public class InnerDataVector extends LongVector implements BaseInnerVector {
   private DataOwner _owner;
   private long _handle;
   private int _field;
   private DataCollection _typeHandler;
   private boolean _persistable;

   public void setTypeHandler(DataCollection typeHandler) {
      this._typeHandler = typeHandler;
   }

   public DataCollection getTypeHandler() {
      return this._typeHandler;
   }

   public long getHandle() {
      return this._handle;
   }

   public void clone(Object v, boolean deep) {
      this._owner.modified(this._handle, this._field);
      int originalSize = this.size();
      LongVector vector = (LongVector)v;
      int size = vector.size();
      this.ensureCapacity(originalSize + size);

      for (int i = 0; i < size; i++) {
         long obj;
         if (deep) {
            obj = this._typeHandler.create();
            this._typeHandler.copyFields(obj, vector.elementAt(i), true);
         } else {
            obj = vector.elementAt(i);
         }

         this.addElement(obj);
      }

      this.removeElements(0, originalSize);
   }

   public int getField() {
      return this._field;
   }

   public void copyFrom(long[] array) {
      this.ensureCapacity(array.length);

      for (int i = 0; i < array.length; i++) {
         this.addElement(array[i]);
      }
   }

   public long elementAtAsIs(int index) {
      return super.elementAt(index);
   }

   @Override
   public Object clone() {
      InnerDataVector cloned = new InnerDataVector(this._owner, this._handle, this._field, this._typeHandler, this._persistable);
      cloned.clone(this);
      return cloned;
   }

   @Override
   public void clone(Object v) {
      this.clone(v, false);
   }

   @Override
   public void setArrayRef(Object array) {
      super._elementData = (long[])array;
      super._elementCount = super._elementData.length;
   }

   @Override
   public Object getArrayRef() {
      return this.getArray();
   }

   @Override
   public void removeElements(int startIndex, int count) {
      this._owner.modified(this._handle, this._field);

      for (int i = startIndex + count - 1; i >= startIndex; i--) {
         this._typeHandler.removeReference(this.elementAt(i), this._persistable);
      }

      super.removeElements(startIndex, count);
   }

   @Override
   public void insertElementAt(long obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.insertElementAt(obj, index);
      this._typeHandler.addReference(obj, this._persistable);
   }

   @Override
   public void addElement(long obj, boolean clone) {
      this._owner.modified(this._handle, this._field);
      this._typeHandler.addReference(obj, this._persistable);
      super.addElement(obj, clone);
   }

   @Override
   public long elementAt(int index) {
      long handle = super.elementAt(index);
      if (handle != -1 && !this._typeHandler.contains(handle)) {
         super.setElementAt(-1, index);
         return -1;
      } else {
         return handle;
      }
   }

   @Override
   public void removeAllElements() {
      this._owner.modified(this._handle, this._field);

      for (int i = this.size() - 1; i >= 0; i--) {
         this._typeHandler.removeReference(this.elementAt(i), this._persistable);
      }

      super.removeAllElements();
   }

   @Override
   public void setElementAt(long obj, int index) {
      long before = this.elementAt(index);
      if (before != obj) {
         this._owner.modified(this._handle, this._field);
         this._typeHandler.addReference(obj, this._persistable);
         this._typeHandler.removeReference(before, this._persistable);
         super.setElementAt(obj, index);
      }
   }

   @Override
   public void trimToSize() {
      super.trimToSize();
   }

   @Override
   public void removeElementAt(int index) {
      this._owner.modified(this._handle, this._field);
      this._typeHandler.removeReference(this.elementAt(index), this._persistable);
      super.removeElementAt(index);
   }

   InnerDataVector(DataOwner owner, long handle, int field, DataCollection typeHandler, boolean persistable) {
      this._owner = owner;
      this._handle = handle;
      this._field = field;
      this._persistable = persistable;
      this._typeHandler = typeHandler;
   }
}
