package server.repository;

import org.springframework.data.repository.CrudRepository;
import server.domain.User;

/**
 * Created by Mocart on 05-Sep-17.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUserName (String userName);
}
