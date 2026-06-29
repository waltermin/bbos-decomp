package net.rim.device.apps.api.setupwizard;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ValidationProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsContext;

public class WizardController implements WizardPage, ValidationProvider {
   private SimpleSortingVector _pages;
   private Vector _visiblePages;
   private WizardController$WizardComparator _wizardComparator = new WizardController$WizardComparator();
   private int _currentIndex = 0;
   private int _lastIndex;
   private boolean _isInitialized;
   private WizardPage _completingWizardPage;
   private boolean _canCalculateProgress;
   private String _title;
   private ResourceBundle _rb;
   private int _rbTitleId;
   private int _priority;
   private WizardCategory _category;
   private int _totalPageCount;
   private int _flags;
   private Log _log;
   private LogManager _logManager;
   private StringBuffer _buffer;
   private Object[] _wizardControllerListeners = new Object[0];
   public static final int NO_PROGRESS_INCREMENT = 1;
   public static final int NO_CLOSE_ON_ESCAPE_KEY = 2;
   public static final int DEFAULT_FLAGS = 0;
   private static final int LOG_IN = 0;
   private static final int LOG_OUT = 1;
   private static final int LOG_SKIP = 2;

   protected void calculateProgress() {
      int visibleMax = 0;
      int absoluteMax = 0;
      boolean foundCompletingPage = false;
      int numPages = this._pages.size();

      for (int i = 0; i < numPages; i++) {
         WizardPage page = this.getAbsolutePage(i);
         if (!page.canSkipWizard()) {
            int pageCount = page.getPageCount();
            if (page == this._completingWizardPage) {
               foundCompletingPage = true;
               visibleMax += pageCount;
            }

            absoluteMax += pageCount;
            if (!foundCompletingPage) {
               visibleMax += pageCount;
            }
         }
      }

      int current = 1;

      for (int i = 0; i < numPages; i++) {
         WizardPage page = this.getAbsolutePage(i);
         page.setProgress(current, visibleMax, absoluteMax);
         if (!page.canSkipWizard()) {
            current += page.getPageCount();
         }
      }
   }

   protected void setPages(Vector pages) {
      int numPages = pages.size();
      this._pages = (SimpleSortingVector)(new Object());
      this._pages.setSort(false);
      this._pages.setSortComparator(this._wizardComparator);
      this._visiblePages = (Vector)(new Object(numPages));
      this._totalPageCount = 0;
      ContextObject context = OptionsContext.getContextObject();

      for (int i = 0; i < numPages; i++) {
         WizardPage page = (WizardPage)pages.elementAt(i);
         if (page != null && (!(page instanceof Object) || ((ValidationProvider)page).isValid(context))) {
            this._totalPageCount = this._totalPageCount + page.getPageCount();
            this._pages.addElement(page);
         }
      }

      this._pages.setSort(true);
      numPages = this._pages.size();

      for (int i = 0; i < numPages; i++) {
         WizardPage page = (WizardPage)this._pages.elementAt(i);
         if (!page.isHidden()) {
            this._visiblePages.addElement(page);
         }
      }

      this._lastIndex = 0;
   }

   protected void log(int event, WizardPage page, int command, boolean isExplicitCommand, long time_ms) {
      if (this._log != null) {
         if (this._buffer == null) {
            this._buffer = (StringBuffer)(new Object());
         }

         this._buffer.setLength(0);
         switch (event) {
            case -1:
               break;
            case 0:
               if (isExplicitCommand) {
                  this._buffer.append("Index: ");
               } else {
                  this._buffer.append("Enter: ");
               }
               break;
            case 1:
               this._buffer.append("Exit : ");
               break;
            case 2:
            default:
               this._buffer.append("Skip : ");
         }

         this._buffer.append(Integer.toString(this._currentIndex));
         this._buffer.append(":\"");
         this._buffer.append(page.getTitle());
         this._buffer.append("\"");
         if (event == 1) {
            this._buffer.append(" (");
            this._buffer.append(Long.toString(time_ms / 1000));
            this._buffer.append(" seconds) -> ");
            this._buffer.append(commandToString(command));
         }

         this._log.log(this._buffer.toString());
      }
   }

