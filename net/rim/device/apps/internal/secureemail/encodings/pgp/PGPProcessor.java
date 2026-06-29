package net.rim.device.apps.internal.secureemail.encodings.pgp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.CombinedKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.pgp.PGPArmorDecoder;
import net.rim.device.api.crypto.pgp.PGPCompressedInputStream;
import net.rim.device.api.crypto.pgp.PGPEncryptedInputStream;
import net.rim.device.api.crypto.pgp.PGPInputStream;
import net.rim.device.api.crypto.pgp.PGPSignedInputStream;
import net.rim.device.api.mime.MIMEInputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.secureemail.SecureEmailConstants;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;
import net.rim.device.apps.internal.secureemail.cache.CachedManager;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.pgp.cache.CachedPGPEncryptionField;
import net.rim.device.apps.internal.secureemail.encodings.pgp.cache.CachedPGPSignatureField;
import net.rim.device.apps.internal.secureemail.encodings.pgp.server.PGPUniversalEnrollmentKeyStore;

final class PGPProcessor extends SecureEmailProcessor {
   private boolean _firstInputStream = true;
   private boolean _plainTextLeading = false;

   public PGPProcessor(PGPBodyModel pgpBodyModel, Object target, boolean allowUI, Object context) {
      super(PGPFactory.getInstance(), pgpBodyModel, target, allowUI, context);
      Hashtable pgpUniversalServerEnrollmentData = ApplicationRegistry.getApplicationRegistry().getHashtable(3676539340381219095L);
      if (pgpUniversalServerEnrollmentData.isEmpty()) {
         super._preferredKeyStore = PGPKeyStore.getInstance();
      } else {
         super._preferredKeyStore = new CombinedKeyStore(new KeyStore[]{PGPKeyStore.getInstance(), PGPUniversalEnrollmentKeyStore.getInstance()}, 0);
      }
   }

   @Override
   protected final byte[] getSupportedSecurityEncoding() {
      return PGPConstants.SECURITY_ENCODING_PGP_BYTES;
   }

   @Override
   protected final InputStream getInputStream(byte[] modelData, CachedMessage cachedMessage) {
      byte[] formatted = PGPSignedInputStream.convertLineEndings(modelData, 0, modelData.length);
      formatted = PGPSignedInputStream.stripSpaces(formatted, 0, formatted.length);
      ByteArrayInputStream byteStream = new ByteArrayInputStream(formatted);
      return new PGPArmorDecoder(byteStream, super._preferredKeyStore, super._allowUI);
   }

   @Override
   protected final boolean getFieldsFromInputStream(InputStream inputStream, CachedManager cachedManager) {
      return this.getFieldsFromInputStream(inputStream, false, cachedManager);
   }

   protected final boolean getFieldsFromInputStream(InputStream inputStream, boolean opaqueData, CachedManager cachedManager) {
      if (inputStream instanceof PGPArmorDecoder) {
         return this.getFieldsFromInputStream_PGP_ArmorDecoder((PGPArmorDecoder)inputStream, opaqueData, cachedManager);
      } else {
         return inputStream instanceof PGPInputStream
            ? this.getFieldsFromInputStream_PGP((PGPInputStream)inputStream, opaqueData, cachedManager)
            : super.getFieldsFromInputStream(inputStream, cachedManager);
      }
   }

