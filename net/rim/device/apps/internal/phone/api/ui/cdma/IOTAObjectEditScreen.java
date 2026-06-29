package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class IOTAObjectEditScreen extends MainScreen {
   private IOTAEditScreen _screen;
   private String _oldObject;
   private String _oldValue;
   private EditField _newObject;
   private EditField _newValue;

   public IOTAObjectEditScreen(IOTAEditScreen screen, String oldObject, String oldValue) {
      super(196608);
      this._screen = screen;
      this.setTitle("Object Editor");
      this._oldObject = oldObject;
      this._oldValue = oldValue;
      this._newObject = (EditField)(new Object("Object: ", this._oldObject));
      this._newObject.setDirty(false);
      this._newValue = (EditField)(new Object("Value: ", this._oldValue));
      this._newValue.setDirty(false);
      this.add(this._newObject);
      this.add(this._newValue);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new IOTAObjectEditScreen$SaveItem(this, "Save"));
   }

   @Override
   protected final boolean onSave() {
      if (this._newValue.isDirty() || this._newObject.isDirty()) {
         this._screen.removeOldValues(this._oldObject);
         this._screen.addNewValues(this._newObject.getText(), this._newValue.getText());
      }

      return super.onSave();
   }
}
