package net.rim.plazmic.internal.mediaengine.registry;

import java.util.Hashtable;

public class RegistryImpl implements Registry {
   private Hashtable registry = (Hashtable)(new Object());
   private String[] supportedVersions = new Object[4];
   private int numSupportedVersion = 0;
   private static final String APPLICATION_RIM_PME;
   private static final int MAX_NUM_SUPPORT_VERSION;

   public String getMaxSupportedVersion() {
      String maxSupportedVersion = null;
      if (this.supportedVersions != null) {
         maxSupportedVersion = this.supportedVersions[0];

         for (int i = 0; i < this.numSupportedVersion; i++) {
            if (this.supportedVersions[i].compareTo(maxSupportedVersion) > 0) {
               maxSupportedVersion = this.supportedVersions[i];
            }
         }
      }

      return maxSupportedVersion;
   }

   public String getMinSupportedVersion() {
      String minSupportedVersion = null;
      if (this.supportedVersions != null) {
         minSupportedVersion = this.supportedVersions[0];

         for (int i = 0; i < this.numSupportedVersion; i++) {
            if (this.supportedVersions[i].compareTo(minSupportedVersion) < 0) {
               minSupportedVersion = this.supportedVersions[i];
            }
         }
      }

      return minSupportedVersion;
   }

   @Override
   public void setValue(String[] keys, String value) {
      this.registry.put(getKey(keys), value);

      for (int i = 0; i < keys.length; i++) {
         if (keys[i].equals("application/x-vnd.rim.pme")) {
            i++;
            if (i < keys.length && !keys[i].equals("VERSION_READER")) {
               this.supportedVersions[this.numSupportedVersion++] = keys[i];
            }
         }
      }
   }

   @Override
   public String getValue(String[] keys) {
      return (String)this.registry.get(getKey(keys));
   }

   public static final String getKey(String[] keys) {
      StringBuffer key = (StringBuffer)(new Object());

      for (int i = 0; i < keys.length; i++) {
         key.append(keys[i]);
         if (keys.length - i > 1) {
            key.append('-');
         }
      }

      return key.toString();
   }
}
