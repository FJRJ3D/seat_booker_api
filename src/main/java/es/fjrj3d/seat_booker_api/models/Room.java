package es.fjrj3d.seat_booker_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String roomName;

    @Column
    private ERoomType roomType;

    @Column
    private Integer rowSeat;

    @Column
    private Integer numberSeat;
}
