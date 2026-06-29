package net.rim.device.cldc.io.mms;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;

public class MultipartMessageImpl implements MultipartMessage {
   private Vector _messageParts;
   private Vector _bccList;
   private Vector _ccList;
   private Vector _toList;
   private String _fromAddress;
   private long _timeStamp;
   private String _subject;
   private Hashtable _headers;
   private String _applicationId;
   private String _startContentID;
   static String TO = "to";
   static String BCC = "bcc";
   static String CC = "cc";
   static String FROM = "from";
   private static String SUBJECT_HEADER = "x-mms-subject";
   private static String FROM_HEADER = "x-mms-from";
   private static String CC_HEADER = "x-mms-cc";
   private static String TO_HEADER = "x-mms-to";
   private static String BCC_HEADER = "x-mms-bcc";
   private static String DELIVERY_TIME_HEADER = "x-mms-delivery-time";
   private static String PRIORITY_HEADER = "x-mms-priority";

   String getApplicationId() {
      return this._applicationId;
   }

   Vector getMessagePartVector() {
      return this._messageParts;
   }

   Hashtable getHeaders() {
      return this._headers;
   }

   Vector getTORecipients() {
      Vector v = new Vector();
      int length = this._toList.size();

      for (int i = 0; i < length; i++) {
         v.addElement(this.getActualAddress((String)this._toList.elementAt(i)));
      }

      return v;
   }

   Vector getCCRecipients() {
      Vector v = new Vector();
      int length = this._ccList.size();

      for (int i = 0; i < length; i++) {
         v.addElement(this.getActualAddress((String)this._ccList.elementAt(i)));
      }

      return v;
   }

   Vector getBCCRecipients() {
      Vector v = new Vector();
      int length = this._bccList.size();

      for (int i = 0; i < length; i++) {
         v.addElement(this.getActualAddress((String)this._bccList.elementAt(i)));
      }

      return v;
   }

   @Override
   public MessagePart[] getMessageParts() {
      if (this._messageParts.size() == 0) {
         return null;
      }

      MessagePart[] parts = new MessagePart[this._messageParts.size()];

      for (int i = 0; i < parts.length; i++) {
         parts[i] = (MessagePart)this._messageParts.elementAt(i);
      }

      return parts;
   }

   @Override
   public String getStartContentId() {
      return this._startContentID;
   }

   @Override
   public String getSubject() {
      return this._subject;
   }

   @Override
   public boolean removeAddress(String type, String address) {
      if (type == null) {
         throw new NullPointerException();
      }

      type = type.toLowerCase();
      if (!type.equals(TO) && !type.equals(CC) && !type.equals(BCC)) {
         throw new IllegalArgumentException("Invalid address type");
      }

      if (address != null) {
         if (type.equals(TO)) {
            return this._toList.removeElement(address);
         }

         if (type.equals(CC)) {
            return this._ccList.removeElement(address);
         }

         if (type.equals(BCC)) {
            return this._bccList.removeElement(address);
         }
      }

      return false;
   }

   @Override
   public void removeAddresses() {
      this._toList.removeAllElements();
      this._ccList.removeAllElements();
      this._bccList.removeAllElements();
   }

   @Override
   public void removeAddresses(String type) {
      if (type == null) {
         throw new NullPointerException();
      }

      type = type.toLowerCase();
      if (!type.equals(TO) && !type.equals(CC) && !type.equals(BCC)) {
         throw new IllegalArgumentException("Invalid address type");
      }

      if (type.equals(TO)) {
         this._toList.removeAllElements();
      } else if (type.equals(CC)) {
         this._ccList.removeAllElements();
      } else {
         if (type.equals(BCC)) {
            this._bccList.removeAllElements();
         }
      }
   }

   @Override
   public boolean removeMessagePart(MessagePart part) {
      if (part == null) {
         throw new NullPointerException();
      } else {
         return this._messageParts.removeElement(part);
      }
   }

