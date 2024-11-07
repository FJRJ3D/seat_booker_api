package es.fjrj3d.seat_booker_api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 100)
    private String title;

    @Column
    @NotNull(message = "Synopsis cannot be null")
    @Size(min = 1, max = 1000)
    private String synopsis;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Genre cannot be null")
    @Column
    private EMovieGenre genre;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Age Rating cannot be null")
    @Column
    private EMovieAgeRating ageRating;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "User Rating cannot be null")
    @Column
    private EMovieUserRating userRating;

    @Column
    @NotNull(message = "Cover Image URL cannot be null")
    private String coverImageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Column
    @NotNull(message = "Duration cannot be null")
    private LocalTime duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @NotNull(message = "Premiere cannot be null")
    @Column
    private LocalDate premiere;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("movie-room")
    private Set<Room> rooms;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("movie-review")
    private Set<Review> reviews;
}
