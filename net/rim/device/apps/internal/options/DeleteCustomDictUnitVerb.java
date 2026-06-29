package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public class DeleteCustomDictUnitVerb extends Verb {
   protected CustomDictUnitModel _model;

   public DeleteCustomDictUnitVerb(CustomDictUnitModel model) {
      super(628992, CommonResource.getBundle(), 17);
      this._model = model;
   }

   @Override
   public Object invoke(Object context) {
      Object deleteObject = this._model.getEntry();
      String prompt = CommonResource.format(10025, deleteObject.toString());
      if (Dialog.ask(2, prompt, 3) == 3) {
         CustomWordlistScreen.getCustomDictionary().remove(deleteObject);
      }

      return null;
   }
}
