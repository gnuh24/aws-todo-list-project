package com.ec.user.service;

import com.ec.user.dto.address.AddressCreateForm;
import com.ec.user.dto.address.AddressUpdateForm;
import com.ec.user.entity.Address;
import com.ec.user.entity.Profile;

import java.util.List;

public interface AddressService {
	
	Address getAddressById(String addressId);
	List<Address> getAddressByProfileId(String profileId);
	Address createAddress(Profile profile, AddressCreateForm form);
	Address updateAddress(String addressId, AddressUpdateForm form);
	Address setDefault(String addressId, String profileId);
	Address deleteAddress(String addressId);
}
