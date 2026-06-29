package net.rim.wica.runtime.metadata.internal.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.SeparatorControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;

public class SeparatorControlImpl extends UIControlImpl implements SeparatorControl {
   private boolean _isWhiteSpace;

   public SeparatorControlImpl(int id, UIContainer parent, int style, int bits, int x, int y, int initId, boolean isWhiteSpace) {
      super(id, 132, parent, style, bits, x, y, initId, null);
      this._isWhiteSpace = isWhiteSpace;
   }

   @Override
   public boolean isWhitespace() {
      return this._isWhiteSpace;
   }
}