   @Override
   public boolean removeMessagePartId(String contentID) {
      if (contentID == null) {
         throw new NullPointerException();
      }

      Vector removeVector = new Vector();

      for (int i = 0; i < this._messageParts.size(); i++) {
         MessagePart mp = (MessagePart)this._messageParts.elementAt(i);
         if (mp.getContentID().equals(contentID)) {
            removeVector.addElement(mp);
         }
      }

      boolean removed = false;

      for (int i = 0; i < removeVector.size(); i++) {
         this._messageParts.removeElement(removeVector.elementAt(i));
         removed = true;
      }

      return removed;
   }

   @Override
   public boolean removeMessagePartLocation(String contentLocation) {
      if (contentLocation == null) {
         throw new NullPointerException();
      }

      Vector removeVector = new Vector();

      for (int i = 0; i < this._messageParts.size(); i++) {
         MessagePart mp = (MessagePart)this._messageParts.elementAt(i);
         if (mp.getContentLocation().equals(contentLocation)) {
            removeVector.addElement(mp);
         }
      }

      boolean removed = false;

      for (int i = 0; i < removeVector.size(); i++) {
         this._messageParts.removeElement(removeVector.elementAt(i));
         removed = true;
      }

      return removed;
   }

   @Override
   public void setAddress(String addr) {
      this.addAddress(TO, addr);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void setHeader(String headerField, String headerValue) {
      if (headerField == null) {
         throw new NullPointerException();
      }

      headerField = headerField.toLowerCase();
      if (headerField.equals(SUBJECT_HEADER)
         || headerField.equals(FROM_HEADER)
         || headerField.equals(TO_HEADER)
         || headerField.equals(CC_HEADER)
         || headerField.equals(BCC_HEADER)) {
         throw new SecurityException();
      }

      if (!headerField.equals(DELIVERY_TIME_HEADER) && !headerField.equals(PRIORITY_HEADER)) {
         throw new IllegalArgumentException("Unknown header field");
      }

      if (headerField.equals(PRIORITY_HEADER) && headerValue != null) {
         String lower = headerValue.toLowerCase();
         if (!lower.equals("high") && !lower.equals("low") && !lower.equals("normal")) {
            throw new IllegalArgumentException();
         }
      }

      if (headerField.equals(DELIVERY_TIME_HEADER) && headerValue != null) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            Long.parseLong(headerValue);
            var5 = false;
         } finally {
            if (var5) {
               throw new IllegalArgumentException();
            }
         }
      }

