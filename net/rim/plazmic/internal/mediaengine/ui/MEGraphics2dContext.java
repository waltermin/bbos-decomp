package net.rim.plazmic.internal.mediaengine.ui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.ui.XYRect;

public final class MEGraphics2dContext {
   MEGraphics2dContext _parent;
   int _fillPaint = Integer.MAX_VALUE;
   Object _fillTexture;
   XYRect _fillTextureDimensions;
   int[] _fillTextureTransformMatrix = VecMath.IDENTITY_3X3;
   int _fillTextureTransformMatrixIndex;
   int[] _computedFillTextureMatrix;
   boolean _dirtyFillTextureTransform;
   int _fillColor = 0;
   int _fillOpacity = Integer.MAX_VALUE;
   int _strokePaint = Integer.MAX_VALUE;
   int _strokeColor = 0;
   int _strokeOpacity = Integer.MAX_VALUE;
   int _strokeLinecap = Integer.MAX_VALUE;
   int _strokeLinejoin = Integer.MAX_VALUE;
   int _strokeMiterlimit = Integer.MAX_VALUE;
   int _strokeWidth = Integer.MAX_VALUE;
   int _objectOpacity = 255;
   int _primitiveOpacity = Integer.MAX_VALUE;
   XYRect _clipRect;
   XYRect _computedClip = new XYRect();
   XYRect _computedIntersectClip = new XYRect();
   int _clipXUnit;
   int _clipYUnit;
   int _clipWidthUnit;
   int _clipHeightUnit;
   boolean _dirtyComputedClipWidth = true;
   boolean _changedClipWidth = false;
   boolean _dirtyComputedClipHeight = true;
   boolean _changedClipHeight = false;
   boolean _computedIntersectClipValid = false;
   int _shapeRenderingHints = Integer.MAX_VALUE;
   int _imageRenderingHints = Integer.MAX_VALUE;
   int _textRenderingHints = Integer.MAX_VALUE;
   int[] _transformMatrix = VecMath.IDENTITY_3X3;
   int _transformMatrixIndex = 0;
   int[] _computedMatrix = new int[9];
   private boolean _transformValid;
   int[] _viewboxClippingMatrix;
   int _aspectRatio = 10;
   boolean _isViewbox = false;
   int _x = Integer.MAX_VALUE;
   int _y = Integer.MAX_VALUE;
   int _width = Integer.MAX_VALUE;
   public int computedWidth = 0;
   int _widthUnit = 0;
   int _height = Integer.MAX_VALUE;
   public int computedHeight = 0;
   int _heightUnit = 0;
   boolean _dirtyComputedWidth = true;
   boolean _changedWidth = false;
   boolean _dirtyComputedHeight = true;
   boolean _changedHeight = false;
   String _fontFamilyName = "inherit";
   int _fontPixelSize = Integer.MAX_VALUE;
   int _fontWeight = Integer.MAX_VALUE;
   int _fontStyle = Integer.MAX_VALUE;
   int _textAnchor = Integer.MAX_VALUE;
   int _textDecoration = Integer.MAX_VALUE;
   int _visibility = Integer.MAX_VALUE;
   int _dx = Integer.MAX_VALUE;
   int _dy = Integer.MAX_VALUE;
   int _currentTextPosX = 0;
   int _currentTextPosY = 0;
   boolean _dirtyViewBox = true;
   private int _dirtyBits;
   private int _dirtyOR;
   boolean _dirtyORValid = false;
   public XYRect _boundingBox = new XYRect();
   public XYRect _boundingBoxTransformed = new XYRect();
   public XYRect _dirtyRect = new XYRect();
   public XYRect _dirtyRectTransformed = new XYRect();
   public int _idx;
   public int _type;
   public Object _image;
   public int[] _coordsX;
   public int[] _coordsY;
   public byte[] _coordsType;
   public int[] _coordsOffset;
   public int[] _coordsColour;
   public StringBuffer _textBuffer;
   public int _textStartIndex;
   public int _textLength;
   public int[] _finalCoordsX;
   public int[] _finalCoordsY;
   private boolean _displayable = true;
   public static final int NONE = Integer.MIN_VALUE;
   public static final String NONE_STRING = "none";
   public static final int INHERIT = Integer.MAX_VALUE;
   public static final String INHERIT_STRING = "inherit";
   static int[] _tempPoint = new int[2];
   static int[] _tempXPoints = new int[4];
   static int[] _transformedXPoints = new int[4];
   static int[] _tempYPoints = new int[4];
   static int[] _transformedYPoints = new int[4];

