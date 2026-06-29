package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.VisualNode;

public class VisualNodeImpl extends NodeImpl implements VisualNode {
   public static void initVisual(int handle, ModelInteractorImpl model) {
      NodeImpl.initNode(handle, model);
      model._nodes[handle + 12] = -1;
      model._nodes[handle + 21] = -1;
      model._nodes[handle + 22] = -1;
      model._nodes[handle + 13] = Integer.MAX_VALUE;
      model._nodes[handle + 15] = 1;
      NodeImpl.setDirtyBits(handle, model, -16777216);
      model._nodes[handle + 8] = -16777072;
   }

   private static int createFill(int handle, ModelInteractorImpl model) {
      int fillHandle = model.createNodeHandle(70, 6);
      model._nodes[handle + 21] = fillHandle;
      model._nodes[fillHandle + 4] = -1;
      model._nodes[fillHandle + 5] = Integer.MAX_VALUE;
      return fillHandle;
   }

   private static int createFillPaint(int paintType, int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      int type = Integer.MIN_VALUE;
      if (paintType == 1) {
         type = 74;
      } else if (paintType == 2) {
         type = 76;
      }

      int paintHandle = model.createNodeHandle(type, 5);
      model._nodes[fillHandle + 4] = paintHandle;
      model._nodes[paintHandle + 4] = -1;
      model._nodes[paintHandle + 3] = Integer.MAX_VALUE;
      model._nodes[fillHandle + 3] = model._nodes[fillHandle + 3] | 2;
      return paintHandle;
   }

   private static int createPaintPattern(Object patternImage, int paintHandle, ModelInteractorImpl model) {
      int patternHandle = model.createNodeHandle(42, 30);
      if (patternHandle < 0) {
         return patternHandle;
      }

      ImageNodeImpl.init(patternHandle, model);
      ImageNodeImpl.setImage(patternImage, patternHandle, model);
      model._nodes[paintHandle + 4] = patternHandle;
      return patternHandle;
   }

   @Override
   public int getFillPaintType() {
      return getFillPaintType(super._handle, super._model);
   }

   public static int getFillPaintType(int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      if (fillHandle >= 0) {
         int paintHandle = model._nodes[fillHandle + 4];
         if (paintHandle >= 0) {
            int type = model._nodes[paintHandle + 1];
            if (type == 74) {
               return 1;
            }

            if (type == 76) {
               return 2;
            }
         }
      }

      return Integer.MAX_VALUE;
   }

   @Override
   public int getFillColor() {
      return getFillColor(super._handle, super._model);
   }

   public static int getFillColor(int handle, ModelInteractorImpl model) {
      if ((model._nodes[handle + 8] & 2048) != 0) {
         return Integer.MIN_VALUE;
      }

      int fillHandle = model._nodes[handle + 21];
      if (fillHandle >= 0) {
         int paintHandle = model._nodes[fillHandle + 4];
         if (paintHandle >= 0) {
            return model._nodes[paintHandle + 3];
         }
      }

      return Integer.MAX_VALUE;
   }

