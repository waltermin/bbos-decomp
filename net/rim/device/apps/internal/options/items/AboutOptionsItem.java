package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.vad.VADLanguageSetting;

public final class AboutOptionsItem extends OptionsListItem implements KeyListener {
   private int _fontHeight = Ui.convertSize(8, 3, 0);
   private int _currentScreenIndex = -1;
   private AboutOptionsItem$AboutNextScreenMenuItem _nextScreenMenuItem = new AboutOptionsItem$AboutNextScreenMenuItem(this);
   private AboutOptionsItem$CopyToClipBoardMenuItem _copyContentsMenuItem = new AboutOptionsItem$CopyToClipBoardMenuItem(this);
   private static AboutOptionsItem$AboutScreen[] _screens;
   private static final String BB_COLOUR_IMAGE = "About_BBImageColour.png";
   private static final String BB_LOGO_COLOUR_IMAGE = "BlackBerry_Logo_Vertical_Color.png";
   private static final String BB_MONO_IMAGE = "About_BBImageMono.png";
   private static final String JAVA_COLOUR_IMAGE = "About_JavaImageColour.png";
   private static final String JAVA_MONO_IMAGE = "About_JavaImageMono.gif";
   private static final String VOICESIGNAL_IMAGE = "voicesignal.png";
   private static final int BITMAP_HSPACE = 0;
   private static final int BITMAP_VSPACE = 5;
   private static final String EMPTY_STRING = "";
   private static final String N_A = "N/A";

   public AboutOptionsItem() {
      super(OptionsResources.getString(100));
   }

   private final void displayAdjacentScreen(boolean forward) {
      Screen screenToPop = null;
      int currentScreenIndex = this._currentScreenIndex;
      if (currentScreenIndex != -1) {
         screenToPop = _screens[currentScreenIndex];
      } else {
         currentScreenIndex = forward ? -1 : _screens.length;
      }

      currentScreenIndex += forward ? 1 : -1;
      if (currentScreenIndex >= _screens.length) {
         currentScreenIndex = 0;
      } else if (currentScreenIndex < 0) {
         currentScreenIndex = _screens.length - 1;
      }

      UiApplication.getUiApplication().pushScreen(_screens[currentScreenIndex]);
      if (screenToPop != null) {
         UiApplication.getUiApplication().popScreen(screenToPop);
      }

      Manager m = _screens[currentScreenIndex].getDelegate();
      this.updateAboutFont(m);
      this._currentScreenIndex = currentScreenIndex;
      int scroll = forward ? 0 : m.getVirtualHeight() - m.getHeight();
      m.setVerticalScroll(scroll < 0 ? 0 : scroll);
   }

   private final void updateAboutFont(Manager m) {
      int fieldCount = m.getFieldCount();
      Font cur_fnt = this.getAboutFont();

      for (int i = 0; i < fieldCount; i++) {
         if (m.getField(i) instanceof Object) {
            RichTextField field = (RichTextField)m.getField(i);
            Font field_fnt = field.getFont();
            if (!field_fnt.getFontFamily().getName().equals(cur_fnt.getFontFamily().getName())
               || field_fnt.getHeight() != cur_fnt.getHeight()
               || field_fnt.getStyle() != cur_fnt.getStyle()) {
               field.setFont(cur_fnt);
            }
         }

         if (m.getField(i) instanceof Object) {
            this.updateAboutFont((Manager)m.getField(i));
         }
      }
   }

