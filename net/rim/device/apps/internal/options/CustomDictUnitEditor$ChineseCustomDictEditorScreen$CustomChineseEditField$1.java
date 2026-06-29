package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.component.Dialog;

class CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField$1 implements Runnable {
   private final CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField this$2;

   CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField$1(CustomDictUnitEditor$ChineseCustomDictEditorScreen$CustomChineseEditField _1) {
      this.this$2 = _1;
   }

   @Override
   public void run() {
      Dialog.inform(OptionsResources.getString(1973));
      this.this$2.this$1._editorScreen.makeComposed();
   }
}