   public static int getResolvedFillColor(int handle, ModelInteractorImpl model) {
      int fillColor;
      for (fillColor = getFillColor(handle, model); fillColor == Integer.MAX_VALUE && handle != -1; fillColor = getFillColor(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return fillColor == Integer.MAX_VALUE ? 0 : fillColor;
   }

   @Override
   public void setFillColor(int color) {
      setFillColor(color, super._handle, super._model);
   }

   public static void setFillColor(int color, int handle, ModelInteractorImpl model) {
      if (color == Integer.MIN_VALUE) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 2048;
      }

      int fillHandle = model._nodes[handle + 21];
      if (fillHandle < 0 && color != Integer.MIN_VALUE) {
         fillHandle = createFill(handle, model);
      }

      if (fillHandle >= 0) {
         int paintHandle = model._nodes[fillHandle + 4];
         if (paintHandle < 0 && color != Integer.MIN_VALUE) {
            paintHandle = createFillPaint(1, handle, model);
         }

         if (paintHandle >= 0) {
            if (model._nodes[paintHandle + 1] != 74) {
               model._nodes[paintHandle + 1] = 74;
            }

            model._nodes[paintHandle + 3] = color;
         }
      }

      NodeImpl.setDirtyBits(handle, model, 536870912);
   }

   @Override
   public Object getFillPattern() {
      return getFillPattern(super._handle, super._model);
   }

   public static Object getFillPattern(int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      if (fillHandle >= 0) {
         int paintHandle = model._nodes[fillHandle + 4];
         if (paintHandle >= 0) {
            int patternIndex = model._nodes[paintHandle + 4];
            if (patternIndex >= 0) {
               int textureIndex = model._nodes[patternIndex + 29];
               if (textureIndex >= 0) {
                  return model.getImage(textureIndex);
               }
            }
         }
      }

      return null;
   }

   @Override
   public void setFillPattern(Object pattern) {
      setFillPattern(pattern, super._handle, super._model);
   }

   public static void setFillPattern(Object pattern, int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      if (fillHandle < 0) {
         fillHandle = createFill(handle, model);
      }

      int paintHandle = model._nodes[fillHandle + 4];
      if (paintHandle < 0) {
         paintHandle = createFillPaint(2, handle, model);
      } else if (model._nodes[paintHandle + 1] != 76) {
         model._nodes[paintHandle + 1] = 76;
      }

      int patternIndex = model._nodes[paintHandle + 4];
      if (patternIndex >= 0) {
         int textureIndex = model._nodes[patternIndex + 29];
         if (textureIndex >= 0) {
            model.setImage(textureIndex, pattern);
         }
      } else {
         patternIndex = createPaintPattern(pattern, paintHandle, model);
         ViewportNodeImpl.setWidth(100, patternIndex, model);
         ViewportNodeImpl.setHeight(100, patternIndex, model);
      }

      NodeImpl.setDirtyBits(handle, model, 536870912);
   }

   @Override
   public int getFillAlpha() {
      return getFillAlpha(super._handle, super._model);
   }

   public static int getFillAlpha(int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      return fillHandle >= 0 ? model._nodes[fillHandle + 5] : Integer.MAX_VALUE;
   }

   @Override
   public void setFillAlpha(int alpha) {
      setFillAlpha(alpha, super._handle, super._model);
   }

   public static void setFillAlpha(int alpha, int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      if (fillHandle < 0) {
         fillHandle = createFill(handle, model);
      }

      model._nodes[fillHandle + 5] = alpha;
      NodeImpl.setDirtyBits(handle, model, 1610612736);
      model._nodes[fillHandle + 3] = model._nodes[fillHandle + 3] | 4;
   }

   private static int createStroke(int handle, ModelInteractorImpl model) {
      int strokeHandle = model.createNodeHandle(72, 8);
      model._nodes[handle + 22] = strokeHandle;
      model._nodes[strokeHandle + 4] = -1;
      model._nodes[strokeHandle + 5] = Integer.MAX_VALUE;
      model._nodes[strokeHandle + 6] = Integer.MAX_VALUE;
      return strokeHandle;
   }

   private static int createStrokePaint(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      int paintHandle = model.createNodeHandle(74, 4);
      model._nodes[strokeHandle + 4] = paintHandle;
      model._nodes[paintHandle + 3] = Integer.MAX_VALUE;
      model._nodes[strokeHandle + 3] = model._nodes[strokeHandle + 3] | 2;
      return paintHandle;
   }

   @Override
   public int getStrokeColor() {
      return getStrokeColor(super._handle, super._model);
   }

   public static int getStrokeColor(int handle, ModelInteractorImpl model) {
      if ((model._nodes[handle + 8] & 4096) != 0) {
         return Integer.MIN_VALUE;
      }

      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle >= 0) {
         int paintHandle = model._nodes[strokeHandle + 4];
         if (paintHandle >= 0) {
            return model._nodes[paintHandle + 3];
         }
      }

      return Integer.MAX_VALUE;
   }

