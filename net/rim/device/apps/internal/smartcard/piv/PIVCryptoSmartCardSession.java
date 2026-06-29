package net.rim.device.apps.internal.smartcard.piv;

import java.io.InputStream;
import net.rim.device.api.crypto.CryptoSmartCardKeyStoreData;
import net.rim.device.api.crypto.CryptoSmartCardSession;
import net.rim.device.api.crypto.HashCodeCalculator;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.CommandAPDU;
import net.rim.device.api.smartcard.ResponseAPDU;
import net.rim.device.api.smartcard.SmartCard;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.internal.compress.Inflater;
import net.rim.vm.Array;

public class PIVCryptoSmartCardSession extends CryptoSmartCardSession {
   private boolean _pivCardApplicationSelected;
   private X509Certificate _idCertificate;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   private static final long UID_INFO_ID = -1640444306254248651L;
   public static final byte[] ID_PIV_AUTHENTICATION_CERT = new byte[]{95, -63, 5};
   public static final byte[] ID_DIGITAL_SIGNATURE_CERT = new byte[]{95, -63, 10};
   public static final byte[] ID_KEY_MANAGEMENT_CERT = new byte[]{95, -63, 11};
   public static final byte[] ID_CARD_AUTHENTICATION_CERT = new byte[]{95, -63, 1};
   public static final byte[] ID_CARD_CAPABILITY_CONTAINER = new byte[]{95, -63, 7};
   public static final byte[] ID_CARD_HOLDER_UNIQUE_ID = new byte[]{95, -63, 2};
   private static final int PIV_PWD_LENGTH = 8;
   private static final byte ZERO_BYTE = 0;
   private static final byte INS_GENERAL_AUTHENTICATE = -121;
   private static final byte INS_GET_DATA = -53;
   private static final byte CLASS_00 = 0;
   private static final byte CLASS_80 = -128;
   private static final byte CLASS_84 = -124;
   private static final byte TAG_DYNAMIC_AUTHENTICATION_TEMPLATE = 124;
   private static final byte TAG_RESPONSE = -126;
   private static final byte TAG_CHALLENGE = -127;
   static final byte KEY_REF_PIV_AUTHENTICATION_KEY = -102;
   static final byte KEY_REF_DIGITAL_SIGNATURE_KEY = -100;
   static final byte KEY_REF_KEY_MANAGMENT_KEY = -99;
   static final byte KEY_REF_CARD_AUTHENTICATION_KEY = -98;
   private static final byte RESPONSE_BYTES_AVAILABLE = 97;
   private static final byte COMMAND_COMPLETE = -112;
   private static final byte[] PIV_CARD_APPLICATION_AID = new byte[]{-96, 0, 0, 3, 8, 0, 0, 16, 0};
   private static CommandAPDU _cmd = (CommandAPDU)(new Object((byte)0, (byte)0, (byte)0, (byte)0));
   private static final int MAX_LENGTH_ENCODING_BYTES = 4;
   static PIVCryptoSmartCardSession$UIDInfo _uidInfo;

   protected PIVCryptoSmartCardSession(SmartCard smartCard, SmartCardReaderSession readerSession) {
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
      ResponseAPDU response = this.pinVerifyHelper(null, true);
      byte sw1 = response.getSW1();
      byte sw2 = response.getSW2();
      if (sw1 == 99) {
         return sw2 & 15;
      } else if (response.checkStatusWords((byte)105, (byte)-125)) {
         throw new Object();
      } else {
         throw new Object();
      }
   }

   @Override
   protected boolean loginImpl(String password) {
      if (password != null && password.length() <= 8 && password.length() != 0) {
         ResponseAPDU response = this.pinVerifyHelper(password, false);
         if (response.checkStatusWords((byte)-112, (byte)0)) {
            return true;
         } else if (!response.checkStatusWords((byte)99, (byte)0) && !response.checkStatusWords((byte)105, (byte)-125)) {
            throw new Object();
         } else {
            throw new Object();
         }
      } else {
         return false;
      }
   }

