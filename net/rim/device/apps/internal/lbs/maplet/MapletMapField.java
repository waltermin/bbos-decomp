package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.lbs.CheckRadioConnections;
import net.rim.device.apps.internal.lbs.CheckRadioConnections$Callback;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.MapField;
import net.rim.device.apps.internal.lbs.protocol.MapRequest;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.render.RenderThread;
import net.rim.device.apps.internal.lbs.render.RenderThread$Listener;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class MapletMapField extends MapField implements Request$Listener, RenderThread$Listener, CheckRadioConnections$Callback {
   RenderThread _renderThread;
   OffscreenBuffer[] _offscreenBuffers = new OffscreenBuffer[2];
   int _frontBuffer;
   private boolean _immediateRedraw;
   private boolean _autoPanning = false;
   private boolean _showProgress = false;
   private boolean _updated;
   private boolean _dataMissing = false;
   private int _requestPID = -1;
   private MapletMapField$Timer _requestTimer = new MapletMapField$Timer(this, 250, 4000);
   private int _renderPID = -1;
   private MapletMapField$Timer _renderTimer = new MapletMapField$Timer(this, 250, 10000);
   private boolean _stageOne = true;
   private static final boolean DRAW_OFFSCREEN;
   private static int _requestCnt = 0;

   public final void doRenderComplete(boolean dataMissing) {
      if (dataMissing && this._updated) {
         MapRequest.request(this, super._rect, super._zoom);
         if (_requestCnt >= 0) {
            _requestCnt++;
         }
      }

      if (_requestCnt != 1 && !super.isSplashScreenOnShow()) {
         super.setFirstTimeLaunch(false);
         _requestCnt = -1;
      }

      this._updated = false;
      if (!this._stageOne) {
         this._frontBuffer = this._frontBuffer == 0 ? 1 : 0;
      }

      this.calculateDriverAssist();
      this._showProgress = false;
      super._progressField.setFooterVisible(false);
      super._progressField.reset(null, 0, 0, 0, true);
      this._renderTimer.cancel();
      this.invalidate();
   }

   public final void setCallback(boolean dataMissing) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void renderComplete(boolean dataMissing) {
      if (dataMissing && this._updated && RadioInfo.getActiveWAFs() == 0
         || !CoverageInfo.isCoverageSufficient(2) && !CoverageInfo.isCoverageSufficient(4) && !CoverageInfo.isCoverageSufficient(1)) {
         CheckRadioConnections checkRadio = new CheckRadioConnections(this);
         this.setCallback(dataMissing);
         MapRequest request = new MapRequest(this, super._rect, super._zoom);
         boolean dataOnSD = this.getMapletFromSDCard(super._rect, super._zoom);
         if (!dataOnSD) {
            checkRadio.checkRadioConnection(request);
         }

         if (this._autoPanning || dataOnSD) {
            this.doRenderComplete(dataMissing);
            return;
         }
      } else {
         this.doRenderComplete(dataMissing);
      }
   }

   @Override
   public final void radioChecked(boolean radioTurnedOn, boolean dataServicesEnabled, boolean outOfCoverage) {
      if (radioTurnedOn && !outOfCoverage && dataServicesEnabled) {
         this.doRenderComplete(this._dataMissing);
      }
   }

   @Override
   public final void mapStageOneComplete() {
      this._stageOne = true;
      Application.getApplication().invokeLater(new MapletMapField$1(this), 50, false);
   }

   @Override
   public final void mapComplete(boolean dataMissing) {
      this._stageOne = false;
      if (!dataMissing) {
         super._timer.dataRetrieved();
      }
   }

   @Override
   public final void requestComplete(Request request) {
      super._timer.endTimer(0);
      super._timer.dataRetrieved();
      if (this.handleErrorInternal(request)) {
         request._listener.setState(1);
         this.render();
         if (!super._progressField.isFooterVisible()) {
            super.setFirstTimeLaunch(false);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void progressTick(int current, int total) {
      String lbl = null;
      if (!super._progressField.isFooterVisible()) {
         super._progressField.setFooterVisible(true);
      }

      if (total >= 1024) {
         String KB = LBSResources.getString(289);
         lbl = ((StringBuffer)(new Object()))
            .append((int)(current / 1149239296))
            .append(KB)
            .append(" / ")
            .append((int)(total / 1149239296))
            .append(KB)
            .toString();
      } else {
         String Byte = LBSResources.getString(290);
         lbl = ((StringBuffer)(new Object())).append(current + 32).append(Byte).append(" / ").append(total).append(' ').append(Byte).toString();
      }

      try {
         super._progressField.reset(lbl, 0, current, total, false);
      } catch (Throwable var6) {
         EventLogger.logEvent(LBSApplication.UID, e.getMessage().getBytes(), 0);
         return;
      }
   }

   @Override
   public final void setState(int state) {
      switch (state) {
         case 1:
            super._progressField.setFooterVisible(this._showProgress);
            super._progressField.reset(LBSResources.getString(313), 0, 0, 10000, true);
            this._renderTimer.cancel();
            this._renderPID = UiApplication.getUiApplication().invokeLater(this._renderTimer, 250, true);
            this._renderTimer.setPID(this._renderPID);
            return;
         case 2:
         default:
            this._showProgress = true;
            super._progressField.setFooterVisible(this._showProgress);
            super._progressField.reset(LBSResources.getString(312), 0, 0, 4000, true);
            this._requestTimer.cancel();
            this._requestPID = UiApplication.getUiApplication().invokeLater(this._requestTimer, 250, true);
            this._requestTimer.setPID(this._requestPID);
            return;
         case 3:
            this._requestTimer.cancel();
         case 0:
      }
   }

   public MapletMapField() {
      this(18014398509481984L);
   }

   @Override
   public final void setAutoPan(boolean autoPan) {
      super.setAutoPan(autoPan);
      this._renderThread.setAutoPan(autoPan);
      this._autoPanning = autoPan;
   }

   @Override
   protected final void update(boolean immediateRedraw) {
      if (Application.getApplication().isForeground()) {
         super._timer.timingStart();
         super._timer.startTimer(0);
         this._updated = true;
         this.render();
         this._immediateRedraw = immediateRedraw;
      }
   }

   private final void render() {
      if (this._offscreenBuffers[0] != null) {
         int backBuffer = this._frontBuffer == 0 ? 1 : 0;
         this._offscreenBuffers[backBuffer]
            .render(this._renderThread, super._rect, super._lblRect, super._zoom, super._rotation, super._paddingTown, super._paddingShield);
      }
   }

   @Override
   protected final boolean isMapComplete() {
      return !this._renderThread.isMapIncomplete();
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._immediateRedraw && (this._renderThread.isMapReady() || this._stageOne)) {
         int backBuffer = this._frontBuffer == 0 ? 1 : 0;
         this._offscreenBuffers[backBuffer].blt(graphics, super._rect, super._zoom, this.getHeight());
      }

      this._offscreenBuffers[this._frontBuffer].blt(graphics, super._rect, super._zoom, this.getHeight());
      super.paint(graphics);
   }

   private final boolean getMapletFromSDCard(MapRect rect, int zoom) {
      MapRect mapletRect = MapRect.getMapletRect(super._rect, super._zoom);
      int mapletSize = Maplet.getMapletSize(super._zoom);

      for (int x = mapletRect._left; x < mapletRect._right; x += mapletSize) {
         for (int y = mapletRect._bottom; y < mapletRect._top; y += mapletSize) {
            if (x < -18000000) {
               x = -18000000;
            }

            if (y < -9000000) {
               y = -9000000;
            }

            int level = Maplet.getMapletLevel(super._zoom);
            Maplet maplet = MapletCache.getInstance().getMaplet(x, y, level);
            if (maplet == null && !MapletFile.getMaplet(x, y, level)) {
               return false;
            }
         }
      }

      return true;
   }

   public MapletMapField(long style) {
      super(style);
      this._renderThread = new RenderThread(this, super._transform);
      super._progressField = new MapletFooterProgressField(this);
   }

   private final void updateProgress(int pid, int value, int total) {
      if (pid == this._requestPID) {
         super._progressField.reset(LBSResources.getString(312), 0, value, total, true);
      } else {
         if (pid == this._renderPID) {
            super._progressField.reset(LBSResources.getString(313), 0, value, total, true);
         }
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (this._offscreenBuffers[0] == null) {
         this._offscreenBuffers[0] = new OffscreenBuffer(Display.getWidth(), Display.getHeight(), super._transform);
         this._offscreenBuffers[1] = new OffscreenBuffer(Display.getWidth(), Display.getHeight(), super._transform);
      }

      super.sublayout(width, height);
   }
}
