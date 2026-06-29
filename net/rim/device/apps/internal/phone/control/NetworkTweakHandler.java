package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.phone.VoiceServices;

public final class NetworkTweakHandler {
   private NetworkTweakHandler$NetworkTweakCommandHandler _myCommandHandler = new NetworkTweakHandler$NetworkTweakCommandHandler(this);
   private NetworkTweakHandler$NetworkTweakEventHandler _myEventHandler = new NetworkTweakHandler$NetworkTweakEventHandler(this);
   private int _networkFeatures;
   private int _networkService;
   private int _mcc;
   private String _voiceMailNumber;
   private static int[] _codes = new int[]{
      1262,
      30234,
      31234,
      32234,
      3232,
      16204,
      30216,
      1230,
      1219,
      2231,
      4220,
      10234,
      2272,
      7214,
      7262,
      8262,
      2232,
      604,
      15234,
      1214,
      1866858752,
      -1574934772,
      1661010020,
      1300917516,
      1443568481,
      1703898223,
      426115328,
      1711341677,
      1970380911,
      16784229,
      38616944,
      -682312344,
      2329784,
      2781953,
      1110078209,
      1091597375,
      1979777140,
      8556063,
      1802466817,
      1979777125,
      1097165679,
      1864803,
      1802466817,
      1684947301,
      221527,
      1802466817,
      1813988197,
      191288916,
      477389633,
      1870004480,
      290219371,
      1979777035,
      1449487215,
      1849778699,
      1869366116,
      -1258225535,
      201720005,
      761447,
      1090935558,
      1634234476,
      1812332785,
      100689154,
      1097138796,
      1950302916,
      100692752,
      1113916012,
      1078335,
      1694657542,
      134281298,
      2032535552,
      638058622,
      671613058,
      673147139,
      1085816688,
      455608320,
      134219553,
      -2073208024,
      -244572349,
      1848117248,
      1644910091
   };
   private static String[] _numbers = new String[]{
      "+491712523311",
      "121",
      "121",
      "121",
      "+436762200",
      "+31624001233",
      "+36309888444",
      "+420603123311",
      "+385981511",
      "+421903333999",
      "+385981511",
      "901",
      "171",
      "123",
      "+491793000333",
      "+491793000333",
      "+420602999999",
      "123",
      "121",
      "177"
   };

   public static final void registerOnceOnSystemStart() {
      new NetworkTweakHandler().initialize();
   }

   private NetworkTweakHandler() {
   }

   private final void initialize() {
      this._myCommandHandler.register();
      this._myEventHandler.register();
      if (RadioInfo.getNetworkType() == 3 && !DeviceInfo.isSimulator()) {
         Application app = VoiceServices.getUiApplication();
         app.invokeLater(new NetworkTweakHandler$1(this));
      }
   }
}
