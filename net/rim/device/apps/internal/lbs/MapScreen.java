package net.rim.device.apps.internal.lbs;

import java.util.Hashtable;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.lbs.Logger;
import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.lbs.gps.GPSProvider$Listener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.BackdoorKeyListener;
import net.rim.device.api.system.BackdoorKeyProcessor;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;
import net.rim.device.apps.internal.lbs.maplet.LayerDictionary;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;
import net.rim.device.apps.internal.lbs.maplet.MapletMapField;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.apps.internal.lbs.verbs.POIVerb;
import net.rim.device.cldc.io.fastdormancy.FastDormancyManager;
import net.rim.vm.Array;

public final class MapScreen extends FullScreen implements GPSProvider$Listener, BackdoorKeyListener {
   public MapField _mapField;
   Application _application;
   Screen _gpsSelectionScreen;
   private Verb[] _gpsCaptureVerbs;
   boolean _tracking = false;
   boolean _gpsDeviceOn;
   boolean _gpsLock = false;
   boolean _restartTracking = false;
   int _gpsDeviceStatus = 0;
   String _statusMessage = "";
   float _currentSpeed;
   float _currentSpeedKph;
   int _currentBearing;
   int _currentAltitude;
   private GPSLocationData _gpsLocationData;
   private MapScreen$GPSRenderUpdater _GPSUpdater;
   int _pingThreadID = -1;
   private boolean _stopTrackingDialogDisplayed;
   int _updates;
   private Field _banner;
   boolean _trackUp;
   boolean _autoTrackUp;
   private boolean _appInForeground;
   private boolean _backgroundRadioOff = false;
   private boolean _backgroundCoverageOff = false;
   private boolean _keepBacklightOn;
   private int _batteryBacklightLevel;
   private BackdoorKeyProcessor _backdoor;
   boolean _init;
   private Hashtable _numbers;
   private Verb _cellData;
   private Verb[] _cellVerbs;
   Timing _timer = new Timing();
   Dashboard _dashboard = new Dashboard();
   ModeManager _modeMgr;
   int _autoTrackingTimeout = 0;
   private boolean _firstLock = false;
   boolean _following = false;
   private int _suspendTimeoutPID = -1;
   private MapScreen$SuspendTimeoutRunnable _suspendTimeoutRunnable = new MapScreen$SuspendTimeoutRunnable(this);
   private boolean _reconnectGPS = false;
   private int _gpsPID = -1;
   private MapScreen$GPSOffRunnable _gpsOffRunnable = new MapScreen$GPSOffRunnable(this);
   private int _hideDashboardPID = -1;
   private MapScreen$HideDashboardRunnable _hideDashboard = new MapScreen$HideDashboardRunnable(this);
   private MapScreen$GPSDataSpeedBearing[] gpsSpeedBearing = new MapScreen$GPSDataSpeedBearing[0];
   private int _backlightUpdaterPID = -1;
   private MapScreen$BacklightUpdater _backlightUpdater = new MapScreen$BacklightUpdater(this, null);
   private LBSMenuItem[] _menuItems = new LBSMenuItem[0];
   static final boolean ENABLE_TRACK_UP = true;
   private static final int REGION_NUM = 8;
   private static final DefaultMapView[] _defaultMapView = new DefaultMapView[0];
   private static final DefaultMapView _europe = new DefaultMapView("Europe", 5090229, 1872831, 202, 295, 14);
   private static final DefaultMapView _canada = new DefaultMapView("Canada", 6354093, -11349367, 302, 302, 14);
   private static final DefaultMapView _us = new DefaultMapView("US", 3690229, -9872832, 310, 311, 14);
   private static final DefaultMapView _asia = new DefaultMapView("Asia", 5090229, 9372832, 400, 472, 15);
   private static final DefaultMapView _oceania = new DefaultMapView("Oceania", -1090229, 12372832, 502, 550, 15);
   private static final DefaultMapView _africa = new DefaultMapView("Africa", 590229, 1872831, 602, 655, 15);
   private static final DefaultMapView _southAmerica = new DefaultMapView("South Amercia", -1090229, -5872832, 702, 901, 14);
   private static final DefaultMapView _northAmerica = new DefaultMapView("North America & the Caribbean", 2090229, -7872832, 334, 376, 14);
   private static final long UID_INITAL_MAP_VIEW_PROVIDER = 2809568335828852197L;
   public static final int FULL_31 = 65536;
   public static final int REDUCED = 131072;
   private static String LATITUDE = "latitude";
   private static String LONGITUDE = "longitude";
   private static String SPEED = "speed";
   public static final ResourceBundleFamily _resources = ResourceBundle.getBundle(6514774203079918781L, "net.rim.device.apps.internal.lbs.LBS");
   private static MapScreen _internalInstance;
   private static final LayoutManager LAYOUT_MANAGER = new LayoutManager();
   static String TITLE_UPDATES = "Updates: ";
   static String TITLE_TILES = " Tiles: ";
   private static final long LBS_CELL_DATA_ITEM = 6503629165413339198L;
   private static final int[] _rotationAdjustmentTable = new int[]{
      0,
      0,
      0,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      0,
      0,
      0,
      0,
      0,
      1,
      1,
      1,
      1,
      2,
      2,
      2,
      2,
      2,
      2,
      2,
      1,
      1,
      1,
      1,
      0,
      0,
      1,
      1,
      1,
      2,
      2,
      2,
      3,
      3,
      3,
      3,
      3,
      2,
      2,
      2,
      1,
      1,
      0,
      0,
      1,
      2,
      2,
      3,
      3,
      4,
      4,
      4,
      4,
      4,
      4,
      3,
      3,
      3,
      2,
      1,
      1,
      0,
      1,
      2,
      3,
      4,
      5,
      5,
      6,
      6,
      6,
      5,
      5,
      5,
      4,
      3,
      3,
      2,
      1,
      0,
      2,
      3,
      4,
      5,
      6,
      7,
      7,
      8,
      8,
      7,
      7,
      6,
      5,
      4,
      3,
      2,
      1,
      0,
      2,
      4,
      6,
      7,
      8,
      9,
      10,
      10,
      10,
      9,
      9,
      8,
      7,
      6,
      4,
      3,
      1,
      0,
      3,
      5,
      8,
      10,
      11,
      12,
      12,
      13,
      12,
      12,
      11,
      10,
      8,
      7,
      5,
      4,
      2,
      0,
      4,
      7,
      10,
      12,
      14,
      15,
      16,
      16,
      15,
      14,
      13,
      12,
      10,
      8,
      6,
      4,
      2,
      0,
      5,
      9,
      13,
      16,
      18,
      19,
      19,
      19,
      18,
      17,
      16,
      14,
      12,
      10,
      7,
      5,
      2,
      0,
      7,
      13,
      17,
      21,
      23,
      24,
      24,
      23,
      22,
      20,
      19,
      16,
      14,
      11,
      9,
      6,
      3,
      0,
      9,
      17,
      23,
      27,
      29,
      29,
      29,
      28,
      26,
      24,
      22,
      19,
      16,
      13,
      10,
      7,
      3,
      0,
      -804650991,
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
      1702195616,
      134247195,
      134247464,
      -1183554751,
      207836005,
      1091043347,
      1706652771,
      1698392148,
      1665206272,
      1466284404,
      7554625,
      1953251592,
      423171,
      1953251592,
      4241443,
      1886404872,
      1284142661,
      134245257,
      -848269247,
      1091043522,
      -1026723728,
      197599565,
      1883310080,
      -1597846160,
      2989671,
      7618824,
      1828930056,
      1107820568,
      -1609011965,
      128872307,
      289540096,
      1283001204,
      9139813,
      1947288072,
      1626765579,
      1614940160,
      1867314433,
      134246247,
      -2076154558,
      1866598400,
      2195055,
      1688355336,
      1107820670,
      1694592193,
      1816332288,
      1081041639,
      1124597860,
      134228391,
      1846324803,
      1996639860,
      12217113,
      212747016,
      115897454,
      -1136457728,
      1227030881,
      7551860,
      -1967373560,
      1124597875,
      323868604,
      1141375093,
      -2111433627,
      1698957312,
      -2125125612,
      1698957312,
      4475156,
      1717912584,
      1953264993,
      1698957312,
      1953367923,
      1698957312,
      1769104243,
      1141375112,
      477417833,
      1158152307,
      -1991472522,
      1174929515,
      1139174511,
      -2080284228,
      1950950356,
      1174929467,
      1825139058,
      1697402489,
      -1136261120,
      1816594432,
      1092575855,
      1634234476,
      1145636864,
      1296631808,
      134236499,
      15035721,
      1729121288,
      273418240,
      108331879,
      273418240,
      108331879,
      134247464,
      1963135308,
      1275592710,
      108331793,
      134247464,
      1229996364,
      -1368435841,
      1275592711,
      4475221,
      459361288,
      -28018510,
      1632372736,
      134253410,
      1717658956,
      1745049535,
      1275592916,
      1649372041,
      134267754,
      12749132,
      -1031189496,
      1930716484,
      -1941174272,
      134276469,
      427229774,
      1886999659,
      1309147237,
      134243628,
      1767102286,
      8678498,
      1467109128,
      424018785,
      1917782016,
      5767527,
      1735544584,
      134240513,
      1232308303,
      7551860,
      1953452552,
      1376256194,
      1756490197,
      1376256116,
      134243831,
      57873235,
      1501103033,
      14883142,
      4346632,
      207835912,
      1393033235,
      2319728,
      1853444872,
      1980777315,
      134220586,
      1668184403,
      6630478,
      1853444872,
      1784827747,
      1393033411,
      1331916409,
      1942186594,
      2035484672,
      1649369966,
      -1566129302,
      1393033332,
      1348693625,
      51997042,
      1393033337,
      1449356921,
      1864459,
      -998092024,
      -1566153469,
      1393033332,
      1143194507,
      134222949,
      600017747,
      -429101242,
      -1957492736,
      -1991498813,
      1393033410,
      -1172061301,
      -1772943360,
      134242619,
      134278227,
      1866919251,
      7595628,
      1886999560,
      1409810533,
      1409810647,
      134261207,
      4475221,
      1280464136,
      1767180288,
      -848269247,
      1426587842,
      1735279977,
      134243585,
      134254165,
      1180830550,
      1763275949,
      134247269,
      1935805270,
      190187520,
      1869652066,
      2031682419,
      -78182400,
      1868171264,
      134245743,
      1936025979,
      8325120,
      561026056,
      134243334,
      -1368427616,
      1688355335,
      -1610088322,
      128872307,
      -1593310991,
      1822820352,
      -1509425127,
      1701407599,
      -1566177280,
      134229364,
      7592104,
      460501512,
      1953460050,
      1924270080,
      1692121115,
      582486016,
      134226795,
      124350392,
      12191744,
      134266632,
      1745049535,
      -1073217324,
      1103104000,
      7982706,
      1969932808,
      9071899,
      561433096,
      -737673209,
      997476643,
      1288964096,
      134267529,
      1644910292,
      -737673101,
      1935805270,
      1665210694,
      -1083852428,
      -2049701888,
      -402128780,
      1956816390,
      -1310128128,
      134247528,
      -167247631,
      1847673440,
      738787339,
      1695088752,
      151047969,
      151011429,
      1698971749,
      594760980,
      1080363264,
      1668184403,
      -1016438193,
      1953655108,
      1862860921,
      655489,
      201355787,
      504102942,
      1930980421,
      16667683,
      194930444,
      -1637413888,
      644763403,
      13595712,
      194930444,
      728991823,
      201385490,
      1702260589,
      1869417472,
      1950442870,
      1869417472,
      -1002347146,
      1869417472,
      -1002347146,
      1668184403,
      -1016438193,
      1829503091,
      1181054575,
      15101039,
      1987013900,
      1869367141,
      1984241506,
      201386890,
      1702260589,
      -339572404,
      1869417472,
      2035508598,
      1649369966,
      201376618,
      1702260589,
      1866919251,
      201385580,
      1702260589,
      482591910,
      1846280427,
      1866884396,
      201385580,
      459634033,
      1735109954,
      6595104,
      1702195468,
      198529051,
      201354831,
      234941555,
      1076236288,
      1375881076,
      268461559,
      1349795878,
      268462447,
      1383350310,
      268461559,
      1970412328,
      1864803,
      1768303376,
      1091567742,
      -2101775261,
      1816334336,
      268468591,
      1957520708,
      268468921,
      -1485792700,
      1158676601,
      38233198,
      6682450,
      1684948240,
      6909776,
      1684948240,
      6682450,
      1734691856,
      1769269279,
      1276116999,
      268485257,
      7672653,
      1935822608,
      1678538083,
      290459648,
      1393557608,
      6649441,
      -1014279408,
      1767182336,
      23555653,
      1953775973,
      2332257,
      1771722256,
      2030295906,
      268499128,
      1936683156,
      1628635171,
      2035514978,
      275945070,
      318816991,
      -1585552799,
      -1971960337,
      773193843,
      773271061,
      779317272,
      -616616422,
      1714321667,
      433663090,
      773193835,
      773271061,
      779317272,
      543305242,
      750455,
      773139990,
      1880632855,
      773467763,
      -651398554,
      204368665,
      -1032675737,
      355341824,
      405673774,
      439251824,
      745694766,
      778770905,
      1812361069,
      355341824,
      405673774,
      439251824,
      745694766,
      778770905,
      6425462,
      773139990,
      1880632855,
      773467763,
      -2067270291,
      355341824,
      405673774,
      439251824,
      -940217042,
      1930178180,
      1929883247,
      355341824,
      405673774,
      439251824,
      -940217042,
      -616616316,
      355341824,
      405673774,
      439251824,
      -940217042,
      11153028,
      773139990,
      1880632855,
      773467763,
      7571567,
      773139990,
      1880632855,
      773467763,
      1801287025,
      1957543011,
      355341824,
      405673774,
      439251824,
      1651077678,
      369102946,
      388896046,
      1936726062,
      -1439819218,
      355341824,
      405673774,
      439251824,
      1655485230,
      7040879,
      773139990,
      1880632855,
      194981491,
      1814962030,
      369128290,
      388896046,
      53353006,
      2043506544,
      355341824,
      439228206,
      14368558,
      773139990,
      773467671,
      482591835,
      355341824,
      439228206,
      -1010541778,
      -616616420,
      355341824,
      439228206,
      1735746094,
      369162764,
      388896046,
      1731074606,
      369128304,
      388896046,
      1764629038,
      7223345,
      773139990,
      773467671,
      7561836,
      773139990,
      773467671,
      779313772,
      7565415,
      773139990,
      773467671,
      6820205,
      773139990,
      773467671,
      1645480819,
      7040879,
      773139990,
      773467671,
      -1771144845,
      -1025568654,
      355341824,
      439228206,
      369142318,
      388896046,
      -1439819218,
      1698375470,
      355341824,
      439228206,
      1664002606,
      184639932,
      355341824,
      439228206,
      1949215278,
      773193943,
      773271061,
      782904858,
      9048272,
      773139990,
      773467671,
      773193954,
      773271061,
      527305631,
      1970037294,
      661614338,
      355341824,
      -1624369362,
      773811723,
      1849176425,
      355341824,
      -1624369362,
      773811723,
      773193898,
      773271061,
      527305631,
      -802248146,
      369134096,
      388896046,
      1846255406,
      14822943,
      773139990,
      785133079,
      1634086541,
      1830380571,
      7955233,
      773139990,
      53382772,
      369124713,
      1949177134,
      2002857614,
      773193844,
      -1904988651,
      1953980718,
      369102638,
      1982731566,
      639041645,
      596424834,
      1819285504,
      996693113,
      1880621157,
      286982345,
      1075511392,
      -1385016204,
      453018731,
      1467184192,
      453011459,
      -90147776,
      1914372228,
      527790405,
      1869506377,
      12665612,
      1918164507,
      8678317,
      -2070553061,
      -1596056832,
      503350522,
      1701519398,
      503380823,
      1182023718,
      15101039,
      555427870,
      673054727,
      1092485236,
      558919487,
      1883315712,
      -1597846160,
      2120376826,
      557981184,
      -1705637010,
      8282729,
      1080377886
   };

