package net.rim.device.api.collection.util;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;
import net.rim.vm.Memory;

public class KeywordPrefixManager implements Persistable {
   private BigIntVector _prefixes;
   private BigLongVector _prefixesLong;
   private ByteVector _intWordIndexes;
   private ByteVector _longWordIndexes;
   private BitSet _validPrefixTuples;
   private BitSet _validTuples;
   public byte DEFAULT_INDEX = 127;
   private int[] _tmpStartIndices = new int[64];
   private int[] _tmpEndIndices = new int[64];
   private boolean _sorted;
   private boolean _sortedLong;
   private int _maxId;
   private boolean _haltSearch;
   private static final int MASK_BIT_COUNT;
   private static final long MASK_BIT_COUNT_LONG;
   private static final int PREFIX_SHIFT;
   private static final int PREFIX_MASK_1;
   private static final long PREFIX_MASK_1_LONG;
   private static final int PREFIX_MASK_2;
   private static final long PREFIX_MASK_2_LONG;
   private static final int PREFIX_MASK_3;
   private static final long PREFIX_MASK_3_LONG;
   private static final int PREFIX_MASK;
   private static final long PREFIX_MASK_LONG;
   private static final int FIRST_WORD_MASK;
   private static final int ID_MASK;
   private static final int PREFIX_LENGTH;
   private static final byte[] charUnifier = new byte[]{
      0,
      31,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      26,
      27,
      28,
      29,
      30,
      21,
      17,
      22,
      23,
      24,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20,
      21,
      22,
      23,
      24,
      25,
      26,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      2,
      0,
      0,
      0,
      0,
      0,
      19,
      0,
      3,
      0,
      0,
      0,
      0,
      18,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      16,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      22,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      3,
      5,
      5,
      5,
      5,
      9,
      9,
      9,
      9,
      4,
      14,
      15,
      15,
      15,
      15,
      15,
      0,
      15,
      21,
      21,
      21,
      21,
      25,
      20,
      19,
      1,
      1,
      1,
      1,
      1,
      1,
      1,
      3,
      5,
      5,
      5,
      5,
      9,
      9,
      9,
      9,
      4,
      14,
      15,
      15,
      15,
      15,
      15,
      0,
      15,
      21,
      21,
      21,
      21,
      25,
      20,
      25
   };
   private static final char[] PREFIX_CODE_TO_CHAR_MAP = new char[]{
      '\u0000',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z',
      '1',
      '2',
      '3',
      '4',
      '\u0001',
      '\u000b',
      '퀄',
      'ऀ',
      '⸴',
      '⸳',
      '⸰',
      '㈱',
      '4',
      '\u0012',
      '퀄',
      'က',
      '䥒',
      '⁍',
      '䱃',
      '䍄',
      '䰠',
      '扩',
      '慲',
      '祲',
      '\u0000',
      '\u0019',
      '퀄',
      'ᜀ',
      '敒',
      '敳',
      '牡',
      '档',
      '䤠',
      '\u206e',
      '潍',
      '楴',
      '湯'
   };

   public KeywordPrefixManager() {
      this.reset();
   }

   private int getKey(String str, int offset, int length) {
      if (length > str.length() - offset) {
         throw new IllegalArgumentException();
      } else {
         return this.getKey0(
            (char)(length > 0 ? str.charAt(offset) : 0), (char)(length > 1 ? str.charAt(offset + 1) : 0), (char)(length > 2 ? str.charAt(offset + 2) : 0)
         );
      }
   }

   private int getKey(char[] data, int offset, int length) {
      if (data.length < length - offset) {
         throw new IllegalArgumentException();
      } else {
         return this.getKey0((char)(length > 0 ? data[offset] : 0), (char)(length > 1 ? data[offset + 1] : 0), (char)(length > 2 ? data[offset + 2] : 0));
      }
   }

   private int getKey0(char letter1, char letter2, char letter3) {
      if (letter1 > 255) {
         letter1 = 0;
      }

      if (letter2 > 255) {
         letter2 = 0;
      }

      if (letter3 > 255) {
         letter3 = 0;
      }

      return charUnifier[letter1] << 26 | charUnifier[letter2] << 21 | charUnifier[letter3] << 16;
   }

