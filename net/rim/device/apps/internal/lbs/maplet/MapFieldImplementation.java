package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.lbs.MapField;
import net.rim.device.api.lbs.MapField$Implementation;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.apps.internal.lbs.CheckRadioConnections;
import net.rim.device.apps.internal.lbs.CheckRadioConnections$Callback;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.LBSOptions;
import net.rim.device.apps.internal.lbs.Transform;
import net.rim.device.apps.internal.lbs.protocol.MapRequest;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.render.RenderThread;
import net.rim.device.apps.internal.lbs.render.RenderThread$Listener;

public final class MapFieldImplementation implements MapField$Implementation, Request$Listener, RenderThread$Listener, CheckRadioConnections$Callback {
   MapField _mapField;
   RenderThread _renderThread;
   OffscreenBuffer[] _offscreenBuffers = new OffscreenBuffer[2];
   int _frontBuffer;
   private boolean _immediateRedraw;
   int _zoom;
   int _longitude;
   int _latitude;
   int _rotation;
   int _paddingTown;
   int _paddingShield;
   MapRect _rect = new MapRect();
   MapRect _lblRect = new MapRect();
   int _width;
   int _height;
   protected Transform _transform = new Transform();
   private boolean _userPromptedMapsUnavailable;
   private boolean _updated;
   private boolean _dataMissing = false;

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
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$5(this));
                  return canContinue;
               }
               break;
            case -10:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$4(this));
                  return canContinue;
               }
               break;
            case -9:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$3(this));
                  return canContinue;
               }
               break;
            case -8:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$2(this));
                  return canContinue;
               }
               break;
            case -7:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$1(this));
                  return canContinue;
               }
               break;
            case 2:
               canContinue = false;
               if (!this._userPromptedMapsUnavailable) {
                  this._userPromptedMapsUnavailable = true;
                  UiApplication.getUiApplication().invokeAndWait(new MapFieldImplementation$6(this));
               }
            default:
               byte[] b = request._lastRequest;
               StringBuffer requestStr = new StringBuffer();
               if (b == null) {
                  requestStr.append("<empty>");
                  return canContinue;
               }

               try {
                  for (int i = 0; i < b.length; i++) {
                     String s = "0" + Integer.toHexString(b[i]);
                     requestStr.append(s.substring(s.length() - 2, s.length()));
                  }
                  break;
               } catch (Throwable var9) {
                  requestStr.append("... error in parsing: " + e.getMessage());
                  return canContinue;
               }
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

   public final void doRenderComplete(boolean dataMissing) {
      if (dataMissing && this._updated) {
         MapRequest.request(this, this._rect, this._zoom);
      }

      this._updated = false;
      this._frontBuffer = this._frontBuffer == 0 ? 1 : 0;
      this._mapField.implementationInvalidate();
   }

   public final void setCallback(boolean dataMissing) {
      this._dataMissing = dataMissing;
   }

   protected final void update(boolean immediateRedraw) {
      this._updated = true;
      this.render();
      if (immediateRedraw) {
         this._mapField.implementationInvalidate();
      }

      this._immediateRedraw = immediateRedraw;
   }

   protected final void updateScreenPosition() {
      int geoWidth = this._width << this._zoom;
      int geoHeight = this._height << this._zoom;
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

      this._transform.update(this._rect, this._zoom, this._rotation);
   }

   @Override
   public final void convertFieldToWorld(XYPoint point, boolean relative) {
      point.x = this._transform.getWorldBLX() - (point.x << this._zoom);
      point.y = this._transform.getWorldBLY() + (point.y << this._zoom);
   }

   @Override
   public final void convertWorldToField(XYPoint point, boolean relative) {
      int[] wldTransform = this._transform.getWorldTransform();
      point.x = point.x - this._transform.getWorldBLX() >> this._zoom;
      point.y = point.y - this._transform.getWorldBLY() >> this._zoom;
      int x = point.x * wldTransform[0] + point.y * wldTransform[1] + wldTransform[2] >> 16;
      int y = point.x * wldTransform[3] + point.y * wldTransform[4] + wldTransform[5] >> 16;
      point.x = x;
      point.y = y;
   }

   @Override
   public final void renderComplete(boolean dataMissing) {
      if ((!dataMissing || !this._updated || RadioInfo.getActiveWAFs() != 0)
         && (CoverageInfo.isCoverageSufficient(2) || CoverageInfo.isCoverageSufficient(4) || CoverageInfo.isCoverageSufficient(1))) {
         this.doRenderComplete(dataMissing);
      } else {
         CheckRadioConnections checkRadio = new CheckRadioConnections(this);
         this.setCallback(dataMissing);
         MapRequest request = new MapRequest(this, this._rect, this._zoom);
         checkRadio.checkRadioConnection(request);
      }
   }

   @Override
   public final void paint(Graphics graphics) {
      if (this._immediateRedraw && this._renderThread.isMapReady()) {
         int backBuffer = this._frontBuffer == 0 ? 1 : 0;
         this._offscreenBuffers[backBuffer].blt(graphics, this._rect, this._zoom, this._height);
      }

      this._offscreenBuffers[this._frontBuffer].blt(graphics, this._rect, this._zoom, this._height);
   }

   @Override
   public final void updateView(int latitude, int longitude, float zoom, int rotation) {
      this._latitude = latitude;
      this._longitude = longitude;
      this._zoom = (int)zoom;
      this._rotation = rotation;
      this.updateScreenPosition();
      if (this._offscreenBuffers[0] != null) {
         this.update(true);
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
      if (this._immediateRedraw) {
         this._mapField.implementationInvalidate();
      }
   }

   @Override
   public final void mapComplete(boolean dataMissing) {
      if (this._immediateRedraw) {
         this._mapField.implementationInvalidate();
      }
   }

   @Override
   public final void setExtent(int width, int height) {
      this._width = width;
      this._height = height;
      if (this._offscreenBuffers[0] == null) {
         this._offscreenBuffers[0] = new OffscreenBuffer(this._width, this._height, this._transform);
         this._offscreenBuffers[1] = new OffscreenBuffer(this._width, this._height, this._transform);
      }

      this.updateScreenPosition();
      this.update(true);
   }

   @Override
   public final void requestComplete(Request request) {
      if (this.handleErrorInternal(request)) {
         this.render();
      }
   }

   @Override
   public final void progressTick(int current, int total) {
   }

   @Override
   public final void setState(int state) {
   }

   private final void render() {
      int backBuffer = this._frontBuffer == 0 ? 1 : 0;
      this._offscreenBuffers[backBuffer]
         .render(this._renderThread, this._rect, this._lblRect, this._zoom, this._rotation, this._paddingTown, this._paddingShield);
   }

   MapFieldImplementation(Object object) {
      this._renderThread = new RenderThread(this, this._transform);
      this._mapField = (MapField)object;
   }

   public static final void registerOnStartup() {
      ApplicationRegistry.getApplicationRegistry().put(MapField.GUID_FACTORY, new MapFieldImplementationFactory());
   }
}
