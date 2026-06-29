package net.rim.blackberry.api.pdap;

public interface InternalBlackBerryMemoList extends BlackBerryMemoList {
   String LIST_NAME;

   void initialize(int var1);

   BlackBerryMemo createMemo(Object var1);

   boolean isInternalMemoModel(Object var1);
}
