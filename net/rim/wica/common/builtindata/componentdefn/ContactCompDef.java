package net.rim.wica.common.builtindata.componentdefn;

public class ContactCompDef extends DataComponentDefinition {
   public static final String CONTACT_COMP_NAME;
   public static final int NAME_TITLE_ID;
   public static final String NAME_TITLE;
   public static final int NAME_TITLE_TYPE;
   public static final int NAME_GIVEN_ID;
   public static final String NAME_GIVEN;
   public static final int NAME_GIVEN_TYPE;
   public static final int NAME_FAMILY_ID;
   public static final String NAME_FAMILY;
   public static final int NAME_FAMILY_TYPE;
   public static final int HOME_ADDRESS_ID;
   public static final String HOME_ADDRESS;
   public static final int HOME_ADDRESS_TYPE;
   public static final int WORK_ADDRESS_ID;
   public static final String WORK_ADDRESS;
   public static final int WORK_ADDRESS_TYPE;
   public static final int EMAIL_1_ID;
   public static final String EMAIL_1;
   public static final int EMAIL_1_TYPE;
   public static final int EMAIL_2_ID;
   public static final String EMAIL_2;
   public static final int EMAIL_2_TYPE;
   public static final int EMAIL_3_ID;
   public static final String EMAIL_3;
   public static final int EMAIL_3_TYPE;
   public static final int ORG_ID;
   public static final String ORG;
   public static final int ORG_TYPE;
   public static final int JOB_TITLE_ID;
   public static final String JOB_TITLE;
   public static final int JOB_TITLE_TYPE;
   public static final int HOME_TEL_ID;
   public static final String HOME_TEL;
   public static final int HOME_TEL_TYPE;
   public static final int HOME2_TEL_ID;
   public static final String HOME2_TEL;
   public static final int HOME2_TEL_TYPE;
   public static final int WORK_TEL_ID;
   public static final String WORK_TEL;
   public static final int WORK_TEL_TYPE;
   public static final int WORK2_TEL_ID;
   public static final String WORK2_TEL;
   public static final int WORK2_TEL_TYPE;
   public static final int MOBILE_TEL_ID;
   public static final String MOBILE_TEL;
   public static final int MOBILE_TEL_TYPE;
   public static final int FAX_TEL_ID;
   public static final String FAX_TEL;
   public static final int FAX_TEL_TYPE;
   public static final int PAGER_TEL_ID;
   public static final String PAGER_TEL;
   public static final int PAGER_TEL_TYPE;
   public static final int OTHER_TEL_ID;
   public static final String OTHER_TEL;
   public static final int OTHER_TEL_TYPE;
   public static final int NOTE_ID;
   public static final String NOTE;
   public static final int NOTE_TYPE;
   public static final int UID_ID;
   public static final int[] KEYS = new int[]{19, 51, -805030417, 67324752};
   public static final String UID;
   public static final int UID_TYPE;
   public static final int WEBPAGE_ID;
   public static final String WEBPAGE;
   public static final int WEBPAGE_TYPE;
   public static final int USER_1_ID;
   public static final String USER_1;
   public static final int USER_1_TYPE;
   public static final int USER_2_ID;
   public static final String USER_2;
   public static final int USER_2_TYPE;
   public static final int USER_3_ID;
   public static final String USER_3;
   public static final int USER_3_TYPE;
   public static final int USER_4_ID;
   public static final String USER_4;
   public static final int USER_4_TYPE;
   public static final int PIN_ID;
   public static final String PIN;
   public static final int PIN_TYPE;
   public static final int DCID_ID;
   public static final String DCID;
   public static final int DCID_TYPE;
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
   public static final int CONTACT_NUM_OBJECT_FIELDS;
   public static final int CONTACT_NUM_INT_FIELDS;
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
