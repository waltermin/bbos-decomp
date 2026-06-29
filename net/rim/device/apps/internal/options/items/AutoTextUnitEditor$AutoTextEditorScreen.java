package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;

final class AutoTextUnitEditor$AutoTextEditorScreen extends AppsMainScreen {
   private EditField _replacedStringField;
   private AutoTextUnitEditor$ReplacementStringPatternField _replacementStringPatternField;
   private AutoTextUnitEditor$CasingField _casingField;
   private ChoiceField _localeField;
   private DisplayableLocale[] _displayableLocales;
   private final AutoTextUnitEditor this$0;

   AutoTextUnitEditor$AutoTextEditorScreen(
      AutoTextUnitEditor _1, String title, String initialReplacedString, String initialReplacementStringPattern, int initialCasing, int initialLocaleCode
   ) {
      super(0);
      this.this$0 = _1;
      this.$initLocales();
      long style = 4505800798109696L;
      if (InputContext.getInstance().getActiveInputMethodID() != 4096) {
         style |= 1073741824;
      }

      this._replacedStringField = (EditField)(new Object(OptionsResources.getString(303), initialReplacedString, Integer.MAX_VALUE, style));
      this._replacedStringField.setCookie(this._replacedStringField);
      this._replacedStringField.setFilter(new AutoTextUnitEditor$ReplacedStringFilter());
      this._replacementStringPatternField = new AutoTextUnitEditor$ReplacementStringPatternField(initialReplacementStringPattern);
      this._casingField = new AutoTextUnitEditor$CasingField(initialCasing);
      this._localeField = (ChoiceField)(new Object(OptionsResources.getString(1801), this._displayableLocales, 0));
      Locale locale = Locale.get(initialLocaleCode);
      int numLocales = this._displayableLocales.length;

      for (int i = 0; i < numLocales; i++) {
         if (this._displayableLocales[i].getLocale() == locale) {
            this._localeField.setSelectedIndex(i);
            break;
         }
      }

      this.setTitle(title);
      this.add(this._replacedStringField);
      this.add(this._replacementStringPatternField);
      this.add(this._casingField);
      this.add(this._localeField);
      if (initialReplacedString != null && initialReplacedString.length() > 0) {
         this._replacementStringPatternField.setFocus();
      }
   }

   private final void $initLocales() {
      Locale[] locales = Locale.getAvailableInputLocales();
      int maxLocales = locales.length;
      int numLocales = 0;
      int currentLocaleCode = Locale.getDefaultInputForSystem().getCode();
      if ((currentLocaleCode & -65536) == 2053636096) {
         currentLocaleCode = 1701707776;
      }

      this._displayableLocales = new DisplayableLocale[maxLocales + 1];
      DisplayableLocale[] displayableLocales = this._displayableLocales;
      displayableLocales[numLocales++] = new DisplayableLocale(Locale.get(0), OptionsResources.getString(314));

      for (int i = 0; i < maxLocales; i++) {
         int localeCode = locales[i].getCode();
         if (localeCode != 0 && localeCode == currentLocaleCode || (currentLocaleCode & 65535) != 0 && localeCode == (currentLocaleCode & -65536)) {
            displayableLocales[numLocales++] = new DisplayableLocale(locales[i]);
         }
      }

      Array.resize(displayableLocales, numLocales);
   }

   private final Verb addCurrentFieldVerbs(SystemEnabledMenu menu) {
      Field field = UiApplication.getUiApplication().getActiveScreen().getFieldWithFocus();
      if (field == null) {
         return null;
      } else {
         Object cookie = field.getCookie();
         if (cookie instanceof Object) {
            Verb[] fieldVerbs = new Object[0];
            Verb defaultVerb = ((VerbProvider)cookie).getVerbs(this.this$0._context, fieldVerbs);
            menu.add(fieldVerbs);
            return defaultVerb;
         } else {
            return null;
         }
      }
   }

   @Override
   protected final ContextObject getContext() {
      return this.this$0._context;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-4364091456717037813L);
      Verb[] repositoryVerbs = verbRepository.getVerbs(null);
      if (repositoryVerbs != null && repositoryVerbs.length > 0) {
         menu.add(repositoryVerbs);
      }

      MenuItem saveItem = MenuItem.getPrefab(15);
      menu.add(saveItem);
      if (!this.this$0._context.getFlag(6)) {
         menu.add(new DeleteAutoTextUnitVerb(this.this$0._model));
      }

      Verb defaultVerb = this.addCurrentFieldVerbs(menu);
      if (this.this$0._mainScreen.isMuddy()) {
         menu.setDefault(saveItem);
      } else {
         if (defaultVerb != null) {
            menu.setDefault(defaultVerb);
         }
      }
   }

   @Override
   protected final boolean onSave() {
      String replacedString = this._replacedStringField.getText();
      String originalReplacedString = "";
      Locale locale = null;
      if (replacedString.equals(originalReplacedString)) {
         Dialog.inform(OptionsResources.getString(306));
         return false;
      }

      if (this.this$0._model != null) {
         originalReplacedString = AutoTextUnitEditor._autoTextEngine.getReplacedString(this.this$0._model.getEntry());
      }

      if (!originalReplacedString.equals(replacedString) && AutoTextUnitEditor._autoTextEngine.checkWord(replacedString) != null) {
         Dialog.inform(OptionsResources.getString(307));
         return false;
      }

      if (!this.this$0._context.getFlag(6) && this.this$0._context.getFlag(0)) {
         locale = Locale.get(AutoTextUnitEditor._autoTextEngine.getLocaleCode(this.this$0._model.getEntry()));
         AutoTextUnitEditor._autoTextEngine.remove(originalReplacedString, locale);
      }

      locale = this._displayableLocales[this._localeField.getSelectedIndex()].getLocale();
      this.this$0._editedAutoTextItem = AutoTextUnitEditor._autoTextEngine
         .add(replacedString, this._replacementStringPatternField.getText(), this._casingField.getSelectedIndex(), locale);
      this.this$0._mainScreen.setDirty(false);
      return true;
   }
}
