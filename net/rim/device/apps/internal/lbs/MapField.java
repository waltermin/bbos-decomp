package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.lbs.hints.GenericHint;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.maplet.MapletCache;
import net.rim.device.apps.internal.lbs.maplet.MapletFooterProgressField;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.Cursor;

public class MapField extends Manager {
   public boolean _pushDirectionsScreen = false;
   public boolean _pointerMode = false;
   private Cursor _cursor;
   private int _cursorX;
   private int _cursorY;
   private boolean _toolbarBottom = true;
   private XYRect _toolbarBottomRect;
   protected int _width;
   protected int _height;
   protected int _latitude;
   protected int _longitude;
   protected int _zoom;
   protected int _rotation;
   protected int _paddingTown = 10;
   protected int _paddingShield = 7;
   protected Route _currentRoute = null;
   protected RoutePlanner _routePlanner = null;
   protected int _lastRollAmountY = 0;
   protected int _lastRollAmountX = 0;
   protected int _searchLatitude;
   protected int _searchLongitude;
   protected boolean _showTitleField = false;
   protected DriverAssist _driverAssist;
   private DriverAssist _pointerAssist;
   protected MapRect _rect = new MapRect();
   protected MapRect _lblRect = new MapRect();
   protected MapRect _expandedRect = new MapRect();
   protected Transform _transform = new Transform();
   protected Timing _timer = new Timing();
   boolean _zoomMode = false;
   boolean _rotateMode = false;
   boolean _dimImage;
   private boolean _isFirstTimeLaunch = false;
   private boolean _isSplahOnShow = false;
   CurrentLocations _currentLocations = new CurrentLocations(this);
   Location[] _currentPOIs = new Location[0];
   Location[] _currentAds = new Location[0];
   String[] _currentLegalNotices = new Object[0];
   String _lbsDocSource = null;
   Location _marker = new Location();
   Decision _decisionMarker = new Decision();
   MapPoint _ptCrosshair = new MapPoint();
   MapPoint _ptMarkerNew = new MapPoint();
   MapPoint _ptMarkerOld;
   public boolean _showCaption = false;
   public XYRect _bbox = (XYRect)(new Object());
   public String _poiKeywords;
   private boolean _paintZoom = false;
   private int _paintZoomPID = -1;
   private MapField$PaintZoomRunnable _paintZoomRunnable = new MapField$PaintZoomRunnable(this);
   private int _zoomUpdatePID = -1;
   private MapField$ZoomUpdateRunnable _zoomUpdateRunnable = new MapField$ZoomUpdateRunnable(this);
   private GenericHint _genericHint;
   protected MapletFooterProgressField _progressField;
   private int _yOffset = 0;
   protected boolean _autoPan = false;
   MapScreen _screen;
   MapField$MapFieldLocationDocumentConverter _documentConverter = new MapField$MapFieldLocationDocumentConverter(this, this);
   private String _currentStreet;
   private String _nextStreet;
   private Transform _overlayTransform = new Transform();
   private boolean _pointerPanned = false;
   private int _updatePID;
   private MapField$UpdateRunnable _updateRunnable = new MapField$UpdateRunnable(this);
   Screen _lastUsedScreen;
   DirectionsListScreen _directionsListScreen;
   LocationsListScreen _locationsListScreen;
   private boolean _userPromptedMapsUnavailable;
   private LBSMenuItem[] _menuItems = new LBSMenuItem[0];
   private RRoute _recordingRoute;
   protected static long UID = -1037010874164756539L;
   public static final int MOVE_UP = 90;
   public static final int MOVE_RIGHT = 0;
   public static final int MOVE_DOWN = 270;
   public static final int MOVE_LEFT = 180;
   protected static final int HINT_NEXT_PREVIOUS = 0;
   protected static final int HINT_NEXT = 1;
   protected static final int HINT_PREVIOUS = -1;
   static final int _miToFt = 5280;
   static final float _mToFt = 3.28F;
   static final double RADS = Math.PI / 180.0;
   static final int[] _kmValues = new int[]{
      50,
      100,
      200,
      500,
      1000,
      2000,
      4000,
      10000,
      15000,
      30000,
      60000,
      100000,
      200000,
      500000,
      1000000,
      2000000,
      5000000,
      51,
      -804650991,
      100,
      200,
      500,
      2000,
      5280,
      10560,
      10560,
      26400,
      52800,
      79200,
      132000,
      264000,
      528000,
      1320000,
      2640000,
      5280000,
      10560000,
      426115328,
      1929445485,
      1929445418,
      7618858,
      -1910540799,
      1979777154,
      729976351,
      12561511,
      -1910540799,
      10438786,
      -1910540799,
      452738,
      1802466817,
      1979777125,
      1097165679,
      1864803,
      1802466817,
      1684947301,
      221527,
      1802466817,
      1886404965,
      1453441741,
      16802315,
      1701539702,
      725324,
      1802466817,
      185683045,
      1846244392,
      -1258225633,
      -1258225467,
      67160261,
      -2107046912
   };
   static final int[] _miValues = new int[]{
      100,
      200,
      500,
      2000,
      5280,
      10560,
      10560,
      26400,
      52800,
      79200,
      132000,
      264000,
      528000,
      1320000,
      2640000,
      5280000,
      10560000,
      426115328,
      1929445485,
      1929445418,
      7618858,
      -1910540799,
      1979777154,
      729976351,
      12561511,
      -1910540799,
      10438786,
      -1910540799,
      452738,
      1802466817,
      1979777125,
      1097165679,
      1864803,
      1802466817,
      1684947301,
      221527,
      1802466817,
      1886404965,
      1453441741,
      16802315,
      1701539702,
      725324,
      1802466817,
      185683045,
      1846244392,
      -1258225633,
      -1258225467,
      67160261,
      -2107046912,
      100671136,
      1350115355,
      -1969327349,
      -1016438193,
      40633856,
      1812332645,
      1866884354,
      100722284,
      1399128684,
      1695315181,
      1957496320,
      134251193,
      2032535552,
      638058622,
      671613058,
      467227,
      1064314888,
      -1117257139,
      1702195616
   };
   static final Font _labelFont = FontRegistry.get("labelfont").getFont(0, 25);
   static Bitmap _splashScreen = Bitmap.getBitmapResource("SplashScreenColor.png");
   private static final NorthArrow _northArrow = new NorthArrow();
   static int accuX = 0;
   static int accuY = 0;
   private static final int PAN_INCREMENT = 16;
   private static final int KEY_PAN_INCREMENT = 32;

