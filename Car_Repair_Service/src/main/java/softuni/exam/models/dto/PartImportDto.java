package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartImportDto {

    @Size(min = 2, max = 19)
    @NotNull
    private String partName;

    @Min(10)
    @Max(2000)
    @NotNull
    private double price;

    @Positive
    @NotNull
    private int quantity;

}
