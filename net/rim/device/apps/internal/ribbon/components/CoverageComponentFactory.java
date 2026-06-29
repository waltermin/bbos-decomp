package net.rim.device.apps.internal.ribbon.components;

import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.ui.Font;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.internal.crypto.vpn.VPN;
import net.rim.device.internal.crypto.vpn.VPNListener;
import net.rim.device.internal.system.LEDEngine;
import net.rim.device.internal.ui.UiSettings;
import net.rim.vm.DebugSupport;

final class CoverageComponentFactory implements Factory, RadioStatusListener, GlobalEventListener, ServiceRoutingListener2, WLANListenerInternal, VPNListener {
   private String _lastCoverageText;
   private int _lastCoverage = -1;
   private int _lastCoverageTextId;
   private String _fakeCoverageText;
   private int _blackberryDataRouteType;
   private int _lastEmailApnId = -1;
   private int _lastApnCDMA = 0;
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private int[] _coverageStringIndexes;
   private ComponentFactoryHelper _helper = new ComponentFactoryHelper();
   private static int ROUTE_TYPE_RF = 1;
   private static int ROUTE_TYPE_WIFI = 2;
   private static int ROUTE_TYPE_SERIAL = 4;
   private static final int NO_COVERAGE = 0;
   private static final int VOICE_COVERAGE = 1;
   private static final int DATA_COVERAGE = 2;
   private static final int DATA_COVERAGE_ATTACHED = 3;
   private static final int DATA_COVERAGE_WITH_PATH = 4;
   private static final int DATA_COVERAGE_WITH_EMAIL = 5;
   private static final int DATA_ONLY_COVERAGE_WITH_EMAIL = 6;
   private static final int EMERGENCY_COVERAGE_ONLY = 7;
   private static final int DATA_COVERAGE_HS = 8;
   private static final int DATA_COVERAGE_HS_WITH_EMAIL = 9;
   static final int NUMBER_OF_LEVELS = 10;
   static final int IDEN_EMERGENCY_COVERAGE_ONLY = 1;
   static final int IDEN_BASE_BITMAP_INDEX = 2;
   private static int[] _GPRSCoverageIndex = new int[]{
      67,
      59,
      59,
      60,
      60,
      62,
      67,
      67,
      119,
      120,
      -804650998,
      67,
      67,
      67,
      67,
      67,
      161,
      67,
      67,
      67,
      161,
      -804650990,
      67,
      74,
      48,
      49,
      58,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      6,
      7,
      8,
      9,
      15
   };
   private static int[] _UMTSCoverageIndex = new int[]{
      67,
      137,
      137,
      138,
      138,
      139,
      67,
      67,
      137,
      139,
      -804650998,
      67,
      160,
      160,
      158,
      158,
      159,
      67,
      67,
      158,
      159,
      426115328,
      1929445485,
      1979777066,
      8556063,
      1802466817,
      1979777125,
      1281715055,
      16780049,
      67159477,
      526976000,
      1812332780,
      100689154,
      1097138796,
      1812332740,
      -1002347262,
      -848269247,
      134247362,
      1980958720,
      556140544
   };
   private static int[] _CDMACoverageIndex = new int[]{
      63,
      64,
      64,
      65,
      144,
      66,
      143,
      63,
      118,
      87,
      -804650998,
      67,
      33,
      33,
      33,
      33,
      19,
      67,
      67,
      33,
      19,
      -804650998,
      67,
      59,
      59,
      60,
      60,
      62,
      67,
      67,
      119,
      120,
      -804650998,
      67,
      67,
      67,
      67,
      67,
      161,
      67
   };
   private static int[] _IDENCoverageIndex_NXTL = new int[]{
      67,
      74,
      48,
      49,
      58,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      6,
      7,
      8,
      9,
      15,
      -804650990,
      67,
      74,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      -804650990,
      67,
      74,
      121,
      122,
      123,
      124,
      125,
      126,
      127,
      128,
      129,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      -804650998,
      67,
      137,
      137,
      138,
      138,
      139,
      67,
      67,
      137,
      139,
      -804650998,
      67,
      160,
      160,
      158
   };
   private static int[] _IDENCoverageIndex_MIKE = new int[]{
      67,
      74,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      -804650990,
      67,
      74,
      121,
      122,
      123,
      124,
      125,
      126,
      127,
      128,
      129,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      -804650998,
      67,
      137,
      137,
      138,
      138,
      139,
      67,
      67,
      137,
      139,
      -804650998,
      67,
      160,
      160,
      158,
      158,
      159,
      67,
      67,
      158,
      159,
      426115328,
      1929445485,
      1979777066,
      8556063,
      1802466817,
      1979777125,
      1281715055,
      16780049,
      67159477,
      526976000,
      1812332780,
      100689154,
      1097138796
   };
   private static int[] _IDENCoverageIndex_IDEN = new int[]{
      67,
      74,
      121,
      122,
      123,
      124,
      125,
      126,
      127,
      128,
      129,
      130,
      131,
      132,
      133,
      134,
      135,
      136,
      -804650998,
      67,
      137,
      137,
      138,
      138,
      139,
      67,
      67,
      137,
      139,
      -804650998,
      67,
      160,
      160,
      158,
      158,
      159,
      67,
      67,
      158,
      159,
      426115328,
      1929445485,
      1979777066,
      8556063,
      1802466817,
      1979777125,
      1281715055,
      16780049,
      67159477,
      526976000,
      1812332780,
      100689154,
      1097138796,
      1812332740,
      -1002347262,
      -848269247,
      134247362,
      1980958720,
      556140544,
      1382301796,
      274883177,
      191766850,
      1637835088,
      134250082,
      8288550,
      8529416,
      555427848,
      671612935,
      1634077556,
      1091043335,
      1344866147,
      743350127
   };
   private static int[] _WLANCoverageIndex = new int[]{
      67,
      33,
      33,
      33,
      33,
      19,
      67,
      67,
      33,
      19,
      -804650998,
      67,
      59,
      59,
      60,
      60,
      62,
      67,
      67,
      119,
      120,
      -804650998,
      67,
      67,
      67,
      67,
      67,
      161,
      67,
      67,
      67,
      161,
      -804650990,
      67,
      74,
      48,
      49,
      58,
      79,
      80
   };
   private static int[] _GANCoverageIndex = new int[]{
      67,
      160,
      160,
      158,
      158,
      159,
      67,
      67,
      158,
      159,
      426115328,
      1929445485,
      1979777066,
      8556063,
      1802466817,
      1979777125,
      1281715055,
      16780049,
      67159477,
      526976000,
      1812332780,
      100689154,
      1097138796,
      1812332740,
      -1002347262,
      -848269247,
      134247362,
      1980958720,
      556140544,
      1382301796,
      274883177,
      191766850,
      1637835088,
      134250082,
      8288550,
      8529416,
      555427848,
      671612935,
      1634077556,
      1091043335
   };
   private static int[] _SerialCoverageIndex = new int[]{
      67,
      67,
      67,
      67,
      67,
      161,
      67,
      67,
      67,
      161,
      -804650990,
      67,
      74,
      48,
      49,
      58,
      79,
      80,
      81,
      82,
      83,
      84,
      85,
      86,
      6,
      7,
      8,
      9,
      15,
      -804650990,
      67,
      74,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109
   };
   private static String IIF_APN;

