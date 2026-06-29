package net.rim.device.api.system;

public class GANConnectivityPreference {
   public static final int GAN_PREFERENCE_WAN_ONLY;
   public static final int GAN_PREFERENCE_WAN_PREFERRED;
   public static final int GAN_PREFERENCE_GAN_ONLY;
   public static final int GAN_PREFERENCE_GAN_PREFERRED;
   public static final long GUID;

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
