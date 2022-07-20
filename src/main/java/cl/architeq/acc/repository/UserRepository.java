package cl.architeq.acc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cl.architeq.acc.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

    @Override
    Optional<User> findById(Integer id);

    Optional<User> findByDni(String dni);

    Optional<User> findFirstByDniContainingIgnoreCase(String dni);

    Optional<List<User>> findAllByEnabled(boolean bln, Pageable pageable);

    @Query(value = "SELECT count(*) FROM users WHERE enabled =?1", nativeQuery = true)
    int countEnabled(boolean bln);


}