   public final boolean displayable() {
      return this._displayable;
   }

   public final void setDisplayable(boolean displayable) {
      this._displayable = displayable;
   }

   public MEGraphics2dContext() {
      this._viewboxClippingMatrix = this._viewboxClippingMatrix;
      _tempXPoints = _tempXPoints;
      _transformedXPoints = _transformedXPoints;
      _tempYPoints = _tempYPoints;
      _transformedYPoints = _transformedYPoints;
      _tempPoint = _tempPoint;
   }

   public final void setParent(MEGraphics2dContext parent) {
      if (this._parent != parent) {
         this._dirtyComputedClipHeight = true;
         this._dirtyComputedClipWidth = true;
         this._dirtyComputedHeight = true;
         this._dirtyComputedWidth = true;
         this._dirtyViewBox = true;
         this._parent = parent;
      }

      this._changedClipHeight = false;
      this._changedClipWidth = false;
      this._changedWidth = false;
      this._changedHeight = false;
      this._computedIntersectClipValid = false;
      this._transformValid = false;
      this._dirtyORValid = false;
   }

   public final MEGraphics2dContext getParent() {
      return this._parent;
   }

   public final void setVisibility(int visibility) {
      this._visibility = visibility;
   }

   public final int getVisibility() {
      int result = this._visibility;

      for (MEGraphics2dContext parent = this._parent; result == Integer.MAX_VALUE; parent = parent._parent) {
         result = parent._visibility;
      }

      return result;
   }

   public final void setX(int x) {
      this._x = x;
   }

   public final int getX() {
      int result = this._x;
      MEGraphics2dContext parent = this._parent;

      while (result == Integer.MAX_VALUE) {
         if (parent != null) {
            result = parent._x;
            parent = parent._parent;
         } else {
            result = 0;
         }
      }

      return result;
   }

   public final void setY(int y) {
      this._y = y;
   }

   public final int getY() {
      int result = this._y;
      MEGraphics2dContext parent = this._parent;

      while (result == Integer.MAX_VALUE) {
         if (parent != null) {
            result = parent._y;
            parent = parent._parent;
         } else {
            result = 0;
         }
      }

      return result;
   }

   public final void setDx(int dx) {
      this._dx = dx;
   }

   public final int getDx() {
      int result = this._dx;

      for (MEGraphics2dContext parent = this._parent; result == Integer.MAX_VALUE; parent = parent._parent) {
         result = parent._dx;
      }

      return result;
   }

   public final void setDy(int dy) {
      this._dy = dy;
   }

   public final int getDy() {
      int result = this._dy;

      for (MEGraphics2dContext parent = this._parent; result == Integer.MAX_VALUE; parent = parent._parent) {
         result = parent._dy;
      }

      return result;
   }

   public final void setCurrentTextPosX(int x) {
      this._currentTextPosX = x;
   }

   public final int getCurrentTextPosX() {
      return this._currentTextPosX;
   }

   public final void setCurrentTextPosY(int y) {
      this._currentTextPosY = y;
   }

   public final int getCurrentTextPosY() {
      return this._currentTextPosY;
   }

   public final void setWidth(int width, int unit) {
      if (width != this._width || unit != this._widthUnit) {
         this._width = width;
         this._widthUnit = unit;
         this._dirtyComputedWidth = true;
         this._changedWidth = false;
      }
   }

   public final int getWidth() {
      this.calculateWidth();
      return this.computedWidth;
   }

