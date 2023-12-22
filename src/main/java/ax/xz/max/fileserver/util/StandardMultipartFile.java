package ax.xz.max.fileserver.util;

import com.vaadin.flow.component.upload.receivers.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class StandardMultipartFile implements MultipartFile {

	private final FileData fileData;

	public StandardMultipartFile(FileData fileData) {
		this.fileData = fileData;
	}

	@Override
	public String getName() {
		return fileData.getFileName();
	}

	@Override
	public String getOriginalFilename() {
		return fileData.getFileName();
	}

	@Override
	public String getContentType() {
		// Provide an appropriate content type based on your file data
		return "application/octet-stream";
	}

	@Override
	public boolean isEmpty() {
		return fileData.getFile().length() == 0;
	}

	@Override
	public long getSize() {
		return fileData.getFile().length();
	}

	@Override
	public byte[] getBytes() {
		return ((ByteArrayOutputStream) fileData.getOutputBuffer()).toByteArray();
	}

	@Override
	public InputStream getInputStream() {
		OutputStream dataFile = fileData.getOutputBuffer();
		return new ByteArrayInputStream(((ByteArrayOutputStream) dataFile).toByteArray());
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		Files.copy(getInputStream(), dest.toPath());
	}

	@Override
	public void transferTo(Path dest) throws IOException, IllegalStateException {
		Files.copy(getInputStream(), dest);
	}
}
