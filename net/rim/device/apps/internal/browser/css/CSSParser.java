package net.rim.device.apps.internal.browser.css;

public interface CSSParser {
   int RGB_FUNCTION_PROPERTY_VALUE_LENGTH = 11;
   int MAX_PROPERTY_VALUE_LENGTH = 11;

   void parseStyleSheet(String var1);

   void parseStyleSheet(byte[] var1);

   void parseStyleDeclaration(String var1);

   String getSource();

   int getStringStartIndex(int var1);

   int getStringEndIndex(int var1);

   CSSString getString(int var1);

   String getURL(int var1);
}