   protected void showHintLabel(String hint) {
      this._genericHint.show(hint);
   }

   protected void cancelHint() {
      this._genericHint.cancel();
   }

   public void setAutoPan(boolean autoPan) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected static int minZoom() {
      return 0;
   }

   protected static int maxZoom() {
      return 15;
   }

   protected void setZoom(int zoom) {
      if (zoom <= maxZoom() && zoom >= minZoom()) {
         this._zoom = zoom;
         this._marker._zoom = zoom;
         this.updateScreenPosition();
         this._transform.clone(this._overlayTransform);
      }
   }

   protected int getRotation() {
      return this._rotation;
   }

   protected void showPOIDirHintLable(int type, boolean isPOI) {
      String next = "";
      String prev = "";
      if (!isReducedKeyboard()) {
         next = "N";
         prev = "P";
      } else {
         next = "6";
         prev = "4";
      }

      String msg = null;
      String msgNext = null;
      String msgPrev = null;
      if (isPOI) {
         msgNext = LBSResources.getString(408);
         msgPrev = LBSResources.getString(409);
      } else {
         msgNext = LBSResources.getString(408);
         msgPrev = LBSResources.getString(409);
      }

      msgNext = MessageFormat.format(msgNext, new Object[]{next});
      msgPrev = MessageFormat.format(msgPrev, new Object[]{prev});
      switch (type) {
         case -2:
            break;
         case -1:
            msg = msgPrev;
            break;
         case 0:
         default:
            msg = ((StringBuffer)(new Object())).append(msgPrev).append(" ").append(msgNext).toString();
            break;
         case 1:
            msg = msgNext;
      }

      this.showHintLabel(msg);
      if (this._screen._following && this._screen._gpsLock) {
         this._screen._following = false;
         this.setAutoPan(false);
         Application.getApplication().invokeLater(new MapField$1(this), 5000, false);
      }
   }

   protected void setRotation(int rotation) {
      if (this._zoom >= 9) {
         rotation = 0;
      }

      while (rotation > 359) {
         rotation -= 360;
      }

      while (rotation < 0) {
         rotation += 360;
      }

      if (this._rotation != rotation) {
         this._rotation = rotation;
         this._transform.update(this._rect, this._zoom, this._rotation);
         this._lblRect = this._transform._cropView;
      }
   }

   protected int getLatitude() {
      return this._latitude;
   }

   protected int getLongitude() {
      return this._longitude;
   }

   protected int getZoom() {
      return this._zoom;
   }

   ContextObject createContext() {
      ContextObject context = (ContextObject)(new Object());
      ContextObject.put(context, -200747095229876690L, new Object(this._latitude));
      ContextObject.put(context, 6606581876924152793L, new Object(this._longitude));
      ContextObject.put(context, 581052036187634982L, new Object(this._zoom));
      ContextObject.put(context, 8035222542232379495L, new Object(this._rotation));
      return context;
   }

   protected void update(boolean _1) {
      throw null;
   }

   public MapField(long style) {
      super(style);
      this.createMenuItems();
      this._transform.setScreenExtent(Display.getWidth(), Display.getHeight());
      this._cursor = Cursor.getPredefinedCursor(0);
      this._cursorX = Display.getWidth() / 2;
      this._cursorY = Display.getHeight() / 2;
      this._genericHint = new GenericHint(this);
      this._yOffset = 50 - Trackball.getSensitivityYForSystem();
      if (this._yOffset > 0) {
         this._yOffset = 0;
      }

      if (this._yOffset < -100) {
         this._yOffset = -100;
      }
   }

   void optionsChanged() {
   }

   void openDocument(String contentType, Object content, int uid, boolean zoomToFit, String source) {
      this._bbox = null;
      this._lbsDocSource = source;
      this._documentConverter.parse(contentType, content);
      this._lbsDocSource = null;
      if (!zoomToFit) {
         this.invalidate();
      } else {
         int screenHeight = -this._transform._screenView.height();
         int height = screenHeight;
         int width = this._width;
         int zoom = 0;
         Location focus = this._currentLocations.getFocus();
         if (focus != null) {
            zoom = MathUtilities.clamp(minZoom(), focus._zoom, maxZoom());
         }

         if (this._bbox != null) {
            while (zoom < 15 && (width << zoom < this._bbox.width || height << zoom < this._bbox.height)) {
               zoom++;
            }

            this._longitude = this._bbox.x + this._bbox.width / 2;
            this._latitude = this._bbox.y + this._bbox.height / 2;
         }

         this.setZoom(zoom);
         this.updateScreenPosition();
         this.update(true);
      }
   }

   protected void calculateDriverAssist() {
      this._timer.startTimer(5);
      GPSMode gpsMode = (GPSMode)this._screen._dashboard.getMode();
      if (this._zoom <= 11 && this._screen._tracking) {
         if (this._latitude == this._marker._latitude && this._longitude == this._marker._longitude) {
            this._driverAssist.clear();
            this._driverAssist
               .populateMap(MapletCache.getInstance().getMaplets(this._rect, this._zoom), this._rect, this._zoom, this._rotation, this._transform);
            this._currentStreet = this._driverAssist.findCurrentPath(this._marker._latitude, this._marker._longitude, this._zoom, this._transform);
            this._nextStreet = this._driverAssist.findNextStreet(this._screen._trackUp ? 0 : this._screen._currentBearing);
            if (gpsMode != null) {
               if (this._nextStreet != null && this._nextStreet != "") {
                  gpsMode.setMessage(((StringBuffer)(new Object("+ "))).append(this._nextStreet).toString(), 2);
               } else if (this._currentStreet != null && this._currentStreet != "") {
                  gpsMode.setMessage(this._currentStreet, 1);
               } else {
                  gpsMode.setMessage("", 1);
               }

               this._screen._dashboard.refresh();
            }
         }
      } else if (gpsMode != null) {
         gpsMode.setMessage("", 3);
         this._screen._dashboard.refresh();
      }

      if (this._zoom > 11 && this._screen._tracking) {
         gpsMode.setMessage(LBSResources.getString(140), 3);
         this._screen._dashboard.refresh();
      }

      this._timer.endTimer(5);
   }

   public void invalidateHint() {
      int x = this.getWidth() - this._genericHint.getWidth();
      int y = this.getHeight() - this._genericHint.getHeight() - this._progressField.getHeight() - 3;
      this.invalidate(x, y, this._genericHint.getWidth(), this._genericHint.getHeight());
   }

