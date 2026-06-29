package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.runtime.util.LongVector;

class InnerLongVector extends LongVector implements BaseInnerVector {
   private DataOwner _owner;
   private long _handle;
   private int _field;

   public void copyFrom(long[] array) {
      this.setSize(array.length);
      System.arraycopy(array, 0, this.getArray(), 0, array.length);
   }

   @Override
   public void setArrayRef(Object array) {
      super._elementData = (long[])array;
      super._elementCount = super._elementData.length;
   }

   @Override
   public void clone(Object v) {
      LongVector toClone = (LongVector)v;
      int size = toClone.size();
      this.setSize(size);
      System.arraycopy(toClone.getArray(), 0, this.getArray(), 0, size);
   }

   @Override
   public Object clone() {
      InnerLongVector cloned = new InnerLongVector(this._owner, this._handle, this._field, null);
      cloned.clone(this);
      return cloned;
   }

   @Override
   public Object getArrayRef() {
      return this.getArray();
   }

   @Override
   public void trimToSize() {
      super.trimToSize();
   }

   InnerLongVector(DataOwner owner, long handle, int field, long[] defaultValues) {
      this._owner = owner;
      this._handle = handle;
      this._field = field;
      if (defaultValues != null) {
         this.copyFrom(defaultValues);
      }
   }

   @Override
   public void setElementAt(long obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.setElementAt(obj, index);
   }

   @Override
   public void removeAllElements() {
      this._owner.modified(this._handle, this._field);
      super.removeAllElements();
   }

   @Override
   public void removeElementAt(int index) {
      this._owner.modified(this._handle, this._field);
      super.removeElementAt(index);
   }

   @Override
   public void insertElementAt(long obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.insertElementAt(obj, index);
   }

   @Override
   public void addElement(long obj) {
      this._owner.modified(this._handle, this._field);
      super.addElement(obj);
   }
}
