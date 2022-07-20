package cl.architeq.acc.repository;

import cl.architeq.acc.model.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Integer> {

    Optional<Company> findByName(String name);

    Optional<Company> findByCod(String cod);

}
