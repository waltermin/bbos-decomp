package net.rim.blackberry.api.pdap;

public interface BlackBerryMemoList extends BlackBerryPIMList {
   BlackBerryMemo createMemo();

   BlackBerryMemo importMemo(BlackBerryMemo var1);

   void removeMemo(BlackBerryMemo var1);
}
