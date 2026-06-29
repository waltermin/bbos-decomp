package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;

public final class WebFeedStatusStore {
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(-3617339743333193477L);
   private WebFeedStatusStore$WebFeedStore _store;
   private static WebFeedStatusStore _instance;
   private static final long STORE_KEY = -3617339743333193477L;

   private WebFeedStatusStore() {
      Object contents = this._persistentObject.getContents();
      if (contents instanceof WebFeedStatusStore$WebFeedStore) {
         this._store = (WebFeedStatusStore$WebFeedStore)contents;
      } else {
         this._store = new WebFeedStatusStore$WebFeedStore();
         this._persistentObject.setContents(this._store, 51);
      }
   }

   public static final WebFeedStatusStore getInstance() {
      if (_instance != null) {
         return _instance;
      }

      _instance = new WebFeedStatusStore();
      return _instance;
   }

   public final void setWebFeedGuids(String url, WebFeedItem[] items) {
      if (url != null) {
         String[] newGuids = new String[0];

         for (int i = items.length - 1; i >= 0; i--) {
            if (items[i] != null) {
               String guid = items[i].getGuid();
               if (guid != null) {
                  Arrays.add(newGuids, guid);
               }
            }
         }

         synchronized (this._store) {
            String[] oldGuids = this._store.getGuids(url);
            if (oldGuids != null) {
               for (int i = oldGuids.length - 1; i >= 0; i--) {
                  boolean found = false;

                  for (int j = newGuids.length - 1; j >= 0; j--) {
                     if (oldGuids[i].equals(newGuids[j])) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     this._store.removeGuid(oldGuids[i]);
                  }
               }
            }

            this._store.setGuids(url, newGuids);
         }
      }
   }

   public final void setItemStatus(WebFeedItem item, boolean read) {
      if (item != null) {
         synchronized (this._store) {
            if (read) {
               this._store.setAsRead(item.getGuid());
               item.setStatus(0);
            } else {
               this._store.removeGuid(item.getGuid());
               item.setStatus(1);
            }
         }
      }
   }

   public final void updateItemStatus(WebFeedItem item) {
      if (item != null) {
         synchronized (this._store) {
            if (this._store.isRead(item.getGuid())) {
               item.setStatus(0);
            } else {
               item.setStatus(1);
            }
         }
      }
   }
}