   private final AboutOptionsItem$AboutScreen createRIMScreen() {
      AboutOptionsItem$AboutScreen screen = new AboutOptionsItem$AboutScreen(this, 299067162820608L);
      Bitmap image;
      if (Graphics.isColor()) {
         image = Bitmap.getBitmapResource("About_BBImageColour.png");
      } else {
         image = Bitmap.getBitmapResource("About_BBImageMono.png");
      }

      int totalBitmapFieldWidth = image.getWidth() + 0;
      BitmapField deviceBitmap = (BitmapField)(new Object(image, 4));
      deviceBitmap.setSpace(0, 5);
      Manager manager = this.getDisplayManager(totalBitmapFieldWidth);
      screen.add(manager);
      screen.getDelegate().setNonfocusableOverride(true);
      Font[] fonts = new Object[]{this.getAboutFont()};
      int[] infoOffsets = new int[]{0, 0};
      int[] copyrightOffsets = new int[]{0, 0};
      byte[] attributes = new byte[]{0};
      manager.add(deviceBitmap);
      String jtwiVersion = System.getProperty("microedition.jtwi.version");
      String mediaVersion = System.getProperty("microedition.media.version");
      String pimVersion = System.getProperty("microedition.pim.version");
      String[] info = new Object[]{
         DeviceInfo.getDeviceName(),
         this.getNetworkString(),
         ModuleInformation.trimVersion(DeviceInfo.getPlatformVersion()),
         this.getApplicationVersion(),
         "3.8.5.11c",
         System.getProperty("microedition.configuration"),
         System.getProperty("microedition.profiles"),
         jtwiVersion == null ? "N/A" : jtwiVersion,
         mediaVersion == null ? "N/A" : mediaVersion,
         pimVersion == null ? "N/A" : pimVersion,
         System.getProperty("microedition.io.file.FileConnection.version"),
         System.getProperty("microedition.bluetooth.version"),
         System.getProperty("microedition.location.version"),
         this.getSATSAVersion(),
         this.getWLANVersion()
      };
      String aboutText = MessageFormat.format(OptionsResources.getString(101), info);
      infoOffsets[1] = aboutText.length();
      Field field = (Field)(new Object(aboutText, infoOffsets, attributes, fonts, 36028797018963968L));
      screen.add(field);
      String copyableText = aboutText;
      aboutText = OptionsResources.getString(102);
      copyrightOffsets[1] = aboutText.length();
      field = (Field)(new Object(aboutText, copyrightOffsets, attributes, fonts, 36028797018963968L));
      screen.add(field);
      copyableText = ((StringBuffer)(new Object())).append(copyableText).append(aboutText).toString();
      screen.setScreenCopyableText(copyableText);
      return screen;
   }

   private final String getApplicationVersion() {
      String applicationVersion = DeviceInfo.getSoftwareVersion();
      return applicationVersion.length() == 0 ? "" : ModuleInformation.trimVersion(((StringBuffer)(new Object("v"))).append(applicationVersion).toString());
   }

   private final String getWLANVersion() {
      if (WLAN.isSupported()) {
         StringBuffer sb = (StringBuffer)(new Object());
         sb.append('\n');
         sb.append(OptionsResources.getString(105));
         String versionString = null;

         label31:
         try {
            versionString = InternalServices.getDriverVersionString(6);
         } finally {
            break label31;
         }

         sb.append(versionString == null ? "N/A" : versionString);
         return sb.toString();
      } else {
         return "";
      }
   }

   private final String getSATSAVersion() {
      if (SIMCard.isSupported() && SIMCard.isJSR177Supported()) {
         StringBuffer sb = (StringBuffer)(new Object());
         sb.append('\n');
         sb.append(OptionsResources.getString(2041));
         sb.append(System.getProperty("microedition.satsa.version"));
         return sb.toString();
      } else {
         return "";
      }
   }

   private final AboutOptionsItem$AboutScreen createJavaScreen() {
      AboutOptionsItem$AboutScreen screen = new AboutOptionsItem$AboutScreen(this, 299067162820608L);
      Bitmap javalogo;
      if (Graphics.isColor()) {
         javalogo = Bitmap.getBitmapResource("About_JavaImageColour.png");
      } else {
         javalogo = Bitmap.getBitmapResource("About_JavaImageMono.gif");
      }

      int totalBitmapFieldWidth = javalogo.getWidth() + (javalogo.getWidth() >> 1) + 0;
      BitmapField javaBitmap = (BitmapField)(new Object(javalogo, this.isHorizontalOrientation(totalBitmapFieldWidth) ? 0 : 4));
      javaBitmap.setSpace((javalogo.getWidth() >> 2) + 0, 5);
      Manager manager = this.getDisplayManager(totalBitmapFieldWidth);
      screen.add(manager);
      screen.getDelegate().setNonfocusableOverride(true);
      Font[] fonts = new Object[]{this.getAboutFont()};
      int[] infoOffsets = new int[]{0, 0};
      int[] copyrightOffsets = new int[]{0, 0};
      byte[] attributes = new byte[]{0};
      manager.add(javaBitmap);
      String aboutText = OptionsResources.getString(103);
      infoOffsets[1] = aboutText.length();
      Field field = (Field)(new Object(aboutText, infoOffsets, attributes, fonts, 36028797018963968L));
      manager.add(field);
      String copyableText = aboutText;
      aboutText = OptionsResources.getString(104);
      copyrightOffsets[1] = aboutText.length();
      field = (Field)(new Object(aboutText, copyrightOffsets, attributes, fonts, 36028797018963968L));
      screen.add(field);
      copyableText = ((StringBuffer)(new Object())).append(copyableText).append(aboutText).toString();
      screen.setScreenCopyableText(copyableText);
      return screen;
   }

