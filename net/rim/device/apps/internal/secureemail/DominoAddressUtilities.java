package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;

public class DominoAddressUtilities {
   private static final OID[] DOMINO_ADDRESS_OIDS = new Object[]{OIDs.getOID(-1253056853), OIDs.getOID(-1252532565), OIDs.getOID(-1252598101)};
   private static final String COMMON_NAME_PREFIX = "cn=";

   private DominoAddressUtilities() {
   }

   public static X509DistinguishedName createDominoAddressDN(String dominoAddress) {
      int atIndex = dominoAddress.indexOf(64);
      if (atIndex >= 0) {
         dominoAddress = dominoAddress.substring(0, atIndex);
      }

      if (StringUtilities.startsWithIgnoreCase(dominoAddress, "cn=", 1701707776) && dominoAddress.indexOf(47) >= 0) {
         try {
            return (X509DistinguishedName)(new Object(dominoAddress, '/'));
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean dominoAddressMatchesX509DN(String dominoAddress, X509DistinguishedName x509DN) {
      X509DistinguishedName dominoAddressDN = createDominoAddressDN(dominoAddress);
      return dominoAddressDN == null ? false : dominoAddressDNMatchesX509DN(dominoAddressDN, x509DN);
   }

   public static boolean dominoAddressDNMatchesX509DN(X509DistinguishedName dominoAddressDN, X509DistinguishedName x509DN) {
      int numOIDs = DOMINO_ADDRESS_OIDS.length;

      for (int i = 0; i < numOIDs; i++) {
         OID currentOID = DOMINO_ADDRESS_OIDS[i];
         String[] dominoOIDValues = dominoAddressDN.getValues(currentOID);
         String[] x509OIDValues = x509DN.getValues(currentOID);
         int numDominoOIDValues = dominoOIDValues != null ? dominoOIDValues.length : 0;
         int numX509OIDValues = x509OIDValues != null ? x509OIDValues.length : 0;
         if (numDominoOIDValues != numX509OIDValues) {
            return false;
         }

         for (int j = 0; j < numDominoOIDValues; j++) {
            String currentDominoOIDValue = dominoOIDValues[j];

            for (int k = 0; k < numX509OIDValues; k++) {
               if (StringUtilities.strEqualIgnoreCase(x509OIDValues[k], currentDominoOIDValue, 1701707776)) {
                  Arrays.removeAt(x509OIDValues, k);
                  numX509OIDValues--;
                  break;
               }
            }
         }

         if (numX509OIDValues > 0) {
            return false;
         }
      }

      return true;
   }

   public static int computeDominoAddressHashCode(X509DistinguishedName distinguishedName) {
      int hashCode = 0;
      int numOIDs = DOMINO_ADDRESS_OIDS.length;

      for (int i = 0; i < numOIDs; i++) {
         OID currentOID = DOMINO_ADDRESS_OIDS[i];
         String[] currentOIDValues = distinguishedName.getValues(currentOID);
         int numCurrentOIDValues = currentOIDValues != null ? currentOIDValues.length : 0;

         for (int j = 0; j < numCurrentOIDValues; j++) {
            hashCode ^= currentOID.hashCode();
            hashCode ^= StringUtilities.toLowerCase(currentOIDValues[j], 1701707776).hashCode();
         }
      }

      return hashCode;
   }
}