   public boolean saveWizard(Verb sender) {
      return true;
   }

   public boolean discardWizard() {
      return true;
   }

   protected WizardPage getAbsolutePage(int index) {
      return (WizardPage)this._pages.elementAt(index);
   }

   protected void afterClose() {
      if (this._wizardControllerListeners != null && this._wizardControllerListeners.length > 0) {
         this._wizardControllerListeners = null;
      }
   }

   public void addWizardControllerListener(WizardController$Listener listener) {
      if (this._wizardControllerListeners != null) {
         this._wizardControllerListeners = ListenerUtilities.addListener(this._wizardControllerListeners, listener);
      }
   }

   @Override
   public boolean isValid(Object context) {
      return this._pages.size() > 0;
   }

   protected void beforeClose(int result) {
   }

   protected void initialize() {
   }

   public WizardPage getLastViewedPage() {
      return this.getAbsolutePage(this._lastIndex);
   }

   public WizardPage getFirstPage() {
      int numPages = this._pages.size();

      for (int i = 0; i < numPages; i++) {
         WizardPage page = (WizardPage)this._pages.elementAt(i);
         if (!(page instanceof WizardCategory)) {
            return page;
         }
      }

      return null;
   }

   public int showWizardPage(int index, int context) {
      this._currentIndex = this.visibleToAbsoluteIndex(index);
      return this.start((context & 1) != 0 ? 0 : 2, context);
   }

   public int showWizardPage(WizardPage page, int context) {
      int index = 0;
      int numPages = this._visiblePages.size();

      for (int i = 0; i < numPages; i++) {
         if (this.getPage(i) == page) {
            index = i;
            break;
         }
      }

      return this.showWizardPage(index, context);
   }

   public void setCompletingWizardPage(WizardPage val) {
      this._completingWizardPage = val;
   }

   public WizardPage getCompletingWizardPage() {
      return this._completingWizardPage;
   }

   public int size() {
      return this._visiblePages.size();
   }

   public WizardPage getPage(int index) {
      return (WizardPage)this._visiblePages.elementAt(index);
   }

   @Override
   public void reloadTitle() {
      if (this._rb != null) {
         this._title = this._rb.getString(this._rbTitleId);
      }

      int numPages = this._pages.size();

      for (int i = 0; i < numPages; i++) {
         ((WizardPage)this._pages.elementAt(i)).reloadTitle();
      }
   }

   @Override
   public WizardCategory getCategory() {
      return this._category;
   }

   @Override
   public int showPage(int lastCommand, int context) {
      if (!this._isInitialized) {
         this._isInitialized = true;
         this.initialize();
      }

      if (this._pages.size() == 0) {
         return 0;
      }

      int initialPage;
      if (lastCommand == 1) {
         initialPage = this._pages.size() - 1;
      } else {
         initialPage = 0;
      }

      this._currentIndex = initialPage;
      this._lastIndex = this._currentIndex;
      return this.start(lastCommand, context);
   }

   @Override
   public boolean isHidden() {
      return false;
   }

