package akross.eclipsehotel.repository;

import akross.eclipsehotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByNumber(String number);
}
