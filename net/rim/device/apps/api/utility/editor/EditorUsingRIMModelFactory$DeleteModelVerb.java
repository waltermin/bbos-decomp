package net.rim.device.apps.api.utility.editor;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.DeleteConfirmationProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class EditorUsingRIMModelFactory$DeleteModelVerb extends Verb {
   RIMModel _model;
   private final EditorUsingRIMModelFactory this$0;

   public EditorUsingRIMModelFactory$DeleteModelVerb(EditorUsingRIMModelFactory _1, RIMModel model) {
      super(16879616);
      this.this$0 = _1;
      this._model = model;
   }

   @Override
   public String toString() {
      return CommonResources.getString(600);
   }

   @Override
   public Object invoke(Object context) {
      boolean deleteModel = true;
      if (this._model instanceof DeleteConfirmationProvider) {
         DeleteConfirmationProvider dcp = (DeleteConfirmationProvider)this._model;
         if (dcp.confirmDelete(context)) {
            if (Dialog.ask(3, CommonResources.getString(3000)) == 4) {
               deleteModel = true;
            } else {
               deleteModel = false;
            }
         }
      }

      if (deleteModel) {
         this.this$0.deleteModel(this._model);
      }

      return null;
   }
}
