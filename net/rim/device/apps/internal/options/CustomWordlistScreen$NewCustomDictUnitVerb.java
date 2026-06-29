package net.rim.device.apps.internal.options;

import net.rim.device.apps.api.options.OptionsItemVerb;

final class CustomWordlistScreen$NewCustomDictUnitVerb extends OptionsItemVerb {
   private String _newWordItem;
   private final CustomWordlistScreen this$0;

   public CustomWordlistScreen$NewCustomDictUnitVerb(CustomWordlistScreen _1, String displayString, String newWordItem) {
      super(displayString, 628224);
      this.this$0 = _1;
      this._newWordItem = newWordItem;
   }

   @Override
   public final Object invoke(Object parameter) {
      CustomDictUnitEditor editor = new CustomDictUnitEditor();
      if (this._newWordItem != null) {
         Object res = editor.open(this._newWordItem);
         this._newWordItem = null;
         return res;
      } else {
         return editor.open(CustomWordlistScreen._screenType == 2 ? "" : this.this$0._customDictScreen.getSearchPattern());
      }
   }
}
