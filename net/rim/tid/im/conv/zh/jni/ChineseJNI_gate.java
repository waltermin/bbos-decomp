package net.rim.tid.im.conv.zh.jni;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.tid.itie.LinguisticData;

public class ChineseJNI_gate {
   private byte[][] _wordlist;
   private int[] _indexes = new int[0];
   private boolean _isNativeSupportBuilt = true;
   private static final long CH_JNI_GATE_REGISTRY_NAME = 637717017681252726L;
   private static ChineseJNI_gate _this;

   public static ChineseJNI_gate getChineseJNIGate() {
      return _this;
   }

   public boolean isNativeSupportBuilt() {
      return this._isNativeSupportBuilt;
   }

   public boolean setLocale(int aLocaleCode) {
      this._wordlist = new byte[0][];
      this._indexes = new int[0];
      return setLocaleToNative(aLocaleCode);
   }

   public boolean initWordlist(LinguisticData aData) {
      boolean isGenericType = aData.getType() >> 4 == 1;
      int res = initWordlistReader(aData.getData(), isGenericType);
      if (res != 0) {
         byte[][] data = aData.getData();
         if (!isGenericType && this._wordlist.length != 0) {
            Arrays.insertAt(this._indexes, data.length, 0);

            for (int i = 0; i < data.length; i++) {
               Arrays.insertAt(this._wordlist, data[i], i);
            }
         } else {
            Arrays.add(this._indexes, data.length);

            for (int i = 0; i < data.length; i++) {
               Arrays.add(this._wordlist, data[i]);
            }
         }
      }

      return res != 0;
   }

   private void removeWordlist(int anIndex) {
      if (this._indexes.length == 1) {
         this._wordlist = new byte[0][];
      } else {
         int startIndex = 0;

         for (int i = 0; i < anIndex; i++) {
            startIndex += this._indexes[anIndex];
         }

         for (int var4 = 0; var4 < this._indexes[anIndex]; var4++) {
            Arrays.removeAt(this._wordlist, startIndex);
         }
      }
   }

   public int unloadLinguisticData(int anIndex) {
      int ret = 2;
      if (removeWordlistReader(anIndex)) {
         this.removeWordlist(anIndex);
         Arrays.removeAt(this._indexes, anIndex);
         ret = 1;
      }

      return ret;
   }

   private native void init();

   private ChineseJNI_gate() {
   }

   private static native boolean setLocaleToNative(int var0);

   public static native int getPredictiveSylCandidates(int[] var0, int var1, char[] var2);

   public static native void enableFuzzySyllable(char var0, boolean var1);

   public static native boolean checkFuzzy(char var0, byte[] var1, int var2);

   public static native char findBopomofoSyllable(int var0, int var1);

   public static native boolean checkPinyinChar(char[] var0, byte[] var1, int var2, int var3);

   public static native boolean isPinyinSyllableExist(char var0, int var1);

   public static native int getChineseCharactersForSyllable(int var0, int var1, int[] var2, char[] var3, int var4);

   public static native int getMaxOneCharWordFreq(int var0, int var1);

   public static native int getMaxFrequencyFor(int[] var0, int var1);

   public static native int getIndexOfCharacterForPin(char var0, int[] var1, char var2);

   public static native char getCandidateByIndex(char var0, byte var1, byte var2);

   public static native int getPinyinBufferByIndex(char var0, byte[] var1);

   public static native boolean isValidFuzzyPrefixFor(char var0, byte[] var1, int var2);

   private static native int initWordlistReader(byte[][] var0, boolean var1);

   private static native boolean removeWordlistReader(int var0);

   public static native boolean isVariantExist(char[] var0, byte[] var1, byte[] var2, int var3, int var4, byte[] var5);

   public static native Object getWords(char[] var0, byte[] var1, byte[] var2, int var3, int var4, byte[] var5);

