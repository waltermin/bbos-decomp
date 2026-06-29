package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.IMSwitcherOption;
import net.rim.device.internal.vad.VADLanguageSetting;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.util.Utils;

public final class LocalizationOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener {
   private ChoiceField _localeField;
   private ChoiceField _orderField;
   private ChoiceField _inputLocaleField;
   private ChoiceField _vadLocaleField;
   private ChoiceField _AltEnterShortcutField;
   private ChoiceField _AltEnterShortcutPromptField;
   private DisplayableLocale[] _displayableLocales;
   private DisplayableOrder[] _displayableOrders;
   private DisplayableLocale[] _inputLocales;
   private ActionProvider _inputMethodOptionsItem;
   private ButtonField _inputMethodOptionsButton;
   private Locale _currentInputLocale;
   private VADLanguageSetting _vadLanguageSetting;

   public LocalizationOptionsItem() {
      super(OptionsResources.getString(1803));
      ContextObject.put(super._context, 244, "language");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      InputContext.getInstance().setIMSwitchEnabled(false);
      this._localeField = (ChoiceField)(new Object(OptionsResources.getString(1801), this._displayableLocales, 0));
      this._inputLocaleField = (ChoiceField)(new Object(OptionsResources.getString(1476), this._inputLocales, 0));
      this._orderField = (ChoiceField)(new Object(OptionsResources.getString(1481), this._displayableOrders, 0));
      boolean addAltEnterShortcutPromtField = false;
      if (Utils.getAvailableInputLocales(true).length > 1) {
         String[] choices = CommonResources.getYesNoArray(0);
         int ind = 0;
         switch (IMSwitcherOption.getInstance().getState()) {
            case 0:
               break;
            case 1:
            default:
               ind = 0;
               break;
            case 2:
               ind = 1;
               break;
            case 3:
               ind = 2;
         }

         this._AltEnterShortcutField = (ChoiceField)(new Object(OptionsResources.getString(1987), choices, ind == 2 ? 1 : 0));
         this._AltEnterShortcutField.setChangeListener(this);
         addAltEnterShortcutPromtField = ind != 2;
         this._AltEnterShortcutPromptField = (ChoiceField)(new Object(OptionsResources.getString(1989), choices, addAltEnterShortcutPromtField ? ind : 0));
         this._AltEnterShortcutPromptField.setChangeListener(this);
      }

      Locale defaultLocale = Locale.getDefaultForSystem();
      int numLocales = this._displayableLocales.length;

      for (int i = 0; i < numLocales; i++) {
         Locale currentLocale = this._displayableLocales[i].getLocale();
         if (currentLocale.equals(defaultLocale)) {
            this._localeField.setSelectedIndex(i);
         }
      }

      Locale defaultInputLocale = Locale.getDefaultInputForSystem();
      int localeIndx = this.findLocale(defaultInputLocale, this._inputLocales, false);
      if (localeIndx == -1) {
         int defaultReplCode = Utils.getDefaultCountryForLanguage(defaultInputLocale);
         if (defaultReplCode != 0) {
            localeIndx = this.findLocale(Locale.get(defaultReplCode), this._inputLocales, true);
         }

         if (localeIndx == -1) {
            localeIndx = this.findLocale(defaultInputLocale, this._inputLocales, true);
         }
      }

      if (localeIndx != -1) {
         this._inputLocaleField.setSelectedIndex(localeIndx);
      }

      this._currentInputLocale = defaultInputLocale;
      int defaultOrder = Locale.getSystemNameOrder();
      int numOrders = this._displayableOrders.length;

      for (int i = 0; i < numOrders; i++) {
         int currentOrder = this._displayableOrders[i].getOrder();
         if (currentOrder == defaultOrder) {
            this._orderField.setSelectedIndex(i);
         }
      }

      mainScreen.add(this._localeField);
      mainScreen.add(this._inputLocaleField);
      this._vadLanguageSetting = VADLanguageSetting.getInstance();
      if (this._vadLanguageSetting != null) {
         this._vadLocaleField = (ChoiceField)(new Object(OptionsResources.getString(2027), this._vadLanguageSetting.getSupportedLanguages()));
         this._vadLocaleField.setSelectedIndex(this._vadLanguageSetting.getLanguageIndex());
         mainScreen.add(this._vadLocaleField);
      }

      mainScreen.add(this._orderField);
      if (this._AltEnterShortcutField != null) {
         mainScreen.add((Field)(new Object(8388608)));
         mainScreen.add(this._AltEnterShortcutField);
         if (addAltEnterShortcutPromtField) {
            mainScreen.add(this._AltEnterShortcutPromptField);
         }

         mainScreen.add((Field)(new Object(8388608)));
      }

      this._inputLocaleField.setChangeListener(this);
      this._localeField.setChangeListener(this);
      this._orderField.setChangeListener(this);
      this._inputMethodOptionsButton = null;
      this.addOrRemoveInputMethodOptionsButton(mainScreen);
   }