   private long getKeyLong(String str, int offset, int length) {
      if (str.length() - offset < length) {
         throw new IllegalArgumentException();
      } else {
         return this.getKeyLong0(
            length > 0 ? str.charAt(offset) : '\u0000', length > 1 ? str.charAt(offset + 1) : '\u0000', length > 2 ? str.charAt(offset + 2) : '\u0000'
         );
      }
   }

   private long getKeyLong(char[] data, int offset, int length) {
      if (data.length < length - offset) {
         throw new IllegalArgumentException();
      } else {
         return this.getKeyLong0(length > 0 ? data[offset] : '\u0000', length > 1 ? data[offset + 1] : '\u0000', length > 2 ? data[offset + 2] : '\u0000');
      }
   }

   private long getKeyLong0(char letter1, char letter2, char letter3) {
      letter1 = CharacterUtilities.getOriginal(CharacterUtilities.toLowerCase(letter1, 1701707776));
      letter2 = CharacterUtilities.getOriginal(CharacterUtilities.toLowerCase(letter2, 1701707776));
      letter3 = CharacterUtilities.getOriginal(CharacterUtilities.toLowerCase(letter3, 1701707776));
      return (long)(letter1 < 255 ? charUnifier[letter1] : letter1) << 48
         | (long)(letter2 < 255 ? charUnifier[letter2] : letter2) << 32
         | (long)(letter3 < 255 ? charUnifier[letter3] : letter3) << 16;
   }

   private int nextKey(int key) {
      if ((key & 2031616) != 0) {
         return key + 65536;
      } else {
         return (key & 65011712) != 0 ? key + 2097152 : key + 67108864;
      }
   }

   private long nextKey(long key) {
      if ((key & 4294901760L) != 0) {
         return key + 65536;
      } else {
         return (key & 281470681743360L) != 0 ? key + 4294967296L : key + 281474976710656L;
      }
   }

   public void reset() {
      this._prefixes = new BigIntVector(64);
      this._prefixesLong = new BigLongVector(64);
      synchronized (this._prefixes) {
         this._sorted = false;
      }

      synchronized (this._prefixesLong) {
         this._sortedLong = false;
      }

      this._intWordIndexes = new ByteVector(64);
      this._longWordIndexes = new ByteVector(64);
      this._validTuples = new BitSet(32768);
      this._validPrefixTuples = new BitSet(32768);
   }

   public int getPrefixCount() {
      return this._prefixes.size() + this._prefixesLong.size();
   }

   public void addWords(int id, String[] words) {
      for (int i = 0; i < words.length; i++) {
         this.addWord(id, words[i], i == 0, (byte)i);
      }
   }

   public void addWords(int id, String words, boolean firstFlag) {
      this.addWords(id, words, firstFlag, this.DEFAULT_INDEX);
   }

   public void addWords(int id, String words, boolean firstFlag, byte propertyIndex) {
      if (words != null) {
         synchronized (this._prefixes) {
            synchronized (this._prefixesLong) {
               int newSize = words.length() / 2 + 1;
               if (this._tmpStartIndices.length < newSize) {
                  Array.resize(this._tmpStartIndices, newSize);
               }

               if (this._tmpEndIndices.length < newSize) {
                  Array.resize(this._tmpEndIndices, newSize);
               }

               int wordCount = StringUtilities.stringToWordsOrKeywords(words, this._tmpStartIndices, this._tmpEndIndices, 0, true);

               for (int i = 0; i < wordCount; i++) {
                  int wordStart = this._tmpStartIndices[i];
                  int wordLength = this._tmpEndIndices[i] - this._tmpStartIndices[i];
                  this.addTuples(words, wordStart, wordLength);
                  if (StringUtilities.getCharacterSize(words) == 2) {
                     long key = this.getKeyLong(words, wordStart, wordLength) | id;
                     if (firstFlag) {
                        key |= 32768;
                        firstFlag = false;
                     }

                     if (propertyIndex != 127) {
                        this.add(key, (byte)(propertyIndex << 4 | (byte)i));
                     } else {
                        this.add(key, propertyIndex);
                     }
                  } else {
                     int key = this.getKey(words, wordStart, wordLength) | id;
                     if (firstFlag) {
                        key |= 32768;
                        firstFlag = false;
                     }

                     if (propertyIndex != 127) {
                        this.add(key, (byte)(propertyIndex << 4 | (byte)i));
                     } else {
                        this.add(key, propertyIndex);
                     }
                  }
               }
            }
         }
      }
   }

