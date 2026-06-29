package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.vm.Array;

public final class TrustedKeyStore extends SyncableRIMKeyStore {
   private static final long TRUSTEDKEYSTORE;
   private static SyncableRIMKeyStore _trustedKeyStore;

   private TrustedKeyStore() {
      super("Trusted", KeyStoreResources.getString(4), -3805845534753462595L, null, new TrustedKeyStoreFactory());
      this.performITPolicyCheck();
   }

   public static final KeyStore getInstance() {
      if (_trustedKeyStore == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _trustedKeyStore = (TrustedKeyStore)ar.getOrWaitFor(-3805845534753462595L);
         if (_trustedKeyStore == null) {
            try {
               _trustedKeyStore = new TrustedKeyStore();
               ar.put(-3805845534753462595L, _trustedKeyStore);
            } catch (KeyStoreRegisterException e) {
               throw new Object();
            }
         }
      }

      return _trustedKeyStore;
   }

   @Override
   protected final KeyStoreData set(
      AssociatedData[] associatedData,
      String label,
      PrivateKey privateKey,
      String privateKeyEncodingAlgorithm,
      int securityLevel,
      PublicKey publicKey,
      long keyUsage,
      Certificate certificate,
      CertificateStatus certStatus,
      KeyStoreTicket ticket
   ) {
      ControlledAccess.assertRCISignatures(true);
      this.checkITPolicy(certificate == null ? null : certificate.getEncoding());
      return super.set(associatedData, label, null, privateKeyEncodingAlgorithm, 2, publicKey, keyUsage, certificate, certStatus, ticket);
   }

   final KeyStoreData set(String label, byte[] encoding, String type, CertificateStatus status) {
      ControlledAccess.assertRCISignatures(true);

      try {
         this.checkITPolicy(encoding);
      } finally {
         ;
      }

      RIMKeyStoreData data = new RIMKeyStoreData(label, encoding, type, status);
      this.set(null, data);
      return data;
   }

   @Override
   final void set(
      AssociatedData[] associatedData,
      String label,
      byte[] privateKey,
      String privateKeyEncodingAlgorithm,
      PublicKey publicKey,
      long keyUsage,
      byte[] certificate,
      String certificateType,
      CertificateStatus certStatus,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed,
      KeyStoreTicket ticket
   ) {
      ControlledAccess.assertRCISignatures(true);
      this.checkITPolicy(certificate);
      super.set(
         associatedData,
         label,
         privateKey,
         privateKeyEncodingAlgorithm,
         publicKey,
         keyUsage,
         certificate,
         certificateType,
         certStatus,
         uid,
         hashes,
         indices,
         notUsed,
         ticket
      );
   }

   @Override
   final void set(
      AssociatedData[] associatedData,
      String label,
      byte[] symmetricKey,
      String symmetricKeyEncodingAlgorithm,
      int uid,
      int[] hashes,
      long[] indices,
      long[] notUsed,
      KeyStoreTicket ticket
   ) {
      ControlledAccess.assertRCISignatures(true);
      super.set(associatedData, label, symmetricKey, symmetricKeyEncodingAlgorithm, uid, hashes, indices, notUsed, ticket);
   }

   @Override
   public final KeyStoreData set(
      AssociatedData[] associatedData, String label, SymmetricKey symmetricKey, String symmetricKeyEncodingAlgorithm, int securityLevel, KeyStoreTicket ticket
   ) {
      ControlledAccess.assertRCISignatures(true);
      return super.set(associatedData, label, symmetricKey, symmetricKeyEncodingAlgorithm, securityLevel, ticket);
   }

   @Override
   public final void removeKey(KeyStoreData data, KeyStoreTicket ticket) {
      ControlledAccess.assertRCISignatures(true);
      super.removeKey(data, ticket);
   }

   @Override
   public final boolean checkTicket(KeyStoreTicket ticket) {
      ControlledAccess.assertRCISignatures(true);
      return !(ticket instanceof SyncableRIMKeyStoreTicket) ? false : ((SyncableRIMKeyStoreTicket)ticket).access(this);
   }

