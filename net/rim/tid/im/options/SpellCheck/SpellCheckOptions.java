package net.rim.tid.im.options.SpellCheck;

import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.tid.im.spellcheck.SpellCheckConstants;

public final class SpellCheckOptions implements SpellCheckConstants {
   private PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(-7831230383556836047L);
   private SpellCheckOptions$PersistedSpellCheckOptions _persistedSpellCheckOptions;
   private OTASyncCapableSyncItem _syncItem;
   private static final long SPELL_CHECK_OPTIONS_SYNC_ITEM = -2621761209518645263L;
   private static final long PERSISTED_SPELL_CHECK_OPTIONS = -7831230383556836047L;
   public static final int DIFFERENT_LETTER_CASE_AS_SAME = 1;
   public static final int UPPER_CASE_IGNORE = 2;
   public static final int WORDS_WITH_NUMBERS_IGNORE = 4;
   public static final int ACCENTED_CHARACTERS_IGNORE = 8;
   public static final int LEARNING_ON_OFF = 16;
   public static final int AUTOMATIC_SUFFIX_HANDLING = 32;
   public static final int DEFAULT_DIALOG_NOTIFICATION = 64;
   public static final int SPELLCHECK_EMAIL_BEFORE_SEND = 128;
   public static final int USE_ADDRESS_BOOK = 256;
   public static final int SUGGESTIONS_LEVEL = 65536;
   public static final int MIN_WORD_SIZE_FOR_CHECK = 131072;
   public static final int CUSTOM_DICT_SIZE = 262144;
   public static final int REPLACEMENT_PAIRS_SIZE = 524288;
   public static final int CONTAINER_SIZE = 1048576;
   private static SpellCheckOptions _options;

   private SpellCheckOptions() {
      synchronized (this._persistentObject) {
         this._persistedSpellCheckOptions = (SpellCheckOptions$PersistedSpellCheckOptions)this._persistentObject.getContents();
         if (this._persistedSpellCheckOptions == null) {
            this._persistedSpellCheckOptions = new SpellCheckOptions$PersistedSpellCheckOptions();
            this._persistentObject.setContents(this._persistedSpellCheckOptions, 51);
            this._persistentObject.commit();
         }
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._syncItem = (OTASyncCapableSyncItem)ar.get(-2621761209518645263L);
      if (this._syncItem == null) {
         this._syncItem = new SpellCheckOptions$SpellCheckOptionsSyncItem();
         ar.put(-2621761209518645263L, this._syncItem);
         SyncManager.getInstance().enableSynchronization(this._syncItem);
      }
   }

   public static final SpellCheckOptions getOptions() {
      if (_options == null) {
         _options = new SpellCheckOptions();
      }

      return _options;
   }

   public final OTASyncCapableSyncItem getSyncItem() {
      return this._syncItem;
   }

   public final void addCollectionListener(Object listener) {
      this._syncItem.addCollectionListener(listener);
   }

   public final byte getFlag(int flag) {
      return (byte)((this._persistedSpellCheckOptions._flags & flag) != 0 ? 1 : 0);
   }

   public final void setFlag(int flag, byte on) {
      if (on != 0) {
         if ((this._persistedSpellCheckOptions._flags & flag) == 0) {
            this._persistedSpellCheckOptions._flags |= flag;
            this._syncItem.fireSyncItemUpdated();
            return;
         }
      } else if ((this._persistedSpellCheckOptions._flags & flag) != 0) {
         this._persistedSpellCheckOptions._flags &= ~flag;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final byte getSuggestionsLevel() {
      return sanitizeSuggestionLevel(this._persistedSpellCheckOptions._suggestionLevel);
   }

   public final void setSuggestionsLevel(byte level) {
      level = sanitizeSuggestionLevel(level);
      if (level != this._persistedSpellCheckOptions._suggestionLevel) {
         this._persistedSpellCheckOptions._suggestionLevel = level;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final byte getMinWordSizeForCheck() {
      return sanitizeMinWordSizeForCheck(this._persistedSpellCheckOptions._minWordSizeForCheck);
   }

   public final void setMinWordSizeForCheck(byte wordSize) {
      wordSize = sanitizeMinWordSizeForCheck(wordSize);
      if (wordSize != this._persistedSpellCheckOptions._minWordSizeForCheck) {
         this._persistedSpellCheckOptions._minWordSizeForCheck = wordSize;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final byte getLearningBufferCustomDictSize() {
      return sanitizeCustomDictSize(this._persistedSpellCheckOptions._learningBuffCustDictSize);
   }

   public final void setLearningBufferCustomDictSize(byte bufSize) {
      bufSize = sanitizeCustomDictSize(bufSize);
      if (bufSize != this._persistedSpellCheckOptions._learningBuffCustDictSize) {
         this._persistedSpellCheckOptions._learningBuffCustDictSize = bufSize;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final byte getLearningBufferReplacementPairsSize() {
      return sanitizePairLearningSize(this._persistedSpellCheckOptions._learningBuffReplPairsSize);
   }

   public final void setLearningBufferReplacementPairsSize(byte bufSize) {
      bufSize = sanitizePairLearningSize(bufSize);
      if (bufSize != this._persistedSpellCheckOptions._learningBuffReplPairsSize) {
         this._persistedSpellCheckOptions._learningBuffReplPairsSize = bufSize;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final void setContainerSize(byte containerSize) {
      containerSize = sanitizeContainerSize(containerSize);
      if (this._persistedSpellCheckOptions._containerSize == containerSize) {
         this._persistedSpellCheckOptions._containerSize = containerSize;
         this._syncItem.fireSyncItemUpdated();
      }
   }

   public final byte getContainerSize() {
      return sanitizeContainerSize(this._persistedSpellCheckOptions._containerSize);
   }

   public final void commit() {
      this._persistentObject.commit();
   }

   private static final byte sanitizeSuggestionLevel(byte suggestionLevel) {
      if (suggestionLevel < 1) {
         return 1;
      } else {
         return suggestionLevel > 10 ? 10 : suggestionLevel;
      }
   }

   private static final byte sanitizeCustomDictSize(byte dictSize) {
      if (dictSize < 1) {
         return 1;
      } else {
         return dictSize > 10 ? 10 : dictSize;
      }
   }

   private static final byte sanitizePairLearningSize(byte size) {
      if (size < 0) {
         return 0;
      } else {
         return size > 10 ? 10 : size;
      }
   }

   private static final byte sanitizeContainerSize(byte size) {
      if (size < 1) {
         return 32;
      } else {
         return size > 10 ? 32 : size;
      }
   }

   private static final byte sanitizeMinWordSizeForCheck(byte size) {
      byte minMinWordSizeToCheck = 2;
      byte maxMinWordSizeToCheck = 10;
      if (size < minMinWordSizeToCheck) {
         return minMinWordSizeToCheck;
      } else {
         return size > maxMinWordSizeToCheck ? maxMinWordSizeToCheck : size;
      }
   }
}
