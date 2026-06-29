package net.rim.device.api.servicebook.selector;

public final class SRSelectorApp {
   private String _name;
   private long _guid;
   private String _cid;

   public SRSelectorApp(String n, long g, String c) {
      this._name = n;
      this._guid = g;
      this._cid = c;
   }

   public final String getName() {
      return this._name;
   }

   public final void setName(String s) {
      this._name = s;
   }

   public final long getGuid() {
      return this._guid;
   }

   public final void setGuid(long g) {
      this._guid = g;
   }

   public final String getCid() {
      return this._cid;
   }

   public final void setCid(String s) {
      this._cid = s;
   }
}
