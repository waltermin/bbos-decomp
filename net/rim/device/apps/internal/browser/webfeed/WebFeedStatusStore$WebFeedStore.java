package net.rim.device.apps.internal.browser.webfeed;

import java.util.Hashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.ToIntHashtable;

final class WebFeedStatusStore$WebFeedStore implements Persistable {
   private ToIntHashtable _readStatus = new ToIntHashtable();
   private Hashtable _guids = new Hashtable();

   public final String[] getGuids(String url) {
      return (String[])this._guids.get(url);
   }

   public final void setGuids(String url, String[] guids) {
      this._guids.put(url, guids);
   }

   public final void removeGuid(String guid) {
      if (guid != null) {
         this._readStatus.remove(guid);
      }
   }

   public final void setAsRead(String guid) {
      if (guid != null) {
         this._readStatus.put(guid, 1);
      }
   }

   public final boolean isRead(String guid) {
      return guid != null && this._readStatus.get(guid) != -1;
   }
}
