package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.internal.lbs.Timing;
import net.rim.device.apps.internal.lbs.Transform;
import net.rim.device.apps.internal.lbs.maplet.MapRect;

public final class RenderThread extends Thread {
   MapRender _mapRender = new MapRender();
   LabelRender _labelRender = new LabelRender();
   Timing _timer = new Timing();
   Graphics _graphics;
   MapRect _rect;
   MapRect _lblRect;
   int _zoom;
   int _rotation;
   int _paddingTown;
   int _paddingShield;
   boolean _dataMissing;
   private boolean _autoPan = false;
   private boolean _stop = false;
   boolean _mapReady;
   boolean _pending = false;
   Transform _transform;
   RenderThread$Listener _listener;
   private boolean _enablePR = true;
   private boolean _incomplete = false;

   public RenderThread(RenderThread$Listener listener, Transform transform) {
      this._listener = listener;
      this._transform = transform;
      this._labelRender.setTransform(transform);
      this.start();
   }

   public final void render(Graphics graphics, MapRect rect, MapRect lblRect, int zoom, int rotation, int paddingTown, int paddingShield) {
      synchronized (this) {
         if (!this._autoPan || !this._pending) {
            this._pending = true;
            this._graphics = graphics;
            this._rect = rect;
            this._lblRect = lblRect;
            this._zoom = zoom;
            this._rotation = rotation;
            this._paddingTown = paddingTown;
            this._paddingShield = paddingShield;
            this._mapReady = false;
            this.notify();
         }
      }
   }

   public final boolean isPending() {
      synchronized (this) {
         return this._pending;
      }
   }

   public final void setAutoPan(boolean autoPan) {
      if (this._enablePR) {
         this._autoPan = autoPan;
         this._mapRender.setAutoPan(autoPan);
      }
   }

   public final boolean isMapReady() {
      return this._mapReady;
   }

   public final void dataMissing() {
      this._dataMissing = true;
   }

   public final boolean isMapIncomplete() {
      return this._incomplete;
   }

   protected final void renderAborted() {
      this._mapRender.setPhase(true);
   }

   public final void renderImmediate(Graphics graphics, MapRect rect, MapRect lblRect, int zoom, int rotation, int paddingTown, int paddingShield) {
      if (!this._autoPan) {
         synchronized (this) {
            if (this._pending) {
               return;
            }
         }
      }

      this._dataMissing = false;
      this._timer.startTimer(1);
      this._incomplete = true;
      this._mapRender.render(this, graphics, rect, zoom, rotation);
      this._incomplete = false;
      this._timer.endTimer(1);
      if (this._autoPan || !this.isPending()) {
         if (this._enablePR && this._mapRender.isPhaseOne() && !this._mapRender.isAutoPan()) {
            this._mapRender.setPhase(false);
            this._listener.mapStageOneComplete();
         } else {
            this._mapReady = true;
            this._mapRender.setPhase(true);
            this._listener.mapComplete(this._dataMissing);
            this._labelRender.render(this, graphics, rect, zoom, rotation, paddingTown, paddingShield);
         }
      }
   }

   @Override
   public final void run() {
      while (!this._stop) {
         synchronized (this) {
            label62:
            if (!this._pending) {
               try {
                  this.wait();
                  if (!this.isInterrupted()) {
                     break label62;
                  }
               } finally {
                  break label62;
               }

               return;
            }

            this._pending = false;
         }

         this.renderImmediate(this._graphics, this._rect, this._lblRect, this._zoom, this._rotation, this._paddingTown, this._paddingShield);
         synchronized (this) {
            if (!this._pending) {
               this._listener.renderComplete(this._dataMissing);
            }
         }
      }
   }

   public final boolean isInterrupted() {
      return this._stop;
   }

   @Override
   public final void interrupt() {
      this._stop = true;
      synchronized (this) {
         this.notify();
      }
   }
}
