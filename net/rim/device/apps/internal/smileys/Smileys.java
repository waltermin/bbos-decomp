package net.rim.device.apps.internal.smileys;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.resources.Resource;

public class Smileys {
   private static EmoticonStringPattern _smileys;

   public static void libMain(String[] args) {
      ApplicationRegistry.getApplicationRegistry().replace(EmoticonStringPattern.SYSTEM_DEFAULT_KEY, getSmileyFacility());
   }

   public static EmoticonStringPattern getSmileyFacility() {
      if (_smileys == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         Object obj = registry.get(EmoticonStringPattern.SYSTEM_DEFAULT_KEY);
         if (!(obj instanceof EmoticonStringPattern)) {
            StaticBitmapSmileys smileys = new StaticBitmapSmileys();
            smileys.setImageData(Resource.getResourceClass().getResource("smileys.png"));
            smileys.add(":)", 0);
            smileys.add(":-)", 0);
            smileys.add(";)", 1);
            smileys.add(";-)", 1);
            smileys.add(":(", 2);
            smileys.add(":-(", 2);
            smileys.add(">:O", 3);
            smileys.add(">:-O", 3);
            smileys.add(":D", 4);
            smileys.add(":-D", 4);
            smileys.add(":'(", 5);
            smileys.add(":'-)", 5);
            smileys.add(":O", 6);
            smileys.add(":-O", 6);
            smileys.add(":p", 7);
            smileys.add(":-p", 7);
            smileys.add("B)", 8);
            smileys.add("B-)", 8);
            smileys.add(":x", 9);
            smileys.add(":-x", 9);
            smileys.add(":s", 10);
            smileys.add(":-s", 10);
            smileys.add(":$", 11);
            smileys.add(":-$", 11);
            smileys.add(":*", 12);
            smileys.add(":-*", 12);
            smileys.add(":/", 13);
            smileys.add(":-/", 13);
            smileys.add("<3", 14);
            smileys.add("(y)", 15);
            smileys.add("(Y)", 15);
            smileys.add("(n)", 16);
            smileys.add("(N)", 16);
            smileys.add(":&", 17);
            smileys.add(":-&", 17);
            smileys.add(":>", 18);
            smileys.add(":->", 18);
            smileys.add("=D", 19);
            if (InternalServices.isReducedFormFactor()) {
               smileys.addLayout(
                  new int[]{
                     10,
                     8,
                     3,
                     4,
                     0,
                     -1,
                     9,
                     -1,
                     -1,
                     7,
                     -1,
                     19,
                     12,
                     -1,
                     6,
                     -1,
                     1,
                     -1,
                     -1,
                     2,
                     5,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -804650982,
                     12,
                     8,
                     18,
                     4,
                     11,
                     -1,
                     13,
                     1,
                     15,
                     5,
                     17,
                     19,
                     -1,
                     -1,
                     6,
                     7,
                     -1,
                     14,
                     10,
                     2,
                     16,
                     3,
                     -1,
                     9,
                     0,
                     -1,
                     -804650982,
                     17,
                     16,
                     18,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     11,
                     -1,
                     -1,
                     13,
                     -1,
                     -1,
                     -1,
                     -1,
                     15,
                     14,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -805044214,
                     1651663662,
                     1852845578,
                     2663,
                     -804651007,
                     51,
                     -805044197,
                     100692579,
                     11408,
                     512,
                     1392508928,
                     -1925260154,
                     15988650,
                     0,
                     -805043806,
                     100692579,
                     11408,
                     1918960128,
                     1392508928,
                     -1925260154,
                     15988650,
                     385875969,
                     16777328,
                     7560065
                  }
               );
               smileys.addLayout(
                  new int[]{
                     17,
                     16,
                     18,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     11,
                     -1,
                     -1,
                     13,
                     -1,
                     -1,
                     -1,
                     -1,
                     15,
                     14,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -805044214,
                     1651663662,
                     1852845578,
                     2663,
                     -804651007,
                     51,
                     -805044197,
                     100692579,
                     11408,
                     512,
                     1392508928,
                     -1925260154,
                     15988650,
                     0,
                     -805043806,
                     100692579,
                     11408,
                     1918960128,
                     1392508928,
                     -1925260154,
                     15988650,
                     385875969,
                     16777328,
                     7560065,
                     18087956,
                     -2132661958,
                     -1479008114,
                     -1428641576,
                     -1478970408,
                     -1445427751,
                     989924864,
                     -1904156119,
                     -642066400,
                     -659367803,
                     17694889,
                     -2132662214,
                     -1378344818,
                     -1965444392,
                     184583897,
                     542063105,
                     -1478968616,
                     -1462192424,
                     973151232,
                     -1479008188,
                     -1428641576,
                     -1478970408,
                     -1445427751,
                     -662447840,
                     -661988952,
                     11131057,
                     658112783,
                     -1904156120,
                     -660023264,
                     -645670489,
                     17760397,
                     -652193990,
                     -643114875,
                     -660088703,
                     10934444,
                     1882849569,
                     -662316768,
                     -1479008081,
                     -2066119463,
                     -1478970408,
                     -668956967,
                     -660088658,
                     -659760975,
                     9165223,
                     692191502,
                     546210018,
                     -2049332008,
                     -2066117927,
                     973152000,
                     -2066145160,
                     -652171304,
                     -645539446,
                     -645473917,
                     -1479008121
                  }
               );
            } else {
               smileys.addLayout(
                  new int[]{
                     12,
                     8,
                     18,
                     4,
                     11,
                     -1,
                     13,
                     1,
                     15,
                     5,
                     17,
                     19,
                     -1,
                     -1,
                     6,
                     7,
                     -1,
                     14,
                     10,
                     2,
                     16,
                     3,
                     -1,
                     9,
                     0,
                     -1,
                     -804650982,
                     17,
                     16,
                     18,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     11,
                     -1,
                     -1,
                     13,
                     -1,
                     -1,
                     -1,
                     -1,
                     15,
                     14,
                     -1,
                     -1,
                     -1,
                     -1,
                     -1,
                     -805044214,
                     1651663662,
                     1852845578,
                     2663,
                     -804651007,
                     51,
                     -805044197,
                     100692579,
                     11408,
                     512,
                     1392508928,
                     -1925260154,
                     15988650,
                     0,
                     -805043806,
                     100692579,
                     11408,
                     1918960128,
                     1392508928,
                     -1925260154,
                     15988650,
                     385875969,
                     16777328,
                     7560065,
                     18087956,
                     -2132661958,
                     -1479008114,
                     -1428641576,
                     -1478970408,
                     -1445427751,
                     989924864,
                     -1904156119,
                     -642066400,
                     -659367803,
                     17694889,
                     -2132662214,
                     -1378344818,
                     -1965444392,
                     184583897,
                     542063105,
                     -1478968616,
                     -1462192424,
                     973151232,
                     -1479008188,
                     -1428641576,
                     -1478970408,
                     -1445427751,
                     -662447840,
                     -661988952,
                     11131057,
                     658112783
                  }
               );
            }

            _smileys = smileys;
            registry.replace(EmoticonStringPattern.SYSTEM_DEFAULT_KEY, _smileys);
         } else {
            _smileys = (EmoticonStringPattern)obj;
         }
      }

      return _smileys;
   }
}
