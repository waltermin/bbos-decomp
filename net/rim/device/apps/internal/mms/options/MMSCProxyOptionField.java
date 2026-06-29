package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class MMSCProxyOptionField extends EditField implements MMSOptionsScreen$Saveable {
   MMSCProxyOptionField(String url) {
      super("Proxy Address: ", url);
   }

   @Override
   public final void saveOption() {
      String url = this.getText().trim();
      if (url.length() > 0) {
         MMSTransportServiceBook.setProxyAddress(url);
      }
   }
}
