package net.rim.device.internal.crypto.vpn;

public class VPNSystem {
   public synchronized int connect(int policyId) {
      return this.connectVPN(this.getActiveProfileSet(), policyId);
   }

   public int disconnect() {
      return this.disconnectVPN(this.getActiveProfileSet());
   }

   public int abort() {
      return this.abortVPN(this.getActiveProfileSet());
   }

   public void createSession() {
      if (VPN.isSupported()) {
         this.setActiveProfileSet(this.createVPNProfileSet());
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void closeSession() {
      this.destroyVPNProfileSet(this.getActiveProfileSet());
   }

   public void resetSession() {
      throw null;
   }

   public int checkSessionStatus() {
      return this.getVPNStatus(this.getActiveProfileSet());
   }

   protected void checkSession() {
      if (this.getActiveProfileSet() == -1) {
         throw new IllegalArgumentException();
      }
   }

   public boolean isVPNAllowed() {
      throw null;
   }

   public int getActiveProfileSet() {
      throw null;
   }

   protected int setActiveProfileSet(int _1) {
      throw null;
   }

   public int getActiveProfileId() {
      throw null;
   }

   public boolean isIPSecRequiredForNetwork(String _1, int _2) {
      throw null;
   }

   public boolean isConnected() {
      throw null;
   }

   public boolean livenessCheckEnabled() {
      throw null;
   }

   public VPNPolicy allocatePolicy() {
      throw null;
   }

   protected VPNProfile createVPNProfile(VPNPolicy _1) {
      throw null;
   }

   public int addPolicy(VPNPolicy policy) {
      return this.addVPNProfile(this.createVPNProfile(policy));
   }

   public void removePolicy(int policyId) {
      this.removeVPNProfile(policyId);
   }

   public String getChallenge(int[] flags) {
      this.checkSession();
      return VPN.getChallenge(this.getActiveProfileSet(), flags);
   }

   public int setResponse(String username, String password, int flags) {
      this.checkSession();
      return VPN.setResponse(this.getActiveProfileSet(), username, password, flags);
   }

   public int getSessionLifetime() {
      this.checkSession();
      return VPN.getSessionLifetime(this.getActiveProfileSet());
   }

   public String getBanner() {
      this.checkSession();
      return VPN.getBanner(this.getActiveProfileSet());
   }

   public byte[] getCertificate() {
      this.checkSession();
      return VPN.getCertificate(this.getActiveProfileSet());
   }

   public int acceptCertificate(boolean accept) {
      this.checkSession();
      return VPN.acceptCertificate(this.getActiveProfileSet(), accept);
   }

   protected final int createVPNProfileSet() {
      if (this.getActiveProfileSet() == -1) {
         return createProfileSet();
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected final int destroyVPNProfileSet(int handle) {
      this.checkSession();
      return destroyProfileSet(handle);
   }

   protected final int addVPNProfile(VPNProfile profile) {
      return addProfile(profile);
   }

   protected final int removeVPNProfile(int profileID) {
      return removeProfile(profileID);
   }

   protected final int connectVPN(int handle, int profileID) {
      this.checkSession();
      return connect(handle, profileID);
   }

   protected final int disconnectVPN(int handle) {
      this.checkSession();
      return disconnect(handle);
   }

   protected final int abortVPN(int handle) {
      this.checkSession();
      return abort(handle);
   }

   protected final int getVPNStatus(int handle) {
      return getStatus(handle);
   }

   private static native int createProfileSet();

   private static native int destroyProfileSet(int var0);

   private static native int addProfile(VPNProfile var0);

   private static native int removeProfile(int var0);

   private static native int connect(int var0, int var1);

   private static native int disconnect(int var0);

   private static native int abort(int var0);

   private static native int getStatus(int var0);
}
