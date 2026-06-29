package net.rim.device.api.ui.theme;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;

class OSVersion {
   private String _name;
   private int _major;
   private int _minor;
   private Integer _subMinor;
   private static Hashtable _valuesByName = (Hashtable)(new Object());
   public static final OSVersion OS_VERSION_4_1 = new OSVersion("4.1", 4, 1);
   public static final OSVersion OS_VERSION_4_2 = new OSVersion("4.2", 4, 2);
   public static final OSVersion OS_VERSION_4_2_1 = new OSVersion("4.2.1", 4, 2, (Integer)(new Object(1)));
   public static final OSVersion OS_VERSION_4_2_2 = new OSVersion("4.2.2", 4, 2, (Integer)(new Object(2)));
   public static final OSVersion OS_VERSION_4_2_3 = new OSVersion("4.2.3", 4, 2, (Integer)(new Object(3)));
   public static final OSVersion OS_VERSION_4_3 = new OSVersion("4.3", 4, 3);
   public static final OSVersion OS_VERSION_4_3_1 = new OSVersion("4.3.1", 4, 3, (Integer)(new Object(1)));
   public static final OSVersion OS_VERSION_4_3_2 = new OSVersion("4.3.2", 4, 3, (Integer)(new Object(2)));
   public static final OSVersion OS_VERSION_4_3_3 = new OSVersion("4.3.3", 4, 3, (Integer)(new Object(3)));
   public static final OSVersion OS_VERSION_5_0 = new OSVersion("5.0", 5, 0);
   private static final OSVersion DEFAULT_OS_VERSION = OS_VERSION_4_3;

   private OSVersion(String name, int major, int minor) {
      this(name, major, minor, null);
   }

   private OSVersion(String name, int major, int minor, Integer subMinor) {
      this._name = name;
      this._major = major;
      this._minor = minor;
      this._subMinor = subMinor;
      _valuesByName.put(name, this);
   }

   public static OSVersion forName(String name) {
      return (OSVersion)_valuesByName.get(name);
   }

   public static OSVersion getCurrentOs() {
      OSVersion result = null;
      ApplicationDescriptor appDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
      int moduleHandle = appDescriptor.getModuleHandle();
      String moduleVersion = CodeModuleManager.getModuleVersion(moduleHandle);
      if (moduleVersion != null) {
         result = getOs(moduleVersion);
      }

      if (result == null) {
         String appVersion = appDescriptor.getVersion();
         result = getOs(appVersion);
      }

      if (result == null) {
         result = DEFAULT_OS_VERSION;
      }

      return result;
   }

   private static OSVersion getOs(String versionString) {
      OSVersion result = null;
      result = forName(versionString);
      if (result == null) {
         VersionTokenizer tokenizer = new VersionTokenizer(versionString);
         Integer major = tokenizer.nextToken();
         if (major != null) {
            Integer minor = tokenizer.nextToken();
            if (minor != null) {
               Integer subMinor = tokenizer.nextToken();
               StringBuffer builtUp = (StringBuffer)(new Object());
               builtUp.append(major.toString()).append('.').append(minor.toString());
               if (subMinor != null) {
                  builtUp.append('.').append(subMinor.toString());
               }

               result = forName(builtUp.toString());
            }
         }
      }

      return result;
   }

   @Override
   public String toString() {
      return this._name;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof OSVersion)) {
         return false;
      }

      OSVersion osVer = (OSVersion)o;
      return this._major == osVer._major
         && this._minor == osVer._minor
         && (this._subMinor == osVer._subMinor || this._subMinor != null && this._subMinor.equals(osVer._subMinor));
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 37 * result + this._major;
      result = 37 * result + this._minor;
      if (this._subMinor != null) {
         result = 37 * result + this._subMinor;
      }

      return result;
   }

   public int compareTo(OSVersion osVer) {
      if (this._major < osVer._major) {
         return -1;
      } else if (this._major > osVer._major) {
         return 1;
      } else if (this._minor < osVer._minor) {
         return -1;
      } else if (this._minor > osVer._minor) {
         return 1;
      } else {
         int selfSubMinor = this._subMinor == null ? 0 : this._subMinor;
         int osVerSubMinor = osVer._subMinor == null ? 0 : osVer._subMinor;
         if (selfSubMinor < osVerSubMinor) {
            return -1;
         } else {
            return selfSubMinor > osVerSubMinor ? 1 : 0;
         }
      }
   }
}
