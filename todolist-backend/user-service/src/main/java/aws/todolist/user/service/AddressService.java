package aws.todolist.user.service;

import aws.todolist.user.dto.address.AddressCreateForm;
import aws.todolist.user.dto.address.AddressUpdateForm;
import aws.todolist.user.entity.Address;
import aws.todolist.user.entity.Profile;

import java.util.List;

public interface AddressService {
	
	Address getAddressById(String addressId);
	List<Address> getAddressByProfileId(String profileId);
	Address createAddress(Profile profile, AddressCreateForm form);
	Address updateAddress(String addressId, AddressUpdateForm form);
	Address setDefault(String addressId, String profileId);
	Address deleteAddress(String addressId);
}
