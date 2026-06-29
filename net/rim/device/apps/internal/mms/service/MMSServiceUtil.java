package net.rim.device.apps.internal.mms.service;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.internal.firewall.Firewall;

public final class MMSServiceUtil {
   public static final void sendMessage(MMSMessageModel message) {
      sendMessage(message, MMSTransportServiceBook.getMMSCUrl(), null, true);
   }

   public static final void sendMessage(MMSMessageModel message, String url, String authHeader, boolean cancelSendIfDeleted) {
      BackgroundTaskThread.addTask(new SendMessageTask(message, url, authHeader, cancelSendIfDeleted));
   }

   private static final boolean autoRequestMessageContent(MMSMessageModel message) {
      if (!MMSUtilities.canRequestContent(message)) {
         return false;
      }

      switch (MMSOptions.getInstance().getAutomaticRetrievalMode()) {
         case 1:
         default:
            return false;
         case 2:
            int networkService = RadioInfo.getNetworkService();
            if ((networkService & 8) != 0) {
               return false;
            }
         case 0:
            return true;
      }
   }

   public static final void requestMessageContent(MMSMessageModel message, boolean autoDownload, Runnable onCompletion, boolean retry) {
      BackgroundTaskThread.addTask(new RetrieveMessageContentTask(message, autoDownload, onCompletion, onCompletion, retry));
   }

   public static final void notifyMessageRead(MMSMessageModel message, int status) {
      String ackUrl = inferMMSCAcknowledgementUrl(message);
      BackgroundTaskThread.addTask(new SendReadNotificationTask(ackUrl, message, status));
   }

   public static final String inferMMSCAcknowledgementUrl(MMSMessageModel message) {
      return inferMMSCAcknowledgementUrl(message.getPayload().getAttribute("x-mms-content-location"));
   }

   public static final String inferMMSCAcknowledgementUrl(String contentUrl) {
      String myMMSCUrl = MMSTransportServiceBook.getMMSCUrl();
      if (MMSClientServiceBook.inferMessageAcknowledgementUrl()) {
         String newDomain = getDomainFromUrl(contentUrl);
         if (newDomain != null) {
            String myProtocol = getProtocolFromUrl(myMMSCUrl);
            if (myProtocol != null) {
               return myProtocol + newDomain + getPathFromUrl(myMMSCUrl);
            }
         }
      }

      return myMMSCUrl;
   }

   private static final String getProtocolFromUrl(String url) {
      int index = findSlash(url, 2);
      return index > 0 ? url.substring(0, index + 1) : null;
   }

   private static final String getDomainFromUrl(String url) {
      int preSlash = findSlash(url, 2);
      int postSlash = findSlash(url, 3);
      return postSlash > 0 ? url.substring(preSlash + 1, postSlash) : null;
   }

   private static final String getPathFromUrl(String url) {
      int index = findSlash(url, 3);
      return index > 0 ? url.substring(index) : "";
   }

   private static final int findSlash(String str, int n) {
      if (str != null) {
         int index = -1;

         for (int count = n; count > 0; count--) {
            index = str.indexOf(47, index + 1);
            if (index < 0) {
               return -1;
            }
         }

         return index;
      } else {
         return -1;
      }
   }

   public static final void processPDU(MMSProtocolDataUnit pdu) {
      processPDU(pdu, null);
   }

   public static final void processPDU(MMSProtocolDataUnit pdu, String transactionID) {
      switch (pdu.getType()) {
         case 128:
         case 131:
         case 133:
         case 135:
            System.out.println("Process PDU unrecognized type " + pdu.getType());
            break;
         case 129:
            System.out.println("MMS Send Confirmation msgid=" + pdu.getAttribute("message-id"));
            handleSendConfirmation(pdu, transactionID);
            break;
         case 130:
         default:
            System.out.println("MMS Notification url=" + pdu.getAttribute("x-mms-content-location"));
            handleNewMessageNotification(pdu);
            break;
         case 132:
            System.out.println("MMS Retrieve msgid=" + pdu.getAttribute("message-id"));
            break;
         case 134:
            System.out.println("MMS Delivery Confirmation msgid=" + pdu.getAttribute("message-id"));
            handleDeliveryConfirmation(pdu);
            break;
         case 136:
            System.out.println("MMS Read Confirmation msgid=" + pdu.getAttribute("message-id"));
            handleReadConfirmation(pdu);
      }

      if (MMSOptions.getInstance().getOptionFlag(512)) {
         pdu.dumpFields();
      }
   }

