package net.rim.vm;

public class WeakReference {
   private Object reference;
   private Object next;

   private native void register();

   public WeakReference(Object object) {
      this.reference = object;
      this.register();
   }

   public Object get() {
      return this.reference;
   }

   public void set(Object object) {
      this.reference = object;
   }
}
