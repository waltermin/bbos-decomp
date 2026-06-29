package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.apps.internal.lbs.LBSOptions;

public final class LayerOptions {
   private LayerGroup[] _layerGroups = new LayerGroup[6];
   private static final long PARAM_LAYER_WATER;
   private static final long PARAM_LAYER_AREAS;
   private static final long PARAM_LAYER_STREETS;
   private static final long PARAM_LAYER_PARKS;
   private static final long PARAM_LAYER_PLACES;
   private static final long PARAM_LAYER_LANDMARKS;
   private static final long PARAM_LAYER_WATERPIPES;
   private static final long PARAM_LAYER_FIREPOL;
   private static final long PARAM_LAYER_EPA;
   private static final long PARAM_LAYER_BUILDING;
   private static LayerOptions INSTANCE;

   private LayerOptions() {
      this._layerGroups[0] = new LayerGroup(0, "Water");
      this._layerGroups[1] = new LayerGroup(1, "Areas");
      this._layerGroups[2] = new LayerGroup(2, "Streets");
      this._layerGroups[3] = new LayerGroup(3, "Parks");
      this._layerGroups[4] = new LayerGroup(4, "Place Names");
      this._layerGroups[5] = new LayerGroup(5, "Landmarks");
      this._layerGroups[0]
         .setLayerIDs(
            new int[]{
               56848,
               56875,
               56879,
               56923,
               56849,
               -804651004,
               56851,
               56872,
               56877,
               56924,
               -804650992,
               56865,
               56880,
               56885,
               56927,
               56866,
               56881,
               56886,
               56867,
               56882
            }
         );
      this._layerGroups[1]
         .setLayerIDs(new int[]{56851, 56872, 56877, 56924, -804650992, 56865, 56880, 56885, 56927, 56866, 56881, 56886, 56867, 56882, 56887, 56868});
      this._layerGroups[2]
         .setLayerIDs(
            new int[]{
               56897,
               56890,
               56894,
               56898,
               56891,
               56895,
               56899,
               56892,
               56896,
               56900,
               56893,
               56901,
               56893,
               56901,
               56908,
               56907,
               56906,
               56905,
               56904,
               56918,
               56917,
               56916,
               56915,
               56921,
               56920,
               56919,
               56843,
               56873,
               -804651004,
               56909,
               56910,
               56911,
               56847,
               -804651004,
               5000,
               50000,
               500000,
               5000000,
               -804651000,
               -24,
               -3,
               -3,
               18,
               18,
               -3,
               -3,
               -24,
               712179968,
               712179968,
               16806977,
               -2104615050,
               712376576,
               1870004480,
               16803179,
               1701539702,
               477389633,
               1870004480,
               1849779563,
               56711012,
               1870004480,
               1883333995,
               -1581068944,
               6425430,
               1802466817,
               185683045,
               -977993472,
               -977993472,
               1527120072,
               1812332550,
               100689154,
               1399128684,
               2343819,
               1694657542,
               600017747,
               2031722056,
               3896393,
               638058504,
               134250105,
               119610152,
               205588480,
               1091043425,
               1706652771,
               319578963,
               1665206272,
               1415952756,
               6634344,
               1952661768,
               1096246713,
               134247238,
               57961537,
               134219381,
               -848269247,
               1091043522,
               -1026723728,
               1668506948,
               421357938,
               1091043443,
               -1026723728,
               1091043455,
               -1026723728,
               765355936,
               1916864512,
               134248910,
               134247489,
               409797442,
               54659072,
               1939871853,
               503407,
               -1569766904,
               134247268,
               2120524354,
               -1388181504
            }
         );
      this._layerGroups[3]
         .setLayerIDs(new int[]{56838, 56874, 56878, 56926, -804651003, 56848, 56875, 56879, 56923, 56849, -804651004, 56851, 56872, 56877, 56924, -804650992});
      this._layerGroups[4]
         .setLayerIDs(
            new int[]{
               56865,
               56880,
               56885,
               56927,
               56866,
               56881,
               56886,
               56867,
               56882,
               56887,
               56868,
               56883,
               56888,
               56869,
               56884,
               56889,
               51,
               -804650980,
               56897,
               56890,
               56894,
               56898,
               56891,
               56895,
               56899,
               56892,
               56896,
               56900,
               56893,
               56901,
               56893,
               56901,
               56908,
               56907,
               56906,
               56905,
               56904,
               56918,
               56917,
               56916,
               56915,
               56921,
               56920,
               56919,
               56843,
               56873,
               -804651004,
               56909,
               56910,
               56911,
               56847,
               -804651004,
               5000,
               50000,
               500000,
               5000000,
               -804651000,
               -24,
               -3,
               -3,
               18,
               18,
               -3,
               -3
            }
         );
      this._layerGroups[5].setLayerIDs(new int[]{56909, 56910, 56911, 56847, -804651004, 5000, 50000, 500000, 5000000, -804651000, -24, -3, -3, 18, 18, -3});
      this._layerGroups[0].setVisible(LBSOptions.getBoolean(396181434621866035L, true));
      this._layerGroups[1].setVisible(LBSOptions.getBoolean(-1918199190376411056L, true));
      this._layerGroups[2].setVisible(LBSOptions.getBoolean(-8188202621221368016L, true));
      this._layerGroups[3].setVisible(LBSOptions.getBoolean(8507163742773983544L, true));
      this._layerGroups[4].setVisible(LBSOptions.getBoolean(-8621727332676546690L, true));
      this._layerGroups[5].setVisible(LBSOptions.getBoolean(9003665314392694763L, true));
   }

   public static final LayerOptions getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new LayerOptions();
      }

      return INSTANCE;
   }

   public final String[] getLayerGroupNames() {
      String[] names = new Object[this._layerGroups.length];

      for (int i = 0; i < names.length; i++) {
         names[i] = this._layerGroups[i].toString();
      }

      return names;
   }

   public final void setVisible(int layerGroup, boolean visible) {
      this._layerGroups[layerGroup].setVisible(visible);
   }

   public final boolean isLayerVisibleByUser(int lid) {
      for (int i = 0; i < this._layerGroups.length; i++) {
         if (this._layerGroups[i].containsLID(lid)) {
            return this._layerGroups[i].isVisible();
         }
      }

      return true;
   }

   public final boolean isGroupVisible(int groupID) {
      return this._layerGroups[groupID].isVisible();
   }

   public final void commit() {
      LBSOptions.setBoolean(396181434621866035L, this._layerGroups[0].isVisible());
      LBSOptions.setBoolean(-1918199190376411056L, this._layerGroups[1].isVisible());
      LBSOptions.setBoolean(-8188202621221368016L, this._layerGroups[2].isVisible());
      LBSOptions.setBoolean(8507163742773983544L, this._layerGroups[3].isVisible());
      LBSOptions.setBoolean(-8621727332676546690L, this._layerGroups[4].isVisible());
      LBSOptions.setBoolean(9003665314392694763L, this._layerGroups[5].isVisible());
   }
}
