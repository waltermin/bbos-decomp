package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public final class DeleteAutoTextUnitVerb extends Verb {
   private AutoTextUnitModel _model;
   private AutoText _autoTextEngine;

   public DeleteAutoTextUnitVerb(AutoTextUnitModel model) {
      super(628992, CommonResource.getBundle(), 17);
      this._model = model;
      this._autoTextEngine = AutoText.getAutoText();
   }

   @Override
   public final Object invoke(Object context) {
      String replacedString = this._autoTextEngine.getReplacedString(this._model.getEntry());
      String prompt = CommonResource.format(10025, replacedString);
      if (Dialog.ask(2, prompt, 3) == 3) {
         Locale locale = Locale.get(this._autoTextEngine.getLocaleCode(this._model.getEntry()));
         this._autoTextEngine.remove(replacedString, locale);
         if (ContextObject.getFlag(context, 0)) {
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         }
      }

      return null;
   }
}
