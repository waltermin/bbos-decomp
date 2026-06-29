package net.rim.device.apps.internal.help;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Stack;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.HistoryEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.tid.im.layout.SLKeyLayout;

class HelpScreen extends AppsMainScreen implements RenderingApplication {
   private RenderingSession _renderingSession = RenderingSession.getNewInstance();
   private Field _currentField;
   private String _currentURL;
   private Stack _history;
   private String _errorMessage;
   private MenuItem _backMenuItem;
   private byte[] _extendedLinks;
   Hashtable _configToUse;
   String _currentBrowserConfigUID;
   private static final String BASE_MODULE_NAME = ((StringBuffer)(new Object("net_rim_bb_help_")))
      .append(getNetworkType())
      .append(getFormFactor())
      .append("__")
      .toString();
   private static final String FILE_EXTENSION;
   private static final int WML_B;
   private static final int WML_CARD;
   private static final int WML_OPTION;
   private static final int WML_P;
   private static final int WML_SELECT;
   private static final int WML_WML;
   private static final int WML_NAME;
   private static final int WML_ONPICK;
   private static final int WML_TITLE;
   private static final int WML_VALUE;
   private static final int WML_ELEMENT_HAS_ATTRIBUTES;
   private static final int WML_ELEMENT_HAS_CONTENT;

   protected void loadHelpTopic(String topic) {
      if (topic != null) {
         String baseModuleName = null;
         boolean isURL = topic.indexOf(58) != -1;
         if (!isURL) {
            int slash = topic.indexOf(47);
            if (slash != -1) {
               baseModuleName = topic.substring(0, slash);
               topic = topic.substring(slash + 1);
            }
         }

         this.displayBrowserContent(this.getBrowserContent(getURL(topic, baseModuleName, isURL)));
      }
   }

   protected String customLoadResource(String url) {
      return url;
   }

   protected boolean canGoBack() {
      return this._history != null && !this._history.empty();
   }

   public void displayBrowserContent(BrowserContent browserField) {
      this.displayBrowserContent(browserField, true);
   }

   protected Manager getContentContainer() {
      return this;
   }

   BrowserContent getBrowserContent(HttpConnection conn, Event e) {
      return this.getBrowserContent(conn, e, -1, -1);
   }

   protected RenderingOptions getRenderingOptions() {
      return this._renderingSession.getRenderingOptions();
   }

   protected void topicFinishedLoading() {
   }

   @Override
   public int getAvailableWidth(BrowserContent browserField) {
      return Display.getWidth();
   }

   @Override
   public int getHistoryPosition(BrowserContent browserField) {
      return this._history == null ? 0 : this._history.size();
   }

   @Override
   public String getHTTPCookie(String url) {
      return null;
   }

   @Override
   public HttpConnection getResource(RequestedResource resource, BrowserContent referrer) {
      if (resource == null) {
         return null;
      }

      if (resource.isCacheOnly()) {
         return null;
      }

      String url = resource.getUrl();
      if (url == null) {
         return null;
      }

      if (referrer != null && !url.startsWith("cod:")) {
         new RetrieveThread(resource, referrer, this).start();
         return null;
      }

      try {
         return (HttpConnection)Connector.open(this.customLoadResource(url));
      } finally {
         ;
      }
   }

   @Override
   public void invokeRunnable(Runnable runnable) {
      ((Thread)(new Object(runnable))).run();
   }

   @Override
   public int getAvailableHeight(BrowserContent browserField) {
      return Display.getWidth();
   }

   @Override
   public Object eventOccurred(Event event) {
      switch (event.getUID()) {
         case 10002:
            this.close();
            return null;
         case 10005:
            HistoryEvent historyEvent = (HistoryEvent)event;
            if (historyEvent.getType() == 0 && historyEvent.getIndex() == -1) {
               this.back();
            }
         default:
            return null;
         case 10010:
            new UrlRequestedEventThread((UrlRequestedEvent)event, this).start();
            return null;
      }
   }

