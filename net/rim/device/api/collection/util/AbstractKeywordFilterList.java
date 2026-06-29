package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.FilterCollection;
import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.util.StringUtilities;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;

public class AbstractKeywordFilterList
   implements KeywordFilterList,
   FilterCollection,
   CollectionEventSource,
   ReadableList,
   PersistentContentListener,
   AccessibleContext {
   private CollectionListenerManager _listeners;
   protected ReadableList _source;
   private KeywordPrefixCache _prefixCache;
   private Object _filterCriteria;
   private String _filterSuffix;
   private Object[] _filteredElements;
   protected KeywordPrefixSearchResult _filterResult;
   private KeywordSearcher _searcher;
   private Object _searchRequestLock;
   private AbstractKeywordFilterList$SearchRequest _currentSearchRequest;
   private AbstractKeywordFilterList$SearchRequest _nextSearchRequest;
   private int _accessibleStateSet = 1;

   protected void clearPrefixCache() {
      if (this._prefixCache != null) {
         this._prefixCache.reset();
      }
   }

   protected void reset() {
      this.clearPrefixCache();
   }

   protected KeywordPrefixCache getPrefixCache() {
      return this._prefixCache;
   }

   protected void setSearcher(KeywordSearcher searcher) {
      this._searcher = searcher;
   }

   protected synchronized void filteringComplete() {
      this.notifyAll();
   }

   public boolean isInProgress() {
      synchronized (this._searchRequestLock) {
         return !this._currentSearchRequest.isEmpty();
      }
   }

   protected void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   protected void removeAccessibleState(int state) {
      this._accessibleStateSet &= ~state;
   }

   protected void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleStateSet |= state;
   }

   protected void fireReset() {
      this._listeners.fireReset(this);
   }

   protected void fireElementAdded(Object element) {
      this._listeners.fireElementAdded(this, element);
   }

   protected void fireElementUpdated(Object oldElement, Object newElement) {
      this._listeners.fireElementUpdated(this, oldElement, newElement);
   }

   protected void fireElementRemoved(Object element) {
      this._listeners.fireElementRemoved(this, element);
   }

   protected void haltSearch() {
      throw null;
   }

   public void halt() {
      this._searcher.halt();
   }

   public KeywordPrefixSearchResult search(String[] _1) {
      throw null;
   }

   public Object[] getElements(KeywordPrefixSearchResult _1) {
      throw null;
   }

   protected synchronized void resetFilterResults() {
      this.clearFilteredElementList();
      this._filterCriteria = null;
      this._filterResult = null;
   }

   void setFilterResult(Object words, KeywordPrefixSearchResult result) {
      synchronized (this) {
         this.resetFilterResults();
         this._filterCriteria = words;
         if (result != null && (result.getPrimaryMatches() == null || result.getSecondaryMatches() == null)) {
            result = null;
         }

         this._filterResult = result;
      }

      this.fireReset();
   }

   protected synchronized void recalculateResults() {
      this.clearFilteredElementList();
      this.buildFilteredElementList();
   }

   protected void clearFilteredElementList() {
      this._filteredElements = null;
   }

   @Override
   public int getIndex(Object element) {
      this.buildFilteredElementList();
      synchronized (this) {
         if (this._filteredElements != null) {
            int count = this._filteredElements.length;

            for (int i = 0; i < count; i++) {
               if (this._filteredElements[i] == element) {
                  return i;
               }
            }

            return -1;
         }
      }

      return this._source.getIndex(element);
   }

   @Override
   public void setSuffix(String suffix) {
      synchronized (this) {
         if (!StringUtilities.strEqual(this._filterSuffix, suffix)) {
            this._filterSuffix = suffix;
            this.setCriteria(this._filterCriteria, null);
            this.waitForComplete();
         }
      }
   }

   @Override
   public String getSuffix() {
      return this._filterSuffix;
   }

   @Override
   public KeywordSearcher getSearcher() {
      return this._searcher;
   }

   @Override
   public synchronized void searchPrefixes(String[] prefixes) {
      KeywordSearcher searcher = this.getSearcher();
      if (searcher != null) {
         searcher.searchPrefixes(prefixes);
      }
   }

   @Override
   public boolean matches(Object _1) {
      throw null;
   }

   @Override
   public void reset(Collection _1) {
      throw null;
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      if (count >= 0 && index >= 0 && destIndex >= 0) {
         this.buildFilteredElementList();
         synchronized (this) {
            if (this._filteredElements != null) {
               if (index + count > this._filteredElements.length) {
                  count = this._filteredElements.length - index;
               }

               if (elements.length < count + destIndex) {
                  Array.resize(elements, count + destIndex);
               }

               System.arraycopy(this._filteredElements, index, elements, destIndex, count);
               return count;
            }
         }

         return this._source.getAt(index, count, elements, destIndex);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public Object getAt(int index) {
      this.buildFilteredElementList();
      synchronized (this) {
         if (this._filteredElements != null) {
            return this._filteredElements[index];
         }
      }

      return this._source.getAt(index);
   }

   @Override
   public int size() {
      this.buildFilteredElementList();
      synchronized (this) {
         if (this._filteredElements != null) {
            return this._filteredElements.length;
         }
      }

      return this._source.size();
   }

   @Override
   public void setCriteria(Object criteria, FilterStatusListener listener) {
      if (criteria != null && !(criteria instanceof String) && !(criteria instanceof String[]) && !(criteria instanceof String[][])) {
         throw new IllegalArgumentException();
      }

      synchronized (this) {
         if (criteria == null && !this.isInProgress() && this.getSuffix() == null) {
            if (listener != null) {
               listener.filterStarted();
            }

            this.setFilterResult(null, null);
            if (listener != null) {
               listener.filterDone(false);
            }
         } else {
            synchronized (this._searchRequestLock) {
               if (this._currentSearchRequest.isEmpty()) {
                  this._currentSearchRequest.setup(criteria, listener);
                  new AbstractKeywordFilterList$SearchThread(this, this._searcher).start();
               } else {
                  if (!this._nextSearchRequest.isEmpty()) {
                     this._nextSearchRequest._listener.filterStarted();
                     this._nextSearchRequest._listener.filterDone(true);
                  }

                  this._nextSearchRequest.setup(criteria, listener);
                  this._searcher.halt();
               }
            }
         }
      }
   }

   @Override
   public synchronized Object getCriteria() {
      return this._filterCriteria;
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 2) {
         this.resetFilterResults();
         this.reset();
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   @Override
   public String getAccessibleName() {
      return this.toString();
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public int getAccessibleStateSet() {
      return this._accessibleStateSet;
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (this._accessibleStateSet & state) != 0;
   }

   @Override
   public int getAccessibleRole() {
      return 26;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return null;
   }

   @Override
   public int getAccessibleChildCount() {
      return this._filteredElements != null ? this._filteredElements.length : 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      Object temp = this.getAt(index);
      return temp != null ? new AccessibleContextFactory(temp.toString()) : null;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return false;
   }

   @Override
   public synchronized void waitForComplete() {
      while (this.isInProgress()) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }
   }

   private void buildFilteredElementList() {
      this.removeAccessibleState(4194304);
      this.addAccessibleState(32);
      synchronized (this._source) {
         synchronized (this) {
            if (this._filteredElements == null) {
               if (this._source.size() == 0) {
                  this._filteredElements = new Object[0];
               } else if ((this._filterCriteria != null || this._filterSuffix != null) && this._filterResult != null) {
                  this._filteredElements = this.getElements(this._filterResult);
               } else {
                  this._filteredElements = null;
               }
            }
         }
      }

      this.removeAccessibleState(32);
      this.addAccessibleState(4194304);
   }

   protected AbstractKeywordFilterList(ReadableList source) {
      if (source == null) {
         throw new IllegalArgumentException();
      }

      this._source = source;
      this._listeners = new CollectionListenerManager();
      this._searchRequestLock = new Object();
      this._currentSearchRequest = new AbstractKeywordFilterList$SearchRequest();
      this._nextSearchRequest = new AbstractKeywordFilterList$SearchRequest();
      if (InputContext.getInstance(false).hasSureType()) {
         this._prefixCache = new KeywordPrefixCache();
      }

      PersistentContent.addWeakListener(this);
   }
}
