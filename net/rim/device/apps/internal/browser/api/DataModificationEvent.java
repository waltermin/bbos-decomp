package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;

public final class DataModificationEvent extends Event {
   private int _size;
   private String _url;

   public DataModificationEvent(Object src, String url, int size) {
      super(8, src);
      this._url = url;
      this._size = size;
   }

   public final int getSize() {
      return this._size;
   }

   public final String getURL() {
      return this._url;
   }
}
