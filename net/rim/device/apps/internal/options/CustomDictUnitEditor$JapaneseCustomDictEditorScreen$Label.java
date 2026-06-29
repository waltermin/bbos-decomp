package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.component.LabelField;

class CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label extends LabelField {
   int _width;
   private final CustomDictUnitEditor$JapaneseCustomDictEditorScreen this$1;

   public CustomDictUnitEditor$JapaneseCustomDictEditorScreen$Label(CustomDictUnitEditor$JapaneseCustomDictEditorScreen _1, String label, long style) {
      super(label, style);
      this.this$1 = _1;
   }

   public void setWidth(int width) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public int getPreferredWidth() {
      return this._width;
   }
}
