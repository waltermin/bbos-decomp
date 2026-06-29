package net.rim.device.apps.internal.help;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.setupwizard.Log;
import net.rim.device.apps.api.setupwizard.LogManager;
import net.rim.device.apps.api.setupwizard.WizardButtonBar;
import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.WizardExitDialog;
import net.rim.device.apps.api.setupwizard.WizardKeyEventManager;
import net.rim.device.apps.api.setupwizard.WizardLayoutManager;
import net.rim.device.apps.api.setupwizard.WizardPage;
import net.rim.device.apps.api.setupwizard.WizardTitleBar;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.internal.browser.model.HTTPAddressModel;

public class HelpWizardProvider extends HelpScreen implements WizardPage, FieldChangeListener, ValidationProvider {
   private String _title;
   private int _priority;
   private WizardCategory _category;
   protected int _wizardResult;
   private ResourceBundle _rb;
   private int _rbTitleId;
   private String[] _topics = new String[0];
   private int _currentTopic;
   private Manager _contentContainer;
   private WizardButtonBar _buttonBar;
   private boolean _closeOnEscapeKey;
   private boolean _simpleMenu;
   private Log _log;
   private StringBuffer _logBuffer;
   private long _startTime;
   private WizardTitleBar _titleBar;
   private int _currentProgress;
   private int _absoluteProgress;
   private int _visibleMaxProgress;
   private int _absoluteMaxProgress;
   private VerticalFieldManager _footer;
   private int _navigationMode;
   protected boolean _warnOnCloseOrHotKeys;
   private int _wizardFlags;
   public static final int NO_TITLE_BAR = 131072;
   public static final int FULL_MENU = 262144;
   protected static String[] WIZARD_INTRO_TOPICS = new String[]{"intro1.rhtml", "intro2.rhtml", "intro3.rhtml"};
   protected static String[] WIZARD_INTRO_TOPICS_POSITRON = new String[]{"intro2.rhtml", "intro3.rhtml"};
   protected static String[] WIZARD_SHORTCUTS_TOPICS = new String[]{"tipstricks3.rhtml"};
   protected static String[] WIZARD_SURETYPE_TOPICS = new String[]{
      "suretype_intro.rhtml", "suretype1.rhtml", "suretype2.rhtml", "suretype3.rhtml", "suretype4.rhtml"
   };
   protected static String[] WIZARD_TIPS_TOPICS = new String[]{"tipstricks1.rhtml", "tipstricks2.rhtml", "tipstricks4.rhtml"};

   public void setTopics(String[] topics) {
      if (topics == null) {
         this._topics = new String[0];
      } else {
         this._topics = topics;
      }
   }

   protected void createTitleBar() {
      if ((this._wizardFlags & 131072) == 0) {
         this._titleBar = new WizardTitleBar("");
         if ((this._wizardFlags & 2) != 0) {
            this._titleBar.setProgressHidden(true);
         }

         this.setTitle(this._titleBar);
      }
   }

   protected void log(String event) {
      if (this._log != null) {
         this._log.log(event);
      }
   }

   protected void logPage(boolean isEntering, String command) {
      if (this._log != null) {
         long currentTime = System.currentTimeMillis();
         if (this._logBuffer == null) {
            this._logBuffer = new StringBuffer();
         }

         this._logBuffer.setLength(0);
         if (isEntering) {
            this._logBuffer.append("Enter: ");
         } else {
            this._logBuffer.append("Exit : ");
         }

         this._logBuffer.append(Integer.toString(this._currentTopic));
         this._logBuffer.append(":");
         this._logBuffer.append(this._topics[this._currentTopic]);
         if (!isEntering) {
            this._logBuffer.append(" (");
            this._logBuffer.append(Long.toString((currentTime - this._startTime) / 1000));
            this._logBuffer.append(" seconds) -> ");
            this._logBuffer.append(command);
         }

         this._log.log(this._logBuffer.toString());
         this._startTime = currentTime;
      }
   }

   protected void launchUrl(String url) {
      HelpScreen helpScreen = new HelpScreen(url);
      UiApplication.getUiApplication().pushScreen(helpScreen);
   }

   protected boolean activateFocusField() {
      Field field = this.getLeafFieldWithFocus();
      Manager manager = field.getManager();

      while (manager != null && !(manager instanceof CookieProvider)) {
         manager = manager.getManager();
      }

      if (manager != null) {
         Object cookie = CookieProviderUtilities.getDefaultCookie(((CookieProvider)manager).getCookieWithFocus());
         if (cookie instanceof HTTPAddressModel) {
            this.launchUrl(((HTTPAddressModel)cookie).getURL());
            return true;
         }
      }

      return false;
   }

   protected MenuItem getBackMenu() {
      return this._navigationMode != 1 ? new HelpWizardProvider$6(this, SetupWizardAPIResources.getResourceBundle(), 2, 268369956, 110) : null;
   }

   protected MenuItem getNextMenu() {
      return this._navigationMode != 2
         ? new HelpWizardProvider$4(this, SetupWizardAPIResources.getResourceBundle(), 3, 268369946, 100)
         : new HelpWizardProvider$5(this, SetupWizardAPIResources.getResourceBundle(), 8, 268369946, 100);
   }

