package ro.spykids.server.repository;

import ro.spykids.server.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Integer> {
    List<Area> findAll();
    Area findByName(String name);

    List<Area> findByDefinedBy(String definedby);

    Area findByNameAndDefinedBy(String name, String definedBy);


    @Query(value = "SELECT * \n" +
            "FROM area a \n" +
            "INNER JOIN child_area ca ON a.id = ca.area_id \n" +
            "WHERE ca.child_id = :childId\n", nativeQuery = true)
    List<Area> findAllAreaByChildId(@Param("childId") Integer childId);

    @Query(value = "SELECT * \n" +
            "FROM area a \n" +
            "INNER JOIN child_area ca ON a.id = ca.area_id \n" +
            "WHERE ca.child_id = :childId AND a.area_type = 0\n", nativeQuery = true)
    List<Area> findAllPolygonAreaByChildId(@Param("childId") Integer childId);    //triangle or square or hexagone


    @Query(value = "SELECT * \n" +
            "FROM area a \n" +
            "INNER JOIN child_area ca ON a.id = ca.area_id \n" +
            "WHERE ca.child_id = :childId AND a.area_type = 1\n", nativeQuery = true)
    List<Area> findAllCircleAreaByChildId(@Param("childId") Integer childId);

    @Modifying
    @Query("UPDATE Area a SET a.safe = :safe WHERE a.name = :name AND a.definedBy = :definedBy")
    void updateSafe(@Param("definedBy") String definedBy, @Param("name") String name, @Param("safe") Boolean safe);

    @Modifying
    @Query("UPDATE Area a SET a.enable = :enable WHERE a.name = :name AND a.definedBy = :definedBy")
    void updateEnable(@Param("definedBy") String definedBy, @Param("name") String name, @Param("enable") Boolean enable);

}
