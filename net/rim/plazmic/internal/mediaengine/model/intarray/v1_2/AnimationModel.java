package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.MediaServices;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObjectPeer;
import net.rim.plazmic.internal.mediaengine.ui.ImageForeignObject;
import net.rim.plazmic.internal.mediaengine.util.Platform;

public class AnimationModel implements MediaModel, ForeignObjectPeer, MediaService {
   protected MediaServices _services;
   boolean _hasbkgColor;
   int _bkgColor;
   int _minAnimatedNodeIdx;
   char[][] _convertedTextStrings;
   String[] _platformFontFamilyStrings;
   String[] _fontFamilyStrings;
   String[] _metaInfo;
   String[] _imageURL;
   String[] _mediaObjectURL;
   String missingURLs;
   String[] _fontUrlStrings;
   int[] _fontHandles;
   String[] _customMessages;
   String[] _hyperlinks;
   Object[] _media;
   Object[] _customObjects;
   Object[] _images;
   int _numCustomObjects;
   int _maxUID;
   ForeignObject[] _foreignObjects;
   MediaService[] _contentServices;
   int[] _contentServiceNodeHandles;
   EventLogic _logic;
   public int[] _nodes;
   int _nextNodeIdx;
   int[] _focusableList;
   int _itemInFocus;
   int _defaultItemInFocus;
   int[][] _keyTimes;
   int[][] _keyValues;
   int[][] _omValues;
   private long[] _behaviorsHaveStarted;
   private long[] _behaviorsHaveEnded;
   int _behaviorsRoot;
   int _visualRoot;
   int[] _triggers;
   int[] _handlesWithId;
   String[] _ids;
   int[][] _coords;
   int[][] _finalCoords;
   byte[][] _pointTypes;
   byte[][] _finalPointTypes;
   boolean _wrapFocus;
   byte[] _rawData;
   String _contentType;
   String _source;
   ResourceProvider _reader;
   boolean _isZoomAndPannable;
   int _version;
   Platform _platform;
   int[][] _freeNodes = new int[16][];
   int[][] _freelists = new int[8][];
   static final boolean FOCUS_ON_INVISIBLE = true;
   public static final int INCREMENT_NODE_CAPACITY = 500;
   private static final int MIN_ARRAY_RESIZE = 50;
   private static final int NUM_FN = 16;
   private static final int FN_ELLIPSE = 0;
   private static final int FN_FILLATTR = 1;
   private static final int FN_FOREIGNOBJECT = 2;
   private static final int FN_GROUP = 3;
   private static final int FN_IMAGE = 4;
   private static final int FN_COLORATTR = 5;
   private static final int FN_PATTERNATTR = 6;
   private static final int FN_PATH = 7;
   private static final int FN_RECTANGLE = 8;
   private static final int FN_STROKEATTR = 9;
   private static final int FN_SVG = 10;
   private static final int FN_TEXTATTR = 11;
   private static final int FN_TEXT = 12;
   private static final int FN_TRANSFORMATTR = 13;
   private static final int FN_VIEWBOXATTR = 14;
   private static final int FN_TIMECONTAINER = 15;
   private static final int NUM_FL = 8;
   private static final int FL_FOREIGNOBJECTS = 0;
   private static final int FL_CUSTOMMESSAGES = 1;
   private static final int FL_IMAGES = 2;
   private static final int FL_MEDIA = 3;
   private static final int FL_CONVERTEDTEXTSTRINGS = 4;
   private static final int FL_COORDS = 5;
   private static final int FL_POINTTYPES = 6;
   private static final int FL_VN_UID = 7;

   public void init() {
      this._nextNodeIdx = 0;
      this._maxUID = 0;
      this._itemInFocus = -1;
      this._defaultItemInFocus = -1;
      this._visualRoot = -1;
      this._behaviorsRoot = -1;
      this.missingURLs = "";
      this._minAnimatedNodeIdx = Integer.MAX_VALUE;
   }

