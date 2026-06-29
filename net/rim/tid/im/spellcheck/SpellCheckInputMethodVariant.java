package net.rim.tid.im.spellcheck;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UIConstants;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodContext;
import net.rim.tid.awt.im.spi.InputModeChangeListener;
import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.conv.SLComposedText;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.SLVariants;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;
import net.rim.tid.im.conv.europe.spellcheck.MisspelledWordIterator;
import net.rim.tid.im.conv.europe.spellcheck.SpellCheckResultContainer;
import net.rim.tid.im.conv.europe.spellcheck.SpellCheckVariantsCreator;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.options.SpellCheck.SpellCheckOptions;
import net.rim.tid.im.ui.ChooseVariantDialog;
import net.rim.tid.itie.IComponent;
import net.rim.tid.itie.ISecureInputMethodBuffer;
import net.rim.tid.itie.LinguisticData;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.TextHitInfo;
import net.rim.tid.util.Utils;

public class SpellCheckInputMethodVariant
   implements InputMethod,
   ISecureInputMethodBuffer,
   SpellCheckConstants,
   HolsterListener,
   GlobalEventListener,
   UIConstants {
   private ResourceBundleFamily _bundleFamily;
   private SpellCheckOptions _options;
   private boolean _showDialogs = true;
   private byte[] _SCEProperties;
   private InputMethod _inputMethod;
   private InputMethodContext _inputMethodContext;
   private Locale _spellCheckLocale;
   private SLControlObject _controlObject;
   private IComponent _currentComponent;
   private FieldChangeListener _listener;
   private SpellCheckVariantsCreator _creator = new SpellCheckVariantsCreator();
   private MisspelledWordIterator _iterator = new MisspelledWordIterator(this._creator);
   private SpellCheckResultContainer _resultContainer;
   private SLComposedText _composedText;
   private SLVariants _variants;
   private boolean _lookupVisible;
   private XYRect _lookupBounds = (XYRect)(new Object());
   private TokenBounds _bounds = new TokenBounds();
   private StringBuffer _misspelledWord = (StringBuffer)(new Object());
   private StringBuffer _correctedWord = (StringBuffer)(new Object());
   private int _textLength = -1;
   private int _state = 1;
   private SpellCheckInputMethodVariant$DispatchEventHandler[] _sureTypeDispatchEventHandler;
   private SpellCheckInputMethodVariant$DispatchEventHandler[] _regularDispatchEventHandler;
   private int _lookupMode = 0;
   private TokenBounds _inputBounds = new TokenBounds();
   private SpellCheckInputMethodVariant$PushScreenRunnable _pushScreenRunnable;
   private SpellCheckInputMethodVariant$DelayedSpellCheck _delayedSpellCheckRunnable;
   private SpellCheckInputMethodVariant$DelayedRunSpellCheck _delayedRunSpellCheckRunnable;
   private SpellCheckInputMethodVariant$DelayedSpellCheckKeyedInput _delayedSpellCheckKeyedInputRunnable;
   private int _delayedSpellCheckKeyedInputRunnableID = -1;
   private SpellCheckInputMethodVariant$DelayedShowMenu _delayedLookupMisspelledRunnable = new SpellCheckInputMethodVariant$DelayedShowMenu(
      this, 1, 4294967296L, 34359738368L
   );
   private SpellCheckInputMethodVariant$DelayedShowMenu _delayedLookupDuplicateRunnable = new SpellCheckInputMethodVariant$DelayedShowMenu(
      this, 2, 4294967296L, 34359738368L
   );
   private SpellCheckInputMethodVariant$DelayedShowMenu _delayedEditModeMenuRunnable = new SpellCheckInputMethodVariant$DelayedShowMenu(
      this, 3, 4294967296L, 34359738368L
   );
   private ChooseVariantDialog _lookup = new ChooseVariantDialog(this);
   private Vector _variantsVector = (Vector)(new Object());
   private boolean _delaying;
   private Application _app;
   private boolean _cancelDelay;
   private boolean _userInteractionOccurred;
   private static final int STARTING_STATE;
   private static final int STOPPED_STATE;
   private static final int STOPPING_STATE;
   private static final int LOOKUP_STATE;
   private static final int SCANNING_STATE;
   private static final int UNCOMMITTED_KEY_INPUT_STATE;
   private static final int COMMITTED_KEY_INPUT_STATE;
   private static final int TOTAL_STATES;
   public static final int INVALID_MODE;
   public static final int MISSPELLED_MODE;
   public static final int DUPLICATE_MODE;
   public static final int EDIT_MODE;
   private static boolean AMBIGUOUS_WORDS_SUPPORTED = false;

   void updateOptionsOnEngine() {
      boolean case_sensitive = this._options.getFlag(1) == 0;
      this._creator.setCaseSensitive(case_sensitive);
      this._iterator.setCaseSensitive(case_sensitive);
      this._iterator.setIgnoreUpperCaseWords(this._options.getFlag(2) != 0);
      this._iterator.setIgnoreWordsWithDigits(this._options.getFlag(4) != 0);
      this._iterator.setMinimalCheckLength(this._options.getMinWordSizeForCheck());
   }

   public void showMenu() {
      this.showMenu(this._lookupMode);
   }

   public void join(InputMethod joinedInputMethod) {
      if (joinedInputMethod != null) {
         this._inputMethod = joinedInputMethod;
         this.useAddressBook();
      }
   }

   public void addCustomWord(String customWord) {
      char[] chs = customWord.toCharArray();
      SLCurrentVariant wordToAdd = (SLCurrentVariant)(new Object());
      wordToAdd._variants = chs;
      wordToAdd._offset = 0;
      wordToAdd._length = chs.length;
      this._creator.addWord(wordToAdd, false);
      LearningDataManager.commit();
   }

   public void addLearningPair(String original, String correction) {
      StringBuffer buf = (StringBuffer)(new Object());
      buf.append(original);
      char[] tmp = new char[correction.length()];
      correction.getChars(0, tmp.length, tmp, 0);
      SLCurrentVariant variant = (SLCurrentVariant)(new Object());
      variant._variants = tmp;
      variant._offset = 0;
      variant._length = correction.length();
      this.addLearningPair(buf, variant);
   }

   public void getSuggestedCorrections(String misspelled, Vector ret) {
      StringBufferGap misspelledBuf = (StringBufferGap)(new Object(misspelled));
      this._resultContainer.reset();
      this._creator.getVariants(misspelledBuf, 0, misspelledBuf.length(), this._resultContainer);
      StringBuffer buf = (StringBuffer)(new Object());

      for (int i = 0; i < this._resultContainer.getVariantsCount(); i++) {
         buf.setLength(0);
         this._resultContainer.getVariantAt(i, buf);
         ret.addElement(buf.toString());
      }
   }

   public int getWordCorrectness(String word) {
      AttributedString attrText = (AttributedString)(new Object(word));
      this._iterator.init(attrText, 0);
      this._bounds.start = 0;
      this._bounds.end = 0;
      this._iterator.findNextWord(this._bounds);
      return this._bounds.type;
   }

   @Override
   public void dispatchEvent(Event event) {
      this._cancelDelay = true;
      InputContext ic = InputContext.getInstance();
      int inputMode = ((SLControlObject)ic.getInputMethodControlObject()).getInputMode();
      if (ic.isSureType() && inputMode != 2) {
         this._sureTypeDispatchEventHandler[this._state].handleEvent(event);
      } else {
         this._regularDispatchEventHandler[this._state].handleEvent(event);
      }
   }

   @Override
   public void notifyClientWindowChange(XYRect bounds) {
      this._inputMethod.notifyClientWindowChange(bounds);
   }

   @Override
   public void activate() {
      this.useAddressBook();
      this._inputMethod.activate();
   }

   @Override
   public void deactivate(boolean isTemporary) {
      this._inputMethod.deactivate(isTemporary);
   }

   @Override
   public void hideWindows() {
      if (this._lookupVisible) {
         this._inputMethod.hideWindows();
         int selected = this._lookup.getSelectedIndex();
         this._variants.setVariantIndex(selected);
         this._lookup.setVisible(false);
         this._lookupVisible = false;
      }
   }

   @Override
   public void removeNotify() {
      this._inputMethod.removeNotify();
   }

   @Override
   public void endComposition() {
      switch (this._state) {
         case 1:
            this._inputMethod.endComposition();
      }
   }

   @Override
   public void reset(int type) {
      if (type == 1) {
         this.stopSpellCheck(1);
      }
   }

   @Override
   public void dispose() {
   }

   @Override
   public Object getControlObject() {
      if (this._controlObject == null) {
         this._controlObject = new SpellCheckInputMethodVariant$SpellCheckControlObject(this, (SLControlObject)this._inputMethod.getControlObject());
      }

      return this._controlObject;
   }

   @Override
   public int loadLinguisticData(LinguisticData data) {
      this._inputMethod.loadLinguisticData(data);
      if ((data.getType() & 1) == 0) {
         return 1;
      }

      int ret = this._creator.loadLinguisticData(data);
      if (ret == 1) {
         this._SCEProperties[10] = (byte)this._creator.getMinWordLength();
      }

      return ret;
   }

   @Override
   public int unloadLinguisticData(int id) {
      return this._inputMethod.unloadLinguisticData(id);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public int actionPerformed(Object src, int action, Object parameter) {
      try {
         return this.actionPerformed0(src, action, parameter);
      } catch (Throwable var7) {
         t.printStackTrace();
         Runnable invoker = new SpellCheckInputMethodVariant$1(this);
         Application.getApplication().invokeLater(invoker, 20, false);
         Utils.reportException(t);
         this.abortSpellCheck(24);
         return 0;
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L) {
         this.abortSpellCheck(32);
      } else {
         if (guid == 5961289116197897667L && Application.getApplication().isForeground()) {
            InputMethodContext inputMethodContext = this._inputMethodContext;
            if (this._inputMethodContext != null) {
               synchronized (inputMethodContext) {
                  this.stopSpellCheck(1);
                  return;
               }
            }
         }
      }
   }

   @Override
   public void inHolster() {
      this.abortSpellCheck(8);
   }

   @Override
   public void outOfHolster() {
   }

   @Override
   public boolean isCorrectWord(StringBufferGap sbg, int startIndex, int length) {
      return this._inputMethod.isCorrectWord(sbg, startIndex, length);
   }

   @Override
   public byte[] getIMProperties(byte propID) {
      switch (propID) {
         case 2:
            return this._SCEProperties;
         default:
            return this._inputMethod.getIMProperties(propID);
      }
   }

   @Override
   public void reconvert() {
      this._inputMethod.reconvert();
   }

   @Override
   public boolean isCompositionEnabled() {
      return this._inputMethod.isCompositionEnabled();
   }

   @Override
   public void setCompositionEnabled(boolean enable) {
      this._inputMethod.setCompositionEnabled(enable);
   }

   @Override
   public Locale getLocale() {
      return this._spellCheckLocale;
   }

   @Override
   public void setIMProperties(byte propID, byte[] inputMethodProperties) {
      switch (propID) {
         case 2:
            this._options.setFlag(128, inputMethodProperties[7]);
            this._options.setFlag(1, inputMethodProperties[0]);
            this._options.setFlag(2, inputMethodProperties[1]);
            this._options.setFlag(4, inputMethodProperties[2]);
            this._options.setMinWordSizeForCheck(inputMethodProperties[11]);
            this._options.commit();
            this.updateProperties();
            return;
         default:
            this._inputMethod.setIMProperties(propID, inputMethodProperties);
      }
   }

   @Override
   public int setListener(InputModeChangeListener listener) {
      return 3;
   }

   @Override
   public InputModeChangeListener getListener() {
      return null;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return this._creator.getRepository(type);
   }

   @Override
   public CustomDictionary getCustomDictionary(int type) {
      switch (type) {
         case 1:
            WordLearningReader newWords = this._creator.getMainRepository().getRepositoryData().getLearningReader();
            WordLearningReader freqWords = newWords;
            Vector checkRepositories = (Vector)(new Object());
            return (CustomDictionary)(new Object(newWords, freqWords, null, checkRepositories));
         default:
            return this._inputMethod.getCustomDictionary(type);
      }
   }

   @Override
   public int setTextInputStyle(int style) {
      return this._inputMethod.setTextInputStyle(style);
   }

   @Override
   public boolean setLocale(Locale locale) {
      return this.setLocale(locale, 0);
   }

   @Override
   public boolean runSecureClean() {
      this._misspelledWord.setLength(0);
      this._misspelledWord.setLength(this._misspelledWord.capacity());
      this._misspelledWord.setLength(0);
      this._correctedWord.setLength(0);
      this._correctedWord.setLength(this._misspelledWord.capacity());
      this._correctedWord.setLength(0);
      this._variantsVector.setSize(0);
      return false;
   }

   @Override
   public boolean setLocale(Locale locale, int state) {
      if (locale == null) {
         return false;
      }

      if (locale.equals(this._spellCheckLocale) && locale.getKeyboardID() == this._spellCheckLocale.getKeyboardID()) {
         try {
            this.reloadLearningData();
            return true;
         } finally {
            ;
         }
      } else {
         if (this._state != 1) {
            return false;
         }

         boolean canSet;
         if (!SpellCheckUtilities.isSpellCheckVariant(locale)) {
            canSet = false;
         } else {
            Locale noSpellCheckLocale = SpellCheckUtilities.removeSpellCheckingLocale(locale);
            canSet = noSpellCheckLocale != null;
         }

         if (canSet) {
            this._spellCheckLocale = locale;
            this._creator.setLocale(locale);
            this._resultContainer.setLocale(locale);
            canSet = this._iterator.setLocale(locale);

            try {
               this.reloadLearningData();
               return canSet;
            } finally {
               return canSet;
            }
         } else {
            return canSet;
         }
      }
   }

   @Override
   public void setInputMethodContext(InputMethodContext context) {
      this._inputMethodContext = context;
      Locale scLocale = this.getLocale();
      Locale replacedLocale = SpellCheckUtilities.removeSpellCheckingLocale(scLocale);
      InputMethod replacedIM = context.getInputMethod(replacedLocale);
      this.join(replacedIM);
      this._state = 0;
   }

   private int actionPerformed0(Object src, int action, Object parameter) {
      int rc = 0;
      switch (action) {
         case 41:
            this._userInteractionOccurred = false;
            this._inputMethod.endComposition();
            this.initState();
            if (parameter instanceof Object) {
               this._listener = (FieldChangeListener)parameter;
            }

            this._currentComponent = InputContext.getInstance().getInputComponent();
            if (this._currentComponent instanceof Object && ((TextField)this._currentComponent).isSelecting()) {
               ((TextField)this._currentComponent).select(false);
            }

            this._app = Application.getApplication();
            this._app.addHolsterListener(this);
            this._app.addGlobalEventListener(this);
            this.runSpellCheck();
            break;
         case 42:
            this.hideWindows();
            if (!(parameter instanceof Object)) {
               this._userInteractionOccurred = true;
               this.stopSpellCheck(1);
            } else {
               int reason = parameter;
               this.stopSpellCheck(reason);
            }
            break;
         case 43:
            this._userInteractionOccurred = true;
            this.hideWindows();
            this.changeTokenToWord(false);
            this.runSpellCheck();
            break;
         case 44:
            this._userInteractionOccurred = true;
            this.hideWindows();
            this.ignoreToken(false);
            this.runSpellCheck();
            break;
         case 45:
            this._userInteractionOccurred = true;
            this.hideWindows();
            this.changeTokenToWord(true);
            this.runSpellCheck();
            break;
         case 46:
            this._userInteractionOccurred = true;
            this.hideWindows();
            this.ignoreToken(true);
            this.runSpellCheck();
            break;
         case 47:
            this._userInteractionOccurred = true;
            this.hideWindows();
            this.addToken();
            this.runSpellCheck();
            break;
         case 60:
            if (this._state != 1 && this._state != 0) {
               this._currentComponent = InputContext.getInstance().getInputComponent();
               if (this._currentComponent instanceof Object && ((TextField)this._currentComponent).isSelecting()) {
                  ((TextField)this._currentComponent).select(false);
               }

               this.continueSpellCheck();
            } else {
               this._inputMethod.endComposition();
               if (parameter instanceof Object) {
                  this._listener = (FieldChangeListener)parameter;
               }

               if (!InputContext.getInstance().getInputComponent().equals(this._currentComponent)) {
                  this._currentComponent = InputContext.getInstance().getInputComponent();
                  this._bounds.start = 0;
                  this._bounds.end = 0;
                  this._bounds.previousStart = 0;
                  this._bounds.previousEnd = 0;
               } else {
                  this._bounds.end = this._bounds.start;
               }

               this._app = Application.getApplication();
               this._app.addHolsterListener(this);
               this._app.addGlobalEventListener(this);
               this.runSpellCheck();
            }
            break;
         case 61:
            this._creator.clearCustomDictionary();
            break;
         case 62:
            this._creator.clearLearningPairs();
            break;
         case 109:
            try {
               this.reloadLearningData();
               break;
            } finally {
               ;
            }
         case 512:
            this._showDialogs = true;
            break;
         case 768:
            this._showDialogs = false;
            break;
         case 1024:
            this._showDialogs = true;
            break;
         default:
            rc = this._inputMethod.actionPerformed(src, action, parameter);
      }

      return rc;
   }

   private void continueSpellCheck() {
      switch (this._state) {
         case 5:
         case 6:
         default:
            this.scanFromCurrentPosition(false);
         case 4:
      }
   }

   private void showMenu(int mode) {
      switch (mode) {
         case 0:
            throw new Object(((StringBuffer)(new Object("Invalid state: "))).append(mode).toString());
         case 1:
            Application.getApplication().invokeLater(this._delayedLookupMisspelledRunnable, 20, false);
            return;
         case 2:
         default:
            Application.getApplication().invokeLater(this._delayedLookupDuplicateRunnable, 20, false);
            return;
         case 3:
            Application.getApplication().invokeLater(this._delayedEditModeMenuRunnable, 20, false);
      }
   }

   private void adjustCaretToBounds(TokenBounds bounds) {
      int caret = this._inputMethodContext.getCaretPosition();
      if (caret > bounds.end) {
         this._inputMethodContext.dispatchInputMethodEvent(1101, null, 0, 0, 0, (TextHitInfo)(new Object(bounds.end - bounds.start, true)), null);
      }
   }

   private boolean scanFromCurrentPosition(boolean ignoreWord) {
      if (this._state == 5) {
         this.sendCommittedComposed(true);
      } else {
         this.unshowInputBounds();
      }

      if (ignoreWord) {
         this._bounds.start = this._bounds.end;
      } else {
         this._bounds.end = this._bounds.start;
      }

      return this.runSpellCheck();
   }

   private void learnCorrection() {
      if (this._state != 5 && this._misspelledWord.length() > 0 && this._correctedWord.length() > 0) {
         char[] tmp = new char[this._correctedWord.length()];
         this._correctedWord.getChars(0, tmp.length, tmp, 0);
         SLCurrentVariant correction = (SLCurrentVariant)(new Object());
         correction._variants = tmp;
         correction._offset = 0;
         correction._length = tmp.length;
         this._iterator.changeAll(this._misspelledWord, this._correctedWord);
         this.addLearningPair(this._misspelledWord, correction);
      }
   }

   private void useAddressBook() {
      this._creator.useAddressBookRepository(true);
   }

   private void reloadLearningData() {
      this._creator.reloadLearningData();
   }

   private void initState() {
      this._bounds.start = 0;
      this._bounds.end = 0;
      this._bounds.previousStart = 0;
      this._bounds.previousEnd = 0;
      this._iterator.reset();
   }

   private void abortSpellCheck(int abortReason) {
      this.stopSpellCheck(2 | abortReason);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void stopSpellCheck(int stopReason) {
      switch (this._state) {
         case 0:
         case 4:
            break;
         case 1:
         case 2:
         default:
            return;
         case 3:
         case 5:
            this._variants.setVariantIndex(-1);
            this.sendCommittedComposed(false);
            break;
         case 6:
            this.unshowInputBounds();
      }

      if (this._app != null) {
         this._app.removeHolsterListener(this);
         this._app.removeGlobalEventListener(this);
         this._app = null;
      }

      boolean tmp = this._showDialogs;
      if (this._showDialogs) {
         this._showDialogs = this._state != 0;
      }

      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         this._state = 2;
         this.hideWindows();
         if (this._showDialogs) {
            if (stopReason != 0) {
               this.showMessage(this._bundleFamily.getString(19));
            } else {
               this.showMessage(this._bundleFamily.getString(20));
            }
         }

         this._inputMethodContext = null;
         InputContext.getInstance().selectInputMethod(this._inputMethod.getLocale());
         this._showDialogs = tmp;
         var5 = false;
      } finally {
         if (var5) {
            this._state = 1;
         }
      }

      this._state = 1;
      if (this._listener != null && this._currentComponent instanceof Object) {
         int evt = 42;
         if (this._userInteractionOccurred) {
            stopReason |= 4;
         }

         evt |= stopReason << 16;
         this._listener.fieldChanged((Field)this._currentComponent, evt);
         this._listener = null;
      }
   }

   private void spellCheckEnteredText() {
      if (this._state == 5 || this._state == 6) {
         if (!this.checkKeyedInput()) {
            this.sendComposed(true);
            this._state = 5;
         } else {
            this._state = 6;
            this.showInputBounds();
         }
      }
   }

   private boolean adjustBounds() {
      int caret = this._inputMethodContext.getCaretPosition();
      AttributedString attrText = this._inputMethodContext.getAttributedText();
      int len = attrText.length();
      int diff = this._textLength - len;
      int oldWordLen = this._bounds.end - this._bounds.start;
      if (caret < this._bounds.start) {
         int newStart = this.findWordStart(attrText, caret);
         this._bounds.start = newStart == -1 ? 0 : newStart;
         int newEnd = this.findWordEnd(attrText, this._bounds.start);
         this._bounds.end = newEnd == -1 ? this._bounds.start : newEnd;
         return false;
      }

      if (this._bounds.start >= len) {
         if (len == 0) {
            this._bounds.start = 0;
            this._bounds.end = 0;
            return false;
         } else {
            int newStart = this.findWordStart(attrText, len - 1);
            this._bounds.start = newStart == -1 ? 0 : newStart;
            int newEnd = this.findWordEnd(attrText, this._bounds.start);
            this._bounds.end = newEnd == -1 ? this._bounds.start : newEnd;
            return false;
         }
      } else {
         int newStart = this.findWordStart(attrText, this._bounds.start);
         if (newStart < this._bounds.start) {
            this._bounds.start = newStart == -1 ? 0 : newStart;
            this._bounds.end = this._bounds.start;
            return false;
         } else {
            int newEnd = this.findWordEnd(attrText, newStart);
            if (newEnd == -1) {
               this._bounds.start = newStart;
               this._bounds.end = newStart;
               return false;
            } else {
               int newLen = newEnd - newStart;
               if (oldWordLen == newLen + diff) {
                  this._bounds.start = newStart;
                  this._bounds.end = newEnd;
                  return true;
               } else {
                  this._bounds.start = newStart;
                  this._bounds.end = newEnd;
                  return false;
               }
            }
         }
      }
   }

   private boolean checkKeyedInput() {
      AttributedString attrText = this._inputMethodContext.getAttributedText();
      TokenBounds subBounds = new TokenBounds();
      if (!this.adjustBounds()) {
         this._misspelledWord.setLength(0);
      }

      this._textLength = attrText.length();
      subBounds.start = this._bounds.start;
      subBounds.end = this._bounds.start;
      this._iterator.init(attrText, 0);
      boolean rc;
      if (!this._iterator.findNextWord(subBounds)) {
         rc = true;
      } else if (subBounds.start > this._bounds.start) {
         if (subBounds.start > this._bounds.end) {
            rc = true;
         } else {
            this._bounds.start = subBounds.start;
            if (subBounds.end > this._bounds.end) {
               this._bounds.end = subBounds.end;
            }

            rc = false;
         }
      } else {
         if (subBounds.end > this._bounds.end) {
            this._bounds.end = subBounds.end;
         }

         rc = false;
      }

      if (rc) {
         String correction = attrText.getText(this._bounds.start, this._bounds.end);
         this._correctedWord.setLength(0);
         this._correctedWord.append(correction);
         int caret = this._inputMethodContext.getCaretPosition();
         if (caret > this._bounds.end) {
            caret = this._bounds.end;
         }

         this._inputMethodContext.dispatchInputMethodEvent(1102, null, 0, 0, 0, (TextHitInfo)(new Object(caret, true)), null);
         return rc;
      } else {
         StringBufferGap misspelled = (StringBufferGap)(new Object(attrText.getText(this._bounds.start, this._bounds.end)));
         this._inputMethodContext.setComposedText(this._bounds.start, this._bounds.end);
         this.adjustCaretToBounds(subBounds);
         int caret = this._inputMethodContext.getCaretPosition();
         this._variants.setOriginal(misspelled);
         this._resultContainer.reset();
         this._resultContainer.setResults(this._variants);
         this._variants.setCaretPosition(caret - subBounds.start);
         return rc;
      }
   }

   private void showInputBounds() {
      if (this._currentComponent instanceof Object) {
         this._inputBounds.start = this._bounds.start;
         this._inputBounds.end = this._bounds.end;
         TextField tf = (TextField)this._currentComponent;
         boolean isDirty = tf.isDirty();
         int caret = tf.getCaretPosition();
         long attrib = (long)Ui.convertColorTo16bit(16711680) << 32;
         long mask = 281470681743360L;
         if (caret >= this._bounds.start && caret <= this._bounds.end) {
            if (caret > this._bounds.start) {
               tf.setAttrib(this._bounds.start, caret, attrib, mask, 0, -1);
            }

            if (caret < tf.getTextLength() + tf.getLabelLength()) {
               tf.setAttrib(caret, caret + 1, 0, mask, 0, -1);
            }

            if (caret < this._bounds.end) {
               tf.setAttrib(caret + 1, this._bounds.end, attrib, mask, 0, -1);
            }
         } else {
            tf.setAttrib(this._bounds.start, this._bounds.end, attrib, mask, 0, -1);
         }

         if (!isDirty) {
            tf.setDirty(false);
         }
      }
   }

   private void unshowInputBounds() {
      if (this._currentComponent instanceof Object) {
         TextField tf = (TextField)this._currentComponent;
         AttributedString attrText = this._inputMethodContext.getAttributedText();
         int caret = tf.getCaretPosition();
         if (this._bounds.end < caret) {
            this._bounds.end = caret;
         }

         if (this._bounds.start > caret) {
            this._bounds.start = caret;
         }

         int textLength = attrText.length();
         if (this._bounds.end > textLength) {
            this._bounds.end = textLength;
            if (this._bounds.start > this._bounds.end) {
               this._bounds.start = this._bounds.end == 0 ? 0 : this._bounds.end - 1;
            }
         }

         boolean isDirty = tf.isDirty();
         long mask = 281470681743360L;
         tf.setAttrib(this._bounds.start, this._bounds.end, 0, mask, 0, -1);
         if (!isDirty) {
            tf.setDirty(false);
         }
      }
   }

   private int findWordStart(AttributedString attrStr, int inWordIndex) {
      if (inWordIndex <= 0) {
         return 0;
      }

      char ch = attrStr.charAt(inWordIndex);
      if (this.isClauseSeparator(ch)) {
         if (--inWordIndex <= 0) {
            return 0;
         }

         ch = attrStr.charAt(inWordIndex);
         if (this.isClauseSeparator(ch)) {
            return inWordIndex + 1;
         }
      }

      inWordIndex--;

      while (inWordIndex > 0) {
         ch = attrStr.charAt(inWordIndex);
         if (this.isClauseSeparator(ch)) {
            return inWordIndex + 1;
         }

         inWordIndex--;
      }

      return 0;
   }

   private int findWordEnd(AttributedString attrStr, int inWordIndex) {
      char ch = attrStr.charAt(inWordIndex);
      if (this.isClauseSeparator(ch)) {
         return inWordIndex;
      }

      inWordIndex++;

      while (inWordIndex < attrStr.length()) {
         ch = attrStr.charAt(inWordIndex);
         if (this.isClauseSeparator(ch)) {
            return inWordIndex;
         }

         inWordIndex++;
      }

      return attrStr.length();
   }

   private boolean isClauseSeparator(char ch) {
      return ch != '\''
         && (CharacterUtilities.isSymbol(ch) || ch == '\n' || ch == '.' || CharacterUtilities.isPunctuation(ch) || CharacterUtilities.isSpaceChar(ch));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean runSpellCheck() {
      boolean var8 = false /* VF: Semaphore variable */;

      boolean var12;
      label103: {
         try {
            var8 = true;
            this._state = 4;
            int labelLength = this._inputMethodContext.getLabelLength();
            if (this._bounds.start < labelLength) {
               this._bounds.start = labelLength;
               this._bounds.end = labelLength;
            }

            AttributedString attrText = this._inputMethodContext.getAttributedText();
            this._textLength = attrText.length();
            if (this._bounds.end > this._textLength) {
               this._bounds.end = this._textLength;
               if (this._bounds.start >= this._bounds.end) {
                  this._bounds.start = this._bounds.end == 0 ? 0 : this._bounds.end - 1;
               }
            }

            int committedLen = this._textLength - (this._inputMethodContext.getComposedTextEnd() - this._inputMethodContext.getComposedTextStart());
            attrText = (AttributedString)(new Object(attrText, 0, committedLen));
            this._iterator.init(attrText, 0);

            while (true) {
               if (!this._iterator.findNextWord(this._bounds)) {
                  this.stopSpellCheck(0);
                  var12 = true;
                  var8 = false;
                  break label103;
               }

               if (this._bounds.type == 2) {
                  this.lookupDuplicate();
                  var8 = false;
                  break;
               }

               attrText = this._inputMethodContext.getAttributedText();
               StringBufferGap misspelled = (StringBufferGap)(new Object(attrText.getText(this._bounds.start, this._bounds.end)));
               String changeTo = this._iterator.isInChangeAll(misspelled, 0, misspelled.length());
               if (changeTo != null) {
                  this.changeTokenToWord(misspelled.toString(), changeTo, false);
               } else if (this.lookupTokenVariants(misspelled, this._bounds.type)) {
                  var8 = false;
                  break;
               }
            }
         } finally {
            if (var8) {
               if (this._state == 4) {
                  this.abortSpellCheck(24);
               }
            }
         }

         if (this._state == 4) {
            this.abortSpellCheck(24);
            return false;
         }

         return false;
      }

      if (this._state == 4) {
         this.abortSpellCheck(24);
      }

      return var12;
   }

   private void lookupDuplicate() {
      this._inputMethodContext.setComposedText(this._bounds.previousStart, this._bounds.end);
      AttributedString attrStr = this._inputMethodContext.getAttributedText();
      StringBufferGap repeated = (StringBufferGap)(new Object(attrStr.getText(this._bounds.previousStart, this._bounds.end)));
      StringBufferGap notRepeated = (StringBufferGap)(new Object(attrStr.getText(this._bounds.start, this._bounds.end)));
      this._bounds.start = this._bounds.previousStart;
      this._variants.setOriginal(repeated);
      this._resultContainer.reset();
      ExtendedCurrentVariant insertedWord = this._resultContainer.getTempInsertedWordContainer();
      char[] tmp = new char[notRepeated.length()];
      notRepeated.getText(0, tmp.length, tmp, 0);
      insertedWord.setData(tmp, 0, tmp.length, 0);
      this._resultContainer.insertWord(insertedWord);
      this._resultContainer.setResults(this._variants);
      this._variants.setCaretPosition(repeated.length());
      this.sendComposed(false);
      this.showLookup(2);
   }

   private boolean lookupTokenVariants(StringBufferGap misspelled, int type) {
      this._inputMethodContext.setComposedText(this._bounds.start, this._bounds.end);
      this._variants.setOriginal(misspelled);
      this._resultContainer.reset();
      if (type == 3) {
         this._creator.getAmbiguousVariants(misspelled, 0, misspelled.length(), this._resultContainer);
      } else {
         this._creator.getVariants(misspelled, 0, misspelled.length(), this._resultContainer);
      }

      this._resultContainer.setResults(this._variants);
      if (this._variants.getVariantsCount() == 0) {
         return false;
      }

      this._variants.setCaretPosition(misspelled.length());
      this.sendComposed(false);
      this.showLookup(1);
      return true;
   }

   private void sendCommittedComposed(boolean isTextChanged) {
      if (!this._composedText.isEmpty()) {
         if (this._currentComponent == InputContext.getInstance().getInputComponent()) {
            this._composedText.commitAll();
            this.sendComposed(isTextChanged);
            if (this._composedText.getCommittedVariantsCount() != 0) {
               this._composedText.deleteCommittedVariants();
            }

            this._variants = this._composedText.getCurrentVariant(true);
         }
      }
   }

   private void sendComposed(boolean isTextChanged) {
      this._inputMethodContext.setComposedText(this._bounds.start, this._bounds.end);
      AttributedString str = this._composedText.getOutput();
      long attrMask = this._composedText.getAttributeMask();
      int committedCcount = this._composedText.getCommittedCharactersCountL();
      int convertedCount = this._composedText.getConvertedCharacterCount();
      int caretPosition = this._composedText.getCaretPosition();
      boolean isDirty = !(this._currentComponent instanceof Object) ? false : ((Field)this._currentComponent).isDirty();
      if (committedCcount > 0) {
         this.unshowInputBounds();
      }

      this._inputMethodContext
         .dispatchInputMethodEvent(
            1100, 0, str, attrMask, committedCcount, convertedCount, TextHitInfo.leading(caretPosition), (TextHitInfo)((Object)null), null
         );
      this.adjustCaretToBounds(this._bounds);
      if (committedCcount == 0) {
         this.showInputBounds();
      }

      if (this._currentComponent instanceof Object && !isTextChanged && !isDirty) {
         ((Field)this._currentComponent).setDirty(false);
      }
   }

   private void changeTokenToWord(boolean changeAll) {
      StringBuffer replacement = (StringBuffer)(new Object());
      this._variants.addCurrentVariantTo(replacement);
      if (changeAll) {
         this._iterator.changeAll(this._variants.getOriginal(), replacement);
      }

      if (this._resultContainer.getVariantsCount() > 0) {
         int varIndex = this._variants.getCurrentVariantIndex();
         if (varIndex >= 0) {
            ExtendedCurrentVariant currentVariant = this._resultContainer.getTempInsertedWordContainer();
            this._resultContainer.getVariantAt(varIndex, currentVariant);
            if (varIndex > 0 || currentVariant._source == 6) {
               this.addLearningPair(this._variants.getOriginal(), currentVariant);
            }
         }
      }

      this.sendCommittedComposed(true);
      this._bounds.end = this._bounds.start + replacement.length();
   }

   private void addLearningPair(StringBuffer original, SLCurrentVariant correction) {
      this._creator.addPair(original, correction);
      this._options.commit();
   }

   private void changeTokenToWord(String original, String word, boolean changeAll) {
      this._variants.setOriginal(word);
      this._variants.setCaretPosition(word.length());
      this.changeTokenToWord(changeAll);
   }

   private void ignoreToken(boolean ignoreAll) {
      if (ignoreAll) {
         this._iterator.ignoreAll(this._variants.getOriginal());
      }

      this._variants.setVariantIndex(-1);
      this.sendCommittedComposed(false);
   }

   private void addToken() {
      SLCurrentVariant wordToAdd = (SLCurrentVariant)(new Object());
      StringBuffer original = this._variants.getOriginal();
      int misspelledLength = original.length();
      char[] dest = new char[misspelledLength];
      original.getChars(0, misspelledLength, dest, 0);
      wordToAdd._variants = dest;
      wordToAdd._offset = 0;
      wordToAdd._length = misspelledLength;
      this._creator.addWord(wordToAdd, false);
      LearningDataManager.commit();
      this._variants.setVariantIndex(-1);
      this.sendCommittedComposed(false);
   }

   private void showLookup(int lookupMode) {
      SLCurrentVariant variant = (SLCurrentVariant)(new Object());
      this._variantsVector.setSize(0);

      for (int i = 0; i < this._variants.getVariantsCount() && i < 8; i++) {
         this._variants.getVariantAt(i, variant);
         this._variantsVector.addElement(variant.toString());
      }

      this._variants.setVariantIndex(0);
      this._lookup.setItems(this._variantsVector);
      this._state = 3;
      this._lookupMode = lookupMode;
      this.setLookupToRelativePos(this._lookup);
      this._lookup.setVisible(true);
      this._lookupVisible = true;
   }

   private void setLookupToRelativePos(ChooseVariantDialog lu) {
      try {
         int[] range = this._composedText.getLookupRange();
         this._inputMethodContext.getTextLocation(TextHitInfo.leading(range[0]), this._lookupBounds);
         lu.setPositionRelativeTo(this._lookupBounds);
      } finally {
         return;
      }
   }

   private void updateProperties() {
      this._SCEProperties[7] = this._options.getFlag(128);
      this._SCEProperties[0] = this._options.getFlag(1);
      this._SCEProperties[1] = this._options.getFlag(2);
      this._SCEProperties[2] = this._options.getFlag(4);
      this._SCEProperties[11] = this._options.getMinWordSizeForCheck();
      this.updateOptionsOnEngine();
   }

   public SpellCheckInputMethodVariant(Locale[] locales) {
      InputContext ic = InputContext.getInstance(false);
      ic.addSecureBuffer(this);
      this._bundleFamily = ResourceBundle.getBundle(-7934727403592703506L, "net.rim.tid.im.options.SpellCheck.SpellCheck");
      this._SCEProperties = new byte[18];
      this._options = SpellCheckOptions.getOptions();
      this.updateProperties();
      this._composedText = (SLComposedText)(new Object(1, 1));
      this._variants = this._composedText.getCurrentVariant(true);
      this._creator.enablePairLearning(true);
      this._creator.setUseLocaleSpecificRules(true);

      label33:
      try {
         this._creator.setPairLearningSizeLimit(3000);
      } finally {
         break label33;
      }

      this._resultContainer = new SpellCheckResultContainer(32);
      this.setSuggestionLevel((byte)2);
      SpellCheckInputMethodVariant$DispatchEventHandler passthru = new SpellCheckInputMethodVariant$PassthruModeEventHandler(this);
      this._sureTypeDispatchEventHandler = new SpellCheckInputMethodVariant$DispatchEventHandler[7];
      this._regularDispatchEventHandler = new SpellCheckInputMethodVariant$DispatchEventHandler[7];
      this._sureTypeDispatchEventHandler[0] = passthru;
      this._sureTypeDispatchEventHandler[1] = passthru;
      this._sureTypeDispatchEventHandler[2] = passthru;
      this._sureTypeDispatchEventHandler[4] = passthru;
      this._regularDispatchEventHandler[0] = passthru;
      this._regularDispatchEventHandler[1] = passthru;
      this._regularDispatchEventHandler[2] = passthru;
      this._regularDispatchEventHandler[4] = passthru;
      if (AMBIGUOUS_WORDS_SUPPORTED) {
         this._iterator.setCheckForAmbiguousWords(true);
      }

      SpellCheckInputMethodVariant$DispatchEventHandler delegate = new SpellCheckInputMethodVariant$DelegateEditModeEventHandler(this);
      SpellCheckInputMethodVariant$DispatchEventHandler lookup = new SpellCheckInputMethodVariant$SureTypeLookupModeEventHandler(this);
      this._sureTypeDispatchEventHandler[5] = delegate;
      this._sureTypeDispatchEventHandler[6] = delegate;
      this._sureTypeDispatchEventHandler[3] = lookup;
      SpellCheckInputMethodVariant$DispatchEventHandler editMode;
      if (ic.isSureType()) {
         editMode = new SpellCheckInputMethodVariant$MultiTapEditModeEventHandler(this);
      } else {
         editMode = new SpellCheckInputMethodVariant$EditModeEventHandler(this);
      }

      lookup = new SpellCheckInputMethodVariant$LookupModeEventHandler(this);
      this._regularDispatchEventHandler[5] = editMode;
      this._regularDispatchEventHandler[6] = editMode;
      this._regularDispatchEventHandler[3] = lookup;
   }

   private void setSuggestionLevel(byte level) {
      this._creator.setFullMetaphone(level > 5);
      this._creator.setWeightJump(SpellCheckConstants.JUMP_WEIGHTS[level - 1]);
      this._creator.setUseTransitivePairLearningSearch(level >= 4);
   }

   private void showMessage(String aMessage) {
      if (this._pushScreenRunnable == null) {
         this._pushScreenRunnable = new SpellCheckInputMethodVariant$PushScreenRunnable(aMessage);
      } else {
         this._pushScreenRunnable.setMessage(aMessage);
      }

      Application.getApplication().invokeLater(this._pushScreenRunnable);
   }

   private void controlObjectActionPerformed(int action, Object obj) {
      InputContext ic = InputContext.getInstance();
      SLControlObject co = (SLControlObject)ic.getInputMethodControlObject();
      co.actionPerformed(action, obj);
   }

   private void addMenuItems(int mode, Menu menu) {
      MenuItem item;
      if (mode == 3) {
         item = new SpellCheckInputMethodVariant$2(this, CommonResource.getBundle(), 10168, 2, 1);
      } else {
         item = new SpellCheckInputMethodVariant$3(this, CommonResource.getBundle(), 10034, 2, 1);
      }

      menu.add(item);
      if (mode == 1) {
         MenuItem var4 = new SpellCheckInputMethodVariant$4(this, CommonResource.getBundle(), 10036, 2, 1);
         menu.add(var4);
         item = new SpellCheckInputMethodVariant$5(this, CommonResource.getBundle(), 10037, 1, 1);
         menu.add(item);
      }

      menu.setDefault(item);
      item = new SpellCheckInputMethodVariant$6(this, CommonResource.getBundle(), 10038, 3, 1);
      menu.add(item);
   }
}
