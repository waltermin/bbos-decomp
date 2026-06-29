package net.rim.device.apps.internal.browser.core;

public interface UrlCollectionListener {
   int ACTION_ADD;
   int ACTION_REMOVE;

   void collectionChanged(String var1, int var2);
}
