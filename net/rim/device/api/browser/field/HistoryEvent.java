package net.rim.device.api.browser.field;

public class HistoryEvent extends Event {
   private int _index;
   private boolean _programmatic;
   private String _url;
   private int _type;
   public static final int TYPE_NAVIGATION = 0;
   public static final int TYPE_ADD_URL = 1;
   public static final int TYPE_REMOVE_URL = 2;

   public HistoryEvent(Object src, int index, boolean programmatic, int type) {
      super(10005, src);
      this._index = index;
      this._programmatic = programmatic;
      this._type = type;
   }

   public HistoryEvent(Object src, String url, int type) {
      super(10005, src);
      this._programmatic = true;
      this._type = type;
      this._url = this.resolveUrl(url);
   }

   public int getIndex() {
      return this._index;
   }

   public boolean isProgrammatic() {
      return this._programmatic;
   }

   public int getType() {
      return this._type;
   }

   public String getURL() {
      return this._url;
   }
}