   protected int addNode(int type, int size) {
      int nodeIdx = this._nextNodeIdx;
      int fnconst = this.getFreeNodesConstant(type);
      boolean usedFree = false;
      if (fnconst >= 0 && this._freeNodes[fnconst] != null && this._freeNodes[fnconst].length > 0) {
         int length = this._freeNodes[fnconst].length;
         nodeIdx = this._freeNodes[fnconst][--length];
         this._platform.arrayResize(this._freeNodes[fnconst], length);
         usedFree = true;
      } else if (nodeIdx + size > this._nodes.length) {
         int[] nodes = new int[nodeIdx + size + 500];
         System.arraycopy(this._nodes, 0, nodes, 0, this._nodes.length);
         this._nodes = nodes;
      }

      this._nodes[nodeIdx + 1] = type;
      this._nodes[nodeIdx + 0] = size;
      if (isVisual(type)) {
         int vnUID = this.addVisualNodeUID();
         this._nodes[nodeIdx + 9] = vnUID;
      }

      if (!usedFree) {
         this._nextNodeIdx = nodeIdx + size;
      }

      return nodeIdx;
   }

   protected void deleteNode(int nodeIdx) {
      int size = this._nodes[nodeIdx + 0];
      int type = this._nodes[nodeIdx + 1];
      int fnconst = this.getFreeNodesConstant(type);
      int lastIdx;
      if (this._freeNodes[fnconst] == null) {
         lastIdx = 0;
         this._freeNodes[fnconst] = new int[1];
      } else {
         lastIdx = this._freeNodes[fnconst].length;
         this._platform.arrayResize(this._freeNodes[fnconst], lastIdx + 1);
      }

      this._freeNodes[fnconst][lastIdx] = nodeIdx;
      if (isVisual(type)) {
         int vnUID = this._nodes[nodeIdx + 9];
         this.removeVisualNodeUID(vnUID);
      }

      this._platform.fillArray(this._nodes, 0, nodeIdx, size);
   }

   public void setBackgroundColor(int color) {
      this._hasbkgColor = true;
      this._bkgColor = color;
   }

   void removeFocusableHandle(int handle) {
      if (this._focusableList != null && this._focusableList.length > 0) {
         for (int i = this._focusableList.length - 1; i >= 0; i--) {
            if (this._focusableList[i] == handle) {
               while (i < this._focusableList.length - 1) {
                  this._focusableList[i] = this._focusableList[i + 1];
                  i++;
               }

               this._platform.arrayResize(this._focusableList, this._focusableList.length - 1);
               return;
            }
         }
      } else {
         throw new Object("There are no focusable items in the model.");
      }
   }

   void addFocusableHandle(int handle) {
      if (this._focusableList == null) {
         this._focusableList = new int[0];
      } else if (this._focusableList.length > 0) {
         this.removeFocusableHandle(handle);
      }

      int length = this._focusableList.length;
      this._platform.arrayResize(this._focusableList, length + 1);
      this._focusableList[length] = handle;
   }

   byte[] getPointTypes(int index) {
      return this._pointTypes[index];
   }

   void setPointTypes(int index, byte[] pointTypes) {
      this._pointTypes[index] = pointTypes;
   }

   protected final void setBits(int node, int bits) {
      this._nodes[node + 8] = this._nodes[node + 8] | bits;
   }

   protected final void unsetBits(int node, int bits) {
      this._nodes[node + 8] = this._nodes[node + 8] & ~bits;
   }

   final boolean bitsAreSet(int node, int bits) {
      return (this._nodes[node + 8] & bits) == bits;
   }

   void removePointTypes(int index) {
      this._pointTypes[index] = null;
      if (this._freelists[6] == null) {
         this._freelists[6] = new int[1];
         this._freelists[6][0] = index;
      } else {
         int length = this._freelists[6].length;
         this._platform.arrayResize(this._freelists[6], length + 1);
         this._freelists[6][length] = index;
      }
   }

