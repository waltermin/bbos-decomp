package net.rim.device.apps.internal.mms.model;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.Phone;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.phone.model.PhoneNumberComparator;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;

public final class MMSMessageModelBuilder {
   private MMSMessageModelImpl _message;
   private MMSPayloadModelImpl _payload;
   private Factory _phoneNumberFactory;
   private Factory _emailAddressFactory;
   private ContextObject _context;

   public MMSMessageModelBuilder() {
      this(new MMSMessageModelImpl(), null);
   }

   public MMSMessageModelBuilder(MMSMessageModel message) {
      this(message, message.getPayload());
   }

   public MMSMessageModelBuilder(MMSMessageModel message, MMSPayloadModel payload) {
      this._message = (MMSMessageModelImpl)message;
      if (payload != null && ObjectGroup.isInGroup(payload)) {
         this._payload = (MMSPayloadModelImpl)ObjectGroup.expandGroup(payload);
      } else if (payload != null) {
         this._payload = new MMSPayloadModelImpl(payload);
         if (payload == this._message.getPayload() && MMSStorage.isFiled(this._message)) {
            this._payload.setCreationDate(payload.getCreationDate());
         }
      } else {
         this._payload = new MMSPayloadModelImpl();
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._phoneNumberFactory = (Factory)ar.waitFor(3797587162219887872L);
      this._emailAddressFactory = (Factory)ar.waitFor(-2985347935260258684L);
      this._context = (ContextObject)(new Object());
   }

   public final MMSMessageModel getResult() {
      if (!MMSStorage.isFiled(this._message)) {
         this._payload.setCreationDate(System.currentTimeMillis());
      }

      MMSMessageModelImpl message = this._message;
      message.setPayload(this._payload);
      this._message = null;
      this._payload = null;
      return message;
   }

   public final void commitResult() {
      this._message.perform(-8063528366659490440L, this._payload);
      this._message = null;
      this._payload = null;
   }

   public final void update(MMSProtocolDataUnit pdu) {
      this.setAttributes(pdu);
      this.setStatus(2047);
      this.setInbound();
      String from = pdu.getAttribute("from");
      if (from != null) {
         this.setSender(from);
      }

      this.addRecipients(pdu.getRecipients());
      this.addCcRecipients(pdu.getCcRecipients());
      this._payload.addAttachments(pdu);
   }

   public final void enableSmartDialing() {
      this._message.setFlags(64);
   }

   public final void setInbound() {
      this._message.setFlags(16);
   }

   public final void setUnopened() {
      this._message.setFlags(1);
   }

   public final void setOpened() {
      this._message.clearFlags(1);
   }

   public final void setNew() {
      this._message.setFlags(512);
   }

   public final void setForwardLocked() {
      this._message.setFlags(128);
   }

   public final void setStatus(int messageStatus) {
      this._message.setStatus(messageStatus);
   }

   public final void setStatus(int messageStatus, int httpErrorCode, int mmsResponseCode, int wapIOExceptionError, int wapIOExceptionAdditionalData) {
      this._message.setStatus(messageStatus, httpErrorCode, mmsResponseCode, wapIOExceptionError, wapIOExceptionAdditionalData);
   }

   public final void setSender(RIMModel address) {
      this._payload.setSender(address);
   }

   public final void setSender(String address) {
      this.setSender(this.createAddressModel(address));
   }

   public final void setPendingReadNotification() {
      this._message.setFlags(256);
   }

   public final void setSubject(String subject) {
      if (subject.length() > 40) {
         subject = subject.substring(0, 40);
      }

      this.setAttribute("subject", subject);
   }

   private final void setAttributes(MMSProtocolDataUnit pdu) {
      Enumeration names = pdu.attributeNames();

      while (names.hasMoreElements()) {
         String name = (String)names.nextElement();
         this._payload.setAttribute(name, pdu.getAttribute(name));
      }
   }

   public final void setAttributes(HttpHeaders headers) {
      int size = headers.size();

      for (int i = 0; i < size; i++) {
         String name = headers.getPropertyKey(i);
         this._payload.setAttribute(name, headers.getPropertyValue(i));
      }
   }

   public final void setAttribute(String attributeName, String value) {
      this._payload.setAttribute(attributeName, value);
   }

   public final void inheritDefaultReporting() {
      MMSOptions options = MMSOptions.getInstance();
      int requestReadReport;
      if (MMSClientServiceBook.isLockedOption(128)) {
         if ((MMSClientServiceBook.getDefaultOptionFlags() & 128) != 0) {
            requestReadReport = 128;
         } else {
            requestReadReport = 129;
         }
      } else {
         requestReadReport = options.getOptionFlag(128) ? 128 : 129;
      }

      int requestDeliveryReport;
      if (MMSClientServiceBook.isLockedOption(64)) {
         if ((MMSClientServiceBook.getDefaultOptionFlags() & 64) != 0) {
            requestDeliveryReport = 128;
         } else {
            requestDeliveryReport = 129;
         }
      } else {
         requestDeliveryReport = options.getOptionFlag(64) ? 128 : 129;
      }

      this._payload.setAttribute("x-mms-read-report", Integer.toString(requestReadReport));
      this._payload.setAttribute("x-mms-delivery-report", Integer.toString(requestDeliveryReport));
   }

   public final void addRecipient(RIMModel address) {
      if (address != null) {
         this._payload.addRecipient(this.copyAddress(address));
      }
   }

   public final void addRecipient(String address) {
      if (address != null) {
         this._payload.addRecipient(this.createAddressModel(address));
      }
   }

   public final void addRecipients(Vector addressList) {
      int count = addressList != null ? addressList.size() : 0;

      for (int idx = 0; idx < count; idx++) {
         Object element = addressList.elementAt(idx);
         if (!(element instanceof Object)) {
            this.addRecipient((RIMModel)element);
         } else {
            String str = (String)element;
            this.addRecipient(str);
         }
      }
   }

   public final void addCcRecipient(RIMModel address) {
      if (address != null) {
         this._payload.addCcRecipient(this.copyAddress(address));
      }
   }

   public final void addCcRecipient(String address) {
      if (address != null) {
         this._payload.addCcRecipient(this.createAddressModel(address));
      }
   }

   public final void addCcRecipients(Vector addressList) {
      int count = addressList != null ? addressList.size() : 0;

      for (int idx = 0; idx < count; idx++) {
         Object element = addressList.elementAt(idx);
         if (!(element instanceof Object)) {
            this.addCcRecipient((RIMModel)element);
         } else {
            String str = (String)element;
            this.addCcRecipient(str);
         }
      }
   }

   public final void addBccRecipient(RIMModel address) {
      if (address != null) {
         this._payload.addBccRecipient(this.copyAddress(address));
      }
   }

   public final void addBccRecipient(String address) {
      if (address != null) {
         this._payload.addBccRecipient(this.createAddressModel(address));
      }
   }

   public final void addBccRecipients(Vector addressList) {
      int count = addressList != null ? addressList.size() : 0;

      for (int idx = 0; idx < count; idx++) {
         Object element = addressList.elementAt(idx);
         if (!(element instanceof Object)) {
            this.addBccRecipient((RIMModel)element);
         } else {
            String str = (String)element;
            this.addBccRecipient(str);
         }
      }
   }

   public final void removeSelfFromAddressList() {
      try {
         if (Phone.isSupported()) {
            String number = Phone.getInstance().getNumber(0);
            if (number != null && number.length() > 0) {
               this._context.put(253, number);
               PhoneNumberModel self = (PhoneNumberModel)this._phoneNumberFactory.createInstance(this._context);
               PhoneNumberComparator comparator = (PhoneNumberComparator)(new Object(self));
               this.removeSelfFromAddressList(this._payload.getRecipients(), comparator);
               this.removeSelfFromAddressList(this._payload.getCcRecipients(), comparator);
               this.removeSelfFromAddressList(this._payload.getBccRecipients(), comparator);
               return;
            }
         }
      } finally {
         return;
      }
   }

   private final void removeSelfFromAddressList(Vector list, PhoneNumberComparator comparator) {
      if (list != null) {
         for (int idx = list.size() - 1; idx >= 0; idx--) {
            Object recipient = list.elementAt(idx);
            if (recipient instanceof Object) {
               comparator.compare((PhoneNumberModel)recipient);
               if (comparator.isSubsetMatch()) {
                  list.removeElementAt(idx);
               }
            }
         }
      }
   }

   private final RIMModel copyAddress(Object address) {
      return (RIMModel)(!(address instanceof Object) ? address : ((Copyable)address).copy());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void addAttachment(String name, int type, String string) {
      String charset = "utf-8";
      boolean var8 = false /* VF: Semaphore variable */;

      byte[] data;
      label20:
      try {
         var8 = true;
         data = string.getBytes(charset);
         var8 = false;
      } finally {
         if (var8) {
            data = string.getBytes();
            break label20;
         }
      }

      this.addAttachment(name, type, data, charset);
   }

   public final void addAttachment(String name, int type, byte[] data, String charset) {
      this.addAttachment(new PersistedAttachmentImpl(name, type, data, charset));
   }

   public final void addAttachment(MMSAttachment attachment) {
      this._payload.addAttachment(attachment);
   }

   public final void addAttachments(AttachmentDataProvider attachmentProvider) {
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            MMSAttachment attachment = attachmentProvider.getAttachment(name);
            if (attachment != null) {
               this.addAttachment(attachment);
            }
         }
      }
   }

