package com.freifeld.tools.quephaestus;

import io.smallrye.mutiny.tuples.Tuple2;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.nio.file.Path;

@ApplicationScoped
public class FileSystemWriter
{
	@Inject
	Vertx vertx;

	@Incoming("content")
	public void writeContent(Tuple2<Path, String> tuple)
	{
		var path = tuple.getItem1();
		var content = tuple.getItem2();
		this.vertx.fileSystem().writeFile(path.toString(), Buffer.buffer(content));
	}
}
