package com.itmsd.medical.controllers;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.repositories.PlombierRepository;
import com.itmsd.medical.services.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itmsd.medical.entities.Plombier;
import com.itmsd.medical.payload.request.EditPhotosCabinet;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.payload.request.UpdatePassword;
import com.itmsd.medical.payload.response.PersonnelRdvs;

@RestController
@RequestMapping({ "/users" })
@CrossOrigin(origins = "*")
public class UserController {
	private AdminRepository adminRepository;
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private UserRepository userRepository;
	private UserService userService;
	private MenuisierRepository MenuisierRepository;
	private ClientRepository clientRepository;
	private ElectricienRepository electricienRepository;	
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;	
	private PeintreRepository peintreRepository;
	private SoudeurRepository soudeurRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private MessageRepository messageRepository;
	
	@Autowired
	public UserController(AdminRepository adminRepository,SerrurierRepository serrurierRepository ,UserRepository userRepository, UserService userService, MenuisierRepository MenuisierRepository,
						  ClientRepository clientRepository, ElectricienRepository electricienRepository,CarreleurRepository carreleurRepository, PlombierRepository plombierRepository,
						  PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
						  SoudeurRepository soudeurRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
						  MessageRepository messageRepository) {
		super();
		this.adminRepository = adminRepository;
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.MenuisierRepository = MenuisierRepository;
		this.clientRepository = clientRepository;
		this.electricienRepository = electricienRepository;
		this.carreleurRepository=carreleurRepository;
		this.plombierRepository = plombierRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.messageRepository = messageRepository;
	}


	@GetMapping("/notconfirmed")
	public ResponseEntity<?> getNotConfirmedAccount(HttpServletRequest request) {
		Collection<User> users= userRepository.findNotConfirmedUsers();
			return ResponseEntity.ok().body(users);


	}


