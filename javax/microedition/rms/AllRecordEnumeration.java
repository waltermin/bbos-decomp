package javax.microedition.rms;

class AllRecordEnumeration extends BaseRecordEnumeration {
   AllRecordEnumeration(RecordStore recordStore, boolean listen) {
      super(recordStore, recordStore._eventGenerator, listen);
      this.rebuild();
   }
}
