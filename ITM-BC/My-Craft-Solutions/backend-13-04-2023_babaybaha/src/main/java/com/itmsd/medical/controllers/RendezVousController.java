package com.itmsd.medical.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import com.itmsd.medical.services.RdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.services.DateAttribute;

@Transactional
@RestController
@RequestMapping("rendezvous")
public class RendezVousController {
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private UserRepository userRepository;
	private MenuisierRepository MenuisierRepository;	
	private ClientRepository clientRepository;	
	private ElectricienRepository electricienRepository;	
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;	
	private PeintreRepository peintreRepository;	
	private SoudeurRepository soudeurRepository;
	private RendezVousRepository rendezVousRepository;
	private ScheduleRepository scheduleRepository;
	private AuthController authController;
	private SessionRepository sessionRepository;
    private SimpMessagingTemplate template;
    private NotificationRepository notificationRepository;
    private UserController userController;
	@Autowired
	RdvService rendezVousService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	public RendezVousController(SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository,UserRepository userRepository,
								MenuisierRepository MenuisierRepository, ClientRepository clientRepository,
								ElectricienRepository electricienRepository, CarreleurRepository carreleurRepository ,PlombierRepository plombierRepository,
								PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
								SoudeurRepository soudeurRepository, RendezVousRepository rendezVousRepository,
								AuthController authController, SessionRepository sessionRepository,
								SimpMessagingTemplate template, NotificationRepository notificationRepository,
								UserController userController, ScheduleRepository scheduleRepository) {
		super();
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.clientRepository = clientRepository;
		this.electricienRepository = electricienRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.plombierRepository = plombierRepository;
		this.carreleurRepository=carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.rendezVousRepository = rendezVousRepository;
		this.authController = authController;
		this.sessionRepository = sessionRepository; 
		this.template = template; 
		this.notificationRepository = notificationRepository;
		this.userController = userController;
		this.scheduleRepository = scheduleRepository;
	}

	
	@PostMapping("add/{personnelId}")
	public ResponseEntity<?> addRendezVous(
			@PathVariable(value = "personnelId") Long personnelId,@RequestBody RendezVous rdv,HttpServletRequest request) throws Exception{
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);

		if (rdv.getStartDate()==null || rdv.getEndDate()==null) {
			throw new RuntimeException("Check the date is null !");
		}

		User personnel = userRepository.findUserById(personnelId);
		Client client = clientRepository.findById(userIdco).get();
		if (userIdco != client.getId() && userIdco != personnel.getId()) throw new RuntimeException("Security: Bad token !");
		if (personnel!=null&&client!=null) {
			//Electricien dent = electricienRepository.findById(personnelId).get();
			rdv.getUsers().add(personnel);
			rdv.getUsers().add(client);
			rdv.setClientId(client.getId());
			rdv.setPersonnelId(personnel.getId());
			String Datedata = DateAttribute.getTime(new Date());
			rdv.setCreatedAt(Datedata);
			rdv.setStatus("en cours");
			Long idRdv = rendezVousRepository.save(rdv).getId();
//			personnel.getSchedule().stream().findFirst().get().getSession().forEach(sess->{
//				if (sess.getStartDate().equals(rdv.getStartDate())) {
//					sess.setStatus("pending");
//					sess.setNbRdvs(sess.getNbRdvs()+1);
//					sessionRepository.save(sess);
//				}
//			});
//			String role = personnel.getRoles().stream().findFirst().get().getRole();
//			if (role.contains("menuisier") || role.contains("soudeur") || role.contains("peintre")
//					|| role.contains("electricien")) {
//				String message = "	Bonjour Docteur, 🩺 \n \n"
//						+ "Vous avez un nouveau rendez-vous de client inscrit sur notre plateforme ! \n"
//						+ "Rendez-vous le "+rdv.getDate()+" de "+rdv.getStartDate()+" à "+rdv.getEndDate()+" 🕑 \n \n"
//								+ "Pour plus de détails et pour confirmer le rendez-vous, vous pouvez consulter votre liste de réservation sur votre éspace. "
//						+   " \nBon travail, à bientôt 🙂 !";
//
//				authController.sendmail(personnel.getEmail(), message, "📅 Nouveau rendez-vous !");
//			}
			Notification notification = new Notification();
			notification.setIdRdv(idRdv);
			notification.setTitle("Nouveau rendez-vous !");
			notification.setContent("Vous venez de recevoir un rendez-vous de la part de "+client.getLastName()+" "+client.getFirstName()+".");
			notification.setCreatedAt(Datedata);
			notification.setIdClient(client.getId());
			notification.setPhotoUrl(client.getPhotoUrl());
			notification.setUser(personnel);
			notificationRepository.save(notification);
			template.convertAndSend("/topic/notifications/"+personnel.getUsername(), notification);
			return ResponseEntity.ok().body("Rendez-vous pris avec succès !");
		}

