package sicpplus.java.scripts.numbers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import sicpplus.java.algebra.Set;
import sicpplus.java.algebra.Structure;
import sicpplus.java.numbers.Natural;
import sicpplus.java.numbers.UnboundedNatural;
import sicpplus.java.prng.PRNG;
import sicpplus.java.test.algebra.SetTests;

//----------------------------------------------------------------
/** Profiling {@link UnboundedNatural}.
 * <p>
 * <pre>
 * j src/scripts/java/sicpplus/java/scripts/numbers/Unbounded.java
 * jy src/scripts/java/sicpplus/java/scripts/numbers/Unbounded.java
 * </pre>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2021-05-31
 */

public final class Unbounded {

  private static final void noOverflow () {
    final UnboundedNatural u = 
      UnboundedNatural.valueOf(Natural.maxValue());
    // no overflow from add 
    final UnboundedNatural v = u.add(u); 
    final int cmp = u.compareTo(v);
    assert (cmp < 0) :
      "\nadd one doesn't increase value\n" 
          + "compareTo -> " + cmp; }

  private static final void monoid () {

    final Structure s = UnboundedNatural.MONOID;
    final int n = 2;
    SetTests.tests(s,n);
    final Map<Set,Supplier> generators =
      s.generators(
        ImmutableMap.of(
          Set.URP,
          PRNG.well44497b("seeds/Well44497b-2019-01-09.txt")));
    for(final Predicate law : s.laws()) {
      for (int i=0; i<n; i++) {
        final boolean result = law.test(generators);
        assert result: 
          s.toString() + " : " + law.toString(); } } }
  
  private static final void addition () {
    final long t0 = System.nanoTime();
    try {
      final Natural max = Natural.maxValue();
      UnboundedNatural u = 
        UnboundedNatural.concatenate(max,max);
      for (int i=0;i<32;i++) { 
        final UnboundedNatural u1 = u.add(u); 
        assert (u.compareTo(u1) < 0); 
        u = u1; } }
    finally {
      System.out.printf("Total seconds: %4.3f\n",
        Double.valueOf((System.nanoTime()-t0)*1.0e-9)); } }

  public static final void main (final String[] args) {
    noOverflow();
    //monoid();
    //addition();
  }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
