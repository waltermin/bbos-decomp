package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class PhoneInfoOption$CopyVerb extends Verb {
   private String _text;

   PhoneInfoOption$CopyVerb(String text) {
      super(16908592);
      this._text = text;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(1800);
   }

   @Override
   public final Object invoke(Object parameter) {
      Clipboard.getClipboard().put(this._text);
      return null;
   }
}
