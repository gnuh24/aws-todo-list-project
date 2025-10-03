package com.ec.catalog.repository;

import com.ec.catalog.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {



}
