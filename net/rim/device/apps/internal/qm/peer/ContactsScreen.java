package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.qm.peer.common.ContactFindField;
import net.rim.device.apps.internal.qm.peer.common.Conversation;
import net.rim.device.apps.internal.qm.peer.common.NewContactListDialog;
import net.rim.device.apps.internal.qm.peer.common.QmMainScreen;
import net.rim.device.apps.internal.qm.peer.common.QmVerb;
import net.rim.device.apps.internal.qm.peer.common.RenameContactListDialog;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class ContactsScreen extends QmMainScreen {
   private PeerContactListCollection _contactListCollection;
   private ContactTreeField _tree;
   private ContactsScreenTitleField _title;
   private ContactFindField _finder;
   private boolean _findShowing;
   private static QmVerb _openConversationVerb = new QmVerb(332032, 52);
   private static QmVerb _startConversationVerb = new QmVerb(332032, 64);
   private static QmVerb _endConversationVerb = new QmVerb(268501005, 28);
   private static QmVerb _addContactListVerb = new QmVerb(528642, 5);
   private static QmVerb _getContactInfoVerb = new QmVerb(594178, 31);
   private static QmVerb _acceptContactVerb = new QmVerb(594185, 2);
   private static QmVerb _declineContactVerb = new QmVerb(594187, 20);
   private static QmVerb _setAlertVerb = new QmVerb(594188, 63);
   private static QmVerb _clearAlertVerb = new QmVerb(594188, 13);
   private static QmVerb _deleteContactListVerb = new QmVerb(528643, 21);
   private static QmVerb _deleteContactVerb = new QmVerb(528642, 22);
   private static QmVerb _renameContactVerb = new QmVerb(594182, 58);
   private static QmVerb _renameContactListVerb = new QmVerb(594181, 57);
   private static QmVerb _moveContactVerb = new QmVerb(594179, 45);
   private static QmVerb _userStatusVerb = new QmVerb(659724, 78);
   private static QmVerb _closeVerb = new QmVerb(268501007, 15);
   private static QmVerb _optionsVerb = new QmVerb(16986368, 54);
   private static QmVerb _userInfoVerb = new QmVerb(659726, 73);
   private static QmVerb _invitationVerb = new QmVerb(397568, 43);
   private static QmVerb _openRequestVerb = new QmVerb(594184, 53);
   private static QmVerb _collapseAllVerb = new QmVerb(463105, 86);
   private static QmVerb _removeRequestVerb = new PeerVerb(594188, 76);

   ContactsScreen(PeerContactListCollection collection) {
      super(3221225472L);
      this.setDefaultClose(false);
      this.setHelp(31945);
      this._contactListCollection = collection;
      this._title = new ContactsScreenTitleField();
      this.setTitle(this._title);
      this._tree = new ContactTreeField(this._contactListCollection);
      this.add(this._tree);
      this._tree.setFocus();
      this._finder = new ContactFindField(null, null, true);
   }

   final void onFontChanged() {
      this._title.update();
      this._tree.onFontChanged();
   }

   final void onLocaleChanged() {
      this._title.update();
      this._tree.onLocaleChanged();
   }

   final void lock() {
      this._tree.lock();
      this._title.lock();
      this._contactListCollection.lock();
   }

   final void updateUserState() {
      this._title.update();
   }

   final void invalidate(Object object) {
      this._tree.invalidate(object);
   }

   @Override
   public final void invalidate() {
      this._title.update();
      super.invalidate();
   }

   @Override
   public final void close() {
      PeerApplication app = PeerApplication.getInstance();
      if (app != null) {
         app.requestBackground();
      }
   }

   private final void selectConversation(PeerContact contact) {
      PeerConversation conversation = PeerApplication.getInstance().getConvByContact(contact);
      if (conversation != null) {
         this._tree.selectConversation(conversation);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean ret = false;
      Object selection = this._tree.getSelectedObject();
      switch (key) {
         case '\n':
            ret = this.onEnter(status, time, selection);
            break;
         case ' ':
            ret = this.onSpace(status, time);
      }

      if (!ret) {
         ret = super.keyChar(key, status, time);
      }

      if (!ret) {
         switch (key) {
            case '\b':
            case '\u007f':
               ret = this.onBackspace(key, status, time, selection);
               break;
            case '\u001b':
               this.onEscape();
               ret = true;
               break;
            default:
               if (!this._findShowing) {
                  this.setTitle(this._finder);
                  this._findShowing = true;
               }

               this._finder.keyChar(key, status, time);
               this._contactListCollection.initiateSearch(this._finder.getSearchPattern());
               ret = true;
         }
      }

      return ret;
   }

   private final boolean onEnter(int status, int time, Object selection) {
      boolean ret = false;
      if (!(selection instanceof PeerContact)) {
         if (selection instanceof PeerConversation) {
            ret = this.startOpenConversation((PeerConversation)selection, null);
         } else if (selection instanceof PeerRequest) {
            ret = this.handleNewRequest((PeerRequest)selection, true);
         } else {
            ret = this.onSpace(status, time);
         }
      } else {
         PeerContact contact = (PeerContact)selection;
         if (!contact.isPending()) {
            ret = this.startOpenConversation(null, contact);
         }
      }

      return ret;
   }

   private final boolean onSpace(int status, int time) {
      return this.hasSearchPattern() ? this._finder.keyChar(' ', status, time) : super.keyChar(' ', status, time);
   }

   private final boolean hasSearchPattern() {
      return this._findShowing && this._finder.hasSearchPattern();
   }

   private final void onEscape() {
      if (this.hasSearchPattern()) {
         this._finder.resetSearch();
         this._finder.setText("");
         this._contactListCollection.initiateSearch(this._finder.getSearchPattern());
         this.setTitle(this._title);
         this._findShowing = false;
      } else {
         this.close();
      }
   }

   private final boolean onBackspace(char key, int status, int time, Object selection) {
      boolean ret = false;
      if (this.hasSearchPattern()) {
         this._finder.keyChar(key, status, time);
         this._contactListCollection.initiateSearch(this._finder.getSearchPattern());
         if (!this._finder.hasSearchPattern()) {
            this.setTitle(this._title);
            this._findShowing = false;
         }

         ret = true;
      } else {
         ret = this.deleteSelection(selection);
      }

      return ret;
   }

   private final boolean deleteSelection(Object selection) {
      boolean ret = false;
      if (selection instanceof PeerContact) {
         this.deleteContact((PeerContact)selection);
         return true;
      }

      if (selection instanceof PeerConversation) {
         PeerContact participant = ((PeerConversation)selection).getFirstParticipant();
         String prompt = participant != null ? PeerResources.format(2050, participant.getDisplayName()) : PeerResources.getString(2052);
         if (Dialog.ask(4, prompt, -1) == 0) {
            this.endConversation((PeerConversation)selection, null);
         }

         ret = true;
      }

      return ret;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      PeerApplication.getInstance().updateUserPresence();
      return super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      PeerApplication.getInstance().updateUserPresence();
      if ((status & 1) != 0) {
         int rowHeight = this.getFont().getHeight();
         int pageHeight = this.getVisibleHeight() - this._title.getHeight();
         amount *= pageHeight / rowHeight - 1;
      }

      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean handleSendKey() {
      boolean ret = super.handleSendKey();
      if (!ret) {
         PeerContact contact = this.getContact(this._tree.getSelectedObject());
         if (contact != null) {
            Object item = Utils.getAddressCard(contact.getOriginalContactInfo());
            if (item != null) {
               ContextObject resultContext = new ContextObject(73);
               resultContext.put(252, item);
               ret = ControllerUtilities.invokeSendKeyVerb(item, resultContext);
            }
         }
      }

      return ret;
   }

   private final PeerContact getContact(Object selection) {
      PeerContact contact = null;
      if (!(selection instanceof PeerContact)) {
         if (selection instanceof Conversation && ((PeerConversation)selection).getParticipants().size() == 1) {
            contact = ((PeerConversation)selection).getFirstParticipant();
         }

         return contact;
      } else {
         return (PeerContact)selection;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         Object selection = this._tree.getSelectedObject();
         if (!(selection instanceof PeerContact)) {
            if (selection instanceof PeerConversation) {
               return this.startOpenConversation((PeerConversation)selection, null);
            }
         } else {
            PeerContact contact = (PeerContact)selection;
            if (!contact.isPending()) {
               return this.startOpenConversation(PeerApplication.getInstance().getConvByContact(contact), contact);
            }
         }
      }

      return this._tree.invokeAction(action);
   }

   private final PeerConversation getPeerConversation(Object selection) {
      return !(selection instanceof PeerConversation) ? null : (PeerConversation)selection;
   }

   private final PeerRequest getPeerRequest(Object selection) {
      return !(selection instanceof PeerRequest) ? null : (PeerRequest)selection;
   }

   private final PeerContact getPeerContact(PeerConversation conversation, Object selection) {
      PeerContact contact = null;
      if (conversation != null) {
         if (conversation.getParticipants().size() == 1) {
            return conversation.getFirstParticipant();
         }
      } else if (selection instanceof PeerContact) {
         contact = (PeerContact)selection;
      }

      return contact;
   }

   private final PeerContactList getPeerContactList(Object selection) {
      PeerContactList list = null;
      if (selection instanceof PeerContact) {
         return this._tree.getSelectedGroup();
      }

      if (selection instanceof PeerContactList) {
         list = (PeerContactList)selection;
      }

      return list;
   }

   private final void onMenuInstanceDefault(SystemEnabledMenu menu) {
      if (this._tree.isExpanded()) {
         menu.add(_collapseAllVerb);
      }

      menu.add(_closeVerb);
      menu.add(_optionsVerb);
      menu.add(_userInfoVerb);
      menu.add(_userStatusVerb);
      menu.add(_invitationVerb);
      menu.setDefault(_invitationVerb);
      menu.add(_addContactListVerb);
   }

   private final void onMenuPeerRequest(PeerRequest request, SystemEnabledMenu menu) {
      if (request != null) {
         menu.add(_openRequestVerb);
         menu.setDefault(_openRequestVerb);
         menu.add(_removeRequestVerb);
         if (request instanceof NewContactRequest) {
            menu.add(_acceptContactVerb);
            menu.add(_declineContactVerb);
         }
      }
   }

   private final void onMenuPeerConversation(PeerConversation conversation, SystemEnabledMenu menu) {
      if (conversation != null) {
         menu.add(_openConversationVerb);
         menu.setDefault(_openConversationVerb);
         menu.add(_endConversationVerb);
      }
   }

   private final void onMenuPeerContact(PeerContact contact, PeerConversation conversation, int instance, SystemEnabledMenu menu) {
      if (instance == 0) {
         menu.add(_getContactInfoVerb);
      }

      menu.add(_deleteContactVerb);
      if (!contact.isPending()) {
         if (instance == 0) {
            menu.add(_renameContactVerb);
            if (conversation == null && this._contactListCollection.getContactListCount() > 1 && contact.isAvailable()) {
               menu.add(_moveContactVerb);
            }
         }

         Conversation associatedConversation = PeerApplication.getInstance().getConvByContact(contact);
         if (associatedConversation == null) {
            if (contact.isAuthorized()) {
               menu.add(_startConversationVerb);
               menu.setDefault(_startConversationVerb);
            }
         } else if (conversation == null) {
            menu.add(_openConversationVerb);
            menu.setDefault(_openConversationVerb);
            menu.add(_endConversationVerb);
         }

         if (instance == 0 && contact.isAlertable()) {
            menu.add(PeerApplication.alerts().isSet(contact) ? _clearAlertVerb : _setAlertVerb);
         }
      }
   }

   private final void onMenuPeerContactList(PeerContactList list, SystemEnabledMenu menu) {
      menu.add(_renameContactListVerb);
      if (this._contactListCollection.getContactListCount() > 1 && list.getContactsCount() == 0) {
         menu.add(_deleteContactListVerb);
      }
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      QmVerb._screen = this;
      Object selection = this._tree.getSelectedObject();
      PeerConversation conversation = this.getPeerConversation(selection);
      PeerContact contact = this.getPeerContact(conversation, selection);
      PeerContactList contactList = this.getPeerContactList(selection);
      PeerRequest request = this.getPeerRequest(selection);
      if (instance == 0) {
         this.onMenuInstanceDefault(menu);
      } else if (request == null) {
         menu.add(_invitationVerb);
         menu.setDefault(_invitationVerb);
         menu.add(_addContactListVerb);
      }

      this.onMenuPeerConversation(conversation, menu);
      if (contact != null) {
         this.onMenuPeerContact(contact, conversation, instance, menu);
      } else if (contactList != null && instance == 0) {
         this.onMenuPeerContactList(contactList, menu);
      }

      this.onMenuPeerRequest(request, menu);
      this.addRepositoryVerbs(menu);
      this.addContextVerbs(menu, contact);
   }

   private final void addRepositoryVerbs(SystemEnabledMenu menu) {
      VerbRepository repository = VerbRepository.getVerbRepository(-1390867101630436713L);
      menu.add(repository.getVerbs(null));
   }

   private final void addContextVerbs(SystemEnabledMenu menu, PeerContact contact) {
      if (contact != null) {
         Verb[] verbs = Utils.getVerbs(contact.getOriginalContactInfo());
         if (verbs != null && verbs.length > 0) {
            menu.add(verbs);
         }
      }
   }

   @Override
   public final void invokeVerbSpecial(int id) {
      Object selection = this._tree.getSelectedObject();
      PeerRequest request = !(selection instanceof PeerRequest) ? null : (PeerRequest)selection;
      switch (id) {
         case 76:
            PeerApplication.getController().getContactListCollection().removeNewRequest(request);
      }
   }

   private final void deleteContact(PeerContact contact) {
      if (Dialog.ask(2, PeerResources.format(25, contact.getDisplayName()), -1) == 3) {
         PeerApplication.getInstance().deleteContact(contact, true);
         this._tree.invalidate(contact);
      }
   }

   private final boolean startOpenConversation(PeerConversation conversation, PeerContact contact) {
      boolean ret = false;
      if (conversation != null) {
         PeerApplication.getInstance().openConversation(conversation);
         return true;
      }

      if (contact != null) {
         PeerApplication.getInstance().openConversation(contact);
         this.selectConversation(contact);
         ret = true;
      }

      return ret;
   }

   private final void endConversation(PeerConversation conversation, PeerContact contact) {
      if (conversation != null) {
         PeerApplication.getInstance();
         PeerApplication.endConversation(conversation);
      } else {
         if (contact != null) {
            PeerApplication.getInstance();
            PeerApplication.endConversation(contact);
         }
      }
   }

   @Override
   public final void invokeVerb(int id) {
      Object selection = this._tree.getSelectedObject();
      PeerConversation conversation = this.getPeerConversation(selection);
      PeerContact contact = this.getPeerContact(conversation, selection);
      PeerContactList contactList = this.getPeerContactList(selection);
      PeerRequest request = this.getPeerRequest(selection);
      switch (id) {
         case 2:
            this.handleNewRequest(request, false);
            return;
         case 5:
            this.onAddContactListVerb();
            return;
         case 13:
            this.onClearAlertVerb(contact);
            return;
         case 15:
            this.close();
            return;
         case 20:
            this.onDeclineContactVerb(request);
            return;
         case 21:
            this.onDeleteContactListVerb(contactList);
            return;
         case 22:
            if (contact != null) {
               this.deleteContact(contact);
               return;
            }
            break;
         case 28:
            this.endConversation(conversation, contact);
            return;
         case 31:
            if (contact != null) {
               this.getContactInfo(contact);
               return;
            }
            break;
         case 43:
            EmailInvitationComposeVerb.doInvite(null, contactList);
            return;
         case 45:
            this.onMoveContactVerb(contact, contactList);
            return;
         case 52:
         case 64:
            this.startOpenConversation(conversation, contact);
            return;
         case 53:
            this.handleNewRequest(request, true);
            return;
         case 54:
            if (PeerData.showOptions()) {
               this._tree.populate(this._contactListCollection);
               return;
            }
            break;
         case 57:
            this.onRenameContactListVerb(contactList);
            return;
         case 58:
            this.onRenameContactVerb(contact);
            return;
         case 63:
            this.onSetAlertVerb(contact);
            return;
         case 73:
            this.onUserInfoVerb();
            return;
         case 78:
            this.onUserStatusVerb();
            return;
         case 86:
            this._tree.collapseAll();
      }
   }

   private final void onAddContactListVerb() {
      NewContactListDialog nclDialog = new NewContactListDialog();
      if (nclDialog.doModal()) {
         this._contactListCollection.addContactList(nclDialog.getName());
      }
   }

   private final void onDeclineContactVerb(PeerRequest request) {
      if (request instanceof NewContactRequest) {
         ((NewContactRequest)request).decline();
         PeerApplication.getInstance().getContactListCollection().removeNewRequest(request);
      }
   }

   private final void onSetAlertVerb(PeerContact contact) {
      if (contact != null) {
         PeerApplication.alerts().set(contact);
         Dialog.inform(QmResources.format(62, contact.getDisplayName()));
         this._tree.invalidate(contact);
      }
   }

   private final void onClearAlertVerb(PeerContact contact) {
      if (contact != null) {
         PeerApplication.alerts().clear(contact);
         this._tree.invalidate(contact);
      }
   }

   private final void onDeleteContactListVerb(PeerContactList list) {
      if (list != null) {
         String prompt = PeerResources.format(25, list.getDisplayName());
         if (Dialog.ask(2, prompt, -1) == 3) {
            this._contactListCollection.removeContactList(list);
         }
      }
   }

   private final void onRenameContactVerb(PeerContact contact) {
      if (contact != null) {
         RenameContactDialog rcDialog = new RenameContactDialog(contact);
         if (rcDialog.doModal()) {
            this._contactListCollection.renameContact(contact, rcDialog.getName());
         }
      }
   }

   private final void onRenameContactListVerb(PeerContactList list) {
      if (list != null) {
         RenameContactListDialog rclDialog = new RenameContactListDialog(list.getDisplayName());
         if (rclDialog.doModal()) {
            this._contactListCollection.renameContactList(list, rclDialog.getName());
         }
      }
   }

   private final void onMoveContactVerb(PeerContact contact, PeerContactList list) {
      PickContactListDialog pclDialog = new PickContactListDialog(this._contactListCollection, list);
      if (pclDialog.doModal()) {
         this._contactListCollection.moveContact(contact, list, pclDialog.getSelection());
      }
   }

   private final void onUserStatusVerb() {
      UserStatusDialog usDialog = new UserStatusDialog();
      if (usDialog.doModal()) {
         PeerApplication.getInstance().setUserPresenceStatus(usDialog.isAvailable() ? 16384 : 4, usDialog.getMessage());
      }
   }

   private final void onUserInfoVerb() {
      String oldDisplayName = PeerApplication.getSession().getDisplayName();
      DisplayNameDialog dialog = new DisplayNameDialog(PeerResources.getString(897), null, oldDisplayName);
      String displayName = dialog.doModal() ? dialog.getName() : null;
      if (displayName != null) {
         PeerApplication.getSession().setDisplayName(displayName);
         this._title.update();
         PeerAuditManager.getInstance().logOutgoingDisplayName(oldDisplayName, displayName);
      }
   }

   private final void getContactInfo(PeerContact contact) {
      UserInfoScreen screen = new UserInfoScreen(contact.getUserInfoFields());
      screen.go();
   }

   private final boolean handleNewRequest(PeerRequest request, boolean open) {
      if (request != null) {
         if (open) {
            this.openNewRequest(request);
            return true;
         }

         request.accept();
         PeerApplication.getController().getContactListCollection().removeNewRequest(request);
      }

      return true;
   }

   private final void openNewRequest(PeerRequest request) {
      if (request instanceof WrongPasscodeNotification) {
         Dialog.alert(request.getBody());
         PeerApplication.getController().getContactListCollection().removeNewRequest(request);
      } else {
         NewContactRequestDialog ncrd = new NewContactRequestDialog(request);
         if (ncrd.doModal()) {
            PeerApplication.getController().getContactListCollection().removeNewRequest(request);
         } else {
            request.markRead(true);
            PeerApplication.getController().getContactListCollection().expireNewRequest(request);
         }
      }
   }
}
