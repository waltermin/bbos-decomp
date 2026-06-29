package net.rim.device.apps.internal.browser.css;

public interface DocumentHandler {
   void startDocument(String var1);

   void endDocument(String var1);

   void importStyle(String var1, String[] var2);

   boolean startMedia(String[] var1);

   void endMedia(String[] var1);

   void startSelector(int[][] var1, int var2);

   void endSelector(int[][] var1, int var2);

   void property(int var1, int[] var2, boolean var3);
}