   public final void setHeight(int height, int unit) {
      if (height != this._height || unit != this._heightUnit) {
         this._height = height;
         this._heightUnit = unit;
         this._dirtyComputedHeight = true;
         this._changedHeight = false;
      }
   }

   public final int getHeight() {
      this.calculateHeight();
      return this.computedHeight;
   }

   public final void setFillPaint(int type) {
      this._fillPaint = type;
   }

   public final int getFillPaint() {
      return this._fillPaint;
   }

   public final MEGraphics2dContext resolveFillPaintInheritance() {
      MEGraphics2dContext result = this;

      while (result._fillPaint == Integer.MAX_VALUE) {
         result = result._parent;
      }

      return result;
   }

   public final void setFillTexture(Object texture) {
      this._fillTexture = texture;
   }

   public final Object getFillTexture() {
      return this._fillTexture;
   }

   public final void setFillTextureDimensions(int x, int y, int width, int height) {
      if (this._fillTextureDimensions == null) {
         this._fillTextureDimensions = new XYRect(x, y, width, height);
      } else {
         this._fillTextureDimensions.x = x;
         this._fillTextureDimensions.y = y;
         this._fillTextureDimensions.width = width;
         this._fillTextureDimensions.height = height;
      }
   }

   public final XYRect getFillTextureDimensions() {
      return this._fillTextureDimensions;
   }

   public final void setFillColor(int color) {
      this._fillColor = color;
   }

   public final int getFillColor() {
      return this._fillColor;
   }

   public final void setFillTextureTransformMatrix(int[] matrix, int index) {
      this._dirtyFillTextureTransform = true;
      this._fillTextureTransformMatrix = matrix;
      this._fillTextureTransformMatrixIndex = index;
   }

   public final int[] getFillTextureTransformMatrix() {
      return this._fillTextureTransformMatrix;
   }

   public final int getFillTextureTransformMatrixIndex() {
      return this._fillTextureTransformMatrixIndex;
   }

   public final int[] getCurrentFillTextureTransformMatrix() {
      MEGraphics2dContext fillPaintContext = this.resolveFillPaintInheritance();
      int[] textureTransformMatrix = fillPaintContext.getFillTextureTransformMatrix();
      int textureTransformMatrixIndex = fillPaintContext.getFillTextureTransformMatrixIndex();
      int[] finalMatrix = this.getCurrentTransformMatrix();
      if (this._computedFillTextureMatrix == null) {
         this._computedFillTextureMatrix = new int[9];
      }

      if (VecMath.isIdentity(textureTransformMatrix, textureTransformMatrixIndex)) {
         System.arraycopy(finalMatrix, 0, this._computedFillTextureMatrix, 0, 9);
      } else if (fillPaintContext._dirtyFillTextureTransform) {
         VecMath.multiply3x3Affine(finalMatrix, 0, textureTransformMatrix, textureTransformMatrixIndex, this._computedFillTextureMatrix, 0);
         fillPaintContext._dirtyFillTextureTransform = false;
      }

      return this._computedFillTextureMatrix;
   }

   public final void setFillOpacity(int opacity) {
      if (opacity > 255) {
         opacity = 255;
      } else if (opacity < 0) {
         opacity = 0;
      }

      this._fillOpacity = opacity;
   }

   public final int getFillOpacity() {
      int result = this._fillOpacity;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._fillOpacity) {
         next = next._parent;
      }

