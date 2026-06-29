package net.rim.device.apps.internal.lbs.verbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.CheckRadioConnections;
import net.rim.device.apps.internal.lbs.CheckRadioConnections$Callback;
import net.rim.device.apps.internal.lbs.EULA;
import net.rim.device.apps.internal.lbs.LocationsListScreen;
import net.rim.device.apps.internal.lbs.MapField;
import net.rim.device.apps.internal.lbs.MapScreen;
import net.rim.device.apps.internal.lbs.locator.ProgressDialog;
import net.rim.device.apps.internal.lbs.locator.ProgressDialog$ProgressThread;
import net.rim.device.apps.internal.lbs.locator.SearchPOIDialog;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.device.apps.internal.lbs.protocol.POIRequest;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class POIVerb extends Verb implements Request$Listener, CheckRadioConnections$Callback {
   private int _bly;
   private int _blx;
   private int _try;
   private int _trx;
   private byte _zoom;
   private String _keywords;
   private boolean _responseReceived = false;
   private boolean _requestError = false;
   private int _returnCode;
   private String _poiXmlString;
   private byte[] _poiXmlData;
   private MapField _mapField;
   private MapScreen _mapScreen;
   private Object _screen;
   private boolean _isNewSearch = false;
   public ProgressDialog _progressDialog;

   private POIVerb() {
      super(1200384);
   }

   public POIVerb(Object screen, MapScreen mapScreen, MapField mapField, MapRect rect, int zoom) {
      this();
      this._screen = screen;
      this._mapScreen = mapScreen;
      this._mapField = mapField;
      this._bly = rect._bottom;
      this._blx = rect._left;
      this._try = rect._bottom + rect.height();
      this._trx = rect._left + rect.width();
      this._zoom = (byte)zoom;
   }

   @Override
   public final Object invoke(Object context) {
      if (!new EULA().confirmAgreement()) {
         return null;
      }

      SearchPOIDialog poiDialog = new SearchPOIDialog(new Object(), LBSResources.getString(287));
      this._keywords = poiDialog.doModal();
      if (this._keywords != null && this._keywords.length() != 0) {
         this._mapField._poiKeywords = this._keywords;
         System.out.println("Sending POI request to the server...");
         this._returnCode = 31;
         if (RadioInfo.getActiveWAFs() != 0
            && (CoverageInfo.isCoverageSufficient(2) || CoverageInfo.isCoverageSufficient(4) || CoverageInfo.isCoverageSufficient(1))) {
            this.setCallback(this._screen);
            this._poiXmlData = this.searchPOIs();
            boolean found = false;
            if (!this._requestError) {
               found = this._mapScreen.handleRespone(this._poiXmlData);
            }

            if (found && this._isNewSearch && UiApplication.getUiApplication().getActiveScreen() instanceof LocationsListScreen) {
               UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            }
         } else {
            CheckRadioConnections checkRadio = new CheckRadioConnections(this);
            this.setCallback(this._screen);
            POIRequest request = new POIRequest(this, this._bly, this._blx, this._try, this._trx, this._zoom, this._keywords, null);
            checkRadio.checkRadioConnection(request);
         }

         return this._poiXmlData;
      } else {
         return null;
      }
   }

   public final void setCallback(Object screen) {
      if (screen instanceof MapScreen) {
         this._isNewSearch = false;
      } else {
         if (screen instanceof LocationsListScreen) {
            this._isNewSearch = true;
         }
      }
   }

   public final byte[] searchPOIs() {
      POIRequest.request(this, this._bly, this._blx, this._try, this._trx, this._zoom, this._keywords, null);
      if (!this._responseReceived) {
         this._progressDialog = new ProgressDialog(this, LBSResources.getString(231), 0, 30);
         this._progressDialog.doModal();
         if (this._progressDialog.isCancelPressed()) {
            this._returnCode = 0;
         }
      }

      switch (this._returnCode) {
         case -10:
            Dialog.alert(LBSResources.getString(489));
            return null;
         case -9:
            Dialog.alert(LBSResources.getString(488));
            this._requestError = true;
            break;
         case -8:
            Dialog.alert(LBSResources.getString(487));
            this._requestError = true;
            break;
         case -7:
            Dialog.alert(LBSResources.getString(486));
            this._requestError = true;
            break;
         case -6:
            this.coverageErrorMsg();
            this._requestError = true;
            break;
         case -4:
         case 500:
            Dialog.alert(LBSResources.getString(103));
            this._requestError = true;
            break;
         case 1:
         case 2:
            Dialog.alert(LBSResources.getString(228));
            this._requestError = true;
            break;
         case 3:
            Dialog.alert(LBSResources.getString(229));
            this._requestError = true;
      }

      System.out.println(((StringBuffer)(new Object("_poiXmlString="))).append(this._poiXmlString).toString());
      return this._poiXmlData;
   }

   @Override
   public final void radioChecked(boolean radioTurnedOn, boolean dataServicesEnabled, boolean outOfCoverage) {
      if (radioTurnedOn && !outOfCoverage && dataServicesEnabled) {
         this._poiXmlData = this.searchPOIs();
         boolean found = this._mapScreen.handleRespone(this._poiXmlData);
         if (this._isNewSearch) {
            LocationsListScreen screen = (LocationsListScreen)this._screen;
            if (found) {
               screen.popLocationListScreen();
            }
         }
      }
   }

   private final void coverageErrorMsg() {
      String netType = (RadioInfo.getActiveWAFs() & 4) == 4 ? LBSResources.getString(336) : LBSResources.getString(335);
      String msg = MessageFormat.format(LBSResources.getString(334), new Object[]{netType});
      Dialog.alert(msg);
   }

   @Override
   public final String toString() {
      return LBSResources.getString(95);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void requestComplete(Request request) {
      int rc = request.getResponseCode();
      if (rc != 0) {
         byte[] b = request._lastRequest;
         StringBuffer requestStr = (StringBuffer)(new Object());
         if (b != null) {
            label65:
            try {
               for (int i = 0; i < b.length; i++) {
                  String s = ((StringBuffer)(new Object("0"))).append(Integer.toHexString(b[i])).toString();
                  requestStr.append(s.substring(s.length() - 2, s.length()));
               }
            } catch (Throwable var11) {
               requestStr.append(((StringBuffer)(new Object("... error in parsing: "))).append(e.getMessage()).toString());
               break label65;
            }
         } else {
            requestStr.append("<empty>");
         }

         long UID = -1037010874164756539L;
         EventLogger.logEvent(
            -1037010874164756539L,
            ((StringBuffer)(new Object("POI Request error: ")))
               .append(request.getResponseCode())
               .append(", URL: ")
               .append(request.getURL())
               .append(", request: ")
               .append(requestStr.toString())
               .toString()
               .getBytes(),
            2
         );
         if (rc >= 300) {
            rc = 500;
         }

         this._returnCode = rc;
      }

      if (request instanceof POIRequest) {
         this._poiXmlString = ((POIRequest)request).getPOIXmlString();
         this._poiXmlData = ((POIRequest)request).getRawData();
         if (this._progressDialog == null) {
            this._responseReceived = true;
            synchronized (this) {
               this.notify();
               return;
            }
         }

         ((ProgressDialog$ProgressThread)this._progressDialog._progressThread).stopProgress();
      }
   }

   @Override
   public final void progressTick(int current, int total) {
   }

   @Override
   public final void setState(int state) {
   }
}
