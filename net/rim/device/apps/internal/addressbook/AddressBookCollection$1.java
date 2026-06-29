package net.rim.device.apps.internal.addressbook;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;

final class AddressBookCollection$1 implements Runnable {
   private final AddressBookCollection this$0;

   AddressBookCollection$1(AddressBookCollection _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab != null) {
         Enumeration en = ab.getAddressCards();
         if (en != null) {
            int count = ab.getAddressCount();
            if (count > 0) {
               int dataSize = (Locale.getDefaultForKeyboard().getCode() == 1684340736 ? 7 : 5) * 2 * count;
               if (this.this$0._customWordRepository != null) {
                  this.this$0._customWordRepository.clear();
                  this.this$0._customWordRepository.init(dataSize > 102400 ? 102400 : dataSize);
               }

               if (this.this$0._customYOMIWordRepository != null) {
                  this.this$0._customYOMIWordRepository.init(0);
               }

               int added = 0;

               while (en.hasMoreElements() && this.this$0._useCustomWordRepository) {
                  this.this$0.addCustomWords(en.nextElement());
                  if (++added % 256 == 0) {
                     LowMemoryManager.poll();
                  }
               }

               if (this.this$0._customWordRepository != null) {
                  this.this$0._customWordRepository.commit();
               }

               if (this.this$0._customYOMIWordRepository != null) {
                  this.this$0._customYOMIWordRepository.commit();
               }
            }
         }
      }
   }
}
