package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.blackberry.api.blackberrymessenger.Message;
import net.rim.device.internal.io.file.FileUtilities;

final class PeerController {
   private PeerApplication _application = PeerApplication.getInstance();
   private PeerSession _session;

   final void userStateChange() {
      this._application.userStateChange();
   }

   final PeerContactListCollection getContactListCollection() {
      return this._application._contactListCollection;
   }

   PeerController(PeerSession session) {
      this._session = session;
   }

   public final void handleDataBlobs(String contactId, PeerDataBlob[] blobs) {
      PeerContact contact = this.getContactListCollection().findContact(contactId);

      for (int i = 0; i < blobs.length; i++) {
         PeerDataBlob b = blobs[i];
         switch (b.getType()) {
            case 0:
            case 3:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
               break;
            case 1:
            default:
               this.handleSimpleMsgDataBlob(contactId, (SimpleMsgDataBlob)b);
               if (contact != null) {
                  this._session._messageReceipts.onReceive(((SimpleMsgDataBlob)b).getConversationId(), b.getId());
                  contact.setTyping(false);
               }
               break;
            case 2:
               this.handleNotificationDataBlob(contact, (NotificationDataBlob)b);
               break;
            case 6:
               this.handleInviteAcceptedDataBlob(contact, (InviteAcceptedDataBlob)b);
               break;
            case 7:
               this.handleUserInfoDataBlob(contact, contactId, (UserInfoDataBlob)b);
               break;
            case 8:
               this._session._messageReceipts.onReadReceipt((ReadReceiptDataBlob)b);
               break;
            case 9:
               this.handleConvInviteBlob(contact, (ConvInviteBlob)b);
               break;
            case 16:
               this.handleConvAcceptBlob(contact, (ConvAcceptBlob)b);
               break;
            case 17:
               this.handleConvJoinBlob(contact, (ConvJoinBlob)b);
               break;
            case 18:
               this.handleConvRemBlob(contact, (ConvRemBlob)b);
               break;
            case 19:
               this.handleChangeInfoBlob(contactId, contact, (ChangeInfoBlob)b);
               break;
            case 20:
               this.handleDeleteContactBlob(contact, (DeleteContactBlob)b);
               break;
            case 21:
               this.handlePasswordKeyBlob(contact, (PasswordKeyBlob)b);
               break;
            case 22:
               this.handleFileTransferBlob(contact, (FileTransferBlob)b);
               break;
            case 23:
               this.handleVerifyHashBlob(contact, contactId, (VerifyHashBlob)b);
               break;
            case 24:
               this.handleSessionBlob(contact, (SessionBlob)b);
         }
      }
   }

   public final void handleSimpleMsgDataBlob(String contactId, SimpleMsgDataBlob blob) {
      String convId = blob.getConversationId();
      String message = blob.getMessage();
      boolean system = blob.isSystemMessage();
      PeerConversation conversation = PeerApplication.getInstance().getConvByUniqueId(convId);
      if (conversation != null) {
         this._application.newMessage(convId, system ? null : contactId, message);
      } else {
         PeerContact contact = this.getContactListCollection().findContact(contactId);
         if (contact == null) {
            VerifyHashBlob requestBlob = new VerifyHashBlob(true, message, "", 0);
            this._session.sendBlob(contactId, requestBlob);
         } else {
            Vector contacts = blob.getContacts();
            if (contacts.size() <= 1) {
               conversation = PeerApplication.getInstance().getConvByContactId(contactId);
               if (conversation != null) {
                  PeerApplication.setConversationId(conversation, convId);
               }

               if (system) {
                  this._application.newMessage(convId, null, message);
                  this._application.modifyConversationParticipant(convId, contactId, true);
               } else {
                  this._application.newMessage(convId, contactId, message);
               }
            } else {
               contacts.insertElementAt(contactId, 0);
               Vector contactNames = blob.getContactNames();
               contactNames.insertElementAt(contact.getDisplayName(), 0);
               int size = contacts.size();

               for (int index = 0; index < size; index++) {
                  String cid = (String)contacts.elementAt(index);
                  if (!cid.equals(this._session.getMyContactId())) {
                     PeerContact current = this.getContactListCollection().findContact(cid);
                     if (current != null) {
                        this._application.newMessage(convId, null, PeerResources.format(882, current.getDisplayName()));
                        if (current.getContactLists().size() == 0) {
                           this._session.requestInfo(convId, cid);
                        }
                     } else {
                        this._application.newMessage(convId, null, PeerResources.format(882, cid));
                     }

                     this._application.modifyConversationParticipant(convId, cid, true);
                  }
               }

               this._application.newMessage(convId, system ? null : contactId, message);
            }

            contact.madeContact();
         }
      }
   }

