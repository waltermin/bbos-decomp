package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.apps.api.ui.ZoomBitmapField;

class SlideshowBitmapField extends ZoomBitmapField {
   private final boolean _isInitialRotated;

   SlideshowBitmapField(boolean isInitialRotated) {
      super(18014398509482020L);
      this._isInitialRotated = isInitialRotated;
      this.setFocusStyle(3);
      this.setEditable(true);
      this.setAddMIMEVerbs(false);
      this.setMinimumZoomFactorFlag(true);
      if (!Trackball.isSupported()) {
         this.setScrollStepSetX(DocViewGUIInternalConstants.SCROLLSTEP_X);
         this.setScrollStepSetY(DocViewGUIInternalConstants.SCROLLSTEP_Y);
      }
   }

   @Override
   public void setImage(EncodedImage image) {
      super.setImage(image);
      if (this._isInitialRotated) {
         this.rotate(90);
      }
   }

   @Override
   protected void trackballSensitivityChanged() {
      super.trackballSensitivityChanged();
      if (this.isFocus() && !this.isZooming()) {
         Screen scr = this.getScreen();
         if (scr != null && this.getTransformedHeight() <= this.getHeight() && this.getTransformedWidth() <= this.getWidth()) {
            scr.setTrackballSensitivityYOffset(38 - Trackball.getSensitivityYForSystem());
            scr.setTrackballFilter(-1);
         }
      }
   }
}
