package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.addressbook.AddressBookOptionsImpl;
import net.rim.device.apps.internal.addressbook.BlackBerryAddressBook;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.lookup.RequestModel;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.commonmodels.categories.FilterByCategoriesVerb;
import net.rim.device.apps.internal.commonmodels.categories.FilteredByCategoriesTitleField;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;

public final class AddressBookListUI extends KeywordFilteredScreen implements GlobalEventListener, OptionsChangeListener {
   private int _mode;
   private AddressBookListField _listField;
   private boolean _terminateOnEsc;
   private Verb[] _useOnceVerbs;
   private Verb[] _useOnceVerbsWrapped;
   private ContextObject _baseContext;
   private ContextObject _trackwheelclickContext;
   private Verb[] _tmpVerbs = new Object[0];
   private SelectionListener _selectionListener;
   private AddressSelectionContext _selectionContext;
   private UiApplication _updatingUIApplication;
   private Screen _reorgScreen;
   private int[] _filterCategoryIds;
   private PersistentObject _persistentFilterObject;
   private FilteredByCategoriesTitleField _titleField;
   private long _lastBackspaceTime;
   private static final int APPLICATION_MODE;
   private static final int SELECTION_MODE;
   private static final long LAST_ADDRESS_USED;
   private static final long PERSISTED_ADDRESS_BOOK_FILTER;
   static final long OPEN_GAL_RESULTS_GUID;
   private static final long BACKSPACE_DELETE_GUARD;

   AddressBookListUI(String title, Verb[] useOnceVerbs, Object initialContext) {
      super(
         title,
         (String)((Object)null),
         AddressBookListField.getInstance(useOnceVerbs != null && useOnceVerbs.length != 0, true, initialContext),
         true,
         null,
         768
      );
      this.setDefaultClose(false);
      this._useOnceVerbs = useOnceVerbs;
      if (this._useOnceVerbs != null) {
         if (this._useOnceVerbs.length == 0) {
            this._useOnceVerbs = null;
         } else {
            this._useOnceVerbsWrapped = new Object[this._useOnceVerbs.length];

            for (int lv = this._useOnceVerbs.length - 1; lv >= 0; lv--) {
               this._useOnceVerbsWrapped[lv] = new AddressBookListUI$CustomActionVerb(this, this._useOnceVerbs[lv]);
            }
         }
      }

      String initialSearchPattern = (String)ContextObject.get(initialContext, 253);
      this.performCommonInitialization(0, initialSearchPattern, initialContext);
      this.setHelp("contacts");
      this.getFinderField().acceptYomiSearch(true);
   }

   AddressBookListUI(AddressSelectionContext selectionContext, SelectionListener selectionListener) {
      super(
         selectionContext.getListScreenTitle() == null ? AddressBookResources.getString(300) : selectionContext.getListScreenTitle(),
         selectionContext.getFindLabel(),
         AddressBookListField.getInstance(selectionContext.getUseOnceVerbs() != null, true, selectionContext.getContext()),
         false,
         selectionContext.getContext(),
         768
      );
      this.setDefaultClose(false);
      this._selectionListener = selectionListener;
      this._selectionContext = selectionContext;
      Verb[] useOnceVerbs = selectionContext.getUseOnceVerbs();
      if (useOnceVerbs != null) {
         this._useOnceVerbs = useOnceVerbs;
         this._useOnceVerbsWrapped = new Object[this._useOnceVerbs.length];

         for (int lv = this._useOnceVerbs.length - 1; lv >= 0; lv--) {
            this._useOnceVerbsWrapped[lv] = new AddressBookListUI$CustomActionVerb(this, this._useOnceVerbs[lv]);
         }
      }

      this.performCommonInitialization(1, selectionContext.getInitialSearchPattern(), selectionContext.getContext());
      this.getFinderField().acceptYomiSearch(true);
   }

