package net.rim.wica.runtime.metadata.internal.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.TextAreaControl;

public class TextAreaControlImpl extends EditControlImpl implements TextAreaControl {
   private int _visibleRows;

   public TextAreaControlImpl(
      int id, UIContainer parent, int style, int bits, int x, int y, int initId, Object inValue, int focusOut, int[] mapping, int visibleRows
   ) {
      super(id, 135, parent, style, bits, x, y, initId, inValue, 0, focusOut, mapping, null);
      this._visibleRows = visibleRows;
   }

   @Override
   public int getVisibleRows() {
      return this._visibleRows;
   }
}
