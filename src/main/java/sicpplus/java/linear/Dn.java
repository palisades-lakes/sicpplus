package sicpplus.java.linear;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import sicpplus.java.accumulators.Accumulator;
import sicpplus.java.accumulators.BigFloatAccumulator;
import sicpplus.java.accumulators.ZhuHayesAccumulator;
import sicpplus.java.algebra.OneSetOneOperation;
import sicpplus.java.algebra.Set;
import sicpplus.java.algebra.TwoSetsOneOperation;
import sicpplus.java.numbers.Doubles;
import sicpplus.java.prng.Generator;

/** The set of instances of <code>double[n]</code>, for some given
 * <code>n</code>.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-10-08
 */
@SuppressWarnings("unchecked")
//strictfp
public final class Dn extends LinearSpaceLike {

  //--------------------------------------------------------------
  // operations on arrays of double
  // TODO: better elsewhere?
  //--------------------------------------------------------------

  public static final int hiElt (final double[] x) {
    final int n = x.length;
    if (0==n) { return 0; }
    int i = n-1;
    while ((0<=i) && (0.0==x[i])) { i--; }
    return i+1; }

  public static final double[] 
    copyWoutTrailingZeros (final double[] x) {
    final int n = hiElt(x);
    return Arrays.copyOf(x,n); }
  
  /** DANGER: only copy if needed. */
  public static final double[] 
    stripTrailingZeros (final double[] x) {
    final int n = hiElt(x);
    if (n==x.length) { return x; }
    return Arrays.copyOf(x,n); }
  
  //--------------------------------------------------------------
  /** hex string. */

  public static final String toHexString (final double[] m) {
    //final StringBuilder b = new StringBuilder("0x");
    final StringBuilder b = new StringBuilder();
    final int n = m.length;
    b.append("[");
    if (0<n) { 
      b.append(Double.toHexString(m[0]));
      for (int i=1;i<n;i++) {
        b.append(", ");
        b.append(Double.toHexString(m[i])); } }
    b.append("]");
    return b.toString(); }

  //--------------------------------------------------------------

  public static final double[] concatenate (final double[] x0,
                                            final double[] x1) {
    final double[] x = new double[x0.length + x1.length];
    for (int i=0;i<x0.length;i++) { x[i] = x0[i]; }
    for (int i=0;i<x1.length;i++) { x[i+x0.length] = x1[i]; }
    return x; }

  //--------------------------------------------------------------

  public static final double[] minus (final double[] x) {
    final double[] y = new double[x.length];
    for (int i=0;i<x.length;i++) { y[i] = -x[i]; }
    return y; }

  //--------------------------------------------------------------

  public static final double l1Dist (final double[] x0,
                                     final double[] x1) {
    final int n = x0.length;
    assert n == x1.length;
    final Accumulator a = BigFloatAccumulator.make();
    for (int i=0;i<n;i++) { a.add(Math.abs(x0[i]-x1[i])); }
    return a.doubleValue(); }

  //--------------------------------------------------------------

  public static final double l1Norm (final double[] x) {
    final int n = x.length;
    final Accumulator a = BigFloatAccumulator.make();
    for (int i=0;i<n;i++) { a.add(Math.abs(x[i])); }
    return a.doubleValue(); }

  //--------------------------------------------------------------

  public static final double maxAbs (final double[] x) {
    double m = NEGATIVE_INFINITY;
    for (final double element : x) {
      m = Math.max(m,Math.abs(element)); }
    return m; }

  //--------------------------------------------------------------

  public static final double max (final double[] x) {
    double m = NEGATIVE_INFINITY;
    for (final double element : x) { m = Math.max(m,element); }
    return m; }

  //--------------------------------------------------------------

  public static final double min (final double[] x) {
    double m = POSITIVE_INFINITY;
    for (final double element : x) { m = Math.min(m,element); }
    return m; }

  //--------------------------------------------------------------
  /** Return the condition number for summing the elements
   * (might be {@link Double#POSITIVE_INFINITY}). */

  public static final double conditionSum (final double[] x) {
    // TODO: choose accumulator based on array length??
    final Accumulator numerator = ZhuHayesAccumulator.make();
    final Accumulator denominator = ZhuHayesAccumulator.make();
    for (final double xi : x) {
      numerator.add(Math.abs(xi));
      denominator.add(xi); }
    final double n = numerator.doubleValue();
    final double d = Math.abs(denominator.doubleValue());
    if (0.0 == d) {
      // TODO: is this right? NaN or infinity better?
      if (0.0 == n) { return 1.0; }
      return Double.POSITIVE_INFINITY; }
    return n / d; }

