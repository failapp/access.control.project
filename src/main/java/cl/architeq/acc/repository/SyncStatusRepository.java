package cl.architeq.acc.repository;

import cl.architeq.acc.model.SyncStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SyncStatusRepository extends CrudRepository<SyncStatus, Integer>, PagingAndSortingRepository<SyncStatus, Integer> {

    Optional<SyncStatus> findByUserId(String userId);

    Page<SyncStatus> findBySyncAndDateSyncBetween(int sync, LocalDateTime from, LocalDateTime to, Pageable pageable);

}
