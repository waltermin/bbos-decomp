package net.rim.device.apps.internal.browser.wml;

public final class WMLContextManager {
   private WMLContext _wmlContext = new WMLContext();
   private boolean _useSuppliedContext;

   final void setContext(WMLContext wmlContext) {
      this._wmlContext = wmlContext;
   }

   final WMLContext getContext() {
      return this._wmlContext;
   }

   public final void newContext() {
      if (this._useSuppliedContext) {
         this._wmlContext.clear();
      } else {
         this._wmlContext = new WMLContext();
         this._useSuppliedContext = true;
      }
   }

   final void useSuppliedContext(boolean useSuppliedContext) {
      this._useSuppliedContext = useSuppliedContext;
   }

   final void put(String name, String value) {
      this._wmlContext.put(name, value);
   }

   final String get(String name) {
      return this._wmlContext.get(name);
   }

   final void setTimerValue(int value) {
      this._wmlContext.setTimerValue(value);
   }
}
