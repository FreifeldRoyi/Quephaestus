package com.freifeld.tools.quephaestus.core;

import java.io.Closeable;

public interface InterpolationSlotResolver extends Closeable
{
	String valueFor(String slot);
}
