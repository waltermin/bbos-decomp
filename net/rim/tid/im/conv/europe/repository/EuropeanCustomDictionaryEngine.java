package net.rim.tid.im.conv.europe.repository;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;

public class EuropeanCustomDictionaryEngine extends CustomDictionary implements Collection {
   private WordLearningReader _reader;
   private WordLearningReader _freqReader;
   private WordLearningReader _rejectedWordsReader;
   private Vector _checkRepositories;

   public EuropeanCustomDictionaryEngine(
      WordLearningReader newWordsReader, WordLearningReader freqReader, WordLearningReader rejectedWordsReader, Vector checkRepositories
   ) {
      this._reader = newWordsReader;
      this._freqReader = freqReader;
      this._rejectedWordsReader = rejectedWordsReader;
      this._checkRepositories = checkRepositories;
   }

   @Override
   public int size() {
      return this._reader.size();
   }

   @Override
   public boolean isInRepository(Object entry) {
      int res = 0;
      if (entry instanceof Object) {
         String word = (String)entry;
         int len = word.length();
         char[] content = new char[len];
         word.getChars(0, len, content, 0);
         CustomWordsRepository repository = null;

         for (int i = 0; i < this._checkRepositories.size() && res == 0; i++) {
            repository = (CustomWordsRepository)this._checkRepositories.elementAt(i);
            res = repository.containsWord(content, len);
         }
      }

      return res != 0;
   }

   @Override
   public Object add(Object newEntry) {
      if (this._reader.addWord((String)newEntry, '\u0000', false, false) == 0 && this._rejectedWordsReader != null) {
         this._rejectedWordsReader.removeWord((String)newEntry, false);
      }

      return super.add(newEntry);
   }

   @Override
   public void remove(Object removeEntry) {
      this._reader.removeWord((String)removeEntry, false);
      if (this._freqReader != null) {
         this._freqReader.removeWord((String)removeEntry, false);
      }

      if (this._rejectedWordsReader != null) {
         this._rejectedWordsReader.addWord((String)removeEntry, '\u0000', false, false);
      }

      super.remove(removeEntry);
   }

   @Override
   public boolean contains(Object element) {
      return this._reader.contains(element);
   }

   @Override
   public Enumeration getElements() {
      return this._reader.getElements();
   }

   @Override
   public int getElements(Object[] elements) {
      return this._reader.getElements(elements);
   }
}
