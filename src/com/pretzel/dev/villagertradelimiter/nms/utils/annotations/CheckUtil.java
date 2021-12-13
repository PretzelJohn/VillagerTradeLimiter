package com.pretzel.dev.villagertradelimiter.nms.utils.annotations;

import java.lang.reflect.Method;

import com.pretzel.dev.villagertradelimiter.nms.NbtApiException;
import com.pretzel.dev.villagertradelimiter.nms.utils.MinecraftVersion;

public class CheckUtil {

	public static boolean isAvaliable(Method method) {
		if(MinecraftVersion.getVersion().getVersionId() < method.getAnnotation(AvailableSince.class).version().getVersionId())
			throw new NbtApiException("The Method '" + method.getName() + "' is only avaliable for the Versions " + method.getAnnotation(AvailableSince.class).version() + "+, but still got called!");
		return true;
	}
	
}
