package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.SpeedDialVerb;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.Array;

public class PhoneListView extends PhoneListFieldManager implements ListFieldCallback, Runnable {
   protected ContextObjectWR PAINTING_CONTEXT_WR = (ContextObjectWR)(new Object());
   protected Application _app;
   protected PhoneListItems _items;
   protected int _flags;
   protected int _type;
   protected boolean _resetCache = true;
   private boolean _pendingUpdate;
   public PhoneListView$DeleteItemVerb _deleteVerb = new PhoneListView$DeleteItemVerb(this);
   public static final int PHONE_LIST_VIEW_TYPE_CALL_LOG;
   public static final int PHONE_LIST_VIEW_TYPE_HOTLIST;
   protected static final int ITEMS_IN_REVERSE_ORDER;

   protected String getDeletePrompt(boolean _1) {
      throw null;
   }

   public int getType() {
      return this._type;
   }

   public boolean showsSpeedDialKeys() {
      return true;
   }

   protected int getDeleteVerbOrdering() {
      return 16908640;
   }

   public void addToMenu(SystemEnabledMenu menu, int instance, boolean systemLocked, boolean outgoingCallAllowed) {
      PhoneListItem phoneListItem = (PhoneListItem)this.getSelectedItem();
      char speedDialKey = 0;
      if (phoneListItem != null) {
         ContextObject context = (ContextObject)(new Object(2));
         if (systemLocked) {
            PhoneUtilities.setPrivateFlag(context, 15);
         }

         if (systemLocked || instance == 65536) {
            PhoneUtilities.setPrivateFlag(context, 76);
            PhoneUtilities.setPrivateFlag(context, 55);
         }

         this.getVerbContextFlags(phoneListItem, context);
         Verb[] verbs = new Object[0];
         Verb defaultVerb = phoneListItem.getVerbs(context, verbs);
         if (PhoneUtilities.getPrivateFlag(context, 7) && instance == 65536) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = new VoiceMailVerb();
            defaultVerb = verbs[verbs.length - 1];
         }

         if (systemLocked) {
            if (!DialVerb.getRecognizer().recognize(defaultVerb)) {
               defaultVerb = null;
            }

            for (int i = verbs.length - 1; i >= 0; i--) {
               if (DialVerb.getRecognizer().recognize(verbs[i])) {
                  menu.add(verbs[i]);
               }
            }
         } else {
            menu.add(verbs);
         }

         menu.setDefault(defaultVerb);
         if (!systemLocked && outgoingCallAllowed && instance == 0 && phoneListItem.canSpeedDial()) {
            speedDialKey = phoneListItem.getSpeedDialKey();
            if (speedDialKey == 0) {
               menu.add(new SpeedDialVerb(phoneListItem, 6072, 1332244, '\u0000'));
            } else {
               menu.add(new SpeedDialVerb(phoneListItem, 6073, 1332249, speedDialKey));
            }
         }
      }

