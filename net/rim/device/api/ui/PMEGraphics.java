package net.rim.device.api.ui;

import net.rim.device.api.system.ApplicationRegistry;

public final class PMEGraphics {
   private int _returnState;
   private int _lastNode;
   private XYRect _lastViewport = new XYRect();
   private XYRect _lastClip = new XYRect();
   private boolean _enabled;
   private int _iNodeRoot = -1;
   private int[] _nodes;
   private int[][] _coords;
   private byte[][] _pointTypes;
   private Object[] _images;
   private Object[] _foreignObjects;
   private char[][] _strings;
   private String[] _fontFamilies;
   private String _defaultFontFamily;
   private XYRect _viewport = new XYRect();
   private int[] _transform;
   private XYRect _clip = new XYRect();
   private int _clipColour = 16711680;
   private int[] _nodeList;
   private int[] _leafNodeList;
   private XYRect _boundsDirty;
   private int _drawBounds = 0;
   private int _boundsColour = 255;
   private int _bkgrndColour = -1;
   private int[] _stats;
   private long _statsResetTime;
   private int _cacheID;
   private PMEGraphics$GfxContext _gfxContext = new PMEGraphics$GfxContext();
   public static final long REG_ID = 7638954399719443704L;
   static PMEGraphics$PMERegistryOptions _regOptions;
   public static final int PMEOPTION_DISABLE = 1;
   public static final int PMEOPTION_SHOW_CLIP = 2;
   public static final int PMEOPTION_SHOW_BOUNDS = 3;
   public static final int PMEOPTION_LOG_STATS = 4;
   public static final int PMEGRAPHICS_FAIL = 2;
   public static final int PMEGRAPHICS_FAIL_ALLOC = 3;
   public static final int PMEGRAPHICS_FAIL_INDEX = 4;
   public static final int PMEGRAPHICS_FAIL_ATTR = 5;
   public static final int PMEGRAPHICS_FAIL_REF = 6;
   public static final int PMEGRAPHICS_FAIL_FILL = 7;
   public static final int PMEGRAPHICS_FAIL_UPDATE = 8;
   public static final int PMEGRAPHICS_OK = 0;
   public static final int PMEGRAPHICS_UNKNOWN_NODE = 1;
   public static final int PMEGRAPHICS_STATS_UPDATE = 0;
   public static final int PMEGRAPHICS_STATS_UPDATE_RECT = 1;
   public static final int PMEGRAPHICS_STATS_UPDATE_ELLIPSE = 2;
   public static final int PMEGRAPHICS_STATS_UPDATE_PATH = 3;
   public static final int PMEGRAPHICS_STATS_UPDATE_IMAGE = 4;
   public static final int PMEGRAPHICS_STATS_UPDATE_TEXT = 5;
   public static final int PMEGRAPHICS_STATS_RENDER = 6;
   public static final int PMEGRAPHICS_STATS_RENDER_RECT = 7;
   public static final int PMEGRAPHICS_STATS_RENDER_ELLIPSE = 8;
   public static final int PMEGRAPHICS_STATS_RENDER_PATH = 9;
   public static final int PMEGRAPHICS_STATS_RENDER_IMAGE = 10;
   public static final int PMEGRAPHICS_STATS_RENDER_TEXT = 11;
   public static final int PMEGRAPHICS_STATS_FILL_DATA = 12;
   public static final int PMEGRAPHICS_STATS_UPDATE_DATA = 13;
   public static final int PMEGRAPHICS_STATS_UPDATE_VP = 14;
   public static final int PMEGRAPHICS_STATS_RENDER_VP = 15;
   public static final int PMEGRAPHICS_STATS_NUM = 16;
   public static final int PMEGRAPHICS_STATS_SIZE = 4;

   public final void setFillContext(Object g) {
      if (g instanceof Graphics) {
         Graphics gfx = (Graphics)g;
         gfx.setColor(this._gfxContext._fillColour);
         gfx.setGlobalAlpha(this._gfxContext._fillOpacity);
      }
   }

   public final void setStrokeContext(Object g) {
      if (g instanceof Graphics) {
         Graphics gfx = (Graphics)g;
         gfx.setColor(this._gfxContext._strokeColour);
         gfx.setGlobalAlpha(this._gfxContext._strokeOpacity);
      }
   }

   public final int clear(Object g) {
      if (g instanceof Graphics) {
         if (this._bkgrndColour != -1) {
            Graphics gfx = (Graphics)g;
            gfx.setColor(this._bkgrndColour);
            gfx.fillRect(this._clip.x, this._clip.y, this._clip.width, this._clip.height);
         }

         this._returnState = 0;
      } else {
         this._returnState = 2;
      }

      return this._returnState;
   }

