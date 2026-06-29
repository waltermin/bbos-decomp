package net.rim.device.apps.internal.browser.cod;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.text.IPTextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.content.ContentHandlerRegistrationHelper;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.vm.Array;

final class JADAttributeParser extends Hashtable implements Persistable {
   private int _codUrlCount;
   private int _totalSize;
   private String _jarURL;
   private String _jadURL;
   private String[] _codURLs;
   private int[] _codSizes;
   private String _jadString;
   private int _certChainStatus = 2;
   private String _errorMessage;
   private boolean _isValid;
   static final String MIDLET_NAME;
   static final String MIDLET_VERSION;
   static final String MIDLET_VENDOR;
   static final String MIDLET_DESCRIPTION;
   static final String MIDLET_JAR_URL;
   static final String MIDLET_JAR_SIZE;
   static final String MIDLET_INSTALL_NOTIFY;
   static final String MIDLET_DELETE_NOTIFY;
   static final String MIDLET_JAR_RSA_SHA1;
   static final String MICROEDITION_PROFILE;
   static final String MICROEDITION_CONFIGURATION;
   static final String RIM_DOWNLOAD_LABEL;
   static final String RIM_MIDLET_SIGNER;
   static final String RIM_JAD_URL;
   static final String RIM_COD_URL;
   private static final int SizeOfRIM_COD_URL = 11;
   private static final String RIM_COD_SIZE;
   private static final String RIM_TRANSCODED_JAR;
   private static final int MIDLET_NAME_MASK;
   private static final int MIDLET_VERSION_MASK;
   private static final int MIDLET_VENDOR_MASK;
   private static final int MIDLET_JAR_URL_MASK;
   private static final int MIDLET_JAR_SIZE_MASK;
   private static final int RIM_COD_MASK;
   private static final int RIM_COD_SIZE_MASK;
   private static final int REQUIRED_FIELDS_MASK;
   private static final int VALUE_TYPE_STRING;
   private static final int VALUE_TYPE_REQUIRED_STRING;
   private static final int VALUE_TYPE_URL;
   private static final int VALUE_TYPE_NONNEGATIVE_INTEGER;
   private static final int VALUE_TYPE_VERSION;
   private static final int VERSION_STATE_MAJOR;
   private static final int VERSION_STATE_MINOR;
   private static final int VERSION_STATE_MICRO;

   JADAttributeParser() {
      this(null);
   }