   private final void performCommonInitialization(int mode, String initialSearchPattern, Object initialContext) {
      this._listField = (AddressBookListField)this.getListField();
      this.addAsListener();
      this._mode = mode;
      this.initializeContexts(initialContext);
      this._terminateOnEsc = ContextObject.getFlag(initialContext, 14);
      this._listField.setFocus();
      Field oldTitleField = this.getTitleField();
      boolean killManagerTag = false;
      if (oldTitleField != null && oldTitleField.getManager() != null && oldTitleField.getManager().getTag() == null) {
         killManagerTag = true;
      }

      this.setTitleField(null);
      this._titleField = (FilteredByCategoriesTitleField)(new Object(oldTitleField));
      this.setTitleField(this._titleField);
      if (killManagerTag) {
         this._titleField.getManager().setTag(null);
      }

      this.initializeFilterCategoryIds();
      FilterByCategoriesVerb.filterByCategories(this, this._titleField, this._filterCategoryIds);
      this.setSearchPattern(initialSearchPattern);
      this._listField.getKeywordFilterList().waitForComplete();
      this.setElementWithFocus(getLastAddressWrapper().get());
   }

   private final void initializeFilterCategoryIds() {
      this._persistentFilterObject = RIMPersistentStore.getPersistentObject(1739655200517691992L);
      synchronized (this._persistentFilterObject) {
         this._filterCategoryIds = (int[])this._persistentFilterObject.getContents();
         if (this._filterCategoryIds == null) {
            this._filterCategoryIds = new int[0];
            this._persistentFilterObject.setContents(this._filterCategoryIds, 51, false);
            this._persistentFilterObject.commit();
         }
      }
   }

   private static final AddressBookListUI$LastAddressWrapper getLastAddressWrapper() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      AddressBookListUI$LastAddressWrapper lastAddress = (AddressBookListUI$LastAddressWrapper)ar.getOrWaitFor(6628691903329633640L);
      if (lastAddress == null) {
         lastAddress = new AddressBookListUI$LastAddressWrapper();
         ar.put(6628691903329633640L, lastAddress);
      }

