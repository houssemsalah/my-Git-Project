package com.itmsd.medical.controllers;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Plombier;
import com.itmsd.medical.payload.request.Personne;
import com.itmsd.medical.payload.request.SearchRequest;
import com.itmsd.medical.payload.response.MembersDto;
import com.itmsd.medical.payload.response.PersonnelRdvs;
import com.itmsd.medical.payload.response.SearchResponse;
import com.itmsd.medical.services.DateAttribute;

@RestController
@RequestMapping("search")
public class SearchController {
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private ClientRepository clientRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private MenuisierRepository MenuisierRepository;
	private ElectricienRepository electricienRepository;
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;
	private PeintreRepository peintreRepository;
	private SoudeurRepository soudeurRepository;
	private MessageRepository messageRepository;
	private UserController userController;
	private AdminRepository adminRepository;
	private ForumRepository forumRepository;

	@Autowired
	public SearchController(SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository,UserRepository userRepository, ClientRepository clientRepository,
							RoleRepository roleRepository, MenuisierRepository MenuisierRepository,
							ElectricienRepository electricienRepository, PlombierRepository plombierRepository,CarreleurRepository carreleurRepository,
							PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
							SoudeurRepository soudeurRepository, MessageRepository messageRepository,
							UserController userController, AdminRepository adminRepository, ForumRepository forumRepository) {
		super();
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.userRepository = userRepository;
		this.clientRepository = clientRepository;
		this.roleRepository = roleRepository;
		this.electricienRepository = electricienRepository;
		this.plombierRepository = plombierRepository;
		this.carreleurRepository=carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.messageRepository = messageRepository;
		this.userController = userController;
		this.adminRepository = adminRepository;
		this.forumRepository = forumRepository;
	}

	@GetMapping("schedule/{userId}")
	public ResponseEntity<?> getSchedule(@PathVariable Long userId){
		User user = userRepository.findUserById(userId);
		if(!user.getSchedule().getSession().isEmpty()) {
			List<Session> ss = user.getSchedule().getSession();
			Collections.sort(ss, new SortSessionByDate() );
			Collections.reverse(ss);
			return ResponseEntity.ok().body(ss);
		} else return ResponseEntity.badRequest().body("This user doesn't have session");

	}

	@GetMapping("most/viewed")
	public ResponseEntity<?> getMostViewed() {
		List<Personne> viewed = new ArrayList<Personne>();

		if (userRepository.findAll()!=null) {
			userRepository.findTopConsulted().forEach(user -> {
				if (user.isDeleted()) {
					userRepository.findTopConsulted().remove(user);
				}
				if (!user.getRoles().stream().findFirst().get().getRole().contains("admin")&&
						!user.getRoles().stream().findFirst().get().getRole().contains("client") && !user.isDeleted()) {
					Personne pt = new Personne();
					pt.id = user.getId();
					pt.phoneNumber = user.getPhoneNumber();
					pt.email = user.getEmail();
					pt.createdAt = user.getCreatedAt();
					pt.role = user.getRoles().stream().findFirst().get().getRole();
					pt.status = user.getOnline();
					pt.active = user.isActive();
					pt.banned = user.isBanned();
					pt.nbViews = user.getNbviews();
					if (user.getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
						Menuisier med = MenuisierRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}if (user.getRoles().stream().findFirst().get().getRole().contains("chauffagiste")) {
						Chauffagiste med = chauffagisteRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}if (user.getRoles().stream().findFirst().get().getRole().contains("serrurier")) {
						Serrurier med = serrurierRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
						Soudeur med = soudeurRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("carreleur")) {
						Carreleur med = carreleurRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("plombier")) {
						Plombier med = plombierRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("electricien")) {
						Electricien med = electricienRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("peintre")) {
						Peintre med = peintreRepository.findById(user.getId()).get();
						pt.firstName = med.getFirstName();
						pt.lastName = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}else if (user.getRoles().stream().findFirst().get().getRole().contains("paysagiste")) {
						Paysagiste med = paysagisteRepository.findById(user.getId()).get();
						pt.name = med.getFirstName();
						pt.name = med.getLastName();
						pt.address = med.getAddress();
						pt.postalCode = med.getPostalCode();
					}

					viewed.add(pt);
				}
			});
			Collections.sort(viewed, new SortPersonnesByDate());
			Collections.reverse(viewed);
			return ResponseEntity.ok(viewed);
		}else return ResponseEntity.badRequest().body("There is no viewed personnels !");

	}

