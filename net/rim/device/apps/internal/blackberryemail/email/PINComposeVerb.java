package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class PINComposeVerb extends Verb {
   public static final long GUID_SEND_AS_PIN = -1946364186576950217L;

   public static final PINComposeVerb getSendAsVerb() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      PINComposeVerb sendAsVerb = (PINComposeVerb)applicationRegistry.get(-1946364186576950217L);
      if (sendAsVerb == null) {
         sendAsVerb = new PINComposeVerb(116);
         applicationRegistry.put(-1946364186576950217L, sendAsVerb);
      }

      return sendAsVerb;
   }

   public PINComposeVerb() {
      super(1265957, EmailResources.getResourceBundle(), 4);
   }

   PINComposeVerb(int rbKey) {
      super(1265957, EmailResources.getResourceBundle(), rbKey);
   }

   @Override
   public final Object invoke(Object context) {
      if (!EmailMessageUtilities.canSendPIN()) {
         Dialog.alert(EmailResources.getString(213));
      }

      ContextObject contextObject = ContextObject.clone(context);
      contextObject.setFlag(31);
      contextObject.setFlag(94);
      contextObject.clearFlag(37);
      contextObject.clearFlag(12);
      contextObject.clearFlag(53);
      contextObject.clearFlag(29);
      contextObject.clearFlag(30);
      contextObject.clearFlag(13);
      EmailMessageModel message;
      if ((message = (EmailMessageModel)ContextObject.get(contextObject, -8485899342890396495L)) == null
         && (message = EmailBuilder.buildMessage(contextObject)) == null) {
         return null;
      }

      contextObject.setFlag(0);
      EmailEditorScreen editor = new EmailEditorScreen(contextObject);
      editor.setModel(message);
      Recognizer fieldRecognizer = null;
      if (editor.findNonBlankHeader(-1) != null) {
         fieldRecognizer = new SubjectRecognizer();
      } else {
         fieldRecognizer = RecognizerRepository.getRecognizers(-3702691709233646541L);
      }

      if (fieldRecognizer != null) {
         editor.setFocus(fieldRecognizer);
      }

      ContextObject returnContext = ContextObject.castOrCreate(editor.go());
      boolean clearTerminalFlag = ContextObject.getFlag(context, 43)
         || ContextObject.getFlag(context, 74)
         || ContextObject.getFlag(context, 55)
         || ContextObject.getPrivateFlag(context, -337556985625701066L, 0);
      if (clearTerminalFlag) {
         returnContext.clearFlag(39);
      } else {
         returnContext.setFlag(39);
      }

      return returnContext;
   }

   @Override
   public final String toString(Object context) {
      RIMModel model = (RIMModel)ContextObject.get(context, 254);
      if (model == null) {
         return this.toString();
      }

      if (!ContextObject.getFlag(context, 51) && !ContextObject.getFlag(context, 63)) {
         String description;
         if (ContextObject.getFlag(context, 7)) {
            description = EmailResources.getString(34);
         } else {
            description = EmailResources.getString(34);
         }

         if (!(model instanceof Object)) {
            return ((StringBuffer)(new Object())).append(description).append(' ').append(model.toString()).toString();
         }

         VerbDescriptionProvider descriptor = (VerbDescriptionProvider)model;
         return ((StringBuffer)(new Object())).append(description).append(' ').append(descriptor.getVerbDescription(context)).toString();
      } else {
         return model.toString();
      }
   }

   @Override
   public final int getVerbGroupId() {
      return 13685231;
   }
}
