package net.rim.tid.im;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;

public class AwnnInterface {
   private int _encoding;
   private byte[] _encodingData;
   private int _encodingDataOffset;
   public static final int LEARN_DIC_BAD_SIZE = 131072;
   public static final int LEARN_DIC_CORRUPTED = 262144;
   public static final int OUT_OF_MEMORY = 524288;
   public static final int BAD_ARGUMENT = 1048576;
   public static final int CORRUPTED_DIC = 2097152;
   public static final int CORRUPTED_ENCODING = 4194304;
   public static final int MISC = 8388608;
   public static final int RELOAD_USER_DATA = 16777216;
   public static final int AWNN_POS_MEISI = 0;
   public static final int AWNN_POS_JINMEI = 1;
   public static final int AWNN_POS_CHIMEI = 2;
   public static final int AWNN_POS_KIGOU = 3;
   public static final int VARIANT_SIZE_PERCENT = 66;
   public static final int SEGMENT_NUMBER_PERCENT = 66;
   private static AwnnInterface _instance;
   private static boolean _dicErrorReported;

   private AwnnInterface(int encoding, byte[] data, int dataOffset) {
      this._encoding = encoding;
      this._encodingData = data;
      this._encodingDataOffset = dataOffset;
   }

   private int getEncoding() {
      return this._encoding;
   }

   private int getEncodingDataOffset() {
      return this._encodingDataOffset;
   }

   private byte[] getEncodingData() {
      return this._encodingData;
   }

   public static AwnnInterface getInstance() {
      if (_instance != null) {
         return _instance;
      } else {
         int[] conversionDataOffset = new int[1];
         TextProcessingRegistry txtRg = TextProcessingRegistry.getInstance();
         int encoding = txtRg.getTextProcessingDataID("Shift_JIS", 0);
         if (encoding == -1) {
            System.out.println("Encoding Shift_JIS is not suported!");
            return null;
         } else {
            byte[][][] bData = (byte[][][])txtRg.getTextProcessingData(encoding, 0, conversionDataOffset);
            if (bData != null && bData.length > 0) {
               _instance = new AwnnInterface(encoding, (byte[])bData[0], conversionDataOffset[0]);
               return _instance;
            } else {
               return null;
            }
         }
      }
   }

   public static int init() {
      return initialize();
   }

