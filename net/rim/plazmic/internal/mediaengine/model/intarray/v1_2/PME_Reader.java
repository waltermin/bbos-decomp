package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.internal.mediaengine.format.v1_2.PMEInputStream;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.util.MediaQueue;
import net.rim.plazmic.internal.mediaengine.service.MediaService;
import net.rim.plazmic.internal.mediaengine.ui.ForeignObject;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.mediaengine.MediaException;

public class PME_Reader implements ResourceProvider {
   protected ModelInteractorImpl _model;
   protected PMEInputStream _in;
   protected ResourceContext _context;
   protected Platform _platform = MediaFactory.getPlatform();
   protected ResourceProvider _resourceProvider;
   private int _streamNodeArraySize;
   private int[] _nodeRefToUpdate;
   private int _nodeRefToUpdateSize;
   private int _streamNextNodeIdx;
   private int[] _streamNodeIndices;
   private int[] _runtimeNodeIndices;
   private int _nodeMapSize;
   private int[] _tempMatrix;
   private int _coordsIndex;
   private int _pointTypesIndex;
   private int _foreignObjectIndex;
   private int _customObjectIndex;
   private int _imageIndex;
   private int _imageIndexOffset;
   private int _mediaIndex;
   private int _messageIndex;
   private int _hyperlinkIndex;
   private int _triggerIndex;
   private int _idIndex;
   private int _focusIndex;
   private int _keyTimeIndex;
   private int _keyValueIndex;
   private int _tempCoordsIndex;
   private int _tempPointTypesIndex;
   private int _tempTextStringsIdx;
   private int _visualRootIdx;
   private int _behaviorsRootIdx;
   private String _encoding;
   private int _numTextElements;
   private int _convertedTextStringsOffset;
   private int _fontFamilyStringsOffset;
   private int _fontHandlesOffset;
   private char[][] _tempTextStrings;
   private int[] _textIndex;
   private int _fontFamilyIndex;
   private int _fontUrlIndex;
   private int _fontIndex;
   private int[] _tempImageNodeIdx;
   private int[][] _tempCoords;
   private byte[][] _tempPointTypes;
   private int _numInterpolators;
   private int _numBehaviors;
   private int _focusOrderIndex;
   private int[] _focusOrderUnreferenced;
   private IntIntHashtable _focusOrderHashtable;
   private int _focusOrderTarget;
   private int[][] _tempKeyTimes;
   private int[][] _tempKeyValues;
   private MediaQueue _durations;
   private MediaQueue _interpolators;
   private boolean _loadAborted;
   private boolean _options;
   private int _scaleShift;
   private int _offsetX;
   private int _offsetY;
   private int _numFractionalBits;
   private int _eventTypeMask;
   private int _eventTargetIndexMask;
   private int _triggerTimeMask;
   private int _tspanOffsetMask;
   private int _triggersDataSizeMask;
   private int _dataSizeMask;
   private int _nodeDataSizeMask;
   private int _keyTimesDataSizeMask;
   private int _keyValuesDataSizeMask;
   private int _numFocusableMask;
   private int _numIdsMask;
   private int _visualNodeIndexMask;
   private int _nodeIndexMask;
   private int _objIndexMask;
   private int _coordIndexMask;
   private int _varDataMask;
   private int _loopCountMask;
   private int _widthHeightMask;
   private int _keyTimeIndexMask;
   private int _keyValIndexMask;
   private int _attrIndexMask;
   private int _keyValMask;
   private int _coordValMask;
   private int _curChildMask;
   private int _vNodeXCoordMask;
   private int _vNodeYCoordMask;
   private int _durationMask;
   private int _numNodesOfTypeMask;
   private int _numNETOfTypeMask;
   private static final int INCREMENT_CAPACITY = 10;
   private static final int INCREMENT_TEXT_ELEMENT_CAPACITY = 5;
   private static final int NODE = 0;
   private static final int TRIGGER = 1;
   private static final int KEY_VALUES = 2;

   protected void readFontNode(int nodeIdx) {
      this._model._fontHandles[this._fontIndex++] = nodeIdx;
      int bits = this.readBits();
      if ((bits & 2) != 0) {
         this._model._fontUrlStrings[this._fontUrlIndex] = this._in.readString(this._encoding);
         this._model._nodes[nodeIdx + 3] = this._fontUrlIndex++;
      } else {
         this._model._nodes[nodeIdx + 3] = this._in.readData(this._objIndexMask);
      }

      if ((bits & 4) != 0) {
         this._model._fontFamilyStrings[this._fontFamilyIndex] = this._in.readString(this._encoding);
         this._model._nodes[nodeIdx + 4] = this._fontFamilyIndex++;
      } else {
         this._model._nodes[nodeIdx + 4] = this._in.readData(this._objIndexMask);
      }

      this._model._nodes[nodeIdx + 5] = -1;
   }

   protected void readTreeData(int nodeIdx, int bits) {
      this._model._nodes[nodeIdx + 3] = -1;
      this._model._nodes[nodeIdx + 7] = -1;
      this._model._nodes[nodeIdx + 5] = -1;
      if ((bits & 2) != 0) {
         this.setNodeReference(0, nodeIdx + 4, this._in.readData(this._nodeIndexMask));
      } else {
         this._model._nodes[nodeIdx + 4] = -1;
      }

      if ((bits & 8) != 0) {
         this.setNodeReference(0, nodeIdx + 6, this._in.readData(this._nodeIndexMask));
      } else {
         this._model._nodes[nodeIdx + 6] = -1;
      }
   }

   public void readIntoModel(
      Object m,
      ModelInteractorImpl$RootsHandles roots,
      byte[] data,
      int offset,
      ResourceContext context,
      ResourceProvider provider,
      int bitShift,
      int offsetX,
      int offsetY,
      int maxValue
   ) {
      this._scaleShift = bitShift;
      this._offsetX = offsetX;
      this._offsetY = offsetY;
      this._numFractionalBits = 16;
      if (maxValue > 32768) {
         int numWholeBits = 16;
         int val = 65536;

         while (maxValue > val) {
            numWholeBits++;
            if ((val *= 2) < 0) {
               break;
            }
         }

         this._numFractionalBits = 31 - numWholeBits;
      }

      if (this._scaleShift == 0 && this._offsetX == 0 && this._offsetY == 0 && this._numFractionalBits == 16) {
         this._options = false;
      } else {
         this._options = true;
      }

      this._resourceProvider = provider;
      this._in.set(data, offset, true);
      this._model = (ModelInteractorImpl)m;
      this._context = context;
      if (this._context == null) {
         this._context = ResourceContext.createContext();
      }

      if (this._tempMatrix == null) {
         this._tempMatrix = new int[9];
      }

      this.initTempVars(false);
      if (this.readVersion() != this._model._version) {
         throw new Object();
      }

      int type;
      while (this._in.available() > 0 && (type = this._in.readData(0)) != 255) {
         this.readSection(type, true, false);
      }

      this.loadResources();
      roots.setVisualRootHandle(this._visualRootIdx);
      roots.setBehaviorsRootHandle(this._behaviorsRootIdx);
      this._resourceProvider = null;
   }

   protected void set(Object m, byte[] data, int offset, ResourceContext context) {
      this._in.set(data, offset, true);
      this._model = (ModelInteractorImpl)m;
      this._context = context;
      if (this._context != null) {
         this._context.set("Media", this._model);
      }

      if (this._model != null) {
         this._model.init();
         this._model._rawData = data;
         this._model._reader = this;
         this._tempMatrix = new int[9];
      }

      this.initTempVars(true);
   }

   protected void initTempVars(boolean newModel) {
      if (newModel) {
         this._imageIndex = 0;
         this._imageIndexOffset = 0;
         this._mediaIndex = 0;
         this._customObjectIndex = 0;
         this._coordsIndex = 0;
         this._pointTypesIndex = 0;
         this._idIndex = 0;
         this._convertedTextStringsOffset = 0;
         this._fontFamilyStringsOffset = 0;
         this._fontFamilyIndex = 0;
         this._fontHandlesOffset = 0;
      } else {
         this._imageIndexOffset = this._model._images == null ? 0 : this._model._images.length;
         this._imageIndex = this._imageIndexOffset;
         this._mediaIndex = this._model._media == null ? 0 : this._model._media.length;
         this._customObjectIndex = this._model._customObjects == null ? 0 : this._model._customObjects.length;
         this._coordsIndex = this._model._coords == null ? 0 : this._model._coords.length;
         this._pointTypesIndex = this._model._pointTypes == null ? 0 : this._model._pointTypes.length;
         this._idIndex = this._model._ids == null ? 0 : this._model._ids.length;
         this._convertedTextStringsOffset = this._model._convertedTextStrings == null ? 0 : this._model._convertedTextStrings.length;
         this._fontFamilyStringsOffset = this._model._fontFamilyStrings == null ? 0 : this._model._fontFamilyStrings.length;
         this._fontFamilyIndex = this._fontFamilyStringsOffset;
         this._fontHandlesOffset = this._model._fontHandles == null ? 0 : this._model._fontHandles.length;
      }

      this._fontUrlIndex = 0;
      this._fontIndex = 0;
      this._tempTextStringsIdx = 0;
      this._numTextElements = 0;
      this._foreignObjectIndex = 0;
      this._streamNodeArraySize = 0;
      this._nodeRefToUpdateSize = 0;
      this._streamNextNodeIdx = 0;
      this._nodeMapSize = 0;
      this._messageIndex = 0;
      this._triggerIndex = 0;
      this._focusIndex = 0;
      this._focusOrderIndex = 0;
      this._keyTimeIndex = 0;
      this._keyValueIndex = 0;
      this._hyperlinkIndex = 0;
      this._tempCoordsIndex = 0;
      this._tempPointTypesIndex = 0;
      this._visualRootIdx = -1;
      this._behaviorsRootIdx = -1;
      this._loadAborted = false;
      this._nodeRefToUpdate = this._streamNodeIndices = this._runtimeNodeIndices = null;
      this._tempCoords = (int[][])null;
      this._tempPointTypes = (byte[][])null;
      this._tempKeyTimes = (int[][])null;
      this._tempKeyValues = (int[][])null;
   }

   protected void readSection(int type, boolean full, boolean newModel) {
      switch (type) {
         case 1:
            int size = this._in.readData(this._dataSizeMask);

            for (int i = 0; i < size; i++) {
               this._in.readData(4);
            }

            return;
         case 2:
         default:
            this.readMetaInformation(newModel);
            return;
         case 3:
            this.readNarrowingBits();
            return;
         case 4:
            this.readSceneInfo(newModel);
            return;
         case 5:
            this.readDataSizes(full, newModel);
            return;
         case 6:
            if (newModel) {
               this._in.validateChecksum();
               return;
            }

            this._in.skipChecksum();
            return;
         case 7:
            this.readNodesArray(newModel);
            this.readFontFamilies();
            this.setupInterpolators();
            this.handleTextElements();
      }
   }

   protected void readHeader() {
      this._model._version = this.readVersion();
   }

