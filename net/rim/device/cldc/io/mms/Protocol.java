package net.rim.device.cldc.io.mms;

import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.verbs.MMSSendVerb;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.internal.firewall.Firewall;

public class Protocol implements MessageConnection, StreamConnection, ConnectionBaseInterface {
   private boolean _isOpen;
   private boolean _isServerMode;
   private String _address;
   private Object _receiveSem = new Object();
   private MessageListener _listener;
   private long _timeout;
   private Vector _messagequeue = new Vector();
   private String _applicationID;
   private static final long ID = 8757735821348010629L;
   public static String APPLICATION_ID = "application-id=";
   public static String REPLY_TO_APPLICATION_ID = "reply-to-application-id=";
   private static Hashtable _connectionTable;
   private static long DEFAULT_TIMEOUT = 30000;

   @Override
   public void close() {
      synchronized (this) {
         this._isOpen = false;
      }
   }

   @Override
   public Connection openPrim(String name, int mode, boolean timeouts) {
      this._address = "mms:" + name;
      if (name.startsWith("//")) {
         name = name.substring(2);
      }

      if (!isValid(name)) {
         throw new IllegalArgumentException("Invalid url");
      }

      int index = name.lastIndexOf(58);
      if (index != 0) {
         if (mode == 1) {
            throw new IllegalArgumentException();
         }

         if (index != -1) {
            this._applicationID = name.substring(index + 1);
         }
      } else {
         this._isServerMode = true;
         this._address = null;
         this._applicationID = name.substring(1);
         this._applicationID = this._applicationID.toLowerCase();
         if (this._applicationID.length() > 32) {
            throw new IllegalArgumentException("Application ID cannot exceed 32 chars");
         }

         synchronized (_connectionTable) {
            if (_connectionTable.containsKey(this._applicationID)) {
               WeakReference ref = (WeakReference)_connectionTable.get(this._applicationID);
               if (ref.get() != null && ((Protocol)ref.get())._isOpen) {
                  throw new IOException("Application ID already in use");
               }
            }

            _connectionTable.put(this._applicationID, new WeakReference(this));
         }
      }

      this._isOpen = true;
      if (timeouts) {
         this._timeout = DEFAULT_TIMEOUT;
      }

      return this;
   }

   public void mmsMessageReceived(MultipartMessage msg) {
      synchronized (this._messagequeue) {
         this._messagequeue.addElement(msg);
      }

      synchronized (this) {
         if (this._listener != null) {
            this._listener.notifyIncomingMessage(this);
         }
      }

      synchronized (this._receiveSem) {
         this._receiveSem.notifyAll();
      }
   }

   public String getApplicationID() {
      return this._applicationID;
   }

   @Override
   public Message newMessage(String type) {
      if (!type.equals("multipart")) {
         throw new IllegalArgumentException("this message type is not supported");
      }

      MultipartMessageImpl msg = new MultipartMessageImpl();
      msg.setAddress(this._address);
      return msg;
   }

   @Override
   public Message newMessage(String type, String address) {
      if (!type.equals("multipart")) {
         throw new IllegalArgumentException("this message type is not supported");
      }

      MultipartMessageImpl msg = new MultipartMessageImpl();
      msg.setAddress(address);
      return msg;
   }

   @Override
   public void send(Message msg) throws IOException {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("mms_send", null, false)) {
         throw new SecurityException("Permission denied");
      }

      if (!this._isOpen) {
         throw new IOException("operation not permitted on a closed connection");
      }

      if (msg == null) {
         throw new NullPointerException();
      }

