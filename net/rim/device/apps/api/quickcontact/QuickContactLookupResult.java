package net.rim.device.apps.api.quickcontact;

public class QuickContactLookupResult {
   QuickContactItem _item;
   int _flags;
   static final int OPERATION_CANCELLED;
   static final int MULTIPLE_MATCHES_DETECTED;

   public QuickContactLookupResult() {
      this(null, 0);
   }

   public QuickContactLookupResult(QuickContactItem item) {
      this(item, 0);
   }

   public QuickContactLookupResult(QuickContactItem item, int flags) {
      this._item = item;
      this._flags = flags;
   }

   public QuickContactItem getItem() {
      return this._item;
   }

   public boolean operationCancelled() {
      return (this._flags & 2) != 0;
   }

   public boolean multipleMatchesDetected() {
      return (this._flags & 4) != 0;
   }
}
