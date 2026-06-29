package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import javax.microedition.io.Connector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.qm.peer.common.Contact;

final class PeerSession {
   private DatagramConnectionBase _connection;
   private PeerSendThread _sender;
   private PeerReceiveThread _receiver;
   private PeerController _controller;
   private PeerApplication _application;
   PeerMessageReceipts _messageReceipts;
   private final NotificationDataBlob _notifBlob = new NotificationDataBlob();
   static final String CID = "RIM_IM";
   static final int MAJOR_VERSION = 16;
   static final int MINOR_VERSION = 0;
   static final int VERSION = 16;
   private static final DeleteContactBlob _deleteContactBlob = new DeleteContactBlob();
   private static final PasswordKeyBlob _passwordBlob = new PasswordKeyBlob();

   public PeerSession(PeerApplication application) {
      this._application = application;
      this._controller = new PeerController(this);
      this._messageReceipts = new PeerMessageReceipts(this._application, this);
      this.login();
   }

   final PeerController getController() {
      return this._controller;
   }

   final void lock() {
   }

   public final void setDisplayName(String newName) {
      if (!PeerApplication.isDeviceLocked() && !newName.equals(this.getDisplayName())) {
         PeerData.saveUserName(newName);
         this.sendInfoBlob(null);
      }
   }

   public final String getDisplayName() {
      return PeerData.getUserName();
   }

   public final String getMyContactId() {
      return Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
   }

   public final void setUserStatus(int userStatus, String customMessage) {
      NotificationDataBlob blob = new NotificationDataBlob(this.mapUserStatus(userStatus), customMessage);
      this.sendToEveryone(2, blob);
      this._controller.userStateChange();
   }

   final void setUserStatus(int userStatus, String customMessage, PeerContact contactToNotify) {
      if (contactToNotify != null) {
         NotificationDataBlob blob = new NotificationDataBlob(this.mapUserStatus(userStatus), customMessage);
         this.sendBlob(contactToNotify, blob);
         this._controller.userStateChange();
      }
   }

   private final int mapUserStatus(int userStatus) {
      int status = -1;
      switch (userStatus) {
         case 1:
            return 6;
         case 2:
            status = 7;
         default:
            return status;
         case 4:
            return 0;
         case 8:
            return 2;
         case 16384:
            return 1;
      }
   }

   public final synchronized void send(PeerConversation conversation, String message, int cookie) {
      if (!PeerApplication.isDeviceLocked()) {
         Vector contacts = new Vector();
         Vector contactNames = new Vector();
         Vector participants = conversation.getParticipants();
         int size = participants.size();

         for (int index = 0; index < size; index++) {
            contacts.addElement(((PeerContact)participants.elementAt(index)).getId());
            contactNames.addElement(((PeerContact)participants.elementAt(index)).getDisplayName());
         }

         if (contacts != null && contacts.size() != 0) {
            SimpleMsgDataBlob msgBlob = new SimpleMsgDataBlob(cookie, message, conversation.getId(), contacts, contactNames, false);
            String[] array = new String[contacts.size()];
            contacts.copyInto(array);
            this.sendBlob(array, msgBlob);
            this._messageReceipts.onSend(conversation, cookie);
         }
      }
   }

   public final synchronized void sendFile(PeerContact contact, String contentType, String filename, byte[] data) {
      this.sendBlob(contact, new FileTransferBlob(contentType, filename, data, 0, null, "BlackBerryMessenger"));
   }

   public final void endConversation(PeerConversation conversation) {
      if (!PeerApplication.isDeviceLocked()) {
         Vector participants = conversation.getParticipants();
         if (participants != null && (participants.size() > 1 || conversation.isConference() && participants.size() != 0)) {
            String convId = conversation.getId();
            ConvRemBlob b = new ConvRemBlob(convId, this.getMyContactId(), this.getDisplayName());
            this.sendBlob(participants, b);
         }
      }
   }

