package com.itmsd.medical.controllers;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.itmsd.medical.services.UserService;

import net.bytebuddy.utility.RandomString;

import com.itmsd.medical.payload.request.LoginRequest;
import com.itmsd.medical.payload.response.LoginResponse;
import com.itmsd.medical.security.JwtUtils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Transactional
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private AuthenticationManager authenticationManager;
	private UserService userService;	
	private UserController userController;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private MenuisierRepository MenuisierRepository;	
	private ClientRepository clientRepository;	
	private ElectricienRepository electricienRepository;	
	private PlombierRepository plombierRepository;
	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;	
	private PeintreRepository peintreRepository;	
	private SoudeurRepository soudeurRepository;	
	private AdminRepository adminRepository;
	private PlanRepository planRepository;
	private JwtUtils jwtUtils;	
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JavaMailSender mailSender ;

	private final TemplateEngine templateEngine ;

	@Autowired
	private Environment env;

	@Autowired
	public AuthController(AuthenticationManager authenticationManager, UserService userService,SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository, UserRepository userRepository,
						  RoleRepository roleRepository, MenuisierRepository MenuisierRepository, ClientRepository clientRepository,
						  ElectricienRepository electricienRepository, PlombierRepository plombierRepository,CarreleurRepository carreleurRepository,
						  PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
						  SoudeurRepository soudeurRepository, JwtUtils jwtUtils, BCryptPasswordEncoder bCryptPasswordEncoder,
						  UserController userController, AdminRepository adminRepository , JavaMailSender mailSender , TemplateEngine templateEngine, PlanRepository planRepository ) {
		super();
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.clientRepository = clientRepository;
		this.electricienRepository = electricienRepository;
		this.plombierRepository = plombierRepository;
		this.carreleurRepository=carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.planRepository= planRepository;
		this.jwtUtils = jwtUtils;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userController = userController;
		this.adminRepository = adminRepository;
        this.mailSender = mailSender ;
		this.templateEngine = templateEngine ;

    }
	@PostMapping("/signinusername")
	public ResponseEntity<?> authenticateUser2(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
			throws Exception {
		String username;
		User userByUsername = userService.findUserByUsername(loginRequest.getUsername().toLowerCase());

		if (userByUsername != null) {
			username = userByUsername.getUsername().toLowerCase();
		} else return ResponseEntity.badRequest().body("User doesn't exist ! Enter Valid username !");
		//throw new RuntimeException("User doesn't exist ! Enter Valid email or username");
		if (userByUsername.isBanned()) throw new RuntimeException("Sorry you are banned from our platform !");
		if (!userByUsername.isActive())
			//	throw new RuntimeException("Your account is not Activated ! check your mail to active your account.");
			throw new RuntimeException("Your account is not Activated yet ! you will receive an email as soon as your account is confirmed.");
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userByUsername.getPassword())) throw new RuntimeException("Incorrect password !");
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		//response.setHeader("Authorization", jwt);
		userByUsername.setJwt(jwt);
		userByUsername.setOnline("online");
		userRepository.save(userByUsername);
		if (userByUsername.getRoles().contains(roleRepository.findByRole("client"))) {
			LoginResponse res = new LoginResponse();
			Client client = clientRepository.findByUsername(username).get();
			res.setId(client.getId());
			res.setFirstName(client.getFirstName());
			res.setLastName(client.getLastName());
			res.setUsername(client.getUsername());
			res.setCreatedAt(client.getCreatedAt());
			res.setImageUrl(client.getPhotoUrl());
			res.setStatus(client.getOnline());
			res.setRoles(client.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("admin"))) {
			LoginResponse res = new LoginResponse();
			Admin admin = adminRepository.findByUsername(username).get();

			res.setId(admin.getId());
			res.setFirstName(admin.getFirstName());
			res.setLastName(admin.getLastName());
			res.setUsername(admin.getUsername());
			res.setCreatedAt(admin.getCreatedAt());
			res.setImageUrl(admin.getPhotoUrl());
			res.setStatus(admin.getOnline());
			res.setRoles(admin.getRoles());
			res.setJwt(jwt);

			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("menuisier"))) {
			LoginResponse res = new LoginResponse();
			Menuisier med = MenuisierRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("electricien"))) {
			LoginResponse res = new LoginResponse();
			Electricien med = electricienRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("plombier"))) {
			LoginResponse res = new LoginResponse();
			Plombier med = plombierRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("carreleur"))) {
			LoginResponse res = new LoginResponse();
			Carreleur med = carreleurRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("paysagiste"))) {
			LoginResponse res = new LoginResponse();
			Paysagiste med = paysagisteRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setName(med.getFirstName());
			res.setName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("peintre"))) {
			LoginResponse res = new LoginResponse();
			Peintre med = peintreRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else if (userByUsername.getRoles().contains(roleRepository.findByRole("soudeur"))) {
			LoginResponse res = new LoginResponse();
			Soudeur med = soudeurRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}
		else if (userByUsername.getRoles().contains(roleRepository.findByRole("serrurier"))) {
			LoginResponse res = new LoginResponse();
			Serrurier med = serrurierRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}
		else if (userByUsername.getRoles().contains(roleRepository.findByRole("chauffagiste"))) {
			LoginResponse res = new LoginResponse();
			Chauffagiste med = chauffagisteRepository.findByUsername(username).get();
			res.setId(med.getId());
			res.setFirstName(med.getFirstName());
			res.setLastName(med.getLastName());
			res.setUsername(med.getUsername());
			res.setSpeciality(med.getSpeciality());
			res.setCreatedAt(med.getCreatedAt());
			res.setImageUrl(med.getPhotoUrl());
			res.setStatus(med.getOnline());
			res.setRoles(med.getRoles());
			res.setJwt(jwt);
			return ResponseEntity.ok().body(res);
		}else return ResponseEntity.badRequest().body("Veuillez entrer des valides cordonnées !");

	}
	/*@PostMapping("/stripe/webhook")
	public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
		try {
			Event event = Webhook.constructEvent(payload, signature, "v1_secret_key");
			if ("payment_intent.succeeded".equals(event.getType())) {
				// Récupérer les données de l'événement
				StripeObject stripeObject = event.getDataObjectDeserializer().getObject().get();
				if (stripeObject instanceof PaymentIntent) {
					PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
					// Effectuer les actions nécessaires, par exemple mettre à jour l'état de la commande
					return ResponseEntity.ok().build();
				}
			}
			return ResponseEntity.ok().build();
		} catch (SignatureVerificationException e) {
			// Échec de la vérification de la signature
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			// Échec du traitement de l'événement
			return ResponseEntity.badRequest().build();
		}
	}*/
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response)
			throws Exception {
		if (loginRequest.getEmail()!= null) {
			String userEmail;
			User userByEmail = userService.findUserByEmail(loginRequest.getEmail().toLowerCase());

			if (userByEmail != null) {
				userEmail = userByEmail.getEmail().toLowerCase();
			} else return ResponseEntity.badRequest().body("User doesn't exist ! Enter Valid email !");
			//throw new RuntimeException("User doesn't exist ! Enter Valid email or username");
			if (userByEmail.isBanned()) throw new RuntimeException("Sorry you are banned from our platform !");
			if (!userByEmail.isActive())
				//	throw new RuntimeException("Your account is not Activated ! check your mail to active your account.");
				throw new RuntimeException("Your account is not Activated yet ! you will receive an email as soon as your account is confirmed.");
			if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userByEmail.getPassword()))
				throw new RuntimeException("Incorrect password !");
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(userEmail, loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			//response.setHeader("Authorization", jwt);
			userByEmail.setJwt(jwt);
			userByEmail.setOnline("online");
			userRepository.save(userByEmail);
			if (userByEmail.getRoles().contains(roleRepository.findByRole("client"))) {
				LoginResponse res = new LoginResponse();
				Client client = clientRepository.findByEmail(userEmail);
				res.setId(client.getId());
				res.setFirstName(client.getFirstName());
				res.setLastName(client.getLastName());
				res.setUsername(client.getUsername());
				res.setCreatedAt(client.getCreatedAt());
				res.setImageUrl(client.getPhotoUrl());
				res.setStatus(client.getOnline());
				res.setRoles(client.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("admin"))) {
				LoginResponse res = new LoginResponse();
				Admin admin = adminRepository.findByEmail(userEmail);

				res.setId(admin.getId());
				res.setFirstName(admin.getFirstName());
				res.setLastName(admin.getLastName());
				res.setUsername(admin.getUsername());
				res.setCreatedAt(admin.getCreatedAt());
				res.setImageUrl(admin.getPhotoUrl());
				res.setStatus(admin.getOnline());
				res.setRoles(admin.getRoles());
				res.setJwt(jwt);

				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("menuisier"))) {
				LoginResponse res = new LoginResponse();
				Menuisier med = MenuisierRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("electricien"))) {
				LoginResponse res = new LoginResponse();
				Electricien med = electricienRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("plombier"))) {
				LoginResponse res = new LoginResponse();
				Plombier med = plombierRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("carreleur"))) {
				LoginResponse res = new LoginResponse();
				Carreleur med = carreleurRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("paysagiste"))) {
				LoginResponse res = new LoginResponse();
				Paysagiste med = paysagisteRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setName(med.getFirstName());
				res.setName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("peintre"))) {
				LoginResponse res = new LoginResponse();
				Peintre med = peintreRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			} else if (userByEmail.getRoles().contains(roleRepository.findByRole("soudeur"))) {
				LoginResponse res = new LoginResponse();
				Soudeur med = soudeurRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByEmail.getRoles().contains(roleRepository.findByRole("serrurier"))) {
				LoginResponse res = new LoginResponse();
				Serrurier med = serrurierRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByEmail.getRoles().contains(roleRepository.findByRole("chauffagiste"))) {
				LoginResponse res = new LoginResponse();
				Chauffagiste med = chauffagisteRepository.findByEmail(userEmail);
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}
			else return ResponseEntity.badRequest().body("Veuillez entrer des valides cordonnées !");
		} else if (loginRequest.getUsername()!= null) {
			String username;
			User userByUsername = userService.findUserByUsername(loginRequest.getUsername().toLowerCase());

			if (userByUsername != null) {
				username = userByUsername.getUsername().toLowerCase();
			} else return ResponseEntity.badRequest().body("User doesn't exist ! Enter Valid username !");
			//throw new RuntimeException("User doesn't exist ! Enter Valid email or username");
			if (userByUsername.isBanned()) throw new RuntimeException("Sorry you are banned from our platform !");
			if (!userByUsername.isActive())
				//	throw new RuntimeException("Your account is not Activated ! check your mail to active your account.");
				throw new RuntimeException("Your account is not Activated yet ! you will receive an email as soon as your account is confirmed.");
			if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userByUsername.getPassword())) throw new RuntimeException("Incorrect password !");
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);
			//response.setHeader("Authorization", jwt);
			userByUsername.setJwt(jwt);
			userByUsername.setOnline("online");
			userRepository.save(userByUsername);
			if (userByUsername.getRoles().contains(roleRepository.findByRole("client"))) {
				LoginResponse res = new LoginResponse();
				Client client = clientRepository.findByUsername(username).get();
				res.setId(client.getId());
				res.setFirstName(client.getFirstName());
				res.setLastName(client.getLastName());
				res.setUsername(client.getUsername());
				res.setCreatedAt(client.getCreatedAt());
				res.setImageUrl(client.getPhotoUrl());
				res.setStatus(client.getOnline());
				res.setRoles(client.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("admin"))) {
				LoginResponse res = new LoginResponse();
				Admin admin = adminRepository.findByUsername(username).get();

				res.setId(admin.getId());
				res.setFirstName(admin.getFirstName());
				res.setLastName(admin.getLastName());
				res.setUsername(admin.getUsername());
				res.setCreatedAt(admin.getCreatedAt());
				res.setImageUrl(admin.getPhotoUrl());
				res.setStatus(admin.getOnline());
				res.setRoles(admin.getRoles());
				res.setJwt(jwt);

				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("menuisier"))) {
				LoginResponse res = new LoginResponse();
				Menuisier med = MenuisierRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("electricien"))) {
				LoginResponse res = new LoginResponse();
				Electricien med = electricienRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("plombier"))) {
				LoginResponse res = new LoginResponse();
				Plombier med = plombierRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("carreleur"))) {
				LoginResponse res = new LoginResponse();
				Carreleur med = carreleurRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("paysagiste"))) {
				LoginResponse res = new LoginResponse();
				Paysagiste med = paysagisteRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setName(med.getFirstName());
				res.setName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("peintre"))) {
				LoginResponse res = new LoginResponse();
				Peintre med = peintreRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("soudeur"))) {
				LoginResponse res = new LoginResponse();
				Soudeur med = soudeurRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}
			else if (userByUsername.getRoles().contains(roleRepository.findByRole("serrurier"))) {
				LoginResponse res = new LoginResponse();
				Serrurier med = serrurierRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}else if (userByUsername.getRoles().contains(roleRepository.findByRole("chauffagiste"))) {
				LoginResponse res = new LoginResponse();
				Chauffagiste med = chauffagisteRepository.findByUsername(username).get();
				res.setId(med.getId());
				res.setFirstName(med.getFirstName());
				res.setLastName(med.getLastName());
				res.setUsername(med.getUsername());
				res.setSpeciality(med.getSpeciality());
				res.setCreatedAt(med.getCreatedAt());
				res.setImageUrl(med.getPhotoUrl());
				res.setStatus(med.getOnline());
				res.setRoles(med.getRoles());
				res.setJwt(jwt);
				return ResponseEntity.ok().body(res);
			}
			else return ResponseEntity.badRequest().body("Veuillez entrer des valides cordonnées !");

		}
		else return ResponseEntity.badRequest().body("Veuillez entrer des valides cordonnées !");
	}

	@PostMapping("/signup")
	public Client createPatient(@Valid @RequestBody Client user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password doesn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"client"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return clientRepository.save(user);
		}

	}
	/*@PostMapping("/uplodeImage")
	public void UplodeImage (@Valid @RequestParam("Image") MultipartFile image, @RequestParam("userEmail") String userEmail)throws IOException, Exception {
		userService.UplodeImage(userEmail, image );}*/


	@PostMapping("/signupmenuisier")
	public Menuisier createMenuisier(@Valid @RequestBody Menuisier user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
         //  user.setPlan(planRepository.findById(user.getPlan().getId()).get());
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"menuisier"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
		return MenuisierRepository.save(user);
		}
	}
	@GetMapping("/notconfirmed")
	public ResponseEntity<?> getNotConfirmedAccount(HttpServletRequest request) {
		Collection<User> users= userRepository.findNotConfirmedUsers();
		return ResponseEntity.ok().body(users);
	}

	@GetMapping("/alltechniciens")
	public ResponseEntity<?> alltechniciens(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization").substring(7);
		Long userIdco = userController.userIdFromToken(jwt);
		User user = userRepository.findUserById(userIdco);
		//if (user) return ResponseEntity.ok().body(user);

		Collection<User> users= userRepository.findAllTechniciens();
		return ResponseEntity.ok().body(users);
	}
	@PostMapping("/signupsoudeur")
	public Soudeur createSoudeur(@Valid @RequestBody Soudeur user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"soudeur"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
		return soudeurRepository.save(user);
		}
	}
	@PostMapping("/signupelectricien")
	public Electricien createElectricien(@Valid @RequestBody Electricien user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"electricien"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return electricienRepository.save(user);
		}
	}
	@PostMapping("/signupplombier")
	public Plombier createPlombier(@Valid @RequestBody Plombier user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
		user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"plombier"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
		return plombierRepository.save(user);
		}
	}
	@PostMapping("/signupserrurier")
	public Serrurier createSerrurrier(@Valid @RequestBody Serrurier user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"serrurier"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return serrurierRepository.save(user);
		}
	}

	
		@PostMapping("/signuppaysagiste")
		public Paysagiste createPaysagiste(@Valid @RequestBody Paysagiste user) throws IOException, Exception {
			User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
			user.setUsername(RandomString.make(7)+"1");
			if (!user.getPasswordConfirmation().equals(user.getPassword()))
				throw new RuntimeException("Password dosn't match");
			while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
			{
			user.setUsername(RandomString.make(7)+"1");
			}
			if (userExists != null) {
				throw new RuntimeException("User exist !");
			} else {
				user.setEmail(user.getEmail().toLowerCase());
				user.setUsername(user.getUsername().toLowerCase());
				Optional<User> saved = Optional.of (userService.saveUser(user,"paysagiste"));
				saved.ifPresent ( u -> {
					try {
						ArrayList<String> languageList = user.getLanguage();
						String lng = languageList.get(0);
						sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
					}catch(Exception e) {
						e.printStackTrace();
					}
				});
			return paysagisteRepository.save(user);
			}
		}
	


	@PostMapping("/signuppeintre")
	@Transactional
	public Peintre createPaysagiste(@Valid @RequestBody Peintre user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"peintre"));
				saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return peintreRepository.save(user);
		}
	}
	@PostMapping("/signupcarreleur")
	@Transactional
	public Carreleur createcarreleur(@Valid @RequestBody Carreleur user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"carreleur"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return carreleurRepository.save(user);
		}
	}
	@PostMapping("/signupchauffagiste")
	@Transactional
	public Chauffagiste createchauffagiste(@Valid @RequestBody Chauffagiste user) throws IOException, Exception {
		User userExists = userService.findUserByEmail(user.getEmail().toLowerCase());
		user.setUsername(RandomString.make(7)+"1");
		if (!user.getPasswordConfirmation().equals(user.getPassword()))
			throw new RuntimeException("Password dosn't match");
		while (userService.findUserByUsername(user.getUsername().toLowerCase()) != null)
		{
			user.setUsername(RandomString.make(7)+"1");
		}
		if (userExists != null) {
			throw new RuntimeException("User exist !");
		} else {
			user.setEmail(user.getEmail().toLowerCase());
			user.setUsername(user.getUsername().toLowerCase());
			Optional<User> saved = Optional.of (userService.saveUser(user,"Chauffagiste"));
			saved.ifPresent ( u -> {
				try {
					ArrayList<String> languageList = user.getLanguage();
					String lng = languageList.get(0);
					sendHtmlMail(user,lng,user.getFirstName(),user.getLastName());
				}catch(Exception e) {
					e.printStackTrace();
				}
			});
			return chauffagisteRepository.save(user);
		}
	}

	@PutMapping("/logout")
	public String logOut(HttpServletRequest request) {
			String jwt = request.getHeader("Authorization").substring(7);
			Long userIdco = userController.userIdFromToken(jwt);
			User user = userRepository.findById(userIdco).get();
					user.setOnline("offline");
					userRepository.save(user);
		return ("User has been logged out !");
	}


	/*** Sending mail configuration ***/
	public static void sendmail(String to, String message, String subject) throws MessagingException {

		// Configuration du serveur SMTP
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// Création de la session de messagerie
		javax.mail.Session session = javax.mail.Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("babaybaha8@gmail.com", "pwyguuowoiuaicef");
			}
		});

		// Création du message à envoyer
		javax.mail.Message emailMessage = new MimeMessage(session);
		emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		emailMessage.setSubject(subject);
		emailMessage.setText(message);

		// Envoi du message
		Transport.send(emailMessage);
	}


	/*** Sending mail ***/

	public void confirmAccount(User user) throws MessagingException {
		String link = env.getProperty("verify.link");
		String token = user.getConfirmationToken();

	}
	@PostMapping("/sendConfirmationMail")
	public void sendConfirmationMail(@Valid @RequestBody User user) throws MessagingException {

		Context context = new Context();
		context.setVariable(  "title", "Your account is activated");
		context.setVariable(  "msg", "Hello ,"+user.getUsername());
		context.setVariable( "signature", "My Health Network");
		context.setVariable( "email","Email: " + user.getEmail());

		String body = templateEngine.process("confirmationMail",context) ;

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper (message, true);
		helper.setTo(user.getEmail());
		helper.setSubject ("Verify your email address");
		helper.setText (body,  true);
		mailSender.send(message);
	}
	public void sendHtmlMail(User user,String lng,String firstname ,String lastname) throws MessagingException {
		String link = env.getProperty("verify.link");

		String token = user.getConfirmationToken();
		Context context = new Context();
		System.out.println("hello"+lng);
		if (lng.equals("Français")) {
			context.setVariable(  "title", "Vérifiez votre adresse e-mail");
			context.setVariable(  "msg", "Salut ,"+firstname +" "+lastname);
			context.setVariable( "signature", "My Craft ");

			context.setVariable( "link", link+token);
			String body = templateEngine.process("VerifyEmailFr", context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(user.getEmail());
			helper.setSubject("Vérifiez votre adresse e-mail");
			helper.setText(body, true);
			mailSender.send(message);
		}else {
			context.setVariable(  "title", "Verify your email address");
			context.setVariable(  "msg", "Hello ,"+firstname +" "+lastname);
			context.setVariable( "signature", "My Craft ");

			context.setVariable( "link", link+token);

			String body = templateEngine.process("VerifyEmail", context);

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(user.getEmail());
			helper.setSubject("Verify your email address");
			helper.setText(body, true);
			mailSender.send(message);
		}
	}
}
