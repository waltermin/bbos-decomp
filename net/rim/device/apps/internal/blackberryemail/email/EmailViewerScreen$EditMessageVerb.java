package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailViewerScreen$EditMessageVerb extends Verb {
   private EmailViewerScreen _screen;

   EmailViewerScreen$EditMessageVerb(EmailViewerScreen screen) {
      super(602368);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      TransitoryEmailMessageModel model = this._screen._transitoryEmailMessageModel;
      EmailMessageModel message = (EmailMessageModel)model.getModel();
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(0);
      CloneProvider cloneProvider = (CloneProvider)message;
      EmailMessageModel resendMessage = (EmailMessageModel)cloneProvider.clone(contextObject);
      resendMessage.setCMIMEReferenceIdentifier(0);
      resendMessage.setGMEReferenceIdentifier(0);
      if (resendMessage.getType() == 0) {
         resendMessage.setType((byte)32);
      }

      contextObject.clearFlag(37);
      EmailEditorScreen editor = new EmailEditorScreen(contextObject);
      editor.setModel(resendMessage);
      return editor.go();
   }

   @Override
   public final String toString() {
      return EmailResources.getString(200);
   }
}
