package nws.mc.servers.data$type;

import net.minecraft.server.level.ServerPlayer;

public class PosData {
    public String Level;
    public double X;
    public double Y;
    public double Z;
    public float Yaw;
    public float Pitch;
    public PosData() {
        this("",0,0,0,0,0);
    }
    public PosData(String level, double x, double y, double z, float yaw, float pitch) {
        Level = level;
        X = x;
        Y = y;
        Z = z;
        Yaw = yaw;
        Pitch = pitch;
    }
    public static PosData create(ServerPlayer player) {
        return new PosData(player.level().dimension().location().toString(),player.getX(),player.getY(),player.getZ(),player.getYRot(),player.getXRot());
    }

    public PosData copy() {
        return new PosData(Level,X,Y,Z,Yaw,Pitch);
    }
}