   boolean confirmClose() {
      boolean canClose = false;
      if (this._warnOnCloseOrHotKeys && this._absoluteProgress < this._visibleMaxProgress) {
         int result = WizardExitDialog.createExitDialog().doModal();
         if (result == 4) {
            canClose = true;
         }

         return canClose;
      } else {
         return true;
      }
   }

   protected void doEscape() {
      this.logPage(false, "Escape");
      if (this._absoluteProgress != 1 || !this._warnOnCloseOrHotKeys || WizardKeyEventManager.promptUserForKeyUseExit(14) == 4) {
         if (this._closeOnEscapeKey && this._currentTopic == 0) {
            this.doCancel();
         } else if (this._buttonBar != null) {
            this._buttonBar.invokeBack();
         } else {
            this.doBack();
         }
      }
   }

   protected boolean doCancel() {
      boolean canClose = this.confirmClose();
      if (canClose) {
         this._wizardResult = 0;
         this.logPage(false, "Cancel");
         UiApplication.getUiApplication().popScreen(this);
      }

      return canClose;
   }

   protected void doBack() {
      this.logPage(false, "Back");
      if (--this._currentTopic >= 0) {
         this.logPage(true, "Back");
         this.setInternalProgress(this._currentTopic);
         this.loadHelpTopic(this.getTopicUrl(this._currentTopic));
         this._contentContainer.getField(0).setFocus();
      } else {
         if (this._absoluteProgress > 1 || this.confirmClose()) {
            this._wizardResult = 1;
            this.log("BOF");
            if (Ui.getUiEngine() == this.getUiEngine()) {
               UiApplication.getUiApplication().popScreen(this);
            }
         }
      }
   }

   protected void doNext() {
      this.logPage(false, "Next");
      if (++this._currentTopic < this._topics.length) {
         this.logPage(true, "Next");
         this.displayCurrentTopic();
      } else {
         this._wizardResult = 2;
         this.log("EOF");
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   protected void setInternalProgress(int internalProgress) {
      this._absoluteProgress = this._currentProgress + internalProgress;
      if (this._titleBar != null) {
         this._titleBar.setProgress(this._absoluteProgress, this._visibleMaxProgress);
      }

      if (this._absoluteProgress == this._absoluteMaxProgress) {
         this._navigationMode = 2;
      } else if (this._absoluteProgress == 1) {
         this._navigationMode = 1;
      } else {
         this._navigationMode = 0;
      }

      this.createButtonBar(this._navigationMode);
   }

   protected void createButtonBar(int navigationMode) {
      if ((this._wizardFlags & 1) == 0) {
         if (this._footer != null) {
            if (this._buttonBar != null) {
               this._footer.delete(this._buttonBar);
               this._buttonBar = null;
            }

            this._footer.add(this._buttonBar = new HelpWizardProvider$3(this, navigationMode));
            this._buttonBar.setChangeListener(this);
         }
      }
   }

   protected void displayCurrentTopic() {
      this.setInternalProgress(this._currentTopic);
      this.loadHelpTopic(this.getTopicUrl(this._currentTopic));
      this._contentContainer.getField(0).setFocus();
   }

   protected int startWizard(int lastCommand) {
      if (lastCommand == 1) {
         this._currentTopic = this._topics.length - 1;
      } else {
         this._currentTopic = 0;
      }

      this._wizardResult = 0;
      this._startTime = System.currentTimeMillis();
      this.logPage(true, "Start");
      this.displayCurrentTopic();
      UiApplication.getUiApplication().pushModalScreen(this);
      return this._wizardResult;
   }

   @Override
   public boolean isValid(Object context) {
      return this._topics.length > 0;
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
         }
      }
   }

   @Override
   public void reloadTitle() {
      if (this._rb != null) {
         this._title = this._rb.getString(this._rbTitleId);
      }
   }

   @Override
   public WizardCategory getCategory() {
      return this._category;
   }

   @Override
   public boolean isHidden() {
      return (this._wizardFlags & 4) != 0;
   }

   @Override
   public boolean canSkipWizard() {
      return this._topics.length == 0;
   }

   @Override
   public int showPage(int lastCommand, int context) {
      this._closeOnEscapeKey = (context & 1) != 0;
      this._warnOnCloseOrHotKeys = (context & 4) != 0;
      if (this._topics.length == 0) {
         return 0;
      }

      Font defaultFont = Font.getDefault();
      int fontPt = Ui.convertSize(defaultFont.getHeight(), 0, 3);
      if (fontPt > 8) {
         fontPt -= 2;
      } else if (fontPt > 7) {
         fontPt--;
      }

      RenderingOptions renderingOptions = super.getRenderingOptions();
      renderingOptions.setProperty(4550690918222697397L, 31, defaultFont.getFontFamily().getName());
      renderingOptions.setProperty(4550690918222697397L, 32, fontPt);
      this._contentContainer = new HelpWizardProvider$1(this, 2306124484190404608L);
      WizardLayoutManager wizardLayoutManager = new WizardLayoutManager();
      wizardLayoutManager.setContent(this._contentContainer);
      wizardLayoutManager.setScrollbarEnabled(true);
      this._buttonBar = null;
      this._footer = new HelpWizardProvider$2(this, 1152921504606846976L);
      wizardLayoutManager.setFooter(this._footer);
      this.deleteAll();
      this.add(wizardLayoutManager);
      this.createTitleBar();
      return this.startWizard(lastCommand);
   }

