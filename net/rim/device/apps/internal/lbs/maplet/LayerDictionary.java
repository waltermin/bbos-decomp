package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Array;

public final class LayerDictionary {
   IntHashtable _styles = new IntHashtable();
   int[] _zorder = new int[0];
   StyleSet[] _stylesInZOrder = new StyleSet[0];
   IntHashtable _drawCache = new IntHashtable();
   IntHashtable _polyCache = new IntHashtable();
   IntHashtable _labelCache = new IntHashtable();
   IntHashtable _visibleCache = new IntHashtable();
   public static final int BACKGROUND_PHASE = 0;
   public static final int FOREGROUND_PHASE = 1;
   public static final int LID_ROOT = 56832;
   protected static final int LID_PARKS = 56838;
   protected static final int LID_RAILROADS = 56843;
   protected static final int LID_LANDMARKS = 56847;
   protected static final int LID_WATERPOLY = 56848;
   protected static final int LID_WATERSEG = 56849;
   protected static final int LID_AREAS = 56851;
   protected static final int LID_C1_LABELS_A = 56865;
   protected static final int LID_C2_LABELS_A = 56866;
   protected static final int LID_C3_LABELS_A = 56867;
   protected static final int LID_C4_LABELS_A = 56868;
   protected static final int LID_C5_LABELS_A = 56869;
   protected static final int LID_AREAS_B = 56872;
   protected static final int LID_RAILROADS_B = 56873;
   protected static final int LID_PARKS_B = 56874;
   protected static final int LID_WATERPOLY_B = 56875;
   protected static final int LID_AREAS_C = 56877;
   protected static final int LID_PARKS_C = 56878;
   protected static final int LID_WATERPOLY_C = 56879;
   protected static final int LID_C1_LABELS_B = 56880;
   protected static final int LID_C2_LABELS_B = 56881;
   protected static final int LID_C3_LABELS_B = 56882;
   protected static final int LID_C4_LABELS_B = 56883;
   protected static final int LID_C5_LABELS_B = 56884;
   protected static final int LID_C1_LABELS_C = 56885;
   protected static final int LID_C2_LABELS_C = 56886;
   protected static final int LID_C3_LABELS_C = 56887;
   protected static final int LID_C4_LABELS_C = 56888;
   protected static final int LID_C5_LABELS_C = 56889;
   public static final int LID_STREETS_1_B = 56890;
   protected static final int LID_STREETS_2_B = 56891;
   protected static final int LID_STREETS_3_B = 56892;
   protected static final int LID_STREETS_4_B = 56893;
   protected static final int LID_STREETS_1_C = 56894;
   protected static final int LID_STREETS_2_C = 56895;
   protected static final int LID_STREETS_3_C = 56896;
   protected static final int LID_STREETS_1_A = 56897;
   protected static final int LID_STREETS_2_A = 56898;
   protected static final int LID_STREETS_3_A = 56899;
   protected static final int LID_STREETS_4_A = 56900;
   public static final int LID_STREETS_5_A = 56901;
   protected static final int LID_BORDERS_B = 56902;
   protected static final int LID_BORDERS_C = 56903;
   protected static final int LID_STREETS_1_AL = 56904;
   protected static final int LID_STREETS_2_AL = 56905;
   protected static final int LID_STREETS_3_AL = 56906;
   protected static final int LID_STREETS_4_AL = 56907;
   protected static final int LID_STREETS_5_AL = 56908;
   protected static final int LID_AREA_LANDMARKS_A = 56909;
   protected static final int LID_AREA_LANDMARKS_B = 56910;
   protected static final int LID_AREA_LANDMARKS_C = 56911;
   protected static final int LID_STREETS_1_BL = 56915;
   protected static final int LID_STREETS_2_BL = 56916;
   protected static final int LID_STREETS_3_BL = 56917;
   protected static final int LID_STREETS_4_BL = 56918;
   protected static final int LID_STREETS_1_CL = 56919;
   protected static final int LID_STREETS_2_CL = 56920;
   protected static final int LID_STREETS_3_CL = 56921;
   protected static final int LID_BORDERS_D = 56922;
   protected static final int LID_WATERPOLY_D = 56923;
   protected static final int LID_AREAS_D = 56924;
   protected static final int LID_STREETS_1_D = 56925;
   protected static final int LID_PARKS_D = 56926;
   protected static final int LID_C1_LABELS_D = 56927;
   protected static final int LID_BORDERS_2_D = 56928;
   static LayerDictionary _instance;
   private static final long GUID = 9075970641935861263L;
   static final boolean USE_OUTLINES = false;

   public static final LayerDictionary getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (LayerDictionary)ar.getOrWaitFor(9075970641935861263L);
      if (_instance == null) {
         _instance = new LayerDictionary();
         ar.put(9075970641935861263L, _instance);
      }

