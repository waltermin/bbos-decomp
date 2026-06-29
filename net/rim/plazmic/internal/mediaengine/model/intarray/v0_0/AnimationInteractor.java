package net.rim.plazmic.internal.mediaengine.model.intarray.v0_0;

import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.event.EventSubscriptionHelper;
import net.rim.plazmic.internal.mediaengine.service.EventSubscription;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.mediaengine.MediaListener;

public class AnimationInteractor implements MediaService, EventSubscription, FocusInteractor, MediaListener {
   private EventSubscriptionHelper _subscription;
   private AnimationModel media;
   private boolean inFocus = false;
   private AnimationSampler sampler;
   protected MediaServices _services;

   public AnimationInteractor() {
      this._subscription = (EventSubscriptionHelper)(new Object());
   }

   @Override
   public void addListener(int event, int eventParam, MediaListener listener) {
      this._subscription.addListener(event, eventParam, listener);
   }

   @Override
   public void addListener(int event, MediaListener listener) {
      this._subscription.addListener(event, listener);
   }

   @Override
   public void addListener(MediaListener listener) {
      this._subscription.addListener(listener);
   }

   @Override
   public void removeListener(MediaListener listener) {
      this._subscription.removeListener(listener);
   }

   private final void fireMediaEvent(int event, int eventParam, Object data) {
      this.mediaEvent(this, event, eventParam, data);
   }

   @Override
   public void mediaEvent(Object sender, int event, int eventParam, Object data) {
      this._subscription.dispatchEvent(sender, event, eventParam, data);
   }

   @Override
   public synchronized void setMedia(Object media) {
      if (this.media != media) {
         if (this.media != null) {
            this.media.setMediaListener(null);
         }

         this.media = (AnimationModel)media;
         if (this.media != null) {
            this.media.setMediaListener(this);
            if (this.inFocus && !this.media.hasFocus()) {
               this.media.moveFocus(1, false);
            }
         }

         this.fireMediaEvent(21, -1, media);
      }
   }

   @Override
   public Object getMedia() {
      return this.media;
   }

   @Override
   public boolean activateItemInFocus() {
      boolean result = false;
      if (this.sampler != null) {
         result = this.sampler.postEvent(4, 0) == 0;
      }

      return result;
   }

   @Override
   public boolean keyChar(int key, int status) {
      return false;
   }

   @Override
   public void setFocusToItem(int id) {
      this.inFocus = id >= 0;
      if (this.sampler != null) {
         this.sampler.postEvent(5, id);
      }
   }

   @Override
   public void setDefaultItem(int id) {
   }

   @Override
   public int getFirstFocusableItem(int direction) {
      return this.media.getNextFocusableItem(direction, -1, false);
   }

   @Override
   public Object getWrappedObject(int index) {
      return null;
   }

   @Override
   public int getItemInFocus() {
      AnimationModel model = this.media;
      return model != null ? model.getItemInFocus() : -1;
   }

   @Override
   public int moveFocus(int direction, int offset, boolean wrap) {
      this.inFocus = true;
      if (this.sampler != null) {
         if (direction == -1) {
            offset = -offset;
         }

         offset = this.sampler.postEvent(3, offset << 1 | (wrap ? 1 : 0));
      }

      return offset;
   }

   @Override
   public int getNextFocusableItem(int direction, int curItem, boolean wrap) {
      AnimationModel model = this.media;
      return model == null ? curItem : model.getNextFocusableItem(direction, curItem, wrap);
   }

   @Override
   public int getItemCount() {
      AnimationModel model = this.media;
      return model == null ? 0 : model.getItemCount();
   }

   @Override
   public boolean hasFocus() {
      AnimationModel model = this.media;
      return this.inFocus && model != null && model.hasFocus();
   }

   @Override
   public void setServices(MediaServices s) {
      if (this._services != s) {
         this._services = s;
         if (this._services != null) {
            this._services.registerService("EventSubscription", this);
            this._services.registerService("FocusInteractor", this);
            this.sampler = (AnimationSampler)this._services.getService("Sampler");
         }
      }
   }

   @Override
   public void dispose() {
   }

   @Override
   public boolean getWrap() {
      return true;
   }

   @Override
   public void setWrap(boolean wrapFocus) {
   }
}
