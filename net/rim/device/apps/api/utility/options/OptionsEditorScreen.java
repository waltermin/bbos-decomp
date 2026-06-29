package net.rim.device.apps.api.utility.options;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.ModelUser;

public class OptionsEditorScreen extends EditorUsingRIMModelFactory implements ModelUser {
   private Verb _saveVerb = new OptionsEditorScreen$SaveOptionsVerb(this);
   private Object _model;
   Object _returnValue;

   public OptionsEditorScreen(Object context, String title, long factoryId, int separator) {
      super(context, title, factoryId, separator);
   }

   @Override
   public void setModel(Object model) {
      this._model = model;
      ContextObject.put(super._context, 254, this._model);
      this.setFocus();
      OptionsEditorScreen$DummyList dummyModel = new OptionsEditorScreen$DummyList();
      super.setModel(dummyModel);
   }

   @Override
   public Object getModel() {
      return this._model;
   }

   public Object run() {
      UiApplication.getUiApplication().pushScreen(this);
      return this._returnValue;
   }

   @Override
   protected void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(this._saveVerb);
      if (this.isMuddy()) {
         menu.setDefault(this._saveVerb);
      }
   }

   @Override
   protected boolean onSave() {
      if (this.validateDataFromEdit()) {
         this.grabDataFromEdit(true);
         return true;
      } else {
         return false;
      }
   }
}
