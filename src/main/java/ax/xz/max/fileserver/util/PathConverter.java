package ax.xz.max.fileserver.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.file.Path;

@Converter
public class PathConverter implements AttributeConverter<Path, String> {
	@Override
	public String convertToDatabaseColumn(Path path) {
		return path == null ? null : path.toString();
	}

	@Override
	public Path convertToEntityAttribute(String string) {
		return string == null ? null : Path.of(string);
	}
}
