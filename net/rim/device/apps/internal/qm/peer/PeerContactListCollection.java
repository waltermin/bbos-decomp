package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.internal.qm.peer.common.Contact;
import net.rim.device.apps.internal.qm.peer.common.ContactKeywordsData;
import net.rim.device.apps.internal.qm.peer.common.ContactListProvider;
import net.rim.device.apps.internal.qm.peer.common.GTPatriciaTree;
import net.rim.device.apps.internal.qm.peer.common.Request;
import net.rim.device.apps.internal.qm.resource.QmResources;

public final class PeerContactListCollection implements CollectionEventSource, ContactListProvider, Collection {
   private CollectionListenerManager _collectionListenerManager;
   private SimpleSortingVector _contacts;
   private SimpleSortingVector _newRequests;
   private IntHashtable _groupId2Group;
   private SimpleSortingVector _contactLists;
   private PeerContactList _defaultContactList;
   private BigVector _persistentContactLists;
   private Vector _persistedNewRequests;
   private ContactKeywordsData _contactKeywordsData;
   private GTPatriciaTree _contactKeywords;
   PeerContactListCollection$SearchData _searchData = new PeerContactListCollection$SearchData();
   PeerContactListCollection$SearcherThread _searcher;
   static final int CONTACT_LISTS;
   static final int NEW_REQUESTS;
   static IntHashtable _pendingContacts = (IntHashtable)(new Object());
   static Comparator _contactsSorter = new PeerContactListCollection$1();
   static Comparator _requestsSorter = new PeerContactListCollection$2();
   static Comparator _contactListsSorter = new PeerContactListCollection$3();

   final void reload(long id) {
      this.reloadCollection(id);
      this.reloadRequests();
   }

   final void lock() {
      for (int i = this._contacts.size() - 1; i >= 0; i--) {
         ((PeerContact)this._contacts.getAt(i)).lock();
      }

      for (int i = this._newRequests.size() - 1; i >= 0; i--) {
         ((NewContactRequest)this._newRequests.getAt(i)).lock();
      }

      Enumeration enumeration = this._groupId2Group.elements();

      while (enumeration.hasMoreElements()) {
         ((PeerContactList)enumeration.nextElement()).lock();
      }
   }

   final void unlock() {
      this._contactKeywords.rebuildKeywordTreeIfNeeded();
   }

   final synchronized void reloadCollection(long id) {
      this.resetCollection();
      this._persistentContactLists = PeerData.getContactListCollectionData(id);
      if (this._persistentContactLists != null) {
         this.reloadContacts();
      } else {
         this._persistentContactLists = (BigVector)(new Object());
         PeerData.addContactListCollectionData(id, this._persistentContactLists);
         this._defaultContactList = this.addContactList(PeerResources.getString(40));
      }

      this._collectionListenerManager.fireReset(this);
   }

   final void resetCollection() {
      this._groupId2Group.clear();
      this._contactLists.removeAll();
      this._contacts.removeAll();
      this._contactKeywords.removeAllKeywords();
   }

   final PeerContact restoreContact(IntHashtable data, PeerContactList list) {
      int hash = 0;
      Object obj = data.get(17);
      if (!(obj instanceof Object)) {
         if (!PeerEntry.getInstance().isDeviceLocked()) {
            hash = PersistentContent.decode(data.get(1)).hashCode();
            data.put(17, new Object(hash));
         }
      } else {
         hash = obj;
      }

      PeerContact contact = hash != 0 ? this.findContactByHashId(hash) : null;
      if (contact == null) {
         contact = new PeerContact(data);
         this.addContact(contact, list, false);
         if (contact.isPending()) {
            addPendingContact(contact.getCookie(), contact);
         }
      }

      return contact;
   }

   final void addContact(PeerContact contact) {
      this.addContact(contact, null);
   }

   final void addContact(PeerContact contact, PeerContactList list) {
      this.addContact(contact, list, true);
   }

   final void addContact(PeerContact contact, PeerContactList list, boolean persist) {
      if (this._contacts != null && this.addContactInternal(contact, list, persist)) {
         this._collectionListenerManager.fireElementAdded(this, contact);
      }
   }

   final void removeContact(PeerContact contact) {
      if (contact != null && this.removeContactInternal(contact)) {
         this._collectionListenerManager.fireElementRemoved(this, contact);
      }
   }

