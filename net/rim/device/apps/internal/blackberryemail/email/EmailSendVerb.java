package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.classification.MessageClassification;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesDefaults;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

final class EmailSendVerb extends Verb {
   private EditorUsingRIMModelFactory _screen;
   private EmailMessageModel _message;
   private TransitoryMessagePropertiesModel _messagePropertiesModel;
   private SpellCheckOnSendAgent _scAgent;

   EmailSendVerb(EditorUsingRIMModelFactory screen, TransitoryMessagePropertiesModel messagePropertiesModel) {
      super(332032, CommonResources.getResourceBundle(), 9150);
      this._screen = screen;
      this._messagePropertiesModel = messagePropertiesModel;
      this._message = null;
   }

   private final boolean isPerformSpellCheckBeforeSend() {
      return SpellCheckOnSendAgent.isSpellCheckOnSendEnabled();
   }

   private final Object spellCheckOnSend(Object context) {
      if (this._scAgent == null) {
         this._scAgent = new SpellCheckOnSendAgent(this);
      }

      this._scAgent.init(this._screen.getFieldsFromEdit(), context);
      switch (this._scAgent.invokeSpellCheck()) {
         case 1:
            return null;
         default:
            return this.performInvocation(context);
      }
   }

   @Override
   public final Object invoke(Object context) {
      return this.isPerformSpellCheckBeforeSend() ? this.spellCheckOnSend(context) : this.performInvocation(context);
   }

   final void asyncPerformInvocation(Object context, boolean invocationCancelled) {
      if (!invocationCancelled) {
         this._screen.finalizeVerbInvocation(this, this.performInvocation(context));
      }
   }

   private final Object performInvocation(Object context) {
      this._message = (EmailMessageModel)this._screen.getModel(false);
      byte encoding = this._message.getEncoding();
      if (encoding != -1 && (encoding & 112) == 0 && context instanceof ContextObject) {
         Font font = (Font)ContextObject.get((ContextObject)context, 77);
         if (font != null) {
            this._message.setEncoding(CMIMEUtilities.addHints(encoding, font));
         }
      }

      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      if (!EmailSendUtility.allAddressesResolved(this._message)) {
         if (this._screen instanceof EmailEditorScreen) {
            Field firstUnresolved = ((EmailEditorScreen)this._screen).findUnresolvedLookup();
            if (firstUnresolved != null) {
               firstUnresolved.setFocus();
            }
         }

         return null;
      } else {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         Object nnePassword = null;
         if (NNEPasswordManager.isPasswordRequired(this._message)) {
            nnePassword = NNEPasswordManager.confirmEncodedPassword(this._message);
            if (nnePassword == null) {
               return null;
            }

            contextObject.put(129, nnePassword);
         }

         SendMethod sendMethod = this._messagePropertiesModel.getSelectedSendMethod();
         if (sendMethod == null) {
            return null;
         }

         MessageClassification messageClassification = this._messagePropertiesModel.getSelectedMessageClassification();
         PrependedDisclaimerModel disclaimerModel = (PrependedDisclaimerModel)SubmemberUtilities.getFirstSubmember(
            this._message, PrependedDisclaimerModel.getRecognizer()
         );
         SubjectModel oldSubject = (SubjectModel)SubmemberUtilities.getFirstSubmember(this._message, new EmailSendVerb$SubjectModelRecognizer(null));
         SubjectModel newSubject = this.getModifiedMessageSubject(oldSubject, messageClassification);
         if (newSubject != null) {
            if (oldSubject != null) {
               this._message.remove(oldSubject);
            }

            this._message.add(newSubject);
         }

         BodyModel oldBody = this._message.getBodyModel();
         BodyModel newBody = this.getModifiedMessageBody(oldBody, messageClassification, disclaimerModel);
         if (newBody != null) {
            if (oldBody != null) {
               this._message.remove(oldBody);
            }

            this._message.add(newBody);
         }

         if (!sendMethod.send(this._message, contextObject)) {
            if (newSubject != null) {
               this._message.remove(newSubject);
               if (oldSubject != null) {
                  this._message.add(oldSubject);
               }
            }

            if (newBody != null) {
               this._message.remove(newBody);
               if (oldBody != null) {
                  this._message.add(oldBody);
               }
            }

            return null;
         } else {
            MessagePropertiesDefaults messagePropertiesDefaults = MessagePropertiesDefaults.getInstance();
            RecipientCache recipientCache = RecipientCache.getInstance();
            int cacheServiceUIDHash = -1;
            int cacheServiceUserId = -1;
            ServiceRecord serviceRecord = sendMethod.getServiceRecord();
            if (serviceRecord != null) {
               cacheServiceUIDHash = serviceRecord.getUidHash();
               cacheServiceUserId = serviceRecord.getUserId();
            }

            int cacheMessageClassificationHash = -1;
            if (messageClassification != null && messageClassification.hashCode() != messagePropertiesDefaults.getMessageClassification()) {
               cacheMessageClassificationHash = messageClassification.hashCode();
            }

            long cacheEncodingUID = -1;
            int cacheEncodingFlags = 0;
            if (sendMethod.getEncodingUID() != messagePropertiesDefaults.getEncodingUID()
               || sendMethod.getEncodingAction() != messagePropertiesDefaults.getEncodingAction()) {
               cacheEncodingUID = sendMethod.getEncodingUID();
               int encodingAction = sendMethod.getEncodingAction();
               if ((encodingAction & 1) != 0) {
                  cacheEncodingFlags |= 256;
               }

               if ((encodingAction & 2) != 0) {
                  cacheEncodingFlags |= 512;
               }
            }

            recipientCache.add(this._message, cacheServiceUserId, cacheServiceUIDHash, cacheMessageClassificationHash, cacheEncodingUID, cacheEncodingFlags);
            if (!ContextObject.getFlag(contextObject, 121)) {
               ShowMessageApp.showMessageApp();
            }

            return new ContextObject(39);
         }
      }
   }