   public void invalidateProgressField() {
      if (this._isFirstTimeLaunch) {
         this.invalidate(0, 0, this.getWidth(), this.getHeight());
      } else {
         this.invalidate(
            this.getWidth() - this._progressField.getWidth(),
            this.getHeight() - this._progressField.getHeight(),
            this._progressField.getWidth(),
            this._progressField.getHeight()
         );
         this._isFirstTimeLaunch = false;
      }
   }

   @Override
   protected void sublayout(int width, int height) {
      this.setExtent(width, height);
      if (this._driverAssist == null || height != this._height || width != this._width) {
         this._driverAssist = new DriverAssist(width, height);
      }

      if (this._pointerAssist == null || height != this._height || width != this._width) {
         this._pointerAssist = new DriverAssist(width, height, (byte)1, true);
      }

      this._width = width;
      this._height = height;
      _northArrow.setFieldSize(this._width, height);
      this._transform._screenAnchor._y = height >> 1;
      this._transform.setYOffset(Display.getHeight() - height >> 1);
      this._transform._screenView._yOffset = this._transform.getYOffset();
      this._genericHint.layout(this._genericHint.getPreferredWidth(), this._genericHint.getPreferredHeight());
      this._progressField.layout(this._progressField.getPreferredWidth(), this._progressField.getPreferredHeight());
      this._transform.clone(this._overlayTransform);
   }

   void paintZoom(Graphics graphics) {
      int numLevels = 16;
      int zoom = this.getZoom();
      int screenHeight = this.getHeight();
      int width = 13;
      int tickHeight = 5;
      int spacerHeight = 5;
      int height = 2 * width + numLevels * tickHeight + (numLevels + 1) * spacerHeight;

      while (height > screenHeight && spacerHeight > 1) {
         height = 2 * width + numLevels * tickHeight + (numLevels + 1) * --spacerHeight;
      }

      if (height > screenHeight) {
         tickHeight = 3;
         spacerHeight = 3;
         height = 2 * width + numLevels * tickHeight + (numLevels + 1) * spacerHeight;

         while (height > screenHeight && spacerHeight > 1) {
            height = 2 * width + numLevels * tickHeight + (numLevels + 1) * --spacerHeight;
         }
      }

      int top = (screenHeight - height) / 2;
      int left = 5;
      graphics.setColor(0);
      graphics.fillRect(left, top, width, width);
      graphics.fillRect(left, top + height - width, width, width);
      graphics.setColor(16777215);
      graphics.fillRect(left + 1, top + 1, width - 2, width - 2);
      graphics.fillRect(left + 1, top + height - width + 1, width - 2, width - 2);
      int y = top + width + spacerHeight;

      for (int i = 0; i < 16; i++) {
         graphics.fillRect(left, y, width, 5);
         y += tickHeight + spacerHeight;
      }

      graphics.setColor(0);
      y = top + width + spacerHeight + 1;
      int x = left + 1;

      for (int i = 0; i < 16; i++) {
         graphics.fillRect(x, y, width - 2, 3);
         y += tickHeight + spacerHeight;
      }

      y = top + width / 2;
      graphics.drawLine(left + 3, y, left + width - 4, y);
      x = left + width / 2;
      graphics.drawLine(x, top + 3, x, top + width - 4);
      y = top + height - 6;
      graphics.drawLine(left + 3, y, left + width - 4, y);
      graphics.fillRect(left + width / 2 - 2, top + width, 5, height - 2 * width);
      graphics.setColor(16777215);
      graphics.fillRect(left + width / 2 - 1, top + width, 3, height - 2 * width);
      y = top + width + spacerHeight + zoom * (tickHeight + spacerHeight);
      graphics.fillRect(left, y, width, 5);
      y++;
      graphics.setColor(8900331);
      graphics.fillRect(left + 1, y, width - 2, 3);
      x = left + width + 5;
      graphics.setFont(_labelFont);
      graphics.drawText(((StringBuffer)(new Object(""))).append(zoom).toString(), x, top);
      x += 5;
      y = top + height - 16;
      double cos = Math.cos(this._latitude / 100000 * 4580687790476533049L);
      int kmMarker = (int)(_kmValues[zoom] / ((1 << zoom) * cos));
      int miMarker = (int)(_miValues[zoom] / 1079110533 / ((1 << zoom) * cos));
      int length = Math.max(kmMarker, miMarker);
      graphics.setColor(16777215);
      graphics.setStrokeWidth(4);
      graphics.drawPathOutline(new int[]{x, length + x + 1}, new int[]{y, y}, null, null, false);
      graphics.drawPathOutline(new int[]{x, x}, new int[]{y - 11, y + 11}, null, null, false);
      graphics.drawPathOutline(new int[]{kmMarker + x, kmMarker + x}, new int[]{y - 11, y}, null, null, false);
      graphics.drawPathOutline(new int[]{miMarker + x, miMarker + x}, new int[]{y + 11, y}, null, null, false);
      graphics.setColor(0);
      graphics.setStrokeWidth(2);
      graphics.drawPathOutline(new int[]{x, length + x}, new int[]{y, y}, null, null, false);
      graphics.drawPathOutline(new int[]{x, x}, new int[]{y - 10, y + 10}, null, null, false);
      graphics.drawPathOutline(new int[]{kmMarker + x, kmMarker + x}, new int[]{y - 10, y}, null, null, false);
      graphics.drawPathOutline(new int[]{miMarker + x, miMarker + x}, new int[]{y + 10, y}, null, null, false);
      String unit = " m";
      int distance = _kmValues[zoom];
      if (distance >= 1000) {
         distance /= 1000;
         unit = " km";
      }

      graphics.drawText(((StringBuffer)(new Object())).append(distance).append(unit).toString(), x + 1, y - _labelFont.getHeight());
      unit = " ft";
      distance = _miValues[zoom];
      if (distance >= 5280) {
         distance /= 5280;
         unit = " mi";
      }

      graphics.drawText(((StringBuffer)(new Object())).append(distance).append(unit).toString(), x + 1, y + 3);
   }

   void paintRotation(Graphics graphics) {
      _northArrow.paint(graphics, this._rotation);
   }