   JADAttributeParser(String url) {
      this._jadURL = url;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final boolean parse(String jadString, String baseURL) {
      boolean valid = true;
      boolean emptyJarURL = false;
      byte required = 0;
      if (this._jarURL != null) {
         required = (byte)(required | 8);
      }

      if (this._totalSize != 0) {
         required = (byte)(required | 16);
      }

      this._jadString = jadString;
      int length = jadString.length();
      int i = 0;

      while (i < length) {
         int valueType = 0;
         boolean max256CharsButAllowEmpty = false;
         i = jadString.indexOf(58);
         if (i == -1) {
            break;
         }

         String name = jadString.substring(0, i);
         String originalName = name.trim();
         name = originalName;
         if (name == null || name.length() == 0 || !this.checkValidity(name, true, true)) {
            valid = false;
            this._errorMessage = MessageFormat.format(BrowserResources.getString(804), new Object[]{name});
            name = null;
         } else if (name.equals("MIDlet-Name")) {
            valueType = 1;
            required = (byte)(required | 1);
         } else if (name.equals("MIDlet-Version")) {
            valueType = 4;
            required = (byte)(required | 2);
         } else if (name.equals("MIDlet-Vendor")) {
            valueType = 1;
            required = (byte)(required | 4);
         } else if (name.equals("MIDlet-Jar-URL")) {
            valueType = 2;
            required = (byte)(required | 8);
         } else if (name.equals("MIDlet-Jar-Size")) {
            valueType = 3;
            required = (byte)(required | 16);
         } else if (name.equals("MIDlet-Install-Notify")) {
            valueType = 2;
            max256CharsButAllowEmpty = true;
         } else if (name.equals("MIDlet-Delete-Notify")) {
            valueType = 2;
            max256CharsButAllowEmpty = true;
         } else if (name.startsWith("RIM-COD-URL")) {
            valueType = 2;
            name = ((StringBuffer)(new Object("RIM-COD-URL"))).append(originalName.substring(SizeOfRIM_COD_URL, name.length())).toString();
            required = (byte)(required | 8);
            this._codUrlCount++;
         } else if (name.startsWith("RIM-COD-Size")) {
            valueType = 3;
            name = ((StringBuffer)(new Object("RIM-COD-Size"))).append(originalName.substring(12, name.length())).toString();
            required = (byte)(required | 16);
         }

         jadString = jadString.substring(i + 1);
         i = jadString.indexOf(10);
         String value;
         if (i == -1) {
            value = jadString;
            jadString = "";
         } else {
            StringBuffer assembleValue = (StringBuffer)(new Object());

            label391:
            try {
               boolean lineContinuation = false;

               do {
                  value = jadString.substring(lineContinuation ? 1 : 0, i);
                  assembleValue.append(value.trim());
                  jadString = jadString.substring(i + 1);
                  i = jadString.indexOf(10);
                  lineContinuation = jadString.charAt(0) == ' ' && jadString.charAt(1) != ' ';
               } while (lineContinuation);
            } finally {
               break label391;
            }

            value = assembleValue.toString();
         }

         if (name != null) {
            value = value.trim();
            if (!this.checkValidity(value, true, false)) {
               valid = false;
               this._errorMessage = MessageFormat.format(BrowserResources.getString(805), new Object[]{name});
            } else {
               switch (valueType) {
                  case 0:
                     break;
                  case 1:
                  default:
                     if (value.length() == 0) {
                        valid = false;
                        value = null;
                        this._errorMessage = MessageFormat.format(BrowserResources.getString(803), new Object[]{name});
                     }
                     break;
                  case 2:
                     if (value.length() == 0) {
                        if (!max256CharsButAllowEmpty) {
                           if (name.equals("MIDlet-Jar-URL")) {
                              emptyJarURL = true;
                           } else {
                              valid = false;
                              this._errorMessage = MessageFormat.format(BrowserResources.getString(803), new Object[]{name});
                           }
                        }

                        value = null;
                     } else {
                        if (max256CharsButAllowEmpty && value.length() > 256) {
                           valid = false;
                           this._errorMessage = MessageFormat.format(BrowserResources.getString(770), new Object[]{name, Integer.toString(256)});
                        }

                        value = URI.getAbsoluteURL(value, baseURL);
                     }
                     break;
                  case 3:
                     boolean var17 = false /* VF: Semaphore variable */;

                     label372:
                     try {
                        var17 = true;
                        int var30 = Integer.parseInt(value);
                        if (var30 < 0) {
                           valid = false;
                           value = null;
                           var17 = false;
                        } else {
                           var17 = false;
                        }
                     } finally {
                        if (var17) {
                           valid = false;
                           value = null;
                           break label372;
                        }
                     }

                     if (value == null) {
                        this._errorMessage = MessageFormat.format(BrowserResources.getString(769), new Object[]{name});
                     }
                     break;
                  case 4:
                     if (!validVersion(value)) {
                        valid = false;
                        value = null;
                        this._errorMessage = MessageFormat.format(BrowserResources.getString(768), new Object[]{name});
                     }
               }

               if (value != null) {
                  this.put(name, value);
               }
            }
         }
      }

      if (required != 31) {
         valid = false;
         String missingAttribute = null;
         if ((required & 1) == 0) {
            missingAttribute = "MIDlet-Name";
         } else if ((required & 4) == 0) {
            missingAttribute = "MIDlet-Vendor";
         } else if ((required & 2) == 0) {
            missingAttribute = "MIDlet-Version";
         } else if ((required & 8) == 0) {
            missingAttribute = "MIDlet-Jar-URL";
         } else if ((required & 16) == 0) {
            missingAttribute = this._codUrlCount > 0 ? "RIM-COD-Size" : "MIDlet-Jar-Size";
         }

         if (missingAttribute != null) {
            this._errorMessage = MessageFormat.format(BrowserResources.getString(766), new Object[]{missingAttribute});
         }
      }

      if (valid && emptyJarURL && this._codUrlCount <= 0) {
         valid = false;
         this._errorMessage = MessageFormat.format(BrowserResources.getString(803), new Object[]{"MIDlet-Jar-URL"});
      }

      if (!valid) {
         return false;
      }

      i = 1;

      while (true) {
         String attribute = ((StringBuffer)(new Object("MIDlet-"))).append(i).toString();
         String midlet = (String)this.get(attribute);
         if (midlet == null) {
            this._isValid = this.extractURLs();
            return this._isValid;
         }

         if (midlet.length() == 0) {
            this._errorMessage = MessageFormat.format(BrowserResources.getString(803), new Object[]{attribute});
            return false;
         }

         i++;
      }
   }

   final boolean isValid() {
      return this._isValid;
   }

   final String getErrorMessage() {
      return this._errorMessage;
   }

   final int getCodUrlCount() {
      return this._codUrlCount;
   }

   final int totalSize() {
      return this._totalSize;
   }

   final void setJarSize(int jarSize) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final String getURLbyNumber(int n) {
      return this._codURLs[n];
   }

   final String getJarURL() {
      return this._jarURL;
   }

   final void setJarURL(String jarURL) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final String getJADString() {
      return this._jadString;
   }

   final String getJadURL() {
      return this._jadURL;
   }

   private static final boolean validVersion(String version) {
      if (version == null) {
         return false;
      }

      int length = version.length();
      int state = 0;
      int lastDot = -1;

      for (int i = 0; i < length; i++) {
         char ch = version.charAt(i);
         if (ch < '0' || ch > '9') {
            if (ch != '.') {
               return false;
            }

            if (i - lastDot == 1) {
               return false;
            }

            state++;
            lastDot = i;
         }
      }

      return (state >= 2 || state == 1) && lastDot != length - 1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean extractURLs() {
      this._totalSize = 0;
      if (this._codUrlCount <= 0) {
         String jarURL = (String)this.get("MIDlet-Jar-URL");
         if (jarURL != null) {
            this._jarURL = jarURL;
         }

         try {
            this._totalSize = Integer.parseInt((String)this.get("MIDlet-Jar-Size"));
            return true;
         } finally {
            return true;
         }
      } else {
         String minCodfileName = null;
         int min = 0;
         int ucount = 0;
         Enumeration keys = this.keys();
         this._codURLs = new Object[this._codUrlCount];
         this._codSizes = new int[this._codUrlCount];

         while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (isURLString(key)) {
               String codURL = (String)this.get(key);
               this._codURLs[ucount] = codURL;
               String codfileName = ApplicationDownloadManager.getNameFromJarURL(codURL);
               if (minCodfileName == null || minCodfileName.compareTo(codfileName) > 0) {
                  min = ucount;
                  minCodfileName = codfileName;
               }

               String sizeKey = ((StringBuffer)(new Object("RIM-COD-Size"))).append(key.substring(SizeOfRIM_COD_URL, key.length())).toString();
               String codSize = (String)this.get(sizeKey);
               if (codSize == null) {
                  this._errorMessage = MessageFormat.format(BrowserResources.getString(766), new Object[]{sizeKey});
                  return false;
               }

               boolean var13 = false /* VF: Semaphore variable */;

               label115:
               try {
                  var13 = true;
                  this._codSizes[ucount] = Integer.parseInt(codSize);
                  var13 = false;
               } finally {
                  if (var13) {
                     this._codSizes[ucount] = 0;
                     break label115;
                  }
               }

               this._totalSize = this._totalSize + this._codSizes[ucount];
               ucount++;
            }
         }

         if (min != 0) {
            String minURL = this._codURLs[min];
            int minSize = this._codSizes[min];
            this._codURLs[min] = this._codURLs[0];
            this._codSizes[min] = this._codSizes[0];
            this._codURLs[0] = minURL;
            this._codSizes[0] = minSize;
            return true;
         } else {
            return true;
         }
      }
   }

   private static final boolean isURLString(String key) {
      if (StringUtilities.startsWithIgnoreCase(key, "RIM-COD-URL", 1701707776)) {
         int length = key.length();
         if (length == SizeOfRIM_COD_URL) {
            return true;
         }

         if (key.charAt(SizeOfRIM_COD_URL) == '-') {
            int i = SizeOfRIM_COD_URL + 1;
            if (i < length) {
               if (!Character.isDigit(key.charAt(i))) {
                  return false;
               }

               return true;
            }
         }
      }

      return false;
   }

   private final boolean checkValidity(String stringToCheck, boolean controlCharacters, boolean separators) {
      int count = stringToCheck.length();

      for (int i = 0; i < count; i++) {
         char ch = stringToCheck.charAt(i);
         if (controlCharacters && (ch <= 31 || ch == 127)) {
            return false;
         }

         if (separators) {
            switch (ch) {
               case '\t':
               case ' ':
               case '"':
               case '\'':
               case '(':
               case ')':
               case ',':
               case '/':
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case '[':
               case ']':
               case '{':
               case '}':
                  return false;
            }
         }
      }

      return true;
   }

   final boolean isSigned() {
      return this.get("MIDlet-Jar-RSA-SHA1") != null;
   }

   final boolean isTranscodedJar() {
      return this.get("RIM-Transcoded-Jar") != null;
   }

   final int validate(boolean upgrade) {
      this.validateCertChains();
      if (this._certChainStatus != 0) {
         return 909;
      }

      String meProfile = (String)this.get("MicroEdition-Profile");
      if (meProfile != null) {
         int retVal = this.validateMicroEditionAttribute("microedition.profiles", meProfile);
         if (retVal != 900) {
            return retVal;
         }
      }

      String meConfig = (String)this.get("MicroEdition-Configuration");
      if (meConfig != null) {
         int retVal = this.validateMicroEditionAttribute("microedition.configuration", meConfig);
         if (retVal != 900) {
            return retVal;
         }
      }

      String[] keys = this.getEnumerableProperties(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX);
      if (keys != null && keys.length != 0) {
         PushRegistryHelper prh = PushRegistryHelper.getInstance();
         String[] midletkeys = this.getEnumerableProperties(PushRegistryHelper.MIDLET_PROPERTY_NAME_PREFIX);

         for (int i = keys.length - 1; i >= 0; i--) {
            String rawProperty = (String)this.get(keys[i]);
            String[] values = PushRegistryHelper.getPushPropertyValues(rawProperty);
            String s = values[0];
            if (!PushRegistryHelper.isConnectionSupported(s)) {
               return 911;
            }

            if (null != prh._connectionMap.get(s)) {
               return 911;
            }

            s = values[1];
            boolean midletclassnameconfirmed = false;

            for (int j = midletkeys.length - 1; j >= 0; j--) {
               String midletstring = (String)this.get(midletkeys[j]);
               String classname = midletstring.substring(midletstring.lastIndexOf(44) + 1);
               classname = classname.trim();
               if (classname.equals(s)) {
                  midletclassnameconfirmed = true;
               }
            }

            if (!midletclassnameconfirmed) {
               return 911;
            }

            s = values[2];
            IPTextFilter filter = new JADAttributeParser$1(this, 3);
            AbstractString as = AbstractStringWrapper.createInstance(s);
            if (!filter.validate(as)) {
               return 911;
            }
         }
      }

      int retval;
      return (retval = ContentHandlerRegistrationHelper.getInstance().verifyJadAttributes(this, upgrade)) != 900 ? retval : 900;
   }

   private final String[] getEnumerableProperties(String prefix) {
      StringBuffer sb = (StringBuffer)(new Object(prefix));
      sb.append('n');
      int charindex = sb.length() - 1;
      String[] keys = new Object[0];
      int i = 1;

      while (true) {
         sb.setCharAt(charindex, (char)(i + 48));
         String key = sb.toString();
         String value = (String)this.get(key);
         if (value == null) {
            return keys;
         }

         Array.resize(keys, keys.length + 1);
         keys[i - 1] = key;
         i++;
      }
   }

   private final int validateMicroEditionAttribute(String systemProperty, String attribute) {
      Hashtable jadHash = (Hashtable)(new Object());
      Hashtable systemHash = (Hashtable)(new Object());

      int blank;
      do {
         blank = attribute.indexOf(32);
         String currAtt;
         if (blank == -1) {
            currAtt = attribute;
         } else {
            currAtt = attribute.substring(0, blank);
            attribute = attribute.substring(blank + 1);
         }

         int i = currAtt.indexOf(45);
         if (i == -1) {
            return 907;
         }

         String jadProfile = currAtt.substring(0, i);
         String jadVersion = currAtt.substring(i + 1);
         String value = (String)jadHash.get(jadProfile);
         if (value == null) {
            jadHash.put(jadProfile, jadVersion);
         } else {
            try {
               if (Float.parseFloat(jadVersion) > Float.parseFloat(value)) {
                  jadHash.put(jadProfile, jadVersion);
               }
            } finally {
               ;
            }
         }
      } while (blank != -1);

      String systemProfiles = System.getProperty(systemProperty);
      if (systemProfiles == null) {
         return 908;
      }

      do {
         blank = systemProfiles.indexOf(32);
         String currAtt;
         if (blank == -1) {
            currAtt = systemProfiles;
         } else {
            currAtt = systemProfiles.substring(0, blank);
            systemProfiles = systemProfiles.substring(blank + 1);
         }

         int i = currAtt.indexOf(45);
         String systemProfile = currAtt.substring(0, i);
         String systemVersion = currAtt.substring(i + 1);
         String value = (String)systemHash.get(systemProfile);
         if (value == null) {
            systemHash.put(systemProfile, systemVersion);
         } else if (Float.parseFloat(systemVersion) > Float.parseFloat(value)) {
            jadHash.put(systemProfile, systemVersion);
         }
      } while (blank != -1);

      Enumeration jadProfiles = jadHash.keys();

      while (jadProfiles.hasMoreElements()) {
         String jadProfile = (String)jadProfiles.nextElement();
         String jadVersion = (String)jadHash.get(jadProfile);
         String systemVersion = (String)systemHash.get(jadProfile);
         if (systemVersion == null) {
            return 908;
         }

         if (Float.parseFloat(jadVersion) > Float.parseFloat(systemVersion)) {
            return 908;
         }
      }

      return 900;
   }

   private final void validateCertChains() {
      int n = 1;

      while (true) {
         Vector certs = (Vector)(new Object());
         int m = 1;

         while (true) {
            String tag = MIDletSecurity.getMIDletCertificateTag(n, m);
            String value = (String)this.get(tag);
            if (value == null) {
               m = certs.size();
               if (m == 0) {
                  if (n == 1) {
                     this._certChainStatus = 0;
                     return;
                  }

                  this._certChainStatus = 2;
                  return;
               }

               String[] strCerts = new Object[m];
               certs.copyInto(strCerts);
               value = MIDletSecurity.checkJADCertChain(strCerts);
               if (value == 0) {
                  this._certChainStatus = 0;
                  return;
               }

               n++;
               break;
            }

            certs.addElement(value);
            m++;
         }
      }
   }
}