		return ResponseEntity.badRequest().body("Rendez-vous n'est pas pris !");
	}

	@PostMapping("update/{rdvId}/{status}")
	public ResponseEntity<?> updateStatusRdv(@PathVariable(value = "rdvId") Long rdvId, @PathVariable(value = "status") String status,HttpServletRequest request ){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);

		RendezVous rdv = rendezVousRepository.findById(rdvId).get();
		if (rdv.getPersonnelId()!=userIdco) throw new RuntimeException("You cannot update appointment you are not the owner !");
		User user = userRepository.findUserById(rdv.getPersonnelId());
		if (rdv != null && status.contains("confirmed"))  {
			user.getSchedule().getSession().forEach(sess -> {
				if (sess.getStartDate().equals(rdv.getStartDate())) {
					sess.setStatus("closed");
					sessionRepository.save(sess);
					rdv.setStatus(status);
					rendezVousRepository.save(rdv);
				}
			});
			Client client = clientRepository.findById(rdv.getClientId()).get();
			Notification notification = new Notification();
			String Datedata = DateAttribute.getTime(new Date());
			notification.setIdRdv(rdv.getId());
			notification.setTitle("Nouveau rendez-vous !");
			notification.setContent("Votre rendez-vous a été confirmé de la part de médecin !");
			notification.setCreatedAt(Datedata);
			notification.setIdClient(rdv.getClientId());
			notification.setPhotoUrl(client.getPhotoUrl());
			notification.setUser(userRepository.findUserById(rdv.getClientId()));
			notificationRepository.save(notification);
			template.convertAndSend("/topic/notifications/"+client.getUsername(), notification);
			return ResponseEntity.ok().body("Rendez-vous update succes !");

		}else if (rdv != null && status.contains("refused"))  {
			user.getSchedule().getSession().forEach(sess -> {
				if (sess.getStartDate().equals(rdv.getStartDate())) {
					sess.setNbRdvs(sess.getNbRdvs()-1);
					if (sess.getNbRdvs()<1) {
						sess.setStatus("open");
					}else sess.setStatus("en cours");
					sessionRepository.save(sess);
					rdv.setStatus(status);
					rendezVousRepository.save(rdv);
				}
			});
			Client client = clientRepository.findById(rdv.getClientId()).get();
			Notification notification = new Notification();
			String Datedata = DateAttribute.getTime(new Date());
			notification.setIdRdv(rdv.getId());
			notification.setTitle("Nouveau rendez-vous !");
			notification.setContent("Désolé votre rendez-vous a été réfusé de la part de médecin !");
			notification.setCreatedAt(Datedata);
			notification.setIdClient(rdv.getClientId());
			notification.setPhotoUrl(client.getPhotoUrl());
			notification.setUser(userRepository.findUserById(rdv.getClientId()));
			notificationRepository.save(notification);
			template.convertAndSend("/topic/notifications/"+client.getUsername(), notification);
			return ResponseEntity.ok().body("Rendez-vous update succes !");
		}
		return ResponseEntity.badRequest().body("Failed to update !");
	}
