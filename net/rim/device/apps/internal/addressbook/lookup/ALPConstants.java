package net.rim.device.apps.internal.addressbook.lookup;

interface ALPConstants {
   long ALP_LOGGER_GUID = -4453883819751179668L;
   long ALP_CONFIGURATION = 3525746332568432596L;
   String ALP_SERVICE_CID = "ALP";
   String ALP_EVENT_LOGGER_NAME = "net.rim.addressbook.lookup";
   String RIM_ALP_REQUEST = "net.rim.AddressLookupProtocol.Request";
   String RIM_ALP_RESULT = "net.rim.AddressLookupProtocol.Result";
   int ALP_MAJOR_VERSION = 1;
   int ALP_MINOR_VERSION = 0;
   int ALP_CURRENT_VERSION = 16;
   int ALP_ACTION_SEARCH = 1;
   int ALP_ACTION_RESULT = 2;
   int ALP_ACTION_TEMPORARY_ERROR = 128;
   int ALP_ACTION_PERMANENT_ERROR = 129;
   int ALP_FIELD_LOOKUP_RESULT = 1;
   int ALP_FIELD_SEARCH_STRING = 2;
   int ALP_FIELD_INCLUDED_MATCHES = 3;
   int ALP_FIELD_AVAILABLE_MATCHES = 4;
   int ALP_FIELD_OFFSET = 5;
   int ALP_FIELD_DESIRED_MATCHES = 6;
   int ALP_FIELD_DESIRED_FIELDS = 7;
   int ALP_FIELD_MATCH_SORT_ORDER = 8;
   int ALP_FIELD_ERROR_STRING = 129;
   int ALP_FIELD_ERROR_CODE = 130;
   int ALP_FIELD_END_OF_FIELDS = 0;
   int ALP_FIELD_SEARCH_STRING_2 = 9;
   int ALP_ERROR_UNKNOWN = 1;
}
