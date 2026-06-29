package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.apps.api.options.OptionsItemVerb;

class KeyStoreBrowserOptionsItem$ShowHideCertsVerb extends OptionsItemVerb {
   int _type;
   private final KeyStoreBrowserOptionsItem this$0;

   KeyStoreBrowserOptionsItem$ShowHideCertsVerb(KeyStoreBrowserOptionsItem _1, int type, int menuOrdering) {
      super(KeyStoreBrowserResources.getStringArray(6045)[type], menuOrdering);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0._showType = this._type;
      this.this$0.configureDisplayFilter();
      this.this$0.loadCertificates(3);
      return null;
   }
}
