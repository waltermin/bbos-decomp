package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.framework.VerbToMenuFactory;
import net.rim.device.internal.i18n.CommonResource;

public class BasicWizardPage implements WizardPage, KeyListener, FieldChangeListener, ValidationProvider, Confirmation, GlobalEventListener {
   protected int _result = 0;
   private int _priority;
   private String _title;
   private long _contentStyle;
   private Manager _content;
   private int _navigationMode = 0;
   private boolean _isInitialized;
   private AppsMainScreen _mainScreen;
   private WizardButtonBar _buttonBar;
   private WizardLayoutManager _wizardLayoutManager;
   private Font _headerFont;
   private Verb _finishVerb;
   private Verb _cancelVerb;
   private Verb _backVerb;
   private Verb _nextVerb;
   private WizardCategory _category;
   private ResourceBundle _rb;
   private int _rbTitleId;
   private boolean _closeOnEscapeKey;
   private int _lastCommand;
   private Log _log;
   private int _wizardFlags;
   private WizardTitleBar _titleBar;
   private int _currentProgress;
   private int _maxProgress;
   private boolean _closeConfirmed;
   protected boolean _warnOnCloseOrHotKey;
   protected Object _context;
   public static final int DEFAULT_WIZARD_FLAGS = 0;
   public static final int HIDE_BUTTON_BAR = 1;
   public static final int HIDE_PROGRESS = 2;
   public static final int HIDE_PAGE = 4;
   public static final int NO_SAVE_PROMPT = 8;
   public static final int NO_SCROLLING_CONTENT = 16;
   public static final long DEFAULT_CONTENT_STYLE = 1152921504606846976L;

   protected WizardButtonBar getButtonBar() {
      return this._buttonBar;
   }

   protected boolean invokeAction(int action) {
      return false;
   }

   protected void resetFonts() {
      this.resetFontsInternal();
      if (this._buttonBar != null) {
         this._buttonBar.resetFonts();
      }
   }

   protected int getLastCommand() {
      return this._lastCommand;
   }

   protected void populateContent(AppsMainScreen _1, Manager _2) {
      throw null;
   }

   protected boolean saveWizard(Verb sender) {
      return true;
   }

   protected void discardWizard() {
   }

   protected boolean canSkipWizardInternal() {
      return false;
   }

   @Override
   public boolean isValid(Object context) {
      return true;
   }

   protected void beforeOpen() {
      this.reloadTitle();
   }

   protected void beforeShow() {
   }

   protected void afterClose() {
   }

   protected void initialize() {
   }

   protected void reloadLabels() {
      if (this._buttonBar != null) {
         this._buttonBar.reloadLabels();
      }
   }

   protected WizardLayoutManager getWizardLayoutManager() {
      return this._wizardLayoutManager;
   }

   protected Verb getBackVerb() {
      return this._navigationMode != 1 ? new BasicWizardPage$WizardVerb(this, 17039696, 2) : null;
   }

   protected Verb getNextVerb() {
      return this._navigationMode != 2 ? new BasicWizardPage$WizardVerb(this, 17039616, 3) : null;
   }

   protected Verb getCancelVerb() {
      return ExitVerb.createCloseVerb(0, this);
   }

   protected Verb getFinishVerb() {
      return this._navigationMode == 2 ? new BasicWizardPage$WizardVerb(this, 17039616, 8) : null;
   }

   protected WizardTitleBar getWizardTitleBar() {
      return this._titleBar;
   }

   protected Font getHeaderFont() {
      return this._headerFont;
   }

   protected AppsMainScreen getMainScreen() {
      return this._mainScreen;
   }

   protected void doEscape() {
      if (this._currentProgress == 1 && this._warnOnCloseOrHotKey) {
         if (WizardKeyEventManager.promptUserForKeyUseExit(14) != 4) {
            this._closeConfirmed = false;
            return;
         }

         this._closeConfirmed = true;
      }

      if (this._closeOnEscapeKey) {
         this.doCancel();
      } else if (this._backVerb != null) {
         this._buttonBar.invokeBack();
      } else {
         this._buttonBar.invokeCancel();
      }
   }

   protected void doFinish() {
      if (this._finishVerb != null) {
         this._finishVerb.invoke(null);
      }
   }

   protected void doCancel() {
      if (this._cancelVerb != null) {
         this._cancelVerb.invoke(null);
      }
   }

   protected void doBack() {
      if (this._backVerb != null) {
         this._backVerb.invoke(null);
      }
   }