   final void userPanned() {
      if (this._gpsLock && this._following) {
         this._following = false;
         this._mapField.setAutoPan(false);
         this._mapField.showHintLabel(LBSResources.getString(316));
      }

      if (this._following && this._gpsLock) {
         this._mapField._currentLocations.setSemiFocused(true);
      } else {
         this._mapField._currentLocations.setFocusAuto();
      }
   }

   public final void setBannerVisible(boolean visible) {
      if (visible) {
         this.insert(this._banner, 0);
      } else {
         this.delete(this._banner);
      }
   }

   final void showLocation(Location loc) {
      this._init = true;
      if (!(loc instanceof Route)) {
         this._mapField.showLocation(loc);
         this._mapField.update(true);
         loc._field = this._mapField;
         this._mapField.showLocationMarkerCaption(false);
      } else {
         Route route = (Route)loc;
         this._mapField._currentRoute = route;
         route._decisions._mapField = this._mapField;
         route._route = route;
         this._mapField._currentLocations.addRoute(route);
         this._mapField._documentConverter.onEndRoute();
         this._mapField.zoomToFit();
      }
   }

   final void openDocument(String contentType, Object content, int uid) {
      this.openDocument(contentType, content, uid, true, null);
   }

   final void openDocument(String contentType, Object content, int uid, boolean zoomToFit, String source) {
      this._init = true;
      this._mapField.openDocument(contentType, content, uid, zoomToFit, source);
   }

