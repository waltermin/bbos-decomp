package net.rim.device.apps.api.search;

public interface SearchCriterion {
   int BODY_SEARCH = 1;
   int SUBJECT_SEARCH = 2;
   int UNUSED_1 = 3;
   int HEADER_SEARCH_FROM = 4;
   int HEADER_SEARCH_ANY = 5;
   int HEADER_SEARCH_TO = 6;
   int HEADER_SEARCH_CC = 7;
   int HEADER_SEARCH_BCC = 8;
   int RECEIVED_SEARCH = 9;
   int SENT_SEARCH = 10;
   int SAVED_SEARCH = 11;
   int EMAIL_TYPE_SEARCH = 12;
   int SMS_TYPE_SEARCH = 13;
   int PHONE_TYPE_SEARCH = 14;
   int FOLDER_SEARCH = 15;
   int DIRECT_CONNECT_TYPE_SEARCH = 16;
   int SERVICE_SEARCH = 17;
   int VOICEMAIL_TYPE_SEARCH = 18;
   int PAGES_TYPE_SEARCH = 19;
   int MMS_TYPE_SEARCH = 20;
   int TEXT_SEARCH = 21;
   int DRAFT_SEARCH = 22;
   int ID_SEARCH = 24;
   int PIN_TYPE_SEARCH = 25;
   int EMAIL_WITH_ATTACHMENTS_TYPE_SEARCH = 26;
   int ENCODING_ACTION_SEARCH = 27;
   int UNOPENED_SEARCH = 28;
   int PHONE_NUMBER_SEARCH = 29;
   int OR_SEARCH = 23;
   int CRITERION_ARRAY_SIZE = 3;
   int ADDRESS_CARDS_INDEX = 0;
   int ADDRESS_CARD_LUIDS_INDEX = 1;
   int WORDS_INDEX = 2;
   int SERVICE_SEARCH_CRITERION_ARRAY_SIZE = 2;
   int SERVICE_USER_ID_INDEX = 0;
   int SERVICE_UID_HASH_INDEX = 1;

   int getType();

   Object getValue();
}