   protected boolean isMapComplete() {
      throw null;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      super.subpaint(graphics);
      if (this.isMapComplete()) {
         this._transform.clone(this._overlayTransform);
      }

      this.drawOverlays(graphics);
      if (this._recordingRoute != null) {
         this._recordingRoute.paint(graphics, this._transform);
      }

      if (this._pushDirectionsScreen) {
         UiApplication.getUiApplication().pushScreen(this._directionsListScreen);
         this._pushDirectionsScreen = false;
      }
   }

   protected void drawOverlays(Graphics graphics) {
      this._timer.startTimer(6);
      if (this._zoomMode || this._paintZoom) {
         this.paintZoom(graphics);
      }

      if (this._rotateMode || this._screen != null && this._screen._trackUp && this._screen._tracking) {
         this.paintRotation(graphics);
      }

      this._currentLocations.paint(graphics, this._overlayTransform, this._showCaption);
      if (this._screen != null && this._screen._tracking && this._screen._gpsLock) {
         this._marker._bearing = this._screen._currentBearing + this._rotation;
         if (this._marker._bearing > 360) {
            this._marker._bearing -= 360;
         }

         this._marker.paint(graphics, this._transform, true, this._showCaption, 0);
      }

      if (this._screen != null && (!this._screen._gpsLock || !this._screen._following) && !this._pointerMode) {
         this.drawCrosshair(graphics);
      }

      if (this._dimImage) {
         int oldAlpha = graphics.getGlobalAlpha();
         graphics.setGlobalAlpha(50);
         graphics.setColor(16);
         graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
         graphics.setGlobalAlpha(oldAlpha);
      }

      this.drawProgressBar(graphics);
      this.drawGenericHint(graphics);
      this.drawSplashScreen(graphics);
      this._timer.endTimer(6);
      this._timer.displayTimingInfo(graphics);
      this.drawBottomToolbar(graphics);
   }

   private void drawSplashScreen(Graphics g) {
      int width = this.getWidth();
      int height = this.getHeight() - this._progressField.getHeight();
      int x = width / 2 - _splashScreen.getWidth() / 2;
      int y = height / 2 - _splashScreen.getHeight() / 2;
      int color = g.getColor();
      int alpha = g.getGlobalAlpha();
      if (this._progressField.isFooterVisible() && this._isFirstTimeLaunch) {
         g.pushContext(0, 0, width, height, 0, 0);
         g.setColor(16777215);
         g.setGlobalAlpha(255);
         g.fillRect(0, 0, width, height);
         g.drawBitmap(x, y, width, height, _splashScreen, 0, 0);
         g.setColor(color);
         g.setGlobalAlpha(alpha);
         g.popContext();
         this._isSplahOnShow = true;
      } else {
         this._isSplahOnShow = false;
      }
   }

   public void setFirstTimeLaunch(boolean isFirstTime) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isSplashScreenOnShow() {
      return this._isSplahOnShow;
   }

   private void drawProgressBar(Graphics g) {
      if (this._progressField.isFooterVisible()) {
         int x = 0;
         int y = this.getHeight() - this._progressField.getHeight();
         g.pushContext(x, y, this._progressField.getWidth(), this._progressField.getHeight(), x, y);
         this._progressField.paint(g);
         g.popContext();
      }
   }

   private void drawGenericHint(Graphics g) {
      if (this._genericHint.isVisible()) {
         int x = this.getWidth() - this._genericHint.getWidth();
         int y = this.getHeight() - this._genericHint.getHeight() - this._progressField.getHeight() - 3;
         g.pushContext(x, y, this._genericHint.getWidth(), this._genericHint.getHeight(), x, y);
         this._genericHint.paint(g);
         g.popContext();
      }
   }

   private void drawCrosshair(Graphics graphics) {
      int cx = this.getWidth() >> 1;
      int cy = this.getHeight() >> 1;
      int color = graphics.getColor();
      int alpha = graphics.getGlobalAlpha();
      graphics.setColor(0);
      graphics.setGlobalAlpha(64);
      graphics.drawArc(cx - 9, cy - 9, 20, 20, 0, 360);
      graphics.drawLine(cx + 3, cy, cx + 16, cy + 1);
      graphics.drawLine(cx - 1, cy + 1, cx - 14, cy + 1);
      graphics.drawLine(cx + 1, cy + 3, cx + 1, cy + 16);
      graphics.drawLine(cx, cy - 1, cx + 1, cy - 14);
      graphics.setColor(16777215);
      graphics.setGlobalAlpha(128);
      graphics.drawArc(cx - 10, cy - 10, 20, 20, 0, 360);
      graphics.drawLine(cx + 2, cy, cx + 15, cy);
      graphics.drawLine(cx - 2, cy, cx - 15, cy);
      graphics.drawLine(cx, cy + 2, cx, cy + 15);
      graphics.drawLine(cx, cy - 2, cx, cy - 15);
      graphics.setColor(color);
      graphics.setGlobalAlpha(alpha);
   }

   private void drawBottomToolbar(Graphics graphics) {
      if (this._pointerMode) {
         int yOffset;
         if (this._toolbarBottom) {
            yOffset = this.getHeight() - 38;
         } else {
            yOffset = this.getHeight();
         }

         this._toolbarBottomRect = (XYRect)(new Object(this.getWidth() / 2 - 36, yOffset - 8, 72, 36));
         graphics.setColor(13882323);
         graphics.fillRoundRect(this.getWidth() / 2 - 36, yOffset - 8, 72, 36, 8, 8);
         graphics.setColor(16777215);
         graphics.setGlobalAlpha(128);
         graphics.drawRoundRect(this.getWidth() / 2 - 36, yOffset - 8, 72, 36, 8, 8);
         graphics.setGlobalAlpha(96);
         graphics.drawRoundRect(this.getWidth() / 2 - 35, yOffset - 8, 70, 35, 8, 8);
         graphics.setGlobalAlpha(255);
         graphics.setColor(4620980);
         graphics.fillRoundRect(this.getWidth() / 2 - 34, yOffset - 6, 68, 36, 8, 8);
         graphics.setColor(16777215);
         graphics.setGlobalAlpha(128);
         graphics.fillRoundRect(this.getWidth() / 2 - 33, yOffset - 5, 66, 35, 8, 8);
         graphics.setGlobalAlpha(255);
         graphics.setColor(13882323);
         graphics.fillRect(0, yOffset, this.getWidth(), this.getHeight());
         graphics.setColor(16777215);
         graphics.setGlobalAlpha(128);
         graphics.fillRect(0, yOffset, this.getWidth(), 2);
         graphics.setGlobalAlpha(96);
         graphics.fillRect(0, yOffset + 2, this.getWidth(), 1);
         graphics.setGlobalAlpha(64);
         graphics.fillRect(0, yOffset + 3, this.getWidth(), 1);
         graphics.setGlobalAlpha(255);
         graphics.drawBitmap(
            this._cursorX + this._cursor.getOrginX(),
            this._cursorY + this._cursor.getOrginY(),
            this._cursor.getBitmap().getWidth(),
            this._cursor.getBitmap().getHeight(),
            this._cursor.getBitmap(),
            0,
            0
         );
         if (yOffset < this.getHeight()) {
            if (this._pointerPanned) {
               this._pointerAssist.clear();
               this._pointerAssist
                  .populateMap(MapletCache.getInstance().getMaplets(this._rect, this._zoom), this._rect, this._zoom, this._rotation, this._transform);
               this._pointerPanned = false;
            }

            String street = this._pointerAssist.findCurrentPath(this._cursorX, this._cursorY);
            if (street != null) {
               try {
                  Font font = FontFamily.forName("BBClarity").getFont(0, 8, 2);
                  graphics.setColor(0);
                  graphics.setFont(font);
                  graphics.drawText(street, 3, yOffset + 3);
                  return;
               } finally {
                  return;
               }
            }
         }
      }
   }

