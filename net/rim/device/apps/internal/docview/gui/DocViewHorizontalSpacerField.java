package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.component.HorizontalSpacerField;

final class DocViewHorizontalSpacerField extends HorizontalSpacerField {
   private int _width;

   DocViewHorizontalSpacerField(int width) {
      super(width);
      this._width = width;
   }

   @Override
   public final int getPreferredWidth() {
      return this._width;
   }
}
