package cl.architeq.acc.repository;

import cl.architeq.acc.model.SyncUserDevice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncUserDeviceRepository extends CrudRepository<SyncUserDevice, Integer> {

    Optional<SyncUserDevice> findByUserDniAndDeviceCod(String userDni, Integer deviceCod);

    //Optional<List<SyncUserDevice>> findByAck(int ack);

    Optional<List<SyncUserDevice>> findByAckAndDeviceCod(int ack, int deviceCod);

}