   private final AboutOptionsItem$AboutScreen createVADScreen() {
      AboutOptionsItem$AboutScreen screen = new AboutOptionsItem$AboutScreen(this, 299067162820608L);
      Bitmap logo = Bitmap.getBitmapResource("voicesignal.png");
      int totalBitmapFieldWidth = logo.getWidth() + (logo.getWidth() >> 1) + 0;
      BitmapField vadBitmap = (BitmapField)(new Object(logo, this.isHorizontalOrientation(totalBitmapFieldWidth) ? 0 : 4));
      vadBitmap.setSpace((logo.getWidth() >> 2) + 0, 5);
      Manager manager = this.getDisplayManager(totalBitmapFieldWidth);
      screen.add(manager);
      screen.getDelegate().setNonfocusableOverride(true);
      Font[] fonts = new Object[]{this.getAboutFont()};
      byte[] attributes = new byte[]{0};
      manager.add(vadBitmap);
      String text = OptionsResources.getString(2029);
      int[] infoOffsets = new int[]{0, text.length()};
      Field field = (Field)(new Object(text, infoOffsets, attributes, fonts, 36028797018963968L));
      manager.add(field);
      String copyableText = text;
      String[] versions = VADLanguageSetting.getInstance().getVersionInfo();
      if (versions != null) {
         Arrays.add(versions, DeviceInfo.getDeviceName());
         text = MessageFormat.format(OptionsResources.getString(2028), versions);
         int[] versionOffsets = new int[]{0, text.length()};
         field = (Field)(new Object(text, versionOffsets, attributes, fonts, 36028797018963968L));
         manager.add((Field)(new Object()));
         manager.add(field);
         manager.add((Field)(new Object()));
         copyableText = ((StringBuffer)(new Object())).append(copyableText).append(text).toString();
      }

      text = OptionsResources.getString(2030);
      int[] copyrightOffsets = new int[]{0, text.length()};
      field = (Field)(new Object(text, copyrightOffsets, attributes, fonts, 36028797018963968L));
      manager.add(field);
      copyableText = ((StringBuffer)(new Object())).append(copyableText).append(text).toString();
      screen.setScreenCopyableText(copyableText);
      return screen;
   }

   private final boolean isHorizontalOrientation(int bitmapWidth) {
      int screenHalfWidth = Display.getWidth() >> 1;
      return bitmapWidth < screenHalfWidth;
   }

   private final Manager getDisplayManager(int bitmapWidth) {
      return (Manager)(this.isHorizontalOrientation(bitmapWidth) ? new Object() : new Object());
   }

   private final Font getAboutFont() {
      Font fnt = Font.getDefault();
      int style = fnt.getStyle();
      this._fontHeight = fnt.getHeight();
      return fnt.derive(style & -3, this._fontHeight, 0, 1, 0);
   }

   private final String getNetworkString() {
      StringBuffer sb = (StringBuffer)(new Object());
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 1) != 0) {
         if (InternalServices.isUMTSCapable()) {
            sb.append("3G");
         } else if (InternalServices.isEDGECapable()) {
            sb.append("EDGE");
         } else {
            sb.append("GPRS");
         }
      }

      if ((wafs & 2) != 0) {
         if (sb.length() != 0) {
            sb.append(", ");
         }

         sb.append("CDMA");
      }

      if ((wafs & 8) != 0) {
         if (sb.length() != 0) {
            sb.append(", ");
         }

         sb.append("iDEN");
      }

      if ((wafs & 4) != 0) {
         if (sb.length() != 0) {
            sb.append(", ");
         }

         sb.append("Wi-Fi");
      }

      return sb.toString();
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (InternalServices.isSoftwareCapable(11)) {
         _screens = new AboutOptionsItem$AboutScreen[]{this.createRIMScreen(), this.createJavaScreen(), this.createVADScreen()};
      } else {
         _screens = new AboutOptionsItem$AboutScreen[]{this.createRIMScreen(), this.createJavaScreen()};
      }

      for (int i = 0; i < _screens.length; i++) {
         _screens[i].addKeyListener(this);
      }
   }

   @Override
   protected final void open() {
      this._currentScreenIndex = -1;
      this.displayAdjacentScreen(true);
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int keyChar = Keypad.map(keycode);
      if (Keypad.key(keycode) != 259 && keyChar != 129 && keyChar != 130 && Keypad.key(keycode) != 4098) {
         this.nextScreen();
         return true;
      } else {
         return false;
      }
   }

   private final void nextScreen() {
      if (this._currentScreenIndex == _screens.length - 1) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      } else {
         this.displayAdjacentScreen(true);
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }
}