   int addPointTypes(byte[] pointTypes) {
      int index;
      if (this._pointTypes == null) {
         this._pointTypes = new byte[1][];
         index = 0;
      } else if (this._freelists[6] != null && this._freelists[6].length > 0) {
         int length = this._freelists[6].length;
         index = this._freelists[6][--length];
         this._platform.arrayResize(this._freelists[6], length);
      } else {
         index = this._pointTypes.length;
         this._platform.arrayResize(this._pointTypes, index + 1);
      }

      this._pointTypes[index] = pointTypes;
      return index;
   }

   public ForeignObject getNodeForeignObject(int node) {
      ForeignObject result = null;
      if (this._nodes[node + 1] == 44) {
         int foreignObjectIdx = this._nodes[node + 29];
         if (foreignObjectIdx >= 0) {
            result = this._foreignObjects[foreignObjectIdx];
         }
      }

      return result;
   }

   int[] getFinalCoordinates(int index) {
      return this._finalCoords[index];
   }

   int[] getCoordinates(int index) {
      return this._coords[index];
   }

   public void reset() {
      for (int i = 0; i < 16; i++) {
         this._freeNodes[i] = null;
      }

      for (int var2 = 0; var2 < 8; var2++) {
         this._freelists[var2] = null;
      }

      this._itemInFocus = -1;
      this._defaultItemInFocus = -1;
      if (this._reader != null) {
         this._reader.createResource(this._contentType, this, null, null);
      }
   }

   void setCoordinates(int index, int[] coords) {
      this._coords[index] = coords;
      int[] finalCoords = new int[coords.length];
      System.arraycopy(coords, 0, finalCoords, 0, coords.length);
      this._finalCoords[index] = finalCoords;
   }

   void removeCoordinates(int index) {
      this._coords[index] = null;
      this._finalCoords[index] = null;
      if (this._freelists[5] == null) {
         this._freelists[5] = new int[1];
         this._freelists[5][0] = index;
      } else {
         int length = this._freelists[5].length;
         this._platform.arrayResize(this._freelists[5], length + 1);
         this._freelists[5][length] = index;
      }
   }

   int getIdIndex(String nodeID) {
      if (this._ids != null) {
         for (int i = this._ids.length - 1; i >= 0; i--) {
            if (this._ids[i] != null && nodeID.compareTo(this._ids[i]) == 0) {
               return i;
            }
         }
      }

      return -1;
   }

   String getId(int nodeHandle) {
      if (this._handlesWithId != null) {
         for (int i = this._handlesWithId.length - 1; i >= 0; i--) {
            if (this._handlesWithId[i] == nodeHandle) {
               return this._ids[i];
            }
         }
      }

      return null;
   }

   void setId(int nodeHandle, String id) {
      if (this._handlesWithId == null) {
         this._handlesWithId = new int[]{nodeHandle};
         this._ids = new Object[]{id};
      } else {
         int i;
         for (i = this._handlesWithId.length - 1; i >= 0; i--) {
            if (this._handlesWithId[i] == nodeHandle) {
               this._ids[i] = id;
               break;
            }
         }

         if (i < 0) {
            int length = this._handlesWithId.length;
            this._platform.arrayResize(this._handlesWithId, length + 1);
            this._platform.arrayResize(this._ids, length + 1);
            this._handlesWithId[length] = nodeHandle;
            this._ids[length] = id;
         }
      }
   }

   int getFontFamilyIdx(String fontFamily) {
      if (this._platformFontFamilyStrings == null) {
         return -1;
      }

      for (int i = 0; i < this._platformFontFamilyStrings.length; i++) {
         if (this._platformFontFamilyStrings[i].equals(fontFamily)) {
            return i;
         }
      }

      return -1;
   }

