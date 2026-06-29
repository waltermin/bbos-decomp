package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailDeleteOriginalTextVerb extends Verb {
   private EditorUsingRIMModelFactory _screen;

   EmailDeleteOriginalTextVerb(EditorUsingRIMModelFactory screen) {
      super(16859472, EmailResources.getResourceBundle(), 900);
      this._screen = screen;
   }

   @Override
   public final Object invoke(Object context) {
      EmailMessageModel message = (EmailMessageModel)this._screen.getModel(false);
      Object originalText = SubmemberUtilities.getFirstSubmember(message, EmailPayloadRecognizer.getInstance());
      if (originalText == null) {
         return null;
      }

      message.remove(originalText);
      ContextObject c = (ContextObject)(new Object());
      c.put(245, originalText);
      EmailBuilder.addOriginalMessageReferenceIdentifierStub(message, c);
      this._screen.deleteModel(originalText);
      int type = message.getType();
      if (type == 2) {
         message.setType((byte)1);
         return null;
      }

      if (type == 8) {
         message.setType((byte)4);
      }

      return null;
   }
}
