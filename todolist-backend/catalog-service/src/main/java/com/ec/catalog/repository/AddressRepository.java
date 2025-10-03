package com.ec.catalog.repository;


import com.ec.catalog.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, String>  {
	
	List<Address> findByProfileIdAndIsDeletedOrderByIsDefaultDesc(String profileId, Boolean isDeleted);
	
	@Modifying
	@Query("UPDATE Address a SET a.isDefault = false WHERE a.profile.id = :profileId")
	void resetDefaultAddressByProfileId(@Param("profileId") String profileId);
	
}
