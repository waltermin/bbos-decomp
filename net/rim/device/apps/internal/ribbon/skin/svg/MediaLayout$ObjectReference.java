package net.rim.device.apps.internal.ribbon.skin.svg;

final class MediaLayout$ObjectReference {
   private Object _obj;
   private Object _obj2;

   public MediaLayout$ObjectReference(Object o) {
      this._obj = o;
      this._obj2 = o;
   }

   public final Object getValue() {
      return this._obj;
   }

   public final Object getLastSetValue() {
      return this._obj != null ? this._obj : this._obj2;
   }

   public final void setValue(Object o) {
      this._obj = o;
      if (o != null) {
         this._obj2 = o;
      }
   }
}
