package com.InfinityRaider.maneuvergear.physics;

import com.InfinityRaider.maneuvergear.entity.EntityDart;
import com.InfinityRaider.maneuvergear.handler.ConfigurationHandler;
import com.InfinityRaider.maneuvergear.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is used to calculate the player's new velocity when using one or two grapples,
 * To truly solve this problem, a set of 5 differential equations in 5 variables (player position and tension in both cables: x, y, z, T_L, T_R) has to be solved:
 *   Newton's law of motion (3 equations)
 *   Constraints for darts (2 equations)
 *
 * Because using either finite differencing and storing data of previous ticks, or iteratively solving the system would take a lot of processing time/memory
 * I decided to find a geometrical approximation, the choices and math is explained in the comments, and there is an excel doc found in the GitHub repo.
 *
 *
 * Yes I know the parameter names are not conform with java naming conventions, but the fact that they didn't conform with my math notations bothered me more.
 *
 * And no I am not happy with this class, it started out as a simple calculation for two cases, but then the exceptions I didn't take into account attacked...
 */
@SideOnly(Side.CLIENT)
public final class PhysicsEngineClientLocal extends PhysicsEngine {
    /** Velocity at which the cable is retracted (blocks per second) */
    public static final double retractingVelocity = ConfigurationHandler.retractingSpeed/20.0D;

    /** Boost intensity */
    public static final int BOOST = 2;

    /** Player */
    private final EntityPlayer player;

    /** Left dart */
    private Vector L;
    private boolean retractingLeft;

    /**  Right Dart */
    private Vector R;
    private boolean retractingRight;

    public PhysicsEngineClientLocal(EntityPlayer player) {
        super();
        this.player = player;
    }

    @Override
    public void updateTick() {
        decrementCableLength(leftDart);
        decrementCableLength(rightDart);
        Vector p = calculateOldPosition();
        Vector v_old = calculateVelocity();
        Vector v = v_old.copy();
        Vector p_new = calculateNewPosition(p, v);
        getAnchoredDartPosition(leftDart);
        getAnchoredDartPosition(rightDart);
        boolean leftOk = compliesWithConstraint(p_new, leftDart);
        boolean rightOk = compliesWithConstraint(p_new, rightDart);
        if (!leftOk && rightOk) {
            v = calculateVelocityForSingleCondition(leftDart, p, p_new);
            leftOk = v!=null;
        } else if (leftOk && !rightOk) {
            v = calculateVelocityForSingleCondition(rightDart, p, p_new);
            rightOk = v!=null;
        }
        /** The new position complies with both constrains: velocity is allowed */
        if (leftOk && rightOk) {
            setPlayerVelocity(v);
        /** The new position complies with neither constraints, this implies both darts are not null */
        } else {
            setPlayerVelocity(calculateVelocityForDoubleCondition(leftDart, rightDart, p , p_new, v_old));
        }
    }

    /**
     * I propose the following approximation for when one dart doesn't comply:
     *  1) Define a vector DPnew, pointing from the conflicting dagger to the player, and scale it according to the permitted length divided by the actual length
     *       DPnew = DP*l/||DP||
     *  2) Determine the absolute new position of the player
     *       Pnew = DPnew + D
     *  3) Check if the new position also complies with the other dart, if it does calculate the velocity for the new position and accept the solution
     */
    private Vector calculateVelocityForSingleCondition(EntityDart conflicting, Vector p_old, Vector P_new) {
        Vector D = conflicting.isLeft()?L:R;
        Vector DPnew = getCableVector(P_new, D);
        double norm = DPnew.norm();
        DPnew = DPnew.scale(conflicting.getCableLength() / norm);
        Vector Pnew = DPnew.add(D);
        return calculateVelocity(Pnew, p_old);
    }

