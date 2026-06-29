package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.implus.InteractiveHHAddressModel;
import net.rim.device.apps.api.messaging.implus.OneWayPagerAddressModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailBuilder;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

final class IMPlusComposeVerb extends Verb {
   private static Recognizer _subjectFieldRecognizer = new SubjectRecognizer();

   public IMPlusComposeVerb() {
      super(1269760);
   }

   @Override
   public final String toString() {
      return IMPlusResources.getString(3);
   }

   @Override
   public final String toString(Object context) {
      RIMModel model = (RIMModel)ContextObject.get(context, 254);
      if (model == null) {
         return this.toString();
      }

      String description = null;
      if (!ContextObject.getFlag(context, 51) && !ContextObject.getFlag(context, 63)) {
         if (model instanceof InteractiveHHAddressModel) {
            description = IMPlusResources.getString(0);
         } else if (model instanceof OneWayPagerAddressModel) {
            description = IMPlusResources.getString(16);
         }

         if (description != null) {
            if (model instanceof VerbDescriptionProvider) {
               VerbDescriptionProvider descriptor = (VerbDescriptionProvider)model;
               return description + " " + descriptor.getVerbDescription(context);
            }

            description = description + " " + model.toString();
         }

         return description;
      } else {
         return model.toString();
      }
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(31);
      contextObject.clearFlag(94);
      contextObject.clearFlag(12);
      contextObject.clearFlag(53);
      contextObject.clearFlag(29);
      contextObject.clearFlag(30);
      contextObject.clearFlag(13);
      ContextObject.setPrivateFlag(contextObject, -3859986508589425865L, 1);
      EmailMessageModel message = EmailBuilder.buildMessage(contextObject);
      if (message == null) {
         return null;
      }

      contextObject.setFlag(0);
      EmailEditorScreen editor = new EmailEditorScreen(contextObject);
      message.setFlags(8388608);
      editor.setModel(message);
      editor.setFocus(_subjectFieldRecognizer);
      ContextObject returnContext = ContextObject.castOrCreate(editor.go());
      if (ContextObject.getFlag(context, 43)) {
         returnContext.clearFlag(39);
      } else {
         returnContext.setFlag(39);
      }

      return returnContext;
   }
}
