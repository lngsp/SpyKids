package ro.spykids.server.repository;

import ro.spykids.server.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository  extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    @Override
    default <S extends User> List<S> findAll(Example<S> example) {
        return null;
    }

    @Query("SELECT u.children FROM User u WHERE u.email = :email")
    List<User> findAllChildrenByEmail(String email);

}
