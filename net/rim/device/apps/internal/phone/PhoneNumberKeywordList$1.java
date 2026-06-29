package net.rim.device.apps.internal.phone;

final class PhoneNumberKeywordList$1 implements Runnable {
   private final PhoneNumberKeywordList this$0;

   PhoneNumberKeywordList$1(PhoneNumberKeywordList _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._expandAddressRunnableId = -1;
      if (this.this$0._expandedAddressInfo != null) {
         this.this$0._expandedAddressInfo.setShowNumbers(true);
         int index = this.this$0.getSelectedIndex();
         if (this.this$0._expandedAddressInfo.numEntries() > 0) {
            index++;
         }

         this.this$0
            .setSize(
               PhoneNumberKeywordList.access$200(this.this$0).size()
                  + (this.this$0._expandedAddressInfo == null ? 0 : this.this$0._expandedAddressInfo.numEntries())
            );
         this.this$0.setSelectedIndex(index);
      }
   }
}
