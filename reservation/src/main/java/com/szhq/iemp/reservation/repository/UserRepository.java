package com.szhq.iemp.reservation.repository;

import com.szhq.iemp.reservation.api.model.Tuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Tuser, String>, JpaSpecificationExecutor<Tuser> {

    @Query(value = "select * from user where id_number=?1", nativeQuery = true)
    public Tuser findByIdNumber(String idNumber);

    @Query(value = "select * from user where phone=?1 and tenant_id = '0' ", nativeQuery = true)
    public Tuser findByPhone(String phone);

    @Query(value = "select * from user where name=?1", nativeQuery = true)
    public List<Tuser> findByOwnerName(String name);

    @Query(value = "select * from user where id in (?1)", nativeQuery = true)
    public List<Tuser> findByIdIn(List<String> userIds);

    @Modifying
    @Query(value = "delete from user where id = ?1", nativeQuery = true)
    public Integer deleteUserById(String userId);

    @Modifying
    @Query(value = "insert into user_role_r(id,user_id,role_id) values(?1,?2,?3)", nativeQuery = true)
    public void insertIntoUserRoleR(String id, String userId, String roleId);

    @Modifying
    @Query(value = "delete from user_role_r where user_id = ?1", nativeQuery = true)
    public Integer deleteUserRoleR(String userId);
}
