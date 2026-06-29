package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.MenuItem;

class CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$1 extends MenuItem {
   private final CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField this$2;

   CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$1(
      CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField _1, String x0, int x1, int x2
   ) {
      super(x0, x1, x2);
      this.this$2 = _1;
   }

   @Override
   public void run() {
      this.this$2._parent.accept(false);
   }
}
