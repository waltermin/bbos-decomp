package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.options.OptionsItemVerb;

public final class SpellCheckOptionsItem$NewCustomDictUnitVerb extends OptionsItemVerb {
   private SpellCheckOptionsItem$TransactedCustomDictionary _transactedCustomDict;

   public SpellCheckOptionsItem$NewCustomDictUnitVerb(String displayString, SpellCheckOptionsItem$TransactedCustomDictionary transactedCustomDict) {
      super(displayString, 628224);
      this._transactedCustomDict = transactedCustomDict;
   }

   @Override
   public final Object invoke(Object parameter) {
      CustomDictUnitEditor editor = new CustomDictUnitEditor(this._transactedCustomDict);
      return editor.open(SpellCheckOptionsItem._customDictScreen.getSearchPattern());
   }
}
