package com.itmsd.medical.services;

import java.io.IOException;
import java.util.*;

import com.itmsd.medical.entities.RendezVous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itmsd.medical.entities.Forum;
import com.itmsd.medical.entities.Role;
import com.itmsd.medical.entities.User;
import com.itmsd.medical.repositories.ForumRepository;
import com.itmsd.medical.repositories.RoleRepository;
import com.itmsd.medical.repositories.UserRepository;

import net.bytebuddy.utility.RandomString;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



@Service("userService")
@Transactional
public class UserService {
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private ForumRepository forumRepository;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder,ForumRepository forumRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.forumRepository = forumRepository ;
	}
	public User saveUser(User user, String role ) {
		String token = RandomString.make(30);


		//user.setId(userRepository.getLastInsertedId()+1);
		user.setActive(false);
		user.setOnline("offline");
		user.setConfirmationToken(token);
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setPasswordConfirmation(bCryptPasswordEncoder.encode(user.getPasswordConfirmation()));
		user.setPhotoUrl(user.getPhotoUrl());
		if (role!="client"){
			user.setExperienceNumber(user.getExperienceNumber());
			user.setSpeciality(user.getSpeciality());
			user.setAccreditation(user.getAccreditation());
		}


		String Datedata = DateAttribute.getTime(new Date());
		user.setCreatedAt(Datedata);
		Role userRole = roleRepository.findByRole(role);
		if (userRole == null) {
			Role newRole = new Role (role) ;
			newRole = roleRepository.save(newRole);		
			user.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
		}else {			
			user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		}
		Forum forum = new Forum();
		
		if (role.contains("menuisier")) {
			if (forumRepository.findByForumName("menuisiers") == null ) {
				forum.setForumName("menuisiers");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}else {
				forum = forumRepository.findByForumName("menuisiers");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}
			
		}else if (role.contains("soudeur")) {
			 if (forumRepository.findByForumName("soudeurs") == null ) {
					forum.setForumName("soudeurs");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("soudeurs");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("peintre")) {
			 if (forumRepository.findByForumName("peintres") == null ) {
					forum.setForumName("peintres");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("peintres");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("electricien")) {
			 if (forumRepository.findByForumName("electriciens") == null ) {
					forum.setForumName("electriciens");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("electriciens");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("plombier")) {
			 if (forumRepository.findByForumName("plombiers") == null ) {
					forum.setForumName("plombiers");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("plombiers");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("carreleur")) {
			if (forumRepository.findByForumName("carreleurs") == null ) {
				forum.setForumName("carreleurs");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}else {
				forum = forumRepository.findByForumName("carreleurs");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}
		}else if (role.contains("paysagiste")) {
			 if (forumRepository.findByForumName("paysagistes") == null ) {
					forum.setForumName("paysagistes");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}else {
					forum = forumRepository.findByForumName("paysagistes");
					forum.getUsers().add(userRepository.save(user));
					forumRepository.save(forum);
				}
		}else if (role.contains("chauffagiste")) {
			if (forumRepository.findByForumName("chauffagiste") == null) {
				forum.setForumName("chauffagiste");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			} else {
				forum = forumRepository.findByForumName("chauffagiste");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}
		}else if (role.contains("serrurier")) {
			if (forumRepository.findByForumName("serrurier") == null) {
				forum.setForumName("serrurier");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			} else {
				forum = forumRepository.findByForumName("serrurier");
				forum.getUsers().add(userRepository.save(user));
				forumRepository.save(forum);
			}
		}
		
		return userRepository.save(user);
	}

	/*public void UplodeImage (String email, MultipartFile file){
		User user = userRepository.findUserByEmail(email.toLowerCase());
		System.out.println(user.getId());
		try{
			user.setPhotoUrl(Base64.getEncoder().encodeToString(file.getBytes()));
		}catch (IOException e){
			e.printStackTrace();
		}
		userRepository.save(user);
	}*/

	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}
	public User findUserByConfirmationToken(String confirmationToken) {
		return userRepository.findUserByConfirmationToken(confirmationToken);
	}

	public List<User> getAllUsers() {
		List<User> u = userRepository.findAll();
		return u;

	}

	public List<User> getAllClients() {
		List<User> u = userRepository.findAll();
		List<User> res = null;
		for (User x: u)
			{
				if(x.getSpeciality().isEmpty() && !Objects.equals(x.getUsername(), "superAdmin1"))
				{
					res.add(x);
				}
			}
		return res;

	}
}
