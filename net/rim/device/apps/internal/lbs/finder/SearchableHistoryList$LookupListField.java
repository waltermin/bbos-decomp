package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.i18n.CommonResource;

final class SearchableHistoryList$LookupListField extends ListField implements ListFieldCallback {
   private final String[] EMPTY_STRING;
   private boolean _locationSelected;
   int[] _searchedIndecies;
   private final SearchableHistoryList this$0;

   SearchableHistoryList$LookupListField(SearchableHistoryList this$0) {
      this.this$0 = this$0;
      this.EMPTY_STRING = new String[]{LBSResources.getString(415), CommonResource.getString(1012)};
   }

   public final void reinit() {
      if (this.this$0._lineCount != 0) {
         this.setSize(this.this$0._historyStore.getItemCount());
      }
   }

   public final void matchList() {
      String pattern = this.this$0._historyEditField.getText();
      this._searchedIndecies = this.this$0._historyStore.match(pattern);
      int sizeMax = FinderHistory.getInstance().getItemCount();
      if (pattern.equals("")) {
         this.setSize(sizeMax);
      } else {
         boolean sizeSet = false;

         for (int i = 0; i < this._searchedIndecies.length; i++) {
            if (this._searchedIndecies[i] == -1) {
               this.setSize(i);
               sizeSet = true;
               break;
            }
         }

         if (!sizeSet) {
            this.setSize(sizeMax);
         }
      }
   }

   @Override
   public final boolean isFocusable() {
      return this.getSize() == 0 ? false : super.isFocusable();
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      return super.moveFocus(amount, status & -2, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n' || key == 144) {
         return this.invokeAction(1);
      } else if (key == '\b') {
         this.deleteSelectedURL();
         return true;
      } else {
         this.this$0._historyEditField.setFocus();
         return this.this$0._historyEditField.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      if (Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) != 0) {
         this._locationSelected = false;
      }

      if (this._locationSelected && Keypad.key(keycode) == 257 && (Keypad.status(keycode) & 1) == 0) {
         this.this$0._historyEditField.setFocus();
         this.this$0.fieldChanged(this.this$0._historyEditField, 0);
         return true;
      } else {
         return false;
      }
   }

   public final Location getLocationAtIndex(int index) {
      return this.this$0._historyStore.getItemAt(index);
   }

   private final void deleteSelectedURL() {
      int selectedIndex = this.getSelectedIndex();
      this.this$0._historyStore.delete(selectedIndex);
      this.delete(selectedIndex);
      if (!this.isFocusable()) {
         this.this$0._historyEditField.getScreen().ensureRegionVisible(this.this$0._historyEditField, 0, 0, 0, 0);
         this.this$0._historyEditField.setFocus();
      }
   }

   private final void setSelectedLocation() {
      String url = this.getLocationAtIndex(this.getSelectedIndex())._label;
      if (url != null) {
         this.this$0._historyEditField.setText(url);
         this.this$0._historyEditField.setDirty(true);
         this._locationSelected = true;
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      if (index > -1 && index < this.this$0._historyStore.getItemCount() && this._searchedIndecies[index] > -1) {
         Location location = this.this$0._historyStore.getItemAt(this._searchedIndecies[index]);
         g.drawText(location._label, 0, y, 70, width);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      if (index > -1 && index < this.this$0._historyStore.getItemCount() && this._searchedIndecies[index] > -1) {
         Location location = this.this$0._historyStore.getItemAt(this._searchedIndecies[index]);
         return location._label;
      } else {
         return null;
      }
   }

   @Override
   public final String getEmptyString() {
      String[] var10000 = this.EMPTY_STRING;
      return this.this$0._historyStore.getItemCount() > 0 ? var10000[0] : var10000[1];
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      FinderHistory history = FinderHistory.getInstance();
      menu.add(new SearchableHistoryList$1(this, LBSResources.getString(72), 0, 524287));
      menu.add(new SearchableHistoryList$2(this, LBSResources.getString(71), 1, 524287));
      menu.add(new SearchableHistoryList$3(this, LBSResources.getString(79), 2, 524287));
      menu.add(new SearchableHistoryList$4(this, LBSResources.getString(420), 3, 524287, history));
      if (history.getItemCount() > 1 && !history.isSorted() && this.this$0._historyEditField.getText().equals("")) {
         menu.add(new SearchableHistoryList$5(this, LBSResources.getString(280), 4, 524287, history));
      }
   }
}
