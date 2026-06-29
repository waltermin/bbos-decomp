package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.util.Arrays;

public class PKCS12Utilities {
   private PKCS12Utilities() {
   }

   public static SafeBag[] getAllSafeBags(PKCS12 p12) {
      PKCS12ContentInfo[] contentInfos = p12.getAuthenticatedSafe().getChildrenContentInfos();
      return getAllSafeBagsInternal(contentInfos);
   }

   private static SafeBag[] getAllSafeBagsInternal(PKCS12ContentInfo[] contentInfos) {
      if (contentInfos == null) {
         return null;
      }

      SafeBag[] safeBags = null;
      int size = contentInfos.length;

      for (int i = 0; i < size; i++) {
         if (contentInfos[i] instanceof SafeBag) {
            if (safeBags == null) {
               safeBags = new SafeBag[]{(SafeBag)contentInfos[i]};
            } else {
               Arrays.add(safeBags, (SafeBag)contentInfos[i]);
            }
         } else {
            SafeBag[] bags = getAllSafeBagsInternal(contentInfos[i].getChildrenContentInfos());
            if (bags != null) {
               if (safeBags == null) {
                  safeBags = bags;
               } else {
                  Arrays.append(safeBags, bags);
               }
            }
         }
      }

      return safeBags;
   }

   public static CertificateBag[] getAllCertificateBags(PKCS12 p12) {
      PKCS12ContentInfo[] contentInfos = p12.getAuthenticatedSafe().getChildrenContentInfos();
      return getAllCertificateBagsInternal(contentInfos);
   }

   private static CertificateBag[] getAllCertificateBagsInternal(PKCS12ContentInfo[] contentInfos) {
      if (contentInfos == null) {
         return null;
      }

      CertificateBag[] certificateBags = null;
      int size = contentInfos.length;

      for (int i = 0; i < size; i++) {
         if (contentInfos[i] instanceof CertificateBag) {
            if (certificateBags == null) {
               certificateBags = new CertificateBag[]{(CertificateBag)contentInfos[i]};
            } else {
               Arrays.add(certificateBags, (CertificateBag)contentInfos[i]);
            }
         } else {
            CertificateBag[] bags = getAllCertificateBagsInternal(contentInfos[i].getChildrenContentInfos());
            if (bags != null) {
               if (certificateBags == null) {
                  certificateBags = bags;
               } else {
                  Arrays.append(certificateBags, bags);
               }
            }
         }
      }

      return certificateBags;
   }

   public static Certificate[] getAllCertificates(PKCS12 p12) {
      CertificateBag[] bags = getAllCertificateBags(p12);
      if (bags == null) {
         return null;
      }

      int size = bags.length;
      Certificate[] certificates = new Certificate[size];

      for (int i = 0; i < size; i++) {
         certificates[i] = bags[i].getCertificate();
      }

      return certificates;
   }

   public static KeyBag[] getAllKeyBags(PKCS12 p12) {
      PKCS12ContentInfo[] contentInfos = p12.getAuthenticatedSafe().getChildrenContentInfos();
      return getAllKeyBagsInternal(contentInfos);
   }

   private static KeyBag[] getAllKeyBagsInternal(PKCS12ContentInfo[] contentInfos) {
      if (contentInfos == null) {
         return null;
      }

      KeyBag[] keyBags = null;
      int size = contentInfos.length;

      for (int i = 0; i < size; i++) {
         if (contentInfos[i] instanceof KeyBag) {
            if (keyBags == null) {
               keyBags = new KeyBag[]{(KeyBag)contentInfos[i]};
            } else {
               Arrays.add(keyBags, (KeyBag)contentInfos[i]);
            }
         } else {
            KeyBag[] bags = getAllKeyBagsInternal(contentInfos[i].getChildrenContentInfos());
            if (bags != null) {
               if (keyBags == null) {
                  keyBags = bags;
               } else {
                  Arrays.append(keyBags, bags);
               }
            }
         }
      }

      return keyBags;
   }

