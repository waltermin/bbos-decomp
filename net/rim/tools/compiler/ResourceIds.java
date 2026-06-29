package net.rim.tools.compiler;

import java.util.Hashtable;

public class ResourceIds {
   private static Hashtable _resourceIds;
   static String midletProfile = "MicroEdition-Profile";
   static String midletConfiguration = "MicroEdition-Configuration";
   static String midletInstallNotify = "MIDlet-Install-Notify";
   static String midletPrefix = "_midlet";
   static String iconTag = "RIM-MIDlet-Icon-";
   static String[] required = new String[]{"MIDlet-Name", "MIDlet-Version", "MIDlet-Vendor"};
   static String[] requiredIds = new String[]{"_midletName", "_version", "_vendor"};
   static String[] manifestVersion = new String[]{"Manifest-Version"};
   static String[] manifestVersionIds = new String[]{"_manifestVersion"};
   static String[] manifestRequired = new Object[]{midletProfile, midletConfiguration};
   static String[][][] manifestRequiredAllowed = new Object[][][]{{"MIDP-1.0", "MIDP-2.0"}, {"CLDC-1.0", "CLDC-1.1"}};
   static String[] manifestRequiredIds = new String[]{"_midletProfile", "_midletConfiguration"};
   static String[] jadRequired = new String[]{"MIDlet-Jar-URL", "MIDlet-Jar-Size"};
   static String[] jadRequiredIds = new String[]{"_url", "_midletJarSize"};
   static String[] optional = new String[]{"MIDlet-Description", "MIDlet-Icon", "MIDlet-Info-URL", "MIDlet-Data-Size", "RIM-Options"};
   static String[] optionalIds = new String[]{"_description", "_midletSuiteIcon28", "_midletInfo", "_midletDataSize", "_additionalOptions"};
   static String library = "RIM-Library-Flags";
   static String libraryId = "_appFlags";
   static String platform = "RIM-Platform";
   static String[] ordinalPrefix = new String[]{
      "MIDlet-", "RIM-MIDlet-Icon-Count-", "RIM-MIDlet-Flags-", "RIM-MIDlet-Position-", "RIM-MIDlet-NameResourceBundle-", "RIM-MIDlet-NameResourceId-"
   };
   static String[] ordinalPrefixIds = new Object[]{null, null, "_appFlags", "_appPosition", "_appNameResourceBundles", "_appNameResourceIds"};
   static String[] ams = new String[]{
      "RIM-COD-URL", "RIM-COD-Size", "RIM-COD-Creation-Time", "RIM-COD-Module-Name", "RIM-COD-Module-Dependencies", "RIM-COD-SHA1"
   };
   static final int ORDINAL_TYPE_BYTE;
   static final int ORDINAL_TYPE_SHORT;
   static final int ORDINAL_TYPE_INT;
   static final int ORDINAL_TYPE_LONG;
   static final int ORDINAL_TYPE_OBJECT;
   static final int ORDINAL_TYPE_STRING;
   static int[] ordinalTypes = new int[]{
      4,
      0,
      0,
      0,
      5,
      2,
      -805044219,
      1717920814,
      10,
      -804651007,
      51,
      -805043232,
      1886938459,
      1937011311,
      1829375325,
      225339745,
      1527385354,
      1953460082,
      1935764579,
      1635022451,
      1566927970,
      1634208269,
      1866688627,
      220226916
   };
   static final int MIDLET_REQUIRED_VERSION;
   static final int JAD_REQUIRED_JARSIZE;
   static final int AMS_COD_URL;
   static final int AMS_COD_SIZE;
   static final int AMS_COD_TIME;
   static final int AMS_COD_NAME;
   static final int AMS_COD_DEPS;
   static final int AMS_COD_SHA1;
   static final String MIDletPermissionsTag;
   static final String MIDletOptionalPermissionsTag;
   static final String MIDletSignatureTag;
   static final String MIDletCertPrefix;
   static final String MIDletProfileSecurity;

   public static String getId(String tag) {
      return (String)_resourceIds.get(tag);
   }

   static String getMIDletCertificateTag(int n, int m) {
      StringBuffer buff = (StringBuffer)(new Object(48));
      buff.append("MIDlet-Certificate-");
      buff.append(n);
      buff.append('-');
      buff.append(m);
      return buff.toString();
   }

   static {
      int num = required.length + manifestVersion.length + manifestRequired.length + jadRequired.length + optional.length + required.length;
      _resourceIds = (Hashtable)(new Object(num * 2));
      num = required.length;

      for (int i = 0; i < num; i++) {
         _resourceIds.put(required[i], requiredIds[i]);
      }

      num = manifestVersion.length;

      for (int var2 = 0; var2 < num; var2++) {
         _resourceIds.put(manifestVersion[var2], manifestVersionIds[var2]);
      }

      num = manifestRequired.length;

      for (int var3 = 0; var3 < num; var3++) {
         _resourceIds.put(manifestRequired[var3], manifestRequiredIds[var3]);
      }

      num = jadRequired.length;

      for (int var4 = 0; var4 < num; var4++) {
         _resourceIds.put(jadRequired[var4], jadRequiredIds[var4]);
      }

      num = optional.length;

      for (int var5 = 0; var5 < num; var5++) {
         _resourceIds.put(optional[var5], optionalIds[var5]);
      }
   }
}
