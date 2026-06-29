package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.SlideManager;

public class MMSModel extends SMILModel {
   private SlideManager _slideManager;

   public SlideManager getSlideManager() {
      return this._slideManager;
   }

   public void setSlideManager(SlideManager slideManager) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
