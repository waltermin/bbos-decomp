package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.device.api.math.Fixed32;

public class TextAttrNodeImpl {
   public static int teStyleToSVGWeight(int style) {
      return (style & 1) != 0 ? 700 : 400;
   }

   static int createTextAttr(int handle, ModelInteractorImpl model) {
      int attrHandle = model.createNodeHandle(68, 9);
      int offset;
      if (NodeImpl.getType(handle, model) == 46) {
         offset = 29;
      } else {
         offset = 23;
      }

      model._nodes[handle + offset] = attrHandle;
      model._nodes[attrHandle + 6] = Integer.MAX_VALUE;
      model._nodes[attrHandle + 8] = Integer.MAX_VALUE;
      model._nodes[attrHandle + 4] = Integer.MAX_VALUE;
      model._nodes[attrHandle + 5] = Integer.MAX_VALUE;
      model._nodes[attrHandle + 7] = -1;
      return attrHandle;
   }

   static void removeTextAttr(int handle, ModelInteractorImpl model) {
      int offset;
      if (NodeImpl.getType(handle, model) == 46) {
         offset = 29;
      } else {
         offset = 23;
      }

      int attrHandle = model._nodes[handle + offset];
      if (attrHandle >= 0) {
         int fontFamilyIndex = model._nodes[attrHandle + 7];
         if (fontFamilyIndex >= 0) {
            model.removeCustomMessage(fontFamilyIndex);
         }

         model.deleteNode(attrHandle);
      }
   }

   public static int getFontSize(int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      return attrHandle >= 0 ? model._nodes[attrHandle + 4] : Integer.MAX_VALUE;
   }

   public static int getResolvedFontSize(int handle, ModelInteractorImpl model) {
      int fontSize;
      for (fontSize = getFontSize(handle, model); fontSize == Integer.MAX_VALUE && handle != -1; fontSize = getFontSize(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return fontSize == Integer.MAX_VALUE ? Fixed32.toFP(12) : fontSize;
   }

   public static void setFontSize(int fontSize, int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle < 0) {
         attrHandle = createTextAttr(handle, model);
      }

      model._nodes[attrHandle + 4] = fontSize;
      model._nodes[attrHandle + 3] = model._nodes[attrHandle + 3] | 32;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   public static int getFontWeight(int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      return attrHandle >= 0 ? model._nodes[attrHandle + 5] : Integer.MAX_VALUE;
   }

   public static int getResolvedFontWeight(int handle, ModelInteractorImpl model) {
      int weight;
      for (weight = getFontWeight(handle, model); weight == Integer.MAX_VALUE && handle != -1; weight = getFontWeight(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return weight == Integer.MAX_VALUE ? 400 : weight;
   }

   public static void setFontWeight(int fontWeight, int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle < 0) {
         attrHandle = createTextAttr(handle, model);
      }

      model._nodes[attrHandle + 5] = fontWeight;
      model._nodes[attrHandle + 3] = model._nodes[attrHandle + 3] | 16;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   public static int getFontStyle(int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      return attrHandle >= 0 ? model._nodes[attrHandle + 6] : Integer.MAX_VALUE;
   }

   public static int getResolvedFontStyle(int handle, ModelInteractorImpl model) {
      int style;
      for (style = getFontStyle(handle, model); style == Integer.MAX_VALUE && handle != -1; style = getFontStyle(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return style == Integer.MAX_VALUE ? 1 : style;
   }

   public static void setFontStyle(int fontStyle, int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle < 0) {
         attrHandle = createTextAttr(handle, model);
      }

      model._nodes[attrHandle + 6] = fontStyle;
      model._nodes[attrHandle + 3] = model._nodes[attrHandle + 3] | 2;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   public static int getTextDecoration(int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      return attrHandle >= 0 ? model._nodes[attrHandle + 8] : Integer.MAX_VALUE;
   }

   public static int getResolvedTextDecoration(int handle, ModelInteractorImpl model) {
      int textDec;
      for (textDec = getTextDecoration(handle, model); textDec == Integer.MAX_VALUE && handle != -1; textDec = getTextDecoration(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return textDec == Integer.MAX_VALUE ? 1 : textDec;
   }

   public static void setTextDecoration(int decoration, int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle < 0) {
         attrHandle = createTextAttr(handle, model);
      }

      model._nodes[attrHandle + 8] = decoration;
      model._nodes[attrHandle + 3] = model._nodes[attrHandle + 3] | 8;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   public static String getFontFamily(int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle >= 0) {
         int familyIndex = model._nodes[attrHandle + 7];
         if (familyIndex != Integer.MAX_VALUE && familyIndex != -1) {
            return model._platformFontFamilyStrings[familyIndex];
         }
      }

      return null;
   }

   public static String getResolvedFontFamily(int handle, ModelInteractorImpl model) {
      String fontFam;
      for (fontFam = getFontFamily(handle, model); fontFam == null && handle != -1; fontFam = getFontFamily(handle, model)) {
         handle = NodeImpl.getParent(handle, model);
      }

      return fontFam == null ? "BBMillbank" : fontFam;
   }

   public static void setFontFamily(String fontFamily, int handle, ModelInteractorImpl model) {
      int attrHandle = getTextAttrHandle(handle, model);
      if (attrHandle < 0) {
         attrHandle = createTextAttr(handle, model);
      }

      int familyIndex = model.getFontFamilyIdx(fontFamily);
      if (familyIndex == -1) {
         familyIndex = model.addFontFamily(fontFamily);
      }

      model._nodes[attrHandle + 7] = familyIndex;
      model._nodes[attrHandle + 3] = model._nodes[attrHandle + 3] | 64;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   private static int getTextAttrHandle(int handle, ModelInteractorImpl model) {
      return NodeImpl.getType(handle, model) == 46 ? model._nodes[handle + 29] : model._nodes[handle + 23];
   }
}
