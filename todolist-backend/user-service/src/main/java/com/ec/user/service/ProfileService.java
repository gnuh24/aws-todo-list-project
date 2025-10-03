package com.ec.user.service;


import com.ec.user.dto.profile.ProfileCreateForm;
import com.ec.user.dto.profile.ProfileUpdateForm;
import com.ec.user.entity.Account;
import com.ec.user.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
	Profile getProfileById(String profileId);
	
	//    Profile getProfileByPhone(String phone);
//    Page<Profile> getAllProfile(Pageable pageable, String search, ProfileFilterForm form);
	Profile createProfile(ProfileCreateForm form);

	Profile updateProfile(Profile profile,  ProfileUpdateForm form);
	
	Profile updateEmail(Profile profile, String newEmail);

}
