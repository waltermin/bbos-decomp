package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.LongEnumeration;
import net.rim.wica.runtime.persistence.MessageStore;

public class MessageStoreImpl implements MessageStore {
   private LongSubstore _inMsgSubstore = new LongSubstore(4535419555376299188L);
   private LongSubstore _outMsgSubstore = new LongSubstore(7843219391424628215L);
   private static final long KEY_IN_REQUEST_QUEUE = -6071652197342507152L;
   private static final long KEY_INMSG = 4535419555376299188L;
   private static final long KEY_OUTMSG = 7843219391424628215L;

   MessageStoreImpl() {
   }

   @Override
   public void storeIncomingMessages(long id, BigVector messages, boolean append) {
      if (append && this._inMsgSubstore.containsKey(id)) {
         BigVector existing = (BigVector)this._inMsgSubstore.get(id);
         int size = messages.size();

         for (int i = 0; i < size; i++) {
            existing.addElement(messages.elementAt(i));
         }

         messages = existing;
      }

      this._inMsgSubstore.put(id, messages);
   }

   @Override
   public void deleteIncomingMessages(long id, int count) {
      BigVector messages = (BigVector)this._inMsgSubstore.get(id);
      if (messages != null) {
         if (count == Integer.MAX_VALUE) {
            count = messages.size();
         }

         for (int i = count - 1; i >= 0; i--) {
            messages.removeElementAt(i);
         }

         this._inMsgSubstore.put(id, messages);
      }
   }

   @Override
   public BigVector loadIncomingMessages(long id) {
      BigVector messages = (BigVector)this._inMsgSubstore.get(id);
      this._inMsgSubstore.remove(id);
      return messages;
   }

   @Override
   public void storeOutgoingMessages(long id, BigVector messages, boolean append) {
      if (append && this._outMsgSubstore.containsKey(id)) {
         BigVector existing = (BigVector)this._outMsgSubstore.get(id);
         int size = messages.size();

         for (int i = 0; i < size; i++) {
            existing.addElement(messages.elementAt(i));
         }

         messages = existing;
      }

      this._outMsgSubstore.put(id, messages);
   }

   @Override
   public void deleteOutgoingMessages(long id, int count) {
      BigVector messages = (BigVector)this._outMsgSubstore.get(id);
      if (messages != null) {
         if (count == Integer.MAX_VALUE) {
            count = messages.size();
         }

         for (int i = count - 1; i >= 0; i--) {
            messages.removeElementAt(i);
         }

         this._outMsgSubstore.put(id, messages);
      }
   }

   @Override
   public BigVector loadOutgoingMessages(long id) {
      BigVector messages = (BigVector)this._outMsgSubstore.get(id);
      this._outMsgSubstore.remove(id);
      return messages;
   }

   @Override
   public void storeInRequestQueue(BigVector queue) {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(-6071652197342507152L);
      persistentObject.setContents(queue, 51);
      persistentObject.commit();
   }

   @Override
   public void deleteInRequestQueue() {
      PersistentStore.destroyPersistentObject(-6071652197342507152L);
   }

   @Override
   public BigVector loadInRequestQueue() {
      PersistentObject persistentObject = PersistentStore.getPersistentObject(-6071652197342507152L);
      Object obj = persistentObject.getContents();
      return (BigVector)(!(obj instanceof Object) ? null : obj);
   }

   public static void wipe() {
      PersistentStore.destroyPersistentObject(-6071652197342507152L);
      PersistentStore.destroyPersistentObject(4535419555376299188L);
      PersistentStore.destroyPersistentObject(7843219391424628215L);
   }

   @Override
   public LongEnumeration getInMessageKeys() {
      return this._inMsgSubstore.keys();
   }

   @Override
   public LongEnumeration getOutMessageKeys() {
      return this._outMsgSubstore.keys();
   }
}
