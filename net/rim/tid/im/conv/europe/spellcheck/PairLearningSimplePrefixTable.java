package net.rim.tid.im.conv.europe.spellcheck;

import java.util.Vector;
import net.rim.tid.OTAsync.CustomWordsSyncManager;
import net.rim.tid.im.conv.europe.repository.ComplexTableRegularExpressionState;
import net.rim.tid.im.conv.europe.repository.LearningGlobalAlphabet;
import net.rim.tid.im.conv.europe.repository.LearningReader;
import net.rim.tid.im.conv.europe.repository.LearningSimplePrefixTable;
import net.rim.tid.im.conv.europe.repository.RegularExpression;
import net.rim.tid.im.conv.europe.repository.RegularExpressionState;
import net.rim.tid.im.conv.repository.ExtendedCurrentVariant;
import net.rim.tid.im.conv.repository.ReIterator;
import net.rim.tid.im.conv.repository.ResultContainer;

public class PairLearningSimplePrefixTable extends LearningSimplePrefixTable {
   protected PairLearningPrefixTableHelper _helper = new PairLearningPrefixTableHelper();
   private static final int MAX_INVOCATION_COUNT;

   public PairLearningSimplePrefixTable(int aLevel, LearningReader aReader, byte type) {
      super(aLevel, aReader);
      super._type = type;
   }

   @Override
   public void getPredictions(ReIterator aPrefix, ResultContainer aRes, char[] aBuffer) {
      if (!aPrefix.hasWildcards()) {
         this.getPredictions(aPrefix.getRe(), aPrefix.getReLength(), aRes, aBuffer);
      } else {
         int offset = super._wordsDataOffset;
         int next_offset = offset;
         LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

         for (int i = 0; i < super._wordsCount; i++) {
            aPrefix.pushState();
            boolean accepted = true;
            offset = next_offset;
            next_offset = offset + (super._wordsDefTable[offset++] & 255);
            int len = super._prefixLength;

            for (byte coded = super._wordsDefTable[offset]; (coded & 128) == 0; coded = super._wordsDefTable[++offset]) {
               char ch = alphabet.charAt(coded);
               if (!aPrefix.accept(ch)) {
                  accepted = false;
                  break;
               }

               aPrefix.nextWildcard();
               len++;
            }

            if (accepted && aPrefix.getCurrentWildcardSubstIndex() != Integer.MAX_VALUE && len == aPrefix.getLength()) {
               this._helper.readAndInsertPairs(super._reader, super._wordsDefTable, aBuffer, super._prefixLength, offset, next_offset, aRes);
            }

            aPrefix.popState();
         }
      }
   }

   public void getPredictions(char[] aPrefix, int aPrefixLen, ResultContainer aRes, char[] aBuffer) {
      int offset = super._wordsDataOffset;
      int next_offset = offset;
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

      for (int i = 0; i < super._wordsCount; i++) {
         int index = super._prefixLength;
         boolean accepted = true;
         offset = next_offset;
         next_offset = offset + (super._wordsDefTable[offset++] & 255);
         int len = super._prefixLength;

         for (byte coded = super._wordsDefTable[offset]; (coded & 128) == 0; coded = super._wordsDefTable[++offset]) {
            char ch = alphabet.charAt(coded);
            if (aPrefix[index] != ch) {
               accepted = false;
               break;
            }

            index++;
            len++;
         }

         if (accepted && len == aPrefixLen) {
            this._helper.readAndInsertPairs(super._reader, super._wordsDefTable, aBuffer, super._prefixLength, offset, next_offset, aRes);
         }
      }
   }