   final void renameContact(PeerContact contact, String name) {
      if (contact != null) {
         contact.setOverrideDisplayName(name);
         this._collectionListenerManager.fireElementUpdated(this, contact, contact);
         this.updateKeywordsFor(contact);
      }
   }

   final void updateContact(PeerContact contact, String pin, String name, String password) {
      contact.setId(pin);
      contact.setPending(false);
      contact.setPresenceStatus(16384);
      if (name != null) {
         contact.setDisplayName(name);
      }

      if (password != null) {
         contact.setKey(password);
      }

      this._contacts.reSort();
      this.updateKeywordsFor(contact);
   }

   final void updateContact(PeerContact contact, String displayName, boolean reSort) {
      contact.setDisplayName(displayName);
      if (reSort) {
         this._contacts.reSort();
      }

      this.updateKeywordsFor(contact);
   }

   final void moveContact(PeerContact contact, PeerContactList oldGroup, PeerContactList newGroup) {
      if (contact != null) {
         if (oldGroup != null) {
            contact.removeContactList(oldGroup);
            oldGroup.removeContact(contact);
         }

         contact.addContactList(newGroup);
         newGroup.addContact(contact);
         this._collectionListenerManager.fireElementUpdated(this, contact, contact);
         this.updateKeywordsFor(contact);
      }
   }

   final PeerContact findContactByHashId(int contactIdHash) {
      PeerContact contact = null;

      for (int i = this._contacts.size() - 1; i >= 0; i--) {
         PeerContact current = (PeerContact)this._contacts.getAt(i);
         if (current != null && current.getIdHash() == contactIdHash) {
            return current;
         }
      }

      return contact;
   }

   final PeerContact findContactByCookie(int cookie) {
      PeerContact contact = null;
      if (cookie != 0) {
         for (int i = this._contacts.size() - 1; i >= 0; i--) {
            PeerContact current = (PeerContact)this._contacts.getAt(i);
            if (current != null && current.getCookie() == cookie) {
               return current;
            }
         }
      }

      return contact;
   }

   final PeerContact findContact(String contactId) {
      PeerContact contact = null;
      if (contactId != null) {
         int index = this._contacts.find(contactId);
         if (index >= 0) {
            contact = (PeerContact)this._contacts.getAt(index);
         }
      }

      return contact;
   }

   public final synchronized PeerContact findAndCreateContact(String contactId) {
      PeerContact contact = this.findContact(contactId);
      if (contact == null) {
         contact = new PeerContact(Utils.resolveName(contactId), contactId, false);
         contact.setAuthorized(false);
         this.addContact(contact);
      }

      return contact;
   }

   final void cleanUnauthorizedContacts(Vector toSearch) {
      synchronized (this._contacts) {
         for (int i = toSearch.size() - 1; i >= 0; i--) {
            Object obj = toSearch.elementAt(i);
            if (obj instanceof PeerContact) {
               PeerContact current = (PeerContact)obj;
               if (this._contacts.find(current) >= 0 && !current.isAuthorized()) {
                  this.removeContact(current);
               }
            }
         }
      }
   }

   final void contactUpdated(PeerContact contact) {
      this._collectionListenerManager.fireElementUpdated(this, contact, contact);
   }

   final PeerContactList addContactList(String group) {
      int hash = group.hashCode();
      Object list = this._groupId2Group.get(hash);
      if (list == null) {
         list = new PeerContactList(group);
         this._groupId2Group.put(hash, list);
         this._contactLists.add(list);
         IntHashtable contactListData = ((PeerContactList)list).getPersistentData();
         this._persistentContactLists.addElement(contactListData);
         this.commit();
      } else {
         PeerApplication.getInstance().alertUser(2, QmResources.format(30, group));
      }

      this._collectionListenerManager.fireElementAdded(this, list);
      return (PeerContactList)list;
   }

   final void removeContactList(PeerContactList list) {
      int hash = list.getIdHash();
      this._groupId2Group.remove(hash);
      this._contactLists.removeElement(list);

      for (int i = this._persistentContactLists.size() - 1; i >= 0; i--) {
         Object obj = ((IntHashtable)this._persistentContactLists.elementAt(i)).get(3);
         if (obj instanceof Object && hash == obj) {
            this._persistentContactLists.removeElementAt(i);
         }
      }

      this.commit();
      this._collectionListenerManager.fireElementRemoved(this, list);
   }

