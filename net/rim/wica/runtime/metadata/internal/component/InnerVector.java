package net.rim.wica.runtime.metadata.internal.component;

import java.util.Vector;

public class InnerVector extends Vector implements BaseInnerVector {
   private DataOwner _owner;
   private long _handle;
   private int _field;

   public Object[] getArray() {
      return super.elementData;
   }

   public void copyFrom(Object[] array) {
      this.setSize(array.length);
      System.arraycopy(array, 0, this.getArray(), 0, array.length);
   }

   @Override
   public void clone(Object v) {
      Vector toClone = (Vector)v;
      int size = toClone.size();
      this.setSize(size);
      toClone.copyInto(this.getArray());
   }

   @Override
   public Object clone() {
      InnerVector cloned = new InnerVector(this._owner, this._handle, this._field, null);
      cloned.clone(this);
      return cloned;
   }

   @Override
   public Object getArrayRef() {
      return this.getArray();
   }

   @Override
   public void setArrayRef(Object array) {
      super.elementData = (Object[])array;
      super.elementCount = super.elementData.length;
   }

   @Override
   public void trimToSize() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public void setElementAt(Object obj, int index) {
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
   public void insertElementAt(Object obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.insertElementAt(obj, index);
   }

   @Override
   public void addElement(Object obj) {
      this._owner.modified(this._handle, this._field);
      super.addElement(obj);
   }

   InnerVector(DataOwner owner, long handle, int field, Object[] defaultValues) {
      this._owner = owner;
      this._handle = handle;
      this._field = field;
      if (defaultValues != null) {
         this.copyFrom(defaultValues);
      }
   }
}