   public final void addReferencedAttachments(MMSPresentationModel presentation, AttachmentDataProvider source) {
      presentation.copyTo(new MMSMessageModelBuilder$1AddAttachmentAdaptor(this, source));
   }

   public final void removeAllAttachments() {
      this._payload.removeAllAttachments();
   }

   private final RIMModel createAddressModel(String str) {
      int pos = str.indexOf("/TYPE");
      if (pos >= 0) {
         String globalPhoneNumber = str.substring(0, pos).trim();
         this._context.put(253, globalPhoneNumber);
         return (RIMModel)this._phoneNumberFactory.createInstance(this._context);
      } else {
         this._context.put(251, parseEmailAddress(str));
         return (RIMModel)this._emailAddressFactory.createInstance(this._context);
      }
   }

   private static final String[] parseEmailAddress(String str) {
      int leftAngleBraceIndex = str.lastIndexOf(60);
      if (leftAngleBraceIndex >= 0) {
         int rightAngleBraceIndex = str.lastIndexOf(62);
         if (rightAngleBraceIndex > leftAngleBraceIndex) {
            String friendlyName = str.substring(0, leftAngleBraceIndex).trim();
            String emailAddress = str.substring(leftAngleBraceIndex + 1, rightAngleBraceIndex).trim();
            if (friendlyName.length() == 0) {
               return new Object[]{emailAddress};
            }

            return new Object[]{emailAddress, friendlyName};
         }
      }

      return new Object[]{str};
   }
}