   public static int convert(
      StringBuffer text,
      int offset,
      int length,
      int segmentIndex,
      int segmentLength,
      int[] readingLengths,
      char[] convertedText,
      int[] segmentLengths,
      byte[] aWnnConvResultArray
   ) {
      if (text != null
         && length <= text.length()
         && length >= 0
         && offset >= 0
         && segmentLength >= 0
         && length >= offset + segmentLength
         && length <= getMaxInputLength() / 2
         && segmentIndex >= 0
         && segmentIndex < getMaxSegmentNumber()
         && readingLengths != null
         && convertedText != null
         && segmentLengths != null
         && aWnnConvResultArray != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               return convertWithEncoding(
                  text,
                  offset,
                  length,
                  segmentIndex,
                  segmentLength,
                  readingLengths,
                  convertedText,
                  segmentLengths,
                  aWnnConvResultArray,
                  encodingType,
                  encodingData,
                  instance.getEncodingDataOffset()
               );
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int getPredictions(StringBuffer text, int offset, int length, char[] variants, byte[] varLengths, byte[] aWnnCandidateData, boolean flexSearch) {
      if (text != null
         && length >= 0
         && offset >= 0
         && text.length() >= offset + length
         && length <= getMaxInputLength() / 2
         && variants != null
         && varLengths != null
         && aWnnCandidateData != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               return getPredictionsWithEncoding(
                  text, offset, length, variants, varLengths, aWnnCandidateData, flexSearch, encodingType, encodingData, instance.getEncodingDataOffset()
               );
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int getVariants(byte[] aWnnConvResultArray, int segmentIndex, char[] words, byte[] lengths, byte[] aWnnCandidateData) {
      if (aWnnConvResultArray != null
         && segmentIndex >= 0
         && segmentIndex < getMaxSegmentNumber()
         && words != null
         && lengths != null
         && aWnnCandidateData != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               return getVariantsWithEncoding(
                  aWnnConvResultArray, segmentIndex, words, lengths, aWnnCandidateData, encodingType, encodingData, instance.getEncodingDataOffset()
               );
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int getDicWords(int dictType, int maxAmount, char[] readings, byte[] readingLengths, char[] kanjis, byte[] kanjiLengths) {
      if (maxAmount > 0 && readings != null && readingLengths != null && kanjis != null && kanjiLengths != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               return getDicWordsWithEncoding(
                  dictType, maxAmount, readings, readingLengths, kanjis, kanjiLengths, encodingType, encodingData, instance.getEncodingDataOffset()
               );
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int getWordFrom_NJ_RESULT(byte[] nj_result, char[] reading, char[] kanji) {
      if (nj_result != null && reading != null && kanji != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               return getWordFromNjResultWithEncoding(nj_result, reading, kanji, encodingType, encodingData, instance.getEncodingDataOffset());
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static native int loadLearnDic(byte[] var0);

   public static native int loadAddrBookLearnDic(byte[] var0);

   public static native int getLearnDic(byte[] var0);

   public static native int getAddrBookLearnDic(byte[] var0);

   public static int addCustomWord(String reading, String kanji, int aPOS) {
      if (reading != null && kanji != null && aPOS >= 0 && aPOS <= 3) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               int result = addCustomWordWithEncoding(reading, kanji, aPOS, encodingType, encodingData, instance.getEncodingDataOffset());
               if (result < 0) {
                  handleAwnnDicError(getLearningDictionaryId());
               }

               return result;
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int addAddrWord(String reading, String kanji, int pos) {
      if (reading != null && kanji != null) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               int result = addAddrWordWithEncoding(reading, kanji, pos, encodingType, encodingData, instance.getEncodingDataOffset());
               if (result < 0) {
                  handleAwnnDicError(getLearningDictionaryId());
               }

               return result;
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int addLearnedWord(StringBuffer reading, StringBuffer kanji, int aPOS, int connection) {
      if (reading != null && kanji != null && aPOS >= 0 && aPOS <= 3) {
         AwnnInterface instance = getInstance();
         if (instance != null) {
            byte[] encodingData = instance.getEncodingData();
            int encodingType = instance.getEncoding();
            if (encodingData != null && encodingType != 0) {
               int result = addLearnedWordWithEncoding(reading, kanji, aPOS, connection, encodingType, encodingData, instance.getEncodingDataOffset());
               if (result < 0) {
                  handleAwnnDicError(getLearningDictionaryId());
               }

               return result;
            }
         }

         return 4194304;
      } else {
         return 1048576;
      }
   }

   public static int handleAwnnDicError(int dicID) {
      int statusCode = checkUserDictionary(dicID);
      if (statusCode >= 0) {
         return 0;
      }

      int staticBufferVerificatioVar = getStaticBufferVerificationVar();
      int dicResetResult = resetUserDictionary(dicID);
      StringBuffer quincyMessage = new StringBuffer("Awnn Dict Error=");
      quincyMessage.append("0x" + Integer.toString(statusCode, 16));
      quincyMessage.append(" ");
      quincyMessage.append("0x" + Integer.toString(staticBufferVerificatioVar, 16));
      quincyMessage.append(" ");
      quincyMessage.append("0x" + Integer.toString(dicResetResult, 16));
      if (!_dicErrorReported) {
         System.err.println(quincyMessage.toString());
         long RADIO_LOGWORTHY_REPORT_REQUEST = -2816799803471967993L;
         RIMGlobalMessagePoster.postGlobalEvent(RADIO_LOGWORTHY_REPORT_REQUEST, 0, 0, quincyMessage.toString(), null);
         _dicErrorReported = true;
      }

      if (dicResetResult >= 0) {
         _dicErrorReported = false;
      }

      return dicResetResult;
   }

   public static int deleteWord(String reading, String kanji, int dictionaryId) {
      if (reading == null) {
         return 1048576;
      }

      AwnnInterface instance = getInstance();
      if (instance != null) {
         byte[] encodingData = instance.getEncodingData();
         int encodingType = instance.getEncoding();
         if (encodingData != null && encodingType != 0) {
            int result = deleteWordWithEncoding(reading, kanji, encodingType, encodingData, instance.getEncodingDataOffset(), dictionaryId);
            if (result < 0) {
               handleAwnnDicError(getLearningDictionaryId());
            }

            return result;
         }
      }

      return 4194304;
   }

   public static int njlearn(byte[] njResultArray, int index, int connection) {
      int error = learn(njResultArray, index, connection);
      if (error < 0) {
         error = handleAwnnDicError(getLearningDictionaryId());
      }

      return error;
   }

   private static native int learn(byte[] var0, int var1, int var2);

   public static native int undoLearn(int var0);

   private static native int initialize();

   private static native int convertWithEncoding(
      StringBuffer var0, int var1, int var2, int var3, int var4, int[] var5, char[] var6, int[] var7, byte[] var8, int var9, byte[] var10, int var11
   );

   private static native int getVariantsWithEncoding(byte[] var0, int var1, char[] var2, byte[] var3, byte[] var4, int var5, byte[] var6, int var7);

   private static native int getPredictionsWithEncoding(
      StringBuffer var0, int var1, int var2, char[] var3, byte[] var4, byte[] var5, boolean var6, int var7, byte[] var8, int var9
   );

   private static native int getDicWordsWithEncoding(int var0, int var1, char[] var2, byte[] var3, char[] var4, byte[] var5, int var6, byte[] var7, int var8);

   private static native int addCustomWordWithEncoding(String var0, String var1, int var2, int var3, byte[] var4, int var5);

   private static native int addAddrWordWithEncoding(String var0, String var1, int var2, int var3, byte[] var4, int var5);

   private static native int addLearnedWordWithEncoding(StringBuffer var0, StringBuffer var1, int var2, int var3, int var4, byte[] var5, int var6);

   private static native int deleteWordWithEncoding(String var0, String var1, int var2, byte[] var3, int var4, int var5);

   public static native int getMaxInputLength();

   public static native int getMaxVariantNumber();

   public static native int get_NJ_RESULT_size();

   public static native int getMaxSegmentNumber();

   public static native int getLearningDictionarySize();

   public static native int getUserDictionarySize();

   public static native int getUserDictionaryId();

   public static native int getAddrBookDictionaryId();

   public static native int getLearningDictionaryId();

   public static native int checkUserDictionary(int var0);

   public static native int getStaticBufferVerificationVar();

   public static native int resetUserDictionary(int var0);

   private static native int getWordFromNjResultWithEncoding(byte[] var0, char[] var1, char[] var2, int var3, byte[] var4, int var5);
}
