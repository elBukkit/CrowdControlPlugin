package com.elbukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.Vector;

import com.elbukkit.plugins.crowd.CrowdControlPlugin;
import com.elbukkit.plugins.crowd.Info;

/*
 * Control where entities can be, i.e. entity zones.
 * 
 * TODO Waiting on the feature request #157
 * http://leaky.bukkit.org/issues/157
 * 
 * @author Andrew Querol(WinSock)
 */

public class MovmentLocationRule extends Rule {

    // private long[] xyzA = { 0, 0, 0 };
    // private long[] xyzB = { 0, 0, 0 };

    private Vector point1, point2;

    public MovmentLocationRule(World world, CreatureType type, CrowdControlPlugin plugin) {
        super(world, type, plugin);
        this.ruleType = Type.Movement;
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean check(Info info) {
        if (info.getLocation().toVector().isInAABB(point1, point2)) {
            return true;
        }
        return false;
    }

    // TODO Move the constructors to init()
    /*
     * public MovmentLocationRule(long[] xyzA, long[] xyzB, Set<World> worlds,
     * CreatureType type) { this.xyzA = xyzA.clone(); this.xyzB = xyzB.clone();
     * this.normalize();
     * 
     * this.point1 = new Vector(xyzA[0], xyzA[1], xyzA[2]); this.point2 = new
     * Vector(xyzB[0], xyzB[1], xyzB[2]);
     * 
     * this.worlds = worlds; this.type = type; }
     * 
     * public MovmentLocationRule(Vector point1, Vector point2, Set<World>
     * worlds, CreatureType type) { this.xyzA[0] = point1.getBlockX();
     * this.xyzA[1] = point1.getBlockY(); this.xyzA[2] = point1.getBlockZ();
     * 
     * this.xyzB[0] = point2.getBlockX(); this.xyzB[1] = point2.getBlockY();
     * this.xyzB[2] = point2.getBlockZ();
     * 
     * this.normalize();
     * 
     * this.point1 = new Vector(xyzA[0], xyzA[1], xyzA[2]); this.point2 = new
     * Vector(xyzB[0], xyzB[1], xyzB[2]);
     * 
     * this.worlds = worlds; this.type = type; }
     */

    @Override
    public void init(String data) {
        // TODO Finish init()
    }

    /*
     * private void normalize() { long temp; for (int i = 0; i < 3; i++) { if
     * (this.xyzA[i] > this.xyzB[i]) { temp = this.xyzA[i]; this.xyzA[i] =
     * this.xyzB[i]; this.xyzB[i] = temp; } } }
     */
}