   public static int getResolvedStrokeColor(int handle, ModelInteractorImpl model) {
      int strokeColor;
      for (strokeColor = getStrokeColor(handle, model); strokeColor == Integer.MAX_VALUE && handle != -1; strokeColor = getStrokeColor(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return strokeColor == Integer.MAX_VALUE ? Integer.MIN_VALUE : strokeColor;
   }

   @Override
   public void setStrokeColor(int color) {
      setStrokeColor(color, super._handle, super._model);
   }

   public static void setStrokeColor(int color, int handle, ModelInteractorImpl model) {
      if (color == Integer.MIN_VALUE) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 4096;
      }

      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle < 0 && color != Integer.MIN_VALUE) {
         strokeHandle = createStroke(handle, model);
      }

      if (strokeHandle >= 0) {
         int paintHandle = model._nodes[strokeHandle + 4];
         if (paintHandle < 0 && color != Integer.MIN_VALUE) {
            paintHandle = createStrokePaint(handle, model);
         }

         if (paintHandle >= 0) {
            model._nodes[paintHandle + 3] = color;
         }
      }

      NodeImpl.setDirtyBits(handle, model, 268435456);
   }

   @Override
   public int getStrokeAlpha() {
      return getStrokeAlpha(super._handle, super._model);
   }

   public static int getStrokeAlpha(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      return strokeHandle >= 0 ? model._nodes[strokeHandle + 5] : Integer.MAX_VALUE;
   }

   @Override
   public void setStrokeAlpha(int alpha) {
      setStrokeAlpha(alpha, super._handle, super._model);
   }

   public static void setStrokeAlpha(int alpha, int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle < 0) {
         strokeHandle = createStroke(handle, model);
      }

