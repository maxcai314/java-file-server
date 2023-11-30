package ax.xz.max.fileserver.util;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Set;


public interface FileDataRepository extends JpaRepository<FileDataEntity, Long> {

	@Modifying
	@Transactional
	int deleteByPath(String path);

	FileDataEntity findByPath(String path);

	Set<FileDataEntity> findAllByVisibility(FileVisibility visibility);

	FileDataEntity findByPathAndVisibility(String path, FileVisibility visibility);

	Set<FileDataEntity> findAllByUploadDateAfter(Instant uploadDate);
	Set<FileDataEntity> findAllByUploadDateBefore(Instant uploadDate); // add more methods as needed

	FileDataEntity findById(long id);
}
