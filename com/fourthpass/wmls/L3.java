package com.fourthpass.wmls;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;

public final class L3 extends Lib {
   @Override
   public final Value invoke(int func, Interpreter$Engine engine) {
      switch (func) {
         case -1:
            throw new Object("Invalid Function Id");
         case 0:
         default:
            return isValid(engine.popStack());
         case 1:
            return getScheme(engine.popStack());
         case 2:
            return getHost(engine.popStack());
         case 3:
            return getPort(engine.popStack());
         case 4:
            return getPath(engine.popStack());
         case 5:
            return getParameters(engine.popStack());
         case 6:
            return getQuery(engine.popStack());
         case 7:
            return getFragment(engine.popStack());
         case 8:
            return getBase(engine.getBrowser());
         case 9:
            return getReferer(engine.getBrowser());
         case 10:
            return resolve(engine.popStack(), engine.popStack());
         case 11:
            return escapeString(engine.popStack());
         case 12:
            return unescapeString(engine.popStack());
         case 13:
            return loadString(engine.getBrowser(), engine.popStack(), engine.popStack());
      }
   }

   public static final Value isValid(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      } else {
         url = url.toStringValue();
         if (!url.isString()) {
            return BooleanValue.FALSE;
         } else {
            String surl = url.toString().trim();
            if (!allOkUrlChars(surl)) {
               return BooleanValue.FALSE;
            } else {
               int start = 0;
               int limit = surl.length();
               if (start < limit) {
                  return surl.charAt(start) == '#' ? BooleanValue.TRUE : isValidURL(surl, start, limit);
               } else {
                  return BooleanValue.FALSE;
               }
            }
         }
      }
   }

   public static final Value getScheme(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString().trim();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      int start = 0;
      int limit = surl.length();

      char c;
      for (int i = 0; i < limit && (c = surl.charAt(i)) != '/'; i++) {
         if (c == ':') {
            String s = StringUtilities.toLowerCase(surl.substring(start, i), 1701707776);
            if (!isValidProtocol(s)) {
               return Value.INVALID;
            }

            return new StringValue(s);
         }
      }

      return StringValue.EMPTY_STRING;
   }

   public static final Value getHost(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      url = skipProtocol(surl);
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      surl = url.toString().trim();
      int start = 0;
      int limit = surl.length();
      String s = "";
      if (start <= limit - 2 && surl.charAt(start) == '/' && surl.charAt(start + 1) == '/') {
         start += 2;
         int i = surl.indexOf(47, start);
         if (i < 0) {
            i = surl.indexOf(35, start);
            if (i < 0) {
               i = surl.indexOf(63, start);
               if (i < 0) {
                  i = limit;
               }
            }
         }

         int pn = surl.indexOf(58, start);
         if (pn < i && pn > 0) {
            s = surl.substring(start, pn);
         } else {
            s = surl.substring(start, i);
         }

         if (!isValidHost(s)) {
            return Value.INVALID;
         }
      }

      return new StringValue(s);
   }

   public static final Value getPort(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString().trim();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      url = skipProtocol(surl);
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      surl = url.toString();
      int start = 0;
      int limit = surl.length();
      String s = "";
      if (start <= limit - 2 && surl.charAt(start) == '/' && surl.charAt(start + 1) == '/') {
         start += 2;
         int i = surl.indexOf(47, start);
         if (i < 0) {
            i = surl.indexOf(35, start);
            if (i < 0) {
               i = surl.indexOf(63, start);
               if (i < 0) {
                  i = limit;
               }
            }
         }

         int pn = surl.indexOf(58, start);
         if (pn < i && pn > 0) {
            try {
               s = surl.substring(pn + 1, i);
               Integer.parseInt(s);
               return new StringValue(s);
            } finally {
               ;
            }
         }
      }

      return new StringValue("");
   }

   public static final Value getPath(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString().trim();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      url = skipToPath(surl);
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      surl = url.toString();
      int start = 0;
      int limit = surl.length();

      int i;
      for (i = 0; i < limit; i++) {
         char c = surl.charAt(i);
         if (c == '#' || c == '?') {
            break;
         }
      }

      String path_param = surl.substring(0, i);
      StringBuffer buff = (StringBuffer)(new Object());

      while (start < limit && (start = path_param.indexOf(47, start)) != -1) {
         i = path_param.indexOf(59, start);
         if (i >= 0) {
            buff.append(path_param.substring(start, i));
            start = i;
         } else {
            start = limit;
            buff.append(path_param);
         }
      }

      return new StringValue(buff.toString());
   }

   public static final Value getParameters(Value url) {
      Value path = lastPathIncludeParam(url);
      if (path.isInvalid()) {
         return Value.INVALID;
      }

      path = path.toStringValue();
      if (!path.isString()) {
         return Value.INVALID;
      }

      String spath = path.toString();
      int i = spath.indexOf(59);
      return i >= 0 ? new StringValue(spath.substring(i + 1, spath.length())) : StringValue.EMPTY_STRING;
   }

   public static final Value getQuery(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString().trim();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      int start = 0;
      int limit = surl.length();
      int i = 0;
      start = surl.indexOf(63);
      if (start >= 0 && start <= limit) {
         i = surl.indexOf(35);
         if (i < 0) {
            i = limit;
         }

         return new StringValue(surl.substring(start + 1, i));
      } else {
         return StringValue.EMPTY_STRING;
      }
   }

   public static final Value getFragment(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString().trim();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      int start = surl.indexOf(35);
      return start >= 0 ? new StringValue(surl.substring(start + 1, surl.length())) : StringValue.EMPTY_STRING;
   }

   public static final Value getBase(IBrowser browser) {
      String url = browser.getCurrentUrl();
      int i = url.indexOf(35);
      return i >= 0 && i < url.length() ? new StringValue(url.substring(0, i)) : new StringValue(url);
   }

   public static final Value getReferer(IBrowser browser) {
      return new StringValue(browser.getCurrentCard(false));
   }

   public static final Value resolve(Value embeddedUrl, Value baseUrl) {
      if (embeddedUrl.isInvalid()) {
         return Value.INVALID;
      }

      embeddedUrl = embeddedUrl.toStringValue();
      if (!embeddedUrl.isString()) {
         return Value.INVALID;
      }

      String eurl = embeddedUrl.toString();
      if (!allOkUrlChars(eurl)) {
         return Value.INVALID;
      }

      BooleanValue is = (BooleanValue)isAbsoluteUrl(eurl);
      if (is.isInvalid()) {
         return Value.INVALID;
      }

      if (is.getValue()) {
         return embeddedUrl;
      }

      if (baseUrl.isInvalid()) {
         return Value.INVALID;
      }

      baseUrl = baseUrl.toStringValue();
      if (!baseUrl.isString()) {
         return Value.INVALID;
      }

      String burl = baseUrl.toString();
      if (!allOkUrlChars(burl)) {
         return Value.INVALID;
      }

      if (eurl.length() == 0) {
         return new StringValue(((StringBuffer)(new Object())).append(burl).append('/').toString());
      }

      if (eurl.charAt(0) == '/') {
         return new StringValue(((StringBuffer)(new Object())).append(burl).append(eurl).toString());
      }

      try {
         return new StringValue(((URI)(new Object(eurl.toString(), burl.toString()))).getAbsoluteURL());
      } finally {
         ;
      }
   }

   public static final Value escapeString(Value string) {
      if (string.isInvalid()) {
         return Value.INVALID;
      }

      string = string.toStringValue();
      if (!string.isString()) {
         return Value.INVALID;
      }

      String str = string.toString();
      StringBuffer buff = (StringBuffer)(new Object());

      for (int i = 0; i < str.length(); i++) {
         char next = str.charAt(i);
         if (isSpecial(next)) {
            buff.append('%');
            buff.append(Integer.toHexString(next));
         } else {
            if (next > 255) {
               return Value.INVALID;
            }

            buff.append(next);
         }
      }

      return new StringValue(buff.toString());
   }

   public static final Value unescapeString(Value string) {
      if (string.isInvalid()) {
         return Value.INVALID;
      }

      string = string.toStringValue();
      if (!string.isString()) {
         return Value.INVALID;
      }

      String str = string.toString();
      StringBuffer buff = (StringBuffer)(new Object());

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c == '%') {
            char ch1 = str.charAt(i + 1);
            char ch2 = str.charAt(i + 2);
            if (ch1 > 255 | ch2 > 255) {
               return Value.INVALID;
            }

            char next = decode(ch1, ch2);
            if (next != ' ') {
               buff.append(next);
               i += 2;
            } else {
               buff.append(c);
            }
         } else {
            if (c > 255) {
               return Value.INVALID;
            }

            buff.append(c);
         }
      }

      return new StringValue(buff.toString());
   }

   public static final Value loadString(IBrowser browser, Value content_type, Value url) {
      String value = browser.loadString(content_type.toString(), url.toString());
      if (value == null) {
         return Value.INVALID;
      } else {
         return value.startsWith("0xafe0c74c3e6cf312L ") ? new IntegerValue(Integer.parseInt(value.substring(value.indexOf(32) + 1))) : new StringValue(value);
      }
   }

   private static final Value isValidURL(String surl, int start, int limit) {
      int i = surl.indexOf(32);
      if (i >= 0 && i < limit) {
         return Value.INVALID;
      }

      char c;
      for (int var10 = 0; var10 < limit && (c = surl.charAt(var10)) != '/'; var10++) {
         if (c == ':') {
            if (!isValidProtocol(StringUtilities.toLowerCase(surl.substring(start, var10), 1701707776))) {
               return BooleanValue.FALSE;
            }

            start = var10 + 1;
            break;
         }
      }

      if (start <= limit - 2 && surl.charAt(start) == '/' && surl.charAt(start + 1) == '/') {
         start += 2;
         i = surl.indexOf(47, start);
         if (i < 0) {
            i = limit;
         }

         int pn = surl.indexOf(58, start);
         if (pn < i && pn > 0) {
            try {
               Integer.parseInt(surl.substring(pn + 1, i));
            } finally {
               ;
            }

            if (pn > start && !isValidHost(surl.substring(start, pn))) {
               return BooleanValue.FALSE;
            }
         } else if (!isValidHost(surl.substring(start, i))) {
            return BooleanValue.FALSE;
         }
      }

      return BooleanValue.TRUE;
   }

   private static final boolean isValidProtocol(String protocol) {
      int len = protocol.length();
      if (len < 2) {
         return false;
      }

      for (int i = 0; i < len; i++) {
         char c = protocol.charAt(i);
         if (!isUSASCIILetterOrDigit(c) && c != '.' && c != '+' && c != '-') {
            return false;
         }
      }

      return true;
   }

   private static final boolean isValidHost(String host) {
      int len = host.length();

      for (int i = 0; i < len; i++) {
         char c = host.charAt(i);
         if (!isUSASCIILetterOrDigit(c) && c != '.' && c != '-') {
            return false;
         }
      }

      return true;
   }

   private static final Value skipProtocol(String url) {
      char c;
      for (int i = 0; i < url.length() && (c = url.charAt(i)) != '/'; i++) {
         if (c == ':') {
            if (!isValidProtocol(StringUtilities.toLowerCase(url.substring(0, i), 1701707776))) {
               return Value.INVALID;
            }

            return new StringValue(url.substring(i + 1, url.length()));
         }
      }

      return new StringValue(url);
   }

   private static final Value skipToPath(String url) {
      Value skip = skipProtocol(url);
      if (skip.isInvalid()) {
         return Value.INVALID;
      }

      skip = skip.toStringValue();
      if (!skip.isString()) {
         return Value.INVALID;
      }

      String rest = skip.toString();
      int i = 0;
      int start = 0;
      if (rest.length() >= 2 && rest.charAt(0) == '/' && rest.charAt(1) == '/') {
         int var6 = 2;
         i = rest.indexOf(47, var6);
         if (i < 0) {
            i = rest.indexOf(35, var6);
            if (i < 0) {
               i = rest.indexOf(63, var6);
               if (i < 0) {
                  i = rest.length();
               }
            }
         }
      }

      return new StringValue(rest.substring(i, rest.length()));
   }

   private static final Value lastPathIncludeParam(Value url) {
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      String surl = url.toString();
      if (!allOkUrlChars(surl)) {
         return Value.INVALID;
      }

      url = skipToPath(surl);
      if (url.isInvalid()) {
         return Value.INVALID;
      }

      url = url.toStringValue();
      if (!url.isString()) {
         return Value.INVALID;
      }

      surl = url.toString();
      int limit = surl.length();
      int start = surl.lastIndexOf(47);

      int i;
      for (i = start; i < limit; i++) {
         char c = surl.charAt(i);
         if (c == '#' || c == '?') {
            break;
         }
      }

      return new StringValue(surl.substring(start, i));
   }

   private static final boolean isSpecial(int n) {
      return n == 32
         || n >= 0 && n <= 31
         || n >= 34 && n <= 38
         || n == 43
         || n == 44
         || n == 47
         || n == 58
         || n == 59
         || n >= 60 && n <= 64
         || n >= 91 && n <= 94
         || n == 96
         || n >= 123 && n <= 125
         || n == 127
         || n >= 143 && n <= 255;
   }

   private static final char decode(char high, char low) {
      int hi = toInt(high);
      int lo = toInt(low);
      return hi != -1 && lo != -1 ? (char)(hi * 16 + lo) : ' ';
   }

   private static final int toInt(char c) {
      int i = -1;
      if (c == 'a' || c == 'A') {
         return 10;
      }

      if (c == 'b' || c == 'B') {
         return 11;
      }

      if (c == 'c' || c == 'C') {
         return 12;
      }

      if (c == 'd' || c == 'D') {
         return 13;
      }

      if (c == 'e' || c == 'E') {
         return 14;
      }

      if (c != 'f' && c != 'F') {
         try {
            return Integer.parseInt(String.valueOf(c));
         } finally {
            ;
         }
      } else {
         return 15;
      }
   }

   private static final Value isAbsoluteUrl(String url) {
      int start = 0;

      char c;
      for (int i = start; i < url.length() && (c = url.charAt(i)) != '/'; i++) {
         if (c == ':') {
            if (!isValidProtocol(url.substring(start, i))) {
               return Value.INVALID;
            }

            start = i + 1;
         }
      }

      if (start <= url.length() - 2 && url.charAt(start + 1) == '/' && url.charAt(start + 2) == '/') {
         start += 2;
         int i = url.indexOf(47, start);
         if (i < 0) {
            i = url.length();
         }

         return !isValidHost(url.substring(start, i)) ? Value.INVALID : new BooleanValue(true);
      } else {
         return new BooleanValue(false);
      }
   }

   private static final boolean isUSASCIILetterOrDigit(char c) {
      return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
   }

   private static final boolean allOkUrlChars(String url) {
      int urlLength = url.length();

      for (int i = 0; i < urlLength; i++) {
         if (url.charAt(i) == '%') {
            if (i >= urlLength - 2) {
               return false;
            }

            if (!isHexDigit(url.charAt(i)) || !isHexDigit(url.charAt(i + 2))) {
               return false;
            }
         }
      }

      return true;
   }

   private static final boolean isHexDigit(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
   }
}
