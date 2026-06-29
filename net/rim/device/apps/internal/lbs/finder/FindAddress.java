package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.lbs.CheckRadioConnections;
import net.rim.device.apps.internal.lbs.CheckRadioConnections$Callback;
import net.rim.device.apps.internal.lbs.DirectionsChoiceScreen;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.protocol.SearchRequest;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class FindAddress extends Verb implements Request$Listener, CheckRadioConnections$Callback {
   private boolean _latOutRange;
   private boolean _longOutRange;
   private boolean _cancelled;
   private FinderProgressDialog _progressDialog;
   private Location[] _foundLocations;
   private String _title;
   private boolean _direction;
   private boolean _requestThreadStart = false;
   private Location _callbackLocation = null;
   private MailingAddressModel _callbackModel = null;
   private DirectionsChoiceScreen _screen = null;
   private int _searchType = -1;
   private static final int MIN_LAT_VAL = -9000000;
   private static final int MAX_LAT_VAL = 8999999;
   private static final int MIN_LONG_VAL = -18000000;
   private static final int MAX_LONG_VAL = 17999999;
   public static final int SEARCH_NO_TYPE = -1;
   public static final int SEARCH_HISTORYLIST = 0;
   public static final int SEARCH_ENTEREDADDRESS = 1;
   public static final int SEARCH_ADDRESSBOOK = 2;

   public FindAddress(String title, boolean direction) {
      super(0);
      if (title == null) {
         this._title = LBSResources.getString(300);
      } else {
         this._title = title;
      }

      this._direction = direction;
   }

   @Override
   public final Object invoke(Object parameter) {
      Location location = null;
      MailingAddressModel model = null;
      if (parameter instanceof SearchAddressModel) {
         FindAddressDialog find = new FindAddressDialog(this._title, this._direction);
         find.setModel(parameter);
         model = find.get();
      } else if (parameter instanceof Object) {
         model = (MailingAddressModel)parameter;
      }

      if (model != null) {
         MapPoint p = this.isLatLongPair(model);
         if (p == null) {
            if (RadioInfo.getActiveWAFs() != 0
               && (CoverageInfo.isCoverageSufficient(2) || CoverageInfo.isCoverageSufficient(4) || CoverageInfo.isCoverageSufficient(1))) {
               location = this.performSearchLocation(location, model);
            } else {
               this.setCallback(location, model, null, -1);
               CheckRadioConnections checkRadio = new CheckRadioConnections(this);
               SearchRequest request = new SearchRequest(this, model);
               checkRadio.checkRadioConnection(request);
            }
         } else {
            int y = p._y != 8999999 ? p._y : p._y + 1;
            int x = p._x != 17999999 ? p._x : p._x + 1;
            String label = ((StringBuffer)(new Object())).append(y / 4681608360884174848L).append(", ").append(x / 4681608360884174848L).toString();
            if (this._latOutRange || this._longOutRange) {
               label = ((StringBuffer)(new Object())).append((int)(y / 4681608360884174848L)).append(", ").append((int)(x / 4681608360884174848L)).toString();
            }

            location = new Location(y, x, 4, label);
            location._address = label;
         }
      }

      if (location != null) {
         FinderHistory.getInstance().add(location);
      }

      return location;
   }

   public final void setCallback(Location location, MailingAddressModel model, DirectionsChoiceScreen screen, int searchType) {
      if (location != null) {
         this._callbackLocation = location;
      }

      if (model != null) {
         this._callbackModel = model;
      }

      if (screen != null) {
         this._screen = screen;
      }

      if (searchType != -1) {
         this._searchType = searchType;
      }
   }

   private final Location performSearchLocation(Location location, MailingAddressModel model) {
      this.setCallback(location, model, null, -1);
      SearchRequest.request(this, model);
      this._progressDialog = new FinderProgressDialog(this);
      this._progressDialog.start();
      if (this._foundLocations != null && this._foundLocations.length > 0) {
         int resultIndex = -1;
         if (this._foundLocations.length == 1) {
            if (this._foundLocations[0]._merit == -1) {
               resultIndex = 0;
            } else {
               FindAddress$MeritInquiry meritInquiry = new FindAddress$MeritInquiry(this, this._foundLocations[0]._label);
               UiApplication.getUiApplication().invokeAndWait(meritInquiry);
               resultIndex = meritInquiry._resultIndex;
            }
         } else {
            FinderResultsSelectionScreen resultsScreen = new FinderResultsSelectionScreen(this._foundLocations);
            resultIndex = resultsScreen.get();
         }

         if (resultIndex > -1) {
            location = this._foundLocations[resultIndex];
            int indexStart = location._label.indexOf("(");
            int indexEnd = location._label.indexOf(")");
            if (indexStart != -1 && indexEnd != -1) {
               location._label = ((StringBuffer)(new Object()))
                  .append(location._label.substring(0, indexStart))
                  .append(location._label.substring(indexEnd + 1, location._label.length()))
                  .toString();
            }

            int index = location._label.indexOf(" ,");
            if (index != -1) {
               location._label = ((StringBuffer)(new Object()))
                  .append(location._label.substring(0, index))
                  .append(location._label.substring(index + 1, location._label.length()))
                  .toString();
            }
         }
      }

      return location;
   }

   @Override
   public final void radioChecked(boolean radioTurnedOn, boolean dataServicesEnabled, boolean outOfCoverage) {
      if (radioTurnedOn && !outOfCoverage && dataServicesEnabled) {
         Location location = this.performSearchLocation(this._callbackLocation, this._callbackModel);
         if (this._screen != null && this._screen instanceof DirectionsChoiceScreen) {
            switch (this._searchType) {
               case -1:
                  break;
               case 0:
               default:
                  this._screen.callbackSelect(location);
                  return;
               case 1:
                  if (!this._screen.searchUserEnteredAddress(location)) {
                     return;
                  }

                  this._screen.searchDirections();
                  return;
               case 2:
                  if (!this._screen.locationFound(location)) {
                     return;
                  }

                  this._screen.searchDirections();
                  return;
            }
         } else if (location != null) {
            FinderHistory.getInstance().add(location);
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            LBSApplication.displayMap(location);
         }
      }
   }

   private final void invokeAndWait(int msgId) {
      if (msgId == 334) {
         String netType = (RadioInfo.getActiveWAFs() & 4) == 4 ? LBSResources.getString(336) : LBSResources.getString(335);
         String msg = MessageFormat.format(LBSResources.getString(334), new Object[]{netType});
         UiApplication.getUiApplication().invokeAndWait(new MessageRunnable(msg));
      } else {
         UiApplication.getUiApplication().invokeAndWait(new MessageRunnable(LBSResources.getString(msgId)));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void requestComplete(Request request) {
      if (request == null) {
         this._cancelled = true;
      } else {
         if (!this._cancelled) {
            System.out.println(((StringBuffer)(new Object("Response code="))).append(request.getResponseCode()).toString());
            boolean logError = false;
            int responseCode = request.getResponseCode();
            if (this._progressDialog != null) {
               this._progressDialog.stop();
               this._progressDialog = null;
            }

            switch (responseCode) {
               case -100:
               case 0:
                  this._foundLocations = ((SearchRequest)request).getResults();
                  break;
               case -11:
                  this.invokeAndWait(490);
                  logError = true;
                  break;
               case -10:
                  this.invokeAndWait(489);
                  logError = true;
                  break;
               case -9:
                  this.invokeAndWait(488);
                  logError = true;
                  break;
               case -8:
                  this.invokeAndWait(487);
                  logError = true;
                  break;
               case -7:
                  this.invokeAndWait(486);
                  logError = true;
                  break;
               case -6:
                  this.invokeAndWait(334);
                  logError = true;
                  break;
               case 2:
                  this.invokeAndWait(64);
                  break;
               case 3:
                  if (!this.verifyModelAddress()) {
                     this.invokeAndWait(483);
                     break;
                  }
               case 1:
               case 4:
               case 5:
                  this.invokeAndWait(39);
                  break;
               case 6:
                  this.invokeAndWait(59);
                  break;
               case 7:
                  this.invokeAndWait(148);
                  break;
               case 8:
               case 9:
               case 10:
                  this.invokeAndWait(149);
                  break;
               case 11:
               case 15:
                  this.invokeAndWait(150);
                  break;
               case 13:
                  this.invokeAndWait(152);
                  break;
               case 14:
                  this.invokeAndWait(151);
                  break;
               case 404:
               case 500:
                  this.invokeAndWait(103);
                  logError = true;
                  break;
               default:
                  logError = responseCode != 0;
            }

            if (logError) {
               try {
                  StringBuffer requestStr = (StringBuffer)(new Object());
                  byte[] b = request._lastRequest;
                  if (b != null) {
                     label119:
                     try {
                        for (int i = 0; i < b.length; i++) {
                           String s = ((StringBuffer)(new Object("0"))).append(Integer.toHexString(b[i])).toString();
                           requestStr.append(s.substring(s.length() - 2, s.length()));
                        }
                     } catch (Throwable var12) {
                        requestStr.append(((StringBuffer)(new Object("... error in parsing: "))).append(e.getMessage()).toString());
                        break label119;
                     }
                  } else {
                     requestStr.append("<empty>");
                  }

                  EventLogger.logEvent(
                     LBSApplication.UID,
                     ((StringBuffer)(new Object("Search Request error: ")))
                        .append(responseCode)
                        .append(", URL: ")
                        .append(request.getURL())
                        .append(", request: ")
                        .append(requestStr.toString())
                        .toString()
                        .getBytes(),
                     2
                  );
                  return;
               } finally {
                  return;
               }
            }
         }
      }
   }

   private final boolean verifyModelAddress() {
      boolean result = true;
      if (this._callbackModel != null) {
         String addressText = "";
         addressText = this._callbackModel.getAddressLine1();
         if (addressText == null || addressText.length() == 0) {
            addressText = this._callbackModel.getAddressLine2();
         }

         if (addressText == null || addressText.length() == 0) {
            addressText = this._callbackModel.getCity();
         }

         if (addressText == null || addressText.length() == 0) {
            result = false;
         }
      }

      return result;
   }

   private final MapPoint isLatLongPair(MailingAddressModel model) {
      try {
         String text = model.getAddressLine1();
         int ix = text.indexOf(44);
         if (ix > -1) {
            String numA = text.substring(0, ix).trim();
            String numB = text.substring(ix + 1, text.length()).trim();
            int x = (int)(Float.parseFloat(numB) * 1203982336);
            int y = (int)(Float.parseFloat(numA) * 1203982336);
            if (x < -18000000) {
               this._longOutRange = true;
               x = -18000000;
            } else if (x > 17999999) {
               this._longOutRange = x != 18000000;
               x = 17999999;
            }

            if (y < -9000000) {
               this._latOutRange = true;
               y = -9000000;
            } else if (y > 8999999) {
               this._latOutRange = y != 9000000;
               y = 8999999;
            }

            MapPoint p = new MapPoint();
            p._x = x;
            p._y = y;
            return p;
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public final void progressTick(int current, int total) {
   }

   @Override
   public final void setState(int state) {
   }
}