   void rotate(boolean on) {
      if (this._rotateMode != on) {
         this._rotateMode = on;
         if (on) {
            this.invalidate();
         } else {
            this.update(true);
         }
      }
   }

   void zoom(boolean on) {
      if (this._zoomMode != on) {
         this._zoomMode = on;
         if (on) {
            this.showHintLabel(LBSResources.getString(475));
            if (Trackball.isSupported()) {
               this._screen.setTrackballSensitivityYOffset(this._yOffset);
            }

            if (Trackball.isSupported()) {
               this._screen.setTrackballFilter(-1);
            }

            this.invalidate();
         } else {
            this.showHintLabel(LBSResources.getString(474));
            if (Trackball.isSupported()) {
               this._screen.setTrackballSensitivityYOffset(100);
            }

            if (Trackball.isSupported()) {
               this._screen.setTrackballFilter(6);
            }

            if (this._zoomUpdatePID != -1) {
               UiApplication.getUiApplication().cancelInvokeLater(this._zoomUpdatePID);
               this._zoomUpdatePID = -1;
            }

            this.update(true);
         }
      }
   }

   void pointerMode(boolean on) {
      if (this._pointerMode != on) {
         this._pointerMode = on;
         if (on) {
            this._pointerPanned = true;
            if (Trackball.isSupported()) {
               this._screen.setTrackballSensitivityXOffset(100);
               this._screen.setTrackballSensitivityYOffset(100);
               this._screen.setTrackballFilter(6);
            }
         } else {
            this._pointerPanned = false;
            if (Trackball.isSupported()) {
               this._screen.setTrackballSensitivityXOffset(100);
               this._screen.setTrackballSensitivityYOffset(100);
               this._screen.setTrackballFilter(-1);
            }
         }

         this.invalidate();
      }
   }

   @Override
   protected boolean invokeAction(int action) {
      if (this.getRoute() != null) {
         new RouteDialog(this.getRoute()).doModal();
      }

      return true;
   }