	@GetMapping("clients")
	public ResponseEntity<?> getClients(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		if (!user.getUsername().equals("superAdmin1")) throw new RuntimeException("Security: Only admin can see this !");
		List<Personne> clients = new ArrayList<Personne>();
		if (clientRepository.findAll()!=null) {
			clientRepository.findAll().forEach(client -> {
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
				clients.add(pt);
			});
			Collections.sort(clients, new SortPersonnesByDate());
			Collections.reverse(clients);
			return ResponseEntity.ok(clients);
		}else return ResponseEntity.badRequest().body("There is no clients !");

	}
	@GetMapping("/get/{forumName}")
	public ResponseEntity<?> getforum(@PathVariable(value = "forumName") String forumName){
		return ResponseEntity.ok().body(forumRepository.findByForumName(forumName));}
	@GetMapping("personnels")
	public ResponseEntity<?> getPersonnel(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		if (!user.getUsername().equals("superAdmin1")) throw new RuntimeException("Security: Only admin can see this !");
		List<Personne> personnels = new ArrayList<Personne>();

		if (MenuisierRepository.findAll()!=null) {
		 MenuisierRepository.findAll().forEach(Menuisier ->{
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
				personnels.add(pt);
		 });
		}
		 if (soudeurRepository.findAll()!=null) {
			 soudeurRepository.findAll().forEach(Menuisier ->{
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
				 personnels.add(pt);
			 });
		 }
		if (chauffagisteRepository.findAll()!=null) {
			chauffagisteRepository.findAll().forEach(Menuisier ->{
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
				personnels.add(pt);
			});
		}
		if (serrurierRepository.findAll()!=null) {
			serrurierRepository.findAll().forEach(Menuisier ->{
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
				personnels.add(pt);
			});
		}
		 if (peintreRepository.findAll()!=null) {
			 peintreRepository.findAll().forEach(Menuisier ->{
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
				 personnels.add(pt);
			 });
		 }
		if (carreleurRepository.findAll()!=null) {
			carreleurRepository.findAll().forEach(Menuisier ->{
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
				personnels.add(pt);
			});
		}
		 if (plombierRepository.findAll()!=null) {
			 plombierRepository.findAll().forEach(Menuisier ->{
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

				 personnels.add(pt);
			 });
		 }
		 if (electricienRepository.findAll()!=null) {
			 electricienRepository.findAll().forEach(Menuisier ->{
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
				 personnels.add(pt);
			 });
		 }
		 if (paysagisteRepository.findAll()!=null) {
			 paysagisteRepository.findAll().forEach(Menuisier ->{
				 Personne pt = new Personne();
				 pt.id = Menuisier.getId();
				 pt.firstName = Menuisier.getFirstName();
				 pt.lastName = Menuisier. getLastName();
				 pt.phoneNumber = Menuisier.getPhoneNumber();
				 pt.email = Menuisier.getEmail();
				 pt.createdAt = Menuisier.getCreatedAt();
				 pt.role = Menuisier.getRoles().stream().findFirst().get().getRole();
				 pt.status = Menuisier.getOnline();
				 pt.active = Menuisier.isActive();
				 pt.banned = Menuisier.isBanned();
				 pt.deleted = Menuisier.isDeleted();
				 personnels.add(pt);
			 });
		 }
		 Collections.sort(personnels, new SortPersonnesByDate());
		 Collections.reverse(personnels);
		if (!personnels.isEmpty()) {
			return ResponseEntity.ok().body(personnels);
		} else return ResponseEntity.ok().body("There is no personnels !");
	}


