package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddressCardCache implements MemoryCleanerListener {
   private AddressCardModel[] _keys = new Object[25];
   private AddressCardModel[] _values = new Object[25];
   private int[] _touched = new int[25];
   private int _counter;
   private static final long ADDRESS_CARD_CACHE_GUID;
   private static final int CACHE_SIZE;
   private static AddressCardCache _theCache;

   private AddressCardCache() {
      MemoryCleanerDaemon.addListener(this, false);
   }

   static final void initialize() {
      getInstance();
   }

   private static final AddressCardCache getInstance() {
      if (_theCache == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _theCache = (AddressCardCache)registry.getOrWaitFor(8455120407288834537L);
         if (_theCache == null) {
            _theCache = new AddressCardCache();
            registry.put(8455120407288834537L, _theCache);
         }
      }

      return _theCache;
   }

   static final AddressCardModel resolve(CompressedAddressCardModel compressedCard) {
      return getInstance().get(compressedCard, true);
   }

   static final AddressCardModel quickResolve(CompressedAddressCardModel compressedCard) {
      return getInstance().get(compressedCard, false);
   }

   static final void remove(AddressCardModel addressCard) {
      getInstance().internalRemove(addressCard);
   }

   static final void clearCache() {
      getInstance().reset();
   }

   private final int locate(AddressCardModel compressedCard) {
      int count = this._keys.length;
      int index = 0;
      int minimum = Integer.MAX_VALUE;

      for (int i = 0; i < count; i++) {
         AddressCardModel entry = this._keys[i];
         if (entry == null) {
            return i;
         }

         if (entry == compressedCard) {
            return i;
         }

         int touched = this._touched[i];
         if (touched < minimum) {
            minimum = touched;
            index = i;
         }
      }

      return index;
   }

   private final synchronized AddressCardModel get(CompressedAddressCardModel compressedCard, boolean updateCache) {
      int index = this.locate(compressedCard);
      AddressCardModel model = null;
      if (this._keys[index] == compressedCard) {
         model = this._values[index];

         try {
            model = AddressCardUtilities.expandGroup(model);
         } finally {
            ;
         }
      }

      if (model == null) {
         model = ((AddressCardModelFactory)ApplicationRegistry.getApplicationRegistry().waitFor(-3124646573404667739L)).uncompressCard(compressedCard);
      }

      if (updateCache) {
         this.put(index, compressedCard, model);
      }

      return model;
   }

   private final void moveEntry(int fromIndex, int toIndex) {
      this._keys[toIndex] = this._keys[fromIndex];
      this._values[toIndex] = this._values[fromIndex];
      this._touched[toIndex] = this._touched[fromIndex];
   }

   private final void put(int index, AddressCardModel compressedCard, AddressCardModel model) {
      if (index != 0) {
         int prevIndex = index - 1;
         this.moveEntry(prevIndex, index);
         index = prevIndex;
      }

      this._counter++;
      this._keys[index] = compressedCard;
      this._values[index] = model;
      this._touched[index] = this._counter;
   }

   private final synchronized void internalRemove(AddressCardModel addressCard) {
      int indexToRemove = this.locate(addressCard);
      if (indexToRemove != -1) {
         int count = this._keys.length;

         for (int i = indexToRemove + 1; i < count; i++) {
            if (this._keys[i] == null) {
               this.moveEntry(i, indexToRemove);
               this._keys[i] = null;
               this._values[i] = null;
               return;
            }
         }

         this.moveEntry(--count, indexToRemove);
         this._keys[count] = null;
         this._values[count] = null;
      }
   }

   private final synchronized boolean reset() {
      boolean cleaned = false;

      for (int i = 0; i < 25; i++) {
         cleaned |= this._keys[i] != null || this._values[i] != null;
         this._keys[i] = null;
         this._values[i] = null;
         this._touched[i] = 0;
      }

      this._counter = 0;
      return cleaned;
   }

   @Override
   public final boolean cleanNow(int event) {
      return this.reset();
   }

   @Override
   public final String getDescription() {
      return AddressBookResources.getString(1726);
   }
}
