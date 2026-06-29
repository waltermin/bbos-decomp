package net.rim.tid.im.conv.europe.repository;

import java.util.Vector;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.im.conv.SLCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;

public class LearningPrefixTable {
   protected LearningReader _reader;
   protected byte[] _wordsDefTable;
   protected char _lastPrefixChar;
   protected int _prefixLength;
   protected LearningPrefixTable _parent;
   protected int _size;
   protected byte _type;

   public LearningPrefixTable(int aLevel, LearningReader aReader) {
      this._prefixLength = aLevel;
      this._reader = aReader;
   }

   public void getPredictions(ReIterator _1, ResultContainer _2, char[] _3) {
      throw null;
   }

   public void init(byte[] _1, int _2, char _3, LearningPrefixTable _4, int _5) {
      throw null;
   }

   public void add(byte[] aEncoded, int aKeyLength, int aUserParam, int aWordOffset) {
      this.add(aEncoded, aKeyLength, aUserParam, aWordOffset, (byte)2);
   }

   public void add(byte[] _1, int _2, int _3, int _4, byte _5) {
      throw null;
   }

   public boolean removeWord(byte[] aEncodedWord, int aLength) {
      return false;
   }

   public int trim(int aMaxCount) {
      return 0;
   }

   public void trim(TrimController aController, char[] buffer) {
   }

   public void growFileSize(int _1) {
      throw null;
   }

   public void setLevel(int aLevel) {
      this._prefixLength = aLevel;
   }

   public int getEntryNo() {
      throw null;
   }

   public void getEntries(Vector _1, char[] _2) {
      throw null;
   }

   public void getEntries(CustomWordsSyncManager _1, char[] _2, String _3) {
      throw null;
   }

   protected void correctCase(SLCurrentVariant aWord) {
      this._reader.correctCase(aWord);
   }

   public void getMatches(RegularExpression _1, RegularExpressionState _2, ComplexTableRegularExpressionState _3, char[] _4, ResultContainer _5) {
      throw null;
   }

   public boolean matches(RegularExpression _1, RegularExpressionState _2, ComplexTableRegularExpressionState _3) {
      throw null;
   }
}
