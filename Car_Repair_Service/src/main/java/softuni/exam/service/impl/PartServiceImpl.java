package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PartImportDto;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.PartRepository;
import softuni.exam.service.PartService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.PARTS_JSON_PATH;

@Service
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    @Autowired
    public PartServiceImpl(
            PartRepository partRepository,
            ModelMapper modelMapper,
            Gson gson,
            Validator validator) {
        this.partRepository = partRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }

    @Override
    public boolean areImported() {
        return this.partRepository.count() > 0;
    }

    @Override
    public String readPartsFileContent() throws IOException {
        return Files.readString(PARTS_JSON_PATH);
    }

    @Override
    public String importParts() throws IOException {
        String jsonParts = readPartsFileContent();

        PartImportDto[] partImportDtos =
                this.gson.fromJson(jsonParts, PartImportDto[].class);

        List<String> results = new ArrayList<>();

        for (PartImportDto partImportDto : partImportDtos) {
            Set<ConstraintViolation<PartImportDto>> errors =
                    this.validator.validate(partImportDto);

            if (errors.isEmpty()) {
                Optional<Part> optionalPart =
                        this.partRepository.findByPartName(partImportDto.getPartName());

                if (optionalPart.isEmpty()) {
                    Part part = this.modelMapper.map(partImportDto, Part.class);

                    this.partRepository.save(part);

                    results.add(String.format("Successfully imported part %s - %.2f",
                            part.getPartName(),
                            part.getPrice()));
                } else {
                    results.add("Invalid part");
                }
            } else {
                results.add("Invalid part");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
