package cn.evolvefield.mods.morechickens.common.net;

import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageChickenParticles implements Message<MessageChickenParticles>{
    private BlockPos pos;

    public MessageChickenParticles(BlockPos pos) {
        this.pos = pos;
    }

    public MessageChickenParticles() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) { //TODO check server crash
        TileEntity tileEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (tileEntity instanceof BreederTileEntity) {
            BreederTileEntity breeder = (BreederTileEntity) tileEntity;
            breeder.spawnParticles();
        }
    }

    @Override
    public MessageChickenParticles fromBytes(PacketBuffer packetBuffer) {
        pos = packetBuffer.readBlockPos();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(pos);
    }
}
