package ax.xz.max.fileserver.util;

import jakarta.persistence.*;

import java.nio.file.Path;
import java.time.Instant;

@Entity
public class FileDataEntity {
	public FileDataEntity() {
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

	public Long getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public Path getFilePath() {
		return Path.of(path);
	}

	public Instant getUploadDate() {
		return uploadDate;
	}

	public FileVisibility getVisibility() {
		return visibility;
	}

	public String getPassword() {
		return password;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setFilePath(Path path) {
		this.path = path.toString();
	}

	public void setUploadDate(Instant uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setVisibility(FileVisibility visibility) {
		this.visibility = visibility;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPasswordProtected() {
		return password != null;
	}
}
