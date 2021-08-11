package com.rtc.groupcall.db.repository;

import com.rtc.groupcall.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Modifying
    @Query("UPDATE UserEntity u SET u.roomId = :roomId WHERE u.userId = :userId")
    void moveUser(Long userId, Long roomId);
}