   public final int render(Object g, int iNodeOffset) {
      if (g instanceof Graphics) {
         this._returnState = this.renderNative((Graphics)g, iNodeOffset);
      } else {
         this._returnState = 2;
      }

      return this._returnState;
   }

   public final int renderList(Object g, int iNodeOffset) {
      if (g instanceof Graphics) {
         this._returnState = this.renderListNative((Graphics)g, iNodeOffset);
      } else {
         this._returnState = 2;
      }

      return this._returnState;
   }

   public final int renderLeafNodes(Object g, int iNodeOffset) {
      if (this._leafNodeList == null) {
         this._returnState = 2;
      } else if (g instanceof Graphics) {
         int[] listOld = this._nodeList;
         this._nodeList = this._leafNodeList;
         if (_regOptions != null) {
            this._drawBounds = _regOptions._showBounds ? 1 : 0;
         } else {
            this._drawBounds = 0;
         }

         this._returnState = this.renderListNative((Graphics)g, iNodeOffset);
         this._nodeList = listOld;
      } else {
         this._returnState = 2;
      }

      return this._returnState;
   }

   public final int renderNode(Object g, int iNode) {
      if (g instanceof Graphics) {
         return 1;
      }

      this._returnState = 2;
      return this._returnState;
   }

   public final int update(int iNodeOffset) {
      if (this._boundsDirty == null) {
         this._boundsDirty = new XYRect();
      }

      int numLeafNodes = this.getLeafCount(this._iNodeRoot);
      if (this._leafNodeList == null || numLeafNodes > this._leafNodeList.length) {
         this._leafNodeList = new int[numLeafNodes];
      }

      return this.updateNative(iNodeOffset);
   }

   public final XYRect getDirtyBounds() {
      return this._boundsDirty;
   }

   private final native int getLeafCount(int var1);

   private final native int renderNative(Graphics var1, int var2);

   private final native int renderListNative(Graphics var1, int var2);

   private final native int updateNative(int var1);

   public final boolean setNodes(int[] nodes) {
      if (this._nodes != nodes) {
         this._nodes = nodes;
         return true;
      } else {
         return false;
      }
   }

   public final void setRootIndex(int rootIndex) {
      this._iNodeRoot = rootIndex;
   }

   public final boolean setCoords(int[][] coords) {
      if (this._coords != coords) {
         this._coords = coords;
         return true;
      } else {
         return false;
      }
   }

   public final boolean setPointTypes(byte[][] pointTypes) {
      if (this._pointTypes != pointTypes) {
         this._pointTypes = pointTypes;
         return true;
      } else {
         return false;
      }
   }

   public final boolean setImages(Object[] images) {
      if (this._images != images) {
         this._images = images;
         return true;
      } else {
         return false;
      }
   }

   public final boolean setForeignObjects(Object[] foreignObjects) {
      if (this._foreignObjects != foreignObjects) {
         this._foreignObjects = foreignObjects;
         return true;
      } else {
         return false;
      }
   }

   public final boolean setStrings(char[][] strings) {
      if (this._strings != strings) {
         this._strings = strings;
         return true;
      } else {
         return false;
      }
   }

   public final boolean setFontFamilies(String[] fontFamilies) {
      if (this._fontFamilies != fontFamilies) {
         this._fontFamilies = fontFamilies;
         return true;
      } else {
         return false;
      }
   }

   public final void setDefaultFontFamily(String defaultFontFamily) {
      this._defaultFontFamily = defaultFontFamily;
   }

   public final void setNodeList(int[] nodeList) {
      this._nodeList = nodeList;
   }

   public final void setClip(int x, int y, int width, int height) {
      this._clip.set(x, y, width, height);
   }

   public final int getLastReturn() {
      return this._returnState;
   }

   public final int getLastNode() {
      return this._lastNode;
   }

   public final int getNextListNode(int iNode) {
      if (this._nodeList == null) {
         return -1;
      }

      int numNxtNodes = this._nodeList.length - 1;
      int iNodeNxt = -1;

      for (int i = 0; i < numNxtNodes; i++) {
         if (this._nodeList[i] == iNode) {
            return this._nodeList[i + 1];
         }
      }

      return iNodeNxt;
   }

   public final int getNextLeafNode(int iNode) {
      if (this._leafNodeList == null) {
         return -1;
      }

      int numNxtNodes = this._leafNodeList.length - 1;
      int iNodeNxt = -1;

      for (int i = 0; i < numNxtNodes; i++) {
         if (this._leafNodeList[i] == iNode) {
            return this._leafNodeList[i + 1];
         }
      }

      return iNodeNxt;
   }

   public final int[] getLeafNodeList() {
      return this._leafNodeList;
   }

   public final XYRect getLastViewport() {
      return this._lastViewport;
   }

   public final XYRect getLastClip() {
      return this._lastClip;
   }

