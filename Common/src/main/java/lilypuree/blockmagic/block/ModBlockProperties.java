package lilypuree.blockmagic.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class ModBlockProperties {
    public static DirectionProperty SECONDARY_FACING = DirectionProperty.create("secondary_facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);

}