      return _instance;
   }

   private LayerDictionary() {
      this.populate();
   }

   public final void optionsChanged() {
      this._drawCache.clear();
      this._labelCache.clear();
      this._polyCache.clear();
   }

   final void add(int id, int zorder, StyleSet styleset) {
      Arrays.add(this._stylesInZOrder, styleset);
      Arrays.add(this._zorder, id);
      this._styles.put(id, styleset);
   }

   public final boolean applyDrawStyle(Graphics graphics, int layerId, int zoom, int phase) {
      StyleSet styleSet = (StyleSet)this._styles.get(layerId);
      return styleSet != null ? styleSet.apply(graphics, zoom, phase) : false;
   }

   public final boolean isMarkerLayer(int layerId) {
      return this._styles.get(layerId) instanceof MarkerStyleSet;
   }

   public final int getLabelPathLayer(int layerId) {
      LabelStyleSet styleSet = (LabelStyleSet)this._styles.get(layerId);
      return styleSet != null ? styleSet._associatedPathLayer : -1;
   }

   public final int[] getDrawLayers(int zoom) {
      int[] layers = (int[])this._drawCache.get(zoom);
      if (layers != null) {
         return layers;
      }

      int count = this._zorder.length;
      layers = new int[0];
      LayerOptions opt = LayerOptions.getInstance();

      for (int i = 0; i < count; i++) {
         StyleSet style = this._stylesInZOrder[i];
         if (style instanceof LineStyleSet && style.isVisibleAt(zoom) && opt.isLayerVisibleByUser(this._zorder[i])) {
            Arrays.add(layers, this._zorder[i]);
         }
      }

      this._drawCache.put(zoom, layers);
      return layers;
   }

   public final int[] getPolygonLayers(int zoom) {
      int[] layers = (int[])this._polyCache.get(zoom);
      if (layers != null) {
         return layers;
      }

      int count = this._zorder.length;
      layers = new int[0];
      LayerOptions opt = LayerOptions.getInstance();

      for (int i = 0; i < count; i++) {
         StyleSet style = this._stylesInZOrder[i];
         if (style instanceof PolygonStyleSet && style.isVisibleAt(zoom) && opt.isLayerVisibleByUser(this._zorder[i])) {
            Arrays.add(layers, this._zorder[i]);
         }
      }

      this._polyCache.put(zoom, layers);
      return layers;
   }

   public final int[] getLabelLayers(int zoom) {
      int[] layers = (int[])this._labelCache.get(zoom);
      if (layers != null) {
         return layers;
      }

      int count = this._zorder.length;
      layers = new int[0];
      LayerOptions opt = LayerOptions.getInstance();

      for (int i = 0; i < count; i++) {
         StyleSet style = this._stylesInZOrder[i];
         if (style instanceof LabelStyleSet && style.isVisibleAt(zoom) && opt.isLayerVisibleByUser(this._zorder[i])) {
            Arrays.add(layers, this._zorder[i]);
         }
      }

      this._labelCache.put(zoom, layers);
      return layers;
   }

   public final byte[] getVisibleLayers(int zoom) {
      byte[] layers = (byte[])this._visibleCache.get(zoom);
      if (layers != null) {
         return layers;
      }

      int count = this._styles.size();
      layers = new byte[count];
      int layerCount = 0;
      IntEnumeration enumeration = this._styles.keys();
      LayerOptions opt = LayerOptions.getInstance();

      for (int i = 0; i < count; i++) {
         int id = enumeration.nextElement();
         StyleSet style = (StyleSet)this._styles.get(id);
         if (style.isVisibleAt(zoom) && opt.isLayerVisibleByUser(id)) {
            layers[layerCount++] = (byte)id;
         }
      }

      Array.resize(layers, layerCount);
      this._visibleCache.put(zoom, layers);
      return layers;
   }

   public final boolean isVisible(int id, int zoom) {
      StyleSet style = (StyleSet)this._styles.get(id);
      return style != null && style.isVisibleAt(zoom);
   }

   public static final int getBackgroundColor() {
      return 12700843;
   }

   final void populate() {
      LineStyleSet lineStyleSet = null;
      this.add(56851, 10, new PolygonStyleSet(0, 4, 13347498));
      this.add(56872, 10, new PolygonStyleSet(5, 8, 13347498));
      this.add(56877, 10, new PolygonStyleSet(9, 11, 13347498));
      this.add(56924, 10, new PolygonStyleSet(12, 13, 13347498));
      this.add(56909, 12, new PolygonStyleSet(0, 4, 13882323));
      this.add(56910, 12, new PolygonStyleSet(5, 8, 13882323));
      this.add(56911, 12, new PolygonStyleSet(9, 11, 13882323));
      this.add(56838, 25, new PolygonStyleSet(0, 4, 8768361));
      this.add(56874, 25, new PolygonStyleSet(5, 8, 8768361));
      this.add(56878, 25, new PolygonStyleSet(9, 11, 8768361));
      this.add(56926, 25, new PolygonStyleSet(12, 13, 8768361));
      this.add(56848, 30, new PolygonStyleSet(0, 4, 10269390));
      this.add(56875, 30, new PolygonStyleSet(5, 8, 10269390));
      this.add(56879, 30, new PolygonStyleSet(9, 11, 10269390));
      this.add(56923, 30, new PolygonStyleSet(12, 15, 10269390));
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 1, 10269390, 6);
      lineStyleSet.add(2, 3, 10269390, 4);
      lineStyleSet.add(4, 5, 10269390, 2);
      lineStyleSet.add(6, 8, 10269390, 1);
      this.add(56849, 30, lineStyleSet);
      this.add(56847, 35, new PolygonStyleSet(0, 11, -6515052));
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 0, 16777215, 8);
      lineStyleSet.add(1, 1, 16777215, 6);
      lineStyleSet.add(2, 2, 16777215, 4);
      lineStyleSet.add(3, 3, 16777215, 1);
      this.add(56901, 40, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 0, 16775885, 16);
      lineStyleSet.add(1, 1, 16775885, 14);
      lineStyleSet.add(2, 2, 16775885, 4);
      lineStyleSet.add(3, 3, 16775885, 3);
      lineStyleSet.add(4, 4, 16775885, 2);
      this.add(56900, 42, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 5, 16775885, 2);
      lineStyleSet.add(6, 6, 16775885, 2);
      this.add(56893, 42, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 0, 16775885, 20);
      lineStyleSet.add(1, 1, 16775885, 16);
      lineStyleSet.add(2, 2, 16775885, 10);
      lineStyleSet.add(3, 3, 16775885, 8);
      lineStyleSet.add(4, 4, 16775885, 6);
      this.add(56899, 43, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 6, 16775885, 2);
      lineStyleSet.add(7, 7, 16775885, 2);
      this.add(56892, 43, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 0, 16775885, 20);
      lineStyleSet.add(1, 1, 16775885, 16);
      lineStyleSet.add(2, 2, 16775885, 10);
      lineStyleSet.add(3, 3, 16775885, 8);
      lineStyleSet.add(4, 4, 16775885, 6);
      this.add(56898, 44, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 6, 16775885, 4);
      lineStyleSet.add(7, 8, 16775885, 2);
      this.add(56891, 44, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(9, 10, 16775885, 1);
      this.add(56895, 44, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 0, 16759603, 24);
      lineStyleSet.add(1, 1, 16759603, 20);
      lineStyleSet.add(2, 2, 16759603, 16);
      lineStyleSet.add(3, 3, 16759603, 12);
      lineStyleSet.add(4, 4, 16759603, 12);
      this.add(56897, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 6, 16759603, 4);
      lineStyleSet.add(7, 8, 16759603, 2);
      this.add(56890, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(9, 11, 16759603, 2);
      this.add(56894, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(12, 12, 16759603, 1);
      this.add(56925, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(0, 4, 0, 1);
      this.add(56843, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 5, 0, 1);
      this.add(56873, 45, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(5, 8, 16711680, 2);
      this.add(56902, 60, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(9, 11, 16711680, 1);
      this.add(56903, 60, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(12, 15, 8421504, 1);
      this.add(56922, 60, lineStyleSet);
      lineStyleSet = new LineStyleSet();
      lineStyleSet.add(12, 15, 16711680, 1);
      this.add(56928, 60, lineStyleSet);
      this.add(56865, 100, new LabelStyleSet(0, 4, -1));
      this.add(56880, 100, new LabelStyleSet(5, 8, -1));
      this.add(56881, 105, new LabelStyleSet(5, 8, -1));
      this.add(56882, 110, new LabelStyleSet(5, 8, -1));
      this.add(56883, 115, new LabelStyleSet(5, 8, -1));
      this.add(56884, 120, new LabelStyleSet(5, 8, -1));
      this.add(56885, 100, new LabelStyleSet(9, 11, -1));
      this.add(56886, 105, new LabelStyleSet(9, 11, -1));
      this.add(56887, 110, new LabelStyleSet(9, 11, -1));
      this.add(56888, 115, new LabelStyleSet(9, 11, -1));
      this.add(56889, 120, new LabelStyleSet(9, 11, -1));
      this.add(56927, 100, new LabelStyleSet(12, 15, -1));
      this.add(56908, 120, new LabelStyleSet(0, 2, 56901));
      this.add(56907, 120, new LabelStyleSet(0, 4, 56900));
      this.add(56906, 120, new LabelStyleSet(0, 4, 56899));
      this.add(56905, 120, new LabelStyleSet(0, 4, 56898));
      this.add(56904, 120, new LabelStyleSet(0, 4, 56897));
      this.add(56915, 120, new MarkerStyleSet(5, 8, 56890));
      this.add(56916, 120, new MarkerStyleSet(5, 8, 56891));
      this.add(56917, 120, new MarkerStyleSet(5, 7, 56892));
      this.add(56918, 120, new MarkerStyleSet(5, 6, 56893));
      this.add(56919, 120, new MarkerStyleSet(9, 11, 56894));
      this.add(56920, 120, new MarkerStyleSet(9, 10, 56895));
   }
}
