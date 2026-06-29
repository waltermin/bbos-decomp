package net.rim.device.api.crypto;

public final class Crypto10Initialization {
   public static final void initialize() {
      DigestFactory.register(new RIMDigestFactoryCrypto10());
   }

   public static final native void setCurrentECMMode(boolean var0);

   public static final native boolean getCurrentECMMode();
}
