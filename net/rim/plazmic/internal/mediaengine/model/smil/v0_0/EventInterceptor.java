package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimingObject;
import net.rim.plazmic.mediaengine.MediaListener;

public class EventInterceptor implements MediaListener {
   private RegionManager _regionManager;
   private MMSModel _model;

   public EventInterceptor(RegionManager regionManager, MMSModel model) {
      this._regionManager = regionManager;
      this._model = model;
   }

   @Override
   public void mediaEvent(Object sender, int type, int id, Object event) {
      if (type == 1) {
         TimingObject timingObject = this._model.getTimingObject(id);
         if (timingObject instanceof Object) {
            this._model.getSlideManager().setFocusToSlide(id);
         }
      }

      this._regionManager.mediaEvent(sender, type, id, event);
   }
}
