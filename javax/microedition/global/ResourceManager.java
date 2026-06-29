package javax.microedition.global;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.resources.Resource;
import net.rim.vm.Array;

public class ResourceManager {
   private String _baseName;
   private String _locale;
   private ResourceFile[] _resourceFile;
   public static final String DEVICE = "";
   private static final String ROOT_DIRECTORY = "global/";
   private static String EMPTY = "";

   private String getResourceFilename(String baseName, String locale) {
      return locale.length() == 0 ? "global/" + baseName + ".res" : "global/" + locale + "/" + baseName + ".res";
   }

   ResourceManager(String baseName, String locale) {
      this._baseName = baseName;
      this._locale = locale;
      if (baseName == "") {
         throw new ResourceException(3, EMPTY);
      }

      this._resourceFile = new ResourceFile[8];
      this._resourceFile[0] = ResourceFile.getResourceFile(this.getResourceFilename(baseName, locale));
      String nextLocale = locale;
      int supportedLocales = 1;

      do {
         nextLocale = getParentLocale(nextLocale);
         if (isSupportedLocale(nextLocale, baseName)) {
            try {
               this._resourceFile[supportedLocales] = ResourceFile.getResourceFile(this.getResourceFilename(baseName, nextLocale));
               supportedLocales++;
            } catch (ResourceException var6) {
            }
         }
      } while (nextLocale.length() > 0);

      Array.resize(this._resourceFile, supportedLocales);
   }

   public static final ResourceManager getManager(String baseName) {
      String defaultLocale = System.getProperty("microedition.locale");
      if (defaultLocale == null) {
         throw new ResourceException(4, EMPTY);
      } else {
         return getManager(baseName, defaultLocale);
      }
   }

   public static final ResourceManager getManager(String baseName, String locale) {
      if (baseName != null && locale != null) {
         String nextLocale = GlobalUtilities.convertUnderscoreToHyphens(locale);
         if (!GlobalUtilities.isValidLocale(nextLocale)) {
            throw new IllegalArgumentException();
         }

         while (!isSupportedLocale(nextLocale, baseName)) {
            if (nextLocale.equals(EMPTY)) {
               throw new UnsupportedLocaleException();
            }

            nextLocale = getParentLocale(nextLocale);
         }

         return new ResourceManager(baseName, nextLocale);
      } else {
         throw new NullPointerException();
      }
   }

   public static final ResourceManager getManager(String baseName, String[] locales) {
      if (baseName != null && locales != null) {
         int numLocales = locales.length;
         if (numLocales == 0) {
            throw new IllegalArgumentException();
         }

         String[] updatedLocales = new String[numLocales];

         for (int i = 0; i < numLocales; i++) {
            updatedLocales[i] = GlobalUtilities.convertUnderscoreToHyphens(locales[i]);
            if (!GlobalUtilities.isValidLocale(updatedLocales[i])) {
               throw new IllegalArgumentException();
            }
         }

         for (int i = 0; i < numLocales; i++) {
            if (isSupportedLocale(updatedLocales[i], baseName)) {
               return new ResourceManager(baseName, updatedLocales[i]);
            }
         }

         throw new UnsupportedLocaleException();
      } else {
         throw new NullPointerException();
      }
   }

   private static String getParentLocale(String locale) {
      int lastHyphen = locale.lastIndexOf(45);
      return lastHyphen < 0 ? EMPTY : locale.substring(0, lastHyphen);
   }

   private int firstIndexContainingId(int id) {
      if (id >= 0 && id <= Integer.MAX_VALUE) {
         for (int i = 0; i < this._resourceFile.length; i++) {
            if (this._resourceFile[i].isValidId(id)) {
               return i;
            }
         }

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public String getBaseName() {
      return this._baseName;
   }

   public String getLocale() {
      return this._locale;
   }

   public boolean isValidResourceID(int id) {
      return this.firstIndexContainingId(id) != -1;
   }

   public byte[] getData(int id) {
      int index = this.firstIndexContainingId(id);
      if (index < 0) {
         throw new ResourceException(1, EMPTY);
      } else if (this._resourceFile[index].getType(id) != 16) {
         throw new ResourceException(2, EMPTY);
      } else {
         return (byte[])this._resourceFile[index].getData(id);
      }
   }

   public Object getResource(int id) {
      int index = this.firstIndexContainingId(id);
      if (index < 0) {
         throw new ResourceException(1, EMPTY);
      } else {
         return this._resourceFile[index].getData(id);
      }
   }

   public String getString(int id) {
      int index = this.firstIndexContainingId(id);
      if (index < 0) {
         throw new ResourceException(1, EMPTY);
      } else if (this._resourceFile[index].getType(id) != 1) {
         throw new ResourceException(2, EMPTY);
      } else {
         return (String)this._resourceFile[index].getData(id);
      }
   }

   public static String[] getSupportedLocales(String baseName) {
      if (baseName == null) {
         throw new NullPointerException();
      }

      if (baseName.equals("")) {
         return new String[0];
      }

      byte[] metafiledata = Resource.getResourceClass().getResource("global/_" + baseName);
      if (metafiledata == null) {
         throw new ResourceException(7, EMPTY);
      }

      StringTokenizer st = new StringTokenizer(new String(metafiledata));
      String[] supportedLocales = new String[st.countTokens()];

      for (int i = 0; st.hasMoreTokens(); i++) {
         supportedLocales[i] = removeQuotes(st.nextToken());
      }

      return supportedLocales;
   }

   private static String removeQuotes(String locale) {
      if (locale.charAt(0) != '"' || locale.charAt(locale.length() - 1) != '"') {
         return locale;
      } else {
         return locale.length() > 2 ? locale.substring(1, locale.length() - 1) : EMPTY;
      }
   }

   private static boolean isSupportedLocale(String locale, String baseName) {
      return Arrays.contains(getSupportedLocales(baseName), locale);
   }

   public boolean isCaching() {
      return true;
   }
}