   public void addWord(int id, String word, boolean firstFlag) {
      this.addWord(id, word, firstFlag, this.DEFAULT_INDEX);
   }

   public void addWord(int id, String word, boolean firstFlag, byte wordIndex) {
      int wordLength = word.length();
      this.addTuples(word, 0, wordLength);
      if (StringUtilities.getCharacterSize(word) == 2) {
         long key = this.getKeyLong(word, 0, wordLength) | id;
         if (firstFlag) {
            key |= 32768;
         }

         this.add(key, wordIndex);
      } else {
         int key = this.getKey(word, 0, wordLength) | id;
         if (firstFlag) {
            key |= 32768;
         }

         this.add(key, wordIndex);
      }
   }

   private int getTuple(String word, int start, int length) {
      int wordLength = word.length();
      if (start + length > wordLength) {
         length = word.length() - start;
         if (length <= 0) {
            return 0;
         }
      }

      char c1 = '\u0000';
      char c2 = 0;
      char c3 = 0;
      c1 = CharacterUtilities.getOriginal(word.charAt(start++));
      if (length > 1) {
         c2 = CharacterUtilities.getOriginal(word.charAt(start++));
         if (length > 2) {
            c3 = CharacterUtilities.getOriginal(word.charAt(start));
         }
      }

      return c1 <= 255 && c2 <= 255 && c3 <= 255 ? (this.getKey0(c1, c2, c3) & 2147418112) >> 16 : 0;
   }

   private void addTuples(String word, int start, int length) {
      if (length >= 2) {
         int tuple = this.getTuple(word, start, 2);
         if (tuple != 0) {
            this._validPrefixTuples.fastSet(tuple);
         }

         if (length >= 3) {
            tuple = this.getTuple(word, start, length);
            if (tuple != 0) {
               this._validPrefixTuples.fastSet(tuple);
            }

            while (length > 3) {
               tuple = this.getTuple(word, ++start, --length);
               if (tuple != 0) {
                  this._validTuples.fastSet(tuple);
               }
            }
         }
      }
   }

   private boolean allTuplesPresent(String[] words) {
      for (int i = words.length - 1; i >= 0; i--) {
         String word = words[i];
         int length = word.length();
         if (length == 2) {
            int tuple = this.getTuple(word, 0, 2);
            if (tuple != 0 && !this._validPrefixTuples.isSet(tuple)) {
               return false;
            }
         } else if (length >= 3) {
            for (int start = length - 3; start > 0; start--) {
               int tuple = this.getTuple(word, start, 3);
               if (tuple != 0 && !this._validTuples.isSet(tuple)) {
                  return false;
               }
            }

            int tuple = this.getTuple(word, 0, 3);
            if (tuple != 0 && !this._validPrefixTuples.isSet(tuple)) {
               return false;
            }
         }
      }

      return true;
   }

   private void add(int key, byte wordIndex) {
      synchronized (this._prefixes) {
         if (!this._sorted) {
            this._prefixes.addElement(key);
            this._intWordIndexes.addElement(wordIndex);
         } else {
            int index = this._prefixes.binarySearch(key);
            if (index < 0) {
               index = -(index + 1);
            }

            this._prefixes.insertElementAt(key, index);
            this._intWordIndexes.insertElementAt(wordIndex, index);
         }

         int id = key & 32767;
         if (id > this._maxId) {
            this._maxId = id;
         }
      }
   }