   int addFontFamily(String fontFamily) {
      if (this._platformFontFamilyStrings == null) {
         this._platformFontFamilyStrings = new Object[]{fontFamily};
         this._fontFamilyStrings = new Object[]{fontFamily};
         return 0;
      } else {
         int length = this._platformFontFamilyStrings.length;
         this._platform.arrayResize(this._platformFontFamilyStrings, length + 1);
         this._platform.arrayResize(this._fontFamilyStrings, length + 1);
         this._platformFontFamilyStrings[length] = fontFamily;
         this._fontFamilyStrings[length] = fontFamily;
         return length;
      }
   }

   int addCoordinates(int[] coords) {
      int index;
      if (this._coords == null) {
         this._coords = new int[1][];
         this._finalCoords = new int[1][];
         index = 0;
      } else if (this._freelists[5] != null && this._freelists[5].length > 0) {
         int length = this._freelists[5].length;
         index = this._freelists[5][--length];
         this._platform.arrayResize(this._freelists[5], length);
      } else {
         index = this._coords.length;
         this._platform.arrayResize(this._coords, index + 1);
         this._platform.arrayResize(this._finalCoords, index + 1);
      }

      this._coords[index] = coords;
      int[] finalCoords = new int[coords.length];
      System.arraycopy(coords, 0, finalCoords, 0, coords.length);
      this._finalCoords[index] = finalCoords;
      return index;
   }

   char[] getConvertedTextString(int index) {
      return this._convertedTextStrings[index];
   }

   void setConvertedTextString(int index, char[] string) {
      if (this._convertedTextStrings != null) {
         synchronized (this._convertedTextStrings) {
            this._convertedTextStrings[index] = string;
         }
      }
   }

   void removeConvertedTextString(int index) {
      this.setConvertedTextString(index, null);
      if (this._freelists[4] == null) {
         this._freelists[4] = new int[1];
         this._freelists[4][0] = index;
      } else {
         int length = this._freelists[4].length;
         this._platform.arrayResize(this._freelists[4], length + 1);
         this._freelists[4][length] = index;
      }
   }

   int addConvertedTextString(char[] string) {
      int index;
      if (this._convertedTextStrings == null) {
         this._convertedTextStrings = new char[1][];
         index = 0;
      } else if (this._freelists[4] != null && this._freelists[4].length > 0) {
         int length = this._freelists[4].length;
         index = this._freelists[4][--length];
         this._platform.arrayResize(this._freelists[4], length);
      } else {
         index = this._convertedTextStrings.length;
         this._platform.arrayResize(this._convertedTextStrings, index + 1);
      }

      this.setConvertedTextString(index, string);
      return index;
   }

   public void setBehaviorHasStarted(int bhIdx, long startTime) {
      int enumeration = this._nodes[bhIdx + 16];
      this._behaviorsHaveStarted[enumeration] = startTime;
   }

   public void setBehaviorHasEnded(int bhIdx, long endTime) {
      int enumeration = this._nodes[bhIdx + 16];
      this._behaviorsHaveEnded[enumeration] = endTime;
   }

   public boolean behaviorHasStarted(int bhIdx, long time) {
      int enumeration = this._nodes[bhIdx + 16];
      return this._behaviorsHaveStarted[enumeration] == time;
   }

   public boolean behaviorHasEnded(int bhIdx, long time) {
      int enumeration = this._nodes[bhIdx + 16];
      return this._behaviorsHaveEnded[enumeration] == time;
   }

   public void resetBehaviorArrays() {
      for (int i = 0; i < this._behaviorsHaveStarted.length; i++) {
         this.resetBehavior(i);
      }
   }

   public void initBehaviorArrays(int len) {
      if (this._behaviorsHaveStarted == null || this._behaviorsHaveStarted.length != len) {
         this._behaviorsHaveStarted = new long[len];
         this._behaviorsHaveEnded = new long[len];
      }

      this.resetBehaviorArrays();
   }

