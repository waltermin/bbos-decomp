package net.rim.vm;

public final class Monitor {
   private Monitor() {
   }

   public static final native boolean monitorOwned(Object var0);
}