   final void optionsChanged() {
      int levelValue = LBSOptions.getInt(827298922757617815L, 0);
      this._batteryBacklightLevel = (4 - levelValue) * 25 - 1;
      boolean autoTrackUp = LBSOptions.getBoolean(-4064050259441269877L, true);
      if (autoTrackUp != this._autoTrackUp) {
         this._autoTrackUp = autoTrackUp;
         this.setTrackUp(this._autoTrackUp);
      }

      boolean showTitle = !LBSOptions.getBoolean(5204834541750260038L, true);
      if (this._mapField._showTitleField != showTitle) {
         this._mapField._showTitleField = showTitle;
         this.setBannerVisible(showTitle);
      }

      LayerDictionary.getInstance().optionsChanged();
      this._mapField.optionsChanged();
      this._mapField.update(true);
   }

   public final LBSMenuItem getMenuItem(int id) {
      for (int i = 0; i < this._menuItems.length; i++) {
         if (this._menuItems[i].getId() == id) {
            return this._menuItems[i];
         }
      }

      return null;
   }

   final void onActivate() {
      FastDormancyManager.getInstance().setFastDormancy(false);
      if (this._backgroundRadioOff && RadioInfo.getActiveWAFs() != 0
         || this._backgroundCoverageOff
            && (CoverageInfo.isCoverageSufficient(2) || CoverageInfo.isCoverageSufficient(4) || CoverageInfo.isCoverageSufficient(1))) {
         this._mapField.update(true);
      }

      if (this._suspendTimeoutPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._suspendTimeoutPID);
      }

      if (this._gpsSelectionScreen != null) {
         this._gpsSelectionScreen.actionPerformed(0, null);
      }

      this._appInForeground = true;
      if (Trackball.isSupported()) {
         this.setTrackballSensitivityXOffset(100);
         this.setTrackballSensitivityYOffset(100);
      }

