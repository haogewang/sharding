package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.TuserPush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPushRepository extends JpaRepository<TuserPush, String>, JpaSpecificationExecutor<TuserPush> {

    @Modifying
    @Query(value = "delete from user_push where id = ?1", nativeQuery = true)
    public Integer deleteByUId(String id);

    @Modifying
    @Query(value = "delete from user_push where user_id = ?1", nativeQuery = true)
    public Integer deleteByUserId(String userId);
}
