package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SpellCheckOptionsItem$CustomDictSaveVerb extends Verb {
   private SpellCheckOptionsItem$TransactedCustomDictionary _transactedCustomDict;

   SpellCheckOptionsItem$CustomDictSaveVerb(SpellCheckOptionsItem$TransactedCustomDictionary transactedCustomDict) {
      super(268435456, CommonResource.getBundle(), 18);
      this._transactedCustomDict = transactedCustomDict;
   }

   @Override
   public final Object invoke(Object parm) {
      UiApplication app = UiApplication.getUiApplication();
      app.popScreen(app.getActiveScreen());
      this._transactedCustomDict.commit();
      SpellCheckOptionsItem.access$102(null);
      return null;
   }
}
