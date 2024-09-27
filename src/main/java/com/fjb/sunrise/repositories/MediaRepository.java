package com.fjb.sunrise.repositories;

import com.fjb.sunrise.models.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Media findByFileCode(String fileCode);
}
