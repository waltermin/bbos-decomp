package net.rim.plazmic.internal.mediaengine.service.node;

public interface VisualNode extends Node {
   int RENDERING_HINT_AUTO = 0;
   int RENDERING_HINT_OPTIMIZE_SPEED = 1;
   int RENDERING_HINT_GEOMETRIC_PRECISION = 2;
   int RENDERING_HINT_SHAPE_CRISP_EDGES = 3;
   int RENDERING_HINT_TEXT_OPTIMIZE_LEGIBILITY = 3;
   int RENDERING_HINT_TEXT_TRUNCATE_ELLIPSIS = 4;
   int RENDERING_HINT_IMAGE_OPTIMIZE_QUALITY = 3;
   int DEFAULT_RENDERING_HINT = 0;
   int DEFAULT_ALPHA = 255;
   int COLOR_NONE = Integer.MIN_VALUE;
   int PAINT_TYPE_COLOR = 1;
   int PAINT_TYPE_TEXTURE = 2;
   int DEFAULT_FILL_COLOR = 0;
   int DEFAULT_FILL_ALPHA = 255;
   int STROKE_LINEJOIN_MITER = 1;
   int STROKE_LINEJOIN_ROUND = 2;
   int STROKE_LINEJOIN_BEVEL = 3;
   int DEFAULT_STROKE_LINEJOIN = 1;
   int STROKE_LINECAP_BUTT = 16;
   int STROKE_LINECAP_ROUND = 32;
   int STROKE_LINECAP_SQUARE = 48;
   int DEFAULT_STROKE_LINECAP = 16;
   int DEFAULT_STROKE_WIDTH = 1;
   int DEFAULT_STROKE_ALPHA = 255;
   int DEFAULT_STROKE_COLOR = Integer.MIN_VALUE;

   int getFillPaintType();

   int getFillAlpha();

   void setFillAlpha(int var1);

   int getFillColor();

   void setFillColor(int var1);

   Object getFillPattern();

   void setFillPattern(Object var1);

   int getStrokeWidth();

   void setStrokeWidth(int var1);

   int getStrokeLinecap();

   void setStrokeLinecap(int var1);

   int getStrokeLinejoin();

   void setStrokeLinejoin(int var1);

   int getStrokeAlpha();

   void setStrokeAlpha(int var1);

   int getStrokeColor();

   void setStrokeColor(int var1);

   void copyTransformationMatrix(int[] var1);

   void setTransformationMatrix(int[] var1);

   boolean isVisible();

   void setVisible(boolean var1);

   boolean isDisplayable();

   void setDisplayable(boolean var1);

   int getTextRenderingHint();

   void setTextRenderingHint(int var1);

   int getImageRenderingHint();

   void setImageRenderingHint(int var1);

   int getShapeRenderingHint();

   void setShapeRenderingHint(int var1);

   int getAlpha();

   void setAlpha(int var1);

   int getX();

   void setX(int var1);

   int getY();

   void setY(int var1);
}
