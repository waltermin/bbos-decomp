package net.rim.device.cldc.io.file;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.crypto.SHA256Digest;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEUtilities;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.KeyProvider;
import net.rim.device.internal.io.file.RootRegister;

public final class FileSystemEncryption {
   public static String KEY_FILE = "/blackberry/system/info.mkf";
   private static final int DEFAULT_KEY_LENGTH = 32;
   private static final int CURRENT_VERSION = 1;
   private static final byte[] DEFAULT_ENCRYPTION_KEY = new byte[]{
      82, 23, 56, 121, 1, 5, 98, -93, 49, -76, -14, -16, -111, 54, -61, -18, -97, 81, -116, 67, 121, 32, 67, -38, -38, 20, 83, -121, -74, 56, 116, -100
   };
   static final long FILE_EVENT_LOGGER_GUID = 4782370668738403183L;
   private static final String FILE_EVENT_LOGGER_TITLE = "net.rim.file";
   private static final int FILE_SYSTEM_LOCKED = 1718840420;
   private static final int FILE_SYSTEM_NULL_USER_KEY = 1718840939;
   private static final int FILE_SYSTEM_BAD_LOCK_MODE = 1651272557;
   private static final int FILE_SYSTEM_MASTER_KEY_FROM_CACHE = 1835755107;
   private static final int FILE_SYSTEM_CIPHER_NOT_FOUND = 1668116070;
   private static final int FILE_SYSTEM_HASH_NOT_FOUND = 1752002150;
   private static final int FILE_SYSTEM_SALT_NOT_FOUND = 1936551526;
   static final int FILE_SYSTEM_LOCKED_TO_WRONG_DEVICE = 1819764580;
   private static final int FILE_SYSTEM_CREATING_NEW_KEY = 1717792363;
   private static final int FILE_SYSTEM_CHANGING_KEYING_PASSWORD = 1667788919;
   private static final int FILE_SYSTEM_CHANGING_LOCK_TYPE = 1667787892;
   private static final int FILE_SYSTEM_ERROR_CHANGING_LOCK_TYPE = 1667786092;
   private static final int FILE_SYSTEM_ERROR_CHANGING_LOCK_TYPE_IOE = 1667787881;
   private static final int FILE_SYSTEM_SKIPPING_CHANGING_LOCK_TYPE = 1667789676;

   private FileSystemEncryption() {
   }

   public static final void changePassword(String oldPassword, String newPassword) {
      EventLogger.logEvent(4782370668738403183L, 1667788919, 0);
      changeKeyFile(oldPassword, newPassword, false);
   }

   public static final void lockTypeChanged(String pwd) {
      EventLogger.logEvent(4782370668738403183L, 1667787892, 0);
      changeKeyFile(pwd, pwd, true);
   }

