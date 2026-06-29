package java.lang;

import net.rim.device.api.crypto.MIDletSecurityCrypto;
import net.rim.vm.DebugSupport;
import net.rim.vm.Memory;

public class Thread implements Runnable {
   private int priority;
   private int flags;
   private Runnable target;
   private Object threadSpecificData;
   private String name;
   public static final int MIN_PRIORITY;
   public static final int NORM_PRIORITY;
   public static final int MAX_PRIORITY;

   public synchronized void start() {
      this.start0();
   }

   public final void join() {
      while (this.isAlive()) {
         sleep(0);
      }
   }

   public void interrupt() {
      this.interrupt0();
   }

   public final native boolean isAlive();

   public final void setPriority(int newPriority) {
      if (newPriority <= 10 && newPriority >= 1) {
         if (newPriority > 5 && !DebugSupport.isDesktopVM()) {
            int status = MIDletSecurityCrypto.verifyMIDletTrailer(null);
            switch (status) {
               case 0:
               case 3:
                  break;
               default:
                  newPriority = 5;
            }
         }

         this.setPriority0(this.priority = newPriority);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getPriority() {
      return this.priority;
   }

   public final String getName() {
      if (this.name == null) {
         this.name = "Thread-" + Memory.objectToInt(this);
      }

      return this.name;
   }

   @Override
   public void run() {
      if (this.target != null) {
         this.target.run();
      }
   }

   public static native Thread currentThread();

   public Thread(String name) {
      this.init(null, name, true);
   }

   public static native void sleep(long var0);

   private void init(Runnable target, String name, boolean explicit) {
      if (explicit && name == null) {
         throw new NullPointerException();
      }

      this.name = name;
      this.target = target;
      Thread parent = currentThread();
      this.priority = parent != null ? parent.getPriority() : 5;
      this.setPriority0(this.priority);
   }

   public Thread() {
      this.init(null, null, false);
   }

   public Thread(Runnable target) {
      this.init(target, null, false);
   }

   public Thread(Runnable target, String name) {
      this.init(target, name, true);
   }

   public static native int activeCount();

   public static native void yield();

   @Override
   public String toString() {
      return "Thread[" + this.getName() + ',' + this.getPriority() + ']';
   }

   private native void setPriority0(int var1);

   private native void interrupt0();

   private native void start0();
}
