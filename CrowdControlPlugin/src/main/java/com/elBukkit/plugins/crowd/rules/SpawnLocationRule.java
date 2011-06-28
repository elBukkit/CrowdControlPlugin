package com.elBukkit.plugins.crowd.rules;

import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.Vector;

import com.elBukkit.plugins.crowd.CrowdControlPlugin;
import com.elBukkit.plugins.crowd.Info;

/*
 * A rule that prevents creatures from spawning in certain 3D cubes.
 * 
 * @author Andrew Querol(WinSock)
 */

public class SpawnLocationRule extends Rule {

    private Vector point1, point2;
    private long[] xyzA = { 0, 0, 0 };

    private long[] xyzB = { 0, 0, 0 };

    public SpawnLocationRule(World world, CreatureType type, CrowdControlPlugin plugin) {
        super(world, type, plugin);
        this.ruleType = Type.Spawn;
    }

    @Override
    public boolean check(Info info) {
        if (info.getLocation().toVector().isInAABB(point1, point2)) {
            return true;
        }
        return false;
    }

    @Override
    public String getData() {
        String data = "";

        for (long l : this.xyzA) {
            data += String.valueOf(l) + ",";
        }

        for (long l : this.xyzB) {
            data += String.valueOf(l) + ",";
        }

        return data.substring(0, data.length() - 1);
    }

    @Override
    public void init(String data) {
        String[] dataSplit = data.split(",");

        this.xyzA[0] = Long.parseLong(dataSplit[0]);
        this.xyzA[1] = Long.parseLong(dataSplit[1]);
        this.xyzA[2] = Long.parseLong(dataSplit[2]);

        this.xyzB[0] = Long.parseLong(dataSplit[3]);
        this.xyzB[1] = Long.parseLong(dataSplit[4]);
        this.xyzB[2] = Long.parseLong(dataSplit[5]);

        normalize();

        this.point1 = new Vector(xyzA[0], xyzA[1], xyzA[2]);
        this.point2 = new Vector(xyzB[0], xyzB[1], xyzB[2]);
    }

    private void normalize() {
        long temp;
        for (int i = 0; i < 3; i++) {
            if (this.xyzA[i] > this.xyzB[i]) {
                temp = this.xyzA[i];
                this.xyzA[i] = this.xyzB[i];
                this.xyzB[i] = temp;
            }
        }
    }
}
