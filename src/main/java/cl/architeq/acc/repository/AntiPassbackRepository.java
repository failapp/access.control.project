package cl.architeq.acc.repository;

import cl.architeq.acc.model.AntiPassback;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AntiPassbackRepository extends CrudRepository<AntiPassback, Integer>, PagingAndSortingRepository<AntiPassback, Integer> {

    Optional<AntiPassback> findByUserId(String userId);

    Optional<AntiPassback> findFirstByUserId(String userId);

    Optional<List<AntiPassback>> findByDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    //Optional<List<AntiPassback>> findBySyncDateTimeBetween(int sync, LocalDateTime from, LocalDateTime to);

    Optional<List<AntiPassback>> findBySync(int sync);

}
