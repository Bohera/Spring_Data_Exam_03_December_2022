package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarImportDto;
import softuni.exam.models.dto.CarImportWrapperDto;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static softuni.exam.util.Paths.CARS_XML_PATH;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;

    @Autowired
    public CarServiceImpl(
            CarRepository carRepository,
            Validator validator,
            ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString(CARS_XML_PATH);
    }

    @Override
    public String importCars() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(CarImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        CarImportWrapperDto carDtos =
                (CarImportWrapperDto) unmarshaller.unmarshal(CARS_XML_PATH.toFile());

        List<CarImportDto> cars = carDtos.getCars();

        List<String> results = new ArrayList<>();

        for (CarImportDto carImportDto : cars) {
            Set<ConstraintViolation<CarImportDto>> errors =
                    this.validator.validate(carImportDto);

            if (errors.isEmpty()) {
                Optional<Car> optionalCar =
                        this.carRepository.findByPlateNumber(carImportDto.getPlateNumber());

                if (optionalCar.isEmpty()) {
                    Car car = this.modelMapper.map(carImportDto, Car.class);



                    this.carRepository.save(car);

                    results.add(String.format("Successfully imported car %s - %s",
                            car.getCarMake(),
                            car.getCarModel()));
                } else {
                    results.add("Invalid car");
                }
            } else {
                results.add("Invalid car");
            }
        }
        return String.join(System.lineSeparator(), results);
    }
}
