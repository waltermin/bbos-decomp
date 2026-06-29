package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class PhoneNumberHashCodeGenerator {
   private StringBuffer _number = (StringBuffer)(new Object());
   private StringBuffer _dtmf = (StringBuffer)(new Object());
   private byte[] _bytes = new byte[0];
   private static final long GENERATOR_KEY;
   private static WeakReference _generatorWR;

   private PhoneNumberHashCodeGenerator() {
   }

   private static final PhoneNumberHashCodeGenerator getInstance() {
      PhoneNumberHashCodeGenerator generator = (PhoneNumberHashCodeGenerator)_generatorWR.get();
      if (generator == null) {
         generator = new PhoneNumberHashCodeGenerator();
         _generatorWR.set(generator);
      }

      return generator;
   }

   public static final int hashCode(PhoneNumberModel model) {
      return getInstance().internalHashCode(model);
   }

   private final synchronized int internalHashCode(PhoneNumberModel model) {
      PhoneNumberConverter.convertForTransmission(this._number, this._dtmf, model.toString().toCharArray(), true);
      int numberCount = Math.min(this._number.length(), 7);
      int numberOffset = this._number.length() - numberCount;
      int dtmfCount = this._dtmf.length();
      Array.resize(this._bytes, numberCount + dtmfCount);

      for (int idx = 0; idx < numberCount; idx++) {
         this._bytes[idx] = (byte)this._number.charAt(numberOffset + idx);
      }

      for (int idx = 0; idx < dtmfCount; idx++) {
         this._bytes[numberCount + idx] = (byte)this._dtmf.charAt(idx);
      }

      return AddressBookServices.getReverseLookupCode(this._bytes, 0, this._bytes.length);
   }

   static {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      _generatorWR = (WeakReference)reg.get(-7650560901485287741L);
      if (_generatorWR == null) {
         _generatorWR = (WeakReference)(new Object(null));

         label21:
         try {
            reg.put(-7650560901485287741L, _generatorWR);
            return;
         } finally {
            break label21;
         }
      }
   }
}