   @Override
   public void getMatches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate, char[] buff, ResultContainer result) {
      PairLearningSimplePrefixTable$SimplePrefixTableWord tableWord = new PairLearningSimplePrefixTable$SimplePrefixTableWord(this);
      PairLearningSimplePrefixTable$SimplePrefixTableWordIterator iter = new PairLearningSimplePrefixTable$SimplePrefixTableWordIterator(this);
      iter.reset();
      long mark = state.mark();
      buff[super._prefixLength - 1] = super._lastPrefixChar;
      boolean needRollback = false;

      label38:
      while (iter.hasNext()) {
         iter.next(tableWord);
         if (needRollback) {
            needRollback = false;
            state.rollback(mark);
         }

         int buffIndex = super._prefixLength;
         int tableWordLen = tableWord.length();
         int actualWordLen = tableWordLen + super._prefixLength;
         if (expr.acceptsLength(state, actualWordLen, true, false)) {
            LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

            for (int i = 0; i < tableWordLen; i++) {
               int coded = tableWord.codedCharAt(i);
               char ch = alphabet.charAt(coded);
               if (!expr.accept(state, ch)) {
                  continue label38;
               }

               needRollback = true;
               buff[buffIndex++] = ch;
            }

            if (state.isFinal()) {
               ExtendedCurrentVariant insertedWord = result.getTempInsertedWordContainer();
               insertedWord.setData(buff, 0, buffIndex, 0);
               result.insertWord(insertedWord);
            }
         }
      }
   }

   @Override
   public boolean matches(RegularExpression expr, RegularExpressionState state, ComplexTableRegularExpressionState cstate) {
      return false;
   }

   @Override
   public void add(byte[] aEncodedPair, int aKeyLength, int aReplacementLength, int aWordOffset, byte aPriority) {
      int encodedStart = super._prefixLength;
      int encodedLength = aKeyLength + 1 + aReplacementLength - super._prefixLength;
      int pairOffset = super._wordsDataOffset + 1;
      int clearOffset = pairOffset - 1;
      int clearLength = encodedLength + 1;
      int offset = super._wordsDataOffset;
      int next_offset = offset;

      label99:
      for (int i = 0; i < super._wordsCount; i++) {
         boolean accepted = true;
         offset = next_offset;
         int size_offset = offset;
         next_offset = offset + (super._wordsDefTable[offset++] & 255);
         int matching_len = super._prefixLength;

         for (byte coded = super._wordsDefTable[offset]; (coded & 128) == 0; coded = super._wordsDefTable[++offset]) {
            if (coded != aEncodedPair[matching_len] || matching_len == aKeyLength) {
               accepted = false;
               break;
            }

            if (++matching_len == aKeyLength && (super._wordsDefTable[offset + 1] & 128) != 0) {
               encodedStart = aKeyLength;
               encodedLength = clearLength = 1 + aReplacementLength;
               clearOffset = next_offset;
               pairOffset = next_offset;
               int replacement_group_offset = ++offset;

               while (offset < next_offset) {
                  int invocationCountIndex = offset++;
                  matching_len = 0;
                  accepted = true;

                  for (byte var28 = super._wordsDefTable[offset]; offset < next_offset && (var28 & 128) == 0; var28 = super._wordsDefTable[++offset]) {
                     if (accepted) {
                        if (var28 == aEncodedPair[aKeyLength + 1 + matching_len] && matching_len != aReplacementLength) {
                           matching_len++;
                           if (matching_len == aReplacementLength && ((super._wordsDefTable[offset + 1] & 128) != 0 || offset + 1 == next_offset)) {
                              int count = (super._wordsDefTable[invocationCountIndex] & 127) + 1;
                              super._wordsDefTable[invocationCountIndex] = (byte)(count | 128);
                              if (count == 127) {
                                 for (int var24 = replacement_group_offset; var24 < next_offset; var24++) {
                                    if ((super._wordsDefTable[var24] & 128) != 0) {
                                       super._wordsDefTable[var24] = (byte)((super._wordsDefTable[var24] & 127) >>> 1 | 128);
                                    }
                                 }
                              }

                              encodedLength = 0;
                              break label99;
                           }
                        } else {
                           accepted = false;
                        }
                     }
                  }
               }

               super._wordsDefTable[size_offset] = (byte)((super._wordsDefTable[size_offset] & 255) + encodedLength);
               break label99;
            }
         }
      }

      if (encodedLength > 0) {
         System.arraycopy(super._wordsDefTable, clearOffset, super._wordsDefTable, clearOffset + clearLength, super._reader.getFileSize() - clearOffset);
         System.arraycopy(aEncodedPair, encodedStart, super._wordsDefTable, pairOffset, encodedLength);
         if (encodedStart == super._prefixLength) {
            super._wordsDefTable[super._wordsDataOffset - 1]++;
            super._wordsDefTable[clearOffset] = (byte)clearLength;
         }

         this.growFileSize(clearLength);
      }
   }

   @Override
   public int trim(int aMaxCount) {
      int offset = super._wordsDataOffset;
      int next_offset = offset;
      int total_trimmed_size = 0;

      for (int i = 0; i < super._wordsCount; i++) {
         offset = next_offset;
         int pair_start = offset;
         int size = super._wordsDefTable[offset] & 255;
         next_offset = offset++ + size;

         for (byte coded = super._wordsDefTable[offset]; (coded & 128) == 0; coded = super._wordsDefTable[++offset]) {
            if ((super._wordsDefTable[offset + 1] & 128) != 0) {
               boolean trim_key = true;
               int trim_start = -1;
               int trim_len = 0;
               int trimmed_size = 0;
               offset++;

               while (offset < next_offset) {
                  int invocation_count = super._wordsDefTable[offset] & 127;
                  boolean to_trim = invocation_count <= aMaxCount;
                  if (to_trim) {
                     if (trim_start == -1) {
                        trim_start = offset;
                     }

                     trim_len++;
                  } else if (trim_start != -1) {
                     if (offset - trim_start != trim_len) {
                        throw new Object("");
                     }

                     System.arraycopy(super._wordsDefTable, offset, super._wordsDefTable, trim_start, super._reader.getFileSize() - offset);
                     trimmed_size += trim_len;
                     next_offset -= trim_len;
                     offset -= trim_len;
                     trim_len = 0;
                     trim_start = -1;
                     trim_key = false;
                  }

                  for (byte var19 = super._wordsDefTable[++offset]; offset < next_offset && (var19 & 128) == 0; var19 = super._wordsDefTable[++offset]) {
                     if (to_trim) {
                        trim_len++;
                     }
                  }
               }

               if (trim_start != -1 && trim_key) {
                  System.arraycopy(super._wordsDefTable, offset, super._wordsDefTable, pair_start, super._reader.getFileSize() - offset);
                  trimmed_size += offset - pair_start;
                  next_offset = pair_start;
                  super._wordsCount--;
                  i--;
                  super._wordsDefTable[super._wordsDataOffset - 1] = (byte)super._wordsCount;
               } else {
                  if (trim_start != -1) {
                     if (offset - trim_start != trim_len) {
                        throw new Object("");
                     }

                     System.arraycopy(super._wordsDefTable, offset, super._wordsDefTable, trim_start, super._reader.getFileSize() - offset);
                     trimmed_size += trim_len;
                     next_offset -= trim_len;
                  }

                  if (trimmed_size > 0) {
                     size -= trimmed_size;
                     super._wordsDefTable[pair_start] = (byte)size;
                  }
               }

               total_trimmed_size += trimmed_size;
               break;
            }
         }
      }

      this.growFileSize(-total_trimmed_size);
      return total_trimmed_size;
   }

   @Override
   public void getEntries(Vector aEntries, char[] aBuffer) {
   }

   @Override
   public void getEntries(CustomWordsSyncManager manager, char[] buf, String locale) {
      buf[super._prefixLength - 1] = super._lastPrefixChar;
      int offset = super._wordsDataOffset;
      int nextWordOffset = offset;
      LearningGlobalAlphabet alphabet = super._reader.getAlphabet();

      for (int i = 0; i < super._wordsCount; i++) {
         int bufIndex = super._prefixLength;
         offset = nextWordOffset;
         nextWordOffset = offset + (super._wordsDefTable[offset++] & 255);

         byte coded;
         for (coded = super._wordsDefTable[offset]; (coded & 128) == 0; coded = super._wordsDefTable[++offset]) {
            char ch = alphabet.charAt(coded);
            buf[bufIndex++] = ch;
         }

         int freq = coded & 127;
         buf[bufIndex++] = '+';
         buf[bufIndex++] = '+';

         while (++offset < nextWordOffset) {
            coded = super._wordsDefTable[offset];
            char ch = alphabet.charAt(coded);
            buf[bufIndex++] = ch;
         }

         manager.addWord(buf, 0, bufIndex, freq, locale, super._type);
      }
   }

   static byte[] access$000(PairLearningSimplePrefixTable x0) {
      return x0._wordsDefTable;
   }

   static LearningReader access$100(PairLearningSimplePrefixTable x0) {
      return x0._reader;
   }

   static int access$200(PairLearningSimplePrefixTable x0) {
      return x0._wordsDataOffset;
   }

   static int access$300(PairLearningSimplePrefixTable x0) {
      return x0._wordsCount;
   }

   static byte[] access$400(PairLearningSimplePrefixTable x0) {
      return x0._wordsDefTable;
   }

   static byte[] access$500(PairLearningSimplePrefixTable x0) {
      return x0._wordsDefTable;
   }
}
