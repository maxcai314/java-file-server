package ax.xz.max.fileserver.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
@Repository
@Transactional
public class FileDataServiceImpl implements FileDataService {
    private final FileDataRepository fileDataRepository;
    private final Path filePath;

    public FileDataServiceImpl(@Autowired FileDataRepository fileDataRepository, @Value("${ax.xz.max.fileserver.file-path}") Path filePath) {
        this.fileDataRepository = fileDataRepository;
        this.filePath = filePath;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean fileExists(Path path) {
        return fileDataRepository.existsByPath(path.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPasswordProtected(Path path) {
        return getPublicFile(path).map(FileDataEntity::isPasswordProtected).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPublic(Path path) {
        return getPublicFile(path).isPresent();
    }

    @Override
    public boolean isValidPath(Path path) {
        Path absolutePath = filePath.resolve(path).normalize();
        return !Files.isDirectory(absolutePath) &&
                absolutePath.startsWith(filePath.normalize()); // prevent upwards path traversal
    }

    private Optional<FileDataEntity> getPublicFile(Path path) {
        return fileDataRepository.findByPathAndVisibility(path.toString(), FileVisibility.PUBLIC);
    }

    private Optional<FileDataEntity> getFile(Path path, String password) {
        return fileDataRepository.findByPathAndPassword(path.toString(), password);
    }

    // todo: un-spaghetti, just use String instead of path; OS-spesific path is used only for read/write to memory
    @Override
    @Transactional
    public boolean addFile(Path path, FileVisibility visibility, String password, Resource resource) throws IOException {
        if (fileExists(path))
            return false;
        if (visibility == FileVisibility.PRIVATE && (password == null || password.isEmpty()))
            return false;

        Path absolutePath = filePath.resolve(path);
        Files.createDirectories(absolutePath.getParent());
        Files.copy(resource.getInputStream(), absolutePath);

        FileDataEntity fileDataEntity = new FileDataEntity();
        fileDataEntity.setPath(path.toString());
        fileDataEntity.setVisibility(visibility);
        fileDataEntity.setPassword(password);
        fileDataEntity.setUploadDate(Instant.now());

        fileDataRepository.save(fileDataEntity);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteUnprotectedFile(Path path) throws IOException {
        Optional<FileDataEntity> fileDataEntity = getPublicFile(path);
        if (fileDataEntity.isEmpty())
            return false;
        if (fileDataEntity.get().isPasswordProtected())
            return false;
        fileDataRepository.delete(fileDataEntity.get());
        Files.deleteIfExists(filePath.resolve(path));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteFile(Path path, String password) throws IOException {
        Optional<FileDataEntity> fileDataEntity = getFile(path, password);
        if (fileDataEntity.isEmpty())
            return false;
        fileDataRepository.delete(fileDataEntity.get());
        Files.deleteIfExists(filePath.resolve(path));
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getPublicFileAsResource(Path path) throws IOException {
        return new InputStreamResource(Files.newInputStream(
                getPublicFile(path)
                        .map(FileDataEntity::getPath)
                        .map(Path::of)
                        .map(this.filePath::resolve)
                        .orElseThrow()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Resource getFileAsResource(Path path, String password) throws IOException {
        return new InputStreamResource(Files.newInputStream(
                getFile(path, password)
                        .map(FileDataEntity::getPath)
                        .map(Path::of)
                        .map(filePath::resolve)
                        .orElseThrow()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Path path, String password) {
        return !isPasswordProtected(path) || getFile(path, password).isPresent();
    }

    @Override
    public String getMimeType(Path path) throws IOException {
        String mimeType = Files.probeContentType(filePath.resolve(path));
	    return Objects.requireNonNullElse(mimeType, "application/octet-stream");
    }
}