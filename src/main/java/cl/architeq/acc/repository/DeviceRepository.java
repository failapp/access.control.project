package cl.architeq.acc.repository;

import cl.architeq.acc.model.Device;
import cl.architeq.acc.model.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends CrudRepository<Device, Integer>, PagingAndSortingRepository<Device, Integer> {

    Optional<Device> findByCod(int cod);

    Optional<Device> findByIpAddr(String ipAddr);

    Optional<Device> findByMacAddr(String mac);

    Optional<List<Device>> findByLocation(Location loc, Pageable page);

    Optional<List<Device>> findByCodNotAndEnabled(int cod, boolean enabled);


}
