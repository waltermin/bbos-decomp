package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.tid.im.conv.SLCurrentVariant;

public class PhoneticReplacements {
   protected String[] iData;
   private int iOrigLength;
   private char[] iOrigBuffer;
   private int iStart;
   private int iEnd;
   private int iCurrentDataId;
   private SLCurrentVariant iResult = new SLCurrentVariant();
   private int iMaxGrowSize = -1;

   public PhoneticReplacements() {
      this.iResult._variants = new char[20];
   }

   public void init(char[] aBuffer, int aLen) {
      this.iOrigLength = aLen;
      this.iOrigBuffer = aBuffer;
      this.iStart = this.iEnd = 0;
      this.iCurrentDataId = -1;
   }

   public SLCurrentVariant nextReplacement() {
      label81:
      while (this.iStart < this.iOrigLength) {
         while (this.iStart < this.iOrigLength && this.iCurrentDataId == -1) {
            this.iCurrentDataId = this.binarySearch(this.iOrigBuffer[this.iStart]);
            if (this.iCurrentDataId == -1) {
               this.iStart++;
               this.iEnd++;
            }
         }

         if (this.iStart >= this.iOrigLength) {
            return null;
         }

         int len = this.iEnd - this.iStart + 1;

         while (len != this.iData[this.iCurrentDataId].length()) {
            this.iEnd++;
            len++;
            if (this.iEnd >= this.iOrigLength) {
               this.iStart++;
               this.iEnd = this.iStart;
               this.iCurrentDataId = -1;
               continue label81;
            }

            int dif = this.iOrigBuffer[this.iEnd] - this.iData[this.iCurrentDataId].charAt(len - 1);
            if (dif > 0) {
               this.iCurrentDataId += 2;
               if (this.iOrigBuffer[this.iStart] != this.iData[this.iCurrentDataId].charAt(0)) {
                  this.iStart++;
                  this.iEnd = this.iStart;
                  this.iCurrentDataId = -1;
                  continue label81;
               }

               this.iEnd = this.iStart;
               len = 1;
            } else if (dif < 0) {
               this.iStart++;
               this.iEnd = this.iStart;
               this.iCurrentDataId = -1;
               continue label81;
            }
         }

         int repl_len = this.iData[this.iCurrentDataId + 1].length();
         int res_len = this.iOrigLength - len + repl_len;
         if (this.iResult._variants.length < res_len) {
            this.iResult._variants = new char[res_len];
         }

         System.arraycopy(this.iOrigBuffer, 0, this.iResult._variants, 0, this.iStart);
         this.iData[this.iCurrentDataId + 1].getChars(0, repl_len, this.iResult._variants, this.iStart);
         System.arraycopy(this.iOrigBuffer, this.iStart + len, this.iResult._variants, this.iStart + repl_len, this.iOrigLength - 1 - this.iEnd);
         this.iResult._length = res_len;
         this.iCurrentDataId += 2;
         len = this.iEnd - this.iStart + 1;
         boolean current_group_exausted = this.iCurrentDataId >= this.iData.length || len > this.iData[this.iCurrentDataId].length();
         if (!current_group_exausted) {
            for (int i = 0; i < len; i++) {
               if (this.iOrigBuffer[this.iStart + i] != this.iData[this.iCurrentDataId].charAt(i)) {
                  current_group_exausted = true;
                  break;
               }
            }
         }

         if (current_group_exausted) {
            this.iStart++;
            this.iEnd = this.iStart;
            this.iCurrentDataId = -1;
         }

         return this.iResult;
      }

      return null;
   }

   public int getMaxGrowSize() {
      if (this.iMaxGrowSize == -1) {
         this.iMaxGrowSize = 1;
         if (this.iData != null) {
            for (int i = 0; i < this.iData.length; i += 2) {
               int growth = this.iData[i + 1].length() - this.iData[i].length();
               if (growth > this.iMaxGrowSize) {
                  this.iMaxGrowSize = growth;
               }
            }
         }
      }

      return this.iMaxGrowSize;
   }

   private int binarySearch(char aFirstLetter) {
      int from = 0;
      int to = this.iData.length / 2;
      if (aFirstLetter >= this.iData[0].charAt(0) && aFirstLetter <= this.iData[this.iData.length - 2].charAt(0)) {
         while (from <= to) {
            int real_half_index = (from + to) / 2;
            int half_index = real_half_index * 2;
            char ch = this.iData[half_index].charAt(0);
            if (ch == aFirstLetter) {
               while (half_index > 0 && this.iData[half_index - 2].charAt(0) == aFirstLetter) {
                  half_index -= 2;
               }

               return half_index;
            }

            if (ch < aFirstLetter) {
               from = real_half_index + 1;
            } else if (ch > aFirstLetter) {
               to = real_half_index - 1;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }
}
