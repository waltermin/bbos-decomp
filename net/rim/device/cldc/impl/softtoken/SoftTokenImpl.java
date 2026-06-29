package net.rim.device.cldc.impl.softtoken;

import net.rim.device.cldc.impl.api.SoftToken;
import net.rim.vm.Memory;

public final class SoftTokenImpl implements SoftToken {
   public int _tag;
   public String _nickName;
   public String _serialNum;
   public byte[] _passphrase;
   public byte[] _pin;
   public int _pinCacheTimeout;
   public long _pinCacheMillis;
   public boolean _readOnly;
   public String _enterpriseConfigID;
   private byte[] _seed;
   private byte[] _seedPassword;
   public static final int MAX_PIN_LENGTH;
   public static final int MIN_PIN_LENGTH;

   public final String getSeedPassword() {
      return (String)(this._seedPassword == null ? "" : new Object(this._seedPassword));
   }

   public final void setSeedPassword(String seedPassword) {
      if (seedPassword != null && seedPassword.length() == 0) {
         seedPassword = null;
      }

      if (seedPassword != null) {
         this._seedPassword = Memory.copyToRAMOnlyBytes(seedPassword.getBytes());
      } else {
         this._seedPassword = null;
      }
   }

   public final void setPassphrase(String passphrase) {
      if (passphrase != null && passphrase.length() == 0) {
         passphrase = null;
      }

      if (passphrase != null) {
         this._passphrase = Memory.copyToRAMOnlyBytes(passphrase.getBytes());
      } else {
         this._passphrase = null;
      }
   }

   public final String getPassphrase() {
      return (String)(this._passphrase == null ? "" : new Object(this._passphrase));
   }

   public final void setPIN(String pin) {
      if (pin != null && pin.length() == 0) {
         pin = null;
      }

      if (this._pinCacheTimeout != 0) {
         if (pin != null) {
            this._pin = Memory.copyToRAMOnlyBytes(pin.getBytes());
         } else {
            this._pin = null;
         }

         this._pinCacheMillis = System.currentTimeMillis();
      } else {
         this._pin = null;
      }
   }

   public final String getPIN() {
      return (String)(this._pin == null ? "" : new Object(this._pin));
   }

   public final String getSeed() {
      return (String)(this._seed == null ? "" : new Object(this._seed));
   }

   public final void setSeed(byte[] seedBytes) {
      if (seedBytes != null && seedBytes.length == 0) {
         seedBytes = null;
      }

      if (seedBytes != null) {
         this._seed = Memory.copyToRAMOnlyBytes(seedBytes);
      } else {
         this._seed = null;
      }
   }

   @Override
   public final void clearCachedPIN() {
      this.setPIN(null);
   }

   @Override
   public final boolean isPINCached() {
      return this.getPIN().length() > 0;
   }

   @Override
   public final String getPasscode() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 1
      // 02: aconst_null
      // 03: astore 2
      // 04: new java/lang/Object
      // 07: dup
      // 08: invokespecial net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.<init> ()V
      // 0b: astore 3
      // 0c: aload 3
      // 0d: aload 0
      // 0e: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 11: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.setActiveToken (I)V
      // 14: aload 3
      // 15: aload 0
      // 16: getfield net/rim/device/cldc/impl/softtoken/SoftTokenImpl._tag I
      // 19: aload 0
      // 1a: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPassphrase ()Ljava/lang/String;
      // 1d: bipush 0
      // 1e: aload 0
      // 1f: invokevirtual net/rim/device/cldc/impl/softtoken/SoftTokenImpl.getPIN ()Ljava/lang/String;
      // 22: invokevirtual net/rim/device/cldc/impl/softtoken/rimsecuridlib/RimSecurIDLib.getTokenPasscode (ILjava/lang/String;ILjava/lang/String;)Ljava/lang/String;
      // 25: astore 2
      // 26: goto 3e
      // 29: astore 3
      // 2a: ldc_w 1162892105
      // 2d: istore 1
      // 2e: goto 3e
      // 31: astore 3
      // 32: ldc_w 1162892100
      // 35: istore 1
      // 36: goto 3e
      // 39: astore 3
      // 3a: ldc_w 1162892112
      // 3d: istore 1
      // 3e: iload 1
      // 3f: ifeq 4b
      // 42: iload 1
      // 43: ldc_w "0"
      // 46: bipush 2
      // 48: invokestatic net/rim/device/cldc/impl/api/SoftTokenManager.logEvent (ILjava/lang/String;I)V
      // 4b: aload 2
      // 4c: areturn
      // try (4 -> 22): 23 null
      // try (4 -> 22): 27 null
      // try (4 -> 22): 31 null
   }

   @Override
   public final String getNickName() {
      return this._nickName;
   }

   @Override
   public final String getSerialNum() {
      return this._serialNum;
   }

   public SoftTokenImpl(int tag, String nickName, String serialNum, String passphrase, int pinCacheTimeout, boolean readOnly, String enterpriseConfigID) {
      this._tag = tag;
      this._nickName = nickName;
      this._serialNum = serialNum;
      this.setPassphrase(passphrase);
      this._pin = null;
      this._pinCacheTimeout = pinCacheTimeout;
      this._pinCacheMillis = -1;
      this._readOnly = readOnly;
      this._enterpriseConfigID = enterpriseConfigID;
      this._seed = null;
      this._seedPassword = null;
   }
}
