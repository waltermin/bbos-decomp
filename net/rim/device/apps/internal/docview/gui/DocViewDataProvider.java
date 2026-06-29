package net.rim.device.apps.internal.docview.gui;

public interface DocViewDataProvider {
   byte DATA_LOADED = 0;
   byte DATA_INSTORE = 1;
   byte DATA_NOTAVAILABLE = 2;
   byte DATA_NOTALLOWED = 3;
   byte RENDER_DATA = 0;
   byte PREVIEW_DATA = 1;
   byte ENLARGE_DATA = 2;
   byte RENDERDOMIDIDX_DATA = 3;
   byte TXTOFFSET_DATA = 4;
   byte DOCINFO_DATA = 5;

   boolean isMoreAvailable();

   int getMoreAvailableBytes(int var1);

   int getMoreAvailableBlocks();

   String getListOfNotRetrievedChunks();

   boolean allowServerFind();

   boolean allowDocInfo();

   boolean executeMore(MoreDescriptor var1, boolean var2, boolean var3);

   boolean isEmbeddedObjectAvailable(String var1);

   byte getTargetBlockStatus(int var1);

   boolean parseTargetBlock(int var1);

   Object parseCustomData(byte var1, Object var2);

   boolean allowMultipleItems();

   boolean hasMultipleBlocks();

   int getStartBlockIndexForArbDomID(String var1);

   int getEndBlockIndexForArbDomID(String var1);

   void preSelectItem();

   void selectItem(DocViewDisplayField$ItemInfo var1, DocViewDisplayField$ItemInfo var2);

   boolean isMoreRequestSent();

   void setAudioCodec(int var1);

   boolean hasRenderedData(String var1);

   short getDocInfoStatus();

   boolean hasDocInfoData();
}
