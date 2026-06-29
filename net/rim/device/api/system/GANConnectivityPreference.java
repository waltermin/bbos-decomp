package net.rim.device.api.system;

public class GANConnectivityPreference {
   public static final int GAN_PREFERENCE_WAN_ONLY = 0;
   public static final int GAN_PREFERENCE_WAN_PREFERRED = 1;
   public static final int GAN_PREFERENCE_GAN_ONLY = 2;
   public static final int GAN_PREFERENCE_GAN_PREFERRED = 3;
   public static final long GUID = 6614774638737238176L;

   public static GANConnectivityPreference getInstance() {
      return (GANConnectivityPreference)ApplicationRegistry.getApplicationRegistry().waitFor(6614774638737238176L);
   }

   public void setPreference(int _1) {
      throw null;
   }

   public int getPreference() {
      throw null;
   }

   public int getSetPreference() {
      throw null;
   }

   public int[] getOptions() {
      throw null;
   }

   public boolean getEditable() {
      throw null;
   }
}