   protected int readVersion() {
      int version = -1;
      int headerEnding = -1;

      try {
         if (this._in.readInt() != -749712059) {
            throw new Object(3);
         } else {
            version = this._in.readInt();
            if (version >>> 8 > 66048) {
               throw new Object(1, ((StringBuffer)(new Object())).append((version & 0xFF0000) >>> 16).append(".").append(version >>> 8).toString());
            } else if ((version & 0xFF0000) >>> 16 < 2) {
               throw new Object(2, ((StringBuffer)(new Object())).append((version & 0xFF0000) >>> 16).append(".").append(version >>> 8).toString());
            } else {
               headerEnding = this._in.readInt();
               if (headerEnding != 218767370) {
                  throw new Object(3);
               } else {
                  return version;
               }
            }
         }
      } finally {
         throw new Object(4);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected void readMetaInformation(boolean newModel) {
      try {
         this._encoding = this._in.readUTF();
      } catch (Throwable var5) {
         this._platform.logDebug(this, 22, -1, e);
         throw e;
      }

      int num = this._in.readData(0) << 1;
      if (num > 0) {
         if (newModel) {
            this._model._metaInfo = new Object[num];

            for (int x = 0; x < num; x++) {
               this._model._metaInfo[x] = this._in.readString(this._encoding);
            }
         } else {
            for (int x = 0; x < num; x++) {
               this._in.skipString();
            }
         }
      }
   }

   protected void readNarrowingBits() {
      int narrowingBits = this._in.readData(3);
      this._numNETOfTypeMask = narrowingBits >>> 26 & 3;
      this._numNodesOfTypeMask = narrowingBits >>> 24 & 3;
      this._visualNodeIndexMask = narrowingBits >>> 22 & 3;
      this._durationMask = narrowingBits >>> 20 & 3;
      this._eventTypeMask = narrowingBits >>> 18 & 3;
      this._eventTargetIndexMask = narrowingBits >>> 16 & 3;
      this._nodeIndexMask = narrowingBits >>> 14 & 3;
      this._tspanOffsetMask = narrowingBits >>> 12 & 3;
      this._triggersDataSizeMask = narrowingBits >>> 10 & 3;
      this._dataSizeMask = narrowingBits >>> 8 & 3;
      this._nodeDataSizeMask = narrowingBits >>> 6 & 3;
      this._keyTimesDataSizeMask = narrowingBits >>> 4 & 3;
      this._keyValuesDataSizeMask = narrowingBits >>> 2 & 3;
      this._numFocusableMask = narrowingBits & 3;
      narrowingBits = this._in.readData(3);
      this._numIdsMask = narrowingBits >>> 30 & 3;
      this._triggerTimeMask = narrowingBits >>> 27 & 7;
      this._objIndexMask = narrowingBits >>> 25 & 3;
      this._coordIndexMask = narrowingBits >>> 24 & 1;
      this._varDataMask = narrowingBits >>> 23 & 1;
      this._loopCountMask = narrowingBits >>> 22 & 1;
      this._widthHeightMask = narrowingBits >>> 20 & 3;
      this._keyTimeIndexMask = narrowingBits >>> 18 & 3;
      this._keyValIndexMask = narrowingBits >>> 16 & 3;
      this._attrIndexMask = narrowingBits >>> 14 & 3;
      this._keyValMask = narrowingBits >>> 11 & 7;
      this._coordValMask = narrowingBits >>> 8 & 7;
      this._curChildMask = narrowingBits >>> 6 & 3;
      this._vNodeYCoordMask = narrowingBits >>> 3 & 7;
      this._vNodeXCoordMask = narrowingBits & 7;
   }

   protected void readSceneInfo(boolean newModel) {
      if (newModel) {
         int hintBits = this._in.readData(0);
         this._model._wrapFocus = (hintBits & 1) != 0;
         this._model._hasbkgColor = (hintBits & 2) != 0 || (hintBits & 16) != 0;
         this._model._bkgColor = this._platform.getColor(this._in.readData(0) & 0xFF, this._in.readData(0) & 0xFF, this._in.readData(0) & 0xFF);
      } else {
         this._in.skipData(0);
         this._in.skipData(0);
         this._in.skipData(0);
         this._in.skipData(0);
      }
   }

   protected void readDataSizes(boolean initArrays, boolean newModel) {
      this._streamNodeArraySize = this._in.readData(this._nodeDataSizeMask);
      int modelNodeArraySize = 0;
      if (newModel) {
         for (int numNodeTypes = this._in.readData(0); numNodeTypes > 0; numNodeTypes--) {
            int nodeType = this._in.readData(0);
            int nodeCount = this._in.readData(this._numNodesOfTypeMask);
            modelNodeArraySize += this.getSize(nodeType) * nodeCount;
         }

         for (int numNETTypes = this._in.readData(0); numNETTypes > 0; numNETTypes--) {
            int nodeType = this._in.readData(0);
            int nodeCount = this._in.readData(this._numNETOfTypeMask);
            modelNodeArraySize += this.getSize(nodeType) * nodeCount;
         }

         int keyTimesSize = this._in.readData(this._keyTimesDataSizeMask);
         int keyValuesSize = this._in.readData(this._keyValuesDataSizeMask);
         this._tempKeyTimes = new int[keyTimesSize][];
         this._tempKeyValues = new int[keyValuesSize][];
      } else {
         for (int num = this._in.readData(0); num > 0; num--) {
            this._in.skipData(0);
            this._in.skipData(this._numNodesOfTypeMask);
         }

         for (int var21 = this._in.readData(0); var21 > 0; var21--) {
            this._in.skipData(0);
            this._in.skipData(this._numNETOfTypeMask);
         }

         this._in.skipData(this._keyTimesDataSizeMask);
         this._in.skipData(this._keyValuesDataSizeMask);
      }

      int triggersSize = this._in.readData(this._triggersDataSizeMask);
      int numDataIds = this._in.readData(this._numIdsMask);
      int numArrays = this._in.readData(this._coordIndexMask);
      if (numArrays > 0) {
         this._tempCoords = new int[numArrays][];
      }

      int numPt = this._in.readData(this._coordIndexMask);
      if (numPt > 0) {
         this._tempPointTypes = new byte[numPt][];
      }

      int numCoordsArrays = numArrays > 0 ? this._in.readData(this._coordIndexMask) : 0;
      int numPointTypes = numPt > 0 ? this._in.readData(this._coordIndexMask) : 0;
      int numImages = this._in.readData(this._objIndexMask);
      int numMedia = this._in.readData(this._objIndexMask);
      int numText = this._in.readData(this._objIndexMask);
      int numFontFamilies = this._in.readData(this._objIndexMask);
      int numFontUrls = this._in.readData(this._objIndexMask);
      int numFontNodes = this._in.readData(this._objIndexMask);
      int numMessages = this._in.readData(this._objIndexMask);
      int numHyperlinks = this._in.readData(this._objIndexMask);
      this._model._numCustomObjects = this._in.readData(this._objIndexMask);
      int numForeignObjects = this._in.readData(this._objIndexMask);
      int numFocusable = this._in.readData(this._numFocusableMask);
      this._tempImageNodeIdx = new int[numImages];
      if (!initArrays) {
         this._platform.fillArray(this._model._nodes, 0);
      } else if (this._tempTextStrings == null || this._tempTextStrings.length < numText) {
         this._tempTextStrings = numText > 0 ? new char[numText][] : (char[][])null;
         this._textIndex = numText > 0 ? new int[numText] : null;
      }

      if (initArrays && newModel) {
         if (this._model._coords == null || this._model._coords.length != numCoordsArrays) {
            this._model._coords = numCoordsArrays > 0 ? new int[numCoordsArrays][] : (int[][])null;
            this._model._finalCoords = numCoordsArrays > 0 ? new int[numCoordsArrays][] : (int[][])null;
         }

         if (this._model._pointTypes == null || this._model._pointTypes.length != numPointTypes) {
            this._model._pointTypes = numPointTypes > 0 ? new byte[numPointTypes][] : (byte[][])null;
            this._model._finalPointTypes = numPointTypes > 0 ? new byte[numPointTypes][] : (byte[][])null;
         }

         if (this._model._nodes != null && this._model._nodes.length >= this._streamNodeArraySize) {
            this._platform.fillArray(this._model._nodes, 0);
         } else {
            this._model._nodes = new int[modelNodeArraySize];
         }

         if (this._model._triggers == null || this._model._triggers.length != triggersSize) {
            this._model._triggers = new int[triggersSize];
         }

         if (this._model._handlesWithId == null || this._model._handlesWithId.length != numDataIds) {
            this._model._handlesWithId = new int[numDataIds];
            this._model._ids = new Object[numDataIds];
         }

         if (this._model._focusableList == null || this._model._focusableList.length != numFocusable) {
            this._model._focusableList = new int[numFocusable];
         }

         if (this._focusOrderUnreferenced == null || this._focusOrderUnreferenced.length != numFocusable) {
            this._focusOrderUnreferenced = new int[numFocusable];
         }

         if (this._model._foreignObjects == null || this._model._foreignObjects.length != numForeignObjects) {
            this._model._foreignObjects = new Object[numForeignObjects];
         }

         if (this._model._images == null || this._model._images.length != numImages) {
            this._model._images = new Object[numImages];
            this._model._imageURL = numImages > 0 ? new Object[numImages] : null;
         }

         if (this._model._media == null || this._model._media.length != numMedia) {
            this._model._media = new Object[numMedia];
            this._model._mediaObjectURL = numMedia > 0 ? new Object[numMedia] : null;
         }

         if (this._model._customObjects == null || this._model._customObjects.length != this._model._numCustomObjects) {
            this._model._customObjects = new Object[this._model._numCustomObjects];
         }

         if (this._model._fontFamilyStrings == null || this._model._fontFamilyStrings.length != numFontFamilies) {
            this._model._fontFamilyStrings = numFontFamilies > 0 ? new Object[numFontFamilies] : null;
            this._model._platformFontFamilyStrings = numFontFamilies > 0 ? new Object[numFontFamilies] : null;
         }

         if (this._model._fontUrlStrings == null || this._model._fontUrlStrings.length != numFontUrls) {
            this._model._fontUrlStrings = numFontUrls > 0 ? new Object[numFontUrls] : null;
         }

         if (this._model._fontHandles == null || this._model._fontHandles.length != numFontNodes) {
            this._model._fontHandles = numFontNodes > 0 ? new int[numFontNodes] : null;
         }

         if (this._model._customMessages == null || this._model._customMessages.length != numMessages) {
            this._model._customMessages = numMessages > 0 ? new Object[numMessages] : null;
         }

         if (this._model._hyperlinks == null || this._model._hyperlinks.length != numHyperlinks) {
            this._model._hyperlinks = numHyperlinks > 0 ? new Object[numHyperlinks] : null;
         }
      } else if (initArrays && !newModel) {
         if (numMedia > 0 || this._model._numCustomObjects > 0) {
            throw new Object(11);
         }

         if (numImages > 0) {
            if (this._model._images == null) {
               this._model._images = new Object[numImages];
            } else {
               this._platform.arrayResize(this._model._images, this._model._images.length + numImages);
            }

            if (this._model._imageURL == null) {
               this._model._imageURL = new Object[numImages];
            } else {
               this._platform.arrayResize(this._model._imageURL, this._model._imageURL.length + numImages);
            }
         }

         if (numCoordsArrays > 0) {
            if (this._model._coords == null) {
               this._model._coords = new int[numCoordsArrays][];
               this._model._finalCoords = new int[numCoordsArrays][];
            } else {
               this._platform.arrayResize(this._model._coords, this._model._coords.length + numCoordsArrays);
               this._platform.arrayResize(this._model._finalCoords, this._model._finalCoords.length + numCoordsArrays);
            }
         }

         if (numPointTypes > 0) {
            if (this._model._pointTypes == null) {
               this._model._pointTypes = new byte[numPointTypes][];
               this._model._finalPointTypes = new byte[numPointTypes][];
            } else {
               this._platform.arrayResize(this._model._pointTypes, this._model._pointTypes.length + numPointTypes);
               this._platform.arrayResize(this._model._finalPointTypes, this._model._finalPointTypes.length + numPointTypes);
            }
         }

         if (numDataIds > 0) {
            if (this._model._ids == null) {
               this._model._ids = new Object[numDataIds];
               this._model._handlesWithId = new int[numDataIds];
            } else {
               this._platform.arrayResize(this._model._ids, this._model._ids.length + numDataIds);
               this._platform.arrayResize(this._model._handlesWithId, this._model._handlesWithId.length + numDataIds);
            }
         }

         if (numFontFamilies > 0) {
            if (this._model._fontFamilyStrings == null) {
               this._model._fontFamilyStrings = new Object[numFontFamilies];
               this._model._platformFontFamilyStrings = new Object[numFontFamilies];
            } else {
               this._platform.arrayResize(this._model._fontFamilyStrings, this._model._fontFamilyStrings.length + numFontFamilies);
               this._platform.arrayResize(this._model._platformFontFamilyStrings, this._model._platformFontFamilyStrings.length + numFontFamilies);
            }
         }

         if (numFontNodes > 0) {
            if (this._model._fontHandles == null) {
               this._model._fontHandles = new int[numFontNodes];
            } else {
               this._platform.arrayResize(this._model._fontHandles, this._model._fontHandles.length + numFontNodes);
            }
         }
      }

      this._durations.reset();
      this._interpolators.reset();
      this._numBehaviors = 0;
      this._numInterpolators = 0;
   }

   protected void readVisualNode(int nodeIdx, boolean newModel) {
      this._model._nodes[nodeIdx + 9] = this._model._maxUID++;
      this.readVisualData(nodeIdx);
      int type = this._model._nodes[nodeIdx + 1];
      switch (type) {
         case 32:
            this.readTSpan(nodeIdx);
            break;
         case 34:
         case 162:
            this.readEllipse(nodeIdx);
            break;
         case 36:
         case 164:
            this.readRectangle(nodeIdx);
            break;
         case 40:
            this.readPath(nodeIdx);
            break;
         case 42:
            this.readImage(nodeIdx, newModel);
            break;
         case 44:
            if (!newModel) {
               throw new Object(11);
            }

            this.readForeignObject(nodeIdx);
            break;
         case 46:
            this.readSVG(nodeIdx);
            break;
         case 48:
         case 176:
            this.readGroup(nodeIdx);
            break;
         case 50:
            this.readText(nodeIdx);
      }

      this._model._nodes[nodeIdx + 8] = this._model._nodes[nodeIdx + 8] & 458494;
      this._model.setBits(nodeIdx, -16777216);
      this._model._nodes[nodeIdx + 15] = 1;
      this._model._nodes[nodeIdx + 16] = 1;
   }

   protected void readShapeData(int nodeIdx) {
      this.readFillData(nodeIdx);
      this.readStrokeData(nodeIdx);
   }

   protected void readBehaviorNode(int nodeIdx) {
      switch (this._model._nodes[nodeIdx + 1]) {
         case 1:
            this.readInterpolator(nodeIdx);
            return;
         case 3:
         case 11:
            this.readMediaObject(nodeIdx);
            return;
         case 5:
            this.readCustomAction(nodeIdx);
            return;
         case 7:
            this.readTimeContainer(nodeIdx);
         default:
            return;
         case 9:
            this.readHyperlink(nodeIdx);
      }
   }

   protected void readBehaviorData(int nodeIdx) {
      this.readDataNodeData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      int var10001 = nodeIdx + 9;
      if ((bits & 64) != 0) {
         this._model._nodes[var10001] = this.readTriggers();
      } else {
         this._model._nodes[var10001] = -1;
      }
   }

   protected void readTimingBehaviorData(int nodeIdx) {
      this.readBehaviorData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      this._model._nodes[nodeIdx + 10] = (bits & 128) != 0 ? this.readTriggers() : -1;
      this._model._nodes[nodeIdx + 11] = (bits & 512) != 0 ? this._in.readData(this._loopCountMask) : 1;
      if ((bits & 1024) != 0) {
         this._model._nodes[nodeIdx + 12] = this._in.readData(this._durationMask);
      }

      if ((bits & 2048) != 0) {
         this._model._nodes[nodeIdx + 13] = this._in.readData(this._durationMask);
      }
   }

   protected void readTimeContainer(int nodeIdx) {
      if (this._behaviorsRootIdx == -1) {
         this._behaviorsRootIdx = nodeIdx;
      }

      if (this._model._behaviorsRoot == -1) {
         this._model._behaviorsRoot = nodeIdx;
      }

      this.readTimingBehaviorData(nodeIdx);
   }

   protected void readMediaObject(int nodeIdx) {
      this.readTimingBehaviorData(nodeIdx);
      this._model._nodes[nodeIdx + 16] = this._numBehaviors++;
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 134217728) != 0) {
         this._model._mediaObjectURL[this._mediaIndex] = this._in.readString(this._encoding);
         this._model._nodes[nodeIdx + 17] = this._mediaIndex++;
      } else {
         this._model._nodes[nodeIdx + 17] = this._in.readData(this._objIndexMask);
      }
   }

