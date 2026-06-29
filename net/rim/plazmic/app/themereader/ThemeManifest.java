package net.rim.plazmic.app.themereader;

import java.io.InputStream;
import net.rim.device.api.io.LineReader;

class ThemeManifest {
   private String _manifestVersion;
   private String _createdBy;
   private String _osVersion;
   private String _description;
   private String _hash;
   public static final String STANDARD_FILENAME;
   private static final String MANIFEST_VERSION_FIELDNAME;
   private static final String CREATED_BY_FIELDNAME;
   private static final String OS_VERSION_FIELDNAME;
   private static final String DESCRIPTION_FIELDNAME_10;
   private static final String DESCRIPTION_FIELDNAME_11;
   private static final String HASH_FIELDNAME;

   public ThemeManifest(InputStream is) {
      String description11 = null;
      LineReader lr = (LineReader)(new Object(is));
      String line = null;

      label72:
      try {
         line = (String)(new Object(lr.readLine()));
      } finally {
         break label72;
      }

      while (line != null) {
         if (line.startsWith("Manifest-Version")) {
            this._manifestVersion = getFieldValue(line);
         } else if (line.startsWith("Created-By")) {
            this._createdBy = getFieldValue(line);
         } else if (line.startsWith("OS-Version")) {
            this._osVersion = getFieldValue(line);
         } else if (line.startsWith("Theme-Description")) {
            this._description = getFieldValue(line);
         } else if (line.startsWith("Theme-XML")) {
            description11 = getFieldValue(line);
         } else if (line.startsWith("Theme-Hash")) {
            this._hash = getFieldValue(line);
         }

         if (lr.lengthUnreadData() <= 0) {
            break;
         }

         line = (String)(new Object(lr.readLine()));
      }

      if (this._description == null) {
         this._description = description11;
      }

      this.assignDefaults();
   }

   private void assignDefaults() {
      if (this._manifestVersion == null) {
         this._manifestVersion = "1.0";
      }

      if (this._createdBy == null) {
         this._createdBy = "1.1.0.14";
      }

      if (this._osVersion == null) {
         this._osVersion = "4.1";
      }
   }

   public String getManifestVersion() {
      return this._manifestVersion;
   }

   public String getCreatedBy() {
      return this._createdBy;
   }

   public String getOsVersion() {
      return this._osVersion;
   }

   public String getDescription() {
      return this._description;
   }

   public String getHash() {
      return this._hash;
   }

   private static String getFieldValue(String line) {
      String result = null;
      int colon = line.indexOf(58);
      if (colon != -1) {
         result = line.substring(colon + 1).trim();
         if (result.length() == 0) {
            result = null;
         }
      }

      return result;
   }
}
