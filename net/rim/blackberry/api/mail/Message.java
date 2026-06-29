package net.rim.blackberry.api.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.blackberry.api.mail.event.MessageListener;
import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailBuilder;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public class Message implements Part {
   private EmailMessageModel _msg;
   private int _messageType;
   protected Folder _folder;
   private String _contentType = CMIMEUtilities.getTextContentType();
   public static int PIN_MESSAGE = 94;
   public static int EMAIL_MESSAGE = 43;
   static Hashtable _headerHandlers = (Hashtable)(new Object());

   public Folder getFolder() {
      return this._folder;
   }

   public void setFrom(Address address) {
      String[] fromAddr;
      if (address instanceof PINAddress) {
         fromAddr = new Object[]{EmailSendUtility.getDevicePINString(), address.getName()};
      } else {
         fromAddr = new Object[]{address.getAddr(), address.getName()};
      }

      this.removeHeader(Header.FROM);
      ContextObject creationContext = (ContextObject)(new Object());
      creationContext.put(251, fromAddr);
      creationContext.put(-4054673099568009991L, HeaderTypes._typesAsInteger[3]);
      PersistableRIMModel model = (PersistableRIMModel)FactoryUtil.createInstance(-8034039608019345282L, creationContext);
      this.add(model);
   }

   public Address getFrom() {
      EmailHeaderModel[] fromAddresses = this.getHeader(3);
      return fromAddresses.length > 0 ? this.createAddressFromEmailHeaderModel(this.getHeader(3)[0]) : null;
   }

   EmailHeaderModel[] getHeader(int headerType) {
      ReadableList l = this._msg;
      int size = l.size();
      Vector v = (Vector)(new Object(size));

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            if (model.getHeaderType() == headerType) {
               v.addElement(model);
            }
         }
      }

      EmailHeaderModel[] array = new Object[v.size()];
      v.copyInto(array);
      return array;
   }

   public boolean removeAllRecipients(int type) throws MessagingException {
      if (type != 2 && type != 1 && type != 0) {
         throw new MessagingException("Recipient type should be RecipientType.BCC or RecipientType.CC or RecipientType.TO");
      }

      ReadableList l = this._msg;
      int size = l.size();
      Vector toRemove = (Vector)(new Object());

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel headerModel = (EmailHeaderModel)element;
            if (headerModel.getHeaderType() == type) {
               toRemove.addElement(headerModel);
            }
         }
      }

      size = toRemove.size();

      for (int i = 0; i < size; i++) {
         this._msg.remove(toRemove.elementAt(i));
      }

      return true;
   }

   public boolean removeRecipients(int type, Address[] list) throws MessagingException {
      if (list == null) {
         throw new Object("Address array is null");
      }

      if (type != 2 && type != 1 && type != 0) {
         throw new MessagingException("Recipient type should be RecipientType.BCC or RecipientType.CC or RecipientType.TO");
      }

      ReadableList l = this._msg;
      Vector toRemove = (Vector)(new Object());
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel headerModel = (EmailHeaderModel)element;
            if (headerModel.getHeaderType() == type) {
               String[] names = new Object[2];
               boolean found = false;
               headerModel.extractNames(names);

               for (int j = 0; j < list.length; j++) {
                  if (list[j]._addr.equals(names[0])) {
                     if (list[j]._name != null) {
                        if (list[j]._name.equals(names[1])) {
                           found = true;
                           break;
                        }
                     } else if (names[1] == null) {
                        found = true;
                        break;
                     }
                  }
               }

               if (found) {
                  toRemove.addElement(headerModel);
               }
            }
         }
      }

      size = toRemove.size();

      for (int i = 0; i < size; i++) {
         this._msg.remove(toRemove.elementAt(i));
      }

      return true;
   }

   public void addRecipient(int type, Address address) {
      if (type != 2 && type != 1 && type != 0) {
         throw new Object("Invalid type; type should be TO, CC or BCC");
      }

      if (address == null) {
         throw new Object("address==null");
      }

      this.addRecipients(type, new Address[]{address});
   }

   public void addRecipients(int type, Address[] addresses) throws MessagingException {
      if (!ITPolicy.getBoolean(16, true) && type == 2) {
         throw new MessagingException("Do not have permissions to add BCC addresses to the Message");
      }

      for (int i = 0; i < addresses.length; i++) {
         if (addresses[i] instanceof PINAddress) {
            if (this._messageType == EMAIL_MESSAGE) {
               throw new MessagingException(" Recipient address types cannot be both PIN and Email types");
            }

            this._messageType = PIN_MESSAGE;
         } else if (addresses[i] instanceof Address) {
            if (this._messageType == PIN_MESSAGE) {
               throw new MessagingException(" Recipient address types cannot be both PIN and Email types");
            }

            this._messageType = EMAIL_MESSAGE;
         }
      }

      if (this._messageType == PIN_MESSAGE) {
         this.addPINRecipients(type, addresses);
      } else {
         String[] names = new Object[2];
         ContextObject contextObject = (ContextObject)(new Object());
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Factory factory = (Factory)ar.waitFor(-2985347935260258684L);
         Object[] payloadandflag = this.beginChanges();

         for (int i = 0; i < addresses.length; i++) {
            names[0] = addresses[i].getAddr();
            names[1] = addresses[i].getName();
            contextObject.reset();
            contextObject.put(251, names);
            Object recipient = factory.createInstance(contextObject);
            EmailBuilderApi.addRecipient(this._msg, type, (RIMModel)recipient);
         }

         this.endChanges(payloadandflag);
      }
   }

   public int getMessageType() {
      return this._msg.flagsSet(8192) ? PIN_MESSAGE : EMAIL_MESSAGE;
   }

   public Address[] getRecipients(int type) {
      Vector retV = (Vector)(new Object());
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            if (model.getHeaderType() == type) {
               Address address = this.createAddressFromEmailHeaderModel(model);
               if (address != null && address._addr != null && !address._addr.trim().equals("")) {
                  retV.addElement(this.createAddressFromEmailHeaderModel(model));
               }
            }
         }
      }

      Address[] addrs = new Address[retV.size()];
      retV.copyInto(addrs);
      return addrs;
   }

   public void setAddressesToFreeForm(boolean isFreeForm) {
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            PersistableRIMModel prm = model.getInsideModel();
            if (prm instanceof Object) {
               FriendlyNameAddressModel addressModel = (FriendlyNameAddressModel)prm;
               addressModel.setFreeForm(isFreeForm);
            }
         }
      }
   }

   public void setReplyTo(Address[] addresses) {
      String[] addr = new Object[2];
      ContextObject creationContext = (ContextObject)(new Object());
      Object[] payloadandflag = this.beginChanges();
      ReadableList l = this._msg;

      for (int i = l.size() - 1; i >= 0; i--) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            if (model.getHeaderType() == 5) {
               this._msg.remove(model);
            }
         }
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory factory = (Factory)ar.waitFor(-8034039608019345282L);

      for (int i = 0; i < addresses.length; i++) {
         addr[0] = addresses[i].getAddr();
         addr[1] = addresses[i].getName();
         creationContext.reset();
         creationContext.put(251, addr);
         creationContext.put(-4054673099568009991L, HeaderTypes._typesAsInteger[5]);
         PersistableRIMModel model = (PersistableRIMModel)factory.createInstance(creationContext);
         this._msg.add(model);
      }

      this.endChanges(payloadandflag);
   }

   public Address[] getReplyTo() {
      Vector retV = (Vector)(new Object());
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            EmailHeaderModel model = (EmailHeaderModel)element;
            if (model.getHeaderType() == 5) {
               retV.addElement(this.createAddressFromEmailHeaderModel(model));
            }
         }
      }

      Address[] addrs = new Address[retV.size()];
      retV.copyInto(addrs);
      return addrs;
   }

   public void setSubject(String subject) {
      Object[] payloadandflag = this.beginChanges();
      boolean foundExistingSubject = false;
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            foundExistingSubject = true;
            SubjectModel subjectModel = (SubjectModel)element;
            if (subject == null) {
               this._msg.remove(subjectModel);
            } else {
               subjectModel.setSubject(subject);
            }
            break;
         }
      }

      if (!foundExistingSubject && subject != null) {
         EmailBuilderApi.addSubjectLine(this._msg, subject);
      }

      this.endChanges(payloadandflag);
   }

   public String getSubject() {
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            SubjectModel subjectModel = (SubjectModel)element;
            String s = subjectModel.toString();
            if (s != null && s.length() != 0) {
               return s;
            }

            return null;
         }
      }

      return null;
   }

   public void setSentDate(Date date) {
      this._msg.setTimestamp(date.getTime());
   }

   public Date getSentDate() {
      return (Date)(new Object(this._msg.getTimestamp()));
   }

   public Date getReceivedDate() {
      EmailPayloadModel epm = this._msg.getPayload();
      return (Date)(new Object(epm.getCreationDate()));
   }

   public void setFlags(int mask) {
      if ((mask & 128) != 0) {
         this._msg.setPriority((byte)2);
         mask &= -129;
      }

      this._msg.changeStatus(mask, 0, 0, 0, true, true, false, true, null);
   }

   public void clearFlags(int mask) {
      if ((mask & 128) != 0) {
         this._msg.setPriority((byte)1);
         mask &= -129;
      }

      this._msg.changeStatus(0, mask, 0, 0, true, true, false, true, null);
   }

   public int getFlags() {
      int mask = this._msg.getFlags();
      if (this._msg.getPriority() == 2) {
         mask |= 128;
      }

      return mask;
   }

   public void setFlag(int flag, boolean set) {
      if (set) {
         this.setFlags(flag);
      } else {
         this.clearFlags(flag);
      }
   }

   public boolean isSet(int flag) {
      return (flag & 128) != 0 ? this._msg.getPriority() == 2 : this._msg.flagsSet(flag);
   }

   public byte getPriority() {
      return this._msg.getPriority();
   }

   public void setPriority(byte p) {
      this._msg.setPriority(p);
   }

   public boolean isInbound() {
      return this._msg.getPayload().inbound();
   }

   public void setInbound(boolean b) {
      this._msg.setInbound(b);
      if (b) {
         this.clearFlags(1);
      }
   }

   public Message reply(boolean replyToAll) {
      return this.reply(replyToAll, false);
   }

   public Message reply(boolean replyToAll, boolean replyWithOriginalText) throws MessagingException {
      EmailMessageModelImpl emmi = (EmailMessageModelImpl)this._msg;
      long fid = emmi.getFolderId();
      if (fid == 0) {
         throw new MessagingException("Unable to reply to this message. Source folder is empty");
      }

      EmailFolder ef = this.getInternalSentFolder();
      Message newMsg = new Message(new Folder(ef));
      EmailMessageModelImpl newemmi = (EmailMessageModelImpl)newMsg.getEmailMessageModel();
      if (replyToAll && replyWithOriginalText) {
         newemmi.setType((byte)8);
      } else if (!replyToAll && replyWithOriginalText) {
         newemmi.setType((byte)2);
      } else if (replyToAll && !replyWithOriginalText) {
         newemmi.setType((byte)4);
      } else {
         newemmi.setType((byte)1);
      }

      newemmi.setFolderId(fid);
      ContextObject c = (ContextObject)(new Object());
      EmailMessageModelImpl emmiClone = (EmailMessageModelImpl)(new Object(c, true));
      emmiClone.setPayload((EmailPayloadModel)((CloneProvider)emmi.getPayload()).clone(c));
      emmiClone.setSensitivity(emmi.getSensitivity());
      emmiClone.setPriority(emmi.getPriority());
      emmiClone.setGMEReferenceIdentifier(emmi.getGMEReferenceIdentifier());
      emmiClone.setType(emmi.getType());
      emmiClone.setTimestamp(emmi.getSentDate());
      c.put(245, emmiClone);
      if (replyToAll && replyWithOriginalText) {
         c.setFlag(30);
      } else if (!replyToAll && replyWithOriginalText) {
         c.setFlag(53);
      } else if (replyToAll && !replyWithOriginalText) {
         c.setFlag(29);
      } else {
         c.setFlag(12);
      }

      EmailBuilder.makeReply(newemmi, c, replyToAll, replyWithOriginalText);
      return newMsg;
   }

   public Message forward() {
      EmailMessageModelImpl original = (EmailMessageModelImpl)this._msg;
      EmailFolder ef = this.getInternalSentFolder();
      Message m = new Message(new Folder(ef));
      EmailMessageModelImpl emmi = (EmailMessageModelImpl)m.getEmailMessageModel();
      emmi.setFolderId(ef.getLUID());
      EmailPayloadModelImpl epmi = (EmailPayloadModelImpl)original.getPayload();
      if (epmi instanceof Object) {
         CloneProvider cp = epmi;
         epmi = (EmailPayloadModelImpl)cp.clone(null);
      }

      emmi.add(epmi);
      emmi.setType((byte)16);
      emmi.setPriority(original.getPriority());
      String subject = this.getSubject();
      if (subject == null) {
         m.setSubject(EmailResources.getString(59));
         return m;
      } else {
         m.setSubject(((StringBuffer)(new Object())).append(EmailResources.getString(59)).append(' ').append(subject).toString());
         return m;
      }
   }

   public int getMessageId() {
      return this._msg.getCMIMEReferenceIdentifier();
   }

   void removeFromFolderAndSetDeleted() {
      Object[] payloadandflag = this.beginChanges();
      this._msg.setFolderId(0);
      this._msg.setCMIMEReferenceIdentifier(0);
      this._msg.clearFlags(-1);
      this._msg.setFlags(1);
      this._msg.setFlags(262144);
      this.endChanges(payloadandflag);
   }

   public void updateUi() {
      ContextObject c = (ContextObject)(new Object());
      c.put(7801730636987331473L, this._msg);
      ModelViewListenerRegistry.notifyOfOpenedModelChange(this._msg, this._msg, c);
   }

   Address createAddressFromEmailHeaderModel(EmailHeaderModel model) {
      if (model == null) {
         return null;
      }

      String[] addrs = new Object[2];
      model.convert(model, addrs);
      return new Address(addrs[0], addrs[1]);
   }

   public Object getInternalModel() {
      return this._msg;
   }

   EmailMessageModel getEmailMessageModel() {
      return this._msg;
   }

   void add(Object o) {
      Object[] payloadandflag = this.beginChanges();
      this._msg.add(o);
      this.endChanges(payloadandflag);
   }

   void remove(Object o) {
      Object[] payloadandflag = this.beginChanges();
      this._msg.remove(o);
      this.endChanges(payloadandflag);
   }

   boolean setEmailMessageModel(EmailMessageModel msg) {
      this._msg = msg;
      return true;
   }

   public String getBodyText() {
      BodyModel bm = this.getInternalBody();
      return bm != null ? bm.getText() : null;
   }

   public void removeMessageListener(MessageListener listener) {
      ListenerManager lm = ListenerManager.getInstance();
      lm.removeMessageListener(this.getEmailMessageModel().getUID(), listener);
   }

   public void addMessageListener(MessageListener listener) {
      ApplicationControl.assertIPCAllowed(true);
      ListenerManager lm = ListenerManager.getInstance();
      int uid = this._msg.getUID();
      if (uid == 0) {
         EmailSendUtility.assignCMIMEReferenceIdentifierToMessage(this._msg, null);
         uid = this._msg.getUID();
      }

      lm.addMessageListener(uid, listener);
   }

   public int getTransmissionError() {
      return this._msg.getTransmissionError();
   }

   public int getStatus() {
      return this._msg.getStatus();
   }

   public void setStatus(int messageStatus, int messageTransmissionError) {
      if (this._msg.getStatus() != 8191 && this._msg.getStatus() != 1) {
         this._msg.changeStatus(0, 0, messageStatus, messageTransmissionError, true, true, false, false, null);
      } else {
         ((EmailMessageModelImpl)this._msg).setStatus(messageStatus, messageTransmissionError, true);
      }
   }

   @Override
   public int getSize() {
      Object o = this.getContent();
      if (o instanceof TextBodyPart) {
         TextBodyPart tbp = (TextBodyPart)o;
         return tbp.getSize();
      }

      if (!(o instanceof Multipart)) {
         return -1;
      }

      Multipart m = (Multipart)o;
      int size = 0;

      for (int i = m.getCount() - 1; i >= 0; i--) {
         size += m.getBodyPart(i).getSize();
      }

      return size;
   }

   @Override
   public void writeTo(OutputStream os) {
      this.writeTo(os, this);
   }

   @Override
   public InputStream getInputStream() {
      Object o = this.getContent();
      if (o instanceof Multipart) {
         Multipart mp = (Multipart)o;

         for (int i = mp.getCount() - 1; i >= 0; i--) {
            BodyPart p = mp.getBodyPart(i);
            if (p instanceof TextBodyPart) {
               o = p;
            }
         }
      }

      if (!(o instanceof TextBodyPart)) {
         return null;
      }

      TextBodyPart bp = (TextBodyPart)o;
      String s = (String)bp.getContent();
      ByteArrayInputStream bais = (ByteArrayInputStream)(new Object(s.getBytes()));
      return bais;
   }

   @Override
   public boolean isMimeType(String mimeType) {
      return mimeType.indexOf(this._contentType) == 0;
   }

   @Override
   public String getContentType() {
      return this._contentType;
   }

   @Override
   public Object getContent() {
      Multipart mp = new Multipart(this);
      if (this._msg.getAttachmentCount() > 0) {
         return mp;
      } else {
         return mp.getCount() > 0 ? mp.getBodyPart(0) : null;
      }
   }

   @Override
   public void setContent(Object content) throws MessagingException {
      BodyModel body = createBodyModel("");
      boolean foundBodyModel = false;
      MailConverterManager mcm = MailConverterManager.getInstance();
      Object[] payloadandflag = this.beginChanges();
      ReadableList l = this._msg;
      Vector elementsToRemove = (Vector)(new Object());
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (!(element instanceof Object)) {
            MailConverter mc = mcm.getConverter(element);
            if (mc != null) {
               elementsToRemove.addElement(element);
            }
         } else {
            body = (BodyModel)element;
            foundBodyModel = true;
         }
      }

      if (content instanceof Object) {
         body.setText((String)content);
         if (!foundBodyModel) {
            this.add(body);
         }
      } else if (!(content instanceof Multipart)) {
         if (content != null) {
            this.endChanges(payloadandflag);
            throw new MessagingException("MailAPI: can't set content to specified parameter");
         }

         if (foundBodyModel) {
            this.remove(body);
         }
      } else {
         Multipart mp = (Multipart)content;
         size = mp.getCount();

         for (int i = 0; i < size; i++) {
            Part p = mp.getBodyPart(i);
            if (!(p instanceof TextBodyPart)) {
               MailConverter mc = mcm.getConverter(p);
               if (mc != null) {
                  this._msg.setAttachmentCount(this._msg.getAttachmentCount() + 1);
                  this.add(mc.convert(p, this._msg));
               }
            } else {
               TextBodyPart tbp = (TextBodyPart)p;
               body.setText(((StringBuffer)(new Object())).append(body.getText()).append((String)tbp.getContent()).toString());
               if (!foundBodyModel) {
                  this.add(body);
                  foundBodyModel = true;
               }
            }
         }
      }

      for (int i = elementsToRemove.size() - 1; i >= 0; i--) {
         this.remove(elementsToRemove.elementAt(i));
      }

      this._msg.setAttachmentCount(this._msg.getAttachmentCount() - elementsToRemove.size());
      this.endChanges(payloadandflag);
   }

   @Override
   public Enumeration getAllHeaders() {
      Vector v = (Vector)(new Object());
      Enumeration e = _headerHandlers.keys();

      while (e.hasMoreElements()) {
         HeaderHandler hh = (HeaderHandler)_headerHandlers.get(e.nextElement());
         hh.getHeaderObjects(v, this);
      }

      return v.elements();
   }

   @Override
   public String[] getHeader(String header) {
      HeaderHandler hh = (HeaderHandler)_headerHandlers.get(header.toLowerCase());
      return hh != null ? hh.getHeader(header, this) : null;
   }

   @Override
   public void removeHeader(String header) {
      HeaderHandler hh = (HeaderHandler)_headerHandlers.get(header.toLowerCase());
      if (hh != null) {
         hh.removeHeader(header, this);
      }
   }

   @Override
   public void setHeader(String header, String value) {
      HeaderHandler hh = (HeaderHandler)_headerHandlers.get(header.toLowerCase());
      if (hh != null) {
         hh.setHeader(header, value, this);
      }
   }

   @Override
   public void addHeader(String header, String value) {
      HeaderHandler hh = (HeaderHandler)_headerHandlers.get(header.toLowerCase());
      if (hh != null) {
         hh.addHeader(header, value, this);
      }
   }

   private BodyModel getInternalBody() {
      ReadableList l = this._msg;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         if (element instanceof Object) {
            return (BodyModel)element;
         }
      }

      return null;
   }

   private void writeTo(OutputStream os, Message msg) {
      Enumeration e = msg.getAllHeaders();

      while (e.hasMoreElements()) {
         Header h = (Header)e.nextElement();
         os.write(h.getName().getBytes());
         os.write(h.getValue().getBytes());
         os.write(BodyPart.CRLF);
      }

      os.write(BodyPart.CRLF);
      Object o = msg.getContent();
      if (!(o instanceof TextBodyPart)) {
         if (o instanceof Multipart) {
            Multipart m = (Multipart)o;
            m.writeTo(os);
         }
      } else {
         TextBodyPart tbp = (TextBodyPart)o;
         os.write(((String)tbp.getContent()).getBytes());
      }

      int size = msg._msg.size();

      for (int i = 0; i < size; i++) {
         RIMModel model = (RIMModel)msg._msg.getAt(i);
         if (model instanceof Object) {
            Message message = new Message();
            message._msg.setPayload((EmailPayloadModel)model);
            os.write(EmailResources.getString(50).getBytes());
            os.write(BodyPart.CRLF);
            this.writeTo(os, message);
         }
      }

      os.write(BodyPart.CRLF);
      os.write(BodyPart.CRLF);
      os.flush();
   }

   Message(EmailMessageModel msg) {
      this._msg = msg;
      long folderId = msg.getFolderId();

      try {
         Store store = DefaultService.getInstance().getStore();
         this._folder = store.getFolder(folderId);
      } catch (FolderNotFoundException var5) {
      } catch (NoSuchServiceException var6) {
      }
   }

   private EmailFolder getInternalSentFolder() {
      EmailMessageModelImpl emmi = (EmailMessageModelImpl)this._msg;
      long fid = emmi.getFolderId();
      EmailHierarchy eh = EmailHierarchy.getEmailHierarchyForFolder(fid);
      if (eh == null) {
         eh = EmailHierarchy.getAnonymousEmailHierarchy();
      }

      fid = eh.getSentFolder();
      EmailFolder ef = (EmailFolder)eh.getFolder(fid);
      return ef;
   }

   static EmailMessageModel createEmailMessageModel() {
      return (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
   }

   public Message() {
      this._msg = createEmailMessageModel();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof Message)) {
         return false;
      }

      Message m = (Message)o;
      return this._msg.equals(m.getEmailMessageModel());
   }

   @Override
   public int hashCode() {
      return this._msg.hashCode();
   }

   private Object[] beginChanges() {
      Boolean grouped = Boolean.FALSE;
      EmailPayloadModel oldpayload = this._msg.getPayload();
      if (ObjectGroup.isInGroup(oldpayload)) {
         grouped = Boolean.TRUE;
         this._msg.setPayload((EmailPayloadModel)ObjectGroup.expandGroup(oldpayload));
      }

      return new Object[]{oldpayload, grouped};
   }

   private void endChanges(Object[] payloadandflag) {
      if (payloadandflag[1]) {
         EmailModifier.endChanges(this._msg, (EmailPayloadModel)payloadandflag[0], null);
      }
   }

   private void addPINRecipients(int type, Address[] addresses) {
      String[] names = new Object[2];
      ContextObject contextObject = (ContextObject)(new Object());
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory factory = (Factory)ar.waitFor(4246852237058296601L);
      Object[] payloadandflag = this.beginChanges();

      for (int i = 0; i < addresses.length; i++) {
         names[0] = addresses[i].getAddr();
         names[1] = addresses[i].getName();
         contextObject.reset();
         contextObject.put(251, names);
         Object recipient = factory.createInstance(contextObject);
         EmailBuilderApi.addRecipient(this._msg, type, (RIMModel)recipient);
      }

      this._msg.setFlags(8192);
      this.endChanges(payloadandflag);
   }

   Message(Folder f, EmailMessageModel msg) {
      this._msg = msg;
      this._folder = f;
   }

   static BodyModel createBodyModel(String text) {
      ContextObject contextObject = (ContextObject)(new Object());
      contextObject.put(-8478555129720928586L, text);
      return (BodyModel)FactoryUtil.createInstance(5987399499453925075L, contextObject);
   }

   public Message(Folder folder) {
      this();
      this._folder = folder;
   }

   static {
      _headerHandlers.put(Header.TO.toLowerCase(), new ToHeaderHandler());
      _headerHandlers.put(Header.DATE.toLowerCase(), new DateHeaderHandler());
      _headerHandlers.put(Header.FROM.toLowerCase(), new FromHeaderHandler());
      _headerHandlers.put(Header.BCC.toLowerCase(), new BccHeaderHandler());
      _headerHandlers.put(Header.SENDER.toLowerCase(), new SenderHeaderHandler());
      _headerHandlers.put(Header.REPLY_TO.toLowerCase(), new ReplyToHeaderHandler());
      _headerHandlers.put(Header.SUBJECT.toLowerCase(), new SubjectHeaderHandler());
      _headerHandlers.put("Message-ID:".toLowerCase(), new MessageIdHeaderHandler());
   }
}
