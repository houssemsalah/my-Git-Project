package com.itmsd.medical.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmsd.medical.entities.Soudeur;
import com.itmsd.medical.repositories.PlombierRepository;
import com.itmsd.medical.services.DateAttribute;

@Transactional
@RestController
@RequestMapping("schedule")
public class ScheduleController {
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private MenuisierRepository MenuisierRepository;
	private ElectricienRepository electricienRepository;
	private PlombierRepository plombierRepository;

	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;
	private PeintreRepository peintreRepository;
	private SoudeurRepository soudeurRepository;
	private ScheduleRepository scheduleRepository;
	private SessionRepository sessionRepository;
	private UserController userController;

	@Autowired
	public ScheduleController(SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository,UserRepository userRepository, RoleRepository roleRepository,
							  MenuisierRepository MenuisierRepository,
							  ElectricienRepository electricienRepository, PlombierRepository plombierRepository,CarreleurRepository carreleurRepository,
							  PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
							  SoudeurRepository soudeurRepository, ScheduleRepository scheduleRepository,
							  SessionRepository sessionRepository, UserController userController) {
		super();
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.electricienRepository = electricienRepository;
		this.plombierRepository = plombierRepository;
		this.carreleurRepository=carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.scheduleRepository = scheduleRepository;
		this.sessionRepository = sessionRepository;
		this.userController = userController;
	}

	@PostMapping("add/{userId}")
	public ResponseEntity<?> addSchedule(@PathVariable(value = "userId") Long userId, @RequestBody Session session,
										 HttpServletRequest request){
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		Session sessionFinal = new Session();
		sessionFinal.setTitle(session.getTitle());
		sessionFinal.setStartDate(session.getStartDate());
		sessionFinal.setEndDate(session.getEndDate());
		sessionFinal.setStatus("open");
		sessionFinal.setNbRdvs(1);
		sessionRepository.save(sessionFinal);
		if (userIdco != userId) throw new RuntimeException("Security: bad token !");
		if (user.getRoles().contains(roleRepository.findByRole("client"))) {
			return ResponseEntity.badRequest().body("Client cannot add schedule !");

		}else if (user.getRoles().contains(roleRepository.findByRole("electricien"))) {
			Electricien dent = electricienRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("menuisier"))) {
			Menuisier dent = MenuisierRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("chauffagiste"))) {
			Chauffagiste dent = chauffagisteRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("plombier"))) {
			Plombier dent = plombierRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));


		}else if (user.getRoles().contains(roleRepository.findByRole("serrurier"))) {
			Serrurier dent = serrurierRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("carreleur"))) {
			Carreleur dent = carreleurRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("soudeur"))) {
			Soudeur dent = soudeurRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));
		}else if (user.getRoles().contains(roleRepository.findByRole("paysagiste"))) {
			Paysagiste dent = paysagisteRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}else if (user.getRoles().contains(roleRepository.findByRole("peintre"))) {
			Peintre dent = peintreRepository.findById(userId).get();
			if (dent.getSchedule()==null) {
				Schedule newSchedule = new Schedule();
				List sessions=new ArrayList();
				sessions.add(sessionFinal);
				newSchedule.setSession(sessions);
				dent.setSchedule(scheduleRepository.save(newSchedule));
			}else {
				dent.getSchedule().getSession().forEach(sess ->{
					try {
						if (DateAttribute.getDate(sess.getStartDate()).equals(DateAttribute.getDate(session.getStartDate()))
								|| DateAttribute.getDate(session.getStartDate()).before(DateAttribute.getDate(sess.getEndDate()))
								|| DateAttribute.getDate(session.getEndDate()).before(DateAttribute.getDate(session.getStartDate()))) {
							throw new RuntimeException("You must verify your start time session ! ");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

				dent.getSchedule().getSession().add(sessionFinal);
				scheduleRepository.save(dent.getSchedule());
			}
			userRepository.save(dent);
			return ResponseEntity.ok().body(sessionRepository.save(sessionFinal));

		}

		return ResponseEntity.badRequest().body("Bad user id !");
	}




}
