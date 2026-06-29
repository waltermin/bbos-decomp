package net.rim.device.api.collection.util;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public class PatriciaKeywordFilterList extends AbstractKeywordFilterList {
   private PatriciaTree _keywordTree;
   public boolean _haltSearch;

   public PatriciaKeywordFilterList(ReadableList source, PatriciaTree keywordTree) {
      super(source);
      this._keywordTree = keywordTree;
      this.setSearcher(new KeywordSearcher(this));
   }

   @Override
   protected void haltSearch() {
      this._haltSearch = true;
   }

   protected int[] getIDsBySortOrder() {
      throw null;
   }

   @Override
   public Object[] getElements(KeywordPrefixSearchResult result) {
      synchronized (super._source) {
         Object[] var10000;
         synchronized (this) {
            Object[] matchElements = new Object[result.getMatchCount()];
            int dest = 0;
            int[] orderList = this.getIDsBySortOrder();
            int elementCount = orderList.length;
            BitSet primaryMatches = result.getPrimaryMatches();
            BitSet secondaryMatches = result.getSecondaryMatches();
            int count = primaryMatches.getNumSet();

            for (int src = 0; dest < count && src < elementCount; src++) {
               int id = orderList[src];
               if (primaryMatches.isSet(id)) {
                  matchElements[dest++] = super._source.getAt(src);
               }
            }

            count = dest + secondaryMatches.getNumSet();

            for (int var20 = 0; dest < count && var20 < elementCount; var20++) {
               int id = orderList[var20];
               if (secondaryMatches.isSet(id)) {
                  matchElements[dest++] = super._source.getAt(var20);
               }
            }

            if (result == super._filterResult && dest != matchElements.length) {
               BitSet usedIDs = new BitSet();

               for (int i = elementCount - 1; i >= 0; i--) {
                  usedIDs.fastSet(orderList[i]);
               }

               primaryMatches.and(usedIDs);
               secondaryMatches.and(usedIDs);
            }

            Array.resize(matchElements, dest);
            var10000 = matchElements;
         }

         return var10000;
      }
   }

   @Override
   public boolean matches(Object object) {
      return false;
   }

   @Override
   public void reset(Collection collection) {
      this.clearPrefixCache();
      this.clearFilteredElementList();
      Object criteria = this.getCriteria();
      if (criteria != null) {
         this.setCriteria(criteria, null);
      } else {
         this.fireReset();
      }
   }

   @Override
   public synchronized KeywordPrefixSearchResult search(String[] words) {
      KeywordPrefixCache cache = this.getPrefixCache();
      this._haltSearch = false;
      byte targetCount = 0;
      byte[] hitCount = null;
      int wordCount = words.length;

      for (int i = 0; i < wordCount; i++) {
         for (int j = 0; j < wordCount; j++) {
            if (StringUtilities.startsWithIgnoreCase(words[i], words[j], 1701707776)) {
               targetCount++;
            }
         }
      }

      if (targetCount > wordCount) {
         hitCount = new byte[0];
         cache = null;
      }

      if (this._haltSearch) {
         return null;
      }

      BitSet visibleSet = new BitSet();
      BitSet primarySet = new BitSet();
      BitSet theSet = visibleSet;
      PatriciaKeywordSearchResult searchData = new PatriciaKeywordSearchResult(this, hitCount, primarySet);

      for (int word = 0; word < wordCount; word++) {
         String wordString = words[word];
         boolean cacheUsed = false;
         if (cache != null) {
            if (word == 0) {
               BitSet cacheEntry = cache.getPrimaryEntry(wordString);
               if (cacheEntry != null) {
                  primarySet.or(cacheEntry);
                  cacheEntry = cache.getSecondaryEntry(wordString);
                  if (cacheEntry != null) {
                     theSet.or(cacheEntry);
                     cacheUsed = true;
                  }
               }
            } else {
               BitSet cacheEntry = cache.getSecondaryEntry(wordString);
               if (cacheEntry != null) {
                  theSet.or(cacheEntry);
                  cacheUsed = true;
               }
            }
         }

         if (!cacheUsed) {
            searchData._wordNumber = word;
            searchData._theSet = theSet;
            this._keywordTree.search(wordString, searchData);
            if (cache != null) {
               if (word == 0) {
                  cache.putPrimaryEntry(wordString, primarySet);
               }

               cache.putSecondaryEntry(wordString, theSet);
            }
         }

         if (this._haltSearch) {
            return null;
         }

         if (word != 0) {
            visibleSet.and(theSet);
            if (word < wordCount - 1) {
               theSet.reset();
            }
         } else if (wordCount > 1) {
            theSet = new BitSet();
         }

         if (this._haltSearch) {
            return null;
         }
      }

      if (hitCount != null) {
         for (int var16 = visibleSet.getFirstSet(); var16 != -1; var16 = visibleSet.getNextSet(var16 + 1)) {
            if (hitCount[var16] < targetCount) {
               visibleSet.fastClear(var16);
            }
         }
      }

      primarySet.and(visibleSet);
      BitSet secondarySet = visibleSet;
      secondarySet.xor(primarySet);
      return this._haltSearch ? null : new KeywordPrefixSearchResult(primarySet, secondarySet);
   }
}