   protected final boolean getFieldsFromInputStream_PGP_ArmorDecoder(PGPArmorDecoder param1, boolean param2, CachedManager param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 4
      // 003: aload 1
      // 004: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getNextStream ()Ljava/io/InputStream;
      // 007: astore 5
      // 009: aload 5
      // 00b: ifnonnull 011
      // 00e: goto 15a
      // 011: aload 0
      // 012: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/PGPProcessor._firstInputStream Z
      // 015: ifeq 02a
      // 018: aload 0
      // 019: bipush 0
      // 01a: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/PGPProcessor._firstInputStream Z
      // 01d: aload 5
      // 01f: instanceof net/rim/device/api/crypto/pgp/PGPInputStream
      // 022: ifne 02a
      // 025: aload 0
      // 026: bipush 1
      // 027: putfield net/rim/device/apps/internal/secureemail/encodings/pgp/PGPProcessor._plainTextLeading Z
      // 02a: iload 4
      // 02c: aload 0
      // 02d: aload 5
      // 02f: iload 2
      // 030: aload 3
      // 031: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/PGPProcessor.getFieldsFromInputStream (Ljava/io/InputStream;ZLnet/rim/device/apps/internal/secureemail/cache/CachedManager;)Z
      // 034: ior
      // 035: istore 4
      // 037: goto 003
      // 03a: astore 5
      // 03c: sipush 8042
      // 03f: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 042: astore 6
      // 044: aload 5
      // 046: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 049: astore 7
      // 04b: aload 7
      // 04d: ifnonnull 053
      // 050: goto 0e3
      // 053: aload 7
      // 055: invokevirtual java/lang/String.length ()I
      // 058: ifgt 05e
      // 05b: goto 0e3
      // 05e: new java/lang/StringBuffer
      // 061: dup
      // 062: invokespecial java/lang/StringBuffer.<init> ()V
      // 065: aload 6
      // 067: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 06a: sipush 8041
      // 06d: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // 070: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 073: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 076: astore 6
      // 078: bipush 0
      // 079: istore 8
      // 07b: aload 7
      // 07d: bipush 44
      // 07f: invokevirtual java/lang/String.indexOf (I)I
      // 082: istore 9
      // 084: iload 9
      // 086: bipush -1
      // 088: if_icmpeq 0c2
      // 08b: new java/lang/StringBuffer
      // 08e: dup
      // 08f: invokespecial java/lang/StringBuffer.<init> ()V
      // 092: aload 6
      // 094: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 097: ldc_w "\n• "
      // 09a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 09d: aload 7
      // 09f: iload 8
      // 0a1: iload 9
      // 0a3: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 0a6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0a9: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ac: astore 6
      // 0ae: iload 9
      // 0b0: bipush 1
      // 0b1: iadd
      // 0b2: istore 8
      // 0b4: aload 7
      // 0b6: bipush 44
      // 0b8: iload 8
      // 0ba: invokevirtual java/lang/String.indexOf (II)I
      // 0bd: istore 9
      // 0bf: goto 084
      // 0c2: new java/lang/StringBuffer
      // 0c5: dup
      // 0c6: invokespecial java/lang/StringBuffer.<init> ()V
      // 0c9: aload 6
      // 0cb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ce: ldc_w "\n• "
      // 0d1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0d4: aload 7
      // 0d6: iload 8
      // 0d8: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 0db: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0de: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0e1: astore 6
      // 0e3: new net/rim/device/apps/internal/secureemail/encodings/pgp/cache/CachedPGPMissingPrivateKeyField
      // 0e6: dup
      // 0e7: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/cache/CachedPGPMissingPrivateKeyField.<init> ()V
      // 0ea: astore 8
      // 0ec: aload 8
      // 0ee: new net/rim/device/apps/internal/secureemail/cache/CachedBodyField
      // 0f1: dup
      // 0f2: aload 6
      // 0f4: invokespecial net/rim/device/apps/internal/secureemail/cache/CachedBodyField.<init> (Ljava/lang/String;)V
      // 0f7: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 0fa: aload 3
      // 0fb: aload 8
      // 0fd: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 100: aload 3
      // 101: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedField.getCachedMessage ()Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 104: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessage.setDoNotCache ()V
      // 107: bipush 1
      // 108: istore 4
      // 10a: goto 003
      // 10d: astore 5
      // 10f: aload 0
      // 110: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 113: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEventLoggerGUID ()J
      // 116: aload 5
      // 118: invokevirtual net/rim/device/api/crypto/CryptoUnsupportedOperationException.toString ()Ljava/lang/String;
      // 11b: invokevirtual java/lang/String.getBytes ()[B
      // 11e: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[B)Z
      // 121: pop
      // 122: bipush 1
      // 123: anewarray 483
      // 126: dup
      // 127: bipush 0
      // 128: aload 0
      // 129: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 12c: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getEncodingString ()Ljava/lang/String;
      // 12f: aastore
      // 130: astore 6
      // 132: sipush 167
      // 135: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailResources.getString (I)Ljava/lang/String;
      // 138: aload 6
      // 13a: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 13d: astore 7
      // 13f: aload 3
      // 140: new net/rim/device/apps/internal/secureemail/cache/CachedErrorField
      // 143: dup
      // 144: aload 7
      // 146: bipush 1
      // 147: invokespecial net/rim/device/apps/internal/secureemail/cache/CachedErrorField.<init> (Ljava/lang/String;Z)V
      // 14a: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 14d: aload 3
      // 14e: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedField.getCachedMessage ()Lnet/rim/device/apps/internal/secureemail/cache/CachedMessage;
      // 151: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedMessage.setDoNotCache ()V
      // 154: bipush 1
      // 155: istore 4
      // 157: goto 003
      // 15a: aload 1
      // 15b: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 15e: istore 5
      // 160: iload 5
      // 162: ifle 19c
      // 165: new net/rim/device/apps/internal/secureemail/cache/CachedIncludedCertificatesField
      // 168: dup
      // 169: aload 0
      // 16a: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 16d: invokespecial net/rim/device/apps/internal/secureemail/cache/CachedIncludedCertificatesField.<init> (Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;)V
      // 170: astore 6
      // 172: bipush 0
      // 173: istore 7
      // 175: iload 7
      // 177: iload 5
      // 179: if_icmpge 193
      // 17c: aload 6
      // 17e: aload 1
      // 17f: iload 7
      // 181: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 184: aload 1
      // 185: iload 7
      // 187: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getPrivateKey (I)Lnet/rim/device/api/crypto/PrivateKey;
      // 18a: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedIncludedCertificatesField.addCertificate (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/PrivateKey;)V
      // 18d: iinc 7 1
      // 190: goto 175
      // 193: aload 3
      // 194: aload 6
      // 196: invokevirtual net/rim/device/apps/internal/secureemail/cache/CachedManager.addField (Lnet/rim/device/apps/internal/secureemail/cache/CachedField;)V
      // 199: bipush 1
      // 19a: istore 4
      // 19c: iload 4
      // 19e: ireturn
      // try (2 -> 7): 29 null
      // try (8 -> 28): 29 null
      // try (2 -> 7): 118 null
      // try (8 -> 28): 118 null
   }

