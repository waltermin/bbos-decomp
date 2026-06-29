package net.rim.device.apps.api.messaging;

import net.rim.device.api.collection.ReadableList;
import net.rim.vm.Array;

class MergedCollection$Merger {
   ReadableList[] sources;
   int[] sourceSize;
   int[] sourceIndex;
   Object[] itemAtSourceIndex;
   long[] valueAtSourceIndex;
   private final MergedCollection this$0;

   MergedCollection$Merger(MergedCollection _1) {
      this.this$0 = _1;
      this.sources = new ReadableList[0];
      this.sourceSize = new int[0];
      this.sourceIndex = new int[0];
      this.itemAtSourceIndex = new Object[0];
      this.valueAtSourceIndex = new long[0];
   }

   public void mergeSources() {
      long start = -System.currentTimeMillis();
      int whichSource = 0;
      int sourceCount = this.this$0._sources.size();
      Array.resize(this.sources, sourceCount);
      Array.resize(this.sourceSize, sourceCount);
      Array.resize(this.sourceIndex, sourceCount);
      Array.resize(this.itemAtSourceIndex, sourceCount);
      Array.resize(this.valueAtSourceIndex, sourceCount);

      for (int var13 = 0; var13 < sourceCount; var13++) {
         this.sources[var13] = (ReadableList)this.this$0._sources.elementAt(var13);
         this.sourceSize[var13] = this.sources[var13].size();
         this.sourceIndex[var13] = 0;
      }

      whichSource = 0;

      while (whichSource < this.sources.length) {
         if (!this.checkIfSourceExhausted(whichSource)) {
            this.itemAtSourceIndex[whichSource] = this.sources[whichSource].getAt(this.sourceIndex[whichSource]);
            this.valueAtSourceIndex[whichSource] = this.this$0._longKeyProviderAdaptor.getLongKey(this.itemAtSourceIndex[whichSource]);
            whichSource++;
         }
      }

      int sourcesLength = this.sources.length;

      while (sourcesLength > 0) {
         int leastSource = 0;
         long leastValue = Long.MAX_VALUE;

         for (int var15 = 0; var15 < sourcesLength; var15++) {
            long currentValue = this.valueAtSourceIndex[var15];
            if (currentValue < leastValue
               || currentValue == leastValue && this.this$0._comparator.compare(this.itemAtSourceIndex[var15], this.itemAtSourceIndex[leastSource]) < 0) {
               leastValue = currentValue;
               leastSource = var15;
            }
         }

         this.this$0._messages.addElement(this.itemAtSourceIndex[leastSource]);
         long priorValue = this.valueAtSourceIndex[leastSource];
         boolean loggedErrorOnce = false;

         while (true) {
            this.sourceIndex[leastSource]++;
            if (this.checkIfSourceExhausted(leastSource)) {
               sourcesLength = this.sources.length;
               break;
            }

            this.itemAtSourceIndex[leastSource] = this.sources[leastSource].getAt(this.sourceIndex[leastSource]);
            this.valueAtSourceIndex[leastSource] = this.this$0._longKeyProviderAdaptor.getLongKey(this.itemAtSourceIndex[leastSource]);
            if (this.valueAtSourceIndex[leastSource] >= priorValue) {
               break;
            }

            if (!loggedErrorOnce) {
               this.this$0.logUnsortedSource(this.sources[leastSource], this.itemAtSourceIndex[leastSource], this.valueAtSourceIndex[leastSource], priorValue);
               loggedErrorOnce = true;
            }
         }
      }

      start += System.currentTimeMillis();
      System.out.println("MC:mergeSources " + start + "ms");
   }

   protected boolean checkIfSourceExhausted(int whichSource) {
      if (this.sourceIndex[whichSource] >= this.sourceSize[whichSource]) {
         int endOfArray = this.sources.length - 1;
         if (whichSource != endOfArray) {
            this.sources[whichSource] = this.sources[endOfArray];
            this.sourceSize[whichSource] = this.sourceSize[endOfArray];
            this.sourceIndex[whichSource] = this.sourceIndex[endOfArray];
            this.itemAtSourceIndex[whichSource] = this.itemAtSourceIndex[endOfArray];
            this.valueAtSourceIndex[whichSource] = this.valueAtSourceIndex[endOfArray];
         }

         Array.resize(this.sources, endOfArray);
         Array.resize(this.sourceSize, endOfArray);
         Array.resize(this.sourceIndex, endOfArray);
         Array.resize(this.itemAtSourceIndex, endOfArray);
         Array.resize(this.valueAtSourceIndex, endOfArray);
         return true;
      } else {
         return false;
      }
   }
}