   public final void setViewport(int fX, int fY, int fWidth, int fHeight) {
      this._viewport.set(fX, fY, fWidth, fHeight);
   }

   public final void setTransform(int[] transform) {
      if (this._transform == null) {
         this._transform = new int[9];
      }

      if (this._transform != null && transform.length == 9) {
         System.arraycopy(transform, 0, this._transform, 0, 9);
      }
   }

   public final void setBackgroundColour(int rgbColour) {
      this._bkgrndColour = rgbColour;
   }

   public final int getBackgroundColour() {
      return this._bkgrndColour;
   }

   public final void setStats(int[] stats) {
      if (this._stats != stats) {
         this._stats = stats;
         this.resetStats(-1);
      }
   }

   public final int getStatsCount(int type) {
      if (this._stats != null) {
         for (int i = 0; i < this._stats.length; i += 4) {
            if (this._stats[i] == type) {
               return this._stats[i + 1];
            }
         }
      }

      return -1;
   }

   public final int getStatsDuration(int type) {
      if (this._stats != null) {
         for (int i = 0; i < this._stats.length; i += 4) {
            if (this._stats[i] == type) {
               return this._stats[i + 2];
            }
         }
      }

      return -1;
   }

   public final int getStatsAvgDuration(int type) {
      int count = this.getStatsCount(type);
      int dur = this.getStatsDuration(type);
      return count > 0 && dur > 0 ? dur / count : -1;
   }

   public static final boolean isDisabled() {
      return _regOptions == null ? false : _regOptions._disabled;
   }

   public static final boolean showClip() {
      return _regOptions == null ? false : _regOptions._showClip;
   }

   public static final boolean showBounds() {
      return _regOptions == null ? false : _regOptions._showBounds;
   }

   public static final boolean logStats() {
      return _regOptions == null ? false : _regOptions._logStats;
   }

   public static final boolean toggleRegOption(int option) {
      if (_regOptions == null) {
         return false;
      }

      switch (option) {
         case 1:
         default:
            _regOptions._disabled = !_regOptions._disabled;
            return true;
         case 2:
            _regOptions._showClip = !_regOptions._showClip;
            return true;
         case 3:
            _regOptions._showBounds = !_regOptions._showBounds;
            return true;
         case 4:
            _regOptions._logStats = !_regOptions._logStats;
         case 0:
            return true;
      }
   }

   public final void renderClip(Object g) {
      Graphics gfx = (Graphics)g;
      gfx.setColor(0);
      this._clipColour >>= 8;
      if (this._clipColour == 0) {
         this._clipColour = 16711680;
      }

      gfx.drawRect(this._clip.x + 1, this._clip.y + 1, this._clip.width - 2, this._clip.height - 2);
   }

   public final void renderStats(Object g) {
      Graphics gfx = (Graphics)g;
      int update = this.getStatsAvgDuration(14);
      int render = this.getStatsAvgDuration(15);
      String strUpdate = Integer.toString(update);
      String strRender = Integer.toString(render);

      try {
         FontFamily fontFamily = FontFamily.forName(FontFamily.FAMILY_SYSTEM);
         gfx.setFont(fontFamily.getFont(1, 10));
         gfx.drawText(strUpdate, this._clip.x, this._clip.y);
         gfx.drawText(strRender, this._clip.x, this._clip.y + 11);
      } catch (Exception var8) {
      }
   }

   public final void resetStats(int type) {
      if (this._stats != null) {
         for (int i = 0; i < this._stats.length; i += 4) {
            if (this._stats[i] == type || type < 0) {
               this._stats[i + 1] = 0;
               this._stats[i + 2] = 0;
               if (this._stats[i] == type) {
                  break;
               }
            }
         }
      }

      if (type < 0) {
         this._statsResetTime = System.currentTimeMillis();
      }
   }

   public final void startStats(int type) {
      if (this._stats != null) {
         for (int i = 0; i < this._stats.length; i += 4) {
            if (this._stats[i] == type) {
               this._stats[i + 3] = (int)(System.currentTimeMillis() - this._statsResetTime);
               return;
            }
         }
      }
   }

   public final void stopStats(int type) {
      if (this._stats != null) {
         for (int i = 0; i < this._stats.length; i += 4) {
            if (this._stats[i] == type) {
               this._stats[i + 1]++;
               this._stats[i + 2] = this._stats[i + 2] + (int)(System.currentTimeMillis() - this._statsResetTime - this._stats[i + 3]);
               return;
            }
         }
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar != null) {
         _regOptions = (PMEGraphics$PMERegistryOptions)ar.get(7638954399719443704L);
         if (_regOptions == null) {
            _regOptions = new PMEGraphics$PMERegistryOptions();
            ar.put(7638954399719443704L, _regOptions);
         }
      }
   }
}