   private ResponseAPDU pinVerifyHelper(String pin, boolean verifyOnly) {
      CommandAPDU pinVerifyAPDU = (CommandAPDU)(new Object((byte)0, (byte)32, (byte)0, (byte)-128));
      if (!verifyOnly) {
         if (pin == null) {
            throw new Object("PIN is null");
         }

         byte[] pinBytes = pin.getBytes();
         int pinLength = pin.length();
         if (pinLength < 8) {
            Array.resize(pinBytes, 8);
            Arrays.fill(pinBytes, (byte)-1, pinLength, 8 - pinLength);
         }

         pinVerifyAPDU.setLcData(pinBytes);
      }

      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(pinVerifyAPDU, response);
      return response;
   }

   private PrivateKey getPrivateKey(Certificate param1, byte param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 06: astore 3
      // 07: aload 3
      // 08: dup
      // 09: instanceof java/lang/Object
      // 0c: ifne 13
      // 0f: pop
      // 10: goto 9e
      // 13: checkcast java/lang/Object
      // 16: astore 4
      // 18: aload 4
      // 1a: invokevirtual net/rim/device/api/crypto/RSAPublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 1d: checkcast java/lang/Object
      // 20: astore 5
      // 22: aload 5
      // 24: invokevirtual net/rim/device/api/crypto/RSACryptoSystem.getModulusLength ()I
      // 27: bipush 8
      // 29: imul
      // 2a: istore 6
      // 2c: iload 6
      // 2e: lookupswitch 55 3 1024 34 2048 41 3072 48
      // 50: bipush 6
      // 52: istore 7
      // 54: goto 67
      // 57: bipush 7
      // 59: istore 7
      // 5b: goto 67
      // 5e: bipush 5
      // 60: istore 7
      // 62: goto 67
      // 65: aconst_null
      // 66: areturn
      // 67: new java/lang/Object
      // 6a: dup
      // 6b: new net/rim/device/apps/internal/smartcard/piv/PIVRSACryptoToken
      // 6e: dup
      // 6f: invokespecial net/rim/device/apps/internal/smartcard/piv/PIVRSACryptoToken.<init> ()V
      // 72: iload 6
      // 74: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 77: astore 8
      // 79: new java/lang/Object
      // 7c: dup
      // 7d: aload 8
      // 7f: new net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData
      // 82: dup
      // 83: aload 0
      // 84: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 87: iload 7
      // 89: iload 2
      // 8a: invokespecial net/rim/device/apps/internal/smartcard/piv/PIVCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;BB)V
      // 8d: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 90: areturn
      // 91: astore 3
      // 92: aconst_null
      // 93: areturn
      // 94: astore 3
      // 95: aconst_null
      // 96: areturn
      // 97: astore 3
      // 98: aconst_null
      // 99: areturn
      // 9a: astore 3
      // 9b: aconst_null
      // 9c: areturn
      // 9d: astore 3
      // 9e: aconst_null
      // 9f: areturn
      // try (0 -> 32): 53 null
      // try (33 -> 52): 53 null
      // try (0 -> 32): 56 null
      // try (33 -> 52): 56 null
      // try (0 -> 32): 59 null
      // try (33 -> 52): 59 null
      // try (0 -> 32): 62 null
      // try (33 -> 52): 62 null
      // try (0 -> 32): 65 null
      // try (33 -> 52): 65 null
   }

