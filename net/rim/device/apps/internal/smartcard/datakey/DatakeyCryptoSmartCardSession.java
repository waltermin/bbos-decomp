package net.rim.device.apps.internal.smartcard.datakey;

import java.util.Vector;
import net.rim.device.api.crypto.CryptoByteArrayArithmetic;
import net.rim.device.api.crypto.CryptoSmartCardKeyStoreData;
import net.rim.device.api.crypto.CryptoSmartCardSession;
import net.rim.device.api.crypto.CryptoTokenCryptoSystemData;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.CommandAPDU;
import net.rim.device.api.smartcard.CommandAPDUGroup;
import net.rim.device.api.smartcard.ResponseAPDU;
import net.rim.device.api.smartcard.ResponseAPDUGroup;
import net.rim.device.api.smartcard.SmartCard;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class DatakeyCryptoSmartCardSession extends CryptoSmartCardSession implements DatakeyPKCS11Constants {
   private static final byte CLASS_80;
   private static final byte CLASS_00;
   private static final byte INS_RSA_SIGN;
   private static final byte INS_RSA_DECRYPT;
   private static final byte RSA_SIGN_PKCS1_v1_0;
   private static final byte RSA_SIGN_PKCS1_v1_5;
   private static final byte RSA_SIGN_RAW;
   private static final byte RSA_DECRYPT_PKCS1_v1_0;
   private static final byte RSA_DECRYPT_PKCS1_v2_0;
   private static final byte RSA_DECRYPT_RAW;
   private static final byte RSA_DECRYPT_WRAPPED_KEY;
   private static final byte PIN_TYPE_USER;
   private static final byte PIN_TYPE_SO;
   private static final byte ZERO_BYTE;
   private static final byte COMMAND_COMPLETE;
   private static final int PWD_LENGTH;
   private static final int MAX_RANDOM_BYTES;
   private static final boolean DEBUG;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   private static final long UID_INFO_ID;
   static DatakeyCryptoSmartCardSession$UIDInfo _uidInfo;

   protected DatakeyCryptoSmartCardSession(SmartCard smartCard, SmartCardReaderSession readerSession) {
   }

   @Override
   protected void closeImpl() {
   }

   @Override
   protected int getMaxLoginAttemptsImpl() {
      throw new Object();
   }

   @Override
   protected int getRemainingLoginAttemptsImpl() {
      throw new Object();
   }

   static CommandAPDU getSelectFileCommand(byte[] file) {
      return (CommandAPDU)(new Object((byte)0, (byte)-92, (byte)8, (byte)0, file, 11));
   }

   private DatakeyFileHeader selectFile(byte[] file) {
      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(getSelectFileCommand(file), response);
      if (response.checkStatusWords((byte)106, (byte)-126)) {
         return null;
      }

      if (response.checkStatusWords((byte)-112, (byte)0)) {
         byte[] fileHeaderData = Arrays.copy(response.getData());
         if (fileHeaderData == null) {
            throw new Object("No file header data");
         } else {
            return new DatakeyFileHeader(fileHeaderData);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   protected boolean loginImpl(String password) {
      CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)32, (byte)1, (byte)0));
      byte[] passwordBytes = password.getBytes();
      int passwordLength = passwordBytes.length;
      if (passwordLength < 20) {
         Array.resize(passwordBytes, 20);
         Arrays.fill(passwordBytes, (byte)32, passwordLength, 20 - passwordLength);
      }

      boolean doublePINHashingEnabled = false;
      SmartCardID id = this.getSmartCardID();
      if (id != null && id.getID() == _uidInfo._uid) {
         doublePINHashingEnabled = _uidInfo._doublePINHashingEnabled;
      } else {
         doublePINHashingEnabled = this.isDoubleHashingEnabled();
         if (id != null) {
            _uidInfo._uid = id.getID();
            _uidInfo._doublePINHashingEnabled = doublePINHashingEnabled;
         }
      }

      if (doublePINHashingEnabled) {
         SHA1Digest digest = (SHA1Digest)(new Object());
         digest.update(passwordBytes);
         passwordBytes = digest.getDigest();
         digest.reset();
         digest.update(passwordBytes);
         passwordBytes = digest.getDigest();
      }

      command.setLcData(passwordBytes);
      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(command, response);
      if (response.checkStatusWords((byte)-112, (byte)0)) {
         return true;
      } else if (response.checkStatusWords((byte)100, (byte)-8)) {
         throw new Object();
      } else {
         return false;
      }
   }

   private boolean isDoubleHashingEnabled() {
      try {
         DatakeyFile file = this.getFile(new byte[]{0, 31});
         if (file != null) {
            byte[] contents = file.getContents();
            if (contents != null && contents.length >= 2) {
               return contents[1] > 0;
            }
         }
      } finally {
         return false;
      }

      return false;
   }

   @Override
   protected byte[] getRandomBytesImpl(int maxBytes) {
      maxBytes = Math.min(maxBytes, 1524);
      CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)76, (byte)0, (byte)0, maxBytes));
      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(command, response);
      return response.checkStatusWords((byte)-112, (byte)0) ? response.getData() : null;
   }

   private DatakeyFile getFile(byte[] file) {
      DatakeyFileHeader fileHeader = this.selectFile(file);
      if (fileHeader == null) {
         return null;
      }

      CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)-80, (byte)0, (byte)0));
      ResponseAPDU response = (ResponseAPDU)(new Object());
      int offset = 0;
      byte[] data = new byte[0];

      while (true) {
         byte offsetHigh = (byte)((offset & 0xFF00) >> 8);
         byte offsetLow = (byte)(offset & 0xFF);
         command.set((byte)0, (byte)-80, offsetHigh, offsetLow, 0);
         this.sendAPDU(command, response);
         byte[] moreData = response.getData();
         if (response.checkStatusWords((byte)105, (byte)-126)) {
            throw new Object();
         }

         if (!response.checkStatusWords((byte)-112, (byte)0)) {
            if (response.checkStatusWords((byte)98, (byte)-126)) {
               if (moreData != null) {
                  int oldLength = data.length;
                  Array.resize(data, oldLength + moreData.length);
                  System.arraycopy(moreData, 0, data, oldLength, moreData.length);
               }

               if (fileHeader.getFileType() == 0) {
                  return new PKCS11DatakeyFile(fileHeader, data);
               }

               return new DatakeyFile(fileHeader, data);
            }

            throw new Object();
         }

         if (moreData != null) {
            int oldLength = data.length;
            Array.resize(data, oldLength + moreData.length);
            System.arraycopy(moreData, 0, data, oldLength, moreData.length);
            offset = data.length;
         }
      }
   }

   private Vector findMDFiles(int numFiles) {
      CommandAPDU[] commands = new Object[numFiles];

      for (int i = 0; i < numFiles; i++) {
         commands[i] = (CommandAPDU)(new Object((byte)0, (byte)-80, (byte)0, (byte)i, 11));
      }

      CommandAPDUGroup commandAPDUGroup = (CommandAPDUGroup)(new Object(commands));
      ResponseAPDUGroup responseAPDUGroup = (ResponseAPDUGroup)(new Object(numFiles));
      this.sendAPDUs(commandAPDUGroup, responseAPDUGroup);
      Vector files = (Vector)(new Object());

      for (int i = 0; i < numFiles; i++) {
         ResponseAPDU response = responseAPDUGroup.getAPDU(i);
         if (response.checkStatusWords((byte)-112, (byte)0)) {
            byte[] tempData = response.getData();
            if (tempData != null && tempData.length == 11) {
               byte[] fileID = new byte[2];
               System.arraycopy(tempData, 2, fileID, 0, 2);
               if (this.fileIDToInt(fileID) >= 8192) {
                  files.addElement(fileID);
               }
            }
         }
      }

      return files;
   }

   private int fileIDToInt(byte[] fileID) {
      int fileIDInt = 0;
      fileIDInt |= fileID[0] & 255;
      return fileIDInt | fileID[1] << 8 & 0xFF00;
   }

   @Override
   protected CryptoSmartCardKeyStoreData[] getKeyStoreDataArrayImpl() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: anewarray 745
      // 004: astore 1
      // 005: aload 0
      // 006: getstatic net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 009: bipush 21
      // 00b: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 00e: bipush 0
      // 00f: bipush 1
      // 010: invokevirtual net/rim/device/api/smartcard/SmartCardSession.loginPrompt (Ljava/lang/String;IZ)V
      // 013: new java/lang/Object
      // 016: dup
      // 017: invokespecial net/rim/device/api/util/MultiMap.<init> ()V
      // 01a: astore 2
      // 01b: aload 0
      // 01c: aconst_null
      // 01d: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession.selectFile ([B)Lnet/rim/device/apps/internal/smartcard/datakey/DatakeyFileHeader;
      // 020: astore 3
      // 021: aload 3
      // 022: ifnonnull 027
      // 025: aconst_null
      // 026: areturn
      // 027: aload 3
      // 028: invokevirtual net/rim/device/apps/internal/smartcard/datakey/DatakeyFileHeader.getHighWaterMark ()I
      // 02b: istore 4
      // 02d: iload 4
      // 02f: bipush 2
      // 031: iadd
      // 032: istore 5
      // 034: bipush 0
      // 035: istore 6
      // 037: aload 0
      // 038: bipush 0
      // 039: invokestatic net/rim/device/apps/internal/api/crypto/CryptoCommonResources.getString (I)Ljava/lang/String;
      // 03c: iload 5
      // 03e: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.displayProgressDialog (Ljava/lang/String;I)V
      // 041: aload 0
      // 042: iload 4
      // 044: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession.findMDFiles (I)Ljava/util/Vector;
      // 047: astore 7
      // 049: aload 0
      // 04a: bipush 2
      // 04c: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 04f: iinc 6 2
      // 052: aload 7
      // 054: invokevirtual java/util/Vector.elements ()Ljava/util/Enumeration;
      // 057: astore 8
      // 059: aload 8
      // 05b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 060: ifne 066
      // 063: goto 1fa
      // 066: aload 8
      // 068: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 06d: checkcast [B
      // 070: astore 9
      // 072: aload 0
      // 073: aload 9
      // 075: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoSmartCardSession.getFile ([B)Lnet/rim/device/apps/internal/smartcard/datakey/DatakeyFile;
      // 078: astore 10
      // 07a: aload 10
      // 07c: ifnonnull 082
      // 07f: goto 059
      // 082: aload 10
      // 084: dup
      // 085: instanceof net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile
      // 088: ifne 08f
      // 08b: pop
      // 08c: goto 1e5
      // 08f: checkcast net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile
      // 092: astore 11
      // 094: aload 11
      // 096: bipush 0
      // 097: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 09a: astore 12
      // 09c: aload 12
      // 09e: ifnull 124
      // 0a1: aload 12
      // 0a3: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getIntValue ()I
      // 0a6: bipush 3
      // 0a8: if_icmpne 124
      // 0ab: aload 11
      // 0ad: sipush 256
      // 0b0: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 0b3: astore 13
      // 0b5: aload 13
      // 0b7: ifnonnull 0bd
      // 0ba: goto 1e5
      // 0bd: aload 13
      // 0bf: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getIntValue ()I
      // 0c2: lookupswitch 58 1 0 18
      // 0d4: aload 11
      // 0d6: sipush 288
      // 0d9: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 0dc: astore 14
      // 0de: aload 14
      // 0e0: ifnonnull 0e6
      // 0e3: goto 1e5
      // 0e6: aload 2
      // 0e7: new java/lang/Object
      // 0ea: dup
      // 0eb: aload 14
      // 0ed: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 0f0: invokespecial net/rim/device/internal/util/ByteArray.<init> ([B)V
      // 0f3: aload 11
      // 0f5: invokevirtual net/rim/device/api/util/MultiMap.add (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 0f8: pop
      // 0f9: goto 1e5
      // 0fc: aload 11
      // 0fe: sipush 258
      // 101: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 104: astore 14
      // 106: aload 14
      // 108: ifnonnull 10e
      // 10b: goto 1e5
      // 10e: aload 2
      // 10f: new java/lang/Object
      // 112: dup
      // 113: aload 14
      // 115: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 118: invokespecial net/rim/device/internal/util/ByteArray.<init> ([B)V
      // 11b: aload 11
      // 11d: invokevirtual net/rim/device/api/util/MultiMap.add (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 120: pop
      // 121: goto 1e5
      // 124: aload 11
      // 126: bipush 17
      // 128: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 12b: astore 13
      // 12d: aload 13
      // 12f: ifnonnull 135
      // 132: goto 059
      // 135: bipush 0
      // 136: anewarray 919
      // 139: astore 14
      // 13b: aload 12
      // 13d: ifnull 160
      // 140: aload 12
      // 142: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getIntValue ()I
      // 145: bipush 1
      // 146: if_icmpne 160
      // 149: aconst_null
      // 14a: aload 13
      // 14c: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 14f: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.readCertificateFile (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 152: astore 15
      // 154: aload 15
      // 156: ifnull 160
      // 159: aload 14
      // 15b: aload 15
      // 15d: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 160: bipush 0
      // 161: istore 16
      // 163: iload 16
      // 165: aload 14
      // 167: arraylength
      // 168: if_icmpge 1e5
      // 16b: aload 14
      // 16d: iload 16
      // 16f: aaload
      // 170: astore 17
      // 172: aload 17
      // 174: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 179: astore 18
      // 17b: aload 18
      // 17d: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 182: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAlgorithm ()Ljava/lang/String; 1
      // 187: astore 19
      // 189: aload 19
      // 18b: ldc_w "RSA"
      // 18e: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 191: ifeq 1b1
      // 194: aload 18
      // 196: checkcast java/lang/Object
      // 199: astore 20
      // 19b: aload 2
      // 19c: new java/lang/Object
      // 19f: dup
      // 1a0: aload 20
      // 1a2: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getN ()[B
      // 1a5: invokespecial net/rim/device/internal/util/ByteArray.<init> ([B)V
      // 1a8: aload 17
      // 1aa: invokevirtual net/rim/device/api/util/MultiMap.add (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 1ad: pop
      // 1ae: goto 1dd
      // 1b1: aload 11
      // 1b3: sipush 258
      // 1b6: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 1b9: astore 20
      // 1bb: aload 20
      // 1bd: ifnull 1dd
      // 1c0: aload 2
      // 1c1: new java/lang/Object
      // 1c4: dup
      // 1c5: aload 20
      // 1c7: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 1ca: invokespecial net/rim/device/internal/util/ByteArray.<init> ([B)V
      // 1cd: aload 17
      // 1cf: invokevirtual net/rim/device/api/util/MultiMap.add (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 1d2: pop
      // 1d3: goto 1dd
      // 1d6: astore 18
      // 1d8: goto 1dd
      // 1db: astore 18
      // 1dd: iinc 16 1
      // 1e0: goto 163
      // 1e3: astore 9
      // 1e5: iinc 6 1
      // 1e8: iload 6
      // 1ea: iload 5
      // 1ec: if_icmplt 1f2
      // 1ef: goto 059
      // 1f2: aload 0
      // 1f3: bipush 1
      // 1f4: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 1f7: goto 059
      // 1fa: aconst_null
      // 1fb: astore 9
      // 1fd: aload 2
      // 1fe: invokevirtual net/rim/device/api/util/MultiMap.keys ()Ljava/util/Enumeration;
      // 201: astore 10
      // 203: aload 10
      // 205: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 20a: ifne 210
      // 20d: goto 3eb
      // 210: aload 10
      // 212: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 217: checkcast java/lang/Object
      // 21a: astore 11
      // 21c: aload 2
      // 21d: aload 11
      // 21f: invokevirtual net/rim/device/api/util/MultiMap.elements (Ljava/lang/Object;)Ljava/util/Enumeration;
      // 222: astore 12
      // 224: aconst_null
      // 225: astore 13
      // 227: aconst_null
      // 228: astore 14
      // 22a: aload 12
      // 22c: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 231: ifne 237
      // 234: goto 3a9
      // 237: aload 12
      // 239: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 23e: astore 15
      // 240: aload 15
      // 242: dup
      // 243: instanceof java/lang/Object
      // 246: ifne 24d
      // 249: pop
      // 24a: goto 255
      // 24d: checkcast java/lang/Object
      // 250: astore 13
      // 252: goto 22a
      // 255: aload 15
      // 257: dup
      // 258: instanceof java/lang/Object
      // 25b: ifne 262
      // 25e: pop
      // 25f: goto 26a
      // 262: checkcast java/lang/Object
      // 265: astore 14
      // 267: goto 22a
      // 26a: aload 15
      // 26c: dup
      // 26d: instanceof net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile
      // 270: ifne 277
      // 273: pop
      // 274: goto 22a
      // 277: checkcast net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile
      // 27a: astore 16
      // 27c: aload 16
      // 27e: ldc_w -2147483647
      // 281: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 284: astore 17
      // 286: aload 17
      // 288: ifnonnull 28e
      // 28b: goto 22a
      // 28e: aload 9
      // 290: ifnonnull 299
      // 293: aload 0
      // 294: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 297: astore 9
      // 299: aload 17
      // 29b: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 29e: astore 18
      // 2a0: aload 18
      // 2a2: bipush 0
      // 2a3: baload
      // 2a4: istore 19
      // 2a6: aload 18
      // 2a8: bipush 0
      // 2a9: aload 18
      // 2ab: bipush 1
      // 2ac: baload
      // 2ad: bastore
      // 2ae: aload 18
      // 2b0: bipush 1
      // 2b1: iload 19
      // 2b3: bastore
      // 2b4: aload 16
      // 2b6: sipush 256
      // 2b9: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 2bc: astore 21
      // 2be: aload 21
      // 2c0: ifnull 22a
      // 2c3: aload 21
      // 2c5: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getIntValue ()I
      // 2c8: tableswitch 28 -1 1 -158 28 107
      // 2e4: aload 16
      // 2e6: sipush 288
      // 2e9: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 2ec: astore 22
      // 2ee: aload 22
      // 2f0: ifnonnull 2f6
      // 2f3: goto 22a
      // 2f6: new net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 2f9: dup
      // 2fa: aload 9
      // 2fc: aload 18
      // 2fe: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;[B)V
      // 301: astore 20
      // 303: aload 22
      // 305: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getLength ()I
      // 308: bipush 8
      // 30a: imul
      // 30b: istore 23
      // 30d: new net/rim/device/apps/internal/smartcard/datakey/DatakeyRSACryptoToken
      // 310: dup
      // 311: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyRSACryptoToken.<init> ()V
      // 314: astore 24
      // 316: new java/lang/Object
      // 319: dup
      // 31a: aload 24
      // 31c: iload 23
      // 31e: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 321: astore 25
      // 323: new java/lang/Object
      // 326: dup
      // 327: aload 25
      // 329: aload 20
      // 32b: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 32e: astore 14
      // 330: goto 22a
      // 333: aload 16
      // 335: ldc_w -2147483643
      // 338: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11DatakeyFile.getAttribute (I)Lnet/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute;
      // 33b: astore 23
      // 33d: aload 23
      // 33f: ifnonnull 345
      // 342: goto 22a
      // 345: aload 23
      // 347: invokevirtual net/rim/device/apps/internal/smartcard/datakey/PKCS11Attribute.getByteArrayValue ()[B
      // 34a: astore 24
      // 34c: aload 24
      // 34e: bipush 0
      // 34f: baload
      // 350: istore 19
      // 352: aload 24
      // 354: bipush 0
      // 355: aload 24
      // 357: bipush 1
      // 358: baload
      // 359: bastore
      // 35a: aload 24
      // 35c: bipush 1
      // 35d: iload 19
      // 35f: bastore
      // 360: new net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData
      // 363: dup
      // 364: aload 9
      // 366: aload 18
      // 368: aload 24
      // 36a: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;[B[B)V
      // 36d: astore 20
      // 36f: new net/rim/device/apps/internal/smartcard/datakey/DatakeyDSACryptoToken
      // 372: dup
      // 373: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyDSACryptoToken.<init> ()V
      // 376: astore 25
      // 378: new java/lang/Object
      // 37b: dup
      // 37c: aload 25
      // 37e: new net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenCryptoSystemData
      // 381: dup
      // 382: invokespecial net/rim/device/apps/internal/smartcard/datakey/DatakeyCryptoTokenCryptoSystemData.<init> ()V
      // 385: invokespecial net/rim/device/api/crypto/DSACryptoSystem.<init> (Lnet/rim/device/api/crypto/DSACryptoToken;Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;)V
      // 388: astore 26
      // 38a: new java/lang/Object
      // 38d: dup
      // 38e: aload 26
      // 390: aload 20
      // 392: invokespecial net/rim/device/api/crypto/DSAPrivateKey.<init> (Lnet/rim/device/api/crypto/DSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 395: astore 14
      // 397: goto 22a
      // 39a: astore 21
      // 39c: goto 22a
      // 39f: astore 21
      // 3a1: goto 22a
      // 3a4: astore 21
      // 3a6: goto 22a
      // 3a9: aload 13
      // 3ab: ifnull 3d6
      // 3ae: aload 1
      // 3af: aload 1
      // 3b0: arraylength
      // 3b1: bipush 1
      // 3b2: iadd
      // 3b3: invokestatic net/rim/vm/Array.resize (Ljava/lang/Object;I)V
      // 3b6: aload 1
      // 3b7: aload 1
      // 3b8: arraylength
      // 3b9: bipush 1
      // 3ba: isub
      // 3bb: new java/lang/Object
      // 3be: dup
      // 3bf: aconst_null
      // 3c0: aload 13
      // 3c2: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSubjectFriendlyName ()Ljava/lang/String; 1
      // 3c7: aload 14
      // 3c9: aconst_null
      // 3ca: bipush 2
      // 3cc: aload 13
      // 3ce: aconst_null
      // 3cf: aconst_null
      // 3d0: bipush 0
      // 3d1: i2l
      // 3d2: invokespecial net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Lnet/rim/device/api/crypto/SymmetricKey;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/PublicKey;J)V
      // 3d5: aastore
      // 3d6: iinc 6 1
      // 3d9: iload 6
      // 3db: iload 5
      // 3dd: if_icmplt 3e3
      // 3e0: goto 203
      // 3e3: aload 0
      // 3e4: bipush 1
      // 3e5: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 3e8: goto 203
      // 3eb: iinc 6 1
      // 3ee: iload 6
      // 3f0: iload 5
      // 3f2: if_icmpgt 408
      // 3f5: aload 0
      // 3f6: bipush 1
      // 3f7: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 3fa: bipush 100
      // 3fc: i2l
      // 3fd: invokestatic java/lang/Thread.sleep (J)V
      // 400: goto 3eb
      // 403: astore 11
      // 405: goto 3eb
      // 408: aload 0
      // 409: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.dismissProgressDialog ()V
      // 40c: aload 1
      // 40d: areturn
      // try (160 -> 199): 200 null
      // try (160 -> 199): 202 null
      // try (51 -> 61): 205 null
      // try (62 -> 130): 205 null
      // try (131 -> 205): 205 null
      // try (298 -> 392): 392 null
      // try (298 -> 392): 394 null
      // try (298 -> 392): 396 null
      // try (442 -> 445): 446 null
   }

   public void sha1Hash() {
      byte[] dataToHash = new byte[1];
      Arrays.fill(dataToHash, (byte)-86);
      CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)-128, (byte)5, (byte)0, dataToHash, 20));
      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(command, response);
      if (!response.checkStatusWords((byte)-112, (byte)0)) {
         ;
      }
   }

   private byte[] getSignBytes(byte[] input, int inputOffset) {
      int length = input.length - inputOffset;
      int encodedMessageOffset = 0;
      if (input[encodedMessageOffset++] == 0 && input[encodedMessageOffset++] == 1) {
         int paddingLength;
         for (paddingLength = 0; input[encodedMessageOffset] == -1 && encodedMessageOffset < length; paddingLength++) {
            encodedMessageOffset++;
         }

         if (encodedMessageOffset != length && input[encodedMessageOffset++] == 0 && paddingLength >= 8) {
            int digestDataLength = length - encodedMessageOffset;
            byte[] rawData = new byte[digestDataLength];
            System.arraycopy(input, encodedMessageOffset, rawData, 0, digestDataLength);
            return rawData;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void pkcs1Format(byte[] input, byte[] output, int outputOffset, int outputBlockLength) {
      if (input != null && output != null && outputOffset >= 0 && output.length - outputBlockLength >= outputOffset) {
         int inputLength = input.length;
         if (inputLength > outputBlockLength - 11) {
            throw new Object("Message too long");
         }

         output[outputOffset++] = 0;
         output[outputOffset++] = 2;
         int randomDataLength = outputBlockLength - (3 + inputLength);
         Arrays.fill(output, (byte)-1, outputOffset, randomDataLength);
         outputOffset += randomDataLength;
         output[outputOffset++] = 0;
         System.arraycopy(input, 0, output, outputOffset, inputLength);
      } else {
         throw new Object("Illegal argument");
      }
   }

   void signDecryptRSA(
      RSACryptoSystem cryptoSystem, DatakeyCryptoTokenData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset, int operation
   ) {
      if (cryptoSystem != null && privateKeyData != null && input != null && output != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (input.length >= inputOffset + modulusLength && output.length >= outputOffset + modulusLength) {
            CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)(operation == 1 ? 90 : 84), (byte)(operation == 1 ? 1 : 1), (byte)0, null));
            command.appendLcData(new byte[]{63, 0});
            command.appendLcData(privateKeyData.getPrivateFile());
            int dataOffset = 0;
            byte[] data;
            int dataLength;
            if (operation == 1) {
               data = this.getSignBytes(input, inputOffset);
               if (data == null) {
                  throw new Object("Unsupported data formatting");
               }

               dataLength = data.length;
            } else {
               data = input;
               dataOffset = inputOffset;
               dataLength = input.length - inputOffset;
            }

            byte dataLengthHigh = (byte)((dataLength & 0xFF00) >> 8);
            byte dataLengthLow = (byte)(dataLength & 0xFF);
            command.appendLcData(new byte[]{dataLengthHigh, dataLengthLow});
            command.appendLcData(data, dataOffset, dataLength);
            if (operation == 2) {
               command.appendLcData(new byte[]{0, 0, 0, 0});
            }

            command.setLe(modulusLength);
            ResponseAPDU response = (ResponseAPDU)(new Object());
            this.sendAPDU(command, response);
            if (response.checkStatusWords((byte)-112, (byte)0)) {
               byte[] responseData = response.getData();
               if (responseData == null || responseData.length <= 0) {
                  throw new Object("Not enough response data received from the smart card.");
               }

               if (operation == 2) {
                  this.pkcs1Format(responseData, output, outputOffset, modulusLength);
               } else {
                  System.arraycopy(responseData, 0, output, outputOffset, responseData.length);
               }
            } else {
               throw new Object(
                  ((StringBuffer)(new Object("Invalid response. SW1: ")))
                     .append(Integer.toHexString(response.getSW1() & 255))
                     .append(" SW2: ")
                     .append(Integer.toHexString(response.getSW2() & 255))
                     .toString()
               );
            }
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   void signDSA(
      CryptoTokenCryptoSystemData cryptoSystemData,
      DatakeyCryptoTokenData privateKeyData,
      byte[] digest,
      int digestOffset,
      int digestLength,
      byte[] r,
      int rOffset,
      byte[] s,
      int sOffset
   ) {
      if (cryptoSystemData != null && privateKeyData != null && digest != null && r != null && s != null) {
         if (r.length >= rOffset + 20 && s.length >= sOffset + 20 && digestLength == 20 && digest.length >= digestOffset + 20) {
            CommandAPDU command = (CommandAPDU)(new Object((byte)0, (byte)70, (byte)0, (byte)0, 40));
            command.appendLcData(new byte[]{63, 0});
            command.appendLcData(privateKeyData.getParamFile());
            command.appendLcData(new byte[]{63, 0});
            command.appendLcData(privateKeyData.getPrivateFile());
            command.appendLcData(digest, digestOffset, digestLength);
            ResponseAPDU response = (ResponseAPDU)(new Object());
            this.sendAPDU(command, response);
            if (response.checkStatusWords((byte)-112, (byte)0)) {
               byte[] responseData = response.getData();
               if (responseData != null && responseData.length == 40) {
                  System.arraycopy(responseData, 0, r, rOffset, 20);
                  System.arraycopy(responseData, 20, s, sOffset, 20);
               } else {
                  throw new Object("Not enough response data received from the smart card.");
               }
            } else {
               throw new Object(
                  ((StringBuffer)(new Object("Invalid response. SW1: "))).append(response.getSW1()).append(" SW2: ").append(response.getSW2()).toString()
               );
            }
         } else {
            throw new Object("invalid parameter length");
         }
      } else {
         throw new Object("null parameter in signDSA");
      }
   }

   @Override
   protected SmartCardID getSmartCardIDImpl() {
      CommandAPDU selectDKISFileAPDU = getSelectFileCommand(new byte[]{0, 6});
      CommandAPDU readDKISBinaryAPDU = (CommandAPDU)(new Object((byte)0, (byte)-80, (byte)0, (byte)0, 8));
      CommandAPDU selectUISFileAPDU = getSelectFileCommand(new byte[]{0, 10});
      CommandAPDU readUISBinaryAPDU = (CommandAPDU)(new Object((byte)0, (byte)-80, (byte)0, (byte)0, 32));
      CommandAPDUGroup commandAPDUGroup = (CommandAPDUGroup)(new Object(
         new Object[]{selectDKISFileAPDU, readDKISBinaryAPDU, selectUISFileAPDU, readUISBinaryAPDU}
      ));
      ResponseAPDUGroup responseAPDUGroup = (ResponseAPDUGroup)(new Object(4));
      this.sendAPDUs(commandAPDUGroup, responseAPDUGroup);
      if (!responseAPDUGroup.getAPDU(1).checkStatusWords((byte)-112, (byte)0)) {
         return null;
      }

      byte[] serialNumber = new byte[8];
      System.arraycopy(responseAPDUGroup.getAPDU(1).getData(), 0, serialNumber, 0, 8);
      long idLong = CryptoByteArrayArithmetic.valueOf(serialNumber);
      if (!responseAPDUGroup.getAPDU(3).checkStatusWords((byte)-112, (byte)0)) {
         return null;
      }

      String cardLabel = (String)(new Object(responseAPDUGroup.getAPDU(3).getData(), 0, 32));
      cardLabel = cardLabel.trim();
      return (SmartCardID)(new Object(idLong, cardLabel, this.getSmartCard()));
   }

   @Override
   protected boolean testCardSupportedImpl() {
      try {
         return this.getSmartCardIDImpl() == null ? false : this.getRandomBytesImpl(32) != null;
      } finally {
         ;
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         _uidInfo = (DatakeyCryptoSmartCardSession$UIDInfo)registry.get(785380077437529605L);
         if (_uidInfo == null) {
            _uidInfo = new DatakeyCryptoSmartCardSession$UIDInfo(0, false);
            registry.put(785380077437529605L, _uidInfo);
         }
      }
   }
}
