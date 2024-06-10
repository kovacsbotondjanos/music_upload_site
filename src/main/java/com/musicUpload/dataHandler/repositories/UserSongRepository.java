package com.musicUpload.dataHandler.repositories;

import com.musicUpload.dataHandler.models.implementations.UserSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface UserSongRepository extends JpaRepository<UserSong, Long> {
    Optional<UserSong> findBySongIdAndUserIdAndYearAndMonth(Long userId, Long songId, int year, int month);
    @Query("SELECT us " +
           "FROM UserSong us WHERE " +
           "(us.year = :year AND us.month = :month) " +
           "OR " +
           "(us.year = :yearOfLastMonth AND us.month = :lastMonth)")
    Set<UserSong> findByLastTwoMonths(@Param("year") int year,
                                     @Param("month") int month,
                                     @Param("yearOfLastMonth") int yearOfLastMonth,
                                     @Param("lastMonth") int lastMonth);
}