      if (msg instanceof MultipartMessage) {
         MultipartMessageImpl mMsg = (MultipartMessageImpl)msg;
         if (mMsg.getAddresses(MultipartMessageImpl.TO) == null) {
            throw new IllegalArgumentException();
         }

         MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
         builder.setSubject(mMsg.getSubject());
         Vector ccList = mMsg.getCCRecipients();
         if (ccList.size() != 0) {
            builder.addCcRecipients(ccList);
         }

         Vector toList = mMsg.getTORecipients();
         if (toList.size() != 0) {
            builder.addRecipients(toList);
         }

         Vector bccList = mMsg.getBCCRecipients();
         if (bccList.size() != 0) {
            builder.addBccRecipients(bccList);
         }

         Hashtable table = mMsg.getHeaders();
         Enumeration en = table.keys();

         while (en.hasMoreElements()) {
            String key = (String)en.nextElement();
            String value = (String)table.get(key);
            builder.setAttribute(key, value);
         }

         StringBuffer sbuffer = new StringBuffer();
         sbuffer.append("application/vnd.wap.multipart.related");
         String appId = mMsg.getApplicationId();
         if (appId != null) {
            sbuffer.append(';');
            sbuffer.append(APPLICATION_ID);
            sbuffer.append(appId);
         }

         if (this._applicationID != null) {
            sbuffer.append(';');
            sbuffer.append(REPLY_TO_APPLICATION_ID);
            sbuffer.append(this._applicationID);
         }

         if (sbuffer.length() > 0) {
            builder.setAttribute("content-type", sbuffer.toString());
         }

         Vector mParts = mMsg.getMessagePartVector();
         en = mParts.elements();

         while (en.hasMoreElements()) {
            MessagePart part = (MessagePart)en.nextElement();
            int mimeType = MMSUtilities.getMIMEType(part.getMIMEType());
            builder.addAttachment(part.getContentID(), mimeType, part.getContent(), part.getEncoding());
         }

         MMSMessageModel message = builder.getResult();
         ContextObject context = new ContextObject();
         context.setPrivateFlag(3826502739478037178L, 0);
         MMSSendVerb.send(message, message.getPayload(), message.getAttachmentDataProvider(), context);
      }
   }

   @Override
   public Message receive() throws IOException {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("mms_receive", null, false)) {
         throw new SecurityException("Permission denied");
      }

      if (!this._isServerMode) {
         throw new IOException("operation not permitted on a client connection");
      }

      if (!this._isOpen) {
         throw new IOException("Connection is already closed");
      }

      synchronized (this._messagequeue) {
         if (this._messagequeue.size() > 0) {
            Message m = (Message)this._messagequeue.firstElement();
            this._messagequeue.removeElement(m);
            return m;
         }
      }

      synchronized (this._receiveSem) {
         label84:
         try {
            this._receiveSem.wait(this._timeout);
         } finally {
            break label84;
         }
      }

      synchronized (this._messagequeue) {
         if (this._messagequeue.size() > 0) {
            Message m = (Message)this._messagequeue.firstElement();
            this._messagequeue.removeElement(m);
            return m;
         } else {
            return null;
         }
      }
   }

   @Override
   public void setMessageListener(MessageListener l) throws IOException {
      if (!ControlledAccess.verifyRRISignatures(true) && !Firewall.getInstance().allowConnection("mms_receive", null, false)) {
         throw new SecurityException("Permission denied");
      }

      if (!this._isServerMode) {
         throw new IOException("operation not permitted on a client connection");
      }

      if (!this._isOpen) {
         throw new IOException("Connection is already closed");
      }

      this._listener = l;
   }

   @Override
   public int numberOfSegments(Message msg) {
      return 1;
   }

   @Override
   public int getProperties(String name) {
      return 2;
   }

   @Override
   public InputStream openInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public DataInputStream openDataInputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public OutputStream openOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      throw new IllegalArgumentException("Not supported");
   }

   public static Protocol getConnection(String contentType) {
      if (contentType == null) {
         return null;
      }

      contentType = contentType.toLowerCase();
      int index = contentType.indexOf(APPLICATION_ID);
      if (index == -1) {
         return null;
      }

      String applicationID = contentType.substring(index + APPLICATION_ID.length());
      index = applicationID.indexOf(59);
      if (index != -1) {
         applicationID = applicationID.substring(0, index);
      }

      synchronized (_connectionTable) {
         if (_connectionTable.containsKey(applicationID)) {
            WeakReference ref = (WeakReference)_connectionTable.get(applicationID);
            if (ref.get() != null && ((Protocol)ref.get())._isOpen) {
               return (Protocol)ref.get();
            }
         }

         return null;
      }
   }

   public static MultipartMessageImpl createWMAMessage(MMSMessageModel message, String contentType) {
      MMSPayloadModel payload = message.getPayload();
      Vector bccList = copyRecipients(payload.getBccRecipients());
      Vector ccList = copyRecipients(payload.getCcRecipients());
      Vector toList = copyRecipients(payload.getRecipients());
      Hashtable headers = new Hashtable();
      Enumeration en = payload.attributeNames();
      if (en != null) {
         while (en.hasMoreElements()) {
            String str = (String)en.nextElement();
            headers.put(str, payload.getAttribute(str));
         }
      }

      AttachmentDataProvider adp = message.getAttachmentDataProvider();
      en = adp.attachmentNames();
      Vector parts = new Vector();
      if (en != null) {
         int contentId = 0;

         while (en.hasMoreElements()) {
            String str = (String)en.nextElement();
            MMSAttachment attachment = adp.getAttachment(str);

            try {
               MessagePart part = new MessagePart(
                  attachment.getData(), MMSUtilities.getMIMETypeString(attachment.getType()), String.valueOf(contentId++), str, attachment.getCharset()
               );
               parts.addElement(part);
            } finally {
               continue;
            }
         }
      }

      String subject = payload.getAttribute("subject");
      RIMModel sender = payload.getSender();
      String senderStr = null;
      if (!(sender instanceof PhoneNumberModel)) {
         if (sender instanceof FriendlyNameAddressModel) {
            senderStr = ((FriendlyNameAddressModel)sender).getAddress();
         }
      } else {
         senderStr = ((PhoneNumberModel)sender).getValue();
      }

      if (contentType != null) {
         contentType = contentType.toLowerCase();
         int index = contentType.indexOf(REPLY_TO_APPLICATION_ID);
         String applicationID = null;
         if (index != -1) {
            applicationID = contentType.substring(index + REPLY_TO_APPLICATION_ID.length());
            index = applicationID.indexOf(59);
            if (index != -1) {
               applicationID = applicationID.substring(0, index);
            }
         }

         if (applicationID == null) {
            senderStr = "mms://" + senderStr;
         } else {
            senderStr = "mms://" + senderStr + ":" + applicationID;
         }
      }

      return new MultipartMessageImpl(bccList, ccList, toList, headers, parts, senderStr, subject, payload.getCreationDate());
   }

   private static Vector copyRecipients(Vector recipients) {
      Vector v = new Vector();
      if (recipients != null) {
         for (int i = recipients.size() - 1; i >= 0; i--) {
            Object obj = recipients.elementAt(i);
            if (obj instanceof String) {
               v.addElement(obj);
            } else if (obj instanceof PhoneNumberModel) {
               v.addElement(((PhoneNumberModel)obj).getValue());
            } else if (obj instanceof FriendlyNameAddressModel) {
               v.addElement(((FriendlyNameAddressModel)obj).getAddress());
            }
         }
      }

      return v;
   }

   static boolean isValid(String s) {
      s = s.toLowerCase();
      int length = s.length();
      if (s.startsWith("+")) {
         for (int i = 1; i < length; i++) {
            if (!Character.isDigit(s.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c) && (c < 'a' || c > 'z') && c != '.' && c != '@' && c != '_' && c != ':') {
               return false;
            }
         }

         return true;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _connectionTable = (Hashtable)ar.getOrWaitFor(8757735821348010629L);
      if (_connectionTable == null) {
         _connectionTable = new Hashtable();
         ar.put(8757735821348010629L, _connectionTable);
      }
   }
}
