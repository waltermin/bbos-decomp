package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.MessageListColumnPainter;
import net.rim.device.apps.internal.qm.peer.common.Contact;
import net.rim.device.apps.internal.qm.peer.common.Conversation;
import net.rim.device.apps.internal.qm.peer.common.EllipsesText;
import net.rim.device.apps.internal.qm.peer.common.NotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.NotificationMessageField;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.vm.Array;

final class PeerConversation
   implements Conversation,
   Collection,
   ReadableList,
   CollectionEventSource,
   ActionProvider,
   KeyProvider,
   HotKeyProvider,
   VerbProvider,
   ColumnPaintProvider {
   private IntHashtable _persistentData;
   private Object _uniqueId;
   private int _idHashCode;
   private Vector _participants;
   private CollectionListenerManager _clm = new CollectionListenerManager();
   private Object _threadState;
   private Vector _messages = new Vector();
   private Vector _persistedMessages = new Vector();
   private long _timeStarted;
   private PeerConversationsFolder _folder;
   private final StringBuffer _history = new StringBuffer();
   private PeerConversation$PeerIconCollection _iconCollection = new PeerConversation$PeerIconCollection(this, null);
   private MessageListColumnPainter _painter;
   private boolean _existsInMessageList;
   private boolean _conference;
   private boolean _unread;
   private PeerConversation$OpenConversationVerb _openConv = new PeerConversation$OpenConversationVerb(this);
   private PeerConversation$ConversationMarkVerb _markUnread = new PeerConversation$ConversationMarkVerb(this);
   static final int UNIQUE_ID = 1;
   static final int UNIQUE_ID_HASH = 2;
   static final int PARTICIPANTS_ID = 3;
   static final int UNREAD_ID = 4;
   static final int CONFERENCE_ID = 5;
   static final int MESSAGES_ID = 6;
   static final int TIMESTARTED_ID = 7;
   static final int FIRST_PARTICIPANT = 8;
   static final int PARTICIPANT_HASH_ID = 1;
   static final int PARTICIPANT_STRING_NAME = 2;
   static final int PARTICIPANT_STRING_ID = 3;

   final IntHashtable getPersistentData() {
      return this._persistentData;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      Array.resize(verbs, 2);
      int numberOfVerbsAdded = 0;
      int var10001 = numberOfVerbsAdded++;
      defaultVerb = this._openConv;
      verbs[var10001] = this._openConv;
      if (this.perform(this.isUnread() ? -5544992959212130441L : 477896226347912237L, null)) {
         verbs[numberOfVerbsAdded++] = this._markUnread;
      }

      return defaultVerb;
   }

   final String getTitle() {
      switch (this._participants.size()) {
         case -1:
            return QmResources.format(29, this.getFirstParticipant().getDisplayName());
         case 0:
         default:
            return PeerResources.getString(29);
         case 1:
            return this.getFirstParticipant().getDisplayName();
      }
   }

   final String getId() {
      try {
         return PersistentContent.decodeString(this._uniqueId);
      } finally {
         ;
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      boolean res = false;
      if (actionId == NotificationMessageField.MATCH) {
         res = this.onNotificationMessageFieldMatch(context);
      } else if (actionId == NotificationMessageField.SWITCH) {
         this.onNotificationMessageFieldSwitch(context);
      } else if (actionId == 8669091670697752579L) {
         res = this.onActionProviderReply(context);
      } else if (actionId == 6099736323056465049L) {
         res = this.onActionProviderOpen(context);
      } else if (actionId == 635678369939227345L) {
         res = true;
      } else if (actionId == 5803508244060051872L) {
         this.onActionProviderMarkOpened();
      } else if (actionId == -8629311385729242560L) {
         this.onActionProviderMarkUnopened();
      } else if (actionId == -6225946334564270161L) {
         this.onActionProviderBulkMarkOpened();
      } else if (actionId == -5544992959212130441L) {
         res = this.onActionProviderCheckCanMarkOpened();
      } else if (actionId == 477896226347912237L) {
         res = this.onActionProviderCheckCanMarkUnopened();
      } else if (actionId == 6780594967363292755L || actionId == -3967872215949752466L) {
         res = this.onActionProviderDelete();
      }

      return res;
   }

   final Object getThreadState() {
      return this._threadState;
   }

   final boolean isConference() {
      return this._conference;
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      long actionID = -1;
      switch (hotkeyID) {
         case 148:
            actionID = 8669091670697752579L;
            break;
         case 152:
            actionID = this.isUnread() ? 5803508244060051872L : -8629311385729242560L;
      }

      return actionID != -1 && this.perform(actionID, context) ? context : null;
   }

   final void populatePersistedContacts(PeerContactListCollection contactList) {
      IntHashtable participants = (IntHashtable)this._persistentData.get(3);
      Object obj = this._persistentData.get(8);
      int first = !(obj instanceof Integer) ? 0 : (Integer)obj;
      Enumeration elements = participants.elements();

      while (elements.hasMoreElements()) {
         IntHashtable data = (IntHashtable)elements.nextElement();
         int hashId = (Integer)data.get(1);
         PeerContact current = contactList.findContactByHashId(hashId);
         if (current == null) {
            current = new PeerContact();
            current.setIdInternal((Integer)data.get(1), data.get(3));
            current.setDisplayNameInternal(data.get(2));
            current.setAuthorized(false);
            contactList.addContact(current);
         }

         if (first == hashId) {
            this._participants.insertElementAt(current, 0);
         } else {
            this._participants.addElement(current);
         }
      }
   }

   final void modifyParticipant(PeerContact participant, boolean add) {
      synchronized (this) {
         if (add) {
            this.addParticipant(participant);
         } else {
            this.removeParticipant(participant);
         }

         this.commit();
         this.fireElementUpdated(this);
         this.update();
      }
   }

   final void updateParticipantData(PeerContact contact) {
      if (contact != null && this._participants.indexOf(contact) != -1) {
         int hashId = contact.getIdHash();
         IntHashtable participant = (IntHashtable)((IntHashtable)this._persistentData.get(3)).get(hashId);
         participant.put(1, new Integer(hashId));
         participant.put(3, PersistentContent.encode(contact.getId(), true, true));
         participant.put(2, PersistentContent.encode(contact.getDisplayName(), true, true));
         this.commit();
      }
   }

   final void update() {
      if (this._existsInMessageList) {
         synchronized (FolderHierarchies.getLockObject()) {
            this._folder.fireElementUpdated(null, this, this);
         }
      }
   }

   final PeerContact getFirstParticipant() {
      return this.getParticipant(0);
   }

   final PeerContact getParticipant(int index) {
      return index >= 0 && index < this._participants.size() ? (PeerContact)this._participants.elementAt(index) : null;
   }

   final boolean isParticipant(Contact contact) {
      int index = this._participants.size() - 1;

      while (index >= 0 && !this._participants.elementAt(index).equals(contact)) {
         index--;
      }

      return index >= 0;
   }

   final void drawTitle(Graphics graphics, int x, int text_y, int smileys_y, int width) {
      int size = this._participants.size();
      if (size == 0) {
         EllipsesText.draw(graphics, PeerResources.getString(29), x, text_y, width);
      } else {
         x += this.getFirstParticipant().drawDisplayName(graphics, x, text_y, smileys_y, width);
         if (size != 1) {
            EllipsesText.draw(graphics, QmResources.format(29, ""), x, text_y, width);
         }
      }
   }

   final void setId(String newId) {
      if (newId != null) {
         newId = newId.toUpperCase();
         this._idHashCode = newId.hashCode();
         this._uniqueId = PersistentContent.encode(newId, true, true);
         this._persistentData.put(1, this._uniqueId);
         this._persistentData.put(2, new Integer(this._idHashCode));
      }
   }

   final void setThreadState(Object state) {
      this._threadState = state;
   }

   final void markUnread(boolean unread) {
      this.markUnread(unread, true);
   }

   final synchronized void markUnread(boolean unread, boolean notify) {
      if (unread != this._unread) {
         this._unread = unread;
         if (this._unread) {
            PeerApplication.unreadConversationCount().increment(this);
         } else {
            PeerApplication.unreadConversationCount().decrement();
            if (notify) {
               PeerApplication.notifications().cancelNewMessage(this);
            }

            PeerApplication.getSession().conversationRead(this);
         }

         this._persistentData.put(4, new Boolean(this._unread));
         this.commit();
         this.fireElementUpdated(this);
         if (this._existsInMessageList) {
            synchronized (FolderHierarchies.getLockObject()) {
               this._folder.fireElementUpdated(null, this, this);
            }
         }
      }

      if (this._unread && notify) {
         PeerApplication.notifications().triggerNewMessage(this);
      }
   }

   public final void cleanUp(long oldest, int maxCount) {
      int count = this._messages.size();
      if (count > maxCount) {
         for (int var6 = count - maxCount; var6 >= 0; var6--) {
            MessengerMessage current = (MessengerMessage)this._messages.elementAt(0);
            if (current.getTime() > oldest) {
               break;
            }

            this._messages.removeElementAt(0);
            this._persistedMessages.removeElementAt(0);
         }

         this.commit();
      }
   }

   final void receive(PeerContact contact, String text) {
      synchronized (FolderHierarchies.getLockObject()) {
         if (contact != null && this._existsInMessageList) {
            this._folder.removeItem(this);
            this._existsInMessageList = false;
         }

         TextMessage message = new TextMessage(contact, text);
         this.internalAddMessage(message);
         if (contact != null && !this._existsInMessageList) {
            this._folder.addItem(this);
            this._existsInMessageList = true;
         }
      }

      if (text.equals("<ding>") && Alert.isVibrateSupported() && PeerData.isVibrateOnBuzz()) {
         Alert.startVibrate(300);
      }
   }

   final void receiveObject(Object object) {
      if (object instanceof MessengerMessage) {
         synchronized (FolderHierarchies.getLockObject()) {
            if (this._existsInMessageList) {
               this._folder.removeItem(this);
               this._existsInMessageList = false;
            }

            this.internalAddMessage((MessengerMessage)object);
            if (!this._existsInMessageList) {
               this._folder.addItem(this);
               this._existsInMessageList = true;
            }
         }
      }
   }

   final TextMessage send(String text) {
      TextMessage message = new TextMessage(text, this.getFirstParticipant());
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._existsInMessageList) {
            this._folder.removeItem(this);
            this._existsInMessageList = false;
         }

         this.internalAddMessage(message);
         if (!this._existsInMessageList) {
            this._folder.addItem(this);
            this._existsInMessageList = true;
         }

         return message;
      }
   }

   final void internalAddMessage(MessengerMessage message) {
      this._messages.addElement(message);
      if (message instanceof MessengerMessageImpl) {
         this._persistedMessages.addElement(((MessengerMessageImpl)message).getPersistentData());
      } else if (message instanceof FileMessage) {
      }

      this.commit();
      this.fireElementAdded(message);
   }

   final void end() {
      this.markUnread(false);
      if (this._existsInMessageList) {
         synchronized (FolderHierarchies.getLockObject()) {
            this._folder.removeItem(this);
         }
      }

      for (int i = this._participants.size() - 1; i >= 0; i--) {
         ((PeerContact)this._participants.elementAt(i)).decrRefCount();
      }
   }

   final void lock() {
      this._uniqueId = PersistentContent.reEncode(this._uniqueId, true, true);
      this._persistentData.put(1, this._uniqueId);
      int size = this._messages.size();

      for (int index = 0; index < size; index++) {
         Object obj = this._messages.elementAt(index);
         if (!(obj instanceof TextMessage)) {
            if (obj instanceof FileMessage) {
            }
         } else {
            ((TextMessage)obj).lock();
         }
      }

      Enumeration elements = ((IntHashtable)this._persistentData.get(3)).elements();

      while (elements.hasMoreElements()) {
         IntHashtable data = (IntHashtable)elements.nextElement();
         data.put(3, PersistentContent.reEncode(data.get(3), true, true));
         data.put(2, PersistentContent.reEncode(data.get(2), true, true));
      }

      this.commit();
   }

   final void clear() {
      synchronized (FolderHierarchies.getLockObject()) {
         if (this._existsInMessageList) {
            this._folder.removeItem(this);
            this._existsInMessageList = false;
         }

         this._timeStarted = System.currentTimeMillis();
         this._persistentData.put(7, new Long(this._timeStarted));
         this._messages.removeAllElements();
         this._persistedMessages.removeAllElements();
         this.commit();
         if (!this._existsInMessageList) {
            this._folder.addItem(this);
            this._existsInMessageList = true;
         }
      }
   }

   final String getHistory() {
      this._history.setLength(0);
      this._history.append(PeerResources.getString(2023));
      this._history.append("-------------\n");
      this._history.append(PeerApplication.getSession().getDisplayName());
      this._history.append(',');
      this.addParticipantsToHistory();
      this.addMessagesToHistory();
      return this._history.toString();
   }

   final void fireElementAdded(Object element) {
      this._clm.fireElementAdded(this, element);
   }

   final void fireElementUpdated(Object element) {
      this._clm.fireElementUpdated(this, element, element);
   }

   final int getIdHash() {
      return this._idHashCode;
   }

   final boolean isUnread() {
      return this._unread;
   }

   final Vector getParticipants() {
      return this._participants;
   }

   final long getConvTimeStamp() {
      MessengerMessage message = this.getDisplayMessageModel();
      return message != null ? message.getTime() : this._timeStarted;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._clm.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._clm.removeCollectionListener(listener);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      int result = 0;
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this.getConvTimeStamp();
         result = 1;
      }

      return result;
   }

   @Override
   public final int getIndex(Object element) {
      return -1;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return -1;
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      MessengerMessage message = this.getDisplayMessageModel();
      if (painter instanceof MessageListColumnPainter) {
         this._painter = (MessageListColumnPainter)painter;
      }

      painter.setPriority(1);
      painter.drawIcon(1, this._iconCollection, -1);
      if (message != null) {
         painter.drawTime(2, message.getTime());
         painter.setEmphasis(this.isUnread());
         painter.drawText(3, this.getTitle(), false);
         String messageText = message.getText();
         painter.drawText(4, !messageText.equals("<ding>") ? messageText : TextMessageField._buzzString, false);
      } else {
         painter.drawTime(2, this._timeStarted);
         painter.setEmphasis(this.isUnread());
         painter.drawText(3, this.getTitle(), false);
         painter.drawText(4, PeerResources.format(2035, PeerResources.getString(2040)), false);
      }

      if (PeerApplication.unreadConversationCount()._newest == this) {
         PeerEntry.getInstance().clearNew();
      }
   }

   @Override
   public final Object getAt(int index) {
      return this._messages.elementAt(index);
   }

   @Override
   public final int size() {
      return this._messages.size();
   }

   private final void addParticipant(PeerContact participant) {
      if (this._participants.indexOf(participant) == -1) {
         this._participants.addElement(participant);
         IntHashtable data = new IntHashtable();
         int hashId = participant.getIdHash();
         Integer hash = new Integer(hashId);
         data.put(1, hash);
         Object id = PersistentContent.encode(participant.getId(), true, true);
         Object name = PersistentContent.encode(participant.getDisplayName(), true, true);
         data.put(3, id);
         data.put(2, name);
         IntHashtable participants = (IntHashtable)this._persistentData.get(3);
         participants.put(hashId, data);
         participant.incrRefCount();
         if (this._participants.size() == 1) {
            this._persistentData.put(8, hash);
            return;
         }

         this._conference = true;
         this._persistentData.put(5, new Boolean(this._conference));
      }
   }

   private final void removeParticipant(PeerContact participant) {
      this._participants.removeElement(participant);
      IntHashtable participants = (IntHashtable)this._persistentData.get(3);
      participants.remove(participant.getIdHash());
      participant.decrRefCount();
      if (this._participants.size() == 0) {
         this._persistentData.remove(8);
      } else {
         Object obj = this._persistentData.get(8);
         if (obj instanceof Integer && participant.getIdHash() == (Integer)obj) {
            this._persistentData.put(8, new Integer(this.getFirstParticipant().getIdHash()));
         }
      }
   }

   private final void addMessagesToHistory() {
      this._history.append(PeerResources.getString(2024));
      this._history.append("---------\n");
      int size = this._messages.size();

      for (int index = 0; index < size; index++) {
         Object obj = this._messages.elementAt(index);
         if (obj instanceof MessengerMessage) {
            MessengerMessage current = (MessengerMessage)obj;
            this._history.append(current.isSystem() ? QmResources.getString(109) : current.getSender());
            this._history.append(':');
            this._history.append(' ');
            this._history.append(current.getText());
            this._history.append('\n');
         }
      }
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof PeerConversation ? this._idHashCode == ((PeerConversation)obj).getIdHash() : super.equals(obj);
   }

   private final MessengerMessage getDisplayMessageModel() {
      MessengerMessage message = null;

      for (int i = this.size() - 1; i >= 0; i--) {
         Object obj = this.getAt(i);
         if (obj instanceof MessengerMessage && !((MessengerMessage)obj).isSystem()) {
            return (MessengerMessage)obj;
         }
      }

      return message;
   }

   PeerConversation(PeerContact participant, String uniqueId, PeerConversationsFolder folder) {
      this._persistentData = new IntHashtable();
      this._timeStarted = System.currentTimeMillis();
      this._persistentData.put(7, new Long(this._timeStarted));
      this._folder = folder;
      if (uniqueId == null && participant != null) {
         uniqueId = participant.getId();
      }

      this.setId(uniqueId);
      this._participants = new Vector(2);
      this._persistentData.put(3, new IntHashtable());
      if (participant != null) {
         this.modifyParticipant(participant, true);
      }

      this._persistentData.put(6, this._persistedMessages);
      this._persistentData.put(4, new Boolean(this._unread));
      this.commit();
   }

   private final void addParticipantsToHistory() {
      if (this._participants.size() > 0) {
         this._history.append(this.getParticipant(0).getDisplayName());
      }

      for (int i = this._participants.size() - 1; i >= 1; i--) {
         this._history.append(',');
         this._history.append(this.getParticipant(i).getDisplayName());
      }

      this._history.append('\n');
      this._history.append('\n');
   }

   PeerConversation(IntHashtable data, PeerContactListCollection contactList, PeerConversationsFolder folder) {
      this._persistentData = data;
      this._uniqueId = data.get(1);
      this._idHashCode = (Integer)data.get(2);
      this._participants = new Vector(2);
      this.populatePersistedContacts(contactList);
      this._persistedMessages = (Vector)data.get(6);
      this._folder = folder;
      this.deserializePersistedMessages(contactList);
      this._timeStarted = (Long)data.get(7);
      boolean unread = (Boolean)data.get(4);
      if (unread) {
         this.markUnread(unread, false);
      }

      Object conference = data.get(5);
      if (conference instanceof Boolean) {
         this._conference = (Boolean)conference;
      }
   }

   private final void deserializePersistedMessages(PeerContactListCollection contactList) {
      if (this._persistedMessages != null) {
         boolean added = false;
         int size = this._persistedMessages.size();

         for (int i = 0; i < size; i++) {
            Object obj = this._persistedMessages.elementAt(i);
            MessengerMessage current = !(obj instanceof IntHashtable) ? null : MessengerMessageImpl.deserialize((IntHashtable)obj, contactList);
            if (current != null) {
               this._messages.addElement(current);
               this.fireElementAdded(current);
               added = true;
            }
         }

         if (size > 0 && added) {
            synchronized (FolderHierarchies.getLockObject()) {
               this._folder.addItem(this);
               this._existsInMessageList = true;
               return;
            }
         }
      }
   }

   private final boolean onNotificationMessageFieldMatch(Object context) {
      boolean res = false;
      if (context instanceof Conversation) {
         return this.equals(context);
      }

      if (context instanceof Contact) {
         Contact firstParticipant = this.getFirstParticipant();
         if (firstParticipant != null) {
            res = firstParticipant.equals(context);
         }
      }

      return res;
   }

   private final void onNotificationMessageFieldSwitch(Object context) {
      Object cookie = context != null ? ((ContextObject)context).get(254) : null;
      Contact contact = null;
      Conversation conversation = null;
      if (cookie instanceof NotificationMessage) {
         NotificationMessage msg = (NotificationMessage)cookie;
         contact = msg.getContact();
         conversation = msg.getConversation();
      }

      NotificationMessageQueue.expireNewNotifications(contact, conversation);
      ConversationScreen cs = PeerApplication.getInstance()._conversationScreen;
      if (cs != null) {
         cs.close();
      }

      if (conversation != null) {
         PeerApplication.getInstance().openConversation((PeerConversation)conversation);
      }
   }

   private final boolean onActionProviderReply(Object context) {
      this._openConv.setParameter(context, true);
      this._openConv.run();
      return true;
   }

   private final boolean onActionProviderOpen(Object context) {
      this._openConv.setParameter(context);
      this._openConv.run();
      return true;
   }

   private final void onActionProviderMarkOpened() {
      if (this.perform(-5544992959212130441L, null)) {
         this.markUnread(!this.isUnread(), true);
      }
   }

   private final void onActionProviderMarkUnopened() {
      if (this.perform(477896226347912237L, null)) {
         this.markUnread(!this.isUnread(), false);
      }
   }

   private final void onActionProviderBulkMarkOpened() {
      if (this.isUnread()) {
         this.markUnread(false, true);
      }
   }

   private final boolean onActionProviderCheckCanMarkOpened() {
      return this.hasIncoming() && this.isUnread();
   }

   private final boolean onActionProviderCheckCanMarkUnopened() {
      return this.hasIncoming() && !this.isUnread();
   }

   private final boolean onActionProviderDelete() {
      PeerApplication.getInstance().postInvokeLaterInternal(new PeerConversation$1(this));
      return true;
   }

   private final boolean hasIncoming() {
      int index;
      for (index = this.size() - 1; index >= 0; index--) {
         Object obj = this.getAt(index);
         if (obj instanceof MessengerMessage && ((MessengerMessage)obj).isIncoming()) {
            break;
         }
      }

      return index >= 0;
   }

   private final void commit() {
      PersistentObject.commit(this._persistentData);
   }

   static final MessageListColumnPainter access$100(PeerConversation x0) {
      return x0._painter;
   }
}
