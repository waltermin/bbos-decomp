package net.rim.tid.text;

import net.rim.vm.Array;

public final class AttribRunList {
   public int[] iOffsets;
   public int[] iAttributes;
   public int iOffsetNo;
   private static final int OFFSETS_ARRAY_GROWTH_SIZE;
   private static AttribRunList iTempList = new AttribRunList(2);

   public AttribRunList() {
      this(10);
   }

   public AttribRunList(int aSize) {
      this.iOffsets = new int[aSize];
      this.iAttributes = new int[aSize];
   }

   public AttribRunList(AttribRunList aList) {
      this(aList.iOffsets.length);
      int len = aList.iOffsets.length;
      System.arraycopy(aList.iOffsets, 0, this.iOffsets, 0, len);
      System.arraycopy(aList.iAttributes, 0, this.iAttributes, 0, len - 1);
      this.iOffsetNo = aList.iOffsetNo;
   }

   private AttribRunList(AttribRunList aList, int start, int end) {
      if (end - start == 0) {
         this.iOffsets = new int[2];
         this.iAttributes = new int[2];
      } else {
         this.iOffsets = new int[10];
         this.iAttributes = new int[10];
         int[] offsets = aList.iOffsets;
         int[] attributes = aList.iAttributes;
         int len = aList.iOffsetNo;
         int firstIndex = 0;

         while (firstIndex < len && start >= offsets[firstIndex]) {
            firstIndex++;
         }

         this.iAttributes[this.iOffsetNo++] = attributes[firstIndex - 1];

         for (int lastIndex = firstIndex; offsets[lastIndex] < end; lastIndex++) {
            this.insureSize(this.iOffsetNo + 1);
            this.iAttributes[this.iOffsetNo] = attributes[lastIndex];
            this.iOffsets[this.iOffsetNo] = offsets[lastIndex] - start;
            this.iOffsetNo++;
         }

         this.insureSize(this.iOffsetNo + 1);
         this.iOffsets[this.iOffsetNo++] = end - start;
      }
   }

   public final void init(int aSet, int aLength) {
      this.iOffsets[1] = aLength;
      this.iOffsetNo = aLength == 0 ? 0 : 2;
      this.iAttributes[0] = aSet;
   }

   public final int getSize() {
      return this.iOffsetNo - 1;
   }

   public final void insertAttrib(int aSet, int aPos, int aLength) {
      iTempList.init(aSet, aLength);
      this.replace(aPos, aPos, iTempList);
   }

   public final void replace(int aStart, int aEnd, AttribRunList aList) {
      if (aList == null || aList.iOffsetNo == 0) {
         if (aStart == aEnd) {
            return;
         }

         aList = null;
      }

      if (this.iOffsetNo == 0) {
         this.insertOffsets(0, aStart, aList);
      } else {
         int start_id = -1;
         int end_id = -1;
         int len = this.iOffsetNo;

         for (int i = 0; i < len; i++) {
            if (start_id == -1 && this.iOffsets[i] >= aStart) {
               start_id = i;
            }

            if (this.iOffsets[i] > aEnd) {
               end_id = i - 1;
               break;
            }

            if (this.iOffsets[i] == aEnd) {
               end_id = i;
               break;
            }
         }

         int id_dif = end_id - start_id;
         int start;
         int end;
         switch (id_dif) {
            case -2:
               start = start_id + 1;
               end = end_id + 1;
               this.iOffsets[start_id] = aStart;
               this.iAttributes[start_id] = this.iAttributes[end_id];
               break;
            case -1:
            default:
               end = start_id;
               start = start_id;
               break;
            case 0:
               start = end = end_id + 1;
               this.iOffsets[end_id] = aStart;
         }

         int dif = aEnd - aStart;

         while (end < this.iOffsetNo) {
            this.iOffsets[start] = this.iOffsets[end] - dif;
            this.iAttributes[start - 1] = this.iAttributes[end - 1];
            end++;
            start++;
         }

         if (id_dif >= 0) {
            this.iOffsetNo -= end_id - start_id;
         }

         if (this.iOffsetNo < 2) {
            this.iOffsetNo = 0;
         }

         this.insertOffsets(start_id, aStart, aList);
         this.combineRuns();
      }
   }

