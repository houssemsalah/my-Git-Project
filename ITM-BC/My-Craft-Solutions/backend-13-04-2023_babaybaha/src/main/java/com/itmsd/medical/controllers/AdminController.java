package com.itmsd.medical.controllers;

import java.util.ArrayList;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import com.itmsd.medical.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.itmsd.medical.entities.Plombier;
import com.itmsd.medical.repositories.MenuisierRepository;

import javax.servlet.http.HttpServletRequest;

@Transactional
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AdminController {


	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private UserRepository userRepository;
	private ExperienceRepository experienceRepository;
	private DiplomeRepository diplomeRepository;
	private NotificationRepository notificationRepository;
	private SessionRepository sessionRepository;
	private ScheduleRepository scheduleRepository;
	private MenuisierRepository menuisierRepository;
	private ElectricienRepository electricienRepository;	
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;	
	private PeintreRepository peintreRepository;	
	private SoudeurRepository soudeurRepository;

	private UserController userController;
	@Autowired
	UserService userService;
	
	@Autowired
	public AdminController(SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository,UserRepository userRepository, ExperienceRepository experienceRepository,
						   DiplomeRepository diplomeRepository, NotificationRepository notificationRepository,
						   SessionRepository sessionRepository, ScheduleRepository scheduleRepository,
						   MenuisierRepository menuisierRepository, ElectricienRepository electricienRepository,
						   PlombierRepository plombierRepository,CarreleurRepository carreleurRepository, PaysagisteRepository paysagisteRepository, UserController userController,
						   PeintreRepository peintreRepository, SoudeurRepository soudeurRepository) {
		super();
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.experienceRepository = experienceRepository;
		this.diplomeRepository = diplomeRepository;
		this.notificationRepository = notificationRepository;
		this.sessionRepository = sessionRepository;
		this.scheduleRepository = scheduleRepository;
		this.menuisierRepository = menuisierRepository;
		this.electricienRepository = electricienRepository;
		this.plombierRepository = plombierRepository;
		this.carreleurRepository = carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.userController = userController;
	}
	
	@PostMapping("/ban/{userId}")
	public String banUser(@PathVariable (value = "userId") Long userId) {
			User user = userRepository.findUserById(userId);
			if (user!=null) {
				user.setBanned(true);
				userRepository.save(user);
			}
			
		return "user has been banned";
	}

	@PostMapping("/unban/{userId}")
	public String unbanUser(@PathVariable (value = "userId") Long userId) {
			User user = userRepository.findUserById(userId);
			if (user!=null) {
				user.setBanned(false);
				userRepository.save(user);
			}
			
		return "user has been unbanned";
	}
	
	@PostMapping("/delete/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable (value = "userId") Long userId) {
		
		return userRepository.findById(userId).map(user -> {
			user.removeForum();
			user.getExperiences().forEach(exp -> {
				experienceRepository.delete(exp);
			});
			user.getDiplomes().forEach(dip->{
				diplomeRepository.delete(dip);
			});
			user.getNotifications().forEach(not->{
				notificationRepository.delete(not);
			});
			if (user.getSchedule()!=null)	{
				if (user.getSchedule().getSession()!=null) {
					user.getSchedule().getSession().forEach(sess -> {
						sessionRepository.delete(sess);

					});
				}
				scheduleRepository.delete(user.getSchedule());
			}
			
			user.setSpeciality(null);
			user.setVille(null);
			user.setPassword(null);
			user.setPasswordConfirmation(null);
			user.setDescription(null);
			user.setInami(null);
			user.setConfirmationToken(null);
			user.setJwt(null);
			user.setOnline("offline");
			user.setDeleted(true);
			user.setPhotoUrl("https://res.cloudinary.com/bilel-moussa/image/upload/v1644533163/user-icon-human-person-sign-vector-10206693_hs5pi9.png");
			userRepository.save(user);
			if (user.getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
				Menuisier perso = menuisierRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
				Soudeur perso = soudeurRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("plombier")) {
				Plombier perso = plombierRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("electricien")) {
				Electricien perso = electricienRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("peintre")) {
				Peintre perso = peintreRepository.findById(userId).get();
				perso.setFirstName("indisponible");
				perso.setLastName("Utilisateur");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());
				perso.setPhotoCabinet(new ArrayList<String>());
			}else if (user.getRoles().stream().findFirst().get().getRole().contains("paysagiste")) {
				Paysagiste perso = paysagisteRepository.findById(userId).get();
				perso.setFirstName("Utilisateur indisponible");
				perso.setLastName("Utilisateur indisponible");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());

			}else if (user.getRoles().stream().findFirst().get().getRole().contains("chauffagiste")) {
				Chauffagiste perso = chauffagisteRepository.findById(userId).get();
				perso.setFirstName("Utilisateur indisponible");
				perso.setLastName("Utilisateur indisponible");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());

			}else if (user.getRoles().stream().findFirst().get().getRole().contains("serrurier")) {
				Serrurier perso = serrurierRepository.findById(userId).get();
				perso.setFirstName("Utilisateur indisponible");
				perso.setLastName("Utilisateur indisponible");
				perso.setNbRdv(0);
				perso.setLanguage(new ArrayList<String>());

			}
			userRepository.save(user);
			return ResponseEntity.ok().body("User "+userId+" has been deleted");
		}).orElseThrow(() -> new IllegalArgumentException("userId " + userId + " not found"));
	}
	@GetMapping("all")
	public ResponseEntity<?> getAllUsers(HttpServletRequest request ){
		return ResponseEntity.ok().body(userService.getAllUsers());
	}



	@GetMapping("single/personnel/{personnelId}")
	public ResponseEntity<?> getPersonell(@PathVariable(value = "personnelId") Long personnelId,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User rdv = userRepository.findById(personnelId).get();
		return ResponseEntity.ok().body(rdv);
	}

	@GetMapping("single/client/{clientId}")
	public ResponseEntity<?> getClient(@PathVariable(value = "clientId") Long clientId,HttpServletRequest request ) {
		//String jwt = request.getHeader("Authorization").substring(7);
		//Long userIdco = userController.userIdFromToken(jwt);
	//	User rdv = userRepository.findById(clientId).get();
		return ResponseEntity.ok().body(userRepository.findById(clientId).get());
	}
}