   public final void handleNotificationDataBlob(PeerContact contact, NotificationDataBlob blob) {
      String from = blob.getFromId();
      if (blob.getFromId() != null) {
         contact = this.getContactListCollection().findContact(from);
      }

      if (contact != null) {
         switch (blob.getNotificationCode()) {
            case -1:
            case 3:
               break;
            case 0:
            default:
               contact.setPresenceStatus(4);
               contact.setCustomStatusMessage(blob.getMessage());
               contact.setTyping(false);
               PeerAuditManager.getInstance().logIncomingStatus(contact, contact.formatStatus(), blob.getMessage());
               return;
            case 1:
               contact.setPresenceStatus(16384);
               contact.setCustomStatusMessage(blob.getMessage());
               PeerAuditManager.getInstance().logIncomingStatus(contact, contact.formatStatus(), blob.getMessage());
               return;
            case 2:
               contact.setPresenceStatus(8);
               return;
            case 4:
               contact.setTyping(true);
               return;
            case 5:
               contact.setTyping(false);
               break;
            case 6:
               contact.setPresenceStatus(1);
               return;
            case 7:
               contact.setPresenceStatus(2);
               return;
         }
      }
   }

   public final void handleInviteAcceptedDataBlob(PeerContact contact, InviteAcceptedDataBlob blob) {
      String pin = blob.getContactId();
      String userName = blob.getUserName();
      String key = blob.getKey();
      String replyTo = blob.getOrigContactInfo();
      if (contact != null) {
         PeerContact pending = this._application._contactListCollection.findContact(replyTo);
         if (pending != null && pending != contact) {
            this._application._contactListCollection.removeContact(pending);
         }

         contact.setPending(false);
         contact.setPresenceStatus(16384);
         this._application._contactListCollection.updateContact(contact, userName, false);
      } else {
         contact = this._application._contactListCollection.findContact(replyTo);
         if (contact != null) {
            this._application._contactListCollection.updateContact(contact, pin, userName, null);
         }
      }

      if (contact != null) {
         contact.setKey(key);
         this._session.sendInfoBlob(contact);
         PeerApplication.getInstance().dispatchUserStatus(contact);
      }
   }

   public final void handleConvInviteBlob(PeerContact contact, ConvInviteBlob blob) {
      if (contact != null) {
         Vector contacts = blob.getContacts();
         Vector contactNames = blob.getContactNames();
         String convId = blob.getConversationId();
         PeerConversation conversation = PeerApplication.getInstance().getConvByUniqueId(convId);
         if (conversation == null) {
            contacts.insertElementAt(contact.getId(), 0);
            contactNames.insertElementAt(contact.getDisplayName(), 0);
         }

         this._session.sendBlob(contact, new ConvAcceptBlob(convId, this._session.getDisplayName()));
         conversation = this._application.newMessage(convId, null, PeerResources.format(883, contact.getDisplayName()));
         int size = contacts.size();

         for (int index = 0; index < size; index++) {
            String cid = (String)contacts.elementAt(index);
            String contactName = (String)contactNames.elementAt(index);
            if (!cid.equals(this._session.getMyContactId())) {
               this._application.modifyConversationParticipant(convId, cid, true);
               PeerContact current = this.getContactListCollection().findContact(cid);
               if (current != null) {
                  this._application._contactListCollection.updateContact(current, contactName, false);
                  conversation.updateParticipantData(current);
                  this._application.newMessage(convId, null, PeerResources.format(882, current.getDisplayName()));
                  if (current.getContactLists().size() == 0) {
                     this._session.requestInfo(convId, cid);
                  }
               }
            }
         }
      }
   }

   public final void handleConvAcceptBlob(PeerContact contact, ConvAcceptBlob blob) {
      String convId = blob.getConversationId();
      String contactName = blob.getContactName();
      String contactId = null;
      if (contact != null) {
         contactId = contact.getId();
         this._application._contactListCollection.updateContact(contact, contactName, false);
      }

      this._application.newMessage(convId, null, PeerResources.format(884, contactName));
      PeerConversation conversation = this._application.getConvByUniqueId(convId);
      if (conversation != null) {
         ConvJoinBlob b = new ConvJoinBlob(convId, contactId, contactName);
         Vector participants = conversation.getParticipants();
         int size = participants.size();
         if (size != 0) {
            PeerContact[] current = new PeerContact[size];
            participants.copyInto(current);
            this._session.sendBlob(current, b);
            conversation.updateParticipantData(contact);
         }
      }

      this._application.modifyConversationParticipant(convId, contactId, true);
   }

   public final void handleConvJoinBlob(PeerContact contact, ConvJoinBlob blob) {
      String convId = blob.getConversationId();
      PeerConversation conversation = this._application.getConvByUniqueId(convId);
      if (conversation != null) {
         String contactName = blob.getContactName();
         this._application.newMessage(convId, null, PeerResources.format(884, contactName));
         this._application.modifyConversationParticipant(convId, blob.getContactId(), true);
         PeerContact contactAdded = PeerApplication.getInstance().getContactListCollection().findContact(blob.getContactId());
         if (contactAdded != null) {
            this._application._contactListCollection.updateContact(contactAdded, contactName, false);
            conversation.updateParticipantData(contactAdded);
         }
      }
   }