   protected final boolean getFieldsFromInputStream_PGP(PGPInputStream pgpInputStream, boolean opaqueData, CachedManager cachedManager) {
      boolean gotFields = false;
      CachedManager innerManager = cachedManager;
      if (pgpInputStream instanceof PGPSignedInputStream) {
         cachedManager.getCachedMessage().setSigned(true);
         int besVerificationState = this.mapParameterValueStringToInt("v", SecureEmailConstants.BES_VERIFICATION_STATES);
         String cannotVerifyOnDeviceReasonValue = (String)super._securityEncodingOptionalParameterTable.get("nv");
         innerManager = new CachedPGPSignatureField(
            (PGPSignedInputStream)pgpInputStream,
            (PGPBodyModel)super._secureEmailBodyModel,
            besVerificationState,
            this.getBESSignerCertificateHash(),
            cannotVerifyOnDeviceReasonValue,
            this._plainTextLeading
         );
         cachedManager.addField(innerManager);
         gotFields = true;
      } else if (!(pgpInputStream instanceof PGPEncryptedInputStream)) {
         if (pgpInputStream instanceof PGPCompressedInputStream) {
            opaqueData = true;
         }
      } else {
         PGPEncryptedInputStream pgpEncryptedInputStream = (PGPEncryptedInputStream)pgpInputStream;
         boolean enrollmentResponse = this.checkEnrollmentResponse(pgpEncryptedInputStream);
         if (enrollmentResponse) {
            super._secureEmailBodyModel.setText(PGPResources.getString(8105));
            cachedManager.getCachedMessage().setDoNotCache();
         } else {
            int besEncryptionState = -1;
            int besWeakRecipientState = -1;
            CachedMessage cachedMessage = cachedManager.getCachedMessage();
            if (!cachedMessage.isEncrypted()) {
               besEncryptionState = this.mapParameterValueStringToInt("be", SecureEmailConstants.BES_ENCRYPTION_STATES);
               besWeakRecipientState = this.mapParameterValueStringToInt("wr", SecureEmailConstants.BES_WEAK_RECIPIENT_STATES);
            }

            Certificate decryptionCertificate = pgpEncryptedInputStream.getPGPCertificate(super._preferredKeyStore);
            cachedMessage.addDecryptionCertificate(decryptionCertificate, super._preferredKeyStore);
            cachedMessage.setEncrypted(true);
            innerManager = new CachedPGPEncryptionField(pgpEncryptedInputStream, besEncryptionState, besWeakRecipientState);
            cachedManager.addField(innerManager);
         }

         opaqueData = true;
         gotFields = true;
      }

      PGPInputStream nextStream = pgpInputStream.getPGPInputStream();
      if (nextStream == null) {
         int containsMIMEState = this.mapParameterValueStringToInt("m", SecureEmailConstants.CONTAINS_MIME_STATES);
         if (containsMIMEState == 0) {
            MIMEInputStream mimeInputStream = new MIMEInputStream(pgpInputStream);
            gotFields |= this.getFieldsFromInputStream(mimeInputStream, innerManager);
         } else {
            gotFields |= this.getFieldsFromInputStream_Text(pgpInputStream, innerManager);
         }
      } else {
         do {
            gotFields |= this.getFieldsFromInputStream(nextStream, opaqueData, innerManager);
            nextStream = pgpInputStream.getNextStream();
         } while (nextStream != null);
      }

      return gotFields;
   }

