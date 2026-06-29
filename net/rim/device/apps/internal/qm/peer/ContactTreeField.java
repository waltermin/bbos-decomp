package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.qm.peer.common.ContactListProvider;
import net.rim.device.apps.internal.qm.peer.common.Conversation;
import net.rim.device.apps.internal.qm.peer.common.EllipsesText;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;
import net.rim.device.apps.internal.qm.peer.common.Request;
import net.rim.device.apps.internal.smileys.Smileys;
import net.rim.vm.WeakReference;

final class ContactTreeField extends VerticalFieldManager implements CollectionListener {
   private PeerApplication _application;
   private ContactListProvider _contactLists;
   private WeakReference _collectionListener;
   private Hashtable _lookup = (Hashtable)(new Object(24));
   private Hashtable _lookupMulti = (Hashtable)(new Object(2));
   private int _fontHeight;
   private int _iconHeight;
   private ContactTreeField$ConversationBranch _conversationBranch = new ContactTreeField$ConversationBranch(null);
   private ContactTreeField$UnauthorizedBranch _unauthorizedBranch = new ContactTreeField$UnauthorizedBranch();
   private ContactTreeField$OfflineBranch _offlineBranch = new ContactTreeField$OfflineBranch();
   private ContactTreeField$PendingBranch _pendingBranch = new ContactTreeField$PendingBranch();
   private ContactTreeField$NewRequestBranch _newRequestBranch = new ContactTreeField$NewRequestBranch();
   private static Tag TAG = Tag.create("bbmessenger-contacttree");
   private static int CONVERSATION_BRANCH_ID = 1;
   private static int UNAUTHORIZED_BRANCH_ID = 2;
   private static int OFFLINE_BRANCH_ID = 3;
   private static int PENDING_BRANCH_ID = 5;
   private static int NEW_REQUEST_BRANCH_ID = 6;
   static EmoticonStringPattern _smileyFacility = Smileys.getSmileyFacility();

   ContactTreeField(ContactListProvider contactList) {
      super(0);
      this.setTag(TAG);
      this._application = PeerApplication.getInstance();
      this._contactLists = contactList;
      this._collectionListener = (WeakReference)(new Object(this));
      if (this._contactLists instanceof Object) {
         CollectionEventSource collectionEventSource = (CollectionEventSource)this._contactLists;
         collectionEventSource.addCollectionListener(this._collectionListener);
      }

      PeerApplication.conversations().addCollectionListener(this._collectionListener);
      this._fontHeight = Font.getDefault().getHeight();
      this._iconHeight = PeerResources.getIconHeight(Font.getDefault());
      this.populate(contactList);
   }

   private final void updateContactListUi() {
      this.updateLayout();
      int branchCount = this.getFieldCount();

      for (int i = 0; i < branchCount; i++) {
         Field field = this.getField(i);
         if (field instanceof Branch) {
            Branch branch = (Branch)field;
            int contactCount = branch.getFieldCount();

            for (int j = 0; j < contactCount; j++) {
               field = branch.getField(j);
               PeerContact contact = this.getContact(field);
               if (contact != null && !contact.isFiltered()) {
                  field.setFocus();
                  if (!branch.isExpanded()) {
                     branch.toggleExpansion();
                  }

                  return;
               }
            }
         }
      }

      this.goTop();
   }

   private final PeerContact getContact(Field field) {
      PeerContact contact = null;
      if (!(field instanceof ContactTreeField$ConversationLeaf)) {
         if (field instanceof ContactTreeField$ContactLeaf) {
            contact = ((ContactTreeField$ContactLeaf)field).getContact();
         }

         return contact;
      } else {
         PeerConversation conversation = (PeerConversation)((ContactTreeField$ConversationLeaf)field).getCookie();
         return conversation.getFirstParticipant();
      }
   }

   final void lock() {
      ContactTreeField$ConversationBranch.access$100(this._conversationBranch);
      Enumeration enumeration = this._lookup.elements();

      while (enumeration.hasMoreElements()) {
         Object obj = enumeration.nextElement();
         if (obj instanceof ContactTreeField$ContactListBranch) {
            ((ContactTreeField$ContactListBranch)obj).lock();
         }
      }
   }

