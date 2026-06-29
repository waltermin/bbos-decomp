package net.rim.device.apps.internal.mms.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;
import net.rim.vm.Persistable;

final class MMSPayloadModelImpl implements MMSPayloadModel, PersistableRIMModel, AttachmentDataProvider, CloneProvider, MatchProvider, EncryptableProvider {
   private long _creationDate = System.currentTimeMillis();
   private String _messageID;
   private String _transactionID;
   private Hashtable _attributes;
   private PersistableRIMModel _sender;
   private Vector _toList;
   private Vector _ccList;
   private Vector _bccList;
   private Hashtable _attachments;
   private Vector _attachmentNames;

   final void addRecipients(Vector addressList) {
      if (this._toList == null) {
         this._toList = new Vector();
      }

      Vector unwrapped = MMSServiceUtil.expandRecipientList(addressList);
      if (unwrapped != null) {
         for (int i = 0; i < unwrapped.size(); i++) {
            this._toList.addElement(copyAddress((RIMModel)unwrapped.elementAt(i)));
         }
      }
   }

   final void setCreationDate(long date) {
      this._creationDate = date;
   }

   @Override
   public final int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      switch (crit.getType()) {
         case 3:
            return -1;
         case 4:
         default:
            return Match.performMatch(this._sender, crit);
         case 5:
            if (Match.performMatch(this._sender, crit) == 1) {
               return 1;
            } else {
               if (!this.addressMatches(this._toList, crit) && !this.addressMatches(this._ccList, crit)) {
                  return 0;
               }

               return 1;
            }
         case 6:
            if (this.addressMatches(this._toList, crit)) {
               return 1;
            }

            return 0;
         case 7:
            return this.addressMatches(this._ccList, crit) ? 1 : 0;
      }
   }

   final void setSender(RIMModel address) {
      this._sender = (PersistableRIMModel)copyAddress(address);
   }

   public final void removeAllAttachments() {
      this._attachments = null;
      this._attachmentNames = null;
   }

   final void addRecipient(RIMModel address) {
      if (this._toList == null) {
         this._toList = new Vector();
      }

      this._toList.addElement(copyAddress(address));
   }

   @Override
   public final Object clone(Object context) {
      return new MMSPayloadModelImpl(this);
   }

   public final void addAttachments(AttachmentDataProvider source) {
      Enumeration names = source.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            this.addAttachment(source.getAttachment(name));
         }
      }
   }

   final void addCcRecipient(RIMModel address) {
      if (this._ccList == null) {
         this._ccList = new Vector();
      }

      this._ccList.addElement(copyAddress(address));
   }

   final void addCcRecipients(Vector addressList) {
      if (this._ccList == null) {
         this._ccList = new Vector();
      }

      Vector unwrapped = MMSServiceUtil.expandRecipientList(addressList);
      if (unwrapped != null) {
         for (int i = 0; i < unwrapped.size(); i++) {
            this._ccList.addElement(copyAddress((RIMModel)unwrapped.elementAt(i)));
         }
      }
   }

   public final void addAttachment(MMSAttachment attachment) {
      if (this._attachments == null) {
         this._attachments = new Hashtable();
         this._attachmentNames = new Vector();
      }

      if (!(attachment instanceof Persistable)) {
         attachment = new PersistedAttachmentImpl(attachment);
      }

      Object oldData = this._attachments.put(attachment.getName(), attachment);
      if (oldData == null) {
         this._attachmentNames.addElement(attachment.getName());
      }
   }

   final void addBccRecipient(RIMModel address) {
      if (this._bccList == null) {
         this._bccList = new Vector();
      }

      this._bccList.addElement(copyAddress(address));
   }

   final void addBccRecipients(Vector addressList) {
      if (this._bccList == null) {
         this._bccList = new Vector();
      }

      Vector unwrapped = MMSServiceUtil.expandRecipientList(addressList);
      if (unwrapped != null) {
         for (int i = 0; i < unwrapped.size(); i++) {
            this._bccList.addElement(copyAddress((RIMModel)unwrapped.elementAt(i)));
         }
      }
   }

   final void setAttribute(String attributeName, String value) {
      if (attributeName.equals("message-id")) {
         this._messageID = value;
         value = value;
      } else if (attributeName.equals("x-mms-transaction-id")) {
         this._transactionID = value;
         value = value;
      }

      if (this._attributes == null) {
         this._attributes = new Hashtable();
      }

      this._attributes.put(StringUtilities.toLowerCase(attributeName, 1701707776), PersistentContent.encode(value));
   }

   @Override
   public final String getAttribute(String attributeName) {
      if (attributeName.equals("message-id")) {
         return this._messageID;
      }

      if (attributeName.equals("x-mms-transaction-id")) {
         return this._transactionID;
      }

      String value = null;
      if (this._attributes != null) {
         try {
            return PersistentContent.decodeString(this._attributes.get(attributeName));
         } finally {
            ;
         }
      } else {
         return value;
      }
   }

   @Override
   public final Enumeration attributeNames() {
      return this._attributes == null ? null : this._attributes.keys();
   }

   @Override
   public final MMSPresentationModel getPresentationModel() {
      MMSAttachment attachment = PresentationModelFactory.findPresentationAttachment(this.attachmentNames(), this);
      if (attachment == null) {
         MMSProtocolDataUnit pdu = this.getPDU();
         if (pdu != null) {
            attachment = PresentationModelFactory.findPresentationAttachment(pdu.attachmentNames(), this);
         }
      }

      if (attachment != null) {
         return PresentationModelFactory.createInstance(attachment);
      } else if (this.hasAttachments()) {
         MMSPresentationModel presentation = PresentationModelFactory.createInstance(65536);
         this.copyTo(presentation);
         return presentation;
      } else {
         return null;
      }
   }

   @Override
   public final boolean hasAttachments() {
      return this._attachments != null ? this._attachments.size() > 0 : false;
   }

   @Override
   public final MMSAttachment getAttachment(String attachmentName) {
      if (this._attachments != null) {
         MMSAttachment attachment = (MMSAttachment)this._attachments.get(attachmentName);
         if (attachment != null) {
            return attachment;
         }
      }

      MMSProtocolDataUnit pdu = this.getPDU();
      return pdu != null ? pdu.getAttachment(attachmentName) : null;
   }

   @Override
   public final boolean hasAttachment(String attachmentName) {
      if (this._attachments != null) {
         MMSAttachment attachment = (MMSAttachment)this._attachments.get(attachmentName);
         if (attachment != null) {
            return true;
         }
      }

      MMSProtocolDataUnit pdu = this.getPDU();
      return pdu != null ? pdu.hasAttachment(attachmentName) : false;
   }

   @Override
   public final int getAttachmentType(String attachmentName) {
      if (this._attachments != null) {
         MMSAttachment attachment = (MMSAttachment)this._attachments.get(attachmentName);
         if (attachment != null) {
            return attachment.getType();
         }
      }

      MMSProtocolDataUnit pdu = this.getPDU();
      return pdu != null ? pdu.getAttachmentType(attachmentName) : -1;
   }

   @Override
   public final int getTotalAttachmentDataSize() {
      int dataSize = 0;
      Enumeration names = this.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            dataSize += this.getAttachment(name).getDataSize();
         }
      }

      return dataSize;
   }

   @Override
   public final Vector getBccRecipients() {
      return this._bccList;
   }

   @Override
   public final Vector getCcRecipients() {
      return this._ccList;
   }

   @Override
   public final Vector getRecipients() {
      return this._toList;
   }

   @Override
   public final Enumeration attachmentNames() {
      return this._attachmentNames != null ? this._attachmentNames.elements() : null;
   }

   @Override
   public final long getCreationDate() {
      return this._creationDate;
   }

   @Override
   public final RIMModel getSender() {
      return this._sender;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return (this._sender == null || this.checkCrypt2(this._sender, compress, encrypt))
         && (this._attributes == null || this.checkCrypt(this._attributes.elements(), compress, encrypt))
         && (this._toList == null || this.checkCrypt(this._toList.elements(), compress, encrypt))
         && (this._ccList == null || this.checkCrypt(this._ccList.elements(), compress, encrypt))
         && (this._attachments == null || this.checkCrypt(this._attachments.elements(), compress, encrypt));
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._sender = (PersistableRIMModel)this.reCrypt2(this._sender, compress, encrypt);
      this.reCrypt(this._attributes, compress, encrypt);
      this.reCrypt(this._toList, compress, encrypt);
      this.reCrypt(this._ccList, compress, encrypt);
      this.reCrypt(this._attachments, compress, encrypt);
      return null;
   }

   MMSPayloadModelImpl() {
      this.setAttribute("subject", "");
   }

   private static final RIMModel copyAddress(RIMModel address) {
      return address == null ? null : (RIMModel)((Copyable)address).copy();
   }

   private final MMSProtocolDataUnit getPDU() {
      if (this._attachments != null) {
         MMSAttachment attachment = (MMSAttachment)this._attachments.get("net_rim_ProtocolDataUnit");
         if (attachment != null) {
            long key = this.getCreationDate();
            return PDUCache.get(key, attachment);
         }
      }

      return null;
   }

   private final boolean addressMatches(Vector addresses, SearchCriterion crit) {
      if (addresses == null) {
         return false;
      }

      for (int i = addresses.size() - 1; i >= 0; i--) {
         if (Match.performMatch((RIMModel)addresses.elementAt(i), crit) == 1) {
            return true;
         }
      }

      return false;
   }

   MMSPayloadModelImpl(MMSPayloadModel otherPayload) {
      Enumeration names = otherPayload.attributeNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String attributeName = (String)names.nextElement();
            this.setAttribute(attributeName, otherPayload.getAttribute(attributeName));
         }
      }

      this.setSender(otherPayload.getSender());
      this.addRecipients(otherPayload.getRecipients());
      this.addCcRecipients(otherPayload.getCcRecipients());
      this.addBccRecipients(otherPayload.getBccRecipients());
      if (otherPayload instanceof AttachmentDataProvider) {
         this.addAttachments((AttachmentDataProvider)otherPayload);
      }
   }

   private final void copyTo(MMSPresentationModel presentation) {
      this.copyTo(presentation, this);
      MMSProtocolDataUnit pdu = this.getPDU();
      if (pdu != null) {
         this.copyTo(presentation, pdu);
      }
   }

   private final boolean checkCrypt(Enumeration e, boolean compress, boolean encrypt) {
      while (e.hasMoreElements()) {
         if (!this.checkCrypt2(e.nextElement(), compress, encrypt)) {
            return false;
         }
      }

      return true;
   }

   private final boolean checkCrypt2(Object obj, boolean compress, boolean encrypt) {
      if (!(obj instanceof EncryptableProvider)) {
         if (obj instanceof String && !PersistentContent.checkEncoding(obj, compress, encrypt)) {
            return false;
         }
      } else {
         EncryptableProvider encryptable = (EncryptableProvider)obj;
         if (!encryptable.checkCrypt(compress, encrypt)) {
            return false;
         }
      }

      return true;
   }

   private final void copyTo(MMSPresentationModel presentation, AttachmentDataProvider attachmentProvider) {
      Enumeration names = attachmentProvider.attachmentNames();
      if (names != null) {
         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (attachmentProvider.hasAttachment(name)) {
               int type = attachmentProvider.getAttachmentType(name);
               MMSProtocolDataUnit pdu = this.getPDU();
               boolean forwardLocked = false;
               if (pdu != null) {
                  forwardLocked = pdu.isForwardLocked();
               }

               presentation.addPresentationElement(name, type, false, forwardLocked);
            }
         }
      }
   }

   private final void reCrypt(Hashtable hashtable, boolean compress, boolean encrypt) {
      if (hashtable != null) {
         Enumeration e = hashtable.keys();

         while (e.hasMoreElements()) {
            Object key = e.nextElement();
            hashtable.put(key, this.reCrypt2(hashtable.get(key), compress, encrypt));
         }
      }
   }

   private final void reCrypt(Vector vector, boolean compress, boolean encrypt) {
      if (vector != null) {
         int size = vector.size();

         for (int i = 0; i < size; i++) {
            vector.setElementAt(this.reCrypt2(vector.elementAt(i), compress, encrypt), i);
         }
      }
   }

   private final Object reCrypt2(Object obj, boolean compress, boolean encrypt) {
      Object newObj;
      if (!(obj instanceof EncryptableProvider)) {
         newObj = PersistentContent.reEncode(obj, compress, encrypt);
      } else {
         EncryptableProvider encryptable = (EncryptableProvider)obj;
         newObj = encryptable.reCrypt(compress, encrypt);
         if (newObj == null) {
            newObj = obj;
         }
      }

      return newObj;
   }
}
