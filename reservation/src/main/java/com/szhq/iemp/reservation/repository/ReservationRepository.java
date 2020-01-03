package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Treservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Treservation, Integer>, JpaSpecificationExecutor<Treservation> {

    @Query(value = "select count(*) from t_reservation r where r.install_site_id=?1 and r.reservation_time > ?2 and r.reservation_time < ?3", nativeQuery = true)
    Integer countByExample(Integer installSiteId, long start, long end);

    Treservation findByReservationNumberEquals(String number);

    @Modifying
    @Query(value = "delete from t_reservation where reservation_time <= ?1", nativeQuery = true)
    Integer deleteByReservationTimeLessThan(Long threeDayAgo);

    Treservation findByVin(String vin);

    Treservation findByPhone(String phone);

    List<Treservation> findByMotorNumber(String motorNumber);

    @Modifying
    @Query(value = "delete from t_reservation where reservation_number = ?1", nativeQuery = true)
    int deleteByReserNo(String reservationNo);


}
