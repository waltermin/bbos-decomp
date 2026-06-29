package net.rim.device.apps.internal.docview.gui;

public interface DocViewDataProvider {
   byte DATA_LOADED;
   byte DATA_INSTORE;
   byte DATA_NOTAVAILABLE;
   byte DATA_NOTALLOWED;
   byte RENDER_DATA;
   byte PREVIEW_DATA;
   byte ENLARGE_DATA;
   byte RENDERDOMIDIDX_DATA;
   byte TXTOFFSET_DATA;
   byte DOCINFO_DATA;

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
