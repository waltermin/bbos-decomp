package net.rim.wica.runtime.messaging.internal;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.util.LongEnumeration;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue;
import net.rim.wica.runtime.messaging.internal.util.ConcurrentQueue$Iterator;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.persistence.MessageStore;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.util.LinkedQueue;
import net.rim.wica.runtime.util.LinkedQueue$Iterator;
import net.rim.wica.transport.message.TransportMessageFactory;

public class PersistenceHelper implements PersistentContentListener {
   private TransportMessageFactory _factory;
   private MessageStore _store;
   static Class class$net$rim$wica$transport$message$TransportMessageFactory;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;

   PersistenceHelper(ServiceProvider provider) {
      this._factory = (TransportMessageFactory)provider.getService(
         class$net$rim$wica$transport$message$TransportMessageFactory == null
            ? (class$net$rim$wica$transport$message$TransportMessageFactory = class$("net.rim.wica.transport.message.TransportMessageFactory"))
            : class$net$rim$wica$transport$message$TransportMessageFactory
      );
      PersistenceService persistence = (PersistenceService)provider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._store = persistence.getMessageStore();
      PersistentContent.addListener(this);
   }

   public synchronized void loadIncomingRequests(ConcurrentQueue queue) {
      BigVector v = this._store.loadInRequestQueue();
      if (v != null && v.size() != 0) {
         int size = v.size();
         queue.lock();

         for (int i = 0; i < size; i++) {
            queue.put(v.elementAt(i));
         }

         queue.unlock();
      }
   }

   public synchronized void storeIncomingRequests(ConcurrentQueue queue) {
      if (!queue.isEmpty()) {
         BigVector v = (BigVector)(new Object(queue.size()));
         queue.lock();
         ConcurrentQueue$Iterator i = queue.iterator();

         while (i.hasNext()) {
            v.addElement(i.next());
         }

         queue.unlock();
         this._store.storeInRequestQueue(v);
      }
   }

   public synchronized void deleteIncomingRequests() {
      this._store.deleteInRequestQueue();
   }

   public synchronized void loadSystemMessages(MessageHandler handler) {
      BigVector v = this._store.loadIncomingMessages(0);
      if (v != null && v.size() != 0) {
         this.deserializeMessages(v, handler);
      }
   }

   public synchronized void storeSystemMessages(LinkedQueue queue) {
      if (!queue.isEmpty()) {
         BigVector v = this.serializeMessages(queue);
         this._store.storeIncomingMessages(0, v, false);
      }
   }

   public synchronized void deleteSystemMessages() {
      this._store.deleteIncomingMessages(0, Integer.MAX_VALUE);
   }

   public synchronized void loadWicletMessages(long id, MessageHandler handler) {
      BigVector v = this._store.loadIncomingMessages(id);
      if (v != null && v.size() != 0) {
         this.deserializeMessages(v, handler);
      }
   }

   public synchronized void storeWicletMessages(long id, ConcurrentQueue queue) {
      if (!queue.isEmpty()) {
         BigVector v = this.serializeMessages(queue);
         this._store.storeIncomingMessages(id, v, false);
      }
   }

   public synchronized void deleteWicletMessages(long id) {
      this._store.deleteIncomingMessages(id, Integer.MAX_VALUE);
   }

   public synchronized void loadOutgoingMessages(long id, MessageHandler handler) {
      BigVector v = this._store.loadOutgoingMessages(id);
      if (v != null && v.size() != 0) {
         this.deserializeMessages(v, handler);
      }
   }

   public synchronized void storeOutgoingMessages(long id, ConcurrentQueue queue) {
      if (!queue.isEmpty()) {
         BigVector v = this.serializeMessages(queue);
         this._store.storeOutgoingMessages(id, v, false);
      }
   }

   public synchronized void storeOutgoingMessages(long id, LinkedQueue queue) {
      if (!queue.isEmpty()) {
         BigVector v = this.serializeMessages(queue);
         this._store.storeOutgoingMessages(id, v, false);
      }
   }

   public synchronized void deleteOutgoingMessages() {
   }

   public synchronized void deleteOutgoingMessages(long id) {
      this._store.deleteOutgoingMessages(id, Integer.MAX_VALUE);
   }

   private BigVector serializeMessages(LinkedQueue queue) {
      BigVector v = (BigVector)(new Object(queue.size()));
      LinkedQueue$Iterator i = queue.iterator();

      while (i.hasNext()) {
         MessageImpl m = (MessageImpl)i.next();
         v.addElement(new PersistableMessage(m));
      }

      return v;
   }

   private BigVector serializeMessages(ConcurrentQueue queue) {
      queue.lock();
      BigVector v = (BigVector)(new Object(queue.size()));
      ConcurrentQueue$Iterator i = queue.iterator();

      while (i.hasNext()) {
         MessageImpl m = (MessageImpl)i.next();
         v.addElement(new PersistableMessage(m));
      }

      queue.unlock();
      return v;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void deserializeMessages(BigVector v, MessageHandler handler) {
      TransportMsgHelper helper = new TransportMsgHelper();
      int size = v.size();

      for (int i = 0; i < size; i++) {
         try {
            PersistableMessage persisted = (PersistableMessage)v.elementAt(i);
            this._factory.handleMessage(persisted.getTransportMsg(), helper);
            Message message = helper.getMessage();
            message.setAGID(persisted.getAgId());
            message.setMessageName(persisted.getMessageName());
            message.setSecurityMode(persisted.getSecurityMode());
            message.setServiceID(persisted.getServiceId());
            message.setDestinationType(persisted.getDestinationType());
            handler.handleMessage(helper.getMessage());
         } catch (Throwable var9) {
            InternalLogger.logError(this, null, t, null);
            continue;
         }
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
   }

   @Override
   public synchronized void persistentContentModeChanged(int generation) {
      LongEnumeration keys = this._store.getInMessageKeys();

      while (keys.hasMoreElements()) {
         long id = keys.nextElement();
         BigVector v = this._store.loadIncomingMessages(id);
         this.recrypt(v);
         this._store.storeIncomingMessages(id, v, false);
      }

      keys = this._store.getOutMessageKeys();

      while (keys.hasMoreElements()) {
         long id = keys.nextElement();
         BigVector v = this._store.loadOutgoingMessages(id);
         this.recrypt(v);
         this._store.storeOutgoingMessages(id, v, false);
      }
   }

   private void recrypt(BigVector v) {
      if (v != null && v.size() != 0) {
         int size = v.size();

         for (int i = 0; i < size; i++) {
            PersistableMessage m = (PersistableMessage)v.elementAt(i);
            m.recrypt();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
