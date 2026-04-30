package ba.unsa.si.docflow.service.storage;

import ba.unsa.si.docflow.config.StorageProperties;
import ba.unsa.si.docflow.exception.StorageException;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements StorageService {
    private final StorageProperties storageProperties;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(storageProperties.getRootDir()).toAbsolutePath().normalize();

            Files.createDirectories(rootLocation);
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not initialize document storage directory.", exception);
        }
    }

    @Override
    public StoredFileInfo store(MultipartFile file, Long companyId) {
        String originalFileName =
                StringUtils.cleanPath(
                        Objects.requireNonNull(
                                file.getOriginalFilename(), "File name is required."));

        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (extension == null) throw new StorageException("Document file extension is missing.");

        extension = extension.toLowerCase();
        String storedFileName = UUID.randomUUID() + "." + extension;

        LocalDate now = LocalDate.now();
        Path targetDirectory =
                rootLocation
                        .resolve("company-" + companyId)
                        .resolve(String.valueOf(now.getYear()))
                        .resolve(String.format("%02d", now.getMonthValue()))
                        .normalize();

        if (!targetDirectory.startsWith(rootLocation))
            throw new StorageException("Invalid storage path.");

        try {
            Files.createDirectories(targetDirectory);
            Path targetFile = targetDirectory.resolve(storedFileName).normalize();

            if (!targetDirectory.startsWith(rootLocation))
                throw new StorageException("Invalid storage path.");

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath =
                    rootLocation.relativize(targetFile).toString().replace(File.separatorChar, '/');

            return new StoredFileInfo(
                    originalFileName,
                    storedFileName,
                    relativePath,
                    file.getSize(),
                    file.getContentType(),
                    extension);
        } catch (IOException exception) {
            throw new StorageException("Could not store document file.", exception);
        }
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();

            if (!filePath.startsWith(rootLocation))
                throw new StorageException("Invalid storage path");

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable())
                throw new StorageException("Document file could not be found.");

            return resource;
        } catch (MalformedURLException exception) {
            throw new StorageException("Document file could not be loaded.", exception);
        }
    }

    @Override
    public void delete(String relativePath) {
        if (!StringUtils.hasText(relativePath)) return;

        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();

            if (!filePath.startsWith(rootLocation))
                throw new StorageException("Invalid storage path.");

            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new StorageException("Document file could not be deleted.", exception);
        }
    }
}