   final int compare(String key, Field field) {
      if (field instanceof ContactTreeField$ContactListBranch) {
         int result = StringUtilities.compareToIgnoreCase(key, ((PeerContactList)field.getCookie()).getDisplayName());
         return result == 0 ? -1 : result;
      } else {
         return !(field instanceof ContactTreeField$ConversationBranch) && !(field instanceof ContactTreeField$NewRequestBranch) ? -1 : 1;
      }
   }

   final int binarySearch(String displayName) {
      int low = 1;
      int high = this.getFieldCount() - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int result = this.compare(displayName, this.getField(mid));
         if (result < 0) {
            high = mid - 1;
         } else {
            if (result <= 0) {
               return mid;
            }

            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   @Override
   public final void add(Field field) {
      if (field instanceof ContactTreeField$ContactListBranch) {
         int index = this.binarySearch(((PeerContactList)field.getCookie()).getDisplayName());
         if (index <= 0) {
            super.insert(field, -index - 1);
            return;
         }
      } else {
         super.add(field);
      }
   }

   final void onFontChanged() {
      this._fontHeight = Font.getDefault().getHeight();
      this._iconHeight = PeerResources.getIconHeight(Font.getDefault());
   }

   private final void goTop() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof BranchLeaf) {
         if ((field = field.getManager().getField(0)) instanceof BranchTitleField) {
            field.setFocus();
            return;
         }
      } else if (field instanceof BranchTitleField) {
         this._conversationBranch.getField(0).setFocus();
      }
   }

   final void selectConversation(PeerConversation c) {
      Field field = (Field)this._lookup.get(c);
      if (field instanceof BranchLeaf) {
         Field manager = field.getManager();
         if (manager instanceof Branch && ((Branch)manager).isExpanded()) {
            field.setFocus();
         }
      }
   }

   final Object getSelectedObject() {
      Object object = null;
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         if (field instanceof BranchTitleField || field instanceof EmptyBranchPlaceholder) {
            return field.getManager().getCookie();
         }

         object = field.getCookie();
      }

