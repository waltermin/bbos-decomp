package net.rim.device.apps.internal.mms.options;

import net.rim.device.api.ui.component.EditField;

final class MessageUrlPrefixOptionField extends EditField implements MMSOptionsScreen$Saveable {
   MessageUrlPrefixOptionField() {
      super("Message URL Prefix: ", MMSTransportServiceBook.getMessageUrlPrefix());
   }

   @Override
   public final void saveOption() {
      if (this.isDirty()) {
         String prefix = this.getText().trim();
         if (prefix.length() == 0) {
            prefix = null;
         }

         MMSTransportServiceBook.setMessageUrlPrefix(prefix);
      }
   }
}
