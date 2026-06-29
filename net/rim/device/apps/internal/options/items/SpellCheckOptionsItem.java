package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.spellcheck.SpellCheckConstants;
import net.rim.tid.im.spellcheck.SpellCheckUtilities;

public final class SpellCheckOptionsItem extends SaveableMainScreenOptionsListItem implements SpellCheckConstants, FieldChangeListener {
   protected Verb _openCustomDictItem;
   private byte[] _spellCheckProperties;
   private CheckboxField _differentLetterCaseAsSame;
   private CheckboxField _upperCaseIngnore;
   private CheckboxField _wordsWithNumbersIgnore;
   private ButtonField _editCustomDictButton;
   private CheckboxField _spellCheckEmailBeforeSendField;
   private NumericChoiceField _minWordSizeForCheck;
   private SpellCheckOptionsItem$TransactedCustomDictionary _transactedCustomDict;
   private static SpellCheckOptionsItem$CustomDictScreen _customDictScreen;
   private static CustomDictionary _customDictionary;

   public SpellCheckOptionsItem() {
      super(getResource(1818));
   }

   public static final CustomDictionary getCustomDictionary() {
      if (_customDictionary == null) {
         InputContext ic = InputContext.getInstance();
         Locale locale = ic.getLocale();
         boolean isSpellChecking = SpellCheckUtilities.isSpellCheckVariant(locale);
         if (!isSpellChecking) {
            SpellCheckUtilities.activateSpellCheckIM();
         }

         _customDictionary = InputContext.getInstance().getCustomDictionary(1);
         if (!isSpellChecking) {
            SpellCheckUtilities.deactivateSpellCheckIM();
         }
      }

      return _customDictionary;
   }

   public static final void clearCustomDictionary() {
      if (_customDictionary != null) {
         InputContext ic = InputContext.getInstance();
         Locale locale = ic.getLocale();
         boolean isSpellChecking = SpellCheckUtilities.isSpellCheckVariant(locale);
         if (!isSpellChecking) {
            SpellCheckUtilities.activateSpellCheckIM();
         }

         SLControlObject co = (SLControlObject)ic.getInputMethodControlObject();
         co.actionPerformed(61, null);
         co.actionPerformed(62, null);
         if (!isSpellChecking) {
            SpellCheckUtilities.deactivateSpellCheckIM();
         }
      }
   }

   public static final boolean isSpellCheckingAvailable() {
      return SpellCheckUtilities.isSpellCheckAvailable();
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._spellCheckProperties = SpellCheckUtilities.getIMProperties();
      if (this._spellCheckProperties != null) {
         this._differentLetterCaseAsSame = (CheckboxField)(new Object(getResource(1822), this.getBooleanOption(0)));
         mainScreen.add(this._differentLetterCaseAsSame);
         this._upperCaseIngnore = (CheckboxField)(new Object(getResource(1823), this.getBooleanOption(1)));
         mainScreen.add(this._upperCaseIngnore);
         this._wordsWithNumbersIgnore = (CheckboxField)(new Object(getResource(1824), this.getBooleanOption(2)));
         mainScreen.add(this._wordsWithNumbersIgnore);
         this._spellCheckEmailBeforeSendField = (CheckboxField)(new Object(getResource(1819), this.getBooleanOption(7)));
         mainScreen.add(this._spellCheckEmailBeforeSendField);
         int initialValue = this.getByteOption(11);
         this._minWordSizeForCheck = (NumericChoiceField)(new Object(getResource(1454), 2, 10, 1));
         this._minWordSizeForCheck.setSelectedValue(initialValue);
         mainScreen.add(this._minWordSizeForCheck);
         this._editCustomDictButton = (ButtonField)(new Object(getResource(1491), 12885000192L));
         this._editCustomDictButton.setChangeListener(this);
         mainScreen.add(this._editCustomDictButton);
      }
   }

   @Override
   protected final boolean save() {
      if (this._spellCheckProperties != null) {
         this.setOption(0, this._differentLetterCaseAsSame.getChecked());
         this.setOption(1, this._upperCaseIngnore.getChecked());
         this.setOption(2, this._wordsWithNumbersIgnore.getChecked());
         this.setOption(7, this._spellCheckEmailBeforeSendField.getChecked());
         this.setOption(11, (byte)this._minWordSizeForCheck.getSelectedValue());
         SpellCheckUtilities.setIMProperties(this._spellCheckProperties);
      }

      return super.save();
   }

   private static final String getResource(int index) {
      return OptionsResources.getString(index);
   }

   private final void customDictionary() {
      if (_customDictScreen == null) {
         CustomDictionary custDict = getCustomDictionary();
         if (custDict == null) {
            return;
         }

         this._transactedCustomDict = new SpellCheckOptionsItem$TransactedCustomDictionary(custDict);
         SortedReadableList sortedList = (SortedReadableList)(new Object(this._transactedCustomDict, new SpellCheckOptionsItem$CustomDictComparator()));
         _customDictScreen = new SpellCheckOptionsItem$CustomDictScreen(
            getResource(1491), (KeywordFilterList)(new Object(sortedList, new SpellCheckOptionsItem$CustomDictIndexHelper())), this._transactedCustomDict
         );
      }

      _customDictScreen.showScreen();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      Field original = field.getOriginal();
      if (original == this._editCustomDictButton) {
         this.customDictionary();
      }
   }

   private final void setOption(int index, boolean propVal) {
      this._spellCheckProperties[index] = (byte)(propVal ? 1 : 0);
   }

   private final void setOption(int index, byte propVal) {
      this._spellCheckProperties[index] = propVal;
   }

   private final boolean getBooleanOption(int index) {
      return this._spellCheckProperties[index] != 0;
   }

   private final byte getByteOption(int index) {
      return this._spellCheckProperties[index];
   }

   static final SpellCheckOptionsItem$CustomDictScreen access$102(SpellCheckOptionsItem$CustomDictScreen x0) {
      _customDictScreen = x0;
      return x0;
   }
}
