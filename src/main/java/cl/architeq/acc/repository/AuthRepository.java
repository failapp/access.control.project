package cl.architeq.acc.repository;

import cl.architeq.acc.model.Auth;
import cl.architeq.acc.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthRepository extends CrudRepository<Auth, Integer> {

    Optional<Auth> findByUsername(String username);

    Optional<Auth> findByEmail(String email);

    Optional<List<Auth>> findByRoles(Role role);


}