   private void add(long key, byte wordIndex) {
      synchronized (this._prefixesLong) {
         if (!this._sortedLong) {
            this._prefixesLong.addElement(key);
            this._longWordIndexes.addElement(wordIndex);
         } else {
            int index = this._prefixesLong.binarySearch(key);
            if (index < 0) {
               index = -(index + 1);
            }

            this._prefixesLong.insertElementAt(key, index);
            this._longWordIndexes.insertElementAt(wordIndex, index);
         }

         int id = (int)key & 32767;
         if (id > this._maxId) {
            this._maxId = id;
         }
      }
   }

   public void delete(int id) {
      synchronized (this._prefixes) {
         int src = 0;
         int prefixCount = this._prefixes.size();
         this._maxId = 0;

         while (src < prefixCount) {
            int tmpId = this._prefixes.elementAt(src) & 32767;
            if (tmpId == id) {
               this._prefixes.removeElementAt(src);
               this._intWordIndexes.removeElementAt(src);
               prefixCount--;
            } else {
               if (tmpId > this._maxId) {
                  this._maxId = tmpId;
               }

               src++;
            }
         }
      }

      synchronized (this._prefixesLong) {
         int prefixCount = this._prefixesLong.size();
         int src = 0;

         while (src < prefixCount) {
            int tmpId = (int)this._prefixesLong.elementAt(src) & 32767;
            if (tmpId == id) {
               this._prefixesLong.removeElementAt(src);
               this._longWordIndexes.removeElementAt(src);
               prefixCount--;
            } else {
               if (tmpId > this._maxId) {
                  this._maxId = tmpId;
               }

               src++;
            }
         }
      }
   }

   protected void sort() {
      if (!this._sorted) {
         synchronized (this._prefixes) {
            Arrays.sort(this._prefixes.getContiguousArray(), 0, this._prefixes.size(), this._intWordIndexes.getArray());
            if (!this._haltSearch && this._prefixes.size() > 0) {
               this._sorted = true;
            }
         }

         Memory.moveToFlash(this._prefixes);
      }

      if (!this._sortedLong) {
         synchronized (this._prefixesLong) {
            Arrays.sort(this._prefixesLong.getContiguousArray(), 0, this._prefixesLong.size(), this._longWordIndexes.getArray());
            if (!this._haltSearch && this._prefixesLong.size() > 0) {
               this._sortedLong = true;
            }

            Memory.moveToFlash(this._prefixesLong);
         }
      }
   }

   public void haltSearch() {
      this._haltSearch = true;
   }

   public String[] getLongWords(String[] words) {
      if (words == null) {
         return null;
      }

      String[] longWords = new String[words.length];
      int dest = 0;

      for (int src = 0; src < words.length; src++) {
         if (words[src].length() > 3) {
            longWords[dest++] = words[src];
         }
      }

      if (dest == 0) {
         return null;
      }

      Array.resize(longWords, dest);
      return longWords;
   }

   public boolean getMatch(char[] prefixes, int offset, int length, int[] resultData) {
      if (resultData[1] == -1) {
         this.rangeSearch(prefixes, offset, length, resultData);
      }

      if (resultData[1] == -1 || resultData[2] == -1 || resultData[1] == resultData[2]) {
         return false;
      } else if (resultData[0] == 0) {
         resultData[3] = this._prefixes.elementAt(resultData[1]) & 32767;
         resultData[4] = this._intWordIndexes.elementAt(resultData[1]);
         return true;
      } else {
         resultData[3] = (int)this._prefixesLong.elementAt(resultData[1]) & 32767;
         resultData[4] = this._longWordIndexes.elementAt(resultData[1]);
         return true;
      }
   }

