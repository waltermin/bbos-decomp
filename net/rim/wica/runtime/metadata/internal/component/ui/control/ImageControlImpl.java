package net.rim.wica.runtime.metadata.internal.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.ImageControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;

public class ImageControlImpl extends UIControlImpl implements ImageControl {
   private int _resId;

   public ImageControlImpl(int id, UIContainer parent, int style, int bits, int x, int y, int initId, Object inValue, int resId) {
      super(id, 133, parent, style, bits, x, y, initId, inValue);
      this._resId = resId;
   }

   @Override
   public int getResourceId() {
      return this._resId;
   }
}