   private final int findLocale(Locale locale, DisplayableLocale[] set, boolean useLocaleCode) {
      int numLocales = set.length;
      int localeCode = locale.getCode();

      for (int i = 0; i < numLocales; i++) {
         Locale currentLocale = set[i].getLocale();
         if (useLocaleCode) {
            int code = currentLocale.getCode();
            if ((code & localeCode) == localeCode) {
               return i;
            }
         } else if (currentLocale.equals(locale)) {
            this._inputLocaleField.setSelectedIndex(i);
            return i;
         }
      }

      return -1;
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (this._displayableLocales == null) {
         Locale[] locales = Locale.getAvailableLocales();
         int numLocales = locales.length;
         int localeIndex = 0;
         this._displayableLocales = new DisplayableLocale[numLocales - 1];

         for (int i = 0; i < numLocales; i++) {
            if (locales[i].getCode() != 0) {
               this._displayableLocales[localeIndex++] = new DisplayableLocale(locales[i], Utils.getDisplayStringFor(locales[i]));
            }
         }
      }

      if (this._inputLocales == null) {
         Locale[] locales = Locale.getAvailableInputLocales();
         Utils.filterRootInputLocales(locales);
         Utils.filterUnsupportedMultitapInputLocales(locales);
         int numLocales = locales.length;
         int localeIndex = 0;
         this._inputLocales = new DisplayableLocale[numLocales];

         for (int i = 0; i < numLocales; i++) {
            this._inputLocales[localeIndex++] = new DisplayableLocale(locales[i], Utils.getDisplayStringFor(locales[i]));
         }
      }

      if (this._displayableOrders == null) {
         this._displayableOrders = new DisplayableOrder[2];
         this._displayableOrders[0] = new DisplayableOrder(0, OptionsResources.getString(1482));
         this._displayableOrders[1] = new DisplayableOrder(1, OptionsResources.getString(1483));
      }
   }

