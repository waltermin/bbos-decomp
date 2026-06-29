package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.KeyUsage;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.TicketDialog;

public final class KeyStoreUtilitiesInternal {
   private static KeyStorePasswordManager _manager = KeyStorePasswordManager.getInstance();
   private static KeyStoreManagerHelper _helper = KeyStoreManagerHelper.getInstance();
   public static final int KEY_MATERIAL = 1;
   public static final int SALT = 2;
   public static final int HASH = 3;
   public static final int PASSWORD_VERSION = 4;
   public static final int SECURITY_LEVEL = 5;

   public static final byte[] getCurrentKeyStorePassword(String reasonDescription, boolean alwaysDisplayDialog, boolean alwaysRefreshCachedPassword) throws KeyStoreCancelException {
      _manager.setPasswordInternal();

      byte[] password;
      do {
         int passwordThreshold = _helper.getPasswordAttemptsThreshold();
         int passwordAttempts = _helper.getPasswordAttempts();
         if (passwordAttempts > passwordThreshold) {
            Security.getInstance().deviceUnderAttack();
         } else if (passwordAttempts == passwordThreshold) {
            reasonDescription = KeyStoreResources.getString(7004);
         }

         long currentTime = System.currentTimeMillis();
         long differenceTime = currentTime - _helper.getLastTime();
         byte[] managerPassword = _manager.getInternalPassword();
         password = null;
         boolean refreshCachedPassword = alwaysRefreshCachedPassword
            || differenceTime > _helper.getPassphraseTimeout()
            || differenceTime <= 0
            || managerPassword == null;
         if (!refreshCachedPassword) {
            password = Arrays.copy(managerPassword);
         }

         if (!alwaysDisplayDialog && !refreshCachedPassword && ApplicationControl.isKeyStoreMediumSecurityAllowed(true)) {
            return password;
         }

         StringBuffer pleaseEnterPasswordPrompt = (StringBuffer)(new Object(KeyStoreResources.getString(3001)));
         if (passwordAttempts > 1 && passwordAttempts < passwordThreshold) {
            pleaseEnterPasswordPrompt.append(' ');
            pleaseEnterPasswordPrompt.append('(');
            pleaseEnterPasswordPrompt.append(passwordAttempts);
            pleaseEnterPasswordPrompt.append('/');
            pleaseEnterPasswordPrompt.append(passwordThreshold);
            pleaseEnterPasswordPrompt.append(')');
         }

         pleaseEnterPasswordPrompt.append(':');
         boolean revealPassword = passwordAttempts > _helper.getRevealPasswordAttempts();
         TicketDialog ticketDialog = (TicketDialog)(new Object(
            RichTextFieldUtilities.getBoldFormattedRichTextField(reasonDescription, 9007199254740992L),
            refreshCachedPassword,
            pleaseEnterPasswordPrompt.toString(),
            revealPassword,
            134217728
         ));
         BackgroundDialog.show(ticketDialog);
         if (ticketDialog.getCloseReason() == -1) {
            throw new KeyStoreCancelException();
         }

         if (!refreshCachedPassword) {
            return password;
         }

         password = ticketDialog.getPassword();
      } while (!checkPasswordAttempt(password));

      return password;
   }

   private static final synchronized boolean checkPasswordAttempt(byte[] password) {
      if (password != null && password.length != 0) {
         byte[] newHash = computeHash(password);
         if (!_helper.checkPasswordEquals(newHash)) {
            return false;
         }

         setTimeoutReminder();
         if (_helper.getPassphraseTimeout() > 0) {
            _manager.setInternalPassword(password);
         }

         return true;
      } else {
         return false;
      }
   }

   public static final byte[] computeHash(byte[] password) {
      SHA256Digest digest = (SHA256Digest)(new Object());
      digest.update(password);
      return digest.getDigest();
   }

   public static final void setTimeoutReminder() {
      long timeout = _helper.getPassphraseTimeout();
      if (timeout != 0) {
         Proxy proxyInstance = Proxy.getInstance();
         int id = _helper.getTimerID();
         if (id > 0) {
            proxyInstance.cancelInvokeLater(id);
         }

         id = proxyInstance.invokeLater(new PasswordClean(), timeout, false);
         _helper.setTimerID(id);
      }
   }

   public static final int getMinutes(long time) {
      long minutes = time / 1000 / 60;
      return (int)minutes;
   }

   public static final String getDate(int minutes) {
      DateFormat format = DateFormat.getInstance(48);
      return format.formatLocal((long)minutes * 60000);
   }

   public static final void addRootCertificate(
      TrustedKeyStore trusted, DeviceKeyStore device, String label, byte[] encoding, String type, CertificateStatus status
   ) {
      try {
         if (!device.isMember(encoding)) {
            device.set(label, encoding, type, status);
         }

         if (trusted != null && !trusted.isMember(encoding)) {
            trusted.set(label, encoding, type, status);
            return;
         }
      } finally {
         return;
      }
   }

   public static final byte[] encode(byte[] key, byte[] salt, byte[] hash, int passwordVersion, int securityLevel) {
      DataBuffer buffer = (DataBuffer)(new Object());
      TLEUtilities.writeDataField(buffer, 1, key);
      TLEUtilities.writeDataField(buffer, 2, salt);
      TLEUtilities.writeDataField(buffer, 3, hash);
      TLEUtilities.writeIntegerField(buffer, 4, passwordVersion, true);
      TLEUtilities.writeIntegerField(buffer, 5, securityLevel, true);
      return buffer.toArray();
   }

