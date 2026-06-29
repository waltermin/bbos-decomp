package net.rim.device.apps.internal.globalsearch;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.FilterProgress;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.FilterProgressListener;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.InvokeLaterRunnable;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.vm.WeakReference;

final class GlobalSearchResultsScreen extends AppsMainScreen implements CollectionListener, FilterProgressListener {
   private byte _status;
   private LabelField _resultsStatusLabel = new LabelField(CommonResources.getString(9139));
   private VerticalFieldManager _vfm = new VerticalFieldManager();
   private SearchResultCollection[] _results;
   private int _numResults;
   private ContextObject _menuContext;
   private Application _application;
   private GlobalSearchResultsScreen$UpdaterRunnable _updaterRunnable;
   private InvokeLaterRunnable _invokeLaterRunnable;
   private boolean _displayIcons;
   private static ResourceBundle _rb = ResourceBundle.getBundle(1181520400124563677L, "net.rim.device.apps.internal.resource.GlobalSearch");
   private static StringBuffer _sb = new StringBuffer();
   private static final int SEARCH_INITIALIZING = 0;
   private static final int SEARCH_IN_PROGRESS = 1;
   private static final int SEARCH_FINISHED = 2;
   private static final int SEARCH_CANCELLED = 3;

   GlobalSearchResultsScreen(boolean displayIcons) {
      super(65536);
      this._displayIcons = displayIcons;
      this._menuContext = new ContextObject(78, 2, 86);
      this._menuContext.setFlag(3);
      this._updaterRunnable = new GlobalSearchResultsScreen$UpdaterRunnable(this);
      this._application = Application.getApplication();
      this._invokeLaterRunnable = new InvokeLaterRunnable();
      this.setTitle(CommonResources.getString(9143));
      this.add(this._resultsStatusLabel);
      this.add(new SeparatorField());
      this.add(this._vfm);
   }

