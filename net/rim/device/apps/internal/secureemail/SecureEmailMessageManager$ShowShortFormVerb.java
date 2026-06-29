package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

class SecureEmailMessageManager$ShowShortFormVerb extends Verb {
   boolean _showShortForm;
   private final SecureEmailMessageManager this$0;

   public SecureEmailMessageManager$ShowShortFormVerb(SecureEmailMessageManager _1, ResourceBundleFamily rb, int rbKey, boolean showShortForm) {
      super(1200384, rb, rbKey);
      this.this$0 = _1;
      this._showShortForm = showShortForm;
   }

   @Override
   public Object invoke(Object context) {
      this.this$0.showShortForm(this._showShortForm);
      return null;
   }
}
