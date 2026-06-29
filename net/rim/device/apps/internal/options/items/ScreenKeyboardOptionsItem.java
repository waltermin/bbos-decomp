package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.LED;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.idlescreen.IdleScreenOptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;
import net.rim.device.internal.ui.UiOptionsRegistry;
import net.rim.device.internal.ui.UiSettings;
import net.rim.vm.Array;

public final class ScreenKeyboardOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private VerticalFieldManager _fontSettings;
   private ChoiceField _fontFamilyField;
   private ObjectChoiceField _fontSizeField;
   private ChoiceField _fontStyleField;
   private ChoiceField _fontAntialiasField;
   private RichTextField _fontSampleField;
   private NumericChoiceField _contrastField;
   private NumericChoiceField _backlightBrightnessField;
   private TimeChoiceField _backlightTimeoutField;
   private BooleanChoiceField _ledIndicatorField;
   private BooleanChoiceField _automaticBacklightField;
   private BooleanChoiceField _keyToneField;
   private ChoiceField _keyDelayField;
   private ChoiceField _keyRateField;
   private ChoiceField _currencyKeyField;
   private LabelField _trackballLabel;
   private NumericChoiceField _trackballSensitivityXField;
   private NumericChoiceField _trackballSensitivityYField;
   private ChoiceField _trackballAudibilityField;
   private ChoiceField _trackballClickActionField;
   private ChoiceField _trackwheelClickActionField;
   private ChoiceField _scrollViewField;
   private LabelField _menuLabel;
   private ChoiceField _menuStyleField;
   private ObjectChoiceField _gammaTypeField;
   private ObjectChoiceField _gammaExponentField;
   private NumericChoiceField _gammaStartField;
   private NumericChoiceField _gammaEndField;
   private ScreenKeyboardOptionsItem$ViewCopyrightVerb _viewCopyrightVerb;
   private int _initialContrast;
   private int _initialBacklightBrightness;
   private int _filterStyle;
   private int _filterAntialiasing;
   private int _filterSize;
   private FontFamily[] _fontFamilies;
   private int[] _cptFontSizes;
   private boolean _isScalable;
   private IdleScreenOptionsProvider _idleScreenOptions;
   private ConvenienceKeyOptionsProvider _convenienceKeys;
   private int[] _clickActions = new int[]{0, 1, -804651003, 0, 1, 64, 3, 2};
   private int[] _menuStyles = new int[]{1, 2, -804651005, 1, 4, 3, -804650999, 6};
   private boolean _isJapanese;
   private boolean _isFontSizeChanged = false;
   private static final boolean SUB_PIXEL_RENDERING_DISABLED;
   private static final byte STYLE_UPDATED;
   private static final byte SIZE_UPDATED;
   private static final byte FAMILY_UPDATED;
   private static final byte ANTIALIAS_UPDATED;
   private static final int[] FONT_STYLES = new int[]{0, 1, 64, 3, 2, -804651006, 1, 2, -804651005, 1, 4, 3, -804650999, 6, 7, 8, 9, 10, 11, 12};
   private static final int[] FONT_SIZES = Display.getVerticalResolution() > 5000
      ? new int[]{
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         0,
         -804519929,
         10000,
         0,
         20000,
         0,
         30000,
         0,
         45000,
         0,
         60000,
         0,
         90000,
         0,
         120000,
         0,
         51,
         -804519925,
         60000,
         0,
         120000,
         0,
         300000,
         0
      }
      : new int[]{
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         -804651000,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         0,
         -804519929,
         10000,
         0,
         20000,
         0,
         30000,
         0,
         45000,
         0,
         60000,
         0,
         90000,
         0,
         120000,
         0,
         51,
         -804519925
      };
   private static final long[] BACKLIGHT_TIMEOUT_VALUES = new long[]{
      10000L,
      20000L,
      30000L,
      45000L,
      60000L,
      90000L,
      120000L,
      -3455386766855372749L,
      60000L,
      120000L,
      300000L,
      600000L,
      900000L,
      1200000L,
      1800000L,
      3600000L,
      7200000L,
      14400000L,
      28800000L,
      2267000562651431168L,
      7308057372952396430L,
      7152109195346968832L,
      7308057372952370292L,
      7741536031140942156L,
      2050372863122755429L,
      -4200449974624517888L,
      5006258338766454984L,
      7567173426031518062L,
      4750206242868227103L,
      432373126602041205L,
      7278498720816366188L,
      -9027055691608570815L,
      5290360362767183955L,
      9113357136763027533L,
      1956822836369033216L,
      5566290981688444705L,
      5722985219443484674L,
      32695112136092258L,
      1171814885545486344L,
      99480991430394734L,
      3210402826027123L,
      2809775727514692360L,
      7885591021088698412L,
      7543930060546399505L,
      9970793982084468L,
      -5669180194766438136L,
      532340683585979502L,
      3339554495877161L,
      3192601033048277256L,
      7330017838163820645L,
      7152006481272710482L,
      5361645297973620L,
      7517745525184217352L,
      825403658278561083L,
      7089031285155070574L,
      7929559577212223627L
   };
   private static final int[] ANTIALIAS_VALUES = new int[]{1, 4, 3, -804650999, 6, 7, 8, 9, 10, 11, 12, 13};
   private static ScreenKeyboardOptionsItem$KeyRepeatRate[] _keyRepeatDelays;
   private static ScreenKeyboardOptionsItem$KeyRepeatRate[] _keyRepeatRates;
   private static String _currencyChoices;
   private static final int JA_SMALL_FONT_SIZE;
   private static final int JA_MIN_FONT_SIZE;
   private static final int JA_MAX_FONT_SIZE;
   private static int _gammaTypeIndex = 1;
   private static int _gammaExponentIndex = 3;
   private static int _gammaStartIndex = 9;
   private static int _gammaEndIndex = 23;

   public ScreenKeyboardOptionsItem() {
      super(OptionsResources.getString(1100));
      ContextObject.put(super._context, 244, "display");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      Font font = Font.getDefault();
      int indent = font.getHeight();
      this._idleScreenOptions = IdleScreenOptionsProvider.getInstance();
      if (this._idleScreenOptions != null) {
         this._idleScreenOptions.populateMainScreen(mainScreen);
         mainScreen.add((Field)(new Object()));
      }

      this._isJapanese = (Locale.getDefaultForSystem().getCode() & -65536) == 1784741888
         || (Locale.getDefaultInputForSystem().getCode() & -65536) == 1784741888;
      this._filterAntialiasing = font.getAntialiasMode();
      if (this._filterAntialiasing == 3) {
         this._filterAntialiasing = 4;
      }

      this._filterSize = font.getHeight(4194307);
      this._fontFamilyField = (ChoiceField)(new Object(OptionsResources.getString(1102), this._fontFamilies, font.getFontFamily()));
      this._fontSizeField = (ObjectChoiceField)(new Object(OptionsResources.getString(1103), null, 0, 134217728));
      this._isScalable = false;
      this.populateFontSizes();
      this._fontStyleField = (ChoiceField)(new Object(OptionsResources.getString(1424), OptionsResources.getStringArray(1425)));
      int index = 0;
      switch (font.getStyle() & 0xFF) {
         case 1:
            index = 1;
            break;
         case 2:
            index = 4;
            break;
         case 3:
            index = 3;
            break;
         case 64:
            index = 2;
      }

      this._fontStyleField.setSelectedIndex(index);
      this._filterStyle = FONT_STYLES[index];
      Object[] tAA = OptionsResources.getStringArray(1459);
      int currAntialias = font.getAntialiasMode();
      Object[] tmpA = new Object[ANTIALIAS_VALUES.length - 1];
      System.arraycopy(tAA, 0, tmpA, 0, tmpA.length);
      tAA = tmpA;
      switch (currAntialias) {
         case 0:
         case 2:
            break;
         case 1:
         default:
            currAntialias = 0;
            break;
         case 3:
            currAntialias = 1;
            break;
         case 4:
            currAntialias = 1;
      }

      this._fontAntialiasField = (ChoiceField)(new Object(OptionsResources.getString(1458), tAA, 0, 134217728));
      this._fontAntialiasField.setSelectedIndex(currAntialias);
      this._fontSampleField = (RichTextField)(new Object(36028797018963968L));
      this._fontSampleField.setText(OptionsResources.getString(1465));
      this._viewCopyrightVerb = new ScreenKeyboardOptionsItem$ViewCopyrightVerb(this._fontSampleField.getFont());
      mainScreen.add(this._fontFamilyField);
      mainScreen.add(this._fontSizeField);
      tmpA = (Object[])(new Object());
      tmpA.add(this._fontStyleField);
      tmpA.add(this._fontAntialiasField);
      mainScreen.add(tmpA);
      this._fontSettings = tmpA;
      mainScreen.add(this._fontSampleField);
      this.updateChoiceOptions();
      this._fontFamilyField.setChangeListener(this);
      this._fontSizeField.setChangeListener(this);
      this._fontStyleField.setChangeListener(this);
      this._fontAntialiasField.setChangeListener(this);
      mainScreen.add((Field)(new Object()));
      if (Display.isContrastConfigurable()) {
         this._initialContrast = UiSettings.getDisplayContrast();
         this._contrastField = (NumericChoiceField)(new Object(OptionsResources.getString(1101), 0, 100, Display.getContrastIncrement()));
         this._contrastField.setSelectedValue(this._initialContrast);
         mainScreen.add(this._contrastField);
         this._contrastField.setChangeListener(this);
      }

      if (Backlight.isBrightnessConfigurable()) {
         this._initialBacklightBrightness = UiSettings.getBacklightBrightness();
         int increment = Backlight.getBrightnessIncrement();
         this._backlightBrightnessField = (NumericChoiceField)(new Object(OptionsResources.getString(1467), increment, 100, increment));
         this._backlightBrightnessField.setSelectedValue(this._initialBacklightBrightness);
         mainScreen.add(this._backlightBrightnessField);
         this._backlightBrightnessField.setChangeListener(this);
      }

      this._backlightTimeoutField = (TimeChoiceField)(new Object(
         OptionsResources.getString(1846), BACKLIGHT_TIMEOUT_VALUES, UiSettings.getBacklightTimeout() * 1000, false
      ));
      mainScreen.add(this._backlightTimeoutField);
      if (InternalServices.isDeviceCapable(16) && this.allowToggleAutomaticBacklight()) {
         this._automaticBacklightField = (BooleanChoiceField)(new Object(OptionsResources.getString(1967), 1, UiSettings.getAutomaticBacklightEnabled()));
         mainScreen.add(this._automaticBacklightField);
      }

      if (LED.isPolychromatic()) {
         this._ledIndicatorField = (BooleanChoiceField)(new Object(OptionsResources.getString(1911), 1, UiSettings.getLEDCoverageIndicatorStatus()));
         mainScreen.add(this._ledIndicatorField);
      }

      mainScreen.add((Field)(new Object()));
      if (Keypad.isKeyToneSupported()) {
         this._keyToneField = (BooleanChoiceField)(new Object(OptionsResources.getString(1104), 1, UiSettings.getKeypadToneEnabled()));
         mainScreen.add(this._keyToneField);
      }

      if (Ui.getMode() == 2) {
         this._keyDelayField = (ChoiceField)(new Object(OptionsResources.getString(1449), _keyRepeatDelays, 0, 134217728));
         int keyRepeatDelay = UiSettings.getKeypadRepeatDelay();

         for (int lv = 0; lv < _keyRepeatRates.length; lv++) {
            if (_keyRepeatDelays[lv].getRepeatRate() == keyRepeatDelay) {
               this._keyDelayField.setSelectedIndex(lv);
               break;
            }
         }

         mainScreen.add(this._keyDelayField);
      }

      this._keyRateField = (ChoiceField)(new Object(OptionsResources.getString(1105), _keyRepeatRates, 0, 134217728));
      int keyRepeatRate = UiSettings.getKeypadRepeatRate();

      for (int lv = 0; lv < _keyRepeatRates.length; lv++) {
         if (_keyRepeatRates[lv].getRepeatRate() == keyRepeatRate) {
            this._keyRateField.setSelectedIndex(lv);
            break;
         }
      }

      mainScreen.add(this._keyRateField);
      if (Keypad.hasCurrencyKey()) {
         char ch = UiSettings.getCurrencyKey();
         ResourceBundle bundle = ResourceBundle.getBundle(-4248492586227566823L, "net.rim.device.internal.resource.Keypad");
         _currencyChoices = bundle.getString(104);
         if (ch == 0) {
            _currencyChoices = ((StringBuffer)(new Object())).append('\u0000').append(_currencyChoices).toString();
         }

         int choiceCount = _currencyChoices.length();
         String[] variants = new Object[choiceCount];

         for (int i = 0; i < choiceCount; i++) {
            if (i == 0 && _currencyChoices.charAt(i) == 0) {
               variants[i] = OptionsResources.getString(1423);
            } else {
               variants[i] = _currencyChoices.substring(i, i + 1);
            }
         }

         index = _currencyChoices.indexOf(ch);
         this._currencyKeyField = (ChoiceField)(new Object(OptionsResources.getString(1961), variants));
         this._currencyKeyField.setSelectedIndex(index);
         mainScreen.add(this._currencyKeyField);
      }

      this._convenienceKeys = ConvenienceKeyOptionsProvider.getInstance();
      if (this._convenienceKeys != null) {
         this._convenienceKeys.populateMainScreen(mainScreen);
      }

      if (Trackball.isSupported()) {
         mainScreen.add((Field)(new Object()));
         this._trackballLabel = (LabelField)(new Object(OptionsResources.getString(2003)));
         mainScreen.add(this._trackballLabel);
         int increment = Trackball.getSensitivityIncrement();
         this._trackballSensitivityXField = (NumericChoiceField)(new Object(OptionsResources.getString(2021), 20, 100, increment));
         this._trackballSensitivityXField.setSelectedValue(Trackball.getSensitivityXForSystem());
         this._trackballSensitivityXField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._trackballSensitivityXField);
         this._trackballSensitivityYField = (NumericChoiceField)(new Object(OptionsResources.getString(2022), 20, 100, increment));
         this._trackballSensitivityYField.setSelectedValue(Trackball.getSensitivityYForSystem());
         this._trackballSensitivityYField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._trackballSensitivityYField);
         String[] audibilityStyles = OptionsResources.getStringArray(2024);
         this._trackballAudibilityField = (ChoiceField)(new Object(OptionsResources.getString(2023), audibilityStyles, 0, 134217728));
         this._trackballAudibilityField.setSelectedIndex(Trackball.isFeedbackAudibleForSystem() ? 1 : 0);
         this._trackballAudibilityField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._trackballAudibilityField);
      }

      mainScreen.add((Field)(new Object()));
      this._menuLabel = (LabelField)(new Object(OptionsResources.getString(2079)));
      mainScreen.add(this._menuLabel);
      String[] menuStyles = OptionsResources.getStringArray(2080);
      int var15 = 1;
      int style = UiOptionsRegistry.getInstance().getInt(8794318953449332169L);
      switch (style) {
         case 1:
            var15 = 0;
      }

      this._menuStyleField = (ChoiceField)(new Object(OptionsResources.getString(2081), menuStyles, var15, 134217728));
      this._menuStyleField.setPadding(0, 0, 0, indent);
      mainScreen.add(this._menuStyleField);
      if (Ui.getTrackballClickAction() == 0 || Ui.getMode() == 2) {
         String[] clickActions = new String[]{"Menu", "Action"};
         this._trackballClickActionField = (ChoiceField)(new Object("  Click Style (EXPERIMENTAL)", clickActions));
         this._trackballClickActionField.setSelectedIndex(Ui.getTrackballClickAction());
         this._trackballClickActionField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._trackballClickActionField);
      }

      if (Ui.getTrackwheelClickAction() == 1 || Ui.getMode() == 2) {
         String[] clickActions = new String[]{"Menu", "Action"};
         this._trackwheelClickActionField = (ChoiceField)(new Object("  TW Click Style (EXPERIMENTAL)", clickActions));
         this._trackwheelClickActionField.setSelectedIndex(Ui.getTrackwheelClickAction());
         this._trackwheelClickActionField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._trackwheelClickActionField);
      }

      if (Ui.getMode() == 2) {
         String[] scrollStyles = new String[]{"Select", "View"};
         this._scrollViewField = (ChoiceField)(new Object("  Scroll Style (EXPERIMENTAL)", scrollStyles));
         this._scrollViewField.setSelectedIndex(UiOptionsRegistry.getInstance().getBoolean(9099188860628284837L) ? 1 : 0);
         this._scrollViewField.setPadding(0, 0, 0, indent);
         mainScreen.add(this._scrollViewField);
      }
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (this._fontFamilies == null) {
         this._fontFamilies = FontFamily.getFontFamilies();
      }

      if (_keyRepeatDelays == null) {
         _keyRepeatDelays = new ScreenKeyboardOptionsItem$KeyRepeatRate[]{
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1106), 600),
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1946), 450),
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1107), 300)
         };
      }

      if (_keyRepeatRates == null) {
         _keyRepeatRates = new ScreenKeyboardOptionsItem$KeyRepeatRate[]{
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1106), 400),
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1946), 200),
            new ScreenKeyboardOptionsItem$KeyRepeatRate(OptionsResources.getString(1107), 100)
         };
      }
   }

   @Override
   protected final void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-5391367152569972535L);
      Verb[] factoryVerbs = verbRepository.getVerbs(null);
      if (factoryVerbs != null && factoryVerbs.length > 0) {
         verbToMenu.addVerbs(factoryVerbs);
      }
   }

   @Override
   protected final boolean save() {
      if (this._isJapanese) {
         int recommendedSize = -1;
         boolean wrongSizeChosen = false;
         boolean wrongAntialiasingChosen = true;
         boolean wrongStyleChosen = true;
         int size = 0;
         int index = this._fontSizeField.getSelectedIndex();
         if (index >= 0) {
            size = this._fontSizeField.getChoice(index);
         }

         if (size < 8) {
            recommendedSize = 8;
         } else if (8 < size && size < 10) {
            recommendedSize = 10;
         } else if (size > 13) {
            recommendedSize = 13;
         }

         if (recommendedSize <= 0) {
            recommendedSize = size;
         } else {
            index = 0;

            while (index < this._fontSizeField.getSize() && this._fontSizeField.getChoice(index) != recommendedSize) {
               index++;
            }

            if (index < this._fontSizeField.getSize()) {
               wrongSizeChosen = true;
            }
         }

         String dialogString;
         int style;
         int antialiasMode;
         if (recommendedSize > 8) {
            style = 1;
            antialiasMode = 1;
            dialogString = OptionsResources.getString(2043);
         } else {
            style = 0;
            antialiasMode = 0;
            dialogString = OptionsResources.getString(2042);
         }

         wrongAntialiasingChosen = this._fontAntialiasField.getSelectedIndex() != antialiasMode;
         wrongStyleChosen = this._fontStyleField.getSelectedIndex() != style;
         if (wrongSizeChosen) {
            dialogString = ((StringBuffer)(new Object()))
               .append(dialogString)
               .append(OptionsResources.getString(2067))
               .append(recommendedSize)
               .append(OptionsResources.getString(2068))
               .toString();
         } else {
            dialogString = ((StringBuffer)(new Object())).append(dialogString).append(OptionsResources.getString(2069)).toString();
         }

         if ((wrongSizeChosen || wrongAntialiasingChosen || wrongStyleChosen) && Dialog.ask(3, dialogString) == 4) {
            if (wrongSizeChosen) {
               FieldChangeListener oldListener = this._fontSizeField.getChangeListener();
               this._fontSizeField.setChangeListener(null);
               this._fontSizeField.setSelectedIndex(index);
               this._fontSizeField.setChangeListener(oldListener);
               this.updateFont((byte)1, this._fontSizeField);
            }

            if (wrongAntialiasingChosen && antialiasMode < this._fontAntialiasField.getSize()) {
               FieldChangeListener oldListener = this._fontAntialiasField.getChangeListener();
               this._fontAntialiasField.setChangeListener(null);
               this._fontAntialiasField.setSelectedIndex(antialiasMode);
               this._fontAntialiasField.setChangeListener(oldListener);
               this.updateFont((byte)3, this._fontAntialiasField);
            }

            if (wrongStyleChosen && style < this._fontStyleField.getSize()) {
               FieldChangeListener oldListener = this._fontStyleField.getChangeListener();
               this._fontStyleField.setChangeListener(null);
               this._fontStyleField.setSelectedIndex(style);
               this._fontStyleField.setChangeListener(oldListener);
               this.updateFont((byte)0, this._fontStyleField);
            }
         }
      }

      Font font = this._fontSampleField.getFont();
      if (font != null) {
         Font.setDefaultFontForSystem(font);
      }

      if (this._keyToneField != null) {
         UiSettings.setKeypadToneEnabled(this._keyToneField.isAffirmative());
      }

      if (this._ledIndicatorField != null && this._ledIndicatorField.isDirty()) {
         LEDEngine ledEngine = LEDEngine.getInstance();
         boolean status = this._ledIndicatorField.isAffirmative();
         ledEngine.updateGSMFlag(status);
         UiSettings.setLEDCoverageIndicatorStatus(status);
      }

      if (this._automaticBacklightField != null && this._automaticBacklightField.isDirty()) {
         boolean status = this._automaticBacklightField.isAffirmative();
         UiSettings.setAutomaticBacklightEnabled(status);
      }

      int rate = _keyRepeatRates[this._keyRateField.getSelectedIndex()].getRepeatRate();
      int delay = _keyRepeatDelays[this._keyDelayField != null ? this._keyDelayField.getSelectedIndex() : this._keyRateField.getSelectedIndex()]
         .getRepeatRate();
      UiSettings.setKeypadRepeatRate(rate);
      UiSettings.setKeypadRepeatDelay(delay);
      if (this._contrastField != null && this._contrastField.isDirty()) {
         UiSettings.setDisplayContrast(this._contrastField.getSelectedValue());
      }

      if (this._backlightBrightnessField != null && this._backlightBrightnessField.isDirty()) {
         UiSettings.setBacklightBrightness(this._backlightBrightnessField.getSelectedValue());
      }

      UiSettings.setBacklightTimeout((int)(BACKLIGHT_TIMEOUT_VALUES[this._backlightTimeoutField.getSelectedIndex()] / 1000));
      if (this._idleScreenOptions != null) {
         this._idleScreenOptions.save();
      }

      if (this._convenienceKeys != null) {
         this._convenienceKeys.save();
      }

      if (this._currencyKeyField != null && this._currencyKeyField.isDirty()) {
         char ch = _currencyChoices.charAt(this._currencyKeyField.getSelectedIndex());
         if (ch != 0) {
            UiSettings.setCurrencyKey(ch);
         }
      }

      if (this._trackballSensitivityXField != null && this._trackballSensitivityXField.isDirty()) {
         int sensitivity = this._trackballSensitivityXField.getSelectedValue();
         Trackball.setSensitivityXForSystem(sensitivity);
      }

      if (this._trackballSensitivityYField != null && this._trackballSensitivityYField.isDirty()) {
         int sensitivity = this._trackballSensitivityYField.getSelectedValue();
         Trackball.setSensitivityYForSystem(sensitivity);
      }

      if (this._trackballAudibilityField != null && this._trackballAudibilityField.isDirty()) {
         boolean audible = this._trackballAudibilityField.getSelectedIndex() != 0;
         Trackball.setFeedbackAudibleForSystem(audible);
      }

      if (this._trackballClickActionField != null && this._trackballClickActionField.isDirty()) {
         Ui.setTrackballClickAction(this._clickActions[this._trackballClickActionField.getSelectedIndex()]);
      }

      if (this._trackwheelClickActionField != null && this._trackwheelClickActionField.isDirty()) {
         Ui.setTrackwheelClickAction(this._clickActions[this._trackwheelClickActionField.getSelectedIndex()]);
      }

      if (this._scrollViewField != null && this._scrollViewField.isDirty()) {
         UiOptionsRegistry.getInstance().setBoolean(9099188860628284837L, this._scrollViewField.getSelectedIndex() != 0);
      }

      if (this._menuStyleField != null && this._menuStyleField.isDirty()) {
         UiOptionsRegistry.getInstance().setInt(8794318953449332169L, this._menuStyles[this._menuStyleField.getSelectedIndex()]);
      }

      if (this._gammaTypeField != null
         && (this._gammaTypeField.isDirty() || this._gammaExponentField.isDirty() || this._gammaStartField.isDirty() || this._gammaEndField.isDirty())) {
         _gammaTypeIndex = this._gammaTypeField.getSelectedIndex();
         _gammaExponentIndex = this._gammaExponentField.getSelectedIndex();
         _gammaStartIndex = this._gammaStartField.getSelectedIndex();
         _gammaEndIndex = this._gammaEndField.getSelectedIndex();
         int param1 = (_gammaExponentIndex + 5) * 65536 / 10;
         int param2 = _gammaStartIndex * 256 * 8;
         int param3 = _gammaEndIndex * 256 * 8;
         Graphics.setGamma(_gammaTypeIndex, param1, param2, param3, 0);
      }

      return super.save();
   }

   @Override
   protected final boolean discard() {
      if (this._contrastField != null) {
         Display.setContrast(this._initialContrast);
      }

      if (this._backlightBrightnessField != null) {
         Backlight.setBrightness(this._initialBacklightBrightness);
      }

      if (this._convenienceKeys != null) {
         this._convenienceKeys.discard();
      }

      return super.discard();
   }

   private final boolean isPointSizeValid(int size) {
      for (int index = 0; index < FONT_SIZES.length; index++) {
         if (FONT_SIZES[index] == size) {
            return true;
         }
      }

      return false;
   }

   private final void populateFontSizes() {
      FontFamily fontFamily = this._fontFamilies[this._fontFamilyField.getSelectedIndex()];
      if (!this._isScalable || (fontFamily.getTypefaceType() & FontFamily.SCALABLE_FONT) == 0) {
         this._isScalable = (fontFamily.getTypefaceType() & FontFamily.SCALABLE_FONT) != 0;
         int out = 0;
         int currentSize = Font.getDefault().getHeight();
         currentSize = Ui.convertSize(currentSize, 0, 3);
         Integer[] ptFontSizes;
         int numFontSizes;
         if (this._isScalable) {
            int[] fontSizes = FONT_SIZES;
            numFontSizes = fontSizes.length;
            ptFontSizes = new Object[numFontSizes];
            this._cptFontSizes = new int[numFontSizes];

            for (int i = 0; i < numFontSizes; i++) {
               int ptSize = fontSizes[i];
               if (!this._isJapanese || ptSize == currentSize || ptSize == 8 || 10 <= ptSize && ptSize <= 13) {
                  ptFontSizes[out] = (Integer)(new Object(ptSize));
                  this._cptFontSizes[out++] = Ui.convertSize(ptSize, 3, 4194307);
               }
            }
         } else {
            int[] fontSizes = fontFamily.getHeights();
            numFontSizes = fontSizes.length;
            ptFontSizes = new Object[numFontSizes];
            this._cptFontSizes = new int[numFontSizes];
            out = 0;

            for (int i = 0; i < numFontSizes; i++) {
               int ptSize = Ui.convertSize(fontSizes[i], 0, 3);
               int cptSize = Ui.convertSize(fontSizes[i], 0, 4194307);
               if (this.isPointSizeValid(ptSize) && (!this._isJapanese || ptSize == currentSize || ptSize == 8 || 10 <= ptSize && ptSize <= 13)) {
                  if (out > 0 && ptFontSizes[out - 1] == ptSize) {
                     if (Math.abs(ptSize * 100 - cptSize) >= Math.abs(ptSize * 100 - this._cptFontSizes[out - 1])) {
                        continue;
                     }

                     out--;
                  }

                  ptFontSizes[out] = (Integer)(new Object(ptSize));
                  this._cptFontSizes[out++] = cptSize;
               }
            }
         }

         if (out == 0) {
            ptFontSizes[out] = (Integer)(new Object(10));
            this._cptFontSizes[out++] = 1000;
         }

         if (out != numFontSizes) {
            Array.resize(ptFontSizes, out);
            Array.resize(this._cptFontSizes, out);
         }

         int currentIndex = this._fontSizeField.getSelectedIndex();
         int fontHeight_cpt;
         if (currentIndex != -1) {
            fontHeight_cpt = this._filterSize;
         } else {
            int fontHeightPx = Font.getDefault().getHeight(0);
            fontHeight_cpt = this._filterSize;
         }

         int fontHeightIndex_cpt = 0;

         for (int var17 = 0; var17 < this._cptFontSizes.length; var17++) {
            if (fontHeight_cpt >= this._cptFontSizes[var17]) {
               fontHeightIndex_cpt = var17;
            }
         }

         if (fontHeightIndex_cpt < this._cptFontSizes.length - 1
            && Ui.convertSize(fontHeight_cpt, 4194307, 3) > Ui.convertSize(this._cptFontSizes[fontHeightIndex_cpt], 4194307, 3)) {
            fontHeightIndex_cpt++;
         }

         FieldChangeListener oldListener = this._fontSizeField.getChangeListener();
         this._fontSizeField.setChangeListener(null);
         this._fontSizeField.setChoices(ptFontSizes);
         this._fontSizeField.setSelectedIndex(fontHeightIndex_cpt);
         this._fontSizeField.setChangeListener(oldListener);
      }
   }

   private final void updateChoiceOptions() {
      FontFamily fontFamily = this._fontFamilies[this._fontFamilyField.getSelectedIndex()];
      int type = fontFamily.getTypefaceType();
      boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
      ChoiceField fontAntialiasField = this._fontAntialiasField;
      VerticalFieldManager fontSettings = this._fontSettings;
      ChoiceField fontStyleField = this._fontStyleField;
      if (isBitmap) {
         if (fontStyleField.getManager() != null) {
            fontSettings.delete(fontStyleField);
            FieldChangeListener oldListener = fontStyleField.getChangeListener();
            fontStyleField.setChangeListener(null);
            fontStyleField.setSelectedIndex(0);
            fontStyleField.setChangeListener(oldListener);
         }

         if (fontAntialiasField.getManager() != null) {
            fontSettings.delete(fontAntialiasField);
            return;
         }
      } else {
         if (fontStyleField.getManager() == null) {
            fontSettings.add(fontStyleField);
            FieldChangeListener oldListener = fontStyleField.getChangeListener();
            fontStyleField.setChangeListener(null);
            int index = 0;

            while (index < FONT_STYLES.length && FONT_STYLES[index] != this._filterStyle) {
               index++;
            }

            fontStyleField.setSelectedIndex(index);
            fontStyleField.setChangeListener(oldListener);
         }

         int size = this._cptFontSizes[this._fontSizeField.getSelectedIndex()];
         if (this._isJapanese) {
            if (this._isFontSizeChanged) {
               size = Ui.convertSize(size, 4194307, 3);
               FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
               FieldChangeListener oldStyleListener = fontStyleField.getChangeListener();
               fontAntialiasField.setChangeListener(null);
               fontStyleField.setChangeListener(null);
               if (size == 8) {
                  fontAntialiasField.setSelectedIndex(0);
                  fontAntialiasField.setChangeListener(oldListener);
                  fontStyleField.setSelectedIndex(0);
                  fontStyleField.setChangeListener(oldStyleListener);
               } else {
                  if (fontAntialiasField.getManager() == null) {
                     fontSettings.add(fontAntialiasField);
                  }

                  if (fontStyleField.getManager() == null) {
                     fontSettings.add(fontStyleField);
                  }

                  fontAntialiasField.setSelectedIndex(1);
                  fontAntialiasField.setChangeListener(oldListener);
                  fontStyleField.setSelectedIndex(1);
                  fontStyleField.setChangeListener(oldStyleListener);
               }

               this.updateFont((byte)3, fontAntialiasField);
               this.updateFont((byte)0, fontStyleField);
               this._isFontSizeChanged = false;
            }
         } else if (Ui.getMode() != 2 && Ui.convertSize(size, 4194307, 0) < 14) {
            FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
            fontAntialiasField.setChangeListener(null);
            fontAntialiasField.setSelectedIndex(0);
            fontAntialiasField.setChangeListener(oldListener);
            if (fontAntialiasField.getManager() != null) {
               fontSettings.delete(fontAntialiasField);
               return;
            }
         } else if (fontAntialiasField.getManager() == null) {
            fontSettings.add(fontAntialiasField);
            FieldChangeListener oldListener = fontAntialiasField.getChangeListener();
            fontAntialiasField.setChangeListener(null);
            int currAntialias = 0;
            switch (this._filterAntialiasing) {
               case 0:
               case 2:
                  break;
               case 1:
               default:
                  currAntialias = 0;
                  break;
               case 3:
                  currAntialias = 1;
                  break;
               case 4:
                  currAntialias = 1;
            }

            fontAntialiasField.setSelectedIndex(currAntialias);
            fontAntialiasField.setChangeListener(oldListener);
            return;
         }
      }
   }

   private final void updateFont(byte reason, Field field) {
      Font current = this._fontSampleField.getFont();
      Font newFont = null;
      ChoiceField choiceField = (ChoiceField)(!(field instanceof Object) ? null : field);
      if (choiceField != null) {
         switch (reason) {
            case -1:
               break;
            case 0:
            default:
               this._filterStyle = FONT_STYLES[choiceField.getSelectedIndex()];
               newFont = current.derive(this._filterStyle);
               break;
            case 1:
               this._filterSize = this._cptFontSizes[choiceField.getSelectedIndex()];
               newFont = current.derive(
                  FONT_STYLES[this._fontStyleField.getSelectedIndex()],
                  this._filterSize,
                  4194307,
                  ANTIALIAS_VALUES[this._fontAntialiasField.getSelectedIndex()],
                  0
               );
               break;
            case 2:
               FontFamily family = this._fontFamilies[choiceField.getSelectedIndex()];
               int type = family.getTypefaceType();
               boolean isBitmap = type == FontFamily.MONO_BITMAP_FONT || type == FontFamily.UNKNOWN_FONT;
               newFont = family.getFont(
                  isBitmap ? 0 : FONT_STYLES[this._fontStyleField.getSelectedIndex()],
                  this._filterSize,
                  4194307,
                  isBitmap ? 1 : ANTIALIAS_VALUES[this._fontAntialiasField.getSelectedIndex()],
                  0
               );
               break;
            case 3:
               this._filterAntialiasing = ANTIALIAS_VALUES[choiceField.getSelectedIndex()];
               newFont = current.derive(current.getStyle(), current.getHeight(), 0, this._filterAntialiasing, 0);
         }
      }

      if (newFont != null) {
         this._fontSampleField.setFont(newFont);
      }
   }

   private final boolean allowToggleAutomaticBacklight() {
      return InternalServices.isToggleAutomaticBacklightSupported();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      Field original = field.getOriginal();
      if (original == this._contrastField) {
         Display.setContrast(((NumericChoiceField)field).getSelectedValue());
      } else if (original == this._fontFamilyField) {
         this.populateFontSizes();
         this.updateChoiceOptions();
         this.updateFont((byte)2, field);
      } else if (original == this._fontSizeField) {
         this._isFontSizeChanged = true;
         this.updateChoiceOptions();
         this.updateFont((byte)1, field);
      } else if (original == this._fontStyleField) {
         this.updateFont((byte)0, field);
      } else if (original == this._fontAntialiasField) {
         this.updateFont((byte)3, field);
      } else {
         if (original == this._backlightBrightnessField) {
            Backlight.setBrightness(((NumericChoiceField)field).getSelectedValue());
         }
      }
   }

   @Override
   public final boolean openProductionBackdoor(int aCode) {
      return false;
   }

   @Override
   public final boolean openDevelopmentBackdoor(int aCode) {
      switch (aCode) {
         case 1195461964:
            return false;
         case 1195461965:
         default:
            super._mainScreen.add((Field)(new Object()));
            String[] gammaTypeChoices = new String[]{"Linear", "Exponential", "Sigmoid"};
            this._gammaTypeField = (ObjectChoiceField)(new Object("Gamma function type", gammaTypeChoices));
            this._gammaTypeField.setSelectedIndex(_gammaTypeIndex);
            super._mainScreen.add(this._gammaTypeField);
            String[] gammaExponentChoices = new String[]{
               "0.5",
               "0.6",
               "0.7",
               "0.8",
               "0.9",
               "1.0",
               "1.1",
               "1.2",
               "1.3",
               "1.4",
               "1.5",
               "1.6",
               "1.7",
               "1.8",
               "1.9",
               "2.0",
               "2.1",
               "2.2",
               "2.3",
               "2.4",
               "2.5",
               "2.6",
               "2.7",
               "2.8",
               "2.9",
               "3.0"
            };
            this._gammaExponentField = (ObjectChoiceField)(new Object("Gamma exponent", gammaExponentChoices));
            this._gammaExponentField.setSelectedIndex(_gammaExponentIndex);
            super._mainScreen.add(this._gammaExponentField);
            this._gammaStartField = (NumericChoiceField)(new Object("Gamma start", 0, 256, 8, 0));
            this._gammaStartField.setSelectedIndex(_gammaStartIndex);
            super._mainScreen.add(this._gammaStartField);
            this._gammaEndField = (NumericChoiceField)(new Object("Gamma end", 0, 256, 8, 32));
            this._gammaEndField.setSelectedIndex(_gammaEndIndex);
            super._mainScreen.add(this._gammaEndField);
            return true;
      }
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      super.populateMenuVerbs(verbToMenu, instance);
      Font font = this._fontSampleField.getFont();
      if (font.getLicense().compareTo("") != 0) {
         this._viewCopyrightVerb.setFont(font);
         verbToMenu.addVerb(this._viewCopyrightVerb);
      }
   }
}
