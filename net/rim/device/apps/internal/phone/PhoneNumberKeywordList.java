package net.rim.device.apps.internal.phone;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.DialVerbCombiner;
import net.rim.device.apps.internal.phone.api.verbs.SpeedDialVerb;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.awt.Event;

final class PhoneNumberKeywordList extends KeywordFilterCollectionListField implements ListFieldCallback {
   protected long _sortOrder = 1232448844688687736L;
   private PhoneAppScreen _phoneAppScreen;
   private PhoneNumberKeywordList$ExpandedAddressInfo _expandedAddressInfo = null;
   private int _expandAddressRunnableId = -1;
   private Field _source;
   private Runnable _expandAddressRunnable = new PhoneNumberKeywordList$1(this);
   private static final int EXPANSION_DELAY;

   public PhoneNumberKeywordList(PhoneAppScreen phoneAppScreen, Field source) {
      super(null, null, 8);
      this._phoneAppScreen = phoneAppScreen;
      this._source = source;
      AddressBook addressBook = AddressBookServices.getAddressBook();
      if (addressBook != null) {
         AddressBookOptions options = addressBook.getAddressBookOptions();
         KeywordFilterList view = null;
         if (options != null) {
            this._sortOrder = options.getSortOrder();
         }

         view = addressBook.getView(this._sortOrder);
         this.setCallback(this);
         this.setKeywordFilterList(view);
         view.setCriteria(null, this);
         view.waitForComplete();
         this.setSize(view.size());
      }
   }

   final void updateField() {
      this.doUpdateList();
   }

   @Override
   protected final void doUpdateList() {
      if (this._phoneAppScreen.isForeground()) {
         super.doUpdateList();
         if (super._list.size() > 0) {
            Manager manager = this.getManager();
            if (manager != null && manager.isFocus()) {
               this.expandAddressInfo(0);
            }
         } else {
            this._expandedAddressInfo = null;
         }

         if (this._expandedAddressInfo == null || !this._expandedAddressInfo.showNumbers()) {
            this.setSelectedIndex(0);
         }
      }
   }

