package es.fjrj3d.seat_booker_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 1, max = 100)
    private String roomName;

    @Enumerated(EnumType.STRING)
    //@NotNull(message = "Room Type cannot be null")
    @Column
    private ERoomType roomType=ERoomType.STANDARD;

    @Column
    @Min(5)
    @Max(10)
    //@NotNull(message = "Row Quantity cannot be null")
    private Integer rowQuantity=5;

    @Column
    @Min(8)
    @Max(12)
    //@NotNull(message = "Seat Quantity cannot be null")
    private Integer seatQuantity=8;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @JsonBackReference("movie-room")
    private Movie movie;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("room-screening")
    private Set<Screening> screenings;
}
