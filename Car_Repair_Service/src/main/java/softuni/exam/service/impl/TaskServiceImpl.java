package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TaskImportDto;
import softuni.exam.models.dto.TaskImportWrapperDto;
import softuni.exam.models.entity.*;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.repository.PartRepository;
import softuni.exam.repository.TaskRepository;
import softuni.exam.service.TaskService;

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
import java.util.stream.Collectors;

import static softuni.exam.util.Paths.TASKS_XML_PATH;

@Service
public class TaskServiceImpl implements TaskService {

    private final CarRepository carRepository;

    private final MechanicRepository mechanicRepository;

    private final PartRepository partRepository;

    private final TaskRepository taskRepository;

    private final Validator validator;

    private final ModelMapper modelMapper;


    @Autowired
    public TaskServiceImpl(
            CarRepository carRepository,
            MechanicRepository mechanicRepository,
            PartRepository partRepository,
            TaskRepository taskRepository, Validator validator, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.mechanicRepository = mechanicRepository;
        this.partRepository = partRepository;
        this.taskRepository = taskRepository;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.taskRepository.count() > 0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString(TASKS_XML_PATH);
    }

    @Override
    public String importTasks() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(TaskImportWrapperDto.class);

        Unmarshaller unmarshaller = context.createUnmarshaller();

        TaskImportWrapperDto taskDtos =
                (TaskImportWrapperDto) unmarshaller.unmarshal(TASKS_XML_PATH.toFile());

        List<TaskImportDto> tasks = taskDtos.getTasks();

        List<String> results = new ArrayList<>();

        for (TaskImportDto taskImportDto : tasks) {
            Set<ConstraintViolation<TaskImportDto>> errors =
                    this.validator.validate(taskImportDto);

            if (errors.isEmpty()) {
                Optional<Mechanic> optionalMechanic =
                        this.mechanicRepository.findByFirstName(taskImportDto.getMechanic().getFirstName());

                Optional<Car> optionalCar =
                        this.carRepository.findById(taskImportDto.getCar().getId());

                if (optionalMechanic.isPresent() && optionalCar.isPresent()) {
                    Task task = this.modelMapper.map(taskImportDto, Task.class);

                    Optional<Part> part =
                            this.partRepository.findById(taskImportDto.getPart().getId());

                    task.setCar(optionalCar.get());
                    task.setMechanic(optionalMechanic.get());
                    task.setPart(part.get());

                    this.taskRepository.save(task);

                    results.add(String.format("Successfully imported task %.2f",
                            task.getPrice()));

                } else {
                    results.add("Invalid task");
                }
            } else {
                results.add("Invalid task");
            }

        }
        return String.join(System.lineSeparator(), results);
    }

    @Override
    public String getCoupeCarTasksOrderByPrice() {

        String carType = CarType.coupe.toString();

        List<Task> highestPricedTasks =
                this.taskRepository.findAllByCar_CarTypeOrderByPriceDesc(carType);

        return highestPricedTasks
                .stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }
}
