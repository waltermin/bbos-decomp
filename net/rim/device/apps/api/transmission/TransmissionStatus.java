package net.rim.device.apps.api.transmission;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.DatagramStatusListenerUtil;
import net.rim.device.api.util.IntIntHashtable;

public final class TransmissionStatus {
   private static ResourceBundle _rb;
   private static IntIntHashtable _statusToResourceIdMap;
   private static int[] _statusToResourceIdMapInitData = new int[]{
      38,
      506,
      37,
      505,
      35,
      503,
      68,
      510,
      49,
      507,
      -1,
      511,
      67,
      509,
      66,
      508,
      32,
      500,
      33,
      501,
      34,
      502,
      36,
      504,
      80,
      512,
      81,
      513,
      -805036032,
      2024608815,
      776349875,
      -218971241,
      -1212803886,
      2080823297,
      -2098468118,
      309182464,
      71004292,
      -1070698486,
      1226839496,
      -939713006,
      1691193751,
      1356362592,
      -1659344436,
      2086650778,
      -1344200725,
      1962173951,
      448602996,
      1076908423,
      700779705,
      -736433280,
      1783696562,
      -2120625279,
      2014341798,
      -2136668647,
      605176356,
      403016212,
      700481740,
      1997445834,
      537102403,
      -605361967,
      -1431599163,
      2062936378,
      -1609547403,
      -2113391210,
      -842417816,
      -1715328876,
      25213209,
      577509698,
      164383376,
      1449514481,
      -1500718881,
      829073062,
      484236497,
      612385028,
      -2096756663,
      -2132219672,
      -632658485,
      -1051799858,
      -1577373463,
      -2079536236,
      13394152,
      -1387066153,
      1968517322,
      -1587047000,
      1090787945,
      1122356096,
      715855404,
      -913681183,
      1493600363,
      -1066561954,
      -58738207,
      -1218049722,
      1983589870,
      -1309477697,
      1267436845,
      -988591376,
      1295356774,
      1189861255,
      -1325289052,
      -1928527240,
      421337101,
      4860033,
      -1398707572,
      -660499359,
      -1905629027,
      1150729760,
      1110137723,
      1783242945,
      -1217157463,
      547929785,
      1109143200,
      742825266
   };

   public static final synchronized String getTransmissionStatusMessage(int code) {
      String message = DatagramStatusListenerUtil.getStatusMessage(code);
      if (message == null) {
         if (_statusToResourceIdMap == null) {
            _rb = ResourceBundle.getBundle(7150616992537201530L, "net.rim.device.apps.internal.resource.TransmissionStatus");
            _statusToResourceIdMap = new IntIntHashtable();
            int count = _statusToResourceIdMapInitData.length;

            for (int i = 0; i < count; i += 2) {
               _statusToResourceIdMap.put(_statusToResourceIdMapInitData[i], _statusToResourceIdMapInitData[i + 1]);
            }
         }

         int statusResourceId = _statusToResourceIdMap.get(code);
         if (statusResourceId == -1) {
            statusResourceId = 1;
         }

         message = _rb.getString(statusResourceId);
      }

      return message;
   }
}
