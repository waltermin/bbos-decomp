package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;

public final class SearchPOIDialog extends PopupScreen implements ListFieldCallback, FieldChangeListener {
   private SearchPOIHistoryDataHandler _dataHandler;
   private EditField _keywordsField;
   private ListField _list;
   private RigidManager _listManager;
   boolean _result;
   String _keywords;
   private LabelField _titleField;

   public SearchPOIDialog(Object context, String title) {
      super(new VerticalFieldManager(1153220571769602048L), 196608);
      this.applyTheme();
      this.addTitle(title);
      this._keywordsField = new EditField("", "", 256, 2147483648L);
      if (this._keywordsField.getTextLength() > 0) {
         this._keywordsField.setCursorPosition(this._keywordsField.getTextLength() - 1);
      } else {
         this._keywordsField.setCursorPosition(0);
      }

      this.add(this._keywordsField);
      this._dataHandler = SearchPOIHistoryDataHandler.getInstance();
      if (this._dataHandler.getItemCount() > 0) {
         this.add(new SeparatorField(65536));
         int fontHeight = Font.getDefault().getHeight();
         this.add(new VerticalSpacerField(fontHeight >> 1));
         this._list = new ListField(this._dataHandler.getItemCount());
         this._list.setCallback(this);
         this._listManager = new RigidManager(100, Math.min(3, this._dataHandler.getItemCount()), 299067162755072L, this._list.getFont());
         this._listManager.add(this._list);
         this.add(this._listManager);
      }

      this._keywordsField.setFocus();
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         this._titleField = new LabelField(title + ":");
         this._titleField.setFont(Font.getDefault().derive(1));
         RigidManager rigidManager = new RigidManager(100, 1, 299067162755072L, this._titleField.getFont());
         rigidManager.add(this._titleField);
         this.add(rigidManager);
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   final boolean validate() {
      if (this._keywords != null && this._keywords.length() != 0) {
         SearchPOIHistoryItem item = new SearchPOIHistoryItem(this._keywords);
         this._dataHandler.add(item);
         return true;
      } else {
         Dialog.alert(LBSResources.getString(219));
         return false;
      }
   }

   public final String doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._keywords;
   }

   final void close(boolean result) {
      if (result) {
         Field field = this.getLeafFieldWithFocus();
         if (field == this._list) {
            this._keywords = this._dataHandler.getItemAt(this._list.getSelectedIndex()).keywords;
         } else {
            this._keywords = this._keywordsField.getText();
         }

         if (this._dataHandler.isSort()) {
            this._dataHandler.unsort();
         }

         if (!this.validate()) {
            return;
         }
      }

      this._result = result;
      this.close();
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (this.getLeafFieldWithFocus() == this._list) {
         return false;
      }

      this.close(true);
      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\b':
            Field field = this.getLeafFieldWithFocus();
            if (field == this._list && this._dataHandler.getItemCount() > 0 && Dialog.ask(3, LBSResources.getString(114), -1) == 4) {
               this.deleteSelected();
               return true;
            }
         default:
            return super.keyChar(key, status, time);
         case '\n':
            this.close(true);
            return true;
         case '\u001b':
            this.close(false);
            return true;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      Field field = this.getLeafFieldWithFocus();
      if (this._dataHandler.getItemCount() > 0 && this._dataHandler.getItemAt(0) != null && field == this._list) {
         this.close(true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      Field field = this.getLeafFieldWithFocus();
      if (this._dataHandler.getItemCount() > 0 && this._dataHandler.getItemAt(0) != null && field == this._list) {
         menu.add(new SearchPOIDialog$1(this, LBSResources.getString(72), 1, 524287));
         menu.add(new SearchPOIDialog$2(this, LBSResources.getString(79), 1, 524287));
         menu.add(new SearchPOIDialog$3(this, LBSResources.getString(280), 1, 524287));
      }

      super.makeMenu(menu, instance);
   }

   private final void deleteSelected() {
      int index = this._list.getSelectedIndex();
      if (index > -1 && index < this._dataHandler.getItemCount()) {
         this._dataHandler.delete(index);
         this._list.delete(index);
         this._titleField.getScreen().ensureRegionVisible(this._titleField, 0, 0, 0, 0);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         ;
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      SearchPOIHistoryItem item = null;

      label27:
      try {
         item = this._dataHandler.getItemAt(index);
      } finally {
         break label27;
      }

      graphics.drawText(item != null ? item.toString() : "", 0, y, 64);
   }

   @Override
   public final Object get(ListField listField, int index) {
      return index > -1 && index < this._dataHandler.getItemCount() ? this._dataHandler.getItemAt(index) : null;
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return super.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }
}