   public static final byte[] getKeyMaterial(byte[] encoding, int offset, int length) {
      return getDataField(encoding, offset, length, 1);
   }

   public static final byte[] getSalt(byte[] encoding, int offset, int length) {
      return getDataField(encoding, offset, length, 2);
   }

   public static final int getPasswordVersion(byte[] encoding) {
      return getPasswordVersion(encoding, 0, encoding == null ? 0 : encoding.length);
   }

   public static final int getPasswordVersion(byte[] encoding, int offset, int length) {
      return getIntegerField(encoding, offset, length, 4);
   }

   public static final void setPasswordVersion(byte[] encoding, int passwordVersion) throws KeyStoreDecodeException {
      if (encoding == null) {
         throw new KeyStoreDecodeException();
      }

      DataBuffer buffer = (DataBuffer)(new Object(encoding, 0, encoding.length, true));
      TLEUtilities.writeIntegerField(buffer, 4, passwordVersion, true);
   }

   public static final int getSecurityLevel(byte[] encoding) {
      return getSecurityLevel(encoding, 0, encoding == null ? 0 : encoding.length);
   }

   public static final int getSecurityLevel(byte[] encoding, int offset, int length) {
      return getIntegerField(encoding, offset, length, 5);
   }

   public static final byte[] getHash(byte[] encoding) {
      return getHash(encoding, 0, encoding == null ? 0 : encoding.length);
   }

   public static final byte[] getHash(byte[] encoding, int offset, int length) {
      return getDataField(encoding, offset, length, 3);
   }

   private static final byte[] getDataField(byte[] param0, int param1, int param2, int param3) throws KeyStoreDecodeException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnull 14
      // 04: iload 1
      // 05: iflt 14
      // 08: iload 2
      // 09: iflt 14
      // 0c: aload 0
      // 0d: arraylength
      // 0e: iload 2
      // 0f: isub
      // 10: iload 1
      // 11: if_icmpge 1c
      // 14: new java/lang/Object
      // 17: dup
      // 18: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1b: athrow
      // 1c: new java/lang/Object
      // 1f: dup
      // 20: aload 0
      // 21: iload 1
      // 22: iload 2
      // 23: bipush 1
      // 24: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 27: astore 4
      // 29: aload 4
      // 2b: iload 3
      // 2c: invokestatic net/rim/device/api/util/TLEUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 2f: ifeq 40
      // 32: aload 4
      // 34: iload 3
      // 35: invokestatic net/rim/device/api/util/TLEUtilities.readDataField (Lnet/rim/device/api/util/DataBuffer;I)[B
      // 38: areturn
      // 39: astore 4
      // 3b: goto 40
      // 3e: astore 4
      // 40: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 43: dup
      // 44: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 47: athrow
      // try (16 -> 31): 32 null
      // try (16 -> 31): 34 null
   }

   private static final int getIntegerField(byte[] param0, int param1, int param2, int param3) throws KeyStoreDecodeException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnull 14
      // 04: iload 1
      // 05: iflt 14
      // 08: iload 2
      // 09: iflt 14
      // 0c: aload 0
      // 0d: arraylength
      // 0e: iload 2
      // 0f: isub
      // 10: iload 1
      // 11: if_icmpge 1c
      // 14: new java/lang/Object
      // 17: dup
      // 18: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 1b: athrow
      // 1c: new java/lang/Object
      // 1f: dup
      // 20: aload 0
      // 21: iload 1
      // 22: iload 2
      // 23: bipush 1
      // 24: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 27: astore 4
      // 29: aload 4
      // 2b: iload 3
      // 2c: invokestatic net/rim/device/api/util/TLEUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 2f: ifeq 40
      // 32: aload 4
      // 34: iload 3
      // 35: invokestatic net/rim/device/api/util/TLEUtilities.readIntegerField (Lnet/rim/device/api/util/DataBuffer;I)I
      // 38: ireturn
      // 39: astore 4
      // 3b: goto 40
      // 3e: astore 4
      // 40: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 43: dup
      // 44: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 47: athrow
      // try (16 -> 31): 32 null
      // try (16 -> 31): 34 null
   }

   public static final int getAppropriateSecurityLevel(int securityLevel, long keyUsage) {
      boolean sign = KeyUsage.isSignKeyUsage(keyUsage);
      boolean encrypt = KeyUsage.isEncryptKeyUsage(keyUsage);
      int signSecurityLevel = ITPolicy.getInteger(24, 45, 1);
      int encryptSecurityLevel = ITPolicy.getInteger(24, 46, 1);
      boolean lowSecurityDisabled = ITPolicy.getBoolean(24, 7, false);
      if (lowSecurityDisabled) {
         signSecurityLevel = maximumSecurityLevel(3, signSecurityLevel);
         encryptSecurityLevel = maximumSecurityLevel(3, encryptSecurityLevel);
      }

      if (sign) {
         securityLevel = maximumSecurityLevel(securityLevel, signSecurityLevel);
      }

      if (encrypt || !sign) {
         securityLevel = maximumSecurityLevel(securityLevel, encryptSecurityLevel);
      }

      return securityLevel;
   }

   private static final int maximumSecurityLevel(int security1, int security2) {
      switch (security1) {
         case 1:
            return security2;
         case 2:
         default:
            return 2;
         case 3:
            return security2 == 2 ? 2 : 3;
      }
   }
}
