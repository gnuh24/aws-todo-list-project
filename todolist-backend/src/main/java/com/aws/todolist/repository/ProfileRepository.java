package com.aws.todolist.repository;

import com.aws.todolist.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, String> {



}