   public static final void handleSendConfirmation(MMSProtocolDataUnit pdu, String curTransID) {
      String transactionID = pdu.getAttribute("x-mms-transaction-id");
      if (transactionID == null) {
         transactionID = curTransID;
      }

      MMSMessageModel message = MMSStorage.findMessageByTransactionID(transactionID);
      if (message == null) {
         System.out.println("MMS - send confirmation ignored.");
      } else {
         MMSMessageModelBuilder builder = new MMSMessageModelBuilder(message);
         String messageID = pdu.getAttribute("message-id");
         if (messageID != null) {
            builder.setAttribute("message-id", messageID);
         }

         String responseStatus = pdu.getAttribute("x-mms-response-status");
         if (responseStatus != null) {
            builder.setAttribute("x-mms-response-status", responseStatus);
         }

         String responseText = pdu.getAttribute("x-mms-response-text");
         if (responseText != null) {
            builder.setAttribute("x-mms-response-text", responseText);
         }

         builder.commitResult();
      }
   }

   public static final void handleDeliveryConfirmation(MMSProtocolDataUnit pdu) {
      String messageID = pdu.getAttribute("message-id");
      MMSMessageModel message = MMSStorage.findMessageByMessageID(949632297110531729L, messageID);
      if (message == null) {
         System.out.println("MMS - delivery confirmation ignored.");
      } else {
         message.perform(-8071174053402202672L, pdu);
      }
   }

   public static final void handleReadConfirmation(MMSProtocolDataUnit pdu) {
      String messageID = pdu.getAttribute("message-id");
      MMSMessageModel message = MMSStorage.findMessageByMessageID(949632297110531729L, messageID);
      if (message == null) {
         System.out.println("MMS - read confirmation ignored.");
      } else {
         message.perform(-1919303899965957599L, pdu);
      }
   }

