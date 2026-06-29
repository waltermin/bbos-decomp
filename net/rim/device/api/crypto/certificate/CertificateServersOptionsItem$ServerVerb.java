package net.rim.device.api.crypto.certificate;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.options.OptionsItemVerb;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

class CertificateServersOptionsItem$ServerVerb extends OptionsItemVerb {
   private int _type;
   private CertificateServerInfo _info;
   private final CertificateServersOptionsItem this$0;
   public static final int ADD = 0;
   public static final int EDIT = 1;
   public static final int DELETE = 2;

   public CertificateServersOptionsItem$ServerVerb(CertificateServersOptionsItem _1, int type, String description, int ordering) {
      super(description, ordering);
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
            UiApplication.getUiApplication().pushScreen(new CertificateServersOptionsScreen(null, this.this$0.getServerTypeOfFieldWithFocus()));
            return null;
         case 1:
            UiApplication.getUiApplication().pushScreen(new CertificateServersOptionsScreen(this._info, this.this$0.getServerTypeOfFieldWithFocus()));
            return null;
         case 2:
            if (!SimpleChoiceDialog.askYesNoQuestion(CertificateServersOptionsItem._rb.getString(215), this._info.getFriendlyName())) {
               return null;
            } else {
               this.this$0._certificateOptions.removeServer(this._info);
               return null;
            }
      }
   }

   public void setInfo(CertificateServerInfo info) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
