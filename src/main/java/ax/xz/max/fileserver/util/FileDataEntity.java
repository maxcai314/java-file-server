package ax.xz.max.fileserver.util;

import jakarta.persistence.*;

import java.nio.file.Path;
import java.time.Instant;

@Entity
public class FileDataEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Convert(converter = PathConverter.class)
	private Path path;
	private Instant uploadDate;

	@Enumerated
	private FileVisibility visibility;
	private String password;
}