   @Override
   protected CryptoSmartCardKeyStoreData[] getKeyStoreDataArrayImpl() {
      this.displayProgressDialog(CryptoCommonResources.getString(0), 5);
      CryptoSmartCardKeyStoreData[] keyStoreDataArray = new Object[4];
      int offset = 0;
      this.stepProgressDialog(1);
      Certificate cert = this.getCertificate(ID_PIV_AUTHENTICATION_CERT);
      if (cert != null) {
         String label = this.getLabel(cert, 22);
         keyStoreDataArray[offset++] = (CryptoSmartCardKeyStoreData)(new Object(null, label, this.getPrivateKey(cert, (byte)-102), null, 2, cert, null, null, 0));
      }

      this.stepProgressDialog(1);
      if (this._idCertificate == null) {
         this._idCertificate = this.getCertificate(ID_DIGITAL_SIGNATURE_CERT);
      }

      if (this._idCertificate != null) {
         String label = this.getLabel(this._idCertificate, 23);
         keyStoreDataArray[offset++] = (CryptoSmartCardKeyStoreData)(new Object(
            null, label, this.getPrivateKey(this._idCertificate, (byte)-100), null, 2, this._idCertificate, null, null, 0
         ));
      }

      this.stepProgressDialog(1);
      cert = this.getCertificate(ID_KEY_MANAGEMENT_CERT);
      if (cert != null) {
         String label = this.getLabel(cert, 24);
         keyStoreDataArray[offset++] = (CryptoSmartCardKeyStoreData)(new Object(null, label, this.getPrivateKey(cert, (byte)-99), null, 2, cert, null, null, 0));
      }

      this.stepProgressDialog(1);
      cert = this.getCertificate(ID_CARD_AUTHENTICATION_CERT);
      if (cert != null) {
         String label = this.getLabel(cert, 25);
         keyStoreDataArray[offset++] = (CryptoSmartCardKeyStoreData)(new Object(null, label, this.getPrivateKey(cert, (byte)-98), null, 2, cert, null, null, 0));
      }

      Array.resize(keyStoreDataArray, offset);
      this.stepProgressDialog(1);

      label42:
      try {
         Thread.sleep(250);
      } finally {
         break label42;
      }

      this.dismissProgressDialog();
      return keyStoreDataArray;
   }

   private byte[] getFile(byte[] file) {
      return this.getFile(file, -1);
   }

   private byte[] getFile(byte[] file, int numBytesNeeded) {
      if (!this.selectPIVCardApplication()) {
         return null;
      }

      ResponseAPDU response = (ResponseAPDU)(new Object());
      byte[] lcData = new byte[]{92, 3};
      Arrays.append(lcData, file);
      CommandAPDU getDataCmd = (CommandAPDU)(new Object((byte)0, (byte)-53, (byte)63, (byte)-1, lcData, 0));
      this.sendAPDU(getDataCmd, response);
      byte[] responseBytes = response.getData();
      if (response.getSW1() == 97) {
         if (numBytesNeeded != -1 && responseBytes != null && responseBytes.length >= numBytesNeeded) {
            return responseBytes;
         }

         responseBytes = this.getResponse(responseBytes, response.getSW2(), (byte)0, numBytesNeeded);
      } else if (!response.checkStatusWords((byte)-112, (byte)0)) {
         if (response.checkStatusWords((byte)105, (byte)-126)) {
            throw new Object();
         }

         if (response.checkStatusWords((byte)106, (byte)-126)) {
            return null;
         }
      }

      return numBytesNeeded == -1 && responseBytes != null ? this.getBuffer(responseBytes, (byte)83) : responseBytes;
   }

   private byte[] getBuffer(byte[] tlvBuffer, byte tagToFind) {
      if (tlvBuffer == null) {
         return null;
      }

      int offset = 0;
      int totalLength = tlvBuffer.length;

      while (offset < totalLength) {
         int tag = tlvBuffer[offset++];
         if (offset >= totalLength) {
            return null;
         }

         int length = 0;
         if ((tlvBuffer[offset] & 255) != 128) {
            length = tlvBuffer[offset++];
            if ((length & 128) != 0) {
               int numLengthOctets = length & 127;
               if (numLengthOctets > 4) {
                  throw new Object();
               }

               length = 0;

               for (int i = 0; i < numLengthOctets; i++) {
                  if (offset >= totalLength) {
                     return null;
                  }

                  length = length << 8 | tlvBuffer[offset++] & 255;
               }
            }
         }

         if (offset + length > totalLength) {
            throw new Object();
         }

         if (tag == tagToFind) {
            return Arrays.copy(tlvBuffer, offset, length);
         }

         offset += length;
      }

      return null;
   }

