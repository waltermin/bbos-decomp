package net.rim.wica.runtime.metadata.internal.component;

import net.rim.device.api.util.IntVector;

class InnerIntVector extends IntVector implements BaseInnerVector {
   private DataOwner _owner;
   private long _handle;
   private int _field;

   public void copyFrom(int[] array) {
      this.setSize(array.length);
      System.arraycopy(array, 0, this.getArray(), 0, array.length);
   }

   @Override
   public Object clone() {
      InnerIntVector cloned = new InnerIntVector(this._owner, this._handle, this._field, null);
      cloned.clone(this);
      return cloned;
   }

   @Override
   public Object getArrayRef() {
      return this.getArray();
   }

   @Override
   public void setArrayRef(Object array) {
      super.elementData = (int[])array;
      super.elementCount = super.elementData.length;
   }

   @Override
   public void clone(Object v) {
      IntVector toClone = (IntVector)v;
      int size = toClone.size();
      this.setSize(size);
      System.arraycopy(toClone.getArray(), 0, this.getArray(), 0, size);
   }

   @Override
   public void trimToSize() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   InnerIntVector(DataOwner owner, long handle, int field, int[] defaultValues) {
      this._owner = owner;
      this._handle = handle;
      this._field = field;
      if (defaultValues != null) {
         this.copyFrom(defaultValues);
      }
   }

   @Override
   public void setElementAt(int obj, int index) {
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
   public void insertElementAt(int obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.insertElementAt(obj, index);
   }

   @Override
   public void addElement(int obj) {
      this._owner.modified(this._handle, this._field);
      super.addElement(obj);
   }
}
