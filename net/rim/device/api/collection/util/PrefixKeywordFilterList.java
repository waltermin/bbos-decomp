package net.rim.device.api.collection.util;

import net.rim.device.api.collection.BulkUpdateCollectionListener;
import net.rim.device.api.collection.ChainableCollection;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.collection.CollectionWithVersion;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.Array;
import net.rim.vm.Memory;
import net.rim.vm.WeakReference;

public class PrefixKeywordFilterList extends AbstractKeywordFilterList implements ChainableCollection, CollectionWithVersion, BulkUpdateCollectionListener {
   private PrefixKeywordFilterListData _filterListData;
   protected SparseList _objectList;
   protected KeywordPrefixManager _prefixList;
   protected BigIntVector _orderList;
   protected KeywordIndexerHelper _keywordHelper;
   private boolean _firstWordBias;
   protected WeakReference _keywordsWR;
   private boolean _commitsSuspended;
   private static final int GROW_SIZE;
   protected static final int KEYWORDS_INITIAL_SIZE;

   public int getPrefixCount() {
      return this._prefixList.getPrefixCount();
   }

   protected boolean getCommitsSuspended() {
      return this._commitsSuspended;
   }

   public PrefixKeywordFilterListData getFilterListData() {
      return this._filterListData;
   }

   protected int addToIndex(int index, Object element) {
      int id = this._objectList.addAndGetIndex(element);
      String[] keywords = WeakReferenceUtilities.getStringArray(this._keywordsWR, 10);
      int count = this._keywordHelper.getKeywords(element, keywords);

      for (int i = 0; i < count; i++) {
         this._prefixList.addWords(id, keywords[i], i == 0);
      }

      this._orderList.insertElementAt(id, index);
      return id;
   }

   protected void commit(boolean afterReload) {
   }

   public boolean matches(Object object, String suffix) {
      boolean matches = false;
      String[] criteria = null;
      Object searchCriteria = this.getCriteria();
      if (searchCriteria instanceof String) {
         criteria = new String[]{(String)searchCriteria};
      } else if (searchCriteria instanceof String[]) {
         criteria = (String[])searchCriteria;
      }

      if (criteria == null && suffix == null) {
         return true;
      }

      int length = criteria != null ? criteria.length : 0;
      int i = 0;

      do {
         String string = criteria != null ? criteria[i] : null;
         if (string == null) {
            string = "";
         } else {
            string = string + ' ';
         }

         if (suffix != null) {
            string = string + suffix;
         }

         String[] words = StringUtilities.stringToKeywords(string);
         if (this._keywordHelper.checkForMatch(object, words)) {
            return true;
         }
      } while (++i < length);

      return matches;
   }

   @Override
   public void endBulkUpdate(Collection collection) {
      this._commitsSuspended = false;
      this.doCommit(collection, true);
   }

   @Override
   public void beginBulkUpdate(Collection collection) {
      this._commitsSuspended = true;
   }

