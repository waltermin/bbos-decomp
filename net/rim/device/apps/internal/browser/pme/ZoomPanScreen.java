package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MediaField;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.MathUtilities;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.TextNodeImpl;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.VisualNodeImpl;
import net.rim.plazmic.internal.mediaengine.service.FocusInteractor;
import net.rim.plazmic.internal.mediaengine.service.MediaViewport;
import net.rim.plazmic.internal.mediaengine.ui.Pannable;
import net.rim.plazmic.internal.mediaengine.ui.Zoomable;
import net.rim.plazmic.mediaengine.MediaPlayer;

public final class ZoomPanScreen extends Screen {
   private MediaField _mediaField;
   private Object _model;
   private FocusInteractor _interactor;
   private ModelInteractorImpl _modelInteractor;
   private Pannable _pannable;
   private Zoomable _zoomable;
   private Manager _delegate;
   private int _currentState;
   private boolean _isZoomModified = false;
   private boolean _isTimerRunning = false;
   private int _invokeLaterId;
   private int _zoomAmount;
   private int _panXVelocity;
   private int _panYVelocity;
   private int _centreX;
   private int _centreY;
   XYEdges _panBounds;
   private Runnable _updateInvalidate;
   public String temp;
   int[] _magPointX = new int[4];
   int[] _magPointY = new int[4];
   private static int ZOOM_FACTOR;
   private static MediaPlayer _player;
   private static final int SCROLL_FACTOR = 12;
   private static final int MAX_ZOOM_AMOUNT = 589856;
   private static final int MIN_ZOOM_AMOUNT = 1;
   public static final int DEFAULT_ZOOM_FACTOR = 65536;
   private static final long MILLISEC_PER_FRAME = 41L;
   public static final int PAN_STATE = 0;
   public static final int ZOOM_STATE = 1;
   private static int _startingState;

   public ZoomPanScreen(long style, Field field, Object model, Object contentPMEPlayer) {
      super((Manager)(new Object(3458764513820540928L)), style | 68719476736L | 1152921504606846976L | 2305843009213693952L);
      this._mediaField = (MediaField)(new Object(style | 1152921504606846976L | 2305843009213693952L | 524288));
      this._delegate = this.getDelegate();
      this.add(this._mediaField);
      this._model = model;
      if (field instanceof Object) {
         this._zoomable = (Zoomable)field;
      } else {
         this._zoomable = null;
      }

      if (field instanceof Object) {
         this._pannable = (Pannable)field;
      } else {
         this._pannable = null;
      }

      this._panBounds = (XYEdges)(new Object(0, 0, 0, 0));
   }