   final boolean performSearch(SearchableWrapper[] searchableWrappers, SearchCriterion[] sc) {
      this._status = 0;
      WeakReference listener = new WeakReference(this);
      this._numResults = searchableWrappers.length;
      this._results = new SearchResultCollection[this._numResults];
      this._vfm.deleteAll();

      for (int i = this._numResults - 1; i >= 0; i--) {
         long id = searchableWrappers[i].getId();
         Searchable s = GlobalSearchRegistry.getSearchable(id);
         this._results[i] = s.search(searchableWrappers[i].getSubComponents(), sc);
         this._results[i].addCollectionListener(listener);
         this._results[i].setFilterProgressListener(listener);
         SearchResultsFieldManager sRFM = new SearchResultsFieldManager(s, id, this._results[i], this._displayIcons);
         this._vfm.add(sRFM);
      }

      if (this._status == 0) {
         this._status = 1;
      }

      this.updateStatus();
      return true;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      Verb defaultVerb = null;
      Field leafWithFocus = this.getLeafFieldWithFocus();
      Verb[] fieldVerbs = new Verb[0];
      if (leafWithFocus instanceof VerbProvider) {
         ContextObject context = ContextObject.castOrCreate(this.getMenuContextObject());
         if (instance == 65536) {
            context.setFlag(81);
         }

         VerbProvider vp = (VerbProvider)leafWithFocus;
         defaultVerb = vp.getVerbs(context, fieldVerbs);
      }

      Object selectedObject = null;
      if (leafWithFocus instanceof SearchResultField) {
         selectedObject = ((SearchResultField)leafWithFocus).getSelectedObject();
      }

      if (selectedObject != null) {
         this._menuContext.put(250, selectedObject);
         this._menuContext.put(3696141428889703675L, selectedObject);
      } else {
         this._menuContext.remove(250);
         this._menuContext.remove(3696141428889703675L);
      }

      Field fieldWithFocus = this._vfm.getFieldWithFocus();
      if (fieldWithFocus instanceof SearchResultsFieldManager && instance == 0) {
         Verb expandOrCollapseVerb = new GlobalSearchResultsScreen$ExpandAndCollapseVerb((SearchResultsFieldManager)fieldWithFocus);
         Arrays.add(fieldVerbs, expandOrCollapseVerb);
         if (defaultVerb == null) {
            defaultVerb = expandOrCollapseVerb;
         }
      }

      if (instance == 0) {
         if (this._status < 2) {
            Arrays.add(fieldVerbs, new GlobalSearchResultsScreen$StopSearchVerb(this));
         }

         Verb backVerb = new GlobalSearchResultsScreen$BackVerb(this);
         Arrays.add(fieldVerbs, backVerb);
         if (defaultVerb == null) {
            defaultVerb = backVerb;
         }
      }

      if (fieldVerbs.length > 0) {
         menu.add(fieldVerbs);
      }

      menu.setDefault(defaultVerb);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         if (this._status < 2) {
            this.stopSearch();
            return true;
         } else {
            this.cleanupSearches(true);
            return super.onClose();
         }
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final void verbInvoked(Verb verb, Object context, Object result) {
   }

   @Override
   public final boolean onClose() {
      this.cleanupSearches(true);
      super.onClose();
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      return activeScreen instanceof GlobalSearchScreen ? activeScreen.onClose() : true;
   }

   private final void stopSearch() {
      this.cleanupSearches(false);
      if (this._status < 2) {
         this._status = 3;
      }

      this.updateStatus();
   }

   private final void cleanupSearches(boolean cleanup) {
      if (this._results != null) {
         for (int i = this._results.length - 1; i >= 0; i--) {
            if (this._results[i] != null) {
               this._results[i].haltFiltering(cleanup);
               if (cleanup) {
                  this._numResults--;
                  this._results[i] = null;
               }
            }
         }

         if (cleanup) {
            this._results = null;
         }
      }
   }

   @Override
   public final void reset(Collection collection) {
      this.updateStatus();
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.updateStatus();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.updateStatus();
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.updateStatus();
   }

   private final void updateStatus() {
      synchronized (this._invokeLaterRunnable) {
         this._invokeLaterRunnable.setRunnable(this._updaterRunnable);
         if (this._invokeLaterRunnable.doneProcessing()) {
            this._invokeLaterRunnable.resetDoneProcessing();
            this._application.invokeLater(this._invokeLaterRunnable);
         } else {
            this._invokeLaterRunnable.resetDoneProcessing();
         }
      }
   }

   private final void doUpdateStatus() {
      int totalInitialSizeOfSourceCollections = 0;
      int totalProgress = 0;
      int matchCount = 0;

      for (int i = this._numResults - 1; i >= 0; i--) {
         SearchResultCollection src = this._results[i];
         if (src == null) {
            return;
         }

         totalInitialSizeOfSourceCollections += src.getInitialSizeOfSourceCollection();
         totalProgress += src.getProgress(false);
         matchCount += src.size();
      }

      if (totalInitialSizeOfSourceCollections != 0) {
         totalProgress = totalProgress * 100 / totalInitialSizeOfSourceCollections;
      } else {
         totalProgress = 100;
      }

      boolean displayMatchCount = false;
      int statusResourceId;
      if (totalProgress < 100) {
         if (this._status == 3) {
            statusResourceId = 7;
         } else {
            statusResourceId = 6;
         }

         displayMatchCount = false;
      } else {
         this._status = 2;
         statusResourceId = 8;
         displayMatchCount = true;
      }

      StringBuffer sb = _sb;
      sb.setLength(0);
      sb.append(_rb.getString(statusResourceId));
      sb.append(' ');
      sb.append('(');
      if (displayMatchCount) {
         sb.append(matchCount);
      } else {
         sb.append(totalProgress);
         sb.append('%');
      }

      sb.append(')');
      this._resultsStatusLabel.setText(_sb);
   }

   @Override
   protected final ContextObject getMenuContextObject() {
      return this._menuContext;
   }

   @Override
   public final void progressUpdated(FilterProgress filterProgress) {
      this.updateStatus();
   }

   static final boolean access$201(GlobalSearchResultsScreen x0) {
      return x0.onClose();
   }
}
