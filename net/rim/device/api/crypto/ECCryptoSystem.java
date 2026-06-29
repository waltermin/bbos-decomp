package net.rim.device.api.crypto;

import net.rim.device.api.util.Persistable;

public final class ECCryptoSystem implements CryptoSystem, Persistable {
   private ECCryptoToken _cryptoToken;
   private CryptoTokenCryptoSystemData _cryptoTokenData;
   private int _hashCode;
   private boolean _verified;
   public static final String EC163K1 = "EC163K1";
   public static final String EC163K2 = "EC163K2";
   public static final String EC163R2 = "EC163R2";
   public static final String EC233K1 = "EC233K1";
   public static final String EC233R1 = "EC233R1";
   public static final String EC239K1 = "EC239K1";
   public static final String EC283K1 = "EC283K1";
   public static final String EC283R1 = "EC283R1";
   public static final String EC409K1 = "EC409K1";
   public static final String EC409R1 = "EC409R1";
   public static final String EC571K1 = "EC571K1";
   public static final String EC571R1 = "EC571R1";
   public static final String EC160R1 = "EC160R1";
   public static final String EC192R1 = "EC192R1";
   public static final String EC224R1 = "EC224R1";
   public static final String EC256R1 = "EC256R1";
   public static final String EC384R1 = "EC384R1";
   public static final String EC521R1 = "EC521R1";
   public static final String WTLS3 = "EC163K1";
   public static final String WTLS5 = "EC163K2";
   public static final String WTLS7 = "EC160R1";

   public final ECKeyPair createECKeyPair() {
      return this._cryptoToken.createECKeyPair(this._cryptoTokenData);
   }

   public final int getPrivateKeyLength() {
      return this._cryptoToken.getECPrivateKeyLength(this._cryptoTokenData);
   }

   public final int getPublicKeyLength(boolean compressed) {
      return this._cryptoToken.getECPublicKeyLength(this._cryptoTokenData, compressed);
   }

   public final CryptoTokenCryptoSystemData getCryptoTokenData() {
      return this._cryptoTokenData;
   }

   public final int getPublicKeyLength() {
      return this.getPublicKeyLength(true);
   }

   public final byte[] getFieldReductor() {
      return this._cryptoToken.getECCryptoSystemFieldReductor(this._cryptoTokenData);
   }

   public final byte[] getCofactor() {
      return this._cryptoToken.getECCryptoSystemCofactor(this._cryptoTokenData);
   }

   public final int getFieldLength() {
      return this._cryptoToken.getECCryptoSystemFieldLength(this._cryptoTokenData);
   }

   public final ECPublicKey getBasePoint() {
      return new ECPublicKey(new ECCryptoSystem(this.getName()), this._cryptoToken.getECCryptoSystemBasePoint(this._cryptoTokenData));
   }

   public final byte[] getGroupOrder() {
      return this._cryptoToken.getECCryptoSystemGroupOrder(this._cryptoTokenData);
   }

   public final byte[] getA() {
      return this._cryptoToken.getECCryptoSystemA(this._cryptoTokenData);
   }

   public final byte[] getB() {
      return this._cryptoToken.getECCryptoSystemB(this._cryptoTokenData);
   }

   @Override
   public final boolean isStrong() {
      return this.getBitLength() >= 160;
   }

   @Override
   public final int getBitLength() {
      return this._cryptoToken.getECCryptoSystemBitLength(this._cryptoTokenData);
   }

   @Override
   public final String getName() {
      return this._cryptoToken.getECCryptoSystemName(this._cryptoTokenData);
   }

   @Override
   public final AsymmetricCryptoToken getAsymmetricCryptoToken() {
      return this._cryptoToken;
   }

   @Override
   public final String getAlgorithm() {
      return "EC";
   }

   @Override
   public final KeyPair createKeyPair() {
      return this.createECKeyPair();
   }

   @Override
   public final void verify() {
      if (!this._verified) {
         ECCryptoToken ecCryptoToken = (ECCryptoToken)this.getAsymmetricCryptoToken();
         ecCryptoToken.verifyECCryptoSystemData(this.getCryptoTokenData());

         try {
            this._verified = true;
         } finally {
            return;
         }
      }
   }

   private final void initialize(ECCryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      if (cryptoToken != null && cryptoTokenData != null) {
         this._cryptoToken = cryptoToken;
         this._cryptoTokenData = cryptoTokenData;
         this.setHashCode();
      } else {
         throw new Object();
      }
   }

   private final void initialize(ECCryptoToken cryptoToken, String name) {
      if (cryptoToken != null && name != null) {
         this.initialize(cryptoToken, cryptoToken.getECCryptoSystemData(name));
      } else {
         throw new Object();
      }
   }

   public ECCryptoSystem(ECCryptoToken cryptoToken, String name) {
      this.initialize(cryptoToken, name);
   }

   public ECCryptoSystem(String param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: invokestatic net/rim/device/api/crypto/SoftwareECCryptoToken.getInstance ()Lnet/rim/device/api/crypto/SoftwareECCryptoToken;
      // 08: aload 1
      // 09: invokespecial net/rim/device/api/crypto/ECCryptoSystem.initialize (Lnet/rim/device/api/crypto/ECCryptoToken;Ljava/lang/String;)V
      // 0c: return
      // 0d: astore 2
      // 0e: new java/lang/Object
      // 11: dup
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/crypto/CryptoTokenException.toString ()Ljava/lang/String;
      // 16: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 19: athrow
      // 1a: astore 2
      // 1b: new java/lang/Object
      // 1e: dup
      // 1f: aload 2
      // 20: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 23: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 26: athrow
      // 27: astore 2
      // 28: new java/lang/Object
      // 2b: dup
      // 2c: aload 2
      // 2d: invokevirtual net/rim/device/api/crypto/UnsupportedCryptoSystemException.toString ()Ljava/lang/String;
      // 30: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 33: athrow
      // try (2 -> 6): 7 null
      // try (2 -> 6): 14 null
      // try (2 -> 6): 21 null
   }

   private final void setHashCode() {
      this._hashCode = this._cryptoToken.hashCode() ^ this._cryptoTokenData.hashCode();
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof ECCryptoSystem)) {
         return false;
      }

      ECCryptoSystem other = (ECCryptoSystem)obj;
      return this._hashCode == other._hashCode && this._cryptoToken.equals(other._cryptoToken) && this._cryptoTokenData.equals(other._cryptoTokenData);
   }

   public ECCryptoSystem(ECCryptoToken cryptoToken, CryptoTokenCryptoSystemData cryptoTokenData) {
      this.initialize(cryptoToken, cryptoTokenData);
   }

   public ECCryptoSystem() {
      this("EC163K1");
   }
}
