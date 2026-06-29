package net.rim.wica.runtime.access.internal.data.collections;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.wica.common.builtindata.componentdefn.EmailCompDef;
import net.rim.wica.runtime.access.internal.data.enumeration.EmailPriorityEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;
import net.rim.wica.runtime.access.invoker.AccessInvokeService;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class EmailCollection extends StdCmpCollectionImpl {
   private AccessInvokeService _invokerService;
   private static EmailHierarchy _emailHierarchy;
   private static final Factory _emailHeaderfactory;
   static Class class$net$rim$wica$runtime$access$invoker$AccessInvokeService;

   public EmailCollection(WicletEx wiclet) {
      super(wiclet, EmailCompDef.getInstance());
      this._invokerService = (AccessInvokeService)wiclet.getRuntime()
         .getService(
            class$net$rim$wica$runtime$access$invoker$AccessInvokeService == null
               ? (class$net$rim$wica$runtime$access$invoker$AccessInvokeService = class$("net.rim.wica.runtime.access.invoker.AccessInvokeService"))
               : class$net$rim$wica$runtime$access$invoker$AccessInvokeService
         );
      this.retrieveEmailHierarchy();
   }

   private void retrieveEmailHierarchy() {
      RIMMessagingService rms = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (null != rms) {
         ServiceRecord sr = rms.getOutgoingServiceRecord();
         if (sr != null) {
            _emailHierarchy = EmailHierarchy.getEmailHierarchy(sr, false);
            if (_emailHierarchy != null) {
               _emailHierarchy.setFriendlyName(sr.getName());
            }
         }
      }
   }

   private Vector getMessagesFromDB() {
      if (_emailHierarchy == null) {
         return null;
      }

      Vector messages = new Vector();
      this.getMessagesInFolder(_emailHierarchy.getInboxFolder(), messages);
      this.getMessagesInFolder(_emailHierarchy.getSentFolder(), messages);
      return messages;
   }

   private void getMessagesInFolder(long emailFolder, Vector messages) {
      if (messages != null) {
         ReadableList unfiled = (ReadableList)EmailHierarchy.getStorageCollection(emailFolder, false);
         this.extractMatchingMessages(emailFolder, unfiled, messages);
      }
   }

   private void extractMatchingMessages(long emailFolder, ReadableList list, Vector messages) {
      int size = list.size();

      for (int i = 0; i < size; i++) {
         EmailMessageModel m = (EmailMessageModel)list.getAt(i);
         long mfid = m.getFolderId();
         if (mfid == emailFolder) {
            messages.addElement(m);
         }
      }
   }

   @Override
   protected int getUID() {
      return CMIMEUtilities.newDeviceSideIdentifier();
   }

   @Override
   public void loadItem(long dataHandle, Object item) {
      if (item instanceof EmailMessageModel) {
         this.setObjectFieldValue(dataHandle, 0, ((ObjectFieldHandler)super._objectFieldHandlers.get(0)).getValue(item));
         this.setObjectFieldValue(dataHandle, 1, ((ObjectFieldHandler)super._objectFieldHandlers.get(1)).getValue(item));
         this.setObjectFieldValue(dataHandle, 2, ((ObjectFieldHandler)super._objectFieldHandlers.get(2)).getValue(item));
         this.setObjectFieldValue(dataHandle, 3, ((ObjectFieldHandler)super._objectFieldHandlers.get(3)).getValue(item));
         this.setObjectFieldValue(dataHandle, 4, ((ObjectFieldHandler)super._objectFieldHandlers.get(4)).getValue(item));
         this.setObjectFieldValue(dataHandle, 5, ((ObjectFieldHandler)super._objectFieldHandlers.get(5)).getValue(item));
         this.setObjectFieldValue(dataHandle, 6, ((ObjectFieldHandler)super._objectFieldHandlers.get(6)).getValue(item));
         this.setIntFieldValue(dataHandle, 9, ((IntFieldHandler)super._intFieldHandlers.get(9)).getValue(item));
         this.setIntFieldValue(dataHandle, 7, ((IntFieldHandler)super._intFieldHandlers.get(7)).getValue(item));
         this.setIntFieldValue(dataHandle, 8, ((IntFieldHandler)super._intFieldHandlers.get(8)).getValue(item));
      }
   }

   @Override
   public IntVector uidsInExternalDB() {
      Vector messages = this.getMessagesFromDB();
      if (messages != null && messages.size() > 0) {
         IntVector uids = new IntVector(messages.size());

         for (int i = messages.size() - 1; i >= 0; i--) {
            uids.addElement(((EmailMessageModel)messages.elementAt(i)).getCMIMEReferenceIdentifier());
         }

         return uids;
      } else {
         return null;
      }
   }

   @Override
   public void saveDeletedItems() {
      if (_emailHierarchy != null && super._handles.size() == 0) {
         WritableSet sentUnfiled = (WritableSet)EmailHierarchy.getStorageCollection(_emailHierarchy.getEmailFolder(4), false);
         if (sentUnfiled != null) {
            sentUnfiled.removeAll();
         }

         WritableSet inboxUnfiled = (WritableSet)EmailHierarchy.getStorageCollection(_emailHierarchy.getEmailFolder(2), false);
         if (inboxUnfiled != null) {
            inboxUnfiled.removeAll();
            return;
         }
      } else {
         for (int i = super._deletedItems.size() - 1; i >= 0; i--) {
            Object messageToRemove = this.getDBItemFromHandle((long)super._defs.getId() << 32 | 4294967295L & super._deletedItems.elementAt(i));
            if (messageToRemove != null) {
               EmailHierarchy.removeMessage((EmailMessageModel)messageToRemove, ((EmailMessageModel)messageToRemove).getFolderId());
            }
         }
      }
   }

   @Override
   public void saveModifiedItems() {
   }

   @Override
   public void saveCreatedItems() {
      for (int i = super._createdItems.size() - 1; i >= 0; i--) {
         int handle = super._createdItems.elementAt(i);
         this._invokerService.saveMessageAsDraft((long)super._defs.getId() << 32 | 4294967295L & handle);
      }
   }

   @Override
   public void resetCache() {
      super._modifiedItems = new IntVector(10, 5);
      super._deletedItems = new IntVector(10, 5);
      super._loadedItems = new IntVector(10, 5);
   }

   private void setDataInModel(EmailMessageModel messageModel, int handle) {
      boolean grouped = false;
      EmailPayloadModel oldpayload = messageModel.getPayload();
      if (ObjectGroup.isInGroup(oldpayload)) {
         grouped = true;
         messageModel.setPayload((EmailPayloadModel)ObjectGroup.expandGroup(oldpayload));
      }

      long dataHandle = (long)super._defs.getId() << 32 | 4294967295L & handle;
      this.setSubjectInModel(dataHandle, messageModel);
      this.setContentInModel(dataHandle, messageModel);
      this.setFromInModel(dataHandle, messageModel);
      this.setReplyToInModel(dataHandle, messageModel);
      this.setToRecipientsInModel(dataHandle, messageModel);
      this.setCCRecipientsInModel(dataHandle, messageModel);
      this.setBCCRecipientsInModel(dataHandle, messageModel);
      messageModel.setPriority((byte)EmailPriorityEnumConverter.commonToDevice(this.getIntFieldValue(dataHandle, 8)));
      if (grouped) {
         EmailModifier.endChanges(messageModel, oldpayload, null);
      }
   }

   private void setHeaderFieldFromStringList(EmailMessageModel messageModel, String fieldString, int headerType) {
      ReadableList l = messageModel;

      for (int i = l.size() - 1; i >= 0; i--) {
         Object element = l.getAt(i);
         if (element instanceof EmailHeaderModel) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            if (model.getHeaderType() == headerType) {
               messageModel.remove(model);
            }
         }
      }

      StringTokenizer st = new StringTokenizer(fieldString, ";,");
      if (st.hasMoreTokens()) {
         String[] addr = new String[]{null, ""};
         ContextObject creationContext = new ContextObject();

         while (st.hasMoreTokens()) {
            addr[0] = st.nextToken();
            if (addr[0] != null) {
               addr[0] = addr[0].trim();
               if (addr[0].length() > 0) {
                  creationContext.reset();
                  creationContext.put(251, addr);
                  creationContext.put(-4054673099568009991L, HeaderTypes._typesAsInteger[headerType]);
                  PersistableRIMModel model = (PersistableRIMModel)_emailHeaderfactory.createInstance(creationContext);
                  messageModel.add(model);
               }
            }
         }
      }
   }

   private void setBCCRecipientsInModel(long dataHandle, EmailMessageModel messageModel) {
      Object o = this.getObjectFieldValue(dataHandle, 6);
      if (o instanceof String) {
         this.setHeaderFieldFromStringList(messageModel, (String)o, 2);
      }
   }

   private void setCCRecipientsInModel(long dataHandle, EmailMessageModel messageModel) {
      Object o = this.getObjectFieldValue(dataHandle, 5);
      if (o instanceof String) {
         this.setHeaderFieldFromStringList(messageModel, (String)o, 1);
      }
   }

   private void setToRecipientsInModel(long dataHandle, EmailMessageModel messageModel) {
      Object o = this.getObjectFieldValue(dataHandle, 4);
      if (o instanceof String) {
         this.setHeaderFieldFromStringList(messageModel, (String)o, 0);
      }
   }

   private void setReplyToInModel(long dataHandle, EmailMessageModel messageModel) {
      Object o = this.getObjectFieldValue(dataHandle, 3);
      if (o instanceof String) {
         this.setHeaderFieldFromStringList(messageModel, (String)o, 5);
      }
   }

   private void setFromInModel(long dataHandle, EmailMessageModel messageModel) {
      Object o = this.getObjectFieldValue(dataHandle, 2);
      if (o instanceof String) {
         this.setHeaderFieldFromStringList(messageModel, (String)o, 3);
      }
   }

   private void setContentInModel(long dataHandle, EmailMessageModel messageModel) {
      String content = (String)this.getObjectFieldValue(dataHandle, 1);
      BodyModel bodyModel = messageModel.getBodyModel();
      if (content == null) {
         if (bodyModel != null) {
            messageModel.remove(bodyModel);
            return;
         }
      } else {
         if (bodyModel == null) {
            bodyModel = (BodyModel)FactoryUtil.createInstance(5987399499453925075L, null);
            messageModel.add(bodyModel);
         }

         bodyModel.setText(content);
      }
   }

   private void setSubjectInModel(long dataHandle, EmailMessageModel messageModel) {
      String subject = (String)this.getObjectFieldValue(dataHandle, 0);
      boolean foundExistingSubject = false;
      ReadableList l = messageModel;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof SubjectModel) {
            foundExistingSubject = true;
            SubjectModel subjectModel = (SubjectModel)element;
            if (subject == null) {
               messageModel.remove(subjectModel);
            } else {
               subjectModel.setSubject(subject);
            }
            break;
         }
      }

      if (!foundExistingSubject && subject != null) {
         EmailBuilderApi.addSubjectLine(messageModel, subject);
      }
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      Object o = MessageLookups.get(-4420850319371185992L, this.getHandle(dataHandle));
      return !(o instanceof EmailMessageModel) ? null : (EmailMessageModel)o;
   }

   @Override
   public void initFieldHandlers() {
      super._objectFieldHandlers = new IntHashtable(8);
      super._objectFieldHandlers.put(0, new EmailCollection$SubjectHandler(null));
      super._objectFieldHandlers.put(1, new EmailCollection$ContentHandler(null));
      super._objectFieldHandlers.put(2, new EmailCollection$FromHandler(null));
      super._objectFieldHandlers.put(3, new EmailCollection$ReplyToHandler(null));
      super._objectFieldHandlers.put(4, new EmailCollection$ToRecipientsHandler(null));
      super._objectFieldHandlers.put(5, new EmailCollection$CCRecipientsHandler(null));
      super._objectFieldHandlers.put(6, new EmailCollection$BCCRecipientsHandler(null));
      super._intFieldHandlers = new IntHashtable(4);
      super._intFieldHandlers.put(9, new EmailCollection$UIDHandler(null));
      super._intFieldHandlers.put(7, new EmailCollection$FolderHandler(null));
      super._intFieldHandlers.put(8, new EmailCollection$PriorityHandler(null));
   }

   public EmailMessageModel getMessageToSend(long emailID) {
      int handle = this.getHandle(emailID);
      if (super._createdItems.contains(handle)) {
         EmailMessageModel messageModel = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
         if (messageModel != null && handle != -1) {
            messageModel.setCMIMEReferenceIdentifier(handle);
            this.setDataInModel(messageModel, handle);
            return messageModel;
         }
      }

      return null;
   }

   public void messageSent(long emailID, EmailMessageModel msg) {
      if (msg != null) {
         int handle = this.getHandle(emailID);
         super._overrideReadOnly = true;
         this.setObjectFieldValue(emailID, 2, ((ObjectFieldHandler)super._objectFieldHandlers.get(2)).getValue(msg));
         this.setIntFieldValue((long)super._defs.getId() << 32 | 4294967295L & handle, 7, ((IntFieldHandler)super._intFieldHandlers.get(7)).getValue(msg));
         super._overrideReadOnly = false;
         super._createdItems.removeElement(handle);
         if (msg.getStatus() == Integer.MAX_VALUE) {
            this.remove(emailID);
         }
      }
   }

   @Override
   protected boolean isFieldWriteable(long dataHandle, int field) {
      return !super._createdItems.contains(this.getHandle(dataHandle)) ? false : super.isFieldWriteable(dataHandle, field);
   }

   private static String getStringFieldValueFromModel(EmailMessageModel emm, int fieldType, boolean getFirstOnly) {
      String result = "";
      if (emm != null) {
         boolean added = false;
         String[] addrs = new String[2];

         for (int i = emm.size() - 1; i >= 0; i--) {
            Object element = emm.getAt(i);
            if (element instanceof EmailHeaderModel) {
               EmailHeaderModel model = (EmailHeaderModel)element;
               if (model.getHeaderType() == fieldType) {
                  model.convert(model, addrs);
                  if (added) {
                     result = result + ";";
                  } else {
                     added = true;
                  }

                  result = result + addrs[0];
                  if (getFirstOnly) {
                     return result;
                  }
               }
            }
         }
      }

      return result;
   }

   @Override
   public boolean isSupported(String feature) {
      return feature.equals("sendEmail") ? true : super.isSupported(feature);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _emailHeaderfactory = (Factory)ar.waitFor(-8034039608019345282L);
   }
}