      if (!systemLocked && instance == 0) {
         if (outgoingCallAllowed) {
            menu.add(new SpeedDialVerb(null, 6094, 1397760, speedDialKey));
         }

         menu.add(this._deleteVerb);
      }
   }

   protected void getVerbContextFlags(PhoneListItem item, ContextObject context) {
      PhoneUtilities.setPrivateFlag(context, 38);
      if (!PhoneUtilities.getPrivateFlag(context, 84)) {
         PhoneUtilities.setPrivateFlag(context, 74);
      }
   }

   protected ContextObject getPaintingContextObject(PhoneListItem itemToBePainted) {
      ContextObject context = this.PAINTING_CONTEXT_WR.getContextObject();
      context.reset();
      return context;
   }

   protected void refreshList() {
      int index = super._listField.getSelectedIndex();
      this.setSize();
      this.setSelectedIndex(index);
   }

   protected void updateOnCorrectThread() {
      if (!this._pendingUpdate) {
         this._pendingUpdate = true;
         this._app.invokeLater(this);
      }
   }

   public void resetCache() {
      this._resetCache = true;
   }

   protected void loadItems() {
      throw null;
   }

   public void shutDown() {
   }

   protected void setSize() {
      super._listField.setSize(this.getCount());
   }

   public void setFirstIndex() {
      super._listField.setSelectedIndex(0);
   }

   public int getCount() {
      return this._items.getCount();
   }

   protected void setSelectedIndex() {
      int phoneListIndex = this._items.getCurrentIndex();
      int listfieldIndex = -1;
      if (phoneListIndex == -1) {
         listfieldIndex = 0;
      } else {
         listfieldIndex = phoneListIndex;
      }

      super._listField.setSelectedIndex(listfieldIndex);
   }

   public void smartSetFocus() {
      if (this.getScreen() != null) {
         this.setSelectedIndex();
         this.setFocus();
      }
   }

   protected void setSelectedIndex(int index) {
      super._listField.setSelectedIndex(index);
   }

   public PhoneListItem getSelectedItem() {
      int[] selItems = super._listField.getSelection();
      return (PhoneListItem)(selItems != null && selItems.length == 1 ? this.getItem(selItems[0]) : null);
   }

   public void deleteSelectedItem() {
      int[] selectedItems = super._listField.getSelection();
      if (selectedItems != null && selectedItems.length > 0) {
         boolean needStatus = false;
         if (selectedItems.length > 1) {
            needStatus = true;
         } else {
            PhoneListItem item = (PhoneListItem)this.getItem(selectedItems[0]);
            if (item == null) {
               return;
            }

            needStatus = item.isLongRunningDelete();
         }

         if (needStatus) {
            PhoneListView$DeleteStatusDialog status = new PhoneListView$DeleteStatusDialog(this, this, selectedItems);
            status.show(10);
            return;
         }

         for (int i = selectedItems.length - 1; i >= 0; i--) {
            this.delete(this.getInternalIndex(selectedItems[i]));
         }
      }
   }

   public PhoneListItem getItem(int index) {
      return this._items.get(this.getInternalIndex(index));
   }

   public void delete(int index) {
      this._items.delete(index);
   }

   public int getSelectedIndex() {
      return super._listField.getSelectedIndex();
   }

   public int getSelectionCount() {
      return super._listField.getSelection().length;
   }

   void onPaintAborted(PhoneListItem item, int index) {
      this.delete(index);
   }

   public void onRemovedFromScreen() {
   }

   public boolean keyClickedAndHeld(int keycode) {
      if (!QuickContactList.isValidQuickContactKey(keycode)) {
         return false;
      } else {
         PhoneListItem phoneListItem = (PhoneListItem)this.getSelectedItem();
         if (phoneListItem != null
            && phoneListItem.canSpeedDial()
            && phoneListItem.getSpeedDialKey() == 0
            && QuickContactList.getInstance().getQuickContactItem(keycode) == null) {
            SpeedDialVerb.assignSpeedDial(UiInternal.map(keycode), keycode, phoneListItem, false, true);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void run() {
      this._pendingUpdate = false;
      this.refreshList();
   }

   private int getInternalIndex(int index) {
      return (this._flags & 2) != 0 ? this._items.getCount() - index - 1 : index;
   }

   @Override
   protected void setEmptyString(ListField listField) {
      listField.setEmptyString(CommonResources.getResourceBundle(), 103, 4);
   }

   public static boolean isContentProtected() {
      return PersistentContent.getTicket() == null;
   }

   @Override
   public boolean isFocusable() {
      return isContentProtected() ? false : super.isFocusable();
   }

   public static PhoneListView getCurrentView(Application app, Object context) {
      return !PhoneUtilities.getPrivateFlag(context, 72) && PhoneOptions.getOptions().getPhoneListViewType() != 3 ? new HotlistView(app) : new CallLogView(app);
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (!isContentProtected()) {
         PhoneListItem item = (PhoneListItem)this.getItem(index);
         if (PhoneUtilities.smallMonoScreen()) {
            this.correctFont();
         }

         if (item != null) {
            Object context = this.getPaintingContextObject(item);
            VariableRowHeightProxy.addHeightAdjusterToContext(context, listField);
            item.paint(graphics, 0, y, width, listField.getRowHeight(), context, this, index);
         }
      }
   }

   private void correctFont() {
      Font font = Font.getDefault();
      if (font.getHeight() < 10) {
         font = font.derive(font.getStyle(), 10);
      }

      this.setFont(font);
   }

   @Override
   protected void applyFont() {
      this.correctFont();
      super.applyFont();
   }

   public PhoneListView(Application app, int flags, int type) {
      super(0, true);
      this._app = app;
      this._flags = flags;
      this._type = type;
      this.setTag(Tag.create("client"));
      this.setId("phone");
      this.loadItems();
      this.setSize();
      this.setSelectedIndex();
   }

   @Override
   protected void paint(Graphics graphics) {
      this._items.onDisplay();
      super.paint(graphics);
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 8:
         case 127:
            this._deleteVerb.invoke(null);
            return true;
         case 17:
            PhoneListItem item = (PhoneListItem)this.getSelectedItem();
            if (item != null) {
               CallerIDInfo cid = item.getCallerIDInfo();
               if (cid != null) {
                  Verb verb;
                  if (cid.isVoicemailCallerIDInfo()) {
                     verb = new VoiceMailVerb();
                  } else {
                     verb = new DialVerb(cid);
                  }

                  PhoneLogger.log("startcall SEND phonelistitem");
                  verb.invoke(null);
               }
            }

            return true;
         default:
            return super.keyUp(keycode, time);
      }
   }
}
