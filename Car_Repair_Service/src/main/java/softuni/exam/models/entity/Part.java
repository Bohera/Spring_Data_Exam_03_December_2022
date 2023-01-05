package softuni.exam.models.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parts")
public class Part extends BaseEntity{

    @Column(name = "part_name", unique = true, nullable = false)
    private String partName;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    @OneToMany(targetEntity = Task.class, mappedBy = "part")
    private List<Task> tasks;

}
