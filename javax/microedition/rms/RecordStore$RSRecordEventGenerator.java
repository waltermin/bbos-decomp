package javax.microedition.rms;

class RecordStore$RSRecordEventGenerator extends RecordEventGenerator {
   private final RecordStore this$0;

   RecordStore$RSRecordEventGenerator(RecordStore _1) {
      this.this$0 = _1;
   }

   @Override
   synchronized void loadRecordIDs(int[] recordIds) {
      this.this$0._recordStoreData.loadRecordIDs(recordIds);
   }
}
