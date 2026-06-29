package net.rim.device.api.system;

public final class SIMCardPhoneBookEntry {
   private int _slot;
   private int _nameId;
   private String _name;
   private int _numberType;
   private int _memberId;
   private int _fleetId;
   private int _urbanId;
   private String _phoneNumber;
   public static final int MAX_NAME_LENGTH = 12;
   public static final int MAX_PHONE_NUMBER_LENGTH = 24;
   public static final int NUMBER_TYPE_MAIN_PHONE = 0;
   public static final int NUMBER_TYPE_PRIVATE_PHONE = 1;
   public static final int NUMBER_TYPE_HOME_PHONE = 2;
   public static final int NUMBER_TYPE_WORK_PHONE = 3;
   public static final int NUMBER_TYPE_MOBILE_PHONE = 4;
   public static final int NUMBER_TYPE_FAX = 5;
   public static final int NUMBER_TYPE_PAGER = 6;
   public static final int NUMBER_TYPE_TALK_GROUP = 7;
   public static final int NUMBER_TYPE_IP_ADDRESS = 8;
   public static final int NUMBER_TYPE_OTHER = 9;

   public final int getSlot() {
      return this._slot;
   }

   public final void setSlot(int slot) {
      this._slot = slot;
   }

   public final int getNameId() {
      return this._nameId;
   }

   public final void setNameId(int nameId) {
      this._nameId = nameId;
   }

   public final String getName() {
      return this._name;
   }

   public final void setName(String name) {
      this._name = name;
   }

   public final int getPhoneNumberType() {
      return this._numberType;
   }

   public final void setPhoneNumber(int memberId, int fleetId, int urbanId) {
      this._numberType = 1;
      this._memberId = memberId;
      this._fleetId = fleetId;
      this._urbanId = urbanId;
   }

   public final void setPhoneNumber(int numberType, String phoneNumber) {
      this._numberType = numberType;
      this._phoneNumber = phoneNumber;
   }

   public final int getMemberId() {
      return this._memberId;
   }

   public final int getFleetId() {
      return this._fleetId;
   }

   public final int getUrbanId() {
      return this._urbanId;
   }

   public final String getPhoneNumber() {
      return this._phoneNumber;
   }
}