      return result;
   }

   public final void setStrokePaint(int type) {
      this._strokePaint = type;
   }

   public final int getStrokePaint() {
      return this._strokePaint;
   }

   public final MEGraphics2dContext resolveStrokePaintInheritance() {
      MEGraphics2dContext result = this;

      while (result._strokePaint == Integer.MAX_VALUE) {
         result = result._parent;
      }

      return result;
   }

   public final void setStrokeColor(int color) {
      this._strokeColor = color;
   }

   public final int getStrokeColor() {
      return this._strokeColor;
   }

   public final void setStrokeOpacity(int opacity) {
      if (opacity > 255) {
         opacity = 255;
      } else if (opacity < 0) {
         opacity = 0;
      }

      this._strokeOpacity = opacity;
   }

   public final int getStrokeOpacity() {
      int result = this._strokeOpacity;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._strokeOpacity) {
         next = next._parent;
      }

      return result;
   }

   public final void setStrokeLinecap(int style) {
      this._strokeLinecap = style;
   }

   public final int getStrokeLinecap() {
      int result = this._strokeLinecap;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._strokeLinecap) {
         next = next._parent;
      }

      return result;
   }

   public final void setStrokeLinejoin(int style) {
      this._strokeLinejoin = style;
   }

   public final int getStrokeLinejoin() {
      int result = this._strokeLinejoin;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._strokeLinejoin) {
         next = next._parent;
      }

      return result;
   }

   public final void setStrokeMiterlimit(int limit) {
      this._strokeMiterlimit = limit;
   }

   public final void setStrokeWidth(int width) {
      this._strokeWidth = width;
   }

   public final int getStrokeWidth() {
      int result = this._strokeWidth;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._strokeWidth) {
         next = next._parent;
      }

      return Math.max(0, result);
   }

   public final void setTransformMatrix(int[] matrix, int index) {
      this._transformMatrix = matrix;
      this._transformMatrixIndex = index;
      System.arraycopy(matrix, index, this._computedMatrix, 0, 9);
      this._transformValid = false;
   }

   public final int[] getCurrentTransformMatrix() {
      this.updateTransformMatrix();
      return this._computedMatrix;
   }

   public final void setObjectOpacity(int opacity) {
      if (opacity > 255) {
         opacity = 255;
      } else if (opacity < 0) {
         opacity = 0;
      }

      this._objectOpacity = opacity;
   }

   public final int getObjectOpacity() {
      if (this._primitiveOpacity != Integer.MAX_VALUE) {
         return this._primitiveOpacity;
      }

      MEGraphics2dContext parent = this._parent;
      int result = this._objectOpacity;

      while (parent != null) {
         if (parent._objectOpacity < 255) {
            result = result * parent._objectOpacity / 255;
         }

         parent = parent._parent;
      }

      return result;
   }

   public final void setClip(XYRect clipRect) {
      this.setClip(clipRect.x, 0, clipRect.y, 0, clipRect.width, 0, clipRect.height, 0);
   }

   public final void setClip(int x, int y, int width, int height) {
      this.setClip(x, 0, y, 0, width, 0, height, 0);
   }

   public final void setClip(int x, int xUnit, int y, int yUnit, int width, int widthUnit, int height, int heightUnit) {
      if (this._clipRect == null) {
         this._clipRect = new XYRect(x, y, width, height);
      }

      this._clipRect.x = x;
      this._clipRect.y = y;
      this._clipRect.width = width;
      this._clipRect.height = height;
      this._clipXUnit = xUnit;
      this._clipYUnit = yUnit;
      this._clipWidthUnit = widthUnit;
      this._clipHeightUnit = heightUnit;
      this._dirtyComputedClipWidth = true;
      this._dirtyComputedClipHeight = true;
      this._changedClipWidth = false;
      this._changedClipHeight = false;
      this._computedIntersectClipValid = false;
   }

   public final int getClipWidth() {
      this.calculateClipWidth();
      return this._computedClip.width;
   }

   public final int getClipHeight() {
      this.calculateClipHeight();
      return this._computedClip.height;
   }

   public final XYRect getClip() {
      if (this._computedIntersectClipValid) {
         return this._computedIntersectClip;
      }

      if (this._parent == null) {
         this._computedIntersectClip.set(this._clipRect);
      } else if (this._clipRect == null) {
         this._computedIntersectClip.set(this._parent.getClip());
      } else {
         int[] matrix = this._parent._parent == null ? this._parent.getCurrentTransformMatrix() : this.getCurrentTransformMatrix();
         int width = this.getClipWidth();
         _tempXPoints[0] = this._clipRect.x;
         _tempXPoints[1] = this._clipRect.x + width;
         _tempXPoints[2] = this._clipRect.x + width;
         _tempXPoints[3] = this._clipRect.x;
         int height = this.getClipHeight();
         _tempYPoints[0] = this._clipRect.y;
         _tempYPoints[1] = this._clipRect.y;
         _tempYPoints[2] = this._clipRect.y + height;
         _tempYPoints[3] = this._clipRect.y + height;
         VecMath.transformPoints32(matrix, 0, _tempXPoints, _tempYPoints, _transformedXPoints, _transformedYPoints);
         this._computedIntersectClip.x = Math.min(
            Math.min(_transformedXPoints[0], _transformedXPoints[1]), Math.min(_transformedXPoints[2], _transformedXPoints[3])
         );
         this._computedIntersectClip.width = Math.max(
            Math.max(_transformedXPoints[0], _transformedXPoints[1]), Math.max(_transformedXPoints[2], _transformedXPoints[3])
         );
         this._computedIntersectClip.width = this._computedIntersectClip.width - this._computedIntersectClip.x;
         this._computedIntersectClip.y = Math.min(
            Math.min(_transformedYPoints[0], _transformedYPoints[1]), Math.min(_transformedYPoints[2], _transformedYPoints[3])
         );
         this._computedIntersectClip.height = Math.max(
            Math.max(_transformedYPoints[0], _transformedYPoints[1]), Math.max(_transformedYPoints[2], _transformedYPoints[3])
         );
         this._computedIntersectClip.height = this._computedIntersectClip.height - this._computedIntersectClip.y;
         this._computedIntersectClip.intersect(this._parent.getClip());
      }

      this._computedIntersectClipValid = true;
      return this._computedIntersectClip;
   }

   public final void invalidateClip() {
      for (MEGraphics2dContext next = this; next != null; next = next._parent) {
         next._computedIntersectClipValid = false;
      }
   }

   public final void setShapeRenderingHints(int hints) {
      this._shapeRenderingHints = hints;
   }

   public final int getShapeRenderingHints() {
      int result = this._shapeRenderingHints;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._shapeRenderingHints) {
         next = next._parent;
      }

      return result;
   }

   public final void setImageRenderingHints(int hints) {
      this._imageRenderingHints = hints;
   }

   public final void setTextRenderingHints(int hints) {
      this._textRenderingHints = hints;
   }

   public final int getTextRenderingHints() {
      int result = this._textRenderingHints;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._textRenderingHints) {
         next = next._parent;
      }

      return result;
   }

   private final void calculateViewBoxMatrix(
      int scaleFactorX, int scaleFactorY, int xMin, int yMin, int preserveAspectRatio, int[] viewBoxMatrix, int viewBoxMatrixStartIndex
   ) {
      if (preserveAspectRatio != 0) {
         int factor = (preserveAspectRatio & 128) != 0 ? Math.max(scaleFactorX, scaleFactorY) : Math.min(scaleFactorX, scaleFactorY);
         viewBoxMatrix[viewBoxMatrixStartIndex] = factor;
         viewBoxMatrix[viewBoxMatrixStartIndex + 4] = factor;
      } else {
         viewBoxMatrix[viewBoxMatrixStartIndex] = scaleFactorX;
         viewBoxMatrix[viewBoxMatrixStartIndex + 4] = scaleFactorY;
      }

      viewBoxMatrix[viewBoxMatrixStartIndex + 2] = Fixed32.mul(viewBoxMatrix[viewBoxMatrixStartIndex], -xMin);
      viewBoxMatrix[viewBoxMatrixStartIndex + 5] = Fixed32.mul(viewBoxMatrix[viewBoxMatrixStartIndex + 4], -yMin);
      viewBoxMatrix[viewBoxMatrixStartIndex + 8] = 65536;
   }

   private final void calculateViewBoxOriginOffset(
      int x, int y, int width, int height, int scaledViewBoxWidth, int scaledViewBoxHeight, int preserveAspectRatio, int[] origin, int originStartIndex
   ) {
      origin[originStartIndex++] = this.calculateViewBoxOriginCoord(x, width, scaledViewBoxWidth, preserveAspectRatio, 2);
      origin[originStartIndex] = this.calculateViewBoxOriginCoord(y, height, scaledViewBoxHeight, preserveAspectRatio, 0);
   }

   private final int calculateViewBoxOriginCoord(int origin, int viewBoxDimension, int contentDimension, int preserveAspectRatio, int styleShift) {
      int originOffset = origin;
      int style = (preserveAspectRatio & 3 << styleShift) >>> styleShift;
      switch (style) {
         case 1:
            break;
         case 2:
         default:
            originOffset += viewBoxDimension - contentDimension >> 1;
            break;
         case 3:
            originOffset += viewBoxDimension - contentDimension;
      }

      return originOffset;
   }

   private final void calculateViewBox(
      int x, int y, int width, int height, int viewBoxXMin, int viewBoxYMin, int viewBoxWidth, int viewBoxHeight, int preserveAspectRatio, int[] destMatrix
   ) {
      if (width > 0 && height > 0 && viewBoxWidth > 0 && viewBoxHeight > 0) {
         int scaleFactorX = Fixed32.div(width, viewBoxWidth);
         int scaleFactorY = Fixed32.div(height, viewBoxHeight);
         this.calculateViewBoxMatrix(scaleFactorX, scaleFactorY, viewBoxXMin, viewBoxYMin, preserveAspectRatio, destMatrix, 0);
         int scaledViewBoxWidth = Fixed32.mul(viewBoxWidth, destMatrix[0]);
         int scaledViewBoxHeight = Fixed32.mul(viewBoxHeight, destMatrix[4]);
         this.calculateViewBoxOriginOffset(x, y, width, height, scaledViewBoxWidth, scaledViewBoxHeight, preserveAspectRatio, _tempPoint, 0);
         destMatrix[2] += _tempPoint[0];
         destMatrix[5] += _tempPoint[1];
      } else {
         System.arraycopy(VecMath.IDENTITY_3X3, 0, destMatrix, 0, 9);
      }
   }

   public final void setViewbox(int viewBoxXMin, int viewBoxYMin, int viewBoxWidth, int viewBoxHeight) {
      if (this._viewboxClippingMatrix == null) {
         this._viewboxClippingMatrix = new int[9];
      }

      if (viewBoxXMin != this._x || viewBoxYMin != this._y || viewBoxWidth != this._width || viewBoxHeight != this._height) {
         this.setX(viewBoxXMin);
         this.setY(viewBoxYMin);
         this.setWidth(viewBoxWidth, 0);
         this.setHeight(viewBoxHeight, 0);
         this.computedWidth = viewBoxWidth;
         this.computedHeight = viewBoxHeight;
         this._dirtyViewBox = true;
      }

      this._isViewbox = true;
   }

   public final void setAspectRatio(int aspectRatio) {
      if (aspectRatio != this._aspectRatio) {
         this._aspectRatio = aspectRatio;
         this._dirtyViewBox = true;
      }
   }

   public final void setViewport(int x, int y, int width, int wUnit, int height, int hUnit) {
      if (x != this._x || y != this._y || width != this._width || height != this._height || wUnit != this._widthUnit || hUnit != this._heightUnit) {
         this.setX(x);
         this.setY(y);
         this.setWidth(width, wUnit);
         this.setHeight(height, hUnit);
         this._dirtyViewBox = true;
      }
   }

   public final void setFontFamilyName(String name) {
      this._fontFamilyName = name;
   }

   public final String getFontFamilyName() {
      String result = this._fontFamilyName;

      for (MEGraphics2dContext next = this; result == "inherit"; result = next._fontFamilyName) {
         next = next._parent;
      }

      return result;
   }

   public final void setFontPixelSize(int size) {
      this._fontPixelSize = size;
   }

   public final MEGraphics2dContext resolveFontSizeInheritance() {
      MEGraphics2dContext result = this;

      while (result._fontPixelSize == Integer.MAX_VALUE) {
         result = result._parent;
      }

      return result;
   }

   public final int getFontPixelSize() {
      return this._fontPixelSize;
   }

   public final void setFontWeight(int weight) {
      this._fontWeight = weight;
   }

   public final int getFontWeight() {
      int result = this._fontWeight;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._fontWeight) {
         next = next._parent;
      }

      return result;
   }

   public final void setFontStyle(int style) {
      this._fontStyle = style;
   }

   public final int getFontStyle() {
      int result = this._fontStyle;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._fontStyle) {
         next = next._parent;
      }

      return result;
   }

   public final void setTextAnchor(int anchor) {
      this._textAnchor = anchor;
   }

   public final int getTextAnchor() {
      int result = this._textAnchor;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._textAnchor) {
         next = next._parent;
      }

      return result;
   }

   public final void setTextDecoration(int decoration) {
      this._textDecoration = decoration;
   }

   public final int getTextDecoration() {
      int result = this._textDecoration;

      for (MEGraphics2dContext next = this; result == Integer.MAX_VALUE; result = next._textDecoration) {
         next = next._parent;
      }

      return result;
   }

   private final boolean calculateClipWidth() {
      if (this._changedClipWidth) {
         return true;
      }

      boolean result = this._dirtyComputedClipWidth;
      if (this._clipRect == null) {
         MEGraphics2dContext parent = this._parent;

         while (parent != null && parent._clipRect == null) {
            parent = parent._parent;
         }

         this._computedClip.width = parent != null ? parent.getWidth() : 0;
         return result;
      } else {
         if (this._clipWidthUnit == 10) {
            if (this._parent != null) {
               result |= this._parent.calculateClipWidth();
               if (result) {
                  if (!this._parent._isViewbox && this._parent._parent != null) {
                     this._computedClip.width = this.calculatePercentage32(this._parent._computedClip.width, this._clipRect.width);
                  } else {
                     this._computedClip.width = this.calculatePercentage32(this._parent._width, this._clipRect.width);
                  }

                  this._dirtyComputedClipWidth = false;
                  this._changedClipWidth = true;
                  return result;
               }
            }
         } else if (this._dirtyComputedClipWidth) {
            this._computedClip.width = this._clipRect.width;
            this._changedClipWidth = true;
            this._dirtyComputedClipWidth = false;
         }

         return result;
      }
   }

   private final boolean calculateClipHeight() {
      if (this._changedClipHeight) {
         return true;
      }

      boolean result = this._dirtyComputedClipHeight;
      if (this._clipRect == null) {
         MEGraphics2dContext parent = this._parent;

         while (parent != null && parent._clipRect == null) {
            parent = parent._parent;
         }

         this._computedClip.height = parent != null ? parent.getHeight() : 0;
         return result;
      } else {
         if (this._clipHeightUnit == 10) {
            if (this._parent != null) {
               result |= this._parent.calculateClipHeight();
               if (result) {
                  if (!this._parent._isViewbox && this._parent._parent != null) {
                     this._computedClip.height = this.calculatePercentage32(this._parent._computedClip.height, this._clipRect.height);
                  } else {
                     this._computedClip.height = this.calculatePercentage32(this._parent._height, this._clipRect.height);
                  }

                  this._dirtyComputedClipHeight = false;
                  this._changedClipHeight = true;
                  return result;
               }
            }
         } else if (this._dirtyComputedClipHeight) {
            this._computedClip.height = this._clipRect.height;
            this._changedClipHeight = true;
            this._dirtyComputedClipHeight = false;
         }

         return result;
      }
   }

   private final boolean calculateWidth() {
      if (this._changedWidth) {
         return true;
      }

      boolean result = this._dirtyComputedWidth;
      if (this._width != Integer.MAX_VALUE) {
         if (this._widthUnit == 10) {
            if (this._parent != null) {
               result |= this._parent.calculateWidth();
               if (result) {
                  this.computedWidth = this.calculatePercentage32(this._parent.computedWidth, this._width);
                  this._dirtyComputedWidth = false;
                  this._changedWidth = true;
                  return result;
               }
            }
         } else if (this._dirtyComputedWidth) {
            this.computedWidth = this._width;
            this._dirtyComputedWidth = false;
            this._changedWidth = true;
         }

         return result;
      } else {
         MEGraphics2dContext parent = this._parent;

         while (parent != null && parent._width == Integer.MAX_VALUE) {
            parent = parent._parent;
         }

         this.computedWidth = parent != null ? parent.getWidth() : 0;
         return result;
      }
   }

   private final boolean calculateHeight() {
      if (this._changedHeight) {
         return true;
      }

      boolean result = this._dirtyComputedHeight;
      if (this._height != Integer.MAX_VALUE) {
         if (this._heightUnit == 10) {
            if (this._parent != null) {
               result |= this._parent.calculateHeight();
               if (result) {
                  this.computedHeight = this.calculatePercentage32(this._parent.computedHeight, this._height);
                  this._dirtyComputedHeight = false;
                  this._changedHeight = true;
                  return result;
               }
            }
         } else if (this._dirtyComputedHeight) {
            this.computedHeight = this._height;
            this._dirtyComputedHeight = false;
            this._changedHeight = true;
         }

         return result;
      } else {
         MEGraphics2dContext parent = this._parent;

         while (parent != null && parent._height == Integer.MAX_VALUE) {
            parent = parent._parent;
         }

         this.computedHeight = parent != null ? parent.getHeight() : 0;
         return result;
      }
   }

   public final int calculatePercentage32(int dimension, int percentage) {
      return Fixed32.mul(percentage, dimension);
   }

   public final boolean isParentViewboxDirty() {
      return this._parent != null ? this._parent._dirtyViewBox : false;
   }

   public final boolean isDirtyTransform() {
      return (this._dirtyBits & 134217728) != 0;
   }

   public final void updateTransformMatrix() {
      if (!this._transformValid) {
         if (this._parent != null && this._isViewbox && (this._parent._dirtyViewBox || this._dirtyViewBox)) {
            this.calculateViewBox(
               this._parent.getX(),
               this._parent.getY(),
               this._parent.getWidth(),
               this._parent.getHeight(),
               this._x,
               this._y,
               this._width,
               this._height,
               this._aspectRatio,
               this._viewboxClippingMatrix
            );
            this._transformMatrix = this._viewboxClippingMatrix;
            this._transformMatrixIndex = 0;
            this._parent._dirtyViewBox = false;
            this._dirtyViewBox = false;
            this.setDirtyBits(this._dirtyBits | 134217728);
         }

         if (this._parent != null) {
            this._parent.updateTransformMatrix();
            if ((this._dirtyOR & 134217728) != 0) {
               VecMath.multiply3x3Affine(this._parent._computedMatrix, 0, this._transformMatrix, this._transformMatrixIndex, this._computedMatrix, 0);
               this._dirtyFillTextureTransform = true;
            }
         } else {
            System.arraycopy(this._transformMatrix, this._transformMatrixIndex, this._computedMatrix, 0, 9);
            this._dirtyFillTextureTransform = true;
         }

         this._transformValid = true;
      }
   }

   public final void reset() {
      this._transformValid = false;
      this._dirtyBits = 0;
      this._dirtyOR = 0;
      this._dirtyORValid = false;
   }

   public final void invalidate() {
      this._changedClipHeight = false;
      this._changedClipWidth = false;
      this._changedWidth = false;
      this._changedHeight = false;
      this._computedIntersectClipValid = false;
      this._transformValid = false;
      this._dirtyORValid = false;
   }

   public final void setDirtyBits(int bits) {
      this._dirtyBits = bits & 0xFF000000;
      this._dirtyORValid = false;
      this.updateDirtyOR();
   }

   public final int getDirtyBits() {
      return this._dirtyBits;
   }

   private final void updateDirtyOR() {
      if (!this._dirtyORValid) {
         this._dirtyOR = this._dirtyBits;
         if (this._parent != null) {
            this._parent.updateDirtyOR();
            this._dirtyOR = this._dirtyOR | this._parent._dirtyOR;
         }

         this._dirtyORValid = true;
      }
   }

   public final int getDirtyOR() {
      return this._dirtyOR;
   }
}