//	@GetMapping("single/{rdvId}")
//	public ResponseEntity<?> getRdv(@PathVariable(value = "rdvId") Long rdvId,HttpServletRequest request ){
//			String jwt = request.getHeader("Authorization").substring(7);
//			Long userIdco = userController.userIdFromToken(jwt);
//			RendezVous rdv = rendezVousRepository.findById(rdvId).get();
//			if (rdv.getPersonnelId() == userIdco || userIdco == rdv.getClientId()) {
//				PersonnelRdvs perso = new PersonnelRdvs();
//				perso.setId(rdv.getId());
//				perso.setStartDate(rdv.getStartDate());
//				perso.setEndDate(rdv.getEndDate());
//				perso.setDate(rdv.getDate());
//				perso.setCreatedAt(rdv.getCreatedAt());
//				perso.setSubject(rdv.getSubject());
//				perso.setMessage(rdv.getText());
//				perso.setStatus(rdv.getStatus());
//				User us = userRepository.findById(rdv.getPersonnelId()).get();
//				if (us.getRoles().stream().findFirst().get().getRole().contains("menuisier")) {
//					menuisier pt = MenuisierRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}else if (us.getRoles().stream().findFirst().get().getRole().contains("soudeur")) {
//					Soudeur pt = soudeurRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}else if (us.getRoles().stream().findFirst().get().getRole().contains("plombier")) {
//					Plombier pt = plombierRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}else if (us.getRoles().stream().findFirst().get().getRole().contains("peintre")) {
//					Peintre pt = peintreRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}else if (us.getRoles().stream().findFirst().get().getRole().contains("electricien")) {
//					Electricien pt = electricienRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}else if (us.getRoles().stream().findFirst().get().getRole().contains("paysagiste")) {
//					Paysagiste pt = paysagisteRepository.findById(rdv.getPersonnelId()).get();
//					Personne p = new Personne(pt.getId(),pt.getName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setDoctor(p);
//				}
//
//					Client pt = clientRepository.findById(rdv.getClientId()).get();
//					Personne p = new Personne(pt.getId(),pt.getFirstName(),pt.getLastName(),
//							pt.getEmail(),pt.getUsername(),pt.getVille(),pt.getAddress(),
//							pt.getPostalCode(),pt.getPhoneNumber());
//					perso.setClient(p);
//
//					return ResponseEntity.ok().body(perso);
//			}
//
//			return ResponseEntity.badRequest().body("Bad token !");
//
//	}