   @Override
   public int getVersion() {
      return this._filterListData._version;
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      boolean fireEvent = false;

      try {
         synchronized (CollectionLock.getGlobalLock()) {
            synchronized (this) {
               this.clearPrefixCache();
               int index = super._source.getIndex(element);
               if (index != -1) {
                  this.addToIndex(index, element);
                  fireEvent = this.doAddCheck(element);
                  this.doCommit(collection, false);
               }
            }
         }

         if (fireEvent) {
            this.fireElementAdded(element);
            return;
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         this.reset(collection);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      boolean changed = false;

      try {
         synchronized (CollectionLock.getGlobalLock()) {
            synchronized (this) {
               this.clearPrefixCache();
               int oldId = this.removeFromIndex(oldElement);
               int index = super._source.getIndex(newElement);
               if (index != -1) {
                  this.addToIndex(index, newElement);
                  changed = this.doRemoveCheck(oldId, oldElement);
                  changed |= this.doAddCheck(newElement);
                  this.doCommit(collection, false);
               }
            }
         }

         if (changed) {
            this.fireElementUpdated(oldElement, newElement);
            return;
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         this.reset(collection);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      boolean fireEvent = false;
      synchronized (CollectionLock.getGlobalLock()) {
         synchronized (this) {
            this.clearPrefixCache();
            int oldId = this.removeFromIndex(element);
            fireEvent = this.doRemoveCheck(oldId, element);
            this.doCommit(collection, false);
         }
      }

      if (fireEvent) {
         this.fireElementRemoved(element);
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
      super.persistentContentStateChanged(state);
      if (state == 1) {
         this.reload(super._source);
      }
   }

   @Override
   protected void reset() {
      super.reset();
      this.resetFilterResults();
      this._objectList.removeAll();
      this._prefixList.reset();
      this._orderList = new BigIntVector(64);
      this._filterListData._orderList = this._orderList;
      this._filterListData._version = -1;
   }

   @Override
   protected void haltSearch() {
      this._prefixList.haltSearch();
   }

   private void reload(ReadableList source) {
      if (source instanceof CollectionWithVersion) {
         CollectionWithVersion versionedCollection = (CollectionWithVersion)source;
         if (source.size() == this._filterListData._orderList.size()
            && versionedCollection.getVersion() == this._filterListData._version
            && this._filterListData._version != 0) {
            return;
         }
      }

      this.reset();
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         this.commit(false);
      } else {
         LowMemoryManager.poll();
         synchronized (CollectionLock.getGlobalLock()) {
            int count = source.size();

            for (int i = 0; i < count; i++) {
               this.addToIndex(i, source.getAt(i));
            }
         }

         synchronized (RIMPersistentStore.getSynchObject()) {
            synchronized (this) {
               LowMemoryManager.poll();
               this._orderList.optimize();
               LowMemoryManager.poll();
               Memory.moveToFlash(this._orderList);
               LowMemoryManager.poll();
               this._prefixList.sort();
               LowMemoryManager.poll();
               Object criteria = this.getCriteria();
               if (criteria != null || this.getSuffix() != null) {
                  this.setCriteria(criteria, new PrefixKeywordFilterList$1(this));
                  this.waitForComplete();
               }

               LowMemoryManager.poll();
               this.doCommit(source, true);
               LowMemoryManager.poll();
            }
         }
      }
   }

   @Override
   public boolean matches(Object object) {
      return this.matches(object, null);
   }

   public PrefixKeywordFilterList(ReadableList source, KeywordIndexerHelper helper, PrefixKeywordFilterListData filterListData) {
      super(source);
      this.setSearcher(new KeywordSearcher(this));
      this._filterListData = filterListData;
      this._objectList = this._filterListData._objectList;
      this._prefixList = this._filterListData._prefixList;
      this._orderList = this._filterListData._orderList;
      this._firstWordBias = this._filterListData._firstWordBias;
      this._keywordsWR = new WeakReference(null);
      this._keywordHelper = helper;
      if (source instanceof CollectionEventSource) {
         CollectionEventSource collectionEventSource = (CollectionEventSource)source;
         WeakReference weak = new WeakReference(this);
         collectionEventSource.addCollectionListener(weak);
      }

      this.reload(source);
      PersistentContent.addWeakListener(this);
   }

   @Override
   public KeywordPrefixSearchResult search(String[] words) {
      KeywordPrefixSearchResult result = this._prefixList.search(words, this.getPrefixCache());
      KeywordSearcher searcher = this.getSearcher();
      String[] longWords = this._prefixList.getLongWords(words);
      if (result != null && longWords != null) {
         BitSet matchSet = result.getPrimaryMatches();

         for (int i = 0; i < 2; matchSet = result.getSecondaryMatches()) {
            int id = matchSet.getFirstSet();
            synchronized (CollectionLock.getGlobalLock()) {
               for (; !searcher._interrupted && id != -1; id = matchSet.getNextSet(id + 1)) {
                  if (!this._keywordHelper.checkForMatch(this._objectList.get(id), longWords)) {
                     matchSet.clear(id);
                  }
               }
            }

            i++;
         }
      }

      if (searcher._interrupted) {
         result = null;
      }

      return result;
   }

   @Override
   public Object[] getElements(KeywordPrefixSearchResult result) {
      synchronized (this) {
         Object[] matchElements = new Object[0];
         int dest = 0;
         Array.resize(matchElements, result.getMatchCount());
         if (this._firstWordBias) {
            BitSet primaryMatches = result.getPrimaryMatches();
            BitSet secondaryMatches = result.getSecondaryMatches();
            int count = primaryMatches.getNumSet();

            for (int src = 0; dest < count; src++) {
               int id = this._orderList.elementAt(src);
               if (primaryMatches.isSet(id)) {
                  matchElements[dest++] = this._objectList.get(id);
               }
            }

            count = dest + secondaryMatches.getNumSet();

            for (int var14 = 0; dest < count; var14++) {
               int id = this._orderList.elementAt(var14);
               if (secondaryMatches.isSet(id)) {
                  matchElements[dest++] = this._objectList.get(id);
               }
            }
         } else {
            BitSet matches = new BitSet(result.getPrimaryMatches());
            matches.or(result.getSecondaryMatches());
            int count = matches.getNumSet();

            for (int src = 0; dest < count; src++) {
               int id = this._orderList.elementAt(src);
               if (matches.isSet(id)) {
                  matchElements[dest++] = this._objectList.get(id);
               }
            }
         }

         Array.resize(matchElements, dest);
         return matchElements;
      }
   }

   public PrefixKeywordFilterList(ReadableList source, KeywordIndexerHelper helper, boolean firstWordBias) {
      this(source, helper, new PrefixKeywordFilterListData(new SparseList(), new KeywordPrefixManager(), new BigIntVector(64), firstWordBias, 0));
   }

   private boolean doAddCheck(Object element) {
      boolean changed = false;
      String[] criteria = null;
      KeywordPrefixSearchResult tmpResult = null;
      synchronized (this) {
         Object searchCriteria = this.getCriteria();
         if (searchCriteria instanceof String) {
            criteria = new String[]{(String)searchCriteria};
         } else if (searchCriteria instanceof String[]) {
            criteria = (String[])searchCriteria;
         }

         String suffix = this.getSuffix();
         if (super._filterResult != null && (criteria != null || suffix != null)) {
            int searches = criteria != null ? criteria.length : 0;
            int i = 0;

            do {
               String string = criteria != null ? criteria[i] : null;
               if (string == null) {
                  string = "";
               } else {
                  string = string + ' ';
               }

               if (suffix != null) {
                  string = string + suffix;
               }

               tmpResult = this.search(StringUtilities.stringToKeywords(string));
               super._filterResult.getPrimaryMatches().or(tmpResult.getPrimaryMatches());
               super._filterResult.getSecondaryMatches().or(tmpResult.getSecondaryMatches());
            } while (++i < searches);

            BitSet primaryMatches = super._filterResult.getPrimaryMatches();
            BitSet notPrimary = new BitSet(primaryMatches);
            notPrimary.not();
            BitSet secondaryMatches = super._filterResult.getSecondaryMatches();
            secondaryMatches.and(notPrimary);
            int id = this._objectList.getKey(element);
            if (primaryMatches.isSet(id) || secondaryMatches.isSet(id)) {
               changed = true;
            }
         } else {
            changed = true;
         }

         if (changed) {
            this.clearFilteredElementList();
         }

         return changed;
      }
   }

   private int removeFromIndex(Object element) {
      synchronized (this) {
         int id = this._objectList.getKey(element);
         if (id != -1) {
            this._objectList.removeAt(id);
            this._prefixList.delete(id);

            for (int i = this._orderList.size() - 1; i >= 0; i--) {
               if (this._orderList.elementAt(i) == id) {
                  this._orderList.removeElementAt(i);
                  break;
               }
            }
         }

         return id;
      }
   }

   private boolean doRemoveCheck(int id, Object element) {
      boolean changed = false;
      synchronized (this) {
         if (super._filterResult != null) {
            BitSet matches = super._filterResult.getPrimaryMatches();
            if (matches.isSet(id)) {
               changed = true;
               matches.clear(id);
            } else {
               matches = super._filterResult.getSecondaryMatches();
               if (matches.isSet(id)) {
                  changed = true;
                  matches.clear(id);
               }
            }
         } else {
            changed = true;
         }

         if (changed) {
            this.clearFilteredElementList();
         }

         return changed;
      }
   }

   @Override
   public void reset(Collection collection) {
      this.clearPrefixCache();
      if (!(collection instanceof ReadableList)) {
         this.reset();
         this.doCommit(null, true);
      } else {
         ReadableList r = (ReadableList)collection;
         this.reload(r);
      }

      this.fireReset();
   }

   public PrefixKeywordFilterList(ReadableList source, KeywordIndexerHelper helper) {
      this(source, helper, false);
   }

   private void doCommit(Collection collection, boolean afterReload) {
      if (!this._commitsSuspended) {
         if (collection instanceof CollectionWithVersion) {
            CollectionWithVersion collectionWithVersion = (CollectionWithVersion)collection;
            this._filterListData._version = collectionWithVersion.getVersion();
         }

         this.commit(afterReload);
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
      super.persistentContentModeChanged(generation);
      if (super._source instanceof CollectionWithVersion) {
         CollectionWithVersion versionedCollection = (CollectionWithVersion)super._source;
         if (super._source.size() == this._filterListData._orderList.size()
            && versionedCollection.getVersion() == this._filterListData._version
            && this._filterListData._version != 0) {
            return;
         }
      }

      this.reset();
      this.commit(false);
   }
}