   int resolveAttributeOffset(int vnIndex, int attributeType) {
      switch (attributeType) {
         case -1:
         case 12:
         case 21:
            return -1;
         case 0:
         default:
            return vnIndex + 13;
         case 1:
            return vnIndex + 8;
         case 2:
            int fillIdx = this._nodes[vnIndex + 21];
            if (fillIdx == -1) {
               return -1;
            }

            return fillIdx + 5;
         case 3:
            int fillIdx = this._nodes[vnIndex + 21];
            int fillPaintIdx = fillIdx == -1 ? -1 : this._nodes[fillIdx + 4];
            if (fillPaintIdx == -1) {
               return -1;
            }

            return fillPaintIdx + 3;
         case 4:
            int var13 = this._nodes[vnIndex + 22];
            if (var13 == -1) {
               return -1;
            }

            return var13 + 5;
         case 5:
            int strokeIdx = this._nodes[vnIndex + 22];
            int strokePaintIdx = strokeIdx == -1 ? -1 : this._nodes[strokeIdx + 4];
            if (strokePaintIdx == -1) {
               return -1;
            }

            return strokePaintIdx + 3;
         case 6:
            return vnIndex + 10;
         case 7:
            return vnIndex + 11;
         case 8:
            return vnIndex + 23;
         case 9:
            return vnIndex + 24;
         case 10:
            return vnIndex + 23;
         case 11:
            return vnIndex + 24;
         case 13:
            return vnIndex + 24;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 23:
         case 24:
            return this._nodes[vnIndex + 12] + 3;
         case 25:
         case 27:
            return vnIndex + 5;
         case 26:
         case 28:
            return vnIndex + 6;
         case 29:
            int vbIdx = this._nodes[vnIndex + 28];
            if (vbIdx == -1) {
               return -1;
            }

            return vbIdx + 3;
         case 30:
            int var11 = this._nodes[vnIndex + 22];
            if (var11 == -1) {
               return -1;
            }

            return var11 + 6;
         case 31:
         case 32:
         case 33:
            return vnIndex + 14;
         case 34:
            return vnIndex + 27;
         case 35:
         case 36:
            int strokeIdx = this._nodes[vnIndex + 22];
            if (strokeIdx == -1) {
               return -1;
            }

            return strokeIdx + 7;
         case 37:
            int nodeType = this._nodes[vnIndex + 1];
            int txtAttrIdx = nodeType == 46 ? this._nodes[vnIndex + 29] : this._nodes[vnIndex + 23];
            if (txtAttrIdx == -1) {
               return -1;
            }

            return txtAttrIdx + 7;
         case 38:
            int var20 = this._nodes[vnIndex + 1];
            int var16 = var20 == 46 ? this._nodes[vnIndex + 29] : this._nodes[vnIndex + 23];
            if (var16 == -1) {
               return -1;
            }

            return var16 + 4;
         case 39:
            int var19 = this._nodes[vnIndex + 1];
            int var15 = var19 == 46 ? this._nodes[vnIndex + 29] : this._nodes[vnIndex + 23];
            if (var15 == -1) {
               return -1;
            }

            return var15 + 6;
         case 40:
            int var18 = this._nodes[vnIndex + 1];
            int var14 = var18 == 46 ? this._nodes[vnIndex + 29] : this._nodes[vnIndex + 23];
            if (var14 == -1) {
               return -1;
            }

            return var14 + 5;
         case 41:
            int nodeType = this._nodes[vnIndex + 1];
            int txtAttrIdx = nodeType == 46 ? this._nodes[vnIndex + 29] : this._nodes[vnIndex + 23];
            if (txtAttrIdx == -1) {
               return -1;
            }

            return txtAttrIdx + 8;
         case 42:
            return vnIndex + 8;
      }
   }

   int resolveAttributeSize(int attributeType) {
      switch (attributeType) {
         case 13:
         case 21:
         case 25:
         case 26:
         case 27:
         case 28:
            return 1;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 23:
         case 24:
            return 9;
         case 29:
         default:
            return 4;
      }
   }