   @Override
   public boolean canSkipWizard() {
      if (this._pages != null) {
         int numPages = this._pages.size();

         for (int i = 0; i < numPages; i++) {
            if (!((WizardPage)this._pages.elementAt(i)).canSkipWizard()) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void setLogManager(LogManager logManager) {
      this._log = null;
      this._logManager = logManager;
      if (this._logManager != null) {
         this._log = this._logManager.getCategory(this.getTitle());
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
   public int getPageCount() {
      return (this._flags & 1) != 0 ? 1 : this._totalPageCount;
   }

   @Override
   public void setProgress(int current, int visibleMax, int absoluteMax) {
      if (visibleMax < current) {
         this._canCalculateProgress = true;
         this.calculateProgress();
      } else {
         int numPages = this._pages.size();

         for (int i = 0; i < numPages; i++) {
            WizardPage page = this.getAbsolutePage(i);
            page.setProgress(current, visibleMax, absoluteMax);
            if ((this._flags & 1) == 0) {
               current += page.getPageCount();
            }
         }
      }
   }

   protected WizardController(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category) {
      this(rb, rbTitleId, priority, category, 0);
   }

   protected WizardController(String title, int priority, WizardCategory category) {
      this(title, priority, category, 0);
   }

   private int visibleToAbsoluteIndex(int index) {
      int numPages = this._pages.size();
      if (numPages == this._visiblePages.size()) {
         return index;
      }

      Object page = this._visiblePages.elementAt(index);

      for (int i = 0; i < numPages; i++) {
         if (this._pages.elementAt(i) == page) {
            return i;
         }
      }

      return 0;
   }

   protected WizardController(String title, int priority, WizardCategory category, int flags) {
      this._title = title;
      this._priority = priority;
      this._category = category;
      this._flags = flags;
   }

   private static String commandToString(int command) {
      switch (command) {
         case -1:
            return "Unknown";
         case 0:
            return "Cancel";
         case 1:
            return "Back";
         case 2:
         default:
            return "Next";
         case 3:
            return "Finish";
         case 4:
            return "Escape";
      }
   }

   protected WizardController(ResourceBundle rb, int rbTitleId, int priority, WizardCategory category, int flags) {
      this(rb.getString(rbTitleId), priority, category, flags);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }

   public WizardController(String title, Vector pages, int flags) {
      this._title = title;
      this._flags = flags;
      this.setPages(pages);
      this._canCalculateProgress = true;
   }

   private int start(int lastCommand, int context) {
      int result = lastCommand;
      boolean explicitCommand = lastCommand == 0;
      boolean finished = false;
      if (this._canCalculateProgress) {
         this.calculateProgress();
      }

      WizardPage page;
      while (!finished && (page = this.getCurrentPage()) != null) {
         boolean canSkipWizard = page.canSkipWizard();
         if (explicitCommand || !canSkipWizard) {
            int localContext = context;
            this.log(0, page, result, explicitCommand, 0);
            this._lastIndex = this._currentIndex;
            long startTime = System.currentTimeMillis();
            page.setLogManager(this._logManager);
            if (this._currentIndex > 0 && (this._flags & 2) != 0) {
               localContext &= -2;
            }

            if (this._wizardControllerListeners != null && this._wizardControllerListeners.length > 0) {
               this.notifyListeners(true, this, page, result, localContext);
            }

            result = page.showPage(result, localContext);
            if (this._wizardControllerListeners != null && this._wizardControllerListeners.length > 0) {
               this.notifyListeners(false, this, page, result, localContext);
            }

            this.log(1, page, result, explicitCommand, System.currentTimeMillis() - startTime);
            explicitCommand = false;
            if (this._canCalculateProgress) {
               this.calculateProgress();
            }
         } else if (this._log != null && !(page instanceof WizardCategory)) {
            this.log(2, page, result, explicitCommand, 0);
         }

         switch (result) {
            case 0:
               finished = true;
               break;
            case 1:
               this.back();
               break;
            case 2:
            default:
               this.next();
         }
      }

      this.beforeClose(result);
      return result;
   }

   private WizardPage getCurrentPage() {
      return this._currentIndex >= 0 && this._currentIndex < this._pages.size() ? (WizardPage)this._pages.elementAt(this._currentIndex) : null;
   }

   private boolean next() {
      if (++this._currentIndex >= this._pages.size()) {
         this._currentIndex = this._pages.size();
      }

      return true;
   }

   private void back() {
      if (--this._currentIndex < 0) {
         this._currentIndex = -1;
      }
   }

   private void notifyListeners(boolean openingPage, WizardController sender, WizardPage page, int result, int context) {
      for (int i = this._wizardControllerListeners.length - 1; i >= 0; i--) {
         if (this._wizardControllerListeners[i] != null) {
            if (openingPage) {
               ((WizardController$Listener)this._wizardControllerListeners[i]).openingWizardPage(sender, page, result, context);
            } else {
               ((WizardController$Listener)this._wizardControllerListeners[i]).closingWizardPage(sender, page, result, context);
            }
         }
      }
   }
}