	@GetMapping("user/{userId}")
	public ResponseEntity<?> getPersonnelOrClient(@PathVariable Long userId,HttpServletRequest request) {
		String ipUser = request.getRemoteAddr();
	if(userRepository.findUserById(userId)!=null) {
		User user = userRepository.findUserById(userId);

		if (user.getRoles().contains(roleRepository.findByRole("client"))) {
			Client pat = clientRepository.findById(userId).get();
			pat.setForum(null);
			pat.setNotifications(null);
			pat.setUsername(null);
			pat.setJwt(null);
			pat.setRendezVous(null);
			return ResponseEntity.ok().body(clientRepository.findById(userId));
		}else if (user.getRoles().contains(roleRepository.findByRole("menuisier"))) {
			Menuisier med = MenuisierRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				MenuisierRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("electricien"))) {
			Electricien med = electricienRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				electricienRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);

		}else if (user.getRoles().contains(roleRepository.findByRole("serrurier"))) {
			Serrurier med = serrurierRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				serrurierRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);

		}else if (user.getRoles().contains(roleRepository.findByRole("chaffagiste"))) {
			Chauffagiste med = chauffagisteRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				chauffagisteRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);

		}else if (user.getRoles().contains(roleRepository.findByRole("plombier"))) {
			Plombier med = plombierRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				plombierRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("carreleur"))) {
			Carreleur med = carreleurRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				carreleurRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);

		}else if (user.getRoles().contains(roleRepository.findByRole("paysagiste"))) {
			Paysagiste med = paysagisteRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				paysagisteRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("peintre"))) {
			Peintre med = peintreRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				peintreRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("soudeur"))) {
			Soudeur med = soudeurRepository.findById(userId).get();
			Collections.sort(med.getExperiences(), new SortExperienceByDate());
			Collections.sort(med.getDiplomes(), new SortDiplomeByDate());
			if (med.getIpUsers().isEmpty() || !med.getIpUsers().contains(ipUser)) {
				med.getIpUsers().add(ipUser);
				med.setNbviews(med.getNbviews()+1);
				soudeurRepository.save(med);
			}
			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			return ResponseEntity.ok().body(med);
		}else if (user.getRoles().contains(roleRepository.findByRole("admin"))) {
			Admin admin = adminRepository.findById(userId).get();
			admin.setJwt(null);
			return ResponseEntity.ok().body(admin);

		}

		return ResponseEntity.ok("Bad id !");
	}
	return ResponseEntity.ok("No user with this id !");
	}
	@GetMapping("all/{userId}")
	public ResponseEntity<?> getall(@PathVariable Long userId,HttpServletRequest request) {
		Optional<Chauffagiste> Menuisier = chauffagisteRepository.findById(userId);
		return ResponseEntity.badRequest().body(Menuisier);
	}

	@GetMapping("user/message/{userId}")
	public ResponseEntity<?> getMessagePersonnel(@PathVariable Long userId,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null) {
		if (user.getRoles().contains(roleRepository.findByRole("client"))) {
			return ResponseEntity.badRequest().body("this is client");
		}else {
				Set<Message> msgs = messageRepository.findMessages(user.getForum().stream().findFirst().get().getId());
					msgs.forEach((msg)-> {
					msg.setAvatarUrl(userRepository.findUserByUsername(msg.getUsername()).getPhotoUrl());
			});
			return ResponseEntity.ok().body(msgs);
		}
	}
	return ResponseEntity.ok("Bad id !");
	}
	@GetMapping("admin/messages/{category}")
	public ResponseEntity<?> getMessageAdmin(@PathVariable(value="category") String category,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null && !user.getRoles().contains(roleRepository.findByRole("admin"))) {
			return ResponseEntity.badRequest().body("Security you are not admin sorry !");
		}else if (category == null){
			throw new RuntimeException("Invalid category !");
		}else {
				Set<Message> msgs = messageRepository.findMessages((forumRepository.findByForumName(category).getId()));
					msgs.forEach((msg)-> {
					msg.setAvatarUrl(userRepository.findUserByUsername(msg.getUsername()).getPhotoUrl());
			});
			return ResponseEntity.ok().body(msgs);
		}
	}

	@GetMapping("members/forum/{category}")
	public ResponseEntity<?> getMembersForum(@PathVariable(value="category") String category,HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findById(userIdco).get();
		if(user!=null) {
			if (category.contains("client")) {
			return ResponseEntity.badRequest().body("this is client");
		}else if (category.contains("paysagiste")){
			List<Paysagiste> paysagiste = paysagisteRepository.findAll();
			List<MembersDto> members = new ArrayList<>();

			paysagiste.forEach(ph -> {
				if (!ph.isDeleted()) {
					MembersDto member = new MembersDto();
					member.setId(ph.getId());
					member.setFirstName(ph.getFirstName());
					member.setLastName(ph.getLastName());
					member.setUsername(ph.getUsername());
					member.setCreatedAt(ph.getCreatedAt());
					member.setImageUrl(ph.getPhotoUrl());
					member.setStatus(ph.getOnline());
					members.add(member);
				}
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("menuisier")){
			List<Menuisier> Menuisier = MenuisierRepository.findAll();
			List<MembersDto> members = new ArrayList<>();
			Menuisier.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setSpeciality(med.getSpeciality());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}

			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("serrurier")){
				List<Serrurier> Menuisier = serrurierRepository.findAll();
				List<MembersDto> members = new ArrayList<>();
				Menuisier.forEach(med -> {
					if (!med.isDeleted()) {
						MembersDto member = new MembersDto();
						member.setId(med.getId());
						member.setFirstName(med.getFirstName());
						member.setLastName(med.getLastName());
						member.setUsername(med.getUsername());
						member.setSpeciality(med.getSpeciality());
						member.setCreatedAt(med.getCreatedAt());
						member.setImageUrl(med.getPhotoUrl());
						member.setStatus(med.getOnline());
						members.add(member);
					}

				});
				Collections.reverse(members);
				return ResponseEntity.ok().body(members);
			}else if (category.contains("chauffagiste")){
				List<Chauffagiste> Menuisier = chauffagisteRepository.findAll();
				List<MembersDto> members = new ArrayList<>();
				Menuisier.forEach(med -> {
					if (!med.isDeleted()) {
						MembersDto member = new MembersDto();
						member.setId(med.getId());
						member.setFirstName(med.getFirstName());
						member.setLastName(med.getLastName());
						member.setUsername(med.getUsername());
						member.setSpeciality(med.getSpeciality());
						member.setCreatedAt(med.getCreatedAt());
						member.setImageUrl(med.getPhotoUrl());
						member.setStatus(med.getOnline());
						members.add(member);
					}

				});
				Collections.reverse(members);
				return ResponseEntity.ok().body(members);
			}else if (category.contains("soudeur")){
			List<Soudeur> Menuisier = soudeurRepository.findAll();
			List<MembersDto> members = new ArrayList<>();
			Menuisier.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}

			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("electricien")){
			List<Electricien> Menuisier = electricienRepository.findAll();
			List<MembersDto> members = new ArrayList<>();
			Menuisier.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}

			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}else if (category.contains("plombier")){
			List<Plombier> Menuisier = plombierRepository.findAll();
			List<MembersDto> members = new ArrayList<>();
			Menuisier.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}

			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
			}else if (category.contains("carreleur")){
				List<Carreleur> Menuisier = carreleurRepository.findAll();
				List<MembersDto> members = new ArrayList<>();
				Menuisier.forEach(med -> {
					if (!med.isDeleted()) {
						MembersDto member = new MembersDto();
						member.setId(med.getId());
						member.setFirstName(med.getFirstName());
						member.setLastName(med.getLastName());
						member.setUsername(med.getUsername());
						member.setCreatedAt(med.getCreatedAt());
						member.setImageUrl(med.getPhotoUrl());
						member.setStatus(med.getOnline());
						members.add(member);
					}

				});
				Collections.reverse(members);
				return ResponseEntity.ok().body(members);
		}else if (category.contains("peintre")){
			List<Peintre> Menuisier = peintreRepository.findAll();
			List<MembersDto> members = new ArrayList<>();
			Menuisier.forEach(med -> {
				if (!med.isDeleted()) {
					MembersDto member = new MembersDto();
				member.setId(med.getId());
				member.setFirstName(med.getFirstName());
				member.setLastName(med.getLastName());
				member.setUsername(med.getUsername());
				member.setCreatedAt(med.getCreatedAt());
				member.setImageUrl(med.getPhotoUrl());
				member.setStatus(med.getOnline());
				members.add(member);
				}
			});
			Collections.reverse(members);
			return ResponseEntity.ok().body(members);
		}
	}
	return ResponseEntity.badRequest().body("Bad id !");
	}