   int addForeignObject(ForeignObject object) {
      int index;
      if (this._foreignObjects == null) {
         this._foreignObjects = new Object[1];
         index = 0;
      } else if (this._freelists[0] != null && this._freelists[0].length > 0) {
         int length = this._freelists[0].length;
         index = this._freelists[0][--length];
         this._platform.arrayResize(this._freelists[0], length);
      } else {
         index = this._foreignObjects.length;
         this._platform.arrayResize(this._foreignObjects, index + 1);
      }

      this._foreignObjects[index] = object;
      return index;
   }

   void removeForeignObject(int index) {
      this._foreignObjects[index] = null;
      if (this._freelists[0] == null) {
         this._freelists[0] = new int[1];
         this._freelists[0][0] = index;
      } else {
         int length = this._freelists[0].length;
         this._platform.arrayResize(this._freelists[0], length + 1);
         this._freelists[0][length] = index;
      }
   }

   void setForeignObject(int index, ForeignObject object) {
      this._foreignObjects[index] = object;
   }

   ForeignObject getForeignObject(int index) {
      return this._foreignObjects[index];
   }

   int addCustomMessage(String message) {
      int index;
      if (this._customMessages == null) {
         this._customMessages = new Object[1];
         index = 0;
      } else if (this._freelists[1] != null && this._freelists[1].length > 0) {
         int length = this._freelists[1].length;
         index = this._freelists[1][--length];
         this._platform.arrayResize(this._freelists[1], length);
      } else {
         index = this._customMessages.length;
         this._platform.arrayResize(this._customMessages, index + 1);
      }

      this._customMessages[index] = message;
      return index;
   }

   void removeCustomMessage(int index) {
      this._customMessages[index] = null;
      if (this._freelists[1] == null) {
         this._freelists[1] = new int[1];
         this._freelists[1][0] = index;
      } else {
         int length = this._freelists[1].length;
         this._platform.arrayResize(this._freelists[1], length + 1);
         this._freelists[1][length] = index;
      }
   }

   void setCustomMessage(int index, String message) {
      this._customMessages[index] = message;
   }

   String getCustomMessage(int index) {
      return this._customMessages[index];
   }

   int addImage(Object image) {
      int index;
      if (this._images == null) {
         this._images = new Object[1];
         index = 0;
      } else if (this._freelists[2] != null && this._freelists[2].length > 0) {
         int length = this._freelists[2].length;
         index = this._freelists[2][--length];
         this._platform.arrayResize(this._freelists[2], length);
      } else {
         index = this._images.length;
         this._platform.arrayResize(this._images, index + 1);
      }

      this._images[index] = image;
      return index;
   }

   void removeImage(int index) {
      this._images[index] = null;
      if (this._freelists[2] == null) {
         this._freelists[2] = new int[1];
         this._freelists[2][0] = index;
      } else {
         int length = this._freelists[2].length;
         this._platform.arrayResize(this._freelists[2], length + 1);
         this._freelists[2][length] = index;
      }
   }

   void setImage(int index, Object image) {
      this._images[index] = image;
   }

   Object getImage(int index) {
      return this._images[index];
   }

   int addMedia(Object media) {
      int index;
      if (this._media == null) {
         this._media = new Object[1];
         index = 0;
      } else if (this._freelists[3] != null && this._freelists[3].length > 0) {
         int length = this._freelists[3].length;
         index = this._freelists[3][--length];
         this._platform.arrayResize(this._freelists[3], length);
      } else {
         index = this._media.length;
         this._platform.arrayResize(this._media, index + 1);
      }

      this._media[index] = media;
      return index;
   }

   void removeMedia(int index) {
      this._media[index] = null;
      if (this._freelists[3] == null) {
         this._freelists[3] = new int[1];
         this._freelists[3][0] = index;
      } else {
         int length = this._freelists[3].length;
         this._platform.arrayResize(this._freelists[3], length + 1);
         this._freelists[3][length] = index;
      }
   }