   protected final int getMaxTextWidth(Font font) {
      if ((RadioInfo.getNetworkService() & 4096) != 0) {
         int gprsMaxWidth = this.getMaxCoverageTextWidth(font, _GPRSCoverageIndex);
         int umtsMaxWidth = this.getMaxCoverageTextWidth(font, _UMTSCoverageIndex);
         return Math.max(gprsMaxWidth, umtsMaxWidth);
      } else {
         return this.getMaxCoverageTextWidth(font, this._coverageStringIndexes);
      }
   }

   final void init() {
      if (6 != RadioInfo.getNetworkType()) {
         Application app = Application.getApplication();
         app.addRadioListener(this);
         app.addGlobalEventListener(this);
         ServiceRouting sr = ServiceRouting.getInstance();
         if (sr != null) {
            sr.addListener(this);
         }

         this.updateCoverageInformation();
         FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
         repos.addFactory("Coverage", this);
         this._fakeCoverageText = this.getFakeCoverageText();
      }
   }

   final String getCoverageText() {
      return this.hasFakeCoverage() ? this._fakeCoverageText : this._lastCoverageText;
   }

   final String getFakeCoverageText() {
      return DeviceInfo.isSimulator() ? DebugSupport.getenv("JvmFakeCoverage") : null;
   }