   public final void setField(Field field, Object contentPMEPlayer) {
      if (field instanceof Object) {
         this._zoomable = (Zoomable)field;
      } else {
         this._zoomable = null;
      }

      if (field instanceof Object) {
         this._pannable = (Pannable)field;
      } else {
         this._pannable = null;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            this.doEscapeAction();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   private final void doEscapeAction() {
      this.resetPan();
      this.popScreen();
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this.initScreen();
      } else {
         if (this._interactor != null) {
            this._interactor.setFocusToItem(-1);
         }

         _player.stop();
         this.onUnfocus();
      }
   }

   private final void popScreen() {
      UiApplication.getUiApplication().popScreen(this);
   }

   @Override
   protected final void sublayout(int width, int height) {
      XYEdges border = (XYEdges)(new Object());
      this.getBorder(border);
      this.setPositionDelegate(this.getBorderLeft(), this.getBorderTop());
      int delegateWidth = width - this.getBorderLeft() - this.getBorderRight() - this.getMarginRight() - this.getMarginLeft();
      int delegateHeight = height - this.getBorderTop() - this.getBorderBottom() - this.getMarginTop() - this.getMarginBottom();
      this.layoutDelegate(delegateWidth, delegateHeight);
      XYRect fmExtent = this._delegate.getExtent();
      int newX = width - fmExtent.width - this.getBorderLeft() - this.getBorderRight() >> 1;
      int newY = height - fmExtent.height - this.getBorderTop() - this.getBorderBottom() >> 1;
      this.setPosition(newX, newY);
      this.setExtent(fmExtent.width + this.getBorderLeft() + this.getBorderRight(), fmExtent.height + this.getBorderTop() + this.getBorderBottom());
      if (this._zoomable != null) {
         Field f = (Field)this._zoomable;
         if (this._pannable != null) {
            f = (Field)this._pannable;
         }

         this._centreX = f.getWidth() >> 1;
         this._centreY = f.getHeight() >> 1;
         this._zoomable.setZoomOriginX(this._centreX << 16);
         this._zoomable.setZoomOriginY(this._centreY << 16);
      }
   }

   @Override
   public final boolean navigationMovement(int dx, int dy, int status, int time) {
      switch (this._currentState) {
         case -1:
            return super.navigationMovement(dx, dy, status, time);
         case 0:
         default:
            this.panField(dx, dy, status);
            return true;
         case 1:
            this.zoomField(dy, time);
            return true;
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.doEscapeAction();
      return true;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      switch (this._currentState) {
         case 0:
         default:
            if ((status & 1) != 0) {
               this.panField(amount, 0, status);
               return true;
            }

            this.panField(0, amount, status);
            return true;
         case 1:
            this.zoomField(amount, time);
         case -1:
            return true;
      }
   }

   private final void zoomField(int amount, int time) {
      if (this._zoomable != null) {
         int zoom = this._zoomable.getZoomAmount();
         int loop = Math.abs(amount);

         for (int i = 0; i < loop; i++) {
            if (amount >= 0) {
               ZOOM_FACTOR = zoom / 10;
               zoom += ZOOM_FACTOR;
            } else {
               ZOOM_FACTOR = zoom / 11;
               zoom -= ZOOM_FACTOR;
            }
         }

         this.setZoomAmount(zoom);
         if (this._pannable != null && this._isZoomModified) {
            MediaViewport _viewport = this._mediaField.getMediaViewport();
            int panX = this._pannable.getPanX();
            int panY = this._pannable.getPanY();
            panX += zoom * _viewport.getAlignX();
            panY += zoom * _viewport.getAlignY();
            int originX = this._zoomable.getZoomOriginX();
            int originY = this._zoomable.getZoomOriginY();
            int x = Fixed32.div(Fixed32.mul(originX, zoom) - panX, zoom);
            int y = Fixed32.div(Fixed32.mul(originY, zoom) - panY, zoom);
            int dZoom = amount * ZOOM_FACTOR;
            int dPanX = Fixed32.mul(originX - x, dZoom);
            int dPanY = Fixed32.mul(originY - y, dZoom);
            this._panXVelocity = Fixed32.toInt(dPanX - dZoom * _viewport.getAlignX());
            this._panYVelocity = Fixed32.toInt(dPanY - dZoom * _viewport.getAlignY());
         }
      }
   }

   private final void panField(int amountx, int amounty, int status) {
      this.changePanXVelocity(amountx);
      this.changePanYVelocity(amounty);
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return this.trackwheelClick(status, time);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void initScreen() {
      boolean var8 = false /* VF: Semaphore variable */;

      label71:
      try {
         var8 = true;
         _player.setMedia(this._model);
         _player.setUI(this._mediaField);
         _player.setMediaTime(0);
         var8 = false;
      } finally {
         if (var8) {
            System.out.println("Exception being thrown from initScreen");
            break label71;
         }
      }

      MediaServices services = (MediaServices)_player.getServices();
      this._interactor = (FocusInteractor)services.getService("FocusInteractor");
      this._modelInteractor = (ModelInteractorImpl)this._model;
      this._updateInvalidate = new ZoomPanScreen$1(this);
      this._panXVelocity = 0;
      this._panYVelocity = 0;
      if (this._zoomable != null) {
         this._zoomAmount = this._zoomable.getZoomAmount();
      }

      if (this._interactor != null) {
         this._currentState = _startingState;
         this._interactor.setDefaultItem(this._currentState);
      }

      if (this._currentState != 1) {
         if (this._currentState == 0) {
            VisualNodeImpl.setVisible(false, this._modelInteractor.getHandle("zoomGroup"), this._modelInteractor);
            VisualNodeImpl.setVisible(true, this._modelInteractor.getHandle("panGroup"), this._modelInteractor);
            int panX = -this._pannable.getPanX();
            int panY = -this._pannable.getPanY();
            if (this._zoomable != null) {
               panX = Fixed32.toInt(Fixed32.div(panX, this._zoomable.getZoomAmount()));
               panY = Fixed32.toInt(Fixed32.div(panY, this._zoomable.getZoomAmount()));
            } else {
               panX = Fixed32.toInt(panX);
               panY = Fixed32.toInt(panY);
            }

            this.temp = "";
            this.temp = ((StringBuffer)(new Object())).append(this.temp).append(panX).append(",").append(panY).toString();
            TextNodeImpl.setString(this.temp.toCharArray(), this._modelInteractor.getHandle("panText"), this._modelInteractor);
         }
      } else {
         VisualNodeImpl.setVisible(true, this._modelInteractor.getHandle("zoomGroup"), this._modelInteractor);
         VisualNodeImpl.setVisible(false, this._modelInteractor.getHandle("panGroup"), this._modelInteractor);
         this.temp = "";
         this.temp = ((StringBuffer)(new Object()))
            .append(this.temp)
            .append(Fixed32.toRoundedInt(Fixed32.div(Fixed32.mul(this._zoomAmount, 6553600), 65536)))
            .toString();
         this.temp = ((StringBuffer)(new Object())).append(this.temp).append("%").toString();
         TextNodeImpl.setString(this.temp.toCharArray(), this._modelInteractor.getHandle("zoomText"), this._modelInteractor);
      }

      try {
         _player.start();
      } finally {
         return;
      }
   }

   private final void updateFO() {
      if (this._currentState == 1) {
         this.temp = "";
         this.temp = ((StringBuffer)(new Object()))
            .append(this.temp)
            .append(Fixed32.toRoundedInt(Fixed32.div(Fixed32.mul(this._zoomable.getZoomAmount(), 6553600), 65536)))
            .append("%")
            .toString();
         TextNodeImpl.setString(this.temp.toCharArray(), this._modelInteractor.getHandle("zoomText"), this._modelInteractor);
      } else {
         if (this._currentState == 0) {
            int panX = -this._pannable.getPanX();
            int panY = -this._pannable.getPanY();
            if (this._zoomable != null) {
               panX = Fixed32.toInt(Fixed32.div(panX, this._zoomable.getZoomAmount()));
               panY = Fixed32.toInt(Fixed32.div(panY, this._zoomable.getZoomAmount()));
            } else {
               panX = Fixed32.toInt(panX);
               panY = Fixed32.toInt(panY);
            }

            this.temp = "";
            this.temp = ((StringBuffer)(new Object())).append(this.temp).append(panX).append(",").append(panY).toString();
            TextNodeImpl.setString(this.temp.toCharArray(), this._modelInteractor.getHandle("panText"), this._modelInteractor);
         }
      }
   }

   private final void updateScreen() {
      MediaField.updateScreen(this);
   }

   private final void updateField() {
      MediaViewport viewport = this._mediaField.getMediaViewport();
      if (viewport != null) {
         viewport.dirtyAll();
      }
   }

   public final void resetZoomPan() {
      Application app = Application.getApplication();
      if (this._isTimerRunning) {
         app.cancelInvokeLater(this._invokeLaterId);
         this._isTimerRunning = false;
      }

      this._zoomAmount = 65536;
      this._isZoomModified = true;
      this._panXVelocity = 0;
      this._panYVelocity = 0;
      if (this._pannable != null) {
         this._pannable.setPanX(0);
         this._pannable.setPanY(0);
      }

      if (this._zoomable != null) {
         this._zoomable.setZoomAmount(this._zoomAmount);
      }

      app.invokeLater(this._updateInvalidate);
      this.show();
      this.doEscapeAction();
   }

   public final void changePanXVelocity(int amount) {
      this._panXVelocity = -amount * 12;
      if (!this._isTimerRunning) {
         Application app = Application.getApplication();
         this._invokeLaterId = app.invokeLater(this._updateInvalidate, 41, true);
         this._isTimerRunning = true;
      }
   }

   public final void changePanYVelocity(int amount) {
      this._panYVelocity = -amount * 12;
      if (!this._isTimerRunning) {
         Application app = Application.getApplication();
         this._invokeLaterId = app.invokeLater(this._updateInvalidate, 41, true);
         this._isTimerRunning = true;
      }
   }

   public final void resetPan() {
      this._panXVelocity = 0;
      this._panYVelocity = 0;
      if (this._isTimerRunning) {
         Application app = Application.getApplication();
         app.cancelInvokeLater(this._invokeLaterId);
         this._isTimerRunning = false;
      }
   }

   private final void updatePanZoom() {
      if (this._pannable != null) {
         this._pannable.getPanBounds(this._panBounds);
      }

      if (this._isZoomModified) {
         this._zoomable.setZoomAmount(this._zoomAmount);
         this._zoomable.setZoomOriginX(this._centreX << 16);
         this._zoomable.setZoomOriginY(this._centreY << 16);
      }

      if (this._panXVelocity != 0 && this._pannable != null) {
         this._pannable.setPanX((this._panXVelocity << 16) + this._pannable.getPanX());
         this._panXVelocity = 0;
      }

      if (this._panYVelocity != 0 && this._pannable != null) {
         this._pannable.setPanY((this._panYVelocity << 16) + this._pannable.getPanY());
         this._panYVelocity = 0;
      }

      this._isZoomModified = false;
      if (this._panXVelocity == 0 && this._panYVelocity == 0 && this._isTimerRunning) {
         Application app = Application.getApplication();
         app.cancelInvokeLater(this._invokeLaterId);
         this._isTimerRunning = false;
      }
   }

   public final void setZoomAmount(int zoom) {
      int newZoom = MathUtilities.clamp(1, zoom, 589856);
      if (newZoom != this._zoomAmount) {
         this._zoomAmount = newZoom;
         this._isZoomModified = true;
      }

      if (!this._isTimerRunning) {
         Application app = Application.getApplication();
         this._invokeLaterId = app.invokeLater(this._updateInvalidate, 41, true);
         this._isTimerRunning = true;
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      this.paintChild(graphics, this._delegate);
      this.subpaint(graphics);
   }

   @Override
   protected final void subpaint(Graphics g) {
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
   }

   public final void show() {
      UiApplication.getUiApplication().pushScreen(this);
   }

   public final void setStartingState(int state) {
      _startingState = state;
   }

   static {
      if (_player == null) {
         _player = (MediaPlayer)(new Object());
      }
   }
}