   public final boolean isAllowed(Certificate certificate) {
      if (certificate == null) {
         return false;
      }

      try {
         this.checkITPolicy(certificate.getEncoding());
         return true;
      } finally {
         ;
      }
   }

   private final void checkITPolicy(byte[] encoding) {
      String thumbprints = ITPolicy.getString(24, 24);
      if (thumbprints != null) {
         this.checkITPolicy(encoding, this.getThumbprints(thumbprints));
      }
   }

   private final void checkITPolicy(byte[] encoding, byte[][][] thumbprints) {
      if (encoding != null && thumbprints != null) {
         if (thumbprints.length == 0) {
            throw new Object();
         }

         Digest digest = (Digest)(new Object());
         digest.update(encoding);
         byte[] shaThumb = digest.getDigest();
         digest = (Digest)(new Object());
         digest.update(encoding);
         byte[] md5Thumb = digest.getDigest();
         int thumbsLength = thumbprints.length;

         for (int i = 0; i < thumbsLength; i++) {
            if (Arrays.equals((byte[])thumbprints[i], shaThumb) || Arrays.equals((byte[])thumbprints[i], md5Thumb)) {
               return;
            }
         }

         throw new Object();
      }
   }

   private final byte[][][] getThumbprints(String thumbprints) {
      StringTokenizer tokenizer = (StringTokenizer)(new Object(thumbprints, ';'));
      int numTokens = tokenizer.countTokens();
      byte[][][] validThumbprints = new byte[numTokens][][];
      int numValidThumbprints = 0;

      for (int i = 0; i < numTokens; i++) {
         try {
            validThumbprints[numValidThumbprints] = (byte[][])this.convertToByteArray(this.stripSpaces(tokenizer.nextToken().getBytes()));
            numValidThumbprints++;
         } finally {
            continue;
         }
      }

      Array.resize(validThumbprints, numValidThumbprints);
      return validThumbprints;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void performITPolicyCheck() {
      String thumbprints = ITPolicy.getString(24, 24);
      if (thumbprints != null) {
         byte[][][] thumbs = this.getThumbprints(thumbprints);
         Vector toBeRemoved = (Vector)(new Object());
         Enumeration enumeration = this.elements();

         while (enumeration.hasMoreElements()) {
            RIMKeyStoreData data = (RIMKeyStoreData)enumeration.nextElement();
            if (data._payload._encoding != null) {
               boolean var8 = false /* VF: Semaphore variable */;

               try {
                  var8 = true;
                  this.checkITPolicy(data._payload._encoding, thumbs);
                  var8 = false;
               } finally {
                  if (var8) {
                     toBeRemoved.addElement(data);
                     continue;
                  }
               }
            }
         }

         enumeration = toBeRemoved.elements();

         while (enumeration.hasMoreElements()) {
            KeyStoreData data = (KeyStoreData)enumeration.nextElement();
            this.deleteKey(data);
         }
      }
   }

   private final byte[] stripSpaces(byte[] data) {
      if (data == null) {
         throw new Object();
      }

      int length = 0;
      byte[] cleanData = new byte[data.length];

      for (int i = 0; i < data.length; i++) {
         if (data[i] != 32 && data[i] != 9) {
            cleanData[length++] = data[i];
         }
      }

      Array.resize(cleanData, length);
      return cleanData;
   }

   private final byte[] convertToByteArray(byte[] hexString) {
      if (hexString == null) {
         throw new Object();
      }

      int length = hexString.length;
      if (length >= 2 && length <= 512 && (length & 1) == 0) {
         byte[] value = new byte[length >> 1];
         int shift = 4;

         for (int i = 0; i < length; i++) {
            byte c = hexString[i];
            if (c >= 48 && c <= 57) {
               c = (byte)(c - 48);
            } else if (c >= 65 && c <= 70) {
               c = (byte)(c - 55);
            } else {
               if (c < 97 || c > 102) {
                  throw new Object();
               }

               c = (byte)(c - 87);
            }

            value[i >> 1] = (byte)(value[i >> 1] | (byte)(c << shift));
            shift ^= 4;
         }

         return value;
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getName() {
      return KeyStoreResources.getString(4);
   }
}
