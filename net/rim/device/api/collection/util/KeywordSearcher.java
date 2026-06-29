package net.rim.device.api.collection.util;

import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.tid.im.conv.repository.IDataSearchRepository;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class KeywordSearcher implements IDataSearchRepository {
   private AbstractKeywordFilterList _list;
   protected FilterStatusListener _listener;
   protected boolean _interrupted;
   protected WeakReference _wordBufferWR = new WeakReference(null);

   protected AbstractKeywordFilterList getList() {
      return this._list;
   }

   protected void halt() {
      this._interrupted = true;
      this._list.haltSearch();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected synchronized BitSet search(String[] words) {
      AbstractKeywordFilterList list = this.getList();
      boolean var10 = false /* VF: Semaphore variable */;

      Object var12;
      label107: {
         BitSet var7;
         try {
            var10 = true;
            this._interrupted = false;
            boolean wordsAreValid = words != null && words.length != 0;
            String suffix = list.getSuffix();
            if (this._listener != null) {
               this._listener.filterStarted();
            }

            if (!wordsAreValid && suffix == null) {
               list.setFilterResult(null, null);
               if (this._listener != null) {
                  this._listener.filterDone(false);
               }

               var12 = null;
               var10 = false;
               break label107;
            }

            KeywordPrefixSearchResult result = new KeywordPrefixSearchResult(null, null);
            BitSet returnResult = this.search(words, result, suffix);
            if (!this._interrupted && (result != null || words == null)) {
               list.setFilterResult(wordsAreValid ? words : null, result);
               if (this._listener != null) {
                  this._listener.filterDone(false);
               }
            } else if (this._listener != null) {
               this._listener.filterDone(true);
            }

            this._listener = null;
            this.notify();
            var7 = returnResult;
            var10 = false;
         } finally {
            if (var10) {
               list.filteringComplete();
            }
         }

         list.filteringComplete();
         return var7;
      }

      list.filteringComplete();
      return (BitSet)var12;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected synchronized void search(String words, FilterStatusListener listener) {
      AbstractKeywordFilterList list = this.getList();
      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         this._interrupted = false;
         this.setFilterStatusListener(listener);
         if (listener != null) {
            this._listener.filterStarted();
         }

         String suffix = list.getSuffix();
         KeywordPrefixSearchResult result = null;
         if (words != null || suffix != null) {
            String[] wordBuffer = WeakReferenceUtilities.getStringArray(this._wordBufferWR, 10);
            int wordCount = 0;
            if (words != null) {
               wordCount = StringUtilities.stringToKeywords(words, wordBuffer, 0);
               Array.resize(wordBuffer, wordCount);
            }

            if (suffix != null) {
               wordCount += StringUtilities.stringToKeywords(suffix, wordBuffer, wordCount);
               Array.resize(wordBuffer, wordCount);
            }

            result = list.search(wordBuffer);
         }

         if (!this._interrupted && (result != null || words == null)) {
            list.setFilterResult(words, result);
            if (this._listener != null) {
               this._listener.filterDone(false);
            }
         } else if (this._listener != null) {
            this._listener.filterDone(true);
         }

         this.setFilterStatusListener(null);
         this.notify();
         var10 = false;
      } finally {
         if (var10) {
            list.filteringComplete();
         }
      }

      list.filteringComplete();
   }

   protected synchronized void search(String[] words, FilterStatusListener listener) {
      this.search(new String[][]{words}, listener);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected synchronized void search(String[][] words, FilterStatusListener listener) {
      AbstractKeywordFilterList list = this.getList();
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;
         this._interrupted = false;
         this.setFilterStatusListener(listener);
         if (listener != null) {
            this._listener.filterStarted();
         }

         BitSet primary = null;
         BitSet secondary = null;
         KeywordPrefixSearchResult result = new KeywordPrefixSearchResult(null, null);

         for (int i = 0; i < words.length; i++) {
            String[] prefixes = words[i];
            if (prefixes != null && prefixes.length != 0) {
               result.setResults(null, null);
               this.search(prefixes, result, null);
               if (this._interrupted) {
                  break;
               }

               if (i == 0) {
                  primary = new BitSet(result.getPrimaryMatches());
                  secondary = new BitSet(result.getSecondaryMatches());
               } else {
                  BitSet tempSet = new BitSet(result.getPrimaryMatches());
                  tempSet.or(result.getSecondaryMatches());
                  primary.and(tempSet);
                  secondary.and(tempSet);
               }
            }
         }

         if (!this._interrupted) {
            String suffix = list.getSuffix();
            if (suffix != null) {
               String[] suffixString = new String[]{suffix};
               this.search(suffixString, result, null);
               if (words.length == 0) {
                  primary = new BitSet(result.getPrimaryMatches());
                  secondary = new BitSet(result.getSecondaryMatches());
               } else {
                  BitSet tempSet = new BitSet(result.getPrimaryMatches());
                  tempSet.or(result.getSecondaryMatches());
                  primary.and(tempSet);
                  secondary.and(tempSet);
               }
            }
         }

         if (!this._interrupted) {
            if (primary == null) {
               primary = new BitSet();
               secondary = new BitSet();
            }

            result.setResults(primary, secondary);
            list.setFilterResult(words, result);
            if (this._listener != null) {
               this._listener.filterDone(false);
            }
         } else if (this._listener != null) {
            this._listener.filterDone(true);
         }

         this.setFilterStatusListener(null);
         this.notify();
         var12 = false;
      } finally {
         if (var12) {
            list.filteringComplete();
         }
      }

      list.filteringComplete();
   }

   public void setFilterStatusListener(FilterStatusListener listener) {
      this._listener = listener;
   }

   public synchronized void waitForComplete() {
   }

   @Override
   public BitSet searchPrefixes(String[] words) {
      return this.search(words);
   }

   private BitSet search(String[] words, KeywordPrefixSearchResult result, String suffix) {
      AbstractKeywordFilterList list = this.getList();
      BitSet returnResult = new BitSet();

      try {
         KeywordPrefixCache prefixCache = list.getPrefixCache();
         BitSet primary = null;
         BitSet secondary = null;
         String cacheEntryName = null;
         boolean cacheUsed = false;
         if (prefixCache != null) {
            StringBuffer tempBuffer = new StringBuffer();
            if (words != null) {
               for (int i = 0; i < words.length; i++) {
                  tempBuffer.append('/').append(words[i]);
               }
            }

            if (suffix != null) {
               tempBuffer.append('/').append(suffix);
            }

            cacheEntryName = tempBuffer.toString();
            primary = prefixCache.getPrimaryEntry(cacheEntryName);
            if (primary != null) {
               secondary = prefixCache.getSecondaryEntry(cacheEntryName);
               returnResult = prefixCache.getReturnResultEntry(cacheEntryName);
               cacheUsed = true;
            }
         }

         KeywordPrefixSearchResult tmpResult = null;
         if (!cacheUsed) {
            returnResult = new BitSet();
            primary = new BitSet();
            secondary = new BitSet();
            String[] wordBuffer = WeakReferenceUtilities.getStringArray(this._wordBufferWR, 10);
            int wordsToSearch = words != null ? words.length : 0;

            for (int i = 0; i <= wordsToSearch; i++) {
               int wordCount = 0;
               if (i == wordsToSearch && wordsToSearch > 0) {
                  break;
               }

               if (i < wordsToSearch) {
                  wordCount = StringUtilities.stringToKeywords(words[i], wordBuffer, 0);
               }

               if (suffix != null) {
                  wordCount += StringUtilities.stringToKeywords(suffix, wordBuffer, wordCount);
               }

               Array.resize(wordBuffer, wordCount);
               tmpResult = list.search(wordBuffer);
               if (tmpResult != null) {
                  if (tmpResult.getPrimaryMatches().getNumSet() != 0) {
                     primary.or(tmpResult.getPrimaryMatches());
                     returnResult.set(i);
                  }

                  if (tmpResult.getSecondaryMatches().getNumSet() != 0) {
                     secondary.or(tmpResult.getSecondaryMatches());
                     returnResult.set(i);
                  }
               }
            }

            BitSet maskSet = new BitSet(primary);
            maskSet.and(secondary);
            secondary.xor(maskSet);
         }

         result.setResults(primary, secondary);
         if (!this._interrupted && (tmpResult != null || words == null) && prefixCache != null && !cacheUsed) {
            prefixCache.putPrimaryEntry(cacheEntryName, primary);
            prefixCache.putSecondaryEntry(cacheEntryName, secondary);
            prefixCache.putReturnResultEntry(cacheEntryName, returnResult);
         }
      } catch (Throwable var16) {
      }

      return returnResult;
   }

   protected KeywordSearcher(AbstractKeywordFilterList list) {
      this._list = list;
   }
}
