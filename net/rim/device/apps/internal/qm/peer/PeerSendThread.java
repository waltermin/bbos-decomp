package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.qm.peer.common.Contact;

final class PeerSendThread extends Thread {
   private DatagramConnectionBase _connection;
   private Vector _requests;
   private boolean _alive;
   private PeerSession _session;

   public PeerSendThread(PeerSession session, DatagramConnectionBase connection) {
      this._connection = connection;
      this._requests = (Vector)(new Object());
      this._alive = true;
      this._session = session;
   }

   public final void addRequest(Object contacts, PeerDataBlob blob) {
      PeerSendThread$Request req = new PeerSendThread$Request(contacts, blob);
      synchronized (this._requests) {
         this._requests.addElement(req);
         this._requests.notifyAll();
      }
   }

   @Override
   public final void run() {
      while (true) {
         synchronized (this._requests) {
            if (!this._alive) {
               return;
            }

            if (this._requests.size() == 0) {
               try {
                  this._requests.wait();
                  continue;
               } finally {
                  continue;
               }
            }
         }

         this.processNextRequest();
      }
   }

   private final void processNextRequest() {
      PeerSendThread$Request req;
      synchronized (this._requests) {
         req = (PeerSendThread$Request)this._requests.elementAt(0);
         this._requests.removeElementAt(0);
      }

      try {
         StringBuffer strBuf = (StringBuffer)(new Object(32));
         strBuf.append("RIM_IM");
         if (req.contacts instanceof PeerContact) {
            strBuf.append(':');
            strBuf.append(((PeerContact)req.contacts).getId());
         } else if (req.contacts instanceof PeerContact[]) {
            PeerContact[] contacts = (PeerContact[])req.contacts;
            if (contacts.length == 0) {
               return;
            }

            for (int i = 0; i < contacts.length; i++) {
               strBuf.append(':');
               strBuf.append(contacts[i].getId());
            }
         } else if (!(req.contacts instanceof Object)) {
            if (req.contacts instanceof Object[]) {
               String[] contacts = (Object[])req.contacts;
               if (contacts.length == 0) {
                  return;
               }

               for (int i = 0; i < contacts.length; i++) {
                  strBuf.append(':');
                  strBuf.append(contacts[i]);
               }
            } else if (req.contacts instanceof Object) {
               strBuf.append(':');
               strBuf.append((String)req.contacts);
            }
         } else {
            Vector contacts = (Vector)req.contacts;
            if (contacts.size() == 0) {
               return;
            }

            for (int i = 0; i < contacts.size(); i++) {
               Object current = contacts.elementAt(i);
               if (current instanceof Contact) {
                  strBuf.append(':');
                  strBuf.append(((Contact)current).getId());
               }
            }
         }

         DatagramBase dg = (DatagramBase)this._connection.newDatagram(32, strBuf.toString());
         dg.setDatagramStatusListener(this._session._messageReceipts);
         dg.writeByte(16);
         req.blob.pickle(dg);
         int id = req.blob.getId();
         if (id != -1) {
            dg.setDatagramId(id);
         }

         this._session._messageReceipts.hitch(req.contacts, dg);
         dg.writeByte(0);
         dg.trim();
         EventLogger.logEvent(-9029900896793868512L, 1415082868, 0);
         this._connection.send(dg);
      } finally {
         return;
      }
   }
}
