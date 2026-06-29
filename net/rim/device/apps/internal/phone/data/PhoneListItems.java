package net.rim.device.apps.internal.phone.data;

public interface PhoneListItems {
   void delete(int var1);

   PhoneListItem get(int var1);

   int getCount();

   int getCapacity();

   int getCurrentIndex();

   int getCandidateIndexForDeletion();

   void onDisplay();
}