   private static final void changeKeyFile(String param0, String param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/internal/io/file/RootRegister.getInstance ()Lnet/rim/device/internal/io/file/RootRegister;
      // 03: astore 3
      // 04: aload 3
      // 05: invokevirtual net/rim/device/internal/io/file/RootRegister.isCardInserted ()Z
      // 08: ifne 17
      // 0b: ldc2_w 4782370668738403183
      // 0e: ldc_w 1667789676
      // 11: bipush 0
      // 12: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 15: pop
      // 16: return
      // 17: aconst_null
      // 18: astore 4
      // 1a: new java/lang/Object
      // 1d: dup
      // 1e: ldc_w "file:///SDCard"
      // 21: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 24: getstatic net/rim/device/cldc/io/file/FileSystemEncryption.KEY_FILE Ljava/lang/String;
      // 27: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 30: checkcast java/lang/Object
      // 33: astore 4
      // 35: aload 4
      // 37: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 3c: ifeq 89
      // 3f: aload 4
      // 41: aload 0
      // 42: ifnull 4c
      // 45: aload 0
      // 46: invokevirtual java/lang/String.getBytes ()[B
      // 49: goto 4d
      // 4c: aconst_null
      // 4d: invokestatic net/rim/device/cldc/io/file/FileSystemEncryption.readMasterKey (Ljavax/microedition/io/file/FileConnection;[B)Lnet/rim/device/cldc/io/file/MasterKeyFile;
      // 50: astore 5
      // 52: aload 5
      // 54: aload 4
      // 56: aload 1
      // 57: ifnull 61
      // 5a: aload 1
      // 5b: invokevirtual java/lang/String.getBytes ()[B
      // 5e: goto 62
      // 61: aconst_null
      // 62: iload 2
      // 63: ifeq 6c
      // 66: invokestatic net/rim/device/cldc/io/file/FileSystemEncryption.getLockType ()I
      // 69: goto 71
      // 6c: aload 5
      // 6e: getfield net/rim/device/cldc/io/file/MasterKeyFile._lockType I
      // 71: invokestatic net/rim/device/cldc/io/file/FileSystemEncryption.writeKey (Lnet/rim/device/cldc/io/file/MasterKeyFile;Ljavax/microedition/io/file/FileConnection;[BI)V
      // 74: aload 3
      // 75: aload 5
      // 77: getfield net/rim/device/cldc/io/file/MasterKeyFile._masterKey [B
      // 7a: aload 4
      // 7c: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 81: aload 5
      // 83: getfield net/rim/device/cldc/io/file/MasterKeyFile._lockType I
      // 86: invokevirtual net/rim/device/internal/io/file/RootRegister.setCachedMasterKey ([BJI)V
      // 89: aload 4
      // 8b: ifnull e9
      // 8e: aload 4
      // 90: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 95: return
      // 96: astore 5
      // 98: return
      // 99: astore 5
      // 9b: ldc2_w 4782370668738403183
      // 9e: ldc_w 1667787881
      // a1: bipush 0
      // a2: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // a5: pop
      // a6: aload 4
      // a8: ifnull e9
      // ab: aload 4
      // ad: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b2: return
      // b3: astore 5
      // b5: return
      // b6: astore 5
      // b8: ldc2_w 4782370668738403183
      // bb: ldc_w 1667786092
      // be: bipush 0
      // bf: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // c2: pop
      // c3: aload 4
      // c5: ifnull e9
      // c8: aload 4
      // ca: invokeinterface javax/microedition/io/Connection.close ()V 1
      // cf: return
      // d0: astore 5
      // d2: return
      // d3: astore 6
      // d5: aload 4
      // d7: ifnull e6
      // da: aload 4
      // dc: invokeinterface javax/microedition/io/Connection.close ()V 1
      // e1: goto e6
      // e4: astore 7
      // e6: aload 6
      // e8: athrow
      // e9: return
      // try (60 -> 62): 63 null
      // try (13 -> 58): 65 null
      // try (73 -> 75): 76 null
      // try (13 -> 58): 78 null
      // try (86 -> 88): 89 null
      // try (13 -> 58): 91 null
      // try (65 -> 71): 91 null
      // try (78 -> 84): 91 null
      // try (94 -> 96): 97 null
      // try (91 -> 92): 91 null
   }

   public static final boolean lockTypeSufficient(int lockType) {
      switch (getLockType()) {
         case 0:
         default:
            return true;
         case 1:
            if (lockType != 1 && lockType != 2) {
               return false;
            }

            return true;
         case 2:
            if (lockType == 2) {
               return true;
            }

            return false;
         case 3:
            return lockType == 3 || lockType == 2;
      }
   }

