package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PasswordDialog;
import net.rim.vm.Memory;

public final class KeyStorePasswordManager {
   private byte[] _password;
   private IntHashtable _passwordHashtable;
   private KeyStoreManagerHelper _helper = KeyStoreManagerHelper.getInstance();
   private KeyStorePasswordManager$KeyStoreDevicePasswordListener _passwordListener;
   private int _oldPasswordMinLength = FIPSPolicy.getMaxInteger(8, 4, 5);
   private int _oldPasswordPattern = ITPolicy.getInteger(13, 0);
   private static final int SALT_LENGTH = 16;
   private static final int AES_KEY_SIZE_BYTES = 32;
   private static final long ID = -2533086573079312352L;
   private static final byte[] PASSWORD = new byte[]{-68, -101, -72, -71, 119, -102, 35, -98, -21, -36, -75, -127, 31, -58, -74, 98};
   private static final int PASSWORD_VERSION_LENGTH = 4;
   private static KeyStorePasswordManager _manager;

   private KeyStorePasswordManager() {
      this._passwordListener = new KeyStorePasswordManager$KeyStoreDevicePasswordListener(this, null);
      Security.getInstance().setKeyStoreListener(this._passwordListener);
   }

   private final byte[] getLowSecurityPassword() {
      int passwordLength = PASSWORD.length;
      byte[] lowSecurityPassword = new byte[passwordLength];
      int z = 3;

      for (int i = 0; i < passwordLength; i++) {
         z = z * 103 % 253;
         lowSecurityPassword[i] = (byte)(PASSWORD[i] ^ z);
      }

      return lowSecurityPassword;
   }

   public static final KeyStorePasswordManager getInstance() {
      if (_manager == null) {
         ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
         _manager = (KeyStorePasswordManager)appRegistry.getOrWaitFor(-2533086573079312352L);
         if (_manager == null) {
            _manager = new KeyStorePasswordManager();
            appRegistry.put(-2533086573079312352L, _manager);
         }
      }

      return _manager;
   }

   public final void challenge() {
      this.challenge(null);
   }

