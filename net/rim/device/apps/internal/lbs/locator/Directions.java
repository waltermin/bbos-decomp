package net.rim.device.apps.internal.lbs.locator;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.internal.lbs.CheckRadioConnections;
import net.rim.device.apps.internal.lbs.CheckRadioConnections$Callback;
import net.rim.device.apps.internal.lbs.DirectionsChoiceScreen;
import net.rim.device.apps.internal.lbs.LBSApplication;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.finder.FindLocationScreen;
import net.rim.device.apps.internal.lbs.model.SearchAddressModel;
import net.rim.device.apps.internal.lbs.protocol.DirectionsRequest;
import net.rim.device.apps.internal.lbs.protocol.Request;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.vm.Array;

public final class Directions implements Request$Listener, ActiveFieldCookie, VerbProvider, CheckRadioConnections$Callback {
   private boolean _responseReceived = false;
   private int _returnCode;
   protected String _routeXMLstring;
   protected byte[] _rawXMLData;
   private boolean _cancelled;
   public ProgressDialog _progressDialog;
   private SearchAddressModel _callbackModel = null;
   private DirectionsChoiceScreen _screen = null;
   private int _searchType = -1;
   String _startAddress;
   int _startLat;
   int _startLon;
   String _endAddress;
   int _endLat;
   int _endLon;

   final String replaceSpecialCharacters(String input) {
      StringBuffer output = (StringBuffer)(new Object());

      for (int i = 0; i < input.length(); i++) {
         char character = input.charAt(i);
         switch (character) {
            case '"':
               output.append("&quot;");
               break;
            case '&':
               output.append("&amp;");
               break;
            case '\'':
               output.append("&apos;");
               break;
            case '<':
               output.append("&lt;");
               break;
            case '>':
               output.append("&gt;");
               break;
            default:
               if (character > 127) {
                  output.append("&#");
                  output.append(Integer.toString(character));
                  output.append(";");
               } else {
                  output.append(character);
               }
         }
      }

      return output.toString();
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb verb = new GetDirectionsVerb(this);
      int index = verbs.length;
      Array.resize(verbs, index + 1);
      verbs[index] = verb;
      return verb;
   }