      model._nodes[strokeHandle + 5] = alpha;
      NodeImpl.setDirtyBits(handle, model, 1342177280);
      model._nodes[strokeHandle + 3] = model._nodes[strokeHandle + 3] | 4;
   }

   @Override
   public int getStrokeWidth() {
      return getStrokeWidth(super._handle, super._model);
   }

   public static int getStrokeWidth(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      return strokeHandle >= 0 ? model._nodes[strokeHandle + 6] : Integer.MAX_VALUE;
   }

   @Override
   public void setStrokeWidth(int width) {
      setStrokeWidth(width, super._handle, super._model);
   }

   public static void setStrokeWidth(int width, int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle < 0) {
         strokeHandle = createStroke(handle, model);
      }

      model._nodes[strokeHandle + 6] = width;
      model._nodes[strokeHandle + 3] = model._nodes[strokeHandle + 3] | 8;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int getStrokeLinecap() {
      return getStrokeLinecap(super._handle, super._model);
   }

   public static int getStrokeLinecap(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle >= 0) {
         int bits = model._nodes[strokeHandle + 3];
         if ((bits & 16) != 0) {
            return model._nodes[strokeHandle + 7] & 240;
         }
      }

      return 16;
   }

   @Override
   public void setStrokeLinecap(int linecap) {
      setStrokeLinecap(linecap, super._handle, super._model);
   }

   public static void setStrokeLinecap(int linecap, int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle < 0) {
         strokeHandle = createStroke(handle, model);
      }

      int style = model._nodes[strokeHandle + 7];
      style &= -241;
      style |= linecap;
      model._nodes[strokeHandle + 7] = style;
      model._nodes[strokeHandle + 3] = model._nodes[strokeHandle + 3] | 16;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public int getStrokeLinejoin() {
      return getStrokeLinejoin(super._handle, super._model);
   }

   public static int getStrokeLinejoin(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle >= 0) {
         int bits = model._nodes[strokeHandle + 3];
         if ((bits & 32) != 0) {
            return model._nodes[strokeHandle + 7] & 15;
         }
      }

      return 1;
   }

   @Override
   public void setStrokeLinejoin(int linejoin) {
      setStrokeLinejoin(linejoin, super._handle, super._model);
   }

   public static void setStrokeLinejoin(int linejoin, int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle < 0) {
         strokeHandle = createStroke(handle, model);
      }

      int style = model._nodes[strokeHandle + 7];
      style &= -16;
      style |= linejoin;
      model._nodes[strokeHandle + 7] = style;
      model._nodes[strokeHandle + 3] = model._nodes[strokeHandle + 3] | 32;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public void copyTransformationMatrix(int[] array) {
      copyTransformationMatrix(array, super._handle, super._model);
   }

   public static void copyTransformationMatrix(int[] array, int handle, ModelInteractorImpl model) {
      int transformHandle = model._nodes[handle + 12];
      if (transformHandle >= 0) {
         System.arraycopy(model._nodes, transformHandle + 3, array, 0, 6);
      } else {
         System.arraycopy(
            new int[]{
               1,
               0,
               0,
               0,
               1,
               0,
               51,
               -804651004,
               -1,
               -1,
               -1,
               -1,
               712179968,
               1411080524,
               2191443,
               -1305840895,
               1398020978,
               16785776,
               -2104615050,
               527827200,
               1816363662,
               1979777255,
               846737962,
               67113293
            },
            0,
            array,
            0,
            6
         );
      }
   }

   @Override
   public void setTransformationMatrix(int[] matrix) {
      setTransformationMatrix(matrix, super._handle, super._model);
   }

   public static void setTransformationMatrix(int[] matrix, int handle, ModelInteractorImpl model) {
      int transformHandle = model._nodes[handle + 12];
      if (transformHandle < 0) {
         transformHandle = model.createNodeHandle(64, 21);
         model._nodes[handle + 12] = transformHandle;
         System.arraycopy(new int[]{0, 0, 1, -804651002, 1, 0, 0, 0, 1, 0, 51, -804651004}, 0, model._nodes, transformHandle + 3 + 6, 3);
      }

      System.arraycopy(matrix, 0, model._nodes, transformHandle + 3, 6);
      NodeImpl.setDirtyBits(handle, model, 134217728);
      model._nodes[handle + 15]++;
   }

   public static boolean isFocusable(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 32) != 0;
   }

   public static void setFocusable(boolean isFocusable, int handle, ModelInteractorImpl model) {
      if (isFocusable) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 32;
         model.addFocusableHandle(handle);
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -33;
         model.removeFocusableHandle(handle);
      }
   }

   @Override
   public boolean isVisible() {
      return isVisible(super._handle, super._model);
   }

   public static boolean isVisible(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 64) != 0 ? (model._nodes[handle + 8] & 128) != 0 : true;
   }

   @Override
   public void setVisible(boolean isVisible) {
      setVisible(isVisible, super._handle, super._model);
   }

   public static void setVisible(boolean isVisible, int handle, ModelInteractorImpl model) {
      if (isVisible) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 128;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -129;
      }

      NodeImpl.setDirtyBits(handle, model, 16777280);
   }

   @Override
   public boolean isDisplayable() {
      return isDisplayable(super._handle, super._model);
   }

   public static boolean isDisplayable(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 16) != 0;
   }

   public static boolean isResolvedDisplayable(int handle, ModelInteractorImpl model) {
      boolean displayable = true;

      while (displayable && handle != -1) {
         displayable = isDisplayable(handle, model);
         handle = NodeImpl.getParent(handle, model);
      }

      return displayable;
   }

   @Override
   public void setDisplayable(boolean isDisplayable) {
      setDisplayable(isDisplayable, super._handle, super._model);
   }

   public static void setDisplayable(boolean isDisplayable, int handle, ModelInteractorImpl model) {
      if (isDisplayable) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 16;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -17;
      }

      NodeImpl.setDirtyBits(handle, model, 16777216);
   }

   @Override
   public int getTextRenderingHint() {
      return getTextRenderingHint(super._handle, super._model);
   }

   public static int getTextRenderingHint(int handle, ModelInteractorImpl model) {
      if ((model._nodes[handle + 8] & 1024) != 0) {
         int hints = model._nodes[handle + 14];
         if (((hints & 0xFF000000) >> 24 & 2) != 0) {
            return (hints & 0xFF00) >> 8;
         }
      }

      return 0;
   }

   @Override
   public void setTextRenderingHint(int renderingHint) {
      setTextRenderingHint(renderingHint, super._handle, super._model);
   }

   public static void setTextRenderingHint(int renderingHint, int handle, ModelInteractorImpl model) {
      int hints = model._nodes[handle + 14];
      hints &= -65281;
      hints |= 33554432 | renderingHint << 8;
      model._nodes[handle + 14] = hints;
      NodeImpl.setDirtyBits(handle, model, -16776192);
   }

   @Override
   public int getImageRenderingHint() {
      return getImageRenderingHint(super._handle, super._model);
   }

   public static int getImageRenderingHint(int handle, ModelInteractorImpl model) {
      if ((model._nodes[handle + 8] & 1024) != 0) {
         int hints = model._nodes[handle + 14];
         if (((hints & 0xFF000000) >> 24 & 1) != 0) {
            return (hints & 0xFF0000) >> 16;
         }
      }

      return 0;
   }

   @Override
   public void setImageRenderingHint(int renderingHint) {
      setImageRenderingHint(renderingHint, super._handle, super._model);
   }

   public static void setImageRenderingHint(int renderingHint, int handle, ModelInteractorImpl model) {
      int hints = model._nodes[handle + 14];
      hints &= -16711681;
      hints |= 16777216 | renderingHint << 16;
      model._nodes[handle + 14] = hints;
      NodeImpl.setDirtyBits(handle, model, -16776192);
   }

   @Override
   public int getShapeRenderingHint() {
      return getShapeRenderingHint(super._handle, super._model);
   }

   public static int getShapeRenderingHint(int handle, ModelInteractorImpl model) {
      if ((model._nodes[handle + 8] & 1024) != 0) {
         int hints = model._nodes[handle + 14];
         if (((hints & 0xFF000000) >> 24 & 4) != 0) {
            return (hints & 0xFF) >> 0;
         }
      }

      return 0;
   }

   @Override
   public void setShapeRenderingHint(int renderingHint) {
      setShapeRenderingHint(renderingHint, super._handle, super._model);
   }

   public static void setShapeRenderingHint(int renderingHint, int handle, ModelInteractorImpl model) {
      int hints = model._nodes[handle + 14];
      hints &= -256;
      hints |= 67108864 | renderingHint << 0;
      model._nodes[handle + 14] = hints;
      NodeImpl.setDirtyBits(handle, model, -16776192);
   }

   @Override
   public int getAlpha() {
      return getAlpha(super._handle, super._model);
   }

   public static int getAlpha(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 13];
   }

   @Override
   public void setAlpha(int alpha) {
      setAlpha(alpha, super._handle, super._model);
   }

   public static void setAlpha(int alpha, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 13] = alpha;
      NodeImpl.setDirtyBits(handle, model, 1073741824);
      model._nodes[handle + 8] = model._nodes[handle + 8] | 512;
   }

   @Override
   public int getX() {
      return getX(super._handle, super._model);
   }

   public static int getX(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 10];
   }

   @Override
   public void setX(int x) {
      setX(x, super._handle, super._model);
   }

   public static void setX(int x, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 10] = x;
      int bits = 67108864;
      int type = NodeImpl.getType(handle, model);
      if (type == 32 || type == 50) {
         bits |= 8192;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
   }

   @Override
   public int getY() {
      return getY(super._handle, super._model);
   }

   public static int getY(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 11];
   }

   @Override
   public void setY(int y) {
      setY(y, super._handle, super._model);
   }

   public static void setY(int y, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 11] = y;
      int bits = 67108864;
      int type = NodeImpl.getType(handle, model);
      if (type == 32 || type == 50) {
         bits |= 131072;
      }

      NodeImpl.setDirtyBits(handle, model, bits);
   }

   private static void removeFill(int handle, ModelInteractorImpl model) {
      int fillHandle = model._nodes[handle + 21];
      if (fillHandle >= 0) {
         int paintHandle = model._nodes[fillHandle + 4];
         if (paintHandle >= 0) {
            removePaint(paintHandle, model);
         }

         model.deleteNode(fillHandle);
         NodeImpl.setDirtyBits(handle, model, 536870912);
      }
   }

   private static void removeStroke(int handle, ModelInteractorImpl model) {
      int strokeHandle = model._nodes[handle + 22];
      if (strokeHandle >= 0) {
         int paintHandle = model._nodes[strokeHandle + 4];
         if (paintHandle >= 0) {
            removePaint(paintHandle, model);
         }

         model.deleteNode(strokeHandle);
         NodeImpl.setDirtyBits(handle, model, 268435456);
      }
   }

   private static void removePaint(int paintHandle, ModelInteractorImpl model) {
      int paintType = model._nodes[paintHandle + 1];
      if (paintType == 76) {
         int patternIndex = model._nodes[paintHandle + 4];
         if (patternIndex >= 0) {
            model.removeImage(patternIndex);
         }
      }

      model.deleteNode(paintHandle);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int transformIndex = model._nodes[handle + 12];
      if (transformIndex >= 0) {
         model.deleteNode(transformIndex);
      }

      removeFill(handle, model);
      removeStroke(handle, model);
   }
}
