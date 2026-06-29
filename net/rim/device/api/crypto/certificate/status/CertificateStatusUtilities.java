package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.crypto.certificate.CertificateStatus;

public class CertificateStatusUtilities {
   private CertificateStatusUtilities() {
   }

   public static int compareStatusCertificate(CertificateStatus object1, CertificateStatus object2) {
      if (object1 != null && object2 != null) {
         int status1 = object1.getStatus();
         int status2 = object2.getStatus();
         switch (status1) {
            case -1:
            default:
               switch (status2) {
                  case -1:
                  default:
                     return compareStatusProducedAt(object1, object2, true);
                  case 0:
                  case 1:
                     return -1;
               }
            case 0:
               switch (status2) {
                  case -1:
                  default:
                     return 1;
                  case 0:
                     return compareStatusProducedAt(object1, object2, true);
                  case 1:
                     return -1;
               }
            case 1:
               switch (status2) {
                  case 1:
                     return compareStatusProducedAt(object1, object2, true);
                  default:
                     return 1;
               }
         }
      } else {
         throw new Object();
      }
   }

   public static int compareStatusCertificateChain(CertificateStatus object1, CertificateStatus object2) {
      if (object1 != null && object2 != null) {
         int status1 = object1.getStatus();
         int status2 = object2.getStatus();
         switch (status1) {
            case -1:
            default:
               switch (status2) {
                  case -1:
                  default:
                     return compareStatusProducedAt(object1, object2, false);
                  case 0:
                     return 1;
                  case 1:
                     return -1;
               }
            case 0:
               switch (status2) {
                  case -1:
                  default:
                     return -1;
                  case 0:
                     return compareStatusProducedAt(object1, object2, false);
                  case 1:
                     return -1;
               }
            case 1:
               switch (status2) {
                  case 1:
                     return compareStatusProducedAt(object1, object2, false);
                  default:
                     return 1;
               }
         }
      } else {
         throw new Object();
      }
   }

   private static int compareStatusProducedAt(CertificateStatus object1, CertificateStatus object2, boolean preferNewer) {
      long producedAt1 = object1.getProducedAtTime();
      long producedAt2 = object2.getProducedAtTime();
      if (producedAt1 == producedAt2) {
         return 0;
      }

      int result;
      if (producedAt1 > producedAt2) {
         result = 1;
      } else {
         result = -1;
      }

      if (!preferNewer) {
         result *= -1;
      }

      return result;
   }
}