   final void renameContactList(PeerContactList group, String newName) {
      int hash = newName.hashCode();
      Object list = this._groupId2Group.get(hash);
      if (list == null) {
         this._groupId2Group.remove(group.getIdHash());
         this._contactLists.removeElement(group);
         group.setDisplayName(newName);
         this._groupId2Group.put(group.getIdHash(), group);
         this._contactLists.add(group);
      } else {
         PeerApplication.getInstance().alertUser(2, QmResources.format(30, newName));
      }

      this._collectionListenerManager.fireElementUpdated(this, group, group);
   }

   final int getContactListCount() {
      return this._groupId2Group.size();
   }

   final synchronized PeerContact[] getContacts(int id) {
      int size = this._contacts.size();
      Vector contacts = (Vector)(new Object());

      for (int i = 0; i < size; i++) {
         PeerContact current = (PeerContact)this._contacts.getAt(i);
         if (PeerContactRecognizer.recognize(id, current)) {
            contacts.addElement(current);
         }
      }

      PeerContact[] result = new PeerContact[contacts.size()];
      contacts.copyInto(result);
      return result;
   }

   final PeerContact[] getInvitationChoices(PeerConversation conversation, String myHashId) {
      Vector v = (Vector)(new Object());
      synchronized (this._contacts) {
         for (int i = 0; i < this.getContactsCount(); i++) {
            PeerContact c = (PeerContact)this.getContactAt(i);
            if (c.isAuthorized() && c.getIdHash() != myHashId.hashCode() && !conversation.isParticipant(c)) {
               v.addElement(c);
            }
         }
      }

      PeerContact[] contacts = new PeerContact[v.size()];
      v.copyInto(contacts);
      return contacts;
   }

   public final void addNewRequest(PeerRequest newRequest) {
      if (newRequest instanceof WrongPasscodeNotification) {
         this.addNewRequestInternal(newRequest);
      } else {
         int index = this._newRequests.find(newRequest);
         if (index >= 0) {
            PeerRequest previousRequest;
            synchronized (this._newRequests) {
               previousRequest = (PeerRequest)this._newRequests.getAt(index);
               this._newRequests.add(newRequest);
            }

            this._persistedNewRequests.removeElement(previousRequest.getPersistentData());
            this._persistedNewRequests.addElement(newRequest.getPersistentData());
            this._collectionListenerManager.fireElementUpdated(this, previousRequest, newRequest);
         } else {
            this.addNewRequestInternal(newRequest);
         }
      }

      PeerData.saveRequests(this._persistedNewRequests);
   }

   final void addNewRequestInternal(PeerRequest newRequest) {
      synchronized (this._newRequests) {
         this._newRequests.add(newRequest);
      }

      this._persistedNewRequests.addElement(newRequest.getPersistentData());
      PeerApplication.getInstance();
      PeerApplication.unreadConversationCount().increment(newRequest);
      PeerApplication.notifications().triggerNewRequest(newRequest);
      this._collectionListenerManager.fireElementAdded(this, newRequest);
   }

   public final void removeNewRequest(PeerRequest request) {
      if (request != null) {
         synchronized (this._newRequests) {
            int index = this._newRequests.find(request);
            if (index >= 0) {
               this._newRequests.remove(index);
            }
         }

         this._persistedNewRequests.removeElement(request.getPersistentData());
         PeerApplication.unreadConversationCount().decrement();
         PeerApplication.notifications().cancelNewRequest(request);
         PeerData.saveRequests(this._persistedNewRequests);
         this._collectionListenerManager.fireElementRemoved(this, request);
      }
   }

   public final void expireNewRequest(PeerRequest request) {
      int index = this._newRequests.find(request);
      if (index >= 0) {
         PeerApplication.notifications().cancelNewRequest(request);
         PeerApplication.unreadConversationCount().decrement();
         this._collectionListenerManager.fireElementUpdated(this, request, request);
      }
   }

