package ax.xz.max.fileserver.util;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class FileDataEntity {
	public FileDataEntity() {
	}

	public FileDataEntity(String path, Instant uploadDate, FileVisibility visibility, String password) {
		this.path = path;
		this.uploadDate = uploadDate;
		this.visibility = visibility;
		this.password = password;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String path;
	private Instant uploadDate;

	@Enumerated
	private FileVisibility visibility;
	private String password;
}
