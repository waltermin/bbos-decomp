package net.rim.wica.runtime.metadata.internal.component.ui.control;

import net.rim.wica.runtime.metadata.component.ui.UIContainer;
import net.rim.wica.runtime.metadata.component.ui.control.ButtonControl;
import net.rim.wica.runtime.metadata.internal.component.ui.UIControlImpl;

public class ButtonControlImpl extends UIControlImpl implements ButtonControl {
   private int _resourceId;
   private Object _imageValue;
   private Object _imageInValue;

   public ButtonControlImpl(
      int id, UIContainer parent, int style, int bits, int x, int y, int initId, Object inValue, int clickId, int resourceId, Object imageInValue
   ) {
      super(id, 131, parent, style, bits, x, y, initId, inValue);
      this._resourceId = resourceId;
      this._imageInValue = imageInValue;
      this.setEvent(2, clickId);
   }

   @Override
   public void onClick() {
      this.eventOccurred(2);
   }

   @Override
   public int getResourceId() {
      return this._resourceId;
   }

   @Override
   public Object getImageValue() {
      return this._imageValue;
   }

   @Override
   protected void reset() {
      this._imageValue = null;
      super.reset();
   }

   @Override
   protected void resolveInValue() {
      if (this._imageInValue != null) {
         this._imageValue = this.resolveInValue(this._imageInValue, super._valueType);
      }

      super.resolveInValue();
   }
}