   @Override
   protected final boolean save() {
      Locale selectedLocale = this._displayableLocales[this._localeField.getSelectedIndex()].getLocale();
      Locale systemLocale = Locale.getDefaultForSystem();
      if (systemLocale != selectedLocale) {
         Locale.setDefaultForSystem(selectedLocale);
      }

      int selectedOrder = this._displayableOrders[this._orderField.getSelectedIndex()].getOrder();
      if (selectedOrder != Locale.getSystemNameOrder()) {
         AddressBookServices.getAddressBookOptions().setSortOrder(selectedOrder == 0 ? 1232448844688687736L : -227891759293611117L);
         Locale.setNameOrder(selectedOrder);
      }

      if (this._AltEnterShortcutField != null) {
         byte state = 1;
         switch (this._AltEnterShortcutField.getSelectedIndex()) {
            case -1:
               break;
            case 0:
            default:
               state = (byte)(this._AltEnterShortcutPromptField.getSelectedIndex() == 0 ? 1 : 2);
               break;
            case 1:
               state = 3;
         }

         if (IMSwitcherOption.getInstance().getState() != state) {
            IMSwitcherOption.getInstance().setState(state);
         }
      }

      selectedLocale = this._inputLocales[this._inputLocaleField.getSelectedIndex()].getLocale();
      systemLocale = Locale.getDefaultInputForSystem();
      if (systemLocale != selectedLocale) {
         Locale.setDefaultInputForSystem(selectedLocale);
      }

      if (this._inputMethodOptionsItem != null) {
         this._inputMethodOptionsItem.perform(-8570780006855731756L, null);
         this._inputMethodOptionsItem = null;
      }

      systemLocale = Locale.getDefaultInputForSystem();
      int slocale = selectedLocale.getCode() & -65536;
      int networkType = RadioInfo.getNetworkType();
      if (networkType == 3 || networkType == 7) {
         int originalSMSCoding = SMSOptions.getFallbackCoding();
         int messageCoding = Locale.isLatinOneCharacterSetLocale(selectedLocale) ? originalSMSCoding : 2;

         label102:
         try {
            if (messageCoding != -1) {
               SMSParameters smsp = (SMSParameters)(new Object());
               RadioInternal.getDefaultSMSParameters(smsp);
               if (originalSMSCoding == -1) {
                  originalSMSCoding = smsp.getMessageCoding();
               } else if (originalSMSCoding == messageCoding) {
                  originalSMSCoding = -1;
               }

               SMSOptions.setFallbackCoding(originalSMSCoding);
               smsp.setMessageCoding(messageCoding);
               RadioInternal.setDefaultSMSParameters(smsp);
            }
         } finally {
            break label102;
         }
      }

      if (this._vadLanguageSetting != null) {
         this._vadLanguageSetting.setLanguageIndex(this._vadLocaleField.getSelectedIndex());
      }

      return super.save();
   }

   @Override
   protected final boolean discard() {
      InputContext.getInstance().selectInputMethod(this._currentInputLocale);
      return super.discard();
   }

   private final boolean openInputMethodOptionsItem() {
      Locale selectedLocale = this._inputLocales[this._inputLocaleField.getSelectedIndex()].getLocale();
      InputContext.getInstance().selectInputMethod(selectedLocale);
      String name = null;
      long id = InputContext.getInstance().getActiveInputMethodID();
      if (id == 4096) {
         name = "net.rim.device.apps.internal.options.items.FastEuropean.FastEuropeanOptionsItem";
      } else if (id == 128 || id == 32) {
         name = "net.rim.device.apps.internal.options.items.chinese.ChineseOptionsItem";
      } else if (id == 16 || id == 8 || id == 256) {
         name = "net.rim.device.apps.internal.options.items.chinese.ChineseCangJeiOptionsItem";
      } else if (id == 1024) {
         name = "net.rim.device.apps.internal.options.items.korean.KoreanOptionsItem";
      } else if (id == 512) {
         name = "net.rim.device.apps.internal.options.items.japanese.JapaneseOptionsItem";
      } else if (id == 16384) {
         name = "net.rim.device.apps.internal.options.items.chinese.PinyinFROptionsItem";
      }

      if (name != null) {
         label58:
         try {
            this._inputMethodOptionsItem = null;
            this._inputMethodOptionsItem = (ActionProvider)Class.forName(name).newInstance();
         } finally {
            break label58;
         }
      }

      if (this._inputMethodOptionsItem == null) {
         return false;
      }

      this._inputMethodOptionsItem.perform(4951292880494466830L, selectedLocale);
      return this._inputMethodOptionsItem.perform(6099736323056465049L, null);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      if (this._inputMethodOptionsItem != null && !this._inputMethodOptionsItem.perform(3103370408204507200L, null)) {
         super._mainScreen.setDirty(true);
      }

      boolean res = super.confirm(verb, context);
      if (res) {
         InputContext.getInstance().setIMSwitchEnabled(true);
      }

      return res;
   }

