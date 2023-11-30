package ax.xz.max.fileserver.util;

import java.nio.file.Path;
import java.time.Instant;

public record FileDataRecord(
	Long id,
	Path path,
	Instant uploadDate,
	FileVisibility visibility,
	String password
) {
}
