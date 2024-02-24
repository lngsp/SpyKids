package ro.spykids.server.repository;

import ro.spykids.server.model.Location;
import ro.spykids.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface LocationRepository  extends JpaRepository<Location, Integer> {
    List<Location> findAll();

    Optional<Location> findTopByUserLOrderByCreatedAtDesc(User user);

    Location findTopByUserLEmailOrderByDepartureTimeDesc(String userEmail);

    List<Location> findAllByUserLId(Integer userId);

    @Query(value = "SELECT * FROM location WHERE user_id = :userId AND DATE(arrival_time) = :date", nativeQuery = true)
    List<Location> findByUserIdAndArrivalDate(@Param("userId") Integer userId, @Param("date") Date date);

    @Query(value = "SELECT * FROM location WHERE user_id = :userId AND DATE(arrival_time) BETWEEN :startDate AND CURRENT_TIMESTAMP", nativeQuery = true)
    List<Location> findByUserIdAndArrivalDateBetween(@Param("userId") Integer userId, @Param("startDate") Date startDate);

    @Modifying
    @Query("DELETE FROM Location l WHERE l.userL.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
