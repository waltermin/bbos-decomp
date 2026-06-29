package net.rim.device.apps.internal.passwordkeeper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.rim.device.api.crypto.BlockDecryptor;
import net.rim.device.api.crypto.BlockEncryptor;
import net.rim.device.api.crypto.BlockFormatterEngine;
import net.rim.device.api.crypto.BlockUnformatterEngine;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.PasswordDialog;
import net.rim.vm.Array;

public final class PasswordKeeperManager {
   private byte[] _password;
   private PasswordKeeperApp _app;
   private static final long MANAGER = -1789407596704811166L;

   private PasswordKeeperManager() {
   }

   public static final PasswordKeeperManager getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      PasswordKeeperManager manager = (PasswordKeeperManager)registry.get(-1789407596704811166L);
      if (manager == null) {
         manager = new PasswordKeeperManager();
         registry.put(-1789407596704811166L, manager);
      }

      return manager;
   }

   public final void setUiApplication(PasswordKeeperApp app) {
      this._app = app;
   }

   public final void clean() {
      this._password = null;
   }

   public final void lock() {
      this.clean();
      if (this._app != null) {
         synchronized (this._app.getAppEventLock()) {
            PasswordKeeperSplashScreen splashScreen = new PasswordKeeperSplashScreen();
            this._app.pushScreen(splashScreen);
         }
      }
   }

   public final void exit(boolean error) {
      DecryptionCache.getInstance().flush();
      ApplicationManager.getApplicationManager().requestForegroundForConsole();
      this._app = null;
      System.exit(error ? -1 : 0);
   }

   public final void checkPassword() {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      PasswordKeeperList list = PasswordKeeper.getInstance().getCollection().getSource();
      if (options.getHash() == null) {
         if (list.size() > 0) {
            int listSize = list.size();

            for (int i = listSize - 1; i >= 0; i--) {
               try {
                  this._password = PasswordKeeperUtilities.getOldPassword((PasswordKeeperElement)list.getAt(0));
                  options.setHash(this.getPasswordHash(this._password));
                  break;
               } catch (DeletionException e) {
                  list.removeAt(i);
               }
            }

            if (this._password == null) {
               this.setPassword();
            }
         } else {
            this.setPassword();
         }
      } else {
         this._password = this.askPassword();
      }

      if (!PasswordKeeperUtilities.checkPasswordValidity(this._password)) {
         PasswordKeeperUtilities.convertElementsToCurrentPassword(this._password);
      }
   }

   private final byte[] getPasswordHash(byte[] password) {
      SHA256Digest digest = (SHA256Digest)(new Object());
      byte[] hash = new byte[digest.getDigestLength()];
      digest.update(password, 0, password.length);
      digest.getDigest(hash, 0);
      return hash;
   }

   private final void setPassword() {
      PasswordDialog dialog = (PasswordDialog)(new Object(PasswordKeeper.getString(3000), PasswordKeeper.getString(3001)));
      dialog.show();
      if (dialog.getCloseReason() == -1) {
         throw new CancelException();
      }

      this._password = dialog.getPassword();
      PasswordKeeperOptions.getOptions().setHash(this.getPasswordHash(this._password));
   }

   private final byte[] askPassword() {
      PasswordKeeperOptions options = PasswordKeeperOptions.getOptions();
      int counter = 0;
      int threshold = options.getPasswordThreshold();

      while (true) {
         counter = options.getCounter();
         StringBuffer buffer = (StringBuffer)(new Object());
         buffer.append(PasswordKeeper.getString(3032));
         buffer.append('(').append(counter).append('/').append(threshold).append(')');
         if (counter > threshold) {
            PasswordKeeper.getInstance().getCollection().removeAllSyncObjects();
            options.setHash(null);
            options.resetCounter();
            throw new CancelException();
         }

         PasswordKeeperPasswordDialog dialog;
         if (counter >= threshold) {
            dialog = new PasswordKeeperPasswordDialog(
               true, ((StringBuffer)(new Object())).append(PasswordKeeper.getString(3005)).append(buffer.toString()).toString(), false
            );
         } else if (counter >= threshold >> 1) {
            dialog = new PasswordKeeperPasswordDialog(
               true, ((StringBuffer)(new Object())).append(PasswordKeeper.getString(3004)).append(buffer.toString()).toString(), false
            );
         } else if (counter >= 2) {
            dialog = new PasswordKeeperPasswordDialog(
               false, ((StringBuffer)(new Object())).append(PasswordKeeper.getString(3004)).append(buffer.toString()).toString(), false
            );
         } else {
            dialog = new PasswordKeeperPasswordDialog(
               false, ((StringBuffer)(new Object())).append(CommonResources.getString(2012)).append(':').toString(), false
            );
         }

         dialog.show();
         if (dialog.getCloseReason() == -1) {
            throw new CancelException();
         }

         byte[] password = dialog.getText().getBytes();
         if (password.length != 0) {
            if (options.getHash() == null) {
               return password;
            }

            byte[] newHash = this.getPasswordHash(password);
            if (Arrays.equals(newHash, options.getHash())) {
               options.resetCounter();
               return password;
            }

            if (password.length > 0) {
               options.incrementCounter();
            }
         }
      }
   }

   public final void changePassword(PasswordKeeperList source) {
      PasswordDialog dialog = (PasswordDialog)(new Object(PasswordKeeper.getString(3000), PasswordKeeper.getString(3001)));
      dialog.show();
      if (dialog.getCloseReason() == -1) {
         Dialog.inform(PasswordKeeper.getString(3007));
      } else {
         byte[] newPassword = dialog.getPassword();
         byte[] hash = this.getPasswordHash(newPassword);
         int size = source.size();

         for (int i = size - 1; i >= 0; i--) {
            try {
               PasswordKeeperElement element = (PasswordKeeperElement)source.getAt(i);
               PasswordKeeperElement conversionElement = element;
               if (ObjectGroup.isInGroup(element)) {
                  conversionElement = (PasswordKeeperElement)ObjectGroup.expandGroup(element);
               }

               conversionElement.changePassword(newPassword);
               ObjectGroup.createGroup(conversionElement);
               source.insertAt(i, conversionElement);
               source.remove(element);
            } catch (DecryptionException e) {
               Dialog.inform(PasswordKeeper.getString(4000));
            } catch (PasswordKeeperLockedException e) {
               Dialog.inform(PasswordKeeper.getString(4000));
            }
         }

         this._password = newPassword;
         PasswordKeeperOptions.getOptions().setHash(hash);
         Dialog.inform(PasswordKeeper.getString(3008));
      }
   }

   public final byte[] changePassword(byte[] element, byte[] password, byte[] salt) {
      return this.changePassword(element, this._password, password, salt);
   }

   public final byte[] changePassword(byte[] element, byte[] oldPassword, byte[] newPassword, byte[] salt) {
      String tempElement = this.decrypt(element, oldPassword, salt);
      return this.encrypt(tempElement, newPassword, salt);
   }

   public final String decrypt(byte[] element, byte[] salt) {
      return this.decrypt(element, this._password, salt);
   }

   public final String decrypt(byte[] param1, byte[] param2, byte[] param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 09
      // 04: aload 1
      // 05: arraylength
      // 06: ifne 0d
      // 09: ldc_w ""
      // 0c: areturn
      // 0d: aload 2
      // 0e: ifnonnull 19
      // 11: new net/rim/device/apps/internal/passwordkeeper/PasswordKeeperLockedException
      // 14: dup
      // 15: invokespecial net/rim/device/apps/internal/passwordkeeper/PasswordKeeperLockedException.<init> ()V
      // 18: athrow
      // 19: invokestatic net/rim/device/apps/internal/passwordkeeper/DecryptionCache.getInstance ()Lnet/rim/device/apps/internal/passwordkeeper/DecryptionCache;
      // 1c: astore 4
      // 1e: aload 4
      // 20: aload 1
      // 21: invokevirtual net/rim/device/apps/internal/passwordkeeper/DecryptionCache.get ([B)Ljava/lang/String;
      // 24: astore 5
      // 26: aload 5
      // 28: ifnonnull 77
      // 2b: new java/lang/Object
      // 2e: dup
      // 2f: aload 2
      // 30: aload 3
      // 31: bipush 3
      // 33: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.<init> ([B[BI)V
      // 36: astore 6
      // 38: bipush 32
      // 3a: newarray 8
      // 3c: astore 7
      // 3e: aload 6
      // 40: aload 7
      // 42: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.xorBytes ([B)V
      // 45: new java/lang/Object
      // 48: dup
      // 49: aload 7
      // 4b: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 4e: astore 8
      // 50: new java/lang/Object
      // 53: dup
      // 54: aload 8
      // 56: invokespecial net/rim/device/api/crypto/AESDecryptorEngine.<init> (Lnet/rim/device/api/crypto/AESKey;)V
      // 59: astore 9
      // 5b: new java/lang/Object
      // 5e: dup
      // 5f: aload 9
      // 61: invokespecial net/rim/device/api/crypto/PKCS5UnformatterEngine.<init> (Lnet/rim/device/api/crypto/BlockDecryptorEngine;)V
      // 64: astore 10
      // 66: aload 0
      // 67: aload 10
      // 69: aload 1
      // 6a: invokespecial net/rim/device/apps/internal/passwordkeeper/PasswordKeeperManager.decryptOperation (Lnet/rim/device/api/crypto/BlockUnformatterEngine;[B)Ljava/lang/String;
      // 6d: astore 5
      // 6f: aload 4
      // 71: aload 1
      // 72: aload 5
      // 74: invokevirtual net/rim/device/apps/internal/passwordkeeper/DecryptionCache.add ([BLjava/lang/String;)V
      // 77: aload 5
      // 79: areturn
      // 7a: astore 4
      // 7c: goto 81
      // 7f: astore 4
      // 81: new net/rim/device/apps/internal/passwordkeeper/DecryptionException
      // 84: dup
      // 85: invokespecial net/rim/device/apps/internal/passwordkeeper/DecryptionException.<init> ()V
      // 88: athrow
      // try (0 -> 6): 60 null
      // try (7 -> 59): 60 null
      // try (0 -> 6): 62 null
      // try (7 -> 59): 62 null
   }

   private final String decryptOperation(BlockUnformatterEngine engine, byte[] ciphertext) {
      BlockDecryptor decryptor = (BlockDecryptor)(new Object(engine, (InputStream)(new Object(ciphertext))));
      byte[] decrypted = new byte[0];
      byte[] temp = new byte[10];

      while (true) {
         int read = decryptor.read(temp, 0, 10);
         if (read < 0) {
            SHA1Digest digest = (SHA1Digest)(new Object());
            int digestLength = digest.getDigestLength();
            if (decrypted.length < digestLength) {
               throw new DecryptionException();
            } else {
               byte[] newCRC = this.getCRC(decrypted, 0, decrypted.length - digestLength);
               if (Arrays.equals(newCRC, 0, decrypted, decrypted.length - digestLength, digestLength)) {
                  return (String)(new Object(decrypted, 0, decrypted.length - digestLength, "UTF8"));
               } else {
                  throw new DecryptionException();
               }
            }
         }

         Array.resize(decrypted, read + decrypted.length);
         System.arraycopy(temp, 0, decrypted, decrypted.length - read, read);
      }
   }

   public final byte[] getCRC(byte[] data, int offset, int length) {
      SHA1Digest digest = (SHA1Digest)(new Object());
      digest.update(data, offset, length);
      return digest.getDigest();
   }

   public final byte[] encrypt(String element, byte[] salt) {
      return this.encrypt(element, this._password, salt);
   }

   public final byte[] encrypt(String param1, byte[] param2, byte[] param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 2
      // 01: ifnonnull 0c
      // 04: new net/rim/device/apps/internal/passwordkeeper/PasswordKeeperLockedException
      // 07: dup
      // 08: invokespecial net/rim/device/apps/internal/passwordkeeper/PasswordKeeperLockedException.<init> ()V
      // 0b: athrow
      // 0c: new java/lang/Object
      // 0f: dup
      // 10: aload 2
      // 11: aload 3
      // 12: bipush 3
      // 14: invokespecial net/rim/device/api/crypto/PKCS5KDF2PseudoRandomSource.<init> ([B[BI)V
      // 17: astore 4
      // 19: bipush 32
      // 1b: newarray 8
      // 1d: astore 5
      // 1f: aload 4
      // 21: aload 5
      // 23: invokevirtual net/rim/device/api/crypto/AbstractPseudoRandomSource.xorBytes ([B)V
      // 26: new java/lang/Object
      // 29: dup
      // 2a: aload 5
      // 2c: invokespecial net/rim/device/api/crypto/AESKey.<init> ([B)V
      // 2f: astore 6
      // 31: new java/lang/Object
      // 34: dup
      // 35: aload 6
      // 37: invokespecial net/rim/device/api/crypto/AESEncryptorEngine.<init> (Lnet/rim/device/api/crypto/AESKey;)V
      // 3a: astore 7
      // 3c: new java/lang/Object
      // 3f: dup
      // 40: aload 7
      // 42: invokespecial net/rim/device/api/crypto/PKCS5FormatterEngine.<init> (Lnet/rim/device/api/crypto/BlockEncryptorEngine;)V
      // 45: astore 8
      // 47: aload 0
      // 48: aload 8
      // 4a: aload 1
      // 4b: invokespecial net/rim/device/apps/internal/passwordkeeper/PasswordKeeperManager.encryptOperation (Lnet/rim/device/api/crypto/BlockFormatterEngine;Ljava/lang/String;)[B
      // 4e: areturn
      // 4f: astore 4
      // 51: goto 56
      // 54: astore 4
      // 56: new java/lang/Object
      // 59: dup
      // 5a: invokespecial java/lang/RuntimeException.<init> ()V
      // 5d: athrow
      // try (0 -> 38): 39 null
      // try (0 -> 38): 41 null
   }

   private final byte[] encryptOperation(BlockFormatterEngine engine, String plaintext) {
      ByteArrayOutputStream output = (ByteArrayOutputStream)(new Object());
      BlockEncryptor encryptor = (BlockEncryptor)(new Object(engine, output));
      byte[] plaintextBytes = plaintext.getBytes("UTF8");
      encryptor.write(plaintextBytes);
      encryptor.write(this.getCRC(plaintextBytes, 0, plaintextBytes.length));
      encryptor.close();
      return output.toByteArray();
   }
}
