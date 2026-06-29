package net.rim.device.apps.api.transmission;

public interface RIMMessagingTransmissionErrorCode {
   int MAILBOX_RESOURCE_FAILURE = 32;
   int MAIL_CREATE_FAILURE = 33;
   int MAIL_SUBMIT_FAILURE = 34;
   int BAD_RECIPIENT = 35;
   int MESSAGE_BODY_PROBLEM = 36;
   int BAD_MESSAGE_EXTRAS = 37;
   int BAD_ATTACHMENT = 38;
   int COULDNT_GET_ORIGINAL_MESSAGE = 49;
   int INVALID_MORE_PART_ID = 66;
   int INVALID_MORE_MSG_ID = 67;
   int COULDNT_GET_MORE = 68;
   int NNE_WRONG_PASSWORD = 80;
   int NNE_UNABLE_TO_DECRYPT = 81;
   int GENERAL_FAILURE = -1;
}
