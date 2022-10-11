package nus.climods.storage.module;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import nus.climods.commons.core.LogsCenter;
import nus.climods.commons.exceptions.DataConversionException;
import nus.climods.commons.exceptions.IllegalValueException;
import nus.climods.commons.util.JsonUtil;
import nus.climods.model.module.Module;
import nus.climods.model.module.ReadOnlyModuleList;

public class JsonModuleListStorage implements ModuleListStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonModuleListStorage.class);

    private final Path filePath;

    public JsonModuleListStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getModuleListFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyModuleList> readModuleList() throws DataConversionException {
        return readModuleList(filePath);
    }

    @Override
    public Optional<ReadOnlyModuleList> readModuleList(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableModuleList> jsonModuleList = JsonUtil.readJsonFile(
            filePath, JsonSerializableModuleList.class);

        if (jsonModuleList.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonModuleList.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveModuleList(List<Module> modules) throws IOException {

    }

    @Override
    public void saveModuleList(List<Module> modules, Path filePath) throws IOException {

    }
}
