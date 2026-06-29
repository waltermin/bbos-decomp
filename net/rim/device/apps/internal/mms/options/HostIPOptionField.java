package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class HostIPOptionField extends EditField implements MMSOptionsScreen$Saveable {
   HostIPOptionField(String hostIP) {
      super("IP/Ports: ", hostIP);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String hostIP = this.getText().trim();
         if (hostIP.length() == 0) {
            hostIP = null;
         }

         MMSTransportServiceBook.setHostIP(hostIP);
      }
   }
}
