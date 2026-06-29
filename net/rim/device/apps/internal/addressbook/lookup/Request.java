package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public final class Request implements ReadableList, VerbDescriptionProvider, PaintProvider {
   int _status;
   int _transactionId;
   int _numMatches;
   int _offsetIntoMatches;
   String _search;
   long _sortOrder;
   byte[] _desiredFields;
   Result _result;
   private boolean _processingFirstResults;
   int _selectedItem;
   RIMModel _selectedItemSubModel;
   private long _service;
   private Object _context;
   public static final int DEFAULT_NUMBER_OF_MATCHES = 20;
   public static final int MAX_SEARCH_STRING_LENGTH = 256;
   public static final int NO_APPROPRIATE_ADDRESS = -3;
   public static final int CANCELLED = -2;
   public static final int ERROR = -1;
   public static final int PENDING = 0;
   public static final int PENDING_MORE = 1;
   public static final int RECEIVED = 2;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      if (!ContextObject.getFlag(context, 128)) {
         graphics.drawText(this.toString(), 0, y, 0, width);
      } else {
         String searchString = this.getSearchString();
         int line0ResId = 311;
         Object[] line0Params = new Object[]{searchString};
         int line1ResId = -1;
         Object[] line1Params = null;
         switch (this.getStatus()) {
            case -4:
            case 0:
               line1ResId = 1755;
               break;
            case -3:
            default:
               if (ContextObject.getFlag(this._context, 94)) {
                  line1ResId = 1747;
               } else {
                  line1ResId = 1748;
               }
               break;
            case -2:
               line1ResId = 1749;
               break;
            case -1:
               line1ResId = 1750;
               break;
            case 1:
               line1ResId = 1754;
               break;
            case 2:
               int matches = this.getAvailableMatches();
               if (matches > 0 && this._selectedItem >= 0) {
                  RIMModel m = this._result.getAddress(this._selectedItem);
                  line0Params = new Object[]{m};
                  if (m instanceof Object) {
                     AddressCardModel model = (AddressCardModel)m;
                     PersonNameModel personName = model.getName();
                     CompanyInfoModel companyInfo = model.getCompanyInfo();
                     if (personName != null && companyInfo != null) {
                        line1ResId = 1756;
                        line1Params = new Object[]{companyInfo};
                     }
                  }
               } else {
                  switch (matches) {
                     case -1:
                        line1ResId = 1753;
                        line1Params = new Object[]{new Object(matches)};
                        break;
                     case 0:
                     default:
                        line1ResId = 1751;
                        break;
                     case 1:
                        line1ResId = 1752;
                  }
               }
         }

         ThemeAttributeSet tas1 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE1);
         ThemeAttributeSet tas2 = ThemeManager.getActiveTheme().getAttributeSet(AddressBookServices.TAG_LINE2);
         ListField listField = (ListField)ContextObject.get(context, -3906294199383546540L);
         int flags = ContextObject.getFlag(context, 17) ? 64 : 0;
         if (tas1 != null) {
            listField.setThemeAttributesSpecial(tas1, graphics);
         }

         String result = MessageFormat.format(AddressBookResources.getString(line0ResId), line0Params);
         graphics.drawText(result, x, y, flags, width);
         if (line1ResId != -1) {
            int fontHeight = graphics.getFont().getHeight();
            if (tas2 != null) {
               listField.setThemeAttributesSpecial(tas2, graphics);
            }

            result = MessageFormat.format(AddressBookResources.getString(line1ResId), line1Params);
            graphics.drawText(result, x, y + fontHeight, flags, width);
            return 0;
         }
      }

      return 0;
   }

   public final int getTransactionId() {
      return this._transactionId;
   }

   public final Object getContext() {
      return this._context;
   }

   public final long getService() {
      return this._service;
   }

   @Override
   public final String getVerbDescription(Object context) {
      Object[] parms = new Object[]{this._search};
      return MessageFormat.format(AddressBookResources.getString(1702), parms);
   }

   final void updateSearchString(String newSearchPattern) {
      this.setSearchString(newSearchPattern);
      this._status = 0;
      this._numMatches = 20;
      this._offsetIntoMatches = 0;
      this._result = null;
      this._processingFirstResults = false;
      this._selectedItem = -1;
      this._selectedItemSubModel = null;
   }

   final void setSearchOrder(long order) {
      this._sortOrder = order;
   }

   final void setDesiredFields(byte[] fields) {
      this._desiredFields = fields;
   }

   final void incomingResult(Result r) {
      if (this.getIncludedMatches() == 0) {
         this._processingFirstResults = true;
      }

      this._offsetIntoMatches = this._offsetIntoMatches + r.getIncludedMatches();
      this._result = r.mergeMore(this._result);
      this._status = this._result.getRawErrorCode() == 0 ? 2 : -1;
      if (this._processingFirstResults) {
         this._processingFirstResults = false;
      }

      if (this._result._matches.size() == 1) {
         this.setResolved(this._result.getAddress(0));
      }
   }

   final void setStatus(int status) {
      this._status = status;
   }

   public final void setContext(Object context) {
      this._context = context;
   }

   public final boolean setResolved(Object element) {
      if (element instanceof Object) {
         AddressCardModel acm = (AddressCardModel)element;
         boolean isPIN = ContextObject.getFlag(this._context, 94);
         FriendlyNameAddressModel firstModel = null;
         int count = 0;
         int n = acm.size();

         for (int i = 0; i < n; i++) {
            Object so = acm.getAt(i);
            if (isPIN && so instanceof Object || !isPIN && so instanceof Object) {
               count++;
               if (firstModel == null) {
                  firstModel = (FriendlyNameAddressModel)so;
               }
            }
         }

         if (count == 1) {
            this._selectedItem = this.getIndex(element);
            if (this._selectedItem != -1) {
               this._selectedItemSubModel = firstModel;
               return true;
            }
         }
      }

      this._status = -3;
      return false;
   }

   final boolean shouldResend() {
      return this._status != 0 && this._status != 2;
   }

   final boolean moreAvailable() {
      return this._status != 0 && this.resultsAvailable() > 0;
   }

   final boolean moreRequest(int amtMore) {
      if (amtMore > 0 && this._status == 2) {
         int resultsAvailable = this.resultsAvailable();
         if (resultsAvailable > 0) {
            if (amtMore > resultsAvailable) {
               amtMore = resultsAvailable;
            }

            this._numMatches = amtMore;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public final String getSearchString() {
      return this._search;
   }

   final int getIncludedMatches() {
      return this._result == null ? 0 : this._result.getIncludedMatches();
   }

   final int getAvailableMatches() {
      return this._result == null ? 0 : this.getIncludedMatches() + this.resultsAvailable();
   }

   final RIMModel getAddress(int index) {
      return this._result.getAddress(index);
   }

   public final RIMModel getSelectedAddress() {
      return !this.needsResolving() && this._selectedItem >= 0 ? this._result.getAddress(this._selectedItem) : null;
   }

   final RIMModel getSelectedAddressSubItem() {
      return this._selectedItemSubModel;
   }

   final int getStatus() {
      if (this._status == -1 && this._result != null && this._result.getIncludedMatches() > 0) {
         return 2;
      } else if (this._status == 2 && this._result != null && this._result.hasError() && this._result.getIncludedMatches() == 0) {
         return -1;
      } else {
         return this._status == 0 && this.getIncludedMatches() > 0 && this._offsetIntoMatches > 0 && !this._processingFirstResults ? 1 : this._status;
      }
   }

   final String getRawErrorMessage() {
      String msg = null;
      if (this._result != null) {
         msg = this._result.getRawErrorMessage();
      }

      if (msg == null) {
         msg = AddressBookResources.getString(1701);
      }

      return msg;
   }

   final int getRawErrorCode() {
      return this._result != null ? this._result.getRawErrorCode() : 0;
   }

   final Object createModel(long managerId) {
      return new RequestModel(managerId, this._sortOrder, this._search, this._transactionId);
   }

   public final boolean needsResolving() {
      return this.getStatus() == 2 ? this._selectedItem < 0 : true;
   }

   public final boolean isViewable() {
      int status = this.getStatus();
      return status == 2 || status == 1;
   }

   final void deleteItem(Object element) {
      if (this._selectedItem == this._result._matches.getIndex(element)) {
         this._selectedItem = -1;
         this._selectedItemSubModel = null;
      }

      this._result.deleteItem(element);
   }

   public final Result getResult() {
      return this._result;
   }

   final void setSearchString(String pattern) {
      StringBuffer patternBuffer = (StringBuffer)(new Object(pattern));
      if (patternBuffer.length() > 256) {
         patternBuffer.setLength(256);
      }

      this._search = patternBuffer.toString();
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return this._result._matches.getAt(index, count, elements, destIndex);
   }

   @Override
   public final int getIndex(Object element) {
      return this._result._matches.getIndex(element);
   }

   @Override
   public final Object getAt(int index) {
      return this._result._matches.getAt(index);
   }

   @Override
   public final int size() {
      return this._result._matches.size();
   }

   @Override
   public final String toString() {
      int resId = 307;
      String search = this.getSearchString();
      Object[] parms = null;
      short var6;
      switch (this.getStatus()) {
         case -4:
         case 0:
            var6 = 307;
            break;
         case -3:
         default:
            if (ContextObject.getFlag(this._context, 94)) {
               var6 = 1737;
            } else {
               var6 = 1738;
            }
            break;
         case -2:
            var6 = 1709;
            break;
         case -1:
            var6 = 1700;
            break;
         case 1:
            var6 = 312;
            break;
         case 2:
            int matches = this.getIncludedMatches();
            if (matches > 0 && this._selectedItem >= 0) {
               var6 = 311;
               RIMModel m = this._result.getAddress(this._selectedItem);
               parms = new Object[]{m};
            } else {
               switch (matches) {
                  case -1:
                     var6 = 310;
                     parms = new Object[]{search, new Object(matches)};
                     break;
                  case 0:
                  default:
                     var6 = 308;
                     break;
                  case 1:
                     var6 = 309;
               }
            }
      }

      if (parms == null) {
         parms = new Object[]{search};
      }

      return MessageFormat.format(AddressBookResources.getString(var6), parms);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final void addLookupResultToAddressBook(Object element) {
      if (element != null) {
         try {
            AddressBookServices.getAddressBook().addAddressCard(element);
            return;
         } catch (Throwable var5) {
            String[] choices = new Object[]{AddressBookResources.getString(1740), AddressBookResources.getString(1741), CommonResources.getString(9042)};
            int choice = Dialog.ask(AddressBookResources.getString(1710), choices, null, 1);
            switch (choice) {
               case -1:
                  break;
               case 0:
               default:
                  AddressBookServices.getAddressBook().forceUpdateAddressCard(ex.GetDuplicates()[0], element);
                  return;
               case 1:
                  AddressBookServices.getAddressBook().mergeUpdateAddressCard(ex.GetDuplicates()[0], element);
                  break;
            }
         }
      }
   }

   private final int resultsAvailable() {
      int currOffset = this._offsetIntoMatches;
      int lastOffset = this._result._availableMatches;
      return lastOffset - currOffset;
   }

   Request(int id, long service) {
      this._transactionId = id;
      this._status = 0;
      this._numMatches = 20;
      this._selectedItem = -1;
      this._service = service;
   }
}
