package net.rim.device.internal.i18n;

public interface UnicodeServiceConstants {
   byte UTF_8 = 0;
   byte UTF_16BE = 1;
   byte UNDEFINED = -1;
   String S_UTF_8 = "UTF-8\r";
   String S_UTF_16BE = "UTF-16BE\r";
   String DEFAULT_SERVICE_ENCODING = "windows-1252\r";
   byte ENCODING_SIGN_FLAG = -128;
   String EMPTY = "";
   byte HINTS_MASK = 112;
   byte HINTS_NONE = 0;
   byte HINTS_JAPANESE = 16;
   byte HINTS_KOREAN = 32;
   byte HINTS_CHINESE_SIMPLIFIED = 48;
   byte HINTS_CHINESE_TRADITIONAL = 64;
}
