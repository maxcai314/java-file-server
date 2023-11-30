package ax.xz.max.fileserver.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public interface FileDataRepository extends JpaRepository<FileDataEntity, Long> {
	FileDataRecord findByPath(Path path);
	List<FileDataRecord> findAllByVisibility(FileVisibility visibility);

	FileDataRecord findByPathAndVisibility(Path path, FileVisibility visibility);

	@Query("SELECT f FROM FileDataEntity f WHERE f.path = :path AND f.visibility = ax.xz.max.fileserver.util.FileVisibility.PUBLIC")
	FileDataRecord findPublicFileByPath(@Param("path") Path path);

	@Query("SELECT f FROM FileDataEntity f WHERE f.path = :path AND f.visibility = ax.xz.max.fileserver.util.FileVisibility.PRIVATE")
	FileDataRecord findPrivateFileByPath(@Param("path") Path path);

	List<FileDataRecord> findAllByUploadDateAfter(Instant uploadDate);
	List<FileDataRecord> findAllByUploadDateBefore(Instant uploadDate); // add more methods as needed

	FileDataRecord findById(long id);
}