   protected void doNext() {
      if (this._nextVerb != null) {
         this._nextVerb.invoke(null);
      }
   }

   protected void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
   }

   protected void log(String event) {
      if (this._log != null) {
         this._log.log(event);
      }
   }

   protected boolean loggingEnabled() {
      return this._log != null;
   }

   protected void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      if (this._cancelVerb != null && instance == 0) {
         verbToMenu.addVerb(this._cancelVerb);
      }

      if (this._finishVerb != null) {
         verbToMenu.addVerb(this._finishVerb);
      }

      if (this._nextVerb != null) {
         verbToMenu.addVerb(this._nextVerb);
      }

      if (this._backVerb != null) {
         verbToMenu.addVerb(this._backVerb);
      }
   }

   @Override
   public boolean confirm(Verb verb, Object context) {
      if (verb instanceof BasicWizardPage$WizardVerb && ((BasicWizardPage$WizardVerb)verb).canAutoSave()) {
         return this.saveWizard(verb);
      }

      boolean exitVerb = verb instanceof Object;
      if (this._currentProgress < this._maxProgress && (this._currentProgress == 1 || exitVerb) && this._warnOnCloseOrHotKey && !this._closeConfirmed) {
         int result = WizardExitDialog.createExitDialog().doModal();
         if (result != 4) {
            return false;
         }
      }

      if (this._mainScreen.isDirty()) {
         this.log("Prompting for save.");
         int result;
         if ((this._wizardFlags & 8) == 0) {
            result = WizardDialog.ask(1, CommonResource.getString(10003), this._warnOnCloseOrHotKey);
         } else {
            result = 2;
         }

         switch (result) {
            case 0:
               this.log("Cancel from save prompt");
               return false;
            case 1:
            default:
               this.log("Saving");
               return this.saveWizard(verb);
            case 2:
               this.log("Discarding");
               this.discardWizard();
               return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4394903006263251010L) {
         Application.getApplication().invokeLater(new BasicWizardPage$1(this));
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field instanceof WizardButtonBar) {
         switch (context) {
            case 0:
               this.doCancel();
               break;
            case 1:
            default:
               this.doBack();
               return;
            case 2:
               this.doNext();
               return;
            case 3:
               this.doFinish();
               return;
         }
      }
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(515, keycode, time, this._warnOnCloseOrHotKey);
   }

   @Override
   public boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(515, keycode, time, this._warnOnCloseOrHotKey);
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(513, keycode, time, this._warnOnCloseOrHotKey);
   }

   @Override
   public boolean keyChar(char c, int status, int time) {
      if (c == 27) {
         this.doEscape();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void reloadTitle() {
      if (this._rb != null) {
         this._title = this._rb.getString(this._rbTitleId);
      }
   }

   @Override
   public final WizardCategory getCategory() {
      return this._category;
   }

   @Override
   public final int showPage(int lastCommand, int context) {
      this._lastCommand = lastCommand;
      if (!this._isInitialized) {
         this.initialize();
         this._isInitialized = true;
      }

      this._closeOnEscapeKey = (context & 1) != 0;
      this._warnOnCloseOrHotKey = (context & 4) != 0;
      this._result = 0;
      this.beforeOpen();
      this.createMainScreen();
      Application.getApplication().addGlobalEventListener(this);
      this.beforeShow();
      UiApplication.getUiApplication().pushModalScreen(this._mainScreen);
      this.afterClose();
      Application.getApplication().removeGlobalEventListener(this);
      return this._result;
   }

   @Override
   public final void setLogManager(LogManager logManager) {
      this._log = null;
      if (logManager != null) {
         this._log = logManager.getCategory(this.getTitle());
      }
   }

   @Override
   public final boolean isHidden() {
      return (this._wizardFlags & 4) == 4;
   }

   @Override
   public final boolean canSkipWizard() {
      if (!this._isInitialized) {
         this.initialize();
         this._isInitialized = true;
      }

      return this.canSkipWizardInternal();
   }

   @Override
   public final int getPriority() {
      return this._priority;
   }

   @Override
   public final String getTitle() {
      return this._title;
   }

   @Override
   public int getPageCount() {
      return 1;
   }

   @Override
   public void setProgress(int current, int visibleMax, int absoluteMax) {
      this._currentProgress = current;
      this._maxProgress = visibleMax;
      if (current == absoluteMax) {
         this._navigationMode = 2;
      } else if (current == 1) {
         this._navigationMode = 1;
      } else {
         this._navigationMode = 0;
      }
   }

   private void makeWizardMenu(Menu menu, int instance) {
      VerbToMenu verbToMenu = VerbToMenuFactory.createInstance();
      this.populateMenuVerbs(verbToMenu, instance);
      verbToMenu.getMenu(menu, null);
   }

   private boolean fieldRequiresDefaultVerb(Field field) {
      return field instanceof Object || field instanceof Object || field instanceof Object;
   }

   private void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      this._cancelVerb = this.getCancelVerb();
      this._finishVerb = this.getFinishVerb();
      this._nextVerb = this.getNextVerb();
      this._backVerb = this.getBackVerb();
      this.addScreenVerbs(verbToMenu, instance);
      this.addRepositoryVerbs(verbToMenu, instance);
      if (!this.fieldRequiresDefaultVerb(this._mainScreen.getLeafFieldWithFocus())) {
         if (this._nextVerb != null) {
            verbToMenu.setDefaultVerb(this._nextVerb);
         } else if (this._finishVerb != null) {
            verbToMenu.setDefaultVerb(this._finishVerb);
         }

         verbToMenu.setDefaultVerbPriority(150);
      }
   }

   public BasicWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category) {
      this(rb, rbTitleId, priority, category, 0, 1152921504606846976L);
   }

   public BasicWizardPage(String title, int priority, WizardCategory category) {
      this(title, priority, category, 0, 1152921504606846976L);
   }

   public BasicWizardPage(String title, int priority, WizardCategory category, int wizardFlags) {
      this(title, priority, category, wizardFlags, 1152921504606846976L);
   }

   private void populateMainScreen(AppsMainScreen screen) {
      screen.addKeyListener(this);
      this._content = (Manager)(new Object(this._contentStyle));
      this._wizardLayoutManager = new WizardLayoutManager();
      if ((this._wizardFlags & 16) == 0) {
         this._wizardLayoutManager.setScrollbarEnabled(true);
      }

      this.populateContent(screen, this._content);
      this._wizardLayoutManager.setContent(this._content);
      if ((this._wizardFlags & 1) == 1) {
         this._buttonBar = null;
      } else {
         this._wizardLayoutManager.setFooter(this._buttonBar = new WizardButtonBar(this._navigationMode));
         this._buttonBar.setChangeListener(this);
      }

      screen.add(this._wizardLayoutManager);
   }

   public BasicWizardPage(String title, int priority, WizardCategory category, int wizardFlags, long contentStyle) {
      this._title = title;
      this._priority = priority;
      this._contentStyle = contentStyle;
      this._category = category;
      this._wizardFlags = wizardFlags;
      this._contentStyle = contentStyle;
      if ((this._wizardFlags & 16) == 0) {
         this._contentStyle |= 281474976710656L;
      } else {
         this._contentStyle |= 562949953421312L;
      }
   }

   private void createMainScreen() {
      this._mainScreen = new BasicWizardPage$WizardMainScreen(this, 562949953421312L);
      this._titleBar = new WizardTitleBar(this.getTitle());
      this._titleBar.setProgress(this._currentProgress, this._maxProgress);
      this._titleBar.setProgressHidden((this._wizardFlags & 2) != 0);
      this._mainScreen.setTitle(this._titleBar);
      this.resetFontsInternal();
      this._finishVerb = this.getFinishVerb();
      this._cancelVerb = this.getCancelVerb();
      this._nextVerb = this.getNextVerb();
      this._backVerb = this.getBackVerb();
      this.populateMainScreen(this._mainScreen);
   }

   private void resetFontsInternal() {
      Font defaultFont = Font.getDefault();
      int fontSize = Ui.convertSize(defaultFont.getHeight(), 0, 3);
      if (fontSize > 8) {
         fontSize--;
      }

      if (this._mainScreen != null) {
         this._mainScreen.setFont(defaultFont.derive(defaultFont.getStyle(), fontSize, 3));
      }

      if (fontSize > 7) {
         fontSize--;
      }

      this._headerFont = defaultFont.derive(0, fontSize, 3);
   }

   public BasicWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int wizardFlags) {
      this(rb.getString(rbTitleId), priority, category, wizardFlags, 1152921504606846976L);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }

   public BasicWizardPage(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int wizardFlags, long contentStyle) {
      this(rb.getString(rbTitleId), priority, category, wizardFlags, contentStyle);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }
}