   private final void addOrRemoveInputMethodOptionsButton(MainScreen mainScreen) {
      Locale selectedLocale = this._inputLocales[this._inputLocaleField.getSelectedIndex()].getLocale();
      InputContext ic = InputContext.getInstance();
      Locale imLocale = ic.getLocale();
      if (!selectedLocale.equals(imLocale)) {
         ic.selectInputMethod(selectedLocale);
      }

      if (this._inputMethodOptionsButton != null) {
         mainScreen.delete(this._inputMethodOptionsButton);
         this._inputMethodOptionsButton = null;
      }

      long id = ic.getActiveInputMethodID();
      if ((id & 4608) != 0) {
         this._inputMethodOptionsButton = (ButtonField)(new Object(OptionsResources.getString(1852), 65536));
         mainScreen.add(this._inputMethodOptionsButton);
         this._inputMethodOptionsButton.setChangeListener(this);
      } else {
         if (id == 128 || id == 256 || id == 32 || id == 16 || id == 8 || id == 1024 || id == 16384) {
            this._inputMethodOptionsButton = (ButtonField)(new Object(OptionsResources.getString(1808), 12884967424L));
            mainScreen.insert(this._inputMethodOptionsButton, this._inputLocaleField.getIndex() + 1);
            this._inputMethodOptionsButton.setChangeListener(this);
         }
      }
   }

   private final int getAppropriateChineseInputLocale(int dispLocaleCode) {
      int matchIndex = -1;
      int candIndex = -1;
      Locale matchLocale = dispLocaleCode == 2053653326 ? Locale.get("zh", "CN", "Pinyin") : Locale.get("zh", "HK", "Jyutping");

      for (int i = 0; i < this._inputLocales.length; i++) {
         Locale current = this._inputLocales[i].getLocale();
         if (matchLocale.equals(current)) {
            matchIndex = i;
            break;
         }

         if (candIndex == -1 && dispLocaleCode == current.getCode()) {
            candIndex = i;
         }
      }

      return matchIndex != -1 ? matchIndex : candIndex;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         if (field != this._localeField && field != this._inputLocaleField) {
            if (field == this._inputMethodOptionsButton) {
               this.openInputMethodOptionsItem();
               field.setDirty(false);
               return;
            }

            if (field == this._AltEnterShortcutField) {
               if (this._AltEnterShortcutField.getSelectedIndex() == 0) {
                  if (this._AltEnterShortcutPromptField.getManager() == null) {
                     super._mainScreen.insert(this._AltEnterShortcutPromptField, this._AltEnterShortcutField.getIndex() + 1);
                     return;
                  }
               } else if (this._AltEnterShortcutPromptField.getScreen() == super._mainScreen) {
                  super._mainScreen.delete(this._AltEnterShortcutPromptField);
               }
            }
         } else {
            if (field == this._localeField) {
               int index = -1;
               Locale locale = this._displayableLocales[this._localeField.getSelectedIndex()].getLocale();
               int dispLocaleCode = locale.getCode();
               if ((dispLocaleCode & -65536) == 2053636096) {
                  index = this.getAppropriateChineseInputLocale(dispLocaleCode);
               } else {
                  int candIndex = -1;

                  for (int i = 0; i < this._inputLocales.length; i++) {
                     int inputLocaleCode = this._inputLocales[i].getLocale().getCode();
                     if (dispLocaleCode == inputLocaleCode) {
                        index = i;
                        break;
                     }

                     if (candIndex == -1 && (dispLocaleCode & -65536) == (inputLocaleCode & -65536)) {
                        if (dispLocaleCode == 1701707776) {
                           if (inputLocaleCode == 1701729619) {
                              candIndex = i;
                           }
                        } else {
                           candIndex = i;
                        }
                     }
                  }

                  if (index == -1 && candIndex != -1) {
                     index = candIndex;
                  }
               }

               if (index != -1) {
                  this._inputLocaleField.setSelectedIndex(index);
               }

               if (this._vadLanguageSetting != null) {
                  this._vadLocaleField.setSelectedIndex(this._vadLanguageSetting.getLanguageIndexForLocale(locale));
               }
            }

            this.addOrRemoveInputMethodOptionsButton(super._mainScreen);
            int ord = Locale.getDefaultNameOrder(this._displayableLocales[this._localeField.getSelectedIndex()].getLocale().getCode());
            if (ord == 0) {
               this._orderField.setSelectedIndex(0);
               return;
            }

            if (ord == 1) {
               this._orderField.setSelectedIndex(1);
               return;
            }
         }
      }
   }
}
