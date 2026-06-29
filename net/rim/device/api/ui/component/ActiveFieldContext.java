package net.rim.device.api.ui.component;

public final class ActiveFieldContext {
   private String _data;
   private long _id;

   public ActiveFieldContext(String data) {
      this._data = data;
   }

   public final String getData() {
      return this._data;
   }

   public final void setData(String data) {
      this._data = data;
   }

   public final long getID() {
      return this._id;
   }

   public final void setID(long ID) {
      this._id = ID;
   }
}
