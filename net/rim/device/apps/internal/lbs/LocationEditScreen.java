package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationEditScreen extends MainScreen implements FieldChangeListener {
   private AutoTextEditField _labelField;
   private AutoTextEditField _descriptionField;
   private AutoTextEditField _addressField;
   private AutoTextEditField _cityField;
   private AutoTextEditField _regionField;
   private AutoTextEditField _countryField;
   private EditField _postalCodeField;
   private EditField _phoneField;
   private EditField _faxField;
   private AutoTextEditField _urlField;
   private AutoTextEditField _emailField;
   private AutoTextEditField _categoriesField;
   Location _location;
   boolean _result;
   boolean _isRoute;
   static LocationDocumentCollection _locationDocumentCollection = LocationDocumentCollection.getInstance();

   final boolean doModal() {
      Ui.getUiEngine().pushModalScreen(this);
      return this._result;
   }

   final void close(boolean result) {
      if (result) {
         result = this.onSave();
         if (!result) {
            return;
         }
      } else if (this.isDirty()) {
         int answer = this.onSavePromptInternal();
         if (answer == -1) {
            return;
         }

         result = answer == 1;
      }

      this._result = result;
      super.close();
   }

   final boolean validate() {
      String label = this._labelField.getText().trim();
      if (!label.equals(this._labelField.getText())) {
         this._labelField.setText(label);
      }

      if (label.length() == 0) {
         Dialog.alert(LBSResources.getString(142));
         return false;
      } else if (this._location._label != null && this._location.isSaved() && this._location._label.equals(label)) {
         return true;
      } else {
         int locationIndex = _locationDocumentCollection.getIndex(this._location._uid);
         int testIndex = _locationDocumentCollection.findElementByLabel(label);
         if (testIndex > -1 && testIndex != locationIndex) {
            Dialog.alert(LBSResources.getString(112));
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         ;
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(MenuItem.getPrefab(15));
      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean onSave() {
      if (!this.validate()) {
         return false;
      }

      this._location._label = this._labelField.getText();
      this._location._description = this._descriptionField.getText();
      this._location._address = this._addressField.getText();
      this._location._city = this._cityField.getText();
      this._location._region = this._regionField.getText();
      this._location._country = this._countryField.getText();
      this._location._postalCode = this._postalCodeField.getText();
      this._location._phone = this._phoneField.getText();
      this._location._fax = this._faxField.getText();
      this._location._url = this._urlField.getText();
      this._location._email = this._emailField.getText();
      this._location._categories = this._categoriesField.getText();
      return super.onSave();
   }

   @Override
   public final void save() {
      _locationDocumentCollection.addOrUpdate(this._location, null);
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

   private final String clipLength(String input, int maxLength) {
      if (input != null && input.length() > maxLength) {
         String output = input.substring(0, maxLength);
         return output;
      } else {
         return input;
      }
   }

   LocationEditScreen(Location location) {
      super(1153220623309406208L);
      this.setTitle(LBSResources.getString(421));
      this._location = location;
      this._isRoute = location instanceof Route;
      String label = location._label;
      if (label == null || label.equals("")) {
         label = _locationDocumentCollection.generateUniqueLabel();
      }

      label = this.clipLength(label, 2048);
      this._labelField = new AutoTextEditField(LBSResources.getString(98), label, 2048, 2147483648L);
      if (this._labelField.getTextLength() > 0) {
         this._labelField.setCursorPosition(this._labelField.getTextLength() - 1);
      } else {
         this._labelField.setCursorPosition(0);
      }

      this.add(this._labelField);
      if (!this._isRoute) {
         String description = location._description;
         description = this.clipLength(description, 2048);
         this._descriptionField = new AutoTextEditField(LBSResources.getString(99), description, 2048, 2147483648L);
         this.add(this._descriptionField);
         String address = location._address;
         address = this.clipLength(address, 2048);
         this._addressField = new AutoTextEditField(LBSResources.getString(207), address, 2048, 2147483648L);
         this.add(this._addressField);
         String city = location._city;
         city = this.clipLength(city, 2048);
         this._cityField = new AutoTextEditField(LBSResources.getString(210), city, 2048, 2147483648L);
         this.add(this._cityField);
         String region = location._region;
         region = this.clipLength(region, 2048);
         this._regionField = new AutoTextEditField(LBSResources.getString(215), region, 2048, 2147483648L);
         this.add(this._regionField);
         String country = location._country;
         country = this.clipLength(country, 2048);
         this._countryField = new AutoTextEditField(LBSResources.getString(212), country, 2048, 2147483648L);
         this.add(this._countryField);
         String postalCode = location._postalCode;
         postalCode = this.clipLength(postalCode, 32);
         this._postalCodeField = new EditField(LBSResources.getString(211), postalCode, 32, 2147483648L);
         this.add(this._postalCodeField);
         String phone = location._phone;
         phone = this.clipLength(phone, 32);
         this._phoneField = new EditField(LBSResources.getString(208), phone, 32, 2147483648L);
         this._phoneField.setFilter(TextFilter.get(6));
         this.add(this._phoneField);
         String fax = location._fax;
         fax = this.clipLength(fax, 32);
         this._faxField = new EditField(LBSResources.getString(209), fax, 32, 2147483648L);
         this._faxField.setFilter(TextFilter.get(6));
         this.add(this._faxField);
         String url = location._url;
         url = this.clipLength(url, 2048);
         this._urlField = new AutoTextEditField(LBSResources.getString(213), url, 2048, 2147483648L);
         this._urlField.setFilter(TextFilter.get(7));
         this.add(this._urlField);
         String email = location._email;
         email = this.clipLength(email, 2048);
         this._emailField = new AutoTextEditField(LBSResources.getString(214), email, 2048, 2147483648L);
         this._emailField.setFilter(TextFilter.get(8));
         this.add(this._emailField);
         String categories = location._categories;
         categories = this.clipLength(categories, 2048);
         this._categoriesField = new AutoTextEditField(LBSResources.getString(216), categories, 2048, 2147483648L);
         this.add(this._categoriesField);
      }

      this._labelField.setFocus();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.close(false);
            return true;
         case '\u001b':
            this.close(false);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return false;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.trackwheelClick(status, time);
   }
}