   public static int getPredictiveWords(
      char[] pins,
      byte[] tones,
      byte[] counts,
      char[] prefix,
      int prefixLength,
      int aFromIndex,
      int aCount,
      int[] anIds,
      int anIdsCount,
      char[] aResultWords,
      byte[] aResultWordsLengths,
      byte[] aResultWordsStartIndexes,
      char[] aPredictiveWordsIds,
      byte[] aPredictiveWordsTones,
      byte[] learningData,
      boolean lookForward,
      boolean convertIds,
      boolean moveStartPosition
   ) {
      return getPredictiveWords(
         pins,
         tones,
         counts,
         prefix,
         prefixLength,
         aFromIndex,
         aCount,
         anIds,
         anIdsCount,
         aResultWords,
         aResultWordsLengths,
         null,
         aResultWordsStartIndexes,
         aPredictiveWordsIds,
         aPredictiveWordsTones,
         learningData,
         lookForward,
         convertIds,
         moveStartPosition
      );
   }

   public static native int getPredictiveWords(
      char[] var0,
      byte[] var1,
      byte[] var2,
      char[] var3,
      int var4,
      int var5,
      int var6,
      int[] var7,
      int var8,
      char[] var9,
      byte[] var10,
      byte[] var11,
      byte[] var12,
      char[] var13,
      byte[] var14,
      byte[] var15,
      boolean var16,
      boolean var17,
      boolean var18
   );

   public static native int getWordsFor(
      char[] var0, byte[] var1, byte[] var2, int var3, int var4, char[] var5, byte[] var6, byte[] var7, byte[] var8, boolean var9
   );

   public static native void initLearningWordlist(byte[] var0);

   public static native int loadLearningWordlist(byte[] var0);

   public static native void clearLearningWordlist(byte[] var0);

   public static native int modifyData(byte[] var0, int var1, boolean var2, boolean var3, byte[] var4, byte[] var5);

   public static native Object getAllWords(byte[] var0);

   public static native int getLearningWordlistSize(byte[] var0);

   public static int getSegmentation(char[] aPinIndexes, byte[] aTones, byte[] anIndexes, int aFromIndex, int aCount, byte[] learningData, byte[] aResult) {
      return getSegmentation(aPinIndexes, aTones, anIndexes, aFromIndex, aCount, learningData, aResult, true);
   }

   public static int getSegmentation(
      char[] aPinIndexes, byte[] aTones, byte[] anIndexes, int aFromIndex, int aCount, byte[] learningData, byte[] linkLearningData, byte[] aResult
   ) {
      return getSegmentation(aPinIndexes, aTones, anIndexes, aFromIndex, aCount, learningData, linkLearningData, aResult, true);
   }

   public static native int getSegmentation(char[] var0, byte[] var1, byte[] var2, int var3, int var4, byte[] var5, byte[] var6, boolean var7);

   public static native int getSegmentation(char[] var0, byte[] var1, byte[] var2, int var3, int var4, byte[] var5, byte[] var6, byte[] var7, boolean var8);

   public static native void setAlgorithmDirection(boolean var0);

   public static native int getComplexSegmentation(char[] var0, byte[] var1, int var2, byte[] var3, int[] var4, byte[] var5);

   public static native int buildLearningIndex(byte[] var0);

   public static native int getLearningIndex(char[] var0);

   public static native int addLearningEntry(byte[] var0, byte[] var1, int[] var2);

   public static native int eraseLearningDictionaryChunk(byte[] var0, int[] var1);

   public static native int getLearningEntries(byte[] var0, byte[] var1, char[] var2, char[] var3, byte[] var4, int var5);

   public static native int entryExists(byte[] var0, byte[] var1);

   public static native int addLearningEntry(byte[] var0, char[] var1, int[] var2);

   public static native int getLearningEntries(byte[] var0, char[] var1, char[] var2, char[] var3, byte[] var4, int var5);

   public static native int entryExists(byte[] var0, char[] var1);

   public static native int convertIdToIndex(int var0);

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _this = (ChineseJNI_gate)ar.getOrWaitFor(637717017681252726L);
      if (_this == null) {
         _this = new ChineseJNI_gate();
         ar.put(637717017681252726L, _this);

         try {
            _this.init();
            return;
         } catch (Throwable t) {
            _this._isNativeSupportBuilt = false;
            System.err.println("Chinese native support is not built!");
            long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
            RIMGlobalMessagePoster.postGlobalEvent(LOGWORTHY_REPORT_REQUEST, 0, 0, "Chinese NS is not built!", null);
         }
      }
   }
}
