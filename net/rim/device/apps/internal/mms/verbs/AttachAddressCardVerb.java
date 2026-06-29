package net.rim.device.apps.internal.mms.verbs;

import java.io.InputStream;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressSelectionContext;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.vm.Array;

public final class AttachAddressCardVerb extends Verb implements ConditionalVerb {
   private MMSPresentationModel _presentation;
   private Factory _converter;

   public AttachAddressCardVerb(MMSPresentationModel presentation) {
      super(16864030);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      this._converter = (Factory)ar.get(-5888220356524146836L);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(94);
   }

   @Override
   public final boolean canInvoke(Object parameter) {
      return this._converter != null;
   }

   @Override
   public final Object invoke(Object context) {
      Object address = this.pickAddress(context);
      if (address != null) {
         InputStream istream = (InputStream)this._converter.createInstance(address);
         if (istream != null) {
            byte[] data = this.readAll(istream);
            if (data != null) {
               String name = istream.toString() + ".vcf";
               int mimeType = 7;
               MMSAttachment attachment = new MMSAttachmentImpl(name, mimeType, data, "utf-8");
               this._presentation.addPresentationElement(attachment, true);
            }
         }
      }

      return null;
   }

   private final Object pickAddress(Object context) {
      Verb addressSelectionVerb = AddressBookServices.getAddressSelectionVerb(-3124646573404667739L);
      if (addressSelectionVerb != null) {
         String title = AddressBookResources.getString(301);
         Recognizer recognizer = RecognizerRepository.getRecognizers(-3124646573404667739L);
         AddressSelectionContext selectionContext = new AddressSelectionContext(null, title, null, recognizer, null);
         return addressSelectionVerb.invoke(selectionContext);
      } else {
         return null;
      }
   }

   private final byte[] readAll(InputStream istream) {
      byte[] data = null;
      byte[] buf = new byte[4000];

      try {
         while (true) {
            int count = istream.read(buf);
            if (count < 0) {
               return data;
            }

            if (count > 0) {
               if (data == null) {
                  data = new byte[count];
                  System.arraycopy(buf, 0, data, 0, count);
               } else {
                  int offset = data.length;
                  Array.resize(data, offset + count);
                  System.arraycopy(buf, 0, data, offset, count);
               }
            }
         }
      } finally {
         return data;
      }
   }
}
