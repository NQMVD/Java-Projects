import java.io.Serializable;


/**
 * BDVector - bidimensionnel Vector - two-dimensional Vector
 * A class to describe a two dimensional vector, specifically a
 * Euclidean (also known as geometric) vector. A vector is an entity that has
 * both magnitude and direction. The datatype, however, stores the components of
 * the vector (x,y for 2D, and x,y,z for 3D). The magnitude and direction can be
 * accessed via the methods <b>mag()</b> and <b>heading()</b>.<br />
 * <br />
 * In many of the Processing examples, you will see <b>BDVector</b> used to
 * describe a position, velocity, or acceleration. For example, if you consider
 * a rectangle moving across the screen, at any given instant it has a position
 * (a vector that points from the origin to its location), a velocity (the rate
 * at which the object's position changes per time unit, expressed as a vector),
 * and acceleration (the rate at which the object's velocity changes per time
 * unit, expressed as a vector). Since vectors represent groupings of values, we
 * cannot simply use traditional addition/multiplication/etc. Instead, we'll
 * need to do some "vector" math, which is made easy by the methods inside the
 * <b>BDVector</b> class.
 *
 * <h3>Advanced</h3>
 * A class to describe a two or three dimensional vector.
 * <p>
 * The result of all functions are applied to the vector itself, with the
 * exception of cross(), which returns a new BDVector (or writes to a specified
 * 'target' BDVector). That is, add() will add the contents of one vector to this
 * one. Using add() with additional parameters allows you to put the result into
 * a new BDVector. Functions that act on multiple vectors also include static
 * versions. Because creating new objects can be computationally expensive, most
 * functions include an optional 'target' BDVector, so that a new BDVector object
 * is not created with each operation.
 * <p>
 * Initially based on the Vector3D class by
 * <a href="http://www.shiffman.net">Dan Shiffman</a>.
 *
 * @webref math
 * @webBrief A class to describe a two or three dimensional vector
 */
public class BDVector implements Serializable {
  /**
   *
   * The x component of the vector. This field (variable) can be used to both
   * get and set the value (see above example.)
   *
   *
   * @webref BDVector:field
   * @usage web_application
   * @webBrief  The x component of the vector
   */
  public float x;

  /**
   *
   * The y component of the vector. This field (variable) can be used to both
   * get and set the value (see above example.)
   *
   *
   * @webref BDVector:field
   * @usage web_application
   * @webBrief  The y component of the vector
   */
  public float y;

  public float w;
  public float h;


  /** Array so that this can be temporarily used in an array context */
  transient protected float[] array;


  /**
   * Constructor for an empty vector: x, y, and z are set to 0.
   */
  public BDVector() {
  }


  /**
   * Constructor for a 3D vector.
   *
   * @param  x the x coordinate.
   * @param  y the y coordinate.
   */
  public BDVector(float x, float y) {
    this.x = x;
    this.y = y;
  }


  /**
   * Constructor for a 2D vector.
   */
  public BDVector(float x, float y) {
    this.x = x;
    this.y = y;
  }