/*@GetMapping("findAllByPostalCode")
	public ResponseEntity<?> findAllByPostalCode (@RequestBody SearchRequest search) {
		return ResponseEntity.ok().body(plombierRepository.findAllByPostalCode(search.getCodePostal()));
	}*/



















	@PostMapping("personnel/precise")
	public ResponseEntity<?> searchPersonnel(@RequestBody SearchRequest search){
			 SearchResponse response = new SearchResponse();
if(search.getCategory()==null){

	if(1==2){response.setMenuisiers(getMenuisierByLanguage(MenuisierRepository.findAll(),search.getLangue()));
		response.setPlombiers(getPlombierByLanguage(plombierRepository.findAll(),search.getLangue()));
		response.setCarreleurs(getCarreleurByLanguage(carreleurRepository.findAll(),search.getLangue()));
		response.setElectriciens(getElectricienByLanguage(electricienRepository.findAll(),search.getLangue()));
		response.setPaysagistes(getPaysagisteByLanguage(paysagisteRepository.findAll(),search.getLangue()));
		response.setSoudeurs(getSoudeurByLanguage(soudeurRepository.findAll(),search.getLangue()));
		response.setPeintres(getPeintreByLanguage(peintreRepository.findAll(),search.getLangue()));
		response.setChauffagistes(getChauffagisteByLanguage(chauffagisteRepository.findAll(),search.getLangue()));
		response.setSerruriers(getSerrurierByLanguage(serrurierRepository.findAll(),search.getLangue()));
		return ResponseEntity.ok().body(response);
	}
			 if((search.getCodePostal() != null && search.getVille()==null && search.getSpeciality() == null && search.getCategory()==null ) || (search.getCodePostal()!= null &&  search.getVille().isEmpty() && search.getSpeciality().isEmpty() && search.getCategory().isEmpty() )) {
		List <Object> techs= new ArrayList<>();
			response.setMenuisiers(MenuisierRepository.findAllByPostalCode(search.getCodePostal()));
			response.setPlombiers(plombierRepository.findAllByPostalCode(search.getCodePostal()));
			response.setCarreleurs(carreleurRepository.findAllByPostalCode(search.getCodePostal()));
			response.setElectriciens(electricienRepository.findAllByPostalCode(search.getCodePostal()));
			response.setPaysagistes(paysagisteRepository.findAllByPostalCode(search.getCodePostal()));
			response.setSoudeurs(soudeurRepository.findAllByPostalCode(search.getCodePostal()));
			response.setPeintres(peintreRepository.findAllByPostalCode(search.getCodePostal()));
			response.setChauffagistes(chauffagisteRepository.findAllByPostalCode(search.getCodePostal()));
			response.setSerruriers(serrurierRepository.findAllByPostalCode(search.getCodePostal()));
			return ResponseEntity.ok().body(response);
		}

		  if(search.getVille()!=null && search.getCodePostal() == null &&  search.getSpeciality() == null && search.getCategory()==null ) {
			List <Object> techs= new ArrayList<>();
			response.setMenuisiers(MenuisierRepository.findAllByVille(search.getVille()));
			response.setPlombiers(plombierRepository.findAllByVille(search.getVille()));
			response.setCarreleurs(carreleurRepository.findAllByVille(search.getVille()));
			response.setElectriciens(electricienRepository.findAllByVille(search.getVille()));
			response.setPaysagistes(paysagisteRepository.findAllByVille(search.getVille()));
			response.setSoudeurs(soudeurRepository.findAllByVille(search.getVille()));
			response.setPeintres(peintreRepository.findAllByVille(search.getVille()));
			response.setChauffagistes(chauffagisteRepository.findAllByVille(search.getVille()));
			response.setSerruriers(serrurierRepository.findAllByVille(search.getVille()));
			return ResponseEntity.ok().body(response);
		}
		if(search.getCodePostal() != null && search.getVille()!=null && search.getSpeciality() == null && search.getCategory()==null ) {
			List <Object> techs= new ArrayList<>();
			response.setMenuisiers(MenuisierRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setPlombiers(plombierRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setCarreleurs(carreleurRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setElectriciens(electricienRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setPaysagistes(paysagisteRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setSoudeurs(soudeurRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setPeintres(peintreRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setChauffagistes(chauffagisteRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			response.setSerruriers(serrurierRepository.findAllByVilleAndPostalCode(search.getVille(),search.getCodePostal()));
			return ResponseEntity.ok().body(response);
		}

				// ResponseEntity.ok().body("Category is null");



	//	if((search.getCodePostal() == null && search.getVille()==null && search.getSpeciality() == null && search.getCategory()!=null && search.getLangue()==null) ) {
	//		response.setMenuisiers(MenuisierRepository.findAll());
		}
	//else
		if (search.getCategory()!=null )
		{

			if (search.getCategory().contains("menuisier")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setMenuisiers(MenuisierRepository.findAll());
					response.getMenuisiers().forEach(med -> {
						if (med.isDeleted()) {
							response.getMenuisiers().remove(med);
						}
					});
					if (!search.getLangue().isEmpty()) {
						response.setMenuisiers(getMenuisierByLanguage(response.getMenuisiers(), search.getLangue()));
						response.getMenuisiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getMenuisiers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				else if ((search.getVille() != null && search.getCodePostal() == null)) {
					response.setMenuisiers(MenuisierRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setMenuisiers(getMenuisierByLanguage(response.getMenuisiers(), search.getLangue()));
						response.getMenuisiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getMenuisiers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				}
				 if (search.getVille() != null && search.getCodePostal() != null ) {
					response.setMenuisiers(MenuisierRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setMenuisiers(getMenuisierByLanguage(response.getMenuisiers(), search.getLangue()));
						response.getMenuisiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setMenuisiers(MenuisierRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setMenuisiers(getMenuisierByLanguage(response.getMenuisiers(), search.getLangue()));
						response.getMenuisiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}

			if (search.getCategory().contains("paysagiste")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setPaysagistes(paysagisteRepository.findAll());
					response.getPaysagistes().forEach(med -> {
						if (med.isDeleted()) {
							response.getPaysagistes().remove(med);
						}
					});
					if (!search.getLangue().isEmpty()) {
						response.setPaysagistes(getPaysagisteByLanguage(response.getPaysagistes(), search.getLangue()));
						response.getPaysagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPaysagistes().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setPaysagistes(paysagisteRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setPaysagistes(getPaysagisteByLanguage(response.getPaysagistes(), search.getLangue()));
						response.getPaysagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPaysagistes().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setPaysagistes(paysagisteRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPaysagistes(getPaysagisteByLanguage(response.getPaysagistes(), search.getLangue()));
						response.getPaysagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setPaysagistes(paysagisteRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPaysagistes(getPaysagisteByLanguage(response.getPaysagistes(), search.getLangue()));
						response.getPaysagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			} else if (search.getCategory().contains("soudeur")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setSoudeurs(soudeurRepository.findAll());
					response.getSoudeurs().forEach(med -> {
						if (med.isDeleted()) {
							response.getSoudeurs().remove(med);
						}
					});
					if (!search.getLangue().isEmpty()) {
						response.setSoudeurs(getSoudeurByLanguage(response.getSoudeurs(), search.getLangue()));
						response.getSoudeurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getSoudeurs().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setSoudeurs(soudeurRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setSoudeurs(getSoudeurByLanguage(response.getSoudeurs(), search.getLangue()));
						response.getSoudeurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getSoudeurs().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setSoudeurs(soudeurRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setSoudeurs(getSoudeurByLanguage(response.getSoudeurs(), search.getLangue()));
						response.getSoudeurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setSoudeurs(soudeurRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setSoudeurs(getSoudeurByLanguage(response.getSoudeurs(), search.getLangue()));
						response.getSoudeurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getCategory().contains("carreleur")) {
					if (search.getVille() == null && search.getCodePostal() == null) {
						response.setCarreleurs(carreleurRepository.findAll());

						if (!search.getLangue().isEmpty()) {
							response.setCarreleurs(getCarreleurByLanguage(response.getCarreleurs(), search.getLangue()));
							response.getCarreleurs().forEach(med -> {
								med.setForum(null);
								med.setNotifications(null);
								med.setUsername(null);
								med.setJwt(null);
								med.setRendezVous(null);
								med.setIpUsers(null);
								med.setSchedule(null);
							});
							return ResponseEntity.ok().body(response);
						}
						response.getCarreleurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					} else if (search.getVille() != null && search.getCodePostal() == null) {
						response.setCarreleurs(carreleurRepository.findAllByVille(search.getVille()));
						if (!search.getLangue().isEmpty()) {
							response.setCarreleurs(getCarreleurByLanguage(response.getCarreleurs(), search.getLangue()));
							response.getCarreleurs().forEach(med -> {
								med.setForum(null);
								med.setNotifications(null);
								med.setUsername(null);
								med.setJwt(null);
								med.setRendezVous(null);
								med.setIpUsers(null);
								med.setSchedule(null);
							});
							return ResponseEntity.ok().body(response);
						}
						response.getCarreleurs().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					} else if (search.getVille() != null && search.getCodePostal() != null) {
						response.setCarreleurs(carreleurRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

						if (!search.getLangue().isEmpty()) {
							response.setCarreleurs(getCarreleurByLanguage(response.getCarreleurs(), search.getLangue()));
							response.getCarreleurs().forEach(med -> {
								med.setForum(null);
								med.setNotifications(null);
								med.setUsername(null);
								med.setJwt(null);
								med.setRendezVous(null);
								med.setIpUsers(null);
								med.setSchedule(null);
							});
							return ResponseEntity.ok().body(response);
						}
					} else if (search.getVille() == null && search.getCodePostal() != null) {
						response.setCarreleurs(carreleurRepository.findAllByPostalCode(search.getCodePostal()));

						if (!search.getLangue().isEmpty()) {
							response.setCarreleurs(getCarreleurByLanguage(response.getCarreleurs(), search.getLangue()));
							response.getCarreleurs().forEach(med -> {
								med.setForum(null);
								med.setNotifications(null);
								med.setUsername(null);
								med.setJwt(null);
								med.setRendezVous(null);
								med.setIpUsers(null);
								med.setSchedule(null);
							});
							return ResponseEntity.ok().body(response);
						}
					}
				}


			} else if (search.getCategory().contains("plombier")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setPlombiers(plombierRepository.findAll());

					if (!search.getLangue().isEmpty()) {
						response.setPlombiers(getPlombierByLanguage(response.getPlombiers(), search.getLangue()));
						response.getPlombiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPlombiers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setPlombiers(plombierRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setPlombiers(getPlombierByLanguage(response.getPlombiers(), search.getLangue()));
						response.getPlombiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPlombiers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setPlombiers(plombierRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPlombiers(getPlombierByLanguage(response.getPlombiers(), search.getLangue()));
						response.getPlombiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setPlombiers(plombierRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPlombiers(getPlombierByLanguage(response.getPlombiers(), search.getLangue()));
						response.getPlombiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			} else if (search.getCategory().contains("chauffagiste")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setChauffagistes(chauffagisteRepository.findAll());

					if (!search.getLangue().isEmpty()) {
						response.setChauffagistes(getChauffagisteByLanguage(response.getChauffagistes(), search.getLangue()));
						response.getChauffagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getChauffagistes().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setChauffagistes(chauffagisteRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setChauffagistes(getChauffagisteByLanguage(response.getChauffagistes(), search.getLangue()));
						response.getChauffagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getChauffagistes().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setChauffagistes(chauffagisteRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setChauffagistes(getChauffagisteByLanguage(response.getChauffagistes(), search.getLangue()));
						response.getChauffagistes().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setChauffagistes(chauffagisteRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPlombiers(getPlombierByLanguage(response.getPlombiers(), search.getLangue()));
						response.getPlombiers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			} else if (search.getCategory().contains("serrurier")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setSerruriers(serrurierRepository.findAll());

					if (!search.getLangue().isEmpty()) {
						response.setSerruriers(getSerrurierByLanguage(response.getSerruriers(), search.getLangue()));
						response.getSerruriers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getSerruriers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setSerruriers(serrurierRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setSerruriers(getSerrurierByLanguage(response.getSerruriers(), search.getLangue()));
						response.getSerruriers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getSerruriers().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setSerruriers(serrurierRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setSerruriers(getSerrurierByLanguage(response.getSerruriers(), search.getLangue()));
						response.getSerruriers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setSerruriers(serrurierRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setSerruriers(getSerrurierByLanguage(response.getSerruriers(), search.getLangue()));
						response.getSerruriers().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			} else if (search.getCategory().contains("peintre")) {
				if (search.getVille() == null && search.getCodePostal() == null) {
					response.setPeintres(peintreRepository.findAll());

					if (!search.getLangue().isEmpty()) {
						response.setPeintres(getPeintreByLanguage(response.getPeintres(), search.getLangue()));
						response.getPeintres().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPeintres().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setPeintres(peintreRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setPeintres(getPeintreByLanguage(response.getPeintres(), search.getLangue()));
						response.getPeintres().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getPeintres().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setPeintres(peintreRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPeintres(getPeintreByLanguage(response.getPeintres(), search.getLangue()));
						response.getPeintres().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setPeintres(peintreRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setPeintres(getPeintreByLanguage(response.getPeintres(), search.getLangue()));
						response.getPeintres().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			} else if (search.getCategory().contains("electricien")) {
				if (search.getVille() == null && search.getCodePostal() == null) {

					if (!search.getLangue().isEmpty()) {
						response.setElectriciens(getElectricienByLanguage(response.getElectriciens(), search.getLangue()));
						response.getElectriciens().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getElectriciens().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() == null) {
					response.setElectriciens(electricienRepository.findAllByVille(search.getVille()));
					if (!search.getLangue().isEmpty()) {
						response.setElectriciens(getElectricienByLanguage(response.getElectriciens(), search.getLangue()));
						response.getElectriciens().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
					response.getElectriciens().forEach(med -> {
						med.setForum(null);
						med.setNotifications(null);
						med.setUsername(null);
						med.setJwt(null);
						med.setRendezVous(null);
						med.setIpUsers(null);
						med.setSchedule(null);
					});
					return ResponseEntity.ok().body(response);
				} else if (search.getVille() != null && search.getCodePostal() != null) {
					response.setElectriciens(electricienRepository.findAllByVilleAndPostalCode(search.getVille(), search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setElectriciens(getElectricienByLanguage(response.getElectriciens(), search.getLangue()));
						response.getElectriciens().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				} else if (search.getVille() == null && search.getCodePostal() != null) {
					response.setElectriciens(electricienRepository.findAllByPostalCode(search.getCodePostal()));

					if (!search.getLangue().isEmpty()) {
						response.setElectriciens(getElectricienByLanguage(response.getElectriciens(), search.getLangue()));
						response.getElectriciens().forEach(med -> {
							med.setForum(null);
							med.setNotifications(null);
							med.setUsername(null);
							med.setJwt(null);
							med.setRendezVous(null);
							med.setIpUsers(null);
							med.setSchedule(null);
						});
						return ResponseEntity.ok().body(response);
					}
				}
			}
			}
		if((search.getCodePostal() == null && search.getVille()==null && search.getSpeciality() == null && search.getCategory()==null ) || (search.getCodePostal()== null &&  search.getVille().isEmpty() && search.getSpeciality().isEmpty() && search.getCategory().isEmpty() )) {


			response.setMenuisiers(MenuisierRepository.findAll());
				response.setPlombiers(plombierRepository.findAll());
				response.setCarreleurs(carreleurRepository.findAll());
				response.setElectriciens(electricienRepository.findAll());
				response.setPaysagistes(paysagisteRepository.findAll());
				response.setSoudeurs(soudeurRepository.findAll());
				response.setPeintres(peintreRepository.findAll());
				response.setChauffagistes(chauffagisteRepository.findAll());
				response.setSerruriers(serrurierRepository.findAll());
		return ResponseEntity.ok().body(response);
			}
		//}
	/*	response.getMenuisiers().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getPaysagistes().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getPeintres().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getSoudeurs().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getElectriciens().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getCarreleurs().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
		response.getPlombiers().forEach(med->{

			med.setForum(null);
			med.setNotifications(null);
			med.setUsername(null);
			med.setJwt(null);
			med.setRendezVous(null);
			med.setIpUsers(null);
			med.setSchedule(null);
		});
*/
		return ResponseEntity.ok().body(response  );

	}
	public List<Menuisier> getMenuisierByLanguage (List<Menuisier> listePh, List<String> languageReq){
		List<Menuisier> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Serrurier> getSerrurierByLanguage (List<Serrurier> listePh, List<String> languageReq){
		List<Serrurier> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Chauffagiste> getChauffagisteByLanguage (List<Chauffagiste> listePh, List<String> languageReq){
		List<Chauffagiste> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}

	public List<Paysagiste> getPaysagisteByLanguage (List<Paysagiste> listePh, List<String> languageReq){
		List<Paysagiste> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Plombier> getPlombierByLanguage (List<Plombier> listePh, List<String> languageReq){
		List<Plombier> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Soudeur> getSoudeurByLanguage (List<Soudeur> listePh, List<String> languageReq){
		List<Soudeur> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Carreleur> getCarreleurByLanguage (List<Carreleur> listePh, List<String> languageReq){
		List<Carreleur> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Electricien> getElectricienByLanguage (List<Electricien> listePh, List<String> languageReq){
		List<Electricien> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}
	public List<Peintre> getPeintreByLanguage (List<Peintre> listePh, List<String> languageReq){
		List<Peintre> result = new ArrayList<>();
		listePh.forEach(ph -> {
			String lg = ph.getLanguage().toString();
			for (int i = 0 ; i < languageReq.size() ; i ++) {
				String test = languageReq.get(i);
				if (lg.matches(".*"+test+".*")) {
					if (!result.stream().anyMatch(rs -> rs.equals(ph))) {
						result.add(ph);
					}
				}
			}
		});
		return result ;
	}

}

class SortDiplomeByDate implements Comparator<Diplome> {

    // Used for sorting in Descending order of
    // roll number
    public int compare(Diplome a,Diplome b) {

		if (a.getId() < b.getId()) {
	        return -1;

	}
	return 0;
    }
}
class SortExperienceByDate implements Comparator<Experience> {

    // Used for sorting in Descending order of
    // roll number
    public int compare(Experience a,Experience b) {


		if (a.getId() < b.getId()) {
	        return -1;

	}
	return 0;
    }
}
class SortSessionByDate implements Comparator<Session> {

    // Used for sorting in Descending order of
    // roll number
    public int compare(Session a,Session b)
    {	try {
		Date d1 = DateAttribute.getDate(a.getStartDate());
		Date d2 = DateAttribute.getDate(b.getStartDate());
		if (d2.before(d1))
	        return -1;
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return 0;
    }
}
class SortPersonnesByDate implements Comparator<Personne> {

    // Used for sorting in Descending order of
    // roll number
    public int compare(Personne a,Personne b) {


		if (a.id < b.id) {
	        return -1;

	}
	return 0;
    }
}
class SortRdvByDate implements Comparator<PersonnelRdvs> {

    // Used for sorting in Descending order of
    // roll number
    public int compare(PersonnelRdvs a,PersonnelRdvs b) {


		if (a.getId() > b.getId()) {
	        return -1;

	}
	return 0;
    }
}
