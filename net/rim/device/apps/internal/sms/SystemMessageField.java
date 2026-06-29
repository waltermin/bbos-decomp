package net.rim.device.apps.internal.sms;

import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class SystemMessageField extends RichTextField {
   SystemMessageField(int messageID) {
      super(SMSResources.getString(messageID), 18014398509481984L);
   }
}