   public final void handleConvRemBlob(PeerContact contact, ConvRemBlob blob) {
      String convId = blob.getConversationId();
      PeerConversation conversation = this._application.getConvByUniqueId(convId);
      if (conversation != null) {
         this._application.newMessage(convId, null, PeerResources.format(885, blob.getContactName()));
         this._application.modifyConversationParticipant(convId, blob.getContactId(), false);
         Vector contacts = conversation.getParticipants();
         if (contacts == null || contacts.size() == 0) {
            this._application.newMessage(convId, null, PeerResources.getString(886));
         }
      }
   }

   public final void handleChangeInfoBlob(String contactId, PeerContact contact, ChangeInfoBlob blob) {
      String newName = blob.getContactName();
      boolean pinChanged = false;
      if (contact == null) {
         String oldPin = blob.getOldPin();
         contact = PeerApplication.getInstance().getContactListCollection().findContact(oldPin);
         if (contact != null) {
            contact.setId(contactId);
            pinChanged = true;
         }
      }

      if (contact != null) {
         String old = contact.getDisplayName();
         this._application._contactListCollection.updateContact(contact, newName, pinChanged);
         PeerAuditManager.getInstance().logIncomingDisplayName(contact, old, newName);
      }
   }

   public final void handlePasswordKeyBlob(PeerContact contact, PasswordKeyBlob blob) {
      if (contact != null) {
         contact.setKey(blob.getKey());
      }
   }

   public final void handleDeleteContactBlob(PeerContact contact, DeleteContactBlob blob) {
      if (contact != null) {
         PeerApplication.getInstance().deleteContact(contact, false);
      }
   }

   public final void handleUserInfoDataBlob(PeerContact contact, String contactId, UserInfoDataBlob blob) {
      String convId = blob.getConversationId();
      boolean request = blob.isRequest();
      PeerConversation conversation = this._application.getConvByUniqueId(convId);
      if (request && conversation != null) {
         this._session.sendInfo(convId, contactId);
      }
   }

   public final void handleVerifyHashBlob(PeerContact contact, String contactId, VerifyHashBlob blob) {
      if (blob.isRequest()) {
         if (contact != null) {
            String message = blob.getMessage();
            String toHash = message + contact.getKey();
            String hash = Utils.byteArrayToHex(Utils.getMD5Hash(toHash));
            VerifyHashBlob newBlob = new VerifyHashBlob(false, message, hash, contact.getCookie());
            this._session.sendBlob(contact, newBlob);
            return;
         }
      } else {
         String hash = blob.getHash();
         String key = PeerData.getPasswordKey();
         String msg = blob.getMessage();
         String myHash = Utils.byteArrayToHex(Utils.getMD5Hash(msg + key));
         if (myHash.equals(hash)) {
            contact = this._application._contactListCollection.findContactByCookie(blob.getCookie());
            if (contact != null) {
               this._application._contactListCollection.updateContact(contact, contactId, blob.getDisplayName(), null);
            } else {
               contact = new PeerContact(blob.getDisplayName(), contactId, false);
               this._application._contactListCollection.addContact(contact);
            }

            PeerApplication.getInstance().newMessage(contactId, contactId, blob.getMessage());
         }
      }
   }

   public final void handleFileTransferBlob(PeerContact contact, FileTransferBlob blob) {
      if (contact != null) {
         String application = blob.getApplication();
         MessageHandler[] handlers = null;
         String type = blob.getContentType();
         if (application != null && !application.equals("BlackBerryMessenger")) {
            Message message = new Message(
               blob.getContentType(), blob.getData(), FileUtilities.getDisplayName(blob.getFilename()), blob.getInteger(), blob.getUrl()
            );
            SessionManager.getInstance().messageReceived(blob.getSessionId(), message);
         } else {
            handlers = ContentHandlerManager.getInstance().getSystemHandlers(type);
         }

         if (handlers != null && handlers.length != 0) {
            for (int index = 0; index < handlers.length; index++) {
               PeerApplication.getInstance()
                  .newObject(contact, handlers[index].handle(contact, type, blob.getFilename(), blob.getData(), blob.getInteger(), null));
            }
         }
      }
   }

   public final void handleSessionBlob(PeerContact contact, SessionBlob blob) {
      switch (blob.getSessionType()) {
         case 0:
         default:
            SessionManager.getInstance().sessionRequested(contact, blob.getId(), blob.getApplication(), blob.getMessage());
            return;
         case 1:
            SessionManager.getInstance().sessionAccepted(contact, blob.getApplication(), blob.getId());
            return;
         case 2:
            SessionManager.getInstance().sessionRefused(blob.getId(), blob.getId());
            return;
         case 3:
            SessionManager.getInstance().sessionEnded(blob.getId(), true);
         case -1:
      }
   }
}
