package aws.todolist.user.service;


import aws.todolist.user.dto.profile.ProfileCreateForm;
import aws.todolist.user.dto.profile.ProfileUpdateForm;
import aws.todolist.user.entity.Profile;

public interface ProfileService {
	Profile getProfileById(String profileId);
	
	//    Profile getProfileByPhone(String phone);
//    Page<Profile> getAllProfile(Pageable pageable, String search, ProfileFilterForm form);
	Profile createProfile(ProfileCreateForm form);

	Profile updateProfile(Profile profile,  ProfileUpdateForm form);
	
	Profile updateEmail(Profile profile, String newEmail);

}
