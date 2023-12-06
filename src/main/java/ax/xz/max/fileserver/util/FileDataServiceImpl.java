package ax.xz.max.fileserver.util;

import jakarta.transaction.Transactional;
import java.time.Instant;

@Repository
@Transactional
public class FileDataServiceImpl implements FileDataService {
    @Autowired
    private FileDataRepository fileDataRepository;

    @Override
    public boolean fileExists(Path path) {
        return fileDataRepository.existsByPath(path.toString());
    }

    @Override
    public boolean isPublic(Path path) {
        return fileDataRepository.findByPathAndVisibility(path, FileVisibility.PUBLIC).isPresent();
    }

    @Override
    public Optional<Path> getPublicFile(Path path) {
        return fileDataRepository.findByPathAndVisibility(path, FileVisibility.PUBLIC).map(FileDataEntity::getPath);
    }

    @Override
    public Optional<Path> getFile(Path path, String password) {
        // if public file exists, return it
        Optional<Path> publicFile = getPublicFile(path);
        if (publicFile.isPresent())
            return publicFile;
        return fileDataRepository.findByPathAndPassword(path, password).map(FileDataEntity::getPath);
    }

    @Override
    @Transactional
    public boolean addPublicFile(Path path) {
        if (fileExists(path))
            return false;
        FileDataEntity fileDataEntity = new FileDataEntity();
        fileDataEntity.setPath(path);
        fileDataEntity.setVisibility(FileVisibility.PUBLIC);
        fileDataEntity.setUploadDate(Instant.now());

        fileDataRepository.save(fileDataEntity);
        return true;
    }

    @Override
    @Transactional
    public boolean addFile(Path path, FileVisibility visibility, String password) {
        if (fileExists(path))
            return false;
        if (visibility == FileVisibility.PRIVATE && (password == null || password.isEmpty()))
            return false;
        FileDataEntity fileDataEntity = new FileDataEntity();
        fileDataEntity.setPath(path);
        fileDataEntity.setVisibility(visibility);
        fileDataEntity.setPassword(password);
        fileDataEntity.setUploadDate(Instant.now());

        fileDataRepository.save(fileDataEntity);
        return true;
    }

    @Override
    @Transactional
    public boolean deletePublicFile(Path path) {
        return fileDataRepository.deleteByPath(path.toString());
    }

    @Override
    @Transactional
    public boolean deleteFile(Path path, String password) {
        Optional<FileDataEntity> fileDataEntity = fileDataRepository.findByPathAndPassword(path, password);
        if (fileDataEntity.isEmpty())
            return false;
        fileDataRepository.delete(fileDataEntity.get());
        return true;
    }
}