    /**
     * Player velocity doesn't comply with both constraints,
     * either from the start or after trying out an approximation,
     * meaning we will go towards an intersection of the two spheres.
     * we are also certain that both darts are not null and are hooked.
     *
     * First we will check if the distance between the darts is shorter than the sum of both cable lengths,
     * if this is not the case, then we are in an impossible solution and we move the player to a position in between both darts,
     * interpolated by the lengths of both cables.
     *
     * If the cable lengths are not in an illegal state, we'll continue with the following approximation:
     * The player's
     */
    private Vector calculateVelocityForDoubleCondition(EntityDart left, EntityDart right, Vector p_old, Vector p_new, Vector v_old) {
        double l = left.getCableLength();
        double r = right.getCableLength();
        Vector L = left.getPositionAsVector();
        Vector R = right.getPositionAsVector();
        Vector Pn = calculatePositionForConflictingCableLengths(L, R, l, r);
        if(Pn != null) {
            return calculateVelocity(Pn, p_old);
        }
        Vector P_L = calculateNewPositionForDoubleCondition(L, p_old, v_old, l);
        Vector P_R = calculateNewPositionForDoubleCondition(R, p_old, v_old, r);
        boolean l_ok = P_L != null && compliesWithConstraint(P_L, right);
        boolean r_ok = P_R != null && compliesWithConstraint(P_R, left);
        if(l_ok && !r_ok) {
            return calculateVelocity(P_L, p_old);
        }
        if(r_ok && !l_ok) {
            return calculateVelocity(P_R, p_old);
        }
        else {
            Vector P = calculateIntersectionPoint(L, l, R, r, p_new);
            if(P != null) {
                return calculateVelocity(P, p_old);
            }
            //one sphere is fully inside the other: only the one with the smallest radius matters
            return calculateVelocity(findInterSectPointWithSphere(l < r ? L : R, l < r ? l : r, p_new), p_old);
        }
    }

    /**
     * This method checks if the
     * @param A position vector of the left dart
     * @param B position vector of the right dart
     * @param a length of the left cable
     * @param b length of the right cable
     * @return a position vector of a point on the line AB, given by a distance interpolated by the ratio of lengths of a and b, or null if the cable lengths are not conflicting
     */
    private Vector calculatePositionForConflictingCableLengths(Vector A, Vector B, double a, double b) {
        Vector AB = B.substract(A);
        double l = AB.norm();
        if((a + b) <= l) {
            double f;
            if(a == 0 && b == 0) {
                //divide by zero check:
                f = 0.5;
            }
            else {
                f = a/ (a + b);
            }
            return A.add(AB.scale(f));
        } else {
            return null;
        }
    }

    /**
     * This method calculates the new position of the player by scaling down the velocity to match the constraint of the dart's cable length
     *
     * It solves the system given by the following equations ot the vector x:
     *   AP + kV = x  (1)
     *   ||x||^2 = l^2 (2)
     * This system can be easily solved by substituting the 3 scalar equations for equation 1 into equation 2 and solving for k,
     * This results in an equation given by a.x^2 + b.x + c = 0, with
     *   a = ||V||^2
     *   b = 2(Vx.APx + Vy.APy + Vz.APz)
     *   c = ||AP||^2 - l^2
     *
     * When the player is currently inside the sphere around the dart with radius l, this will have two solutions for k,
     * one positive and one negative (from which we use the positive).
     *
     * If the player is currently outside that sphere, there might be no solution and we return null and do another calculation
     *
     *
     * @param A the position of the dart
     * @param P the position of the player
     * @param V the velocity of the player
     * @param l the length of the dart's cable
     * @return the new position of the player, might be null if there is no valid solution
     */
    private Vector calculateNewPositionForDoubleCondition(Vector A, Vector P, Vector V, double l) {
        Vector AP = P.substract(A);
        double a = Vector.dotProduct(V, V);
        double b = 2*(V.getX()*AP.getX() + V.getY()*AP.getY()+ V.getZ()*AP.getZ());
        double c = Vector.dotProduct(AP, AP) - l*l;
        double d = b*b - 4*a*c;
        if(d < 0) {
            return null;
        }
        double k1 = (-b + Math.sqrt(d))/(2*a);
        double k2 = (-b + Math.sqrt(d))/(2*a);
        double k;
        if(k1 < 0 && k2 >= 0) {
            //Player was inside the sphere, positive k is the accepted solution
            k = k2;
        }
        else if(k2 < 0 && k1 >= 0) {
            //Player was inside the sphere, positive k is the accepted solution
            k = k1;
        }
        else {
            //Player was outside the sphere, but there is a solution.
            //Either both k's are negative, meaning the sphere is behind the player,
            //or both k's are positive, meaning the sphere is in front of the player.
            //either way, the solution with the smallest absolute value is the accepted solution
            double k1_abs = Math.abs(k1);
            double k2_abs = Math.abs(k2);
            k = k1_abs < k2_abs ? k1 : k2;
        }
        return A.add(AP.add(V.copy().scale(k)));
    }

