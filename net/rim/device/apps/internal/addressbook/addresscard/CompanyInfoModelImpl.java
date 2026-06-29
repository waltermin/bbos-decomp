package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;

final class CompanyInfoModelImpl
   implements CompanyInfoModel,
   PersistableRIMModel,
   PaintProvider,
   FieldProvider,
   KeyProvider,
   ConversionProvider,
   EncryptableProvider,
   Copyable,
   MatchProvider {
   private Object _companyNameEncoding;
   private Object _companyNameYOMIEncoding;

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return paint(this.getCompanyName(), g, x, y, width, height, context);
   }

   @Override
   public final String getCompanyName() {
      return AddressCardUtilities.decodeString(this._companyNameEncoding);
   }

   @Override
   public final int match(Object criteria) {
      SearchCriterion crit = (SearchCriterion)criteria;
      if (crit.getType() != 21) {
         return -1;
      }

      String companyName = this.getCompanyName();
      return companyName != null && ((StringMatch)crit.getValue()).indexOf(companyName) >= 0 ? 1 : 0;
   }

   @Override
   public final void setCompanyNameYOMI(String companyNameYOMI) {
      this._companyNameYOMIEncoding = AddressCardUtilities.encodeString(companyNameYOMI);
   }

   @Override
   public final Field getField(Object context) {
      String companyName = this.getCompanyName();
      long flags = 0;
      if (!ContextObject.getFlag(context, 26)) {
         flags |= 18014398509481984L;
      } else {
         flags |= 36028797018963968L;
      }

      if (ContextObject.getFlag(context, 17)) {
         flags |= 64;
      }

      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      boolean isMultiSendMethodCapable = implusService != null && implusService.isIMPlusCompose(context);
      if (ContextObject.getFlag(context, 51)) {
         Field field;
         if (ContextObject.getFlag(context, 45)) {
            field = new LabelField(companyName, flags);
         } else {
            if (isMultiSendMethodCapable) {
               flags |= 67108864;
            }

            field = new RichTextField(companyName, flags);
         }

         field.setCookie(this);
         return field;
      } else if (!ContextObject.getFlag(context, 0)) {
         boolean singleLineField = ContextObject.getFlag(context, 106);
         if (companyName != null) {
            if (ContextObject.getFlag(context, 11) && this._companyNameYOMIEncoding != null) {
               companyName = companyName + " (" + this.getCompanyNameYOMI() + ")";
            }

            Field f;
            if (singleLineField) {
               f = new LabelField(companyName, flags);
            } else {
               if (isMultiSendMethodCapable) {
                  flags |= 67108864;
               }

               f = new RichTextField(companyName, flags);
            }

            f.setCookie(this);
            return f;
         } else {
            return null;
         }
      } else {
         if (companyName == null) {
            companyName = "";
         }

         EditField companyNameField = new AutoTextEditField(AddressBookResources.getString(101), companyName, 2048, 4505800798126336L);
         companyNameField.setCookie(this);
         String companyNameYOMI = this.getCompanyNameYOMI();
         Field f;
         if (companyNameYOMI == null && (InputContext.getInstance().getAvailableInputMethods() & 512) == 0) {
            f = companyNameField;
         } else {
            if (companyNameYOMI == null) {
               companyNameYOMI = "";
            }

            EditField yomiField = new YomiField(AddressBookResources.getString(1746), companyNameYOMI, 2048, 4505800798109696L);
            yomiField.setCookie(this);
            YomiFieldTextChangeListener companyInfoYOMIlistener = new YomiFieldTextChangeListener(yomiField);
            companyNameField.addTextChangeListener(companyInfoYOMIlistener);
            VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
            vfm.add(companyNameField);
            vfm.add(yomiField);
            vfm.setCookie(this);
            f = vfm;
         }

         return f;
      }
   }

   @Override
   public final boolean convert(Object context, Object target) {
      return AddressCardUtilities.convertCompanyInfoModel(context, target, this);
   }

   @Override
   public final boolean validate(Field field, Object context) {
      if (field instanceof EditField) {
         if (ContextObject.getFlag(context, 95)) {
            String companyName = ((EditField)field).getText().trim();
            if (companyName.length() == 0) {
               return false;
            }
         }

         return true;
      } else {
         return field instanceof VerticalFieldManager ? this.validate(((VerticalFieldManager)field).getField(0), context) : false;
      }
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 2200 : 1100;
      } else {
         return 0;
      }
   }

   @Override
   public final void setCompanyName(String companyName) {
      this._companyNameEncoding = AddressCardUtilities.encodeString(companyName);
      this._companyNameYOMIEncoding = null;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      String companyName = this.getCompanyName();
      String companyNameYOMI = AddressCardUtilities.convertYomi(this.getCompanyNameYOMI());
      if (keyRequested == -6544199576583918793L) {
         int keyCount = 1;
         Array.resize(keyArray, index + 2);
         keyArray[index++] = companyName;
         if (companyNameYOMI != null) {
            keyArray[index] = companyNameYOMI;
            keyCount++;
         }

         return keyCount;
      } else if (companyName != null && (keyRequested == -4388042602796535003L || keyRequested == 1232448844688687736L || keyRequested == -227891759293611117L)
         )
       {
         int keyCount = 1;
         if (index + 2 > keyArray.length) {
            Array.resize(keyArray, index + 2);
         }

         if (companyNameYOMI != null) {
            keyArray[index++] = companyNameYOMI;
            keyCount++;
         }

         keyArray[index] = companyName;
         return keyCount;
      } else {
         return 0;
      }
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof EditField)) {
         if (field instanceof VerticalFieldManager) {
            VerticalFieldManager vfm = (VerticalFieldManager)field;
            boolean result = this.grabDataFromField(vfm.getField(0), context);
            String yomi = ((EditField)vfm.getField(1)).getText().trim();
            this.setCompanyNameYOMI(yomi);
            return result;
         }
      } else {
         EditField ef = (EditField)field;
         String companyName = ef.getText().trim();
         if (companyName.length() > 0) {
            this.setCompanyName(companyName);
            return true;
         }
      }

      return false;
   }

   @Override
   public final Object copy() {
      return new CompanyInfoModelImpl(this);
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._companyNameEncoding, false, encrypt)
         && PersistentContent.checkEncoding(this._companyNameYOMIEncoding, false, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._companyNameEncoding = PersistentContent.reEncode(this._companyNameEncoding, false, encrypt);
      this._companyNameYOMIEncoding = PersistentContent.reEncode(this._companyNameYOMIEncoding, false, encrypt);
      return null;
   }

   @Override
   public final String getCompanyNameYOMI() {
      return AddressCardUtilities.decodeString(this._companyNameYOMIEncoding);
   }

   @Override
   public final String toString() {
      return this.getCompanyName();
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof CompanyInfoModel)) {
         return false;
      }

      CompanyInfoModel cim = (CompanyInfoModel)o;
      return StringUtilities.strEqualIgnoreCase(this.getCompanyName(), cim.getCompanyName());
   }

   CompanyInfoModelImpl(CompanyInfoModelImpl model) {
      if (model != null) {
         this._companyNameEncoding = PersistentContent.copyEncoding(model._companyNameEncoding);
         this._companyNameYOMIEncoding = PersistentContent.copyEncoding(model._companyNameYOMIEncoding);
      }
   }

   CompanyInfoModelImpl(String companyName) {
      this(companyName, null);
   }

   CompanyInfoModelImpl(Object initialData) {
      if (initialData instanceof String[]) {
         String[] info = (String[])initialData;
         if (info.length == 1) {
            this.setCompanyName(info[0]);
         } else {
            throw new IllegalArgumentException();
         }
      } else if (initialData instanceof String) {
         this.setCompanyName((String)initialData);
      } else {
         if (initialData instanceof ContextObject) {
            String str = (String)ContextObject.get(initialData, 253);
            if (str != null) {
               this.setCompanyName(str);
            }
         }
      }
   }

   static final int paint(String companyName, Graphics g, int x, int y, int width, int height, Object context) {
      int flags = ContextObject.getFlag(context, 17) ? 64 : 0;
      if (companyName != null) {
         y = VariableRowHeightProxy.getAdjustedY(context, g.getFont(), companyName, y);
         return g.drawText(companyName, x, y, flags, width);
      } else {
         return 0;
      }
   }

   CompanyInfoModelImpl(String companyName, String companyNameYOMI) {
      this.setCompanyName(companyName);
      this.setCompanyNameYOMI(companyNameYOMI);
   }
}
