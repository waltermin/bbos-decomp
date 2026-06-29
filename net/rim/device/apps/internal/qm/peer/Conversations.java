package net.rim.device.apps.internal.qm.peer;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.messaging.FolderHierarchies;

final class Conversations implements ReadableList, CollectionEventSource, CollectionListener, Comparator {
   private CollectionListenerManager _clm = new CollectionListenerManager();
   private SimpleSortingVector _conversations = new SimpleSortingVector();
   private Vector _persistedConversations;
   private PeerConversationsFolder _folder;
   private IntHashtable _hashtable = new IntHashtable();
   private IntHashtable _contact2Conv = new IntHashtable();
   private static long MILLISECONDS_INA_DAY = 86400000;

   Conversations(PeerContactListCollection contactList, PeerConversationsFolder folder) {
      this._folder = folder;
      this._folder.setConversations(this);
      this._conversations.setSortComparator(this);
      this.populatePersistedConversations(contactList);
   }

   final void populatePersistedConversations(PeerContactListCollection contactList) {
      Object obj = PeerData.getPersistedConversations();
      if (!(obj instanceof Vector)) {
         this._persistedConversations = new Vector();
         PeerData.setPersistedConversations(this._persistedConversations);
      } else {
         this._persistedConversations = (Vector)obj;
         int length = this._persistedConversations.size();

         for (int index = 0; index < length; index++) {
            IntHashtable data = (IntHashtable)this._persistedConversations.elementAt(index);
            int idHash = (Integer)data.get(2);
            if (!this._hashtable.containsKey(idHash)) {
               PeerConversation conversation = new PeerConversation(data, contactList, this._folder);
               this._conversations.add(conversation);
               this._hashtable.put(conversation.getIdHash(), conversation);
               PeerContact contact = conversation.getFirstParticipant();
               if (contact != null) {
                  this._contact2Conv.put(contact.getIdHash(), conversation);
               }

               conversation.addCollectionListener(this);
               this.fireElementAdded(conversation);
            }
         }

         this._conversations.reSort();
      }
   }

   final PeerConversation start(PeerContact contact, String uniqueId) {
      if (uniqueId == null) {
         uniqueId = getRandomId();
      }

      PeerConversation conversation = new PeerConversation(contact, uniqueId, this._folder);
      this._persistedConversations.addElement(conversation.getPersistentData());
      this.commit();
      this._hashtable.put(conversation.getIdHash(), conversation);
      if (contact != null) {
         this._contact2Conv.put(contact.getIdHash(), conversation);
      }

      this._conversations.add(conversation);
      conversation.addCollectionListener(this);
      this.fireElementAdded(conversation);
      return conversation;
   }

   private static final String getRandomId() {
      StringBuffer randomId = new StringBuffer();

      for (int i = 0; i < 5; i++) {
         randomId.append((char)(65 + RandomSource.getInt(26)));
      }

      return randomId.toString();
   }

   final PeerConversation getByUniqueId(int uniqueId) {
      return (PeerConversation)this._hashtable.get(uniqueId);
   }

   final PeerConversation getByContactId(int contactId) {
      return (PeerConversation)this._contact2Conv.get(contactId);
   }

   final PeerConversation getByContact(PeerContact contact) {
      return this.getByContactId(contact.getIdHash());
   }

   final void setConversationId(PeerConversation conversation, String newId) {
      this._hashtable.remove(conversation.getIdHash());
      conversation.setId(newId);
      this._hashtable.put(conversation.getIdHash(), conversation);
   }

   final void end(PeerConversation conversation) {
      if (conversation != null) {
         this._persistedConversations.removeElement(conversation.getPersistentData());
         this.commit();
         conversation.end();
         this._hashtable.remove(conversation.getIdHash());
         if (conversation.getFirstParticipant() != null) {
            this._contact2Conv.remove(conversation.getFirstParticipant().getIdHash());
         } else {
            this._contact2Conv.removeValue(conversation);
         }

         this._conversations.remove(this._conversations.find(conversation));
         PeerApplication.getInstance().getContactListCollection().cleanUnauthorizedContacts(conversation.getParticipants());
         this.fireElementRemoved(conversation);
      }
   }