    /**
     * This method calculates the position vector of a point on the intersection of both spheres defined by A and B and respective radii a and b.
     * The new position lies on a circle and the exact point is determined by the position of the player
     *
     * First the point M is calculated, this is the intersection of line AB with the plane of the intersection circle of the two spheres
     * The radius (d) of the intersection circle is also calculated
     *
     * Then the point on this circle, closest to the point P is determined by projecting the point P onto AB, resulting in P'
     * The new position is then M + d.P'P/||P'P||
     *
     * Note that if one sphere is fully inside of the other sphere, or the spheres are too far from each other to intersect,
     * there will be no solution and null will be returned.
     *
     * @param A position of the left dart
     * @param a length of the left cable
     * @param B position of the right dart
     * @param b length of the right cable
     * @param P new position of the player after adding old velocity
     * @return The position vector, or null if there is no solution
     */
    private Vector calculateIntersectionPoint(Vector A, double a, Vector B, double b, Vector P) {
        Vector AB = B.substract(A);
        double c = AB.norm();
        //calculate intersection of circle plane and AB
        double k = calculateHypotenuseIntersectRatio(a, b, c);
        if(k > a/c) {
            return null;
        }
        double d = calculateHeight(a, c, k);
        Vector M = A.add(AB.copy().scale(k));
        //calculate projection of P on AB
        k = calculateProjectionRatio(AB, A, P);
        Vector Pp = A.add(AB.copy().scale(k));
        Vector PpP = P.substract(Pp);
        //point is defined by M + d.PpP/||PpP||
        return M.add(PpP.normalize().scale(d));
    }

    /**
     * Calculates the position vector for the intersection of line AB and the sphere with radius a and center A
     * @param A center point of sphere
     * @param a radius of sphere
     * @param P point to define AP
     * @return the intersection point with the sphere
     */
    private Vector findInterSectPointWithSphere(Vector A, double a, Vector P) {
        Vector AP = P.substract(A);
        return A.add(AP.normalize().scale(a));
    }

    @Override
    public void onDartAnchored(EntityDart dart) {
        if(dart.isLeft()) {
            L = dart.getPositionAsVector();
        }
        else {
            R = dart.getPositionAsVector();
        }
    }

    public void onDartRetracted(boolean left) {
    }

    private void decrementCableLength(EntityDart dart) {
        if(dart == null) {
            return;
        }
        if((dart.isLeft() && retractingLeft) || (!dart.isLeft() && retractingRight)) {
            dart.setCableLength(dart.getCableLength() - retractingVelocity);
        }
    }

    public void toggleRetracting(boolean left, boolean status) {
        if(left) {
            retractingLeft = status;
        }
        else {
            retractingRight = status;
        }
    }