   private void rangeSearch(char[] prefixes, int offset, int length, int[] resultData) {
      int low = -1;
      int high = -1;
      if (!this._sorted || !this._sortedLong) {
         this.sort();
      }

      if (resultData[0] == 0) {
         int key = this.getKey(prefixes, offset, length);
         low = this._prefixes.binarySearch(key);
         high = this._prefixes.binarySearch(this.nextKey(key));
         if (low < 0) {
            low = -(low + 1);
         }

         if (high < 0) {
            high = -(high + 1);
         }

         if (high < low) {
            high = this._prefixes.size();
         }
      } else {
         long key = this.getKeyLong(prefixes, offset, length);
         low = this._prefixesLong.binarySearch(key);
         high = this._prefixesLong.binarySearch(this.nextKey(key));
         if (low < 0) {
            low = -(low + 1);
         }

         if (high < 0) {
            high = -(high + 1);
         }

         if (high < low) {
            high = this._prefixesLong.size();
         }
      }

      resultData[1] = low;
      resultData[2] = high;
   }

   public KeywordPrefixSearchResult search(String[] words) {
      return this.search(words, null);
   }

   public KeywordPrefixSearchResult search(String[] words, KeywordPrefixCache cache) {
      boolean allTuplesFound = this.allTuplesPresent(words);
      if (!allTuplesFound && this._prefixesLong.size() == 0) {
         BitSet emptySet = new BitSet();
         return new KeywordPrefixSearchResult(emptySet, emptySet);
      }

      synchronized (this._prefixes) {
         synchronized (this._prefixesLong) {
            this._haltSearch = false;
            if (!this._sorted || !this._sortedLong) {
               this.sort();
            }

            if (this._haltSearch) {
               return null;
            }

            byte targetCount = 0;
            byte[] hitCount = null;
            int wordCount = words.length;

            for (int i = 0; i < wordCount; i++) {
               for (int j = 0; j < wordCount; j++) {
                  if (StringUtilities.startsWithIgnoreCase(words[i], words[j])) {
                     targetCount++;
                  }
               }
            }

            if (targetCount > wordCount) {
               hitCount = new byte[this._maxId + 1];
               cache = null;
            }

            BitSet visibleSet = new BitSet(this._maxId + 1);
            BitSet primarySet = new BitSet(this._maxId + 1);
            BitSet theSet = visibleSet;

            for (int word = 0; word < wordCount; word++) {
               boolean longBased = StringUtilities.getCharacterSize(words[word]) == 2;
               if (!longBased && allTuplesFound) {
                  boolean cacheUsed = false;
                  if (cache != null) {
                     if (word == 0) {
                        BitSet cacheEntry = cache.getPrimaryEntry(words[word]);
                        if (cacheEntry != null) {
                           primarySet.or(cacheEntry);
                           cacheEntry = cache.getSecondaryEntry(words[word]);
                           if (cacheEntry != null) {
                              theSet.or(cacheEntry);
                              cacheUsed = true;
                           }
                        }
                     } else {
                        BitSet cacheEntry = cache.getSecondaryEntry(words[word]);
                        if (cacheEntry != null) {
                           theSet.or(cacheEntry);
                           cacheUsed = true;
                        }
                     }
                  }

                  if (!cacheUsed) {
                     int key = this.getKey(words[word], 0, words[word].length());
                     int low = this._prefixes.binarySearch(key);
                     int high = this._prefixes.binarySearch(this.nextKey(key));
                     if (low < 0) {
                        low = -(low + 1);
                     }

                     if (high < 0) {
                        high = -(high + 1);
                     }

                     if (high < low) {
                        high = this._prefixes.size();
                     }

                     for (int var28 = low; !this._haltSearch && var28 < high; var28++) {
                        int element = this._prefixes.elementAt(var28);
                        int id = element & 32767;
                        if (id <= this._maxId) {
                           if (word == 0 && (element & 32768) != 0) {
                              primarySet.fastSet(id);
                           }

                           theSet.fastSet(id);
                           if (hitCount != null) {
                              hitCount[id]++;
                           }

                           if (this._haltSearch) {
                              return null;
                           }
                        } else {
                           System.err.println("Object with id " + id + " maxId is " + this._maxId);
                        }
                     }

                     if (cache != null) {
                        if (word == 0) {
                           cache.putPrimaryEntry(words[word], primarySet);
                        }

                        cache.putSecondaryEntry(words[word], theSet);
                     }
                  }
               }

               boolean longVarFound = false;
               long key = this.getKeyLong(words[word], 0, words[word].length());
               int low = this._prefixesLong.binarySearch(key);
               int high = this._prefixesLong.binarySearch(this.nextKey(key));
               if (low < 0) {
                  low = -(low + 1);
               }

               if (high < 0) {
                  high = -(high + 1);
               }

               if (high < low) {
                  high = this._prefixesLong.size();
               }

               for (int var29 = low; !this._haltSearch && var29 < high; var29++) {
                  int element = (int)this._prefixesLong.elementAt(var29);
                  int id = element & 32767;
                  if (id <= this._maxId) {
                     if (word == 0 && (element & 32768) != 0) {
                        primarySet.fastSet(id);
                     }

                     theSet.fastSet(id);
                     if (hitCount != null) {
                        hitCount[id]++;
                     }

                     longVarFound = true;
                  } else {
                     System.err.println("Object with id " + id + " maxId is " + this._maxId);
                  }
               }

               if (this._haltSearch) {
                  return null;
               }

               if (longBased && !longVarFound) {
                  this.adjustWideCharacterSearch(words[word], 0, primarySet, theSet, word, hitCount);
               }

               if (word != 0) {
                  visibleSet.and(theSet);
                  if (word < wordCount - 1) {
                     theSet.reset();
                  }
               } else if (wordCount > 1) {
                  theSet = new BitSet(this._maxId + 1);
               }

               if (this._haltSearch) {
                  return null;
               }
            }

            if (hitCount != null) {
               for (int var30 = visibleSet.getFirstSet(); var30 != -1; var30 = visibleSet.getNextSet(var30 + 1)) {
                  if (hitCount[var30] < targetCount) {
                     visibleSet.fastClear(var30);
                  }
               }
            }

            primarySet.and(visibleSet);
            BitSet secondarySet = visibleSet;
            secondarySet.xor(primarySet);
            return new KeywordPrefixSearchResult(primarySet, secondarySet);
         }
      }
   }