	@DeleteMapping("/delete/message/{messageId}")
	public String deleteMessage(@PathVariable(value="messageId") Long messageId,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		Message msg = messageRepository.findById(messageId).get();
	//	if (msg.getUsername().contains(user.getUsername()) || user.getRoles().stream().findFirst().get().getRole().contains("admin")) {
			
			messageRepository.deleteById(messageId);
			return "Message deleted !";
	//	}else
		//throw new RuntimeException("Security : Bad token !");
	//	return "Message deleted !";
	}
	@PutMapping("/editadmin")
	public Admin updateAdmin(@Valid @RequestBody Admin userRequest, HttpServletRequest request) throws Exception{

		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return adminRepository.findById(userIdco).map(user -> {

			if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			return adminRepository.save(user);

		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
	}
	@GetMapping("/rendezvous/{userId}")
	public ResponseEntity<?> getAllRendezVous(@PathVariable(value = "userId") Long userId,HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		if (userId!=userIdco) throw new RuntimeException("Security: you cannot have access !");
		User user = userRepository.findUserById(userId);

		Set<RendezVous> rends = user.getRendezVous();
		List<PersonnelRdvs> persoRdvs = new ArrayList<>() ;
		if (user.getRoles().stream().findFirst().get().getRole().contains("client")) {
			
			rends.forEach(rdv -> {
				PersonnelRdvs perso = new PersonnelRdvs();
				perso.setId(rdv.getId());
				perso.setStartDate(rdv.getStartDate());
				perso.setEndDate(rdv.getEndDate());
				perso.setDate(rdv.getDate());
				perso.setCreatedAt(rdv.getCreatedAt());
				perso.setSubject(rdv.getSubject());
				perso.setMessage(rdv.getText());
				perso.setStatus(rdv.getStatus());
				User us = userRepository.findById(rdv.getPersonnelId()).get();
				if (us.getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
					Menuisier pt = MenuisierRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
					Soudeur pt = soudeurRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("carreleur")) {
					Carreleur pt = carreleurRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("plombier")) {
					Plombier pt = plombierRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("peintre")) {
					Peintre pt = peintreRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("electricien")) {
					Electricien pt = electricienRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("chauffagiste")) {
					Chauffagiste pt = chauffagisteRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("serrurier")) {
					Serrurier pt = serrurierRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}else if (us.getRoles().stream().findFirst().get().getRole().contains("paysagiste")) {
					Paysagiste pt = paysagisteRepository.findById(rdv.getPersonnelId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(), pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setDoctor(p);
					persoRdvs.add(perso);
				}
			});

		}else if (!user.getRoles().stream().findFirst().get().getRole().contains("client")) {
			rends.forEach(rdv -> {
				PersonnelRdvs perso = new PersonnelRdvs();
				perso.setId(rdv.getId());
				perso.setStartDate(rdv.getStartDate());
				perso.setEndDate(rdv.getEndDate());
				perso.setDate(rdv.getDate());
				perso.setCreatedAt(rdv.getCreatedAt());
				perso.setSubject(rdv.getSubject());
				perso.setMessage(rdv.getText());
				perso.setStatus(rdv.getStatus());
				
					Client pt = clientRepository.findById(rdv.getClientId()).get();
					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
							pt.getPostalCode(),pt.getPhoneNumber());
					perso.setClient(p);
					persoRdvs.add(perso);
				
		});
		}
		Collections.sort(persoRdvs, new SortRdvByDate());
		return ResponseEntity.ok().body(persoRdvs);
	}

	@GetMapping("single/client/{clientId}")
	public ResponseEntity<?> getClient(@PathVariable(value = "clientId") Long clientId,HttpServletRequest request ) {
		//String jwt = request.getHeader("Authorization").substring(7);
		//Long userIdco = userController.userIdFromToken(jwt);
			//User rdv = u;
		return ResponseEntity.ok().body(userRepository.findById(clientId).get());
	}

	@PutMapping("/changestatus/{userId}/{status}")
	public String changeStatus (@PathVariable(value = "userId") Long userId, @PathVariable(value = "status") String status,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
			if(status.contains("online")) {
				user.setOnline("online");
			}else if(status.contains("offline")) {
				user.setOnline("offline");
			}
			userRepository.save(user);
		return "update status with success !";
	}
	
	@PutMapping("/changepassword")
	public String changePassword (@RequestBody UpdatePassword userRequest,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if (bCryptPasswordEncoder.matches(userRequest.getOldPassword(), user.getPassword())) {
			if (userRequest.getPassword()==null && userRequest.getPasswordConfirmation()==null) {
			}else if (userRequest.getPassword()!=null && userRequest.getPasswordConfirmation()!=null && userRequest.getPassword().equals(userRequest.getPasswordConfirmation()) ) {
				user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
			}else throw new RuntimeException("password doesn't match");
			userRepository.save(user);
			return "update password with success !";
		}else throw new RuntimeException("Old password incorrect !");
	}
	
	@PutMapping("/editclient")
	public Client updateClient(@Valid @RequestBody Client userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return clientRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null   ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  
			  
			return clientRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/edit/photocabinet")
	public String updatePhotoCabinet(@Valid @RequestBody EditPhotosCabinet userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		User req = userRepository.findById(userIdco).get();
		if (req.getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
			return MenuisierRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				MenuisierRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			

		}else if (req.getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
			return soudeurRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				soudeurRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("serrurier")) {
			return serrurierRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				serrurierRepository.save(user);
				return "Photos cabinet updated with success !";

			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("chauffagiste")) {
			return chauffagisteRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				chauffagisteRepository.save(user);
				return "Photos cabinet updated with success !";

			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("plombier")) {
			return plombierRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				plombierRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("electricien")) {
			return electricienRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				electricienRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else if (req.getRoles().stream().findFirst().get().getRole().contains("peintre")) {
			return peintreRepository.findById(userIdco).map(user -> {
				if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
				peintreRepository.save(user);
				return "Photos cabinet updated with success !";
				
			}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
		}else 
		return "Bad jwt !";
		}
	
	@PutMapping("/editMenuisier")
	public Menuisier updateMenuisier(@Valid @RequestBody Menuisier userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return MenuisierRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return userRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editsoudeur")
	public Soudeur updateSoudeur(@Valid @RequestBody Soudeur userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return soudeurRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return soudeurRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	@PutMapping("/editserrurier")
	public Serrurier updateSerrurier(@Valid @RequestBody Serrurier userRequest, HttpServletRequest request) throws Exception{

		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return serrurierRepository.findById(userIdco).map(user -> {

			if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());


			return serrurierRepository.save(user);

		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
	}
	@PutMapping("/editchauffagiste")
	public Chauffagiste updateChauffagiste(@Valid @RequestBody Chauffagiste userRequest, HttpServletRequest request) throws Exception{

		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return chauffagisteRepository.findById(userIdco).map(user -> {

			if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());


			return chauffagisteRepository.save(user);

		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
	}
	
	@PutMapping("/editplombier")
	public Plombier updatePlombier(@Valid @RequestBody Soudeur userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return plombierRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return plombierRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editpeintre")
	public Peintre updatePeintre(@Valid @RequestBody Peintre userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return peintreRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			  
			return peintreRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}

	@PutMapping("/editpaysagiste")
	public Paysagiste updatePeintre(@Valid @RequestBody Paysagiste userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return paysagisteRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			if( userRequest.getLastName()!=null ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());

			  
			return paysagisteRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	
	@PutMapping("/editelectricien")
	public Electricien updatePeintre(@Valid @RequestBody Electricien userRequest, HttpServletRequest request) throws Exception{
		
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return electricienRepository.findById(userIdco).map(user -> {
			
			  if( userRequest.getFirstName()!=null ) user.setFirstName(userRequest.getFirstName());
			  if( userRequest.getLastName()!=null  ) user.setLastName(userRequest.getLastName());
			  if( userRequest.getPhoneNumber()>0   )  user.setPhoneNumber(userRequest.getPhoneNumber());
			  if( userRequest.getVille()!=null ) user.setVille(userRequest.getVille());
			  if( userRequest.getAddress()!=null  ) user.setAddress(userRequest.getAddress());
			  if( userRequest.getPostalCode()>0    ) user.setPostalCode(userRequest.getPostalCode());
			  if( userRequest.getLanguage()!=null  ) user.setLanguage(userRequest.getLanguage());
			  if( userRequest.getSpeciality()!=null ) user.setSpeciality(userRequest.getSpeciality());
			  if( userRequest.getInami()!=null    ) user.setInami(userRequest.getInami());
			  if( userRequest.getDescription()!=null    ) user.setInami(userRequest.getDescription());
			  if( userRequest.getExperienceNumber()>0) user.setExperienceNumber(userRequest.getExperienceNumber());
			  if( userRequest.getPhotoUrl()!=null  ) user.setPhotoUrl(userRequest.getPhotoUrl());
			  if( userRequest.getPhotoCabinet()!=null  ) user.setPhotoCabinet(userRequest.getPhotoCabinet());
			  
			return electricienRepository.save(user);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));			
	}
	@GetMapping("/myclients")
	public ResponseEntity<?> getClients(HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		 return userRepository.findById(userIdco).map(user -> {
			 if (user.getRoles().stream().findFirst().get().getRole().contains("client")) 
				 throw new RuntimeException("You are client !") ;
			 List<Personne> myClients = new ArrayList<>();
			 	user.getRendezVous().forEach(rdv ->{
			 		if (rdv.getStatus().contains("confirmed") || rdv.getStatus().contains("accepté")) {
			 			Client client = clientRepository.findById(rdv.getClientId()).get();
			 			
			 			Personne pt = new Personne();
						pt.id = client.getId();
						pt.firstName = client.getFirstName();
						pt.lastName = client.getLastName();
						pt.phoneNumber = client.getPhoneNumber();
						pt.email = client.getEmail();
						pt.createdAt = client.getCreatedAt();
						pt.role = client.getRoles().stream().findFirst().get().getRole();
						pt.status = client.getOnline();
						pt.active = client.isActive();
						pt.banned = client.isBanned();
						pt.deleted = client.isDeleted();
						if (!myClients.stream().anyMatch(pat-> pat.id==pt.id)) {
							myClients.add(pt);
						}
			 		}
			 	});
			 	Collections.sort(myClients, new SortPersonnesByDate());
			 	Collections.reverse(myClients);
			return ResponseEntity.ok().body(myClients);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));	
	}

	@GetMapping("all/clients")
	public ResponseEntity<?> getAllClients(HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		return userRepository.findById(userIdco).map(user -> {
			if (user.getRoles().stream().findFirst().get().getRole().contains("client"))
				throw new RuntimeException("You are client !") ;
			List<Personne> myClients = new ArrayList<>();
			userRepository.findAll().forEach(u ->{
				if (u.getRoles().stream().findFirst().get().getRole().contains("client")) {
//					Client client = clientRepository.findById(rdv.getClientId()).get();

					Personne pt = new Personne();
					pt.id = u.getId();
					pt.username = u.getUsername();
					pt.phoneNumber = u.getPhoneNumber();
					pt.email = u.getEmail();
					pt.createdAt = u.getCreatedAt();
					pt.role = u.getRoles().stream().findFirst().get().getRole();
					pt.status = u.getOnline();
					pt.active = u.isActive();
					pt.banned = u.isBanned();
					pt.deleted = u.isDeleted();
					if (!myClients.stream().anyMatch(pat-> pat.id==pt.id)) {
						myClients.add(pt);
					}
				}
			});
			Collections.sort(myClients, new SortPersonnesByDate());
			Collections.reverse(myClients);
			return ResponseEntity.ok().body(myClients);

		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));
	}

	@GetMapping("/mypersonnels")
	public ResponseEntity<?> getMyPersonnels(HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userIdFromToken(jwt);
		 return userRepository.findById(userIdco).map(user -> {
			 if (!user.getRoles().stream().findFirst().get().getRole().contains("client")) 
				 throw new RuntimeException("You are menuisier !") ;
			
			 List<Personne> myPersonnels = new ArrayList<>();
			 user.getRendezVous().forEach(rdv ->{
				 if (rdv.getStatus().contains("accepté")) {
					 
					 if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
						 Menuisier Menuisier = MenuisierRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("electricien")) {
						 Electricien Menuisier = electricienRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("plombier")) {
						 Plombier Menuisier = plombierRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("serrurier")) {
						 Serrurier Menuisier = serrurierRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("chauffagiste")) {
						 Chauffagiste Menuisier = chauffagisteRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("peintre")) {
						 Peintre Menuisier = peintreRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
						 Soudeur Menuisier = soudeurRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }else if (userRepository.findById(rdv.getPersonnelId()).get()
							 .getRoles().stream().findFirst().get().getRole().contains("paysagiste")) {
						 Paysagiste Menuisier = paysagisteRepository.findById(rdv.getPersonnelId()).get();
						 Personne pt = new Personne();
						 pt.id = Menuisier.getId();
						 pt.firstName = Menuisier.getFirstName();
						 pt.lastName = Menuisier.getLastName();
						 pt.phoneNumber = Menuisier.getPhoneNumber();
						 pt.email = Menuisier.getEmail();
						 pt.createdAt = Menuisier.getCreatedAt();
						 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
						 pt.status = Menuisier.getOnline();
						 pt.active = Menuisier.isActive();
						 pt.banned = Menuisier.isBanned();
						 pt.deleted = Menuisier.isDeleted();
						 if (!myPersonnels.stream().anyMatch(per-> per.id==pt.id)) {
							 myPersonnels.add(pt);
						 }
					 }
					 
				 }
			 	});
			 	Collections.sort(myPersonnels, new SortPersonnesByDate());
			 	Collections.reverse(myPersonnels);
			return ResponseEntity.ok().body(myPersonnels);
		
		}).orElseThrow(() -> new IllegalArgumentException("user Id" + userIdco + " not found"));	
	}
		public Long userIdFromToken (String jwt) {	
		DecodedJWT jwtd = JWT.decode(jwt);
		String usernameFromToken = jwtd.getSubject();
		User user = userService.findUserByUsername(usernameFromToken);
		Long userIdco = null;
		if (user==null) {
			return userIdco;
		}else return user.getId();

	}

	@GetMapping("single/personnel/{personnelId}")
	public ResponseEntity<?> getPersonell(@PathVariable(value = "personnelId") Long personnelId,HttpServletRequest request ) {
		//String jwt = request.getHeader("Authorization").substring(7);
		//Long userIdco = userController.userIdFromToken(jwt);
		User rdv = userRepository.findById(personnelId).get();

		//long nbrdv=rdv.getRendezVous().size();

		return ResponseEntity.ok().body(rdv);
	}

	@GetMapping("single/client/{patientId}")
	public ResponseEntity<?> getClientt(@PathVariable(value = "patientId") Long patientId,HttpServletRequest request ) {
		//String jwt = request.getHeader("Authorization").substring(7);
		//Long userIdco = userController.userIdFromToken(jwt);
		User rdv = userRepository.findById(patientId).get();
		return ResponseEntity.ok().body(rdv);
	}
}