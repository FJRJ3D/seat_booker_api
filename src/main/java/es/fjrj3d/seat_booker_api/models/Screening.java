package es.fjrj3d.seat_booker_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Column
    private LocalTime schedule;

    @Column
    private Duration duration;

    @Column
    private boolean availability = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonBackReference("room-screening")
    private Room room;

    @OneToMany(mappedBy = "screening", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("screening-seat")
    private List<Seat> seats;

    public Integer getRowQuantity() {
        return room != null ? room.getRowQuantity() : null;
    }

    public Integer getSeatQuantity() {
        return room != null ? room.getSeatQuantity() : null;
    }
}
