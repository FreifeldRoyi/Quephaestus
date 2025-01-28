package com.freifeld.tools.quephaestus;

import java.io.Closeable;

public interface DatamapSource extends Closeable
{
	String valueFor(String slot);
}
