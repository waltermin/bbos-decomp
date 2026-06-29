package net.rim.device.api.system;

public class WLANSystem {
   protected boolean isWLANAllowed() {
      throw null;
   }

   protected void setWLANOverride(boolean _1) {
      throw null;
   }

   protected boolean isWLANRadioOn() {
      throw null;
   }

   public int getActiveProfileSet() {
      throw null;
   }

   public int isAssociated() {
      int handle = this.getActiveProfileSet();
      return handle != -1 ? this.getActiveProfileId(handle) : -1;
   }

   public WLANNetInfo[] getWLANNetworkInfo(int _1) {
      throw null;
   }

   public WLANNetInfo getWLANNetworkInfo(int _1, int _2) {
      throw null;
   }

   public final int getActiveProfileId(int handle) {
      int ret = getCurrentProfileId(handle);
      return ret >= 0 ? ret : -1;
   }

   public String getActiveProfileSSID() {
      return this.getProfileSSID(this.isAssociated());
   }

   public String getActiveProfileName() {
      return this.getProfileName(this.isAssociated());
   }

   public String getActiveProfileNameOrSSID() {
      throw null;
   }

   public String getProfileSSID(int _1) {
      throw null;
   }

   public String getProfileName(int _1) {
      throw null;
   }

   public boolean isEnterpriseConnectionAvailable() {
      throw null;
   }

   public boolean isBlackberryInfrastructureConnectionAvailable() {
      throw null;
   }

   public boolean isEnterpriseConnectionProvisioned() {
      throw null;
   }

   public boolean isBlackberryInfrastructureConnectionProvisioned() {
      throw null;
   }

   protected final void registerWLANEvents() {
      this.registerWLANEvent(4609);
      this.registerWLANEvent(4610);
      this.registerWLANEvent(4618);
      this.registerWLANEvent(4619);
      this.registerWLANEvent(4620);
      this.registerWLANEvent(4621);
      this.registerWLANEvent(4623);
      this.registerWLANEvent(4622);
   }

   protected final void deregisterWLANEvents() {
      this.deregisterWLANEvent(4609);
      this.deregisterWLANEvent(4610);
      this.deregisterWLANEvent(4618);
      this.deregisterWLANEvent(4619);
      this.deregisterWLANEvent(4620);
      this.deregisterWLANEvent(4621);
      this.deregisterWLANEvent(4623);
      this.deregisterWLANEvent(4622);
   }

   protected final void registerWLANEvent(int event) {
      register(event);
   }

   protected final void deregisterWLANEvent(int event) {
      deregister(event);
   }

   protected final int createWLANProfileSet() {
      return createProfileSet();
   }

   protected final int destroyWLANProfileSet(int handle) {
      return destroyProfileSet(handle);
   }

   protected final int addWLANProfile(int handle, WLANProfile profile, boolean lastProfile) {
      return addProfile(handle, profile, lastProfile);
   }

   protected final int updateWLANProfile(int handle, WLANProfile profile) {
      return updateProfile(handle, profile);
   }

   protected final int flushWLANProfiles(int handle) {
      return flushProfiles(handle);
   }

   protected final int enableWLANProfileScanning(int handle) {
      return enableProfileScanning(handle);
   }

   protected final int disableWLANProfileScanning(int handle) {
      return disableProfileScanning(handle);
   }

   protected final int connectWLANProfile(int handle, int profileID) {
      return connectProfile(handle, profileID);
   }

   protected final int disconnectWLANProfile(int handle, int profileID) {
      return disconnectProfile(handle, profileID);
   }

   private static native int register(int var0);

   private static native int deregister(int var0);

   private static native int createProfileSet();

   private static native int destroyProfileSet(int var0);

   private static native int addProfile(int var0, WLANProfile var1, boolean var2);

   private static native int updateProfile(int var0, WLANProfile var1);

   private static native int flushProfiles(int var0);

   private static native int enableProfileScanning(int var0);

   private static native int disableProfileScanning(int var0);

   private static native int connectProfile(int var0, int var1);

   private static native int disconnectProfile(int var0, int var1);

   private static native int getCurrentProfileId(int var0);
}
