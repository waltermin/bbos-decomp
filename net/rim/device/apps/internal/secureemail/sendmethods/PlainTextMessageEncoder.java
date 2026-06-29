package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.body.EmailBodyModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.SecureEmailBodyModel;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.ui.component.BackgroundDialog;

public class PlainTextMessageEncoder implements MessageEncoder {
   private Object _sendContext;
   protected static final ResourceBundle _rb = ResourceBundle.getBundle(-6165272894895379810L, "net.rim.device.apps.internal.resource.secureemail.SecureEmail");

   public PlainTextMessageEncoder(EmailMessageModel message, ServiceRecord serviceRecord, Object context) {
      this._sendContext = ContextObject.clone(context);
      SecureEmailBodyModel originalSecureEmailBodyModel = this.getOriginalSecureEmailBodyModel(message);
      if (originalSecureEmailBodyModel != null && ((originalSecureEmailBodyModel.getEncodingAction() & 2) != 0 || !this.isBESVersionAtLeast412(serviceRecord))) {
         ContextObject.setFlag(this._sendContext, 70);
         this.showTruncatedWarning(message);
         this.showIgnoredAttachmentWarning(message);
      }
   }

   public boolean isAttachmentPresent(EmailMessageModel message) {
      int numMessageModels = message.size();

      for (int i = 0; i < numMessageModels; i++) {
         RIMModel currentMessageModel = (RIMModel)message.getAt(i);
         if (currentMessageModel instanceof Object) {
            EmailPayloadModel emailPayloadModel = (EmailPayloadModel)currentMessageModel;
            int numOriginalMessageModels = emailPayloadModel.size();

            for (int j = 0; j < numOriginalMessageModels; j++) {
               Object currentOriginalMessageModel = emailPayloadModel.getAt(j);
               if ((currentOriginalMessageModel instanceof Object || currentOriginalMessageModel instanceof Object)
                  && !(currentOriginalMessageModel instanceof Object)
                  && !(currentOriginalMessageModel instanceof Object)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isEmailBodyTruncated(EmailMessageModel message) {
      int numMessageModels = message.size();

      for (int i = 0; i < numMessageModels; i++) {
         RIMModel currentMessageModel = (RIMModel)message.getAt(i);
         if (currentMessageModel instanceof Object) {
            EmailPayloadModel emailPayloadModel = (EmailPayloadModel)currentMessageModel;
            int numOriginalMessageModels = emailPayloadModel.size();

            for (int j = 0; j < numOriginalMessageModels; j++) {
               Object currentOriginalMessageModel = emailPayloadModel.getAt(j);
               if (!(currentOriginalMessageModel instanceof SecureEmailBodyModel)) {
                  if (currentOriginalMessageModel instanceof Object) {
                     EmailBodyModelImpl bodyModel = (EmailBodyModelImpl)currentOriginalMessageModel;
                     if (bodyModel.isMoreAvailable() || bodyModel.isTruncated()) {
                        return true;
                     }
                  }
               } else {
                  SecureEmailBodyModel bodyModel = (SecureEmailBodyModel)currentOriginalMessageModel;
                  if (bodyModel.isMoreAvailable() || bodyModel.isTruncated()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private SecureEmailBodyModel getOriginalSecureEmailBodyModel(EmailMessageModel message) {
      int numMessageModels = message.size();

      for (int i = 0; i < numMessageModels; i++) {
         RIMModel currentMessageModel = (RIMModel)message.getAt(i);
         if (currentMessageModel instanceof Object) {
            EmailPayloadModel emailPayloadModel = (EmailPayloadModel)currentMessageModel;
            int numOriginalMessageModels = emailPayloadModel.size();

            for (int j = 0; j < numOriginalMessageModels; j++) {
               Object currentOriginalMessageModel = emailPayloadModel.getAt(j);
               if (currentOriginalMessageModel instanceof SecureEmailBodyModel) {
                  return (SecureEmailBodyModel)currentOriginalMessageModel;
               }
            }
         }
      }

      return null;
   }

   protected void showIgnoredAttachmentWarning(EmailMessageModel message) {
      SecureEmailBodyModel body = this.getOriginalSecureEmailBodyModel(message);
      if (body != null) {
         SecureEmailFactory factory = (SecureEmailFactory)body.getSecureEmailFactory();
         SecureEmailOptions secureEmailOptions = (SecureEmailOptions)factory.createGlobalOptionsCopy();
         if (secureEmailOptions != null && secureEmailOptions.getPromptTruncatedMessage() && this.isAttachmentPresent(message)) {
            String[] choices = _rb.getStringArray(184);
            int choice = BackgroundDialog.getChoice(_rb.getString(185), choices, 0);
            if (choice == 1) {
               return;
            }

            if (choice == 2) {
               secureEmailOptions.setPromptTruncatedMessage(false);
               factory.saveGlobalOptions(secureEmailOptions);
               return;
            }

            throw new AbortSendSecureEmailException();
         }
      }
   }

   protected void showTruncatedWarning(EmailMessageModel message) {
      SecureEmailBodyModel body = this.getOriginalSecureEmailBodyModel(message);
      if (body != null) {
         SecureEmailFactory factory = (SecureEmailFactory)body.getSecureEmailFactory();
         SecureEmailOptions secureEmailOptions = (SecureEmailOptions)factory.createGlobalOptionsCopy();
         if (secureEmailOptions != null && secureEmailOptions.getPromptTruncatedMessage() && this.isEmailBodyTruncated(message)) {
            String[] choices = _rb.getStringArray(181);
            int choice = BackgroundDialog.getChoice(_rb.getString(182), choices, 0);
            if (choice == 1) {
               return;
            }

            if (choice == 2) {
               secureEmailOptions.setPromptTruncatedMessage(false);
               factory.saveGlobalOptions(secureEmailOptions);
               return;
            }

            throw new AbortSendSecureEmailException();
         }
      }
   }

   private boolean isBESVersionAtLeast412(ServiceRecord serviceRecord) {
      if (serviceRecord != null && ITPolicyInternal.verifyITAdminService(serviceRecord.getUid(), false)) {
         String besVersion = ITPolicy.getString(21, 3);
         if (besVersion == null) {
            return false;
         }

         StringTokenizer tokenizer = (StringTokenizer)(new Object(besVersion, '.'));

         try {
            int majorVersion = Integer.parseInt(tokenizer.nextToken());
            int minorVersion = Integer.parseInt(tokenizer.nextToken());
            int patchVersion = Integer.parseInt(tokenizer.nextToken());
            if (majorVersion > 4 || majorVersion == 4 && minorVersion > 1 || majorVersion == 4 && minorVersion == 1 && patchVersion > 1) {
               return true;
            }
         } finally {
            return false;
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean encodeMessage() {
      return true;
   }

   @Override
   public long getEncodingUID() {
      return 182808770805039415L;
   }

   @Override
   public int getEncodingAction() {
      return 0;
   }

   @Override
   public Object getSendContext() {
      return this._sendContext;
   }
}
