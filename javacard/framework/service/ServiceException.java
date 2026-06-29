package javacard.framework.service;

import javacard.framework.CardRuntimeException;

public class ServiceException extends CardRuntimeException {
   public static final short ILLEGAL_PARAM;
   public static final short DISPATCH_TABLE_FULL;
   public static final short COMMAND_DATA_TOO_LONG;
   public static final short CANNOT_ACCESS_IN_COMMAND;
   public static final short CANNOT_ACCESS_OUT_COMMAND;
   public static final short COMMAND_IS_FINISHED;
   public static final short REMOTE_OBJECT_NOT_EXPORTED;

   public ServiceException(short reason) {
      super(reason);
   }

   public static void throwIt(short reason) {
      throw new ServiceException(reason);
   }
}