   protected void readInterpolator(int nodeIdx) {
      this._numInterpolators++;
      this._model._nodes[nodeIdx + 16] = this._numBehaviors++;
      this.readTimingBehaviorData(nodeIdx);
      int targetAttribute = this._model._nodes[nodeIdx + 17] = this._in.readData(4);
      int pmeTargetIndex = this._in.readData(this._visualNodeIndexMask);
      int modelTargetIndex = this.setNodeReference(0, nodeIdx + 18, pmeTargetIndex);
      int targetMask;
      int targetShift;
      switch (targetAttribute) {
         case 1:
            targetMask = 128;
            targetShift = 7;
            break;
         case 31:
            targetMask = 255;
            targetShift = 0;
            break;
         case 32:
            targetMask = 65280;
            targetShift = 8;
            break;
         case 33:
            targetMask = 16711680;
            targetShift = 16;
            break;
         case 35:
            targetMask = 240;
            targetShift = 0;
            break;
         case 36:
            targetMask = 15;
            targetShift = 0;
            break;
         case 42:
            targetMask = 16;
            targetShift = 4;
            break;
         default:
            targetMask = -1;
            targetShift = 0;
      }

      this._model._nodes[nodeIdx + 29] = targetMask;
      this._model._nodes[nodeIdx + 30] = targetShift;
      if (!this.targetExists(modelTargetIndex, targetAttribute)) {
         this.createDefaultAttributeNode(modelTargetIndex, targetAttribute);
      }

      this.enableTarget(modelTargetIndex, targetAttribute);
      if (modelTargetIndex < this._model._minAnimatedNodeIdx) {
         this._model._minAnimatedNodeIdx = modelTargetIndex;
      }

      int calcMode = this._in.readData(4);
      this._model._nodes[nodeIdx + 19] = calcMode;
      int bits = this._model._nodes[nodeIdx + 8];
      int dur = -1;
      if (this._model.bitsAreSet(nodeIdx, 1024)) {
         dur = this._model._nodes[nodeIdx + 12];
      }

      this._interpolators.enqueue(nodeIdx);
      this._durations.enqueue(dur);
      if ((bits & 262144) != 0) {
         this._model._nodes[nodeIdx + 21] = this._keyTimeIndex;
         int numKeyTimes = this._in.readData(this._varDataMask);
         this._tempKeyTimes[this._keyTimeIndex] = new int[numKeyTimes];
         this._tempKeyTimes[this._keyTimeIndex][0] = 0;
         if (numKeyTimes > 2) {
            this._in.readDataArray(this._tempKeyTimes[this._keyTimeIndex], 1, numKeyTimes - 2, 1);
         }

         this._tempKeyTimes[this._keyTimeIndex++][numKeyTimes - 1] = 65536;
      } else {
         this._model._nodes[nodeIdx + 21] = this._in.readData(this._keyTimeIndexMask);
      }

      if ((bits & 524288) == 0) {
         this._model._nodes[nodeIdx + 20] = this._in.readData(this._keyValIndexMask);
         if (targetAttribute == 14) {
            this._model._nodes[nodeIdx + 17] = 16;
         }
      } else {
         this._model._nodes[nodeIdx + 20] = this._keyValueIndex;
         int numKeyValues = this._in.readData(this._varDataMask);
         this._tempKeyValues[this._keyValueIndex] = new int[numKeyValues];
         this._in.readDataArray(this._tempKeyValues[this._keyValueIndex], 0, numKeyValues, this._keyValMask);
         if (targetAttribute == 14) {
            this._model._nodes[nodeIdx + 17] = 16;
            int[] tempKeyValues = new int[2 * numKeyValues];
            int i = 0;

            for (int xIdx = 0; i < numKeyValues; xIdx += 2) {
               tempKeyValues[xIdx] = this._tempKeyValues[this._keyValueIndex][i];
               tempKeyValues[xIdx + 1] = 0;
               i++;
            }

            this._tempKeyValues[this._keyValueIndex] = tempKeyValues;
         }

         int targetBits = this._model._nodes[modelTargetIndex + 8];
         if (!this.isPercentKeyValue(targetAttribute, targetBits) && this.convertKeyValuesToFixed32(targetAttribute)) {
            for (int i = 0; i < numKeyValues; i++) {
               this._tempKeyValues[this._keyValueIndex][i] = Fixed32.toFP(this._tempKeyValues[this._keyValueIndex][i]);
            }
         }

         if (targetAttribute == 13) {
            for (int i = 0; i < numKeyValues; i++) {
               if (this._tempKeyValues[this._keyValueIndex][i] == -1) {
                  this._tempKeyValues[this._keyValueIndex][i] = -1;
               }
            }
         }

         this._keyValueIndex++;
      }
   }

   protected void readCustomAction(int nodeIdx) {
      this.readBehaviorData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      this._model._nodes[nodeIdx + 10] = (bits & 512) != 0 ? this._in.readData(this._eventTypeMask) : 107;
      if ((bits & 1024) != 0) {
         if ((bits & 134217728) != 0) {
            this._model._nodes[nodeIdx + 11] = this.readMessage();
         } else {
            this._model._nodes[nodeIdx + 11] = this._in.readData(this._objIndexMask);
         }
      } else {
         this._model._nodes[nodeIdx + 11] = -1;
      }

      if ((bits & 128) != 0) {
         this.setNodeReference(0, nodeIdx + 12, this._in.readData(this._nodeIndexMask));
      } else {
         this._model._nodes[nodeIdx + 12] = -1;
      }
   }

