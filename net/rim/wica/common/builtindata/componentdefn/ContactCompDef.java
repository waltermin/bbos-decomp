package net.rim.wica.common.builtindata.componentdefn;

public class ContactCompDef extends DataComponentDefinition {
   public static final String CONTACT_COMP_NAME = "Contact";
   public static final int NAME_TITLE_ID = 0;
   public static final String NAME_TITLE = "nameTitle";
   public static final int NAME_TITLE_TYPE = 3;
   public static final int NAME_GIVEN_ID = 1;
   public static final String NAME_GIVEN = "nameGiven";
   public static final int NAME_GIVEN_TYPE = 3;
   public static final int NAME_FAMILY_ID = 2;
   public static final String NAME_FAMILY = "nameFamily";
   public static final int NAME_FAMILY_TYPE = 3;
   public static final int HOME_ADDRESS_ID = 3;
   public static final String HOME_ADDRESS = "homeAddress";
   public static final int HOME_ADDRESS_TYPE = 6;
   public static final int WORK_ADDRESS_ID = 4;
   public static final String WORK_ADDRESS = "workAddress";
   public static final int WORK_ADDRESS_TYPE = 6;
   public static final int EMAIL_1_ID = 5;
   public static final String EMAIL_1 = "Email1";
   public static final int EMAIL_1_TYPE = 3;
   public static final int EMAIL_2_ID = 6;
   public static final String EMAIL_2 = "Email2";
   public static final int EMAIL_2_TYPE = 3;
   public static final int EMAIL_3_ID = 7;
   public static final String EMAIL_3 = "Email3";
   public static final int EMAIL_3_TYPE = 3;
   public static final int ORG_ID = 8;
   public static final String ORG = "org";
   public static final int ORG_TYPE = 3;
   public static final int JOB_TITLE_ID = 9;
   public static final String JOB_TITLE = "jobTitle";
   public static final int JOB_TITLE_TYPE = 3;
   public static final int HOME_TEL_ID = 10;
   public static final String HOME_TEL = "homeTel";
   public static final int HOME_TEL_TYPE = 3;
   public static final int HOME2_TEL_ID = 11;
   public static final String HOME2_TEL = "home2Tel";
   public static final int HOME2_TEL_TYPE = 3;
   public static final int WORK_TEL_ID = 12;
   public static final String WORK_TEL = "workTel";
   public static final int WORK_TEL_TYPE = 3;
   public static final int WORK2_TEL_ID = 13;
   public static final String WORK2_TEL = "work2Tel";
   public static final int WORK2_TEL_TYPE = 3;
   public static final int MOBILE_TEL_ID = 14;
   public static final String MOBILE_TEL = "mobileTel";
   public static final int MOBILE_TEL_TYPE = 3;
   public static final int FAX_TEL_ID = 15;
   public static final String FAX_TEL = "faxTel";
   public static final int FAX_TEL_TYPE = 3;
   public static final int PAGER_TEL_ID = 16;
   public static final String PAGER_TEL = "pagerTel";
   public static final int PAGER_TEL_TYPE = 3;
   public static final int OTHER_TEL_ID = 17;
   public static final String OTHER_TEL = "otherTel";
   public static final int OTHER_TEL_TYPE = 3;
   public static final int NOTE_ID = 18;
   public static final String NOTE = "note";
   public static final int NOTE_TYPE = 3;
   public static final int UID_ID = 19;
   public static final int[] KEYS = new int[]{19, 51, -805030417, 67324752};
   public static final String UID = "uid";
   public static final int UID_TYPE = 1;
   public static final int WEBPAGE_ID = 20;
   public static final String WEBPAGE = "webpage";
   public static final int WEBPAGE_TYPE = 3;
   public static final int USER_1_ID = 21;
   public static final String USER_1 = "user1";
   public static final int USER_1_TYPE = 3;
   public static final int USER_2_ID = 22;
   public static final String USER_2 = "user2";
   public static final int USER_2_TYPE = 3;
   public static final int USER_3_ID = 23;
   public static final String USER_3 = "user3";
   public static final int USER_3_TYPE = 3;
   public static final int USER_4_ID = 24;
   public static final String USER_4 = "user4";
   public static final int USER_4_TYPE = 3;
   public static final int PIN_ID = 25;
   public static final String PIN = "pin";
   public static final int PIN_TYPE = 3;
   public static final int DCID_ID = 26;
   public static final String DCID = "dcid";
   public static final int DCID_TYPE = 3;
   private static final int[] _fieldTypes = new int[]{
      3,
      3,
      3,
      6,
      6,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      1,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      -804651005,
      5,
      1,
      4,
      -804651006,
      5,
      3,
      -804651005,
      5,
      6,
      7,
      -804651007,
      9,
      -804651007,
      19,
      51,
      -805030417,
      67324752,
      10,
      1645346816,
      14051,
      0,
      0,
      655360,
      1701969924,
      1920298867,
      796091747,
      51966,
      67324752,
      524308,
      1645346824,
      14051,
      0,
      0,
      1638400,
      1701969920,
      1920298867,
      796091747,
      1819308129,
      1952539497,
      778989417,
      -2057014172,
      -596718765,
      129764400,
      2075148232,
      -2130825622,
      -1062197214,
      579962170,
      -1972784627,
      -342157026,
      -1759145710,
      -1083133022,
      591354558,
      748587877,
      2027194125,
      -1862354380,
      -1894367777,
      -690290369,
      -825587344,
      -2112154010,
      1615165133,
      -1343936433,
      -1949571123,
      1101117931,
      -2007827530,
      -424892820,
      -1116992001,
      -1712298422,
      -1418564596,
      -2029367260,
      -872144113,
      136134941,
      -200091619,
      -1007676498,
      1914317964,
      -1194379630,
      273672971,
      1067999125,
      1291626810,
      167157177,
      -894293107
   };
   private static final String[] _fieldNames = new String[]{
      "nameTitle",
      "nameGiven",
      "nameFamily",
      "homeAddress",
      "workAddress",
      "Email1",
      "Email2",
      "Email3",
      "org",
      "jobTitle",
      "homeTel",
      "home2Tel",
      "workTel",
      "work2Tel",
      "mobileTel",
      "faxTel",
      "pagerTel",
      "otherTel",
      "note",
      "uid",
      "webpage",
      "user1",
      "user2",
      "user3",
      "user4",
      "pin",
      "dcid"
   };
   public static final int CONTACT_NUM_OBJECT_FIELDS = 24;
   public static final int CONTACT_NUM_INT_FIELDS = 1;
   private static ContactCompDef _instance;

   public static ContactCompDef getInstance() {
      if (_instance == null) {
         _instance = new ContactCompDef();
      }

      return _instance;
   }

   protected ContactCompDef() {
   }

   @Override
   public int getNumFields() {
      return _fieldTypes.length;
   }

   @Override
   public int getFieldType(int fieldIndex) {
      return fieldIndex >= 0 && fieldIndex <= _fieldTypes.length - 1 ? _fieldTypes[fieldIndex] : -1;
   }

   @Override
   public String getComponentName() {
      return "Contact";
   }

   @Override
   public int getId() {
      return 2;
   }

   @Override
   public int getFieldReferenceType(int fieldId) {
      int retValue = -1;
      switch (fieldId) {
         case 3:
         case 4:
         default:
            retValue = 1;
         case 2:
            return retValue;
      }
   }

   @Override
   public String[] getFieldNames() {
      return _fieldNames;
   }

   @Override
   public boolean hasKey() {
      return true;
   }

   @Override
   public int[] getKeyFields() {
      return KEYS;
   }
}
