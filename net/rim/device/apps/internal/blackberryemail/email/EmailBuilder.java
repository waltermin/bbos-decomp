package net.rim.device.apps.internal.blackberryemail.email;

import java.io.InputStream;
import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.messagelist.MessageAttachment;
import net.rim.device.apps.api.messaging.messagelist.MessagePartsProvider;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModelFactory;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public final class EmailBuilder {
   private static String[][] _arrayOfStringPairs = new String[1][];

   static final void addSubjectLine(EmailMessageModel msg, Object context) {
      PersistableRIMModel subject = (PersistableRIMModel)FactoryUtil.createInstance(3928489455534245796L, context);
      msg.add(subject);
   }

   static final void addOriginalMessage(EmailMessageModel msg, Object context) {
      Object originalMessage = ContextObject.get(context, 245);
      if (!(originalMessage instanceof EmailMessageModelImpl)) {
         if (!(originalMessage instanceof MessagePartsProvider)) {
            throw new ClassCastException("Only RIM EmailModel implementations are currently supported");
         }

         MessagePartsProvider m = (MessagePartsProvider)originalMessage;
         StringBuffer body = new StringBuffer();
         if (m.allowDescriptiveForwardHeader()) {
            String sourceName = StringUtilities.removeChars(m.getName(), "̲");
            if (sourceName.length() > 0) {
               body.append('\n');
               addLine(body, "------ " + sourceName + " ------", null);
            }

            String sender = m.getSender();
            if (sender != null && sender.length() > 0) {
               addLine(body, EmailResources.getString(54), sender);
            }

            String[] recipients = m.getRecipients();
            if (recipients != null && recipients.length > 0) {
               for (int i = recipients.length - 1; i > -1; i--) {
                  String recipient = recipients[i];
                  if (recipient != null && recipient.length() > 0) {
                     addLine(body, EmailResources.getString(51), recipient);
                  }
               }
            }

            long sentDate = m.getSentDate();
            if (sentDate != 0) {
               addLine(body, CommonResources.getString(2001), DateFormat.getInstance(54).formatLocal(sentDate));
            }

            String subject = m.getSubject();
            if (subject != null && subject.length() > 0) {
               addLine(body, EmailResources.getString(58), subject);
            }

            body.append('\n');
         }

         String originalMessageBody = m.getBody();
         if (originalMessageBody != null && originalMessageBody.length() > 0) {
            body.append(originalMessageBody);
         }

         if (body.length() > 0) {
            EmailBuilderApi.addMessageBody(msg, body.toString());
         }

         if (!ContextObject.getFlag(context, 94)) {
            MessageAttachment[] attachments = m.getAttachments();
            long totalAttachmentSize = 0;
            long attachmentSize = 0;
            String filePath = null;
            byte[] dataInByte = null;
            if (attachments != null) {
               for (short i = 0; i < attachments.length; i++) {
                  attachmentSize = Long.parseLong(attachments[i].getProperty("Content-Length"));
                  totalAttachmentSize += attachmentSize;
                  if (totalAttachmentSize > NativeAttachmentRequestProcessor$Helper.getInMemoryAttachmentsTotalMaxSize()) {
                     totalAttachmentSize -= attachmentSize;
                     return;
                  }

                  LargeAttachmentModel$LargeCachedAttachmentModel attachmentModel = null;
                  attachmentModel = new LargeAttachmentModel$LargeCachedAttachmentModel();
                  attachmentModel.setContentType(attachments[i].getProperty("Content-Type"));
                  filePath = attachments[i].getProperty("Content-Location");
                  attachmentModel.setDisplayName(filePath);
                  attachmentModel.setFile(filePath);
                  attachmentModel.setFileSize(attachmentSize);
                  Object attachmentData = attachments[i].getData();
                  if (!(attachmentData instanceof byte[])) {
                     if (attachmentData instanceof DataBuffer) {
                        label133:
                        try {
                           dataInByte = ((DataBuffer)attachmentData).readByteArray();
                        } finally {
                           break label133;
                        }
                     }
                  } else {
                     dataInByte = (byte[])attachmentData;
                  }

                  attachmentModel.setData(dataInByte);
                  msg.add(attachmentModel);
               }
            }
         }
      } else {
         EmailPayloadModel payloadModel = ((EmailMessageModelImpl)originalMessage)._payload;
         msg.add(payloadModel);
      }
   }

   private static final void addLine(StringBuffer sb, String label, String text) {
      sb.append(label);
      if (text != null) {
         sb.append(text);
      }

      sb.append('\n');
   }

   static final void addOriginalMessageReferenceIdentifierStub(EmailMessageModel msg, Object context) {
      RIMModel originalMessage = (RIMModel)ContextObject.get(context, 245);
      int messageId;
      if (!(originalMessage instanceof EmailMessageModel)) {
         if (!(originalMessage instanceof CMIMEReferenceIdProvider)) {
            return;
         }

         CMIMEReferenceIdProvider c = (CMIMEReferenceIdProvider)originalMessage;
         messageId = c.getCMIMEReferenceIdentifier();
      } else {
         messageId = ((EmailMessageModel)originalMessage).getCMIMEReferenceIdentifier();
      }

      EmailBuilderApi.addOriginalMessageReferenceIdentifierStub(msg, messageId);
   }

   public static final boolean makeReply(EmailMessageModel msg, Object context, boolean reply_all, boolean with_text) {
      setFolderBasedOnOriginalMessage(msg, context);
      if (reply_all) {
         if (!populateWithReplyToAllAddressees(context, msg)) {
            return false;
         }
      } else if (!populateWithReplyAddressee(context, msg)) {
         return false;
      }

      addSubjectLine(msg, context);
      if (with_text) {
         addOriginalMessage(msg, context);
         return true;
      } else {
         addOriginalMessageReferenceIdentifierStub(msg, context);
         return true;
      }
   }

   static final void setFolderBasedOnOriginalMessage(EmailMessageModel newMessage, Object context) {
      MessagePartsProvider originalMessage = (MessagePartsProvider)ContextObject.get(context, 245);
      if (originalMessage instanceof EmailMessageModel) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(((EmailMessageModel)originalMessage).getFolderId());
         long outbox = hierarchy.getSentFolder();
         newMessage.setFolderId(outbox);
      }
   }

   static final void setPriorityBasedOnOriginalMessage(EmailMessageModel newMessage, Object context) {
      MessagePartsProvider originalMessage = (MessagePartsProvider)ContextObject.get(context, 245);
      if (originalMessage instanceof EmailMessageModel) {
         newMessage.setPriority(((EmailMessageModel)originalMessage).getPriority());
      }
   }

   private static final void setSensitivityAndEncodingHintsBasedOnOriginalMessage(EmailMessageModel newMessage, Object context) {
      MessagePartsProvider originalMessage = (MessagePartsProvider)ContextObject.get(context, 245);
      if (originalMessage instanceof EmailMessageModel) {
         EmailMessageModel originalMessageModel = (EmailMessageModel)originalMessage;
         newMessage.setSensitivity(originalMessageModel.getSensitivity());
         newMessage.setEncoding(CMIMEUtilities.replaceHints(newMessage.getEncoding(), originalMessageModel.getEncoding()));
      }
   }

   public static final EmailMessageModelImpl buildMessage(Object context) {
      ContextObject creationContext;
      if (ContextObject.getFlag(context, 38)) {
         creationContext = ContextObject.clone(context);
         creationContext.clearFlag(38);
      } else {
         creationContext = ContextObject.castOrCreate(context);
      }

      EmailMessageModelImpl newMessage = new EmailMessageModelImpl(creationContext);
      newMessage.setEncoding(CMIMEUtilities.resolveEncoding(CMIMEUtilities.getEncodings()));
      if (ContextObject.getFlag(creationContext, 31)) {
         newMessage.setType((byte)32);
         if (createAutoDropHeaderModel(newMessage, creationContext) == null) {
            return null;
         }

         addSubjectLine(newMessage, creationContext);
         PersistableRIMModel body = (PersistableRIMModel)FactoryUtil.createInstance(5987399499453925075L, creationContext);
         newMessage.add(body);
         createAndAttachMessageAttachment(newMessage, creationContext);
         return newMessage;
      } else {
         setSensitivityAndEncodingHintsBasedOnOriginalMessage(newMessage, creationContext);
         if (ContextObject.getFlag(creationContext, 12)) {
            newMessage.setType((byte)1);
            if (!makeReply(newMessage, creationContext, false, false)) {
               return null;
            }
         } else if (ContextObject.getFlag(creationContext, 53)) {
            newMessage.setType((byte)2);
            if (!makeReply(newMessage, creationContext, false, true)) {
               return null;
            }
         } else if (ContextObject.getFlag(creationContext, 29)) {
            newMessage.setType((byte)4);
            if (!makeReply(newMessage, creationContext, true, false)) {
               return null;
            }
         } else if (ContextObject.getFlag(creationContext, 30)) {
            newMessage.setType((byte)8);
            if (!makeReply(newMessage, creationContext, true, true)) {
               return null;
            }
         } else if (ContextObject.getFlag(creationContext, 13)) {
            setPriorityBasedOnOriginalMessage(newMessage, creationContext);
            newMessage.setType((byte)16);
            EmailHeaderModel headerModel = createAutoDropHeaderModel(newMessage, creationContext);
            if (headerModel == null) {
               return null;
            }

            Object originalMessage = ContextObject.get(context, 245);
            if (originalMessage instanceof EmailMessageModelImpl) {
               int originalMessageWasPIN = ((EmailMessageModelImpl)originalMessage).getFlags() & 8192;
               if ((newMessage.getFlags() & 8192) == originalMessageWasPIN) {
                  setFolderBasedOnOriginalMessage(newMessage, creationContext);
               }
            }

            addSubjectLine(newMessage, creationContext);
            addOriginalMessage(newMessage, creationContext);
         }

         return newMessage;
      }
   }

   public static final boolean modelIsAGroupWithAllInvalidAddresses(Object obj, boolean isPIN) {
      if (!(obj instanceof GroupAddressCardModel)) {
         return false;
      }

      GroupAddressCardModel group = (GroupAddressCardModel)obj;
      int size = group.size();
      boolean badAddressFound = false;
      boolean goodAddressFound = false;

      for (int i = 0; i < size; i++) {
         byte addressType = group.getAddressModelTypeAt(i);
         if (!isPIN && addressType == 0 || isPIN && addressType == 1) {
            if (group.getAddressModelAt(i) != null) {
               goodAddressFound = true;
            } else {
               badAddressFound = true;
            }
         } else {
            badAddressFound = true;
         }
      }

      if (goodAddressFound) {
         if (badAddressFound) {
            group.warnUserSomeAddressesCannotReceive(isPIN ? EmailResources.getString(34) : EmailResources.getString(187));
         }

         return false;
      } else {
         return true;
      }
   }

   private static final EmailHeaderModel createAutoDropHeaderModel(EmailMessageModel message, Object context) {
      EmailHeaderModel header = null;
      if (message.getType() == 32) {
         RIMModel existingModel = (RIMModel)ContextObject.get(context, 254);
         if (existingModel != null && !modelIsAGroupWithAllInvalidAddresses(existingModel, message.flagsSet(8192))) {
            EmailHeaderModel to = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, context);
            message.add(to);
            header = to;
         }
      }

      ContextObject clone = ((ContextObject)context).clone();
      clone.clearFlag(5);
      clone.remove(-4055106280780392421L);
      clone.put(254, EmailHeaderModel.createBlankFreeFormAddress(context));
      clone.putIntegerData(0);
      EmailHeaderModel to = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, clone);
      message.add(to);
      if (header == null) {
         header = to;
      }

      clone.putIntegerData(1);
      clone.put(254, EmailHeaderModel.createBlankFreeFormAddress(context));
      EmailHeaderModel cc = (EmailHeaderModel)FactoryUtil.createInstance(-8034039608019345282L, clone);
      message.add(cc);
      return header;
   }

   private static final boolean populateWithReplyAddressee(Object context, EmailMessageModel newMessage) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      Object existingMessageSubmembers = contextObject.get(245);
      if (!(existingMessageSubmembers instanceof ReadableList)) {
         return false;
      }

      Vector replyToAddresses = getReplyToAddresses((ReadableList)existingMessageSubmembers);
      return populate(contextObject, newMessage, replyToAddresses);
   }

   private static final boolean populateWithReplyToAllAddressees(Object context, EmailMessageModel newMessage) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      Object existingMessageSubmembers = contextObject.get(245);
      if (existingMessageSubmembers instanceof ReadableList) {
         String deviceAddress = getDeviceAddress(newMessage);
         Vector replyToAllAddresses = getReplyToAllAddresses((ReadableList)existingMessageSubmembers, deviceAddress);
         return populate(contextObject, newMessage, replyToAllAddresses);
      } else {
         return false;
      }
   }

   private static final boolean populate(Object context, EmailMessageModel newMessage, Vector replyAddresses) {
      ContextObject temp = ContextObject.castOrCreate(context);
      ContextObject contextObject = temp.clone();
      EmailHeaderModel replyAddressee = null;
      int headerCount = 0;
      Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(-8034039608019345282L);
      int size = replyAddresses.size();

      for (int i = 0; i < size; i++) {
         Object o = replyAddresses.elementAt(i);
         if (o instanceof EmailHeaderModel) {
            EmailHeaderModel header = (EmailHeaderModel)o;
            RIMModel insideModel = header.getInsideModel();
            if (insideModel instanceof FriendlyNameAddressModel) {
               FriendlyNameAddressModel addressModel = (FriendlyNameAddressModel)insideModel;
               String addressString = addressModel.getAddress();
               if (addressExistsInMessage(newMessage, addressString)) {
                  continue;
               }
            }

            RIMModel addressBookEntry = header.getAddressBookEntry();
            if (addressBookEntry != null) {
               contextObject.put(-4055106280780392421L, addressBookEntry);
            } else {
               contextObject.remove(-4055106280780392421L);
            }

            contextObject.put(254, insideModel);
            int header_type = header.getHeaderType();
            switch (header_type) {
               case -1:
                  break;
               case 0:
               case 3:
               case 4:
               case 5:
               default:
                  header_type = 0;
               case 1:
               case 2:
                  contextObject.put(-4054673099568009991L, HeaderTypes._typesAsInteger[header_type]);
                  replyAddressee = (EmailHeaderModel)factory.createInstance(contextObject);
                  newMessage.add(replyAddressee);
                  headerCount++;
            }
         }
      }

      return createAutoDropHeaderModel(newMessage, context) != null || headerCount > 0;
   }

   static final boolean addressExistsInMessage(EmailMessageModel message, String addressToLookFor) {
      int size = message.size();

      for (int i = 0; i < size; i++) {
         Object o = message.getAt(i);
         if (o instanceof EmailHeaderModel) {
            EmailHeaderModel header = (EmailHeaderModel)o;
            int headerType = header.getHeaderType();
            if (headerType == 0 || headerType == 1 || headerType == 2) {
               RIMModel model = header.getInsideModel();
               if (model instanceof FriendlyNameAddressModel) {
                  FriendlyNameAddressModel addressModel = (FriendlyNameAddressModel)model;
                  String addressString = addressModel.getAddress();
                  if (StringUtilities.compareToIgnoreCase(addressString, addressToLookFor, 1701707776) == 0) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static final String getDeviceAddress(EmailMessageModel message) {
      String deviceAddress = null;
      if (message.flagsSet(8192)) {
         return EmailSendUtility.getDevicePINString();
      }

      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
      if (serviceRecord != null) {
         deviceAddress = CMIMEUtilities.getEmailAddress(serviceRecord.getId());
      }

      if (deviceAddress == null) {
         deviceAddress = CMIMEUtilities.getEmailAddress();
      }

      return deviceAddress;
   }

   private static final Vector getReplyToAddresses(ReadableList list) {
      Vector replyAddresses = new Vector();
      Object fromModel = null;
      Object senderModel = null;
      int size = list.size();

      for (int idx = 0; idx < size; idx++) {
         Object o = list.getAt(idx);
         if (o instanceof EmailHeaderModel) {
            EmailHeaderModel header = (EmailHeaderModel)o;
            switch (header.getHeaderType()) {
               case 2:
                  break;
               case 3:
                  if (fromModel == null) {
                     fromModel = o;
                  }
                  break;
               case 4:
                  if (senderModel == null) {
                     senderModel = o;
                  }
                  break;
               case 5:
               default:
                  replyAddresses.addElement(o);
            }
         }
      }

      if (replyAddresses.size() == 0) {
         if (fromModel != null) {
            replyAddresses.addElement(fromModel);
            return replyAddresses;
         }

         if (senderModel != null) {
            replyAddresses.addElement(senderModel);
         }
      }

      return replyAddresses;
   }

   private static final Vector getReplyToAllAddresses(ReadableList list, String deviceAddress) {
      Vector replyAddresses = new Vector();
      boolean hasReplyTo = false;
      int size = list.size();

      for (int idx = 0; idx < size; idx++) {
         Object o = list.getAt(idx);
         if (o instanceof EmailHeaderModel) {
            EmailHeaderModel header = (EmailHeaderModel)o;
            switch (header.getHeaderType()) {
               case 2:
                  break;
               case 3:
               case 4:
                  if (!hasReplyTo) {
                     if (deviceAddress != null) {
                        String headerAddress = header.getInsideModel().toString();
                        if (headerAddress != null && 0 == StringUtilities.compareToIgnoreCase(headerAddress, deviceAddress, 1701707776)) {
                           continue;
                        }
                     }

                     replyAddresses.addElement(o);
                  }
                  break;
               case 5:
               default:
                  if (!hasReplyTo) {
                     replyAddresses.removeAllElements();
                     hasReplyTo = true;
                  }

                  replyAddresses.addElement(o);
            }
         }
      }

      for (int idx = 0; idx < size; idx++) {
         Object o = list.getAt(idx);
         if (o instanceof EmailHeaderModel) {
            EmailHeaderModel header = (EmailHeaderModel)o;
            switch (header.getHeaderType()) {
               case -1:
                  break;
               case 0:
               case 1:
               case 2:
               default:
                  if (deviceAddress != null) {
                     String headerAddress = header.getInsideModel().toString();
                     if (headerAddress != null && 0 == StringUtilities.compareToIgnoreCase(headerAddress, deviceAddress, 1701707776)) {
                        continue;
                     }
                  }

                  replyAddresses.addElement(o);
            }
         }
      }

      return replyAddresses;
   }

   static final boolean createHeaderForStringPairs(
      String[][] arrayOfStringPairs, int headerType, Factory factory, ContextObject initialData, WritableSet message, String addressToMatch
   ) {
      boolean returnValue = false;
      if (arrayOfStringPairs != null) {
         for (int i = 0; i < arrayOfStringPairs.length; i++) {
            if (arrayOfStringPairs[i] != null) {
               if (addressToMatch != null) {
                  if (StringUtilities.compareToIgnoreCase(arrayOfStringPairs[i][0], addressToMatch, 1701707776) == 0) {
                     returnValue = true;
                  }

                  if (initialData.getFlag(94) && headerType == 2) {
                     continue;
                  }
               }

               initialData.put(251, arrayOfStringPairs[i]);
               initialData.put(-4054673099568009991L, HeaderTypes._typesAsInteger[headerType]);
               PersistableRIMModel model = (PersistableRIMModel)factory.createInstance(initialData);
               message.add(model);
            }
         }
      }

      return returnValue;
   }

   static final void createHeaderForGroupAddress(String groupName, int groupUID, int headerType, ContextObject initialData, WritableSet message) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory headerFactory = (Factory)ar.waitFor(-8034039608019345282L);
      Factory groupAddressFactory = (Factory)ar.waitFor(-1326186686655625745L);
      if (groupAddressFactory != null) {
         ContextObject groupContext = new ContextObject();
         groupContext.put(253, groupName);
         groupContext.putIntegerData(groupUID);
         PersistableRIMModel gacm = (PersistableRIMModel)groupAddressFactory.createInstance(groupContext);
         initialData.put(-4054673099568009991L, HeaderTypes._typesAsInteger[headerType]);
         initialData.put(254, gacm);
         PersistableRIMModel headerModel = (PersistableRIMModel)headerFactory.createInstance(initialData);
         message.add(headerModel);
      }
   }

   static final void createHeaderForStringPairs(String[] stringPair, int headerType, long objectType, ContextObject initialData, WritableSet message) {
      if (stringPair != null) {
         _arrayOfStringPairs[0] = stringPair;
         Factory factory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(objectType);
         createHeaderForStringPairs(_arrayOfStringPairs, headerType, factory, initialData, message, null);
      }
   }

   static final void createEmailHeaderModel(String[] stringPair, int headerType, ContextObject contextObject, WritableSet message, int messageStatus) {
      EmailHeaderModel model = EmailHeaderModelFactory.createInstance(stringPair, headerType, contextObject);
      switch (messageStatus) {
         case 8191:
            model.setFlags((byte)4);
            break;
         case 2097151:
            model.setFlags((byte)2);
            break;
         case 4194303:
            model.setFlags((byte)1);
      }

      message.add(model);
   }

   static final Object createTextObjectForString(String stringData, long objectType, ContextObject initialData, WritableSet message) {
      if (stringData != null) {
         initialData.put(253, stringData);
         PersistableRIMModel model = (PersistableRIMModel)FactoryUtil.createInstance(objectType, initialData);
         message.add(model);
         return model;
      } else {
         return null;
      }
   }

   private static final void processWlanConfigurationAttachments(EmailMessageModel messageModel, ContextObject contextObject) {
      Object dataObj = ContextObject.get(contextObject, -7256629588853673607L);
      if (dataObj != null) {
         if (dataObj instanceof byte[]) {
            byte[] data = (byte[])dataObj;
            String name = (String)ContextObject.get(contextObject, 2065150413548757754L);
            WLANProfileMessageAttachmentModel model = createProfileModel(data, name);
            if (model != null) {
               messageModel.add(model);
            }
         }
      }
   }

   private static final WLANProfileMessageAttachmentModel createProfileModel(byte[] data, String fileName) {
      WLANProfileMessageAttachmentModel model = new WLANProfileMessageAttachmentModel();
      model.setData(data);
      model.setFilename(fileName);
      return model;
   }

   private static final boolean createAndAttachMessageAttachment(EmailMessageModel messageModel, ContextObject contextObject) {
      if (messageModel != null && contextObject != null) {
         boolean attachmentBasedOnFile = !contextObject.containsKey(5473606008898265655L)
            && (contextObject.containsKey(6420606222376351919L) || contextObject.containsKey(2765042845091913199L));
         boolean attachmentBasedOnInputStream = !attachmentBasedOnFile && contextObject.containsKey(5473606008898265655L);
         ServiceRecord serviceRecord = CMIMEUtilities.findFirstAvailableServiceRecordSupportingNativeAttachment();
         if (serviceRecord == null) {
            if (attachmentBasedOnFile) {
               return processFileBasedCompressedAttachment(messageModel, serviceRecord, contextObject);
            }

            if (attachmentBasedOnInputStream) {
               return processInputStreamBasedCompressedAttachment(messageModel, serviceRecord, contextObject);
            }
         } else {
            if (attachmentBasedOnFile) {
               return processFileBasedNativeAttachment(messageModel, serviceRecord, contextObject);
            }

            if (attachmentBasedOnInputStream) {
               return processInputStreamBasedNativeAttachment(messageModel, serviceRecord, contextObject);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final boolean processFileBasedNativeAttachment(EmailMessageModel messageModel, ServiceRecord serviceRecord, ContextObject contextObject) {
      String filename = (String)contextObject.get(6420606222376351919L);
      if (filename == null) {
         filename = (String)contextObject.get(2765042845091913199L);
      }

      if (filename == null) {
         return false;
      }

      try {
         LargeAttachmentModel attachmentModel = NativeAttachmentRequestProcessor$Helper.createAttachment(serviceRecord, messageModel, filename);
         if (attachmentModel == null) {
            return false;
         }

         messageModel.add(attachmentModel);
         return true;
      } catch (Throwable var6) {
         throw new RuntimeException(e.getMessage());
      }
   }

   private static final boolean processFileBasedCompressedAttachment(EmailMessageModel messageModel, ServiceRecord serviceRecord, ContextObject contextObject) {
      String filename = (String)contextObject.get(6420606222376351919L);
      if (filename == null) {
         filename = (String)contextObject.get(2765042845091913199L);
      }

      if (filename == null) {
         return false;
      }

      CompressedFileAttachmentModel model = CompressedFileAttachmentModel$Helper.createModelForImage(filename);
      if (model == null) {
         return false;
      }

      messageModel.add(model);
      return true;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final boolean processInputStreamBasedNativeAttachment(
      EmailMessageModel messageModel, ServiceRecord serviceRecord, ContextObject contextObject
   ) {
      String fileName = (String)contextObject.get(2765042845091913199L);
      if (fileName == null) {
         fileName = (String)contextObject.get(-4886909117188079897L);
      }

      String mimeType = (String)contextObject.get(-4241241545455759532L);

      try {
         InputStream inputStream = (InputStream)contextObject.get(5473606008898265655L);
         byte[] data = IOUtilities.streamToBytes(inputStream);
         LargeAttachmentModel$LargeCachedAttachmentModel model = NativeAttachmentRequestProcessor$Helper.createCachedAttachment(
            serviceRecord, messageModel, data, mimeType, fileName
         );
         if (model == null) {
            return false;
         }

         messageModel.add(model);
         return true;
      } catch (Throwable var9) {
         throw new RuntimeException(e.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static final boolean processInputStreamBasedCompressedAttachment(
      EmailMessageModel messageModel, ServiceRecord serviceRecord, ContextObject contextObject
   ) {
      String fileName = (String)contextObject.get(2765042845091913199L);

      try {
         InputStream inputStream = (InputStream)contextObject.get(5473606008898265655L);
         byte[] data = IOUtilities.streamToBytes(inputStream);
         CompressedFileAttachmentModel model = CompressedFileAttachmentModel$Helper.createModelForImage(data, fileName);
         if (model == null) {
            return false;
         }

         messageModel.add(model);
         return true;
      } catch (Throwable var8) {
         throw new RuntimeException(e.getMessage());
      }
   }
}