   @Override
   public void setLogManager(LogManager logManager) {
      this._log = null;
      if (logManager != null) {
         this._log = logManager.getCategory(this.getTitle());
      }
   }

   @Override
   public int getPriority() {
      return this._priority;
   }

   @Override
   public String getTitle() {
      return this._title;
   }

   @Override
   public void setProgress(int current, int visibleMax, int absoluteMax) {
      this._currentProgress = current;
      this._visibleMaxProgress = visibleMax;
      this._absoluteMaxProgress = absoluteMax;
   }

   @Override
   public int getPageCount() {
      return this._topics.length;
   }

   @Override
   public boolean keyRepeat(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(514, keycode, time, this._warnOnCloseOrHotKeys) ? true : super.keyRepeat(keycode, time);
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(513, keycode, time, this._warnOnCloseOrHotKeys) ? true : super.keyDown(keycode, time);
   }

   @Override
   protected Manager getContentContainer() {
      return this._contentContainer;
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      return WizardKeyEventManager.processKeyEvent(515, keycode, time, this._warnOnCloseOrHotKeys) ? true : super.keyUp(keycode, time);
   }

   private String getTopicUrl(int index) {
      return this._topics[index];
   }

   @Override
   protected boolean handleSendKey() {
      return false;
   }

   @Override
   protected String customLoadResource(String resourceUrl) {
      return WizardCustomResourceLoader.getCustomizedResource(resourceUrl);
   }

   @Override
   protected boolean handleEndKey() {
      return WizardKeyEventManager.processKeyEvent(513, Keypad.keycode('\u0012', 0), 0, this._warnOnCloseOrHotKeys) ? true : super.handleEndKey();
   }

   @Override
   protected boolean canGoBack() {
      return false;
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      if (this._simpleMenu) {
         menu.deleteAll();
         Field field = this.getLeafFieldWithFocus();
         if (field != null) {
            if (instance == 0) {
               ContextMenu cmenu = field.getContextMenu();
               menu.add(cmenu, true);
            } else {
               ContextMenu.getInstance().setTarget(field);
            }
         }
      }

      super.makeMenu(menu, instance);
      MenuItem nextMenu = this.getNextMenu();
      MenuItem backMenu = this.getBackMenu();
      if (nextMenu != null) {
         menu.add(nextMenu);
         menu.setDefault(nextMenu);
      }

      if (backMenu != null) {
         menu.add(backMenu);
      }
   }

   @Override
   protected boolean keyCharUnhandled(char key, int status, int time) {
      if (key == 27) {
         this.doEscape();
         return true;
      } else {
         return key == '\n' && this.activateFocusField() ? true : super.keyCharUnhandled(key, status, time);
      }
   }

   @Override
   public Object eventOccurred(Event event) {
      switch (event.getUID()) {
         case 10001:
            if (this._titleBar != null && event.getSource() instanceof BrowserContent) {
               BrowserContent browserField = (BrowserContent)event.getSource();
               String newTitle = browserField.getTitle();
               synchronized (Application.getApplication().getAppEventLock()) {
                  this._titleBar.setLabel(newTitle);
                  return null;
               }
            }

            return null;
         case 10010:
            this.launchUrl(((UrlRequestedEvent)event).getURL());
            return null;
         default:
            return super.eventOccurred(event);
      }
   }

   public HelpWizardProvider(String title, int priority, WizardCategory category, int helpWizardFlags) {
      super(null, 2306405959167115264L);
      this._title = title;
      this._priority = priority;
      this._simpleMenu = (helpWizardFlags & 262144) == 0;
      this._category = category;
      this._wizardFlags = helpWizardFlags;
   }

   @Override
   public boolean onClose() {
      return this.doCancel();
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (this.activateFocusField()) {
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      return this.activateFocusField() ? true : super.trackwheelClick(status, time);
   }

   public HelpWizardProvider(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category) {
      this(rb.getString(rbTitleId), priority, category, 0);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }

   public HelpWizardProvider(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int helpWizardFlags) {
      this(rb.getString(rbTitleId), priority, category, helpWizardFlags);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }

   public HelpWizardProvider(String title, int priority, WizardCategory category) {
      this(title, priority, category, 0);
   }

   private static void paintBackgroundColor(XYRect extent, Graphics graphics, int color) {
      graphics.setGlobalAlpha(255);
      int oldColor = graphics.getColor();
      graphics.setColor(color);
      graphics.fillRect(0, 0, extent.width, extent.height);
      graphics.setColor(oldColor);
   }
}
