package net.rim.tid.im.conv.repository.OTAReaders;

public class OTAReader__es__spp extends FastEuropeanPairLearningOTAReader {
   @Override
   public void init(byte type) {
      super.init(type);
      super._header.setLocaleStr("es");
   }
}
