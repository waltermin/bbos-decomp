package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreRequest;
import net.rim.device.apps.api.ui.PopupStatus;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.NNEPasswordManager;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.transmission.TransmissionHelper;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;

public class EmailMoreVerb extends Verb {
   private EmailMessageModel _emailMessageModel;
   private byte _moreType;
   private byte[] _contentType;
   private byte _requestTarget;
   public MorePartModel _morePartModel;
   private static final int MORE_REQUEST_SIZE = 3000;
   public static final byte MORE = 1;
   public static final byte MORE_ALL = 2;
   public static final byte AUTO_MORE = 3;
   public static final byte AUTO_MORE_ALL = 4;
   public static final byte MORE_BODY_REQUEST = 1;
   public static final byte MORE_DOWNLOAD_REQUEST = 2;
   public static final byte MORE_ATTACHMENT_REQUEST = 3;

   public EmailMoreVerb(byte moreType) {
      super(moreType != 2 && moreType != 4 ? 344064 : 344069, EmailResources.getResourceBundle(), moreType != 2 && moreType != 4 ? 80 : 143);
      this._moreType = moreType;
   }

   public EmailMoreVerb(EmailMessageModel message, byte moreType) {
      this(moreType);
      this._emailMessageModel = message;
   }

   public EmailMoreVerb(MorePartModel morePartModel, byte moreType) {
      this(morePartModel, moreType, null);
   }

   public EmailMoreVerb(MorePartModel morePartModel, byte moreType, byte[] contentType) {
      this(moreType);
      this._morePartModel = morePartModel;
      this._contentType = contentType;
   }

   public static MorePartModel findBodyMorePartModel(EmailMessageModel model) {
      BodyModel bodyModel = model.getBodyModel();
      return !(bodyModel instanceof MorePartModel) ? null : (MorePartModel)bodyModel;
   }

   public static MorePartModel findMorePartByIdentifier(ReadableList model, int partIdentifier) {
      MorePartModel morePartModel = null;

      for (int i = model.size() - 1; i >= 0; i--) {
         Object element = model.getAt(i);
         if (element instanceof ProxyModel) {
            element = ((ProxyModel)element).getObject();
         }

         if (element instanceof MorePartModel) {
            morePartModel = (MorePartModel)element;
            if (morePartModel.getMorePartID() == partIdentifier) {
               return morePartModel;
            }
         }
      }

      return null;
   }

   public void setMoreRequestTarget(byte targetType) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public Object invoke(Object context) {
      if (this._moreType != 3 && this._moreType != 4) {
         EventLogger.logEvent(-1237457833540244999L, 1632457297, 0);
      } else {
         EventLogger.logEvent(-1237457833540244999L, 1631670865, 0);
      }

      EmailMessageModelImpl messageImpl = (EmailMessageModelImpl)this._emailMessageModel;
      if (messageImpl != null && messageImpl.getTransmissionError() == 49 && this._moreType != 3 && this._moreType != 4) {
         PopupStatus.show(EmailResources.getString(1003), 1000);
         EventLogger.logEvent(-1237457833540244999L, 1632522061, 3);
         return null;
      }

      EmailMessageModel message = this._emailMessageModel;
      if (message == null) {
         message = (EmailMessageModel)ContextObject.get(context, 246);
         if (message == null) {
            Object model = ContextObject.get(context, 254);
            if (model instanceof EmailMessageModel) {
               message = (EmailMessageModel)model;
            }
         }

         if (message == null) {
            EventLogger.logEvent(-1237457833540244999L, 1632456257, 3);
            return null;
         }
      }

      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
      if (serviceRecord == null) {
         Dialog.alert(EmailResources.getString(62));
         return null;
      }

      Object nnePassword = null;
      if (NNEPasswordManager.isPasswordRequired(message)) {
         nnePassword = NNEPasswordManager.confirmEncodedPassword(message);
         if (nnePassword == null) {
            EventLogger.logEvent(-1237457833540244999L, 1632655172, 3);
            return null;
         }
      }

      MorePartModel morePartModel = this._morePartModel;
      if (morePartModel == null) {
         RIMModel model = (RIMModel)ContextObject.get(context, 254);
         if (model instanceof MorePartModel) {
            morePartModel = (MorePartModel)model;
         }

         if (morePartModel == null) {
            morePartModel = findBodyMorePartModel(message);
         }

         if (morePartModel == null) {
            EventLogger.logEvent(-1237457833540244999L, 1632522317, 3);
            return null;
         }
      }

      if ((this._moreType == 2 || this._moreType == 4) && !isMoreAllAllowed(message)) {
         EventLogger.logEvent(-1237457833540244999L, 1632521537, 3);
         return null;
      }

      if (!EmailMessageUtilities.canSendEmail()) {
         EventLogger.logEvent(-1237457833540244999L, 1631931969, 3);
         return null;
      }

      if (!morePartModel.isMoreAvailable()) {
         EventLogger.logEvent(-1237457833540244999L, 1632521552, 3);
         return null;
      }

      if (this._moreType != 3 && this._moreType != 4) {
         message.clearFlags(65536);
      } else {
         message.setFlags(65536);
      }

      int identifier = message.getCMIMEReferenceIdentifier();
      MessageLookups.put(-4420850319371185992L, identifier, message);
      RIMMessagingMoreRequest more = new RIMMessagingMoreRequest();
      more.setReferenceIdentifier(identifier);
      int morePartIdentifier = morePartModel.getMorePartID();
      more.setPartIdentifier(morePartIdentifier);
      int lengthOnDevice = morePartModel.getLengthOnDevice();
      more.setOffset(lengthOnDevice < 0 ? 0 : lengthOnDevice);
      more.setContentType(this._contentType);
      String descriptor = (String)ContextObject.get(context, 253);
      if (descriptor != null && descriptor.length() > 0) {
         more.setObjectDescriptor(descriptor.getBytes());
      }

      ContextObject contextObject = ContextObject.clone(context);
      ContextObject.put(contextObject, -6095803566992128485L, serviceRecord);
      EmailPayloadModel oldPayload = EmailModifier.beginChanges(message, contextObject);
      morePartModel = findMorePartByIdentifier(message, morePartIdentifier);
      morePartModel.setMoreRequestSent();
      ContextObject.setFlag(contextObject, 137);
      EmailModifier.endChanges(message, oldPayload, contextObject);
      more.setLength(this._moreType != 2 && this._moreType != 4 ? 3000 : -1);
      String decodedPassword = null;

      label147:
      try {
         decodedPassword = PersistentContent.decodeString(nnePassword);
      } finally {
         break label147;
      }

      more.setNNEPassword(decodedPassword);
      TransmissionHelper.getInstance()
         .transmitObject(
            new EmailMoreVerb$EmailMoreRequestTransmissionWrapper(
               message,
               morePartIdentifier,
               "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MORE",
               more,
               contextObject,
               UiApplication.getUiApplication(),
               this._requestTarget
            )
         );
      return null;
   }

   public static boolean isMoreAllAllowed(EmailMessageModel message) {
      if (message.flagsSet(8192)) {
         return false;
      }

      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
      return serviceRecord == null ? false : CMIMEUtilities.getMoreAllAllowed(serviceRecord);
   }
}