   private static final int getLockType() {
      switch (FileSystemOptions.getExternalEncryptionLevel()) {
         case 0:
         default:
            return 4;
         case 1:
         case 2:
            return 3;
         case 3:
         case 4:
            return 1;
         case 5:
         case 6:
            return 2;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final MasterKeyFile getMasterKeyFile(FileConnection conn) {
      InputStream in = null;
      boolean var17 = false /* VF: Semaphore variable */;

      MasterKeyFile var4;
      try {
         var17 = true;
         in = conn.openDataInputStream();
         byte[] fileData = IOUtilities.streamToBytes(in);
         MasterKeyFile fr = new MasterKeyFile(conn.lastModified());
         TLEUtilities.parseBuffer((DataBuffer)(new Object(fileData, 0, fileData.length, true)), fr);
         if (fr._cipher == null) {
            EventLogger.logEvent(4782370668738403183L, 1668116070, 5);
            throw new Object("Ciphering material not found");
         }

         if (fr._salt == null) {
            EventLogger.logEvent(4782370668738403183L, 1936551526, 5);
            throw new Object("Salt material not found");
         }

         var4 = fr;
         var17 = false;
      } finally {
         if (var17) {
            if (in != null) {
               label102:
               try {
                  in.close();
               } finally {
                  break label102;
               }
            }
         }
      }

      if (in != null) {
         try {
            in.close();
         } finally {
            return var4;
         }
      }

      return var4;
   }

   private static final MasterKeyFile readMasterKey(FileConnection conn, byte[] pwd) {
      MasterKeyFile fr = getMasterKeyFile(conn);
      if (fr.isDeviceLocked() && !fr.keyFileMatchesDevice()) {
         EventLogger.logEvent(4782370668738403183L, 1819764580, 0);
         throw new Object("Ciphering material does not match this device");
      } else {
         RootRegister register = RootRegister.getInstance();
         fr.decryptMasterKey(pwd);
         register.setCachedMasterKey(fr._masterKey, conn.lastModified(), fr._lockType);
         return fr;
      }
   }

   static final byte[] getDeviceKeyHash() {
      KeyProvider keyProvider = RootRegister.getInstance().getKeyProvider();
      SHA256Digest digest = (SHA256Digest)(new Object());
      digest.update(keyProvider.getDeviceKey());
      return digest.getDigest();
   }

   private static final void writeKey(MasterKeyFile fr, FileConnection conn, byte[] pwd, int lockType) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   static final byte[] getUserKey(byte[] pwd, byte[] salt, int mode) {
      KeyProvider keyProvider = RootRegister.getInstance().getKeyProvider();
      byte[] userKey;
      if (mode != 2 && mode != 3) {
         if (mode == 4) {
            userKey = DEFAULT_ENCRYPTION_KEY;
         } else {
            if (mode != 1) {
               EventLogger.logEvent(4782370668738403183L, 1651272557, 5);
               throw new Object("Bad lock mode");
            }

            userKey = new byte[32];
         }
      } else {
         if (pwd != null) {
            userKey = keyProvider.getUserKey(pwd, salt);
         } else {
            userKey = keyProvider.getUserKey(salt);
         }

         if (userKey == null) {
            if (ApplicationManager.getApplicationManager().isSystemLocked()) {
               EventLogger.logEvent(4782370668738403183L, 1718840420, 0);
               throw new Object("System is locked, trying to access encrypted content");
            }

            EventLogger.logEvent(4782370668738403183L, 1718840939, 5);
            throw new Object("User key null");
         }
      }

      if (mode == 2 || mode == 1) {
         byte[] deviceKey = keyProvider.getDeviceKey();

         for (int i = 0; i < deviceKey.length; i++) {
            userKey[i] ^= deviceKey[i];
         }
      }

      return userKey;
   }

   public static final MasterKeyFile getKeyFile() {
      FileConnection conn = null;

      try {
         conn = (FileConnection)Connector.open(((StringBuffer)(new Object("file:///SDCard"))).append(KEY_FILE).toString());
         if (conn.exists()) {
            return getMasterKeyFile(conn);
         }
      } finally {
         return null;
      }

      return null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final byte[] getMasterKeyInternal(boolean checkPermissions) {
      FileConnection conn = null;
      RootRegister register = RootRegister.getInstance();
      boolean var42 = false /* VF: Semaphore variable */;

      byte[] var52;
      label362: {
         label363: {
            try {
               var42 = true;
               synchronized (register) {
                  conn = (FileConnection)Connector.open(((StringBuffer)(new Object("file:///SDCard"))).append(KEY_FILE).toString());
                  long lastMod = conn.lastModified();
                  byte[] masterKey = register.getCachedMasterKey(lastMod);
                  if (masterKey != null) {
                     if (checkPermissions && !lockTypeSufficient(register.getCachedLockType())) {
                        throw new Object("Key file insuffient lock access");
                     }

                     EventLogger.logEvent(4782370668738403183L, 1835755107, 5);
                     var52 = masterKey;
                     var42 = false;
                     break label362;
                  }

                  if (conn.exists()) {
                     MasterKeyFile fr = readMasterKey(conn, null);
                     if (checkPermissions && !lockTypeSufficient(fr._lockType)) {
                        throw new Object("Key file insuffient lock access");
                     }

                     var52 = fr._masterKey;
                     var42 = false;
                     break label363;
                  }

                  EventLogger.logEvent(4782370668738403183L, 1717792363, 0);
                  MasterKeyFile fr = new MasterKeyFile(System.currentTimeMillis());
                  fr._salt = RandomSource.getBytes(32);
                  fr._cipherAlgorithm = 1;
                  fr._masterKey = RandomSource.getBytes(32);
                  fr._version = 1;
                  writeKey(fr, conn, null, getLockType());
                  register.setCachedMasterKey(fr._masterKey, conn.lastModified(), fr._lockType);
                  var52 = fr._masterKey;
                  var42 = false;
               }
            } finally {
               if (var42) {
                  if (conn != null) {
                     label320:
                     try {
                        conn.close();
                     } finally {
                        break label320;
                     }
                  }
               }
            }

            if (conn != null) {
               try {
                  conn.close();
               } finally {
                  return var52;
               }
            }

            return var52;
         }

         if (conn != null) {
            try {
               conn.close();
            } finally {
               return var52;
            }
         }

         return var52;
      }

      if (conn != null) {
         try {
            conn.close();
         } finally {
            return var52;
         }
      }

      return var52;
   }

   static final byte[] getMasterKey(boolean param0) throws IOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 0
      // 01: invokestatic net/rim/device/cldc/io/file/FileSystemEncryption.getMasterKeyInternal (Z)[B
      // 04: areturn
      // 05: astore 1
      // 06: aload 1
      // 07: athrow
      // 08: astore 1
      // 09: new java/lang/Object
      // 0c: dup
      // 0d: ldc_w "Failed to read key file"
      // 10: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 13: athrow
      // try (0 -> 2): 3 null
      // try (0 -> 2): 6 null
   }

   static {
      EventLogger.register(4782370668738403183L, "net.rim.file", 2);
   }
}
