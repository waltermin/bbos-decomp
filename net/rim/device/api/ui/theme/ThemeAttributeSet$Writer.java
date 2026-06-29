package net.rim.device.api.ui.theme;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.Background;

public final class ThemeAttributeSet$Writer {
   private ResourceFetcher _resourceFetcher;
   private final ThemeAttributeSet this$0;

   public ThemeAttributeSet$Writer(ThemeAttributeSet _1, ResourceFetcher resourceFetcher) {
      this.this$0 = _1;
      this._resourceFetcher = resourceFetcher;
   }

   public final ThemeAttributeSet getThemeAttributeSet() {
      return this.this$0;
   }

   public final void setBackground(Background background) {
      this.this$0._background = background;
   }

   public final void setBackgroundPosition(String name) {
   }

   public final void setAltFontFamily(String alt) {
      this.this$0._altFontFamily = alt;
   }

   public final void setFontStrokeOpacity(int opacity) {
      this.this$0._fontStrokeOpacity = opacity;
   }

   public final void setBackgroundImage(String name) {
      this.this$0._backgroundLocation = this._resourceFetcher;
      this.this$0._backgroundName = name;
      if (name == null) {
         this.this$0._set &= -1025;
      } else if (name.length() == 0) {
         this.this$0._set |= 1024;
      } else {
         this.this$0._background = Background.createBitmapBackground(this.this$0._backgroundLocation, name);
         this.this$0._set |= 525312;
      }
   }

   public final void setBackgroundImage(Bitmap bitmap) {
      this.this$0._backgroundLocation = null;
      this.this$0._backgroundName = null;
      if (bitmap == null) {
         this.this$0._set &= -525313;
      } else {
         this.this$0._set |= 525312;
         this.this$0._background = Background.createBitmapBackground(bitmap);
      }
   }

   public final void setBackgroundOpacity(int opacity) {
      this.this$0._set |= 1048576;
      this.this$0._opacity = opacity;
   }

   public final void setBackgroundPosition(int horizontalPosition, int verticalPosition) {
      this.this$0._background.setPosition(horizontalPosition, verticalPosition);
   }

   public final void setRepeat(int repeat) {
      this.this$0._background.setRepeat(repeat);
   }

   public final void setBorder(String borderName) {
      this.this$0._borderName = borderName;
      this.this$0._set |= 2048;
      this.this$0._set |= 8192;
   }

   public final void setScrollbar(String scrollbarName) {
      this.this$0._scrollbarName = scrollbarName;
   }

   public final void setColor(int colorType, int color) {
      this.this$0._colors[colorType] = color;
      this.this$0._set |= 1 << 0 + colorType;
      if (colorType == 0) {
         this.this$0._background = Background.createSolidBackground(color);
         this.this$0._set |= 524288;
      } else {
         if (colorType == 7 && (this.this$0._set & 2) == 0) {
            this.setColor(1, color);
         }
      }
   }

   public final void setColor(int colorType, String color) {
      this.this$0._palettedColors[colorType] = color;
      this.this$0._set |= 1 << 0 + colorType;
   }

   public final void setMaximumLineWrapping(int maxLines) {
      this.this$0._maxLineWrap = maxLines;
   }

