package net.rim.device.api.ui;

import java.util.EmptyStackException;
import java.util.Stack;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.accessibility.AccessibleEventListener;
import net.rim.device.api.util.NumericOverflowException;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.UiInternalListener;
import net.rim.device.internal.ui.UiOptionsRegistry;
import net.rim.device.internal.util.OptionsRegistry$IntParameter;
import net.rim.tid.text.AttributedString;
import net.rim.vm.TraceBack;

public final class Ui {
   static final boolean DEBUG = false;
   public static final long UI_LOG_GUID = -4685663286194863677L;
   static boolean DRAW_FOCUS_IN_PAINT = true;
   static boolean IN_MAKE_REGION_VISIBLE;
   static XYEdges _tmpEdges = new XYEdges();
   private static Stack _tmpDrawStack = new Stack();
   private static Stack _tmpTextMetricsStack = new Stack();
   private static Stack _tmpXYRectStack = new Stack();
   public static final int MODE_BEGINNER = 0;
   public static final int MODE_NORMAL = 1;
   public static final int MODE_ADVANCED = 2;
   public static final int INCREASE_UP = -1;
   public static final int INCREASE_DOWN = 1;
   public static final int CLICK_ACTION_OPEN = 1;
   public static final int CLICK_ACTION_MENU = 0;
   private static final int UNITS_PREFIX_Mb = 536870912;
   private static final int UNITS_PREFIX_Kb = 268435456;
   private static final int UNITS_PREFIX_T = 134217728;
   private static final int UNITS_PREFIX_G = 67108864;
   private static final int UNITS_PREFIX_M = 33554432;
   private static final int UNITS_PREFIX_k = 16777216;
   private static final int UNITS_PREFIX_d = 8388608;
   private static final int UNITS_PREFIX_c = 4194304;
   private static final int UNITS_PREFIX_m = 2097152;
   private static final int UNITS_PREFIX_µ = 1048576;
   private static final int UNITS_PREFIX_n = 524288;
   private static final int UNITS_PREFIX_p = 262144;
   private static final int UNITS_PREFIX_FIX8 = 32768;
   private static final int UNITS_PREFIX_FIX16 = 16384;
   public static final int UNITS_m = 4;
   public static final int UNITS_pt = 2;
   public static final int UNITS_ptd = 3;
   public static final int UNITS_px = 0;
   public static final int UNITS_cpt = 4194306;
   public static final int UNITS_cptd = 4194307;
   public static final int UNITS_cm = 4194308;
   public static final int UNITS_mm = 2097156;
   public static final int UNITS_µm = 1048580;
   public static final int UNITS_pm = 262148;
   public static int DEFAULT_16BIT_COLOR = 32767;
   public static final long DEFAULT_COLOR_ATTRIBS = (long)DEFAULT_16BIT_COLOR << 32 | (long)DEFAULT_16BIT_COLOR << 48;
   private static long _pxScale = 1000000000000L / Display.getVerticalResolution();
   private static long _ptdScale = _pxScale <= 317500000 ? 352777778 : _pxScale;
   private static final OptionsRegistry$IntParameter _mode = UiOptionsRegistry.getInstance().getIntParam(-7317849688793253196L);
   private static final OptionsRegistry$IntParameter _increaseDirection = UiOptionsRegistry.getInstance().getIntParam(7732797588618697066L);
   private static final OptionsRegistry$IntParameter _trackballClickAction = UiOptionsRegistry.getInstance().getIntParam(-792512479819739610L);
   private static final OptionsRegistry$IntParameter _trackwheelClickAction = UiOptionsRegistry.getInstance().getIntParam(-4786794427536080535L);
   static Runnable nullRunnable;

   private Ui() {
   }

   public static final void addUiEngineListener(UiEngineListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      UiEngineImpl.getUiEngine().addUiEngineListener(listener);
   }

   public static final int convertColorTo16bit(int color) {
      int ret = (color & 248) >> 3 | (color & 64512) >> 5 | (color & 16252928) >> 8;
      if (ret == DEFAULT_16BIT_COLOR) {
         ret--;
      }

      return ret;
   }

