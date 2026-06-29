package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.ldap.LDAPAttribute;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;

public class X509LDAPCertificateFetch extends LDAPCertificateFetch {
   private static String USER_CERT_BINARY = "usercertificate;binary";
   private static String USER_CERT = "usercertificate";
   private static String MAIL = "mail";
   private static String COMMON_NAME = "cn";
   private static String COMMON_NAME_PREFIX = "cn=";

   @Override
   protected void addAttributesAndFilters(LDAPQuery query, String[] emailAddresses) {
      query.addAttribute(USER_CERT);
      query.addAttribute(USER_CERT_BINARY);
      StringBuffer buffer = new StringBuffer();
      int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;
      if (numEmailAddresses > 0) {
         buffer.append('(').append('|');

         for (int i = 0; i < numEmailAddresses; i++) {
            buffer.append('(').append(MAIL).append('=').append(emailAddresses[i]).append(')');
            StringBuffer dominoCanonicalNameFilter = new StringBuffer();
            StringBuffer dominoCanonicalNameQuery = new StringBuffer();
            if (this.parseDominoCanonicalName(emailAddresses[i], dominoCanonicalNameFilter, dominoCanonicalNameQuery)) {
               buffer.append(dominoCanonicalNameFilter.toString());
            }
         }

         buffer.append(')');
         query.addFilter(buffer.toString());
      }

      buffer.setLength(0);
      buffer.append('(').append('|');
      buffer.append('(').append(USER_CERT).append('=').append('*').append(')');
      buffer.append('(').append(USER_CERT_BINARY).append('=').append('*').append(')');
      buffer.append(')');
      query.addFilter(buffer.toString());
   }

   private boolean parseDominoCanonicalName(String emailAddress, StringBuffer dominoCanonicalNameFilter, StringBuffer dominoCanonicalNameQuery) {
      int atIndex = emailAddress.indexOf(64);
      String possibleDominoCanonicalName;
      if (atIndex >= 0) {
         possibleDominoCanonicalName = emailAddress.substring(0, atIndex);
      } else {
         possibleDominoCanonicalName = emailAddress;
      }

      if (StringUtilities.startsWithIgnoreCase(possibleDominoCanonicalName, COMMON_NAME_PREFIX, 1701707776) && possibleDominoCanonicalName.indexOf(47) >= 0) {
         dominoCanonicalNameFilter.setLength(0);
         dominoCanonicalNameQuery.setLength(0);
         StringTokenizer tokenizer = new StringTokenizer(possibleDominoCanonicalName, '/');

         while (tokenizer.hasMoreTokens()) {
            String currentToken = StringUtilities.toLowerCase(tokenizer.nextToken(), 1701707776);
            int equalsIndex = currentToken.indexOf(61);
            if (equalsIndex < 0) {
               return false;
            }

            if (currentToken.indexOf(COMMON_NAME) >= 0) {
               dominoCanonicalNameFilter.append('(').append(currentToken).append(')');
            } else {
               if (dominoCanonicalNameQuery.length() > 0) {
                  dominoCanonicalNameQuery.append(',');
               }

               dominoCanonicalNameQuery.append(currentToken);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void addAttributesAndFilters(LDAPQuery query, Object[] keyIDs) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void addCertificates(LDAPEntry entry, Certificate[] certificates) {
      if (entry != null && certificates != null) {
         LDAPAttribute certAttribute = null;
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            certAttribute = entry.getAttribute(USER_CERT_BINARY);
            var18 = false;
         } finally {
            if (var18) {
               boolean var14 = false /* VF: Semaphore variable */;

               label103:
               try {
                  var14 = true;
                  certAttribute = entry.getAttribute(USER_CERT);
                  var14 = false;
                  break label103;
               } finally {
                  if (var14) {
                     return;
                  }
               }
            }
         }

         int numCertificates = certAttribute.getSize();

         for (int i = 0; i < numCertificates; i++) {
            byte[] encoding = (byte[])certAttribute.getValue(i);

            try {
               Certificate certificate = CertificateFactory.getInstance("X509", encoding);
               if (certificate != null) {
                  Arrays.add(certificates, certificate);
               }
            } finally {
               continue;
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
