package com.infinityraider.maneuvergear.physics;

import com.infinityraider.maneuvergear.ManeuverGear;
import com.infinityraider.maneuvergear.entity.EntityDart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
 * And no I am not fully satisfied with the result, it started out as a simple calculation for two cases, but then the exceptions I didn't take into account decided to exist...
 *
 */
@OnlyIn(Dist.CLIENT)
public final class PhysicsEngineClientGeometric extends PhysicsEngineClientBase {
    /** Velocity at which the cable is retracted (blocks per second) */
    public static final double retractingVelocity = ManeuverGear.instance.getConfig().getRetractingSpeed()/20.0D;

    /** Locked state*/
    private boolean locked;

    public PhysicsEngineClientGeometric(Player player) {
        super(player);
    }

    @Override
    protected void doUpdateLogic() {
        //apply gravity: velocity should increase with 10m/s / s, therefore velocity has to increase with 0.0025m/tick / tick
        this.player().setDeltaMovement(this.player().getDeltaMovement().add(0, -0.0025D, 0));
        //decrement cables if necessary
        this.decrementCableLength(this.getLeftDart());
        this.decrementCableLength(this.getRightDart());
        this.locked = false;
        //fetch initial conditions
        Vec3 p = this.playerPosition();
        Vec3 v_old = this.fetchCurrentVelocity();
        Vec3 v = v_old.add(0,0,0);
        Vec3 p_new = this.calculateNewPosition(p, v);
        //only left dart is active
        if(this.L() != null && this.R() == null) {
            //check if the new position conflicts with the left constraint
            boolean leftOk = this.compliesWithConstraint(p_new, this.getLeftDart());
            if (!leftOk) {
                //calculate velocity respecting the left constraint
                v = this.calculateVelocityForSingleCondition(this.getLeftDart(), p, p_new);
            }
            this.setPlayerVelocity(v);
        }
        //only right  dart is active
        else if(this.L() == null && this.R() != null) {
            //check if the new position conflicts with the right constraint
            boolean rightOk = this.compliesWithConstraint(p_new, this.getRightDart());
            if (!rightOk) {
                //calculate velocity respecting the right constraint
                v = this.calculateVelocityForSingleCondition(this.getRightDart(), p, p_new);
            }
            setPlayerVelocity(v);
        }
        // both darts are active
        else {
            //check if the new position conflicts with either constraints
            boolean leftOk = this.compliesWithConstraint(p_new, this.getLeftDart());
            boolean rightOk = this.compliesWithConstraint(p_new, this.getRightDart());
            if (leftOk && rightOk) {
                // the new position complies with both constrains: velocity is allowed
                this.setPlayerVelocity(v);
            } else if (!leftOk && rightOk) {
                //Right constraint is already fulfilled, calculate velocity respecting the left constraint
                this.setPlayerVelocity(this.calculateVelocityForSingleCondition(this.getLeftDart(), p, p_new));
            } else if (leftOk /*rightOK is certainly false at this point, no need to add it to the if*/) {
                //Left constraint is already fulfilled, calculate velocity respecting the right constraint
                this.setPlayerVelocity(this.calculateVelocityForSingleCondition(this.getRightDart(), p, p_new));
            } else {
                // the new position does not comply with either of the constraints: math is required
                this.setPlayerVelocity(this.calculateVelocityForDoubleCondition(this.getLeftDart(), this.getRightDart(), p, p_new, v_old));
            }
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
    private Vec3 calculateVelocityForSingleCondition(EntityDart conflicting, Vec3 p_old, Vec3 P_new) {
        Vec3 D = conflicting.isLeft() ? this.L() : this.R();
        Vec3 DPnew = getCableVector(P_new, D);
        double norm = DPnew.length();
        double factor = conflicting.getCableLength() / norm;
        DPnew = DPnew.multiply(factor, factor, factor);
        Vec3 Pnew = DPnew.add(D);
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
    private Vec3 calculateVelocityForDoubleCondition(EntityDart left, EntityDart right, Vec3 p_old, Vec3 p_new, Vec3 v_old) {
        double l = left.getCableLength();
        double r = right.getCableLength();
        //determine new position in case of conflicting cable lengths, in case this is null, the cable lengths are not in conflict
        Vec3 Pn = calculatePositionForConflictingCableLengths(this.L(), this.R(), l, r);
        if(Pn != null) {
            return calculateVelocity(Pn, p_old);
        }
        Vec3 P_L = calculateNewPositionForDoubleCondition(this.L(), p_old, v_old, l);
        Vec3 P_R = calculateNewPositionForDoubleCondition(this.R(), p_old, v_old, r);
        boolean l_ok = P_L != null && compliesWithConstraint(P_L, right);
        boolean r_ok = P_R != null && compliesWithConstraint(P_R, left);
        if(l_ok && !r_ok) {
            return calculateVelocity(P_L, p_old);
        }
        if(r_ok && !l_ok) {
            return calculateVelocity(P_R, p_old);
        }
        else {
            Vec3 P = calculateIntersectionPoint(this.L(), l, this.R(), r, p_new);
            if(P != null) {
                return calculateVelocity(P, p_old);
            }
            //one sphere is fully inside the other: only the one with the smallest radius matters
            return calculateVelocity(findInterSectPointWithSphere(l < r ? this.L() : this.R(), Math.min(l, r), p_new), p_old);
        }
    }

    /**
     * This method checks if the two darts are further apart than their combined cable lengths, and if so,
     * calculates the player's new position by interpolating based on the respective cable lengths
     * @param A position vector of the left dart
     * @param B position vector of the right dart
     * @param a length of the left cable
     * @param b length of the right cable
     * @return a position vector of a point on the line AB, given by a distance interpolated by the ratio of lengths of a and b, or null if the cable lengths are not conflicting
     */
    private Vec3 calculatePositionForConflictingCableLengths(Vec3 A, Vec3 B, double a, double b) {
        Vec3 AB = B.subtract(A);
        double l = AB.length();
        if((a + b) <= l) {
            double f;
            if(a == 0 && b == 0) {
                //divide by zero check:
                f = 0.5;
            }
            else {
                f = a/ (a + b);
            }
            this.locked = true;
            return A.add(AB.multiply(f, f, f));
        } else {
            return null;
        }
    }

    /**
     * This method calculates the new position of the player by scaling down the velocity to match the constraint of the dart's cable length
     *
     * It solves the system given by the following equations of the vector x:
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
    private Vec3 calculateNewPositionForDoubleCondition(Vec3 A, Vec3 P, Vec3 V, double l) {
        Vec3 AP = P.subtract(A);
        double a = V.dot(V);
        double b = 2*(V.x()*AP.x() + V.y()*AP.y()+ V.z()*AP.z());
        double c = AP.dot(AP) - l*l;
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
        return A.add(AP.add(V.multiply(k, k, k)));
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
    private Vec3 calculateIntersectionPoint(Vec3 A, double a, Vec3 B, double b, Vec3 P) {
        Vec3 AB = B.subtract(A);
        double c = AB.length();
        //calculate intersection of circle plane and AB
        double k = calculateHypotenuseIntersectRatio(a, b, c);
        if(k > a/c) {
            return null;
        }
        double d = calculateHeight(a, c, k);
        Vec3 M = A.add(AB.multiply(k, k, k));
        //calculate projection of P on AB
        k = calculateProjectionRatio(AB, A, P);
        Vec3 Pp = A.add(AB.multiply(k, k, k));
        Vec3 PpP = P.subtract(Pp);
        //point is defined by M + d.PpP/||PpP||
        return M.add(PpP.normalize().multiply(d, d, d));
    }

    /**
     * Calculates the position vector for the intersection of line AB and the sphere with radius a and center A
     * @param A center point of sphere
     * @param a radius of sphere
     * @param P point to define AP
     * @return the intersection point with the sphere
     */
    private Vec3 findInterSectPointWithSphere(Vec3 A, double a, Vec3 P) {
        Vec3 AP = P.subtract(A);
        return A.add(AP.normalize().multiply(a, a, a));
    }

    private void decrementCableLength(EntityDart dart) {
        if(this.locked) {
            return;
        }
        if(dart == null) {
            return;
        }
        if((dart.isLeft() && isRetractingLeft()) || (!dart.isLeft() && isRetractingRight())) {
            dart.setCableLength(dart.getCableLength() - retractingVelocity);
        }
    }

    private Vec3 calculateNewPosition(Vec3 p, Vec3 v) {
        return p.add(v);
    }

    private Vec3 calculateVelocity(Vec3 p_new, Vec3 p_old) {
        return p_new.subtract(p_old);
    }

    /** checks if the player's distance to the dart is smaller than the cable length */
    private boolean compliesWithConstraint(Vec3 position, EntityDart dart) {
        return dart == null || !dart.isHooked() || position.subtract(dart.isLeft() ? this.L() : this.R()).length() <= dart.getCableLength();
    }

    /** Returns a vector pointing from the player to the dart */
    private Vec3 getCableVector(Vec3 position, Vec3 dartPosition) {
        return position.subtract(dartPosition);
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
     * This method solves the set of equations to determine the projection of a point P on a line AB
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
    private double calculateProjectionRatio(Vec3 AB, Vec3 A, Vec3 P) {
        double a = AB.dot(AB);
        double b = P.subtract(A).dot(AB);
        return b/a;
    }
}
