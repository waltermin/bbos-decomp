package net.rim.device.internal.system;

public final class SimulatorServices {
   private SimulatorServices() {
   }

   public static final native Object[] dnsLookup(String var0, boolean var1);

   public static final native String generateEvent(String var0);
}