   private void writeLength(DataBuffer buffer, int length) {
      if (length <= 127) {
         buffer.writeByte((byte)length);
      } else {
         int mask = -16777216;
         int shift = 24;
         int numLengthBytes = 1;
         boolean writeSucceedingBytes = false;

         for (int i = 0; i < 4; i++) {
            if (!writeSucceedingBytes && (length & mask) != 0) {
               numLengthBytes = 4 - i;
               buffer.writeByte(128 | numLengthBytes);
               writeSucceedingBytes = true;
            }

            if (writeSucceedingBytes) {
               buffer.writeByte((length & mask) >> shift);

               for (int j = i + 1; j < 4; j++) {
                  mask >>>= 8;
                  shift -= 8;
                  buffer.writeByte((length & mask) >> shift);
               }
               break;
            }

            mask >>>= 8;
            shift -= 8;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private X509Certificate getCertificate(byte[] file) {
      byte[] certFile = this.getFile(file);
      if (certFile == null) {
         return null;
      }

      byte[] certificate = this.getBuffer(certFile, (byte)112);
      byte[] certInfo = this.getBuffer(certFile, (byte)113);
      if (certificate == null || certInfo == null || certInfo.length != 1) {
         return null;
      }

      if ((certInfo[0] & 1) == 1) {
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            certificate = Inflater.gzipDecompress(certificate, 0, certificate.length);
            var11 = false;
         } finally {
            if (var11) {
               throw new Object("Unable to decompress certificate");
            }
         }
      }

      InputStream inputStream = (InputStream)(new Object(certificate));

      try {
         return (X509Certificate)(new Object(inputStream));
      } finally {
         throw new Object("Unable to create certficate");
      }
   }

   private String getLabel(Certificate certificate, int resourceID) {
      StringBuffer buffer = (StringBuffer)(new Object());
      String friendlyName = certificate.getSubjectFriendlyName();
      if (friendlyName != null) {
         int lastIndex = friendlyName.lastIndexOf(46);
         if (lastIndex != -1) {
            buffer.append(friendlyName.substring(0, lastIndex));
         } else {
            buffer.append(friendlyName);
         }

         buffer.append(" ( ");
         buffer.append(_rb.getString(resourceID));
         buffer.append(" )");
      }

      if (buffer.length() == 0) {
         buffer.append(_rb.getString(resourceID));
      }

      return buffer.toString();
   }

   public void signDecrypt(RSACryptoSystem cryptoSystem, PIVCryptoTokenData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) {
      if (cryptoSystem != null && privateKeyData != null && input != null && output != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (input.length >= inputOffset + modulusLength && output.length >= outputOffset + modulusLength) {
            ResponseAPDU response = (ResponseAPDU)(new Object());
            CommandAPDU signAPDU = (CommandAPDU)(new Object((byte)0, (byte)0, (byte)0, (byte)0));
            DataBuffer innerBuffer = (DataBuffer)(new Object());
            innerBuffer.writeByte(-126);
            innerBuffer.writeByte(0);
            innerBuffer.writeByte(-127);
            this.writeLength(innerBuffer, input.length);
            innerBuffer.write(input, inputOffset, input.length);
            DataBuffer lcData = (DataBuffer)(new Object());
            lcData.writeByte(124);
            this.writeLength(lcData, innerBuffer.getLength());
            lcData.write(innerBuffer.getArray(), innerBuffer.getArrayStart(), innerBuffer.getLength());
            byte[] data = lcData.getArray();
            int offset = lcData.getArrayStart();
            int length = lcData.getLength();
            int BLOCK_SIZE = 255;

            while (length > 0) {
               int bytesToWrite = Math.min(BLOCK_SIZE, length);
               byte cmd = (byte)(length <= BLOCK_SIZE ? 0 : 16);
               signAPDU.set(cmd, (byte)-121, privateKeyData.getAlgorithmReference(), privateKeyData.getKeyReference());
               signAPDU.setLcData(data, offset, bytesToWrite);
               signAPDU.setLe(0);
               this.sendAPDU(signAPDU, response);
               offset += bytesToWrite;
               length -= bytesToWrite;
            }

            byte[] responseData = response.getData();
            if (response.getSW1() != -112) {
               if (response.getSW1() != 97) {
                  throw new Object(
                     ((StringBuffer)(new Object("Invalid response code, sw1=")))
                        .append(Integer.toHexString(response.getSW1() & 255))
                        .append(" sw2=")
                        .append(Integer.toHexString(response.getSW2() & 255))
                        .toString()
                  );
               }

               responseData = this.getResponse(responseData, response.getSW2(), (byte)-128, -1);
            }

            responseData = this.getBuffer(responseData, (byte)124);
            responseData = this.getBuffer(responseData, (byte)-126);
            if (responseData != null && responseData.length > 0) {
               System.arraycopy(responseData, 0, output, outputOffset, responseData.length);
            } else {
               throw new Object("Not enough response data received from the smart card.");
            }
         } else {
            throw new Object();
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public SmartCardID getSmartCardIDImpl() {
      byte[] cardHolderUIDFile = this.getFile(ID_CARD_HOLDER_UNIQUE_ID, 255);
      if (cardHolderUIDFile == null) {
         return null;
      }

      long idLong = HashCodeCalculator.getDigest64(cardHolderUIDFile);
      String friendlyName;
      if (idLong == _uidInfo._uid) {
         friendlyName = _uidInfo._friendlyName;
      } else {
         if (this._idCertificate == null) {
            this._idCertificate = this.getCertificate(ID_DIGITAL_SIGNATURE_CERT);
         }

         if (this._idCertificate != null) {
            friendlyName = this._idCertificate.getSubjectFriendlyName();
            _uidInfo._uid = idLong;
            _uidInfo._friendlyName = friendlyName;
         } else {
            friendlyName = "";
         }
      }

      return (SmartCardID)(new Object(idLong, friendlyName, this.getSmartCard()));
   }

   @Override
   protected byte[] getRandomBytesImpl(int maxBytes) {
      return null;
   }

   private byte[] getResponse(byte[] initialBytes, byte dataLength, byte classByte, int numBytesNeeded) {
      byte[] responseData = initialBytes;
      if (responseData == null) {
         responseData = new byte[0];
      }

      _cmd.set(classByte, (byte)-64, (byte)0, (byte)0, dataLength & 255);
      ResponseAPDU response = (ResponseAPDU)(new Object());
      this.sendAPDU(_cmd, response);
      byte[] tempResponseData = response.getData();
      if (tempResponseData != null) {
         Arrays.append(responseData, tempResponseData);
      }

      if (response.getSW1() == -112) {
         return responseData;
      } else if (response.getSW1() == 97) {
         return numBytesNeeded != -1 && responseData.length >= numBytesNeeded
            ? responseData
            : this.getResponse(responseData, response.getSW2(), classByte, numBytesNeeded);
      } else {
         throw new Object();
      }
   }

   private CommandAPDU getSelectFileCommand(byte[] file) {
      return (CommandAPDU)(new Object((byte)0, (byte)-92, (byte)4, (byte)0, file));
   }

   private boolean selectPIVCardApplication() {
      if (!this._pivCardApplicationSelected) {
         try {
            ResponseAPDU response = (ResponseAPDU)(new Object());
            CommandAPDU selectFileAPDU = this.getSelectFileCommand(PIV_CARD_APPLICATION_AID);
            this.sendAPDU(selectFileAPDU, response);
            this._pivCardApplicationSelected = response.checkStatusWords((byte)-112, (byte)0) || response.getSW1() == 97;
         } finally {
            return this._pivCardApplicationSelected;
         }
      }

      return this._pivCardApplicationSelected;
   }

   @Override
   protected boolean testCardSupportedImpl() {
      if (!this.selectPIVCardApplication()) {
         return false;
      }

      try {
         return this.getFile(ID_CARD_CAPABILITY_CONTAINER) != null;
      } finally {
         ;
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         _uidInfo = (PIVCryptoSmartCardSession$UIDInfo)registry.get(-1640444306254248651L);
         if (_uidInfo == null) {
            _uidInfo = new PIVCryptoSmartCardSession$UIDInfo(0, null);
            registry.put(-1640444306254248651L, _uidInfo);
         }
      }
   }
}
