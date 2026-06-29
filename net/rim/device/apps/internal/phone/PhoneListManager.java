package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.KeywordFilteredListFinder;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList$Listener;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.data.PhoneListView;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneListManager extends VerticalFieldManager implements GlobalEventListener, QuickContactList$Listener {
   private int _view = -1;
   private UiApplication _app;
   protected PhoneNumberKeywordList _addressBookSearchList;
   private VerticalFieldManager _phoneNumberKeywordListManager = (VerticalFieldManager)(new Object(299067162755072L));
   private PhoneListView _phoneListView;
   private LabelField _hintField = (LabelField)(new Object(PhoneResources.getString(6300), 1152921504606846980L));
   private LabelField _emptyAddressField = (LabelField)(new Object(PhoneResources.getString(6306), 1152921504606846980L));
   private Field _currentList;
   static final int NO_VIEW;
   static final int PHONE_LIST_VIEW;
   static final int PHONE_ADDRESS_VIEW;
   static final int EMPTY_ADDRESS_VIEW;
   static final int HINT_VIEW;

   PhoneListManager(PhoneAppScreen phoneAppScreen, KeywordFilteredListFinder finder, Field source, UiApplication app) {
      super(0);
      this.setId("phone");
      this.setTag(Tag.create("client"));
      this._app = app;
      if (AddressBookServices.getAddressBook() != null) {
         this._addressBookSearchList = new PhoneNumberKeywordList(phoneAppScreen, source);
         finder.linkToField(this._addressBookSearchList);
         this._phoneNumberKeywordListManager.add(this._addressBookSearchList);
      }

      this._phoneListView = PhoneListView.getCurrentView(this._app, null);
      this.adjustHintFontSize();
      this.updateText();
   }

   final void updateView(int view, Object context) {
      if (view == 1 && this._phoneListView.getCount() == 0) {
         view = 4;
      }

      if (view != this._view) {
         this.deleteAll();
         if (this._view == 1) {
            this._phoneListView.onRemovedFromScreen();
         }

         switch (view) {
            case 0:
               break;
            case 2:
               if (this._addressBookSearchList != null) {
                  this.add(this._phoneNumberKeywordListManager);
                  this._currentList = this._phoneNumberKeywordListManager;
                  break;
               }
            case 1:
               this._phoneListView = PhoneListView.getCurrentView(this._app, context);
               this._currentList = this._phoneListView;
               this.add(this._phoneListView);
               break;
            case 3:
            default:
               this._currentList = this._emptyAddressField;
               this.add(this._emptyAddressField);
               break;
            case 4:
               this.add(this._hintField);
               view = 4;
         }

         this._view = view;
      }
   }

   final boolean hasEntries() {
      return this._view == 2 ? this._addressBookSearchList.getListSize() > 0 : false;
   }

   @Override
   public final void setFocus() {
      if (this._view == 2) {
         this._addressBookSearchList.setFocus();
      } else {
         if (this._view == 1) {
            this._phoneListView.setFocus();
            this._phoneListView.setFirstIndex();
         }
      }
   }

   final void updateField() {
      this._addressBookSearchList.updateField();
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      return this._currentList.processKeyEvent(event, key, keycode, time);
   }

   final void adjustHintFontSize() {
      Font font = Font.getDefault();
      if (font.getHeight(3) > 10) {
         font = font.derive(font.getStyle(), 10, 3);
      }

      this._hintField.setFont(font);
   }

   private final void updateText() {
      this._hintField.setText(PhoneResources.getString(6300));
      this._emptyAddressField.setText(PhoneResources.getString(6306));
      if (this._addressBookSearchList != null) {
         this._addressBookSearchList.setEmptyString(PhoneResources.getString(6306), 0);
      }
   }

   final void addToMenu(SystemEnabledMenu menu, int instance, boolean systemLocked, boolean outgoingCallAllowed) {
      switch (this._view) {
         case 0:
         case 3:
            break;
         case 1:
            this._phoneListView.addToMenu(menu, instance, systemLocked, outgoingCallAllowed);
            break;
         case 2:
            this._addressBookSearchList.addToMenu(menu, instance, systemLocked, outgoingCallAllowed);
            break;
         case 4:
         default:
            return;
      }

      Verb defaultVerb = menu.getDefaultVerb();
      DefaultVerbProvider defaultVerbProvider = null;
      if (defaultVerb instanceof Object) {
         DialVerb dv = (DialVerb)defaultVerb;
         Object o = dv.getIdentity();
         if (o instanceof Object) {
            defaultVerbProvider = (DefaultVerbProvider)(new Object((RIMModel)o));
         }
      }

      menu.coalesce(-3072555018635390988L, defaultVerbProvider);
   }

   final boolean keyClickedAndHeld(int keycode) {
      return this._currentList instanceof PhoneNumberKeywordList
         ? this._addressBookSearchList.keyClickedAndHeld(keycode)
         : this._phoneListView.keyClickedAndHeld(keycode);
   }

   public final void onRollOffPhoneNumberInput() {
      if (this._currentList instanceof PhoneNumberKeywordList) {
         this._addressBookSearchList.onRollOffPhoneNumberInput();
      } else {
         if (this._currentList instanceof Object) {
            this._phoneListView.setFirstIndex();
         }
      }
   }

   final Object getSelectedItem() {
      return this._currentList instanceof Object ? this._phoneListView.getSelectedItem() : null;
   }

   @Override
   public final void onQuickContactListEvent(int eventId, int index, QuickContactItem item) {
      synchronized (this._app.getAppEventLock()) {
         this.invalidate();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3206808455257958298L) {
         this._app.invokeLater(new PhoneListManager$1(this));
      } else if (guid != 7207871974803693937L
         && guid != 8877632280522743328L
         && guid != 3596208183088439728L
         && guid != -8040378802380461050L
         && guid != -1438311245835636745L) {
         if (guid == -4394903006263251010L) {
            this.adjustHintFontSize();
         }
      } else {
         this._phoneListView.resetCache();
         this.updateText();
      }
   }
}
