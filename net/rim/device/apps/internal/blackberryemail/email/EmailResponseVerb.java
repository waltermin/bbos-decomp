package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailResponseVerb extends Verb implements SetParameter {
   private MessagePartsProvider _message;
   private int _responseType;
   private boolean _isPIN = false;
   private boolean _isForwardAsTarget;

   EmailResponseVerb(int menuOrdering, int resourceIdWithinCommonResources, int responseType, EmailMessageModelImpl message) {
      this(menuOrdering, resourceIdWithinCommonResources, responseType, false, false);
      this._message = message;
   }

   EmailResponseVerb(int menuOrdering, int resourceIdWithinCommonResources, int responseType, boolean isPIN, boolean isForwardAsTarget) {
      super(menuOrdering, CommonResources.getResourceBundle(), resourceIdWithinCommonResources);
      this._responseType = responseType;
      this._isForwardAsTarget = isForwardAsTarget;
      this._isPIN = isPIN;
   }

   @Override
   public final void setParameter(Object message) {
      this._message = (MessagePartsProvider)message;
   }

   @Override
   public final String toString() {
      return this._isForwardAsTarget ? EmailResources.getString(this._isPIN ? 202 : 201) : super.toString();
   }

   @Override
   public final Object invoke(Object context) {
      boolean IMPlus = false;
      boolean isPIN = this._isPIN;
      ContextObject contextObject = ContextObject.clone(context);
      contextObject.put(245, this._message);
      contextObject.setFlag(this._responseType);
      if (this._message instanceof EmailMessageModelImpl && !this._isForwardAsTarget) {
         EmailMessageModelImpl emailMessage = (EmailMessageModelImpl)this._message;
         isPIN = emailMessage.flagsSet(8192);
         IMPlus = emailMessage.flagsSet(8388608);
      }

      if (isPIN) {
         contextObject.setFlag(94);
      } else {
         contextObject.clearFlag(94);
      }

      EmailMessageModelImpl newMessage = EmailBuilder.buildMessage(contextObject);
      if (newMessage == null) {
         return null;
      }

      if (IMPlus) {
         newMessage.setFlags(8388608);
      }

      isPIN = newMessage.flagsSet(8192);
      if (isPIN) {
         contextObject.setFlag(94);
         if (!EmailMessageUtilities.canSendPIN()) {
            Dialog.alert(EmailResources.getString(213));
         }
      } else {
         contextObject.clearFlag(94);
         if (!CMIMEUtilities.canSendEmail()) {
            Dialog.alert(EmailResources.getString(210));
         }
      }

      contextObject.clearFlag(37);
      contextObject.setFlag(0);
      byte encoding = newMessage.getEncoding();
      if (encoding != -1) {
         Font font = CMIMEUtilities.getSuggestedFontForEncoding(encoding);
         if (font != null) {
            ContextObject.put(contextObject, 77, font);
         }
      }

      EmailEditorScreen editor = new EmailEditorScreen(contextObject);
      editor.setModel(newMessage);
      switch (this._responseType) {
         case 12:
         case 13:
         case 29:
         case 30:
         case 53:
            Recognizer recognizer = RecognizerRepository.getRecognizers(this._responseType == 13 ? -3702691709233646541L : 5987399499453925075L);
            if (recognizer != null) {
               this._message.setRead(context);
               editor.setFocus(recognizer);
            }

            recognizer = new SubjectRecognizer();
            if (recognizer != null) {
               Field subjectField = editor.findField(recognizer);
               if (subjectField != null) {
                  subjectField.setNonSpellCheckable(true);
                  subjectField.setChangeListener(new EmailResponseVerb$SpellCheckWhenModifiedListener(this, null));
               }
            }
         default:
            return editor.go();
      }
   }
}
