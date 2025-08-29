package virtuoel.no_fog.mixin.client.compat119plus;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import virtuoel.no_fog.NoFogClient;
import virtuoel.no_fog.util.FogToggleType;
import virtuoel.no_fog.util.ReflectionUtils;

@Mixin(value = FogRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
	@Redirect(method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"))
	private boolean applyFogModifyDistance(FogModifier instance, CameraSubmersionType cameraSubmersionType, Entity entity, @Local(argsOnly = true) boolean thick)
	{
//		final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
//		final Entity entity = camera.getFocusedEntity();

		if (!NoFogClient.isToggleEnabled(getFogType(instance, thick, cameraSubmersionType, entity), entity))
		{
//			Fog fog = cir.getReturnValue();
//			cir.setReturnValue(new Fog(NoFogClient.FOG_START, NoFogClient.FOG_END, FogShape.CYLINDER, color.x, color.y, color.z, color.w));
			return false;
		}

		return instance.shouldApply(cameraSubmersionType,entity);
	}
	
	@Unique
	private static FogToggleType getFogType(FogModifier fogInstance, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity)
	{
		if (cameraSubmersionType == CameraSubmersionType.LAVA)
		{
			return FogToggleType.LAVA;
		}
		
		if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
		{
			return FogToggleType.POWDER_SNOW;
		}
		
		if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.BLINDNESS))
		{
			return FogToggleType.BLINDNESS;
		}
		
		if (entity instanceof LivingEntity && ReflectionUtils.hasStatusEffect((LivingEntity) entity, ReflectionUtils.DARKNESS))
		{
			return FogToggleType.DARKNESS;
		}
		
		if (cameraSubmersionType == CameraSubmersionType.WATER)
		{
			return FogToggleType.WATER;
		}
		
		if (thickFog)
		{
			return FogToggleType.THICK;
		}
		
		if (fogInstance instanceof AtmosphericFogModifier)
		{
			return FogToggleType.SKY;
		}
		
		return FogToggleType.TERRAIN;
	}
}