   private void adjustWideCharacterSearch(String word, int offset, BitSet primarySet, BitSet theSet, int wordNumber, byte[] hitCount) {
      StringBuffer sb = new StringBuffer();
      int length = word.length() - offset;

      for (int i = 0; i < 3 && length > 0; length--) {
         char current = word.charAt(i + offset);
         if ((current & '\uff00') != 0) {
            return;
         }

         sb.append(current);
         i++;
      }

      int key = this.getKey(sb.toString(), 0, sb.length());
      int low = this._prefixes.binarySearch(key);
      int high = this._prefixes.binarySearch(this.nextKey(key));
      if (low < 0) {
         low = -(low + 1);
      }

      if (high < 0) {
         high = -(high + 1);
      }

      if (high < low) {
         high = this._prefixes.size();
      }

      for (int i = low; !this._haltSearch && i < high; i++) {
         int element = this._prefixes.elementAt(i);
         int id = element & 32767;
         if (id <= this._maxId) {
            if (wordNumber == 0 && (element & 32768) != 0) {
               primarySet.fastSet(id);
            }

            theSet.fastSet(id);
            if (hitCount != null) {
               hitCount[id]++;
            }
         } else {
            System.err.println("Object with id " + id + " maxId is " + this._maxId);
         }
      }
   }

   public static char getPrefixChar(int prefixCode) {
      return prefixCode >= 0 && prefixCode <= 31 ? PREFIX_CODE_TO_CHAR_MAP[prefixCode] : '\u0000';
   }

   public static boolean startsWithUsingMapping(String string, String prefix) {
      if (string.length() >= prefix.length()) {
         if (StringUtilities.getCharacterSize(string) != 2 && StringUtilities.getCharacterSize(prefix) != 2) {
            for (int index = 0; index < prefix.length(); index++) {
               if (charUnifier[string.charAt(index)] != charUnifier[prefix.charAt(index)]) {
                  return false;
               }
            }

            return true;
         } else {
            return StringUtilities.startsWithIgnoreCaseAndAccents(string, prefix);
         }
      } else {
         return false;
      }
   }
}
