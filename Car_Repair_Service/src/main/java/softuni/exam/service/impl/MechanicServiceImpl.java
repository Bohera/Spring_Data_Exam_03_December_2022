package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.MechanicImportDto;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.service.MechanicService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.MECHANICS_JSON_PATH;

@Service
public class MechanicServiceImpl implements MechanicService {

    private final MechanicRepository mechanicRepository;

    private final ModelMapper modelMapper;

    private final Gson gson;

    private final Validator validator;

    @Autowired
    public MechanicServiceImpl(
            MechanicRepository mechanicRepository,
            ModelMapper modelMapper,
            Gson gson,
            Validator validator) {
        this.mechanicRepository = mechanicRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validator = validator;
    }

    @Override
    public boolean areImported() {
        return this.mechanicRepository.count() > 0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        return Files.readString(MECHANICS_JSON_PATH);
    }

    @Override
    public String importMechanics() throws IOException {
        String jsonMechanic = readMechanicsFromFile();

        MechanicImportDto[] mechanicImportDtos =
                this.gson.fromJson(jsonMechanic, MechanicImportDto[].class);

        List<String> results = new ArrayList<>();

        for (MechanicImportDto mechanicImportDto : mechanicImportDtos) {
            Set<ConstraintViolation<MechanicImportDto>> errors =
                    this.validator.validate(mechanicImportDto);

            if (errors.isEmpty()) {
                Optional<Mechanic> optionalMechanic =
                        this.mechanicRepository.findByEmail(mechanicImportDto.getEmail());

                if (optionalMechanic.isEmpty()) {
                    Mechanic mechanic =
                            this.modelMapper.map(mechanicImportDto, Mechanic.class);

                    this.mechanicRepository.save(mechanic);

                    results.add(String.format("Successfully imported mechanic %s %s",
                            mechanic.getFirstName(),
                            mechanic.getLastName()));

                } else {
                    results.add("Invalid mechanic");
                }
            } else {
                results.add("Invalid mechanic");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
