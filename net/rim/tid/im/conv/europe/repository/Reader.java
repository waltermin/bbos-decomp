package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.europe.CaseCorrector;
import net.rim.tid.im.conv.europe.spellcheck.Word;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;
import net.rim.tid.io.ContinuousByteArray;
import net.rim.tid.io.ContinuousByteArray$Position;
import net.rim.tid.io.ContinuousInputStream;
import net.rim.tid.itie.LinguisticData;
import net.rim.tid.util.SLTextDataContainer;
import net.rim.tid.util.Utils;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class Reader implements ReaderConstants {
   private ContinuousByteArray iBa;
   private DataInputStream iDis;
   private byte[] iBiGrammOffsets;
   private int iBiGrammOffsetsStart;
   private ContinuousByteArray iWordsDefinitionTables;
   String iAlphabet;
   IntIntHashtable iAlphabetLookup = new IntIntHashtable();
   private String iBiGramAlphabet;
   private int iBiGramAlphabetLen;
   private IntIntHashtable iBiGramAlphabetLookup = new IntIntHashtable();
   private String iName = "";
   Reader$SubstitutionTable iSubstTable;
   int iSubstShift = 0;
   private Reader$Header iHeader;
   private Locale iLocale;
   boolean iIncludeFrequency;
   private boolean iIsComplex;
   private int _ID;
   private ReIterator iReIterator;
   private WeakReference _tempBufferWR = new WeakReference(null);
   private WeakReference _wordBufferWR = new WeakReference(null);
   private char[] iCaseCorrectionBuffer;
   ComplexPrefixTable[] iComplexPrefixTables;
   private SimplePrefixTable iSimplePrefixTable = new SimplePrefixTable(0, this);
   private ExtendedCurrentVariant _tempInsWord = new ExtendedCurrentVariant();
   byte _caseControlCount;
   public byte CAPITAL_PAIR_BYTE;
   private Reader$EditDistanceFrequencyModifier _spellCheckFrequencyModifier;
   private PredictiveWordMatch _spellCheckVariantsExpr;
   private PredictiveWordMatchState _spellCheckVariantsState;
   public static final int WORDLIST_MAJOR_VERSION = 1;

   public SimplePrefixTable getSimplePrefixTable(int aLevel) {
      this.iSimplePrefixTable.setLevel(aLevel);
      return this.iSimplePrefixTable;
   }

   public Reader() {
   }

   public Reader(Reader aReader) {
      this.iHeader = aReader.iHeader;
      this.iLocale = aReader.iLocale;
      this.iAlphabet = aReader.iAlphabet;

      for (int i = 0; i < this.iAlphabet.length(); i++) {
         char ch = this.iAlphabet.charAt(i);
         this.iAlphabetLookup.put(ch, i);
      }

      this.iSubstShift = aReader.iSubstShift;
      this.iSubstTable = aReader.iSubstTable;
      ContinuousByteArray wdt = aReader.iWordsDefinitionTables;
      this.iWordsDefinitionTables = wdt.subArray(0, wdt.length());
      this.iBiGramAlphabet = aReader.iBiGramAlphabet;
      this.iBiGramAlphabetLen = aReader.iBiGramAlphabetLen;

      for (int i = 0; i < this.iBiGramAlphabet.length(); i++) {
         char ch = this.iBiGramAlphabet.charAt(i);
         this.iBiGramAlphabetLookup.put(ch, i);
      }

      this.iBiGrammOffsetsStart = aReader.iBiGrammOffsetsStart;
      this.iBiGrammOffsets = aReader.iBiGrammOffsets;
      this.iName = aReader.iName;
      this.iIncludeFrequency = aReader.iIncludeFrequency;
      this.iReIterator = new ReIterator(this.iHeader.iMaxLevel + 1);
      int len = this.getLongestWordLength();
      this.iCaseCorrectionBuffer = new char[len];
      this.iComplexPrefixTables = new ComplexPrefixTable[this.iHeader.iMaxLevel];

      for (int i = 0; i < this.iHeader.iMaxLevel; i++) {
         this.iComplexPrefixTables[i] = new ComplexPrefixTable(i + 2, this);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean load(byte[][] aData) {
      ContinuousInputStream bais = new ContinuousInputStream(aData);

      try {
         this.readStream(bais);
         this.init();
         return this.validityCheck();
      } catch (Throwable var5) {
         e.printStackTrace();
         return false;
      }
   }

   public int loadLinguisticData(LinguisticData aData) {
      if (!this.load(aData.getData())) {
         System.out.println("WARNING " + aData.getDiagnosticMessage() + " in class " + this.getClass().getName());
         return 2;
      } else {
         this._ID = aData.getID();
         this.iHeader.type = aData.getType();
         return 1;
      }
   }

   public int getLongestWordLength() {
      return this.iHeader.iMaxWordLength;
   }

   public int getID() {
      return this._ID;
   }

   public int getMinWordLength() {
      return this.iHeader.iMinWordLength;
   }

   public int getMaxNestingLevel() {
      return this.iHeader.iMaxLevel;
   }

   public int get1CharWordFreq(char aCh) {
      char ch = CaseCorrector.toLowerCase(aCh, this.iLocale);
      int id = this.iBiGramAlphabet.indexOf(ch);
      return this.iHeader.i1CharWordNo != 0 && id >= 0 && id < this.iHeader.i1CharWordNo && (ch != aCh || (this.iHeader.i1CharWordUpperCase & 1 << id) <= 0)
         ? Math.max(65535 - 65535 * (id + 1) / this.iHeader.i1CharWordNo, 1)
         : 0;
   }

   private void init() throws Exception {
      this.iHeader = new Reader$Header();
      this.iHeader.read(this.iDis);
      if (this.iHeader.version_major != 1) {
         throw new Exception("Incorrect wordlist version " + this.iHeader.version_major + '.' + this.iHeader.version_minor + " . Must be " + 1 + ".x .");
      }

      if (this.iHeader.iLocale != null) {
         this.iLocale = Locale.get(this.iHeader.iLocale, "");
      } else {
         this.iLocale = null;
      }

      this.iIncludeFrequency = (this.iHeader.type & 1) == 1;
      if (this.iReIterator == null) {
         this.iReIterator = new ReIterator(this.iHeader.iMaxLevel + 1);
      } else {
         this.iReIterator.setMaxStackSize(this.iHeader.iMaxLevel + 1);
      }

      int len = this.getLongestWordLength();
      if (this.iCaseCorrectionBuffer == null || len > this.iCaseCorrectionBuffer.length) {
         this.iCaseCorrectionBuffer = new char[len];
      }

      this._caseControlCount = this.iHeader._caseControlCount;
      if (this._caseControlCount > 5) {
         this.CAPITAL_PAIR_BYTE = -123;
      }

      Reader$IndexTable indexTable = new Reader$IndexTable();
      indexTable.read(this.iDis);
      this.iDis.mark(this.iDis.available());
      int offset0 = indexTable.getTableOffset(0);
      this.iDis.reset();
      this.iDis.skip(offset0);
      this.readName(this.iDis);
      int offset1 = indexTable.getTableOffset(1);
      this.iDis.reset();
      this.iDis.skip(offset1);
      this.readAlphabet(this.iDis);
      this.iSubstShift = this.iAlphabet.length();
      int offset2 = indexTable.getTableOffset(2);
      this.iDis.reset();
      this.iDis.skip(offset2);
      this.iSubstTable = new Reader$SubstitutionTable();
      this.iSubstTable.read(this.iDis);
      int offset3 = indexTable.getTableOffset(3);
      this.iDis.reset();
      int gap = this.iBa.length() - this.iDis.available();
      this.defineOffsets(offset3 + gap, this.iBiGramAlphabetLen * this.iBiGramAlphabetLen);
      int offset4 = indexTable.getTableOffset(4);
      this.iWordsDefinitionTables = this.iBa.subArray(offset4 + gap, this.iBa.length());
      this.iDis = null;
      this.iBa = null;
      this.iComplexPrefixTables = new ComplexPrefixTable[this.iHeader.iMaxLevel];

      for (int i = 0; i < this.iHeader.iMaxLevel; i++) {
         this.iComplexPrefixTables[i] = new ComplexPrefixTable(i + 2, this);
      }
   }

   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, byte aCaseType) {
      if (aRes.getCurrentSource() == 14) {
         long start = System.currentTimeMillis();
         this.getSpellCheckPredictions(aPrefix, aRes);
         long end = System.currentTimeMillis();
         System.out.println("Time to get variants: " + (end - start));
      } else {
         char[] orig_prefix = null;
         int len = 0;
         char[] tempBuffer = WeakReferenceUtilities.getCharArray(this._tempBufferWR, 10);
         boolean prefix_modified = false;
         if (aCaseType != 0) {
            orig_prefix = aPrefix.getRe();
            len = aPrefix.getReLength();
            if (len > tempBuffer.length) {
               Array.resize(tempBuffer, len + 5);
            }

            System.arraycopy(orig_prefix, 0, tempBuffer, 0, len);
            prefix_modified = true;
            CaseCorrector.toLowerCase(orig_prefix, 0, len, this.iLocale);
         }

         this.getPredictionsHelper(aPrefix, aRes);
         if (aCaseType == 3 || aCaseType == 4) {
            boolean are_equal = true;

            for (int i = 2; i < len; i++) {
               if (orig_prefix[i] != tempBuffer[i]) {
                  orig_prefix[i] = tempBuffer[i];
                  are_equal = false;
               }
            }

            if (!are_equal) {
               aPrefix.reset();
               this.getPredictionsHelper(aPrefix, aRes);
            }
         }

         if (prefix_modified) {
            System.arraycopy(tempBuffer, 0, orig_prefix, 0, len);
         }
      }
   }

   private void getPredictionsHelper(ReIterator aPrefix, ResultContainer aRes) {
      char[] re = aPrefix.getRe();
      int offset = this.getOffsetFor(re);
      if (offset != -1) {
         char[] wordBuffer = WeakReferenceUtilities.getCharArray(this._wordBufferWR, 50);
         if (this.iIsComplex) {
            ComplexPrefixTable cwd = this.iComplexPrefixTables[0];
            cwd.init(this.iWordsDefinitionTables, offset, re[1]);
            cwd.getPredictions(aPrefix, aRes, wordBuffer);
         } else {
            SimplePrefixTable r = this.getSimplePrefixTable(2);
            r.init(this.iWordsDefinitionTables, offset, re[1]);
            r.getPredictions(aPrefix, aRes, wordBuffer);
         }
      }
   }

   private void readAlphabet(DataInputStream ds) {
      int totalAlph = ds.readByte();
      this.iBiGramAlphabetLen = ds.readByte();
      StringBuffer sb = new StringBuffer();

      for (char i = 0; i < this._caseControlCount; i++) {
         sb.append(i);
      }

      for (int i = 0; i < totalAlph; i++) {
         sb.append(ds.readChar());
      }

      this.iAlphabet = sb.toString();

      for (int i = 0; i < this.iAlphabet.length(); i++) {
         char ch = this.iAlphabet.charAt(i);
         this.iAlphabetLookup.put(ch, i);
      }

      this.iBiGramAlphabet = this.iAlphabet.substring(this._caseControlCount, this._caseControlCount + this.iBiGramAlphabetLen);

      for (int i = 0; i < this.iBiGramAlphabet.length(); i++) {
         char ch = this.iBiGramAlphabet.charAt(i);
         this.iBiGramAlphabetLookup.put(ch, i);
      }
   }

   private void readName(DataInputStream aDis) {
      int length = aDis.readShort();
      StringBuffer nm = new StringBuffer();

      for (int i = 0; i < length; i++) {
         nm.append(aDis.readChar());
      }

      this.iName = nm.toString();
   }

   private void defineOffsets(int startOffset, int length) {
      this.iBiGrammOffsetsStart = startOffset;
      this.iBiGrammOffsets = this.iBa.getData()[0];
   }

   public String getName() {
      return this.iName;
   }

   public int getPriority() {
      return this.iHeader.iPriority;
   }

   public Locale getLocale() {
      return this.iLocale;
   }

   public boolean isFrequencyIncluded() {
      return this.iIncludeFrequency;
   }

   public String getAlphabet() {
      return this.iAlphabet;
   }

   public int getSubstShift() {
      return this.iSubstShift;
   }

   public Reader$SubstitutionTable getSubstitutionTable() {
      return this.iSubstTable;
   }

   public int getType() {
      return this.iHeader.type;
   }

   public int getCaseControlCount() {
      return this._caseControlCount;
   }

   private void readStream(ContinuousInputStream stream) {
      this.iBa = stream.getAllData();
      this.iDis = new DataInputStream(stream);
      this.iDis.mark(this.iDis.available());
   }

   private int getOffsetFor(char[] bigramm) {
      int index1 = this.iBiGramAlphabetLookup.get(bigramm[0]);
      int index2 = this.iBiGramAlphabetLookup.get(bigramm[1]);
      if (index1 != -1 && index2 != -1) {
         int index = this.iBiGramAlphabetLen * index1 + index2;
         int off = index * 4 + this.iBiGrammOffsetsStart;
         int begin = Utils.bytes2Int(this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++]);
         if (begin < 0) {
            this.iIsComplex = true;
            return begin & 2147483647;
         }

         this.iIsComplex = false;
         if (begin >= this.iWordsDefinitionTables.length()) {
            return -1;
         }

         int end = Utils.bytes2Int(this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++]);
         if (end < 0) {
            end &= Integer.MAX_VALUE;
         }

         return begin != end ? begin : -1;
      } else {
         return -1;
      }
   }

   public boolean isInAlphabet(char aCh) {
      return this.iAlphabet.indexOf(aCh, this._caseControlCount) != -1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean matches(RegularExpression expr, RegularExpressionState state, ResultContainer result, boolean isCaseSensitive) {
      char[] buff = new char[50];
      int resultSize = result == null ? 0 : result.getVariantsCount();
      RegularExpression$SimpleCharacterIterator chsStr1 = null;
      RegularExpression$SimpleCharacterIterator chsStr2 = null;
      boolean var23 = false /* VF: Semaphore variable */;

      int ucmark2;
      label245: {
         boolean var20;
         label246: {
            label247: {
               char ch1;
               try {
                  var23 = true;
                  chsStr1 = expr.getAcceptableChars(state, this.iBiGramAlphabet);
                  ComplexTableRegularExpressionState cstate = new ComplexTableRegularExpressionState(true, !isCaseSensitive, expr, state, this.iLocale);
                  Object mark1 = state.newMark();
                  int ucmark1 = cstate.ucMark();
                  buff[0] = 0;

                  label235:
                  while (chsStr1.hasNext()) {
                     state.rollback(mark1);
                     cstate.ucRollback(ucmark1);
                     ch1 = chsStr1.next();
                     if (expr.accept(state, ch1)) {
                        char lc1 = this.recordCaseUsed(cstate, ch1);
                        if (lc1 != buff[0]) {
                           buff[0] = lc1;
                           if (state.isFinal()) {
                              int id = this.iBiGramAlphabet.indexOf(ch1);
                              if (this.iHeader.i1CharWordNo > 0 && id >= 0 && id < this.iHeader.i1CharWordNo) {
                                 if (result == null) {
                                    ucmark2 = 1;
                                    var23 = false;
                                    break label245;
                                 }

                                 ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
                                 this.iCaseCorrectionBuffer[0] = buff[0];
                                 insertedWord.setData(this.iCaseCorrectionBuffer, 0, 1, Math.max(65535 - 65535 * (id + 1) / this.iHeader.i1CharWordNo, 1));
                                 result.insertWord(insertedWord);
                                 if (result.isFull()) {
                                    break;
                                 }
                              }
                           }

                           Object mark2 = state.newMark();
                           ucmark2 = cstate.ucMark();
                           if (chsStr2 != null) {
                              chsStr2.close();
                           }

                           buff[1] = 0;
                           chsStr2 = expr.getAcceptableChars(state, this.iBiGramAlphabet);

                           while (chsStr2.hasNext()) {
                              state.rollback(mark2);
                              cstate.ucRollback(ucmark2);
                              char ch2 = chsStr2.next();
                              if (expr.accept(state, ch2)) {
                                 char lc2 = this.recordCaseUsed(cstate, ch2);
                                 if (lc2 != buff[1]) {
                                    buff[1] = lc2;
                                    int offset = this.getOffsetFor(buff);
                                    if (offset != -1) {
                                       if (this.iIsComplex) {
                                          ComplexPrefixTable cwd = this.iComplexPrefixTables[0];
                                          cwd.init(this.iWordsDefinitionTables, offset, buff[1]);
                                          if (result == null) {
                                             if (cwd.matches(expr, state, cstate)) {
                                                var20 = true;
                                                var23 = false;
                                                break label246;
                                             }
                                          } else {
                                             cwd.getMatches(expr, state, cstate, buff, result);
                                             if (result.isFull()) {
                                                break label235;
                                             }
                                          }
                                       } else {
                                          SimplePrefixTable r = this.getSimplePrefixTable(2);
                                          r.init(this.iWordsDefinitionTables, offset, buff[1]);
                                          if (result == null) {
                                             if (r.matches(expr, state, cstate)) {
                                                var20 = true;
                                                var23 = false;
                                                break label247;
                                             }
                                          } else {
                                             r.getMatches(expr, state, cstate, buff, result);
                                             if (result.isFull()) {
                                                break label235;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  ch1 = result == null ? false : resultSize < result.getVariantsCount();
                  var23 = false;
               } finally {
                  if (var23) {
                     if (chsStr1 != null) {
                        chsStr1.close();
                     }

                     if (chsStr2 != null) {
                        chsStr2.close();
                     }
                  }
               }

               if (chsStr1 != null) {
                  chsStr1.close();
               }

               if (chsStr2 != null) {
                  chsStr2.close();
               }

               return (boolean)ch1;
            }

            if (chsStr1 != null) {
               chsStr1.close();
            }

            if (chsStr2 != null) {
               chsStr2.close();
            }

            return var20;
         }

         if (chsStr1 != null) {
            chsStr1.close();
         }

         if (chsStr2 != null) {
            chsStr2.close();
         }

         return var20;
      }

      if (chsStr1 != null) {
         chsStr1.close();
      }

      if (chsStr2 != null) {
         chsStr2.close();
      }

      return (boolean)ucmark2;
   }

   private char recordCaseUsed(ComplexTableRegularExpressionState cstate, char charUsed) {
      if (CharacterUtilities.isUpperCase(charUsed)) {
         cstate.setUpperCaseUsed(true, charUsed);
         return CaseCorrector.toLowerCase(charUsed, this.iLocale);
      } else {
         cstate.setUpperCaseUsed(false, charUsed);
         return charUsed;
      }
   }

   public void getPredictions(ReIterator aPrefix, ResultContainer aRes) {
      if (aPrefix.getLength() == 1 && aRes.isFast()) {
         this._tempInsWord._offset = 0;
         this._tempInsWord.insureCapacity(1);
         this._tempInsWord._length = 1;
         this._tempInsWord.setValidWord(false);
         int alphLen = this.iBiGramAlphabet.length();

         while (aPrefix.hasMoreSubst()) {
            aPrefix.getChars(this._tempInsWord._variants);
            this._tempInsWord._frequency = this.iBiGramAlphabet.indexOf(this._tempInsWord._variants[0]);
            if (this._tempInsWord._frequency == -1) {
               this._tempInsWord._frequency = 0;
            } else {
               this._tempInsWord._frequency = alphLen - this._tempInsWord._frequency;
            }

            aRes.insert(this._tempInsWord, 1, null, 1);
            aPrefix.nextSubst();
         }
      } else {
         int dot_id = -1;
         char[] re = aPrefix.getRe();
         if (re[0] == 4) {
            dot_id = 0;
         } else if (re[1] == 4) {
            dot_id = 1;
         }

         if (dot_id == -1) {
            this.getPredictions(aPrefix, aRes, (byte)0);
         } else {
            int len = aPrefix.getReLength();
            boolean caseSensitive = this.iReIterator.isCaseSensitive();
            this.iReIterator.setCaseSensitive(aPrefix.isCaseSensitive());

            for (int i = 0; i < this.iBiGramAlphabet.length(); i++) {
               if (i == 0) {
                  re[dot_id] = this.iBiGramAlphabet.charAt(i);
                  this.iReIterator.init(re, 0, len);
               } else {
                  this.iReIterator.reset();
                  this.iReIterator.getRe()[dot_id] = this.iBiGramAlphabet.charAt(i);
               }

               this.getPredictions(this.iReIterator, aRes, (byte)0);
            }

            re[dot_id] = 4;
            this.iReIterator.setCaseSensitive(caseSensitive);
         }
      }
   }

   public void getSpellCheckPredictions(ReIterator aPrefix, ResultContainer aRes) {
      if (this._spellCheckVariantsExpr == null) {
         this._spellCheckVariantsExpr = new PredictiveWordMatch(1, this.iAlphabet);
         this._spellCheckVariantsState = new PredictiveWordMatchState();
      }

      char[] re = aPrefix.getRe();
      int len = aPrefix.getReLength();
      if (re.length < len + 2) {
         Array.resize(re, len + 2);
      }

      if (this._spellCheckFrequencyModifier == null) {
         this._spellCheckFrequencyModifier = new Reader$EditDistanceFrequencyModifier(this, null);
      }

      this._spellCheckFrequencyModifier.setUserInput(re, len);
      aRes.setFrequencyModifier(this._spellCheckFrequencyModifier);
      byte source = aRes.getCurrentSource();
      aRes.setCurrentSource((byte)14);
      Word word = new Word();
      word.init(re, 0, len, this.iLocale);
      this._spellCheckVariantsExpr.setWord(word, this.iLocale, null);
      this._spellCheckVariantsState.set(this._spellCheckVariantsExpr);
      this.matches(this._spellCheckVariantsExpr, this._spellCheckVariantsState, aRes, false);
      aRes.setFrequencyModifier(null);
      aRes.setCurrentSource(source);
   }

   public void getPredictions(String aPrefix, ResultContainer aRes) {
      if (aPrefix.length() >= 2) {
         this.iReIterator.init(aPrefix);
         byte case_type = CaseCorrector.classifyCase(aPrefix);
         this.getPredictions(this.iReIterator, aRes, case_type);
      }
   }

   public void getPredictions(char[] aPrefix, int aLen, ResultContainer aRes) {
      this.getPredictions(aPrefix, 0, aLen, aRes);
   }

   public void getPredictions(char[] aPrefix, int aOffset, int aLen, ResultContainer aRes) {
      if (aLen >= 2) {
         byte case_type = CaseCorrector.classifyCase(aPrefix, aOffset, aLen);
         int dot_id = -1;
         if (aPrefix[aOffset] == 4) {
            dot_id = aOffset;
         } else if (aPrefix[aOffset + 1] == 4) {
            dot_id = aOffset + 1;
         }

         if (dot_id != -1) {
            int rel_dot_id = dot_id - aOffset;

            for (int i = 0; i < this.iBiGramAlphabet.length(); i++) {
               if (i == 0) {
                  aPrefix[dot_id] = this.iBiGramAlphabet.charAt(i);
                  this.iReIterator.init(aPrefix, aOffset, aLen);
               } else {
                  this.iReIterator.reset();
                  this.iReIterator.getRe()[rel_dot_id] = this.iBiGramAlphabet.charAt(i);
               }

               this.getPredictions(this.iReIterator, aRes, case_type);
            }

            aPrefix[dot_id] = 4;
         } else {
            this.iReIterator.init(aPrefix, aOffset, aLen);
            this.getPredictions(this.iReIterator, aRes, case_type);
         }
      }
   }

   private boolean validityCheck() {
      int index = (this.iBiGramAlphabetLen + 1) * (this.iBiGramAlphabetLen - 1);
      int off = index * 4 + this.iBiGrammOffsetsStart;
      int offset = Utils.bytes2Int(this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++], this.iBiGrammOffsets[off++]);
      if (offset < 0) {
         offset &= Integer.MAX_VALUE;
      }

      ContinuousByteArray$Position pos = this.iWordsDefinitionTables.getPosition(offset - 1);
      return pos.iBlock != null;
   }

   public void getPredictionsFor1CharWords(SLTextDataContainer aRegexpPrefixes, ResultContainer aRes) {
      int count = aRegexpPrefixes._count;
      char[] prefixes = aRegexpPrefixes._words;

      for (int i = 0; i < count; i++) {
         char ch = prefixes[i];
         int freq = this.iBiGramAlphabet.indexOf(ch);
         if (freq == -1) {
            freq = 0;
         } else {
            freq = this.iBiGramAlphabetLen - freq;
         }

         ExtendedCurrentVariant insertedWord = aRes.getTempInsertedWordContainer();
         insertedWord.setData(prefixes, i, 1, freq);
         insertedWord.setValidWord(false);
         aRes.insert(insertedWord, 1, null, 1);
      }
   }

   void correctCase(SLCurrentVariant aWord) {
      char ch = aWord._variants[aWord._offset + aWord._length - 1];
      if (ch < this._caseControlCount) {
         aWord._length--;
         System.arraycopy(aWord._variants, aWord._offset, this.iCaseCorrectionBuffer, 0, aWord._length);
         aWord._variants = this.iCaseCorrectionBuffer;
         aWord._offset = 0;
         if (ch == 5) {
            aWord._hadCaseControl = false;
         } else {
            if (ch == 2) {
               CaseCorrector.toUpperCase(this.iCaseCorrectionBuffer, aWord._length, this.iLocale);
            } else {
               if (ch == 1 || ch == 4) {
                  this.iCaseCorrectionBuffer[0] = CaseCorrector.toUpperCase(this.iCaseCorrectionBuffer[0], this.iLocale);
               }

               if (ch == 3 || ch == 4) {
                  this.iCaseCorrectionBuffer[1] = CaseCorrector.toUpperCase(this.iCaseCorrectionBuffer[1], this.iLocale);
               }
            }

            aWord._hadCaseControl = true;
         }
      } else {
         aWord._hadCaseControl = false;
      }
   }
}