      return lastAddress;
   }

   public static final void setLastSelectedAddress(Object lastSelectedAddress) {
      if (lastSelectedAddress != null) {
         getLastAddressWrapper().set(lastSelectedAddress);
      }
   }

   private final void optionsUpdated(boolean force) {
      AddressBookOptions options = AddressBookServices.getAddressBookOptions();
      long sortOrder = options.getSortOrder();
      if (force || this._listField.getSortOrder() != sortOrder) {
         this.showListReorganizationDialog();
         KeywordFilterList list = this._listField.getKeywordFilterList();
         this._listField.setSortOrder(sortOrder);
         if (list != this._listField.getKeywordFilterList()) {
            String suffix = list.getSuffix();
            if (suffix != null) {
               this._listField.getKeywordFilterList().setSuffix(suffix);
            }
         }

         this.setSearchPattern(this.getSearchPattern());
         this.hideListReorganizationDialog();
      }

      this._listField.setListSeparatorAppearance(options.getListSeparatorAppearance());
   }

   private final void initializeContexts(Object baseContext) {
      this._baseContext = ContextObject.castOrCreate(baseContext);
      this.setContext(this._baseContext);
      this._trackwheelclickContext = this._baseContext.clone();
      this._trackwheelclickContext.setFlag(3, 2, 18);
   }

   private final void terminateScreen() {
      UiApplication uiApp = UiApplication.getUiApplication();
      uiApp.popScreen(this);
      this._persistentFilterObject.commit();
      if (uiApp.getActiveScreen() == null) {
         System.exit(0);
      }
   }

   public static final AddressBookListUI getInstance(String title) {
      return getInstance(title, null, null);
   }

   public static final AddressBookListUI getInstance(String title, Verb[] useOnceVerbs, Object initialContext) {
      return new AddressBookListUI(title, useOnceVerbs, initialContext);
   }

   private final void addAsListener() {
      Application.getApplication().addGlobalEventListener(this);
      AddressBookOptionsImpl.getOptions().addOptionsChangeListener(this);
   }

   private final void removeAsListener() {
      Application.getApplication().removeGlobalEventListener(this);
      AddressBookOptionsImpl.getOptions().removeOptionsChangeListener(this);
   }

   @Override
   protected final boolean handleEndKey() {
      if (this._terminateOnEsc) {
         this.terminateScreen();
      }

      return false;
   }

   @Override
   protected final boolean handleSendKey() {
      if (super.handleSendKey()) {
         return true;
      }

      Object defaultMenuItem = this.getDefaultMenuItem(0);
      if (defaultMenuItem instanceof Object) {
         VerbMenuItem vmi = (VerbMenuItem)defaultMenuItem;
         if (vmi.getVerb() instanceof Object && this.invokeDefaultMenuItem(0)) {
            return true;
         }
      }

      Object item = this.getSelectedElement();
      ContextObject resultContext = (ContextObject)(new Object());
      if (ControllerUtilities.invokeSendKeyVerb(item, resultContext)) {
         if (ContextObject.getFlag(resultContext, 39)) {
            this.terminateScreen();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      long currentTime = System.currentTimeMillis();
      if (Keypad.key(keycode) == 8) {
         if (!this.isSearchStringEmpty()) {
            this._lastBackspaceTime = currentTime;
         } else {
            if (currentTime - this._lastBackspaceTime < 300) {
               this._lastBackspaceTime = currentTime;
               return 65536;
            }

            this._lastBackspaceTime = currentTime;
         }
      } else {
         this._lastBackspaceTime = 0;
      }

      return super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      return Keypad.key(keycode) == 21 && ControllerUtilities.invokeApplicationKeyVerb(this.getSelectedElement()) ? true : super.keyDown(keycode, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      int selectedIndex = this._listField.getSelectedIndex();
      switch (key) {
         case '\b':
            if (!this.isSearchStringEmpty()) {
               break;
            }
         case '\u007f':
            this.getKeywordFilterList().waitForComplete();
            Object selectedElement = this.getSelectedElement();
            if (selectedElement != null) {
               Verb deleteItem = new DeleteEntryVerb(selectedElement, key);
               deleteItem.invoke(null);
            }

            this.setElementWithFocus(this._listField.getElementAtIncludingLookup(selectedIndex));
            return true;
         case '\n':
            this.invokeAction(1);
            this.setElementWithFocus(this._listField.getElementAtIncludingLookup(selectedIndex));
            return true;
         case '\u001b':
            if (!this.isSearchStringEmpty() && !this._terminateOnEsc) {
               this.setSearchPattern(null);
               return true;
            }

            this.terminateScreen();
            return true;
      }

      return super.keyChar(key, status, time);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void doVerbAction(Verb verb) {
      InputContext.getInstance().endComposition();
      String pattern = this.getSearchPattern();
      int selectedIndex = this._listField.getSelectedIndex();
      int useOnceVerbIndex = -1;
      if (this._useOnceVerbs != null) {
         for (int i = this._useOnceVerbs.length - 1; i >= 0; i--) {
            if (verb == this._useOnceVerbs[i]) {
               useOnceVerbIndex = i;
               break;
            }
         }
      }

      Object lastStringData = this._baseContext.get(253);
      if (pattern == null || useOnceVerbIndex < 0 && !(verb instanceof AddressBookListUI$NewAddressBookEntryVerb) && verb.getVerbGroupId() != 7201896) {
         this._baseContext.remove(253);
      } else {
         this._baseContext.put(253, pattern);
      }

      Object result = null;
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         Object o = this._listField.getSelectedElement();
         if (o != null) {
            this._baseContext.put(3696141428889703675L, o);
         }

         result = verb.invoke(this._baseContext);
         var10 = false;
      } finally {
         if (var10) {
            this._baseContext.remove(3696141428889703675L);
         }
      }

      this._baseContext.remove(3696141428889703675L);
      if (lastStringData != null) {
         this._baseContext.put(253, lastStringData);
      } else {
         this._baseContext.remove(253);
      }

      if (ContextObject.getFlag(result, 39) && !ContextObject.getFlag(result, 40)) {
         this.terminateScreen();
      }

      if (useOnceVerbIndex >= 0 && result != null) {
         if (this._selectionListener != null && this._selectionListener.select(result, useOnceVerbIndex)) {
            UiApplication.getUiApplication().popScreen(this);
         }
      } else if (!(verb instanceof Object) && this._selectionListener != null && this._selectionListener.hasSelectedObject()) {
         UiApplication.getUiApplication().popScreen(this);
      } else {
         if (!this._listField.isEmpty()) {
            this._listField.setFocus();
         }

         if (verb instanceof AddressBookListUI$NewAddressBookEntryVerb && result != null) {
            this.setElementWithFocus(result);
            pattern = null;
         } else if (!(result instanceof Object)) {
            if (result instanceof Object) {
               this.setElementWithFocus(result);
               pattern = null;
            } else {
               this.setElementWithFocus(this._listField.getElementAtIncludingLookup(selectedIndex));
            }
         }

         if (!(verb instanceof AddressBookListUI$SimpleVerb) || ((AddressBookListUI$SimpleVerb)verb).getType() != 4) {
            this.setSearchPattern(pattern);
         }

         this._listField.setSize(this._listField.getSize() - this._listField.getExtraRowCount());
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (!this._listField.isUseOnceIndex(this._listField.getSelectedIndex())) {
                  return this.invokeDefaultMenuItem(0);
               }

               Verb useOnceVerb = null;
               if (this._selectionContext != null) {
                  useOnceVerb = this._useOnceVerbsWrapped[this._selectionContext.getPreferredDefaultIndex()];
               } else {
                  Menu menu = this.getMenu(0);
                  if (menu instanceof Object) {
                     useOnceVerb = ((SystemEnabledMenu)menu).getFirstVerbAt(327680);
                  }

                  if (useOnceVerb == null || useOnceVerb.getOrdering() > 367001) {
                     useOnceVerb = this._useOnceVerbsWrapped[0];
                  }
               }

               useOnceVerb.invoke(null);
               return true;
         }
      }

      return handled;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      this.getKeywordFilterList().waitForComplete();
      Object selectedElement = this.getSelectedElement();
      int index = this._listField.getSelectedIndex();
      Verb defaultVerb = null;
      Verb tmpDefaultVerb = null;
      RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(-7921492803965144520L);
      int priority = Integer.MAX_VALUE;

      for (int i = 0; i < factories.length; i++) {
         Verb[] factoryVerbs = factories[i].getVerbs(this._trackwheelclickContext);

         for (int j = 0; j < factoryVerbs.length; j++) {
            Verb verb = new AddressBookListUI$CustomActionVerb(this, new AddressBookListUI$NewAddressBookEntryVerb(this, factoryVerbs[j]));
            if (selectedElement == null && this._mode == 0 && verb.getOrdering() < priority) {
               defaultVerb = verb;
               priority = verb.getOrdering();
            }

            menu.add(verb);
         }
      }

      if (this._listField.isUseOnceIndex(index)) {
         menu.add(this._useOnceVerbsWrapped);
         if (this._selectionContext != null) {
            Verb verb = this._useOnceVerbsWrapped[this._selectionContext.getPreferredDefaultIndex()];
            if (verb != null && verb.getOrdering() < priority) {
               defaultVerb = verb;
               priority = verb.getOrdering();
            }
         }
      } else if (selectedElement != null) {
         if (!ContextObject.getFlag(this._baseContext, 108) && instance != 65536) {
            menu.add(new DeleteEntryVerb(selectedElement));
         }

         if (selectedElement instanceof Object) {
            VerbProvider verbProvider = (VerbProvider)selectedElement;
            boolean oldBackgroundFlag = this._trackwheelclickContext.getFlag(96);
            this._trackwheelclickContext.setFlag(96);

            label348:
            try {
               if (selectedElement instanceof Object) {
                  ContextObject.put(this._trackwheelclickContext, -7302237785847991426L, this._selectionListener);
               }
            } finally {
               break label348;
            }

            tmpDefaultVerb = verbProvider.getVerbs(this._trackwheelclickContext, this._tmpVerbs);
            ContextObject.remove(this._trackwheelclickContext, -7302237785847991426L);
            if (!oldBackgroundFlag) {
               this._trackwheelclickContext.clearFlag(96);
            }

            for (int i = 0; i < this._tmpVerbs.length; i++) {
               Verb tmpVerb = this._tmpVerbs[i];
               if (tmpVerb instanceof Object) {
                  this._tmpVerbs[i] = new AddressBookListUI$CustomActionVerb(this, tmpVerb);
               }

               if (tmpVerb == tmpDefaultVerb) {
                  tmpDefaultVerb = this._tmpVerbs[i];
               }
            }
         }
      }

      label340:
      if (this._mode == 1) {
         if (selectedElement instanceof Object) {
            RequestModel requestModel = (RequestModel)selectedElement;
            Request request = requestModel.fetchRequest();
            if (request != null && !requestModel.isResolved() && requestModel.numberAvailableForResolution() != 1) {
               break label340;
            }
         }

         if (!this._listField.isUseOnceIndex(index)) {
            Object element = selectedElement;
            if (element instanceof Object) {
               ResolvedStatusProvider resolvedStatus = (ResolvedStatusProvider)element;
               if (resolvedStatus.isResolved()) {
                  element = resolvedStatus.getResolvedItem();
               }
            }

            if (this._selectionListener.canSelect(element) && this._trackwheelclickContext.getFlag(5)) {
               Verb[] verbs = new Object[0];
               Verb verb = this._selectionListener.getVerbs(verbs, element, this._trackwheelclickContext);
               if (verb != null && verb.getOrdering() < priority) {
                  defaultVerb = verb;
                  priority = verb.getOrdering();
               }

               menu.add(verbs);
            }
         }
      }

      if ((defaultVerb == null || defaultVerb instanceof AddressBookListUI$NewAddressBookEntryVerb) && tmpDefaultVerb != null) {
         defaultVerb = tmpDefaultVerb;
      }

      menu.add(this._tmpVerbs);
      menu.add(new AddressBookListUI$SimpleVerb(this, 16986368, CommonResource.getBundle(), 20, 1));
      int finderTextLength = this.getFinderField().getTextLength();
      if (finderTextLength != 0) {
         menu.add(new AddressBookListUI$SimpleVerb(this, 479488, AddressBookResources.getResourceBundleFamily(), 201, 4));
      }

      if (ALPConfiguration.isActive() && !this._baseContext.getFlag(108)) {
         Verb verb = new AddressBookListUI$CustomActionVerb(
            this, new AddressBookListUI$SimpleVerb(this, 413952, AddressBookResources.getResourceBundleFamily(), 204, 2)
         );
         menu.add(verb);
         boolean lookupPreferred = AddressBookServices.getAddressBookOptions().getComposePreference() == 2;
         if (finderTextLength != 0 && selectedElement == null && (!this._listField.isUseOnceIndex(index) || lookupPreferred)) {
            defaultVerb = verb;
            priority = 0;
         }
      }

      VerbRepository verbRepository = VerbRepository.getVerbRepository(5911208747185054768L);
      menu.add(verbRepository.getVerbs(this._trackwheelclickContext));
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-5325383713183591786L);
      if (verbFactories != null && verbFactories.length > 0) {
         ContextObject verbFactoryContext = ContextObject.clone(this._trackwheelclickContext);
         if (selectedElement != null) {
            verbFactoryContext.put(252, selectedElement);
         }

         if (this._selectionContext != null) {
            verbFactoryContext.put(-2045338148608603372L, this._selectionContext);
         }

         for (int i = verbFactories.length - 1; i >= 0; i--) {
            Verb[] verbs = verbFactories[i].getVerbs(verbFactoryContext);
            if (verbs != null) {
               for (int j = 0; j < verbs.length; j++) {
                  if (verbs[j] != null) {
                     menu.add(new AddressBookListUI$CustomActionVerb(this, verbs[j]));
                  }
               }
            }
         }
      }

      DefaultVerbProvider defaultVerbProvider = null;
      if (selectedElement instanceof Object) {
         defaultVerbProvider = (DefaultVerbProvider)(new Object((RIMModel)selectedElement));
      }

      Array.resize(this._tmpVerbs, 0);
      menu.setDefault(defaultVerb);
      menu.coalesce(-3072555018635390988L, defaultVerbProvider);
      defaultVerb = menu.getDefaultVerb();
      boolean defaultVerbIsContinuationVerb = false;
      if (defaultVerb != null) {
         int defaultVerbOrder = defaultVerb.getOrdering();
         defaultVerbIsContinuationVerb = defaultVerbOrder >= 327680 && defaultVerbOrder <= 367001;
      }

      Verb continuationVerb = menu.getFirstVerbAt(327680);
      if (!defaultVerbIsContinuationVerb && continuationVerb != null && continuationVerb.getOrdering() <= 367001) {
         if (!(selectedElement instanceof Object)) {
            if (continuationVerb.getOrdering() < priority) {
               defaultVerb = continuationVerb;
               priority = continuationVerb.getOrdering();
            }
         } else {
            RequestModel requestModel = (RequestModel)selectedElement;
            if (requestModel.isResolved()) {
               defaultVerb = continuationVerb;
            }
         }
      }

      Verb verb;
      if (UiApplication.getUiApplication().getScreenCount() == 1) {
         verb = new AddressBookListUI$CloseApplicationVerb(this);
         menu.add(verb);
      } else {
         verb = new AddressBookListUI$SimpleVerb(this, 268500992, CommonResource.getBundle(), 19, 3);
         menu.add(verb);
      }

      if (defaultVerb == null) {
         defaultVerb = verb;
      }

      menu.add((Verb)(new Object(this, this._titleField, this._filterCategoryIds)));
      menu.setDefault(defaultVerb);
   }

   @Override
   protected final void onExposed() {
      super.onExposed();
      BlackBerryAddressBook addressBook = BlackBerryAddressBook.getAddressBook();
      boolean busyFlag = addressBook.isBusy();
      if (busyFlag) {
         this.showListReorganizationDialog();
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (!attached) {
         setLastSelectedAddress(this.getSelectedElement());
         this.removeAsListener();
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      ContextObject co = this.getContext();
      co = ContextObject.castOrCreate(co);
      Object element = this.getSelectedElement();
      if (element != null) {
         if (!(element instanceof Object)) {
            ContextObject.put(co, 3696141428889703675L, element);
         } else {
            Request r = ((RequestModel)element).fetchRequest();
            if (r != null) {
               RIMModel model = r.getSelectedAddress();
               if (model != null) {
                  ContextObject.put(co, 3696141428889703675L, model);
                  return co;
               }
            }
         }
      }

      return co;
   }

   private final void showListReorganizationDialog() {
      this._reorgScreen = (Screen)(new Object((Manager)(new Object())));
      DialogFieldManager manager = (DialogFieldManager)this._reorgScreen.getDelegate();
      manager.setMessage((RichTextField)(new Object(AddressBookResources.getString(52), 36028797018963968L)));
      manager.setIcon((BitmapField)(new Object(Bitmap.getPredefinedBitmap(3), 51539607552L)));
      synchronized (this) {
         this._updatingUIApplication = UiApplication.getUiApplication();
         this._updatingUIApplication.pushScreen(this._reorgScreen);
         this._reorgScreen.doPaint();
         this._reorgScreen.updateDisplay();
      }
   }

   private final void hideListReorganizationDialog() {
      synchronized (this) {
         if (this._updatingUIApplication != null) {
            this._updatingUIApplication.popScreen(this._reorgScreen);
            this._updatingUIApplication = null;
            this._reorgScreen = null;
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -3950819934062689467L) {
         this.optionsUpdated(false);
      } else {
         if (guid == 1381575293511996494L) {
            if (UiApplication.getUiApplication().getActiveScreen() == this) {
               this.showListReorganizationDialog();
               return;
            }
         } else {
            if (guid == -992760093076435005L) {
               this.hideListReorganizationDialog();
               return;
            }

            if (guid == -6376745458772725637L
               && object0 instanceof Object
               && this._listField.getElementWithFocus() == object0
               && UiApplication.getUiApplication().getActiveScreen() == this) {
               RequestModel requestModel = (RequestModel)object0;
               if (this._selectionListener != null) {
                  ContextObject.put(this._trackwheelclickContext, -7302237785847991426L, this._selectionListener);
               }

               Verb[] verbs = new Object[0];
               Verb defaultVerb = requestModel.getVerbs(this._trackwheelclickContext, verbs);
               if (this._selectionListener != null) {
                  ContextObject.remove(this._trackwheelclickContext, -7302237785847991426L);
               }

               if (defaultVerb != null) {
                  defaultVerb.invoke(this._baseContext);
               }
            }
         }
      }
   }

   @Override
   protected final boolean openProductionBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1380076612:
            this.showListReorganizationDialog();
            BlackBerryAddressBook.getAddressBook().getCollection().forceRebuildTables();
            this.setSearchPattern(null);
            this.hideListReorganizationDialog();
            return true;
         case 1447119940:
            this.showListReorganizationDialog();
            BlackBerryAddressBook.getAddressBook().getCollection().validateTables();
            this.setSearchPattern(null);
            this.hideListReorganizationDialog();
            return true;
         default:
            return super.openProductionBackdoor(backdoorCode);
      }
   }

   @Override
   public final void optionsChanged(int changedOptions) {
      if ((changedOptions & (AddressBookOptionsImpl.SORT_ORDER | AddressBookOptionsImpl.LIST_SEPARATOR_APPEARANCE)) != 0) {
         this.optionsUpdated(false);
      }
   }
}
