package net.rim.device.api.ui;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextInputDialog;
import net.rim.device.api.ui.menu.MenuItemPrefab;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.Background;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.Cursor;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.ComponentEvent;
import net.rim.tid.awt.event.FocusEvent;
import net.rim.tid.awt.event.InputMethodEvent;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.InputMethodRequests;
import net.rim.tid.itie.EventHandler;
import net.rim.tid.itie.IComponent;
import net.rim.vm.TraceBack;

public class Field implements IComponent, AccessibleContext {
   private Manager _manager;
   private XYRect _extent = new XYRect();
   private XYRect _content = new XYRect();
   private Object _cookie;
   private int _index = -1;
   private long _style;
   private int _state = 64;
   private FieldChangeListener _changeListener;
   private FocusChangeListener _focusListener;
   private Font _font;
   private Font _fontSet;
   private ThemeAttributeSet _themeAttributes;
   private ThemeAttributeSet _themeAttributesAll;
   private ThemeAttributeSet _themeAttributesFocus;
   private ThemeAttributeSet _themeAttributesSpecial;
   private Tag _tag;
   private String _idName;
   private boolean _inDrawHighlightRegion;
   private boolean _borderSet;
   private int _borderTop;
   private int _borderRight;
   private int _borderBottom;
   private int _borderLeft;
   private Border _border;
   private boolean _marginSet;
   private int _marginTop;
   private int _marginRight;
   private int _marginBottom;
   private int _marginLeft;
   private boolean _paddingSet;
   private int _paddingTop;
   private int _paddingRight;
   private int _paddingBottom;
   private int _paddingLeft;
   private InputContext _inputContext;
   private Field$UpdateLayoutRunnable _layoutRunnable;
   private int _accessibleStateSet = 1;
   private static Tag TAG = Tag.create("object");
   public static final int SYSTEM_STYLE_SHIFT = 32;
   protected static final long FIELD_HALIGN_MASK = 12884901888L;
   public static final long FIELD_LEFT = 4294967296L;
   public static final long FIELD_RIGHT = 8589934592L;
   public static final long FIELD_HCENTER = 12884901888L;
   protected static final long FIELD_VALIGN_MASK = 51539607552L;
   public static final long FIELD_TOP = 17179869184L;
   public static final long FIELD_BOTTOM = 34359738368L;
   public static final long FIELD_VCENTER = 51539607552L;
   public static final long USE_ALL_WIDTH = 1152921504606846976L;
   public static final long USE_ALL_HEIGHT = 2305843009213693952L;
   protected static final long EDITABLE_MASK = 13510798882111488L;
   public static final long EDITABLE = 4503599627370496L;
   public static final long READONLY = 9007199254740992L;
   private static final long EDITABLE_DEFAULT = 9007199254740992L;
   protected static final long SPELLCHECKABLE_MASK = 3298534883328L;
   public static final long SPELLCHECKABLE = 1099511627776L;
   public static final long NON_SPELLCHECKABLE = 2199023255552L;
   private static final long SPELLCHECKABLE_DEFAULT = 2199023255552L;
   protected static final long FOCUSABLE_MASK = 54043195528445952L;
   public static final long FOCUSABLE = 18014398509481984L;
   public static final long NON_FOCUSABLE = 36028797018963968L;
   private static final long FOCUSABLE_DEFAULT = 36028797018963968L;
   public static final int HIGHLIGHT_FOCUS = 1;
   public static final int HIGHLIGHT_SELECT = 2;
   private static final int STATE_DIRTY = 1;
   private static final int STATE_MUDDY = 2;
   private static final int STATE_FOCUS = 4;
   private static final int STATE_CLIPBOARD_ENABLED = 8;
   private static final int STATE_CLIPBOARD_DETERMINED = 16;
   private static final int STATE_THEME_SPECIAL_CLEAR = 32;
   private static final int STATE_IM_ENABLED = 64;
   private static final int STATE_LAYOUT_SETPOSITION = 128;
   private static final int STATE_LAYOUT_SETEXTENT = 256;
   private static final int STATE_DEFAULT = 64;
   public static final long OPAQUE = 137438953472L;
   public static final long RIGHT_TO_LEFT = 274877906944L;
   public static final long LEFT_TO_RIGHT = 549755813888L;
   public static final int ACTION_INVOKE = 1;
   public static final int STATUS_MOVE_FOCUS_HORIZONTALLY = 65536;
   public static final int STATUS_MOVE_FOCUS_VERTICALLY = 131072;
   public static final int AXIS_SEQUENTIAL = 0;
   public static final int AXIS_HORIZONTAL = 1;
   public static final int AXIS_VERTICAL = 2;
   public static final int DEBUG_NO_EXTENT = 1;
   public static final int DEBUG_PACKAGE = 2;
   public static final int DEBUG_NO_TAG = 4;
   public static final int DEBUG_NO_FOCUS = 8;

   protected void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   public boolean acceptVisitor(FieldVisitor visitor) {
      return visitor.visit(this, 3);
   }

   public int adjustVolume(int volumeLevelChange) {
      return -1;
   }

   void assertLayoutComplete() {
      if (!this.isState(384)) {
         int moduleName = ApplicationDescriptor.currentApplicationDescriptor().getName().hashCode();
         switch (moduleName) {
            case 325008992:
            case 1416671651:
               return;
            default:
               throw new IllegalStateException("Field must call setExtent and setPosition during layout.");
         }
      }
   }

   protected void removeAccessibleState(int state) {
      this._accessibleStateSet &= ~state;
   }

