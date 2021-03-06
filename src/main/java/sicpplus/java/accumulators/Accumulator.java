package sicpplus.java.accumulators;

import sicpplus.java.exceptions.Exceptions;

/** Convenience interface for mutable, <em>non-</em>thread safe
 * objects used for general kinds of reductions of data sets,
 * typically online.
 * <p>
 * All methods are optional.
 * <p>
 * Must throw an //assertionError for non-finite
 * input data, either on add or doubleValue.
 * TODO: tighten this requirement.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2019-07-29
 */

@SuppressWarnings("unchecked")
public interface Accumulator<T extends Accumulator> {

  //--------------------------------------------------------------
  // start with only immediate needs
  //--------------------------------------------------------------

  // TODO: numerical error bounds for inexact accumulators,
  // or some other predicate for testing that results are as
  // as accurate as expected.

  /** An <em>exact</em> accumulator returns values equivalent
   * to half-even rounding to nearest of infinite precision
   * calculation.
   */
  default boolean isExact () {
    throw Exceptions.unsupportedOperation(this,"isExact"); }

  /** Intermediate results will never silently <em>overflow</em>
   * to an absorbing 'infinity' state. They may fail due to
   * implementation bounds on memory, etc.
   */
  default boolean noOverflow () {
    throw Exceptions.unsupportedOperation(this,"noOverflow"); }

  /** General accumulators provide this. */
  default Object value () {
    throw
    Exceptions.unsupportedOperation(this,"value"); }

  /** Half-even rounding to nearest <code>double</code>. */
  default double doubleValue () {
    throw
    Exceptions.unsupportedOperation(this,"doubleValue"); }

  /** Half-even rounding to nearest <code>float</code>. */
  default float floatValue () {
    throw
    Exceptions.unsupportedOperation(this,"floatValue"); }

  default T clear () {
    throw
    Exceptions.unsupportedOperation(this,"clear"); }

  default T add (final double z) {
    throw
    Exceptions.unsupportedOperation(this,"add",z); }

  default T addAll (final double[] z)  {
    T a = (T) this;
    for (final double zi : z) { a = (T) a.add(zi); }
    return a; }

  default T addAbs (final double z) {
    add(Math.abs(z));
    return (T) this; }

  default T addAbsAll (final double[] z)  {
    for (final double zi : z) { addAbs(zi); }
    return (T) this; }

  /** add <code>z<sup>2</sup></code> to the accumulator. */

  default T add2 (final double z) {
    throw
    Exceptions.unsupportedOperation(this,"add2",z); }

  /** add all <code>z<sub>i</sub><sup>2</sup></code> to the
   * accumulator.
   * */
  default T add2All (final double[] z)  {
    for (final double zi : z) { add2(zi); }
    return (T) this; }

  default T addProduct (final double z0,
                        final double z1) {
    throw
    Exceptions.unsupportedOperation(this,"addProduct",z0,z1); }

  default T addProducts (final double[] z0,
                         final double[] z1)  {
    final int n = z0.length;
    //assert n == z1.length;
    for (int i=0;i<n;i++) { addProduct(z0[i],z1[i]); }
    return (T) this; }

  /** Add squared difference. */
  default T addL2 (final double z0,
                   final double z1) {
    throw
    Exceptions.unsupportedOperation(this,"addL2",z0,z1); }

  default T addL2Distance (final double[] z0,
                           final double[] z1)  {
    //Debug.println("addL2Distance");
    //Debug.println(Classes.className(this));
    final int n = z0.length;
    //assert n == z1.length;
    for (int i=0;i<n;i++) { addL2(z0[i],z1[i]); }
    return (T) this; }

  /** Add absolute difference. */
  default T addL1 (final double z0,
                   final double z1) {
    throw
    Exceptions.unsupportedOperation(this,"addL1",z0,z1); }

  default T addL1Distance (final double[] z0,
                           final double[] z1)  {
    final int n = z0.length;
    //assert n == z1.length;
    for (int i=0;i<n;i++) { addL1(z0[i],z1[i]); }
    return (T) this; }

  //--------------------------------------------------------------

  default void partialSums (final double[] x,
                            final double[] s) {
    final int n = x.length;
    //assert s.length==n;
    clear();
    for (int i=0;i<n;i++) { s[i] = add(x[i]).doubleValue(); } }

  default double[] partialSums (final double[] x) {
    final int n = x.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) { s[i] = add(x[i]).doubleValue(); }
    return s; }

  default double[] partialL1s (final double[] x) {
    final int n = x.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) { s[i] = addAbs(x[i]).doubleValue(); }
    return s; }

  default double[] partialL2s (final double[] x) {
    final int n = x.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) { s[i] = add2(x[i]).doubleValue(); }
    return s; }

  default double[] partialDots (final double[] x0,
                                final double[] x1) {
    final int n = x0.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) {
      s[i] = addProduct(x0[i],x1[i]).doubleValue(); }
    return s; }

  default double[] partialL1Distances (final double[] x0,
                                       final double[] x1) {
    final int n = x0.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) {
      s[i] = addL1(x0[i],x1[i]).doubleValue(); }
    return s; }

  default double[] partialL2Distances (final double[] x0,
                                       final double[] x1) {
    final int n = x0.length;
    final double[] s = new double[n];
    clear();
    for (int i=0;i<n;i++) {
      s[i] = addL2(x0[i],x1[i]).doubleValue(); }
    return s; }

  //--------------------------------------------------------------
}
//--------------------------------------------------------------
