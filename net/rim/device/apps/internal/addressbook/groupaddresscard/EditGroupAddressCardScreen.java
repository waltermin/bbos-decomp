package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.i18n.CommonResource;

final class EditGroupAddressCardScreen extends GroupAddressCardScreen implements Confirmation {
   private final int[] resourceId = new int[]{
      807, 808, 809, 804, -804651007, 51, -804651005, 706, 1005, 810, 712179968, 712179968, 16806977, -2104615050, 1870004480, 16803179
   };
   private final int[] menuOrdering = new int[]{
      607760, 607776, 607792, 610640, -804651004, 807, 808, 809, 804, -804651007, 51, -804651005, 706, 1005, 810, 712179968
   };
   private Verb _changeMemberVerb;
   private Verb _deleteMemberVerb;
   private Verb _addMemberVerb;
   private Verb _saveVerb;
   private AutoTextEditField _nameField;
   private boolean _isNew;
   private GroupAddressCardMember[] _originalMembers = new GroupAddressCardMember[0];
   private static final char CHANGE_MEMBER_VERB;
   private static final char DELETE_MEMBER_VERB;
   private static final char ADD_MEMBER_VERB;
   private static final char SAVE_GROUP_VERB;

   protected final byte pickAddressFromList(Object acm, Object[] members) {
      String[] values = new Object[members.length];
      AddressSelectionContext selectionContext = (AddressSelectionContext)(new Object(
         null, null, null, RecognizerRepository.getRecognizers(-3124646573404667739L), null
      ));
      String dialogTitle = AddressBookResources.getString(301);
      ContextObject contextObject = (ContextObject)(new Object(63, 42, 34));

      for (byte i = 0; i < members.length; i++) {
         if (values[i] == null) {
            if (members[i] instanceof Object) {
               values[i] = ((VerbDescriptionProvider)members[i]).getVerbDescription(contextObject);
            } else {
               values[i] = members[i].toString();
            }
         }
      }

      int defaultIndex = 0;
      DefaultProvider defaultProvider = null;
      if (acm instanceof Object) {
         defaultProvider = (DefaultProvider)acm;
         Object defaultEntry = defaultProvider.getDefault(null, selectionContext);

         for (byte i = 0; i < members.length; i++) {
            if (defaultEntry == members[i]) {
               defaultIndex = i;
            }
         }
      }

      return (byte)Dialog.ask(dialogTitle, values, defaultIndex);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean result = true;
      if (this.isDirty()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case -2:
            case 0:
               break;
            case -1:
               result = false;
               break;
            case 1:
               this._saveVerb.invoke(context);
               return false;
            case 2:
            default:
               ((GroupAddressCardModelImpl)super._gacm).setMembers(this._originalMembers);
               return result;
         }
      }

      return result;
   }

   private final boolean checkDuplicates(Object element) {
      for (int i = super._gacm.size() - 1; i >= 0; i--) {
         Object rm = super._gacm.getAddressModelAt(i);
         if (rm != null && rm.equals(element)) {
            this.setFocus(super._listField, 0, 0, 0, 0);
            super._listField.setSelectedIndex(i);
            Dialog.alert(((StringBuffer)(new Object())).append(AddressBookResources.getString(811)).append(rm.toString()).toString());
            return false;
         }
      }

      return true;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._addMemberVerb);
      menu.add(this._saveVerb);
      if (super._gacm.size() > 0 && this.getFieldWithFocus() == super._listField) {
         menu.add(this._changeMemberVerb);
         menu.add(this._deleteMemberVerb);
      }

      menu.setDefault(this._addMemberVerb);
   }

   EditGroupAddressCardScreen(GroupAddressCardModel gacm, boolean isNew) {
      super(gacm);
      this.setDefaultClose(false);
      String label = isNew ? AddressBookResources.getString(706) : AddressBookResources.getString(1005);
      this._nameField = (AutoTextEditField)(new Object(
         ((StringBuffer)(new Object())).append(label).append(": ").toString(), gacm.getName(), 100, 4503601774854144L
      ));
      this._nameField.setCursorPosition(this._nameField.getTextLength());
      this.setTitle(this._nameField);
      ((GroupAddressCardModelImpl)super._gacm).getMembers(this._originalMembers);
      this._isNew = isNew;
      this._changeMemberVerb = new EditGroupAddressCardScreen$GroupMemberVerb(this, '\u0000');
      this._deleteMemberVerb = new EditGroupAddressCardScreen$GroupMemberVerb(this, '\u0001');
      this._addMemberVerb = new EditGroupAddressCardScreen$GroupMemberVerb(this, '\u0002');
      this._saveVerb = new EditGroupAddressCardScreen$GroupMemberVerb(this, '\u0003');
      this.setLeaveScreenVerb(ExitVerb.createCloseVerb(0, this));
   }
}