   protected void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleStateSet |= state;
   }

   void callOnObscured() {
      this.onObscured();
   }

   protected void onObscured() {
   }

   void callOnExposed() {
      this.onExposed();
   }

   protected void onExposed() {
   }

   protected void onUndisplay() {
   }

   protected void onDisplay() {
   }

   protected void applyFont() {
      this.assertHaveEventLock();
      if (this._fontSet != null) {
         this._font = this._fontSet;
      } else {
         Font font = null;
         if (this.getState() == 6 && this._themeAttributesFocus != null) {
            font = this._themeAttributesFocus.getFont();
         }

         if (font == null && this._themeAttributes != null) {
            font = this._themeAttributes.getFont();
         }

         if (font == null) {
            Manager manager = this.getManager();
            if (manager != null) {
               font = manager._font;
            }

            if (font == null) {
               font = Font.getDefault();
            }
         }

         this._font = font;
      }
   }

   protected void applyTheme() {
      this.assertHaveEventLock();
      Theme theme = ThemeManager.getActiveTheme();
      this._themeAttributesAll = theme.getAttributeSet(this, 0);
      this._themeAttributesFocus = theme.getAttributeSet(this, 6);
      this.applyThemeOnStateChange();
   }

   protected void applyThemeOnStateChange() {
      this.assertHaveEventLock();
      ThemeAttributeSet tas = null;
      if (this.getState() == 6 && this._themeAttributesFocus != null) {
         tas = this._themeAttributesFocus;
      } else {
         tas = this._themeAttributesAll;
      }

      if (tas != this._themeAttributes) {
         this._themeAttributes = tas;
         if (!this._paddingSet) {
            XYEdges edges = ThemeAttributeSet.getEdges(this, 0);
            if (edges != null) {
               this._paddingTop = edges.top;
               this._paddingRight = edges.right;
               this._paddingBottom = edges.bottom;
               this._paddingLeft = edges.left;
            } else {
               this._paddingTop = this._paddingRight = this._paddingBottom = this._paddingLeft = 0;
            }
         }

         if (!this._borderSet) {
            XYEdges edges = ThemeAttributeSet.getEdges(this, 1);
            if (edges != null) {
               this._borderTop = edges.top;
               this._borderRight = edges.right;
               this._borderBottom = edges.bottom;
               this._borderLeft = edges.left;
               this._border = ThemeAttributeSet.getBorder(this);
            } else {
               this._borderTop = this._borderRight = this._borderBottom = this._borderLeft = 0;
               this._border = null;
            }
         }

         if (!this._marginSet) {
            XYEdges edges = ThemeAttributeSet.getEdges(this, 2);
            if (edges != null) {
               this._marginTop = edges.top;
               this._marginRight = edges.right;
               this._marginBottom = edges.bottom;
               this._marginLeft = edges.left;
            } else {
               this._marginTop = this._marginRight = this._marginBottom = this._marginLeft = 0;
            }
         }

         this.applyFont();
         this.invalidate();
      }
   }

   protected void applyTheme(Graphics graphics, boolean drawBackground) {
      this.assertHaveEventLock();
      if (this._themeAttributes != null) {
         this._themeAttributes.applyToGraphics(graphics);
      }

      if (this._themeAttributesSpecial != null) {
         this._themeAttributesSpecial.applyToGraphics(graphics);
      }

      if (drawBackground) {
         this.paintBackground(graphics);
      }

      graphics.setFont(this.getFont());
   }

   final void applyThemeSpecial(Graphics graphics, boolean drawBackground) {
      this.assertHaveEventLock();
      Font font = null;
      if (this._themeAttributesSpecial != null) {
         this._themeAttributesSpecial.applyToGraphics(graphics);
         font = this._themeAttributesSpecial.getFont();
         boolean drawFocus = graphics.isDrawingStyleSet(8);
         boolean drawSelect = graphics.isDrawingStyleSet(16);
         if (!drawFocus && !drawSelect) {
            if (drawBackground) {
               this.paintBackground(graphics);
            }
         } else {
            if (drawFocus) {
               graphics.setColor(ThemeAttributeSet.getColor(this, 3));
               graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 2));
            } else {
               graphics.setColor(ThemeAttributeSet.getColor(this, 5));
               graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 4));
            }

            graphics.setBackgroundImage(null, 0, 0);
            if (drawBackground) {
               graphics.clear();
            }
         }
      }

      if (font == null) {
         font = this.getFont();
      }

      graphics.setFont(font);
   }

   void callOnDisplayOrUndisplay(boolean attached) {
      if (attached) {
         this.onDisplay();
      } else {
         this.onUndisplay();
      }
   }

   public void setThemeAttributeSet(ThemeAttributeSet themeAttributes) {
      this._themeAttributes = themeAttributes;
      this._themeAttributesAll = themeAttributes;
   }

   protected void setThemeAttributesAll(ThemeAttributeSet themeAttributes, ThemeAttributeSet themeAttributesFocus) {
      this._themeAttributesAll = themeAttributes;
      this._themeAttributesFocus = themeAttributesFocus;
   }

   public void setThemeAttributesSpecial(ThemeAttributeSet themeAttributesSpecial, Graphics graphics) {
      this._themeAttributesSpecial = themeAttributesSpecial;
      if (graphics != null) {
         this.applyThemeSpecial(graphics, this.isState(32));
      }
   }

   public void setThemeAttributesSpecialClear(boolean setThemeAttributesSpecialClear) {
      this.setState(32, setThemeAttributesSpecialClear);
   }

   protected void drawFocus(Graphics graphics, boolean on) {
      XYRect rect = Ui.getTmpXYRect();
      this.getFocusRect(rect);
      this.drawHighlightRegion(graphics, 1, on, rect.x, rect.y, rect.width, rect.height);
      Ui.returnTmpXYRect(rect);
   }

   protected final void drawHighlightRegion(Graphics graphics, int style, boolean on, int x, int y, int width, int height) {
      if (!this._inDrawHighlightRegion) {
         this._inDrawHighlightRegion = true;
         label122:
         switch (ThemeAttributeSet.getFocusStyle(this)) {
            case -1:
               break;
            case 0:
            default:
               switch (style) {
                  case -1:
                     break label122;
                  case 0:
                     throw new IllegalArgumentException();
                  case 1:
                  case 2:
                  case 3:
                  default:
                     graphics.invert(x, y, width, height);
                     break label122;
               }
            case 1:
               switch (style) {
                  case -1:
                     break label122;
                  case 0:
                     throw new IllegalArgumentException();
                  case 2:
                     graphics.invert(x, y, width, height);
                     break label122;
                  case 3:
                  default:
                     graphics.invert(x, y, width, height);
                  case 1:
                     int mid = y + (height >> 1);
                     graphics.invert(x, mid, width, 1);
                     break label122;
               }
            case 2:
               if (on) {
                  switch (style) {
                     case -1:
                        break label122;
                     case 0:
                        throw new IllegalArgumentException();
                     case 1:
                        graphics.drawRect(x, y, width, height);
                        break label122;
                     case 3:
                     default:
                        graphics.drawRect(x, y, width, height);
                     case 2:
                        graphics.invert(x, y, width, height);
                  }
               } else {
                  boolean notEmpty = graphics.pushContext(x, y, width, height, 0, 0);
                  if (notEmpty) {
                     if (on) {
                        graphics.setDrawingStyle(8, (style & 1) != 0);
                        graphics.setDrawingStyle(16, (style & 2) != 0);
                     }

                     graphics.clear();
                     int depth = graphics.getContextStackSize();
                     this.paint(graphics);
                     if (graphics.getContextStackSize() > depth) {
                        throw new IllegalStackSizeException("push(dh1_ib)", this.getClass(), graphics, depth);
                     }

                     if (graphics.getContextStackSize() < depth) {
                        throw new IllegalStackSizeException("pop(dhl_ib)", this.getClass(), graphics, depth);
                     }
                  }

                  graphics.popContext();
               }
               break;
            case 3:
               boolean notEmpty = graphics.pushContext(x, y, width, height, 0, 0);
               if (notEmpty) {
                  if (!on) {
                     if (!this.isFocusDrawn()) {
                        graphics.clear();
                     }
                  } else {
                     graphics.setDrawingStyle(8, (style & 1) != 0);
                     graphics.setDrawingStyle(16, (style & 2) != 0);
                     switch (style) {
                        case -1:
                           break;
                        case 0:
                           throw new IllegalArgumentException();
                        case 1:
                        case 3:
                        default:
                           graphics.setColor(ThemeAttributeSet.getColor(this, 3));
                           graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 2));
                           break;
                        case 2:
                           graphics.setColor(ThemeAttributeSet.getColor(this, 5));
                           graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 4));
                     }

                     graphics.setBackgroundImage(null, 0, 0);
                     graphics.clear();
                  }

                  int depth = graphics.getContextStackSize();
                  this.paint(graphics);
                  if (graphics.getContextStackSize() > depth) {
                     throw new IllegalStackSizeException("push(dhl_d)", this.getClass(), graphics, depth);
                  }

                  if (graphics.getContextStackSize() < depth) {
                     throw new IllegalStackSizeException("pop(dhl_d)", this.getClass(), graphics, depth);
                  }
               }

               graphics.popContext();
               break;
            case 4:
               boolean notEmpty = graphics.pushContext(x, y, width, height, 0, 0);
               if (notEmpty) {
                  if (on) {
                     graphics.setDrawingStyle(8, (style & 1) != 0);
                     graphics.setDrawingStyle(16, (style & 2) != 0);
                  }

                  Background background;
                  if ((background = ThemeAttributeSet.getBackground(this)) != null) {
                     background.draw(graphics, this._content);
                  } else {
                     graphics.clear();
                  }

                  int depth = graphics.getContextStackSize();
                  this.paint(graphics);
                  if (graphics.getContextStackSize() > depth) {
                     throw new IllegalStackSizeException("push(dhl_c)", this.getClass(), graphics, depth);
                  }

                  if (graphics.getContextStackSize() < depth) {
                     throw new IllegalStackSizeException("pop(dhl_c)", this.getClass(), graphics, depth);
                  }
               }

               graphics.popContext();
         }

         this._inDrawHighlightRegion = false;
      }
   }

   protected void fieldChangeNotify(int context) {
      if ((context & -2147483648) != 0) {
         this.setDirty(false);
      } else {
         this.setMuddy(true);
      }

      if (this._changeListener != null) {
         try {
            this._changeListener.fieldChanged(this, context);
            return;
         } catch (Throwable var3) {
         }
      }
   }

   protected void focusAdd(boolean draw) {
      if (this instanceof Manager) {
         throw new IllegalArgumentException("Field.focusAdd called on non-leaf field.");
      }

      Screen screen = this.getScreen();
      if (screen != null) {
         screen.doAddFocus(draw);
      }
   }

   public void focusChangeNotify(int action) {
      Screen screen = this.getScreen();
      if (action == 1
         && screen != null
         && screen.isDisplayed()
         && !(screen instanceof TextInputDialog)
         && screen.isScreenFocus()
         && (this instanceof Screen || !(this instanceof Manager))) {
         EventHandler.getInstance().focusGained(this, (int)System.currentTimeMillis(), Application.getApplication().getProcessId());
      }

      if (this._focusListener != null) {
         try {
            this._focusListener.focusChanged(this, action);
         } catch (Throwable var4) {
         }
      }

      if (screen != null) {
         screen.focusChangeNotifyListeners(this, action);
      }
   }

   protected void focusRemove() {
      if (this instanceof Manager) {
         throw new IllegalArgumentException("Field.focusRemove called on non-leaf field.");
      }

      Screen screen = this.getScreen();
      if (screen != null) {
         screen.doRemoveFocus();
      }
   }

   public final Border getBorder() {
      return this._border;
   }

   public final void getBorder(XYEdges border) {
      border.top = this._borderTop;
      border.right = this._borderRight;
      border.bottom = this._borderBottom;
      border.left = this._borderLeft;
   }

   public final int getBorderBottom() {
      return this._borderBottom;
   }

   public final int getBorderLeft() {
      return this._borderLeft;
   }

   public final int getBorderRight() {
      return this._borderRight;
   }

   public final int getBorderTop() {
      return this._borderTop;
   }

   public FieldChangeListener getChangeListener() {
      return this._changeListener;
   }

   public final int getContentHeight() {
      return this._content.height;
   }

   public final int getContentLeft() {
      return this._content.x;
   }

   public final XYRect getContentRect() {
      return new XYRect(this._content);
   }

   public final void getContentRect(XYRect rect) {
      rect.set(this._content);
   }

   public final int getContentTop() {
      return this._content.y;
   }

   public final int getContentWidth() {
      return this._content.width;
   }

   public ContextMenu getContextMenu() {
      ContextMenu contextMenu = ContextMenu.getInstance();
      contextMenu.setTarget(this);
      this.makeContextMenu(contextMenu);
      this.addIMActions(contextMenu, 0);
      return contextMenu;
   }

   public ContextMenu getContextMenu(int instance) {
      ContextMenu contextMenu = ContextMenu.getInstance();
      contextMenu.setTarget(this);
      this.makeContextMenu(contextMenu, instance);
      this.addIMActions(contextMenu, instance);
      return contextMenu;
   }

   public final Object getCookie() {
      return this.getCookieInternal();
   }

   protected Object getCookieInternal() {
      return this._cookie;
   }

   public String getDebugTree(int treeStyle) {
      StringBuffer buffer = new StringBuffer();
      this.getDebugTreeHelper(treeStyle, buffer, 0);
      return buffer.toString();
   }

   void getDebugTreeHelper(int treeStyle, StringBuffer buffer, int indent) {
      for (int index = 4 * indent - 1; index >= 0; index--) {
         buffer.append(' ');
      }

      String className = this.getClass().getName();
      int dot = className.lastIndexOf(46);
      if ((treeStyle & 2) == 0 && dot >= 0) {
         className = className.substring(dot + 1);
      }

      buffer.append(className);
      if ((treeStyle & 1) == 0) {
         buffer.append(' ');
         buffer.append('(');
         buffer.append(this.getLeft());
         buffer.append(',');
         buffer.append(' ');
         buffer.append(this.getTop());
         buffer.append(')');
         buffer.append('+');
         buffer.append('(');
         buffer.append(this.getWidth());
         buffer.append(',');
         buffer.append(' ');
         buffer.append(this.getHeight());
         buffer.append(')');
         if (this instanceof Manager) {
            Manager manager = (Manager)this;
            buffer.append(' ');
            buffer.append('[');
            buffer.append(manager.getVirtualWidth());
            buffer.append(',');
            buffer.append(' ');
            buffer.append(manager.getVirtualHeight());
            buffer.append(']');
            if (manager.isStyle(281474976710656L)) {
               buffer.append(' ');
               buffer.append('V');
               buffer.append('+');
               buffer.append(manager.getVerticalScroll());
            }

            if (manager.isStyle(1125899906842624L)) {
               buffer.append(' ');
               buffer.append('H');
               buffer.append('+');
               buffer.append(manager.getHorizontalScroll());
            }
         }
      }

      if ((treeStyle & 4) == 0) {
         buffer.append(' ');
         buffer.append('"');
         buffer.append(this.getTag().toString());
         buffer.append('"');
      }

      if ((treeStyle & 8) == 0) {
         Manager manager = this.getManager();
         if (manager != null && manager.getFieldWithFocus() == this) {
            buffer.append(" *focus*");
         }
      }

      buffer.append('\n');
   }

   public final XYRect getExtent() {
      return this._extent;
   }

   public final void getExtent(XYRect extent) {
      extent.set(this._extent);
   }

   public final void assertHaveEventLock() {
      Screen screen = this.getScreen();
      if (screen != null) {
         UiEngineImpl engine = screen.getUiEngineImpl();
         if (engine != null) {
            engine.assertHaveEventLock();
         }
      }
   }

   public FocusChangeListener getFocusListener() {
      return this._focusListener;
   }

   public void getFocusRect(XYRect rect) {
      rect.set(this._extent.x - this._content.x, this._extent.y - this._content.y, this._extent.width, this._extent.height);
   }

   public void getFocusRectPhantom(XYRect rect) {
      this.getFocusRect(rect);
   }

   void doVisibilityWalk(boolean visible) {
      this.onVisibilityChange(visible);
   }

   Font getFont0() {
      Field step = this;

      while (step._fontSet == null) {
         ThemeAttributeSet theme = step._themeAttributes;
         if (theme != null && theme.getFont() != null) {
            return theme.getFont();
         }

         step = step._manager;
         if (step == null) {
            return Font.getDefault();
         }
      }

      return step._fontSet;
   }

   public Font getFontIfSet() {
      return this._fontSet;
   }

   protected final Graphics getGraphics0() {
      XYRect clip = Ui.getTmpXYRect();
      clip.set(0, 0, this.getContentWidth(), this.getContentHeight());
      Graphics graphics = this.getGraphics0(clip, false);
      Ui.returnTmpXYRect(clip);
      return graphics;
   }

   Graphics getGraphics0(XYRect clip, boolean drawBackground) {
      clip.intersect(0, 0, this.getContentWidth(), this.getContentHeight());
      int offsetX = 0;
      int offsetY = 0;
      if (this instanceof Manager) {
         Manager manager = (Manager)this;
         offsetX = manager.getHorizontalScroll();
         offsetY = manager.getVerticalScroll();
         clip.translate(offsetX, offsetY);
      }

      this.transformToScreen(clip);
      boolean drawManagersBackground;
      Manager manager;
      if (!(this instanceof Screen)) {
         manager = this.getManager();
         drawManagersBackground = drawBackground && this.isFieldTransparent();
      } else {
         manager = (Screen)this;
         drawManagersBackground = false;
      }

      Graphics graphics = manager.getGraphics0(clip, drawManagersBackground);
      graphics.pushRegion(this.getContentRect(), -offsetX, -offsetY);
      this.applyTheme(graphics, drawBackground);
      return graphics;
   }

   public final int getHeight() {
      return this._extent.height;
   }

   public String getId() {
      return this._idName;
   }

   public final int getIndex() {
      return this._index;
   }

   public Field getLeafFieldWithFocus() {
      return this;
   }

   public final int getLeft() {
      return this._extent.x;
   }

   public final void getMargin(XYEdges margin) {
      margin.top = this._marginTop;
      margin.right = this._marginRight;
      margin.bottom = this._marginBottom;
      margin.left = this._marginLeft;
   }

   public final int getMarginBottom() {
      return this._marginBottom;
   }

   public final int getMarginLeft() {
      return this._marginLeft;
   }

   public final int getMarginRight() {
      return this._marginRight;
   }

   public final int getMarginTop() {
      return this._marginTop;
   }

   public final Manager getManager() {
      return this._manager;
   }

   public void getNextFocus(int direction, XYRect rect) {
      this.getFocusRect(rect);
   }

   public Field getOriginal() {
      return this;
   }

   public final void getPadding(XYEdges padding) {
      padding.top = this._paddingTop;
      padding.right = this._paddingRight;
      padding.bottom = this._paddingBottom;
      padding.left = this._paddingLeft;
   }

   public final int getPaddingBottom() {
      return this._paddingBottom;
   }

   public final int getPaddingLeft() {
      return this._paddingLeft;
   }

   public final int getPaddingRight() {
      return this._paddingRight;
   }

   public final int getPaddingTop() {
      return this._paddingTop;
   }

   public int getPreferredHeight() {
      return 0;
   }

   public int getPreferredWidth() {
      return 0;
   }

   public final Screen getScreen() {
      Field step = this;

      while (true) {
         Field parent = step.getManager();
         if (parent == null) {
            return !(step instanceof Screen) ? null : (Screen)step;
         }

         step = parent;
      }
   }

   public int getState() {
      return this.isState(4) ? 6 : 0;
   }

   public final long getStyle() {
      return this._style;
   }

   public Tag getTag() {
      return this._tag;
   }

   public final ThemeAttributeSet getThemeAttributeSet() {
      return this._themeAttributes;
   }

   public final ThemeAttributeSet getThemeAttributeSetSpecial() {
      return this._themeAttributesSpecial;
   }

   public final int getTop() {
      return this._extent.y;
   }

   public final int getWidth() {
      return this._extent.width;
   }

   protected void invalidate() {
      Manager manager = this.getManager();
      if (manager != null) {
         XYRect extent = this.getExtent();
         manager.invalidate(extent.x, extent.y, extent.width, extent.height);
      }
   }

   protected void invalidate(int x, int y, int width, int height) {
      XYRect content = Ui.getTmpXYRect();
      this.getContentRect(content);
      this.invalidateCommon(x, y, width, height, content);
      Ui.returnTmpXYRect(content);
   }

   protected void invalidateAll(int x, int y, int width, int height) {
      this.invalidateCommon(x, y, width, height, this.getExtent());
   }

   void invalidateLayout0() {
   }

   protected boolean invokeAction(int action) {
      return false;
   }

   public boolean isDataValid() {
      return true;
   }

   public boolean isDirty() {
      return this.isState(1);
   }

   public boolean isEditable() {
      return (this._style & 13510798882111488L) == 4503599627370496L;
   }

   boolean isFieldTransparent() {
      Background background = this.getBackground();
      return background == null || background.isTransparent();
   }

   public boolean isFocus() {
      return this.isState(4);
   }

   public boolean isFocusable() {
      return (this._style & 18014398509481984L) != 0;
   }

   protected boolean isFocusDrawn() {
      return true;
   }

   protected void onVisibilityChange(boolean visible) {
   }

   public boolean isMuddy() {
      return this.isState(2);
   }

   public boolean isPasteable() {
      return false;
   }

   public boolean isSelectable() {
      return false;
   }

   public boolean isSelecting() {
      return false;
   }

   public boolean isSelectionCopyable() {
      return false;
   }

   public final boolean isSelectionCutable() {
      return this.isSelectionCopyable() && this.isSelectionDeleteable();
   }

   public boolean isSelectionDeleteable() {
      return false;
   }

   public boolean isSpellCheckable() {
      return (this._style & 3298534883328L) == 1099511627776L;
   }

   public final boolean isStyle(long style) {
      return (this._style & style) == style;
   }

   public final boolean isVisible() {
      return this.isVisible0();
   }

   boolean isVisible0() {
      Screen scr = this.getScreen();
      return scr != null && scr.isVisible0();
   }

   protected boolean keyChar(char character, int status, int time) {
      if (this.isSelecting() && !(this instanceof Manager)) {
         switch (character) {
            case '\b':
            case '\u007f':
               if (this.isSelectionDeleteable()) {
                  if ((status & 2) != 0) {
                     if (this.isSelectionCutable() && this.isCutCopyPasteEnabled()) {
                        this.selectionCut(Clipboard.getClipboard());
                     }
                  } else {
                     this.selectionDelete();
                  }

                  this.select(false);
               }

               return true;
            case '\u001b':
               this.select(false);
               return true;
         }
      }

      return false;
   }

   protected boolean keyControl(char character, int status, int time) {
      if (this.isSelecting()) {
         switch (character) {
            case '\u001b':
               this.select(false);
               return true;
            case '\u007f':
               if (this.isSelectionDeleteable()) {
                  if ((status & 2) != 0) {
                     if (this.isSelectionCutable() && this.isCutCopyPasteEnabled()) {
                        this.selectionCut(Clipboard.getClipboard());
                        Clipboard.getClipboard().setNotYetPasted(true);
                     }
                  } else {
                     this.selectionDelete();
                  }

                  this.select(false);
               }

               return true;
         }
      }

      return false;
   }

   protected boolean keyDown(int keycode, int time) {
      return false;
   }

   protected boolean keyRepeat(int keycode, int time) {
      return false;
   }

   protected boolean keyStatus(int keycode, int time) {
      return false;
   }

   protected boolean keyUp(int keycode, int time) {
      return false;
   }

   protected void layout(int _1, int _2) {
      throw null;
   }

   protected void makeContextMenu(ContextMenu contextMenu) {
      this.makeContextMenu(contextMenu, 0);
   }

   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      if (this.isSelectable()) {
         if (this.isSelecting()) {
            contextMenu.addItem(MenuItem.getPrefab(5));
         } else if (!this.getScreen().isScrollBehaviourView() && instance != 65536) {
            contextMenu.addItem(MenuItem.getPrefab(4));
         }
      }

      if (this.isSelectionCopyable() && this.isCutCopyPasteEnabled()) {
         MenuItem copyItem = MenuItem.getPrefab(1);
         contextMenu.addItem(copyItem);
         if (this.isSelecting()) {
            contextMenu.setDefaultItem(copyItem);
         }
      }

      if (this.isSelectionCutable() && this.isCutCopyPasteEnabled()) {
         contextMenu.addItem(MenuItem.getPrefab(2));
      }

      if (this.isPasteable() && Clipboard.getClipboard().get() != null && this.isCutCopyPasteEnabled()) {
         MenuItem pasteItem = MenuItem.getPrefab(3);
         contextMenu.addItem(pasteItem);
         Clipboard clip = Clipboard.getClipboard();
         if (clip.isNotYetPasted() && clip.isTimeForPasteAsDefaultNotPassed()) {
            contextMenu.setDefaultItem(pasteItem);
         }
      }
   }

   protected void makeMenu(Menu menu, int instance) {
   }

   protected void onMenuDismissed(Menu menu) {
      this.onMenuDismissed();
   }

   protected void onMenuDismissed() {
   }

   protected int moveFocus(int amount, int status, int time) {
      this.setState(0, 2);
      return amount;
   }

   protected void moveFocus(int x, int y, int status, int time) {
   }

   boolean moveFocusToPoint(int x, int y, int status, int time) {
      return this.isFocusable();
   }

   protected void onFocus(int direction) {
      this.setState(4, 0);
      this.applyThemeOnStateChange();
      if (Ui.isTTSEnabled()) {
         this.addAccessibleState(2);
         this.accessibleEventOccurred(1, null, new Integer(2), this);
      }
   }

   protected void onUnfocus() {
      this.setMuddy(false);
      if (this.isSelecting()) {
         this.select(false);
      }

      this.setState(0, 4);
      this.applyThemeOnStateChange();
   }

   protected void paint(Graphics _1) {
      throw null;
   }

   Background getBackground() {
      Background background = null;
      if (this._themeAttributesSpecial != null) {
         return this._themeAttributesSpecial.getBackground();
      }

      if (this._themeAttributes != null) {
         background = this._themeAttributes.getBackground();
      }

      return background;
   }

   protected void paintBackground(Graphics graphics) {
      Background background = this.getBackground();
      if (background != null) {
         XYRect rect = Ui.getTmpXYRect();
         rect.x = 0;
         rect.y = 0;
         if (!(this instanceof Manager)) {
            rect.width = this.getContentWidth();
            rect.height = this.getContentHeight();
         } else {
            Manager manager = (Manager)this;
            rect.width = Math.max(manager.getVirtualWidth(), this.getContentWidth() + this.getPaddingLeft() + this.getPaddingRight());
            rect.height = Math.max(manager.getVirtualHeight(), this.getContentHeight() + this.getPaddingTop() + this.getPaddingBottom());
            rect.height = rect.height + manager.getVerticalScroll();
         }

         rect.width = rect.width + this.getPaddingLeft() + this.getPaddingRight();
         rect.height = rect.height + this.getPaddingTop() + this.getPaddingBottom();
         background.draw(graphics, rect);
         Ui.returnTmpXYRect(rect);
      }
   }

   void paintBorder(Graphics graphics) {
      Border border = this.getBorder();
      if (border != null) {
         int fgPrevious = graphics.getColor();
         int bgPrevious = graphics.getBackgroundColor();
         graphics.setColor(ThemeAttributeSet.getColor(this, 1));
         graphics.setBackgroundColor(ThemeAttributeSet.getColor(this, 0));
         XYRect rect = Ui.getTmpXYRect();
         rect.set(this._extent);
         border.paint(graphics, rect);
         Ui.returnTmpXYRect(rect);
         graphics.setColor(fgPrevious);
         graphics.setBackgroundColor(bgPrevious);
      }
   }

   void paintSelf(Graphics graphics, boolean addExtent, int xContentAdjust, int yContentAdjust) {
      this.paintBorder(graphics);
      boolean paddingPushed = false;
      XYRect rect = Ui.getTmpXYRect();
      if (addExtent) {
         rect.x = this._extent.x + this._borderLeft;
         rect.y = this._extent.y + this._borderTop;
      } else {
         rect.x = this._borderLeft;
         rect.y = this._borderTop;
      }

      rect.width = this._extent.width - this._borderLeft - this._borderRight;
      rect.height = this._extent.height - this._borderTop - this._borderBottom;
      boolean notEmpty = graphics.pushRegion(rect, xContentAdjust, yContentAdjust);
      Ui.returnTmpXYRect(rect);
      if (notEmpty) {
         this.applyTheme(graphics, true);
         if (this.isPaddingDefined()) {
            rect = Ui.getTmpXYRect();
            rect.x = this._paddingLeft;
            rect.y = this._paddingTop;
            rect.width = this._content.width;
            rect.height = this._content.height;
            if (this instanceof Manager) {
               Manager manager = (Manager)this;
               rect.width = Math.max(manager.getVirtualWidth(), rect.width);
               rect.height = Math.max(manager.getVirtualHeight(), rect.height);
            }

            paddingPushed = true;
            notEmpty = graphics.pushRegion(rect);
            Ui.returnTmpXYRect(rect);
         }

         graphics.setStipple(-1);
         int depth = graphics.getContextStackSize();
         this.paint(graphics);
         if (graphics.getContextStackSize() > depth) {
            throw new IllegalStackSizeException("push(paint)", this.getClass(), graphics, depth);
         }

         if (graphics.getContextStackSize() < depth) {
            throw new IllegalStackSizeException("pop(paint)", this.getClass(), graphics, depth);
         }

         if (!(this instanceof Manager)
            && this._manager != null
            && this._manager.getFieldWithFocus() == this
            && Ui.DRAW_FOCUS_IN_PAINT
            && !Ui.IN_MAKE_REGION_VISIBLE) {
            graphics.setDrawingStyle(8, true);
            this.drawFocus(graphics, true);
            graphics.setDrawingStyle(8, false);
         }
      }

      graphics.popContext();
      if (paddingPushed) {
         graphics.popContext();
      }
   }

   public boolean paste(Clipboard cb) {
      return false;
   }

   public int processKeyEvent(int event, char key, int keycode, int time) {
      switch (event) {
         case 513:
         case 514:
         case 520:
            return EventHandler.getInstance().processKeyEvent(event, keycode, key, keycode, time, false);
         default:
            return 0;
      }
   }

   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return false;
   }

   protected final void setExtent(int width, int height) {
      if (this instanceof Screen) {
         Screen screen = (Screen)this;
         if (!screen.isInLayout()) {
            throw new IllegalStateException("setExtent called outside of layout");
         }
      }

      this._content.width = width;
      this._content.height = height;
      this.updateExtent();
      this.setState(256, 0);
   }

   boolean validateFieldStyle(long style) {
      if ((style & 54043195528445952L) == 54043195528445952L) {
         return false;
      } else if ((style & 13510798882111488L) == 13510798882111488L) {
         return false;
      } else {
         return (style & 3298534883328L) == 3298534883328L ? false : (style & -3526322837558132736L) == 0;
      }
   }

   public void setFont(Font font, boolean layout) {
      if (this._fontSet != font) {
         this._fontSet = font;
         if (this._font != null) {
            this.applyFont();
         }

         if (layout) {
            this.updateLayout();
         }
      }
   }

   protected final void setPosition(int x, int y) {
      if (this instanceof Screen) {
         Screen screen = (Screen)this;
         if (!screen.isInLayout()) {
            throw new IllegalStateException("setPosition called outside of layout");
         }
      }

      this._extent.x = x;
      this._extent.y = y;
      this._content.x = x + this._borderLeft + this._paddingLeft;
      this._content.y = y + this._borderTop + this._paddingTop;
      this.setState(128, 0);
   }

   final void setStyleSystem(long on, long off) {
      this._style |= on & -1;
      this._style &= off & -1 ^ -1;
   }

   public void setTag(Tag tag) {
      this._tag = tag;
      if (this._manager != null && this._manager.isValidLayout() && this.getScreen() != null) {
         this.applyTheme();
         this.updateLayout();
      }
   }

   public final void transformToScreen(XYRect rect) {
      rect.translate(this._content.x, this._content.y);
      if (this instanceof Manager) {
         Manager manager = (Manager)this;
         int dx = manager.getHorizontalScroll();
         int dy = manager.getVerticalScroll();
         rect.translate(-dx, -dy);
      }

      rect.intersect(this._content);

      for (Manager manager = this.getManager(); manager != null; manager = manager.getManager()) {
         Field field = manager;
         int dx = manager.getHorizontalScroll();
         int dy = manager.getVerticalScroll();
         rect.translate(field._content.x - dx, field._content.y - dy);
         rect.intersect(field._content);
      }
   }

   protected final void updateLayout() {
      this.assertHaveEventLock();
      this.updateLayoutHelper();
   }

   protected final void updateLayoutNowOrLater() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      Class[] stack = TraceBack.getCallingClasses();
      if (!stack[1].getName().startsWith("net.rim.device.api.ui")) {
         throw new ControlledAccessException();
      }

      if (!this.isEventLockRequired()) {
         this.updateLayoutHelper();
      } else {
         if (this._layoutRunnable == null) {
            this._layoutRunnable = new Field$UpdateLayoutRunnable(this);
         }

         this._layoutRunnable.invokeLater();
      }
   }

   public void select(boolean enable) {
   }

   public void selectionCopy(Clipboard cb) {
   }

   public void selectionCut(Clipboard cb) {
      this.selectionCopy(cb);
      this.selectionDelete();
   }

   public void selectionDelete() {
   }

   public void setBorder(int top, int right, int bottom, int left) {
      this._borderSet = true;
      this._borderTop = top;
      this._borderRight = right;
      this._borderBottom = bottom;
      this._borderLeft = left;
      this.updateLayout();
   }

   public void setBorder(XYEdges border) {
      this._borderSet = true;
      this._borderTop = border.top;
      this._borderRight = border.right;
      this._borderBottom = border.bottom;
      this._borderLeft = border.left;
      this.updateLayout();
   }

   public void setBorder(Border border) {
      this.setBorderWithoutLayout(border);
      this.updateLayout();
   }

   public void setBorderWithoutLayout(Border border) {
      this._border = border;
      if (border != null) {
         this._borderSet = true;
         this._borderTop = border.getTop();
         this._borderRight = border.getRight();
         this._borderBottom = border.getBottom();
         this._borderLeft = border.getLeft();
      } else {
         this._borderSet = false;
         this._borderTop = this._borderRight = this._borderBottom = this._borderLeft = 0;
      }
   }

   public void setBorder(int state, Border border) {
      if (state == 0) {
         this.setBorder(border);
      }
   }

   public void setChangeListener(FieldChangeListener listener) {
      if (listener != null && this._changeListener != null) {
         throw new IllegalStateException("Multiple change listeners not allowed.");
      }

      this._changeListener = listener;
   }

   public final void setCookie(Object cookie) {
      this.setCookieInternal(cookie);
   }

   protected void setCookieInternal(Object cookie) {
      this._cookie = cookie;
   }

   public void setDirty(boolean dirty) {
      this._state = dirty ? this._state | 1 : this._state & -4;
   }

   public void setEditable(boolean editable) {
      if (editable) {
         this._style |= 4503599627370496L;
         this._style &= -9007199254740993L;
      } else {
         this._style |= 9007199254740992L;
         this._style &= -4503599627370497L;
      }

      if (this.isFocus() && this.getInputContext() != null) {
         boolean isInputComponent = this.getInputContext().getInputComponent() == this;
         if (editable != isInputComponent) {
            FocusEvent updateFocus = new FocusEvent(this, 1004, Event.FOCUS_EVENT_MASK, 0);
            this.dispatchEvent(updateFocus);
         }
      }
   }

   public void setFocus() {
      Screen scr = this.getScreen();
      if (scr == null) {
         throw new IllegalStateException("setFocus called on a field that is not attached to a screen.");
      }

      if (this.isFocusable()) {
         scr.setFocus(this);
      }
   }

   public void setFocusListener(FocusChangeListener listener) {
      if (listener != null && this._focusListener != null) {
         throw new IllegalStateException("Multiple focus listeners not allowed.");
      }

      this._focusListener = listener;
   }

   public void setId(String idName) {
      if (idName != null) {
         for (int lv = idName.length() - 1; lv >= 0; lv--) {
            char ch = idName.charAt(lv);
            if (!Character.isLowerCase(ch) && (ch != '-' || lv == 0)) {
               throw new IllegalArgumentException();
            }
         }
      }

      this._idName = idName;
   }

   final void setIndex(int index) {
      this._index = index;
   }

   void setManager(Manager manager, int index) {
      if (manager == null) {
         if (this._manager == null) {
            throw new IllegalStateException("Field removed from a manager but it isn't parented.");
         }

         this._index = -1;
         this.invalidateLayout0();
         this._extent.set(0, 0, 0, 0);
      } else {
         if (this._manager != null) {
            throw new IllegalStateException("Field added to a manager while it is already parented.");
         }

         this._index = index;
      }

      this._manager = manager;
   }

   public void setMargin(int top, int right, int bottom, int left) {
      this._marginSet = true;
      this._marginTop = top;
      this._marginRight = right;
      this._marginBottom = bottom;
      this._marginLeft = left;
   }

   public void setMargin(XYEdges margin) {
      this._marginSet = true;
      this._marginTop = margin.top;
      this._marginRight = margin.right;
      this._marginBottom = margin.bottom;
      this._marginLeft = margin.left;
   }

   public void setPadding(int top, int right, int bottom, int left) {
      this._paddingSet = true;
      this._paddingTop = top;
      this._paddingRight = right;
      this._paddingBottom = bottom;
      this._paddingLeft = left;
      this.updateExtent();
   }

   public void setPadding(XYEdges padding) {
      this._paddingSet = true;
      this._paddingTop = padding.top;
      this._paddingRight = padding.right;
      this._paddingBottom = padding.bottom;
      this._paddingLeft = padding.left;
      this.updateExtent();
   }

   public void setMuddy(boolean muddy) {
      this._state = muddy ? this._state | 2 | 1 : this._state & -3;
   }

   public void setNonSpellCheckable(boolean nonSpellCheckable) {
      if (nonSpellCheckable) {
         this._style |= 2199023255552L;
         this._style &= -1099511627777L;
      } else {
         this._style |= 1099511627776L;
         this._style &= -2199023255553L;
      }
   }

   protected boolean stylusDown(int x, int y, int status, int time) {
      return false;
   }

   protected boolean stylusDrag(int x, int y, int status, int time) {
      return false;
   }

   protected boolean stylusUp(int x, int y, int status, int time) {
      return false;
   }

   protected boolean stylusTap(int x, int y, int status, int time) {
      return false;
   }

   protected boolean onCursorHover(int x, int y) {
      return false;
   }

   public Cursor getFocusCursor() {
      return Cursor.getPredefinedCursor(0);
   }

   protected boolean stylusDoubleTap(int x, int y, int status, int time) {
      return false;
   }

   protected boolean stylusTapHold(int x, int y, int status, int time) {
      return false;
   }

   protected boolean navigationClick(int status, int time) {
      if ((status & 2) != 0 && (status & 4) == 0 && this.isPasteable() && Clipboard.getClipboard().get() != null && this.isCutCopyPasteEnabled()) {
         this.paste(Clipboard.getClipboard());
         Clipboard.getClipboard().setNotYetPasted(false);
         return true;
      }

      if ((status & 1) == 0 || (status & 16) != 0 || !this.isSelectable()) {
         return false;
      }

      if (this.isSelecting()) {
         if (this.isSelectionCopyable() && this.isCutCopyPasteEnabled()) {
            this.selectionCopy(Clipboard.getClipboard());
            Clipboard.getClipboard().setNotYetPasted(true);
            this.select(false);
         }

         return true;
      } else {
         this.select(true);
         return true;
      }
   }

   protected boolean navigationUnclick(int status, int time) {
      return false;
   }

   protected boolean navigationMovement(int dx, int dy, int status, int time) {
      return false;
   }

   protected boolean trackwheelClick(int status, int time) {
      if ((status & 2) != 0 && (status & 4) == 0 && this.isPasteable() && Clipboard.getClipboard().get() != null && this.isCutCopyPasteEnabled()) {
         this.paste(Clipboard.getClipboard());
         Clipboard.getClipboard().setNotYetPasted(false);
         return true;
      }

      if ((status & 1) == 0 || (status & 16) != 0 || !this.isSelectable()) {
         return false;
      }

      if (this.isSelecting()) {
         if (this.isSelectionCopyable() && this.isCutCopyPasteEnabled()) {
            this.selectionCopy(Clipboard.getClipboard());
            Clipboard.getClipboard().setNotYetPasted(true);
            this.select(false);
         }

         return true;
      } else {
         this.select(true);
         return true;
      }
   }

   protected boolean trackwheelUnclick(int status, int time) {
      return false;
   }

   protected boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   @Override
   public void setFont(Font font) {
      this.setFont(font, true);
   }

   @Override
   public boolean isInputMethodEnabled() {
      return this.isState(64) && !this.isStyle(9007199254740992L);
   }

   public Font getFont() {
      return this._font == null ? this.getFont0() : this._font;
   }

   @Override
   public final int getFieldStyle() {
      return (int)this._style;
   }

   @Override
   public int caretPositionChanged(InputMethodEvent event) {
      return 1;
   }

   @Override
   public void setIMCookieCache(Object cookie) {
   }

   @Override
   public int inputMethodTextChanged(InputMethodEvent event) {
      return 1;
   }

   @Override
   public XYRect getBounds() {
      return null;
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      if (this.isState(64)) {
         if (rEvent instanceof ComponentEvent) {
            InputContext ic = this.getInputContext();
            if (ic != null) {
               ic.dispatchEvent(rEvent);
            }
         }
      }
   }

   @Override
   public void enableInputMethods(boolean enable) {
      this.setState(64, enable);
   }

   @Override
   public String getAccessibleName() {
      return null;
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return this.getScreen();
   }

   @Override
   public int getAccessibleChildCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      return null;
   }

   @Override
   public int getAccessibleStateSet() {
      return this._accessibleStateSet;
   }

   @Override
   public InputContext getInputContext() {
      if (this._inputContext == null) {
         this._inputContext = InputContext.getInstance();
      }

      return this._inputContext;
   }

   @Override
   public InputMethodRequests getInputMethodRequests() {
      return null;
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (this._accessibleStateSet & state) != 0;
   }

   @Override
   public int getAccessibleRole() {
      return 0;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return false;
   }

   @Override
   public void actionPerformed(int action, Object parameter) {
   }

   private void invalidateCommon(int x, int y, int width, int height, XYRect extent) {
      if (width > 0 && height > 0) {
         Manager manager = this.getManager();
         if (manager != null) {
            int clipx1 = extent.x;
            int clipx2 = extent.x + extent.width;
            int clipy1 = extent.y;
            int clipy2 = extent.y + extent.height;
            x += clipx1;
            y += clipy1;
            int newx1 = MathUtilities.clamp(clipx1, x, clipx2);
            int newx2 = MathUtilities.clamp(clipx1, x + width, clipx2);
            int newy1 = MathUtilities.clamp(clipy1, y, clipy2);
            int newy2 = MathUtilities.clamp(clipy1, y + height, clipy2);
            manager.invalidate(newx1, newy1, newx2 - newx1, newy2 - newy1);
         }
      }
   }

   private boolean isBackgroundDefined() {
      return this._themeAttributes != null && this._themeAttributes.isBackgroundDefined()
         || this._themeAttributesSpecial != null && this._themeAttributesSpecial.isBackgroundDefined();
   }

   private boolean isState(int state) {
      return (this._state & state) == state;
   }

   private boolean isCutCopyPasteEnabled() {
      if (!this.isState(16)) {
         boolean isClipboardEnabled = !ITPolicy.getBoolean(24, 36, false);
         this.setState(8, isClipboardEnabled);
         this.setState(16, 0);
      }

      return this.isState(8);
   }

   private boolean isEventLockRequired() {
      Screen screen = this.getScreen();
      if (screen != null) {
         UiEngineImpl engine = screen.getUiEngineImpl();
         if (engine != null) {
            return engine.isEventLockRequired();
         }
      }

      return false;
   }

   protected Field(long style) {
      this.setTag(TAG);
      if ((style & 54043195528445952L) == 0) {
         style |= 36028797018963968L;
      }

      if ((style & 13510798882111488L) == 0) {
         style |= 9007199254740992L;
      }

      if ((style & 3298534883328L) == 0) {
         style |= 2199023255552L;
      }

      if (!this.validateFieldStyle(style)) {
         throw new IllegalArgumentException();
      }

      this._style = style;
   }

   private void addIMActions(ContextMenu contextMenu, int instance) {
      if (instance == 0) {
         InvokableAction[] actions = this.getInputContext().getIMActions(this);
         if (actions != null && actions.length > 0) {
            if (!contextMenu.isEmpty()) {
               contextMenu.addSeparatorInternal();
            }

            for (int i = 0; i < actions.length; i++) {
               MenuItem item = MenuItemPrefab.get(actions[i]);
               contextMenu.addItem(item);
               if (actions[i].isDefault()) {
                  contextMenu.setDefaultItem(item);
               }
            }
         }
      }
   }

   private final void setState(int on, int off) {
      this._state |= on;
      this._state &= ~off;
   }

   private final void setState(int state, boolean on) {
      if (on) {
         this._state |= state;
      } else {
         this._state &= ~state;
      }
   }

   private boolean isPaddingDefined() {
      return (this._paddingTop | this._paddingRight | this._paddingBottom | this._paddingLeft) != 0;
   }

   private final void updateLayoutHelper() {
      this.assertHaveEventLock();
      Screen screen = this.getScreen();
      if (screen != null && screen.isInLayout()) {
         throw new IllegalStateException("Layout requested during layout");
      }

      if (this._manager != null && this._manager.isValidLayout()) {
         this._manager.runLayoutUpdate(this.getIndex(), 1, 1);
      } else if (this == screen && screen.isValidLayout()) {
         screen.runLayoutUpdate0(0, 1, 1);
      }

      if (this._layoutRunnable != null) {
         this._layoutRunnable._layoutPending = false;
      }
   }

   private final void updateExtent() {
      this._extent.width = this._content.width + this._borderLeft + this._borderRight + this._paddingLeft + this._paddingRight;
      this._extent.height = this._content.height + this._borderTop + this._borderBottom + this._paddingTop + this._paddingBottom;
   }

   protected Field() {
      this(45038195296960512L);
   }
}