   private final BodyModel getModifiedMessageBody(BodyModel oldBodyModel, MessageClassification messageClassification, PrependedDisclaimerModel disclaimerModel) {
      String currentBodyString = null;
      if (disclaimerModel != null && disclaimerModel.prependDisclaimer() && !this._message.flagsSet(8192)) {
         String prependedDisclaimer = ITPolicy.getString(23, 3);
         if (prependedDisclaimer != null && prependedDisclaimer.length() > 0) {
            if (currentBodyString == null && oldBodyModel != null) {
               currentBodyString = oldBodyModel.getText();
            }

            StringBuffer sb = new StringBuffer(prependedDisclaimer);
            if (currentBodyString != null) {
               sb.append('\n');
               sb.append(currentBodyString);
            }

            currentBodyString = sb.toString();
         }
      }

      if (messageClassification != null) {
         if (currentBodyString == null && oldBodyModel != null) {
            currentBodyString = oldBodyModel.getText();
         }

         currentBodyString = messageClassification.addBodyIndicator(currentBodyString);
      }

      return currentBodyString == null ? null : (BodyModel)FactoryUtil.createInstance(5987399499453925075L, currentBodyString);
   }

   private final SubjectModel getModifiedMessageSubject(SubjectModel oldSubjectModel, MessageClassification messageClassification) {
      String currentSubjectString = null;
      if (messageClassification != null) {
         if (currentSubjectString == null && oldSubjectModel != null) {
            currentSubjectString = oldSubjectModel.getSubject();
         }

         MessageClassification[] allClassifications = MessageClassification.getMessageClassifications();
         int numClassifications = allClassifications != null ? allClassifications.length : 0;

         for (int i = 0; i < numClassifications; i++) {
            if (allClassifications[i].matchesSubject(currentSubjectString)) {
               currentSubjectString = allClassifications[i].removeSubjectIndicator(currentSubjectString);
            }
         }

         currentSubjectString = messageClassification.addSubjectIndicator(currentSubjectString);
      }

      if (currentSubjectString == null) {
         return null;
      }

      ContextObject subjectCreationContext = new ContextObject();
      ContextObject.put(subjectCreationContext, 253, currentSubjectString);
      return (SubjectModel)FactoryUtil.createInstance(3928489455534245796L, subjectCreationContext);
   }
}