   public static PrivateKey[] getAllPrivateKeys(PKCS12 p12) {
      KeyBag[] bags = getAllKeyBags(p12);
      if (bags == null) {
         return null;
      }

      int size = bags.length;
      PrivateKey[] keys = new PrivateKey[size];

      for (int i = 0; i < size; i++) {
         keys[i] = bags[i].getPrivateKey();
      }

      return keys;
   }

   public static CertificatePrivateKeyPair[] getAllCertificateKeyPairs(PKCS12 p12) {
      CertificateBag[] certBags = getAllCertificateBags(p12);
      KeyBag[] keyBags = getAllKeyBags(p12);
      if (certBags == null && keyBags == null) {
         return null;
      }

      CertificatePrivateKeyPair[] certKeyPairs = new CertificatePrivateKeyPair[0];
      if (certBags == null) {
         int keyBagLength = keyBags.length;

         for (int i = 0; i < keyBagLength; i++) {
            Arrays.add(certKeyPairs, new CertificatePrivateKeyPair(null, keyBags[i].getPrivateKey()));
         }

         return certKeyPairs;
      } else if (keyBags == null) {
         int certBagLength = certBags.length;

         for (int i = 0; i < certBagLength; i++) {
            Arrays.add(certKeyPairs, new CertificatePrivateKeyPair(certBags[i].getCertificate(), null));
         }

         return certKeyPairs;
      } else {
         int certBagLength = certBags.length;
         int keyBagLength = keyBags.length;
         String certFriendlyName = null;
         String keyFriendlyName = null;

         for (int i = 0; i < keyBagLength; i++) {
            boolean paired = false;
            keyFriendlyName = keyBags[i].getFriendlyName();
            if (keyFriendlyName != null) {
               certBagLength = certBags.length;

               for (int j = 0; j < certBagLength; j++) {
                  certFriendlyName = certBags[j].getFriendlyName();
                  if (certFriendlyName != null && certFriendlyName.equals(keyFriendlyName)) {
                     CertificatePrivateKeyPair ckPair = new CertificatePrivateKeyPair(certBags[j].getCertificate(), keyBags[i].getPrivateKey());
                     Arrays.add(certKeyPairs, ckPair);
                     Arrays.remove(certBags, certBags[j]);
                     paired = true;
                     break;
                  }
               }

               if (paired) {
                  Arrays.remove(keyBags, keyBags[i]);
               }
            }
         }

         byte[] certId = null;
         byte[] keyId = null;
         keyBagLength = keyBags.length;

         for (int i = 0; i < keyBagLength; i++) {
            boolean paired = false;
            keyId = keyBags[i].getLocalKeyId();
            if (keyId != null) {
               certBagLength = certBags.length;

               for (int j = 0; j < certBagLength; j++) {
                  certId = certBags[j].getLocalKeyId();
                  if (certId != null && Arrays.equals(certId, keyId)) {
                     CertificatePrivateKeyPair ckPair = new CertificatePrivateKeyPair(certBags[j].getCertificate(), keyBags[i].getPrivateKey());
                     Arrays.add(certKeyPairs, ckPair);
                     Arrays.remove(certBags, certBags[j]);
                     paired = true;
                     break;
                  }
               }

               if (paired) {
                  Arrays.remove(keyBags, keyBags[i]);
               }
            }
         }

         certBagLength = certBags.length;

         for (int i = 0; i < certBagLength; i++) {
            Arrays.add(certKeyPairs, new CertificatePrivateKeyPair(certBags[i].getCertificate(), null));
         }

         keyBagLength = keyBags.length;

         for (int i = 0; i < keyBagLength; i++) {
            Arrays.add(certKeyPairs, new CertificatePrivateKeyPair(null, keyBags[i].getPrivateKey()));
         }

         return certKeyPairs;
      }
   }
}