   public final void reloadRequests() {
      this._persistedNewRequests = PeerData.getRequests();
      if (this._persistedNewRequests != null) {
         synchronized (this._persistedNewRequests) {
            for (int i = this._persistedNewRequests.size() - 1; i >= 0; i--) {
               PeerRequest request = this.getPeerRequestAt(i);
               if (request != null) {
                  this._newRequests.add(request);
                  PeerApplication.getInstance();
                  PeerApplication.unreadConversationCount().increment(request);
               } else {
                  this._persistedNewRequests.removeElementAt(i);
               }
            }
         }
      } else {
         this._persistedNewRequests = (Vector)(new Object());
         PeerData.saveRequests(this._persistedNewRequests);
      }
   }

   public final void expireNewRequests(String requestName) {
      for (int i = this._newRequests.size() - 1; i >= 0; i--) {
         PeerRequest request = (PeerRequest)this._newRequests.getAt(i);
         if (request instanceof NewContactRequest && request.equals(requestName)) {
            this._newRequests.remove(i);
            this._persistedNewRequests.removeElement(request.getPersistentData());
            if (!request.isRead()) {
               PeerApplication.notifications().cancelNewRequest(request);
               PeerApplication.unreadConversationCount().decrement();
            }

            this._collectionListenerManager.fireElementRemoved(this, request);
         }
      }

      if (this._persistedNewRequests != null) {
         PeerData.saveRequests(this._persistedNewRequests);
      }
   }

   public final void expireNewRequests(boolean commit, boolean remove) {
      synchronized (this._newRequests) {
         for (int i = this._newRequests.size() - 1; i >= 0; i--) {
            PeerApplication.unreadConversationCount().decrement();
            PeerApplication.notifications().cancelNewRequest((PeerRequest)this._newRequests.getAt(i));
            if (remove) {
               this._newRequests.remove(i);
            }
         }
      }

      if (remove) {
         this._persistedNewRequests.removeAllElements();
      }

      if (commit) {
         PeerData.saveRequests(this._persistedNewRequests);
      }
   }

   final void initiateSearch(String pattern) {
      synchronized (this._searchData) {
         if (this._searcher == null) {
            this._searchData._current = pattern;
            this._searchData._currentAvailable = true;
            this._searcher = new PeerContactListCollection$SearcherThread(this);
            this._searcher.start();
         } else {
            this._searchData._next = pattern;
            this._searchData._nextAvailable = true;
         }
      }
   }

   public final SimpleSortingVector getContactListsAsVector() {
      return this._contactLists;
   }

   public final boolean update(String newPattern) {
      if (newPattern != null) {
         newPattern = newPattern.trim();
         if (newPattern.length() == 0) {
            newPattern = null;
         }
      }

      PeerContact contact = null;
      int numContacts = this.getContactsCount();
      if (newPattern != null) {
         BitSet bitset = this._contactKeywords.getMatchingContacts(newPattern);
         synchronized (this._searchData) {
            if (this._searchData._nextAvailable) {
               return false;
            }
         }

         if (bitset != null) {
            for (int i = 0; i < numContacts; i++) {
               contact = (PeerContact)this.getContactAt(i);
               contact.setFiltered(!bitset.isSet(contact.getUID()));
            }
         }
      } else {
         for (int i = 0; i < numContacts; i++) {
            contact = (PeerContact)this.getContactAt(i);
            contact.setFiltered(false);
         }
      }

      return true;
   }

   final PeerContactList getDefaultContactList() {
      return this._defaultContactList;
   }

   @Override
   public final Contact getContactAt(int index) {
      return (Contact)this._contacts.getAt(index);
   }

   @Override
   public final int getContactsCount() {
      return this._contacts.size();
   }

   @Override
   public final int getRequestsCount() {
      return this._newRequests.size();
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collectionListenerManager.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collectionListenerManager.removeCollectionListener(listener);
   }

   @Override
   public final Request getRequestAt(int index) {
      return (Request)this._newRequests.getAt(index);
   }

   @Override
   public final Enumeration getContactLists() {
      return this._groupId2Group.elements();
   }

   private final boolean addContactInternal(PeerContact contact, PeerContactList list, boolean persist) {
      boolean added = false;
      synchronized (this._contacts) {
         if (this._contacts.find(contact) < 0) {
            this._contacts.add(contact);
            added = true;
            if (contact.isAuthorized()) {
               this.addAuthorizedContact(contact, list, persist);
            }
         }

         return added;
      }
   }

