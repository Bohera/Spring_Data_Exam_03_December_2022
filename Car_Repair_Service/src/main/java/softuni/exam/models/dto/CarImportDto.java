package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.entity.CarType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
public class CarImportDto {

    @XmlElement
    @NotNull
    @Enumerated(EnumType.STRING)
    private CarType carType;

    @XmlElement
    @Size(min = 2, max = 30)
    @NotNull
    private String carMake;

    @XmlElement
    @Size(min = 2, max = 30)
    @NotNull
    private String carModel;

    @XmlElement
    @Positive
    @NotNull
    private int year;

    @XmlElement
    @Size(min = 2, max = 30)
    @NotNull
    private String plateNumber;

    @XmlElement
    @Positive
    @NotNull
    private int kilometers;

    @XmlElement
    @DecimalMin(value = "1.00")
    @NotNull
    private double engine;

}
