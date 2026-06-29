package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.text.EmailAddressTextFilter;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.ribbon.indicators.NewMessageEventManager;
import net.rim.device.apps.api.transmission.TransmissionStatus;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.proxy.Proxy;

public final class EmailMessageUtilities {
   public static final byte CMIME_ATTACHMENT_DATA;
   private static ContextObject _immediateContext = (ContextObject)(new Object());
   private static ContextObject _immediateContextForLevel1 = ContextObject.clone(_immediateContext);
   private static Proxy _proxy = Proxy.getInstance();
   private static EmailMessageUtilities$CancelImmediateEvent _cancelImmediateEvent = new EmailMessageUtilities$CancelImmediateEvent();

   private EmailMessageUtilities() {
   }

   public static final boolean treatAsLevel1(EmailMessageModel message) {
      return message.getNotificationLevel() == 1 || message.flagsSet(8192) && MessageListOptions.getOptions().getFlag(32);
   }

   private static final boolean isPage(EmailMessageModel message) {
      return message.flagsSet(524288);
   }

   private static final ContextObject getImmediateNotificationsContext(EmailMessageModel model, boolean sendError, Object context) {
      ContextObject contextToReturn = treatAsLevel1(model) ? _immediateContextForLevel1 : _immediateContext;
      if ((model.getFlags() & 1) != 0 || sendError) {
         contextToReturn = contextToReturn.clone();
         contextToReturn.setPrivateFlag(5593808198828868151L, 1);
      }

      return contextToReturn;
   }

   private static final ContextObject getDeferredNotificationsContext(EmailMessageModel model, boolean receivingMore, long viewedId, Object context) {
      ContextObject deferredContext = (ContextObject)(new Object());
      deferredContext.setFlag(65);
      if (treatAsLevel1(model)) {
         deferredContext.setFlag(68);
      }

      if (receivingMore) {
         deferredContext.setFlag(66);
         if (model.getCMIMEReferenceIdentifier() == viewedId) {
            deferredContext.setFlag(37);
         }
      }

      return deferredContext;
   }

   public static final void triggerNotifications(EmailMessageModel model, boolean receivingMore, long viewedId, Object context) {
      triggerNotifications(model, receivingMore, false, viewedId, context);
   }

   public static final void triggerNotifications(EmailMessageModel model, boolean receivingMore, boolean sendError, long viewedId, Object context) {
      ContextObject immediateContext = getImmediateNotificationsContext(model, sendError, context);
      Object deferredContext = getDeferredNotificationsContext(model, receivingMore, viewedId, context);
      AddressReference ar = model.getSenderInfo();
      if (ar != null) {
         int addresscardUID = (int)ar.getAddressBookEntryLUID();
         ContextObject.put(immediateContext, -7004855975111283545L, new Object(addresscardUID));
         NewMessageEventManager.addFlag(1, addresscardUID);
      }

      _proxy.invokeLater(new EmailMessageUtilities$1(model, deferredContext, immediateContext));
   }

   public static final void cancelImmediateNotifications(EmailMessageModel model, Object context) {
      long type = -1845850106795451018L;
      ServiceRecord sr = model.getServiceRecordForMessage();
      type = CMIMEUtilities.getProfileSourceIDForService(sr);
      if (treatAsLevel1(model)) {
         type = -327746170160875990L;
      }

      if (isPage(model)) {
         type = 6432934947797527350L;
      }

      if (NotificationsManager.isImmediateEventOccuring(type)) {
         long id = model.getCMIMEReferenceIdentifier();
         Object immediateContext = getImmediateNotificationsContext(model, false, context);
         EmailMessageUtilities$CancelImmediateEvent cie = _cancelImmediateEvent;
         boolean finished;
         synchronized (cie) {
            finished = cie._finished;
            cie.setCancelInfo(type, id, immediateContext);
         }

         if (finished) {
            _proxy.invokeLater(cie);
         }
      }
   }

   public static final boolean canSendEmail() {
      if (!CMIMEUtilities.canSendEmail()) {
         Dialog.alert(EmailResources.getString(212));
         return false;
      } else {
         return true;
      }
   }

   public static final boolean canSendPIN() {
      return ITPolicy.getBoolean(7, true);
   }

   public static final String getTransmissionErrorMessage(EmailMessageModel messageModel) {
      String errorMessage = messageModel.getTransmissionErrorMessage();
      if (errorMessage == null) {
         int transmissionError = messageModel.getTransmissionError();
         if (transmissionError != 0) {
            errorMessage = TransmissionStatus.getTransmissionStatusMessage(messageModel.getTransmissionError());
         }
      }

      return errorMessage;
   }

   public static final ServiceRecord getServiceRecordForMessage(EmailMessageModel message) {
      if (message == null) {
         return null;
      }

      long folderId = message.getFolderId();
      if (folderId != 0) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchyForFolder(folderId);
         if (hierarchy != null) {
            String cid = hierarchy.getServiceContentIdentifier();
            int nameHash = hierarchy.getServiceNameHash();
            int uidHash = hierarchy.getServiceUidHash();
            int userId = hierarchy.getServiceUserId();
            ServiceBook sb = ServiceBook.getSB();
            return sb.getRecordByCidAndUserId(cid, userId, nameHash, uidHash);
         }
      }

      return null;
   }

   public static final boolean allowOutboundMessages(EmailMessageModel message) {
      return !message.flagsSet(33554432) && (!message.flagsSet(8192) || ITPolicy.getBoolean(7, true));
   }

   public static final boolean moreRequestSent(EmailMessageModel message) {
      int size = message.size();

      for (int i = 0; i < size; i++) {
         Object o = message.getAt(i);
         if (o instanceof MorePartModel) {
            MorePartModel morePartModel = (MorePartModel)o;
            if (morePartModel.getMoreRequestSent()) {
               return true;
            }
         }
      }

      return false;
   }

   public static final int getOriginalSubjectIndex(String subject) {
      int subjectLength = subject.length();
      int originalSubjectStart = 0;

      while (true) {
         int colon = subject.indexOf(58, originalSubjectStart);
         if (colon == -1) {
            return originalSubjectStart;
         }

         if (colon >= subjectLength - 1) {
            return subjectLength;
         }

         if (!CharacterUtilities.isSpaceChar(subject.charAt(colon + 1))) {
            return originalSubjectStart;
         }

         originalSubjectStart = colon + 2;
      }
   }

   public static final boolean isEmailAddressFullyQualified(String address) {
      if (address == null) {
         return false;
      }

      address = address.trim();
      EmailAddressTextFilter textFilter = (EmailAddressTextFilter)(new Object());
      boolean valid = true;
      int i = 0;

      while (valid && i < address.length()) {
         valid = valid && textFilter.validate(address.charAt(i++));
      }

      if (valid) {
         int atIndex = address.indexOf("@");
         int tldSeperator = address.lastIndexOf(46);
         int lastSpaceCharacter = address.lastIndexOf(32);
         valid = valid && atIndex > 0 && tldSeperator > atIndex + 2 && lastSpaceCharacter < atIndex;
         if (valid) {
            String localPart = address.substring(0, atIndex);
            if (localPart.length() != localPart.trim().length()) {
               valid = false;
            }
         }
      }

      return valid;
   }

   static {
      _immediateContext.putIntegerData(0);
      _immediateContextForLevel1.setFlag(68);
   }
}
