package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

class ProxyTLSOptionsProvider$DeleteHostVerb extends OptionsItemVerb {
   int _item;
   private final ProxyTLSOptionsProvider this$0;

   ProxyTLSOptionsProvider$DeleteHostVerb(ProxyTLSOptionsProvider _1, int item) {
      super(ProxyTLSOptionsProvider._rb.getString(34), 1);
      this.this$0 = _1;
      this._item = item;
   }

   @Override
   public Object invoke(Object parameter) {
      String trustedHost = (String)this.this$0._trustedHosts.elementAt(this._item);
      if (!SimpleChoiceDialog.askYesNoQuestion(ProxyTLSOptionsProvider._rb.getString(41), trustedHost)) {
         return null;
      }

      this.this$0._trustedHosts.removeElement(trustedHost);
      this.this$0._trustedHostsField.setSize(this.this$0._trustedHosts.size());
      this.this$0._vfm.setDirty(true);
      return null;
   }
}
