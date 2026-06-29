package net.rim.device.apps.api.utility.general;

import net.rim.device.api.util.NumberUtilities;
import net.rim.device.cldc.io.utility.URIEncoder;

public class URI {
   private String _scheme;
   private String _authority;
   private String _path;
   private String _query;
   private String _fragment;
   private String _absoluteURL;
   private String _schemeSpecificPart;
   private boolean _absolute;
   private boolean _opaque;
   private static String SLASH_DOT_DOT_SLASH = "/../";
   private static String SLASH_DOT_SLASH = "/./";

   public URI(String uri) {
      this(uri, (URI)null);
   }

   public URI(String relativeURI, String baseURI) {
      this(relativeURI, new URI(baseURI), true);
   }

   public URI(String relativeURI, String baseURI, boolean allowRelativeRef) {
      this(relativeURI, new URI(baseURI), allowRelativeRef);
   }

   public URI(String relativeURI, URI baseURI) {
      this.parse(relativeURI, baseURI, true);
   }

   public URI(String relativeURI, URI baseURI, boolean allowRelativeRef) {
      this.parse(relativeURI, baseURI, allowRelativeRef);
   }

   public URI(String scheme, String authority, String path, String query, String fragment) {
      this._scheme = scheme;
      this._authority = authority;
      this._path = path;
      this._query = query;
      this._fragment = fragment;
      this._absolute = true;
      this._opaque = false;
   }

   public URI(String scheme, String schemeSpecificPart, String fragment) {
      this._scheme = scheme;
      this._schemeSpecificPart = schemeSpecificPart;
      this._fragment = fragment;
      this._opaque = true;
      this._absolute = true;
   }

   public String getAuthority() {
      return this._authority;
   }

   public String getFragment() {
      return this._fragment;
   }

   public String getPath() {
      return this._path;
   }

   public String getQuery() {
      return this._query;
   }

   public String getScheme() {
      return this._scheme;
   }

   public String getSchemeSpecificPart() {
      return this._schemeSpecificPart;
   }

   public boolean isAbsolute() {
      return this._absolute;
   }

   public boolean saveInCache() {
      return this._absolute;
   }

   public boolean isOpaque() {
      return this._opaque;
   }

   public String getFileName() {
      if (this._path != null && this._path.length() != 0) {
         int nameStart = this._path.lastIndexOf(47);
         return this._path.substring(nameStart + 1);
      } else {
         return null;
      }
   }

   public String getAbsoluteURL() {
      if (this._absoluteURL == null) {
         StringBuffer buffer = new StringBuffer();
         if (this._scheme != null && this._scheme.length() != 0) {
            buffer.append(this._scheme);
            buffer.append(':');
         }

         if (this._opaque) {
            buffer.append(this._schemeSpecificPart);
         } else {
            buffer.append("//");
            if (this._authority != null && this._authority.length() != 0) {
               buffer.append(this._authority);
            }

            if (this._path != null && this._path.length() != 0) {
               buffer.append(this._path);
            }

            if (this._query != null && this._query.length() != 0) {
               buffer.append('?');
               buffer.append(this._query);
            }
         }

         if (this._fragment != null) {
            buffer.append('#');
            buffer.append(this._fragment);
         }

         this._absoluteURL = buffer.toString();
      }

      return this._absoluteURL;
   }

