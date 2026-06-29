package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.hrt.GprsHRI;
import net.rim.device.api.hrt.HRUtils;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.ExtendedRadioStatusListener;
import net.rim.device.api.system.GAN;
import net.rim.device.api.system.GANServiceZoneInfo;
import net.rim.device.api.system.GANStatusListener;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GPRSInfo$GPRSCellInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardEFListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANSystem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.WorldPhoneDisclaimerDialog;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.SIMCardEfHandlerCallback;
import net.rim.device.internal.system.SIMCardEfTask;
import net.rim.device.internal.system.SIMServiceTable;

public final class SystemMonitor
   extends RibbonNetworkInfo
   implements SIMCardEfHandlerCallback,
   SIMCardStatusListener,
   SIMCardEFListener,
   ExtendedRadioStatusListener,
   GlobalEventListener,
   SystemListener,
   GANStatusListener,
   Runnable {
   private AggregatedNetworkProps _networkProps = new AggregatedNetworkProps();
   private NITZNetworkName _NITZNetworkName = new NITZNetworkName();
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   private int _SSTlen;
   private int _ESTlen;
   private int _USTlen;
   private OPLRecord[] _oplData;
   private int _oplIndex;
   private String _spnString;
   private String _cphsOnLongString;
   private String _cphsOnsShortString;
   private boolean _simCachePopulated;
   private boolean _homePLMNOnly;
   private SIMCardEfHandler _efHandler;
   private int[] _pdpErrorCauseContainer = new int[1];
   private int _simStatusId = -1;
   private boolean _simPresent;
   private int _netStatusId = -1;
   private int _coverageSearchTimerId = -1;
   private int _coverageSearchState = 0;
   private int _onsRenderMode;
   private byte[] _simBuffer = new byte[256];
   private int[] _spdiList;
   private SystemMonitor$WLANMonitor _wlanMonitor = new SystemMonitor$WLANMonitor(this);
   private static final int WORLD_PHONE_WAFS = 3;
   private static final int SUPPORTED_WAFS = RadioInfo.getSupportedWAFs();
   private static final boolean CDMA_GSM_WORLD_PHONE = (SUPPORTED_WAFS & 3) == 3;
   private static final int SIM_STATUS_NULL = -1;
   private static final int NET_STATUS_NULL = -1;
   private static final int CSS_RADIO_OFF = 0;
   private static final int CSS_SEARCHING = 1;
   private static final int CSS_NET_FOUND = 2;
   private static final int CSS_OUT_OF_COVERAGE = 3;
   private static final int EVENT_SIGNAL_LEVEL = 0;
   private static final int EVENT_RADIO_OFF = 1;
   private static final int EVENT_NETWORK_FOUND = 2;
   private static final int EVENT_TIMEOUT = 3;
   private static final int SEARCHING_TIMEOUT = 120000;
   public static final int ONS_RENDER_UNASSIGNED = -1;
   public static final int ONS_RENDER_DEFAULT = 0;
   public static final int ONS_RENDER_SPN_1 = 1;
   public static final int ONS_RENDER_SPN_SPDI = 2;
   private static byte[] UST_TO_COMMON_ST_INDEX = new byte[]{
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      28,
      27,
      29,
      30,
      31,
      32,
      33,
      34,
      35,
      36,
      37,
      38,
      39,
      40,
      41,
      42,
      43,
      44,
      45,
      46,
      47,
      48,
      25,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      58,
      59,
      60,
      61,
      62,
      63,
      64,
      65,
      66,
      67,
      68,
      69,
      70
   };
   private static byte[] EST_TO_COMMON_ST_INDEX = new byte[]{1, 5, 34};
   private static byte[] SST_TO_COMMON_ST_INDEX = new byte[]{
      71,
      72,
      1,
      9,
      12,
      73,
      74,
      25,
      20,
      75,
      2,
      11,
      76,
      14,
      16,
      17,
      18,
      3,
      4,
      25,
      55,
      56,
      23,
      24,
      28,
      27,
      77,
      78,
      79,
      15,
      5,
      6,
      35,
      36,
      10,
      54,
      80,
      81,
      21,
      22,
      82,
      31,
      19,
      41,
      42,
      38,
      39,
      83,
      40,
      25,
      44,
      45,
      46,
      47,
      48,
      49,
      50,
      51,
      53
   };

   static final void init() {
      SystemMonitor sm = new SystemMonitor();
      Application app = Application.getApplication();
      ApplicationRegistry.getApplicationRegistry().put(RibbonNetworkInfo.NETWORK_INFO_SINGLETON_GUID, sm);
      app.addRadioListener(sm);
      SIMCard.addListener(app, sm);
      app.addGlobalEventListener(sm);
      app.addSystemListener(sm);
      int networkService = RadioInfo.getNetworkService();
      int status = DeviceInfo.getBatteryStatus();
      if ((status & 268435456) != 0) {
         sm.setNetworkProp(-1986748551626928033L, appendHomeZoneInfo(networkService, sm._rbf.getString(50)));
      }

      sm.updateONSActiveNetwork(networkService);
      sm.updateActivationRequired();
   }

   private SystemMonitor() {
      if (BackdoorKeyProcessor.isDevelopmentDevice()) {
         RibbonApi._logONSState = true;
      }
   }

   public static final SystemMonitor getSystemMonitor() {
      return (SystemMonitor)ApplicationRegistry.getApplicationRegistry().get(RibbonNetworkInfo.NETWORK_INFO_SINGLETON_GUID);
   }

   public final void setONSRenderMode(int newMode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private final int getONSRenderMode() {
      if (this._onsRenderMode == -1) {
         this._onsRenderMode = 0;
         byte[] data = Branding.getData(22);
         if (data != null && data.length >= 1) {
            switch (data[0]) {
               case 0:
                  break;
               case 1:
               case 2:
               default:
                  this._onsRenderMode = data[0];
            }
         }

         if (this._onsRenderMode == 0 && this._spnString != null && this._spnString.length() > 0 && SIMServiceTable.isServiceEnabled(49)) {
            this._onsRenderMode = 2;
         }

         switch (Branding.getVendorId()) {
            case 102:
               this._onsRenderMode = 1;
            default:
               try {
                  byte[] imsi = SIMCard.getIMSI();
                  if (SIMCard.getMCCFromIMSI(imsi) == 262 && SIMCard.getMNCFromIMSI(imsi) == 3) {
                     this._onsRenderMode = 1;
                  }
               } finally {
                  return this._onsRenderMode;
               }
         }
      }

      return this._onsRenderMode;
   }

   private final void resetSIMCache() {
      this._oplData = null;
      this._SSTlen = 0;
      this._ESTlen = 0;
      this._USTlen = 0;
      this._spnString = null;
      this._cphsOnLongString = null;
      this._cphsOnsShortString = null;
      this._homePLMNOnly = false;
      this._simCachePopulated = false;
   }

   private final void simCachePopulated() {
      this._simCachePopulated = true;
      this.loadSPDI();
      this.updateONSActiveNetwork(RadioInfo.getNetworkService());
      if (RibbonApi._logONSState && this._oplData != null) {
         StringBuffer sb = new StringBuffer();
         System.out.println("OPL Data:");

         for (int i = 0; i < this._oplData.length; i++) {
            OPLRecord record = this._oplData[i];
            if (record != null) {
               sb.append('[').append(Integer.toString(record._mccSmallest, 16)).append(',').append(Integer.toString(record._mccLargest, 16)).append(']');
               sb.append('[').append(Integer.toString(record._mncSmallest, 16)).append(',').append(Integer.toString(record._mncLargest, 16)).append(']');
               sb.append('[').append(record._lacSmallest).append(',').append(record._lacLargest).append(']');
               sb.append("->").append(record._pnnIndex);
               if (record._pnnName != null) {
                  sb.append(':').append(record._pnnName);
               }

               System.out.println(sb.toString());
               sb.setLength(0);
            }
         }
      }

      if (SIMServiceTable.isServiceEnabled(47)) {
         VoicemailIconManager.getInstance().initializeMWIS();
      }
   }

   private final void loadHomeNetworkNamesToCache() {
      if (this._spnString == null && SIMServiceTable.isSPNEnabled()) {
         this._spnString = this.getSIMString(11, 1);
         if (this._spnString != null && this._spnString.length() == 0) {
            this._spnString = null;
         }

         if (this._spnString != null) {
            if (RibbonApi._logONSState) {
               System.out.println("loaded SPN: " + this._spnString);
            }

            this._spnString = this._spnString.trim();
         } else if (RibbonApi._logONSState) {
            System.out.println("SPN not loaded.");
         }
      }

      if (this._cphsOnLongString == null) {
         this._cphsOnLongString = this.getSIMString(51, 0);
         if (RibbonApi._logONSState) {
            if (this._cphsOnLongString != null) {
               System.out.println("loaded cphson: " + this._cphsOnLongString);
            } else {
               System.out.println("cphson not loaded.");
            }
         }
      }

      if (this._cphsOnsShortString == null) {
         this._cphsOnsShortString = this.getSIMString(55, 0);
         if (RibbonApi._logONSState) {
            if (this._cphsOnsShortString != null) {
               System.out.println("loaded cphsons: " + this._cphsOnsShortString);
               return;
            }

            System.out.println("cphsons not loaded.");
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void loadSIMCache() {
      boolean completed = false;
      boolean isUSIMPresent = false;

      label388:
      try {
         isUSIMPresent = SIMCard.isUSIMPresent();
      } finally {
         break label388;
      }

      if (isUSIMPresent) {
         SystemMonitor$USIM_ServiceTableReader reader = new SystemMonitor$USIM_ServiceTableReader(this, null);
         completed = reader.startRead();
      } else {
         SystemMonitor$SIM_ServiceTableReader reader = new SystemMonitor$SIM_ServiceTableReader(this, null);
         completed = reader.startRead();
      }

      if (completed) {
         if (!SIMServiceTable.isPNNEnabled()) {
            if (RibbonApi._logONSState) {
               System.out.println("*** PNN not allocated and activated");
            }

            this.simCachePopulated();
         } else {
            if (SIMServiceTable.isOPLEnabled()) {
               if (this._oplData == null) {
                  boolean var19 = false /* VF: Semaphore variable */;

                  try {
                     var19 = true;
                     SIMCard.requestEFInfo(60);
                     var19 = false;
                  } finally {
                     if (var19) {
                        this.simCachePopulated();
                        return;
                     }
                  }

                  this._oplIndex = 0;
                  return;
               }
            } else if (this._oplData == null) {
               this._homePLMNOnly = true;
               this._oplIndex = 0;
               this._oplData = new OPLRecord[1];
               if (RibbonApi._logONSState) {
                  System.out.println("*** OPL not activated, creating dummy entry");
               }

               OPLRecord record = new OPLRecord();
               record._mccSmallest = Integer.MIN_VALUE;
               record._mccLargest = Integer.MAX_VALUE;
               record._mncSmallest = Integer.MIN_VALUE;
               record._mncLargest = Integer.MAX_VALUE;
               record._lacSmallest = Integer.MIN_VALUE;
               record._lacLargest = Integer.MAX_VALUE;
               record._pnnIndex = 1;
               this._oplData[0] = record;
            }

            for (; this._oplIndex < this._oplData.length; this._oplIndex++) {
               OPLRecord record = this._oplData[this._oplIndex];
               if (record == null) {
                  int len;
                  try {
                     len = SIMCard.requestEFRead(60, 1, this._oplIndex + 1, this._simBuffer);
                  } finally {
                     continue;
                  }

                  if (len == -1) {
                     return;
                  }

                  if (this._simBuffer[0] == -1) {
                     continue;
                  }

                  record = new OPLRecord();
                  int number = 0;
                  number = this._simBuffer[0] & 15;
                  if (number == 13) {
                     record._mccLargest = 2304;
                  } else {
                     number <<= 8;
                     record._mccLargest = number;
                     record._mccSmallest = number;
                  }

                  number = (this._simBuffer[0] & 240) >> 4;
                  if (number == 13) {
                     record._mccLargest += 144;
                  } else {
                     number <<= 4;
                     record._mccLargest += number;
                     record._mccSmallest += number;
                  }

                  number = this._simBuffer[1] & 15;
                  if (number == 13) {
                     record._mccLargest += 9;
                  } else {
                     record._mccLargest += number;
                     record._mccSmallest += number;
                  }

                  number = this._simBuffer[2] & 15;
                  if (number == 13) {
                     record._mncLargest = 2304;
                  } else {
                     number <<= 8;
                     record._mncLargest = number;
                     record._mncSmallest = number;
                  }

                  number = (this._simBuffer[2] & 240) >> 4;
                  if (number == 13) {
                     record._mncLargest += 144;
                  } else {
                     number <<= 4;
                     record._mncLargest += number;
                     record._mncSmallest += number;
                  }

                  number = (this._simBuffer[1] & 240) >> 4;
                  if (number == 13) {
                     record._mncLargest += 9;
                  } else if (number != 15) {
                     record._mncLargest += number;
                     record._mncSmallest += number;
                  }

                  record._lacSmallest = (this._simBuffer[4] & 255) + ((this._simBuffer[3] & 255) << 8);
                  record._lacLargest = (this._simBuffer[6] & 255) + ((this._simBuffer[5] & 255) << 8);
                  record._pnnIndex = this._simBuffer[7];
                  this._oplData[this._oplIndex] = record;
               }

               if (record._pnnIndex == 0) {
                  record._pnnName = null;
               } else {
                  int len;
                  try {
                     len = SIMCard.requestEFRead(61, 1, record._pnnIndex, this._simBuffer);
                  } finally {
                     continue;
                  }

                  if (len == -1) {
                     return;
                  }

                  if (this._simBuffer[0] == -1) {
                     record._pnnName = null;
                  } else {
                     int strlength = this._simBuffer[1] + 2;
                     int spareBits = this._simBuffer[2] & 7;
                     record._showMCCInitials = (this._simBuffer[2] & 8) != 0;
                     if ((this._simBuffer[2] & 112) == 1) {
                        record._pnnName = this.convertUCS2DataToString(this._simBuffer, 3, strlength);
                     } else {
                        record._pnnName = this.convertGSM7DataToString(this._simBuffer, 3, strlength, spareBits);
                     }
                  }
               }
            }

            this.simCachePopulated();
         }
      }
   }

   private final void updateONSSpecifiedNetwork(int mcc, int mnc, int lac, String mccName, int category) {
      int networkService = RadioInfo.getNetworkService();
      String eonsString = null;
      boolean homeNetwork = (category & 4) != 0;
      if (this.isEONSReady()) {
         eonsString = this.getEONSString(networkService, mcc, mnc, lac, mccName, category);
      }

      String ons = null;
      if (eonsString != null) {
         ons = eonsString;
         if (RibbonApi._logONSState) {
            System.out.println("spec. net eons: " + eonsString);
         }
      } else if (homeNetwork) {
         if (this._spnString != null) {
            ons = this._spnString;
            if (RibbonApi._logONSState) {
               System.out.println("spec. net spn: " + this._spnString);
            }
         } else if (this._cphsOnLongString != null) {
            ons = this._cphsOnLongString;
            if (RibbonApi._logONSState) {
               System.out.println("spec. net cphson: " + this._cphsOnLongString);
            }
         }
      }

      if (RibbonApi._logONSState && ons == null) {
         System.out.println("spec. net: null");
      }

      this.setNetworkProp(-8817962046913284182L, ons);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void updateONSActiveNetwork(int networkService) {
      int activeNetworks = RadioInfo.getActiveWAFs();
      this.updateWiFiConnection(networkService);
      if ((activeNetworks & 8) != 0) {
         this.putOperatorName(IDENInfo.getHomeNetworkName());
      } else {
         if ((SUPPORTED_WAFS & 2) != 0 || RadioInternal.isUMTSCapable()) {
            if ((networkService & 2048) != 0) {
               this.setNetworkProp(2594167647522947521L, this._rbf.getString(78));
            } else {
               this.setNetworkProp(2594167647522947521L, null);
            }
         }

         if ((activeNetworks & 2) != 0) {
            boolean roaming = (8 & networkService) != 0 || (16 & networkService) != 0;
            if (Branding.getVendorId() == 122 && !roaming) {
               int index = RadioInfo.getCurrentNetworkIndex();
               String name = RadioInfo.getNetworkName(index);
               this.putOperatorName(name);
            } else {
               this.putOperatorName(null);
            }
         }

         if ((activeNetworks & 1) == 0) {
            this.setOperatorName(networkService, null);
         } else if (this._simCachePopulated) {
            if (RibbonApi._logONSState) {
               System.out.println("** Radio values for ONS **");
            }

            boolean var19 = false /* VF: Semaphore variable */;

            int var21;
            String var22;
            int var23;
            try {
               var19 = true;
               int mcc = RadioInfo.getCurrentNetworkIndex();
               var21 = RadioInfo.getNetworkId(mcc);
               var22 = RadioInfo.getNetworkCountryCode(mcc);
               var23 = RadioInternal.getNetworkCategory(mcc);
               var19 = false;
            } finally {
               if (var19) {
                  if (RibbonApi._logONSState) {
                     System.out.println("invalid current network index");
                  }

                  this.setOperatorName(networkService, null);
                  return;
               }
            }

            int mcc = var21 & 65535;
            int mnc = var21 >> 16 & 65535;
            GPRSInfo$GPRSCellInfo cInfo = GPRSInfo.getCellInfo();
            int lac = cInfo.getLAC();
            if (RibbonApi._logONSState) {
               System.out.println("mcc: " + Integer.toHexString(mcc) + " mnc: " + Integer.toHexString(mnc) + " lac: " + lac);
               System.out.println("category: " + var23 + " networkService: " + networkService);
            }

            if ((networkService & 2) == 0 && (networkService & 4) == 0) {
               if (RibbonApi._logONSState) {
                  System.out.println("no voice or data service");
               }

               this.setOperatorName(networkService, null);
            } else {
               String eonsString = null;
               boolean roaming = (networkService & 8) != 0;
               if (this.isEONSReady()) {
                  eonsString = this.getEONSString(networkService, mcc, mnc, lac, var22, var23);
               }

               Object ons = null;
               int onsRenderMode = this.getONSRenderMode();
               if (RibbonApi._logONSState) {
                  System.out.println("ons mode: " + onsRenderMode);
                  if (eonsString != null) {
                     System.out.println("eons: " + eonsString);
                  }

                  if (this._spnString != null) {
                     System.out.println("spn: " + this._spnString);
                  }

                  if (this._cphsOnLongString != null) {
                     System.out.println("cphs_on: " + this._cphsOnLongString);
                  }

                  if (this._cphsOnsShortString != null) {
                     System.out.println("cphs_ons: " + this._cphsOnsShortString);
                  }

                  System.out.println("roaming: " + roaming);
               }

               String longNetworkString = null;
               String shortNetworkString = null;
               if (this._NITZNetworkName.longNameIsValid(var21)) {
                  longNetworkString = this._NITZNetworkName.getLongName(var22);
                  if (RibbonApi._logONSState) {
                     System.out.println("Long NITZ: " + longNetworkString);
                  }
               }

               if (this._NITZNetworkName.shortNameIsValid(var21)) {
                  String tempName = this._NITZNetworkName.getShortName(var22);
                  if (RibbonApi._logONSState) {
                     System.out.println("Short NITZ: " + tempName);
                  }

                  if (longNetworkString == null) {
                     longNetworkString = tempName;
                  } else {
                     shortNetworkString = tempName;
                  }
               }

               if (longNetworkString == null) {
                  longNetworkString = this.getLongNetworkString(networkService);
                  if (RibbonApi._logONSState) {
                     System.out.println("SE13: " + longNetworkString);
                  }
               }

               label464:
               switch (onsRenderMode) {
                  case 0:
                  default:
                     if (eonsString != null) {
                        ons = eonsString;
                        if (RibbonApi._logONSState) {
                           System.out.println("displayEONS: " + ons);
                        }
                     } else if (roaming) {
                        ons = longNetworkString;
                        if (shortNetworkString != null) {
                           ons = this.addONSString(ons, shortNetworkString);
                        }

                        if (RibbonApi._logONSState) {
                           System.out.println("roaming: true");
                           System.out.println("networkName: " + ons);
                        }
                     } else if (this._spnString != null) {
                        ons = this._spnString;
                     } else if (this._cphsOnLongString != null) {
                        ons = this._cphsOnLongString;
                     } else {
                        ons = longNetworkString;
                        if (shortNetworkString != null) {
                           ons = this.addONSString(ons, shortNetworkString);
                        }
                     }
                     break;
                  case 1:
                     if (longNetworkString == null) {
                        longNetworkString = "";
                     }

                     if (shortNetworkString == null) {
                        shortNetworkString = "";
                     }

                     if (eonsString != null) {
                        ons = eonsString;
                        if (RibbonApi._logONSState) {
                           System.out.println("displayEONS: " + ons);
                        }
                     } else if (this._spnString == null) {
                        if (this._cphsOnLongString != null && !roaming) {
                           ons = this._cphsOnLongString;
                        } else {
                           ons = longNetworkString;
                           if (shortNetworkString != null) {
                              ons = this.addONSString(ons, shortNetworkString);
                           }
                        }
                     } else {
                        byte spnDisplay = this.getSPNDisplayCode();
                        if (RibbonApi._logONSState) {
                           System.out.println("spn_display: " + spnDisplay);
                        }

                        switch (spnDisplay & 3) {
                           case -1:
                              break label464;
                           case 0:
                           default:
                              if (roaming) {
                                 ons = this.addONSString(ons, longNetworkString + " " + this._spnString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, longNetworkString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString);
                                 }
                              } else {
                                 ons = this._spnString;
                              }
                              break label464;
                           case 1:
                              if (roaming) {
                                 ons = this.addONSString(ons, longNetworkString + " " + this._spnString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, longNetworkString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString);
                                 }
                              } else if (this._cphsOnLongString != null) {
                                 ons = this.addONSString(ons, this._cphsOnLongString + " " + this._spnString);
                                 if (this._cphsOnsShortString != null) {
                                    ons = this.addONSString(ons, this._cphsOnsShortString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, this._spnString);
                              } else {
                                 ons = this.addONSString(ons, longNetworkString + " " + this._spnString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, this._spnString);
                              }
                              break label464;
                           case 2:
                              if (this._cphsOnLongString != null && !roaming) {
                                 ons = this._cphsOnLongString;
                                 if (this._cphsOnsShortString != null) {
                                    ons = this.addONSString(ons, this._cphsOnsShortString);
                                 }
                              } else {
                                 ons = longNetworkString;
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString);
                                 }
                              }
                              break label464;
                           case 3:
                              if (roaming) {
                                 ons = longNetworkString;
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString);
                                 }
                              } else if (this._cphsOnLongString != null) {
                                 ons = this.addONSString(ons, this._cphsOnLongString + " " + this._spnString);
                                 if (this._cphsOnsShortString != null) {
                                    ons = this.addONSString(ons, this._cphsOnsShortString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, this._spnString);
                              } else {
                                 ons = this.addONSString(ons, longNetworkString + " " + this._spnString);
                                 if (shortNetworkString != null) {
                                    ons = this.addONSString(ons, shortNetworkString + " " + this._spnString);
                                 }

                                 ons = this.addONSString(ons, this._spnString);
                              }
                        }
                     }
                     break;
                  case 2:
                     byte spnDisplayCode = this.getSPNDisplayCode();
                     if (longNetworkString == null) {
                        longNetworkString = "";
                     }

                     if (shortNetworkString == null) {
                        shortNetworkString = "";
                     }

                     if (eonsString != null) {
                        ons = eonsString;
                        if (RibbonApi._logONSState) {
                           System.out.println("displayEONS: " + ons);
                        }
                     } else if (roaming && !this.isNetIdInSpdiList(var21)) {
                        if ((spnDisplayCode & 2) == 0 && this._spnString != null) {
                           if (StringUtilities.compareToIgnoreCase(longNetworkString, this._spnString) != 0) {
                              ons = this.addONSString(ons, longNetworkString + " " + this._spnString);
                           }

                           if (StringUtilities.compareToIgnoreCase(shortNetworkString, this._spnString) != 0) {
                              ons = this.addONSString(ons, shortNetworkString + " " + this._spnString);
                           }
                        }

                        ons = this.addONSString(ons, longNetworkString);
                        ons = this.addONSString(ons, shortNetworkString);
                     } else {
                        String spnStr = this._spnString == null ? "" : this._spnString;
                        if ((spnDisplayCode & 1) != 0) {
                           if (StringUtilities.compareToIgnoreCase(longNetworkString, spnStr) != 0) {
                              ons = this.addONSString(ons, longNetworkString + " " + spnStr);
                           }

                           if (StringUtilities.compareToIgnoreCase(shortNetworkString, spnStr) != 0) {
                              ons = this.addONSString(ons, shortNetworkString + " " + spnStr);
                           }
                        }

                        ons = this.addONSString(ons, spnStr);
                     }
               }

               if (ons == null || ons.equals("")) {
                  ons = Integer.toHexString(mcc) + "-" + Integer.toHexString(mnc);
               }

               if (RibbonApi._logONSState) {
                  System.out.println("One of:");
                  if (ons instanceof String) {
                     System.out.println((String)ons);
                  } else if (ons instanceof String[]) {
                     String[] strings = (String[])ons;

                     for (int i = 0; i < strings.length; i++) {
                        System.out.println(strings[i]);
                     }
                  }
               }

               this.setOperatorName(networkService, ons);
            }
         }
      }
   }

   private final void updateWiFiConnection(int networkService) {
      if ((SUPPORTED_WAFS & 4) != 0) {
         int activeWAFs = RadioInfo.getActiveWAFs();
         String wifiConnectionStr = null;
         if ((activeWAFs & 4) != 0) {
            if ((SUPPORTED_WAFS & 1) != 0 && (RadioInternal.get3GPPSupportedRats() & 4) != 0 && (networkService & 16384) != 0) {
               GANServiceZoneInfo info = GAN.getGANServiceZoneInfo();
               if (info != null && info._serviceZoneName != null && info._serviceZoneName.length() > 0) {
                  wifiConnectionStr = info._serviceZoneName;
                  if (RibbonApi._logONSState) {
                     System.out.println("WiFi UMA SZN: " + wifiConnectionStr);
                  }
               }
            }

            if (wifiConnectionStr == null) {
               WLANSystem system = WLAN.getWLANSystem();
               if (system != null) {
                  String profileName = system.getActiveProfileNameOrSSID();
                  if (profileName != null && profileName.length() > 0) {
                     wifiConnectionStr = profileName;
                     if (RibbonApi._logONSState) {
                        System.out.println("WiFi profile name: " + wifiConnectionStr);
                     }
                  } else {
                     String ssid = system.getActiveProfileSSID();
                     if (ssid != null && ssid.length() > 0) {
                        wifiConnectionStr = ssid;
                        if (RibbonApi._logONSState) {
                           System.out.println("WiFi ssid: " + wifiConnectionStr);
                        }
                     }
                  }
               }
            }
         }

         if (RibbonApi._logONSState && wifiConnectionStr == null) {
            System.out.println("WiFi: null");
         }

         this.setNetworkProp(-1839664706744174700L, wifiConnectionStr);
      }
   }

   private final boolean isNetIdInSpdiList(int netId) {
      if (this._spdiList != null) {
         int i = this._spdiList.length;

         while (--i >= 0) {
            if (this._spdiList[i] == netId) {
               return true;
            }
         }
      }

      return false;
   }

   private final Object addONSString(Object ons, String newString) {
      if (ons == null) {
         return newString;
      }

      if (newString != null && newString.length() != 0) {
         if (ons instanceof String) {
            ons = new String[]{(String)ons};
         }

         String[] strings = (String[])ons;
         Arrays.add(strings, newString);
         return strings;
      } else {
         return ons;
      }
   }

   private final void setOperatorName(int networkService, Object onsData) {
      this.putOperatorName(appendHomeZoneInfo(networkService, onsData));
   }

   private static final Object appendHomeZoneInfo(int networkService, Object stringData) {
      boolean inHome = (networkService & 64) != 0;
      boolean inCity = (networkService & 128) != 0;
      if (inHome || inCity) {
         if (stringData == null) {
            stringData = "";
         }

         if (stringData instanceof String) {
            stringData = new String[]{(String)stringData};
         }

         StringBuffer sb = new StringBuffer();
         String[] strings = (String[])stringData;

         for (int i = strings.length - 1; i >= 0; i--) {
            sb.append(strings[i]);
            String zoneName = GPRSInfo.getZoneName();
            if (zoneName != null) {
               sb.append(' ');
               sb.append(zoneName);
            }

            strings[i] = sb.toString();
         }

         if (strings.length == 1) {
            stringData = strings[0];
         }
      }

      return stringData;
   }

   private final String getLongNetworkString(int networkService) {
      try {
         int networkCount = RadioInfo.getNumberOfNetworks();
         int networkIndex = RadioInfo.getCurrentNetworkIndex();
         String ons = RadioInfo.getNetworkName(networkIndex);
         if (RibbonApi._logONSState) {
            System.out.println("Network count: " + networkCount);
            System.out.println("SE13 index: " + networkIndex);
            System.out.println("SE13 name: " + ons);
         }

         return ons;
      } finally {
         if (RibbonApi._logONSState) {
            System.out.println("Can't get SE13 name");
         }

         return "";
      }
   }

   private final boolean isEONSReady() {
      return true;
   }

   private final String getEONSString(int networkService, int mcc, int mnc, int lac, String mccName, int category) {
      if (this._oplData == null) {
         return null;
      }

      if (this._homePLMNOnly && (category & 4) == 0) {
         return null;
      }

      for (int i = 0; i < this._oplData.length; i++) {
         OPLRecord record = this._oplData[i];
         if (record != null
            && mcc >= record._mccSmallest
            && mcc <= record._mccLargest
            && mnc >= record._mncSmallest
            && mnc <= record._mncLargest
            && lac >= record._lacSmallest
            && lac <= record._lacLargest) {
            String ons = record._pnnName;
            if (ons == null) {
               return null;
            }

            if (record._showMCCInitials && mccName != null) {
               ons = ons + " " + mccName;
            }

            return ons;
         }
      }

      return null;
   }

   private final String getSIMString(int file, int offset) {
      int len;
      try {
         len = SIMCard.requestEFRead(file, 0, 0, this._simBuffer);
      } finally {
         ;
      }

      if (len <= 0) {
         return null;
      }

      String ons = this.convertSIMDataToString(this._simBuffer, offset, len);
      if (ons != null && ons.length() != 0) {
         return ons;
      }

      if (RibbonApi._logONSState) {
         System.out.println("Bad " + file + " data");
      }

      return null;
   }

   private final byte getSPNDisplayCode() {
      if (!SIMServiceTable.isSPNEnabled()) {
         return 0;
      }

      int len;
      try {
         len = SIMCard.requestEFRead(11, 0, 0, this._simBuffer);
      } finally {
         ;
      }

      return len <= 0 ? 0 : this._simBuffer[0];
   }

   private final String convertSIMDataToString(byte[] data, int offset, int length) {
      int i = offset;

      while (i < length && data[i] != -1) {
         i++;
      }

      try {
         return new String(data, offset, i - offset, "SMS");
      } finally {
         ;
      }
   }

   private final String convertUCS2DataToString(byte[] data, int offset, int length) {
      int i = offset;

      while (i < length && data[i] != -1) {
         i++;
      }

      try {
         return new String(data, offset, i - offset, "UCS2");
      } finally {
         ;
      }
   }

   private final String convertGSM7DataToString(byte[] data, int offset, int length, int spareBits) {
      int byteCount = 0;
      int bitCount = 0;
      int simLength = length - offset;
      byte[] unravelled = new byte[256];

      for (int i = 0; i < simLength; i++) {
         int b = data[offset + i];
         unravelled[bitCount++] = (byte)(b & 1);
         unravelled[bitCount++] = (byte)(b >> 1 & 1);
         unravelled[bitCount++] = (byte)(b >> 2 & 1);
         unravelled[bitCount++] = (byte)(b >> 3 & 1);
         unravelled[bitCount++] = (byte)(b >> 4 & 1);
         unravelled[bitCount++] = (byte)(b >> 5 & 1);
         unravelled[bitCount++] = (byte)(b >> 6 & 1);
         unravelled[bitCount++] = (byte)(b >> 7 & 1);
      }

      byte[] SIMdata = new byte[64];
      int totalBits = bitCount;
      int totalBytes = (totalBits - spareBits) / 7;
      bitCount = 0;

      for (byteCount = 0; byteCount < totalBytes; byteCount++) {
         SIMdata[byteCount] = (byte)(
            unravelled[bitCount++]
               + (unravelled[bitCount++] << 1)
               + (unravelled[bitCount++] << 2)
               + (unravelled[bitCount++] << 3)
               + (unravelled[bitCount++] << 4)
               + (unravelled[bitCount++] << 5)
               + (unravelled[bitCount++] << 6)
         );
      }

      try {
         return new String(SIMdata, 0, byteCount, "SMS");
      } finally {
         ;
      }
   }

   private final String getEmailAPN() {
      String apn = null;
      HostRoutingInfo hri = HRUtils.getDefaultHRT().getActiveHri();
      if (hri == null) {
         hri = HRUtils.getRegistrationHRT().getActiveHri();
      }

      if (hri instanceof GprsHRI) {
         apn = ((GprsHRI)hri).getApn();
      }

      return apn;
   }

   private final boolean isFatalPDPReject(int activeNetworks, int state, int cause) {
      if ((activeNetworks & 3) != 0 && state == 2 && (RadioInfo.getNetworkService() & 4) != 0) {
         switch (cause) {
            case 26:
            case 30:
            case 31:
            case 34:
            case 35:
            case 38:
            case 102:
               break;
            default:
               return true;
         }
      }

      return false;
   }

   private final void clearPDPError() {
      this.setNetworkProp(1040431808191919625L, null);
      this.setPDPRejectCause(0);
   }

   private final void setPDPRejectCause(int cause) {
      if (this._pdpErrorCauseContainer[0] != cause) {
         this._pdpErrorCauseContainer[0] = cause;
         this._networkProps.internalSet(-7072296818759564103L, this._pdpErrorCauseContainer);
      }
   }

   private final void setNetworkProp(long prop, Object message) {
      if (this._networkProps.get(prop) != message) {
         this._networkProps.internalSet(prop, message);
      }
   }

   private final void setSimStatus(int id) {
      if (this._simStatusId != id) {
         this._simStatusId = id;
         this.setNetworkProp(-8960794396193289546L, this._simStatusId == -1 ? null : this._rbf.getString(this._simStatusId));
      }
   }

   private final void updateServiceInfo() {
      String newMode = null;
      if ((SUPPORTED_WAFS & 2) != 0 || RadioInternal.isUMTSCapable()) {
         if ((RadioInfo.getNetworkService() & 256) != 0) {
            newMode = this._rbf.getString(88);
         }

         this.setNetworkProp(1812321293200299507L, newMode);
      }
   }

   private final void updateNetworkStatus() {
      int id = -1;
      int enabledWafs = RadioInfo.getEnabledWAFs();
      int radioState = RadioInfo.getState();
      int signalLevel = RadioInfo.getSignalLevel();
      if (((RadioInternal.getActiveRadios() & 1) != 0 && (RadioInfo.getNetworkService() & 16384) == 0 || CDMA_GSM_WORLD_PHONE) && radioState == 1) {
         if (signalLevel == -256) {
            if (this._coverageSearchState == 1) {
               if (CDMA_GSM_WORLD_PHONE) {
                  switch (enabledWafs) {
                     case 0:
                        break;
                     case 1:
                     default:
                        id = 166;
                        break;
                     case 2:
                        id = 163;
                        break;
                     case 3:
                        id = 71;
                  }
               } else {
                  id = 71;
               }
            } else if (this._coverageSearchState == 3) {
               if (RadioInternal.getNetworkSelectionMode() == 3) {
                  id = 72;
               } else {
                  id = 92;
               }
            }
         } else if (this._coverageSearchState == 3 && RadioInternal.getNetworkSelectionMode() == 3) {
            id = 72;
         }
      }

      if (this._netStatusId != id) {
         this._netStatusId = id;
         this._networkProps.internalSet(4822241500382547294L, this._netStatusId != -1 ? this._rbf.getString(this._netStatusId) : null);
      }
   }

   private final void updateSIMStatus() {
      if (RadioInfo.getState() == 1) {
         if ((RadioInfo.getEnabledWAFs() & 1) != 0) {
            if (RadioInfo.getSignalLevel() == -256) {
               if (!this._simPresent) {
                  this.setSimStatus(44);
                  return;
               }
            } else if (this._simStatusId == 44 && CDMA_GSM_WORLD_PHONE && RadioInfo.getActiveWAFs() == 2) {
               this.setSimStatus(-1);
               return;
            }
         } else if (this._simStatusId == 44 && (RadioInfo.getActiveWAFs() & 2) != 0) {
            this.setSimStatus(-1);
         }
      }
   }

   private final synchronized void updateCoverageSearchState(int message, int signalLevel) {
      int timerAction = 0;
      int newState = this._coverageSearchState;
      switch (this._coverageSearchState) {
         case -1:
            break;
         case 0:
         default:
            if (message == 0) {
               timerAction = 1;
               newState = 1;
            } else if (message == 2) {
               timerAction = 2;
               newState = 2;
            }
            break;
         case 1:
            if (message == 1) {
               timerAction = 2;
               newState = 0;
            } else if (message == 2) {
               timerAction = 2;
               newState = 2;
            } else if (message == 3) {
               newState = 3;
            }
            break;
         case 2:
            if (message == 1) {
               timerAction = 2;
               newState = 0;
            } else if (message == 0 && signalLevel == -256) {
               timerAction = 1;
               newState = 1;
            }
            break;
         case 3:
            if (message == 1) {
               timerAction = 2;
               newState = 0;
            } else if (message == 0 && signalLevel != -256) {
               timerAction = 1;
               newState = 1;
            } else if (message == 2) {
               timerAction = 2;
               newState = 2;
            }
      }

      if (newState != this._coverageSearchState) {
         this._coverageSearchState = newState;
      }

      this.updateNetworkStatus();
      if (CDMA_GSM_WORLD_PHONE) {
         this.updateSIMStatus();
      }

      if (message == 3) {
         this._coverageSearchTimerId = -1;
      }

      if (timerAction != 0) {
         Application app = Application.getApplication();
         if (this._coverageSearchTimerId != -1) {
            app.cancelInvokeLater(this._coverageSearchTimerId);
            this._coverageSearchTimerId = -1;
         }

         if (timerAction == 1) {
            this._coverageSearchTimerId = app.invokeLater(this, 120000, false);
         }
      }
   }

   private final void updateActivationRequired() {
      this.setNetworkProp(3170113883629495887L, cdmaActivationRequired() ? Boolean.TRUE : null);
   }

   @Override
   public final void run() {
      this.updateCoverageSearchState(3, -1);
   }

   @Override
   public final void baseStationChange() {
      this.setNetworkProp(6665563664396523075L, null);
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.setNetworkProp(6665563664396523075L, null);
      this.clearPDPError();
      this.updateONSActiveNetwork(service);
      this.updateServiceInfo();
      if (service != 0) {
         this.updateCoverageSearchState(2, -1);
      }

      this.updateActivationRequired();
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.setNetworkProp(6665563664396523075L, null);
      this.clearPDPError();
      this.updateONSActiveNetwork(service);
      this.updateServiceInfo();
      if (service != 0) {
         this.updateCoverageSearchState(2, -1);
      }
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
      int activeNetworks = RadioInfo.getActiveWAFs();
      if ((activeNetworks & 1) != 0) {
         try {
            String emailAPN = this.getEmailAPN();
            if (emailAPN != null) {
               String apnName = RadioInfo.getAccessPointName(apn);
               if (apnName != null && emailAPN.equalsIgnoreCase(apnName)) {
                  if (state == 2 && (RadioInfo.getNetworkService() & 4) != 0) {
                     this.setPDPRejectCause(cause);
                     this.setNetworkProp(1040431808191919625L, this._rbf.getString(53));
                  } else {
                     this.clearPDPError();
                  }
               }
            }
         } finally {
            return;
         }
      } else if ((activeNetworks & 2) != 0) {
         if (this.isFatalPDPReject(activeNetworks, state, cause)) {
            this.setNetworkProp(1040431808191919625L, this._rbf.getString(53));
         } else {
            this.clearPDPError();
         }

         if (state == 2) {
            this.setPDPRejectCause(cause);
         }
      }
   }

   @Override
   public final void radioTurnedOff() {
      this.setNetworkProp(6665563664396523075L, null);
      this.putOperatorName(null);
      this.setIdleModeText(null);
      this.clearPDPError();
      this.updateServiceInfo();
      this.updateCoverageSearchState(1, -1);
   }

   @Override
   public final void signalLevel(int level) {
      if (level == -256) {
         this.putOperatorName(null);
      } else if (this.getOperatorName() == null) {
         this.updateONSActiveNetwork(RadioInfo.getNetworkService());
      }

      this.updateServiceInfo();
      this.updateCoverageSearchState(0, level);
   }

   @Override
   public final void networkSelectionFailed(int networkId, int cause) {
   }

   @Override
   public final void flowControlStatusChange(int flowControlStatus) {
   }

   @Override
   public final void networkScanStatus(int status) {
   }

   @Override
   public final void networkNameChangeViaNITZ(int longNameLength, int shortNameLength) {
      if (this._NITZNetworkName.queryNewNameData(longNameLength, shortNameLength)) {
         this.updateONSActiveNetwork(RadioInfo.getNetworkService());
      }
   }

   @Override
   public final void ganEventOccurred(int event, int status, int errorCause) {
      if (event == 5) {
         this.updateWiFiConnection(RadioInfo.getNetworkService());
      }
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      this.setNetworkProp(-1986748551626928033L, (status & 268435456) != 0 ? appendHomeZoneInfo(RadioInfo.getNetworkService(), this._rbf.getString(50)) : null);
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int id) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int id) {
   }

   @Override
   public final void responseEFInfo(int code, int id, int fileStatus, int structure, int fileSize, int recordLength, int numRecords) {
      switch (id) {
         case 60:
            if (code == 0) {
               this._oplData = new OPLRecord[numRecords];
               this.loadSIMCache();
               return;
            }

            if (RibbonApi._logONSState) {
               System.out.println("efInfo(" + id + "): " + code);
            }

            this.simCachePopulated();
            return;
         default:
            if (this._efHandler != null && this._efHandler.isRunning()) {
               this._efHandler.responseEFInfo(code, id, fileStatus, structure, fileSize, recordLength, numRecords);
            }
      }
   }

   @Override
   public final void responseEFRead(int code, int id, int structure, int length, int recordNumber) {
      switch (id) {
         case 7:
         case 60:
         case 61:
         case 100:
            if (code == 0) {
               this.loadSIMCache();
            } else {
               this.simCachePopulated();
            }
            break;
         case 11:
         case 51:
         case 55:
            if (code == 0) {
               this.loadHomeNetworkNamesToCache();
               this.updateONSActiveNetwork(RadioInfo.getNetworkService());
            }
            break;
         default:
            if (this._efHandler != null && this._efHandler.isRunning()) {
               this._efHandler.responseEFRead(code, id, structure, length, recordNumber);
            }

            return;
      }

      if (code != 0 && RibbonApi._logONSState) {
         System.out.println("efRead(" + id + ":" + recordNumber + "): " + code);
      }
   }

   @Override
   public final void responseEFWrite(int code, int id, int structure, int recordNumber) {
   }

   @Override
   public final void cardInserted() {
      this._simPresent = true;
      this.setSimStatus(-1);
   }

   @Override
   public final void cardInvalid(int code, int subCode) {
      int resourceId;
      this._onsRenderMode = 0;
      label28:
      switch (code) {
         case 0:
            return;
         case 2:
            if (CDMA_GSM_WORLD_PHONE && RadioInfo.getEnabledWAFs() != 1) {
               resourceId = -1;
            } else {
               resourceId = 44;
            }

            this._simPresent = false;
            break;
         case 4:
            resourceId = 69;
            break;
         case 17:
            resourceId = 43;
            break;
         case 21:
         case 22:
            switch (subCode) {
               case 0:
               case 4:
               case 5:
                  resourceId = 14;
                  break label28;
               case 1:
               default:
                  resourceId = 179;
                  break label28;
               case 2:
                  resourceId = 176;
                  break label28;
               case 3:
                  resourceId = 177;
                  break label28;
               case 6:
                  resourceId = 178;
                  break label28;
            }
         case 23:
            resourceId = 51;
            break;
         default:
            resourceId = 45;
      }

      this.setSimStatus(resourceId);
      this.resetSIMCache();
      this.putOperatorName(null);
   }

   @Override
   public final void cardFault(int code) {
      this._onsRenderMode = -1;
   }

   @Override
   public final void cardReady() {
      this._onsRenderMode = -1;
      this.setSimStatus(-1);
      new Thread(new SystemMonitor$1(this)).start();
      if (WorldPhoneDisclaimerDialog.isDisclaimerNeeded() && CDMA_GSM_WORLD_PHONE) {
         UiApplication.getUiApplication().invokeLater(new SystemMonitor$2(this));
      }
   }

   @Override
   public final void cardUpdated() {
      this._onsRenderMode = -1;
      this._simPresent = true;
      this.resetSIMCache();
      this.loadSIMCache();
   }

   private final void loadSPDI() {
      if (SIMServiceTable.isServiceEnabled(49)) {
         if (this._efHandler == null) {
            this._efHandler = new SIMCardEfHandler(this);
         }

         if (!this._efHandler.isRunning()) {
            this._efHandler.startTask(new SystemMonitor$ServiceProviderDisplayInfo(this), false);
         }
      }
   }

   private final void updateNetworkProperty(long key, int resourceId) {
      String msg = this._rbf.getString(resourceId);
      String s = (String)this._networkProps.get(key);
      if (s != null && !s.equals(msg)) {
         this._networkProps.internalSet(key, msg);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L) {
         if (this._simStatusId != -1) {
            this.updateNetworkProperty(-8960794396193289546L, this._simStatusId);
         }

         if (this._netStatusId != -1) {
            this.updateNetworkProperty(4822241500382547294L, this._netStatusId);
         }

         this.updateNetworkProperty(1040431808191919625L, 53);
         this.updateNetworkProperty(-1986748551626928033L, 50);
         this.updateNetworkProperty(1812321293200299507L, 88);
         this._rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
         this._networkProps.internalSet(4822241500382547294L, this._netStatusId != -1 ? this._rbf.getString(this._netStatusId) : null);
      }

      if (guid == 2950066364548195165L && WLAN.isAssociated() != null) {
         this.updateWiFiConnection(RadioInfo.getNetworkService());
      }
   }

   private final void putOperatorName(Object name) {
      Object oldName = this.getOperatorName();
      if (oldName == null) {
         if (name == null) {
            return;
         }
      } else {
         if (oldName instanceof String && name instanceof String && ((String)oldName).equals((String)name)) {
            return;
         }

         if (oldName instanceof String[] && name instanceof String[]) {
            String[] oldNames = (String[])oldName;
            String[] names = (String[])name;
            if (oldNames.length == names.length) {
               int i = oldNames.length - 1;

               while (i >= 0 && oldNames[i].equals(names[i])) {
                  i--;
               }

               if (i < 0) {
                  return;
               }
            }
         }
      }

      this.setNetworkProp(-7219683504990287771L, name);
      RIMGlobalMessagePoster.postGlobalEvent(-7219683504990287771L);
   }

   @Override
   public final String getOperatorName() {
      Object names = this._networkProps.get(-7219683504990287771L);
      if (names instanceof String[]) {
         names = ((String[])names)[0];
      }

      return (String)names;
   }

   @Override
   public final String getOperatorName(int mcc, int mnc, int lac, String mccName, int category) {
      this.updateONSSpecifiedNetwork(mcc, mnc, lac, mccName, category);
      return (String)this._networkProps.get(-8817962046913284182L);
   }

   @Override
   public final ReadableLongMap getNetworkPropsCollection() {
      return this._networkProps;
   }

   @Override
   public final void setIdleModeText(String text) {
      this.setNetworkProp(-7608742199570488450L, text);
   }

   @Override
   public final void taskComplete(int error, SIMCardEfTask task) {
      if (error == 0 && task instanceof SystemMonitor$ServiceProviderDisplayInfo) {
         this._spdiList = ((SystemMonitor$ServiceProviderDisplayInfo)task)._spdiList;
      }

      System.out.println("SPDI LIST COMPLETED");
   }

   private static final boolean cdmaActivationRequired() {
      if ((RadioInfo.getActiveWAFs() & 2) != 0 || (RadioInfo.getSupportedWAFs() & 2) == 2) {
         String deviceNumber = null;

         label111:
         try {
            deviceNumber = Phone.getInstance().getNumber(0);
         } finally {
            break label111;
         }

         if (deviceNumber != null) {
            int mdnLength = deviceNumber.length();
            if (mdnLength != 10) {
               return false;
            }

            for (int i = 0; i < 6; i++) {
               char c = deviceNumber.charAt(i);
               if (c != '0') {
                  return false;
               }
            }

            int mdn = 0;

            label100:
            try {
               mdn = Integer.valueOf(deviceNumber.substring(6));
            } finally {
               break label100;
            }

            int esn = CDMAInfo.getESN();
            if (mdn == esn % 10000) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
