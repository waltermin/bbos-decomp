package net.rim.device.apps.internal.addressbook.lookup;

class RetryRequestVerb extends RequestVerb {
   public RetryRequestVerb(Request request, int ordering, int resId) {
      super(request, ordering, resId);
   }

   @Override
   public Object invoke(Object parameter) {
      String newSearchPattern = SearchEntryDialog.getSearchPattern(super._request.getSearchString());
      if (newSearchPattern != null) {
         super._request.setSearchString(newSearchPattern);
         return super.invoke(parameter);
      } else {
         return null;
      }
   }
}
