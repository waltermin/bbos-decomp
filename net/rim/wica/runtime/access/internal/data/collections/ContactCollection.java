package net.rim.wica.runtime.access.internal.data.collections;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.internal.addressbook.addresscard.WebPageAddressModel;
import net.rim.device.apps.internal.addressbook.userfields.UserFieldsModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.wica.common.builtindata.componentdefn.ContactCompDef;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class ContactCollection extends StdCmpCollectionImpl {
   private static AddressBook aBook = AddressBookServices.getAddressBook();
   private static Factory _addressCardModelFactory;
   private static Factory _userDefinedFieldsFactory;
   private static Factory _homephoneFactory;
   private static Factory _home2phoneFactory;
   private static Factory _mobilephoneFactory;
   private static Factory _workphoneFactory;
   private static Factory _work2phoneFactory;
   private static Factory _pagerphoneFactory;
   private static Factory _faxFactory;
   private static Factory _otherphoneFactory;
   private static Factory _personFactory;
   private static Factory _companyFactory;
   private static Factory _titleFactory;
   private static Factory _emailFactory;
   private static Factory _bodyFactory;
   private static Factory _pinAddressFactory;
   private static Factory _directConnectFactory;
   private static Factory _webpageFactory;

   public ContactCollection(WicletEx wiclet) {
      super(wiclet, ContactCompDef.getInstance());
   }

   @Override
   public void saveDeletedItems() {
      if (super._handles.size() == 0) {
         AddressBookServices.removeAllAddressCards();
      } else {
         for (int i = super._deletedItems.size() - 1; i >= 0; i--) {
            Object addressToRemove = this.getDBItemFromHandle((long)super._defs.getId() << 32 | 4294967295L & super._deletedItems.elementAt(i));
            if (addressToRemove != null) {
               AddressBookServices.removeAddressCard(addressToRemove);
            }
         }
      }
   }

   @Override
   public void saveModifiedItems() {
      for (int i = super._modifiedItems.size() - 1; i >= 0; i--) {
         AddressCardModel model = this.getAddressCardModel(super._modifiedItems.elementAt(i));
         Object existingModel = aBook.getAddressCard(model.getUID());
         if (existingModel == null) {
            aBook.addAddressCard(model);
         } else {
            aBook.updateAddressCard(existingModel, model);
         }
      }
   }

   @Override
   public void saveCreatedItems() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/wica/runtime/access/internal/data/collections/StdCmpCollectionImpl._createdItems Lnet/rim/device/api/util/IntVector;
      // 04: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 07: bipush 1
      // 08: isub
      // 09: istore 1
      // 0a: iload 1
      // 0b: iflt 70
      // 0e: aload 0
      // 0f: aload 0
      // 10: getfield net/rim/wica/runtime/access/internal/data/collections/StdCmpCollectionImpl._createdItems Lnet/rim/device/api/util/IntVector;
      // 13: iload 1
      // 14: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 17: invokespecial net/rim/wica/runtime/access/internal/data/collections/ContactCollection.getAddressCardModel (I)Lnet/rim/device/apps/api/addressbook/AddressCardModel;
      // 1a: astore 2
      // 1b: getstatic net/rim/wica/runtime/access/internal/data/collections/ContactCollection.aBook Lnet/rim/device/apps/api/addressbook/AddressBook;
      // 1e: aload 2
      // 1f: invokeinterface net/rim/device/apps/api/addressbook/AddressBook.addAddressCard (Ljava/lang/Object;)V 2
      // 24: goto 6a
      // 27: astore 3
      // 28: aload 0
      // 29: invokevirtual net/rim/wica/runtime/access/internal/data/collections/ContactCollection.toString ()Ljava/lang/String;
      // 2c: new java/lang/StringBuffer
      // 2f: dup
      // 30: ldc_w "Attempt to add duplicate contact: "
      // 33: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 36: aload 2
      // 37: invokeinterface net/rim/device/apps/api/addressbook/AddressCardModel.getName ()Lnet/rim/device/apps/api/addressbook/PersonNameModel; 1
      // 3c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // 3f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 42: bipush 2
      // 44: invokestatic net/rim/wica/runtime/logging/Logger.log (Ljava/lang/String;Ljava/lang/String;I)V
      // 47: goto 6a
      // 4a: astore 3
      // 4b: aload 0
      // 4c: invokevirtual net/rim/wica/runtime/access/internal/data/collections/ContactCollection.toString ()Ljava/lang/String;
      // 4f: new java/lang/StringBuffer
      // 52: dup
      // 53: ldc_w "Error adding contact: "
      // 56: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 59: aload 2
      // 5a: invokeinterface net/rim/device/apps/api/addressbook/AddressCardModel.getName ()Lnet/rim/device/apps/api/addressbook/PersonNameModel; 1
      // 5f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/Object;)Ljava/lang/StringBuffer;
      // 62: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 65: bipush 2
      // 67: invokestatic net/rim/wica/runtime/logging/Logger.log (Ljava/lang/String;Ljava/lang/String;I)V
      // 6a: iinc 1 -1
      // 6d: goto 0a
      // 70: return
      // try (15 -> 18): 19 null
      // try (15 -> 18): 33 null
   }

   private AddressCardModel getAddressCardModel(int handle) {
      AddressCardModel model = (AddressCardModel)_addressCardModelFactory.createInstance(null);
      if (model != null) {
         if (handle != -1) {
            model.setUID(handle);
         }

         long dataHandle = (long)super._defs.getId() << 32 | 4294967295L & handle;
         this.setNameInModel(model, dataHandle);
         this.setAddressesInModel(model, dataHandle);
         this.setEmailAddressesInModel(model, dataHandle);
         this.setCompanyInfoInModel(model, dataHandle);
         this.setTelephoneNumbersInModel(model, dataHandle);
         this.setUserFieldsInModel(model, dataHandle);
         this.setMiscInfoInModel(model, dataHandle);
      }

      return model;
   }

   private void setUserFieldsInModel(AddressCardModel model, long dataHandle) {
      UserFieldsModel userFieldsModel = (UserFieldsModel)_userDefinedFieldsFactory.createInstance(null);
      userFieldsModel.setUserDefinedField(0, (String)this.getObjectFieldValue(dataHandle, 21));
      userFieldsModel.setUserDefinedField(1, (String)this.getObjectFieldValue(dataHandle, 22));
      userFieldsModel.setUserDefinedField(2, (String)this.getObjectFieldValue(dataHandle, 23));
      userFieldsModel.setUserDefinedField(3, (String)this.getObjectFieldValue(dataHandle, 24));
      model.add(userFieldsModel);
   }

   private void setMiscInfoInModel(AddressCardModel model, long dataHandle) {
      BodyModel bodyModel = (BodyModel)_bodyFactory.createInstance(null);
      bodyModel.setText((String)this.getObjectFieldValue(dataHandle, 18));
      model.add(bodyModel);
      PINAddressModel pinModel = (PINAddressModel)_pinAddressFactory.createInstance(null);
      pinModel.setAddress((String)this.getObjectFieldValue(dataHandle, 25));
      model.add(pinModel);
      if (RadioInfo.getNetworkType() == 5) {
         AbstractPhoneNumberModel _dcModel = (AbstractPhoneNumberModel)_directConnectFactory.createInstance(null);
         _dcModel.setValue((String)this.getObjectFieldValue(dataHandle, 26));
         model.add(_dcModel);
      }

      WebPageAddressModel webModel = (WebPageAddressModel)_webpageFactory.createInstance((String)this.getObjectFieldValue(dataHandle, 20));
      model.add(webModel);
   }

   private void setTelephoneNumbersInModel(AddressCardModel model, long dataHandle) {
      this.setPhoneModel(model, dataHandle, 10, _homephoneFactory);
      this.setPhoneModel(model, dataHandle, 11, _home2phoneFactory);
      this.setPhoneModel(model, dataHandle, 12, _workphoneFactory);
      this.setPhoneModel(model, dataHandle, 13, _work2phoneFactory);
      this.setPhoneModel(model, dataHandle, 14, _mobilephoneFactory);
      this.setPhoneModel(model, dataHandle, 15, _faxFactory);
      this.setPhoneModel(model, dataHandle, 16, _pagerphoneFactory);
      this.setPhoneModel(model, dataHandle, 17, _otherphoneFactory);
   }

   private void setPhoneModel(AddressCardModel model, long dataHandle, int fieldID, Factory factory) {
      String tempString = (String)this.getObjectFieldValue(dataHandle, fieldID);
      if (tempString != null) {
         PhoneNumberModel telModel = (PhoneNumberModel)factory.createInstance(null);
         telModel.setValue(tempString);
         model.add(telModel);
      }
   }

   private void setCompanyInfoInModel(AddressCardModel model, long dataHandle) {
      CompanyInfoModel orgModel = (CompanyInfoModel)_companyFactory.createInstance(null);
      orgModel.setCompanyName((String)this.getObjectFieldValue(dataHandle, 8));
      model.add(orgModel);
      TitleModel titleModel = (TitleModel)_titleFactory.createInstance(null);
      titleModel.setTitle((String)this.getObjectFieldValue(dataHandle, 9));
      model.add(titleModel);
   }

   private void setEmailAddressesInModel(AddressCardModel model, long dataHandle) {
      this.setEmailModel(model, dataHandle, 5);
      this.setEmailModel(model, dataHandle, 6);
      this.setEmailModel(model, dataHandle, 7);
   }

   private void setEmailModel(AddressCardModel model, long dataHandle, int fieldID) {
      String tempString = (String)this.getObjectFieldValue(dataHandle, fieldID);
      if (tempString != null) {
         EmailAddressModel emailModel = (EmailAddressModel)_emailFactory.createInstance(null);
         emailModel.setAddress(tempString);
         model.add(emailModel);
      }
   }

   private void setAddressesInModel(AddressCardModel model, long dataHandle) {
      DataCollection dc = super._wiclet.getDataCollection(1);
      if (dc instanceof AddressCollection) {
         long addressHandle = this.getReferenceField(dataHandle, 3);
         if (addressHandle != -1) {
            ((AddressCollection)dc).saveAddressInModel(addressHandle, model, this.getMailingAddressType(3));
         }

         addressHandle = this.getReferenceField(dataHandle, 4);
         if (addressHandle != -1) {
            ((AddressCollection)dc).saveAddressInModel(addressHandle, model, this.getMailingAddressType(4));
         }
      }
   }

   private void setNameInModel(AddressCardModel model, long dataHandle) {
      PersonNameModel nameModel = (PersonNameModel)_personFactory.createInstance(null);
      nameModel.setNames(
         (String)this.getObjectFieldValue(dataHandle, 0), (String)this.getObjectFieldValue(dataHandle, 1), (String)this.getObjectFieldValue(dataHandle, 2)
      );
      model.add(nameModel);
   }

   int getMailingAddressType(int fieldID) {
      return fieldID == 3 ? 1 : 0;
   }

   @Override
   public void initFieldHandlers() {
      super._objectFieldHandlers = new IntHashtable(36);
      super._objectFieldHandlers.put(0, new ContactCollection$TitleHandler(null));
      super._objectFieldHandlers.put(1, new ContactCollection$GivenNameHandler(null));
      super._objectFieldHandlers.put(2, new ContactCollection$FamilyNameHandler(null));
      super._objectFieldHandlers.put(5, new ContactCollection$EmailHandler(0));
      super._objectFieldHandlers.put(6, new ContactCollection$EmailHandler(1));
      super._objectFieldHandlers.put(7, new ContactCollection$EmailHandler(2));
      super._objectFieldHandlers.put(8, new ContactCollection$OrgHandler(null));
      super._objectFieldHandlers.put(9, new ContactCollection$JobTitleHandler(null));
      super._objectFieldHandlers.put(10, new ContactCollection$PhoneHandler(3));
      super._objectFieldHandlers.put(11, new ContactCollection$PhoneHandler(4));
      super._objectFieldHandlers.put(12, new ContactCollection$PhoneHandler(1));
      super._objectFieldHandlers.put(13, new ContactCollection$PhoneHandler(2));
      super._objectFieldHandlers.put(14, new ContactCollection$PhoneHandler(5));
      super._objectFieldHandlers.put(16, new ContactCollection$PhoneHandler(6));
      super._objectFieldHandlers.put(15, new ContactCollection$PhoneHandler(7));
      super._objectFieldHandlers.put(17, new ContactCollection$PhoneHandler(8));
      super._objectFieldHandlers.put(18, new ContactCollection$NoteHandler(null));
      super._objectFieldHandlers.put(20, new ContactCollection$WebPageHandler(null));
      super._objectFieldHandlers.put(21, new ContactCollection$UserFieldHandler(0));
      super._objectFieldHandlers.put(22, new ContactCollection$UserFieldHandler(1));
      super._objectFieldHandlers.put(23, new ContactCollection$UserFieldHandler(2));
      super._objectFieldHandlers.put(24, new ContactCollection$UserFieldHandler(3));
      super._objectFieldHandlers.put(25, new ContactCollection$PINAddressHandler(null));
      super._objectFieldHandlers.put(26, new ContactCollection$DCIDHandler(null));
      super._intFieldHandlers = new IntHashtable(1);
      super._intFieldHandlers.put(19, new ContactCollection$UIDHandler(null));
   }

   @Override
   public IntVector uidsInExternalDB() {
      int numItems = aBook.getAddressCount();
      if (numItems < 1) {
         return null;
      }

      IntVector uidsInDB = new IntVector(numItems);
      Enumeration e = aBook.getAddressCards();

      while (e.hasMoreElements()) {
         Object address = e.nextElement();
         if (address instanceof AddressCardModel) {
            uidsInDB.addElement(((AddressCardModel)address).getUID());
         }
      }

      return uidsInDB;
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      int uid = this.getHandle(dataHandle);
      if (uid != -1) {
         Enumeration e = aBook.getAddressCards();

         while (e.hasMoreElements()) {
            Object address = e.nextElement();
            if (address instanceof AddressCardModel && uid == ((AddressCardModel)address).getUID()) {
               return address;
            }
         }
      }

      return null;
   }

   @Override
   public void loadItem(long dataHandle, Object item) {
      if (item instanceof AddressCardModel) {
         AddressCardModel model = (AddressCardModel)item;
         this.setIntFieldValue(dataHandle, 19, model.getUID());
         this.loadName(dataHandle, model);
         this.loadCompanyInfo(dataHandle, model);
         int size = model.size();
         int emailCount = 0;

         for (int i = 0; i < size; i++) {
            Object subModel = model.getAt(i);
            if (subModel instanceof EmailAddressModel) {
               this.setEmailAddress(dataHandle, (EmailAddressModel)subModel, emailCount);
               emailCount++;
            } else if (subModel instanceof PhoneNumberModel) {
               this.setPhoneNumber(dataHandle, (PhoneNumberModel)subModel);
            } else if (subModel instanceof BodyModel) {
               this.setObjectFieldValue(dataHandle, 18, ((BodyModel)subModel).getText());
            } else if (subModel instanceof UserFieldsModel) {
               this.setUserFields(dataHandle, (UserFieldsModel)subModel);
            } else if (subModel instanceof PINAddressModel) {
               this.setObjectFieldValue(dataHandle, 25, ((PINAddressModel)subModel).getAddress());
            } else if (subModel instanceof TitleModel) {
               this.setObjectFieldValue(dataHandle, 9, ((TitleModel)subModel).getTitle());
            } else if (subModel.getClass().getName().equals("net.rim.device.apps.internal.phone.direct.DirectConnectNumberModel")) {
               this.setObjectFieldValue(dataHandle, 26, ((AbstractPhoneNumberModel)subModel).getValue());
            } else if (subModel instanceof WebPageAddressModel) {
               this.setObjectFieldValue(dataHandle, 20, ((WebPageAddressModel)subModel).getAddress());
            }
         }
      }
   }

   private void setUserFields(long dataHandle, UserFieldsModel model) {
      if (model.getUserDefinedField(0) != null) {
         this.setObjectFieldValue(dataHandle, 21, model.getUserDefinedField(0));
      }

      if (model.getUserDefinedField(1) != null) {
         this.setObjectFieldValue(dataHandle, 22, model.getUserDefinedField(1));
      }

      if (model.getUserDefinedField(2) != null) {
         this.setObjectFieldValue(dataHandle, 23, model.getUserDefinedField(2));
      }

      if (model.getUserDefinedField(3) != null) {
         this.setObjectFieldValue(dataHandle, 24, model.getUserDefinedField(3));
      }
   }

   private void setPhoneNumber(long dataHandle, PhoneNumberModel model) {
      switch (model.getType()) {
         case 1:
            this.setObjectFieldValue(dataHandle, 12, model.getDisplayablePhoneNumber());
            return;
         case 2:
            this.setObjectFieldValue(dataHandle, 13, model.getDisplayablePhoneNumber());
         case 0:
            return;
         case 3:
            this.setObjectFieldValue(dataHandle, 10, model.getDisplayablePhoneNumber());
            return;
         case 4:
            this.setObjectFieldValue(dataHandle, 11, model.getDisplayablePhoneNumber());
            return;
         case 5:
            this.setObjectFieldValue(dataHandle, 14, model.getDisplayablePhoneNumber());
            return;
         case 6:
            this.setObjectFieldValue(dataHandle, 16, model.getDisplayablePhoneNumber());
            return;
         case 7:
         default:
            this.setObjectFieldValue(dataHandle, 15, model.getDisplayablePhoneNumber());
            return;
         case 8:
            this.setObjectFieldValue(dataHandle, 17, model.getDisplayablePhoneNumber());
      }
   }

   private void setEmailAddress(long dataHandle, EmailAddressModel model, int emailCount) {
      int[] emailFieldIDs = new int[]{5, 6, 7, -804651007, 9, -804651007, 19, 51, -805030417, 67324752, 10, 1645346816};
      if (emailCount >= 0 && emailCount < emailFieldIDs.length) {
         this.setObjectFieldValue(dataHandle, emailFieldIDs[emailCount], model.getAddress());
      }
   }

   private void loadCompanyInfo(long dataHandle, AddressCardModel item) {
      CompanyInfoModel companyInfo = item.getCompanyInfo();
      if (companyInfo != null) {
         this.setObjectFieldValue(dataHandle, 8, companyInfo.getCompanyName());
      }
   }

   private void loadName(long dataHandle, AddressCardModel item) {
      PersonNameModel name = item.getName();
      if (name != null) {
         this.setObjectFieldValue(dataHandle, 0, name.getSalutation());
         this.setObjectFieldValue(dataHandle, 1, name.getFirstName());
         this.setObjectFieldValue(dataHandle, 2, name.getLastName());
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _addressCardModelFactory = (Factory)ar.waitFor(-3124646573404667739L);
      _personFactory = (Factory)ar.waitFor(5149066071290992769L);
      _companyFactory = (Factory)ar.waitFor(-2467076596918202204L);
      _titleFactory = (Factory)ar.waitFor(-4904857078378172834L);
      _emailFactory = (Factory)ar.waitFor(-2985347935260258684L);
      _homephoneFactory = (Factory)ar.waitFor(7064935308737611579L);
      _home2phoneFactory = (Factory)ar.waitFor(7076766837289517896L);
      _mobilephoneFactory = (Factory)ar.waitFor(-442687637293762776L);
      _workphoneFactory = (Factory)ar.waitFor(8414046446004092553L);
      _work2phoneFactory = (Factory)ar.waitFor(476826571898366139L);
      _pagerphoneFactory = (Factory)ar.waitFor(6627402073208639065L);
      _faxFactory = (Factory)ar.waitFor(2862138288634470671L);
      _otherphoneFactory = (Factory)ar.waitFor(-1843891697376347796L);
      _bodyFactory = (Factory)ar.waitFor(2096811533660483L);
      _pinAddressFactory = (Factory)ar.waitFor(4246852237058296601L);
      _userDefinedFieldsFactory = (Factory)ar.waitFor(-8069221209051907189L);
      _webpageFactory = (Factory)ar.waitFor(-2606680735022884905L);
      if (RadioInfo.getNetworkType() == 5) {
         _directConnectFactory = (Factory)ar.waitFor(532879436795165891L);
      }
   }
}
