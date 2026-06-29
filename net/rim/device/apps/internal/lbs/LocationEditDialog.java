package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationEditDialog extends PopupScreen implements FieldChangeListener {
   private SimpleFolder _folder;
   private ButtonField _okField;
   private ButtonField _cancelField;
   private FlowFieldManager _hfm = (FlowFieldManager)(new Object(12884901888L));
   private AutoTextEditField _labelField;
   private LabelField _folderField;
   Location _location;
   boolean _result;
   private LBSMenuItem[] _menuItems = new LBSMenuItem[0];
   static LocationDocumentCollection _locationDocumentCollection = LocationDocumentCollection.getInstance();

   LocationEditDialog(Location location) {
      super((Manager)(new Object(1153220571769602048L)), 196608);
      this.applyTheme();
      this.addTitle(LBSResources.getString(110));
      this._location = location;
      String label = location._label;
      if (label == null || label.equals("")) {
         label = _locationDocumentCollection.generateUniqueLabel();
      }

      label = this.clipLength(label, 2048);
      this._labelField = (AutoTextEditField)(new Object(LBSResources.getString(98), label, 2048, 2147483648L));
      if (this._labelField.getTextLength() > 0) {
         this._labelField.setCursorPosition(this._labelField.getTextLength() - 1);
      } else {
         this._labelField.setCursorPosition(0);
      }

      this.add(this._labelField);
      this._folder = FavouritesManager.getRootFolder();
      this._folderField = (LabelField)(new Object(
         ((StringBuffer)(new Object())).append(LBSResources.getString(315)).append(' ').append(this._folder.getFriendlyName()).toString()
      ));
      this.add(this._folderField);
      this.addButtons();
      this._okField.setFocus();
      this.createMenuItems();
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         LabelField labelField = (LabelField)(new Object(title));
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
         this.add((Field)(new Object()));
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

   private final void addButtons() {
      this._okField = (ButtonField)(new Object(CommonResources.getString(117), 65536));
      this._okField.setChangeListener(this);
      this._hfm.add(this._okField);
      this._cancelField = (ButtonField)(new Object(CommonResources.getString(9042), 65536));
      this._cancelField.setChangeListener(this);
      this._hfm.add(this._cancelField);
      this.add(this._hfm);
   }

   final boolean validate() {
      String label = this._labelField.getText().trim();
      if (!label.equals(this._labelField.getText())) {
         this._labelField.setText(label);
      }

      if (label.length() == 0) {
         Dialog.alert(LBSResources.getString(142));
         return false;
      } else if (_locationDocumentCollection.findElementByLabel(label) >= 0) {
         Dialog.alert(LBSResources.getString(112));
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._okField) {
         this.close(true);
         return true;
      } else if (field == this._cancelField) {
         this.close(false);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }

   final boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   final void add(LBSMenuItem menuItem) {
      Arrays.add(this._menuItems, menuItem);
   }

   final void createMenuItems() {
      int order = 30270;
      this.add(new LocationEditDialog$1(this, 314, order++));
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      for (int i = this._menuItems.length - 1; i >= 0; i--) {
         LBSMenuItem menuItem = this._menuItems[i];
         menu.add(menuItem);
      }

      super.makeMenu(menu, instance);
   }

   final void close(boolean result) {
      if (result) {
         result = this.onSave();
         if (!result) {
            return;
         }
      } else {
         this._okField.setDirty(false);
         this._cancelField.setDirty(false);
         if (this.isDirty()) {
            int answer = this.onSavePromptInternal();
            if (answer == -1) {
               return;
            }

            result = answer == 1;
         }
      }

      this._result = result;
      super.close();
   }

   @Override
   public final void close() {
      this.close(false);
   }

   @Override
   protected final boolean onSave() {
      if (!this.validate()) {
         return false;
      }

      this._location._label = this._labelField.getText();
      return super.onSave();
   }

   @Override
   public final void save() {
      _locationDocumentCollection.addOrUpdate(this._location, this._folder);
   }

   private final int onSavePromptInternal() {
      int answer = Dialog.ask(1);
      if (answer == 1) {
         return this.onSave() ? 1 : -1;
      }

      if (answer == -1) {
         answer = -1;
      }

      return answer;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.close(this.getLeafFieldWithFocus() != this._cancelField);
            return true;
         case '\u001b':
            this.close(false);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._okField) {
            this.close(true);
         }

         if (field == this._cancelField) {
            this.close(false);
         }
      }
   }

   private final String clipLength(String input, int maxLength) {
      if (input != null && input.length() > maxLength) {
         String output = input.substring(0, maxLength);
         return output;
      } else {
         return input;
      }
   }
}
