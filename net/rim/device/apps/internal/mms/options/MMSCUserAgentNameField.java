package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class MMSCUserAgentNameField extends EditField implements MMSOptionsScreen$Saveable {
   MMSCUserAgentNameField() {
      super("Agent Name:", "");
      String name = MMSClientServiceBook.getUserAgentName();
      if (name == null) {
         name = MMSTransportServiceBook.getUserAgentName();
      }

      this.setText(name);
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         MMSClientServiceBook.setUserAgentName(this.getText().trim());
      }
   }
}
