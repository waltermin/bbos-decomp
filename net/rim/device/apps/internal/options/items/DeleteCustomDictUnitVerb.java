package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class DeleteCustomDictUnitVerb extends Verb {
   private CustomDictUnitModel _model;

   public DeleteCustomDictUnitVerb(CustomDictUnitModel model) {
      super(628992, CommonResource.getBundle(), 17);
      this._model = model;
   }

   @Override
   public final Object invoke(Object context) {
      String deleteString = (String)this._model.getEntry();
      String prompt = CommonResource.format(10025, deleteString);
      if (Dialog.ask(2, prompt, 3) == 3) {
         this._model.getCustomDictionary().remove(deleteString);
      }

      return null;
   }
}