   void setMedia(int index, Object media) {
      this._media[index] = media;
   }

   Object getMedia(int index) {
      return this._media[index];
   }

   @Override
   public void invalidate(ForeignObject fo) {
      int index = fo.getHandle();
      this.setBits(index, -16777216);
      this._nodes[index + 16]++;
   }

   @Override
   public String getMetaInfo(String key) {
      if (this._metaInfo != null) {
         int num = this._metaInfo.length >> 1;

         for (int x = 0; x < num; x += 2) {
            if (this._metaInfo[x].equals(key)) {
               return this._metaInfo[x + 1];
            }
         }
      }

      return null;
   }

   @Override
   public String getMetaValue(int index) {
      return this._metaInfo != null && index <= this._metaInfo.length >> 1 ? this._metaInfo[(index << 1) + 1] : null;
   }

   @Override
   public String getMetaKey(int index) {
      return this._metaInfo != null && index <= this._metaInfo.length >> 1 ? this._metaInfo[index << 1] : null;
   }

   @Override
   public int getNumMetaInfo() {
      return this._metaInfo != null ? this._metaInfo.length >> 1 : 0;
   }

   @Override
   public Class getMediaClass() {
      return this.getClass();
   }

   @Override
   public Object getResource(String resourceId) {
      if (this._imageURL != null) {
         for (int count = this._imageURL.length - 1; count >= 0; count--) {
            if (resourceId.equals(this._imageURL[count])) {
               return this._images[count];
            }
         }
      }

      if (this._mediaObjectURL != null) {
         for (int count = this._mediaObjectURL.length - 1; count >= 0; count--) {
            if (resourceId.equals(this._mediaObjectURL[count])) {
               return this._media[count];
            }
         }
      }

      int nodeIdx = this.getIdIndex(resourceId);
      if (nodeIdx != -1) {
         nodeIdx = this._handlesWithId[nodeIdx];
         switch (this._nodes[nodeIdx + 1]) {
            case 44:
               int resourceIndex = this._nodes[nodeIdx + 29];
               if (resourceIndex != -1) {
                  return this._foreignObjects[resourceIndex];
               }
               break;
            case 96:
               int resourceIndexx = this._nodes[nodeIdx + 9];
               if (resourceIndexx != -1) {
                  return this._customObjects[resourceIndexx];
               }
         }
      }

      return null;
   }

   @Override
   public String getMissingExtURLs() {
      return this.missingURLs;
   }

   @Override
   public String[] getExternalResources(int category) {
      int size = this.getExternalResourcesCount(category);
      String[] result = new Object[size];
      if (size > 0) {
         int offset = 0;
         if ((category & 4) != 0 && this._mediaObjectURL != null) {
            System.arraycopy(this._mediaObjectURL, 0, result, offset, this._mediaObjectURL.length);
            offset += this._mediaObjectURL.length;
         }

         if ((category & 2) != 0 && this._imageURL != null) {
            System.arraycopy(this._imageURL, 0, result, offset, this._imageURL.length);
            offset += this._imageURL.length;
         }

         if ((category & 1) != 0 && this._hyperlinks != null) {
            System.arraycopy(this._hyperlinks, 0, result, offset, this._hyperlinks.length);
            offset += this._hyperlinks.length;
         }

         if ((category & 32) != 0 && this._fontUrlStrings != null) {
            System.arraycopy(this._fontUrlStrings, 0, result, offset, this._fontUrlStrings.length);
            offset += this._fontUrlStrings.length;
         }
      }

      return result;
   }

   @Override
   public ForeignObject[] getForeignObjects() {
      return this._foreignObjects;
   }

