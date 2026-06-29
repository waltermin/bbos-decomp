package net.rim.wica.transport.security;

public interface SequenceProvider {
   int next(long var1);

   boolean verify(int var1, long var2);
}
