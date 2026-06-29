package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

public final class CopyPhoneNumberVerb extends Verb {
   private String _number;

   public CopyPhoneNumberVerb() {
      super(16908592);
   }

   public final void setNumber(String number) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object parameter) {
      Clipboard.getClipboard().put(this._number);
      return null;
   }

   @Override
   public final String toString() {
      return MessageFormat.format(CommonResources.getString(1810), new Object[]{this._number});
   }
}
