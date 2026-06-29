package net.rim.device.apps.internal.mms.verbs;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class MMSReplyVerb extends AbstractComposeVerb {
   private boolean _replyToAll;
   private MMSMessageModel _originalMessage;

   public MMSReplyVerb(MMSMessageModel message) {
      this(message, false);
   }

   public MMSReplyVerb(MMSMessageModel message, boolean replyToAll) {
      super(replyToAll ? 603136 : 602624);
      this._replyToAll = replyToAll;
      this._originalMessage = message;
   }

   @Override
   public final Object copy() {
      return new MMSReplyVerb(this._originalMessage, this._replyToAll);
   }

   @Override
   public final String toString(Object context) {
      int msgid = this._replyToAll ? 9148 : 9147;
      return CommonResources.getString(msgid);
   }

   @Override
   public final Object doInvoke(Object context) {
      if (this._originalMessage.isInbound()) {
         this._originalMessage.perform(5803508244060051872L, null);
      }

      ContextObject contextObject = ContextObject.clone(context);
      MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
      builder.inheritDefaultReporting();
      MMSPayloadModel originalPayload = this._originalMessage.getPayload();
      if (this._replyToAll) {
         builder.addRecipients(originalPayload.getRecipients());
         builder.addCcRecipients(originalPayload.getCcRecipients());
         builder.removeSelfFromAddressList();
      }

      builder.addRecipient(originalPayload.getSender());
      String subject = originalPayload.getAttribute("subject");
      builder.setSubject(
         ((StringBuffer)(new Object())).append(EmailResources.getString(60)).append(' ').append(AbstractComposeVerb.trimSubject(subject)).toString()
      );
      String textAttachmentName = ((StringBuffer)(new Object(""))).append(System.currentTimeMillis()).append(".txt").toString();
      int textAttachmentType = 3;
      MMSPresentationModel newPresentation = PresentationModelFactory.createInstance(65536);
      newPresentation.addPresentationElement(textAttachmentName, textAttachmentType, true);
      builder.addAttachment(textAttachmentName, textAttachmentType, getInitialReplyText(this._originalMessage));
      builder.addAttachment(newPresentation);
      builder.setStatus(Integer.MAX_VALUE);
      if (super._useSmartDialing) {
         builder.enableSmartDialing();
      }

      MMSMessageModel newMessage = builder.getResult();
      contextObject.put(-7651695713744129224L, newMessage);
      return super.doInvoke(contextObject);
   }

   private static final String getInitialReplyText(MMSMessageModel message) {
      StringBuffer buf = (StringBuffer)(new Object());
      buf.append("\n");
      MMSPayloadModel payload = message.getPayload();
      buf.append(MMSResources.getString(113));
      Vector toList = payload.getRecipients();
      if (toList != null) {
         String label = MMSResources.getString(16);
         addRecipients(buf, label, toList);
      }

      Vector ccList = payload.getCcRecipients();
      if (ccList != null) {
         String label = MMSResources.getString(17);
         addRecipients(buf, label, ccList);
      }

      Vector bccList = payload.getBccRecipients();
      if (bccList != null) {
         String label = MMSResources.getString(65);
         addRecipients(buf, label, bccList);
      }

      RIMModel sender = payload.getSender();
      if (sender != null) {
         buf.append("\n");
         buf.append(MMSResources.getString(15));
         buf.append(sender.toString());
      }

      buf.append("\n");
      buf.append(MMSResources.getString(18));
      String subject = payload.getAttribute("subject");
      if (subject != null) {
         buf.append(subject);
      }

      if (!message.isForwardLocked()) {
         buf.append("\n");
         MMSUtilities.getMessageBodyText(buf, message.getAttachmentDataProvider());
      }

      return buf.toString();
   }

   private static final void addRecipients(StringBuffer buf, String label, Vector recipients) {
      int count = recipients.size();

      for (int idx = 0; idx < count; idx++) {
         buf.append('\n');
         buf.append(label);
         buf.append(recipients.elementAt(idx).toString());
      }
   }
}