   private void parse(String url, URI base, boolean allowRelativeRef) {
      url = url.trim();
      int strLength = url.length();

      int pos;
      label719:
      for (pos = 0; pos < strLength; pos++) {
         switch (url.charAt(pos)) {
            case '#':
            case '/':
            case ':':
            case '?':
               break label719;
         }
      }

      int schemeEndIndex = 0;
      if (pos < strLength && url.charAt(pos) == ':') {
         this._absolute = true;
         schemeEndIndex = pos++;
      } else {
         this._absolute = false;
         pos = 0;
      }

      int authorityStartIndex = pos;
      int authorityEndIndex = pos;
      boolean hasSlash = pos < strLength && url.charAt(pos) == '/';
      if (pos < strLength - 1 && url.charAt(pos) == '/' && url.charAt(pos + 1) == '/') {
         pos += 2;
         authorityStartIndex = pos;

         label697:
         try {
            label696:
            while (true) {
               switch (url.charAt(pos)) {
                  case '#':
                  case '/':
                  case '?':
                     break label696;
                  default:
                     pos++;
               }
            }
         } finally {
            break label697;
         }

         authorityEndIndex = pos;
      }

      int schemeSpecificStartIndex = pos;
      int schemeSpecificEndIndex = pos;
      if (pos < strLength && url.charAt(pos) == '/') {
         pos++;
      } else if (this._absolute && !hasSlash) {
         this._opaque = true;

         while (pos < strLength && url.charAt(pos) != '#') {
            pos++;
         }

         schemeSpecificEndIndex = pos;
      }

      label675:
      while (pos < strLength) {
         switch (url.charAt(pos)) {
            case '#':
            case '?':
               break label675;
            default:
               pos++;
         }
      }

      int pathEndIndex = pos;
      int queryStartIndex = pathEndIndex;
      int queryEndIndex = pathEndIndex;
      if (pos < strLength && url.charAt(pos) == '?') {
         queryStartIndex = ++pos;

         while (pos < strLength && url.charAt(pos) != '#') {
            pos++;
         }

         queryEndIndex = pos;
      }

      boolean fragmentPresent = false;
      int fragmentStartIndex = pos;
      int fragmentEndIndex = pos;
      if (pos < strLength && url.charAt(pos) == '#') {
         pos++;
         fragmentPresent = true;
         this._fragment = "";
         fragmentStartIndex = pos;
         fragmentEndIndex = strLength;
      }

      if (allowRelativeRef && base != null) {
         this._scheme = base.getScheme();
         this._authority = base.getAuthority();
         this._path = base.getPath();
         this._query = base.getQuery();
         this._schemeSpecificPart = base.getSchemeSpecificPart();
      } else {
         String nullStr = "";
         this._scheme = nullStr;
         this._authority = nullStr;
         this._path = nullStr;
         this._query = nullStr;
         this._fragment = null;
         this._schemeSpecificPart = nullStr;
      }

      if (schemeEndIndex > 0) {
         this._scheme = url.substring(0, schemeEndIndex);
         this._authority = url.substring(authorityStartIndex, authorityEndIndex);
         this._path = url.substring(authorityEndIndex, pathEndIndex);
         this._query = url.substring(queryStartIndex, queryEndIndex);
         if (fragmentStartIndex < fragmentEndIndex) {
            this._fragment = url.substring(fragmentStartIndex, fragmentEndIndex);
            fragmentPresent = true;
         }

         this._schemeSpecificPart = url.substring(schemeSpecificStartIndex, schemeSpecificEndIndex);
      } else if (authorityEndIndex > schemeEndIndex) {
         this._authority = url.substring(authorityStartIndex, authorityEndIndex);
         this._path = url.substring(authorityEndIndex, pathEndIndex);
         this._query = url.substring(queryStartIndex, queryEndIndex);
         if (fragmentStartIndex < fragmentEndIndex) {
            this._fragment = url.substring(fragmentStartIndex, fragmentEndIndex);
            fragmentPresent = true;
         }
      } else if (pathEndIndex <= authorityEndIndex) {
         if (queryEndIndex > pathEndIndex) {
            this._query = url.substring(queryStartIndex, queryEndIndex);
            if (fragmentStartIndex < fragmentEndIndex) {
               this._fragment = url.substring(fragmentStartIndex, fragmentEndIndex);
               fragmentPresent = true;
            }
         } else if (fragmentEndIndex > queryEndIndex && fragmentStartIndex < fragmentEndIndex) {
            this._fragment = url.substring(fragmentStartIndex, fragmentEndIndex);
            fragmentPresent = true;
         }
      } else {
         if (url.charAt(authorityEndIndex) == '/') {
            this._path = url.substring(authorityEndIndex, pathEndIndex);
         } else {
            int fileStartIndex = this._path.lastIndexOf(47);
            if (fileStartIndex == -1) {
               this._path = "/" + url.substring(authorityEndIndex, pathEndIndex);
            } else {
               this._path = this._path.substring(0, fileStartIndex + 1) + url.substring(authorityEndIndex, pathEndIndex);
            }
         }

         this._query = url.substring(queryStartIndex, queryEndIndex);
         if (fragmentStartIndex < fragmentEndIndex) {
            this._fragment = url.substring(fragmentStartIndex, fragmentEndIndex);
            fragmentPresent = true;
         }
      }

      boolean impliedScheme = false;
      if (this._scheme == null || this._scheme.length() == 0) {
         this._scheme = "http";
         this._opaque = false;
         impliedScheme = true;
      }

      if (!this._opaque) {
         if (this._path == null || this._path.length() == 0 && this._authority != null && !this._authority.equals("//")) {
            this._path = "/";
         }

         if (impliedScheme && !hasSlash && !this._path.equals("/") && (this._authority == null || this._authority.length() == 0)) {
            this._path = this._path.substring(1);
            authorityEndIndex = this._path.indexOf(47);
            this._authority = authorityEndIndex == -1 ? this._path : this._path.substring(0, authorityEndIndex);
            this._path = authorityEndIndex == -1 ? "/" : this._path.substring(authorityEndIndex);
         }

         this._query = URIEncoder.encodeBlanks(this._query);
         int index = 0;

         label622:
         while (true) {
            index = this._path.indexOf("%2e", index);
            if (index == -1) {
               index = 0;

               while (true) {
                  index = this._path.indexOf("%2E", index);
                  if (index == -1) {
                     for (int var36 = this._path.indexOf(SLASH_DOT_SLASH); var36 != -1; var36 = this._path.indexOf(SLASH_DOT_SLASH)) {
                        StringBuffer str = new StringBuffer(this._path.substring(0, var36 + 1));
                        str.append(this._path.substring(var36 + 3));
                        this._path = str.toString();
                     }

                     int length = this._path.length();
                     if (length >= 2 && this._path.charAt(length - 2) == '/' && this._path.charAt(length - 1) == '.') {
                        this._path = this._path.substring(0, length - 1);
                     }

                     for (int var37 = this._path.indexOf(SLASH_DOT_DOT_SLASH); var37 != -1; var37 = this._path.indexOf(SLASH_DOT_DOT_SLASH)) {
                        int start = this._path.lastIndexOf(47, var37 - 1);
                        if (start <= -1 || this._path.substring(start + 1, var37).equals("..")) {
                           break;
                        }

                        StringBuffer str = new StringBuffer();
                        str.append(this._path.substring(0, start + 1));
                        str.append(this._path.substring(var37 + 4));
                        this._path = str.toString();
                     }

                     length = this._path.length();
                     if (length >= 3 && this._path.charAt(length - 3) == '/' && this._path.charAt(length - 2) == '.' && this._path.charAt(length - 1) == '.') {
                        int end = length - 3;
                        int start = this._path.lastIndexOf(47, end - 1);
                        if (start > -1 && !this._path.substring(start + 1, end).equals("..")) {
                           this._path = this._path.substring(0, start);
                        }
                     }

                     while (this._path.length() >= 4 && this._path.startsWith(SLASH_DOT_DOT_SLASH)) {
                        this._path = this._path.substring(3, this._path.length());
                     }

                     if (this._path.length() == 3 && this._path.equals("/..")) {
                        this._path = "/";
                     }
                     break label622;
                  }

                  this._path = this._path.substring(0, index) + '.' + this._path.substring(index + 3);
               }
            }

            this._path = this._path.substring(0, index) + '.' + this._path.substring(index + 3);
         }
      }

      if (this._authority != null) {
         int colonIndex = this._authority.indexOf(58);
         if (colonIndex != -1) {
            label585:
            try {
               int port = NumberUtilities.parseInt(this._authority, colonIndex + 1, this._authority.length(), 10);
               if ("http".equals(this._scheme) && port == 80 || "https".equals(this._scheme) && port == 443) {
                  this._authority = this._authority.substring(0, colonIndex);
               }
            } finally {
               break label585;
            }
         }
      }

      this._path = URIEncoder.encodeBlanks(this._path);
      if (this._fragment != null) {
         if (fragmentPresent) {
            this._fragment = URIEncoder.encodeBlanks(this._fragment);
         } else {
            this._fragment = null;
         }
      }

      this._schemeSpecificPart = URIEncoder.encodeBlanks(this._schemeSpecificPart);
   }

   public static String getAbsoluteURL(String relative, String base) {
      if (!relative.startsWith("http://") && !relative.startsWith("https://") && !relative.startsWith("file://")) {
         try {
            return base == null ? new URI(relative).getAbsoluteURL() : new URI(relative, base).getAbsoluteURL();
         } catch (MalformedURIException mue) {
            return relative;
         }
      } else {
         return relative;
      }
   }
}
