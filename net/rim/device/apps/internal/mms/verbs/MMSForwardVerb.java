package net.rim.device.apps.internal.mms.verbs;

import java.util.Enumeration;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;

public final class MMSForwardVerb extends AbstractComposeVerb {
   private MessagePartsProvider _originalMessage;
   private boolean _isForwardAsTarget;

   public MMSForwardVerb(boolean isForwardAsTarget) {
      super(602880);
      this._isForwardAsTarget = isForwardAsTarget;
   }

   public MMSForwardVerb(MessagePartsProvider message, boolean isForwardAsTarget) {
      this(isForwardAsTarget);
      this.setParameter(message);
   }

   @Override
   public final Object copy() {
      return new MMSForwardVerb(this._originalMessage, this._isForwardAsTarget);
   }

   @Override
   public final void setParameter(Object parameter) {
      super._address = null;
      if (parameter instanceof MessagePartsProvider) {
         this._originalMessage = (MessagePartsProvider)parameter;
      } else {
         super.setParameter(parameter);
      }
   }

   @Override
   public final String toString(Object context) {
      return this._isForwardAsTarget ? MMSResources.getString(121) : CommonResources.getString(9149);
   }

   @Override
   public final Object doInvoke(Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      if (this._originalMessage == null) {
         return null;
      }

      if (super._address == null) {
         this.resolveAddress();
         if (super._address == null) {
            return null;
         }
      }

      if (this._originalMessage.inbound()) {
         this._originalMessage.setRead(contextObject);
      }

      MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
      builder.inheritDefaultReporting();
      builder.addRecipient(super._address);
      String priority = null;
      String messageClass = null;
      String subject = this._originalMessage.getSubject();
      if (!(this._originalMessage instanceof MMSMessageModel)) {
         MMSPresentationModel newMessage = PresentationModelFactory.createInstance(65536);
         StringBuffer bodyText = new StringBuffer();
         String sourceName = this._originalMessage.getName();
         if (sourceName != null && sourceName.length() > 0) {
            sourceName = StringUtilities.removeChars(sourceName, "̲");
            bodyText.append('\n');
            bodyText.append("------ " + sourceName + " ------");
         }

         String fwdBody = this._originalMessage.getBody();
         if (fwdBody != null) {
            bodyText.append('\n');
            bodyText.append(fwdBody);
         }

         String bodyAttachmentName = "body.txt";
         builder.addAttachment(bodyAttachmentName, 3, bodyText.toString());
         newMessage.addPresentationElement(bodyAttachmentName, 3, true);
         builder.addAttachment(newMessage);
      } else {
         MMSMessageModel messageModel = (MMSMessageModel)this._originalMessage;
         priority = messageModel.getPayload().getAttribute("x-mms-priority");
         messageClass = messageModel.getPayload().getAttribute("x-mms-message-class");
         AttachmentDataProvider attachmentProvider = messageModel.getAttachmentDataProvider();
         MMSAttachment pduAttachment = attachmentProvider.getAttachment("net_rim_ProtocolDataUnit");
         MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(pduAttachment.getData());
         Enumeration names = pdu.attachmentNames();
         if (names != null) {
            MMSPresentationModel newPresentation = PresentationModelFactory.createInstance(65536);

            while (names.hasMoreElements()) {
               String name = (String)names.nextElement();
               MMSAttachment attachment = pdu.getAttachment(name);
               if (attachment != null) {
                  switch (attachment.getType()) {
                     case 65536:
                     default:
                        newPresentation.addPresentationElement(attachment, true);
                        builder.addAttachment(attachment);
                     case 65537:
                  }
               }
            }

            builder.addAttachment(newPresentation);
         }
      }

      if (subject != null) {
         builder.setSubject(EmailResources.getString(59) + ' ' + AbstractComposeVerb.trimSubject(subject));
      }

      if (priority != null) {
         builder.setAttribute("x-mms-priority", priority);
      }

      if (messageClass != null) {
         builder.setAttribute("x-mms-message-class", messageClass);
      }

      builder.setStatus(Integer.MAX_VALUE);
      if (super._useSmartDialing) {
         builder.enableSmartDialing();
      }

      MMSMessageModel newMessage = builder.getResult();
      contextObject.put(-7651695713744129224L, newMessage);
      contextObject.setFlag(13);
      return super.doInvoke(contextObject);
   }
}
