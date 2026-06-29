package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class SpellCheckOptionsItem$ClearCustomDictVerb extends Verb {
   private SpellCheckOptionsItem$TransactedCustomDictionary _transactedCustomDict;

   SpellCheckOptionsItem$ClearCustomDictVerb(SpellCheckOptionsItem$TransactedCustomDictionary transactedCustomDict) {
      super(629072, OptionsResources.getResourceBundle(), 1836);
      this._transactedCustomDict = transactedCustomDict;
   }

   @Override
   public final Object invoke(Object parm) {
      if (Dialog.ask(3, OptionsResources.getString(1836), -1) != -1) {
         this._transactedCustomDict.removeAll();
      }

      return null;
   }
}