   private final PeerRequest getPeerRequestAt(int index) {
      PeerRequest result = null;
      IntHashtable data = (IntHashtable)this._persistedNewRequests.elementAt(index);
      Object obj = data.get(9);
      if (obj instanceof Object && obj.equals("1.1.0")) {
         switch (data.get(1)) {
            case 1:
               break;
            case 2:
            default:
               return new NewContactRequest(data);
            case 3:
               result = new WrongPasscodeNotification(data);
         }
      }

      return result;
   }

   private final boolean removeAuthorizedContact(PeerContact contact) {
      boolean ret = false;
      synchronized (this._contactKeywords) {
         if (this._contactKeywords.contains(contact)) {
            this._contactKeywords.removeKeywords(contact);
            this._contactKeywords.remove(contact);
         } else {
            String error = ((StringBuffer)(new Object("Contact list error detected on deleting contact "))).append(contact.getId()).toString();
            EventLogger.logEvent(-9029900896793868512L, error.getBytes(), 3);
         }
      }

      if (contact.getRefCount() != 0) {
         contact.setAuthorized(false);
         return ret;
      }

      synchronized (this._contacts) {
         int index = this._contacts.find(contact);
         if (index >= 0) {
            this._contacts.remove(index);
            ret = true;
         }

         return ret;
      }
   }

   private final void updateKeywordsFor(PeerContact contact) {
      if (contact.isAuthorized()) {
         synchronized (this._contactKeywords) {
            this._contactKeywords.updateKeywords(contact);
         }
      }
   }

   private final boolean removeContactInternal(PeerContact contact) {
      if (contact.isPending()) {
         _pendingContacts.remove(contact.getCookie());
      }

      contact.removeItselfFromContactList();
      return contact.isAuthorized() ? this.removeAuthorizedContact(contact) : false;
   }

   private final void addAuthorizedContact(PeerContact contact, PeerContactList list, boolean persist) {
      if (list == null) {
         list = this.getDefaultContactList();
      }

      contact.addContactList(list);
      if (persist) {
         list.addContact(contact);
      }

      synchronized (this._contactKeywords) {
         contact.setUID(this._contactKeywords.addAndGetIndex(contact));
         this._contactKeywords.addKeywords(contact, null);
      }
   }

   public PeerContactListCollection() {
      this._collectionListenerManager = (CollectionListenerManager)(new Object());
      this._groupId2Group = (IntHashtable)(new Object());
      this._contactLists = (SimpleSortingVector)(new Object());
      this._contactLists.setSortComparator(_contactListsSorter);
      this._contactLists.setSort(true);
      this._contacts = (SimpleSortingVector)(new Object());
      this._contacts.setSortComparator(_contactsSorter);
      this._contacts.setSort(true);
      this._newRequests = (SimpleSortingVector)(new Object());
      this._newRequests.setSortComparator(_requestsSorter);
      this._newRequests.setSort(true);
      this._contactKeywordsData = new ContactKeywordsData();
      this._contactKeywords = new GTPatriciaTree(this, this._contactKeywordsData);
   }

   static final void addPendingContact(int cookie, PeerContact contact) {
      _pendingContacts.put(cookie, contact);
   }

   static final PeerContact getPendingContact(int cookie) {
      return (PeerContact)_pendingContacts.get(cookie);
   }

   private final void reloadContacts() {
      int size = this._persistentContactLists.size();

      for (int i = 0; i < size; i++) {
         IntHashtable contactListData = (IntHashtable)this._persistentContactLists.elementAt(i);
         PeerContactList list = new PeerContactList(contactListData);
         this._groupId2Group.put(list.getIdHash(), list);
         this._contactLists.add(list);
         Vector contacts = (Vector)contactListData.get(5);
         int count = contacts.size();

         for (int j = 0; j < count; j++) {
            this.restoreContact((IntHashtable)contacts.elementAt(j), list);
         }

         if (this._defaultContactList == null) {
            this._defaultContactList = list;
         }
      }
   }

   private final void commit() {
      PersistentObject.commit(this._persistentContactLists);
   }

   static final CollectionListenerManager access$000(PeerContactListCollection x0) {
      return x0._collectionListenerManager;
   }
}
