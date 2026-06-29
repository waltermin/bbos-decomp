package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class SearchableHistoryList extends VerticalFieldManager implements FieldChangeListener {
   private SearchableHistoryList$SearchHistoryEditField _historyEditField;
   private SearchableHistoryList$LookupListField _historyList;
   private FinderHistory _historyStore;
   private SearchableHistoryList$Callback _callback;
   protected int _lineCount;

   public final Location getSelectedLocation() {
      int index = this._historyList.getSelectedIndex();
      int searchedIndex = this._historyList._searchedIndecies[index];
      Location location = this._historyStore.getItemAt(searchedIndex);
      if (location != null) {
         this._historyStore.stackItemToPop(searchedIndex);
      }

      return location;
   }

   public final void setSelectedIndex(int index) {
      this._historyList.setSelectedIndex(index);
   }

   public final void update() {
      this._historyList.matchList();
   }

   public final void reinit() {
      this._historyList.reinit();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._historyEditField) {
         if (!this._historyEditField.getText().equals("") && !this._historyStore.isSorted()) {
            this._historyStore.sort();
            this.invalidate();
         }

         if ((context & -2147483648) == 0) {
            this._historyList.matchList();
         }
      }
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      FinderHistory.popItems();
   }

   private final void editHistoryItem() {
      int index = this._historyList.getSelectedIndex();
      Location location = this._historyStore.getItemAt(index);
      if (location != null) {
         this._callback.onLocation(location, 1);
      }
   }

   private final void handleHistoryListClick() {
      int index = this._historyList.getSelectedIndex();
      int searchedIndex = this._historyList._searchedIndecies[index];
      Location location = this._historyStore.getItemAt(searchedIndex);
      if (location != null) {
         this._historyStore.stackItemToPop(searchedIndex);
         this._callback.onLocation(location, 0);
      }
   }

   public SearchableHistoryList(SearchableHistoryList$Callback callback) {
      super(562949953421312L);
      this._callback = callback;
      this._historyStore = FinderHistory.getInstance();
      this._historyEditField = new SearchableHistoryList$SearchHistoryEditField(
         this, ((StringBuffer)(new Object())).append(LBSResources.getString(327)).append(' ').toString(), "", Integer.MAX_VALUE, 2147483648L
      );
      this._historyEditField.setChangeListener(this);
      Font f = this._historyEditField.getFont();
      if (f == null) {
         f = Font.getDefault();
      }

      this.add((Field)(new Object(f.getHeight() >> 2)));
      this.add(this._historyEditField);
      this.add((Field)(new Object()));
      this.add((Field)(new Object(f.getHeight() >> 2)));
      VerticalFieldManager vfm = (VerticalFieldManager)(new Object(299067162755072L));
      vfm.add(this._historyList = new SearchableHistoryList$LookupListField(this));
      this.add(vfm);
      this._historyList.matchList();
   }

   private final void deleteSelectedHistoryItem() {
      int index = this._historyList.getSelectedIndex();
      int searchedIndex = this._historyList._searchedIndecies[index];
      if (searchedIndex > -1 && this._historyStore.getItemCount() > 0 && Dialog.ask(3, LBSResources.getString(114), 4) == 4) {
         this._historyStore.delete(searchedIndex);
         this._historyList.setSize(this._historyStore.getItemCount());
         this.update();
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._historyList) {
         switch (key) {
            case '\b':
            case '\u007f':
               this.deleteSelectedHistoryItem();
               return true;
            case '\n':
               this.handleHistoryListClick();
               return true;
         }
      }

      return this._historyEditField.keyChar(key, status, time);
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      boolean altPressed = false;
      Field fieldWithFocus = this.getLeafFieldWithFocus();
      if (fieldWithFocus == this._historyList && (status & 1) != 0) {
         status &= -2;
         altPressed = true;
      }

      int result = super.moveFocus(amount, status, time);
      fieldWithFocus = this.getLeafFieldWithFocus();
      if (((status & 1) != 0 || altPressed) && fieldWithFocus == this._historyList && this._historyList != null && this._historyList.getSelectedIndex() > -1) {
         this._historyList.setSelectedLocation();
      }

      return result;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._historyList) {
         this.handleHistoryListClick();
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }
}