   public final void setCallback(DirectionsChoiceScreen screen, SearchAddressModel model, int searchType) {
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

   public final byte[] getRawData() {
      return this._rawXMLData;
   }

   public final String getDirections() {
      SearchAddressModel model = new SearchAddressModel(null);
      System.out.println("Directions.getDirections() sending a request to the server");
      this._returnCode = 31;
      if (RadioInfo.getActiveWAFs() != 0
         && (CoverageInfo.isCoverageSufficient(2) || CoverageInfo.isCoverageSufficient(4) || CoverageInfo.isCoverageSufficient(1))) {
         this._routeXMLstring = this.searchDirections(model);
         if (this._screen != null) {
            if (this._searchType != -1) {
               this._screen.UpdateDirections(this._routeXMLstring);
            } else {
               this._screen.showDirections(this._routeXMLstring, this);
            }
         }
      } else {
         CheckRadioConnections checkRadio = new CheckRadioConnections(this);
         this.setCallback(this._screen, model, -1);
         DirectionsRequest request = new DirectionsRequest(
            this, model, this._startAddress, this._endAddress, this._startLat, this._startLon, this._endLat, this._endLon
         );
         checkRadio.checkRadioConnection(request);
      }

      return this._routeXMLstring;
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return false;
   }

   @Override
   public final void radioChecked(boolean radioTurnedOn, boolean dataServicesEnabled, boolean outOfCoverage) {
      if (radioTurnedOn && !outOfCoverage && dataServicesEnabled) {
         this._routeXMLstring = this.searchDirections(this._callbackModel);
         if (this._searchType != -1) {
            this._screen.UpdateDirections(this._routeXMLstring);
         } else {
            this._screen.showDirections(this._routeXMLstring, this);
         }
      } else {
         if (this._screen != null) {
            this._screen.close();
         }
      }
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void requestComplete(Request request) {
      this._returnCode = 0;
      if (!this._cancelled) {
         this._cancelled = true;
         int rc = request.getResponseCode();
         if (rc != 0) {
            byte[] b = request._lastRequest;
            StringBuffer requestStr = (StringBuffer)(new Object());
            if (b != null) {
               label70:
               try {
                  for (int i = 0; i < b.length; i++) {
                     String s = ((StringBuffer)(new Object("0"))).append(Integer.toHexString(b[i])).toString();
                     requestStr.append(s.substring(s.length() - 2, s.length()));
                  }
               } catch (Throwable var11) {
                  requestStr.append(((StringBuffer)(new Object("... error in parsing: "))).append(e.getMessage()).toString());
                  break label70;
               }
            } else {
               requestStr.append("<empty>");
            }

            long UID = -1037010874164756539L;
            EventLogger.logEvent(
               -1037010874164756539L,
               ((StringBuffer)(new Object("Directions Request error: ")))
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

         if (request instanceof DirectionsRequest) {
            this._routeXMLstring = ((DirectionsRequest)request).getRouteXMLstring();
            this._rawXMLData = ((DirectionsRequest)request).getRawXMLData();
            System.out.println(((StringBuffer)(new Object("_routeXMLstring="))).append(this._routeXMLstring).toString());
            if (this._progressDialog == null) {
               this._responseReceived = true;
               synchronized (this) {
                  this.notify();
                  return;
               }
            }

            this._progressDialog._progressThread.stopProgress();
         }
      }
   }

   @Override
   public final void progressTick(int current, int total) {
   }

   @Override
   public final void setState(int state) {
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final String searchDirections(SearchAddressModel model) {
      if (this._startLat == Integer.MAX_VALUE || this._startLon == Integer.MAX_VALUE || this._endLat == Integer.MAX_VALUE || this._endLon == Integer.MAX_VALUE) {
         Location location = null;
         GPSLocationData gpsLocationData = (GPSLocationData)(new Object());
         if (gpsLocationData != null && gpsLocationData.isValid()) {
            location = new Location(gpsLocationData.getLatitudeInt(), gpsLocationData.getLongitudeInt(), 0);
         } else {
            location = FindLocationScreen.getGPSLocation();
         }

         if (this._startLat == Integer.MAX_VALUE && this._startLon == Integer.MAX_VALUE) {
            this._startAddress = LBSResources.getString(416);
            this._startLat = location._latitude;
            this._startLon = location._longitude;
         }

         if (this._endLat == Integer.MAX_VALUE && this._endLon == Integer.MAX_VALUE) {
            this._endAddress = LBSResources.getString(416);
            this._endLat = location._latitude;
            this._endLon = location._longitude;
         }
      }

      DirectionsRequest.request(this, model, this._startAddress, this._endAddress, this._startLat, this._startLon, this._endLat, this._endLon);
      if (!this._responseReceived) {
         this._progressDialog = new ProgressDialog(this, LBSResources.getString(231), 0, 30);
         this._progressDialog.doModal();
         if (this._progressDialog.isCancelPressed()) {
            return null;
         }
      }

      switch (this._returnCode) {
         case -11:
            Dialog.alert(LBSResources.getString(490));
            return null;
         case -10:
            Dialog.alert(LBSResources.getString(489));
            return null;
         case -9:
            Dialog.alert(LBSResources.getString(488));
            return null;
         case -8:
            Dialog.alert(LBSResources.getString(487));
            return null;
         case -7:
            Dialog.alert(LBSResources.getString(486));
            return null;
         case -6:
            this.coverageErrorMsg();
            return null;
         case -4:
         case 500:
            Dialog.alert(LBSResources.getString(103));
            return null;
         case 1:
         case 2:
         case 5:
            Dialog.alert(LBSResources.getString(201));
            return null;
         case 3:
            Dialog.alert(LBSResources.getString(197));
            return null;
         case 4:
            Dialog.alert(LBSResources.getString(198));
            return null;
         case 30:
            Dialog.alert(LBSResources.getString(200));
            return null;
         default:
            if (this._routeXMLstring != null) {
               if (!this._routeXMLstring.equals("")) {
                  int index = this._routeXMLstring.indexOf("<instruction ");
                  int offset = 13;
                  if (index == -1) {
                     index = this._routeXMLstring.indexOf("<location ");
                     offset = 10;
                  }

                  if (index != -1) {
                     int firstIndex = index;
                     if (this._startAddress == null) {
                        this._startAddress = "";
                     }

                     String address = this.replaceSpecialCharacters(this._startAddress);
                     this._routeXMLstring = ((StringBuffer)(new Object()))
                        .append(this._routeXMLstring.substring(0, index + offset))
                        .append("address='")
                        .append(address)
                        .append("' ")
                        .append(this._routeXMLstring.substring(index + offset, this._routeXMLstring.length()))
                        .toString();
                     int var15 = 13;

                     int lastIndex;
                     for (lastIndex = index; index != -1; index = this._routeXMLstring.indexOf("<instruction ", lastIndex + var15)) {
                        lastIndex = index;
                     }

                     if (lastIndex == firstIndex) {
                        var15 = 10;
                        index = firstIndex;

                        for (lastIndex = index; index != -1; index = this._routeXMLstring.indexOf("<location ", lastIndex + var15)) {
                           lastIndex = index;
                        }
                     }

                     if (lastIndex != -1 && lastIndex != firstIndex) {
                        if (this._endAddress == null) {
                           this._endAddress = "";
                        }

                        address = this.replaceSpecialCharacters(this._endAddress);
                        this._routeXMLstring = ((StringBuffer)(new Object()))
                           .append(this._routeXMLstring.substring(0, lastIndex + var15))
                           .append("address='")
                           .append(address)
                           .append("' ")
                           .append(this._routeXMLstring.substring(lastIndex + var15, this._routeXMLstring.length()))
                           .toString();
                     }
                  }
               }

               String encoding = "UTF-8";
               boolean var8 = false /* VF: Semaphore variable */;

               label147:
               try {
                  var8 = true;
                  int var16 = this._routeXMLstring.indexOf("encoding=");
                  if (var16 != -1) {
                     var16 += 9;
                     encoding = this._routeXMLstring.substring(var16 + 1, this._routeXMLstring.indexOf(39, var16 + 1));
                  }

                  this._rawXMLData = this._routeXMLstring.getBytes(encoding);
                  var8 = false;
               } finally {
                  if (var8) {
                     EventLogger.logEvent(LBSApplication.UID, ((StringBuffer)(new Object("UnsupportedEncoding: "))).append(encoding).toString().getBytes(), 2);
                     this._rawXMLData = this._routeXMLstring.getBytes();
                     break label147;
                  }
               }
            }

            System.out.println(((StringBuffer)(new Object("_routeXMLstring="))).append(this._routeXMLstring).toString());
            return this._routeXMLstring;
      }
   }

   private final void coverageErrorMsg() {
      String netType = (RadioInfo.getActiveWAFs() & 4) == 4 ? LBSResources.getString(336) : LBSResources.getString(335);
      String msg = MessageFormat.format(LBSResources.getString(334), new Object[]{netType});
      Dialog.alert(msg);
   }

   public Directions(String startAddress, int startLat, int startLon, String endAddress, int endLat, int endLon) {
      this._startAddress = startAddress;
      this._startLat = startLat;
      this._startLon = startLon;
      this._endAddress = endAddress;
      this._endLat = endLat;
      this._endLon = endLon;
   }
}
