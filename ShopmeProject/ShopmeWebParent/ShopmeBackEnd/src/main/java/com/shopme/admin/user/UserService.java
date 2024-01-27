package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional   // Spring sẽ bắt đầu một giao dịch trước khi phương thức được gọi và commit giao dịch sau khi phương thức hoàn thành (hoặc rollback nếu có lỗi).
public class UserService {
	
	public static final int USERS_PER_PAGE = 5;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}

	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		
		return userRepo.findAll(pageable);
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		if(isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			if(user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			}else {
				encodePassword(user);
			}
		}else {
			encodePassword(user); // mã hóa mật khẩu trong user rồi mới save.
		}
		
		return userRepo.save(user);
	}
	
	public User updateAccount(User userInForm) {
		User UserInDB = userRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			UserInDB.setPassword(userInForm.getPassword());
			encodePassword(UserInDB);
		}else {
			System.out.println("userInForm.getPassword() isEmpty");
		}
		
		if(userInForm.getPhotos() != null) {
			UserInDB.setPhotos(userInForm.getPhotos());
		}else {
			System.out.println("pho to is null");
		}
		
		UserInDB.setFirstName(userInForm.getFirstName());
		UserInDB.setLastName(userInForm.getLastName());
		
		return userRepo.save(UserInDB);
	}

	// phương thức mã hóa
	private void encodePassword(User user) {
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
	}

	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);
		
		if(userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if(isCreatingNew) {
			if(userByEmail != null) return false;
		}else {
			if(userByEmail.getId() != id) {
				return false;
			}
		}
		
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		}catch(NoSuchElementException ex){
			throw new UserNotFoundException("Could not find any user with id " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		//System.out.println("countById: " + countById);
		if(countById == null || countById == 0) { // nếu ko tìm thấy bản ghi nào thì throw new ...
			throw new UserNotFoundException("Could not find any user with id " + id);
		}
		
		// nếu có bản ghi với id tương ứng thì tiến hành delete
		userRepo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}
}
