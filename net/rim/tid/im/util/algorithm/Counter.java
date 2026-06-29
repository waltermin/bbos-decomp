package net.rim.tid.im.util.algorithm;

import net.rim.vm.Persistable;

public final class Counter implements Persistable {
   private int _value;

   public Counter(int value) {
      this._value = value;
   }

   public final int get() {
      return this._value;
   }

   public final int decrement() {
      return --this._value;
   }

   public final int increment() {
      return ++this._value;
   }

   public final void set(int value) {
      this._value = value;
   }
}
