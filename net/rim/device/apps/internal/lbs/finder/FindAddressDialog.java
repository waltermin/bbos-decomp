package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.addressbook.mailingaddress.MailingAddressModelImpl;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class FindAddressDialog extends PopupScreen implements FieldChangeListener {
   private Object _context = new Object();
   private MailingAddressModel _model;
   private VerticalFieldManager _vfm;
   private ButtonField _searchButton;
   private ButtonField _cancelButton;

   FindAddressDialog(String title, boolean directions) {
      super((Manager)(new Object(1153220571769602048L)), 196608);
      this.add((Field)(new Object(title)));
      this.add((Field)(new Object()));
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(12884901888L));
      this.add(hfm);
      hfm.add(this._searchButton = (ButtonField)(new Object(LBSResources.getString(95))));
      this._searchButton.setChangeListener(this);
      hfm.add(this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042))));
      this._cancelButton.setChangeListener(this);
      this._model = new SearchAddressModel(null);
      this.setModel(this._model);
      this.setButtonEditable();
   }

   public final void setModel(Object model) {
      this._model = (MailingAddressModel)model;
      this.populateFields();
   }

   private final void populateFields() {
      FieldProvider fp = (FieldProvider)this._model;
      ContextObject context = ContextObject.castOrCreate(this._context);
      context.setFlag(0);
      if (this._vfm != null && this._vfm.getManager() != null) {
         this.delete(this._vfm);
         this._vfm = null;
      }

      this._vfm = (VerticalFieldManager)fp.getField(context);
      this._vfm.getField(0).setChangeListener(this);
      this._vfm.getField(1).setChangeListener(this);
      this._vfm.getField(2).setChangeListener(this);
      this._vfm.getField(3).setChangeListener(this);
      this.insert(this._vfm, 2);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.isModelValid()) {
         menu.add(new FindAddressDialog$1(this, LBSResources.getString(95), 0, 0));
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.focusAddressField();
         this.setButtonEditable();
      }
   }

   @Override
   public final boolean onSavePrompt() {
      return true;
   }

   public final MailingAddressModel get() {
      UiApplication.getUiApplication().invokeAndWait(new FindAddressDialog$2(this));
      return this._model;
   }

   private final boolean grabDataFromField() {
      if (!(this._model instanceof SearchAddressModel)) {
         return !(this._model instanceof Object) ? false : ((MailingAddressModelImpl)this._model).grabDataFromField(this._vfm, this._context);
      } else {
         return ((SearchAddressModel)this._model).grabDataFromField(this._vfm, this._context);
      }
   }

   private final boolean isModelValid() {
      return this._model.getAddressLine1() != null && !this._model.getAddressLine1().equals("")
         || this._model.getCity() != null && !this._model.getCity().equals("")
         || this._model.getArea() != null && !this._model.getArea().equals("")
         || this._model.getCountry() != null && !this._model.getCountry().equals("");
   }

   public final void close(boolean result) {
      if (result) {
         this._model = null;
      }

      super.close();
   }

   @Override
   public final void close() {
      this.close(true);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            Field f = this.getLeafFieldWithFocus();
            if (f == this._cancelButton) {
               f = this._cancelButton;
            } else {
               f = this._searchButton;
            }

            this.fieldChanged(f, 0);
            return true;
         case '\u001b':
            boolean result = super.keyChar(key, status, time);
            if (!result) {
               this.fieldChanged(this._cancelButton, 0);
               result = true;
            }

            return result;
         default:
            return super.keyChar(key, status, time);
      }
   }

   private final boolean handleClick() {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._searchButton) {
         field = this._searchButton;
         this.fieldChanged(field, 0);
         return true;
      } else if (field == this._cancelButton) {
         field = this._cancelButton;
         this.fieldChanged(field, 0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return !this.handleClick() ? super.navigationClick(status, time) : true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (!this.handleClick()) {
         this.onMenu(0);
      }

      return true;
   }

   private final void focusAddressField() {
      if (this._vfm != null) {
         this._vfm.getField(0).setFocus();
      }
   }

   private final void searchSelected() {
      this.grabDataFromField();
      if (!this.isModelValid()) {
         this.close(true);
      } else {
         this.close(false);
      }
   }

   private final void setButtonEditable() {
      this.grabDataFromField();
      if (this.isModelValid()) {
         this._searchButton.setEditable(true);
      } else {
         this._searchButton.setEditable(false);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         this.setButtonEditable();
         if (field == this._searchButton) {
            this.searchSelected();
         } else {
            if (field == this._cancelButton) {
               this.close(true);
            }
         }
      }
   }
}
