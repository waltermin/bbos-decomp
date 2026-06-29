package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.runtime.util.DoubleVector;

public class InnerDoubleVector extends DoubleVector implements BaseInnerVector {
   private DataOwner _owner;
   private long _handle;
   private int _field;

   public void copyFrom(double[] array) {
      this.setSize(array.length);
      System.arraycopy(array, 0, this.getArray(), 0, array.length);
   }

   @Override
   public void setArrayRef(Object array) {
      super._elementData = (double[])array;
      super._elementCount = super._elementData.length;
   }

   @Override
   public void clone(Object v) {
      DoubleVector toClone = (DoubleVector)v;
      int size = toClone.size();
      this.setSize(size);
      System.arraycopy(toClone.getArray(), 0, this.getArray(), 0, size);
   }

   @Override
   public Object clone() {
      InnerDoubleVector cloned = new InnerDoubleVector(this._owner, this._handle, this._field, null);
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

   InnerDoubleVector(DataOwner owner, long handle, int field, double[] defaultValues) {
      this._owner = owner;
      this._handle = handle;
      this._field = field;
      if (defaultValues != null) {
         this.copyFrom(defaultValues);
      }
   }

   @Override
   public void setElementAt(double obj, int index) {
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
   public void insertElementAt(double obj, int index) {
      this._owner.modified(this._handle, this._field);
      super.insertElementAt(obj, index);
   }

   @Override
   public void addElement(double obj) {
      this._owner.modified(this._handle, this._field);
      super.addElement(obj);
   }
}
