package net.rim.device.api.crypto.certificate;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

class CertificateServersOptionsScreen$ServerVerb extends Verb {
   private int _type;
   private final CertificateServersOptionsScreen this$0;
   public static final int SAVE = 0;
   public static final int DELETE = 1;
   public static final int CLOSE = 2;

   public CertificateServersOptionsScreen$ServerVerb(CertificateServersOptionsScreen _1, int type, int ordering, ResourceBundleFamily rb, int rbKey) {
      super(ordering, rb, rbKey);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public Object invoke(Object parameter) {
      switch (this._type) {
         case -1:
            throw new Object();
         case 0:
         default:
            this.this$0.doSave();
            return null;
         case 1:
            if (!SimpleChoiceDialog.askYesNoQuestion(CertificateServersOptionsScreen._rb.getString(215), this.this$0._serverInfo.getFriendlyName())) {
               return null;
            }

            this.this$0.doDelete();
            this.this$0.doClose();
            return null;
         case 2:
            if (this.this$0.hasChanged()) {
               this.this$0.promptForSave();
               return null;
            } else {
               this.this$0.doClose();
               return null;
            }
      }
   }
}
