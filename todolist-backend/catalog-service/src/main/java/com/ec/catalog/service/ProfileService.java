package com.ec.catalog.service;


import com.ec.catalog.dto.profile.ProfileCreateForm;
import com.ec.catalog.dto.profile.ProfileUpdateForm;
import com.ec.catalog.entity.Profile;

public interface ProfileService {
	Profile getProfileById(String profileId);
	
	//    Profile getProfileByPhone(String phone);
//    Page<Profile> getAllProfile(Pageable pageable, String search, ProfileFilterForm form);
	Profile createProfile(ProfileCreateForm form);

	Profile updateProfile(Profile profile,  ProfileUpdateForm form);
	
	Profile updateEmail(Profile profile, String newEmail);

}
