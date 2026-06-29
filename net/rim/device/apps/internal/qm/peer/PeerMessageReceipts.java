package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.util.DataBuffer;

final class PeerMessageReceipts implements DatagramStatusListener {
   QMTimer _timers = new QMTimer();
   PeerApplication _application;
   PeerSession _session;

   PeerMessageReceipts(PeerApplication application, PeerSession session) {
      this._session = session;
      this._application = application;
   }

   final void onReceive(String conversationId, int messageId) {
      PeerConversation conversation = PeerApplication.getInstance().getConvByUniqueId(conversationId);
      if (conversation != null) {
         PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
         if (state == null) {
            state = new PeerMessageReceipts$ThreadState(this, conversation);
            conversation.setThreadState(state);
         }

         state.onReceive(messageId);
      }
   }

   final void conversationRead(PeerConversation conversation) {
      if (conversation != null) {
         PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
         if (state != null) {
            state.onRead();
         }
      }
   }

   final void hitch(Object contacts, DataBuffer db) {
      if (!(contacts instanceof PeerContact)) {
         if (contacts instanceof Object[]) {
            String id = (String)((Object[])contacts)[0];
            PeerConversation conversation = PeerApplication.getInstance().getConvByContactId(id);
            if (conversation != null) {
               PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
               if (state != null) {
                  state.hitch(db);
               }
            }
         }
      } else {
         PeerContact contact = (PeerContact)contacts;
         PeerConversation conversation = PeerApplication.getInstance().getConvByContactId(contact.getId());
         if (conversation != null) {
            PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
            if (state != null) {
               state.hitch(db);
               return;
            }
         }
      }
   }

   final void onSend(PeerConversation conversation, int cookie) {
      if (conversation != null) {
         PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
         if (state == null) {
            state = new PeerMessageReceipts$ThreadState(this, conversation);
            conversation.setThreadState(state);
         }

         state.onSend(cookie);
      }
   }

   final void onReadReceipt(ReadReceiptDataBlob b) {
      String convId = b.getConvId();
      PeerConversation conversation = PeerApplication.getInstance().getConvByUniqueId(convId);
      if (conversation != null) {
         PeerMessageReceipts$ThreadState state = (PeerMessageReceipts$ThreadState)conversation.getThreadState();
         if (state != null) {
            state.onReadReceipt(b.getMsgId());
         }
      }
   }

   @Override
   public final void updateDatagramStatus(int cookie, int code, Object context) {
      int newState = -1;
      switch (code) {
         case -1:
         case 3:
         case 4:
            break;
         case 0:
         default:
            newState = 9;
            break;
         case 1:
            newState = 7;
            break;
         case 2:
            newState = 8;
            break;
         case 5:
            newState = 10;
      }

      if (newState != -1) {
         this._application._messageFieldLookup.messageStateChange(cookie, newState);
         SessionManager.getInstance().messageStateChange(cookie, newState);
      }
   }
}