      if (this._gpsPID != -1) {
         this.restartGPS();
      }
   }

   final void onDeactivate() {
      EventLogger.logEvent(LBSApplication.UID, "onDeactivate".getBytes(), 0);
      FastDormancyManager.getInstance().setFastDormancy(true);
      this.shutdownGPS();
      this._mapField.onDeactivate();
      this._appInForeground = false;
   }

   final int getAdjustment(int rotation, int latitude) {
      if (latitude < 0) {
         latitude = -latitude;
      }

      latitude = (latitude + 2) / 5;
      latitude -= 3;
      if (latitude < 0) {
         return 0;
      }

      if (latitude > 11) {
         latitude = 11;
      }

      if (rotation < 90) {
         return -_rotationAdjustmentTable[latitude * 18 + (rotation + 2) / 5];
      } else if (rotation < 180) {
         rotation = 180 - rotation;
         return _rotationAdjustmentTable[latitude * 18 + (rotation + 2) / 5];
      } else if (rotation < 270) {
         rotation -= 180;
         return -_rotationAdjustmentTable[latitude * 18 + (rotation + 2) / 5];
      } else {
         rotation = 360 - rotation;
         return _rotationAdjustmentTable[latitude * 18 + (rotation + 2) / 5];
      }
   }

   final void locationUpdatedInternal() {
      this._timer.startTimer(4);
      this._updates++;
      int satCount = this._gpsLocationData.getSatelliteCount();
      GPSMode gpsMode = (GPSMode)this._modeMgr.getMode(1);
      if (gpsMode != null) {
         gpsMode.setSatelliteCount(satCount);
      }

      boolean GPSChanged = this._gpsLock != this._gpsLocationData.isLocked();
      boolean satelliteChanged = satCount != this._gpsLocationData.getSatelliteCount();
      boolean updateView = false;
      if (GPSChanged || satelliteChanged) {
         this._gpsLock = this._gpsLocationData.isLocked() && this._gpsDeviceOn && satCount > 0;
         if (this._gpsLock && this._firstLock) {
            this._mapField.setZoom(Math.min(4, this._mapField._zoom));
         }

         this._firstLock = false;
      }

      boolean updateSpeedBubble = this._gpsLock;
      if (gpsMode.getMessageType() == 0 && this._gpsLock) {
         gpsMode.setMessage("", 1);
         this._dashboard.refresh();
      }

      if (!this._gpsLocationData.isValid()) {
         updateSpeedBubble = updateSpeedBubble && this._currentSpeed != false;
         this._currentSpeed = (float)false;
         if (this._mapField._marker._latitude == Integer.MAX_VALUE || this._mapField._marker._longitude == Integer.MAX_VALUE) {
            this._mapField._marker._latitude = this._mapField._latitude;
            this._mapField._marker._longitude = this._mapField._longitude;
            this._mapField.invalidate();
         }
      } else {
         float newSpeed = this._gpsLocationData.getSpeed();
         updateSpeedBubble = updateSpeedBubble && this._currentSpeed != newSpeed;
         this._currentSpeed = newSpeed;
         if (Float.isNaN(this._currentSpeed)) {
            this._mapField.invalidate();
            return;
         }

         this.AddSpeedToArray(this._currentSpeed / 1073270757);
         if (!this.isNewSpeedValid()) {
            this._mapField.invalidate();
            return;
         }

         if (this._currentSpeed > 4607182418800017408L && satCount > 3) {
            int bearing = (int)this._gpsLocationData.getBearing();
            int altitude = (int)this._gpsLocationData.getAltitude();
            if (this._currentBearing != bearing) {
               this._currentBearing = bearing;
               this._currentAltitude = altitude;
               if (this._currentBearing < 0) {
                  this._currentBearing += 360;
               } else if (this._currentBearing > 360) {
                  this._currentBearing -= 360;
               }

               gpsMode.setBearing(this._currentBearing);
               if (this._trackUp) {
                  int rotation = 360 - this._currentBearing;
                  if (!LBSOptions.SPHERICAL_CORRECTION) {
                     int latitude = this._gpsLocationData.getLatitudeInt() / 100000;
                     int adjustment = this.getAdjustment(rotation, latitude);
                     rotation -= adjustment;
                     this._currentBearing += adjustment;
                  }

                  rotation += 2;
                  if (rotation >= 360) {
                     rotation -= 360;
                  }

                  this._mapField.setRotation(rotation);
                  updateView = true;
               } else if (!updateSpeedBubble) {
                  this.invalidate();
               }
            }

            if (this._currentAltitude != altitude) {
               this._currentAltitude = altitude;
               this._currentBearing = bearing;
            }
         }

         int newLat = this._gpsLocationData.getLatitudeInt();
         int newLong = this._gpsLocationData.getLongitudeInt();
         int previousLat = this._mapField._latitude / 10;
         int previousLong = this._mapField._longitude / 10;
         if (previousLat == newLat / 10 && previousLong == newLong / 10) {
            if (this._mapField._marker._latitude == Integer.MAX_VALUE || this._mapField._marker._longitude == Integer.MAX_VALUE) {
               this._mapField._marker._latitude = newLat;
               this._mapField._marker._longitude = newLong;
               if (!updateSpeedBubble) {
                  this._mapField.invalidate();
               }
            }
         } else {
            this._mapField._marker._latitude = newLat;
            this._mapField._marker._longitude = newLong;
            if (this._gpsLock && this._following) {
               this._mapField.moveTo(newLat, newLong);
               updateView = true;
            }
         }

         this._mapField.locationUpdated(this._gpsLocationData);
      }

      if (updateView) {
         this._mapField.update(false);
      }

      if (updateSpeedBubble) {
         float preSpeedKph = this._currentSpeedKph;
         this.setSpeedBubbleData();
         gpsMode.setSpeed(this._currentSpeedKph);
         this._mapField._marker._speed = this._currentSpeedKph;
         if (this._currentSpeedKph == false && preSpeedKph != false
            || this._currentSpeedKph != false && preSpeedKph == false
            || this._gpsLock && !this._following) {
            this._mapField.invalidate();
         }
      }

      this._dashboard.refresh();
      this._timer.endTimer(4);
   }

   final void setSpeedBubbleData() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: nop
      // 01: ldc_w 1094713344
      // 04: aload 0
      // 05: getfield net/rim/device/apps/internal/lbs/MapScreen._gpsLocationData Lnet/rim/device/api/lbs/gps/GPSLocationData;
      // 08: invokevirtual net/rim/device/api/lbs/gps/GPSLocationData.getSatelliteCount ()I
      // 0b: bipush 13
      // 0d: if_icmpge 1a
      // 10: aload 0
      // 11: getfield net/rim/device/apps/internal/lbs/MapScreen._gpsLocationData Lnet/rim/device/api/lbs/gps/GPSLocationData;
      // 14: invokevirtual net/rim/device/api/lbs/gps/GPSLocationData.getSatelliteCount ()I
      // 17: goto 1c
      // 1a: bipush 12
      // 1c: i2f
      // 1d: fdiv
      // 1e: nop
      // 1f: fstore 1
      // 20: aload 0
      // 21: aload 0
      // 22: getfield net/rim/device/apps/internal/lbs/MapScreen._currentSpeed F
      // 25: f2d
      // 26: nop
      // 27: ldc2_w 4603129179135383962
      // 2a: nop
      // 2b: fload 1
      // 2c: f2d
      // 2d: dmul
      // 2e: dcmpl
      // 2f: ifle 3e
      // 32: aload 0
      // 33: getfield net/rim/device/apps/internal/lbs/MapScreen._currentSpeed F
      // 36: nop
      // 37: ldc_w 1072500310
      // 3a: fmul
      // 3b: goto 41
      // 3e: nop
      // 3f: bipush 0
      // 41: putfield net/rim/device/apps/internal/lbs/MapScreen._currentSpeedKph F
      // 44: return
   }

   final void stopTracking() {
      label42:
      try {
         if (this._cellData != null) {
            this._cellData.invoke(Boolean.FALSE);
         }
      } finally {
         break label42;
      }

      if (this._mapField.isRecording()) {
         this._mapField.stopRecording();
      }

      this._firstLock = false;
      this._gpsLock = false;
      this._following = false;
      this._mapField.setAutoPan(false);
      GPSProvider.getInstance().stopReporting(null);
      GPSProvider.getInstance().removeLocationListener(this);
      EventLogger.logEvent(LBSApplication.UID, "Stopping Navigation.".getBytes(), 0);
      if (this._gpsCaptureVerbs != null && this._gpsCaptureVerbs.length > 0) {
         this._gpsCaptureVerbs[0].invoke(Boolean.FALSE);
      }

      this._mapField._marker._label = null;
      this._restartTracking = false;
      this._tracking = false;
      this._reconnectGPS = false;
      this._gpsDeviceOn = false;
      this._keepBacklightOn = false;
      this._mapField.setRotation(0);
      this._statusMessage = "";
      this._autoTrackingTimeout = 0;
      this.navigateMode(false);
      this._mapField.update(true);
      if (this._backlightUpdaterPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._backlightUpdaterPID);
         this._backlightUpdaterPID = -1;
      }

      this.setTrackUp(false);
      this._gpsDeviceStatus = 0;
   }

   final void navigateMode(boolean mode) {
      if (mode) {
         this._modeMgr.enableMode(1, true);
         this._dashboard.setMode(this._modeMgr.setCurrentMode(1));
         this._dashboard.setView(1);
         this._mapField.showHintLabel(LBSResources.getString(338));
         GPSMode gpsMode = (GPSMode)this._modeMgr.getMode(1);
         if (gpsMode != null) {
            gpsMode.setSatelliteCount(0);
            gpsMode.setMessage("", 0);
         }
      } else {
         this._modeMgr.enableMode(1, false);
         this._dashboard.setMode(null);
         this._dashboard.setView(0);
      }

      this.updateLayout();
      this.invalidate();
      this._mapField.updateScreenPosition();
   }

   final void startTracking() {
      GPSProvider locProvider = GPSProvider.getInstance();
      GPSDevice[] gpsDevices = locProvider.getLocationDevices(false);
      GPSDevice currentDevice = null;
      String deviceID = LBSOptions.getString(6531936621597631078L, null);
      EventLogger.logEvent(LBSApplication.UID, ("start tracking: " + deviceID).getBytes(), 0);
      if (deviceID != null) {
         for (int i = 0; i < gpsDevices.length; i++) {
            if (gpsDevices[i].equals(deviceID)) {
               currentDevice = gpsDevices[i];
               break;
            }
         }
      }

      this._firstLock = true;
      this._mapField._marker._latitude = Integer.MAX_VALUE;
      this._mapField._marker._longitude = Integer.MAX_VALUE;
      if (currentDevice == null) {
         GPSDeviceSelectionDialog selectionScreen = new GPSDeviceSelectionDialog();
         this._gpsSelectionScreen = selectionScreen;
         if (selectionScreen.doModal()) {
            this._gpsSelectionScreen = null;
            currentDevice = selectionScreen.getSelectedDevice();
            if (currentDevice != null) {
               LBSOptions.setString(6531936621597631078L, currentDevice.getDeviceID().toString());
            }
         }
      }

      this._pingThreadID = -1;
      if (currentDevice != null) {
         locProvider.addLocationListener(this, this._gpsLocationData);
         this._stopTrackingDialogDisplayed = false;
         this._tracking = true;
         this._gpsLock = false;
         this._gpsDeviceOn = false;
         this._keepBacklightOn = true;
         this.navigateMode(true);
         EventLogger.logEvent(LBSApplication.UID, "Start Navigation.".getBytes(), 0);
         if (!locProvider.startReporting(currentDevice)) {
            this.stopTracking();
            return;
         }

         if (this._gpsCaptureVerbs != null && this._gpsCaptureVerbs.length > 0) {
            this._gpsCaptureVerbs[0].invoke(Boolean.TRUE);
         }
      }

      if (this._backlightUpdaterPID != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._backlightUpdaterPID);
      }

      this._backlightUpdaterPID = UiApplication.getUiApplication().invokeLater(this._backlightUpdater, 5000, true);
      this.setTrackUp(this._autoTrackUp);
      this._following = true;
      this._mapField.setAutoPan(true);
   }

   final void add(LBSMenuItem menuItem) {
      Arrays.add(this._menuItems, menuItem);
   }

   final void createMenuItems() {
      int order = 332032;
      this.add(new MapScreen$5(this, 281, order++));
      this.add(new MapScreen$6(this, 282, order++));
      this.add(new MapScreen$7(this, 41, order++));
      this.add(new MapScreen$8(this, 86, order++));
      this.add(new MapScreen$9(this, 284, order++));
      order += 65536;
      this.add(new MapScreen$10(this, 322, order++));
      order += 65536;
      this.add(new MapScreen$12(this, 317, order++, 10));
      this.add(new MapScreen$13(this, 287, order++, 500));
      this.add(new MapScreen$14(this, 319, order++, 500));
      order += 65536;
      this.add(new MapScreen$15(this, 403, order++, 500));
      this.add(new MapScreen$16(this, 402, order++, 500));
      order += 65536;
      LBSMenuItem.MODE_ORDER = order;
      order += 65536;
      LBSMenuItem.CONTEXT_ORDER = order;
      order += 65536;
      this.add(new MapScreen$17(this, 274, order++));
      this.add(new MapScreen$18(this, 275, order++));
      this.add(new MapScreen$19(this, 49, order++));
      this.add(new MapScreen$20(this, 110, order++));
      order += 65536;
      this.add(new MapScreen$21(this, 3, order++));
      this.add(new MapScreen$22(this, 4, order++));
   }

   final void getDirections() {
      UiApplication.getUiApplication().pushScreen(new DirectionsChoiceScreen(this._mapField));
   }

   final boolean getPOIs(Object screen) {
      this._mapField._searchLatitude = this._mapField._latitude;
      this._mapField._searchLongitude = this._mapField._longitude;
      new POIVerb(screen, this, this._mapField, this._mapField._transform._cropView, this._mapField._zoom).invoke(null);
      return false;
   }

   public final boolean handleRespone(Object response) {
      if (response != null) {
         if (response instanceof String) {
            System.out.println("response=" + (String)response);
            if (!response.equals("")) {
               this._mapField._currentLocations.clear();
               this.openDocument("XML", (String)response, 0, false, "POI_SERVER");
               if (this._mapField._currentPOIs.length > 0) {
                  this._mapField._locationsListScreen = new LocationsListScreen(this._mapField, -1);
                  UiApplication.getUiApplication().pushScreen(this._mapField._locationsListScreen);
                  return true;
               }

               Dialog.alert(LBSResources.getString(102));
               return false;
            }
         } else if (response instanceof byte[]) {
            System.err.println("response=" + new String((byte[])response));
            this._mapField._currentLocations.clear();
            this.openDocument("XML", response, 0, false, "POI_SERVER");
            if (this._mapField._currentPOIs.length > 0) {
               this._mapField._locationsListScreen = new LocationsListScreen(this._mapField, -1);
               UiApplication.getUiApplication().pushScreen(this._mapField._locationsListScreen);
               return true;
            }

            if (UiApplication.getUiApplication().getActiveScreen() instanceof LocationsListScreen) {
               UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            }

            Dialog.alert(LBSResources.getString(102));
            return false;
         }
      }

      return false;
   }

   @Override
   public final void locationUpdated() {
      this._GPSUpdater.invoke();
      if (this._cellData != null) {
         if (this._gpsLocationData.getSatelliteCount() == 0) {
            this._cellData.invoke(Boolean.FALSE);
            return;
         }

         this._cellData.invoke(Boolean.TRUE);
         this._numbers.clear();
         this._numbers.put(LATITUDE, new Integer(this._gpsLocationData.getLatitudeInt()));
         this._numbers.put(LONGITUDE, new Integer(this._gpsLocationData.getLongitudeInt()));
         this._numbers.put(SPEED, new Float(this._gpsLocationData.getSpeed()));
         this._cellData.invoke(this._numbers);
      }
   }

   @Override
   public final void deviceStateChanged(GPSDevice device, String userMessage) {
      this._gpsDeviceOn = device.getDeviceState() == 1;
      if (userMessage != "") {
         this._statusMessage = userMessage;
         boolean showDashboard = false;
         GPSMode gpsMode = (GPSMode)this._modeMgr.getMode(1);
         if (this._gpsDeviceOn) {
            this._gpsDeviceStatus = 1;
            this._keepBacklightOn = (Display.getProperties() & 16384) != 0 || Backlight.isEnabled();
            this._trackUp = this._autoTrackUp;
            if (!this._gpsLocationData.isValid() && gpsMode != null) {
               showDashboard = true;
               EventLogger.logEvent(LBSApplication.UID, "GPS REPORTING but INVALID".getBytes(), 5);
               gpsMode.setMessage(userMessage, 0);
               this._dashboard.refresh();
               Application.getApplication().invokeLater(new MapScreen$4(this), 1000, false);
            }
         } else {
            this._mapField._marker._latitude = Integer.MAX_VALUE;
            this._mapField._marker._longitude = Integer.MAX_VALUE;
            String displayMessage = "";
            this._gpsLock = false;
            switch (device.getDeviceState()) {
               case 0:
                  boolean wasNotReporting = this._gpsDeviceStatus == 0;
                  this._gpsDeviceStatus = device.getDeviceState();
                  if (gpsMode != null) {
                     gpsMode.setSatelliteCount(0);
                     if (wasNotReporting) {
                        userMessage = LBSResources.getString(322);
                     }

                     showDashboard = true;
                     EventLogger.logEvent(LBSApplication.UID, "GPS NOT REPORTING".getBytes(), 5);
                     gpsMode.setMessage(userMessage, 0);
                     this._dashboard.refresh();
                  }
                  break;
               case 2:
               case 8:
                  this._gpsDeviceStatus = device.getDeviceState();
                  if (gpsMode != null) {
                     gpsMode.setSatelliteCount(0);
                     showDashboard = this._dashboard.getMode().getView() != 0;
                     gpsMode.setMessage(userMessage, 0);
                     if (showDashboard) {
                        this._dashboard.refresh();
                     }
                  }
                  break;
               case 4:
                  EventLogger.logEvent(LBSApplication.UID, "GPS FORCED STOP".getBytes(), 5);
                  String deviceName = device != null ? device.toString() : "";
                  displayMessage = MessageFormat.format(LBSResources.getString(70), new String[]{deviceName});
               case 10:
                  EventLogger.logEvent(LBSApplication.UID, "GPS ERROR".getBytes(), 5);
                  displayMessage = userMessage;
                  if (gpsMode != null) {
                     gpsMode.setSatelliteCount(0);
                     showDashboard = true;
                     gpsMode.setMessage(userMessage, 0);
                     this._dashboard.refresh();
                  }

                  this._statusMessage = "";
                  synchronized (Application.getEventLock()) {
                     this.stopTracking();
                  }

                  if (!this._stopTrackingDialogDisplayed) {
                     this._stopTrackingDialogDisplayed = true;
                     String msg = displayMessage;
                     synchronized (Application.getApplication().getAppEventLock()) {
                        Application.getApplication().invokeLater(new MapScreen$3(this, msg));
                     }
                  }
               default:
                  this._gpsDeviceStatus = 0;
            }

            this._mapField.invalidate();
         }

         if (showDashboard) {
            if (gpsMode.getView() == 0) {
               gpsMode.changeView(1);
               synchronized (Application.getApplication().getAppEventLock()) {
                  this.updateLayout();
               }

               this._hideDashboardPID = UiApplication.getUiApplication().invokeLater(this._hideDashboard, 5000, false);
            }

            this._dashboard.refresh();
         }
      }
   }

   @Override
   protected final boolean keyStatus(int keycode, int time) {
      return this._mapField.keyStatus(keycode, time);
   }

   static final void saveMapApp() {
      if (_internalInstance != null) {
         _internalInstance.saveView();
      }
   }

   private final void saveView() {
      LBSOptions.setLong(5352172232924914325L, this._mapField.getLatitude());
      LBSOptions.setLong(9116370231748750706L, this._mapField.getLongitude());
      LBSOptions.setInt(4041863254631303397L, this._mapField.getZoom());
      LBSOptions.setInt(-8669706351812607009L, this._mapField._rotation);
   }

   @Override
   public final boolean onClose() {
      FastDormancyManager.getInstance().setFastDormancy(true);
      this.saveView();
      this._tracking = false;
      GPSProvider GPS = GPSProvider.getInstance();
      GPSDevice device = GPS.getDeviceInUse();
      if (this._tracking && device != null && device.getDeviceState() == 1 && device.isInternalGPS() && !this._mapField.isRecording()) {
         this.stopTracking();
      }

      return super.onClose();
   }

   @Override
   protected final boolean invokeAction(int action) {
      if (Trackball.isSupported()) {
         this._mapField.zoom(true);
      }

      return true;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      String deviceID = LBSOptions.getString(6531936621597631078L, null);
      if (deviceID != null && deviceID.equals("")) {
         deviceID = null;
      }

      if (deviceID == null || deviceID != null && deviceID.equals("") && GPS.isSupported() && GPSProvider.isGPSSupportedOnNetwork()) {
         GPSDevice[] devices = GPSProvider.getInstance().getLocationDevices(false);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         Object obj = ar.get(-5162649070632360034L);
         if (obj != null) {
            for (int i = 0; i < devices.length; i++) {
               if (devices[i] == obj) {
                  GPSProvider.getInstance().setDeviceToUse(devices[i]);
                  deviceID = devices[i].getDeviceID().toString();
                  LBSOptions.setString(6531936621597631078L, deviceID);
                  break;
               }
            }
         }
      }

      if (instance != 0) {
         if (instance == 65536) {
            menu.add(this.getMenuItem(41));
            LBSMenuItem item = null;
            if (deviceID != null || GPS.isSupported()) {
               item = this.getMenuItem(322);
            }

            if (deviceID != null && item == null) {
               item = this.getMenuItem(323);
            }

            if (item != null) {
               menu.add(item);
            }

            menu.add(this.getMenuItem(317));
            if (this.getMenuItem(287).isVisible()) {
               menu.add(this.getMenuItem(287));
            }

            menu.add(this.getMenuItem(319));
         }
      } else {
         VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
         VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(4804476335504286437L);
         if (verbFactories != null && verbFactories.length > 0) {
            ContextObject context = this._mapField.createContext();

            for (int i = verbFactories.length - 1; i >= 0; i--) {
               verbToMenu.addVerbs(verbFactories[i].getVerbs(context));
            }

            verbToMenu.getMenu(menu, null, context);
         }

         ContextMenu cmenu = this._mapField.getContextMenu();
         menu.add(cmenu, true);

         for (int i = this._menuItems.length - 1; i >= 0; i--) {
            LBSMenuItem menuItem = this._menuItems[i];
            if (menuItem.isVisible()) {
               if (menuItem.getResourceId() == 322 && deviceID != null) {
                  menu.add(menuItem);
               } else if (menuItem.getResourceId() != 322) {
                  menu.add(menuItem);
               }
            }
         }
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._mapField.isSplashScreenOnShow()) {
         return true;
      }

      this._mapField.setFirstTimeLaunch(false);
      return this._mapField.navigationMovement(dx, dy, status, time) ? true : super.trackwheelRoll(dx, status, time);
   }

   @Override
   public final Menu getMenu(int instance) {
      ContextObject menuContext = new ContextObject();
      menuContext.put(244, new Integer(29488));
      SystemEnabledMenu menu = new SystemEnabledMenu(menuContext, null);
      Menu.setTargetScreen(this);
      menu.setInstance(instance);
      this.makeMenuWithContext(menu, instance);
      return menu;
   }

   private final int getMCC() {
      int mcc = -1;
      if (RadioInfo.getState() != 1) {
         return mcc;
      }

      if (RadioInfo.getNetworkType() != 8) {
         try {
            mcc = RadioInfo.getMCC(RadioInfo.getCurrentNetworkIndex());
            if (mcc != 0) {
               String mccStr = Integer.toHexString(mcc);
               return Integer.parseInt(mccStr);
            }
         } finally {
            ;
         }
      } else {
         try {
            if (SIMCard.isSupported()) {
               byte[] imsi = SIMCard.getIMSI();
               return SIMCard.getMCCFromIMSI(imsi);
            }
         } finally {
            ;
         }
      }

      return mcc;
   }

   private final DefaultMapView locateDefaultMapView(int mcc) {
      boolean find = false;
      DefaultMapView mapview = new DefaultMapView();
      Arrays.add(_defaultMapView, _europe);
      Arrays.add(_defaultMapView, _canada);
      Arrays.add(_defaultMapView, _us);
      Arrays.add(_defaultMapView, _asia);
      Arrays.add(_defaultMapView, _oceania);
      Arrays.add(_defaultMapView, _africa);
      Arrays.add(_defaultMapView, _northAmerica);
      Arrays.add(_defaultMapView, _southAmerica);

      for (int idx = 0; idx < 8; idx++) {
         find = _defaultMapView[idx].isValid(mcc);
         if (find) {
            mapview.copy(_defaultMapView[idx]);
            return mapview;
         }
      }

      return null;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return this._mapField.trackwheelClick(status, time) ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27 && this._gpsLock && !this._following && !this._mapField._zoomMode) {
         this._mapField.moveTo(this._gpsLocationData.getLatitudeInt(), this._gpsLocationData.getLongitudeInt());
         this._mapField.update(true);
         this._following = true;
         this._mapField.setAutoPan(true);
         this._mapField.cancelHint();
         this._mapField._currentLocations.setSemiFocused(false);
         return true;
      }

      if (this._mapField.keyChar(key, status, time)) {
         return true;
      }

      if (key == 27 && (this._tracking || this._mapField._currentLocations._count > 0 || this._mapField.getRoute() != null)) {
         UiApplication.getUiApplication().requestBackground();
         this._suspendTimeoutPID = UiApplication.getUiApplication().invokeLater(this._suspendTimeoutRunnable, 3600000, false);
         if (RadioInfo.getActiveWAFs() == 0) {
            this._backgroundRadioOff = true;
            return true;
         }

         if (!CoverageInfo.isCoverageSufficient(2) && !CoverageInfo.isCoverageSufficient(4) && !CoverageInfo.isCoverageSufficient(1)) {
            this._backgroundCoverageOff = true;
         }

         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private final void AddSpeedToArray(float speed) {
      MapScreen$GPSDataSpeedBearing temp = new MapScreen$GPSDataSpeedBearing(this);
      temp.setSpeed(speed);
      if (this.gpsSpeedBearing.length < 15) {
         Array.resize(this.gpsSpeedBearing, this.gpsSpeedBearing.length + 1);
         this.gpsSpeedBearing[this.gpsSpeedBearing.length - 1] = temp;
      } else {
         for (int i = 0; i < this.gpsSpeedBearing.length - 1; i++) {
            this.gpsSpeedBearing[i] = this.gpsSpeedBearing[i + 1];
         }

         this.gpsSpeedBearing[this.gpsSpeedBearing.length - 1] = temp;
      }
   }

   private final boolean isNewSpeedValid() {
      int speedZeroIdx = -1;

      for (int i = 0; i < this.gpsSpeedBearing.length; i++) {
         new MapScreen$GPSDataSpeedBearing(this);
         MapScreen$GPSDataSpeedBearing temp = this.gpsSpeedBearing[i];
         if (temp.getSpeed() < 4602678819172646912L) {
            speedZeroIdx = i;
         }
      }

      return speedZeroIdx < 10 || speedZeroIdx == this.gpsSpeedBearing.length - 1;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 259 && this._gpsDeviceStatus == 1) {
         this._keepBacklightOn = !Backlight.isEnabled();
      }

      int status = Keypad.status(keycode);
      boolean results = this._backdoor.keyDown(keycode) || (status & 1) == 1;
      if (!results && this._mapField.keyDown(keycode, time)) {
         return true;
      }

      int key = Keypad.key(keycode);
      switch (key) {
         case 32:
            if (this._tracking) {
               if (this._dashboard.getMode().getView() == 0) {
                  this._dashboard.getMode().changeView(1);
                  this._mapField.showHintLabel(LBSResources.getString(338));
               } else {
                  this._dashboard.getMode().changeView(0);
                  this._mapField.showHintLabel(LBSResources.getString(337));
               }

               this.updateLayout();
               return true;
            } else if (this._hideDashboardPID != -1) {
               UiApplication.getUiApplication().cancelInvokeLater(this._hideDashboardPID);
               this._hideDashboardPID = -1;
            }
         default:
            return results;
         case 72:
            if (LBSOptions.ADJUST_DENSITY) {
               if ((status & 1) == 1) {
                  this._mapField._paddingShield = Math.max(0, this._mapField._paddingShield - 1);
               } else {
                  this._mapField._paddingShield++;
               }

               this._mapField.showHintLabel("Shield padding is " + this._mapField._paddingShield + ".");
               this._mapField.update(true);
            }

            return true;
         case 84:
            if (LBSOptions.ADJUST_DENSITY) {
               if ((status & 1) == 1) {
                  this._mapField._paddingTown = Math.max(0, this._mapField._paddingTown - 1);
               } else {
                  this._mapField._paddingTown++;
               }

               this._mapField.showHintLabel("Town padding is " + this._mapField._paddingTown + ".");
               this._mapField.update(true);
            }

            return true;
      }
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      return this._mapField.keyRepeat(keycode, time);
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      if (Keypad.key(keycode) == 259 && this._gpsDeviceStatus == 1) {
         this._keepBacklightOn = Backlight.isEnabled();
      }

      return this._mapField.keyUp(keycode, time);
   }

   private final void shutdownGPS() {
      if (!this._application.isForeground() && this._tracking) {
         GPSProvider GPS = GPSProvider.getInstance();
         GPSDevice device = GPS.getDeviceInUse();
         if (device != null && device.getDeviceState() == 1) {
            this._restartTracking = true;
            if (this._gpsPID == -1) {
               this._gpsOffRunnable._device = device;
               EventLogger.logEvent(LBSApplication.UID, "Pausing GPS in 5 minutes.".getBytes(), 0);
               this._gpsPID = Application.getApplication().invokeLater(this._gpsOffRunnable, 300000, false);
            }

            this._reconnectGPS = true;
            return;
         }

         this._pingThreadID = -1;
      }
   }

   private final void restartGPS() {
      GPSProvider GPS = GPSProvider.getInstance();
      GPSDevice device = GPS.getDeviceInUse();
      if (device != null && device.getDeviceState() != 1) {
         if (this._reconnectGPS) {
            EventLogger.logEvent(LBSApplication.UID, ("Restarting GPS after being paused: " + device).getBytes(), 0);
            if (device.isInternalGPS()) {
               GPS.startReporting(device);
            } else {
               this.startTracking();
            }

            this._reconnectGPS = false;
         }
      } else if (device != null && device.getDeviceState() == 1 && this._gpsPID != -1) {
         EventLogger.logEvent(LBSApplication.UID, ("Cancel GPS pause. Resume GPS: " + device).getBytes(), 0);
         Application.getApplication().cancelInvokeLater(this._gpsPID);
         this._gpsPID = -1;
      }

      if (this._restartTracking) {
         this._restartTracking = false;
         if (this._tracking) {
            this._mapField.update(true);
         }
      }
   }

   private final void setTrackUp(boolean trackUp) {
      if (!this._tracking) {
         trackUp = false;
      }

      this._trackUp = trackUp;
      if (this._trackUp) {
         this._mapField.setRotation(360 - this._currentBearing);
      } else {
         this._mapField.setRotation(0);
      }

      this._mapField.update(true);
   }

   MapScreen() {
      super(LAYOUT_MANAGER, 562949953617920L);
      _internalInstance = this;
      this.createMenuItems();
      FontRegistry.loadFont("labelfont.cbtf", "net_rim_bb_lbs", "LabelFont");
      FontRegistry.loadFont("labelfontsmall.cbtf", "net_rim_bb_lbs", "LabelFontSmall");
      FontRegistry.loadFont("labelfontmedium.cbtf", "net_rim_bb_lbs", "LabelFontMedium");
      this._application = Application.getApplication();
      this._banner = RibbonBanner.getInstance().getStatusBanner(null, 3);
      this._banner.setTag(Tag.create("explorer-banner"));
      this.initMapField();
      this._modeMgr = new ModeManager(this._mapField);
      this.add(this._dashboard);
      this._gpsLocationData = new GPSLocationData();
      VerbRepository verbs = VerbRepository.getVerbRepository(6503629165413339198L);
      if (verbs != null) {
         this._cellVerbs = verbs.getVerbs("");
         if (this._cellVerbs.length > 0) {
            this._cellData = this._cellVerbs[0];
            this._numbers = new Hashtable();
         }
      }

      this._backdoor = new BackdoorKeyProcessor(true, this);
      this._GPSUpdater = new MapScreen$GPSRenderUpdater(this, null);
   }

   private final void initMapField() {
      this._mapField = new MapletMapField();
      this._mapField._screen = this;
      this.add(this._mapField);
      this._autoTrackUp = LBSOptions.getBoolean(-4064050259441269877L, true);
      int units = LBSOptions.getInt(-6817208986109478597L, 0);
      if (units == 0) {
         int defaultUnits = 2;
         String lang = Locale.getDefault().getLanguage();
         String ctry = Locale.getDefault().getCountry();
         if (!lang.equalsIgnoreCase("en") || !ctry.equalsIgnoreCase("US") && !ctry.equals("") || DeviceInfo.isSimulator()) {
            defaultUnits = 1;
         }

         LBSOptions.setInt(-6817208986109478597L, defaultUnits);
      }

      this._mapField.optionsChanged();
      GPSProvider locProvider = GPSProvider.getInstance();
      GPSDevice currentDevice = locProvider.getDeviceInUse();
      if (currentDevice == null || currentDevice == GPSDevice.NO_DEVICE) {
         GPSDevice[] gpsDevices = locProvider.getLocationDevices(true);
         String deviceID = LBSOptions.getString(6531936621597631078L, null);
         if (deviceID != null) {
            currentDevice = null;

            for (int i = 0; i < gpsDevices.length; i++) {
               if (gpsDevices[i].equals(deviceID)) {
                  currentDevice = gpsDevices[i];
                  break;
               }
            }

            if (currentDevice != null) {
               locProvider.setDeviceToUse(currentDevice);
            }
         } else if (GPS.isSupported() && GPSProvider.isGPSSupportedOnNetwork()) {
            for (int i = 0; i < gpsDevices.length; i++) {
               currentDevice = gpsDevices[i];
               if (currentDevice.isInternalGPS()) {
                  locProvider.setDeviceToUse(currentDevice);
                  LBSOptions.setString(6531936621597631078L, currentDevice.getDeviceID().toString());
                  break;
               }
            }
         }
      }

      this.optionsChanged();
      Application.getApplication().invokeLater(new MapScreen$1(this));
   }

   public static final MapPoint getMapCentre() {
      MapPoint currentCentre = new MapPoint();
      if (_internalInstance != null && _internalInstance._mapField != null) {
         currentCentre._y = _internalInstance._mapField._latitude;
         currentCentre._x = _internalInstance._mapField._longitude;
         return currentCentre;
      } else {
         currentCentre._y = (int)LBSOptions.getLong(5352172232924914325L, 4527776);
         currentCentre._x = (int)LBSOptions.getLong(9116370231748750706L, -9677355);
         return currentCentre;
      }
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      return this._mapField.trackwheelRoll(amount, status, time) ? true : super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this._mapField.navigationClick(status, time) ? true : super.trackwheelClick(status, time);
   }

   @Override
   protected final void onExposed() {
      if (Trackball.isSupported()) {
         this.setTrackballSensitivityXOffset(100);
         this.setTrackballSensitivityYOffset(100);
      }

      this.restartGPS();
      super.onExposed();
   }

   @Override
   protected final void onObscured() {
      if (Trackball.isSupported()) {
         this.setTrackballSensitivityXOffset(0);
         this.setTrackballSensitivityYOffset(0);
      }

      this.shutdownGPS();
      super.onObscured();
   }

   @Override
   protected final void onVisibilityChange(boolean visible) {
      if (visible) {
         this.restartGPS();
         this._mapField.scheduleUpdate();
      } else {
         this.shutdownGPS();
      }

      super.onVisibilityChange(visible);
   }

   @Override
   public final boolean openProductionBackdoor(int backdoorCode) {
      return false;
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1279415107:
         case 1279415109:
         case 1279415110:
         case 1279415111:
         case 1279415112:
         case 1279415113:
         case 1279415114:
         case 1279415115:
         case 1279415121:
            return Logger.openConfigScreen(backdoorCode);
         case 1279415108:
            LBSOptions.ADJUST_DENSITY = !LBSOptions.ADJUST_DENSITY;
            if (LBSOptions.ADJUST_DENSITY) {
               this._mapField.showHintLabel("Adjust Density mode is on.");
               this._mapField.showHintLabel("T/Shift-T: towns, H/Shift-H: hwy shields");
               return true;
            }

            this._mapField.showHintLabel("Adjust Density mode is off.");
            return true;
         case 1279415116:
            LBSOptions.TOGGLE_LAYERS = !LBSOptions.TOGGLE_LAYERS;
            if (LBSOptions.TOGGLE_LAYERS) {
               this._mapField.showHintLabel("Toggle Layers mode is on.");
               return true;
            }

            this._mapField.showHintLabel("Toggle Layers mode is off.");
            return true;
         case 1279415117:
            LBSOptions.SPHERICAL_CORRECTION = !LBSOptions.SPHERICAL_CORRECTION;
            this._mapField.updateScreenPosition();
            if (LBSOptions.SPHERICAL_CORRECTION) {
               this._mapField.showHintLabel("Spherical correction is on.");
            } else {
               this._mapField.showHintLabel("Spherical correction is off.");
            }

            this._mapField.update(true);
            return true;
         case 1279415118:
            InternalLBSNetworkScreen network = new InternalLBSNetworkScreen();
            Ui.getUiEngine().pushScreen(network);
            return true;
         case 1279415119:
         default:
            InternalLBSOptionsScreen server = new InternalLBSOptionsScreen();
            Ui.getUiEngine().pushScreen(server);
            return true;
         case 1279415120:
            LBSOptions.POINTER_MODE = !LBSOptions.POINTER_MODE;
            if (LBSOptions.POINTER_MODE) {
               this._mapField.showHintLabel("Pointer mode is on.");
               return true;
            }

            this._mapField.showHintLabel("Pointer mode is off.");
            return true;
         case 1279415122:
            try {
               long GPS_SIMULATOR_ITEM = -896149987693190195L;
               if (this._gpsCaptureVerbs == null) {
                  this._gpsCaptureVerbs = VerbRepository.getVerbRepository(GPS_SIMULATOR_ITEM).getVerbs(null);
               }

               if (this._gpsCaptureVerbs != null) {
                  this._gpsCaptureVerbs[0].invoke(new Long(GPS_SIMULATOR_ITEM));
                  return true;
               }
            } finally {
               return true;
            }

            return true;
         case 1279415123:
            this._mapField.rotate(!this._mapField._rotateMode);
            if (this._mapField._rotateMode) {
               this._mapField.showHintLabel("Rotate mode is on.");
               return true;
            }

            this._mapField.showHintLabel("Rotate mode is off.");
            return true;
         case 1279415124:
            this._mapField._timer.toggleTimer();
            this._mapField.update(true);
            return true;
      }
   }

   static final void access$800(MapScreen x0) {
      x0.updateLayout();
   }

   static final void access$1400(MapScreen x0) {
      x0.updateLayout();
   }
}