   final boolean hasFakeCoverage() {
      return this._fakeCoverageText != null && RadioInfo.getState() == 1;
   }

   @Override
   public final Object createInstance(Object initialData) {
      CoverageField cf = new CoverageField(this);
      this._helper.addComponentForUpdate(cf);
      return cf;
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateCoverageInformation(service);
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateCoverageInformation(service);
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      this._lastApnCDMA = apn;
      this.updateCoverageInformation();
   }

   @Override
   public final void radioTurnedOff() {
      this.updateCoverageInformation();
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256 ^ this._lastCoverage == 0) {
         this.updateCoverageInformation();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         this.updateCoverageInformation();
      } else {
         if ((guid == -3864212166794284297L || guid == -6531073315810526672L || guid == 2200641410611652722L) && object0 == HRUtils.getDefaultHRT()) {
            this.updateCoverageInformation();
         }
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void vpnStatusChanged(int eventCode, int statusCode, int handle, int data) {
      if (RadioInfo.getSupportedWAFs() == 4) {
         switch (eventCode) {
            case 8960:
               if (statusCode != 0) {
                  break;
               }
            case 8961:
            case 8966:
               this.updateCoverageInformation();
               return;
         }
      }
   }

   @Override
   public final void radioStatus(boolean started) {
      if (RadioInfo.getSupportedWAFs() == 4) {
         this.updateCoverageInformation();
      }
   }

   @Override
   public final void networkSuccess() {
      if (RadioInfo.getSupportedWAFs() == 4) {
         this.updateCoverageInformation();
      }
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      if (RadioInfo.getSupportedWAFs() == 4) {
         this.updateCoverageInformation();
      }
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      ServiceRouting sr = ServiceRouting.getInstance();
      ServiceRoutingProperties srs = sr.getInterface(routeHandle);
      if (srs != null) {
         int linkType = srs.getLinkType();
         int[] handles = sr.getRouteHandles(linkType);
         if (handles != null && handles.length > 0) {
            for (int i = handles.length - 1; i >= 0; i--) {
               boolean state = sr.isServiceRoutable(null, handles[i]);
               if (routeHandle == handles[i] && routeState != state && routeState) {
                  ServiceRoutingProperties srps = sr.getInterface(routeHandle);
                  if (srps != null && srps.getLinkType() == 1 && RadioInfo.isDataServiceSuspended()) {
                     state = true;
                  }
               }

               routeState = state;
               if (routeState) {
                  break;
               }
            }

            int route = 0;
            switch (linkType) {
               case 0:
                  return;
               case 1:
               default:
                  route = ROUTE_TYPE_RF;
                  break;
               case 2:
                  route = ROUTE_TYPE_SERIAL;
                  break;
               case 3:
                  route = ROUTE_TYPE_WIFI;
            }

            if (routeState) {
               this._blackberryDataRouteType |= route;
            } else {
               this._blackberryDataRouteType &= ~route;
            }

            this.updateCoverageInformation();
         }
      }
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
   }

   private final String getAPN() {
      return ServiceRouting.getInstance().getServiceApn();
   }

   private final int getMaxCoverageTextWidth(Font font, int[] coverageStringIndexes) {
      int maxWidth = 0;
      int max = coverageStringIndexes.length;

      for (int index = 0; index < max; index++) {
         String str = this._rbf.getString(coverageStringIndexes[index]);
         int thisWidth = font.getBounds(str);
         if (thisWidth > maxWidth) {
            maxWidth = thisWidth;
         }
      }

      return maxWidth;
   }

   CoverageComponentFactory() {
      int supportedNetworks = RadioInfo.getSupportedWAFs();
      if ((supportedNetworks & 2) != 0) {
         this._coverageStringIndexes = _CDMACoverageIndex;
      } else if ((supportedNetworks & 1) != 0) {
         this._coverageStringIndexes = _GPRSCoverageIndex;
      } else if ((supportedNetworks & 8) != 0) {
         this._coverageStringIndexes = _IDENCoverageIndex_IDEN;
      } else {
         if ((supportedNetworks & 4) != 0) {
            this._coverageStringIndexes = _WLANCoverageIndex;
         }
      }
   }

   private final void updateCoverageInformation() {
      this.updateCoverageInformation(RadioInfo.getNetworkService());
   }

   private final void updateCoverageInformation(int networkService) {
      int newCoverage = this.determineCoverage(networkService);
      int newCoverageTextId = this._coverageStringIndexes[newCoverage];
      if (newCoverageTextId != this._lastCoverageTextId) {
         System.out.println("** Determining Coverage **");
         System.out.println("Last Coverage = " + this._lastCoverage);
         this._lastCoverage = newCoverage;
         System.out.println("New Coverage = " + this._lastCoverage);
         this._lastCoverageTextId = newCoverageTextId;
         this._lastCoverageText = this._rbf.getString(newCoverageTextId);
         System.out.println("New Coverage Text = " + (this._lastCoverageText == null ? "NULL" : this._lastCoverageText));
      }

      LEDEngine ledEngine = LEDEngine.getInstance();
      if (ledEngine != null) {
         if (Phone.isSupported() && (networkService & 2) != 0 && UiSettings.getLEDCoverageIndicatorStatus()) {
            ledEngine.setFlag(8);
         } else {
            ledEngine.clearFlag(8);
         }
      } else {
         this._lastCoverage = -1;
      }

      this._helper.doUpdates();
   }

   private final int determineCoverage(int networkService) {
      int coverage = 0;
      int radioState = RadioInfo.getState();
      if (radioState == 1) {
         int activeNetwork = RadioInfo.getActiveWAFs();
         if ((activeNetwork & 1) == 0) {
            if ((activeNetwork & 2) != 0) {
               this._coverageStringIndexes = _CDMACoverageIndex;
               if ((networkService & 2) != 0) {
                  coverage = 1;
               }

               if ((networkService & 4) != 0) {
                  coverage = 3;
                  boolean isHighSpeed = (networkService & 1024) != 0;
                  boolean isEVOnly = (networkService & 8192) != 0;
                  if ((this._blackberryDataRouteType & ROUTE_TYPE_RF) == ROUTE_TYPE_RF && RadioInfo.isPDPContextActive(this._lastApnCDMA)) {
                     if (isHighSpeed) {
                        return 9;
                     }

                     if (isEVOnly) {
                        return 6;
                     }

                     return 5;
                  }

                  if (isHighSpeed) {
                     return 8;
                  }

                  if (isEVOnly) {
                     return 4;
                  }
               }
            } else if ((activeNetwork & 8) == 0) {
               if ((activeNetwork & 4) != 0) {
                  if (RadioInfo.getSupportedWAFs() != 4) {
                     return coverage;
                  }

                  if (RadioInfo.getSignalLevel() != -256) {
                     boolean wFlag = WLAN.isSupported() ? WLAN.isAssociated() != null : (networkService & 4) != 0;
                     boolean aFlag = wFlag;
                     boolean vFlag = wFlag && (networkService & 2) != 0;
                     boolean eFlag = wFlag && VPN.isConnected();
                     coverage = 1;
                     if (wFlag) {
                        coverage += 8;
                     }

                     if (aFlag) {
                        ServiceRouting sr = ServiceRouting.getInstance();
                        if (sr != null && sr.isServiceRoutable(null, -1)) {
                           coverage += 4;
                        }
                     }

                     if (vFlag) {
                        coverage += 2;
                     }

                     if (eFlag) {
                        return coverage + 1;
                     }
                  } else {
                     coverage = 0;
                  }
               }
            } else {
               if ((networkService & 1) != 0) {
                  return 1;
               }

               if (RadioInfo.getSignalLevel() == -256) {
                  return 0;
               }

               if (Branding.isFast100Carrier()) {
                  this._coverageStringIndexes = _IDENCoverageIndex_IDEN;
               } else {
                  int idenMCC = IDENInfo.getHomeMCC();
                  int idenNDC = IDENInfo.getHomeNDC();
                  if (idenMCC == 0 && idenNDC == 0) {
                     return 0;
                  }

                  if (idenMCC == 770 && idenNDC == 864) {
                     this._coverageStringIndexes = _IDENCoverageIndex_MIKE;
                  } else {
                     this._coverageStringIndexes = _IDENCoverageIndex_NXTL;
                  }
               }

               boolean nFlag = (networkService & 2) != 0;
               boolean xFlag = (networkService & 32) != 0;
               boolean tFlag = (networkService & 4) != 0;
               boolean lFlag = (this._blackberryDataRouteType & ROUTE_TYPE_RF) == ROUTE_TYPE_RF ? tFlag && RadioInfo.isPDPContextActive(0) : false;
               coverage = 2;
               if (nFlag) {
                  coverage += 8;
               }

               if (xFlag) {
                  coverage += 4;
               }

               if (tFlag) {
                  coverage += 2;
               }

               if (lFlag) {
                  return coverage + 1;
               }
            }
         } else {
            if ((networkService & 4096) != 0) {
               this._coverageStringIndexes = _UMTSCoverageIndex;
            } else if ((networkService & 16384) != 0) {
               this._coverageStringIndexes = _GANCoverageIndex;
            } else {
               this._coverageStringIndexes = _GPRSCoverageIndex;
            }

            boolean isHighSpeed = (networkService & 1024) != 0;
            if ((networkService & 2) != 0) {
               coverage = 1;
            }

            if ((networkService & 4) != 0) {
               try {
                  String emailAPN = this.getAPN();
                  if (isHighSpeed) {
                     coverage = 8;
                  } else {
                     coverage = 3;
                  }

                  if ((this._blackberryDataRouteType & ROUTE_TYPE_RF) == ROUTE_TYPE_RF
                     && (emailAPN != null || IIF_APN != null)
                     && this._lastEmailApnId != -1
                     && RadioInfo.isPDPContextActive(this._lastEmailApnId)) {
                     String apn = RadioInfo.getAccessPointName(this._lastEmailApnId);
                     if (emailAPN != null && emailAPN.equalsIgnoreCase(apn) || IIF_APN != null && IIF_APN.equalsIgnoreCase(apn)) {
                        byte var14;
                        if (isHighSpeed) {
                           var14 = 9;
                        } else {
                           var14 = 5;
                        }

                        return var14;
                     }
                  }

                  this._lastEmailApnId = -1;

                  for (int apnId = 0; apnId < 7; apnId++) {
                     if (RadioInfo.isPDPContextActive(apnId)) {
                        if (isHighSpeed) {
                           coverage = 8;
                        } else {
                           coverage = 4;
                        }

                        if ((this._blackberryDataRouteType & ROUTE_TYPE_RF) == ROUTE_TYPE_RF && (emailAPN != null || IIF_APN != null)) {
                           String apn = RadioInfo.getAccessPointName(apnId);
                           if (emailAPN != null && emailAPN.equalsIgnoreCase(apn) || IIF_APN != null && IIF_APN.equalsIgnoreCase(apn)) {
                              byte var13;
                              if (isHighSpeed) {
                                 var13 = 9;
                              } else {
                                 var13 = 5;
                              }

                              this._lastEmailApnId = apnId;
                              return var13;
                           }
                        }
                     }
                  }
               } finally {
                  ;
               }
            }
         }
      }

      return coverage;
   }

   static {
      byte[] data = Branding.getData(13824);
      if (data != null) {
         IIF_APN = new String(data);
      }
   }
}