   public final void setEdges(int edgeType, int top, int right, int bottom, int left) {
      if (top >= 0 && right >= 0 && bottom >= 0 && left >= 0) {
         XYEdges edges = new XYEdges(top, right, bottom, left);
         switch (edgeType) {
            case 0:
               this.this$0._padding = edges;
               break;
            case 2:
               this.this$0._margin = edges;
         }

         this.this$0._set |= 1 << 12 + edgeType;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void setPosition(int x, int y, int width, int height) {
      this.this$0._position = new XYRect(x, y, width, height);
   }

   public final void setFocusStyle(int style) {
      if (style >= 0 && style < 5) {
         this.this$0._focusStyle = style;
         this.this$0._set |= 256;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void setElement(String element) {
      this.this$0._element = element;
   }

   public final void setFontFamily(String fontFamily) {
      this.this$0._fontFamily = fontFamily;
   }

   public final void setFontStyle(int fontStyle) {
      this.this$0._fontStyle = fontStyle | 1073741824;
   }

   public final void setFontSize(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: No common supertype for ternary expression
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getExprType(FunctionExprent.java:224)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.checkExprTypeBounds(FunctionExprent.java:372)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:156)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:132)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:125)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:241)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: iload 1
      // 05: tableswitch 47 0 7 92 47 55 63 70 75 80 86
      // 34: bipush 3
      // 36: istore 2
      // 37: bipush 1
      // 38: istore 3
      // 39: goto 6c
      // 3c: bipush 2
      // 3e: istore 2
      // 3f: bipush 1
      // 40: istore 3
      // 41: goto 6c
      // 44: bipush 1
      // 45: istore 2
      // 46: bipush 1
      // 47: istore 3
      // 48: goto 6c
      // 4b: bipush 0
      // 4c: istore 2
      // 4d: goto 6c
      // 50: bipush 1
      // 51: istore 2
      // 52: goto 6c
      // 55: bipush 2
      // 57: istore 2
      // 58: goto 6c
      // 5b: bipush 3
      // 5d: istore 2
      // 5e: goto 6c
      // 61: new java/lang/IllegalArgumentException
      // 64: dup
      // 65: ldc_w "Incorrect relatvie font size specified."
      // 68: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 6b: athrow
      // 6c: aload 0
      // 6d: getfield net/rim/device/api/ui/theme/ThemeAttributeSet$Writer.this$0 Lnet/rim/device/api/ui/theme/ThemeAttributeSet;
      // 70: iload 2
      // 71: ifne 7b
      // 74: nop
      // 75: ldc_w 1065353216
      // 78: goto 81
      // 7b: aload 0
      // 7c: iload 2
      // 7d: iload 3
      // 7e: invokespecial net/rim/device/api/ui/theme/ThemeAttributeSet$Writer.powerHelper (IZ)F
      // 81: putfield net/rim/device/api/ui/theme/ThemeAttributeSet._fontRelativePercent F
      // 84: return
   }

   private final float powerHelper(int power, boolean negative) {
      float result = (float)1065353216;

      for (int i = power; i > 0; i--) {
         result = (float)(result * 4608083138725491507L);
      }

      return negative ? 1065353216 / result : result;
   }

   public final void setFontSize(int fontSize, int fontSizeUnits) {
      this.setFontSize(fontSize, fontSizeUnits, false);
   }

   public final void setFontSize(int fontSize, int fontSizeUnits, boolean relative) {
      this.this$0._fontSizeUnits = fontSizeUnits;
      if (relative) {
         this.this$0._fontRelativeChange = fontSize;
      } else {
         this.this$0._fontSize = fontSize;
      }
   }

   public final void setFontAntialiasMode(int fontAntialiasMode) {
      this.this$0._fontAntialiasMode = fontAntialiasMode;
   }

   public final void setLayout(String layout) {
      if (!layout.equals("empty") && layout.indexOf(58) == -1) {
         layout = this._resourceFetcher.getBaseURL() + layout;
      }

      this.this$0._layout = layout;
   }

   public final void setLayoutParameters(String[] params) {
      this.this$0._layoutParams = params;
   }

   public final void setScrollArrow(int type, String filename) {
      if (null == filename) {
         this.this$0._set &= ~(1 << 15 + type);
      } else {
         this.this$0._set |= 1 << 15 + type;
      }

      if (this.this$0._scrollArrowName == null) {
         this.this$0._scrollArrowName = new String[4];
         this.this$0._scrollArrow = new Bitmap[4];
      }

      this.this$0._scrollArrowName[type] = filename;
   }

   public final void setTextAlign(int align) {
      switch (align) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            this.this$0._set &= -2097153;
            this.this$0._textAlign = align;
            return;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            this.this$0._set |= 2097152;
            this.this$0._textAlign = align;
      }
   }
}
