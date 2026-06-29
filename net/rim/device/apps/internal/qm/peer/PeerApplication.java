package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import javax.microedition.io.InputConnection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ribbon.RadioOffWarningManager;
import net.rim.device.apps.api.ribbon.RadioOffWarningManager$Listener;
import net.rim.device.apps.internal.qm.peer.common.Alerts;
import net.rim.device.apps.internal.qm.peer.common.NewNotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.NotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.QmComposeVerbCombiner;
import net.rim.device.apps.internal.qm.peer.common.QmRenderScreen;
import net.rim.device.apps.internal.qm.peer.common.QueuesManager;
import net.rim.device.apps.internal.qm.peer.common.SystemNotificationMessage;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.device.internal.io.file.FileUtilities;

final class PeerApplication
   extends UiApplication
   implements GlobalEventListener,
   DialogClosedListener,
   SystemListener,
   RadioStatusListener,
   RadioOffWarningManager$Listener,
   CollectionListener {
   private PeerSession _session;
   private Conversations _conversations;
   private Notifications _notifications;
   private UnreadConversationCount _unreadConversationCount;
   private Alerts _alerts = new Alerts();
   private BlackBerryMessengerImpl _blackberryMessengerApi;
   private PeerConversation _unholsterConversation;
   private ContextObject _unholsterContext;
   private PeerConversationsFolder _folder;
   private ContactsScreen _contactsScreen;
   private QmRenderScreen _renderScreen;
   private IntHashtable _conversationScreens = (IntHashtable)(new Object());
   private UiApplication _uiApplication;
   private DisplayNameDialog _displayNameDialog;
   private boolean _registered;
   private boolean _deviceIsLocked;
   private PeerApplication$ContactAvailableDialog[] _alertScreens = new PeerApplication$ContactAvailableDialog[0];
   private PeerApplication$LmmRunnable _lmmRunnable = new PeerApplication$LmmRunnable(this);
   private QueuesManager _queuesManager = new QueuesManager();
   private Runnable _userStateChangeRunnable = new PeerApplication$1(this);
   ConversationScreen _conversationScreen;
   PeerPresenceManager _presenceManager;
   PeerContactListCollection _contactListCollection;
   MessageFieldLookup _messageFieldLookup;
   private static final boolean DEBUG_LMM;
   private static final int IT_POLICY_GROUP;
   private static final int DISABLE_BLACKBERRY_MESSENGER;
   static final long GUID;
   private static final int MAX_FILE_SIZE;
   static final int USER_ALERT_SYSTEM_MESSAGE;
   static final int USER_ALERT_IMMEDIATE;
   private static PeerApplication _instance = (PeerApplication)ApplicationRegistry.getApplicationRegistry().get(-9029900896793868512L);

   public final void postInvokeLaterInternal(Runnable runnable) {
      this.postInvokeLaterInternal(this, runnable);
   }

   public final void postInvokeLaterInternal(UiApplication app, Runnable runnable) {
      this._queuesManager.postInvokeLater(app, runnable);
   }

   public final void scheduleApplication(UiApplication application) {
      this._queuesManager.schedule(application);
   }

   final PeerContactListCollection getContactListCollection() {
      return this._contactListCollection;
   }

   final BlackBerryMessengerImpl getBlackBerryMessengerApi() {
      return _instance._blackberryMessengerApi;
   }

   final PeerConversation getConvByUniqueId(String uniqueId) {
      return uniqueId != null ? this._conversations.getByUniqueId(uniqueId.hashCode()) : null;
   }

   final PeerConversation getConvByContactId(String contactId) {
      return contactId != null ? this._conversations.getByContactId(contactId.hashCode()) : null;
   }

   final PeerConversation getConvByContact(PeerContact contact) {
      return contact != null ? this._conversations.getByContact(contact) : null;
   }

   final String getCustomStatusMessage() {
      return this._presenceManager.getCustomStatusMessage();
   }

   final boolean isUserAvailable() {
      return this._presenceManager.isAvailable();
   }

   final void notificationQueueChanged(NotificationMessage message) {
      if (this._conversationScreen != null) {
         this._conversationScreen.updateNotification(message);
      }
   }

   final void showContacts() {
      this._contactsScreen = new ContactsScreen(this._contactListCollection);
      this.invokeLater(new PeerApplication$3(this));
   }

   final boolean showWelcome() {
      String displayName = this._session.getDisplayName();
      if (displayName != null) {
         return true;
      }

      if (this._displayNameDialog == null) {
         this._displayNameDialog = new DisplayNameDialog(QmResources.getString(81), QmResources.getString(25), Owner.getOwnerName());
      } else if (this._displayNameDialog.isDisplayed()) {
         return false;
      }

      if (this._displayNameDialog.doModal()) {
         displayName = this._displayNameDialog.getName();
      }

      if (displayName != null) {
         Dialog.inform(QmResources.getString(23));
         this._session.setDisplayName(displayName);
         PeerAuditManager.getInstance().logOutgoingDisplayName("", displayName);
         if (this._contactListCollection.getContactsCount() == 0) {
            String welcome = QmResources.getString(49);
            if (welcome != null && welcome.length() > 0) {
               Dialog.alert(welcome);
            }
         }

         return true;
      } else {
         this._displayNameDialog = null;
         this.requestBackground();
         return false;
      }
   }

   final void checkPassword() {
      String random = PeerData.getPasswordKey();
      if (random == null || random.equals("1234")) {
         random = Utils.byteArrayToHex(RandomSource.getBytes(16));
         PeerData.savePasswordKey(random);
         getSession().broadcastNewKey();
      }
   }

   final void openConversation(PeerContact contact) {
      PeerConversation conversation = this.getConvByContact(contact);
      if (conversation == null) {
         conversation = this._conversations.start(contact, null);
      }

      this.showConversation(conversation, null);
   }

   final void openConversation(PeerConversation conversation) {
      this.showConversation(conversation, null);
   }

   final void showConversation(PeerConversation conversation, ContextObject context) {
      NotificationMessageQueue.expireNewNotifications(conversation);
      ConversationScreen screen = (ConversationScreen)this._conversationScreens.get(conversation.getIdHash());
      if (screen != null && screen.isDisplayed()) {
         if (screen.getApplication() == Application.getApplication()) {
            return;
         }

         screen.dismiss();
      }

      if (screen == null) {
         getInstance()._conversationScreen = new ConversationScreen(conversation, context);
         this._conversationScreens.put(conversation.getIdHash(), getInstance()._conversationScreen);
      } else {
         getInstance()._conversationScreen = screen;
      }

      getInstance()._uiApplication = UiApplication.getUiApplication();
      getInstance()._conversationScreen.doModal(context);
      if (conversation.size() == 0) {
         endConversation(conversation);
      }
   }

   public final void displayRenderScreen(InputConnection connection) {
      this._renderScreen = new QmRenderScreen(connection);
      this._renderScreen.doModal();
   }

   final void contactAvailableDialog(PeerContact contact) {
      String message = QmResources.format(16, contact.getDisplayName());
      PeerApplication$ContactAvailableDialog dialog = new PeerApplication$ContactAvailableDialog(contact, message);
      dialog.setDialogClosedListener(this);
      Arrays.add(this._alertScreens, dialog);
      dialog.queue();
   }

   final void internalDismissContactAvailableDialog(PeerContact contact) {
      for (int i = this._alertScreens.length - 1; i >= 0; i--) {
         PeerApplication$ContactAvailableDialog dialog = this._alertScreens[i];
         if (dialog._contact.equals(contact)) {
            Arrays.remove(this._alertScreens, dialog);
            _instance.popScreen(dialog);
         }
      }
   }

   public final boolean freeStaleObject(int priority) {
      this._lmmRunnable.cleanUp(priority);
      return false;
   }

   final void openConversationTriggeredByUnHolster(PeerConversation conversation, ContextObject context) {
      if (this._conversationScreen != null && this._conversationScreen.isDisplayed()) {
         if (this._conversationScreen.getConversation() == conversation && context == null) {
            this.requestForeground();
         }

         this._conversationScreen.dismiss();
      }

      if (context == null) {
         this.requestForeground();
      }

      if (this._deviceIsLocked) {
         this._unholsterConversation = conversation;
         this._unholsterContext = context;
      } else {
         this.showConversation(conversation, context);
      }
   }

   final void openNewRequestTriggeredByUnHolster(PeerRequest request) {
      if (request != null) {
         this.requestForeground();
         this._contactListCollection.expireNewRequests(false, false);
      }
   }

   final void setUserPresenceStatus(int status, String message) {
      this._presenceManager.setPresenceStatus(status, message);
      PeerAuditManager.getInstance().logOutgoingStatus(this._presenceManager.toString(), message);
   }

   final void updateUserPresence() {
      this._presenceManager.clockUpdated();
   }

   final void dispatchUserStatus(PeerContact contactToNotify) {
      this._presenceManager.dispatchStatus(contactToNotify);
   }

   final void send(PeerConversation conversation, String message) {
      TextMessage model = conversation.send(message);
      this._session.send(conversation, message, model.hashCode());
      PeerAuditManager.getInstance().logOutgoingMessage(conversation.getParticipants(), message);
   }

   final void sendFile(PeerContact contact, String contentType, String filename, byte[] data) {
      if (contact != null && data != null && data.length > 0) {
         if (data.length > 15360 && !contentType.startsWith("image")) {
            Dialog.alert(PeerResources.getString(2015));
            return;
         }

         this._session.sendFile(contact, contentType, filename, data);
         getInstance().sendDisplayNoTransport(contact, PeerResources.format(2000, FileUtilities.getDisplayName(filename)));
         PeerAuditManager.getInstance().logOutgoingObject(contact, filename, data);
      }
   }

   final void sendDisplayNoTransport(PeerContact contact, String message) {
      PeerConversation conversation = this.getConvByContact(contact);
      if (conversation == null) {
         conversation = getInstance()._conversations.start(contact, null);
      }

      conversation.receive(null, message);
   }

   public final void finalizeInvitation(PeerContact contact, String cookie, String origContactInfo) {
      this._session.acceptInvitation(contact, cookie, origContactInfo);
      getInstance().dispatchUserStatus(contact);
   }

   public final void deleteContact(PeerContact contact, boolean notify) {
      endConversation(contact);
      this._contactListCollection.removeContact(contact);
      if (this._alerts.isSet(contact)) {
         this._alerts.clear(contact);
      }

      if (!contact.isPending() && notify) {
         this._session.deleteContact(contact);
      }
   }

   public final void userStateChange() {
      this.invokeLater(this._userStateChangeRunnable);
   }

   public final PeerConversation newMessage(String conversationId, String contactId, String message) {
      PeerConversation conversation = this.getConvByUniqueId(conversationId);
      PeerContact contact = this._contactListCollection.findContact(contactId);
      if (contact != null || contactId == null) {
         if (conversation == null) {
            conversation = this._conversations.start(contact, conversationId);
         }

         conversation.receive(contact, message);
         if (contact != null) {
            conversation.markUnread(true);
            if (this._contactsScreen != null) {
               this._contactsScreen.invalidate(contact);
            }
         }

         NotificationMessageQueue.postMessage(
            contact != null ? new NewNotificationMessage(conversation, contact, message) : new SystemNotificationMessage(conversation, message)
         );
      }

      return conversation;
   }

   public final void newObject(PeerContact contact, Object object) {
      PeerConversation conversation = this.getConvByContact(contact);
      if (conversation == null) {
         conversation = this._conversations.start(contact, null);
      }

      conversation.receiveObject(object);
      if (contact != null) {
         conversation.markUnread(true);
         if (this._contactsScreen != null) {
            this._contactsScreen.invalidate(contact);
         }

         contact.setPresenceStatus(16384);
      }

      this._notifications.triggerNewMessage(conversation);
      if (object instanceof FileMessage) {
         FileMessage file = (FileMessage)object;
         PeerAuditManager.getInstance().logIncomingObject(contact, file);
      }
   }

   public final void modifyConversationParticipant(String conversationId, String contactId, boolean add) {
      PeerConversation conversation = conversationId != null ? this.getConvByUniqueId(conversationId) : null;
      if (conversation == null && contactId != null) {
         conversation = this.getConvByContactId(contactId);
      }

      if (conversation != null) {
         PeerContact contact = this._contactListCollection.findAndCreateContact(contactId);
         if (contact != null) {
            this._conversations.modifyParticipant(conversation, contact, add);
         }
      }
   }

   public final int alertUser(int type, String message) {
      UiApplication app = this._uiApplication != null ? this._uiApplication : getInstance();
      app.invokeLater(new PeerApplication$4(this, type, message));
      return 0;
   }

   final PeerContact[] getInvitationChoices(PeerConversation conversation) {
      return this._contactListCollection.getInvitationChoices(conversation, this._session.getMyContactId());
   }

   final void deviceLocked() {
      if (this._conversations != null) {
         this._conversations.lock();
      }

      if (this._contactsScreen != null) {
         this._contactsScreen.lock();
      }

      if (this._conversationScreen != null && this._conversationScreen.isDisplayed()) {
         UiApplication app = this._uiApplication != null ? this._uiApplication : getInstance();
         synchronized (app.getAppEventLock()) {
            this._conversationScreen.close();
         }
      }

      Enumeration enumeration = this._conversationScreens.elements();

      while (enumeration.hasMoreElements()) {
         ((ConversationScreen)enumeration.nextElement()).lock();
      }

      NotificationMessageQueue.getInstance().lock();
      if (this._session != null) {
         this._session.lock();
      }

      PeerData.lock();
      PeerAuditManager.getInstance().deviceLocked();
   }

   final void deviceUnlocked() {
      PeerAuditManager.getInstance().deviceUnlocked();
      this._contactListCollection.unlock();
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         this.onFontChanged();
      } else if (guid == -7464003439710973532L) {
         this.onFontChanged();
         this.onLocaleChanged();
      } else if (guid == 2573494863350550132L) {
         PeerEntry.getInstance().onThemeChanged();
      } else if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this.onItPolicyChanged();
      } else if (guid == 6345609069135580235L) {
         this.onSystemUnlocked();
      } else {
         if (guid == -7131874474196788121L) {
            this._deviceIsLocked = true;
         }
      }
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog instanceof PeerApplication$ContactAvailableDialog) {
         PeerApplication$ContactAvailableDialog contactAvailableDialog = (PeerApplication$ContactAvailableDialog)dialog;
         this._alerts.clear(contactAvailableDialog._contact);
         Arrays.remove(this._alertScreens, dialog);
         this._notifications.cancelContactAvailable();
         if (choice == 4) {
            this.requestForeground();
            this.openConversation(contactAvailableDialog._contact);
         }
      }
   }

   @Override
   public final void powerOff() {
      this.autoLogout();
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.networkServiceChange(networkId, service);
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
      this.autoLogout();
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
   }

   @Override
   public final void onEvent(int event) {
      switch (event) {
      }
   }

   @Override
   public final void reset(Collection collection) {
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (collection == null && newElement instanceof Object) {
         int size = this._conversations.size();

         for (int index = 0; index < size; index++) {
            this._notifications.cancelNewMessage((PeerConversation)this._conversations.getAt(index));
         }

         this._contactListCollection.expireNewRequests(false, true);
         this._conversations.clear();
         boolean registered = this._folder.isRegistered();
         if (PeerData.isMessagelistIntegration()) {
            if (!registered) {
               this._folder.register();
            }
         } else if (registered) {
            this._folder.deregister();
         }

         this._contactListCollection = new PeerContactListCollection();
         this._contactListCollection.reload(5587026183716775221L);
         this.updatePin();
         this.showContacts();
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   private final void onLocaleChanged() {
      if (this._displayNameDialog != null && this._displayNameDialog.isDisplayed()) {
         this._displayNameDialog.close();
         this._displayNameDialog = null;
      }

      Enumeration screens = this._conversationScreens.elements();

      while (screens.hasMoreElements()) {
         ((ConversationScreen)screens.nextElement()).onLocaleChanged();
      }

      if (this._contactsScreen != null) {
         this._contactsScreen.onLocaleChanged();
      }
   }

   private final void onItPolicyChanged() {
      if (isDisabled()) {
         this.invokeLater(new PeerApplication$5(this));
      }
   }

   private final void onSystemUnlocked() {
      this._deviceIsLocked = false;
      if (this._unholsterConversation != null) {
         this.openConversationTriggeredByUnHolster(this._unholsterConversation, this._unholsterContext);
         this._unholsterConversation = null;
         this._unholsterContext = null;
      }
   }

   static final void endConversation(PeerContact contact) {
      Conversations conversations = conversations();
      int size = conversations.size();

      for (int i = size - 1; i >= 0; i--) {
         PeerConversation conversation = (PeerConversation)conversations.getAt(i);
         if (conversation.isParticipant(contact)) {
            endConversation(conversation);
         }
      }
   }

   @Override
   public final void deactivate() {
      PeerEntry.getInstance().hold(false);
   }

   private final void autoLogout() {
   }

   static final UnreadConversationCount unreadConversationCount() {
      return _instance._unreadConversationCount;
   }

   PeerApplication() {
      _instance = this;
      ApplicationRegistry.getApplicationRegistry().replace(-9029900896793868512L, this);
      this.addGlobalEventListener(this);
      this.addSystemListener(this);
      this.addRadioListener(this);
      this._presenceManager = new PeerPresenceManager(PeerData.getUserStatus());
      this._unreadConversationCount = new UnreadConversationCount();
      this._contactListCollection = new PeerContactListCollection();
      this._contactListCollection.reload(5587026183716775221L);
      this._folder = PeerConversationsFolder.getInstance();
      this._conversations = new Conversations(this._contactListCollection, this._folder);
      this._notifications = new Notifications();
      this._session = new PeerSession(this);
      this.updatePin();
      this._messageFieldLookup = new MessageFieldLookup();
      PeerData.updateSyncItem(this);
      RadioOffWarningManager.getInstance().addListener(this);
      this._blackberryMessengerApi = new BlackBerryMessengerImpl(this);
      QmComposeVerbCombiner.registerOnceOnSystemStartUp();
      PeerComposeVerb.registerOnceOnSystemStartUp();
      SendFileVerb.register();
      EmailListener.register();
      this.showContacts();
   }

   static final PeerApplication getInstance() {
      if (_instance != null && !_instance.isAlive()) {
         _instance = null;
      }

      if (_instance == null) {
         _instance = (PeerApplication)ApplicationRegistry.getApplicationRegistry().get(-9029900896793868512L);
      }

      return _instance;
   }

   static final PeerSession getSession() {
      return _instance._session;
   }

   static final PeerController getController() {
      return _instance._session.getController();
   }

   static final Conversations conversations() {
      return getInstance()._conversations;
   }

   static final void dismissConversationScreen() {
      if (_instance != null && _instance._conversationScreen != null && _instance._conversationScreen.isDisplayed()) {
         _instance._conversationScreen.dismiss();
      }
   }

   public static final void main(String[] args) {
      if (!isDisabled()) {
         PeerEntry entry = PeerEntry.getInstance();
         entry.storePid();
         LowMemoryManager.addLowMemoryListener(entry);
         new PeerApplication().enterEventDispatcher();
      }
   }

   static final boolean isDisabled() {
      return ITPolicy.getBoolean(149, 15, false);
   }

   static final void dismissContactAvailableDialog(PeerContact contact) {
      _instance.internalDismissContactAvailableDialog(contact);
   }

   @Override
   public final void activate() {
      PeerEntry.getInstance().hold(true);
      if (!this._registered) {
         this.checkPassword();
         this.invokeLater(new PeerApplication$2(this));
      }
   }

   static final void setConversationId(PeerConversation conversation, String newId) {
      PeerApplication app = getInstance();
      ConversationScreen screen = (ConversationScreen)app._conversationScreens.get(conversation.getIdHash());
      if (screen != null) {
         app._conversationScreens.remove(conversation.getIdHash());
      }

      conversations().setConversationId(conversation, newId);
      if (screen != null) {
         app._conversationScreens.put(conversation.getIdHash(), screen);
      }
   }

   static final Alerts alerts() {
      return _instance._alerts;
   }

   private final void updatePin() {
      String oldPin = PeerData.getPin();
      String newPin = this._session.getMyContactId();
      if (oldPin == null) {
         PeerData.savePin(newPin);
      } else {
         if (!oldPin.equals(newPin)) {
            PeerData.savePin(newPin);
            this._session.sendInfoBlob(null, oldPin);
         }
      }
   }

   static final Notifications notifications() {
      return _instance._notifications;
   }

   static final void endConversation(PeerConversation conversation) {
      ConversationScreen screen = (ConversationScreen)getInstance()._conversationScreens.get(conversation.getIdHash());
      if (screen != null) {
         screen.dismiss();
         getInstance()._conversationScreens.remove(conversation.getIdHash());
      }

      NotificationMessageQueue.expireNewNotifications(conversation.getFirstParticipant(), conversation);
      conversations().end(conversation);
      getSession().endConversation(conversation);
   }

   static final boolean isDeviceLocked() {
      return PeerEntry.getInstance().isDeviceLocked();
   }

   private final void onFontChanged() {
      PeerResources.onFontChanged();
      Enumeration screens = this._conversationScreens.elements();

      while (screens.hasMoreElements()) {
         ((ConversationScreen)screens.nextElement()).onFontChanged();
      }

      if (this._contactsScreen != null) {
         this._contactsScreen.onFontChanged();
      }
   }

   static final ContactsScreen access$000(PeerApplication x0) {
      return x0._contactsScreen;
   }

   static final boolean access$102(PeerApplication x0, boolean x1) {
      return x0._registered = x1;
   }

   static final PeerApplication access$200() {
      return _instance;
   }

   static final Conversations access$300(PeerApplication x0) {
      return x0._conversations;
   }
}
