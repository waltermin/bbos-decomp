package net.rim.device.apps.internal.browser.core;

public interface UrlCollectionListener {
   int ACTION_ADD = 0;
   int ACTION_REMOVE = 1;

   void collectionChanged(String var1, int var2);
}
