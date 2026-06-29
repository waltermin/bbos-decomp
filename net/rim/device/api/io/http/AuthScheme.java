package net.rim.device.api.io.http;

import java.util.Hashtable;
import net.rim.device.api.util.StringUtilities;

public class AuthScheme {
   protected Hashtable _parms;
   public static final int BASIC = 0;
   public static final int DIGEST = 1;
   protected static final String BASIC_SCHEME = "basic";
   protected static final String DIGEST_SCHEME = "digest";
   protected static final String NEGOTIATE_SCHEME = "negotiate";

   public AuthScheme(Hashtable parms) {
      this._parms = parms;
   }

   public static AuthScheme getAuthScheme(String challenge) {
      if (challenge == null) {
         return null;
      }

      Hashtable parms = new Hashtable();
      String lChallenge = StringUtilities.toLowerCase(challenge, 1701707776);
      int currentIndex;
      int type;
      if (lChallenge.startsWith("basic")) {
         type = 0;
         currentIndex = lChallenge.indexOf("basic") + 5;
      } else if (lChallenge.startsWith("digest")) {
         type = 1;
         currentIndex = lChallenge.indexOf("digest") + 6;
      } else {
         currentIndex = lChallenge.indexOf("basic");
         if (currentIndex == -1) {
            return null;
         }

         type = 0;
         currentIndex += 5;
      }

      int equalIndex;
      while ((equalIndex = challenge.indexOf(61, currentIndex)) > 0) {
         String name = challenge.substring(currentIndex, equalIndex).trim();
         if (StringUtilities.strEqualIgnoreCase(name, "realm", 1701707776)) {
            currentIndex = readValue("realm", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "nonce", 1701707776)) {
            currentIndex = readValue("nonce", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "opaque", 1701707776)) {
            currentIndex = readValue("opaque", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "domain", 1701707776)) {
            currentIndex = readValue("domain", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "qop", 1701707776)) {
            currentIndex = readValue("qop", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "algorithm", 1701707776)) {
            currentIndex = readValue("algorithm", parms, challenge, equalIndex);
         } else if (StringUtilities.strEqualIgnoreCase(name, "stale", 1701707776)) {
            currentIndex = readValue("stale", parms, challenge, equalIndex);
         }

         int commaIndex = challenge.indexOf(44, currentIndex);
         int semicolonIndex = challenge.indexOf(59, currentIndex);
         if (commaIndex == -1 || semicolonIndex != -1 && semicolonIndex < commaIndex) {
            commaIndex = semicolonIndex;
         }

         if (commaIndex < 0) {
            break;
         }

         currentIndex = commaIndex + 1;
      }

      if (type == 0) {
         return new BasicAuthScheme(parms);
      } else {
         return type == 1 ? new DigestAuthScheme(parms) : null;
      }
   }

   static int readValue(String name, Hashtable parms, String challenge, int equalIndex) {
      int quoteIndex = challenge.indexOf(34, equalIndex + 1);
      int secondQuoteIndex = challenge.indexOf(34, equalIndex + 2);
      int commaIndex = challenge.indexOf(44, equalIndex + 2);
      int semicolonIndex = challenge.indexOf(59, equalIndex + 2);
      if (commaIndex == -1 || semicolonIndex != -1 && semicolonIndex < commaIndex) {
         commaIndex = semicolonIndex;
      }

      if (commaIndex < 0) {
         commaIndex = challenge.length();
      }

      if (quoteIndex != equalIndex + 1) {
         parms.put(name, challenge.substring(equalIndex + 1, commaIndex));
         return commaIndex;
      } else {
         parms.put(name, challenge.substring(quoteIndex + 1, secondQuoteIndex));
         return secondQuoteIndex;
      }
   }

   public void setParameter(String name, String value) {
      this._parms.put(name, value);
   }

   public String getParameter(String name) {
      return (String)this._parms.get(name);
   }

   public String getAuthResponse() {
      throw null;
   }

   public void updateAuth(String _1) {
      throw null;
   }

   public int getType() {
      throw null;
   }
}
