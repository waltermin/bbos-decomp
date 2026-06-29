package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.util.Comparator;

class PGPUniversalKeyCache$CachedKeyExpiryComparator implements Comparator {
   private PGPUniversalKeyCache$CachedKeyExpiryComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 == null) {
         return o2 == null ? 0 : -1;
      } else if (o2 == null) {
         return 1;
      } else {
         long expiryTime1 = ((PGPUniversalKeyCache$CachedKeyExpiryData)o1).getExpiryTime();
         long expiryTime2 = ((PGPUniversalKeyCache$CachedKeyExpiryData)o2).getExpiryTime();
         if (expiryTime1 < expiryTime2) {
            return -1;
         } else {
            return expiryTime1 == expiryTime2 ? 0 : 1;
         }
      }
   }

   PGPUniversalKeyCache$CachedKeyExpiryComparator(PGPUniversalKeyCache$1 x0) {
      this();
   }
}
