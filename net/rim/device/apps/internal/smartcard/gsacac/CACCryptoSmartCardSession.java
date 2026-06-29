package net.rim.device.apps.internal.smartcard.gsacac;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import net.rim.device.api.compress.ZLibInputStream;
import net.rim.device.api.crypto.CryptoByteArrayArithmetic;
import net.rim.device.api.crypto.CryptoSmartCardKeyStoreData;
import net.rim.device.api.crypto.CryptoSmartCardSession;
import net.rim.device.api.crypto.RSACryptoSystem;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.smartcard.CommandAPDU;
import net.rim.device.api.smartcard.CommandAPDUGroup;
import net.rim.device.api.smartcard.ResponseAPDU;
import net.rim.device.api.smartcard.SmartCard;
import net.rim.device.api.smartcard.SmartCardAccessDeniedException;
import net.rim.device.api.smartcard.SmartCardException;
import net.rim.device.api.smartcard.SmartCardID;
import net.rim.device.api.smartcard.SmartCardLockedException;
import net.rim.device.api.smartcard.SmartCardReaderSession;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public class CACCryptoSmartCardSession extends CryptoSmartCardSession {
   private X509Certificate _idCertificate;
   private static ResourceBundle _rb = ResourceBundle.getBundle(-2744454300651253428L, "net.rim.device.apps.internal.resource.crypto.SmartCard");
   public static final byte ID_PKI = 0;
   public static final byte SIGNING_PKI = 1;
   public static final byte ENCRYPTION_PKI = 2;
   private static final int CAC_PWD_LENGTH = 8;
   private static final byte T_BUFFER = 1;
   private static final byte V_BUFFER = 2;
   private static final byte INS_GET_PROPERTIES = 86;
   private static final byte INS_READ_BUFFER = 82;
   private static final byte INS_GET_CERTIFICATE = 54;
   private static final byte INS_SIGN_DECRYPT = 66;
   private static final byte INS_GENERATE_KEY = 70;
   private static final byte INS_INITIALIZE_UPDATE = 80;
   private static final byte ZERO_BYTE = 0;
   private static final byte CLASS_00 = 0;
   private static final byte CLASS_80 = -128;
   private static final byte CLASS_84 = -124;
   private static final int GOLD_TAG = 1;
   private static final int BSI_TAG = 5;
   private static final byte RESPONSE_BYTES_AVAILABLE = 97;
   private static final byte COMMAND_COMPLETE = -112;
   private static final byte[] CARD_MANAGER_AID = new byte[]{-96, 0, 0, 0, 3, 0, 0};
   private static final byte[] PIN_MANAGEMENT_AID = new byte[]{-96, 0, 0, 0, 121, 3, 0};
   public static final int MAX_LOGIN_ATTEMPTS = 3;
   private static CommandAPDU _cmd = new CommandAPDU((byte)0, (byte)0, (byte)0, (byte)0);
   private static final long UID_INFO_ID = 2984846373662370609L;
   static CACCryptoSmartCardSession$UIDInfo _uidInfo;

   protected CACCryptoSmartCardSession(SmartCard smartCard, SmartCardReaderSession readerSession) {
   }

   @Override
   protected void closeImpl() {
   }

   @Override
   protected int getMaxLoginAttemptsImpl() {
      return 3;
   }

   @Override
   protected int getRemainingLoginAttemptsImpl() throws SmartCardException, SmartCardLockedException {
      ResponseAPDU response = this.pinVerifyHelper(null, true);
      byte sw1 = response.getSW1();
      byte sw2 = response.getSW2();
      if (sw1 == -112) {
         return 3;
      } else if (sw1 == 99) {
         return sw2;
      } else if (response.checkStatusWords((byte)105, (byte)-125)) {
         throw new SmartCardLockedException();
      } else {
         throw new SmartCardException();
      }
   }

   @Override
   protected boolean loginImpl(String password) throws SmartCardAccessDeniedException, SmartCardLockedException {
      if (password != null && password.length() <= 8 && password.length() != 0) {
         ResponseAPDU response = this.pinVerifyHelper(password, false);
         if (response.checkStatusWords((byte)-112, (byte)0)) {
            return true;
         } else if (!response.checkStatusWords((byte)99, (byte)0) && !response.checkStatusWords((byte)105, (byte)-125)) {
            throw new SmartCardAccessDeniedException();
         } else {
            throw new SmartCardLockedException();
         }
      } else {
         return false;
      }
   }

   @Override
   protected CryptoSmartCardKeyStoreData[] getKeyStoreDataArrayImpl() throws SmartCardException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: bipush 0
      // 002: invokestatic net/rim/device/apps/internal/api/crypto/CryptoCommonResources.getString (I)Ljava/lang/String;
      // 005: bipush 4
      // 007: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.displayProgressDialog (Ljava/lang/String;I)V
      // 00a: aload 0
      // 00b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 00e: astore 1
      // 00f: new net/rim/device/apps/internal/smartcard/gsacac/CACRSACryptoToken
      // 012: dup
      // 013: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACRSACryptoToken.<init> ()V
      // 016: astore 2
      // 017: new net/rim/device/api/crypto/RSACryptoSystem
      // 01a: dup
      // 01b: aload 2
      // 01c: sipush 1024
      // 01f: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 022: astore 3
      // 023: bipush 3
      // 025: anewarray 299
      // 028: astore 6
      // 02a: aload 0
      // 02b: bipush 1
      // 02c: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 02f: new net/rim/device/api/crypto/RSAPrivateKey
      // 032: dup
      // 033: aload 3
      // 034: new net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 037: dup
      // 038: aload 1
      // 039: bipush 0
      // 03a: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;B)V
      // 03d: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 040: astore 4
      // 042: aload 0
      // 043: getfield net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession._idCertificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 046: ifnonnull 052
      // 049: aload 0
      // 04a: aload 0
      // 04b: bipush 0
      // 04c: invokevirtual net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getCertificate (B)Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 04f: putfield net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession._idCertificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 052: aload 0
      // 053: getfield net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession._idCertificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 056: ifnull 07c
      // 059: aload 6
      // 05b: bipush 0
      // 05c: new net/rim/device/api/crypto/CryptoSmartCardKeyStoreData
      // 05f: dup
      // 060: aconst_null
      // 061: aload 0
      // 062: aload 0
      // 063: getfield net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession._idCertificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 066: bipush 13
      // 068: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getLabel (Lnet/rim/device/api/crypto/certificate/Certificate;I)Ljava/lang/String;
      // 06b: aload 4
      // 06d: aconst_null
      // 06e: bipush 2
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession._idCertificate Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 074: aconst_null
      // 075: aconst_null
      // 076: bipush 0
      // 077: i2l
      // 078: invokespecial net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Lnet/rim/device/api/crypto/SymmetricKey;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/PublicKey;J)V
      // 07b: aastore
      // 07c: aload 0
      // 07d: bipush 1
      // 07e: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 081: new net/rim/device/api/crypto/RSAPrivateKey
      // 084: dup
      // 085: aload 3
      // 086: new net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 089: dup
      // 08a: aload 1
      // 08b: bipush 1
      // 08c: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;B)V
      // 08f: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 092: astore 4
      // 094: aload 0
      // 095: bipush 1
      // 096: invokevirtual net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getCertificate (B)Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 099: astore 5
      // 09b: aload 5
      // 09d: ifnull 0bf
      // 0a0: aload 6
      // 0a2: bipush 1
      // 0a3: new net/rim/device/api/crypto/CryptoSmartCardKeyStoreData
      // 0a6: dup
      // 0a7: aconst_null
      // 0a8: aload 0
      // 0a9: aload 5
      // 0ab: bipush 14
      // 0ad: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getLabel (Lnet/rim/device/api/crypto/certificate/Certificate;I)Ljava/lang/String;
      // 0b0: aload 4
      // 0b2: aconst_null
      // 0b3: bipush 2
      // 0b5: aload 5
      // 0b7: aconst_null
      // 0b8: aconst_null
      // 0b9: bipush 0
      // 0ba: i2l
      // 0bb: invokespecial net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Lnet/rim/device/api/crypto/SymmetricKey;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/PublicKey;J)V
      // 0be: aastore
      // 0bf: aload 0
      // 0c0: bipush 1
      // 0c1: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 0c4: new net/rim/device/api/crypto/RSAPrivateKey
      // 0c7: dup
      // 0c8: aload 3
      // 0c9: new net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData
      // 0cc: dup
      // 0cd: aload 1
      // 0ce: bipush 2
      // 0d0: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoTokenData.<init> (Lnet/rim/device/api/smartcard/SmartCardID;B)V
      // 0d3: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 0d6: astore 4
      // 0d8: aload 0
      // 0d9: bipush 2
      // 0db: invokevirtual net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getCertificate (B)Lnet/rim/device/api/crypto/certificate/x509/X509Certificate;
      // 0de: astore 5
      // 0e0: aload 5
      // 0e2: ifnull 105
      // 0e5: aload 6
      // 0e7: bipush 2
      // 0e9: new net/rim/device/api/crypto/CryptoSmartCardKeyStoreData
      // 0ec: dup
      // 0ed: aconst_null
      // 0ee: aload 0
      // 0ef: aload 5
      // 0f1: bipush 15
      // 0f3: invokespecial net/rim/device/apps/internal/smartcard/gsacac/CACCryptoSmartCardSession.getLabel (Lnet/rim/device/api/crypto/certificate/Certificate;I)Ljava/lang/String;
      // 0f6: aload 4
      // 0f8: aconst_null
      // 0f9: bipush 2
      // 0fb: aload 5
      // 0fd: aconst_null
      // 0fe: aconst_null
      // 0ff: bipush 0
      // 100: i2l
      // 101: invokespecial net/rim/device/api/crypto/CryptoSmartCardKeyStoreData.<init> ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Lnet/rim/device/api/crypto/SymmetricKey;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/PublicKey;J)V
      // 104: aastore
      // 105: aload 0
      // 106: bipush 1
      // 107: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.stepProgressDialog (I)V
      // 10a: sipush 250
      // 10d: i2l
      // 10e: invokestatic java/lang/Thread.sleep (J)V
      // 111: goto 116
      // 114: astore 7
      // 116: aload 0
      // 117: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.dismissProgressDialog ()V
      // 11a: aload 6
      // 11c: areturn
      // 11d: astore 1
      // 11e: goto 126
      // 121: astore 1
      // 122: goto 126
      // 125: astore 1
      // 126: new net/rim/device/api/smartcard/SmartCardException
      // 129: dup
      // 12a: invokespecial net/rim/device/api/smartcard/SmartCardException.<init> ()V
      // 12d: athrow
      // try (145 -> 148): 149 null
      // try (0 -> 153): 154 null
      // try (0 -> 153): 156 null
      // try (0 -> 153): 158 null
   }

   public X509Certificate getCertificate(byte certType) throws SmartCardException {
      try {
         byte[] certificate = this.getCertificateHelper(certType);
         InputStream inputStream = new ByteArrayInputStream(certificate, 1, certificate.length - 1);
         if (certificate[0] == 1) {
            inputStream = new ZLibInputStream(inputStream);
         } else if (certificate[0] == 0) {
         }

         return new X509Certificate(inputStream);
      } finally {
         throw new SmartCardException();
      }
   }

   private byte[] getCertificateHelper(byte certType) throws SmartCardAccessDeniedException, SmartCardException {
      byte[] certificate = new byte[0];
      byte[] certFile = new byte[]{-96, 0, 0, 0, 121, 1, certType};
      this.selectFile(certFile);
      ResponseAPDU response = new ResponseAPDU();
      CommandAPDU cmd = new CommandAPDU((byte)-128, (byte)54, (byte)0, (byte)0, 100);

      while (true) {
         this.sendAPDU(cmd, response);
         byte sw1 = response.getSW1();
         byte sw2 = response.getSW2();
         if (sw1 != 99 && sw1 != -112) {
            if (sw1 == 105 && sw2 == -127) {
               throw new SmartCardException();
            }

            if (sw1 == 105 && sw2 == -126) {
               throw new SmartCardAccessDeniedException();
            }

            throw new SmartCardException();
         }

         int offset = certificate.length;
         byte[] responseData = response.getData();
         if (responseData != null && responseData.length > 0) {
            Array.resize(certificate, offset + responseData.length);
            System.arraycopy(responseData, 0, certificate, offset, responseData.length);
         }

         if (sw1 == -112) {
            return certificate;
         }

         cmd.setLe(sw2);
      }
   }

   private String getLabel(Certificate certificate, int resourceID) {
      StringBuffer buffer = new StringBuffer();
      String friendlyName = certificate.getSubjectFriendlyName();
      if (friendlyName != null) {
         int lastIndex = friendlyName.lastIndexOf(46);
         if (lastIndex != -1) {
            buffer.append(friendlyName.substring(0, lastIndex));
            buffer.append(" ( ");
            buffer.append(_rb.getString(resourceID));
            buffer.append(" )");
         }
      }

      if (buffer.length() == 0) {
         buffer.append(_rb.getString(resourceID));
      }

      return buffer.toString();
   }

   public void signDecrypt(RSACryptoSystem cryptoSystem, CACCryptoTokenData privateKeyData, byte[] input, int inputOffset, byte[] output, int outputOffset) throws SmartCardException {
      if (cryptoSystem != null && privateKeyData != null && input != null && output != null) {
         int modulusLength = cryptoSystem.getModulusLength();
         if (input.length >= inputOffset + modulusLength && output.length >= outputOffset + modulusLength) {
            CommandAPDU selectFileAPDU = this.getSelectFileCommand(new byte[]{-96, 0, 0, 0, 121, 1, privateKeyData.getFile()});
            ResponseAPDU response = new ResponseAPDU();
            CommandAPDU signAPDU = new CommandAPDU((byte)-128, (byte)66, (byte)0, (byte)0, modulusLength);
            signAPDU.setLcData(input, inputOffset, input.length - inputOffset);
            this.sendAPDUs(new CommandAPDUGroup(new CommandAPDU[]{selectFileAPDU, signAPDU}), response);
            byte[] responseData = null;
            if (response.getSW1() == -112) {
               responseData = response.getData();
            } else {
               if (response.getSW1() != 97) {
                  throw new SmartCardException(
                     "Invalid response code, sw1=" + Integer.toHexString(response.getSW1() & 255) + " sw2=" + Integer.toHexString(response.getSW2() & 255)
                  );
               }

               responseData = this.getResponse(response.getSW2(), (byte)-128);
            }

            if (responseData != null && responseData.length > 0) {
               System.arraycopy(responseData, 0, output, outputOffset, responseData.length);
            } else {
               throw new SmartCardException("Not enough response data received from the smart card.");
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public SmartCardID getSmartCardIDImpl() {
      byte[] uniqueData = this.getUniqueCardID();
      if (uniqueData != null && uniqueData.length != 0) {
         SHA1Digest digest = new SHA1Digest();
         digest.update(uniqueData);
         byte[] bytes = digest.getDigest();
         Array.resize(bytes, 8);
         long idLong = CryptoByteArrayArithmetic.valueOf(bytes);
         String friendlyName;
         if (idLong == _uidInfo._uid) {
            friendlyName = _uidInfo._friendlyName;
         } else {
            if (this._idCertificate == null) {
               this._idCertificate = this.getCertificate((byte)0);
            }

            if (this._idCertificate != null) {
               friendlyName = this._idCertificate.getSubjectFriendlyName();
               _uidInfo._uid = idLong;
               _uidInfo._friendlyName = friendlyName;
            } else {
               friendlyName = "";
            }
         }

         return new SmartCardID(idLong, friendlyName, this.getSmartCard());
      } else {
         return null;
      }
   }

   private byte[] getUniqueCardID() {
      CommandAPDU selectFileAPDU = this.getSelectFileCommand(CARD_MANAGER_AID);
      ResponseAPDU response = new ResponseAPDU();
      CommandAPDU getDataAPDU = new CommandAPDU((byte)-128, (byte)-54, (byte)-97, (byte)127, 0);
      this.sendAPDUs(new CommandAPDUGroup(new CommandAPDU[]{selectFileAPDU, getDataAPDU}), response);
      return response.getData();
   }

   @Override
   protected byte[] getRandomBytesImpl(int maxBytes) {
      return null;
   }

   private byte[] getResponse(byte dataLength, byte classByte) throws SmartCardException {
      _cmd.set(classByte, (byte)-64, (byte)0, (byte)0, dataLength);
      ResponseAPDU response = new ResponseAPDU();
      this.sendAPDU(_cmd, response);
      byte[] responseData = response.getData();
      if (responseData == null) {
         responseData = new byte[0];
      }

      if (response.getSW1() != -112) {
         if (response.getSW1() != 97) {
            throw new SmartCardException();
         }

         byte[] moreData = this.getResponse(response.getSW2(), classByte);
         if (moreData != null && moreData.length > 0) {
            int offset = responseData.length;
            Array.resize(responseData, moreData.length + offset);
            System.arraycopy(moreData, 0, responseData, offset, moreData.length);
         }
      }

      return responseData;
   }

   private CommandAPDU getSelectFileCommand(byte[] file) {
      return new CommandAPDU((byte)0, (byte)-92, (byte)4, (byte)0, file, 0);
   }

   private void selectFile(byte[] file) throws SmartCardException {
      ResponseAPDU response = new ResponseAPDU();
      CommandAPDU cmd = new CommandAPDU((byte)0, (byte)-92, (byte)4, (byte)0, file, 0);
      this.sendAPDU(cmd, response);
      if (response.getSW1() == 97) {
         this.getResponse(response.getSW2(), (byte)0);
      } else {
         if (response.getSW1() != -112) {
            throw new SmartCardException();
         }
      }
   }

   private byte[] readBuffer(SmartCardReaderSession readerSession, byte bufferType, int offset, int length) throws SmartCardAccessDeniedException, SmartCardException {
      ResponseAPDU response = new ResponseAPDU();
      byte[] data = new byte[0];
      CommandAPDU cmd = new CommandAPDU((byte)-128, (byte)82, (byte)0, (byte)0);

      while (length > 0) {
         byte lengthToRead;
         if (length <= 100) {
            lengthToRead = (byte)length;
         } else {
            lengthToRead = 100;
         }

         length -= lengthToRead;
         byte offsetHigh = (byte)((offset & 0xFF00) >> 8);
         byte offsetLow = (byte)(offset & 0xFF);
         cmd.set((byte)-128, (byte)82, offsetHigh, offsetLow, new byte[]{bufferType, lengthToRead});
         readerSession.sendAPDU(cmd, response);
         offset += lengthToRead;
         byte[] moreData;
         if (response.getSW1() == 97) {
            moreData = this.getResponse(response.getSW2(), (byte)0);
         } else {
            if (response.getSW1() != -112) {
               if (response.getSW1() == 105 && response.getSW2() == -126) {
                  throw new SmartCardAccessDeniedException();
               }

               throw new SmartCardException();
            }

            moreData = response.getData();
         }

         if (moreData != null) {
            int oldLength = data.length;
            Array.resize(data, oldLength + moreData.length);
            System.arraycopy(moreData, 0, data, oldLength, moreData.length);
         }
      }

      return data;
   }

   private ResponseAPDU pinVerifyHelper(String pin, boolean verifyOnly) {
      CommandAPDU selectFileAPDU = this.getSelectFileCommand(PIN_MANAGEMENT_AID);
      ResponseAPDU response = new ResponseAPDU();
      CommandAPDU pinVerifyAPDU = new CommandAPDU((byte)-128, (byte)32, (byte)0, (byte)0);
      if (!verifyOnly) {
         if (pin == null) {
            throw new IllegalArgumentException("PIN is null");
         }

         pinVerifyAPDU.setLcData(pin.getBytes());
         int pinLength = pin.length();
         if (pinLength < 8) {
            byte[] pinPadding = new byte[8 - pinLength];
            Arrays.fill(pinPadding, (byte)-1);
            pinVerifyAPDU.appendLcData(pinPadding);
         }
      }

      this.sendAPDUs(new CommandAPDUGroup(new CommandAPDU[]{selectFileAPDU, pinVerifyAPDU}), response);
      return response;
   }

   @Override
   protected boolean testCardSupportedImpl() {
      try {
         if (this.getUniqueCardID() == null) {
            return false;
         }

         byte[] idCertFile = new byte[]{-96, 0, 0, 0, 121, 1, 0};
         byte[] signingCertFile = new byte[]{-96, 0, 0, 0, 121, 1, 1};
         byte[] encryptionCertFile = new byte[]{-96, 0, 0, 0, 121, 1, 2};
         this.selectFile(idCertFile);
         this.selectFile(signingCertFile);
         this.selectFile(encryptionCertFile);
         return true;
      } finally {
         ;
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      synchronized (registry) {
         _uidInfo = (CACCryptoSmartCardSession$UIDInfo)registry.get(2984846373662370609L);
         if (_uidInfo == null) {
            _uidInfo = new CACCryptoSmartCardSession$UIDInfo(0, null);
            registry.put(2984846373662370609L, _uidInfo);
         }
      }
   }
}
