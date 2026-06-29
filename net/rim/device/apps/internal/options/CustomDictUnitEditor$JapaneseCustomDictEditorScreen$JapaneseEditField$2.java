package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.component.Menu;

class CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$2 implements Runnable {
   private final Menu val$menu;
   private final CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField this$2;

   CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField$2(CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField _1, Menu _2) {
      this.this$2 = _1;
      this.val$menu = _2;
   }

   @Override
   public void run() {
      try {
         this.val$menu.show();
         CustomDictUnitEditor$JapaneseCustomDictEditorScreen$JapaneseEditField.access$800(this.this$2, this.val$menu);
      } finally {
         ;
      }
   }
}
