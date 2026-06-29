package java.lang;

public class Object {
   public final native Class getClass();

   public native int hashCode();

   public boolean equals(Object obj) {
      return this == obj;
   }

   public String toString() {
      return this.getClass().getName() + '@' + Integer.toHexString(this.hashCode());
   }

   public final native void notify();

   public final native void notifyAll();

   public final native void wait(long var1);

   public final void wait(long timeout, int nanos) {
      if (timeout >= 0 && nanos >= 0 && nanos <= 999999) {
         if (nanos >= 500000 || nanos != 0 && timeout == 0) {
            timeout += 1;
         }

         this.wait(timeout);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void wait() {
      this.wait(0);
   }
}