    @Override
    public void applyBoost() {
        Vector boost = (new Vector(player.getLookVec())).scale(BOOST);
        Vector v = calculateVelocity();
        setPlayerVelocity(v.add(boost));
    }

    private Vector calculateOldPosition() {
        return new Vector(player.posX, player.posY, player.posZ);
    }

    private Vector calculateNewPosition(Vector p, Vector v) {
        return p.add(v);
    }

    private Vector calculateVelocity() {
        return new Vector(player.motionX, player.motionY, player.motionZ);
    }

    private Vector calculateVelocity(Vector p_new, Vector p_old) {
        return p_new.substract(p_old);
    }

    private Vector getAnchoredDartPosition(EntityDart dart) {
        if(dart == null) {
            return null;
        }
        if(dart.isLeft()) {
            if(L == null) {
                L = dart.isHooked()?dart.getPositionAsVector():null;
            }
            return L;
        } else {
            if(R == null) {
                R = dart.isHooked()?dart.getPositionAsVector():null;
            }
            return R;
        }
    }

    /** Sets the player's velocity to correspond to a certain velocity given by vector V */
    private void setPlayerVelocity(Vector V) {
        double vX = V.getX();
        double vY = V.getY();
        double vZ = V.getZ();
        if(Double.isNaN(vX)) {
            LogHelper.debug("vX is Nan");
            vX = 0;
        }
        if(Double.isNaN(vY)) {
            LogHelper.debug("vY is Nan");
            vY = 0;
        }
        if(Double.isNaN(vZ)) {
            LogHelper.debug("vZ is Nan");
            vZ = 0;
        }
        player.motionX = vX;
        player.motionY = vY;
        player.motionZ = vZ;
    }

    /** checks if the player's distance to the dart is smaller than the cable length */
    private boolean compliesWithConstraint(Vector position, EntityDart dart) {
        return dart==null || !dart.isHooked() || position.substract(dart.isLeft()?L:R).norm() <= dart.getCableLength();
    }

    /** Returns a vector pointing from the player to the dart */
    private Vector getCableVector(Vector position, Vector dartPosition) {
        return position.substract(dartPosition);
    }

    /**
     * Consider a triangle ABP with sides a, b and c:
     *   AB = c
     *   BP = b
     *   AP = a
     *
     * This method calculates the fraction of side c where the line perpendicular to c, trough P intersects c
     *
     * This is simply done by splitting the triangle in two rectangular triangles and using Pythagoras' theorem to calculate d in both triangles
     * Equating both expressions for d results in a single expression for x
     *
     * @param a length of side a
     * @param b length of side b
     * @param c length of side d
     * @return fraction of c
     */
    private double calculateHypotenuseIntersectRatio(double a, double b, double c) {
        return (a*a - b*b + c*c)/(2*c*c);
    }

    /**
     * Method used with calculateHypotenuseIntersect:
     * This calculates the distance of the intersection point defined by 'calculateHypotenuseIntersect' and P
     * @param a length of side a
     * @param c length of side b
     * @param x fraction of c where the line perpendicular to c, trough P intersects c
     * @return height of the triangle abc perpendicular to c
     */
    private double calculateHeight(double a, double c, double x) {
        return Math.sqrt(a*a - x*x*c*c);
    }

    /**
     * This method solves the set of equations to determine the projection of a point p on a line AB
     * The equations are:
     *   PX.AB = 0 = (X - P).AB (1)
     *   AX = k.AB = (X - A)    (2)
     * Substituting the three scalar equations of 2 into equation 1 gives a single expression for k:
     *   k.||AB||^2 = (P - A).AB
     *
     * @param AB The vector defining AB
     * @param A The vector defining the position of point A on line AB
     * @param P The point to be projected
     * @return k
     */
    private double calculateProjectionRatio(Vector AB, Vector A, Vector P) {
        double a = Vector.dotProduct(AB, AB);
        double b = Vector.dotProduct(P.substract(A), AB);
        return b/a;
    }
}
