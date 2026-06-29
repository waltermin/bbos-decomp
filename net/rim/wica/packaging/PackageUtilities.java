package net.rim.wica.packaging;

public final class PackageUtilities {
   public static final String constructResourcePath(String filename, char pathSeparator, String language) {
      if (filename == null) {
         throw new NullPointerException("filename");
      }

      StringBuffer sb = new StringBuffer("resources");
      sb.append(pathSeparator);
      if (language != null) {
         sb.append(language);
         sb.append(pathSeparator);
      }

      sb.append(filename);
      return sb.toString();
   }

   public static final String extractPathFromURI(String uri) {
      if (uri == null) {
         throw new NullPointerException("uri");
      }

      String path = null;
      int schemeIndex = uri.indexOf(58);
      String scheme = uri.substring(0, schemeIndex);
      if (scheme.equals("jar")) {
         String relativePath = uri.substring(schemeIndex + 3);
         if (relativePath != null && relativePath.length() > 0) {
            path = relativePath;
         }
      }

      return path;
   }

   public static final String constructWicletPackageURL(String baseUrl, String encodedPackageName) {
      StringBuffer sb = new StringBuffer(baseUrl);
      sb.append('/');
      sb.append("provisioning");
      sb.append('/');
      sb.append(encodedPackageName);
      return sb.toString();
   }

   public static final String getPackageName(String uri, String version, String locale) {
      String path = uri;
      path = stringReplace(path, ":", '_');
      path = stringReplace(path, "\\", '/');
      path = stringReplace(path, "//", '/');
      path = stringReplace(path, "///", '/');
      StringBuffer sb = new StringBuffer(path);
      sb.append('-');
      sb.append(version);
      sb.append('-');
      sb.append(locale);
      return sb.toString();
   }

   public static final String getFullPackageName(String uri, String version, String locale) {
      String name = getPackageName(uri, version, locale);
      return name + "." + "jar";
   }

   private static final String stringReplace(String text, String pattern, char replacement) {
      int pos = text.indexOf(pattern);
      if (pos < 0) {
         return text;
      }

      StringBuffer sb = new StringBuffer();

      while (pos != -1) {
         sb.append(text.substring(0, pos)).append(replacement);
         text = text.substring(pos + pattern.length());
         pos = text.indexOf(pattern);
      }

      sb.append(text);
      return sb.toString();
   }
}
