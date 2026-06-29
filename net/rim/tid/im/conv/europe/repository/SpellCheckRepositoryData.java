package net.rim.tid.im.conv.europe.repository;

public class SpellCheckRepositoryData extends EuropeanRepositoryData {
   @Override
   protected void initLearning() {
      this.initLearning((byte)5);
   }
}
