package org.example;
import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;
}