  /**
   *
   * Sets the x, y, and z component of the vector using two or three separate
   * variables, the data from a <b>BDVector</b>, or the values from a float array.
   *
   *
   * @webref BDVector:method
   * @param x the x component of the vector
   * @param y the y component of the vector
   * @webBrief  Set the components of the vector
   */
  public BDVector set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }


  /**
   * @param x the x component of the vector
   * @param y the y component of the vector
   */
  public BDVector set(float x, float y) {
    this.x = x;
    this.y = y;
    return this;
  }


  /**
   * @param v any variable of type BDVector
   */
  public BDVector set(BDVector v) {
    x = v.x;
    y = v.y;
    return this;
  }


  /**
   * Set the x, y coordinates using a float[] array as the source.
   * @param source array to copy from
   */
  public BDVector set(float[] source) {
    if (source.length >= 2) {
      x = source[0];
      y = source[1];
    }
    return this;
  }


  /**
   *
   * Returns a new 2D unit vector with a random direction. If you pass in
   * <b>this</b> as an argument, it will use the PApplet's random number
   * generator.
   *
   * @webref BDVector:method
   * @usage web_application
   * @return the random BDVector
   * @webBrief Make a new 2D unit vector with a random direction
   * @see BDVector#random3D()
   */
  static public BDVector random2D() {
    return random2D(null, null);
  }


  /**
   * Make a new 2D unit vector with a random direction
   * using Processing's current random number generator
   * @param parent current PApplet instance
   * @return the random BDVector
   */
  static public BDVector random2D(PApplet parent) {
    return random2D(null, parent);
  }

  /**
   * Set a 2D vector to a random unit vector with a random direction
   * @param target the target vector (if null, a new vector will be created)
   * @return the random BDVector
   */
  static public BDVector random2D(BDVector target) {
    return random2D(target, null);
  }


  /**
   * Make a new 2D unit vector with a random direction. Pass in the parent
   * PApplet if you want randomSeed() to work (and be predictable). Or leave
   * it null and be... random.
   * @return the random BDVector
   */
  static public BDVector random2D(BDVector target, PApplet parent) {
    return (parent == null) ?
      fromAngle((float) (Math.random() * Math.PI*2), target) :
      fromAngle(parent.random(PConstants.TAU), target);
  }


  /**
   *
   * Calculates and returns a new 2D unit vector from the specified angle value
   * (in radians).
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief Make a new 2D unit vector from an angle
   * @param angle the angle in radians
   * @return the new unit BDVector
   */
  static public BDVector fromAngle(float angle) {
    return fromAngle(angle,null);
  }


  /**
   * Make a new 2D unit vector from an angle
   *
   * @param target the target vector (if null, a new vector will be created)
   * @return the BDVector
   */
  static public BDVector fromAngle(float angle, BDVector target) {
    if (target == null) {
      target = new BDVector((float)Math.cos(angle),(float)Math.sin(angle),0);
    } else {
      target.set((float)Math.cos(angle),(float)Math.sin(angle),0);
    }
    return target;
  }


  /**
   *
   * Copies the components of the vector and returns the result as a <b>BDVector</b>.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief  Get a copy of the vector
   */
  public BDVector copy() {
    return new BDVector(x, y);
  }


  /**
   * @param target
   */
  public float[] get(float[] target) {
    if (target == null) {
      return new float[] { x, y };
    }
    if (target.length >= 2) {
      target[0] = x;
      target[1] = y;
    }
    return target;
  }


  /**
   *
   * Calculates the magnitude (length) of the vector and returns the result
   * as a float (this is simply the equation <em>sqrt(x*x + y*y + z*z)</em>.)
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief  Calculate the magnitude of the vector
   * @return magnitude (length) of the vector
   * @see BDVector#magSq()
   */
  public float mag() {
    return (float) Math.sqrt(x*x + y*y);
  }


  /**
   *
   * Calculates the magnitude (length) of the vector, squared. This method is
   * often used to improve performance since, unlike <b>mag()</b>, it does not
   * require a <b>sqrt()</b> operation.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief Calculate the magnitude of the vector, squared
   * @return squared magnitude of the vector
   * @see BDVector#mag()
   */
  public float magSq() {
    return (x*x + y*y);
  }


  /**
   *
   * Adds x, y, and z components to a vector, adds one vector to another, or adds
   * two independent vectors together. The version of the method that adds two
   * vectors together is a static method and returns a new <b>BDVector</b>, the others act
   * directly on the vector itself. See the examples for more context.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param v the vector to be added
   * @webBrief Adds x, y, and z components to a vector, one vector to another, or
   *           two independent vectors
   */
  public BDVector add(BDVector v) {
    x += v.x;
    y += v.y;
    return this;
  }


  /**
   * @param x x component of the vector
   * @param y y component of the vector
   */
  public BDVector add(float x, float y) {
    this.x += x;
    this.y += y;
    return this;
  }


  /**
   * Add two vectors
   * @param v1 a vector
   * @param v2 another vector
   */
  static public BDVector add(BDVector v1, BDVector v2) {
    return add(v1, v2, null);
  }


  /**
   * Add two vectors into a target vector
   * @param target the target vector (if null, a new vector will be created)
   */
  static public BDVector add(BDVector v1, BDVector v2, BDVector target) {
    if (target == null) {
      target = new BDVector(v1.x + v2.x,v1.y + v2.y);
    } else {
      target.set(v1.x + v2.x, v1.y + v2.y);
    }
    return target;
  }


  /**
   *
   * Subtracts x, y, and z components from a vector, subtracts one vector from
   * another, or subtracts two independent vectors. The version of the method that
   * substracts two vectors is a static method and returns a <b>BDVector</b>, the others
   * act directly on the vector. See the examples for more context. In all cases,
   * the second vector (v2) is subtracted from the first (v1), resulting in v1-v2.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param v any variable of type BDVector
   * @webBrief Subtract x, y, and z components from a vector, one vector from
   *           another, or two independent vectors
   */
  public BDVector sub(BDVector v) {
    x -= v.x;
    y -= v.y;
    return this;
  }


  /**
   * @param x the x component of the vector
   * @param y the y component of the vector
   */
  public BDVector sub(float x, float y) {
    this.x -= x;
    this.y -= y;
    return this;
  }



  /**
   * Subtract one vector from another
   * @param v1 the x, y, and z components of a BDVector object
   * @param v2 the x, y, and z components of a BDVector object
   */
  static public BDVector sub(BDVector v1, BDVector v2) {
    return sub(v1, v2, null);
  }


  /**
   * Subtract one vector from another and store in another vector
   * @param target BDVector in which to store the result
   */
  static public BDVector sub(BDVector v1, BDVector v2, BDVector target) {
    if (target == null) {
      target = new BDVector(v1.x - v2.x, v1.y - v2.y);
    } else {
      target.set(v1.x - v2.x, v1.y - v2.y);
    }
    return target;
  }


  /**
   *
   * Multiplies a vector by a scalar. The version of the method that uses a float
   * acts directly on the vector upon which it is called (as in the first example
   * above). The versions that receive both a <b>BDVector</b> and a float as arguments are
   * static methods, and each returns a new <b>BDVector</b> that is the result of the
   * multiplication operation. Both examples above produce the same visual output.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief Multiply a vector by a scalar
   * @param n the number to multiply with the vector
   */
  public BDVector mult(float n) {
    x *= n;
    y *= n;
    return this;
  }


  /**
   * @param v the vector to multiply by the scalar
   */
  static public BDVector mult(BDVector v, float n) {
    return mult(v, n, null);
  }


  /**
   * Multiply a vector by a scalar, and write the result into a target BDVector.
   * @param target BDVector in which to store the result
   */
  static public BDVector mult(BDVector v, float n, BDVector target) {
    if (target == null) {
      target = new BDVector(v.x*n, v.y*n);
    } else {
      target.set(v.x*n, v.y*n);
    }
    return target;
  }


  /**
   *
   * Divides a vector by a scalar. The version of the method that uses a float
   * acts directly on the vector upon which it is called (as in the first example
   * above). The version that receives both a <b>BDVector</b> and a <b>float</b> as arguments is
   * a static methods, and returns a new <b>BDVector</b> that is the result of the
   * division operation. Both examples above produce the same visual output.
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief Divide a vector by a scalar
   * @param n the number by which to divide the vector
   */
  public BDVector div(float n) {
    x /= n;
    y /= n;
    return this;
  }


  /**
   * Divide a vector by a scalar and return the result in a new vector.
   * @param v the vector to divide by the scalar
   * @return a new vector that is v1 / n
   */
  static public BDVector div(BDVector v, float n) {
    return div(v, n, null);
  }


  /**
   * Divide a vector by a scalar and store the result in another vector.
   * @param target BDVector in which to store the result
   */
  static public BDVector div(BDVector v, float n, BDVector target) {
    if (target == null) {
      target = new BDVector(v.x/n, v.y/n);
    } else {
      target.set(v.x/n, v.y/n);
    }
    return target;
  }


  /**
   *
   * Calculates the Euclidean distance between two points (considering a
   * point as a vector object).
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param v the x, y, and z coordinates of a BDVector
   * @webBrief  Calculate the distance between two points
   */
  public float dist(BDVector v) {
    float dx = x - v.x;
    float dy = y - v.y;
    return (float) Math.sqrt(dx*dx + dy*dy);
  }


  /**
   * @param v1 any variable of type BDVector
   * @param v2 any variable of type BDVector
   * @return the Euclidean distance between v1 and v2
   */
  static public float dist(BDVector v1, BDVector v2) {
    float dx = v1.x - v2.x;
    float dy = v1.y - v2.y;
    return (float) Math.sqrt(dx*dx + dy*dy);
  }


  /**
   *
   * Calculates the dot product of two vectors.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param v any variable of type BDVector
   * @return the dot product
   * @webBrief  Calculate the dot product of two vectors
   */
  public float dot(BDVector v) {
    return x*v.x + y*v.y;
  }


  /**
   * @param x x component of the vector
   * @param y y component of the vector
   */
  public float dot(float x, float y) {
    return this.x*x + this.y*y;
  }


  /**
   * @param v1 any variable of type BDVector
   * @param v2 any variable of type BDVector
   */
  static public float dot(BDVector v1, BDVector v2) {
    return v1.x*v2.x + v1.y*v2.y;
  }


  /**
   *
   * Normalize the vector to length 1 (make it a unit vector).
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief  Normalize the vector to a length of 1
   */
  public BDVector normalize() {
    float m = mag();
    if (m != 0 && m != 1) {
      div(m);
    }
    return this;
  }


  /**
   * @param target Set to null to create a new vector
   * @return a new vector (if target was null), or target
   */
  public BDVector normalize(BDVector target) {
    if (target == null) {
      target = new BDVector();
    }
    float m = mag();
    if (m > 0) {
      target.set(x/m, y/m);
    } else {
      target.set(x, y);
    }
    return target;
  }


  /**
   *
   * Limit the magnitude of this vector to the value used for the <b>max</b> parameter.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param max the maximum magnitude for the vector
   * @webBrief  Limit the magnitude of the vector
   */
  public BDVector limit(float max) {
    if (magSq() > max*max) {
      normalize();
      mult(max);
    }
    return this;
  }


  /**
   *
   * Set the magnitude of this vector to the value used for the <b>len</b> parameter.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param len the new length for this vector
   * @webBrief  Set the magnitude of the vector
   */
  public BDVector setMag(float len) {
    normalize();
    mult(len);
    return this;
  }


  /**
   * Sets the magnitude of this vector, storing the result in another vector.
   * @param target Set to null to create a new vector
   * @param len the new length for the new vector
   * @return a new vector (if target was null), or target
   */
  public BDVector setMag(BDVector target, float len) {
    target = normalize(target);
    target.mult(len);
    return target;
  }


  /**
   *
   * Calculate the angle of rotation for this vector (only 2D vectors)
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @return the angle of rotation
   * @webBrief  Calculate the angle of rotation for this vector
   */
  public float heading() {
    float angle = (float) Math.atan2(y, x);
    return angle;
  }

  public BDVector setHeading(float angle) {
    float m = mag();
    x = (float) (m * Math.cos(angle));
    y = (float) (m * Math.sin(angle));
    return this;
  }


  /**
   *
   * Rotate the vector by an angle (only 2D vectors), magnitude remains the same
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief  Rotate the vector by an angle (2D only)
   * @param theta the angle of rotation
   */
  public BDVector rotate(float theta) {
    float temp = x;
    // Might need to check for rounding errors like with angleBetween function?
    x = x*PApplet.cos(theta) - y*PApplet.sin(theta);
    y = temp*PApplet.sin(theta) + y*PApplet.cos(theta);
    return this;
  }


  /**
   *
   * Calculates linear interpolation from one vector to another vector. (Just like
   * regular <b>lerp()</b>, but for vectors.)<br />
   * <br />
   * Note that there is one <em>static</em> version of this method, and two
   * <em>non-static</em> versions. The static version, <b>lerp(v1, v2, amt)</b> is
   * given the two vectors to interpolate and returns a new BDVector object. The
   * static version is used by referencing the BDVector class directly. (See the
   * middle example above.) The non-static versions, <b>lerp(v, amt)</b> and
   * <b>lerp(x, y, z, amt)</b>, do not create a new BDVector, but transform the
   * values of the <b>BDVector</b> on which they are called. These non-static versions
   * perform the same operation, but the former takes another vector as input,
   * while the latter takes three float values. (See the top and bottom examples
   * above, respectively.)
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @webBrief Linear interpolate the vector to another vector
   * @param v   the vector to lerp to
   * @param amt The amount of interpolation; some value between 0.0 (old vector)
   *            and 1.0 (new vector). 0.1 is very near the old vector; 0.5 is
   *            halfway in between.
   * @see PApplet#lerp(float, float, float)
   */
  public BDVector lerp(BDVector v, float amt) {
    x = PApplet.lerp(x, v.x, amt);
    y = PApplet.lerp(y, v.y, amt);
    return this;
  }


  /**
   * Linear interpolate between two vectors (returns a new BDVector object)
   * @param v1 the vector to start from
   * @param v2 the vector to lerp to
   */
  public static BDVector lerp(BDVector v1, BDVector v2, float amt) {
    BDVector v = v1.copy();
    v.lerp(v2, amt);
    return v;
  }


  /**
   * Linear interpolate the vector to x,y values
   * @param x the x component to lerp to
   * @param y the y component to lerp to
   */
  public BDVector lerp(float x, float y, float amt) {
    this.x = PApplet.lerp(this.x, x, amt);
    this.y = PApplet.lerp(this.y, y, amt);
    return this;
  }


  /**
   *
   * Calculates and returns the angle (in radians) between two vectors.
   *
   *
   * @webref BDVector:method
   * @usage web_application
   * @param v1 the x, y components of a BDVector
   * @param v2 the x, y components of a BDVector
   * @webBrief  Calculate and return the angle between two vectors
   */
  static public float angleBetween(BDVector v1, BDVector v2) {

    // We get NaN if we pass in a zero vector which can cause problems
    // Zero seems like a reasonable angle between a (0,0,0) vector and something else
    if (v1.x == 0 && v1.y == 0) return 0.0f;
    if (v2.x == 0 && v2.y == 0) return 0.0f;

    double dot = v1.x * v2.x + v1.y * v2.y;
    double v1mag = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
    double v2mag = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
    // This should be a number between -1 and 1, since it's "normalized"
    double amt = dot / (v1mag * v2mag);
    // But if it's not due to rounding error, then we need to fix it
    // http://code.google.com/p/processing/issues/detail?id=340
    // Otherwise if outside the range, acos() will return NaN
    // http://www.cppreference.com/wiki/c/math/acos
    if (amt <= -1) {
      return PConstants.PI;
    } else if (amt >= 1) {
      // http://code.google.com/p/processing/issues/detail?id=435
      return 0;
    }
    return (float) Math.acos(amt);
  }


  @Override
  public String toString() {
    return "[ " + x + ", " + y + " ]";
  }


  /**
   *
   * Return a representation of this vector as a float array. This is only for
   * temporary use. If used in any other fashion, the contents should be copied by
   * using the <b>copy()</b> method to copy into your own array.
   *
   *
   * @webref BDVector:method
   * @usage: web_application
   * @webBrief Return a representation of the vector as a float array
   */
  public float[] array() {
    if (array == null) {
      array = new float[2];
    }
    array[0] = x;
    array[1] = y;
    return array;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BDVector)) {
      return false;
    }
    final BDVector p = (BDVector) obj;
    return x == p.x && y == p.y;
  }


  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + Float.floatToIntBits(x);
    result = 31 * result + Float.floatToIntBits(y);
    return result;
  }
}