//	@GetMapping("all")
//	public ResponseEntity<?> getAllRendezVouss(HttpServletRequest request ){
//		String jwt = request.getHeader("Authorization").substring(7);
//		Long userIdco = userController.userIdFromToken(jwt);
//		return ResponseEntity.ok().body(rendezVousService.getAllRendezVouss(userIdco));
//	}

	@GetMapping("AllRdvPerso")
	public ResponseEntity<?> getAllRendezVousperso(HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		return ResponseEntity.ok().body(rendezVousService.getAllRendezVousperso(userIdco));
	}

	@GetMapping("AllRdvClient")
	public ResponseEntity<?> getAllRendezVousClient(HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		return ResponseEntity.ok().body(rendezVousService.getAllRendezVousClient(userIdco));
	}

	@GetMapping("single/{rdvId}")
	public ResponseEntity<?> getRdv(@PathVariable(value = "rdvId") Long rdvId,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		RendezVous rdv = rendezVousRepository.findById(rdvId).get();
	return ResponseEntity.ok().body(rdv);
	}


	@GetMapping("schedule/{personnelId}")
	public ResponseEntity<?> getHisSchedule(@PathVariable(value = "personnelId") Long personnelId){
		User personnel = userRepository.findUserById(personnelId);
		String role = personnel.getRoles().stream().findFirst().get().getRole();
		if (role.contains("chauffagiste") ||role.contains("serrurier") ||role.contains("menuisier") || role.contains("soudeur") || role.contains("peintre") || role.contains("electricien") || role.contains("plombier") || role.contains("paysagiste") || role.contains("carreleur")||role.contains("client")) {

			return ResponseEntity.ok().body(rendezVousService.getHisSchedule(personnelId));
		}else {
			return ResponseEntity.badRequest().body("ID ne correspond à aucun personnel");
		}

	}

	@PostMapping("AccepterRefuser/{rdvId}/{status}")
	public ResponseEntity<?> AccepterRefuser(@PathVariable(value = "rdvId") Long rdvId, @PathVariable(value = "status") String status,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User connected = userRepository.findUserById(userIdco);
		RendezVous rdv = rendezVousRepository.findById(rdvId).get();

		User patient = userRepository.findUserById(rdv.getClientId());
		Session session = new Session();
		if(rdv.getStatus()!= "accepté" && rdv.getStatus()!= "refusé") {
			if (status.contains("accepter")) {
				rdv.setStatus("accepté");
				rendezVousRepository.save(rdv);
				session.setStatus("programmé");
				session.setNbRdvs(1);
				session.setTitle(rdv.getSubject());
				session.setStartDate(rdv.getStartDate());
				session.setEndDate(rdv.getEndDate());
				sessionRepository.save(session);
				//Schedule schedules = connected.getSchedule();
				String Datedata = DateAttribute.getTime(new Date());

				//Schedule schedulesPat = patient.getSchedule();
				Notification notification = new Notification();
				notification.setIdRdv(rdvId);
				notification.setTitle("Nouveau rendez-vous !");
				notification.setContent("Votre rendez-vous a été confirmé de la part de médecin !");
				notification.setCreatedAt(Datedata);
				notification.setIdClient(patient.getId());
				notification.setPhotoUrl(patient.getPhotoUrl());
				notification.setUser(patient);
				notificationRepository.save(notification);
				//template.convertAndSend("/topic/notifications/"+personnel.getId(), notification);
				messagingTemplate.convertAndSend("/mychannel/" + patient.getUsername(), notification);
				if (patient.getSchedule()==null){
					Schedule newSchedulePat = new Schedule();
					List sessionsPat=new ArrayList();
					sessionsPat.add(session);
					newSchedulePat.setSession(sessionsPat);
					patient.setSchedule(scheduleRepository.save(newSchedulePat));
				}else{
					Schedule schedulesPat = patient.getSchedule();
					schedulesPat.getSession().add(session);
					//scheduleRepository.save(schedules);
					scheduleRepository.save(schedulesPat);
				}
				if (connected.getSchedule() == null) {
					Schedule newSchedule = new Schedule();
					List sessions=new ArrayList();
					sessions.add(session);
					//session.getSchedule().add(newSchedule);
					//newSchedule.setMedecin(connected);
					newSchedule.setSession(sessions);
					connected.setSchedule(scheduleRepository.save(newSchedule));
				} else {
					Schedule schedules = connected.getSchedule();
					schedules.getSession().add(session);
					//session.getSchedule().add(schedules);

					scheduleRepository.save(schedules);
				}
				userRepository.save(connected);

				return ResponseEntity.ok().body("Rendez-vous accepté !");

			} else if (status.contains("refuser")) {
				rdv.setStatus("refusé");
				String Datedata = DateAttribute.getTime(new Date());
				Notification notification = new Notification();
				notification.setIdRdv(rdvId);
				notification.setTitle("Nouveau rendez-vous !");
				notification.setContent("Votre rendez-vous a été refusé de la part de médecin !");
				notification.setCreatedAt(Datedata);
				notification.setIdClient(patient.getId());
				notification.setPhotoUrl(patient.getPhotoUrl());
				notification.setUser(patient);
				notificationRepository.save(notification);
				//template.convertAndSend("/topic/notifications/"+personnel.getId(), notification);
				messagingTemplate.convertAndSend("/mychannel/" + patient.getUsername(), notification);
				rendezVousRepository.save(rdv);
				return ResponseEntity.ok().body("Rendez-vous refusé !");
			}
		}

		return ResponseEntity.badRequest().body("Échec de mise à jour !");
	}
	@PostMapping("termineAnnuler/{sessId}/{action}")
	public ResponseEntity<?> AcceptRefuse(@PathVariable(value = "sessId") Long sessId, @PathVariable(value = "action") String action,HttpServletRequest request ) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User connected = userRepository.findUserById(userIdco);
		Session session = sessionRepository.findById(sessId).get();
		if(session.getStatus()!= "terminée" && session.getStatus()!= "annulée") {
			if (action.contains("terminer")) {
				session.setStatus("terminée");
				sessionRepository.save(session);

				return ResponseEntity.ok().body("Session terminée !");

			} else if (action.contains("annuler")) {
				session.setStatus("annulée");
				sessionRepository.save(session);

				return ResponseEntity.ok().body("Session annulée !");
			}
		}

		return ResponseEntity.badRequest().body("Échec de mise à jour !");

	}
}
