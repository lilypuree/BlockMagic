package lilypuree.blockmagic.client;

import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface BlockDefinitionProvider {
    Map<String, MultiVariant> getVariants();
}