   protected void readHyperlink(int nodeIdx) {
      this.readBehaviorData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 134217728) != 0) {
         this._model._hyperlinks[this._hyperlinkIndex] = this._in.readString(this._encoding);
         this._model._nodes[nodeIdx + 10] = this._hyperlinkIndex++;
      } else {
         this._model._nodes[nodeIdx + 10] = this._in.readData(this._objIndexMask);
      }
   }

   protected int readTriggers() {
      int triggersOffset = -1;
      int numTriggers = this._in.readData(this._varDataMask);
      if (numTriggers > 0) {
         triggersOffset = this._triggerIndex;
         this._model._triggers[this._triggerIndex++] = numTriggers;

         for (int i = 0; i < numTriggers; i++) {
            this.readTriggerAttr(this._triggerIndex);
            this._triggerIndex += 4;
         }
      }

      return triggersOffset;
   }

   protected void setUpNodesTree(int nodeIdx) {
      int type = this._model._nodes[nodeIdx + 1];
      if (this.isContainerNode(type)) {
         int child = this._model._nodes[nodeIdx + 6];

         while (child != -1) {
            if (child == nodeIdx || child == this._model._visualRoot || child == this._model._behaviorsRoot) {
               throw new Object(((StringBuffer)(new Object("PME12: invalid child index "))).append(child).append(" for node ").append(nodeIdx).toString());
            }

            this._model._nodes[child + 3] = nodeIdx;
            int next = this._model._nodes[child + 4];
            if (next == -1) {
               this._model._nodes[nodeIdx + 7] = child;
            } else {
               this._model._nodes[next + 5] = child;
            }

            this.setUpNodesTree(child);
            child = next;
         }
      }
   }

   protected void readCustomObjectNode(int nodeIdx) {
      this.readDataNodeData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      int typeIndex;
      if ((bits & 33554432) != 0) {
         typeIndex = this._model._nodes[nodeIdx + 10] = this.readMessage();
      } else {
         typeIndex = this._model._nodes[nodeIdx + 10] = (bits & 8388608) != 0 ? this._in.readData(this._objIndexMask) : -1;
      }

      String data = this._in.readString(this._encoding);
      if (this._resourceProvider != null) {
         this._model._nodes[nodeIdx + 9] = -1;

         label42:
         try {
            String type = typeIndex == -1 ? "custom" : this._model._customMessages[typeIndex];
            this._model._customObjects[this._customObjectIndex] = this._resourceProvider.createResource(type, data, this._context, null);
            this._model._nodes[nodeIdx + 9] = this._customObjectIndex;
         } finally {
            break label42;
         }
      }

      this._customObjectIndex++;
   }

   protected void readCustomEventNode(int nodeIdx) {
      this.readId(nodeIdx);
   }

   protected void readId(int nodeIdx) {
      this._model._ids[this._idIndex] = this._in.readString(this._encoding);
      this._model._handlesWithId[this._idIndex] = nodeIdx;
      this._idIndex++;
   }

   protected void readDataNode(int nodeIdx) {
      switch (this._model._nodes[nodeIdx + 1]) {
         case 96:
         default:
            this.readCustomObjectNode(nodeIdx);
            return;
         case 97:
            this.readFontNode(nodeIdx);
            return;
         case 98:
            this.readCustomEventNode(nodeIdx);
         case 95:
      }
   }

   protected boolean isSupportedNode(int type) {
      return this.getSize(type) != -1;
   }

   protected void setupNodes() {
      this.setUpNodesTree(0);
      if (this._model._visualRoot > 0) {
         this.setUpNodesTree(this._model._visualRoot);
      }

      if (this._model._behaviorsRoot > 0) {
         this.setUpNodesTree(this._model._behaviorsRoot);
      }
   }

   protected boolean isAttrNode(int type) {
      return type >= 64 && type < 95;
   }

   protected boolean isVisualNode(int type) {
      if ((type & 128) != 0) {
         type ^= 128;
      }

      return type >= 32 && type < 63;
   }

   protected boolean isBehaviorNode(int type) {
      return type >= 1 && type < 31;
   }

   protected boolean isDataNode(int type) {
      return type >= 96 && type < 127;
   }

   protected boolean isTreeNode(int type) {
      return this.isSupportedNode(type) && (this.isVisualNode(type) || this.isBehaviorNode(type));
   }

   protected boolean isContainerNode(int type) {
      return this.isTreeNode(type) && (this.isBehaviorNode(type) || this.isVisualContainerNode(type));
   }

   protected boolean isVisualContainerNode(int type) {
      if ((type & 128) != 0) {
         type ^= 128;
      }

      return type == 46 || type == 48 || type == 50;
   }

   protected int readNode(boolean newModel) {
      int type = this._in.readData(0);
      if (type == 0) {
         type |= this._in.readData(0) << 8;
      }

      if (this.isSupportedNode(type)) {
         return this.readKnownNode(type, newModel);
      } else if (newModel) {
         return this.readUnknownNode(type);
      } else {
         throw new Object(11);
      }
   }

   protected int readKnownNode(int type, boolean newModel) {
      int size = this.getSize(type);
      int nodeIdx = this._model.addNode(type, size);
      this.setReferencesToNewNode(nodeIdx, this._streamNextNodeIdx);
      if (this._streamNextNodeIdx != nodeIdx) {
         this.addNodeMapping(nodeIdx, this._streamNextNodeIdx);
      }

      this._streamNextNodeIdx = this._streamNextNodeIdx + this.getFormatSize(nodeIdx);
      if (this.isVisualNode(type)) {
         this.readVisualNode(nodeIdx, newModel);
      } else if (this.isAttrNode(type)) {
         this.readAttrNode(nodeIdx);
      } else if (this.isBehaviorNode(type)) {
         if (!newModel && this._behaviorsRootIdx != -1) {
            throw new Object(11);
         }

         this.readBehaviorNode(nodeIdx);
      } else if (this.isDataNode(type)) {
         if (!newModel) {
            throw new Object(11);
         }

         this.readDataNode(nodeIdx);
      }

      return nodeIdx;
   }

   protected int readUnknownNode(int type) {
      int size = this._in.readData(this._dataSizeMask);
      int nodeIdx = this._model.addNode(type, size);
      this.setReferencesToNewNode(nodeIdx, this._streamNextNodeIdx);
      if (this._streamNextNodeIdx != nodeIdx) {
         this.addNodeMapping(nodeIdx, this._streamNextNodeIdx);
      }

      this._streamNextNodeIdx += size;

      for (int i = 0; i < size; i++) {
         this._model._nodes[nodeIdx + 3 + i] = this._in.readData(4);
      }

      return nodeIdx;
   }

   protected int readBits() {
      int bits = 0;

      for (int shift = 0; shift < 32; shift += 8) {
         int extraBits = this._in.readData(0);
         bits |= extraBits << shift;
         if ((extraBits & 16843009) == 0) {
            return bits;
         }
      }

      return bits;
   }

   protected void readDataNodeData(int nodeIdx) {
      int bits = this.readBits();
      this._model._nodes[nodeIdx + 8] = bits;
      if ((bits & 4) != 0) {
         this.readId(nodeIdx);
      }

      this.readTreeData(nodeIdx, bits);
   }

   @Override
   public Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      return null;
   }

   @Override
   public synchronized Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      Object object = null;
      if ("application/x-vnd.rim.pme".equals(type)) {
         if (context == null) {
            context = ResourceContext.createContext();
         }

         if (data instanceof AnimationModel) {
            this.read(data, ((AnimationModel)data)._rawData, 0, false, null);
            object = data;
         } else if (data instanceof byte[]) {
            if (referrer instanceof Object) {
               this._resourceProvider = (ResourceProvider)referrer;
            }

            object = this.read(null, (byte[])data, 0, context);
            if (object instanceof AnimationModel) {
               ((AnimationModel)object)._contentType = type;
            }

            this._resourceProvider = null;
         }
      }

      return object;
   }

   private void readOpacityData(int nodeIdx) {
      if (this._model.bitsAreSet(nodeIdx, 512)) {
         this._model._nodes[nodeIdx + 13] = this._in.readData(0);
      } else {
         this._model._nodes[nodeIdx + 13] = 255;
      }
   }

   private void readFillData(int nodeIdx) {
      if (this._model.bitsAreSet(nodeIdx, 1048576)) {
         this.setNodeReference(0, nodeIdx + 21, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 21] = -1;
      }
   }

   private void readStrokeData(int nodeIdx) {
      if (this._model.bitsAreSet(nodeIdx, 2097152)) {
         this.setNodeReference(0, nodeIdx + 22, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 22] = -1;
      }
   }

   private void readTransformData(int nodeIdx) {
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 524288) != 0) {
         this.setNodeReference(0, nodeIdx + 12, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 12] = -1;
      }
   }

   private void readFontFamilies() {
      if (this._model._fontFamilyStrings != null) {
         for (int i = this._fontFamilyStringsOffset; i < this._model._fontFamilyStrings.length; i++) {
            this._model._fontFamilyStrings[i] = this._in.readString(this._encoding);
            if (this._model._platformFontFamilyStrings[i] == null) {
               this._model._platformFontFamilyStrings[i] = this._model._fontFamilyStrings[i];
            }
         }
      }
   }

   private void readViewboxAttr(int attrIdx) {
      this._model._nodes[attrIdx + 3] = Fixed32.toFP(this._in.readData(this._vNodeXCoordMask));
      this._model._nodes[attrIdx + 4] = Fixed32.toFP(this._in.readData(this._vNodeYCoordMask));
      this._model._nodes[attrIdx + 5] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      this._model._nodes[attrIdx + 6] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      this.createDefaultTransformNode(attrIdx, 7);
   }

   private Object read(Object m, byte[] data, int offset, ResourceContext context) {
      if (m == null) {
         m = new ModelInteractorImpl();
      }

      this.read(m, data, offset, true, context);
      return m;
   }

   private void readVisualData(int nodeIdx) {
      this.readDataNodeData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 1024) != 0) {
         int hints = 0;
         int hintBits = this._in.readData(0);
         if ((hintBits & 1) != 0) {
            hints |= 16777216;
            hints |= this._in.readData(0) << 16;
         }

         if ((hintBits & 2) != 0) {
            hints |= 33554432;
            hints |= this._in.readData(0) << 8;
         }

         if ((hintBits & 4) != 0) {
            hints |= 67108864;
            hints |= this._in.readData(0) << 0;
         }

         this._model._nodes[nodeIdx + 14] = hints;
      }

      this._model._nodes[nodeIdx + 10] = Fixed32.toFP(this._in.readData(this._vNodeXCoordMask));
      this._model._nodes[nodeIdx + 11] = Fixed32.toFP(this._in.readData(this._vNodeYCoordMask));
      this.readTransformData(nodeIdx);
      this.readOpacityData(nodeIdx);
      if ((bits & 32) != 0) {
         this._focusOrderUnreferenced[this._focusIndex] = nodeIdx;
         this._focusOrderHashtable.put(nodeIdx, this._focusIndex++);
      }
   }

   private void read(Object m, byte[] data, int offset, boolean full, ResourceContext context) {
      this.set(m, data, offset, context);
      this.readHeader();

      int type;
      while (this._in.available() > 0 && (type = this._in.readData(0)) != 255) {
         this.readSection(type, full, true);
      }

      if (full) {
         this.loadResources();
      }

      this.set(null, null, 0, null);
   }

   private void readText(int nodeIdx) {
      this.readShapeData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 67108864) != 0) {
         this.setNodeReference(0, nodeIdx + 23, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 23] = -1;
      }

      if (this._numTextElements >= this._textIndex.length) {
         int length = this._textIndex.length;
         this._platform.arrayResize(this._textIndex, length + 5);

         for (int i = length; i < this._textIndex.length; i++) {
            this._textIndex[i] = -1;
         }
      }

      if ((bits & 134217728) != 0) {
         this._tempTextStrings[this._tempTextStringsIdx] = this._in.readString(this._encoding).toCharArray();
         this._textIndex[this._numTextElements] = this._tempTextStringsIdx++;
      } else {
         this._textIndex[this._numTextElements] = this._in.readData(this._objIndexMask);
      }

      this._model._nodes[nodeIdx + 24] = this._convertedTextStringsOffset + this._numTextElements++;
      this._model._nodes[nodeIdx + 25] = 0;
      int dx = this._in.readData(this._vNodeXCoordMask);
      int dy = this._in.readData(this._vNodeYCoordMask);
      this._model._nodes[nodeIdx + 26] = Fixed32.toFP(dx);
      this._model._nodes[nodeIdx + 27] = Fixed32.toFP(dy);
      this._model._nodes[nodeIdx + 29] = -1;
   }

   private boolean convertKeyValuesToFixed32(int targetAttribute) {
      return targetAttribute == 6
         || targetAttribute == 7
         || targetAttribute == 10
         || targetAttribute == 11
         || targetAttribute == 12
         || targetAttribute == 8
         || targetAttribute == 9
         || targetAttribute == 25
         || targetAttribute == 26
         || targetAttribute == 27
         || targetAttribute == 28
         || targetAttribute == 29
         || targetAttribute == 30;
   }

   private boolean isPercentKeyValue(int targetAttribute, int targetBits) {
      return targetAttribute == 8 && (targetBits & 16384) != 0 ? true : targetAttribute == 9 && (targetBits & 32768) != 0;
   }

   private void handleTextElements() {
      if (this._numTextElements != 0) {
         if (this._model._convertedTextStrings == null) {
            this._model._convertedTextStrings = new char[this._numTextElements][];
         } else {
            this._platform.arrayResize(this._model._convertedTextStrings, this._convertedTextStringsOffset + this._numTextElements);
         }

         boolean[] tempStringUsed = new boolean[this._tempTextStrings.length];

         for (int i = 0; i < this._numTextElements; i++) {
            int textIndex = this._textIndex[i];
            if (!tempStringUsed[textIndex]) {
               this._model._convertedTextStrings[this._convertedTextStringsOffset + i] = this._tempTextStrings[textIndex];
               tempStringUsed[textIndex] = true;
            } else {
               char[] strRef = this._tempTextStrings[textIndex];
               char[] newTextStr = new char[strRef.length];
               System.arraycopy(strRef, 0, newTextStr, 0, strRef.length);
               this._model._convertedTextStrings[this._convertedTextStringsOffset + i] = newTextStr;
            }
         }
      }
   }

   private void enableTarget(int vnIndex, int targetAttribute) {
      int nodeOffset = 0;
      int bitsOffset = 0;
      int bits = 0;
      int defaultValue = 0;
      int targetOffset = 0;
      switch (targetAttribute) {
         case 2:
            nodeOffset = this._model._nodes[vnIndex + 21];
            bitsOffset = nodeOffset + 3;
            bits = 4;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 5;
            break;
         case 4:
            nodeOffset = this._model._nodes[vnIndex + 22];
            bitsOffset = nodeOffset + 3;
            bits = 4;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 5;
            break;
         case 6:
         case 7:
            if (this._model._nodes[vnIndex + 1] == 32 || this._model._nodes[vnIndex + 1] == 50) {
               int type = targetAttribute == 6 ? 0 : 1;
               nodeOffset = vnIndex;
               bitsOffset = nodeOffset + 8;
               bits = type == 0 ? 8192 : 131072;
               defaultValue = Integer.MAX_VALUE;
               targetOffset = type == 0 ? nodeOffset + 10 : nodeOffset + 11;
            }
            break;
         case 30:
            nodeOffset = this._model._nodes[vnIndex + 22];
            bitsOffset = nodeOffset + 3;
            bits = 8;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 6;
            break;
         case 31:
            nodeOffset = vnIndex;
            bitsOffset = nodeOffset + 8;
            bits = 1024;
            defaultValue = 67108864;
            targetOffset = nodeOffset + 14;
            break;
         case 32:
            nodeOffset = vnIndex;
            bitsOffset = nodeOffset + 8;
            bits = 1024;
            defaultValue = 33554432;
            targetOffset = nodeOffset + 14;
            break;
         case 33:
            nodeOffset = vnIndex;
            bitsOffset = nodeOffset + 8;
            bits = 1024;
            defaultValue = 16777216;
            targetOffset = nodeOffset + 14;
            break;
         case 34:
            nodeOffset = vnIndex;
            bitsOffset = nodeOffset + 8;
            bits = 8192;
            defaultValue = this._model._nodes[nodeOffset + 27];
            if (defaultValue == -1) {
               defaultValue = 10;
            }

            targetOffset = nodeOffset + 27;
            break;
         case 35:
            nodeOffset = this._model._nodes[vnIndex + 22];
            bitsOffset = nodeOffset + 3;
            bits = 16;
            defaultValue = 240;
            targetOffset = nodeOffset + 7;
            break;
         case 36:
            nodeOffset = this._model._nodes[vnIndex + 22];
            bitsOffset = nodeOffset + 3;
            bits = 32;
            defaultValue = 15;
            targetOffset = nodeOffset + 7;
            break;
         case 37:
            nodeOffset = this._model._nodes[vnIndex + 1] == 46 ? this._model._nodes[vnIndex + 29] : this._model._nodes[vnIndex + 23];
            bitsOffset = nodeOffset + 3;
            bits = 64;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 7;
            break;
         case 38:
            nodeOffset = this._model._nodes[vnIndex + 1] == 46 ? this._model._nodes[vnIndex + 29] : this._model._nodes[vnIndex + 23];
            bits = 32;
            bitsOffset = nodeOffset + 3;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 4;
            break;
         case 39:
            nodeOffset = this._model._nodes[vnIndex + 1] == 46 ? this._model._nodes[vnIndex + 29] : this._model._nodes[vnIndex + 23];
            bitsOffset = nodeOffset + 3;
            bits = 2;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 6;
            break;
         case 40:
            nodeOffset = this._model._nodes[vnIndex + 1] == 46 ? this._model._nodes[vnIndex + 29] : this._model._nodes[vnIndex + 23];
            bitsOffset = nodeOffset + 3;
            bits = 16;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 5;
            break;
         case 41:
            nodeOffset = this._model._nodes[vnIndex + 1] == 46 ? this._model._nodes[vnIndex + 29] : this._model._nodes[vnIndex + 23];
            bitsOffset = nodeOffset + 3;
            bits = 8;
            defaultValue = Integer.MAX_VALUE;
            targetOffset = nodeOffset + 8;
            break;
         default:
            return;
      }

      if ((this._model._nodes[bitsOffset] & bits) != bits) {
         this._model._nodes[bitsOffset] = this._model._nodes[bitsOffset] | bits;
         if (defaultValue == Integer.MAX_VALUE) {
            this._model._nodes[targetOffset] = defaultValue;
            return;
         }

         this._model._nodes[targetOffset] = this._model._nodes[targetOffset] | defaultValue;
      }
   }

   private boolean targetExists(int vnIndex, int targetAttribute) {
      int offset = 0;
      switch (targetAttribute) {
         case 1:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 21:
         case 25:
         case 26:
         case 27:
         case 28:
         case 31:
         case 32:
         case 33:
         case 34:
            return true;
         case 2:
         default:
            offset = vnIndex + 21;
            break;
         case 3:
            offset = vnIndex + 21;
            offset = this._model._nodes[offset] == -1 ? offset : this._model._nodes[offset] + 4;
            break;
         case 4:
         case 30:
         case 35:
         case 36:
            offset = vnIndex + 22;
            break;
         case 5:
            offset = vnIndex + 22;
            offset = this._model._nodes[offset] == -1 ? offset : this._model._nodes[offset] + 4;
            break;
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
            offset = vnIndex + 12;
            break;
         case 29:
            offset = vnIndex + 28;
            break;
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            if (this._model._nodes[vnIndex + 1] == 46) {
               offset = vnIndex + 29;
            } else {
               offset = vnIndex + 23;
            }
      }

      return this._model._nodes[offset] != -1;
   }

   private void createDefaultAttributeNode(int targetIndex, int targetAttribute) {
      switch (targetAttribute) {
         case 2:
         default:
            this.createDefaultFillNode(targetIndex);
            return;
         case 3:
            this.createDefaultFillPaintNode(targetIndex);
            return;
         case 4:
         case 30:
         case 35:
         case 36:
            this.createDefaultStrokeNode(targetIndex);
            return;
         case 5:
            this.createDefaultStrokePaintNode(targetIndex);
            return;
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
            this.createDefaultTransformNode(targetIndex, 12);
            return;
         case 29:
            this.createDefaultViewBoxNode(targetIndex);
            return;
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            this.createDefaultTextAttributeNode(targetIndex);
         case 1:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 21:
         case 25:
         case 26:
         case 27:
         case 28:
         case 31:
         case 32:
         case 33:
         case 34:
      }
   }

   private void createDefaultTextAttributeNode(int vnIndex) {
      int type = 68;
      int size = this.getSize(type);
      int textAttrIdx = this._model.addNode(type, size);
      if (this._model._nodes[vnIndex + 1] == 46) {
         this._model._nodes[vnIndex + 29] = textAttrIdx;
      } else {
         this._model._nodes[vnIndex + 23] = textAttrIdx;
      }
   }

   private int createDefaultFillNode(int vnIndex) {
      int type = 70;
      int size = this.getSize(type);
      int nodeIdx = this._model.addNode(type, size);
      this._model._nodes[vnIndex + 21] = nodeIdx;
      this._model._nodes[nodeIdx + 4] = -1;
      return nodeIdx;
   }

   private void createDefaultFillPaintNode(int vnIndex) {
      int fillIdx = this._model._nodes[vnIndex + 21];
      switch (fillIdx) {
         case -1:
            fillIdx = this.createDefaultFillNode(vnIndex);
         default:
            int paintIdx = this.createDefaultPaintNode(vnIndex, 2048);
            this._model._nodes[fillIdx + 4] = paintIdx;
      }
   }

   private int createDefaultStrokeNode(int vnIndex) {
      int type = 72;
      int size = this.getSize(type);
      int nodeIdx = this._model.addNode(type, size);
      this._model._nodes[vnIndex + 22] = nodeIdx;
      this._model._nodes[nodeIdx + 4] = -1;
      return nodeIdx;
   }

   private void createDefaultStrokePaintNode(int vnIndex) {
      int strokeIdx = this._model._nodes[vnIndex + 22];
      switch (strokeIdx) {
         case -1:
            strokeIdx = this.createDefaultStrokeNode(vnIndex);
         default:
            int paintIdx = this.createDefaultPaintNode(vnIndex, 4096);
            this._model._nodes[strokeIdx + 4] = paintIdx;
      }
   }

   private void createDefaultViewBoxNode(int targetIndex) {
      int type = 66;
      int size = this.getSize(type);
      int nodeIdx = this._model.addNode(type, size);
      int offset = this._model._nodes[targetIndex + 28];
      this._model._nodes[targetIndex + 28] = nodeIdx;
      if (offset == -1) {
         this._model._nodes[nodeIdx + 3] = Integer.MIN_VALUE;
         this._model._nodes[nodeIdx + 4] = Integer.MIN_VALUE;
         this._model._nodes[nodeIdx + 5] = Integer.MIN_VALUE;
         this._model._nodes[nodeIdx + 6] = Integer.MIN_VALUE;
      } else {
         this._model._nodes[nodeIdx + 3] = this._model._nodes[offset + 3];
         this._model._nodes[nodeIdx + 4] = this._model._nodes[offset + 4];
         this._model._nodes[nodeIdx + 5] = this._model._nodes[offset + 5];
         this._model._nodes[nodeIdx + 6] = this._model._nodes[offset + 6];
      }

      this.createDefaultTransformNode(nodeIdx, 7);
   }

   private void createDefaultTransformNode(int targetIndex, int targetOffset) {
      int type = 64;
      int size = this.getSize(type);
      int nodeIdx = this._model.addNode(type, size);
      this._model._nodes[targetIndex + targetOffset] = nodeIdx;
      this._platform.setIdentity(this._model._nodes, nodeIdx + 3);
   }

   private int createDefaultPaintNode(int vnIndex, int paintNoneBitMask) {
      int type = 74;
      int size = this.getSize(type);
      int paintIdx = this._model.addNode(type, size);
      int nodeBits = this._model._nodes[vnIndex + 8];
      boolean hasPaintNoneBit = (nodeBits & paintNoneBitMask) != 0;
      if (hasPaintNoneBit) {
         this._model._nodes[paintIdx + 3] = Integer.MIN_VALUE;
      } else {
         this._model._nodes[paintIdx + 3] = Integer.MAX_VALUE;
      }

      return paintIdx;
   }

   private void readTSpan(int nodeIdx) {
      this.readText(nodeIdx);
      this._model._nodes[nodeIdx + 25] = this._in.readData(this._tspanOffsetMask);
   }

   private void readGroup(int nodeIdx) {
      this.readShapeData(nodeIdx);
      int type = this._model._nodes[nodeIdx + 1];
      if ((type & 128) != 0) {
         this._model._nodes[nodeIdx + 1] = type ^ 128;
      }

      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 67108864) != 0) {
         this.setNodeReference(0, nodeIdx + 23, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 23] = -1;
      }

      if (type == 176) {
         if ((bits & 8192) != 0) {
            int currentChild = this._in.readData(this._curChildMask);
            this.setNodeReference(0, nodeIdx + 24, currentChild);
         } else {
            this._model._nodes[nodeIdx + 24] = (bits & 16384) != 0 ? -2 : -1;
         }
      } else {
         this._model._nodes[nodeIdx + 24] = -2;
      }
   }

   private void readViewportData(int nodeIdx) {
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 16384) == 0) {
         this._model._nodes[nodeIdx + 23] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      } else {
         this._model._nodes[nodeIdx + 23] = this._in.readData(this._widthHeightMask);
      }

      if ((bits & 32768) == 0) {
         this._model._nodes[nodeIdx + 24] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      } else {
         this._model._nodes[nodeIdx + 24] = this._in.readData(this._widthHeightMask);
      }

      if (this._model.bitsAreSet(nodeIdx, 4194304)) {
         this.setNodeReference(0, nodeIdx + 28, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 28] = -1;
      }

      if ((bits & 8192) != 0) {
         this._model._nodes[nodeIdx + 27] = this._in.readData(0);
      }

      this._model._nodes[nodeIdx + 26] = this._model._nodes[nodeIdx + 24];
      this._model._nodes[nodeIdx + 25] = this._model._nodes[nodeIdx + 23];
   }

   private void readTriggerAttr(int attrIndex) {
      int bits = this._in.readData(0);
      if ((bits & 8) != 0) {
         this._model._triggers[attrIndex + 0] = 107;
         this.setNodeReference(1, attrIndex + 1, this._in.readData(this._eventTypeMask));
      } else {
         this._model._triggers[attrIndex + 0] = (bits & 1) != 0 ? this._in.readData(this._eventTypeMask) : -1;
         if ((bits & 2) != 0) {
            this._focusOrderTarget = this.setNodeReference(1, attrIndex + 1, this._in.readData(this._eventTargetIndexMask));
            this.addFocusableItem(this._focusOrderTarget);
         } else {
            this._model._triggers[attrIndex + 1] = -1;
         }
      }

      this._model._triggers[attrIndex + 2] = (bits & 4) != 0 ? this._in.readData(this._triggerTimeMask) : 0;
      if ((bits & 16) != 0) {
         this._model._triggers[attrIndex + 3] = this._in.readData(this._eventTypeMask);
      }
   }

   private void addFocusableItem(int targetId) {
      if (this._focusOrderHashtable.containsKey(targetId)) {
         int index = this._focusOrderHashtable.remove(targetId);
         this._focusOrderUnreferenced[index] = -1;
         this._model._focusableList[this._focusOrderIndex++] = targetId;
      }
   }

   private void readImage(int nodeIdx, boolean newModel) {
      this.readShapeData(nodeIdx);
      this.readViewportData(nodeIdx);
      this._model._nodes[nodeIdx + 21] = -1;
      this._model._nodes[nodeIdx + 22] = -1;
      int bits = this._model._nodes[nodeIdx + 8];
      this._model._nodes[nodeIdx + 29] = -1;
      if ((bits & 134217728) != 0) {
         this._model._imageURL[this._imageIndex] = this._in.readString(this._encoding);
         this._model._nodes[nodeIdx + 29] = this._imageIndex++;
      } else {
         this._model._nodes[nodeIdx + 29] = this._in.readData(this._objIndexMask) + this._imageIndexOffset;
      }

      this._tempImageNodeIdx[this._model._nodes[nodeIdx + 29] - this._imageIndexOffset] = nodeIdx;
   }

   private int readMessage() {
      this._model._customMessages[this._messageIndex] = this._in.readString(this._encoding);
      return this._messageIndex++;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void readForeignObject(int nodeIdx) {
      this.readShapeData(nodeIdx);
      this.readViewportData(nodeIdx);
      this._model._nodes[nodeIdx + 21] = -1;
      this._model._nodes[nodeIdx + 22] = -1;
      int bits = this._model._nodes[nodeIdx + 8];
      this._model._nodes[nodeIdx + 29] = this._foreignObjectIndex;
      int typeIndex;
      if ((bits & 33554432) != 0) {
         typeIndex = this._model._nodes[nodeIdx + 30] = this.readMessage();
      } else {
         typeIndex = this._model._nodes[nodeIdx + 30] = (bits & 8388608) != 0 ? this._in.readData(this._objIndexMask) : -1;
      }

      if ((bits & 134217728) != 0) {
         String data = this._in.readString(this._encoding);
         if (this._resourceProvider != null) {
            boolean var9 = false /* VF: Semaphore variable */;

            try {
               var9 = true;
               String type = typeIndex == -1 ? "foreignObject" : this._model._customMessages[typeIndex];
               this._context.set("Handle", new Object(nodeIdx));
               Object resource = this._resourceProvider.createResource(type, data, this._context, null);
               if (!(resource instanceof Object)) {
                  if (this._model._contentServices == null) {
                     this._model._contentServices = new Object[1];
                     this._model._contentServiceNodeHandles = new int[1];
                     this._model._contentServices[0] = (MediaService)resource;
                     this._model._contentServiceNodeHandles[0] = nodeIdx;
                     var9 = false;
                  } else {
                     Arrays.add(this._model._contentServices, resource);
                     Arrays.add(this._model._contentServiceNodeHandles, nodeIdx);
                     var9 = false;
                  }
               } else {
                  ForeignObject foreignObject = (ForeignObject)resource;
                  if (foreignObject == null) {
                     return;
                  }

                  foreignObject.setHandle(nodeIdx);
                  foreignObject.setPeer(this._model);
                  foreignObject.setPosition(Fixed32.toInt(this._model._nodes[nodeIdx + 10]), Fixed32.toInt(this._model._nodes[nodeIdx + 11]));
                  foreignObject.setExtent(Fixed32.toInt(this._model._nodes[nodeIdx + 23]), Fixed32.toInt(this._model._nodes[nodeIdx + 24]));
                  this._model._foreignObjects[this._foreignObjectIndex] = foreignObject;
                  var9 = false;
               }
            } finally {
               if (var9) {
                  return;
               }
            }
         }
      }

      this._foreignObjectIndex++;
   }

   private void readSVG(int nodeIdx) {
      if (this._visualRootIdx == -1) {
         this._visualRootIdx = nodeIdx;
      }

      if (this._model._visualRoot == -1) {
         this._model._visualRoot = nodeIdx;
      }

      this.readShapeData(nodeIdx);
      int bits = this._model._nodes[nodeIdx + 8];
      if ((bits & 67108864) != 0) {
         this.setNodeReference(0, nodeIdx + 29, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[nodeIdx + 29] = -1;
      }

      this.readViewportData(nodeIdx);
      this._model._isZoomAndPannable = (bits & 131072) != 0;
   }

   private void readEllipse(int nodeIdx) {
      this.readShapeData(nodeIdx);
      int type = this._model._nodes[nodeIdx + 1];
      if ((type & 128) != 0) {
         this._model._nodes[nodeIdx + 1] = type ^ 128;
      }

      if (type == 34) {
         this._model._nodes[nodeIdx + 23] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
         this._model._nodes[nodeIdx + 24] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      } else {
         int radius = Fixed32.toFP(this._in.readData(this._widthHeightMask));
         this._model._nodes[nodeIdx + 23] = radius;
         this._model._nodes[nodeIdx + 24] = radius;
      }
   }

   private int getFormatSize(int nodeIdx) {
      switch (this._model._nodes[nodeIdx + 1]) {
         case 1:
            return 30;
         case 3:
         case 11:
            return 18;
         case 5:
            return 14;
         case 7:
            return 5;
         case 9:
            return 12;
         case 32:
            return 22;
         case 34:
         case 162:
            return 19;
         case 36:
         case 164:
            return 19;
         case 40:
            return 21;
         case 42:
            return 22;
         case 44:
            return 23;
         case 46:
            return 21;
         case 48:
         case 176:
            return 19;
         case 50:
            return 21;
         case 64:
            return 12;
         case 66:
            return 7;
         case 68:
            return 9;
         case 70:
            return 6;
         case 72:
            return 8;
         case 74:
            return 6;
         case 76:
            return 4;
         case 96:
            return 12;
         case 97:
            return 9;
         case 98:
            return 3;
         default:
            return this._model._nodes[nodeIdx + 0];
      }
   }

   private int getSize(int type) {
      switch (type) {
         case 1:
            return 31;
         case 3:
         case 11:
            return 19;
         case 5:
            return 13;
         case 7:
            return 17;
         case 9:
            return 11;
         case 32:
            return 30;
         case 34:
         case 162:
            return 25;
         case 36:
         case 164:
            return 25;
         case 40:
            return 27;
         case 42:
            return 30;
         case 44:
            return 31;
         case 46:
            return 30;
         case 48:
         case 176:
            return 25;
         case 50:
            return 30;
         case 64:
            return 21;
         case 66:
            return 8;
         case 68:
            return 9;
         case 70:
            return 6;
         case 72:
            return 8;
         case 74:
            return 4;
         case 76:
            return 5;
         case 96:
            return 11;
         case 97:
            return 6;
         case 98:
            return 3;
         default:
            return -1;
      }
   }

   private void readRectangle(int nodeIdx) {
      this.readShapeData(nodeIdx);
      int type = this._model._nodes[nodeIdx + 1];
      if ((type & 128) != 0) {
         this._model._nodes[nodeIdx + 1] = type ^ 128;
      }

      int width = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      this._model._nodes[nodeIdx + 23] = width;
      if (type == 164) {
         this._model._nodes[nodeIdx + 24] = width;
      } else {
         this._model._nodes[nodeIdx + 24] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void readNodesArray(boolean newModel) {
      while (this._streamNextNodeIdx < this._streamNodeArraySize) {
         this.readNode(newModel);
      }

      label45:
      try {
         if (newModel) {
            this.setupNodes();
         } else {
            this.setUpNodesTree(this._visualRootIdx);
         }
      } catch (Throwable var4) {
         EventLogger.logEvent(-7509200465648525729L, me.getMessage().getBytes(), 3);
         break label45;
      }

      for (int i = 0; i < this._focusOrderUnreferenced.length; i++) {
         if (this._focusOrderUnreferenced[i] != -1) {
            this._model._focusableList[this._focusOrderIndex++] = this._focusOrderUnreferenced[i];
         }
      }
   }

   private int cloneCoords(int tempIndex) {
      this._model._coords[this._coordsIndex] = new int[this._tempCoords[tempIndex].length];
      System.arraycopy(this._tempCoords[tempIndex], 0, this._model._coords[this._coordsIndex], 0, this._tempCoords[tempIndex].length);
      this._model._finalCoords[this._coordsIndex] = new int[this._tempCoords[tempIndex].length];
      System.arraycopy(this._tempCoords[tempIndex], 0, this._model._finalCoords[this._coordsIndex], 0, this._tempCoords[tempIndex].length);
      return this._coordsIndex++;
   }

   private int clonePointTypes(int tempIndex) {
      this._model._pointTypes[this._pointTypesIndex] = new byte[this._tempPointTypes[tempIndex].length];
      System.arraycopy(this._tempPointTypes[tempIndex], 0, this._model._pointTypes[this._pointTypesIndex], 0, this._tempPointTypes[tempIndex].length);
      this._model._finalPointTypes[this._pointTypesIndex] = new byte[this._tempPointTypes[tempIndex].length];
      System.arraycopy(this._tempPointTypes[tempIndex], 0, this._model._finalPointTypes[this._pointTypesIndex], 0, this._tempPointTypes[tempIndex].length);
      return this._pointTypesIndex++;
   }

   private int readCoords() {
      int numCoords = this._in.readData(this._varDataMask);
      if (this._tempCoords[this._tempCoordsIndex] == null || this._tempCoords[this._tempCoordsIndex].length != numCoords) {
         this._tempCoords[this._tempCoordsIndex] = new int[numCoords];
      }

      this._in.readDataArray(this._tempCoords[this._tempCoordsIndex], 0, numCoords, this._coordValMask);
      return this.cloneCoords(this._tempCoordsIndex++);
   }

   private void fillOMValues(int interpIndex, int omValuesIndex) {
      int targetAttrType = this._model._nodes[interpIndex + 17];
      int vnIndex = this._model._nodes[interpIndex + 18];
      int valuesOffset = this._model.resolveAttributeOffset(vnIndex, targetAttrType);
      int size = this._model.resolveAttributeSize(targetAttrType);
      this._model._omValues[omValuesIndex] = new int[size];

      for (int i = 0; i < size; i++) {
         this._model._omValues[omValuesIndex][i] = this._model._nodes[valuesOffset + i];
      }
   }

   private void setupInterpolators() {
      this._model._keyTimes = new int[this._numInterpolators][];
      this._model._keyValues = new int[this._numInterpolators][];
      this._model._omValues = new int[this._numInterpolators][];

      for (int i = 0; i < this._numInterpolators; i++) {
         int interpIndex = this._interpolators.dequeue();
         int dur = this._durations.dequeue();
         int tempKeyTimesIndex = this._model._nodes[interpIndex + 21];
         int tempKeyValuesIndex = this._model._nodes[interpIndex + 20];
         this._model._nodes[interpIndex + 21] = this._model._nodes[interpIndex + 20] = 0;
         this.fillOMValues(interpIndex, i);
         this._model._keyTimes[i] = new int[this._tempKeyTimes[tempKeyTimesIndex].length];
         this._model._keyValues[i] = new int[this._tempKeyValues[tempKeyValuesIndex].length];

         for (int j = 0; j < this._model._keyTimes[i].length; j++) {
            long temp = (long)this._tempKeyTimes[tempKeyTimesIndex][j] * dur >> 16;
            this._model._keyTimes[i][j] = (int)temp;
         }

         for (int j = 0; j < this._model._keyValues[i].length; j++) {
            this._model._keyValues[i][j] = this._tempKeyValues[tempKeyValuesIndex][j];
         }

         this._model._nodes[interpIndex + 20] = i;
         if (this._model._nodes[interpIndex + 17] == 3 || this._model._nodes[interpIndex + 17] == 5) {
            for (int j = 0; j < this._model._keyValues[i].length; j++) {
               if (this._model._keyValues[i][j] == -1) {
                  this._model._keyValues[i][j] = Integer.MIN_VALUE;
               }
            }
         }

         if (this._model._nodes[interpIndex + 17] == 38) {
            for (int j = 0; j < this._model._keyValues[i].length; j++) {
               int unit = (this._model._keyValues[i][j] & -1073741824) >>> 30;
               switch (unit) {
                  case -1:
                     break;
                  case 0:
                  default:
                     unit = 6;
                     break;
                  case 1:
                     unit = 7;
                     break;
                  case 2:
                     unit = 0;
                     break;
                  case 3:
                     unit = 1;
               }

               int currentSize = this._model._keyValues[i][j] & 1073741823;
               this._model._keyValues[i][j] = this._platform.convertToPixels(currentSize, unit);
            }
         }
      }

      this._model.initBehaviorArrays(this._numBehaviors);
      this._tempKeyTimes = (int[][])null;
      this._tempKeyValues = (int[][])null;
   }

   private int readPointTypes() {
      int bitsOffset = -2;
      int tempData = 0;
      int tempFlag = 0;
      int numPoints = this._in.readData(this._varDataMask);
      this._tempPointTypes[this._tempPointTypesIndex] = new byte[numPoints];

      for (int j = 0; j < numPoints; j++) {
         switch (bitsOffset) {
            case -2:
               bitsOffset = 6;
               tempData = this._in.readData(0);
         }

         tempFlag = tempData >> bitsOffset & 3;
         switch (tempFlag) {
            case -1:
               break;
            case 0:
            default:
               this._tempPointTypes[this._tempPointTypesIndex][j] = 0;
               break;
            case 1:
               this._tempPointTypes[this._tempPointTypesIndex][j] = 1;
               break;
            case 2:
               this._tempPointTypes[this._tempPointTypesIndex][j] = 2;
         }

         bitsOffset -= 2;
      }

      this._platform.resolveBezierPointTypes(this._tempPointTypes[this._tempPointTypesIndex]);
      return this.clonePointTypes(this._tempPointTypesIndex++);
   }

   private void readPath(int nodeIdx) {
      this.readShapeData(nodeIdx);
      int xCoordsIdx = nodeIdx + 23;
      int yCoordsIdx = nodeIdx + 24;
      int pointTypesIdx = nodeIdx + 26;
      int offsetsIdx = nodeIdx + 25;
      int bits = this._in.readData(0);
      if ((bits & 1) != 0) {
         this._model._nodes[xCoordsIdx] = this.cloneCoords(this._in.readData(this._coordIndexMask));
      } else {
         this._model._nodes[xCoordsIdx] = this.readCoords();
      }

      if ((bits & 2) != 0) {
         this._model._nodes[yCoordsIdx] = this.cloneCoords(this._in.readData(this._coordIndexMask));
      } else {
         this._model._nodes[yCoordsIdx] = this.readCoords();
      }

      if (!this._options) {
         for (int i = 0; i < this._model._coords[this._model._nodes[xCoordsIdx]].length; i++) {
            this._model._coords[this._model._nodes[xCoordsIdx]][i] = Fixed32.toFP(this._model._coords[this._model._nodes[xCoordsIdx]][i]);
            this._model._coords[this._model._nodes[yCoordsIdx]][i] = Fixed32.toFP(this._model._coords[this._model._nodes[yCoordsIdx]][i]);
         }
      } else {
         for (int i = 0; i < this._model._coords[this._model._nodes[xCoordsIdx]].length; i++) {
            this._model._coords[this._model._nodes[xCoordsIdx]][i] = toFPn(
               (this._model._coords[this._model._nodes[xCoordsIdx]][i] << this._scaleShift) + this._offsetX, this._numFractionalBits
            );
            this._model._coords[this._model._nodes[yCoordsIdx]][i] = toFPn(
               (this._model._coords[this._model._nodes[yCoordsIdx]][i] << this._scaleShift) + this._offsetY, this._numFractionalBits
            );
         }
      }

      if ((bits & 8) != 0) {
         if ((bits & 32) != 0) {
            this._model._nodes[pointTypesIdx] = this.clonePointTypes(this._in.readData(this._coordIndexMask));
         } else {
            this._model._nodes[pointTypesIdx] = this.readPointTypes();
         }
      } else {
         this._model._nodes[pointTypesIdx] = -1;
      }

      if ((bits & 4) != 0) {
         if ((bits & 16) != 0) {
            this._model._nodes[offsetsIdx] = this.cloneCoords(this._in.readData(this._coordIndexMask));
         } else {
            this._model._nodes[offsetsIdx] = this.readCoords();
         }
      } else {
         this._model._nodes[offsetsIdx] = -1;
      }
   }

   private void readTransformAttr(int attrIdx) {
      int matrixMask = this._in.readData(2);
      int bitsOffset = 15;
      int matrixLen = 6;
      this._tempMatrix[6] = 0;
      this._tempMatrix[7] = 0;
      this._tempMatrix[8] = 65536;

      for (int j = 0; j < matrixLen; j++) {
         switch (matrixMask >> bitsOffset & 7) {
            case -1:
            case 2:
            case 3:
               break;
            case 0:
               this._tempMatrix[j] = 0;
               break;
            case 1:
               this._tempMatrix[j] = 65536;
               break;
            case 4:
            default:
               this._tempMatrix[j] = this._in.readData(4);
               break;
            case 5:
               this._tempMatrix[j] = this._in.readData(5);
               break;
            case 6:
               this._tempMatrix[j] = this._in.readData(6);
               break;
            case 7:
               this._tempMatrix[j] = this._in.readData(7);
         }

         bitsOffset -= 3;
      }

      System.arraycopy(this._tempMatrix, 0, this._model._nodes, attrIdx + 3, 9);
   }

   private void readAttrNode(int nodeIdx) {
      switch (this._model._nodes[nodeIdx + 1]) {
         case 64:
            this.readTransformAttr(nodeIdx);
            return;
         case 66:
            this.readViewboxAttr(nodeIdx);
            return;
         case 68:
            this.readTextAttr(nodeIdx);
         default:
            return;
         case 70:
            this.readFillAttr(nodeIdx);
            return;
         case 72:
            this.readStrokeAttr(nodeIdx);
            return;
         case 74:
            this.readColorPaintAttr(nodeIdx);
            return;
         case 76:
            this.readPatternPaintAttr(nodeIdx);
      }
   }

   private void readColorPaintAttr(int attrIdx) {
      int red = this._in.readData(0);
      int green = this._in.readData(0);
      int blue = this._in.readData(0);
      int color = (red & 0xFF) << 16;
      color |= (green & 0xFF) << 8;
      color |= blue & 0xFF;
      this._model._nodes[attrIdx + 3] = color;
   }

   private void setReferencesToNewNode(int runtimeIdx, int streamIdx) {
      if (this._nodeRefToUpdate != null) {
         for (int i = 0; i < this._nodeRefToUpdate.length; i += 3) {
            if (streamIdx == this._nodeRefToUpdate[i]) {
               int arrayId = this._nodeRefToUpdate[i + 1];
               int[] refarray = this.getArray(arrayId);
               if (refarray == null) {
                  throw new Object("Invalid reference array");
               }

               int linkIdx = this._nodeRefToUpdate[i + 2];
               refarray[linkIdx] = runtimeIdx;
               this._nodeRefToUpdate[i] = this._nodeRefToUpdate[i + 1] = this._nodeRefToUpdate[i + 2] = -1;
            }
         }
      }
   }

   private void readPatternPaintAttr(int attrIdx) {
      this.setNodeReference(0, attrIdx + 4, this._in.readData(this._nodeIndexMask));
      this._model._nodes[attrIdx + 3] = Integer.MAX_VALUE;
   }

   private void addNodeMapping(int runtimeIdx, int streamIdx) {
      if (this._streamNodeIndices == null) {
         this._streamNodeIndices = new int[10];
         this._runtimeNodeIndices = new int[10];
      }

      if (this._streamNodeIndices.length == this._nodeMapSize) {
         int[] array = new int[this._nodeMapSize + 10];
         System.arraycopy(this._streamNodeIndices, 0, array, 0, this._streamNodeIndices.length);
         this._streamNodeIndices = array;
         array = new int[this._nodeMapSize + 10];
         System.arraycopy(this._runtimeNodeIndices, 0, array, 0, this._runtimeNodeIndices.length);
         this._runtimeNodeIndices = array;
      }

      this._streamNodeIndices[this._nodeMapSize] = streamIdx;
      this._runtimeNodeIndices[this._nodeMapSize] = runtimeIdx;
      this._nodeMapSize++;
   }

   private int setNodeReference(int arrayId, int ref, int streamIndex) {
      int[] refArray = this.getArray(arrayId);
      if (refArray == null) {
         throw new Object("Invalid reference array");
      }

      int index = this.resolveReference(streamIndex);
      if (index != -1) {
         refArray[ref] = index;
      } else {
         refArray[ref] = -1;
         if (this._nodeRefToUpdate == null) {
            this._nodeRefToUpdate = new int[10];
         }

         if (this._nodeRefToUpdate.length <= this._nodeRefToUpdateSize + 2) {
            int[] array = new int[this._nodeRefToUpdateSize + 10];
            System.arraycopy(this._nodeRefToUpdate, 0, array, 0, this._nodeRefToUpdate.length);
            this._nodeRefToUpdate = array;
         }

         this._nodeRefToUpdate[this._nodeRefToUpdateSize++] = streamIndex;
         this._nodeRefToUpdate[this._nodeRefToUpdateSize++] = arrayId;
         this._nodeRefToUpdate[this._nodeRefToUpdateSize++] = ref;
      }

      return index;
   }

   private int[] getArray(int arrayId) {
      switch (arrayId) {
         case -1:
            return null;
         case 0:
         default:
            return this._model._nodes;
         case 1:
            return this._model._triggers;
      }
   }

   private int resolveReference(int formatIdx) {
      int low = 0;
      int high = this._nodeMapSize - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         if (formatIdx < this._streamNodeIndices[mid]) {
            high = mid - 1;
         } else {
            if (formatIdx <= this._streamNodeIndices[mid]) {
               return this._runtimeNodeIndices[mid];
            }

            low = mid + 1;
         }
      }

      return this._streamNextNodeIdx > formatIdx ? formatIdx : -1;
   }

   private void readFillAttr(int attrIdx) {
      int bits = this.readBits();
      this._model._nodes[attrIdx + 3] = bits;
      if ((bits & 2) != 0) {
         this.setNodeReference(0, attrIdx + 4, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[attrIdx + 4] = -1;
      }

      if ((bits & 4) != 0) {
         this._model._nodes[attrIdx + 5] = this._in.readData(0);
      }
   }

   private void readStrokeAttr(int attrIdx) {
      int bits = this.readBits();
      this._model._nodes[attrIdx + 3] = bits;
      if ((bits & 2) != 0) {
         this.setNodeReference(0, attrIdx + 4, this._in.readData(this._attrIndexMask));
      } else {
         this._model._nodes[attrIdx + 4] = -1;
      }

      if ((bits & 4) != 0) {
         this._model._nodes[attrIdx + 5] = this._in.readData(0);
      }

      if ((bits & 8) != 0) {
         this._model._nodes[attrIdx + 6] = Fixed32.toFP(this._in.readData(this._widthHeightMask));
      }

      if ((bits & 16) != 0 || (bits & 32) != 0) {
         this._model._nodes[attrIdx + 7] = this._in.readData(0);
      }
   }

   private void readTextAttr(int attrIdx) {
      int bits = this.readBits();
      this._model._nodes[attrIdx + 3] = bits;
      if ((bits & 64) != 0) {
         this._model._nodes[attrIdx + 7] = this._in.readData(this._objIndexMask);
      } else {
         this._model._nodes[attrIdx + 7] = -1;
      }

      if ((bits & 32) != 0) {
         this._model._nodes[attrIdx + 4] = this._in.readData(0) << 16;
      } else {
         this._model._nodes[attrIdx + 4] = Integer.MAX_VALUE;
      }

      if ((bits & 16) != 0) {
         int formatWeight = this._in.readData(0);
         int modelWeight = -1;
         short var9;
         switch (formatWeight) {
            case 0:
               throw new Object(((StringBuffer)(new Object("Unsupported format value for font-weight: "))).append(formatWeight).toString());
            case 1:
               var9 = 400;
               break;
            case 2:
            default:
               var9 = 700;
         }

         this._model._nodes[attrIdx + 5] = var9;
      } else {
         this._model._nodes[attrIdx + 5] = Integer.MAX_VALUE;
      }

      if ((bits & 2) != 0) {
         int formatStyle = this._in.readData(0);
         int modelStyle = -1;
         byte var11;
         switch (formatStyle) {
            case 0:
               throw new Object(((StringBuffer)(new Object("Unsupported format value for font-style: "))).append(formatStyle).toString());
            case 1:
               var11 = 1;
               break;
            case 2:
            default:
               var11 = 2;
               break;
            case 3:
               var11 = 3;
         }

         this._model._nodes[attrIdx + 6] = var11;
      } else {
         this._model._nodes[attrIdx + 6] = Integer.MAX_VALUE;
      }

      if ((bits & 8) != 0) {
         int formatDecoration = this._in.readData(0);
         int modelDecoration = -1;
         byte var13;
         switch (formatDecoration) {
            case 0:
               throw new Object(((StringBuffer)(new Object("Unsupported format text decoration value: "))).append(formatDecoration).toString());
            case 1:
               var13 = 1;
               break;
            case 2:
               var13 = 2;
               break;
            case 3:
               var13 = 3;
               break;
            case 4:
               var13 = 4;
               break;
            case 5:
            default:
               var13 = 5;
         }

         this._model._nodes[attrIdx + 8] = var13;
      } else {
         this._model._nodes[attrIdx + 8] = Integer.MAX_VALUE;
      }

      if ((bits & 4) != 0) {
         int formatUnits = this._in.readData(0);
         int meUnits = -1;
         byte var15;
         switch (formatUnits) {
            case -1:
               throw new Object(((StringBuffer)(new Object("Unsupported format units value: "))).append(formatUnits).toString());
            case 0:
               var15 = 0;
               break;
            case 1:
               var15 = 1;
               break;
            case 2:
               var15 = 2;
               break;
            case 3:
               var15 = 3;
               break;
            case 4:
               var15 = 4;
               break;
            case 5:
               var15 = 5;
               break;
            case 6:
            default:
               var15 = 6;
               break;
            case 7:
               var15 = 7;
               break;
            case 8:
               var15 = 8;
               break;
            case 9:
               var15 = 9;
         }

         if (var15 != 0) {
            int currSize = this._model._nodes[attrIdx + 4];
            this._model._nodes[attrIdx + 4] = this._platform.convertToPixels(currSize, var15);
         }
      }
   }

   private void loadResources() {
      this.loadResources(this._model._mediaObjectURL, this._model._media, 0, "audio/midi");
      this.loadResources(this._model._imageURL, this._model._images, this._imageIndexOffset, "image/png");
      this.loadFonts();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void loadFonts() {
      if (this._resourceProvider != null && this._model._fontHandles != null) {
         for (int i = this._fontHandlesOffset; i < this._model._fontHandles.length; i++) {
            if (this._loadAborted) {
               return;
            }

            int nodeIdx = this._model._fontHandles[i];
            int fontURLOffset = this._model._nodes[nodeIdx + 4];
            int fontFamilyOffset = this._model._nodes[nodeIdx + 3];

            try {
               String platformFontFamilyName = this._model._fontFamilyStrings[fontFamilyOffset];
               String uniqueKey = Integer.toHexString(this._model.hashCode()).substring(0, 4);
               platformFontFamilyName = ((StringBuffer)(new Object())).append(platformFontFamilyName).append("_pme").append(uniqueKey).toString();
               this._context.set("FontFamily", platformFontFamilyName);
               Integer fontHandle = (Integer)this._resourceProvider
                  .createResourceFromURI(this._model._fontUrlStrings[fontURLOffset], "font", this._context, null);
               if (fontHandle != null) {
                  if ((fontHandle & -65536) == -131072) {
                     int existingHandle = fontHandle & 65535;
                     platformFontFamilyName = this._platform.getFontName(existingHandle);
                     this._context.set("FontFamily", platformFontFamilyName);
                     fontHandle = (Integer)this._resourceProvider
                        .createResourceFromURI(this._model._fontUrlStrings[fontURLOffset], "font", this._context, null);
                     this._model._nodes[nodeIdx + 5] = fontHandle;
                  } else {
                     this._model._nodes[nodeIdx + 5] = fontHandle;
                  }
               }

               this._model._platformFontFamilyStrings[fontFamilyOffset] = platformFontFamilyName;
            } catch (Throwable var10) {
               this._platform.logDebug(this, 22, -1, e);
               if (e instanceof Object && ((MediaException)e).getCode() == 5) {
                  this.abortLoad();
               } else {
                  this._model.missingURLs = ((StringBuffer)(new Object()))
                     .append(this._model.missingURLs)
                     .append(this._model._fontUrlStrings[fontURLOffset])
                     .toString();
                  this._model.missingURLs = ((StringBuffer)(new Object())).append(this._model.missingURLs).append("\n").toString();
                  this._model._nodes[nodeIdx + 5] = -1;
               }
               continue;
            }
         }
      }
   }

   private void loadResources(String[] param1, Object[] param2, int param3, String param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._resourceProvider Lnet/rim/plazmic/internal/mediaengine/ResourceProvider;
      // 004: ifnonnull 00a
      // 007: goto 1ae
      // 00a: aload 1
      // 00b: ifnonnull 011
      // 00e: goto 1ae
      // 011: aload 1
      // 012: arraylength
      // 013: istore 5
      // 015: iload 3
      // 016: istore 6
      // 018: aload 4
      // 01a: ldc_w "image/png"
      // 01d: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 020: istore 7
      // 022: aload 0
      // 023: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._loadAborted Z
      // 026: ifeq 02c
      // 029: goto 1ae
      // 02c: iload 6
      // 02e: iload 5
      // 030: if_icmplt 036
      // 033: goto 1ae
      // 036: iload 7
      // 038: ifne 03e
      // 03b: goto 0ec
      // 03e: aload 0
      // 03f: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._context Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 042: ldc_w "Handle"
      // 045: new java/lang/Object
      // 048: dup
      // 049: aload 0
      // 04a: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._tempImageNodeIdx [I
      // 04d: iload 6
      // 04f: aload 0
      // 050: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._imageIndexOffset I
      // 053: isub
      // 054: iaload
      // 055: invokespecial java/lang/Integer.<init> (I)V
      // 058: invokevirtual net/rim/plazmic/internal/mediaengine/ResourceContext.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 05b: aload 2
      // 05c: iload 6
      // 05e: aload 0
      // 05f: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._resourceProvider Lnet/rim/plazmic/internal/mediaengine/ResourceProvider;
      // 062: aload 1
      // 063: iload 6
      // 065: aaload
      // 066: aload 4
      // 068: aload 0
      // 069: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._context Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 06c: aconst_null
      // 06d: invokeinterface net/rim/plazmic/internal/mediaengine/ResourceProvider.createResourceFromURI (Ljava/lang/String;Ljava/lang/String;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object; 5
      // 072: aastore
      // 073: aload 2
      // 074: iload 6
      // 076: aaload
      // 077: dup
      // 078: instanceof java/lang/Object
      // 07b: ifne 082
      // 07e: pop
      // 07f: goto 0a8
      // 082: checkcast java/lang/Object
      // 085: astore 8
      // 087: aload 8
      // 089: aload 0
      // 08a: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._model Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 08d: invokeinterface net/rim/plazmic/internal/mediaengine/ui/ForeignObject.setPeer (Lnet/rim/plazmic/internal/mediaengine/ui/ForeignObjectPeer;)V 2
      // 092: aload 8
      // 094: aload 0
      // 095: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._tempImageNodeIdx [I
      // 098: iload 6
      // 09a: aload 0
      // 09b: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._imageIndexOffset I
      // 09e: isub
      // 09f: iaload
      // 0a0: invokeinterface net/rim/plazmic/internal/mediaengine/ui/ForeignObject.setHandle (I)V 2
      // 0a5: goto 1a8
      // 0a8: aload 2
      // 0a9: iload 6
      // 0ab: aaload
      // 0ac: instanceof java/lang/Object
      // 0af: ifne 0b5
      // 0b2: goto 1a8
      // 0b5: new net/rim/plazmic/internal/mediaengine/ui/ImageForeignObject
      // 0b8: dup
      // 0b9: aload 2
      // 0ba: iload 6
      // 0bc: aaload
      // 0bd: checkcast net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel
      // 0c0: invokespecial net/rim/plazmic/internal/mediaengine/ui/ImageForeignObject.<init> (Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel;)V
      // 0c3: astore 8
      // 0c5: aload 8
      // 0c7: aload 0
      // 0c8: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._model Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 0cb: invokeinterface net/rim/plazmic/internal/mediaengine/ui/ForeignObject.setPeer (Lnet/rim/plazmic/internal/mediaengine/ui/ForeignObjectPeer;)V 2
      // 0d0: aload 8
      // 0d2: aload 0
      // 0d3: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._tempImageNodeIdx [I
      // 0d6: iload 6
      // 0d8: aload 0
      // 0d9: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._imageIndexOffset I
      // 0dc: isub
      // 0dd: iaload
      // 0de: invokeinterface net/rim/plazmic/internal/mediaengine/ui/ForeignObject.setHandle (I)V 2
      // 0e3: aload 2
      // 0e4: iload 6
      // 0e6: aload 8
      // 0e8: aastore
      // 0e9: goto 1a8
      // 0ec: aload 2
      // 0ed: iload 6
      // 0ef: aload 0
      // 0f0: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._resourceProvider Lnet/rim/plazmic/internal/mediaengine/ResourceProvider;
      // 0f3: aload 1
      // 0f4: iload 6
      // 0f6: aaload
      // 0f7: aload 4
      // 0f9: aload 0
      // 0fa: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._context Lnet/rim/plazmic/internal/mediaengine/ResourceContext;
      // 0fd: aconst_null
      // 0fe: invokeinterface net/rim/plazmic/internal/mediaengine/ResourceProvider.createResourceFromURI (Ljava/lang/String;Ljava/lang/String;Lnet/rim/plazmic/internal/mediaengine/ResourceContext;Ljava/lang/Object;)Ljava/lang/Object; 5
      // 103: aastore
      // 104: goto 1a8
      // 107: astore 8
      // 109: invokestatic net/rim/device/api/system/BackdoorKeyProcessor.isDevelopmentDevice ()Z
      // 10c: ifne 112
      // 10f: goto 1a8
      // 112: ldc2_w -7509200465648525729
      // 115: new java/lang/Object
      // 118: dup
      // 119: ldc_w "PME12: Failed to load: "
      // 11c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 11f: aload 1
      // 120: iload 6
      // 122: aaload
      // 123: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 126: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 129: invokevirtual java/lang/String.getBytes ()[B
      // 12c: bipush 3
      // 12e: invokestatic net/rim/device/api/system/EventLogger.logEvent (J[BI)Z
      // 131: pop
      // 132: goto 1a8
      // 135: astore 8
      // 137: aload 0
      // 138: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._platform Lnet/rim/plazmic/internal/mediaengine/util/Platform;
      // 13b: aload 0
      // 13c: bipush 22
      // 13e: bipush -1
      // 140: aload 8
      // 142: invokeinterface net/rim/plazmic/internal/mediaengine/util/Platform.logDebug (Ljava/lang/Object;IILjava/lang/Object;)V 5
      // 147: aload 8
      // 149: dup
      // 14a: instanceof java/lang/Object
      // 14d: ifne 154
      // 150: pop
      // 151: goto 166
      // 154: checkcast java/lang/Object
      // 157: invokevirtual net/rim/plazmic/mediaengine/MediaException.getCode ()I
      // 15a: bipush 5
      // 15c: if_icmpne 166
      // 15f: aload 0
      // 160: invokespecial net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader.abortLoad ()V
      // 163: goto 1a8
      // 166: new java/lang/Object
      // 169: dup
      // 16a: invokespecial java/lang/StringBuffer.<init> ()V
      // 16d: aload 0
      // 16e: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._model Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 171: dup_x1
      // 172: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel.missingURLs Ljava/lang/String;
      // 175: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 178: aload 1
      // 179: iload 6
      // 17b: aaload
      // 17c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 17f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 182: putfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel.missingURLs Ljava/lang/String;
      // 185: new java/lang/Object
      // 188: dup
      // 189: invokespecial java/lang/StringBuffer.<init> ()V
      // 18c: aload 0
      // 18d: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/PME_Reader._model Lnet/rim/plazmic/internal/mediaengine/model/intarray/v1_2/ModelInteractorImpl;
      // 190: dup_x1
      // 191: getfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel.missingURLs Ljava/lang/String;
      // 194: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 197: ldc_w "\n"
      // 19a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 19d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1a0: putfield net/rim/plazmic/internal/mediaengine/model/intarray/v1_2/AnimationModel.missingURLs Ljava/lang/String;
      // 1a3: aload 2
      // 1a4: iload 6
      // 1a6: aconst_null
      // 1a7: aastore
      // 1a8: iinc 6 1
      // 1ab: goto 022
      // 1ae: return
      // try (24 -> 123): 124 null
      // try (24 -> 123): 143 null
   }

   private void abortLoad() {
      this._loadAborted = true;
      this._model.missingURLs = "";
   }

   public PME_Reader() {
      this._in = new PMEInputStream();
      this._durations = new MediaQueue();
      this._interpolators = new MediaQueue();
      this._focusOrderHashtable = (IntIntHashtable)(new Object());
   }

   private static final int toFPn(int i, int n) {
      return i << n;
   }
}
