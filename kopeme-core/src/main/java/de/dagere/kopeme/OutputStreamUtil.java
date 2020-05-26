package de.dagere.kopeme;
import java.io.PrintStream;

import org.openjdk.jmh.infra.Blackhole;


public class OutputStreamUtil {
   private static final PrintStream oldOut = System.out;
   private static final PrintStream oldErr = System.err;
   private static final Blackhole blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
   
   public static void redirectToNullStream() {
      final java.io.PrintStream nullStream = new java.io.PrintStream(new java.io.OutputStream() {
         @Override public void write(int b) {blackhole.consume(b);}
     }) {
         @Override public void flush() {blackhole.evaporate("Yes, I am Stephen Hawking, and know a thing or two about black holes.");}
         @Override public void close() {}
         @Override public void write(int b) {blackhole.consume(b);}
         @Override public void write(byte[] b) {blackhole.consume(b);}
         @Override public void write(byte[] buf, int off, int len) {}
         @Override public void print(boolean b) {blackhole.consume(b);}
         @Override public void print(char c) {blackhole.consume(c);}
         @Override public void print(int i) {blackhole.consume(i);}
         @Override public void print(long l) {blackhole.consume(l);}
         @Override public void print(float f) {blackhole.consume(f);}
         @Override public void print(double d) {blackhole.consume(d);}
         @Override public void print(char[] s) {blackhole.consume(s);}
         @Override public void print(String s) {blackhole.consume(s);}
         @Override public void print(Object obj) {blackhole.consume(obj);}
         @Override public void println() {}
         @Override public void println(boolean x) {blackhole.consume(x);}
         @Override public void println(char x) {blackhole.consume(x);}
         @Override public void println(int x) {blackhole.consume(x);}
         @Override public void println(long x) {blackhole.consume(x);}
         @Override public void println(float x) {blackhole.consume(x);}
         @Override public void println(double x) {blackhole.consume(x);}
         @Override public void println(char[] x) {blackhole.consume(x);}
         @Override public void println(String x) {blackhole.consume(x);}
         @Override public void println(Object x) {blackhole.consume(x);}
         @Override public java.io.PrintStream printf(String format, Object... args) { return this; }
         @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
         @Override public java.io.PrintStream format(String format, Object... args) { return this; }
         @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
         @Override public java.io.PrintStream append(CharSequence csq) { return this; }
         @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return this; }
         @Override public java.io.PrintStream append(char c) { return this; }
     };
      System.setOut(nullStream);
      System.setErr(nullStream);
   }
   
   public static void resetStreams(){
      System.out.flush();
      System.setOut(oldOut);
      System.setErr(oldErr);
   }
}