   public static final int convertColorFrom16bit(int color, int defaultColor) {
      return color == DEFAULT_16BIT_COLOR
         ? defaultColor
         : (color & 31) << 3 | (color & 28) >> 2 | (color & 2016) << 5 | (color & 1536) >> 1 | (color & 63488) << 8 | (color & 57344) << 3;
   }

   public static final int convertSize(int size, int inUnits, int outUnits) {
      long outScale = convertSizeGetScale(inUnits);
      long inScale = convertSizeGetScale(outUnits);
      if (inUnits == outUnits) {
         return size;
      }

      if (((outScale | inScale) & 255) == 0) {
         outScale >>= 8;
         inScale >>= 8;
      }

      long round = (inScale >> 1) * (size >= 0 ? 1 : -1);
      long convertedLong = (size * outScale + round) / inScale;
      if (convertedLong <= Integer.MAX_VALUE && convertedLong >= Integer.MIN_VALUE) {
         return (int)convertedLong;
      } else {
         throw new NumericOverflowException("Ui.convertSize(): Converted integer exceeded integer boundaries");
      }
   }

   private static final long convertSizeGetScale(int units) {
      long scale = 0;
      switch (units) {
         case 0:
            return _pxScale;
         case 2:
            return 352777778;
         case 3:
            return _ptdScale;
         case 4:
            return 1000000000000L;
         case 262148:
            return 1;
         case 1048580:
            return 1000000;
         case 2097156:
            return 1000000000;
         case 4194306:
            return 3527778;
         case 4194307:
            return _ptdScale / 100;
         case 4194308:
            return 10000000000L;
         default:
            throw new IllegalArgumentException("Unrecognized unit");
      }
   }

   public static final long getAttributesFromFont(Font font) {
      long attribs = 0;
      int fontIndex = FontRegistry.getInstance().getTypefaceNameIndex(font.getFontFamily().getName());
      attribs |= fontIndex << 10;
      int height = font.getHeight();
      if (height > 63) {
         height = 63;
      }

      attribs |= height << 0;
      int style = font.getStyle();
      if ((style & 1) != 0) {
         attribs |= 256;
      }

      if ((style & 2) != 0) {
         attribs |= 512;
      }

      if ((style & 64) != 0) {
         attribs |= 134217984;
      }

      if ((style & 8) != 0) {
         attribs |= 786432;
      } else if ((style & 16) != 0) {
         attribs |= 786432;
      } else if ((style & 4) != 0) {
         attribs |= 262144;
      }

      if ((style & 32) != 0) {
         attribs |= 1048576;
      }

      switch (font.getAntialiasMode()) {
         case 2:
         default:
            return attribs | 64;
         case 3:
            return attribs | 128;
         case 4:
            attribs |= 192;
         case 1:
            return attribs;
      }
   }

   public static final Font getFontFromAttributes(long attrib, Font defaultFont) {
      int fontIndex = AttributedString.fontNameIndex(attrib);
      FontFamily family = FontRegistry.get(FontRegistry.getTypefaceNameByIndex(fontIndex));
      int height = AttributedString.fontHeight(attrib);
      int style = 0;
      if ((attrib & 134217984) == 256) {
         style |= 1;
      } else if ((attrib & 134217984) == 134217984) {
         style |= 64;
      }

      if ((attrib & 512) != 0) {
         style |= 2;
      }

      if ((attrib & 786432) != 0) {
         style &= -29;
         long underline = attrib & 786432;
         if (underline == 524288) {
            style |= 20;
         } else if (underline == 262144) {
            style |= 4;
         } else if (underline == 786432) {
            style |= 12;
         }
      }

      if ((attrib & 3145728) != 0) {
         style &= -33;
         long strikethrough = attrib & 3145728;
         if (strikethrough == 1048576) {
            style |= 32;
         }
      }

      long antialias = attrib & 192;
      int antialiasMode;
      if (antialias == 64) {
         antialiasMode = 2;
      } else if (antialias == 128) {
         antialiasMode = 3;
      } else if (antialias == 192) {
         antialiasMode = 4;
      } else {
         antialiasMode = 1;
      }

      style |= defaultFont.getStyle() & 7168;
      return family.getFont(style, height, 0, antialiasMode, 0);
   }