   final void lock() {
      if (this._conversations != null && this._conversations.size() > 0) {
         for (int i = this._conversations.size() - 1; i >= 0; i--) {
            ((PeerConversation)this._conversations.getAt(i)).lock();
         }

         this.commit();
      }
   }

   final void clear() {
      synchronized (FolderHierarchies.getLockObject()) {
         IntEnumeration keys = this._hashtable.keys();

         while (keys.hasMoreElements()) {
            int key = keys.nextElement();
            Object obj = this._hashtable.get(key);
            if (obj instanceof PeerConversation) {
               this._folder.removeItem((PeerConversation)obj);
            }
         }
      }

      this._hashtable.clear();
      this._contact2Conv.clear();
      this._conversations.removeAll();
      PeerApplication.unreadConversationCount().set(0);
   }

   private final void cleanUp(int days, int maxCount, boolean deleteRead) {
      long oldest = System.currentTimeMillis() - days * MILLISECONDS_INA_DAY;

      for (int i = this._conversations.size() - 1; i >= 0; i--) {
         PeerConversation conversation = (PeerConversation)this._conversations.getAt(i);
         if (deleteRead && !conversation.isUnread()) {
            this.end(conversation);
         } else {
            conversation.cleanUp(oldest, maxCount);
            if (conversation.size() == 0) {
               this.end(conversation);
            }
         }
      }
   }

   final void cleanUp(int priority) {
      switch (priority) {
         case 0:
         default:
            this.cleanUp(7, 200, false);
            return;
         case 1:
            this.cleanUp(3, 100, false);
            return;
         case 2:
            this.cleanUp(1, 3, true);
         case -1:
      }
   }

   final void modifyParticipant(PeerConversation conversation, PeerContact participant, boolean add) {
      PeerContact first = conversation.getFirstParticipant();
      conversation.modifyParticipant(participant, add);
      PeerContact newFirst = conversation.getFirstParticipant();
      if (!add && first != newFirst) {
         this._contact2Conv.remove(first.getIdHash());
         if (newFirst != null) {
            this._contact2Conv.put(newFirst.getIdHash(), conversation);
         }
      }
   }

   final void fireElementAdded(Object element) {
      this._clm.fireElementAdded(this, element);
   }

   final void fireElementRemoved(Object element) {
      this._clm.fireElementRemoved(this, element);
   }

   final void fireElementUpdated(Object element) {
      this._clm.fireElementUpdated(this, element, element);
   }

   @Override
   public final synchronized Object getAt(int index) {
      return this._conversations.getAt(index);
   }

   @Override
   public final synchronized int getAt(int index, int count, Object[] elements, int destIndex) {
      throw new RuntimeException();
   }

   @Override
   public final synchronized int getIndex(Object element) {
      return this._conversations.find(element);
   }

   @Override
   public final synchronized int size() {
      return this._conversations.size();
   }

   @Override
   public final synchronized void addCollectionListener(Object listener) {
      this._clm.addCollectionListener(listener);
   }

   @Override
   public final synchronized void removeCollectionListener(Object listener) {
      this._clm.removeCollectionListener(listener);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (collection instanceof PeerConversation) {
         this._conversations.reSort();
      }

      this.fireElementUpdated(collection);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (collection instanceof PeerConversation) {
         this._conversations.reSort();
      }

      this.fireElementUpdated(collection);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection instanceof PeerConversation) {
         this._conversations.reSort();
      }

      this.fireElementUpdated(collection);
   }

   @Override
   public final void reset(Collection collection) {
      if (collection instanceof PeerConversation) {
         this._conversations.reSort();
      }

      this.fireElementUpdated(collection);
   }

   private final void commit() {
      PersistentObject.commit(this._persistedConversations);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof PeerConversation && o2 instanceof PeerConversation) {
         long t1 = ((PeerConversation)o1).getConvTimeStamp();
         long t2 = ((PeerConversation)o2).getConvTimeStamp();
         if (t1 < t2) {
            return -1;
         } else {
            return t1 > t2 ? 1 : 0;
         }
      } else {
         throw new RuntimeException();
      }
   }

   @Override
   public final boolean equals(Object obj) {
      throw new RuntimeException();
   }
}
