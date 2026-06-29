package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.SelectionListener;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.ResolvedStatusProvider;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.vm.Array;

public class RequestModel
   implements VerbDescriptionProvider,
   FieldProvider,
   VerbProvider,
   ConversionProvider,
   ResolvedStatusProvider,
   PaintProvider,
   EmailAddressModel,
   EncryptableProvider {
   long _managerId;
   long _sortOrder;
   private Object _search;
   int _transactionId;
   public static final long SELECTION_LISTENER = -7302237785847991426L;

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      String nameToDraw = null;
      if (this.isResolved()) {
         Request req = this.fetchRequest();
         if (req != null) {
            RIMModel addressBookEntry = req.getSelectedAddress();
            if (addressBookEntry instanceof PaintProvider) {
               return ((PaintProvider)addressBookEntry).paint(g, x, y, width, height, context);
            }
         }
      }

      if (nameToDraw == null) {
         nameToDraw = this.getSearch();
      }

      return g.drawText(nameToDraw, x, y, 64, width);
   }

   @Override
   public Verb getVerbs(Object contextObject, Verb[] verbs) {
      ContextObject context = ContextObject.castOrCreate(contextObject);
      int size = 4;
      Array.resize(verbs, size);
      int curr = 0;
      Verb defaultVerb = null;
      boolean showComposeVerb = true;
      Request r = this.fetchRequest();
      if (r != null) {
         Object modelUser = ContextObject.get(r.getContext(), -6581931217101110672L);
         boolean fromEmailCompose = modelUser instanceof EditorUsingRIMModelFactory;
         if (r.isViewable()) {
            SelectionListener selectionListener = (SelectionListener)ContextObject.get(contextObject, -7302237785847991426L);
            int numMatches = r._result.getIncludedMatches();
            if (numMatches > 1) {
               defaultVerb = new RequestVerb(r, 413968, 1703, false, selectionListener);
               verbs[curr++] = defaultVerb;
            }

            if (!ContextObject.getFlag(context, 18)) {
               showComposeVerb = false;
            } else {
               if (!this.isResolved()) {
                  if (numMatches > 1) {
                     showComposeVerb = false;
                  }
               } else {
                  RIMModel resolvedModel = r.getSelectedAddress();
                  if (resolvedModel instanceof VerbProvider) {
                     Verb[] addressVerbs = new Verb[0];
                     context.put(113, r);
                     context.setFlag(114);
                     Verb tempDefault = ((VerbProvider)resolvedModel).getVerbs(context, addressVerbs);
                     context.clearFlag(114);
                     if (defaultVerb == null && tempDefault != null) {
                        defaultVerb = tempDefault;
                     }

                     if (addressVerbs.length > 0) {
                        size += addressVerbs.length;
                        Array.resize(verbs, size);

                        for (int i = 0; i < addressVerbs.length; i++) {
                           verbs[curr++] = addressVerbs[i];
                        }
                     }

                     showComposeVerb = false;
                     verbs[curr++] = new RequestVerb(r, 414000, 1706);
                  }
               }

               if (numMatches == 0) {
                  showComposeVerb = false;
               }
            }
         }

         if (r.getStatus() == -1 || r.getStatus() == 2 && r._result != null && r._result.getIncludedMatches() == 0) {
            defaultVerb = new RetryRequestVerb(r, 414064, 1722);
            verbs[curr++] = defaultVerb;
            showComposeVerb = false;
         }

         if (showComposeVerb && ContextObject.getFlag(context, 18) && !ContextObject.getFlag(context, 5)) {
            Verb[] composeVerbs = VerbRepository.getVerbRepository(-7881764549058890736L).getVerbs(-2985347935260258684L);

            for (int i = 0; i < composeVerbs.length; i++) {
               if (composeVerbs[i].getVerbGroupId() == 15556151) {
                  Verb composeAdapterVerb = new RequestModel$RequestComposeAdapter(this, composeVerbs[i], 327952, context);
                  verbs[curr++] = composeAdapterVerb;
                  if (defaultVerb == null) {
                     defaultVerb = composeAdapterVerb;
                  }
                  break;
               }
            }
         }

         if ((!context.getFlag(18) || !context.getFlag(3)) && !fromEmailCompose) {
            verbs[curr++] = new RequestVerb(r, 414080, 1704, false, null);
         }
      }

      if (curr != size) {
         Array.resize(verbs, curr);
      }

      return defaultVerb;
   }

   public Request fetchRequest() {
      Request request = ALPConfiguration.getManager().fetchRequestFromModel(this);
      if (request != null) {
         String search = this.getSearch();
         if (search == null || !search.equals(request._search)) {
            this.setSearch(request._search);
         }
      }

      return request;
   }

   public int numberAvailableForResolution() {
      Request r = this.fetchRequest();
      return r != null && r._result != null ? r._result.getIncludedMatches() : 0;
   }

   @Override
   public String getVerbDescription(Object context) {
      return this.formatResourceWithSearch(1702);
   }

   @Override
   public boolean convert(Object contextObject, Object target) {
      boolean addressAndFriendlyConversion = ContextObject.getFlag(contextObject, 10);
      if (addressAndFriendlyConversion && target instanceof String[]) {
         String[] strings = (String[])target;
         Request r = this.fetchRequest();
         if (r != null && !r.needsResolving()) {
            RIMModel sm = r.getSelectedAddressSubItem();
            if (sm instanceof ConversionProvider && ((ConversionProvider)sm).convert(contextObject, strings)) {
               String emailAddr = strings[0];
               strings[0] = null;
               strings[1] = null;
               RIMModel m = r.getSelectedAddress();
               if (m instanceof ConversionProvider && ((ConversionProvider)m).convert(contextObject, strings)) {
                  strings[0] = emailAddr;
                  return true;
               }

               strings[0] = emailAddr;
               strings[1] = null;
               return true;
            }
         }
      }

      return false;
   }

   void setSearch(String search) {
      this._search = PersistentContent.encode(search, false, true);
   }

   String getSearch() {
      try {
         String string = PersistentContent.decodeString(this._search);
         if (string != null && string.length() != 0) {
            return string;
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public void setFreeForm(boolean isFreeForm) {
      throw new IllegalStateException("Called setFreeForm on a RequestModel");
   }

   @Override
   public boolean isFreeForm() {
      return false;
   }

   @Override
   public void setFriendlyName(String fn) {
   }

   @Override
   public Field getField(Object context) {
      LabelField label = new LabelField(this.toString(), 18014398509481984L);
      label.setCookie(this);
      return label;
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      return false;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return false;
   }

   @Override
   public String getAddress() {
      return null;
   }

   @Override
   public String getFriendlyName() {
      return null;
   }

   @Override
   public boolean isResolved() {
      Request r = this.fetchRequest();
      return r != null && !r.needsResolving();
   }

   @Override
   public Object getResolvedItem() {
      Request r = this.fetchRequest();
      return r != null ? r.getSelectedAddress() : null;
   }

   @Override
   public Object getResolvedSubItem() {
      Request r = this.fetchRequest();
      return r != null ? r.getSelectedAddressSubItem() : null;
   }

   @Override
   public void setAddressAndFriendlyName(String fn, String addr) {
   }

   @Override
   public void setAddress(String addr) {
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._search, false, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._search = PersistentContent.reEncode(this._search, false, encrypt);
      return null;
   }

   private String formatResourceWithSearch(int resId) {
      Object[] parms = new Object[]{this.getSearch()};
      return MessageFormat.format(AddressBookResources.getString(resId), parms);
   }

   @Override
   public String toString() {
      Request r = this.fetchRequest();
      return r != null ? r.toString() : this.formatResourceWithSearch(1718);
   }

   RequestModel(long managerId, long sortOrder, String pattern, int transactionId) {
      this._managerId = managerId;
      this._sortOrder = sortOrder;
      this.setSearch(pattern);
      this._transactionId = transactionId;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof RequestModel)) {
         return super.equals(obj);
      }

      RequestModel requestModel = (RequestModel)obj;
      Request request = requestModel.fetchRequest();
      return request == this.fetchRequest();
   }
}
