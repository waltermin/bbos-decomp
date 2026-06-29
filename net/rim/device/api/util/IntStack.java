package net.rim.device.api.util;

public class IntStack extends IntVector implements Persistable {
   public IntStack() {
   }

   public IntStack(int initialCapacityInt) {
      super(initialCapacityInt);
   }

   public int push(int valueInt) {
      this.addElement(valueInt);
      return valueInt;
   }

   public int pop() {
      int index = this.size() - 1;
      int result = this.elementAt(index);
      this.removeElementAt(index);
      return result;
   }

   public int peek() {
      int index = this.size() - 1;
      return this.elementAt(index);
   }

   public int search(int valueInt) {
      int index = this.lastIndexOf(valueInt);
      return index >= 0 ? this.size() - index : -1;
   }
}
