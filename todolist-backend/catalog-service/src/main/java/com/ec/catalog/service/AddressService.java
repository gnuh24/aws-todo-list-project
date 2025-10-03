package com.ec.catalog.service;

import com.ec.catalog.dto.address.AddressCreateForm;
import com.ec.catalog.dto.address.AddressUpdateForm;
import com.ec.catalog.entity.Address;
import com.ec.catalog.entity.Profile;

import java.util.List;

public interface AddressService {
	
	Address getAddressById(String addressId);
	List<Address> getAddressByProfileId(String profileId);
	Address createAddress(Profile profile, AddressCreateForm form);
	Address updateAddress(String addressId, AddressUpdateForm form);
	Address setDefault(String addressId, String profileId);
	Address deleteAddress(String addressId);
}