   public final void inviteUser(PeerConversation conversation, Contact contact) {
      if (!PeerApplication.isDeviceLocked()) {
         Vector contacts = new Vector();
         Vector contactNames = new Vector();
         Vector participants = conversation.getParticipants();
         int size = participants.size();

         for (int index = 0; index < size; index++) {
            contacts.addElement(((PeerContact)participants.elementAt(index)).getId());
            contactNames.addElement(((PeerContact)participants.elementAt(index)).getDisplayName());
         }

         this.sendBlob(contact, new ConvInviteBlob(conversation.getId(), contacts, contactNames));
         String confInvite = PeerResources.getString(1201);
         String message = MessageFormat.format(confInvite, new String[]{contact.getDisplayName(), this.getDisplayName()}).toString();
         this.sendBlob(contacts, new SimpleMsgDataBlob(message.hashCode(), message, conversation.getId(), contacts, contactNames, true));
         message = MessageFormat.format(confInvite, new String[]{contact.getDisplayName()}).toString();
         PeerApplication.getInstance().newMessage(conversation.getId(), null, message);
      }
   }

   public final void deleteContact(PeerContact contact) {
      this.sendBlob(contact, _deleteContactBlob);
   }

   public final void userTyping(boolean typing, PeerConversation conversation) {
      int code = typing ? 4 : 5;
      this._notifBlob.setData(code, null);
      this.sendBlob(conversation.getParticipants(), this._notifBlob);
   }

   final void conversationRead(PeerConversation conversation) {
      this._messageReceipts.conversationRead(conversation);
   }

   public final void requestInfo(String conversationId, String contactId) {
      UserInfoDataBlob uib = new UserInfoDataBlob(true, conversationId, null);
      this.sendBlob(contactId, uib);
   }

   public final void sendInfo(String conversationId, String contactId) {
      if (!PeerApplication.isDeviceLocked()) {
         String originalInfo = null;
         RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
         if (service != null) {
            ServiceRecord sr = service.getOutgoingServiceRecord();
            if (sr != null) {
               originalInfo = CMIMEUtilities.getEmailAddress(sr);
            }
         }

         if (originalInfo == null || originalInfo.length() == 0) {
            originalInfo = this.getMyContactId();
         }

         UserInfoDataBlob uib = new UserInfoDataBlob(false, conversationId, originalInfo);
         this.sendBlob(contactId, uib);
      }
   }

   public final void acceptInvitation(PeerContact contact, String cookie, String origContactInfo) {
      if (!PeerApplication.isDeviceLocked()) {
         this.sendBlob(contact, new InviteAcceptedDataBlob(cookie, this.getMyContactId(), this.getDisplayName(), PeerData.getPasswordKey(), origContactInfo));
      }
   }

   public final void broadcastNewKey() {
      if (!PeerApplication.isDeviceLocked()) {
         _passwordBlob.setData(PeerData.getPasswordKey());
         this.sendToEveryone(_passwordBlob);
      }
   }

   final void login() {
      try {
         this._connection = (DatagramConnectionBase)Connector.open("gme:RIM_IM");
         EventLogger.register(-9029900896793868512L, "PeerTransport", 2);
         this._sender = new PeerSendThread(this, this._connection);
         this._sender.start();
         this._receiver = new PeerReceiveThread(this._connection, this._controller);
         this._receiver.start();
      } finally {
         return;
      }
   }

   public final void sendInfoBlob(PeerContact contact) {
      this.sendInfoBlob(contact, null);
   }

   public final void sendInfoBlob(PeerContact contact, String oldPin) {
      if (!PeerApplication.isDeviceLocked()) {
         ChangeInfoBlob infoBlob = new ChangeInfoBlob();
         infoBlob.setData(this.getMyContactId(), this.getDisplayName(), oldPin == null ? this.getMyContactId() : oldPin);
         if (contact != null) {
            this.sendBlob(contact, infoBlob);
            return;
         }

         this.sendToEveryone(infoBlob);
      }
   }

   final void sendToEveryone(PeerDataBlob blob) {
      this.sendToEveryone(0, blob);
   }

   final void sendToEveryone(int recognizer, PeerDataBlob blob) {
      PeerContact[] contacts = PeerApplication.getInstance().getContactListCollection().getContacts(recognizer);
      if (contacts.length > 0) {
         this.sendBlob(contacts, blob);
      }
   }

   final void sendBlob(Object contact, PeerDataBlob blob) {
      if (!PeerApplication.isDeviceLocked()) {
         this._sender.addRequest(contact, blob);
      }
   }
}
