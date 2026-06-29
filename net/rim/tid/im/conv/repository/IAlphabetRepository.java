package net.rim.tid.im.conv.repository;

public interface IAlphabetRepository {
   boolean isInExtendedAlphabet(char var1);

   boolean isInBasicAlphabet(char var1);

   void addAlphabetChangeListener(AlphabetChangeListener var1);

   void removeAlphabetChangeListener(AlphabetChangeListener var1);
}
