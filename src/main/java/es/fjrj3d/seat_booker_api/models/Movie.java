package es.fjrj3d.seat_booker_api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
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
    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    @Column
    @Size(max = 1000)
    private String synopsis;

    @Enumerated(EnumType.STRING)
    @Column
    private EMovieGenre genre;

    @Enumerated(EnumType.STRING)
    @Column
    private EMovieAgeRating ageRating;

    @Enumerated(EnumType.STRING)
    @Column
    private EMovieUserRating userRating;

    @Column
    private String coverImageUrl;

    @Column
    private Duration duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column
    @Future
    private LocalDate premiere;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("movie-room")
    private Set<Room> rooms;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("movie-review")
    private Set<Review> reviews;
}
