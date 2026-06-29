package net.rim.device.api.ui.theme;

import java.io.IOException;
import java.util.Hashtable;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.XYDimension;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ribbon.ApplicationFolder;
import net.rim.device.apps.api.ribbon.ApplicationHierarchy;
import net.rim.device.apps.api.ribbon.ApplicationHierarchyProvider;
import net.rim.device.apps.api.ribbon.ApplicationProperties;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.BorderBitmap;
import net.rim.device.resources.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ParameterizedFactory extends Theme$Factory implements ApplicationHierarchyProvider {
   private boolean _listHighlightColorChanged;
   private boolean _dialogFontChanged;
   Hashtable _effectedAttributeList;
   private ParameterizedFactory$Pass _pass;
   private final String _themeModuleName;
   private final Resource _themeResource;
   private final Element _themeRootElement;
   private final XYDimension _adjustedAppIconSize;
   private boolean _isAppIconSizeSet;
   private final boolean _mangleFolderNames;
   private final boolean _messageListCompatibility;
   private final Theme _parentTheme;
   private final int _priority;
   private ApplicationHierarchy _appHierarchyInfo;
   private static final int HASHTABLE_SIZE = 5;
   private static final String ALLOW_HIDE = "allowHide";
   private static final String ALLOW_MOVE = "allowMove";
   private static final String ALTERNATE_BASE = "alternateBase";
   private static final String ALTERNATE_BASE_FIRST_REDUNDANT = "alternateBaseFirstRedundant";
   private static final String ALTERNATE_BASE_SEARCH_SLEEP = "alternateBaseSearchSleep";
   private static final String ANTIALIAS = "antialias";
   private static final String BASE = "base";
   private static final String BOLD = "bold";
   private static final String BOLD_ITALIC = "bold-italic";
   private static final String BORDER_DEFINITION = "BorderDefinition";
   private static final String BOTTOM = "bottom";
   private static final String COLOR = "color";
   private static final String COLOR_DEPTH = "colorDepth";
   private static final String COLOR_SCHEME = "ColorScheme";
   private static final String DEFAULT_FOLDER = "defaultFolder";
   private static final String DISABLE = "disable";
   private static final String FONT = "Font";
   private static final String HEIGHT = "height";
   private static final String HIDE = "hide";
   private static final String ITALIC = "italic";
   private static final String LEFT = "left";
   private static final String NAME = "name";
   private static final String PLAIN = "plain";
   private static final String POSITION = "position";
   private static final String PRIORITY = "priority";
   private static final String REMOVABLE = "removable";
   private static final String RIGHT = "right";
   private static final String ROW_HEIGHT = "rowHeight";
   private static final String SIZE = "size";
   private static final String SOLID_FILL = "SolidFill";
   private static final String SRC = "src";
   private static final String STYLE = "style";
   private static final String THEME = "Theme";
   private static final String TOP = "top";
   private static final String VALUE = "value";
   private static final String WIDTH = "width";
   private static final String X = "x";
   private static final String Y = "y";
   private static final String BACKGROUND_COLOR = "backgroundColor";
   private static final String FOREGROUND_COLOR = "foregroundColor";
   private static final String MENU_HIGHLIGHT_BACKGROUND = "MenuHighlightBackground";
   private static final String MENU_HIGHLIGHT_FOREGROUND = "MenuHighlightForeground";
   private static final String MENU_HIGHLIGHT_TEXT = "MenuHighlightText";
   private static final String MENU_NORMAL_BACKGROUND = "MenuNormalBackground";
   private static final String MENU_NORMAL_FOREGROUND = "MenuNormalForeground";
   private static final String MENU_NORMAL_TEXT = "MenuNormalText";
   private static final String MENU_THEME = "MenuTheme";
   private static final String BUTTON_THEME = "ButtonTheme";
   private static final String BUTTON_NORMAL_BACKGROUND = "ButtonNormalBackground";
   private static final String BUTTON_NORMAL_FOREGROUND = "ButtonNormalForeground";
   private static final String BUTTON_NORMAL_TEXT = "ButtonNormalText";
   private static final String BUTTON_HIGHLIGHT_BACKGROUND = "ButtonHighlightBackground";
   private static final String BUTTON_HIGHLIGHT_FOREGROUND = "ButtonHighlightForeground";
   private static final String BUTTON_HIGHLIGHT_TEXT = "ButtonHighlightText";
   private static final String DIALOG_BOX_THEME = "DialogBoxTheme";
   private static final String DIALOG_BOX_BACKGROUND = "DialogBoxBackground";
   private static final String DIALOG_BOX_FOREGROUND = "DialogBoxForeground";
   private static final String DIALOG_BOX_TEXT = "DialogBoxText";
   private static final String PROGRESS_BAR_THEME = "ProgressBarTheme";
   private static final String PROGRESS_BAR_BACKGROUND = "ProgressBarBackground";
   private static final String PROGRESS_BAR_FOREGROUND = "ProgressBarForeground";
   private static final String PROGRESS_FILL_BACKGROUND = "ProgressFillBackground";
   private static final String PROGRESS_FILL_FOREGROUND = "ProgressFillForeground";
   private static final String ICON_GRID_THEME = "IconGridTheme";
   private static final String ICON_GRID_BACKGROUND = "IconGridBackground";
   private static final String ICON_GRID_OVERLAY = "IconGridOverlay";
   private static final String STATUS_BAR_FOREGROUND = "StatusBarForeground";
   private static final String STATUS_BAR_TEXT = "StatusBarText";
   private static final String STATUS_BAR_OVERLAY = "StatusBarOverlay";
   private static final String SVG_THEME = "SvgTheme";
   private static final String SVG_HOMESCREEN = "SvgHomescreen";
   private static final String SVG_BACKGROUND_WALLPAPER = "SvgBackgroundWallpaper";
   private static final String IDLE_SCREEN = "IdleScreen";
   private static final String THUMBNAIL_PREVIEW = "ThumbnailPreview";
   private static final String LOCK_SCREEN_THEME = "LockScreenTheme";
   private static final String SVG_LOCKSCREEN = "SvgLockscreen";
   private static final String OWNER_INFO_BACKGROUND = "OwnerInfoBackground";
   private static final String OWNER_INFO_FOREGROUND = "OwnerInfoForeground";
   private static final String BANNER = "Banner";
   private static final String ACTIVE_CALL_BANNER = "ActiveCallBanner";
   private static final String GRID_APP_MENU = "GridAppMenu";
   private static final String GRID_APP_MENU_MARGIN = "GridAppMenuMargin";
   private static final String GRID_APP_MENU_PADDING = "GridAppMenuPadding";
   private static final String GRID_APP_MENU_POSITION = "GridAppMenuPosition";
   private static final String PHONE_THEME = "PhoneTheme";
   private static final String HOTLIST_DIALED_NUMBER_FOREGROUND = "HotlistDialedNumberForeground";
   private static final String MY_NUMBER = "MyNumber";
   private static final String INCOMING_CALL_BACKGROUND = "IncomingCallBackground";
   private static final String INCOMING_CALL_FOREGROUND = "IncomingCallForeground";
   private static final String ACTIVE_CALL_FOREGROUND = "ActiveCallForeground";
   private static final String TITLE_BAR_THEME = "TitleBarTheme";
   private static final String TITLE_BAR_BACKGROUND = "TitleBarBackground";
   private static final String TITLE_BAR_FOREGROUND = "TitleBarForeground";
   private static final String TITLE_BAR_TEXT = "TitleBarText";
   private static final String TITLE_BAR_CARET = "TitleBarCaret";
   private static final String VERTICAL_LIST_THEME = "VerticalListTheme";
   private static final String VERTICAL_LIST_BACKGROUND = "VerticalListBackground";
   private static final String VERTICAL_LIST_NORMAL_TEXT_BORDER = "VerticalListNormalTextBorder";
   private static final String VERTICAL_LIST_NORMAL_TEXT = "VerticalListNormalText";
   private static final String VERTICAL_LIST_HIGHLIGHT_TEXT_BORDER = "VerticalListHighlightTextBorder";
   private static final String VERTICAL_LIST_HIGHLIGHT_TEXT = "VerticalListHighlightText";
   private static final String VERTICAL_LIST_NORMAL_FOREGROUND = "VerticalListNormalForeground";
   private static final String VERTICAL_LIST_HIGHLIGHT_FOREGROUND = "VerticalListHighlightForeground";
   private static final String MESSAGE_LIST_THEME = "MessageListTheme";
   private static final String DATE_SEPERATOR_BACKGROUND = "DateSeparatorBackground";
   private static final String DATE_SEPERATOR_TEXT = "DateSeparatorText";
   private static final String DATE_SEPERATOR_FOREGROUND = "DateSeparatorForeground";
   private static final String MESSAGE_LIST_LINE_1_FOREGROUND = "MessageListLine1Foreground";
   private static final String MESSAGE_LIST_LINE_1_TEXT = "MessageListLine1Text";
   private static final String MESSAGE_LIST_LINE_2_FOREGROUND = "MessageListLine2Foreground";
   private static final String MESSAGE_LIST_LINE_2_TEXT = "MessageListLine2Text";
   private static final String MESSAGE_LIST_LINE_1_LEVEL_1_FOREGROUND = "MessageListLine1Level1Foreground";
   private static final String MESSAGE_LIST_LINE_1_LEVEL_1_TEXT = "MessageListLine1Level1Text";
   private static final String MESSAGE_LIST_LINE_2_LEVEL_1_FOREGROUND = "MessageListLine2Level1Foreground";
   private static final String MESSAGE_LIST_LINE_2_LEVEL_1_TEXT = "MessageListLine2Level1Text";
   private static final String DATE_SEPERATOR_HIGHLIGHT_FOREGROUND = "DateSeparatorHighlightForeground";
   private static final String DATE_SEPERATOR_HIGHLIGHT_BACKGROUND = "DateSeparatorHighlightBackground";
   private static final String LIST_THEME = "ListTheme";
   private static final String LIST_NORMAL_FOREGROUND = "ListNormalForeground";
   private static final String LIST_NORMAL_TEXT = "ListNormalText";
   private static final String LIST_HIGHLIGHT_BACKGROUND = "ListHighlightBackground";
   private static final String LIST_HIGHLIGHT_FOREGROUND = "ListHighlightForeground";
   private static final String LIST_HIGHLIGHT_TEXT = "ListHighlightText";
   private static final String LIST_SELECTION_BACKGROUND = "ListSelectionBackground";
   private static final String APP_BACKGROUND = "AppBackground";
   private static final String APPLICATION_THEME = "ApplicationTheme";
   private static final String APP_NORMAL_BACKGROUND = "AppNormalBackground";
   private static final String APP_SELECTION_BACKGROUND = "AppSelectionBackground";
   private static final String APP_NORMAL_FOREGROUND = "AppNormalForeground";
   private static final String APP_HIGHLIGHT_FOREGROUND = "AppHighlightForeground";
   private static final String APP_HIGHLIGHT_CARET = "AppHighlightCaret";
   private static final String APPLICATION_HIERARCHY = "ApplicationHierarchy";
   private static final String FOLDER = "Folder";
   private static final String APPLICATION = "Application";
   private static final String APPLICATION_ICON_SIZE = "ApplicationIconSize";
   private static final String APPLICATION_ICON_WIDTH = "ApplicationIconWidth";
   private static final String APPLICATION_ICON_HEIGHT = "ApplicationIconHeight";
   private static final String MENU_BORDER_NAME = "menu";
   private static final String DIALOG_BORDER_NAME = "dialogBorder";
   private static final String BUTTON_BORDER_NAME = "button";
   private static final String BUTTON_FOCUS_BORDER_NAME = "button~focus";
   private static final String TITLE_BORDER_NAME = "title";
   private static final String BULLET_BORDER_NAME = "bullet";
   private static final String BULLET_FOCUS_BORDER_NAME = "bullet~focus";
   private static final String HEADER_BORDER_NAME = "header";
   private static final String INCOMING_CALL_BORDER_NAME = "incomingBorder";
   private static final String MENU_ELEMENT = "menu";
   private static final String MENUITEM_FOCUS_ELEMENT = "menuitem:focus";
   private static final String BUTTON_ELEMENT = "button";
   private static final String BUTTON_FOCUS_ELEMENT = "button:focus";
   private static final String POPUP_ELEMENT = "popup";
   private static final String TITLE_ELEMENT = "title";
   private static final String LIST_ELEMENT = "list";
   private static final String BANNER_ELEMENT = "banner";
   private static final String BANNER_ACTIVE_CALL_ELEMENT = "banner#activecall";
   private static final String APPLICATION_MENU_ICON = "application-menu-icon";
   private static final String HOMESCREEN_ELEMENT = "homescreen";
   private static final String HOMESCREEN_CHOOSER_ELEMENT = "homescreen#chooser";
   private static final String HOMESCREEN_CHOOSER_FOCUS_ELEMENT = "homescreen#chooser:focus";
   private static final String HOMESCREEN_MASK_ELEMENT = "homescreen_mask";
   private static final String STATUS_HOMESCREEN_ELEMENT = "status#homescreen";
   private static final String HEADER_ELEMENT = "header";
   private static final String PHONE_DIAL_ELEMENT = "phone-dial";
   private static final String POPUP_CALL_DISPLAY_ELEMENT = "popup#calldisplay";
   private static final String GLOBAL_POPUP_CALL_DISPLAY_ELEMENT = "global-popup#calldisplay";
   private static final String GLOBAL_POPUP_ELEMENT = "global-popup";
   private static final String CLIENT_ACTIVE_CALL_ELEMENT = "client#activecall";
   private static final String SYMBOLFIELD_ELEMENT = "symbolfield";
   private static final String SYMBOLFIELDNEXT_ELEMENT = "symbolfield-next";
   private static final String IDLE_ELEMENT = "idle";
   private static final String MY_NUMBER_FIELD_ELEMENT = "list#phonestatusfield";
   private static final String OWNER_INFO_ELEMENT = "owner-info";
   private static final String CLIENT_ELEMENT = "client";
   private static final String GAUGE_ELEMENT = "gauge";
   private static final String GAUGE_BAR_ELEMENT = "gauge-bar";
   private static final String GAUGE_FILL_ELEMENT = "gauge-fill";
   private static final String MESSAGE_LIST_LINE_1_ELEMENT = "messagelist-line1";
   private static final String MESSAGE_LIST_LINE_2_ELEMENT = "messagelist-line2";
   private static final String MESSAGE_LIST_LINE_1_LEVEL_1_ELEMENT = "messagelist-line1-level1";
   private static final String MESSAGE_LIST_LINE_2_LEVEL_1_ELEMENT = "messagelist-line2-level1";
   private static final String POINTS_UNIT = "pt";
   private static final String PIXELS_UNIT = "px";
   private static final int MAX_BORDER_SIZE = 30;
   private static final String DEFAULT_ATTRIBUTE_SET_NAME = "";
   private static final ParameterizedFactory$Pass CREATE_BORDERS_AND_FONTS = new ParameterizedFactory$Pass(null);
   private static final ParameterizedFactory$Pass WRITE_ATTRIBUTES = new ParameterizedFactory$Pass(null);

   public ParameterizedFactory(
      String themeModuleName,
      Resource themeResource,
      Element themeRootElement,
      String themeName,
      String parentThemeName,
      XYDimension adjustedAppIconSize,
      boolean mangleFolderNames,
      boolean messageListCompatibility
   ) {
      super(themeName, parentThemeName);
      this._themeModuleName = themeModuleName;
      this._themeResource = themeResource;
      this._themeRootElement = themeRootElement;
      this._adjustedAppIconSize = adjustedAppIconSize;
      this._mangleFolderNames = mangleFolderNames;
      this._messageListCompatibility = messageListCompatibility;
      this._parentTheme = ParameterizedThemeManager.getTheme(parentThemeName);
      this.setTargetDisplay(
         DOMSupport.getInt(themeRootElement, "width"), DOMSupport.getInt(themeRootElement, "height"), DOMSupport.getInt(themeRootElement, "colorDepth")
      );
      this.setRemovable(DOMSupport.getBoolean(themeRootElement, "removable"));
      this._priority = DOMSupport.getInt(themeRootElement, "priority");
      this._appHierarchyInfo = this.getParentHierarchy();
      this._dialogFontChanged = false;
      this._listHighlightColorChanged = false;
      this._effectedAttributeList = new Hashtable(5);
      this._pass = null;
      this._isAppIconSizeSet = false;
   }

   private static Theme$Factory getParentFactory(Theme$Factory factory) {
      Theme$Factory result = null;
      if (factory != null) {
         String parent = factory.getParent();
         if (parent != null && parent.length() > 0) {
            String id = ThemeManager.getPersistableIdForName(parent);
            if (id != null) {
               result = ThemeManager.getThemeFactory(id);
            }
         }
      }

      return result;
   }

   private ApplicationHierarchy getParentHierarchy() {
      ApplicationHierarchy parentHierarchy = null;

      for (Theme$Factory parentThemeFactory = getParentFactory(this); parentThemeFactory != null; parentThemeFactory = getParentFactory(parentThemeFactory)) {
         if (parentThemeFactory instanceof ApplicationHierarchyProvider) {
            return ((ApplicationHierarchyProvider)parentThemeFactory).getApplicationHierarchyInfo();
         }
      }

      return parentHierarchy;
   }

   @Override
   public ApplicationHierarchy getApplicationHierarchyInfo() {
      if (this._appHierarchyInfo == null) {
         return null;
      } else {
         return !(this._appHierarchyInfo instanceof CopyableApplicationHierarchy)
            ? this.getParentHierarchy()
            : ((CopyableApplicationHierarchy)this._appHierarchyInfo).getCopy();
      }
   }

   @Override
   public int getPriority() {
      return this._priority;
   }

   @Override
   public String getIdExtension() {
      return this._themeModuleName;
   }

   @Override
   public void populate(Theme$Writer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/api/ui/theme/ParameterizedResourceFetcher
      // 03: dup
      // 04: aload 0
      // 05: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeModuleName Ljava/lang/String;
      // 08: aload 0
      // 09: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeResource Lnet/rim/device/resources/Resource;
      // 0c: invokespecial net/rim/device/api/ui/theme/ParameterizedResourceFetcher.<init> (Ljava/lang/String;Lnet/rim/device/resources/Resource;)V
      // 0f: astore 2
      // 10: aload 1
      // 11: aload 2
      // 12: invokevirtual net/rim/device/api/ui/theme/Theme$Writer.setResourceFetcher (Lnet/rim/device/api/ui/theme/ResourceFetcher;)V
      // 15: aload 1
      // 16: invokevirtual net/rim/device/api/ui/theme/Theme$Writer.addResources ()V
      // 19: new net/rim/device/api/ui/theme/RemoteThemeAttributeSetWriter
      // 1c: dup
      // 1d: aload 0
      // 1e: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeResource Lnet/rim/device/resources/Resource;
      // 21: aload 2
      // 22: aload 1
      // 23: aload 0
      // 24: getfield net/rim/device/api/ui/theme/ParameterizedFactory._parentTheme Lnet/rim/device/api/ui/theme/Theme;
      // 27: invokespecial net/rim/device/api/ui/theme/RemoteThemeAttributeSetWriter.<init> (Lnet/rim/device/resources/Resource;Lnet/rim/device/api/ui/theme/ResourceFetcher;Lnet/rim/device/api/ui/theme/Theme$Writer;Lnet/rim/device/api/ui/theme/Theme;)V
      // 2a: astore 3
      // 2b: aload 0
      // 2c: getstatic net/rim/device/api/ui/theme/ParameterizedFactory.CREATE_BORDERS_AND_FONTS Lnet/rim/device/api/ui/theme/ParameterizedFactory$Pass;
      // 2f: putfield net/rim/device/api/ui/theme/ParameterizedFactory._pass Lnet/rim/device/api/ui/theme/ParameterizedFactory$Pass;
      // 32: aload 0
      // 33: aload 0
      // 34: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeRootElement Lorg/w3c/dom/Element;
      // 37: aload 3
      // 38: invokespecial net/rim/device/api/ui/theme/ParameterizedFactory.visitRoot (Lorg/w3c/dom/Element;Lnet/rim/device/api/ui/theme/RemoteThemeAttributeSetWriter;)V
      // 3b: aload 0
      // 3c: getstatic net/rim/device/api/ui/theme/ParameterizedFactory.WRITE_ATTRIBUTES Lnet/rim/device/api/ui/theme/ParameterizedFactory$Pass;
      // 3f: putfield net/rim/device/api/ui/theme/ParameterizedFactory._pass Lnet/rim/device/api/ui/theme/ParameterizedFactory$Pass;
      // 42: aload 0
      // 43: aload 0
      // 44: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeRootElement Lorg/w3c/dom/Element;
      // 47: aload 3
      // 48: invokespecial net/rim/device/api/ui/theme/ParameterizedFactory.visitRoot (Lorg/w3c/dom/Element;Lnet/rim/device/api/ui/theme/RemoteThemeAttributeSetWriter;)V
      // 4b: goto 77
      // 4e: astore 4
      // 50: invokestatic net/rim/plazmic/app/themereader/ThemeEventLogger.getThemeEventLogger ()Lnet/rim/plazmic/app/themereader/ThemeEventLogger;
      // 53: aload 0
      // 54: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeModuleName Ljava/lang/String;
      // 57: aload 4
      // 59: invokevirtual net/rim/plazmic/app/themereader/ThemeEventLogger.activationWarning (Ljava/lang/String;Ljava/lang/Exception;)V
      // 5c: aload 0
      // 5d: bipush 0
      // 5e: invokespecial net/rim/device/api/ui/theme/ParameterizedFactory.showErrorDialog (I)V
      // 61: goto 77
      // 64: astore 4
      // 66: invokestatic net/rim/plazmic/app/themereader/ThemeEventLogger.getThemeEventLogger ()Lnet/rim/plazmic/app/themereader/ThemeEventLogger;
      // 69: aload 0
      // 6a: getfield net/rim/device/api/ui/theme/ParameterizedFactory._themeModuleName Ljava/lang/String;
      // 6d: aload 4
      // 6f: invokevirtual net/rim/plazmic/app/themereader/ThemeEventLogger.unknownActivationWarning (Ljava/lang/String;Ljava/lang/Exception;)V
      // 72: aload 0
      // 73: bipush 1
      // 74: invokespecial net/rim/device/api/ui/theme/ParameterizedFactory.showErrorDialog (I)V
      // 77: aload 0
      // 78: getfield net/rim/device/api/ui/theme/ParameterizedFactory._isAppIconSizeSet Z
      // 7b: ifne 9a
      // 7e: aload 0
      // 7f: getfield net/rim/device/api/ui/theme/ParameterizedFactory._adjustedAppIconSize Lnet/rim/device/api/ui/XYDimension;
      // 82: ifnull 9a
      // 85: aload 3
      // 86: invokevirtual net/rim/device/api/ui/theme/RemoteThemeAttributeSetWriter.getThemeWriter ()Lnet/rim/device/api/ui/theme/Theme$Writer;
      // 89: aload 0
      // 8a: getfield net/rim/device/api/ui/theme/ParameterizedFactory._adjustedAppIconSize Lnet/rim/device/api/ui/XYDimension;
      // 8d: getfield net/rim/device/api/ui/XYDimension.width I
      // 90: aload 0
      // 91: getfield net/rim/device/api/ui/theme/ParameterizedFactory._adjustedAppIconSize Lnet/rim/device/api/ui/XYDimension;
      // 94: getfield net/rim/device/api/ui/XYDimension.height I
      // 97: invokevirtual net/rim/device/api/ui/theme/Theme$Writer.setApplicationIconSize (II)V
      // 9a: return
      // try (23 -> 39): 40 null
      // try (23 -> 39): 50 null
   }

   private void showErrorDialog(int messageId) {
      if (Application.getApplication().isEventThread()) {
         try {
            String msg = ResourceBundle.getBundle("net.rim.plazmic.app.themereader.ThemeReaderRes").getString(messageId);
            Dialog.alert(msg);
         } finally {
            return;
         }
      }
   }

   public static String getThemeName(Element elem) {
      return DOMSupport.getString(elem, "name");
   }

   public static String getParentThemeName(Element elem) {
      return DOMSupport.getString(elem, "base");
   }

   public static String getAlternateBase(Element elem) {
      return elem.hasAttribute("alternateBase") ? DOMSupport.getString(elem, "alternateBase") : null;
   }

   public static Integer getAlternateBaseFirstRedundant(Element elem) {
      return elem.hasAttribute("alternateBaseFirstRedundant") ? new Integer(DOMSupport.getInt(elem, "alternateBaseFirstRedundant")) : null;
   }

   public static Integer getAlternateBaseSearchSleep(Element elem) {
      return elem.hasAttribute("alternateBaseSearchSleep") ? new Integer(DOMSupport.getInt(elem, "alternateBaseSearchSleep")) : null;
   }

   private void visitRoot(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();

      for (int i = 0; i < childNodes.getLength(); i++) {
         if (childNodes.item(i) instanceof Element) {
            Element child = (Element)childNodes.item(i);
            String tagName = child.getTagName();
            if ("MenuTheme".equals(tagName)) {
               this.visitMenuTheme(child, remoteWriter);
            } else if ("DialogBoxTheme".equals(tagName)) {
               this.visitDialogBoxTheme("popup", child, remoteWriter);
               if (this._parentTheme != null && this._parentTheme.getAttributeSet(Tag.get("global-popup")) != null) {
                  this.visitDialogBoxTheme("global-popup", child, remoteWriter);
               }

               if (this._parentTheme != null && this._parentTheme.getAttributeSet(Tag.get("symbolfield")) != null) {
                  this.visitSymbolfieldTheme(child, remoteWriter);
               }
            } else if ("ButtonTheme".equals(tagName)) {
               this.visitButtonTheme(child, remoteWriter);
            } else if ("TitleBarTheme".equals(tagName)) {
               this.visitTitleBarTheme(child, remoteWriter);
            } else if ("ListTheme".equals(tagName)) {
               this.visitListTheme(child, remoteWriter);
            } else if ("IdleScreen".equals(tagName)) {
               this.visitIdleScreen(child, remoteWriter);
            } else if ("ThumbnailPreview".equals(tagName)) {
               this.visitThumbnailPreview(child, remoteWriter);
            } else if ("Banner".equals(tagName)) {
               this.visitBanner(child, remoteWriter);
            } else if ("ActiveCallBanner".equals(tagName)) {
               this.visitActiveCallBanner(child, remoteWriter);
            } else if ("GridAppMenu".equals(tagName)) {
               this.visitGridAppMenu(child, remoteWriter);
            } else if ("VerticalListTheme".equals(tagName)) {
               this.visitVerticalListTheme(child, remoteWriter);
            } else if ("IconGridTheme".equals(tagName)) {
               this.visitIconGridTheme(child, remoteWriter);
            } else if ("MessageListTheme".equals(tagName)) {
               this.visitMessageListTheme(child, remoteWriter);
            } else if ("PhoneTheme".equals(tagName)) {
               this.visitPhoneTheme(child, remoteWriter);
            } else if ("SvgTheme".equals(tagName)) {
               this.visitSvgTheme(child, remoteWriter);
            } else if ("LockScreenTheme".equals(tagName)) {
               this.visitLockScreenTheme(child, remoteWriter);
            } else if ("AppBackground".equals(tagName)) {
               this.visitAppBackground(child, remoteWriter);
            } else if ("ApplicationTheme".equals(tagName)) {
               this.visitApplicationTheme(child, remoteWriter);
            } else if ("ApplicationHierarchy".equals(tagName)) {
               this.visitApplicationHierarchy(child);
            } else if ("ProgressBarTheme".equals(tagName)) {
               this.visitProgressBarTheme(child, remoteWriter);
            } else if ("ApplicationIconSize".equals(tagName)) {
               this.visitApplicationIconSize(child, remoteWriter);
            }
         }
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         if (this._dialogFontChanged) {
            remoteWriter.resetElement("button");
            remoteWriter.resetElement("button:focus");
         }

         if (this._listHighlightColorChanged) {
            remoteWriter.startElement("header");
            this.setDateSepCaretAtt(remoteWriter);
            remoteWriter.endElement();
         }

         remoteWriter.resetElement("");
      }
   }

   private void visitMenuTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("menu");
      }

      if ((i = DOMSupport.indexOfElement("MenuNormalBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "menu");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("menu");
         }
      }

      if ((i = DOMSupport.indexOfElement("MenuNormalForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("MenuNormalText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if ((i = DOMSupport.indexOfElement("MenuHighlightBackground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         Element solidFill = DOMSupport.getFirstChildNamed("SolidFill", childNodes.item(i));
         remoteWriter.setColor(2, DOMSupport.getColor(solidFill, "color"));
      }

      if ((i = DOMSupport.indexOfElement("MenuHighlightForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(3, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("menuitem:focus");
      }

      if ((i = DOMSupport.indexOfElement("MenuHighlightText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitButtonTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      this._dialogFontChanged = false;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("button");
      }

      if ((i = DOMSupport.indexOfElement("ButtonNormalBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "button");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("button");
         }
      }

      if ((i = DOMSupport.indexOfElement("ButtonNormalForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("ButtonNormalText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("button:focus");
      }

      if ((i = DOMSupport.indexOfElement("ButtonHighlightBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "button~focus");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("button~focus");
         }
      }

      if ((i = DOMSupport.indexOfElement("ButtonHighlightForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("ButtonHighlightText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitDialogBoxTheme(String elementName, Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement(elementName);
      }

      if ((i = DOMSupport.indexOfElement("DialogBoxBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "dialogBorder");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("dialogBorder");
         }
      }

      if ((i = DOMSupport.indexOfElement("DialogBoxForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("DialogBoxText", childNodes, i + 1)) != -1 && this._pass != CREATE_BORDERS_AND_FONTS && this._pass == WRITE_ATTRIBUTES
         )
       {
         setFont(childNodes.item(i), remoteWriter);
         this._dialogFontChanged = true;
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitSymbolfieldTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("symbolfield");
      }

      if ((i = DOMSupport.indexOfElement("DialogBoxBackground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         Element borderDef = DOMSupport.getFirstChildNamed("BorderDefinition", childNodes.item(i));
         String src = DOMSupport.getString(borderDef, "src");
         int top = DOMSupport.getInt(borderDef, "top");
         int right = DOMSupport.getInt(borderDef, "right");
         int bottom = DOMSupport.getInt(borderDef, "bottom");
         int left = DOMSupport.getInt(borderDef, "left");
         byte[] imageBytes = this._themeResource.getResource(src);
         if (imageBytes != null) {
            EncodedImage image = EncodedImage.createEncodedImage(imageBytes, 0, imageBytes.length);
            Bitmap imageBitmap = image.getBitmap();
            Bitmap centre = UISupport.submap(imageBitmap, left, top, imageBitmap.getWidth() - left - right, imageBitmap.getHeight() - top - bottom);
            Bitmap rolled = UISupport.rollmap(centre, 2, 2);
            remoteWriter.setBackgroundImage(rolled);
         }
      }

      if ((i = DOMSupport.indexOfElement("DialogBoxForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         int dialogForeground = DOMSupport.getColor((Element)childNodes.item(i), "color");
         remoteWriter.setColor(1, dialogForeground);
         remoteWriter.setColor(3, UISupport.roundColor(UISupport.invertColor(dialogForeground)));
         remoteWriter.setColor(2, dialogForeground);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitProgressBarTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         NodeList childNodes = elem.getChildNodes();
         int i = -1;
         remoteWriter.startElement("gauge-bar");
         if ((i = DOMSupport.indexOfElement("ProgressBarBackground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(0, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("ProgressBarForeground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         remoteWriter.endElement();
         remoteWriter.startElement("gauge-fill");
         if ((i = DOMSupport.indexOfElement("ProgressFillBackground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(4, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("ProgressFillForeground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(5, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         remoteWriter.endElement();
      }
   }

   private void visitTitleBarTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("title");
      }

      if ((i = DOMSupport.indexOfElement("TitleBarBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "title");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("title");
         }
      }

      if ((i = DOMSupport.indexOfElement("TitleBarForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("TitleBarText", childNodes, i + 1)) != -1 && this._pass != CREATE_BORDERS_AND_FONTS && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if ((i = DOMSupport.indexOfElement("TitleBarCaret", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         this.setColorScheme(childNodes.item(i), remoteWriter, true);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitListTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      Element normalForegroundColor = null;
      Element normalFont = null;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("list");
      }

      if ((i = DOMSupport.indexOfElement("ListNormalForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         normalForegroundColor = (Element)childNodes.item(i);
         remoteWriter.setColor(1, DOMSupport.getColor(normalForegroundColor, "color"));
      }

      if ((i = DOMSupport.indexOfElement("ListNormalText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         normalFont = (Element)childNodes.item(i);
         setFont(normalFont, remoteWriter);
         this._listHighlightColorChanged = true;
      }

      if ((i = DOMSupport.indexOfElement("ListHighlightBackground", childNodes, i + 1)) != -1) {
         Element solidFill = DOMSupport.getFirstChildNamed("SolidFill", childNodes.item(i));
         if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setColor(2, DOMSupport.getColor(solidFill, "color"));
            this._effectedAttributeList.put("DateSeparatorHighlightBackground", solidFill);
            this._listHighlightColorChanged = true;
         }
      }

      if ((i = DOMSupport.indexOfElement("ListHighlightForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         int color = DOMSupport.getColor((Element)childNodes.item(i), "color");
         remoteWriter.setColor(3, color);
         remoteWriter.setColor(5, color);
         this._effectedAttributeList.put("DateSeparatorHighlightForeground", (Element)childNodes.item(i));
         this._listHighlightColorChanged = true;
      }

      if ((i = DOMSupport.indexOfElement("ListSelectionBackground", childNodes, i + 1)) != -1) {
         Element solidFill = DOMSupport.getFirstChildNamed("SolidFill", childNodes.item(i));
         if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setColor(4, DOMSupport.getColor(solidFill, "color"));
         }
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._messageListCompatibility && this._pass == WRITE_ATTRIBUTES) {
         this.doMessageListBackwardsCompatibility(normalForegroundColor, normalFont, remoteWriter);
      }
   }

   private void doMessageListBackwardsCompatibility(Element foregroundColor, Element font, RemoteThemeAttributeSetWriter remoteWriter) {
      if (foregroundColor != null || font != null) {
         this.setMessageListElement("messagelist-line1", foregroundColor, font, remoteWriter);
         this.setMessageListElement("messagelist-line1-level1", foregroundColor, font, remoteWriter);
         this.setMessageListElement("messagelist-line2", foregroundColor, font, remoteWriter);
         this.setMessageListElement("messagelist-line2-level1", foregroundColor, font, remoteWriter);
      }
   }

   private void setMessageListElement(String messageListElement, Element foregroundColor, Element font, RemoteThemeAttributeSetWriter remoteWriter) {
      remoteWriter.startElement(messageListElement);
      if (foregroundColor != null) {
         remoteWriter.setColor(1, DOMSupport.getColor(foregroundColor, "color"));
      }

      if (font != null) {
         setFont(font, remoteWriter);
      }

      remoteWriter.endElement();
   }

   private void visitIdleScreen(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         String idleScreenSrc = DOMSupport.getString(elem, "src");
         remoteWriter.getThemeWriter().setIdleScreenName(idleScreenSrc);
      }
   }

   private void visitThumbnailPreview(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         String thumbnailPreviewSrc = DOMSupport.getString(elem, "src");
         if (thumbnailPreviewSrc.endsWith(ThemeConstants.EXT_GIF)) {
            thumbnailPreviewSrc = thumbnailPreviewSrc.substring(0, thumbnailPreviewSrc.length() - ThemeConstants.EXT_GIF.length());
         } else if (thumbnailPreviewSrc.endsWith(ThemeConstants.EXT_PNG)) {
            thumbnailPreviewSrc = thumbnailPreviewSrc.substring(0, thumbnailPreviewSrc.length() - ThemeConstants.EXT_PNG.length());
         }

         remoteWriter.getThemeWriter().setThumbnailName(thumbnailPreviewSrc);
      }
   }

   private void visitBanner(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         this.setLayout(elem, "banner", remoteWriter);
      }
   }

   private void visitActiveCallBanner(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         this.setLayout(elem, "banner#activecall", remoteWriter);
      }
   }

   private void visitGridAppMenu(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         NodeList childNodes = elem.getChildNodes();
         int i = -1;
         if ((i = DOMSupport.indexOfElement("GridAppMenuMargin", childNodes)) != -1) {
            remoteWriter.startElement("application-menu-icon");
            setEdges((Element)childNodes.item(i), remoteWriter, 2);
         }

         if ((i = DOMSupport.indexOfElement("GridAppMenuPadding", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("application-menu-icon");
            }

            setEdges((Element)childNodes.item(i), remoteWriter, 0);
         }

         if ((i = DOMSupport.indexOfElement("GridAppMenuPosition", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("application-menu-icon");
            }

            Element child = (Element)childNodes.item(i);
            int x = DOMSupport.getInt(child, "x");
            int y = DOMSupport.getInt(child, "y");
            int width = DOMSupport.getInt(child, "width");
            int height = DOMSupport.getInt(child, "height");
            remoteWriter.setPosition(x, y, width, height);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }
      }
   }

   private void visitSvgTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         NodeList childNodes = elem.getChildNodes();
         remoteWriter.startElement("homescreen");
         int i = -1;
         if ((i = DOMSupport.indexOfElement("SvgHomescreen", childNodes)) != -1) {
            String src = DOMSupport.getString((Element)childNodes.item(i), "src");
            remoteWriter.setLayout(src);
         }

         if ((i = DOMSupport.indexOfElement("SvgBackgroundWallpaper", childNodes, i)) != -1) {
            remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
         }

         remoteWriter.endElement();
         if ((i = DOMSupport.indexOfElement("StatusBarForeground", childNodes, i + 1)) != -1) {
            remoteWriter.startElement("status#homescreen");
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("StatusBarText", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("status#homescreen");
            }

            setFont(childNodes.item(i), remoteWriter);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }
      }
   }

   private void visitLockScreenTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if ((i = DOMSupport.indexOfElement("SvgLockscreen", childNodes, i)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         this.setLayout((Element)childNodes.item(i), "idle", remoteWriter);
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("owner-info");
      }

      if ((i = DOMSupport.indexOfElement("OwnerInfoBackground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
      }

      if ((i = DOMSupport.indexOfElement("OwnerInfoForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitVerticalListTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) throws IOException {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      int iNormalText = DOMSupport.indexOfElement("VerticalListNormalText", childNodes, 0);
      int iHighlightText = DOMSupport.indexOfElement("VerticalListHighlightText", childNodes, 0);
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("homescreen");
         int rowHeight = 22;
         if (elem.hasAttribute("rowHeight")) {
            rowHeight = DOMSupport.getInt(elem, "rowHeight");
            if (rowHeight <= 0) {
               throw new IOException("rowHeight must be greater than 0");
            }
         }

         remoteWriter.setLayoutParameters(new String[]{"Banner?align=title", "AppChooser.VerticalAppChooser?chooser&rowHeight=" + rowHeight});
      }

      if ((i = DOMSupport.indexOfElement("VerticalListBackground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if ((i = DOMSupport.indexOfElement("VerticalListNormalTextBorder", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "bullet");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.startElement("homescreen#chooser");
            remoteWriter.setBorder("bullet");
         }
      }

      if ((i = DOMSupport.indexOfElement("VerticalListNormalForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         if (!remoteWriter.isOpen()) {
            remoteWriter.startElement("homescreen#chooser");
         }

         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      i = iNormalText;
      if (iNormalText != -1 && this._pass != CREATE_BORDERS_AND_FONTS && this._pass == WRITE_ATTRIBUTES) {
         if (!remoteWriter.isOpen()) {
            remoteWriter.startElement("homescreen#chooser");
         }

         setFont(childNodes.item(i), remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if ((i = DOMSupport.indexOfElement("VerticalListHighlightTextBorder", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "bullet~focus");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.startElement("homescreen#chooser:focus");
            remoteWriter.setBorder("bullet~focus");
         }
      }

      if ((i = DOMSupport.indexOfElement("VerticalListHighlightForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         if (!remoteWriter.isOpen()) {
            remoteWriter.startElement("homescreen#chooser:focus");
         }

         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      i = iHighlightText;
      if (iHighlightText != -1 && this._pass != CREATE_BORDERS_AND_FONTS && this._pass == WRITE_ATTRIBUTES) {
         if (!remoteWriter.isOpen()) {
            remoteWriter.startElement("homescreen#chooser:focus");
         }

         setFont(childNodes.item(i), remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitIconGridTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("homescreen");
      }

      if ((i = DOMSupport.indexOfElement("IconGridBackground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("homescreen_mask");
      }

      if ((i = DOMSupport.indexOfElement("IconGridOverlay", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("status#homescreen");
      }

      if ((i = DOMSupport.indexOfElement("StatusBarForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         int fillColor = DOMSupport.getColor((Element)childNodes.item(i), "color");
         remoteWriter.setColor(1, fillColor);
         remoteWriter.setColor(7, fillColor);
         remoteWriter.setColor(6, -16777216);
      }

      if ((i = DOMSupport.indexOfElement("StatusBarText", childNodes, i + 1)) != -1 && this._pass != CREATE_BORDERS_AND_FONTS && this._pass == WRITE_ATTRIBUTES
         )
       {
         setFont(childNodes.item(i), remoteWriter);
      }

      if ((i = DOMSupport.indexOfElement("StatusBarOverlay", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setBackgroundImage(DOMSupport.getString((Element)childNodes.item(i), "src"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitMessageListTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      this._listHighlightColorChanged = false;
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("header");
      }

      if ((i = DOMSupport.indexOfElement("DateSeparatorBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "header");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("header");
         }
      }

      if ((i = DOMSupport.indexOfElement("DateSeparatorForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if ((i = DOMSupport.indexOfElement("DateSeparatorText", childNodes, i + 1)) != -1
         && this._pass != CREATE_BORDERS_AND_FONTS
         && this._pass == WRITE_ATTRIBUTES) {
         setFont(childNodes.item(i), remoteWriter);
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         this.setDateSepCaretAtt(remoteWriter);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         if ((i = DOMSupport.indexOfElement("MessageListLine1Foreground", childNodes, i + 1)) != -1) {
            remoteWriter.startElement("messagelist-line1");
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine1Text", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("messagelist-line1");
            }

            setFont(childNodes.item(i), remoteWriter);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine2Foreground", childNodes, i + 1)) != -1) {
            remoteWriter.startElement("messagelist-line2");
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine2Text", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("messagelist-line2");
            }

            setFont(childNodes.item(i), remoteWriter);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine1Level1Foreground", childNodes, i + 1)) != -1) {
            remoteWriter.startElement("messagelist-line1-level1");
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine1Level1Text", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("messagelist-line1-level1");
            }

            setFont(childNodes.item(i), remoteWriter);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine2Level1Foreground", childNodes, i + 1)) != -1) {
            remoteWriter.startElement("messagelist-line2-level1");
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("MessageListLine2Level1Text", childNodes, i + 1)) != -1) {
            if (!remoteWriter.isOpen()) {
               remoteWriter.startElement("messagelist-line2-level1");
            }

            setFont(childNodes.item(i), remoteWriter);
         }

         if (remoteWriter.isOpen()) {
            remoteWriter.endElement();
         }
      }
   }

   private void setDateSepCaretAtt(RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES && remoteWriter.getElementName().equals("header")) {
         Element highBackgroundElement = (Element)this._effectedAttributeList.get("DateSeparatorHighlightBackground");
         Element highForegroundElement = (Element)this._effectedAttributeList.get("DateSeparatorHighlightForeground");
         if (highBackgroundElement != null) {
            remoteWriter.setColor(2, DOMSupport.getColor(highBackgroundElement, "color"));
         }

         if (highForegroundElement != null) {
            int color = DOMSupport.getColor(highForegroundElement, "color");
            remoteWriter.setColor(3, color);
            remoteWriter.setColor(5, color);
         }
      }
   }

   private void visitAppBackground(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         int backgroundColor = DOMSupport.getColor(elem, "color");
         remoteWriter.startElement("client");
         remoteWriter.setColor(0, backgroundColor);
         remoteWriter.endElement();
      }
   }

   private void visitApplicationTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         NodeList childNodes = elem.getChildNodes();
         int i = -1;
         remoteWriter.startElement("client");
         if ((i = DOMSupport.indexOfElement("AppNormalBackground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(0, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("AppSelectionBackground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(4, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("AppNormalForeground", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         if ((i = DOMSupport.indexOfElement("AppHighlightForeground", childNodes, i + 1)) != -1) {
            int highlightColor = DOMSupport.getColor((Element)childNodes.item(i), "color");
            remoteWriter.setColor(3, highlightColor);
            remoteWriter.setColor(5, highlightColor);
         }

         if ((i = DOMSupport.indexOfElement("AppHighlightCaret", childNodes, i + 1)) != -1) {
            remoteWriter.setColor(2, DOMSupport.getColor((Element)childNodes.item(i), "color"));
         }

         remoteWriter.endElement();
      }
   }

   private void visitPhoneTheme(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      NodeList childNodes = elem.getChildNodes();
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("phone-dial");
      }

      if ((i = DOMSupport.indexOfElement("HotlistDialedNumberForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("list#phonestatusfield");
      }

      if ((i = DOMSupport.indexOfElement("MyNumber", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         this.setColorScheme(childNodes.item(i), remoteWriter, false);
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }

      this.visitIncomingCall("popup#calldisplay", elem, remoteWriter, childNodes);
      if (this._parentTheme != null && this._parentTheme.getAttributeSet(Tag.get("global-popup")) != null) {
         this.visitIncomingCall("global-popup#calldisplay", elem, remoteWriter, childNodes);
      }

      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement("client#activecall");
      }

      if ((i = DOMSupport.indexOfElement("ActiveCallForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitIncomingCall(String elementName, Element elem, RemoteThemeAttributeSetWriter remoteWriter, NodeList childNodes) {
      int i = -1;
      if (this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.startElement(elementName);
      }

      if ((i = DOMSupport.indexOfElement("IncomingCallBackground", childNodes, i + 1)) != -1) {
         if (this._pass == CREATE_BORDERS_AND_FONTS) {
            this.createBorderBitmap(childNodes.item(i), remoteWriter.getThemeWriter(), "incomingBorder");
         } else if (this._pass == WRITE_ATTRIBUTES) {
            remoteWriter.setBorder("incomingBorder");
         }
      }

      if ((i = DOMSupport.indexOfElement("IncomingCallForeground", childNodes, i + 1)) != -1 && this._pass == WRITE_ATTRIBUTES) {
         remoteWriter.setColor(1, DOMSupport.getColor((Element)childNodes.item(i), "color"));
      }

      if (remoteWriter.isOpen()) {
         remoteWriter.endElement();
      }
   }

   private void visitApplicationHierarchy(Element elem) {
      if (this._pass == WRITE_ATTRIBUTES) {
         this._appHierarchyInfo = new CopyableApplicationHierarchy("BlackBerry");
         this._appHierarchyInfo.setAllowHideIcons(DOMSupport.getBoolean(elem, "allowHide"));
         this._appHierarchyInfo.setAllowMoveIcons(DOMSupport.getBoolean(elem, "allowMove"));
         this._appHierarchyInfo.setDefaultFolderName(DOMSupport.getString(elem, "defaultFolder"));
         NodeList childNodes = elem.getChildNodes();
         int numChildren = childNodes.getLength();

         for (int i = 0; i < numChildren; i++) {
            Node n = childNodes.item(i);
            if (n instanceof Element && "Folder".equals(n.getNodeName())) {
               this.visitFolder((Element)n, null, 1);
            }
         }
      }
   }

   private int visitFolder(Element elem, ApplicationFolder parent, int folderId) {
      ApplicationFolder folder;
      if (elem.hasAttribute("name") && parent != null) {
         String folderName = DOMSupport.getString(elem, "name");
         ApplicationProperties properties;
         if (this._mangleFolderNames) {
            String folderName422 = folderName.trim().replace(' ', '_');
            String appName = removeControlCharacters(folderName422);
            properties = this._appHierarchyInfo.createApplicationProperties(appName);
         } else {
            properties = this._appHierarchyInfo.createApplicationProperties(folderName);
         }

         properties.setVisible(true);
         properties.setFolderName(parent.getName());
         properties.setPosition(DOMSupport.getInt(elem, "position"));
         folder = new ApplicationFolder(folderName, this._themeModuleName, folderId++);
         this._appHierarchyInfo.addFolder(folder);
      } else {
         folder = this._appHierarchyInfo.getRootFolder();
      }

      folder.setAllowHideIcons(DOMSupport.getBoolean(elem, "allowHide"));
      folder.setAllowMoveIcons(DOMSupport.getBoolean(elem, "allowMove"));
      NodeList childNodes = elem.getChildNodes();
      int numChildren = childNodes.getLength();

      for (int i = 0; i < numChildren; i++) {
         Node n = childNodes.item(i);
         if (n instanceof Element) {
            if ("Folder".equals(n.getNodeName())) {
               folderId = this.visitFolder((Element)n, folder, folderId);
            } else if ("Application".equals(n.getNodeName())) {
               this.visitApplication((Element)n, folder);
            }
         }
      }

      return folderId;
   }

   private static String removeControlCharacters(String string) {
      if (string.indexOf(32) != -1 || string.indexOf(818) != -1) {
         StringBuffer buffer = new StringBuffer();
         int length = string.length();

         for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != 818) {
               buffer.append(c);
            }
         }

         string = buffer.toString();
      }

      return string;
   }

   private void visitApplication(Element elem, ApplicationFolder parent) {
      ApplicationProperties properties = this._appHierarchyInfo.createApplicationProperties(DOMSupport.getString(elem, "name"));
      boolean visibility = true;
      if (elem.hasAttribute("hide")) {
         visibility = !DOMSupport.getBoolean(elem, "hide");
      }

      properties.setVisible(visibility);
      properties.setFolderName(parent.getName());
      properties.setPosition(DOMSupport.getInt(elem, "position"));
      properties.setCanHide(DOMSupport.getBoolean(elem, "allowHide"));
      if (elem.hasAttribute("disable")) {
         properties.setDisabled(DOMSupport.getBoolean(elem, "disable"));
      }
   }

   private void visitApplicationIconSize(Element elem, RemoteThemeAttributeSetWriter remoteWriter) {
      if (this._pass == WRITE_ATTRIBUTES) {
         int width = -1;
         int height = -1;
         int i = -1;
         NodeList children = elem.getChildNodes();
         if ((i = DOMSupport.indexOfElement("ApplicationIconWidth", children)) != -1) {
            width = DOMSupport.getInt((Element)children.item(i), "value");
         }

         if ((i = DOMSupport.indexOfElement("ApplicationIconHeight", children, i)) != -1) {
            height = DOMSupport.getInt((Element)children.item(i), "value");
         }

         if (width > 0 && height > 0) {
            remoteWriter.getThemeWriter().setApplicationIconSize(width, height);
            this._isAppIconSizeSet = true;
         }
      }
   }

   private void setLayout(Element elem, String tagName, RemoteThemeAttributeSetWriter remoteWriter) {
      remoteWriter.startElement(tagName);
      String src = DOMSupport.getString(elem, "src");
      remoteWriter.setLayout(src);
      remoteWriter.endElement();
   }

   private void setColorScheme(Node parent, RemoteThemeAttributeSetWriter remoteWriter, boolean isCaret) {
      int bgConst = isCaret ? 2 : 0;
      int fgConst = isCaret ? 3 : 1;
      Element colorScheme = DOMSupport.getFirstChildNamed("ColorScheme", parent);
      if (colorScheme.hasAttribute("backgroundColor")) {
         remoteWriter.setColor(bgConst, DOMSupport.getColor(colorScheme, "backgroundColor"));
      }

      if (colorScheme.hasAttribute("foregroundColor")) {
         remoteWriter.setColor(fgConst, DOMSupport.getColor(colorScheme, "foregroundColor"));
      }
   }

   private static void setEdges(Element elem, RemoteThemeAttributeSetWriter remoteWriter, int edgeType) {
      int left = DOMSupport.getInt(elem, "left");
      int top = DOMSupport.getInt(elem, "top");
      int right = DOMSupport.getInt(elem, "right");
      int bottom = DOMSupport.getInt(elem, "bottom");
      remoteWriter.setEdges(edgeType, top, right, bottom, left);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void setFont(Node parent, RemoteThemeAttributeSetWriter remoteWriter) throws IOException {
      Element fontDef = DOMSupport.getFirstChildNamed("Font", parent);
      if (fontDef.hasAttribute("name")) {
         remoteWriter.setFontFamily(fontDef.getAttribute("name"));
      }

      if (fontDef.hasAttribute("size")) {
         String fontSize = fontDef.getAttribute("size");
         int units = 0;
         if (fontSize.endsWith("pt")) {
            fontSize = fontSize.substring(0, fontSize.length() - 2);
            units = 2;
         } else if (fontSize.endsWith("px")) {
            fontSize = fontSize.substring(0, fontSize.length() - 2);
         }

         try {
            remoteWriter.setFontSize(Integer.parseInt(fontSize), units);
         } catch (Throwable var7) {
            throw new IOException(nfe.getMessage());
         }
      }

      if (fontDef.hasAttribute("style")) {
         String fontStyle = fontDef.getAttribute("style");
         int styleNum;
         if ("plain".equals(fontStyle)) {
            styleNum = 0;
         } else if ("bold".equals(fontStyle)) {
            styleNum = 1;
         } else if ("italic".equals(fontStyle)) {
            styleNum = 2;
         } else {
            if (!"bold-italic".equals(fontStyle)) {
               throw new IOException("invalid font style " + fontStyle);
            }

            styleNum = 3;
         }

         remoteWriter.setFontStyle(styleNum);
      }

      if (fontDef.hasAttribute("antialias")) {
         String antiAlias = fontDef.getAttribute("antialias");
         if (antiAlias.equals("true")) {
            remoteWriter.setFontAntialiasMode(2);
         } else if (antiAlias.equals("false")) {
            remoteWriter.setFontAntialiasMode(1);
         } else {
            throw new IOException("invalid antiAlias mode " + antiAlias);
         }
      }
   }

   private void createBorderBitmap(Node parent, Theme$Writer themeWriter, String name) {
      Element borderDef = DOMSupport.getFirstChildNamed("BorderDefinition", parent);
      this.createBorderBitmap(
         themeWriter,
         DOMSupport.getString(borderDef, "src"),
         name,
         DOMSupport.getInt(borderDef, "top"),
         DOMSupport.getInt(borderDef, "right"),
         DOMSupport.getInt(borderDef, "bottom"),
         DOMSupport.getInt(borderDef, "left")
      );
   }

   private void createBorderBitmap(Theme$Writer themeWriter, String src, String name, int top, int right, int bottom, int left) {
      this.createBorderBitmap(themeWriter, src, name, top, right, bottom, left, top, right, bottom, left);
   }

   private void createBorderBitmap(
      Theme$Writer themeWriter,
      String src,
      String name,
      int top,
      int right,
      int bottom,
      int left,
      int topCorners,
      int rightCorners,
      int bottomCorners,
      int leftCorners
   ) throws IOException {
      if (top > 30 || right > 30 || bottom > 30 || left > 30 || topCorners > 30 || rightCorners > 30 || bottomCorners > 30 || leftCorners > 30) {
         throw new IOException("Border Definition exceeds maximum value of: 30");
      }

      if (themeWriter.getBorder(name) == null) {
         byte[] imageBytes = this._themeResource.getResource(src);
         if (imageBytes != null) {
            EncodedImage image = EncodedImage.createEncodedImage(imageBytes, 0, imageBytes.length);
            Bitmap bitmap = image.getBitmap();
            Border border = new BorderBitmap(top, right, bottom, left, bitmap, topCorners, rightCorners, bottomCorners, leftCorners);
            themeWriter.putBorder(name, border);
         }
      }
   }

   @Override
   protected int remove() {
      int moduleHandle = CodeModuleManager.getModuleHandle(this._themeModuleName);
      int rc = CodeModuleManager.deleteModuleEx(moduleHandle, true);
      switch (rc) {
         case 0:
            return 0;
         case 6:
            return 1;
         default:
            return 2;
      }
   }
}