      return object;
   }

   final PeerContactList getSelectedGroup() {
      PeerContactList group = null;
      Field field = this.getLeafFieldWithFocus();
      if (field != null) {
         Object object = null;
         if (!(field instanceof ContactTreeField$ContactLeaf) && !(field instanceof BranchTitleField) && !(field instanceof EmptyBranchPlaceholder)) {
            object = field.getCookie();
         } else {
            object = field.getManager().getCookie();
         }

         if (object instanceof PeerContactList) {
            group = (PeerContactList)object;
         }
      }

      return group;
   }

   final void invalidate(Object object) {
      if (this._application.isEventThread()) {
         this.handleInvalidate(object);
      } else {
         this._application.postInvokeLaterInternal(new ContactTreeField$1(this, object));
      }
   }

   private final TreeItem getTreeItem(Object object) {
      if (!(object instanceof TreeItem)) {
         Object o = this._lookup.get(object);
         if (o != null) {
            return (TreeItem)o;
         }

         Vector v = (Vector)this._lookupMulti.get(object);
         return v != null ? (TreeItem)v.elementAt(0) : null;
      } else {
         return (TreeItem)object;
      }
   }

   private final void handleInvalidate(Object object) {
      TreeItem treeItem = this.getTreeItem(object);
      if (treeItem != null) {
         treeItem.doInvalidate(true);
      }
   }

   final void paintNewRequest(Graphics graphics, PeerRequest request, Field fieldToPaintFor) {
      if (request != null) {
         int y = this._fontHeight > this._iconHeight ? (this._fontHeight - this._iconHeight) / 2 : 0;
         int x = PeerResources.iconIndent(this.getFont());
         int width = this.getWidth();
         int iconId = -1;
         int id = -1;
         byte var12;
         if (request.getIconId() == 9) {
            iconId = request.isRead() ? 1 : 3;
            var12 = 9;
         } else {
            iconId = request.isRead() ? 0 : 2;
            var12 = 7;
         }

         PeerResources.drawIcon(graphics, x, y, iconId);
         PeerResources.drawIcon(graphics, x, y, var12);
         x += PeerResources.getIconHeight(this.getFont());
         y = this._fontHeight < this._iconHeight ? (this._iconHeight - this._fontHeight) / 2 : 0;
         EllipsesText.draw(graphics, request.toString(), x, y, width - x);
      }
   }

   final void paintContact(Graphics graphics, PeerConversation conversation, PeerContact contact, Field fieldToPaintFor) {
      if (contact != null) {
         contact.paint(graphics, conversation, PeerResources.iconIndent(this.getFont()), 0, this.getWidth(), this._fontHeight, this._iconHeight, true);
      }
   }

   private final int getFontHeight(PeerConversation conversation) {
      PeerContact contact = conversation.getFirstParticipant();
      return QmUtil.calculateTrueFontHeight(contact != null ? contact.getDisplayName() : null);
   }

   final void paintConversation(Graphics graphics, PeerConversation conversation) {
      int fontHeight = this.getFontHeight(conversation);
      int absHeight = fontHeight > this._iconHeight ? fontHeight : this._iconHeight;
      int smileysHeight = _smileyFacility.emoticonSize();
      if (smileysHeight > absHeight) {
         absHeight = smileysHeight;
      }

      int x = PeerResources.iconIndent(this.getFont());
      this.paintIcons(graphics, conversation, x, this.getY(absHeight, this._iconHeight));
      x += PeerResources.getIconHeight(this.getFont());
      Font originalFont = conversation.getParticipants().size() == 0 ? this.changeFont(graphics) : null;
      int y = this.paintTitle(graphics, conversation, x, absHeight, fontHeight, smileysHeight);
      if (originalFont != null) {
         graphics.setFont(originalFont);
      }
   }

   private final Font changeFont(Graphics graphics) {
      Font originalFont = graphics.getFont();
      Font newFont = originalFont.derive(2);
      graphics.setFont(newFont);
      return originalFont;
   }

   private final int paintTitle(Graphics graphics, PeerConversation conversation, int x, int absHeight, int fontHeight, int smileysHeight) {
      int y = this.getY(absHeight, fontHeight);
      conversation.drawTitle(graphics, x, y, this.getY(absHeight, smileysHeight), this.getWidth() - x);
      return y;
   }

   private final void paintIcons(Graphics graphics, PeerConversation conversation, int x, int y) {
      int iconId = -1;
      int size = conversation.getParticipants().size();
      if (size != 0 && size <= 1) {
         PeerContact contact = conversation.getFirstParticipant();
         contact.drawIcon(graphics, x, y, conversation.isUnread());
         if (contact.isAlertable() && !contact.isPending() && PeerApplication.alerts().isSet(contact)) {
            PeerResources.drawIcon(graphics, x, y, 10);
         }
      } else {
         iconId = conversation.isUnread() ? 5 : 4;
         PeerResources.drawIcon(graphics, x, y, iconId);
      }

      for (int i = conversation.getParticipants().size() - 1; i >= 0; i--) {
         if (conversation.getParticipant(i).isTyping()) {
            PeerResources.drawIcon(graphics, x, y, 12);
            return;
         }
      }
   }

   private final int getY(int absHeight, int height) {
      return absHeight > height ? absHeight - height >> 1 : 0;
   }

   private final Branch getContactListBranch(PeerContactList group) {
      ContactTreeField$ContactListBranch branch = (ContactTreeField$ContactListBranch)this._lookup.get(group);
      if (branch == null) {
         branch = new ContactTreeField$ContactListBranch(group);
         this._lookup.put(group, branch);
         this.add(branch);
      }

      return branch;
   }

   final ContactTreeField$ContactLeaf getContactLeaf(PeerContact contact) {
      return (ContactTreeField$ContactLeaf)this._lookup.get(contact);
   }

   final ContactTreeField$ContactLeaf createContactLeaf(PeerContact contact) {
      ContactTreeField$ContactLeaf contactLeaf = this.getContactLeaf(contact);
      if (contactLeaf == null) {
         Vector v = (Vector)this._lookupMulti.get(contact);
         if (v == null) {
            contactLeaf = new ContactTreeField$ContactLeaf(this, contact);
            this._lookup.put(contact, contactLeaf);
            return contactLeaf;
         }

         int size = v.size();

         for (int i = 0; i < size; i++) {
            ContactTreeField$ContactLeaf leaf = (ContactTreeField$ContactLeaf)v.elementAt(i);
            if (leaf.getManager() == null) {
               contactLeaf = leaf;
               break;
            }
         }

         if (contactLeaf == null) {
            contactLeaf = new ContactTreeField$ContactLeaf(this, contact);
            v.addElement(contactLeaf);
            return contactLeaf;
         }
      } else if (contactLeaf.getManager() != null) {
         this._lookup.remove(contact);
         Vector v = (Vector)(new Object(2));
         v.addElement(contactLeaf);
         contactLeaf = new ContactTreeField$ContactLeaf(this, contact);
         v.addElement(contactLeaf);
         this._lookupMulti.put(contact, v);
      }

      return contactLeaf;
   }

   final ContactTreeField$NewRequestLeaf createNewRequestLeaf(Request request) {
      ContactTreeField$NewRequestLeaf requestLeaf = new ContactTreeField$NewRequestLeaf(this, request);
      this._lookup.put(request, requestLeaf);
      return requestLeaf;
   }

   private final ContactTreeField$ConversationLeaf getConversationLeaf(Conversation conversation) {
      return (ContactTreeField$ConversationLeaf)this._lookup.get(conversation);
   }

   final ContactTreeField$ConversationLeaf createConversationLeaf(PeerConversation conversation) {
      ContactTreeField$ConversationLeaf conversationLeaf = this.getConversationLeaf(conversation);
      if (conversationLeaf == null) {
         conversationLeaf = new ContactTreeField$ConversationLeaf(this, conversation);
         this._lookup.put(conversation, conversationLeaf);
      }

      return conversationLeaf;
   }

   private final Branch[] chooseBranch(PeerContact contact) {
      Branch[] branch = null;
      if (contact.isPending()) {
         branch = new Branch[]{this._pendingBranch};
      } else if (contact.isAvailable()) {
         Vector groups = contact.getContactLists();
         int size = groups.size();
         branch = new Branch[size];

         for (int i = 0; i < size; i++) {
            branch[i] = this.getContactListBranch((PeerContactList)groups.elementAt(i));
         }
      }

      return branch == null ? new Branch[]{this._offlineBranch} : branch;
   }

   private final void populateContactLists(ContactListProvider collection) {
      Enumeration groups = this._contactLists.getContactLists();

      while (groups.hasMoreElements()) {
         this.getContactListBranch((PeerContactList)groups.nextElement());
      }

      int size = this._contactLists.getContactsCount();

      for (int i = 0; i < size; i++) {
         this.addContact((PeerContact)this._contactLists.getContactAt(i));
      }
   }

   private final void addContact(PeerContact contact) {
      Branch[] branches = this.chooseBranch(contact);

      for (int i = 0; i < branches.length; i++) {
         ContactTreeField$ContactLeaf contactLeaf = this.createContactLeaf(contact);
         branches[i].add(contactLeaf);
      }
   }

   private final void addConversation(PeerConversation conversation) {
      ContactTreeField$ConversationLeaf conversationLeaf = this.getConversationLeaf(conversation);
      if (conversationLeaf == null) {
         conversationLeaf = this.createConversationLeaf(conversation);
         this._conversationBranch.add(conversationLeaf);
      } else {
         conversationLeaf.doInvalidate(true);
      }
   }

   private final void addNewRequest(Request request) {
      ContactTreeField$NewRequestLeaf requestLeaf = this.createNewRequestLeaf(request);
      this._newRequestBranch.add(requestLeaf);
   }

   private final void populateConversations(ContactListProvider collection) {
      Conversations conversations = PeerApplication.conversations();

      for (int i = conversations.size() - 1; i >= 0; i--) {
         this.addConversation((PeerConversation)conversations.getAt(i));
      }
   }

   private final void populateNewRequests(ContactListProvider collection) {
      int size = this._contactLists.getRequestsCount();

      for (int i = 0; i < size; i++) {
         this.addNewRequest(this._contactLists.getRequestAt(i));
      }
   }

   private final void clear() {
      this.deleteAll();
      this._conversationBranch.clear();
      this._newRequestBranch.clear();
      this._unauthorizedBranch.clear();
      this._offlineBranch.clear();
      this._pendingBranch.clear();
      this._lookup.clear();
      this._lookupMulti.clear();
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         Field field = this.getLeafFieldWithFocus();
         if (field instanceof BranchTitleField) {
            ((BranchTitleField)field).toggleExpansion();
            return true;
         }
      }

      return super.invokeAction(action);
   }

   final boolean isExpanded() {
      boolean result = this._conversationBranch.isExpanded()
         || this._newRequestBranch.isExpanded()
         || this._unauthorizedBranch.isExpanded()
         || this._offlineBranch.isExpanded()
         || this._pendingBranch.isExpanded();
      if (!result) {
         Enumeration groups = this._contactLists.getContactLists();

         while (groups.hasMoreElements() && !result) {
            result = this.getContactListBranch((PeerContactList)groups.nextElement()).isExpanded();
         }
      }

      return result;
   }

   final void collapseAll() {
      if (this._conversationBranch.isExpanded()) {
         this._conversationBranch.toggleExpansion();
      }

      if (this._newRequestBranch.isExpanded()) {
         this._newRequestBranch.toggleExpansion();
      }

      if (this._unauthorizedBranch.isExpanded()) {
         this._unauthorizedBranch.toggleExpansion();
      }

      if (this._offlineBranch.isExpanded()) {
         this._offlineBranch.toggleExpansion();
      }

      if (this._pendingBranch.isExpanded()) {
         this._pendingBranch.toggleExpansion();
      }

      Enumeration groups = this._contactLists.getContactLists();

      while (groups.hasMoreElements()) {
         Branch current = this.getContactListBranch((PeerContactList)groups.nextElement());
         if (current.isExpanded()) {
            current.toggleExpansion();
         }
      }

      Field field = this.getLeafFieldWithFocus();
      if (field instanceof BranchLeaf && (field = field.getManager().getField(0)) instanceof BranchTitleField) {
         field.setFocus();
      }
   }

   final void onLocaleChanged() {
      this._conversationBranch.doInvalidate(false);
   }

   final void populate(ContactListProvider collection) {
      synchronized (this._contactLists) {
         Manager manager = this.getManager();
         if (manager != null) {
            manager.delete(this);
         }

         this.clear();
         this.add(this._conversationBranch);
         this.add(this._newRequestBranch);
         this.add(this._unauthorizedBranch);
         this.add(this._offlineBranch);
         this.add(this._pendingBranch);
         this.populateConversations(collection);
         this.populateNewRequests(collection);
         this.populateContactLists(collection);
         if (manager != null) {
            manager.add(this);
         }
      }
   }

   private final void handleElementAdded(Object element) {
      if (element instanceof PeerContactList) {
         this.getContactListBranch((PeerContactList)element);
      } else if (!(element instanceof PeerContact)) {
         if (!(element instanceof PeerConversation)) {
            if (element instanceof PeerRequest) {
               PeerRequest request = (PeerRequest)element;
               this.addNewRequest(request);
            }
         } else {
            PeerConversation conversation = (PeerConversation)element;
            this.addConversation(conversation);
         }
      } else {
         PeerContact contact = (PeerContact)element;
         this.addContact(contact);
      }
   }

   private final void handleElementRemoved(Object element) {
      Object field = this._lookup.get(element);
      if (field != null) {
         this.removeField((Field)field);
         this._lookup.remove(element);
      } else {
         Vector v = (Vector)this._lookupMulti.get(element);
         if (v != null) {
            for (int i = 0; i < v.size(); i++) {
               this.removeField((Field)v.elementAt(i));
            }

            this._lookupMulti.remove(element);
         }
      }

      this.updateLayout();
   }

   private final void removeField(Field field) {
      Manager manager = field.getManager();
      if (manager != null) {
         manager.delete(field);
      }
   }

   private final void handleElementUpdated(Object oldElement, Object newElement) {
      if (oldElement == newElement) {
         this.invalidate(oldElement);
      } else {
         this.handleElementRemoved(oldElement);
         this.handleElementAdded(newElement);
      }
   }

   private final void handleReset(Collection collection) {
      if (collection instanceof ContactListProvider) {
         Field selection = this.getLeafFieldWithFocus();
         Manager parent = selection != null ? selection.getManager() : null;
         int index = selection != null ? parent.getFieldWithFocusIndex() : -1;
         this.populate((ContactListProvider)collection);
         if (selection != null) {
            Field field = this.getField(selection, parent);
            if (field == null) {
               field = parent.getField(index < parent.getFieldCount() ? index : parent.getFieldCount() - 1);
            }

            if (field != null) {
               if (field.getScreen() == null) {
                  return;
               }

               field.setFocus();
            }
         }
      }
   }

   private final Field getField(Field selection, Manager parent) {
      Field field = null;
      Object cookie = selection.getCookie();
      if (cookie != null) {
         field = (Field)this._lookup.get(cookie);
         if (field == null && parent != null) {
            Vector v = (Vector)this._lookupMulti.get(cookie);
            if (v != null) {
               Field f = null;

               for (int i = 0; i < v.size(); i++) {
                  f = (Field)v.elementAt(i);
                  if (f.getManager() == parent) {
                     return f;
                  }
               }
            }
         }
      }

      return field;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (this._application.isEventThread()) {
         this.handleElementAdded(element);
      } else {
         this._application.postInvokeLaterInternal(new ContactTreeField$2(this, element));
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (this._application.isEventThread()) {
         this.handleElementRemoved(element);
      } else {
         this._application.postInvokeLaterInternal(new ContactTreeField$3(this, element));
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._application.isEventThread()) {
         this.handleElementUpdated(oldElement, newElement);
      } else {
         this._application.postInvokeLaterInternal(new ContactTreeField$4(this, oldElement, newElement));
      }
   }

   @Override
   public final void reset(Collection collection) {
      synchronized (this._application.getAppEventLock()) {
         if (collection instanceof ContactListProvider) {
            this.handleReset(collection);
         } else {
            this.updateContactListUi();
         }
      }
   }

   static final void access$200(ContactTreeField x0, Object x1) {
      x0.handleInvalidate(x1);
   }

   static final void access$300(ContactTreeField x0, Object x1) {
      x0.handleElementAdded(x1);
   }

   static final void access$400(ContactTreeField x0, Object x1) {
      x0.handleElementRemoved(x1);
   }

   static final void access$500(ContactTreeField x0, Object x1, Object x2) {
      x0.handleElementUpdated(x1, x2);
   }

   static final int access$600() {
      return CONVERSATION_BRANCH_ID;
   }

   static final int access$800() {
      return UNAUTHORIZED_BRANCH_ID;
   }

   static final int access$900() {
      return OFFLINE_BRANCH_ID;
   }

   static final int access$1000() {
      return PENDING_BRANCH_ID;
   }

   static final int access$1100() {
      return NEW_REQUEST_BRANCH_ID;
   }

   static final Branch[] access$1200(ContactTreeField x0, PeerContact x1) {
      return x0.chooseBranch(x1);
   }

   static final Hashtable access$1300(ContactTreeField x0) {
      return x0._lookupMulti;
   }

   static final ContactTreeField$ConversationLeaf access$1400(ContactTreeField x0, Conversation x1) {
      return x0.getConversationLeaf(x1);
   }

   static final Hashtable access$1500(ContactTreeField x0) {
      return x0._lookup;
   }

   static final int access$1600(ContactTreeField x0) {
      return x0._iconHeight;
   }

   static final PeerApplication access$1700(ContactTreeField x0) {
      return x0._application;
   }

   static final ContactTreeField$ConversationBranch access$1800(ContactTreeField x0) {
      return x0._conversationBranch;
   }

   static final TreeItem access$1900(ContactTreeField x0, Object x1) {
      return x0.getTreeItem(x1);
   }

   static final int access$2000(ContactTreeField x0) {
      return x0._fontHeight;
   }
}