  //  public static final double conditionSum (final double[] x) {
  //    // TODO: use an accurate summation algorithm?
  //    double numerator = 0.0;
  //    double denominator = 0.0;
  //    for (final double xi : x) {
  //      numerator += Math.abs(xi);
  //      denominator += xi; }
  //    return numerator / Math.abs(denominator); }

  //--------------------------------------------------------------
  // operations for algebraic structures over double[] arrays.
  //--------------------------------------------------------------

  public final double[] add (final double[] x0,
                             final double[] x1) {
    assert contains(x0);
    assert contains(x1);
    final double[] qq = new double[dimension()];
    for (int i=0;i<dimension();i++) { qq[i] = x0[i] + x1[i]; }
    return qq; }

  @Override
  public final double[] add (final Object x0,
                             final Object x1) {
    return add((double[]) x0, (double[]) x1); }

  //--------------------------------------------------------------

  @Override
  public final double[] zero (final int n) {
    final double[] qq = new double[n];
    Arrays.fill(qq,0.0);
    return qq; }

  //--------------------------------------------------------------

  public final double[] negate (final double[] x) {
    assert contains(x);
    return minus(x); }

  @Override
  public final double[] negate (final Object x) {
    return negate((double[]) x); }

  //--------------------------------------------------------------

  public final double[] scale (final double a,
                               final double[] x) {
    assert contains(x);
    final double[] qq = new double[dimension()];
    for (int i=0;i<dimension();i++) {
      qq[i] = a * x[i]; }
    return qq; }

  @Override
  public final double[] scale (final Object a,
                               final Object x) {
    return scale(((Number) a).doubleValue(), (double[]) x); }

  //--------------------------------------------------------------
  // Set methods
  //--------------------------------------------------------------

  @Override
  public final boolean equals (final Object x0,
                               final Object x1) {
    assert contains(x0);
    assert contains(x1);
    return Arrays.equals((double[]) x0, (double[]) x1); }

  //--------------------------------------------------------------

  @Override
  public final boolean contains (final Object element) {
    if (null == element) { return false; }
    final Class c = element.getClass();
    if (! c.isArray()) { return false; }
    if (! Double.TYPE.equals((c.getComponentType()))) { return false; }
    return Array.getLength(element) == dimension(); }

  //--------------------------------------------------------------
  /** Intended primarily for testing.
   */

  @Override
  public final Supplier generator (final Map options) {
    final UniformRandomProvider urp = Set.urp(options);
    return
      new Supplier () {
      final Generator g =
        Doubles.finiteGenerator(dimension(),urp);
      @Override
      public final Object get () { return g.next(); } }; }

  //--------------------------------------------------------------
  // Object methods
  //--------------------------------------------------------------

  @Override
  public final String toString () { return "D^" + dimension(); }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------
  // TODO: support zero-dimensional space?

  private Dn (final int dimension) { super(dimension); }

  private static final IntObjectMap<Dn> _cache =
    new IntObjectHashMap();

  public static final Dn get (final int dimension) {
    final Dn dn0 = _cache.get(dimension);
    if (null != dn0) { return dn0; }
    final Dn dn1 = new Dn(dimension);
    _cache.put(dimension,dn1);
    return dn1; }

  //--------------------------------------------------------------

  public static final OneSetOneOperation magma (final int n) {
    final Dn g = get(n);
    return OneSetOneOperation.magma(g.adder(),g); }

  //--------------------------------------------------------------
  /** n-dimensional rational vector space, implemented with
   * any known rational array.
   */

  private static final TwoSetsOneOperation
  makeSpace (final int n) {
    return
      TwoSetsOneOperation.floatingPointSpace(
        Dn.get(n).scaler(),
        Dn.magma(n),
        Doubles.FLOATING_POINT); }

  private static final IntObjectMap<TwoSetsOneOperation>
  _spaceCache = new IntObjectHashMap();

  /** n-dimensional floating point space, implemented with
   * <code>double[]</code>.
   */
  public static final TwoSetsOneOperation
  space (final int dimension) {
    final TwoSetsOneOperation space0 = _spaceCache.get(dimension);
    if (null != space0) { return space0; }
    final TwoSetsOneOperation space1 = makeSpace(dimension);
    _spaceCache.put(dimension,space1);
    return space1; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------

