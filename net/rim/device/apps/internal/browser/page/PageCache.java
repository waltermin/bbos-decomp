package net.rim.device.apps.internal.browser.page;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryManager;

public final class PageCache {
   private Hashtable _contents;
   private Vector _keys;
   private int _maxSize;

   public PageCache(int size) {
      this._maxSize = size;
      this._contents = (Hashtable)(new Object(this._maxSize));
      this._keys = (Vector)(new Object(this._maxSize));
   }

   public final synchronized void destroy(boolean markAsReachable) {
      Enumeration items = this._contents.elements();

      while (items.hasMoreElements()) {
         PageCacheContent content = (PageCacheContent)items.nextElement();
         content.destroy();
      }

      if (markAsReachable) {
         LowMemoryManager.markAsRecoverable(this._contents);
         this._contents = (Hashtable)(new Object(this._maxSize));
      } else {
         this._contents.clear();
      }

      this._keys.setSize(0);
   }

   public final synchronized boolean addContent(String key, PageCacheContent newContent) {
      if (key != null && newContent != null) {
         if (this._keys.size() + 1 > this._maxSize) {
            Object removeKey = this._keys.elementAt(0);
            this._keys.removeElementAt(0);
            PageCacheContent removeContent = (PageCacheContent)this._contents.get(removeKey);
            removeContent.destroy();
            this._contents.remove(removeKey);
         }

         PageCacheContent replaceContent = (PageCacheContent)this._contents.put(key, newContent);
         if (replaceContent != null) {
            replaceContent.destroy();
            this._keys.removeElement(key);
         }

         this._keys.addElement(key);
         newContent.setDestroyOnUndisplay(false);
         return true;
      } else {
         return false;
      }
   }

   public final synchronized PageCacheContent removeContent(String key, int navigation, String previousUrl) {
      if (key == null) {
         return null;
      }

      int fragment = key.indexOf(35);
      if (fragment != -1) {
         key = key.substring(0, fragment);
      }

      PageCacheContent content = (PageCacheContent)this._contents.remove(key);
      if (content != null) {
         this._keys.removeElement(key);
         content.setDestroyOnUndisplay(true);
         if (navigation != 2) {
            content.destroy();
            content = null;
         }
      }

      return content;
   }

   public final int getCount() {
      return this._keys.size();
   }
}