   private final void expandAddressInfo(int index) {
      Object element = this.getElementAt(index);
      if (element instanceof Object) {
         AddressCardModel acm = (AddressCardModel)element;
         if (this._expandedAddressInfo == null || this._expandedAddressInfo.getAddressCard() != acm) {
            this._expandedAddressInfo = new PhoneNumberKeywordList$ExpandedAddressInfo(acm, index);
         }

         if (this._expandedAddressInfo != null && !this._expandedAddressInfo.showNumbers()) {
            if (this._expandAddressRunnableId != -1) {
               this._phoneAppScreen.getApplication().cancelInvokeLater(this._expandAddressRunnableId);
               this._expandAddressRunnableId = -1;
            }

            this._expandAddressRunnableId = this._phoneAppScreen.getApplication().invokeLater(this._expandAddressRunnable, 400, false);
         }
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (action == 1) {
         Object obj = this.getSelectedElement();
         if (obj == null) {
            return true;
         }

         if (obj instanceof Object) {
            DialVerb dialVerb = (DialVerb)(new Object(obj, this.getSelectedAddress()));
            dialVerb.invoke(null);
            return true;
         }
      }

      return false;
   }

   @Override
   protected final void onUnfocus() {
      if (this._expandAddressRunnableId != -1) {
         this._phoneAppScreen.getApplication().cancelInvokeLater(this._expandAddressRunnableId);
         this._expandAddressRunnableId = -1;
      }

      super.onUnfocus();
      if (this._expandedAddressInfo != null) {
         if (this._expandedAddressInfo.showNumbers()) {
            this.setSize(super._list.size());
         }

         this._expandedAddressInfo = null;
      }

      this.setSelectedIndex(0);
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      this.setSelectedIndex(0);
      this.expandAddressInfo(0);
   }

   final AddressCardModel getSelectedAddress() {
      if (super._list.size() == 0) {
         return null;
      } else if (this._expandedAddressInfo != null) {
         return this._expandedAddressInfo.getAddressCard();
      } else {
         return (AddressCardModel)(this.getElementAt(this.getSelectedIndex()) instanceof Object ? this.getElementAt(this.getSelectedIndex()) : null);
      }
   }

   @Override
   public final Object getSelectedElement() {
      return this._expandedAddressInfo != null && this.getSelectedIndex() != this._expandedAddressInfo.getIndex()
         ? this._expandedAddressInfo.getSelectedPhoneNumber(this.getSelectedIndex() - 1)
         : this.getSelectedAddress();
   }

   @Override
   protected final int getListSize() {
      int size = super.getListSize();
      if (size == 0) {
         this._expandedAddressInfo = null;
         return size;
      }

      if (this._expandedAddressInfo != null && this._expandedAddressInfo.showNumbers()) {
         size += this._expandedAddressInfo.numEntries();
      }

      return size;
   }

   final void onRollOffPhoneNumberInput() {
      this.setSelectedIndex(0);
      this.expandAddressInfo(0);
   }

   @Override
   public final void dispatchEvent(Event rEvent) {
      this._source.dispatchEvent(rEvent);
   }

   public final boolean keyClickedAndHeld(int keycode) {
      if (!QuickContactList.isValidQuickContactKey(keycode)) {
         return true;
      }

      Object obj = this.getSelectedElement();
      if (obj instanceof Object) {
         PhoneNumberModel pnm = (PhoneNumberModel)obj;
         QuickContactList qcl = QuickContactList.getInstance();
         if (pnm.canSpeedDial() && qcl.getQuickContactKey(pnm) == 0 && qcl.getQuickContactItem(keycode) == null) {
            SpeedDialVerb.assignSpeedDial(UiInternal.map(keycode), keycode, pnm, false, true);
            return true;
         }
      }

      return false;
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      if ((status & 131072) == 0 && Trackball.isSupported()) {
         return 0;
      }

      int size = this.getSize();
      int index = this.getSelectedIndex() + amount;
      if (index < 0) {
         return super.moveFocus(amount, status, time);
      }

      if (index >= size) {
         int listSize = super._list.size();
         if (this._expandedAddressInfo != null && this._expandedAddressInfo.getIndex() != listSize - 1) {
            this._expandedAddressInfo = null;
            this.setSize(listSize);
            this.expandAddressInfo(--listSize);
            this.setSelectedIndex(listSize);
         }

         return super.moveFocus(amount, status, time);
      } else {
         if (this._expandedAddressInfo != null) {
            int expandedIndex = this._expandedAddressInfo.getIndex();
            int expandedEntries = this._expandedAddressInfo.showNumbers() ? this._expandedAddressInfo.numEntries() : 0;
            if (index < expandedIndex) {
               size -= expandedEntries;
               this._expandedAddressInfo = null;
            } else if (index > expandedIndex + expandedEntries) {
               size -= expandedEntries;
               index -= expandedEntries;
               this._expandedAddressInfo = null;
            }
         }

         if (this._expandedAddressInfo == null) {
            this.setSize(size);
            this.expandAddressInfo(index);
         }

         this.setSelectedIndex(index);
         return 0;
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 10:
         case 17:
         case 18:
         case 27:
         case 4098:
            return Keypad.map(keycode);
         default:
            return super.processKeyEvent(event, key, keycode & -2, time);
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 10:
         case 17:
            if (this.invokeAction(1)) {
               return true;
            } else {
               AddressCardModel addressCard = this.getSelectedAddress();
               if (addressCard != null) {
                  Object[] objects = addressCard.getPhoneNumberModels();
                  if (objects != null && objects.length > 0) {
                     Verb[] dialVerbs = new Object[objects.length];

                     for (int i = 0; i < objects.length; i++) {
                        if (objects[i] instanceof Object) {
                           dialVerbs[i] = (Verb)(new Object(objects[i], addressCard));
                        }
                     }

                     if (dialVerbs.length > 1) {
                        DialVerbCombiner dvc = DialVerbCombiner.getInstance();
                        dvc.createWrapperVerb(dialVerbs, dialVerbs[0]).invoke(null);
                        return true;
                     }

                     dialVerbs[0].invoke(null);
                  }
               }

               return true;
            }
         default:
            return false;
      }
   }

   private final void drawCurrentItem(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      synchronized (AddressBookServices.getAddressBookCollection()) {
         Object element = collectionListField.getElementAt(index);
         if (!(element instanceof Object)) {
            if (index == 0 && element == null) {
               graphics.drawText(this.getEmptyString(), 0, y, 4, width);
            }
         } else {
            PaintProvider painter = (PaintProvider)element;
            painter.paint(graphics, 0, y, width, 100, new Object(4, 18));
         }
      }
   }

   final void addToMenu(SystemEnabledMenu menu, int instance, boolean systemLocked, boolean outgoingCallAllowed) {
      Object obj = this.getSelectedElement();
      if (obj != null) {
         Verb[] verbs = new Object[0];
         Verb defaultVerb = null;
         ContextObject context = (ContextObject)(new Object(11, 114));
         if (systemLocked) {
            context.setFlag(36);
            PhoneUtilities.setPrivateFlag(context, 15);
         }

         if (obj instanceof Object) {
            context.setFlag(34);
            PhoneUtilities.setPrivateFlag(context, 59);
            PhoneNumberModel pnm = (PhoneNumberModel)obj;
            if (!systemLocked && outgoingCallAllowed && pnm.canSpeedDial()) {
               char key = QuickContactList.getInstance().getQuickContactKey(pnm);
               if (key == 0) {
                  menu.add((Verb)(new Object(pnm, 6072, 1332244, '\u0000')));
               } else {
                  menu.add((Verb)(new Object(pnm, 6073, 1332244, key)));
               }
            }
         } else if (!systemLocked && instance == 0) {
            AbstractPhoneNumberModel.addViewContactVerb(verbs, obj);
         }

         if (obj instanceof Object) {
            VerbProvider vp = (VerbProvider)obj;
            defaultVerb = vp.getVerbs(context, verbs);
         }

         Recognizer recognizer = DialVerb.getRecognizer();

         for (int i = 0; i < verbs.length; i++) {
            if (recognizer.recognize(verbs[i])) {
               defaultVerb = verbs[i];
               break;
            }
         }

         if (!systemLocked && outgoingCallAllowed && instance == 0) {
            menu.add((Verb)(new Object(null, 6094, 1397760, '\u0000')));
         }

         if (systemLocked) {
            menu.add(defaultVerb);
         } else {
            menu.add(verbs);
         }

         menu.setDefault(defaultVerb);
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (index != 0 && this._expandedAddressInfo != null) {
         if (this._expandedAddressInfo != null) {
            if (this._expandedAddressInfo.showNumbers()) {
               int extraEntries = this._expandedAddressInfo == null ? 0 : this._expandedAddressInfo.numEntries();
               if (index > this._expandedAddressInfo.getIndex() && index <= this._expandedAddressInfo.getIndex() + extraEntries) {
                  this._expandedAddressInfo.drawItem(graphics, index, y, width, index == this.getSelectedIndex());
                  return;
               }

               if (index > this._expandedAddressInfo.getIndex()) {
                  index -= this._expandedAddressInfo.numEntries();
               }
            }

            this.drawCurrentItem(listField, graphics, index, y, width);
         }
      } else {
         this.drawCurrentItem(listField, graphics, index, y, width);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return this.getSelectedIndex();
   }

   static final KeywordFilterList access$200(PhoneNumberKeywordList x0) {
      return x0._list;
   }
}
