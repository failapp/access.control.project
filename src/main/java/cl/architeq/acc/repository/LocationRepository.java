package cl.architeq.acc.repository;

import cl.architeq.acc.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

    Optional<Location> findByName(String name);

    Optional<Location> findByCod(String cod);

}