   @Override
   protected boolean keyCharUnhandled(char key, int status, int time) {
      if (key == 27) {
         if (!this.back()) {
            this.close();
         }

         return true;
      } else {
         if (key == ' ') {
            this.scroll((status & 2) == 0 ? 512 : 256);
            return true;
         }

         char latinKey = key;
         SLKeyLayout keyLayout = Keypad.getLayout();
         if (keyLayout != null) {
            latinKey = UiInternal.map(keyLayout.getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
         }

         if (InternalServices.isReducedFormFactor()) {
            switch (CharacterUtilities.toUpperCase(latinKey, 1701707776)) {
               case 'B':
                  if (this.dispatchTrackwheelEvent(519, 1, 0, time)) {
                     return true;
                  }

                  return false;
               case 'C':
                  this.scroll(2);
                  return true;
               case 'E':
                  this.scroll(1);
                  return true;
               case 'G':
               case 'M':
                  this.scroll(512);
                  return true;
               case 'T':
                  if (this.dispatchTrackwheelEvent(519, -1, 0, time)) {
                     return true;
                  }

                  return false;
               case 'U':
                  this.scroll(256);
                  return true;
            }
         } else {
            latinKey = CharacterUtilities.toUpperCase(latinKey, 1701707776);
            if (latinKey == this.getHotkey(366)) {
               this.scroll(1);
               return true;
            }

            if (latinKey == this.getHotkey(367)) {
               this.scroll(2);
               return true;
            }
         }

         return super.keyCharUnhandled(key, status, time);
      }
   }

   private char getHotkey(int resourceID) {
      return CharacterUtilities.toUpperCase(BrowserResources.getString(resourceID).charAt(0), 1701707776);
   }

   static String getURL(String topic, boolean isURL) {
      return getURL(topic, null, isURL);
   }

   private static String getURL(String topic, String baseModuleName, boolean isURL) {
      if (topic == null) {
         throw new Object();
      }

      String url = null;
      if (isURL) {
         if (StringUtilities.startsWithIgnoreCase(topic, "help:", 1701707776)) {
            int moduleStart = 5;
            int length = topic.length();

            while (moduleStart < length && topic.charAt(moduleStart) == '/') {
               moduleStart++;
            }

            if (moduleStart < length) {
               int moduleEnd = topic.indexOf(47, moduleStart);
               if (moduleEnd > moduleStart) {
                  baseModuleName = topic.substring(moduleStart, moduleEnd);
                  topic = topic.substring(moduleEnd + 1);
               }
            }
         } else {
            url = topic;
         }
      } else if (baseModuleName == null) {
         topic = topicNameToID(topic);
      }

      if (url == null) {
         String moduleName = getModuleName(baseModuleName);
         StringBuffer urlBuffer = (StringBuffer)(new Object());
         urlBuffer.append("cod://");
         urlBuffer.append(moduleName != null ? moduleName : baseModuleName);
         urlBuffer.append('/');
         boolean addExtension = true;
         int topicLength = topic.length();

         for (int i = 0; i < topicLength; i++) {
            char ch = topic.charAt(i);
            if (ch == '.') {
               addExtension = false;
            } else if (addExtension && (ch == '#' || ch == '?')) {
               urlBuffer.append(".wmlc");
               addExtension = false;
            }

            urlBuffer.append(ch);
         }

         if (addExtension) {
            urlBuffer.append(".wmlc");
         }

         url = urlBuffer.toString();
      }

      return url;
   }

   private void displayBrowserContent(BrowserContent browserField, boolean updateHistory) {
      synchronized (Application.getEventLock()) {
         if (updateHistory && this._currentURL != null) {
            if (this._history == null) {
               this._history = (Stack)(new Object());
            }

            int scrollPosition = -1;
            int focusPosition = -1;
            if (this._currentField instanceof Object && !(this._currentField instanceof Object)) {
               Manager contentManager = this._currentField.getManager();
               if (contentManager != null) {
                  XYRect rect = (XYRect)(new Object());
                  contentManager.getFocusRect(rect);
                  scrollPosition = contentManager.getVerticalScroll();
                  focusPosition = scrollPosition + rect.y;
               }
            }

            this._history.push(new HistoryNode(this._currentURL, scrollPosition, focusPosition));
         }

         Manager contentContainer = this.getContentContainer();
         contentContainer.deleteAll();
         Field contentField = null;
         if (browserField != null) {
            contentField = browserField.getDisplayableContent();
         } else if (this._errorMessage != null) {
            contentField = (Field)(new Object(null, this._errorMessage, 1000000, 9007199254740992L));
         }

         if (contentField != null) {
            contentContainer.add(contentField);
         }

         this._currentURL = browserField != null ? browserField.getURL() : null;
         this._currentField = contentField;
      }
   }

   private BrowserContent getBrowserContent(String absoluteUrl) {
      return this.getBrowserContent(absoluteUrl, -1, -1);
   }

   private BrowserContent getBrowserContent(String absoluteUrl, int scrollPosition, int focusPosition) {
      HttpConnection conn = null;

      try {
         conn = (HttpConnection)Connector.open(absoluteUrl);
      } finally {
         return this.getBrowserContent(conn, null, scrollPosition, focusPosition);
      }

      return this.getBrowserContent(conn, null, scrollPosition, focusPosition);
   }

   private static String topicNameToID(String topicName) {
      if (topicName != null && topicName.length() > 0) {
         int id = -1;
         switch (topicName.charAt(0)) {
            case 'a':
               if (topicName.equals("alarm")) {
                  id = 26006;
               }
               break;
            case 'b':
               if (topicName.equals("bluetooth")) {
                  id = 26045;
               } else if (topicName.equals("brickbreaker")) {
                  id = 26062;
               } else if (topicName.equals("browser")) {
                  id = 27786;
               }
               break;
            case 'c':
               if (topicName.equals("calculator")) {
                  id = 26116;
               } else if (topicName.equals("calendar")) {
                  id = 26123;
               } else if (topicName.equals("contacts")) {
                  id = 26152;
               } else if (topicName.equals("coverage")) {
                  id = 26285;
               }
               break;
            case 'd':
               if (topicName.equals("date_and_time")) {
                  id = 29521;
               } else if (topicName.equals("display")) {
                  id = 26185;
               }
               break;
            case 'l':
               if (topicName.equals("language")) {
                  id = 30960;
               }
               break;
            case 'm':
               if (topicName.equals("memos")) {
                  id = 26225;
               } else if (topicName.equals("messages_index")) {
                  id = 34140;
               } else if (topicName.equals("mms_messages")) {
                  id = 26274;
               }
               break;
            case 'p':
               if (topicName.equals("phone")) {
                  id = 181552;
               }
               break;
            case 's':
               if (topicName.equals("searching")) {
                  id = 26358;
               } else if (topicName.equals("security")) {
                  id = 26381;
               } else if (topicName.equals("sim_card")) {
                  id = 26400;
               } else if (topicName.equals("sms_messages")) {
                  id = 26407;
               } else if (topicName.equals("sounds")) {
                  id = 26428;
               }
               break;
            case 't':
               if (topicName.equals("tasks")) {
                  id = 26446;
               } else if (topicName.equals("third_party_program_control")) {
                  id = 28057;
               } else if (topicName.equals("typing")) {
                  id = 26464;
               }
         }

         if (id >= 0) {
            return String.valueOf(id);
         }
      }

      return topicName;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private BrowserContent getBrowserContent(HttpConnection conn, Event e, int scrollPosition, int focusPosition) {
      BrowserContent field = null;
      this._errorMessage = null;

      try {
         field = this._renderingSession.getBrowserContent(this.getFilteredConnection(conn), this, e);
      } catch (Throwable var8) {
         this._errorMessage = re.getMessage();
         return null;
      }

      new RenderingThread(field, scrollPosition, focusPosition, this).start();
      return field;
   }

   private synchronized HttpConnection getFilteredConnection(HttpConnection conn) {
      if (conn != null) {
         String url = conn.getURL();
         if (url != null && url.startsWith("cod://net_rim_bb_help_")) {
            this._currentBrowserConfigUID = null;
            if ("index.wmlc".equals(conn.getFile())) {
               if (this._extendedLinks == null) {
                  String privateMdsUID = null;
                  String defaultConfigUID = GeneralProperty.getDefaultBrowserConfigServiceUID();
                  String enterpriseHelp = ITPolicy.getString(42, 1);
                  String enterpriseGroupLabel = ITPolicy.getString(42, 2);
                  String carrierHelp = null;
                  String carrierGroupLabel = null;
                  String carrierUID = null;
                  BrowserConfigRecord[] browserConfigs = BrowserConfigRecord.getValidBrowserConfigRecords();

                  for (int i = 0; i < browserConfigs.length; i++) {
                     BrowserConfigRecord config = browserConfigs[i];
                     int configType = config.getPropertyAsInt(12);
                     switch (configType) {
                        case 0:
                        case 4:
                        case 7:
                        case 8:
                           if (carrierHelp == null || configType == 4 || configType == 8) {
                              String newHelpLinks = config.getPropertyAsString(44);
                              if (newHelpLinks != null && newHelpLinks.length() > 0) {
                                 carrierHelp = newHelpLinks;
                                 carrierGroupLabel = config.getLocalizedString(45);
                                 carrierUID = config.getUid();
                              }
                           }
                           break;
                        case 1:
                           String configUID = config.getUid();
                           if (privateMdsUID == null || configUID != null && configUID.equals(defaultConfigUID)) {
                              privateMdsUID = configUID;
                           }
                     }
                  }

                  if (carrierHelp != null || enterpriseHelp != null) {
                     if (privateMdsUID == null) {
                        privateMdsUID = defaultConfigUID;
                     }

                     ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object(256));

                     label125:
                     try {
                        this._configToUse = (Hashtable)(new Object(2));
                        this.writeExtendedHelpLinks(baos, carrierHelp, carrierGroupLabel, carrierUID);
                        this.writeExtendedHelpLinks(baos, enterpriseHelp, enterpriseGroupLabel, privateMdsUID);
                        this._extendedLinks = baos.toByteArray();
                     } finally {
                        break label125;
                     }
                  }
               }

               if (this._extendedLinks == null) {
                  this._extendedLinks = new byte[0];
                  return conn;
               }

               if (this._extendedLinks.length > 0) {
                  return new FilteredHttpConnection(conn, this._extendedLinks);
               }
            }
         } else if (this._configToUse != null && url != null) {
            String configForURL = (String)this._configToUse.get(url);
            if (configForURL != null) {
               this._currentBrowserConfigUID = configForURL;
            }
         }
      }

      return conn;
   }

   private void writeExtendedHelpLinks(OutputStream os, String helpLinks, String helpGroupLabel, String configUID) {
      if (helpLinks != null) {
         ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object(128));
         int numLinks = 0;
         StringTokenizer tokenizer = (StringTokenizer)(new Object(helpLinks, '|'));
         String uri = null;

         while (tokenizer.hasMoreTokens()) {
            uri = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens()) {
               break;
            }

            String label = tokenizer.nextToken();
            if (uri == null
               || uri.length() <= 0
               || label == null
               || label.length() <= 0
               || numLinks >= 1 && (helpGroupLabel == null || helpGroupLabel.length() <= 0)) {
               break;
            }

            baos.write(245);
            baos.write(36);
            int colon = uri.indexOf(58);
            if (colon == -1) {
               uri = ((StringBuffer)(new Object("http://"))).append(uri).toString();
            }

            writeWBXMLString(baos, uri);
            baos.write(77);
            writeWBXMLString(baos, ((StringBuffer)(new Object("ext"))).append(configUID).append(numLinks).toString());
            baos.write(1);
            writeWBXMLString(baos, label);
            baos.write(1);
            numLinks++;
         }

         if (numLinks > 1) {
            os.write(245);
            os.write(36);
            os.write(3);
            String dataURL = writeDataURL(os, helpGroupLabel, baos.toByteArray(), configUID);
            os.write(0);
            os.write(77);
            writeWBXMLString(os, ((StringBuffer)(new Object("ext"))).append(configUID).toString());
            os.write(1);
            writeWBXMLString(os, helpGroupLabel);
            os.write(1);
            this._configToUse.put(dataURL, configUID);
            return;
         }

         if (numLinks == 1) {
            os.write(baos.toByteArray());
            if (uri != null) {
               this._configToUse.put(uri, configUID);
            }
         }
      }
   }

   private static byte[] strToBytes(String str) {
      byte[] bytes = null;

      try {
         return str.getBytes("utf-8");
      } finally {
         return str.getBytes();
      }
   }

   private static void writeWBXMLString(OutputStream os, String str) {
      os.write(3);
      os.write(strToBytes(str));
      os.write(0);
   }

   private static String writeDataURL(OutputStream os, String helpGroupLabel, byte[] options, String selectName) {
      String schemeAndType = "data:application/vnd.wap.wmlc;base64,";
      os.write(schemeAndType.getBytes());
      ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object(128));
      Base64OutputStream b64os = (Base64OutputStream)(new Object(baos));
      b64os.write(2);
      b64os.write(8);
      b64os.write(106);
      b64os.write(0);
      b64os.write(127);
      b64os.write(231);
      b64os.write(54);
      writeWBXMLString(b64os, helpGroupLabel);
      b64os.write(1);
      b64os.write(96);
      b64os.write(100);
      writeWBXMLString(b64os, helpGroupLabel);
      b64os.write(1);
      b64os.write(1);
      b64os.write(96);
      b64os.write(247);
      b64os.write(33);
      writeWBXMLString(b64os, selectName != null ? selectName : "ext");
      b64os.write(1);
      b64os.write(options);
      b64os.write(1);
      b64os.write(1);
      b64os.write(1);
      b64os.write(1);
      b64os.close();
      byte[] b64bytes = baos.toByteArray();
      os.write(b64bytes);
      return ((StringBuffer)(new Object())).append(schemeAndType).append((String)(new Object(b64bytes))).toString();
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this.canGoBack()) {
         if (this._backMenuItem == null) {
            this._backMenuItem = new HelpScreen$1(this, BrowserResources.getResourceBundle(), 105, 1180416, 100);
         }

         menu.add(this._backMenuItem);
      }
   }

   @Override
   public boolean onClose() {
      this.close();
      return true;
   }

   private static String getNetworkType() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return "unknown";
         case 4:
            if ((RadioInfo.getSupportedWAFs() & 1) != 0) {
               return "cdma_gprs";
            }

            return "cdma";
         case 5:
            return "iden";
         case 6:
            return "wlan";
         case 7:
         default:
            if (!InternalServices.isReducedFormFactor()) {
               return "umts";
            }
         case 3:
            return WLAN.isSupported() ? "gprs_wlan" : "gprs";
      }
   }

   private static String getFormFactor() {
      switch (InternalServices.getFormFactor()) {
         case 9:
            return "_charm";
         case 13:
            if (GPS.isSupported()) {
               return "_gps_positron";
            }

            return "_positron";
         case 14:
            if (!WLAN.isSupported() && RadioInfo.getNetworkType() != 4) {
               return "_b";
            }

            return "";
         case 15:
            if (GPS.isSupported()) {
               return "_c_gps";
            }

            return "_c";
         default:
            return "";
      }
   }

   HelpScreen(String topic, long flags) {
      super(flags);
      RenderingOptions renderingOptions = this._renderingSession.getRenderingOptions();
      if (renderingOptions != null) {
         renderingOptions.setProperty(4550690918222697397L, 26, false);
         renderingOptions.setProperty(4550690918222697397L, 31, Font.getDefault().getFontFamily().getName());
         renderingOptions.setProperty(4550690918222697397L, 32, Font.getDefaultHeight(2));
      }

      this.loadHelpTopic(topic);
   }

   HelpScreen(String topic) {
      this(topic, 0);
   }

   private boolean back() {
      if (this.canGoBack()) {
         HistoryNode historyNode = (HistoryNode)this._history.pop();
         this.displayBrowserContent(this.getBrowserContent(historyNode.getURL(), historyNode.getScrollPosition(), historyNode.getFocusPosition()), false);
         return true;
      } else {
         return false;
      }
   }

   private static String getModuleName(Locale locale, String baseModuleName) {
      String language = locale.getLanguage();
      int languageLength = language == null ? 0 : language.length();
      String country = locale.getCountry();
      int countryLength = country == null ? 0 : country.length();
      String variant = locale.getVariant();
      int variantLength = variant == null ? 0 : variant.length();
      int baseModuleNameLength = baseModuleName.length();
      StringBuffer moduleBuffer = (StringBuffer)(new Object(baseModuleNameLength + languageLength + countryLength + variantLength + 2));
      moduleBuffer.append(baseModuleName);
      String moduleName = null;
      Resource resource = null;
      if (languageLength > 0) {
         moduleBuffer.append(language);
         if (countryLength > 0) {
            moduleBuffer.append('_');
            moduleBuffer.append(country);
            if (variantLength > 0) {
               moduleBuffer.append('_');
               moduleBuffer.append(variant);
               moduleName = moduleBuffer.toString();
               resource = Resource$Internal.getResourceClass(moduleName);
            }

            if (resource == null) {
               moduleBuffer.setLength(baseModuleNameLength + languageLength + 1 + countryLength);
               moduleName = moduleBuffer.toString();
               resource = Resource$Internal.getResourceClass(moduleName);
            }
         }

         if (resource == null) {
            moduleBuffer.setLength(baseModuleNameLength + languageLength);
            moduleName = moduleBuffer.toString();
            resource = Resource$Internal.getResourceClass(moduleName);
         }
      }

      return resource != null ? moduleName : null;
   }

   static String getModuleName(String baseModuleName) {
      String baseModuleNameWithLocaleSeparator = baseModuleName != null
         ? ((StringBuffer)(new Object())).append(baseModuleName).append("__").toString()
         : BASE_MODULE_NAME;
      String moduleName = getModuleName(Locale.getDefault(), baseModuleNameWithLocaleSeparator);
      if (moduleName == null) {
         Locale systemLocale = Locale.getDefaultForSystem();
         moduleName = getModuleName(systemLocale, baseModuleNameWithLocaleSeparator);
         if (moduleName == null) {
            if ("ca".equals(systemLocale.getLanguage())) {
               moduleName = getModuleName(Locale.get(1702035456), baseModuleNameWithLocaleSeparator);
            }

            if (moduleName == null) {
               moduleName = getModuleName(Locale.get(1701707776), baseModuleNameWithLocaleSeparator);
               if (moduleName == null && baseModuleName != null && Resource$Internal.getResourceClass(baseModuleName) != null) {
                  return baseModuleName;
               }
            }
         }
      }

      return moduleName;
   }
}
