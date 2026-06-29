package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class EmailComposeVerb extends ShowMessageAppVerb {
   public static final long GUID_SEND_AS_EMAIL = -532296207860186005L;

   public static final EmailComposeVerb getSendAsVerb() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      EmailComposeVerb sendAsEmailComposeVerb = (EmailComposeVerb)applicationRegistry.get(-532296207860186005L);
      if (sendAsEmailComposeVerb == null) {
         sendAsEmailComposeVerb = new EmailComposeVerb(103);
         applicationRegistry.put(-532296207860186005L, sendAsEmailComposeVerb);
      }

      return sendAsEmailComposeVerb;
   }

   public EmailComposeVerb() {
      super(1265920, EmailResources.getResourceBundle(), 5);
   }

   private EmailComposeVerb(int rbKey) {
      super(1265920, EmailResources.getResourceBundle(), rbKey);
   }

   @Override
   public final Object doInvoke(Object context) {
      if (!CMIMEUtilities.canSendEmail()) {
         Dialog.alert(EmailResources.getString(210));
      }

      ContextObject contextObject = ContextObject.clone(context);
      if (this.hasAttachmentData(contextObject) && !CMIMEUtilities.isLargeAttachmentUploadSupportedByAnyCMIMEService()) {
         Dialog.alert(MessageResources.getString(236));
      }

      contextObject.setFlag(31);
      contextObject.clearFlag(94);
      contextObject.clearFlag(37);
      contextObject.clearFlag(12);
      contextObject.clearFlag(53);
      contextObject.clearFlag(29);
      contextObject.clearFlag(30);
      contextObject.clearFlag(13);
      EmailMessageModel message;
      if ((message = (EmailMessageModel)ContextObject.get(context, -8485899342890396495L)) == null
         && (message = EmailBuilder.buildMessage(contextObject)) == null) {
         return null;
      }

      contextObject.setFlag(0);
      contextObject.remove(250);
      byte encoding = message.getEncoding();
      if (encoding != -1) {
         Font font = CMIMEUtilities.getSuggestedFontForEncoding(encoding);
         if (font != null) {
            ContextObject.put(contextObject, 77, font);
         }
      }

      return showEditorScreen(contextObject, context, message);
   }

   private final boolean hasAttachmentData(ContextObject contextObject) {
      return contextObject.containsKey(6420606222376351919L)
         || contextObject.containsKey(2765042845091913199L)
         || contextObject.containsKey(5473606008898265655L);
   }

   public static final Object showEditorScreen(ContextObject context, Object originalContext, EmailMessageModel message) {
      EmailEditorScreen editor = new EmailEditorScreen(context);
      editor.setModel(message);
      Recognizer fieldRecognizer = null;
      if (editor.findNonBlankHeader(-1) != null) {
         String subject = message.getSubject();
         if (subject != null && subject.length() > 0) {
            fieldRecognizer = RecognizerRepository.getRecognizers(5987399499453925075L);
         } else {
            fieldRecognizer = new SubjectRecognizer();
         }
      } else {
         fieldRecognizer = RecognizerRepository.getRecognizers(-3702691709233646541L);
      }

      if (fieldRecognizer != null) {
         editor.setFocus(fieldRecognizer);
      }

      ContextObject returnContext = ContextObject.castOrCreate(editor.go());
      boolean clearTerminalFlag = ContextObject.getFlag(originalContext, 43)
         || ContextObject.getFlag(originalContext, 74)
         || ContextObject.getFlag(originalContext, 55)
         || ContextObject.getPrivateFlag(originalContext, -337556985625701066L, 0);
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
            description = EmailResources.getString(44);
         } else {
            description = EmailResources.getString(44);
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
      return 15556151;
   }
}