   private final void insertOffsets(int pos, int aStart, AttribRunList aList) {
      if (aList != null) {
         int toInsert = aList.iOffsetNo;
         if (this.iOffsetNo == 0) {
            this.insureSize(toInsert);
            System.arraycopy(aList.iOffsets, 0, this.iOffsets, 0, toInsert);
            System.arraycopy(aList.iAttributes, 0, this.iAttributes, 0, toInsert - 1);
            this.iOffsetNo = aList.iOffsetNo;
         } else {
            int ins_len = aList.iOffsets[toInsert - 1];
            if (this.iOffsets[pos] == aStart) {
               toInsert--;
            }

            this.insertOffset(toInsert, pos);
            int len = aList.iOffsetNo;

            for (int i = 0; i < len; pos++) {
               this.iOffsets[pos] = aList.iOffsets[i] + aStart;
               if (i < len - 1) {
                  this.iAttributes[pos] = aList.iAttributes[i];
               }

               i++;
            }

            while (pos < this.iOffsetNo) {
               this.iOffsets[pos] = this.iOffsets[pos] + ins_len;
               pos++;
            }
         }
      }
   }

   private final void insertOffset(int aNo, int aPos) {
      int old_offset_no = this.iOffsetNo;
      this.iOffsetNo += aNo;
      this.insureSize(this.iOffsetNo);
      if (aNo != 0 && aPos >= 0 && aPos < old_offset_no) {
         int from = aPos == 0 ? aPos : aPos - 1;
         int no = old_offset_no - from - 1;
         System.arraycopy(this.iOffsets, from + 1, this.iOffsets, from + aNo + 1, no);
         System.arraycopy(this.iAttributes, from, this.iAttributes, from + aNo, no);
      }
   }

   private final void insureSize(int size) {
      if (size > this.iOffsets.length) {
         Array.resize(this.iOffsets, this.iOffsetNo + 10);
         Array.resize(this.iAttributes, this.iOffsetNo + 10);
      }
   }

   private final void combineRuns() {
      int last_unique = 0;
      int to = this.iOffsetNo - 1;

      for (int i = 1; i < to; i++) {
         if (this.iAttributes[i - 1] == this.iAttributes[i]) {
            this.iOffsetNo--;
         } else {
            last_unique++;
         }

         if (last_unique != i) {
            this.iOffsets[last_unique + 1] = this.iOffsets[i + 1];
            this.iAttributes[last_unique] = this.iAttributes[i];
         }
      }
   }

   final void setAttrib(int aValsMask, int aVals, int aStart, int aEnd) {
      int range_length = aEnd - aStart;
      if (range_length <= 0) {
         throw new IllegalArgumentException("aStart (" + aStart + "should be less than aEnd (" + aEnd);
      }

      int start_id = -1;
      int end_id = this.iOffsetNo - 1;

      for (int i = 0; i < end_id; i++) {
         if (this.iOffsets[i + 1] > aStart) {
            start_id = i;
            break;
         }
      }

      int first_updated_id = start_id;
      if (this.iOffsets[start_id] != aStart) {
         first_updated_id++;
         this.insertOffset(1, start_id + 1);
         this.iOffsets[start_id + 1] = aStart;
      }

      end_id = this.iOffsetNo - 1;
      int end = end_id;

      for (int i = first_updated_id; i < end; i++) {
         boolean end_loop = false;
         if (this.iOffsets[i + 1] > aEnd) {
            end_id = i;
            end_loop = true;
            if (aEnd > this.iOffsets[i]) {
               this.insertOffset(1, end_id + 1);
               this.iOffsets[i + 1] = aEnd;
            }
         }

         if (this.iOffsets[i] < aEnd) {
            this.iAttributes[i] = this.iAttributes[i] & ~aValsMask;
            this.iAttributes[i] = this.iAttributes[i] | aVals;
         }

         if (end_loop) {
            break;
         }
      }

      this.combineRuns();
   }

   public final AttribRunList sublist(int start, int end) {
      return new AttribRunList(this, start, end);
   }
}
