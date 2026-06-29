package net.rim.plazmic.internal.mediaengine.service.node;

public interface VisualNode extends Node {
   int RENDERING_HINT_AUTO;
   int RENDERING_HINT_OPTIMIZE_SPEED;
   int RENDERING_HINT_GEOMETRIC_PRECISION;
   int RENDERING_HINT_SHAPE_CRISP_EDGES;
   int RENDERING_HINT_TEXT_OPTIMIZE_LEGIBILITY;
   int RENDERING_HINT_TEXT_TRUNCATE_ELLIPSIS;
   int RENDERING_HINT_IMAGE_OPTIMIZE_QUALITY;
   int DEFAULT_RENDERING_HINT;
   int DEFAULT_ALPHA;
   int COLOR_NONE;
   int PAINT_TYPE_COLOR;
   int PAINT_TYPE_TEXTURE;
   int DEFAULT_FILL_COLOR;
   int DEFAULT_FILL_ALPHA;
   int STROKE_LINEJOIN_MITER;
   int STROKE_LINEJOIN_ROUND;
   int STROKE_LINEJOIN_BEVEL;
   int DEFAULT_STROKE_LINEJOIN;
   int STROKE_LINECAP_BUTT;
   int STROKE_LINECAP_ROUND;
   int STROKE_LINECAP_SQUARE;
   int DEFAULT_STROKE_LINECAP;
   int DEFAULT_STROKE_WIDTH;
   int DEFAULT_STROKE_ALPHA;
   int DEFAULT_STROKE_COLOR;

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