   public static final int getGlobalScreenCount() {
      return GlobalScreenManager.getVisibleScreenCount();
   }

   public static final int getIncreaseDirection() {
      if (UiOptionsRegistry.getInstance().getBoolean(-920146301111564303L)) {
         return _increaseDirection.getValue();
      } else {
         return Trackball.isSupported() ? -1 : 1;
      }
   }

   public static final int getTrackballClickAction() {
      return _trackballClickAction.getValue();
   }

   public static final int getTrackwheelClickAction() {
      return _trackwheelClickAction.getValue();
   }

   public static final int getMode() {
      return _mode.getValue();
   }

   public static final DrawTextParam getTmpDrawTextParam() {
      try {
         return (DrawTextParam)_tmpDrawStack.pop();
      } catch (EmptyStackException e) {
         return new DrawTextParam();
      }
   }

   public static final TextMetrics getTmpTextMetrics() {
      try {
         return (TextMetrics)_tmpTextMetricsStack.pop();
      } catch (EmptyStackException e) {
         return new TextMetrics();
      }
   }

   public static final XYRect getTmpXYRect() {
      try {
         return (XYRect)_tmpXYRectStack.pop();
      } catch (EmptyStackException e) {
         return new XYRect();
      }
   }

   public static final void removeUiEngineListener(UiEngineListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      UiEngineImpl.getUiEngine().removeUiEngineListener(listener);
   }

   public static final void returnTmpDrawTextParam(DrawTextParam param) {
      param.reset();
      _tmpDrawStack.push(param);
   }

   public static final void returnTmpTextMetrics(TextMetrics metrics) {
      metrics.reset();
      _tmpTextMetricsStack.push(metrics);
   }

   public static final void returnTmpXYRect(XYRect rect) {
      rect.set(0, 0, 0, 0);
      _tmpXYRectStack.push(rect);
   }

   public static final native boolean isStylusSupported();

   public static final void setTrackballClickAction(int style) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      UiOptionsRegistry.getInstance().setInt(-792512479819739610L, style);
   }

   public static final void setTrackwheelClickAction(int style) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      UiOptionsRegistry.getInstance().setInt(-4786794427536080535L, style);
   }

   public static final void setMode(int mode) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (mode >= 0 && 2 >= mode) {
         UiOptionsRegistry.getInstance().setInt(-7317849688793253196L, mode);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final void setIncreaseDirection(int direction) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      if (direction != -1 && direction != 1) {
         throw new IllegalArgumentException();
      }

      UiOptionsRegistry registry = UiOptionsRegistry.getInstance();
      registry.setInt(7732797588618697066L, direction);
      registry.setBoolean(-920146301111564303L, true);
   }

   public static final void setNewInvalidate(boolean newInvalidate) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(2));
      UiOptionsRegistry.getInstance().setBoolean(-9149825875359456202L, newInvalidate);
   }

   public static final UiEngine getUiEngine() {
      return UiEngineImpl.getUiEngine();
   }

   public static final void setInternalListener(UiInternalListener listener) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      GlobalScreenManager.setUiInternalListener(listener);
   }

   static final native void setCurrentGlobalStatus(Screen var0, XYRect var1);

   public static final boolean isTTSEnabled() {
      return GlobalScreenManager.getAccessibleEventListener() != null;
   }

   public static final void setAccessibleEventListener(AccessibleEventListener listener) {
      ApplicationControl.assertScreenCapturePermitted(true, CommonResource.getBundle(), 10176);
      GlobalScreenManager.setAccessibleEventListener(listener);
   }

   public static final void removeAccessibleEventListener(AccessibleEventListener listener) {
      ApplicationControl.assertScreenCapturePermitted(true, CommonResource.getBundle(), 10176);
      GlobalScreenManager.removeAccessibleEventListener(listener);
   }

   static {
      EventLogger.register(-4685663286194863677L, "UI", 2);
      int up = getIncreaseDirection();
      if (up == 0) {
         setIncreaseDirection(1);
      }

      nullRunnable = new Ui$NullRunnable();
   }
}
