package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.SlideManager;

public class MMSPrevSlideVerb extends Verb {
   private SlideManager _slideManager;

   public MMSPrevSlideVerb(SlideManager sm) {
      super(472350, 8432718016989017157L, "net.rim.device.apps.internal.resource.MMS", 110);
      this._slideManager = sm;
   }

   @Override
   public Object invoke(Object context) {
      this._slideManager.prevSlide();
      return null;
   }
}
