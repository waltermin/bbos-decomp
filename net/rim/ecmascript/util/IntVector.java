package net.rim.ecmascript.util;

public class IntVector {
   private net.rim.device.api.util.IntVector _v = (net.rim.device.api.util.IntVector)(new Object());

   public int size() {
      return this._v.size();
   }

   public int elementAt(int index) {
      return this._v.elementAt(index);
   }

   public void setElementAt(int obj, int index) {
      this._v.setElementAt(obj, index);
   }

   public void addElement(int obj) {
      this._v.addElement(obj);
   }

   public void removeAllElements() {
      this._v.removeAllElements();
   }

   public int[] toArray() {
      return this._v.toArray();
   }

   public char[] toCharArray() {
      int size = this._v.size();
      char[] ia = new char[size];

      for (int i = 0; i < size; i++) {
         int e = this.elementAt(i);
         ia[i] = (char)e;
      }

      return ia;
   }
}
