package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class EditAutoTextUnitVerb extends Verb {
   private AutoTextUnitModel _model;

   public EditAutoTextUnitVerb(AutoTextUnitModel model) {
      super(628480, CommonResource.getBundle(), 16);
      this._model = model;
   }

   @Override
   public final Object invoke(Object parameter) {
      AutoTextUnitEditor editor = new AutoTextUnitEditor();
      return editor.open(this._model);
   }
}
