package net.rim.device.api.crypto.tls;

import net.rim.device.apps.api.options.OptionsItemVerb;
import net.rim.device.internal.ui.component.SimpleOKCancelInputDialog;

class ProxyTLSOptionsProvider$AddHostVerb extends OptionsItemVerb {
   private final ProxyTLSOptionsProvider this$0;

   ProxyTLSOptionsProvider$AddHostVerb(ProxyTLSOptionsProvider _1) {
      super(ProxyTLSOptionsProvider._rb.getString(35), 1);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      SimpleOKCancelInputDialog dialog = new SimpleOKCancelInputDialog(9, ProxyTLSOptionsProvider._rb.getString(40), 0, 1000000, 0);
      dialog.show();
      if (dialog.getCloseReason() == -1) {
         return null;
      }

      String host = dialog.getText();
      if (host != null) {
         host.trim();
         if (host.length() != 0 && !this.this$0._trustedHosts.contains(host)) {
            this.this$0._trustedHosts.addElement(host);
            this.this$0._trustedHostsField.setSize(this.this$0._trustedHosts.size());
            this.this$0._vfm.setDirty(true);
         }
      }

      return null;
   }
}
