package net.rim.device.apps.internal.browser.page;

import java.util.Hashtable;
import javax.microedition.io.SecurityInfo;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.servicebook.ServiceRoutingProperties;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.RibbonComponent;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.RibbonComponentIDs;
import net.rim.device.apps.api.ribbon.RibbonComponentInitializer;
import net.rim.device.apps.api.ribbon.SimpleRibbonComponent;
import net.rim.device.apps.api.ribbon.TextRibbonComponent;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.IconCollection;

public final class PageHeaderField
   extends Field
   implements RibbonComponent$RibbonComponentChangeListener,
   RadioStatusListener,
   WLANListenerInternal,
   ServiceRoutingListener2 {
   private String _title;
   private EncodedImage _icon;
   private EncodedImage _wiFiOnIcon;
   private EncodedImage _wiFiConnectedIcon;
   private SecurityInfo _securityInfo;
   private SimpleRibbonComponent _indicatorField;
   private TextRibbonComponent _coverageField;
   private SimpleRibbonComponent _signalField;
   private SimpleRibbonComponent _wlanSignalField;
   private int _indicatorHeight;
   private Font _indicatorFont;
   private int _x1;
   private int _x2;
   private int _x3;
   private int _x4;
   private int _y1;
   BrowserImpl _browser = BrowserDaemonRegistry.getInstance();
   private PageHeaderField$RedrawRunnable _redrawRunnable = new PageHeaderField$RedrawRunnable(this);
   private boolean _isWiFiSupported = WLAN.isSupported();
   private boolean _showWiFiIcon;
   private boolean _usingWiFi;
   private boolean _hasWiFiConnection;
   private boolean _hasWiFiDataConnection;
   private boolean _hasGANConnection;
   private boolean _layoutUpdateRequired;
   private BrowserConfigRecord _currentConfig;
   private static final Tag TAG = Tag.create("title");
   private static Hashtable _indicatorParameters = new Hashtable();
   private static Hashtable _signalParameters = new Hashtable();
   private static Hashtable _wlanSignalParameters = new Hashtable();
   private static Hashtable _coverageParameters = new Hashtable();
   private static ThemeAttributeSet _signalAttributes;
   private static final int SEPARATOR_HEIGHT = 1;
   private static final int COMPONENT_GAP = 2;
   private static String INDICATOR_ICONS = "net_rim_Browser_Indicator";
   private static final int SIGNAL_ARROW_WIDTH = Graphics.isColor() ? (InternalServices.isReducedFormFactor() ? -2 : 12) : 7;
   private static IconCollection _browserIndicatorIcons;
   private static final int ICON_COUNT = 4;
   private static int _browserIndicatorIconWidth;
   private static int _browserIndicatorIconHeight;
   private static int _themeGeneration;

   public final void setTitle(String title, EncodedImage icon) {
      if (title != null) {
         title = title.trim();
         if (title.length() == 0) {
            title = null;
         }
      }

      synchronized (Application.getEventLock()) {
         this._title = title;
         this._icon = icon;
      }

      if (this._icon != null) {
         int prefHeight = this.getPreferredHeight();
         int iconHeight = this._icon.getScaledHeight();
         if (iconHeight > prefHeight) {
            this._icon.setScale((iconHeight + prefHeight - 1) / prefHeight);
         }
      }

      this.redraw();
   }

   public final void mobilityManagementEvent(int eventCode, int cause) {
   }

   public final void setSecurityInfo(SecurityInfo info) {
      this._securityInfo = info;
      this.redraw();
   }

   public final void configChanged() {
      BrowserSession session = BrowserSession.getCurrentSession();
      this._currentConfig = session != null ? session.getConfig() : null;
      this.updateServiceRoute();
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      if (!this._hasGANConnection && (service & 16384) != 0) {
         this._hasGANConnection = true;
         this._layoutUpdateRequired = true;
         this.redraw();
      }
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void ribbonComponentChanged(RibbonComponent component) {
      this.redraw();
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      if (this._hasGANConnection && (service & 16384) == 0) {
         this._hasGANConnection = false;
         this._layoutUpdateRequired = true;
         this.redraw();
      } else {
         if (!this._hasGANConnection && (service & 16384) != 0) {
            this._hasGANConnection = true;
            this._layoutUpdateRequired = true;
            this.redraw();
         }
      }
   }

   @Override
   public final void radioStatus(boolean started) {
   }

   @Override
   public final void networkSuccess() {
      this._hasWiFiConnection = true;
      if (this._usingWiFi) {
         this.redraw();
      }
   }

   @Override
   public final void networkFail(int status, int error, int extendedInfo) {
      this._hasWiFiConnection = false;
      if (this._usingWiFi) {
         this.redraw();
      }
   }

   @Override
   public final void networkFound(int handle) {
   }

   @Override
   public final void networkApChange() {
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (this._currentConfig != null && StringUtilities.strEqualIgnoreCase(service, this._currentConfig.getPropertyAsString(4), 1701707776)) {
         this.updateServiceRoute();
      }
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      ServiceRouting sr = ServiceRouting.getInstance();
      ServiceRoutingProperties srp = sr.getInterface(routeHandle);
      if (srp != null && srp.getLinkType() == 3) {
         this._hasWiFiDataConnection = sr.isRouteActive(3);
         if (this._usingWiFi) {
            this.redraw();
         }
      }
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
   }

   PageHeaderField() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      Factory factory = repos.getFactory(RibbonComponentIDs.SIGNAL_NAME);
      RibbonComponent component = (RibbonComponent)factory.createInstance(null);
      RibbonComponentInitializer rci = (RibbonComponentInitializer)component;
      rci.initialize(_signalParameters, _signalAttributes);
      this._signalField = (SimpleRibbonComponent)component;
      factory = repos.getFactory(RibbonComponentIDs.HORIZ_INDICATOR_NAME);
      component = (RibbonComponent)factory.createInstance(null);
      this._indicatorField = (SimpleRibbonComponent)component;
      ((RibbonComponentInitializer)this._indicatorField).initialize(_indicatorParameters, null);
      if (Display.getWidth() > 300 && !RadioInfo.areWAFsSupported(2)) {
         factory = repos.getFactory(RibbonComponentIDs.COVERAGE_NAME);
         if (factory != null) {
            component = (RibbonComponent)factory.createInstance(null);
            if (component != null) {
               this._coverageField = (TextRibbonComponent)component;
               this._coverageField.initialize(_coverageParameters, null);
            }
         }
      }

      if (this._isWiFiSupported) {
         factory = repos.getFactory("WLANSignalLevel");
         component = (RibbonComponent)factory.createInstance(null);
         rci = (RibbonComponentInitializer)component;
         rci.initialize(_wlanSignalParameters, _signalAttributes);
         this._wlanSignalField = (SimpleRibbonComponent)component;
         this._wiFiOnIcon = ThemeManager.getActiveTheme().getImage("wifi_on", true);
         this._wiFiConnectedIcon = ThemeManager.getActiveTheme().getImage("wifi_connected", true);
         if (WLAN.isAssociated() != null) {
            this._hasWiFiConnection = true;
            if (ServiceRouting.getInstance().isRouteActive(3)) {
               this._hasWiFiDataConnection = true;
            }
         }

         this._hasGANConnection = (RadioInfo.getNetworkService() & 16384) != 0;
      }

      this.setTag(TAG);
      this.setId("browser");
      this.configChanged();
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setFont(null);
      Font font = this.getFont();
      int fontHeight = font.getHeight();
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      int size = Math.max(this._signalField.getComponentHeight(), Ui.convertSize(6, 3, 0));
      attributesWriter.setFontSize(fontHeight > size ? size : fontHeight, 0, false);
      _coverageParameters.put("font-size", Integer.toString(fontHeight > size ? size : fontHeight));
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      attributes.apply();
      if (fontHeight > size) {
         this.setFont(font.derive(font.getStyle(), size));
      }

      RibbonComponentInitializer rci = (RibbonComponentInitializer)this._signalField;
      rci.initialize(_signalParameters, _signalAttributes);
      this._signalField.applyTheme();
      rci = (RibbonComponentInitializer)this._indicatorField;
      rci.initialize(_indicatorParameters, attributes);
      this._indicatorField.applyTheme();
      if (this._coverageField != null) {
         rci = this._coverageField;
         rci.initialize(_coverageParameters, attributes);
         this._coverageField.applyTheme();
      }

      if (this._isWiFiSupported) {
         rci = (RibbonComponentInitializer)this._wlanSignalField;
         rci.initialize(_wlanSignalParameters, _signalAttributes);
         this._wlanSignalField.applyTheme();
         this._wiFiOnIcon = ThemeManager.getActiveTheme().getImage("wifi_on", true);
         this._wiFiConnectedIcon = ThemeManager.getActiveTheme().getImage("wifi_connected", true);
      }
   }

   @Override
   public final void onUndisplay() {
      super.onUndisplay();
      this._indicatorField.setChangeListener(null);
      this._signalField.setChangeListener(null);
      if (this._coverageField != null) {
         this._coverageField.setChangeListener(null);
      }

      if (this._isWiFiSupported) {
         this._wlanSignalField.setChangeListener(null);
         this._browser.removeRadioListener(this);
         ServiceRouting.getInstance().removeListener(this);
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      this._indicatorField.setChangeListener(this);
      this._signalField.setChangeListener(this);
      if (this._coverageField != null) {
         this._coverageField.setChangeListener(this);
      }

      if (this._isWiFiSupported) {
         this._wlanSignalField.setChangeListener(this);
         this._browser.addRadioListener(this);
         ServiceRouting.getInstance().addListener(this);
      }
   }

   private final void updateServiceRoute() {
      if (this._isWiFiSupported) {
         boolean usingWiFi = false;
         if (this._hasWiFiConnection && this._currentConfig != null) {
            String transportUID = this._currentConfig.getPropertyAsString(4);
            String transportCID = this._currentConfig.getPropertyAsString(3);
            if (StringUtilities.strEqualIgnoreCase(transportCID, BrowserConfigRecord.IPPP_SERVICE_CID)) {
               ServiceRouting sr = ServiceRouting.getInstance();
               int[] handles = sr.getRouteHandles(3);
               usingWiFi = false;
               if (handles != null) {
                  for (int i = handles.length - 1; i >= 0; i--) {
                     if (sr.isServiceRoutable(transportUID, handles[i])) {
                        usingWiFi = true;
                        break;
                     }
                  }
               }
            } else if (StringUtilities.strEqualIgnoreCase(transportCID, WPTCPServiceRecord.SERVICE_CID, 1701707776)) {
               ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(transportUID, transportCID);
               if (sr != null) {
                  WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(sr);
                  if (record != null && StringUtilities.strEqualIgnoreCase(record.getPropertyAsString(19), "wifi", 1701707776)) {
                     usingWiFi = this._hasWiFiConnection;
                  }
               }
            }
         }

         if (this._usingWiFi != usingWiFi) {
            this._usingWiFi = usingWiFi;
            this._layoutUpdateRequired = true;
            this.redraw();
         }
      }
   }

   private final void updateIndicators() {
      int generation = ThemeManager.getGeneration();
      if (_browserIndicatorIcons == null || _themeGeneration != generation) {
         _themeGeneration = generation;
         _browserIndicatorIcons = IconCollection.get(INDICATOR_ICONS, 4);
         _browserIndicatorIconWidth = _browserIndicatorIcons.getWidth(0, 0);
         _browserIndicatorIconHeight = _browserIndicatorIcons.getHeight(0, 0);
      }

      this._indicatorHeight = Math.max(this._signalField.getComponentHeight(), _browserIndicatorIconHeight);
      if (this._indicatorFont == null || this._indicatorFont.getHeight() > this._indicatorHeight) {
         Font font = this.getFont();
         this._indicatorFont = font.derive(font.getStyle(), this._indicatorHeight);
      }
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      this.updateIndicators();
      int contentHeight = Math.max(this._indicatorField.getComponentHeight(), this._indicatorHeight);
      contentHeight = Math.max(contentHeight, this.getFont().getHeight());
      return contentHeight + 1 + 1;
   }

   @Override
   protected final void layout(int width, int height) {
      int fieldWidth = Math.min(this.getPreferredWidth(), width);
      int fieldHeight = this.getPreferredHeight();
      if (height < fieldHeight) {
         this.setExtent(fieldWidth, 0);
      } else {
         int widthAvailable = fieldWidth;
         if (this._usingWiFi) {
            this._showWiFiIcon = true;
            this._x1 = 0;
            this._x2 = widthAvailable - 2 - SIGNAL_ARROW_WIDTH;
            if (this._wiFiOnIcon != null) {
               this._x2 = this._x2 - this._wiFiOnIcon.getScaledWidth();
            }
         } else {
            this._showWiFiIcon = false;
            int signalWidth = this._hasGANConnection ? this._wlanSignalField.getComponentWidth() : this._signalField.getComponentWidth();
            this._x1 = widthAvailable - signalWidth - 2 - SIGNAL_ARROW_WIDTH;
            if (this._coverageField != null) {
               int coverageWidth = this._coverageField.getPreferredWidth();
               if (coverageWidth < this._x1) {
                  this._coverageField.setDimensionsAvailable(coverageWidth, fieldHeight);
               }

               this._x2 = this._x1 - 2 - this._coverageField.getComponentWidth();
            } else {
               this._x2 = this._x1;
            }
         }

         this._x3 = this._x2 - 2 - _browserIndicatorIconWidth;
         this._x4 = this._x3 - 2 - _browserIndicatorIconWidth;
         this._y1 = Math.max(0, fieldHeight - this._indicatorHeight - 1 - 1 >> 1);
         this.setExtent(fieldWidth, fieldHeight);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      graphics.pushRegion(0, 0, 1073741823, 1073741823, 0, 0);
      if (this._showWiFiIcon) {
         EncodedImage wiFiIcon = this._hasWiFiDataConnection ? this._wiFiConnectedIcon : this._wiFiOnIcon;
         if (wiFiIcon != null) {
            graphics.drawImage(this._x2, 1, wiFiIcon.getScaledWidth(), wiFiIcon.getScaledHeight(), wiFiIcon, 0, 0, 0);
         }
      } else {
         if (this._hasGANConnection) {
            this._wlanSignalField.paintComponent(graphics, this._x1, this._y1, this._wlanSignalField.getComponentWidth(), this._indicatorHeight, null);
         } else {
            this._signalField.paintComponent(graphics, this._x1, this._y1, this._signalField.getComponentWidth(), this._indicatorHeight, null);
         }

         if (this._coverageField != null) {
            this._coverageField.paintComponent(graphics, this._x2, 1, this._coverageField.getComponentWidth(), this.getHeight(), null);
         }
      }

      int height = this.getContentHeight();
      _browserIndicatorIcons.paint(graphics, this._x3, this._y1, _browserIndicatorIconWidth, height, this._securityInfo != null ? 1 : 0);
      _browserIndicatorIcons.paint(graphics, this._x4, this._y1, _browserIndicatorIconWidth, height, this.hasCoverage() ? 3 : 2);
      int remaining = this._x4 - 2;
      this._indicatorField.setDimensionsAvailable(remaining, this._indicatorHeight);
      Font font = graphics.getFont();
      graphics.setFont(this._indicatorFont);
      this._indicatorField.paintComponent(graphics, 0, this._y1, remaining, this._indicatorHeight, null);
      graphics.setFont(font);
      remaining -= this._indicatorField.getComponentWidth() + 2;
      graphics.popContext();
      int xStart = 0;
      if (this._icon != null) {
         int width = this._icon.getScaledWidth();
         if (remaining > width) {
            graphics.drawImage(0, 1, width, this._icon.getScaledHeight(), this._icon, 0, 0, 0);
            remaining -= width;
            xStart = width + 1;
         }
      }

      if (this._title != null && remaining > 0) {
         DrawTextParam drawParam = Ui.getTmpDrawTextParam();
         drawParam.iTruncateWithEllipsis = 2;
         drawParam.iMaxAdvance = remaining;
         graphics.drawText(this._title, 0, this._title.length(), xStart, 1, drawParam, null);
         Ui.returnTmpDrawTextParam(drawParam);
      }

      XYRect extent = this.getExtent();
      int yPos = extent.height - 1;
      graphics.drawLine(0, yPos, extent.width, yPos);
   }

   private final boolean hasCoverage() {
      String serviceCID = null;
      int coverageLevel = this._browser.getLastCoverage();
      BrowserSession session = BrowserSession.getCurrentSession();
      if (session != null) {
         serviceCID = session.getConfig().getPropertyAsString(3);
      }

      return StringUtilities.strEqualIgnoreCase(serviceCID, WAPServiceRecord.SERVICE_CID, 1701707776)
            || StringUtilities.strEqualIgnoreCase(serviceCID, WPTCPServiceRecord.SERVICE_CID, 1701707776)
         ? this._browser.getBrowserState() == 4 && coverageLevel != 0
         : coverageLevel == 2;
   }

   private final void redraw() {
      if (this._browser.isForeground() || this._layoutUpdateRequired) {
         synchronized (this._redrawRunnable) {
            if (this._redrawRunnable._invokeLaterPending) {
               return;
            }

            this._redrawRunnable._invokeLaterPending = true;
         }

         this._browser.invokeLater(this._redrawRunnable);
      }
   }

   static final void access$100(PageHeaderField x0) {
      x0.updateLayout();
   }

   static final void access$200(PageHeaderField x0) {
      x0.invalidate();
   }

   static {
      _indicatorParameters.put("align", "right");
      _indicatorParameters.put("reportUsed", "reportUsed");
      Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
      ThemeAttributeSet$Writer attributesWriter = themeWriter.createThemeAttributeSetWriter(null);
      attributesWriter.setFontFamily(FontFamily.FAMILY_SYSTEM);
      attributesWriter.setFontStyle(0);
      attributesWriter.setFontSize(1000, 4194306, false);
      _signalAttributes = attributesWriter.getThemeAttributeSet();
      _signalAttributes.apply();
      _signalParameters.put("xOfs", "10");
      _signalParameters.put("yOfs", "2");
      _signalParameters.put("align", "left");
      _signalParameters.put("icon", "net_rim_Browser_SignalLevel");
      _wlanSignalParameters.put("xOfs", "10");
      _wlanSignalParameters.put("yOfs", "2");
      _wlanSignalParameters.put("align", "left");
      _wlanSignalParameters.put("icon", "net_rim_Browser_WlanSignalLevel");
      _coverageParameters.put("id", "browser");
      _coverageParameters.put("valign", "top");
      _coverageParameters.put("align", "center");
      _coverageParameters.put("font-family", FontFamily.FAMILY_SYSTEM);
      _coverageParameters.put("font-size", Integer.toString(Ui.convertSize(1000, 4194306, 0)));
   }
}
