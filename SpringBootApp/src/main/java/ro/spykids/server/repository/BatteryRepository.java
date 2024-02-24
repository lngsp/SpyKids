package ro.spykids.server.repository;

import ro.spykids.server.model.Battery;
import ro.spykids.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatteryRepository  extends JpaRepository<Battery, Integer> {
    List<Battery> findAllByUserBId(Integer id);

    Battery findTopByUserBOrderByIdDesc(User user);
}
