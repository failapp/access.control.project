package cl.architeq.acc.repository;


import cl.architeq.acc.model.EventData;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventDataRepository extends CrudRepository<EventData, Integer>, PagingAndSortingRepository<EventData, Integer> {

    Optional<List<EventData>> findByUserId(String userId, Pageable pageable);

    Optional<List<EventData>> findByDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<EventData> findBySyncAndEventCodeBetween(int sync, int fromCod, int toCod, Pageable pageable);

}
