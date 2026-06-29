package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.ui.SlideManager;

public class MMSNextSlideVerb extends Verb {
   private SlideManager _slideManager;

   public MMSNextSlideVerb(SlideManager sm) {
      super(472340, 8432718016989017157L, "net.rim.device.apps.internal.resource.MMS", 107);
      this._slideManager = sm;
   }

   @Override
   public Object invoke(Object context) {
      this._slideManager.nextSlide();
      return null;
   }
}
