package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.crypto.pgp.PGPEncryptedInputStream;
import net.rim.device.apps.internal.secureemail.SecureEmailEncryptionField;

public final class PGPEncryptionField extends SecureEmailEncryptionField {
   private PGPEncryptedInputStream _pgpEncryptedInputStream;
   private PGPCertificate _recipientCertificate;

   public PGPEncryptionField(PGPEncryptedInputStream pgpEncryptedInputStream, int besEncryptionState, int besWeakRecipientState, Object context) {
      super(PGPFactory.getInstance(), besEncryptionState, besWeakRecipientState, context);
      this._pgpEncryptedInputStream = pgpEncryptedInputStream;
      this.initialize();
   }

   @Override
   protected final SymmetricKey getSessionKey() {
      return this._pgpEncryptedInputStream.getSessionKey();
   }

   @Override
   protected final String getRecipientPublicKeyAlgorithm() {
      Certificate recipientCertificate = this.getRecipientCertificate();
      return recipientCertificate != null ? recipientCertificate.getPublicKeyAlgorithm() : null;
   }

   @Override
   protected final int getRecipientPublicKeyBitLength() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/apps/internal/secureemail/encodings/pgp/PGPEncryptionField.getRecipientCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 04: astore 1
      // 05: aload 1
      // 06: ifnull 21
      // 09: aload 1
      // 0a: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 0f: invokeinterface net/rim/device/api/crypto/PublicKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem; 1
      // 14: invokeinterface net/rim/device/api/crypto/CryptoSystem.getBitLength ()I 1
      // 19: ireturn
      // 1a: astore 2
      // 1b: bipush 0
      // 1c: ireturn
      // 1d: astore 2
      // 1e: bipush 0
      // 1f: ireturn
      // 20: astore 2
      // 21: bipush 0
      // 22: ireturn
      // try (5 -> 9): 10 null
      // try (5 -> 9): 13 null
      // try (5 -> 9): 16 null
   }

   @Override
   protected final Certificate getRecipientCertificate() {
      if (this._recipientCertificate == null) {
         this._recipientCertificate = this._pgpEncryptedInputStream.getPGPCertificate(PGPKeyStore.getInstance());
      }

      return this._recipientCertificate;
   }

   @Override
   protected final int getContentCipher() {
      return PGPFactory.getInstance().getUtilities().getContentCipherForConstant(this._pgpEncryptedInputStream.getContentCipherConstant());
   }
}