   public final void challenge(String additionalPrompt) {
      String fullPrompt = KeyStoreResources.getString(2008);
      if (additionalPrompt != null) {
         fullPrompt = fullPrompt.concat(additionalPrompt);
      }

      KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, true);
   }

   public final synchronized byte[] encrypt(int securityLevel, long salt, byte[] input, String label, KeyStorePasswordTicket ticket) {
      return this.encrypt(securityLevel, input, label, ticket);
   }

   public final synchronized byte[] encrypt(int securityLevel, byte[] input, String label, KeyStorePasswordTicket ticket) {
      return this.doEncryptWork(securityLevel, RandomSource.getBytes(16), input, 0, input.length, label, ticket);
   }

   public final synchronized byte[] encrypt(int securityLevel, long salt, byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket) {
      return this.encrypt(securityLevel, input, offset, length, label, ticket);
   }

   public final synchronized byte[] encrypt(int securityLevel, byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket) {
      return this.doEncryptWork(securityLevel, RandomSource.getBytes(16), input, offset, length, label, ticket);
   }

   public final synchronized byte[] decrypt(int securityLevel, long salt, byte[] input, String label, KeyStorePasswordTicket ticket) {
      return this.decrypt(input, label, ticket);
   }

   public final synchronized byte[] decrypt(byte[] input, String label, KeyStorePasswordTicket ticket) {
      return this.doDecryptWork(input, 0, input.length, label, ticket);
   }

   public final synchronized byte[] decrypt(int securityLevel, long salt, byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket) {
      return this.decrypt(input, offset, length, label, ticket);
   }

   public final synchronized byte[] decrypt(byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket) {
      return this.doDecryptWork(input, offset, length, label, ticket);
   }

   private final synchronized byte[] doEncryptWork(
      int securityLevel, byte[] salt, byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket
   ) {
      return this.cipher_encrypt(securityLevel, this.getPasswordFromTicket(securityLevel, ticket), salt, input, offset, length);
   }

   private final synchronized byte[] doDecryptWork(byte[] input, int offset, int length, String label, KeyStorePasswordTicket ticket) {
      int securityLevel = KeyStoreUtilitiesInternal.getSecurityLevel(input, offset, length);
      return this.cipher_decrypt(this.getPasswordFromTicket(securityLevel, ticket), input, offset, length);
   }

   private final synchronized byte[] getPasswordFromTicket(int securityLevel, KeyStorePasswordTicket passwordTicket) {
      if (securityLevel == 1) {
         return this.getLowSecurityPassword();
      } else {
         return this.checkTicket(passwordTicket)
            ? ((KeyStorePasswordManager$RIMKeyStorePasswordTicket)passwordTicket).getPassword()
            : KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(KeyStoreResources.getString(2008), true, false);
      }
   }

   public final synchronized void changePassword() {
      String fullPrompt = ((StringBuffer)(new Object())).append(KeyStoreResources.getString(2008)).append(KeyStoreResources.getString(7016)).toString();
      byte[] oldPasswordBytes = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, true);
      String newPasswordPrompt = KeyStoreResources.getString(1003);
      String confirmPasswordPrompt = KeyStoreResources.getString(1004);
      String passwordPatternMask = ITPolicy.getString(24, 73);
      if (passwordPatternMask != null) {
         StringBuffer sb = (StringBuffer)(new Object(2 + passwordPatternMask.length() + 1));
         sb.append(' ');
         sb.append('(');
         sb.append(passwordPatternMask);
         sb.append(')');
         String suffix = sb.toString();
         newPasswordPrompt = ((StringBuffer)(new Object())).append(newPasswordPrompt).append(suffix).toString();
         confirmPasswordPrompt = ((StringBuffer)(new Object())).append(confirmPasswordPrompt).append(suffix).toString();
      }

      byte[] newPasswordBytes;
      do {
         PasswordDialog dialog = (PasswordDialog)(new Object(newPasswordPrompt, confirmPasswordPrompt, 32, 134217728));
         BackgroundDialog.show(dialog);
         if (dialog.getCloseReason() == -1) {
            throw new KeyStoreCancelException();
         }

         newPasswordBytes = dialog.getPassword();
      } while (!this.isPasswordValid(newPasswordBytes) || !this.isHistoryValid(newPasswordBytes));

      new Object(newPasswordBytes);
      this.saveNewPassword(oldPasswordBytes, newPasswordBytes);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final synchronized void checkPassword() {
      boolean var5 = false /* VF: Semaphore variable */;

      label27: {
         try {
            var5 = true;
            this._passwordHashtable = (IntHashtable)(new Object());
            KeyStoreManager.getInstance().changePassword();
            var5 = false;
            break label27;
         } catch (KeyStoreDecodeRuntimeException var6) {
            var5 = false;
         } finally {
            if (var5) {
               this._passwordHashtable = null;
            }
         }

         this._passwordHashtable = null;
         return;
      }

      this._passwordHashtable = null;
   }

   public final byte[] decryptReEncrypt(int securityLevel, long salt, byte[] ciphertext) {
      return this.decryptReEncrypt(securityLevel, ciphertext);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final byte[] decryptReEncrypt(int securityLevel, byte[] ciphertext) {
      boolean setHashTable = false;
      boolean var15 = false /* VF: Semaphore variable */;

      byte[] var10;
      label98: {
         byte[] currentPasswordBytes;
         try {
            var15 = true;
            if (this._passwordHashtable == null) {
               this._passwordHashtable = (IntHashtable)(new Object());
               setHashTable = true;
            }

            int currentPasswordVersion = this._helper.getPasswordVersion();
            int oldPasswordVersion = KeyStoreUtilitiesInternal.getPasswordVersion(ciphertext);
            if (oldPasswordVersion != currentPasswordVersion && securityLevel != 1) {
               try {
                  currentPasswordBytes = (byte[])this._passwordHashtable.get(currentPasswordVersion);
                  if (currentPasswordBytes == null) {
                     String fullPrompt = ((StringBuffer)(new Object()))
                        .append(KeyStoreResources.getString(2008))
                        .append(KeyStoreResources.getString(7019))
                        .toString();
                     currentPasswordBytes = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, false);
                     currentPasswordVersion = this._helper.getPasswordVersion();
                     this._passwordHashtable.put(currentPasswordVersion, currentPasswordBytes);
                  }

                  while (true) {
                     byte[] oldPasswordBytes = this.getHistoricalPassword(oldPasswordVersion);

                     try {
                        byte[] plaintext = this.cipher_decrypt(oldPasswordBytes, ciphertext, 0, ciphertext.length);
                        this._passwordHashtable.put(oldPasswordVersion, oldPasswordBytes);
                        byte[] salt = KeyStoreUtilitiesInternal.getSalt(ciphertext, 0, ciphertext.length);
                        var10 = this.cipher_encrypt(securityLevel, currentPasswordBytes, salt, plaintext, 0, plaintext.length);
                        var15 = false;
                        break label98;
                     } catch (KeyStoreDecodeException var16) {
                     }
                  }
               } catch (KeyStoreCancelException e) {
                  throw new KeyStoreDecodeException();
               }
            }

            currentPasswordBytes = ciphertext;
            var15 = false;
         } finally {
            if (var15) {
               if (setHashTable) {
                  this._passwordHashtable = null;
               }
            }
         }

         if (setHashTable) {
            this._passwordHashtable = null;
         }

         return currentPasswordBytes;
      }

      if (setHashTable) {
         this._passwordHashtable = null;
      }

      return var10;
   }

   private final byte[] getHistoricalPassword(int passwordVersion) {
      byte[] password = (byte[])this._passwordHashtable.get(passwordVersion);
      if (password == null) {
         if (passwordVersion == 0) {
            return this.getLowSecurityPassword();
         }

         String message = MessageFormat.format(KeyStoreResources.getString(6088), new Object[]{KeyStoreUtilitiesInternal.getDate(passwordVersion)});
         PasswordDialog dialog = (PasswordDialog)(new Object(message, false, 32, 134217728));
         BackgroundDialog.show(dialog);
         if (dialog.getCloseReason() == -1) {
            throw new KeyStoreCancelException();
         }

         password = dialog.getPassword();
      }

      return password;
   }

   final synchronized KeyStorePasswordTicket createTicket(byte[] hash) {
      return this.createTicket(hash, null);
   }

   private final synchronized KeyStorePasswordTicket createTicket(byte[] hash, byte[] password) {
      if (Arrays.equals(hash, this._helper.getHash())) {
         KeyStorePasswordTicket ticket = new KeyStorePasswordManager$RIMKeyStorePasswordTicket(password);
         PersistentContent.markAsPlaintext(ticket);
         return ticket;
      }

      try {
         Thread.sleep(1000);
         return null;
      } finally {
         ;
      }
   }

   public final KeyStorePasswordTicket getTicket(KeyStore keyStore) {
      return this.getTicket(null, keyStore);
   }

   public final KeyStorePasswordTicket getTicket(String additionalPrompt, KeyStore keyStore) {
      String fullPromptPattern = KeyStoreResources.getString(2002);
      if (additionalPrompt != null) {
         fullPromptPattern = fullPromptPattern.concat(additionalPrompt);
      }

      String fullPrompt = MessageFormat.format(fullPromptPattern, new Object[]{keyStore.getName()});
      byte[] password = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, false);
      return this.createTicket(this._helper.getHash(), password);
   }

   public final KeyStorePasswordTicket getTicket(KeyStoreData keyStoreData) {
      return this.getTicket(null, keyStoreData);
   }

   public final KeyStorePasswordTicket getTicket(String additionalPrompt, KeyStoreData keyStoreData) {
      int securityLevel = keyStoreData.getSecurityLevel();
      if (securityLevel != 1 && securityLevel != 0) {
         String fullPromptPattern;
         if (keyStoreData.isPrivateKeySet()) {
            fullPromptPattern = KeyStoreResources.getString(2001);
         } else {
            fullPromptPattern = KeyStoreResources.getString(7017);
         }

         if (additionalPrompt != null) {
            fullPromptPattern = fullPromptPattern.concat(additionalPrompt);
         }

         String fullPrompt = MessageFormat.format(fullPromptPattern, new Object[]{keyStoreData.getLabel()});
         byte[] password = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, securityLevel == 2, false);
         return this.createTicket(this._helper.getHash(), password);
      } else {
         return null;
      }
   }

   public final KeyStorePasswordTicket getTicket() {
      return this.getTicket((String)((Object)null));
   }

   public final KeyStorePasswordTicket getTicket(String additionalPrompt) {
      String fullPrompt = KeyStoreResources.getString(2008);
      if (additionalPrompt != null) {
         fullPrompt = fullPrompt.concat(additionalPrompt);
      }

      byte[] password = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, false);
      return this.createTicket(this._helper.getHash(), password);
   }

   public final boolean checkTicket(KeyStorePasswordTicket ticket) {
      return ticket instanceof KeyStorePasswordManager$RIMKeyStorePasswordTicket && ((KeyStorePasswordManager$RIMKeyStorePasswordTicket)ticket).access();
   }

   public final synchronized byte[] setPassword() {
      this.setPasswordInternal();
      return null;
   }

   final synchronized byte[] setPasswordInternal() {
      if (this._helper.isPassphraseSet()) {
         return null;
      }

      String newPasswordPrompt = KeyStoreResources.getString(1003);
      String confirmPasswordPrompt = KeyStoreResources.getString(1004);
      String passwordPatternMask = ITPolicy.getString(24, 73);
      if (passwordPatternMask != null) {
         StringBuffer sb = (StringBuffer)(new Object(2 + passwordPatternMask.length() + 1));
         sb.append(' ');
         sb.append('(');
         sb.append(passwordPatternMask);
         sb.append(')');
         String suffix = sb.toString();
         newPasswordPrompt = ((StringBuffer)(new Object())).append(newPasswordPrompt).append(suffix).toString();
         confirmPasswordPrompt = ((StringBuffer)(new Object())).append(confirmPasswordPrompt).append(suffix).toString();
      }

      byte[] password;
      do {
         PasswordDialog dialog = (PasswordDialog)(new Object(newPasswordPrompt, confirmPasswordPrompt, 32, 134217728));
         BackgroundDialog.show(dialog);
         if (dialog.getCloseReason() == -1) {
            throw new KeyStoreCancelException();
         }

         password = dialog.getPassword();
      } while (!this.isPasswordValid(password) || !this.isHistoryValid(password));

      this.saveNewPassword(null, password);
      return password;
   }

   private final boolean isHistoryValid(byte[] password) {
      if (this._helper.checkPasswordHistory(password)) {
         BackgroundDialog.showMessage(KeyStoreResources.getString(6085));
         return false;
      } else {
         return true;
      }
   }

   private final boolean isPasswordValid(byte[] password) {
      Security security = Security.getInstance();
      String passwordString = (String)(new Object(password));
      int returnCode = security.isPasswordValid(passwordString);
      switch (returnCode) {
         case -1:
            BackgroundDialog.showMessage(KeyStoreResources.getString(6079));
            return false;
         case 0:
         default:
            int result = security.verifyPasswordPattern(passwordString);
            switch (result) {
               case 0:
                  return true;
               case 4:
                  BackgroundDialog.showMessage(KeyStoreResources.getString(6081));
                  return false;
               case 5:
                  BackgroundDialog.showMessage(KeyStoreResources.getString(6082));
                  return false;
               case 6:
                  BackgroundDialog.showMessage(KeyStoreResources.getString(6080));
                  return false;
               case 8:
                  BackgroundDialog.showMessage(KeyStoreResources.getString(7024));
               default:
                  BackgroundDialog.showMessage(KeyStoreResources.getString(6079));
                  return false;
            }
         case 1:
            BackgroundDialog.showMessage(KeyStoreResources.getString(6076));
            return false;
         case 2:
            BackgroundDialog.showMessage(KeyStoreResources.getString(6077));
            return false;
         case 3:
            BackgroundDialog.showMessage(KeyStoreResources.getString(6078));
            return false;
      }
   }

   public final boolean clean() {
      this._helper.setLastTime(0);
      this._helper.setTimerID(0);
      if (this._password != null) {
         this._password = null;
         return true;
      } else {
         return false;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized void saveNewPassword(byte[] oldPassword, byte[] newPassword) {
      int oldPasswordVersion = this._helper.getPasswordVersion();
      this._helper.addPasswordToHistory(newPassword);
      this._helper.setHash(KeyStoreUtilitiesInternal.computeHash(newPassword));
      this._helper.setLastTime(System.currentTimeMillis());
      KeyStoreUtilitiesInternal.setTimeoutReminder();
      this._helper.setPasswordAttempts(1);
      int minutes = KeyStoreUtilitiesInternal.getMinutes(System.currentTimeMillis());
      if (minutes == this._helper.getPasswordVersion()) {
         minutes++;
      }

      this._helper.setPasswordVersion(minutes);
      if (this._helper.getPassphraseTimeout() > 0) {
         this._password = Memory.copyToRAMOnlyBytes(newPassword);
      }

      if (oldPassword != null) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            this._passwordHashtable = (IntHashtable)(new Object());
            this._passwordHashtable.put(oldPasswordVersion, oldPassword);
            this._passwordHashtable.put(this._helper.getPasswordVersion(), newPassword);
            KeyStoreManager.getInstance().changePassword();
            var9 = false;
         } catch (KeyStoreDecodeRuntimeException e) {
            throw new KeyStoreCancelException();
         } finally {
            if (var9) {
               this._passwordHashtable = null;
            }
         }

         this._passwordHashtable = null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void performITPolicyCheck() {
      if (this._helper.isPassphraseSet()) {
         int currentPasswordMinLength = FIPSPolicy.getMaxInteger(8, 4, 5);
         int currentPasswordPattern = ITPolicy.getInteger(13, 0);
         boolean var7 = false /* VF: Semaphore variable */;

         label61: {
            try {
               var7 = true;
               if (currentPasswordMinLength <= this._oldPasswordMinLength) {
                  if (currentPasswordPattern <= this._oldPasswordPattern) {
                     var7 = false;
                     break label61;
                  }

                  var7 = false;
               } else {
                  var7 = false;
               }
            } finally {
               if (var7) {
                  this._oldPasswordMinLength = currentPasswordMinLength;
                  this._oldPasswordPattern = currentPasswordPattern;
               }
            }

            this._oldPasswordMinLength = currentPasswordMinLength;
            this._oldPasswordPattern = currentPasswordPattern;

            while (true) {
               try {
                  String fullPrompt = ((StringBuffer)(new Object()))
                     .append(KeyStoreResources.getString(2008))
                     .append(KeyStoreResources.getString(7020))
                     .toString();
                  byte[] currentPassword = KeyStoreUtilitiesInternal.getCurrentKeyStorePassword(fullPrompt, true, false);
                  if (!this.isPasswordValid(currentPassword)) {
                     this.changePassword();
                  }

                  return;
               } catch (KeyStoreCancelException var8) {
               }
            }
         }

         this._oldPasswordMinLength = currentPasswordMinLength;
         this._oldPasswordPattern = currentPasswordPattern;
      }
   }

   final void setInternalPassword(byte[] password) {
      this._password = Memory.copyToRAMOnlyBytes(password);
   }

   final byte[] getInternalPassword() {
      return this._password;
   }

   private final byte[] cipher_encrypt(int param1, byte[] param2, byte[] param3, byte[] param4, int param5, int param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 4
      // 02: ifnull 22
      // 05: iload 5
      // 07: iflt 22
      // 0a: iload 5
      // 0c: aload 4
      // 0e: arraylength
      // 0f: if_icmpgt 22
      // 12: iload 6
      // 14: iflt 22
      // 17: aload 4
      // 19: arraylength
      // 1a: iload 6
      // 1c: isub
      // 1d: iload 5
      // 1f: if_icmpge 2a
      // 22: new java/lang/Object
      // 25: dup
      // 26: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 29: athrow
      // 2a: new java/lang/Object
      // 2d: dup
      // 2e: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 31: astore 7
      // 33: aload 7
      // 35: aload 4
      // 37: iload 5
      // 39: iload 6
      // 3b: invokevirtual net/rim/device/api/crypto/SHA1Digest.update ([BII)V
      // 3e: aload 7
      // 40: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ()[B
      // 43: astore 8
      // 45: new net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource
      // 48: dup
      // 49: aload 2
      // 4a: aload 3
      // 4b: bipush 3
      // 4d: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.<init> ([B[BI)V
      // 50: bipush 32
      // 52: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 55: astore 9
      // 57: new net/rim/device/api/crypto/AESKey
      // 5a: dup
      // 5b: aload 9
      // 5d: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 60: astore 10
      // 62: new net/rim/device/api/crypto/AESEncryptorEngine
      // 65: dup
      // 66: aload 10
      // 68: invokespecial net/rim/device/api/crypto/AESEncryptorEngine.<init> (Lnet/rim/device/api/crypto/AESKey;)V
      // 6b: astore 11
      // 6d: new net/rim/device/api/crypto/CBCEncryptorEngine
      // 70: dup
      // 71: aload 11
      // 73: new net/rim/device/api/crypto/InitializationVector
      // 76: dup
      // 77: aload 3
      // 78: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 7b: invokespecial net/rim/device/api/crypto/CBCEncryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 7e: astore 12
      // 80: new net/rim/device/api/crypto/PKCS5FormatterEngine
      // 83: dup
      // 84: aload 12
      // 86: invokespecial net/rim/device/api/crypto/PKCS5FormatterEngine.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 89: astore 13
      // 8b: new java/lang/Object
      // 8e: dup
      // 8f: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 92: astore 14
      // 94: new net/rim/device/api/crypto/BlockEncryptor
      // 97: dup
      // 98: aload 13
      // 9a: aload 14
      // 9c: invokespecial net/rim/device/api/crypto/BlockEncryptor.<init> (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/io/OutputStream;)V
      // 9f: astore 15
      // a1: aload 15
      // a3: aload 4
      // a5: iload 5
      // a7: iload 6
      // a9: invokevirtual net/rim/device/api/crypto/BlockEncryptor.write ([BII)V
      // ac: aload 15
      // ae: invokevirtual net/rim/device/api/crypto/BlockEncryptor.close ()V
      // b1: aload 14
      // b3: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // b6: iload 1
      // b7: bipush 1
      // b8: if_icmpne bf
      // bb: bipush 0
      // bc: goto c6
      // bf: aload 0
      // c0: getfield net/rim/device/api/crypto/keystore/KeyStorePasswordManager._helper Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // c3: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getPasswordVersion ()I
      // c6: istore 16
      // c8: aload 14
      // ca: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // cd: aload 3
      // ce: aload 8
      // d0: iload 16
      // d2: iload 1
      // d3: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.encode ([B[B[BII)[B
      // d6: areturn
      // d7: astore 7
      // d9: new java/lang/Object
      // dc: dup
      // dd: aload 7
      // df: invokevirtual net/rim/device/api/crypto/CryptoException.toString ()Ljava/lang/String;
      // e2: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // e5: athrow
      // e6: astore 7
      // e8: new java/lang/Object
      // eb: dup
      // ec: aload 7
      // ee: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // f1: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // f4: athrow
      // try (0 -> 100): 101 null
      // try (0 -> 100): 108 null
   }

   private final byte[] cipher_decrypt(byte[] param1, byte[] param2, int param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 2
      // 001: ifnull 01c
      // 004: iload 3
      // 005: iflt 01c
      // 008: iload 3
      // 009: aload 2
      // 00a: arraylength
      // 00b: if_icmpgt 01c
      // 00e: iload 4
      // 010: iflt 01c
      // 013: aload 2
      // 014: arraylength
      // 015: iload 4
      // 017: isub
      // 018: iload 3
      // 019: if_icmpge 024
      // 01c: new java/lang/Object
      // 01f: dup
      // 020: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 023: athrow
      // 024: aload 2
      // 025: iload 3
      // 026: iload 4
      // 028: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getSalt ([BII)[B
      // 02b: astore 5
      // 02d: new net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource
      // 030: dup
      // 031: aload 1
      // 032: aload 5
      // 034: bipush 3
      // 036: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.<init> ([B[BI)V
      // 039: bipush 32
      // 03b: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.getBytes (I)[B
      // 03e: astore 6
      // 040: new net/rim/device/api/crypto/AESKey
      // 043: dup
      // 044: aload 6
      // 046: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 049: astore 7
      // 04b: new net/rim/device/api/crypto/AESDecryptorEngine
      // 04e: dup
      // 04f: aload 7
      // 051: invokespecial net/rim/device/api/crypto/AESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/AESKey;)V
      // 054: astore 8
      // 056: new net/rim/device/api/crypto/CBCDecryptorEngine
      // 059: dup
      // 05a: aload 8
      // 05c: new net/rim/device/api/crypto/InitializationVector
      // 05f: dup
      // 060: aload 5
      // 062: invokespecial net/rim/device/api/crypto/InitializationVector.<init> ([B)V
      // 065: invokespecial net/rim/device/api/crypto/CBCDecryptorEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;Lnet/rim/device/api/crypto/InitializationVector;)V
      // 068: astore 9
      // 06a: new net/rim/device/api/crypto/PKCS5UnformatterEngine
      // 06d: dup
      // 06e: aload 9
      // 070: invokespecial net/rim/device/api/crypto/PKCS5UnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 073: astore 10
      // 075: aload 2
      // 076: iload 3
      // 077: iload 4
      // 079: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getKeyMaterial ([BII)[B
      // 07c: astore 11
      // 07e: aload 11
      // 080: arraylength
      // 081: istore 12
      // 083: new java/lang/Object
      // 086: dup
      // 087: aload 11
      // 089: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 08c: astore 13
      // 08e: new net/rim/device/api/crypto/BlockDecryptor
      // 091: dup
      // 092: aload 10
      // 094: aload 13
      // 096: invokespecial net/rim/device/api/crypto/BlockDecryptor.<init> (Lnet/rim/device/api/crypto/BlockUnformatterEngine;Ljava/io/InputStream;)V
      // 099: astore 14
      // 09b: iload 12
      // 09d: newarray 8
      // 09f: astore 15
      // 0a1: aload 14
      // 0a3: aload 15
      // 0a5: bipush 0
      // 0a6: iload 12
      // 0a8: invokevirtual net/rim/device/api/crypto/BlockDecryptor.read ([BII)I
      // 0ab: istore 16
      // 0ad: aload 14
      // 0af: bipush 1
      // 0b0: newarray 8
      // 0b2: invokevirtual net/rim/device/api/crypto/CryptoInputStream.read ([B)I
      // 0b5: istore 17
      // 0b7: iload 17
      // 0b9: ifle 0c4
      // 0bc: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 0bf: dup
      // 0c0: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 0c3: athrow
      // 0c4: aload 15
      // 0c6: iload 16
      // 0c8: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 0cb: new java/lang/Object
      // 0ce: dup
      // 0cf: invokespecial net/rim/device/api/crypto/SHA1Digest.<init> ()V
      // 0d2: astore 18
      // 0d4: aload 18
      // 0d6: aload 15
      // 0d8: bipush 0
      // 0d9: aload 15
      // 0db: arraylength
      // 0dc: invokevirtual net/rim/device/api/crypto/SHA1Digest.update ([BII)V
      // 0df: bipush 20
      // 0e1: newarray 8
      // 0e3: astore 19
      // 0e5: aload 18
      // 0e7: aload 19
      // 0e9: bipush 0
      // 0ea: invokevirtual net/rim/device/api/crypto/AbstractDigest.getDigest ([BI)I
      // 0ed: pop
      // 0ee: aload 2
      // 0ef: iload 3
      // 0f0: iload 4
      // 0f2: invokestatic net/rim/device/api/crypto/keystore/KeyStoreUtilitiesInternal.getHash ([BII)[B
      // 0f5: astore 20
      // 0f7: aload 19
      // 0f9: aload 20
      // 0fb: invokestatic net/rim/device/api/util/Arrays.equals ([B[B)Z
      // 0fe: ifne 109
      // 101: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 104: dup
      // 105: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 108: athrow
      // 109: aload 15
      // 10b: areturn
      // 10c: astore 5
      // 10e: aload 5
      // 110: invokevirtual net/rim/device/api/crypto/CryptoIOException.getCryptoException ()Lnet/rim/device/api/crypto/CryptoException;
      // 113: astore 6
      // 115: aload 6
      // 117: instanceof net/rim/device/api/crypto/tls/SessionInformation
      // 11a: ifeq 125
      // 11d: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 120: dup
      // 121: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 124: athrow
      // 125: new java/lang/Object
      // 128: dup
      // 129: invokespecial java/lang/RuntimeException.<init> ()V
      // 12c: athrow
      // 12d: astore 5
      // 12f: new net/rim/device/api/crypto/keystore/KeyStoreDecodeException
      // 132: dup
      // 133: invokespecial net/rim/device/api/crypto/keystore/KeyStoreDecodeException.<init> ()V
      // 136: athrow
      // 137: astore 5
      // 139: new java/lang/Object
      // 13c: dup
      // 13d: aload 5
      // 13f: invokevirtual java/io/IOException.toString ()Ljava/lang/String;
      // 142: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 145: athrow
      // try (0 -> 132): 133 net/rim/device/api/crypto/CryptoIOException
      // try (0 -> 132): 148 null
      // try (0 -> 132): 153 null
   }
}