   public static final void handleNewMessageNotification(MMSProtocolDataUnit pdu) {
      String id = pdu.getAttribute("x-mms-transaction-id");
      Firewall fw = Firewall.getInstance();
      String ackUrl = inferMMSCAcknowledgementUrl(pdu.getAttribute("x-mms-content-location"));
      if (fw.isBlockingEnabled((byte)2)) {
         fw.incrementBlockedCount((byte)2);
         System.out.println("MMS Blocked");
         BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 130));
      } else {
         boolean filterAds;
         if (MMSClientServiceBook.isLockedOption(8)) {
            if ((MMSClientServiceBook.getDefaultOptionFlags() & 8) != 0) {
               filterAds = true;
            } else {
               filterAds = false;
            }
         } else {
            filterAds = MMSOptions.getInstance().getOptionFlag(8);
         }

         if (filterAds) {
            int messageClass = MMSUtilities.parseInt(pdu.getAttribute("x-mms-message-class"), 0);
            if (messageClass == 129) {
               System.out.println("MMS Rejected - advertisement");
               BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 130));
               return;
            }
         }

         boolean filterAnon;
         if (MMSClientServiceBook.isLockedOption(4)) {
            if ((MMSClientServiceBook.getDefaultOptionFlags() & 4) != 0) {
               filterAnon = true;
            } else {
               filterAnon = false;
            }
         } else {
            filterAnon = MMSOptions.getInstance().getOptionFlag(4);
         }

         if (filterAnon) {
            String fromName = pdu.getAttribute("from");
            if (fromName == null || fromName.length() == 0) {
               System.out.println("MMS Rejected - anonymous");
               BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 130));
               return;
            }
         }

         String transactionID = pdu.getAttribute("x-mms-transaction-id");
         if (MMSStorage.findMessageByTransactionID(transactionID) != null) {
            System.out.println("MMS Rejected - duplicate");
            BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 131));
         } else {
            MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
            builder.setPendingReadNotification();
            builder.setUnopened();
            builder.setNew();
            builder.update(pdu);
            MMSMessageModel message = builder.getResult();
            Object ticket = PersistentContent.getTicket();
            if (ticket != null && autoRequestMessageContent(message)) {
               requestMessageContent(message, true, new StoreAndNotifyRunnable(message), true);
            } else {
               StoreAndNotifyRunnable.run(message);
               BackgroundTaskThread.addTask(new SendNotificationResponseTask(ackUrl, id, 131));
            }
         }
      }
   }

   static final void updateMessageStatus(MMSMessageModel message, int status) {
      updateMessageStatus(message, status, 0, 0, 0, 0);
   }

   static final void updateMessageStatus(
      MMSMessageModel message, int status, int httpErrorCode, int mmsResponseCode, int wapIOExceptionError, int wapIOExceptionAdditionalData
   ) {
      int[] statusBuffer = new int[]{status, httpErrorCode, mmsResponseCode, wapIOExceptionError, wapIOExceptionAdditionalData};
      message.perform(-3923698019885371449L, statusBuffer);
   }

   private static final boolean containsGroups(Vector v) {
      for (int idx = 0; idx < v.size(); idx++) {
         if (v.elementAt(idx) instanceof GroupAddressCardModel) {
            return true;
         }
      }

      return false;
   }

   public static final Vector expandRecipientList(Vector v) {
      if (v == null) {
         return null;
      }

      if (!containsGroups(v)) {
         return v;
      }

      Vector expanded = new Vector();

      for (int idx = 0; idx < v.size(); idx++) {
         Object o = v.elementAt(idx);
         if (o != null) {
            if (!(o instanceof GroupAddressCardModel)) {
               expanded.addElement(o);
            } else {
               GroupAddressCardModel gacm = (GroupAddressCardModel)o;
               int size = gacm.size();

               for (int i = 0; i < size; i++) {
                  byte type = gacm.getAddressModelTypeAt(i);
                  if (type == 0 || type == 2) {
                     Object adr = gacm.getAddressModelAt(i);
                     if (adr != null) {
                        expanded.addElement(adr);
                     }
                  }
               }
            }
         }
      }

      return expanded;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] buildSendRequestPdu(MMSPayloadModel payload, AttachmentDataProvider attachmentProvider, ContextObject context) {
      DataBuffer dataBuffer = new DataBuffer();

      label183:
      try {
         MMSProtocolDataUnitWriter writer = new MMSProtocolDataUnitWriter(dataBuffer, 128);
         writer.writeTransactionID(Long.toString(payload.getCreationDate()));
         writer.writeMMSVersion();
         writer.writeSubject(payload.getAttribute("subject"));
         writer.writeFrom(context);
         int recipientByteSize = 0;
         int recipientCount = 0;
         Vector recipients = expandRecipientList(payload.getRecipients());
         if (recipients != null) {
            int count = recipients.size();

            for (int idx = 0; idx < count; idx++) {
               recipientByteSize += writer.writeTo(MMSUtilities.addressToString(recipients.elementAt(idx), context));
               recipientCount++;
            }
         }

         recipients = expandRecipientList(payload.getCcRecipients());
         if (recipients != null) {
            int count = recipients.size();

            for (int idx = 0; idx < count; idx++) {
               recipientByteSize += writer.writeCc(MMSUtilities.addressToString(recipients.elementAt(idx), context));
               recipientCount++;
            }
         }

         recipients = expandRecipientList(payload.getBccRecipients());
         if (recipients != null) {
            int count = recipients.size();

            for (int idx = 0; idx < count; idx++) {
               recipientByteSize += writer.writeBcc(MMSUtilities.addressToString(recipients.elementAt(idx), context));
               recipientCount++;
            }
         }

         if (recipientByteSize > MMSClientServiceBook.getMaxRecipientByteSize() || recipientCount > MMSClientServiceBook.getMaxRecipientCount()) {
            switch (MMSClientServiceBook.getRestrictedSendMode()) {
               case 0:
                  break;
               case 1:
               default:
                  Dialog.alert(MMSResources.getString(101));
                  return null;
               case 2:
                  if (Dialog.ask(3, MMSResources.getString(102)) == -1) {
                     return null;
                  }
            }
         }

         int priority = MMSUtilities.parseInt(payload.getAttribute("x-mms-priority"), 129);
         if (priority != 129) {
            writer.writePriority(priority);
         }

         boolean requestReadReport = MMSUtilities.parseInt(payload.getAttribute("x-mms-read-report"), 129) == 128;
         if (requestReadReport) {
            writer.writeReadReportRequested(128);
         }

         boolean requestDeliveryReport = MMSUtilities.parseInt(payload.getAttribute("x-mms-delivery-report"), 129) == 128;
         if (requestDeliveryReport) {
            writer.writeDeliveryReportRequested(128);
         }

         int messageClass = MMSUtilities.parseInt(payload.getAttribute("x-mms-message-class"), 128);
         if (messageClass != 128) {
            writer.writeMessageClass(messageClass);
         }

         if (context.getPrivateFlag(3826502739478037178L, 0)) {
            Vector v = getAttachmentsForSend(attachmentProvider);
            int attachmentCount = v.size();
            String contentType = payload.getAttribute("content-type");
            writer.writeContentType(contentType);
            writer.writeByte(attachmentCount);

            for (int idx = 0; idx < attachmentCount; idx++) {
               writer.addAttachment((MMSAttachment)v.elementAt(idx));
            }

            writer.endContent();
         } else {
            writeContent(writer, payload, attachmentProvider);
         }
      } catch (Throwable var17) {
         System.out.println("buildPDU " + e.toString());
         break label183;
      }

      if (dataBuffer.getLength() > MMSClientServiceBook.getMaxMessageSize()) {
         switch (MMSClientServiceBook.getRestrictedSizeMode()) {
            case 0:
               break;
            case 1:
            default:
               Dialog.alert(MMSResources.getString(103));
               return null;
            case 2:
               if (Dialog.ask(3, MMSResources.getString(104)) == -1) {
                  return null;
               }
         }
      }

      return dataBuffer.toArray();
   }

   private static final void writeContent(MMSProtocolDataUnitWriter writer, MMSPayloadModel payload, AttachmentDataProvider attachmentProvider) {
      MMSAttachment pduAttachment = attachmentProvider.getAttachment("net_rim_ProtocolDataUnit");
      if (pduAttachment != null) {
         writer.copyContent(pduAttachment);
      } else {
         MMSAttachment templateAttachment = attachmentProvider.getAttachment("net_rim_Template");
         if (templateAttachment != null) {
            MMSProtocolDataUnit pdu = new MMSProtocolDataUnit(templateAttachment.getData());
            MMSAttachment textAttachment = attachmentProvider.getAttachment("net_rim_TextEntry");
            writeTemplateContent(writer, pdu, textAttachment);
         } else {
            Vector v = getAttachmentsForSend(attachmentProvider);
            int attachmentCount = v.size();
            boolean simpleContent = attachmentCount == 1
               && MMSClientServiceBook.sendTextAsSimpleContent()
               && MMSUtilities.isTextType(((MMSAttachment)v.elementAt(0)).getType());
            if (simpleContent) {
               writer.beginContent((MMSAttachment)v.elementAt(0));
               writer.endContent();
            } else {
               MMSAttachment smilAttachment = SMILAttachmentBuilder.convert(payload.getPresentationModel(), attachmentProvider);
               if (smilAttachment != null) {
                  String smilName = smilAttachment.getName();
                  String smilType = MMSUtilities.getMIMETypeString(smilAttachment.getType());
                  writer.beginContent(attachmentCount + 1, smilName, smilType);
                  writer.addAttachment(smilAttachment);
               } else {
                  writer.beginContent(attachmentCount);
               }

               for (int idx = 0; idx < attachmentCount; idx++) {
                  writer.addAttachment((MMSAttachment)v.elementAt(idx));
               }

               writer.endContent();
            }
         }
      }
   }

   private static final Vector getAttachmentsForSend(AttachmentDataProvider attachmentProvider) {
      Vector v = new Vector();
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            MMSAttachment attachment = attachmentProvider.getAttachment(name);
            if (attachment != null) {
               int type = attachment.getType();
               if ((!MMSUtilities.isTextType(type) || attachment.getDataSize() != 0) && (type < 128 || MMSUtilities.getMIMETypeString(type) != null)) {
                  v.addElement(attachment);
               }
            }
         }
      }

      return v;
   }

   private static final void writeTemplateContent(MMSProtocolDataUnitWriter writer, MMSProtocolDataUnit pdu, MMSAttachment userText) {
      Enumeration names = pdu.attachmentNames();
      int attachmentCount = 0;
      int rootType = 0;
      String rootName = null;

      while (names.hasMoreElements()) {
         String name = (String)names.nextElement();
         MMSAttachment attachment = pdu.getAttachment(name);
         if (attachment != null) {
            attachmentCount++;
            if (attachment.getType() == 65537) {
               rootType = attachment.getType();
               rootName = attachment.getName();
            }
         }
      }

      if (rootName != null) {
         writer.beginContent(attachmentCount, rootName, MMSUtilities.getMIMETypeString(rootType));
      } else {
         writer.beginContent(attachmentCount);
      }

      names = pdu.attachmentNames();

      while (names.hasMoreElements()) {
         String name = (String)names.nextElement();
         MMSAttachment attachment = pdu.getAttachment(name);
         if (attachment != null) {
            if (userText != null && MMSUtilities.isTextType(attachment.getType())) {
               MMSAttachment newTextAttachment = new MMSAttachmentImpl(attachment.getName(), userText.getType(), userText.getData(), userText.getCharset());
               writer.addAttachment(newTextAttachment);
               userText = null;
            } else {
               writer.addAttachment(attachment);
            }
         }
      }

      writer.endContent();
   }
}