      if (headerValue != null) {
         this._headers.put(headerField, headerValue);
      }
   }

   @Override
   public void setStartContentId(String contentId) {
      if (contentId != null) {
         boolean found = false;

         for (int i = this._messageParts.size() - 1; i >= 0; i--) {
            MessagePart part = (MessagePart)this._messageParts.elementAt(i);
            if (contentId.equals(part.getContentID())) {
               found = true;
               break;
            }
         }

         if (!found) {
            throw new IllegalArgumentException("None of the added MessageParts objects matches the contentId");
         }
      }

      this._startContentID = contentId;
   }

   @Override
   public void setSubject(String subject) {
      this._subject = subject;
   }

   @Override
   public Date getTimestamp() {
      return new Date(this._timeStamp);
   }

   @Override
   public MessagePart getMessagePart(String contentID) {
      if (contentID == null) {
         throw new NullPointerException();
      }

      for (int i = 0; i < this._messageParts.size(); i++) {
         MessagePart mp = (MessagePart)this._messageParts.elementAt(i);
         if (mp.getContentID().equals(contentID)) {
            return mp;
         }
      }

      return null;
   }

   @Override
   public String getHeader(String headerField) {
      if (headerField == null) {
         throw new IllegalArgumentException();
      } else {
         headerField = headerField.toLowerCase();
         if (headerField.equals(SUBJECT_HEADER)
            || headerField.equals(FROM_HEADER)
            || headerField.equals(TO_HEADER)
            || headerField.equals(CC_HEADER)
            || headerField.equals(BCC_HEADER)) {
            throw new SecurityException();
         } else if (!headerField.equals(DELIVERY_TIME_HEADER) && !headerField.equals(PRIORITY_HEADER)) {
            throw new IllegalArgumentException("Unknown header field");
         } else {
            return (String)this._headers.get(headerField);
         }
      }
   }

   @Override
   public String[] getAddresses(String type) {
      Vector v = null;
      type = type.toLowerCase();
      if (type.equals(TO)) {
         v = this._toList;
      } else if (type.equals(CC)) {
         v = this._ccList;
      } else if (type.equals(BCC)) {
         v = this._bccList;
      } else if (type.equals(FROM) && this._fromAddress != null) {
         return new String[]{this._fromAddress};
      }

      if (v != null && v.size() != 0) {
         String[] array = new String[v.size()];

         for (int i = 0; i < v.size(); i++) {
            array[i] = (String)v.elementAt(i);
         }

         return array;
      } else {
         return null;
      }
   }

   @Override
   public String getAddress() {
      if (this._fromAddress != null) {
         return this._fromAddress;
      } else {
         return this._toList.size() > 0 ? (String)this._toList.elementAt(0) : null;
      }
   }

   @Override
   public void addMessagePart(MessagePart part) {
      if (part == null) {
         throw new NullPointerException("MessagePart is null");
      }

      String contentId = part.getContentID();

      for (int i = 0; i < this._messageParts.size(); i++) {
         MessagePart mp = (MessagePart)this._messageParts.elementAt(i);
         if (mp.getContentID().equals(contentId)) {
            throw new IllegalArgumentException("Content-ID of the MessagePart conflicts with Messagepart already contained");
         }
      }

      this._messageParts.addElement(part);
   }

   @Override
   public boolean addAddress(String type, String address) {
      if (type != null && (address == null || !address.trim().equals(""))) {
         type = type.toLowerCase();
         if (!type.equals(TO) && !type.equals(CC) && !type.equals(BCC)) {
            throw new IllegalArgumentException("Invalid address type");
         }

         if (address == null) {
            return false;
         }

         String addr = null;
         if (address.startsWith("mms://")) {
            addr = address.substring(6);
         } else {
            addr = address;
         }

         int index = addr.lastIndexOf(58);
         if (index != -1) {
            String applicationId = addr.substring(index + 1);
            if (this._applicationId != null && !this._applicationId.equals(applicationId)) {
               throw new IllegalArgumentException("Addresses do not have the same Application ID");
            }

            this._applicationId = applicationId;
         }

         if (!Protocol.isValid(index != -1 ? addr.substring(0, index) : addr)) {
            throw new IllegalArgumentException("Invalid address");
         }

         if (address == null) {
            return false;
         }

         if (type.equals(TO)) {
            this._toList.addElement(address);
            return true;
         }

         if (type.equals(CC)) {
            this._ccList.addElement(address);
            return true;
         }

         if (type.equals(BCC)) {
            this._bccList.addElement(address);
         }

         return true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public MultipartMessageImpl(Vector bcc, Vector cc, Vector to, Hashtable headers, Vector messageParts, String from, String subject, long timeStamp) {
      this._bccList = bcc;
      this._ccList = cc;
      this._toList = to;
      this._headers = headers;
      this._messageParts = messageParts;
      this._subject = subject;
      this._fromAddress = from;
      this._timeStamp = timeStamp;
   }

   private String getActualAddress(String addr) {
      if (addr.startsWith("mms://")) {
         addr = addr.substring(6);
      }

      int index = addr.lastIndexOf(58);
      if (index != -1) {
         addr = addr.substring(0, index);
      }

      return addr;
   }

   public MultipartMessageImpl() {
      this._messageParts = new Vector();
      this._bccList = new Vector();
      this._ccList = new Vector();
      this._toList = new Vector();
      this._headers = new Hashtable();
   }
}
