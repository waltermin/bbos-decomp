package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateExtension;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataMap;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;

public class KeyUsageEmailAddressKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = 3687411874034296952L;
   public static final int SIGNING = 0;
   public static final int ENCRYPTION = 1;
   public static final int SIGNING_AND_ENCRYPTION = 2;

   @Override
   public long getID() {
      return 3687411874034296952L;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      Certificate certificate = data.getCertificate();
      if (certificate != null) {
         if (SecureEmailUtilities.isCertificateSupported(data, 2)) {
            if (certificate.queryKeyUsage(4096) == 0) {
               CertificateExtension extendedKeyUsageExtension = certificate.getExtension(OIDs.getOID(-1250822229));
               if (extendedKeyUsageExtension == null) {
                  return;
               }

               byte[] extendedKeyUsageExtensionValue = extendedKeyUsageExtension.getValue();
               if (extendedKeyUsageExtensionValue == null) {
                  return;
               }

               boolean var11 = false /* VF: Semaphore variable */;

               try {
                  var11 = true;
                  ASN1InputByteArray sign = new Object(extendedKeyUsageExtensionValue);
                  ((ASN1InputByteArray)sign).readSequence();
                  boolean encrypt = false;
                  int i = ((ASN1InputByteArray)sign).getEndPosition();

                  while (((ASN1InputByteArray)sign).getStartPosition() < i) {
                     OID oid = ((ASN1InputByteArray)sign).readOID();
                     if (oid.equals(OIDs.getOID(-477712248))) {
                        encrypt = true;
                        break;
                     }
                  }

                  if (!encrypt) {
                     return;
                  }

                  var11 = false;
               } finally {
                  if (var11) {
                     return;
                  }
               }
            }

            byte[][][] emailAddresses = (byte[][][])data.getAssociatedData(-1124699153917633064L);
            if (emailAddresses != null) {
               int numEmailAddresses = emailAddresses.length;
               boolean sign = certificate.queryKeyUsage(1) != 0;
               boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;

               for (int i = 0; i < numEmailAddresses; i++) {
                  if (sign) {
                     dataMap.add(KeyUsageEmailAddressKeyStoreIndex$Alias.computeHashCode(0, (byte[])emailAddresses[i]), data);
                     if (encrypt) {
                        dataMap.add(KeyUsageEmailAddressKeyStoreIndex$Alias.computeHashCode(2, (byte[])emailAddresses[i]), data);
                     }
                  }

                  if (encrypt) {
                     dataMap.add(KeyUsageEmailAddressKeyStoreIndex$Alias.computeHashCode(1, (byte[])emailAddresses[i]), data);
                  }
               }
            }
         }
      }
   }

   @Override
   public int getHash(Object target) {
      if (!(target instanceof KeyUsageEmailAddressKeyStoreIndex$Alias)) {
         throw new Object();
      }

      KeyUsageEmailAddressKeyStoreIndex$Alias a = (KeyUsageEmailAddressKeyStoreIndex$Alias)target;
      return a.hashCode();
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (!(target instanceof KeyUsageEmailAddressKeyStoreIndex$Alias)) {
         throw new Object();
      }

      KeyUsageEmailAddressKeyStoreIndex$Alias a = (KeyUsageEmailAddressKeyStoreIndex$Alias)target;
      byte[][][] emailAddresses = (byte[][][])data.getAssociatedData(-1124699153917633064L);
      if (emailAddresses == null) {
         return false;
      }

      Certificate certificate = data.getCertificate();
      if (certificate == null) {
         return false;
      }

      boolean sign = certificate.queryKeyUsage(1) != 0;
      boolean encrypt = certificate.queryKeyUsage(4) != 0 || certificate.queryKeyUsage(16) != 0;
      switch (a._usage) {
         case -1:
            throw new Object();
         case 0:
         default:
            if (!sign) {
               return false;
            }
            break;
         case 1:
            if (!encrypt) {
               return false;
            }
            break;
         case 2:
            if (!sign || !encrypt) {
               return false;
            }
      }

      int numEmailAddresses = emailAddresses.length;

      for (int i = 0; i < numEmailAddresses; i++) {
         if (Arrays.equals((byte[])emailAddresses[i], a._emailAddress)) {
            return true;
         }
      }

      return false;
   }

   public static Object getAlias(int usage, String emailAddress) {
      return new KeyUsageEmailAddressKeyStoreIndex$Alias(usage, StringUtilities.toLowerCase(emailAddress, 1701707776).getBytes());
   }
}
