package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.io.Base64OutputStream;

public final class UAProfDiff {
   private static String BEGIN_TAG = "<?xml version=\"1.0\"?><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:prf=\"http://www.openmobilealliance.org/tech/profiles/UAPROF/ccppschema-20021212#\"><rdf:Description rdf:ID=\"DeviceProfile\"><prf:component><rdf:Description rdf:ID=\"BrowserUA\">";
   private static String END_TAG = "</rdf:Description></prf:component></rdf:Description></rdf:RDF>";
   private static String _cachedProfileDiff;
   private static String _cachedXWapProfileDiff;
   private static String _cachedProfile;
   private static String _cachedXWapProfile;
   private static String _cachedMD5Value;
   private static byte _cachedBits;
   private static Object _syncObject = new Object();

   private UAProfDiff() {
   }

   public static final String getXWapProfile(String profile) {
      synchronized (_syncObject) {
         if (profile.equals(_cachedProfile)) {
            return _cachedXWapProfile;
         }

         _cachedProfile = profile;
         _cachedXWapProfile = ((StringBuffer)(new Object())).append('"').append(profile).append('"').toString();
         if (_cachedMD5Value != null) {
            _cachedXWapProfile = ((StringBuffer)(new Object())).append(_cachedXWapProfile).append(", \"1-").append(_cachedMD5Value).append('"').toString();
         }

         return _cachedXWapProfile;
      }
   }

   public static final String getProfileDiff(RenderingOptions renderingOptions) {
      verifyValues(renderingOptions);
      return _cachedProfileDiff;
   }

   public static final String getXWapProfileDiff(RenderingOptions renderingOptions) {
      verifyValues(renderingOptions);
      return _cachedXWapProfileDiff;
   }

   private static final void verifyValues(RenderingOptions renderingOptions) {
      synchronized (_syncObject) {
         boolean tablesOn = renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 17, true);
         boolean javascriptOn = renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, true);
         byte tempBits = 0;
         if (!tablesOn) {
            tempBits = (byte)(tempBits | 1);
         }

         if (!javascriptOn) {
            tempBits = (byte)(tempBits | 2);
         }

         if (_cachedBits != tempBits) {
            if (tablesOn && javascriptOn) {
               _cachedBits = tempBits;
               _cachedProfileDiff = null;
               _cachedXWapProfileDiff = null;
               _cachedMD5Value = null;
            } else {
               StringBuffer result = (StringBuffer)(new Object(BEGIN_TAG));
               if (!tablesOn) {
                  result.append("<prf:TablesCapable>No</prf:TablesCapable>");
               }

               if (!javascriptOn) {
                  result.append("<prf:JavaScriptEnabled>No</prf:JavaScriptEnabled>");
               }

               result.append(END_TAG);
               _cachedProfileDiff = result.toString();
               result.insert(0, "1; ");
               _cachedXWapProfileDiff = result.toString();

               label64:
               try {
                  MD5Digest digest = (MD5Digest)(new Object());
                  digest.update(_cachedProfileDiff.getBytes());
                  byte[] outData = digest.getDigest();
                  _cachedMD5Value = Base64OutputStream.encodeAsString(outData, 0, outData.length, false, false);
               } finally {
                  break label64;
               }

               _cachedBits = tempBits;
            }

            _cachedProfile = null;
         }
      }
   }
}
