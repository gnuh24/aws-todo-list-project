package aws.todolist.user.service;

import aws.todolist.user.dto.address.AddressCreateForm;
import aws.todolist.user.dto.address.AddressUpdateForm;
import aws.todolist.user.entity.Address;
import aws.todolist.user.entity.Profile;
import aws.todolist.user.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Override
	public Address getAddressById(String addressId) {
		return addressRepository.findById(addressId).orElseThrow(
		    () -> new RuntimeException("Không tìm thấy địa chỉ có ID: " + addressId)
		);
	}
	
	@Override
	public List<Address> getAddressByProfileId(String profileId) {
		return addressRepository.findByProfileIdAndIsDeletedOrderByIsDefaultDesc(profileId, false);
	}
	
	@Override
	public Address createAddress(Profile profile, AddressCreateForm form) {
		Address address = new Address();
		address.setAddress(form.getAddress());
		address.setPhone(form.getPhone());
		address.setFullName(form.getFullName());
		address.setProfile(profile);
		return addressRepository.save(address);
	}
	
	
	@Override
	public Address updateAddress(String addressId, AddressUpdateForm form) {
		Address address = getAddressById(addressId);
		
		if (form.getAddress() != null) {
			address.setAddress(form.getAddress());
		}
		
		if (form.getPhone() != null) {
			address.setPhone(form.getPhone());
		}
		
		if (form.getFullName() != null) {
			address.setFullName(form.getFullName());
		}
		
		return addressRepository.save(address);
	}

	
	@Override
	@Transactional
	public Address setDefault(String addressId, String profileId) {
		addressRepository.resetDefaultAddressByProfileId(profileId);
		
		Address address = getAddressById(addressId);
		address.setIsDefault(true);
		return addressRepository.save(address);
	}
	
	@Override
	public Address deleteAddress(String addressId) {
		Address address = getAddressById(addressId);
		address.setIsDeleted(true);
		return addressRepository.save(address);
	}
}
