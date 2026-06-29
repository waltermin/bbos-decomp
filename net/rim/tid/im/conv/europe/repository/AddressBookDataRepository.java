package net.rim.tid.im.conv.europe.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.im.conv.repository.AlphabetChangeListener;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.IRepository;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.util.SLTextDataContainer;

public final class AddressBookDataRepository extends IRepository implements CustomWordsRepository {
   private AddressBookReader _reader;
   private CustomWordsRepository _repository;
   private CustomWordsRepository _freqRepository;
   private ExtendedCurrentVariant _tempVariant = (ExtendedCurrentVariant)(new Object(15));
   private ResultContainer _resContainer = (ResultContainer)(new Object());
   private static final int MAX_SPLIT_NESTING_LEVEL;
   private static final int DEFAULT_FRQ;
   private static final int MAX_FRQ;
   private static final int FRQ_INCREASE_FACTOR;

   public final void init(CustomWordsRepository repository, CustomWordsRepository freqRepository) {
      if (this._reader == null) {
         label19:
         try {
            this._reader = new AddressBookReader();
            this._reader.setMaxSplitNestingLevel(3);
         } finally {
            break label19;
         }
      }

      this._repository = repository;
      this._freqRepository = freqRepository;
      this._resContainer.setCurrentSource((byte)8, false);
   }

   public final void setOverrideListenersMode(boolean isEnabled) {
      this._reader.setOverrideListenersMode(isEnabled);
   }

   @Override
   public final int addWord(char[] word, int length) {
      return this.addWord(word, length, (byte)2);
   }

   @Override
   public final int addWord(char[] word, int length, byte priority) {
      return 4;
   }

   @Override
   public final int addWord(char[] word, int length, byte priority, int type) {
      return 4;
   }

   @Override
   public final boolean containsCharInAlphabet(char ch) {
      return false;
   }

   @Override
   public final void init(int anInitialSize) {
      try {
         if (this._reader == null) {
            this._reader = new AddressBookReader();
         }

         this._reader.init(anInitialSize);
      } finally {
         this._reader = null;
         return;
      }
   }

   @Override
   public final void addWords(Object obj) {
   }

   @Override
   public final void removeWords(Object obj) {
   }

   @Override
   public final void addWords(StringBuffer words) {
      int indexStart = -1;
      if (this._reader != null) {
         for (int i = 0; i < words.length(); i++) {
            if (words.charAt(i) != ' ') {
               if (indexStart == -1) {
                  indexStart = i;
               }
            } else if (indexStart != -1 && i > indexStart) {
               this._tempVariant.set(words, indexStart, i - indexStart);
               this._resContainer.reset(1, this._tempVariant._length, this._tempVariant._length, true, true);
               this._reader.getVariants(this._tempVariant._variants, this._tempVariant._offset, this._tempVariant._length, this._resContainer);
               if (this._resContainer.getVariantsCount() == 0) {
                  this._reader.addWord(this._tempVariant, '憨', false);
               } else {
                  this._resContainer.getVariantAt(0, this._tempVariant);
                  int frq = this._tempVariant._frequency;
                  int newFrq = Math.min(frq + 3900, 64000);
                  if (frq != newFrq) {
                     this._reader.addWord(this._tempVariant, (char)newFrq, false);
                  }
               }

               indexStart = -1;
            }
         }
      }
   }

   @Override
   public final void removeWords(StringBuffer words) {
      if (this._repository != null) {
         int indexStart = -1;
         if (this._reader != null) {
            for (int i = 0; i < words.length(); i++) {
               if (words.charAt(i) != ' ') {
                  if (indexStart == -1) {
                     indexStart = i;
                  }
               } else if (indexStart != -1 && i > indexStart) {
                  this._tempVariant.set(words, indexStart, i - indexStart);
                  this._reader.removeWord(this._tempVariant, false);
                  if (this._freqRepository != null && this._repository.containsWord(this._tempVariant._variants, this._tempVariant._length) == 0) {
                     this._freqRepository.removeWord(this._tempVariant._variants, this._tempVariant._length);
                  }

                  indexStart = -1;
               }
            }
         }
      }
   }

   @Override
   public final int removeWord(char[] word, int length) {
      if (this._repository != null && this._reader != null) {
         this._tempVariant.set(word, 0, length);
         int ret = this._reader.removeWord(this._tempVariant, false);
         return this._freqRepository != null && this._repository.containsWord(word, length) == 0 ? this._freqRepository.removeWord(word, length) : ret;
      } else {
         return 1;
      }
   }

   @Override
   public final void clear() {
      if (this._reader != null) {
         this._reader.clearAll();
         this._reader = null;
      }
   }

   @Override
   public final void commit() {
      if (this._reader != null) {
         this._reader.commit();
      }
   }

   @Override
   public final int getType() {
      return 1;
   }

   @Override
   public final int containsWord(char[] word, int length) {
      if (this._reader != null) {
         this._resContainer.reset(1, length, length, true, true);
         this._reader.getVariants(word, length, this._resContainer);
         return this._resContainer.getVariantsCount() > 0 ? 2 : 0;
      } else {
         return 0;
      }
   }

   @Override
   public final int getVariants(ReIterator aIterator, ResultContainer aRes) {
      if (this._reader != null) {
         this._reader.getVariants(aIterator, aRes);
         return aRes.getVariantsCount();
      } else {
         return 0;
      }
   }

   @Override
   public final boolean clearAll() {
      this.clear();
      return true;
   }

   @Override
   public final synchronized int getVariants(SLTextDataContainer aRegexpPrefixes, ResultContainer aRes) {
      return 0;
   }

   @Override
   public final void setLocale(Locale locale) {
   }

   @Override
   public final boolean isInAlphabet(char aCh) {
      return this._reader != null ? this._reader.isInAlphabet(aCh) : false;
   }

   @Override
   public final void addAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._reader != null) {
         this._reader.addAlphabetChangeListener(l);
      }
   }

   @Override
   public final void removeAlphabetChangeListener(AlphabetChangeListener l) {
      if (this._reader != null) {
         this._reader.removeAlphabetChangeListener(l);
      }
   }

   @Override
   public final void protectContent(int flag) {
      switch (flag) {
         case 0:
            this._reader = null;
            this._repository = null;
            this._freqRepository = null;
            this._tempVariant = (ExtendedCurrentVariant)(new Object(15));
            this._resContainer = (ResultContainer)(new Object());
      }
   }

   public AddressBookDataRepository() {
      try {
         this._reader = new AddressBookReader();
         this._reader.setMaxSplitNestingLevel(3);
      } finally {
         return;
      }
   }
}