   private final boolean checkEnrollmentResponse(PGPEncryptedInputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 03: ldc2_w 3676539340381219095
      // 06: invokevirtual net/rim/device/api/system/ApplicationRegistry.getHashtable (J)Ljava/util/Hashtable;
      // 09: astore 2
      // 0a: aload 2
      // 0b: invokevirtual java/util/Hashtable.isEmpty ()Z
      // 0e: ifeq 1d
      // 11: ldc2_w 234044482576569793
      // 14: ldc_w 1431195461
      // 17: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 1a: pop
      // 1b: bipush 0
      // 1c: ireturn
      // 1d: aload 1
      // 1e: invokevirtual net/rim/device/api/crypto/pgp/PGPEncryptedInputStream.getKeyID ()[B
      // 21: astore 3
      // 22: aload 2
      // 23: new net/rim/device/internal/util/ByteArray
      // 26: dup
      // 27: aload 3
      // 28: invokespecial net/rim/device/internal/util/ByteArray.<init> ([B)V
      // 2b: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 2e: checkcast java/lang/String
      // 31: astore 4
      // 33: aload 4
      // 35: ifnonnull 44
      // 38: ldc2_w 234044482576569793
      // 3b: ldc_w 1431194949
      // 3e: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // 41: pop
      // 42: bipush 0
      // 43: ireturn
      // 44: aload 0
      // 45: getfield net/rim/device/apps/internal/secureemail/SecureEmailProcessor._secureEmailFactory Lnet/rim/device/apps/internal/secureemail/SecureEmailFactory;
      // 48: invokevirtual net/rim/device/apps/internal/secureemail/SecureEmailFactory.getUtilities ()Lnet/rim/device/apps/internal/secureemail/SecureEmailUtilities;
      // 4b: checkcast net/rim/device/apps/internal/secureemail/encodings/pgp/PGPUtilities
      // 4e: aconst_null
      // 4f: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/PGPUtilities.getUniversalServerAddress (Ljava/lang/String;)Ljava/lang/String;
      // 52: astore 5
      // 54: invokestatic net/rim/device/api/xml/parsers/SAXParserFactory.newInstance ()Lnet/rim/device/api/xml/parsers/SAXParserFactory;
      // 57: invokevirtual net/rim/device/api/xml/parsers/SAXParserFactory.newSAXParser ()Lnet/rim/device/api/xml/parsers/SAXParser;
      // 5a: astore 6
      // 5c: new net/rim/device/api/xml/XMLHashtable
      // 5f: dup
      // 60: aload 6
      // 62: aload 1
      // 63: bipush 0
      // 64: bipush 0
      // 65: invokespecial net/rim/device/api/xml/XMLHashtable.<init> (Lnet/rim/device/api/xml/parsers/SAXParser;Ljava/io/InputStream;ZZ)V
      // 68: astore 7
      // 6a: aload 7
      // 6c: ldc_w "/EnrollmentStatus/user-value"
      // 6f: invokevirtual net/rim/device/api/xml/XMLHashtable.getString (Ljava/lang/String;)Ljava/lang/String;
      // 72: astore 8
      // 74: aload 4
      // 76: aload 8
      // 78: ldc_w 1701707776
      // 7b: invokestatic net/rim/device/api/util/StringUtilities.strEqualIgnoreCase (Ljava/lang/String;Ljava/lang/String;I)Z
      // 7e: ifne 83
      // 81: bipush 0
      // 82: ireturn
      // 83: aload 7
      // 85: ldc_w "/EnrollmentStatus/cookie"
      // 88: invokevirtual net/rim/device/api/xml/XMLHashtable.getString (Ljava/lang/String;)Ljava/lang/String;
      // 8b: astore 9
      // 8d: aload 9
      // 8f: ifnonnull 94
      // 92: bipush 0
      // 93: ireturn
      // 94: aload 5
      // 96: aload 9
      // 98: aload 3
      // 99: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.completeEnrollmentEmail (Ljava/lang/String;Ljava/lang/String;[B)V
      // 9c: bipush 1
      // 9d: ireturn
      // 9e: astore 6
      // a0: goto aa
      // a3: astore 6
      // a5: goto aa
      // a8: astore 6
      // aa: ldc2_w 234044482576569793
      // ad: ldc_w 1430865234
      // b0: invokestatic net/rim/device/api/system/EventLogger.logEvent (JI)Z
      // b3: pop
      // b4: bipush 0
      // b5: ireturn
      // try (39 -> 60): 75 null
      // try (61 -> 68): 75 null
      // try (69 -> 74): 75 null
      // try (39 -> 60): 77 null
      // try (61 -> 68): 77 null
      // try (69 -> 74): 77 null
      // try (39 -> 60): 79 null
      // try (61 -> 68): 79 null
      // try (69 -> 74): 79 null
   }
}