   @Override
   public int getExternalResourcesCount(int category) {
      int count = 0;
      if ((category & 4) != 0 && this._mediaObjectURL != null) {
         count += this._mediaObjectURL.length;
      }

      if ((category & 2) != 0 && this._imageURL != null) {
         count += this._imageURL.length;
      }

      if ((category & 32) != 0 && this._fontUrlStrings != null) {
         count += this._fontUrlStrings.length;
      }

      if ((category & 1) != 0 && this._hyperlinks != null) {
         count += this._hyperlinks.length;
      }

      if ((category & 16) != 0 && this._foreignObjects != null) {
         count += this._foreignObjects.length;
      }

      if ((category & 8) != 0) {
         count += this._numCustomObjects;
      }

      return count;
   }

   @Override
   public String getContentType() {
      return this._contentType;
   }

   @Override
   public void setSource(String s) {
      this._source = s;
   }

   @Override
   public String getSource() {
      return this._source;
   }

   @Override
   public synchronized void disposeModel() {
      if (this._media != null) {
         for (int i = 0; i < this._media.length; i++) {
            this._platform.disposeMedia(this._media[i]);
         }
      }

      if (this._images != null) {
         for (int i = 0; i < this._images.length; i++) {
            Object var10000 = this._images[i];
            if (this._images[i] instanceof ImageForeignObject) {
               ((ImageForeignObject)var10000).dispose();
            }
         }
      }

      if (this._fontHandles != null) {
         for (int i = this._fontHandles.length - 1; i >= 0; i--) {
            this._platform.unloadFont(this._nodes[this._fontHandles[i] + 5]);
         }
      }
   }

   @Override
   public void setServices(MediaServices s) {
      if (s != this._services) {
         this._services = s;
         if (s != null && this._contentServices != null) {
            for (int i = 0; i < this._contentServices.length; i++) {
               s.registerService(s.getUniqueId(), this._contentServices[i]);
            }

            this._contentServices = null;
            this._contentServiceNodeHandles = null;
         }
      }
   }

   @Override
   public void dispose() {
      try {
         this.disposeModel();
      } finally {
         return;
      }
   }

   @Override
   public Object getMedia() {
      return this;
   }

   @Override
   public void setMedia(Object media) {
   }

   @Override
   public int getVersionNumber() {
      return this._version;
   }

   private void removeVisualNodeUID(int vnUID) {
      int lastIdx;
      if (this._freelists[7] == null) {
         lastIdx = 0;
         this._freelists[7] = new int[1];
      } else {
         lastIdx = this._freelists[7].length;
         this._platform.arrayResize(this._freelists[7], lastIdx + 1);
      }

      this._freelists[7][lastIdx] = vnUID;
   }

   private int addVisualNodeUID() {
      int length;
      if (this._freelists[7] != null && (length = this._freelists[7].length) != 0) {
         int vnUID = this._freelists[7][--length];
         this._platform.arrayResize(this._freelists[7], length);
         return vnUID;
      } else {
         return this._maxUID++;
      }
   }

   private int getFreeNodesConstant(int type) {
      int fnconstant = -1;
      switch (type) {
         case 7:
            fnconstant = 15;
         default:
            return fnconstant;
         case 34:
            return 0;
         case 36:
            return 8;
         case 40:
            return 7;
         case 42:
            return 4;
         case 44:
            return 2;
         case 46:
            return 10;
         case 48:
            return 3;
         case 50:
            return 12;
         case 64:
            return 13;
         case 66:
            return 14;
         case 68:
            return 11;
         case 70:
            return 1;
         case 72:
            return 9;
         case 74:
            return 5;
         case 76:
            return 6;
      }
   }

   static boolean isData(int type) {
      return type >= 96 && type < 127;
   }

   static boolean isBehavior(int type) {
      return type >= 1 && type < 31;
   }

   public static boolean isVisual(int type) {
      return type >= 32 && type < 63;
   }

   private void resetBehavior(int enumeration) {
      this._behaviorsHaveStarted[enumeration] = this._behaviorsHaveEnded[enumeration] = Integer.MIN_VALUE;
   }

   public AnimationModel() {
      this.init();
      this._platform = MediaFactory.getPlatform();
   }
}