   @Override
   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._pointerMode && !this._zoomMode) {
         if (this._cursorX + dx < 0) {
            if (this._cursorX == 0) {
               if (dx > 0) {
                  this.pan(0, Math.min(dx, this._width - 16));
               } else if (dx < 0) {
                  this.pan(0, Math.max(dx, 16 - this._width));
               }

               this._pointerPanned = true;
               this.updateScreenPosition();
               this.scheduleUpdate();
               this._screen.userPanned();
            } else {
               this._cursorX = 0;
               this._pointerPanned = false;
            }
         } else if (this._cursorX + dx > this._width - this._cursor.getBitmap().getWidth()) {
            if (this._cursorX == this._width - this._cursor.getBitmap().getWidth()) {
               if (dx > 0) {
                  this.pan(0, Math.min(dx, this._width - 16));
               } else if (dx < 0) {
                  this.pan(0, Math.max(dx, 16 - this._width));
               }

               this._pointerPanned = true;
               this.updateScreenPosition();
               this.scheduleUpdate();
               this._screen.userPanned();
            } else {
               this._cursorX = this._width - this._cursor.getBitmap().getWidth();
               this._pointerPanned = false;
            }
         } else {
            this._cursorX += dx;
            this._pointerPanned = false;
            this.invalidate();
         }

         if (this._cursorY + dy >= 0) {
            if (this._cursorY + dy <= this._height - 4) {
               this._cursorY += dy;
               this._pointerPanned = false;
               this.invalidate();
               return true;
            }

            if (this._cursorY == this._height - 4) {
               if (dy > 0) {
                  this.pan(270, Math.min(dy, this._height - 16));
               } else if (dy < 0) {
                  this.pan(270, Math.max(dy, 16 - this._height));
               }

               this._pointerPanned = true;
               this.updateScreenPosition();
               this.scheduleUpdate();
               this._screen.userPanned();
               return true;
            } else {
               this._cursorY = this._height - 4;
               this._pointerPanned = false;
               return true;
            }
         } else if (this._cursorY == 0) {
            if (dy > 0) {
               this.pan(270, Math.min(dy, this._height - 16));
            } else if (dy < 0) {
               this.pan(270, Math.max(dy, 16 - this._height));
            }

            this._pointerPanned = true;
            this.updateScreenPosition();
            this.scheduleUpdate();
            this._screen.userPanned();
            return true;
         } else {
            this._cursorY = 0;
            this._pointerPanned = false;
            return true;
         }
      } else if (this._zoomMode) {
         this.setZoom(this._zoom + dy);
         if (this._zoomUpdatePID != -1) {
            UiApplication.getUiApplication().cancelInvokeLater(this._zoomUpdatePID);
         }

         this._zoomUpdatePID = UiApplication.getUiApplication().invokeLater(this._zoomUpdateRunnable, 1000, false);
         this.invalidate();
         return true;
      } else {
         if (this._rotateMode) {
            this.setRotation(this._rotation + dx);
            this.update(true);
            return true;
         }

         boolean trackball = false;
         if (Trackball.isSupported()) {
            trackball = true;
         }

         if ((status & 1) != 0 && trackball) {
            if (trackball) {
               this._screen.setTrackballSensitivityXOffset(0);
               this._screen.setTrackballSensitivityYOffset(0);
            }

            if (Trackball.isSupported()) {
               this._screen.setTrackballFilter(-1);
            }

            boolean focusMoved = false;
            if (dx != 0) {
               focusMoved = this._currentLocations.moveFocus(dx, 0);
            }

            if (dy != 0) {
               focusMoved = this._currentLocations.moveFocus(-dy, 0);
            }

            if (focusMoved) {
               this.invalidate();
            }

            return true;
         } else {
            if (trackball) {
               this._screen.setTrackballSensitivityXOffset(100);
               this._screen.setTrackballSensitivityYOffset(100);
               if (this._screen._gpsLock && this._screen._following) {
                  accuX = accuX + Math.abs(dx);
                  accuY = accuY + Math.abs(dy);
               }
            }

            if (Trackball.isSupported()) {
               this._screen.setTrackballFilter(6);
            }

            if (this._screen._gpsLock && this._screen._following && Math.abs(accuX) < 10 && Math.abs(accuY) < 10) {
               return true;
            }

            if (this._screen._gpsLock && this._screen._following && (Math.abs(accuX) >= 10 || Math.abs(accuY) >= 10)) {
               accuX = 0;
               accuY = 0;
            }

            if (!this._screen._tracking && (dx != 0 || dy != 0) || this._screen._tracking && (Math.abs(dx) >= 2 || Math.abs(dy) >= 2)) {
               if (dx > 0) {
                  this.pan(0, Math.min(dx, this._width - 16));
               } else if (dx < 0) {
                  this.pan(0, Math.max(dx, 16 - this._width));
               }

               if (dy > 0) {
                  this.pan(270, Math.min(dy, this._height - 16));
               } else if (dy < 0) {
                  this.pan(270, Math.max(dy, 16 - this._height));
               }

               this.updateScreenPosition();
               this.scheduleUpdate();
               this._screen.userPanned();
               return true;
            } else {
               return false;
            }
         }
      }
   }

   protected void scheduleUpdate() {
      if (this._updatePID != -1) {
         Application.getApplication().cancelInvokeLater(this._updatePID);
      }

      this._updatePID = Application.getApplication().invokeLater(this._updateRunnable, 300, false);
      this.invalidate();
   }

   @Override
   protected boolean navigationClick(int status, int time) {
      if (this._zoomMode) {
         this.zoom(false);
         return true;
      } else if (this._rotateMode) {
         this.rotate(false);
         return true;
      } else if (this._pointerMode && this._toolbarBottomRect.intersects(this._cursorX, this._cursorY, 1, 1)) {
         this._toolbarBottom = !this._toolbarBottom;
         this.invalidate();
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected boolean trackwheelRoll(int amount, int status, int time) {
      if (amount != 0) {
         if (this._zoomMode) {
            this.setZoom(this._zoom + amount);
            if (this._zoomUpdatePID != -1) {
               UiApplication.getUiApplication().cancelInvokeLater(this._zoomUpdatePID);
            }

            this._zoomUpdatePID = UiApplication.getUiApplication().invokeLater(this._zoomUpdateRunnable, 1000, false);
            this.invalidate();
            return true;
         } else {
            if (this._rotateMode) {
               this.setRotation(this._rotation + amount);
               this.update(true);
               return true;
            }

            int inc = amount * 16;
            if ((status & 1) != 0) {
               this.pan(0, inc);
            } else {
               this.pan(270, inc);
            }

            this.updateScreenPosition();
            this.scheduleUpdate();
            this._screen.userPanned();
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (this._zoomMode) {
         this.zoom(false);
         return true;
      }

      if (this._rotateMode) {
         this.rotate(false);
      }

      return super.trackwheelClick(status, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\b':
            Location location = this.getFocusLocation();
            if (location != null) {
               if (Dialog.ask(3, LBSResources.getString(114)) == 4) {
                  this._currentLocations.clear(location);
               }
            } else if (this.getRoute() != null && Dialog.ask(3, LBSResources.getString(437)) == 4) {
               this._currentLocations.clearRoutes();
            }

            this.invalidate();
         default:
            return super.keyChar(key, status, time);
         case '\n':
            if (this._zoomMode) {
               this.zoom(false);
               return true;
            } else {
               if (this._rotateMode) {
                  this.rotate(false);
                  return true;
               }

               if (this._currentLocations._count > 0 && !(this._lastUsedScreen instanceof DirectionsListScreen)) {
                  Location location = this.getFocusLocation();
                  if (location != null) {
                     new LocationDialog(this.getFocusLocation(), this._currentLegalNotices).doModal();
                  }
               }

               return true;
            }
         case '\u001b':
            if (this._zoomMode) {
               this.zoom(false);
               return true;
            } else if (this._rotateMode) {
               this.rotate(false);
               return true;
            } else if (this._lastUsedScreen == null) {
               return false;
            } else if (!(this._lastUsedScreen instanceof LocationsListScreen)) {
               if (this._lastUsedScreen instanceof DirectionsListScreen) {
                  DirectionsListScreen screen = (DirectionsListScreen)this._lastUsedScreen;
                  screen.setFocusIndex(this.getRoute()._decisions._focus);
                  UiApplication.getUiApplication().pushScreen(screen);
               }

               return true;
            } else {
               LocationsListScreen screen = (LocationsListScreen)this._lastUsedScreen;
               screen.setFocusIndex(this._currentLocations._focus);
               UiApplication.getUiApplication().pushScreen(screen);
               return true;
            }
      }
   }

   public static boolean isReducedKeyboard() {
      return InternalServices.isReducedFormFactor();
   }

   void toggleTitleField() {
      if (this._showTitleField) {
         this._showTitleField = false;
         this._screen.setBannerVisible(false);
         LBSOptions.setBoolean(5204834541750260038L, true);
      } else {
         this._showTitleField = true;
         this._screen.setBannerVisible(true);
         LBSOptions.setBoolean(5204834541750260038L, false);
      }

      this.updateScreenPosition();
      this.update(true);
   }

   private void schedulePaintZoom() {
      this._paintZoom = true;
      if (this._paintZoomPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._paintZoomPID);
      }

      this._paintZoomPID = UiApplication.getUiApplication().invokeLater(this._paintZoomRunnable, 1000, false);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (isReducedKeyboard()) {
         switch (key) {
            case 76:
               this.zoomIn();
               this.schedulePaintZoom();
               this.update(true);
               return true;
            case 79:
               this.zoomOut();
               this.schedulePaintZoom();
               this.update(true);
               return true;
            case 81:
               this.toggleTitleField();
               return true;
         }
      } else {
         switch (key) {
            case 73:
               this.zoomIn();
               this.schedulePaintZoom();
               this.update(true);
               return true;
            case 78:
               this._currentLocations.moveFocus(1, 0);
               return true;
            case 79:
               this.zoomOut();
               this.schedulePaintZoom();
               this.update(true);
               return true;
            case 80:
               this._currentLocations.moveFocus(-1, 0);
               return true;
            case 85:
               this.toggleTitleField();
               return true;
         }
      }

      boolean handled = false;
      int altkey = Keypad.getAltedChar((char)key);
      if (isReducedKeyboard()) {
         switch (altkey) {
            case 52:
               this._currentLocations.moveFocus(-1, 0);
               return true;
            case 54:
               this._currentLocations.moveFocus(1, 0);
               return true;
         }
      } else {
         switch (altkey) {
            case 48:
            case 53:
               break;
            case 49:
               this.pan(90);
               this.pan(180);
               this.updateScreenPosition();
               handled = true;
               break;
            case 50:
            default:
               this.pan(90);
               this.updateScreenPosition();
               handled = true;
               break;
            case 51:
               this.pan(90);
               this.pan(0);
               this.updateScreenPosition();
               handled = true;
               break;
            case 52:
               this.pan(180);
               this.updateScreenPosition();
               handled = true;
               break;
            case 54:
               this.pan(0);
               this.updateScreenPosition();
               handled = true;
               break;
            case 55:
               this.pan(270);
               this.pan(180);
               this.updateScreenPosition();
               handled = true;
               break;
            case 56:
               this.pan(270);
               this.updateScreenPosition();
               handled = true;
               break;
            case 57:
               this.pan(270);
               this.pan(0);
               this.updateScreenPosition();
               handled = true;
         }
      }

      if (handled) {
         this._screen.userPanned();
         this.scheduleUpdate();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      return this.keyDown(keycode, time);
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   protected boolean keyStatus(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key != 258 && key != 256 && key != 261) {
         return false;
      } else if ((Keypad.status(keycode) & 2) != 0) {
         this.zoom(true);
         return true;
      } else {
         this.zoom(false);
         return true;
      }
   }

   private int pixelsToDegrees(int pixels) {
      return pixels << this._zoom;
   }

   protected void updateScreenPosition() {
      int geoWidth;
      int geoHeight;
      if (this._transform._mapView == null) {
         geoWidth = this._width << this._zoom;
         geoHeight = this._height << this._zoom;
      } else {
         geoWidth = this._transform._screenView.width() << this._zoom;
         geoHeight = -this._transform._screenView.height() << this._zoom;
      }

      this._latitude = Math.max(this._latitude, -9000000);
      this._latitude = Math.min(this._latitude, 9000000);
      this._longitude = Math.max(this._longitude, -18000000);
      this._longitude = Math.min(this._longitude, 18000000);
      this._rect._bottom = this._latitude - geoHeight / 2;
      this._rect._top = this._rect._bottom + geoHeight;
      if (this._rect._bottom < -9000000) {
         int offset = -9000000 - this._rect._bottom;
         this._latitude += offset;
         this._rect._bottom += offset;
         this._rect._top += offset;
      } else if (this._rect._top > 8999999) {
         int offset = this._rect._top - 8999999;
         this._latitude -= offset;
         this._rect._bottom -= offset;
         this._rect._top -= offset;
      }

      if (LBSOptions.SPHERICAL_CORRECTION) {
         int sphericalCorrection = Transform.getSphericalCorrection(this._latitude, this._zoom);
         geoWidth = Fixed32.mul(geoWidth, Fixed32.div(65536, sphericalCorrection));
      }

      this._rect._left = this._longitude - geoWidth / 2;
      this._rect._right = this._rect._left + geoWidth;
      if (this._rect._left < -18000000) {
         int var6 = -18000000 - this._rect._left;
         this._longitude += var6;
         this._rect._left += var6;
         this._rect._right += var6;
      } else if (this._rect._right > 17999999) {
         int var7 = this._rect._right - 17999999;
         this._longitude -= var7;
         this._rect._left -= var7;
         this._rect._right -= var7;
      }

      this._ptMarkerNew._x = this._longitude;
      this._ptMarkerNew._y = this._latitude;
      this._transform.convertWorldToScreen(this._ptMarkerNew);
      this._transform.update(this._rect, this._zoom, this._rotation);
      this._lblRect = this._transform._cropView;
   }

   protected void showLocation(Location location) {
      this._currentLocations.clear();
      this._currentLocations.add(location.copy(location));
      this._latitude = location._latitude;
      this._longitude = location._longitude;
      this.setZoom(location._zoom);
   }

   protected void moveTo(int latitude, int longitude, int zoom) {
      this._latitude = latitude;
      this._longitude = longitude;
      this.setZoom(zoom);
   }

   protected void moveTo(int latitude, int longitude) {
      this.moveTo(latitude, longitude, this.getZoom());
   }

   void pan(int direction) {
      this.pan(direction, 32);
   }

   void pan(int direction, int inc) {
      int dir = this._rotation + direction;

      while (dir > 359) {
         dir -= 360;
      }

      this._latitude = this._latitude + this.pixelsToDegrees(inc * Utilities.sin(dir) + 32768 >> 16);
      this._longitude = this._longitude + this.pixelsToDegrees(inc * Utilities.cos(dir) + 32768 >> 16);
   }

   void zoom(int amount) {
      this._pointerPanned = true;
      this.setZoom(this.getZoom() + amount);
   }

   void zoomIn() {
      if (this.getZoom() > minZoom()) {
         this.zoom(-1);
      }
   }

   void zoomOut() {
      if (this.getZoom() < maxZoom()) {
         this.zoom(1);
      }
   }

   void onDeactivate() {
      this.zoom(false);
   }

   Location getFocusLocation() {
      return this._currentLocations.getFocus();
   }

   Route getRoute() {
      return this._currentLocations._routeLocation != null ? this._currentLocations._routeLocation._route : null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final boolean handleErrorInternal(Request request) {
      boolean canContinue = true;
      int rc = request.getResponseCode();
      if (rc != 0 && (rc < 200 || rc > 299)) {
         switch (rc) {
            case -11:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$6(this));
                  return canContinue;
               }
               break;
            case -10:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$5(this));
                  return canContinue;
               }
               break;
            case -9:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$4(this));
                  return canContinue;
               }
               break;
            case -8:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$3(this));
                  return canContinue;
               }
               break;
            case -7:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$2(this));
                  return canContinue;
               }
               break;
            case 2:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapField$7(this));
               }
            default:
               byte[] b = request._lastRequest;
               StringBuffer requestStr = (StringBuffer)(new Object());
               if (b != null) {
                  label85:
                  try {
                     for (int i = 0; i < b.length; i++) {
                        String s = ((StringBuffer)(new Object("0"))).append(Integer.toHexString(b[i])).toString();
                        requestStr.append(s.substring(s.length() - 2, s.length()));
                     }
                  } catch (Throwable var9) {
                     requestStr.append(((StringBuffer)(new Object("... error in parsing: "))).append(e.getMessage()).toString());
                     break label85;
                  }
               } else {
                  requestStr.append("<empty>");
               }

               EventLogger.logEvent(
                  UID,
                  ((StringBuffer)(new Object("Maplet Request error: ")))
                     .append(rc)
                     .append(" zoom: ")
                     .append(this._zoom)
                     .append(", lat: ")
                     .append(this._latitude)
                     .append(", lon: ")
                     .append(this._longitude)
                     .append(", left: ")
                     .append(this._rect._left)
                     .append(", top: ")
                     .append(this._rect._top)
                     .append(", right: ")
                     .append(this._rect._right)
                     .append(", bottom: ")
                     .append(this._rect._bottom)
                     .append(", URL: ")
                     .append(request.getURL())
                     .append(", request: ")
                     .append(requestStr.toString())
                     .toString()
                     .getBytes(),
                  2
               );
               return canContinue;
            case 5:
               LBSOptions.setString(-9040565055715388692L, null);
            case 4:
               if (request.getVersion() == 0) {
                  LBSApplication.runApplicationUpdate();
                  return false;
               }
         }
      } else {
         this._userPromptedMapsUnavailable = false;
      }

      return canContinue;
   }

   void add(LBSMenuItem menuItem) {
      Arrays.add(this._menuItems, menuItem);
   }

   void createMenuItems() {
      int order = LBSMenuItem.MODE_ORDER;
      order = LBSMenuItem.CONTEXT_ORDER;
      this.add(new MapField$8(this, 94, order++, order++));
      this.add(new MapField$9(this, 109, order++));
      this.add(new MapField$10(this, 295, order++));
   }

   public void zoomToFit() {
      this.zoomToFit(null);
   }

   public void zoomToFit(XYRect rect) {
      XYRect bbox = null;
      if (rect != null) {
         bbox = (XYRect)(new Object(rect));
      } else {
         bbox = (XYRect)(new Object(this._currentLocations._bbox));
      }

      int zoom = 0;
      int screenHeight = -this._transform._screenView.height();
      int height = screenHeight;
      int width = this._width;
      int widthCorrected;
      if (LBSOptions.SPHERICAL_CORRECTION) {
         int sphericalCorrection = Transform.getSphericalCorrection(this._latitude, zoom);
         widthCorrected = Fixed32.mul(width, Fixed32.div(65536, sphericalCorrection));
      } else {
         widthCorrected = width;
      }

      this._longitude = bbox.x + bbox.width / 2;
      this._latitude = bbox.y + bbox.height / 2;

      while (zoom < 15 && (widthCorrected << zoom < bbox.width || height << zoom < bbox.height)) {
         zoom++;
         if (LBSOptions.SPHERICAL_CORRECTION) {
            int sphericalCorrection = Transform.getSphericalCorrection(this._latitude, zoom);
            widthCorrected = Fixed32.mul(width, Fixed32.div(65536, sphericalCorrection));
         } else {
            widthCorrected = width;
         }
      }

      this.setZoom(zoom);
      this.updateScreenPosition();
      this.update(true);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      for (int i = this._menuItems.length - 1; i >= 0; i--) {
         LBSMenuItem menuItem = this._menuItems[i];
         if (menuItem.isVisible()) {
            contextMenu.addItem(menuItem);
         }
      }

      super.makeContextMenu(contextMenu);
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      if (instance == 65536) {
         if (this._currentLocations._count > 0 && !(this._lastUsedScreen instanceof DirectionsListScreen)) {
            contextMenu.addItem(this.getMenuItem(94));
         }

         if (this._currentLocations.getFocus() != null) {
            contextMenu.addItem(this.getMenuItem(109));
         }
      }

      super.makeContextMenu(contextMenu, instance);
   }

   private LBSMenuItem getMenuItem(int id) {
      for (int i = 0; i < this._menuItems.length; i++) {
         if (this._menuItems[i].getId() == id) {
            return this._menuItems[i];
         }
      }

      return null;
   }

   void locationUpdated(GPSLocationData gpsLocationData) {
      if (this._recordingRoute != null) {
         this._recordingRoute.addObservation(gpsLocationData);
      }
   }

   boolean isRecording() {
      return this._recordingRoute != null;
   }

   void stopRecording() {
      this._recordingRoute.save();
      this._recordingRoute = null;
   }

   boolean isTracking() {
      return this._screen._tracking && this._screen._gpsLock;
   }

   void setLastUsedScreen(Screen screen) {
      this._lastUsedScreen = screen;
      if (this._lastUsedScreen == null) {
         this._screen.getMenuItem(317).setPriority(10);
         this._screen.getMenuItem(319).setPriority(500);
         if (this._screen.getMenuItem(287) != null) {
            this._screen.getMenuItem(287).setPriority(500);
            return;
         }
      } else if (this._lastUsedScreen != null && this._lastUsedScreen instanceof DirectionsListScreen) {
         this._screen.getMenuItem(317).setPriority(500);
         this._screen.getMenuItem(319).setPriority(10);
         if (this._screen.getMenuItem(287) != null) {
            this._screen.getMenuItem(287).setPriority(500);
            return;
         }
      } else if (this._lastUsedScreen != null && this._lastUsedScreen instanceof LocationsListScreen) {
         this._screen.getMenuItem(317).setPriority(500);
         this._screen.getMenuItem(319).setPriority(500);
         if (this._screen.getMenuItem(287) != null) {
            this._screen.getMenuItem(287).setPriority(10);
         }
      }
   }

   void showLocationMarkerCaption(boolean schedule) {
      this._showCaption = true;
   }

   boolean isMarkerBubbleShowing() {
      return this._showCaption;
   }
}
