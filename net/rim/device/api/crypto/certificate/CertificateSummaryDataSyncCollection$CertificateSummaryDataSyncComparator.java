package net.rim.device.api.crypto.certificate;

import net.rim.device.api.util.Comparator;

class CertificateSummaryDataSyncCollection$CertificateSummaryDataSyncComparator implements Comparator {
   private CertificateSummaryDataSyncCollection$CertificateSummaryDataSyncComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      int hash1 = o1.hashCode();
      int hash2 = o2.hashCode();
      if (hash1 == hash2) {
         return 0;
      } else {
         return hash1 < hash2 ? -1 : 1;
      }
   }
}
