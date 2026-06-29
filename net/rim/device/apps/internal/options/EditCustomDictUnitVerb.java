package net.rim.device.apps.internal.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class EditCustomDictUnitVerb extends Verb {
   private CustomDictUnitModel _model;

   public EditCustomDictUnitVerb(CustomDictUnitModel model) {
      super(628480, CommonResource.getBundle(), 16);
      this._model = model;
   }

   @Override
   public final Object invoke(Object parameter) {
      return new CustomDictUnitEditor().open(this._model);
   }
}
