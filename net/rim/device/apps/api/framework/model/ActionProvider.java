package net.rim.device.apps.api.framework.model;

public interface ActionProvider extends RIMModel {
   long DELETE;
   long MARK_OPENED;
   long MARK_UNOPENED;
   long INITIALIZE;
   long MARK_FILED;
   long CHECK_CAN_OPEN;
   long CHECK_CAN_OPEN_UNREAD;
   long CHECK_CAN_MARK_OPENED;
   long CHECK_CAN_MARK_UNOPENED;
   long CHECK_CAN_OPEN_THREAD;
   long OPEN;
   long BULK_DELETE;
   long BULK_MARK_OPENED;
   long BULK_MARK_OLD;
   long CHECK_IS_SAVED;
   long CHECK_IS_SAVED_THEN_ORPHANED;
   long CHECK_IS_ERROR;
   long SAVE;
   long CHECK_CAN_SAVE;
   long REPLY;
   long CHECK_CAN_REPLY;
   long DELETE_SAVED;
   long CHECK_IS_NEW;
   long PREPARE_FOR_REMOVAL;
   long PARENT_TO_BE_DELETED;
   long PARENT_TO_BE_DELETED_BULK;
   long PARENT_PERFORMACTIONSWHENMESSAGEISREALLYABOUTTOBEBLOWNAWAY;

   boolean perform(long var1, Object var3);
}
