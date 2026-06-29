package net.rim.device.apps.api.addressbook;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.util.BitSet;
import net.rim.device.apps.api.framework.model.PhoneNumberProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

public interface AddressCardModel extends AddressCardElement, ReadableList, WritableSet, MemberComparator, PhoneNumberProvider {
   PersonNameModel getName();

   CompanyInfoModel getCompanyInfo();

   DisplayPictureModel getContactPicture(Object var1);

   EventModel getEvent(int var1);

   boolean isValid();

   int getWord(int var1, long var2, char[] var4);

   int getIndexes(BitSet var1, long var2);

   Verb wrapToUpdateLastUsedEntry(RIMModel var1, Verb var2);
}
