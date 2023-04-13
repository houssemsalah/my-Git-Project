package com.itmsd.medical.admin;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import com.itmsd.medical.entities.*;
import com.itmsd.medical.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.itmsd.medical.services.DateAttribute;

@Component
public class AdminData implements CommandLineRunner{
	private ForumRepository forumRepository;
	private RoleRepository roleRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private AdminRepository adminRepository;
	private PlombierRepository plombierRepository;
	private ChauffagisteRepository chauffagisteRepository;
	private SerrurierRepository serrurierRepository;
	private MenuisierRepository MenuisierRepository;
	private ClientRepository clientRepository;
	private ElectricienRepository electricienRepository;

	private CarreleurRepository carreleurRepository;
	private PaysagisteRepository paysagisteRepository;
	private PeintreRepository peintreRepository;
	private SoudeurRepository soudeurRepository;


	@Autowired
	public AdminData(ForumRepository forumRepository,SerrurierRepository serrurierRepository ,ChauffagisteRepository chauffagisteRepository, UserRepository userRepository,
					 MenuisierRepository MenuisierRepository, ClientRepository clientRepository,
					 ElectricienRepository electricienRepository,CarreleurRepository carreleurRepository,
					 PaysagisteRepository paysagisteRepository, PeintreRepository peintreRepository,
					 SoudeurRepository soudeurRepository,RoleRepository roleRepository,BCryptPasswordEncoder bCryptPasswordEncoder,
			AdminRepository adminRepository,PlombierRepository plombierRepository) {
		super();
		this.forumRepository =forumRepository;
		this.serrurierRepository=serrurierRepository;
		this.chauffagisteRepository=chauffagisteRepository;
		this.MenuisierRepository = MenuisierRepository;
		this.clientRepository = clientRepository;
		this.electricienRepository = electricienRepository;

		this.carreleurRepository=carreleurRepository;
		this.paysagisteRepository = paysagisteRepository;
		this.peintreRepository = peintreRepository;
		this.soudeurRepository = soudeurRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.adminRepository = adminRepository;
		this.plombierRepository = plombierRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Role userRole = roleRepository.findByRole("admin");
		if (userRole == null) {
			Role newRole = new Role ("admin") ;
			newRole = roleRepository.save(newRole);		
			
			Admin admin = new Admin();
			admin.setEmail("myhealthnetwork.service@gmail.com");
			admin.setPassword(bCryptPasswordEncoder.encode("123456789"));
			admin.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			admin.setUsername("superAdmin1");
			admin.setActive(true);
			admin.setCreatedAt(DateAttribute.getTime(new Date()));
			admin.setOnline("offline");
			admin.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			admin.setFirstName("Admin");
			admin.setLastName("Super");
			admin.setEmail("myhealthnetwork.service@gmail.com");
			Long phoneNumber = (long) 00000000;
			admin.setPhoneNumber(phoneNumber);

			adminRepository.save(admin);
		}



		Role user2Role = roleRepository.findByRole("plombier");
		if (user2Role == null) {
			Role newRole = new Role ("plombier") ;
			newRole = roleRepository.save(newRole);

			Plombier plombier = new Plombier();
			plombier.setEmail("plombier@gmail.com");
			plombier.setPassword(bCryptPasswordEncoder.encode("123456789"));
			plombier.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			plombier.setUsername("plombier");
			plombier.setActive(true);
			plombier.setCreatedAt(DateAttribute.getTime(new Date()));
			plombier.setOnline("offline");
			plombier.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			plombier.setFirstName("Plombier");
			plombier.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			plombier.setPhoneNumber(phoneNumber);
            plombier.getLanguage().add("Français");


			plombierRepository.save(plombier);
			Forum forum = new Forum("plombiers");
			forum.getUsers().add(plombier);
			forumRepository.save(forum);
		}

		Role user3Role = roleRepository.findByRole("chauffagiste");
		if (user3Role == null) {
			Role newRole = new Role ("chauffagiste") ;
			newRole = roleRepository.save(newRole);

			Chauffagiste chauffagiste = new Chauffagiste();
			chauffagiste.setEmail("chauffagiste@gmail.com");
			chauffagiste.setPassword(bCryptPasswordEncoder.encode("123456789"));
			chauffagiste.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			chauffagiste.setUsername("chauffagiste");
			chauffagiste.setActive(true);
			chauffagiste.setCreatedAt(DateAttribute.getTime(new Date()));
			chauffagiste.setOnline("offline");
			chauffagiste.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			chauffagiste.setFirstName("chauffagiste");
			chauffagiste.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			chauffagiste.setPhoneNumber(phoneNumber);
			chauffagiste.getLanguage().add("Français");
			chauffagisteRepository.save(chauffagiste);
			Forum forum = new Forum("chauffagistes");
			forum.getUsers().add(chauffagiste);
			forumRepository.save(forum);
		}
		Role user4Role = roleRepository.findByRole("serrurier");
		if (user4Role == null) {
			Role newRole = new Role ("serrurier") ;
			newRole = roleRepository.save(newRole);

			Serrurier serrurier = new Serrurier();
			serrurier.setEmail("serrurier@gmail.com");
			serrurier.setPassword(bCryptPasswordEncoder.encode("123456789"));
			serrurier.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			serrurier.setUsername("serrurier");
			serrurier.setActive(true);
			serrurier.setCreatedAt(DateAttribute.getTime(new Date()));
			serrurier.setOnline("offline");
			serrurier.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			serrurier.setFirstName("serrurier");
			serrurier.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			serrurier.setPhoneNumber(phoneNumber);
			serrurier.getLanguage().add("Français");
			serrurierRepository.save(serrurier);
			Forum forum = new Forum("serruriers");
			forum.getUsers().add(serrurier);
			forumRepository.save(forum);
		}
		Role user6Role = roleRepository.findByRole("peintre");
		if (user6Role == null) {
			Role newRole = new Role ("peintre") ;
			newRole = roleRepository.save(newRole);

			Peintre peintre = new Peintre();
			peintre.setEmail("peintre@gmail.com");
			peintre.setPassword(bCryptPasswordEncoder.encode("123456789"));
			peintre.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			peintre.setUsername("peintre");
			peintre.setActive(true);
			peintre.setCreatedAt(DateAttribute.getTime(new Date()));
			peintre.setOnline("offline");
			peintre.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			peintre.setFirstName("peintre");
			peintre.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			peintre.setPhoneNumber(phoneNumber);
			peintre.getLanguage().add("Français");
			peintreRepository.save(peintre);
			Forum forum = new Forum("peintres");
			forum.getUsers().add(peintre);
			forumRepository.save(forum);
		}
		Role user7Role = roleRepository.findByRole("electricien");
		if (user7Role == null) {
			Role newRole = new Role ("electricien") ;
			newRole = roleRepository.save(newRole);

			Electricien electricien = new Electricien();
			electricien.setEmail("electricien@gmail.com");
			electricien.setPassword(bCryptPasswordEncoder.encode("123456789"));
			electricien.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			electricien.setUsername("electricien");
			electricien.setActive(true);
			electricien.setCreatedAt(DateAttribute.getTime(new Date()));
			electricien.setOnline("offline");
			electricien.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			electricien.setFirstName("electricien");
			electricien.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			electricien.setPhoneNumber(phoneNumber);
			electricien.getLanguage().add("Français");
			electricienRepository.save(electricien);
			Forum forum = new Forum("electriciens");
			forum.getUsers().add(electricien);
			forumRepository.save(forum);
		}
		Role user8Role = roleRepository.findByRole("paysagiste");
		if (user8Role == null) {
			Role newRole = new Role ("paysagiste") ;
			newRole = roleRepository.save(newRole);

			Paysagiste paysagiste = new Paysagiste();
			paysagiste.setEmail("paysagiste@gmail.com");
			paysagiste.setPassword(bCryptPasswordEncoder.encode("123456789"));
			paysagiste.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			paysagiste.setUsername("paysagiste");
			paysagiste.setActive(true);
			paysagiste.setCreatedAt(DateAttribute.getTime(new Date()));
			paysagiste.setOnline("offline");
			paysagiste.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			paysagiste.setFirstName("paysagiste");
			paysagiste.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			paysagiste.setPhoneNumber(phoneNumber);
			paysagiste.getLanguage().add("Français");
			paysagisteRepository.save(paysagiste);
			Forum forum = new Forum("paysagistes");
			forum.getUsers().add(paysagiste);
			forumRepository.save(forum);
		}
		Role user9Role = roleRepository.findByRole("menuisier");
		if (user9Role == null) {
			Role newRole = new Role ("menuisier") ;
			newRole = roleRepository.save(newRole);

			Menuisier menuisier = new Menuisier();
			menuisier.setEmail("menuisier@gmail.com");
			menuisier.setPassword(bCryptPasswordEncoder.encode("123456789"));
			menuisier.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			menuisier.setUsername("menuisier");
			menuisier.setActive(true);
			menuisier.setCreatedAt(DateAttribute.getTime(new Date()));
			menuisier.setOnline("offline");
			menuisier.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			menuisier.setFirstName("menuisier");
			menuisier.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			menuisier.setPhoneNumber(phoneNumber);
			menuisier.getLanguage().add("Français");
			MenuisierRepository.save(menuisier);
			Forum forum = new Forum("menuisiers");
			forum.getUsers().add(menuisier);
			forumRepository.save(forum);
		}
		Role user10Role = roleRepository.findByRole("soudeur");
		if (user10Role == null) {
			Role newRole = new Role ("soudeur") ;
			newRole = roleRepository.save(newRole);

			Soudeur soudeur = new Soudeur();
			soudeur.setEmail("soudeur@gmail.com");
			soudeur.setPassword(bCryptPasswordEncoder.encode("123456789"));
			soudeur.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			soudeur.setUsername("soudeur");
			soudeur.setActive(true);
			soudeur.setCreatedAt(DateAttribute.getTime(new Date()));
			soudeur.setOnline("offline");
			soudeur.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			soudeur.setFirstName("Soudeur");
			soudeur.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			soudeur.setPhoneNumber(phoneNumber);
			soudeur.getLanguage().add("Français");
			soudeurRepository.save(soudeur);
			Forum forum = new Forum("soudeurs");
			forum.getUsers().add(soudeur);
			forumRepository.save(forum);
		}
		Role user11Role = roleRepository.findByRole("carreleur");
		if (user11Role == null) {
			Role newRole = new Role ("carreleur") ;
			newRole = roleRepository.save(newRole);

			Carreleur carreleur = new Carreleur();
			carreleur.setEmail("carreleur@gmail.com");
			carreleur.setPassword(bCryptPasswordEncoder.encode("123456789"));
			carreleur.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			carreleur.setUsername("carreleur");
			carreleur.setActive(true);
			carreleur.setCreatedAt(DateAttribute.getTime(new Date()));
			carreleur.setOnline("offline");
			carreleur.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			carreleur.setFirstName("Carreleur");
			carreleur.setLastName("Technicien");
			Long phoneNumber = (long) 11111111;
			carreleur.setPhoneNumber(phoneNumber);
			carreleur.getLanguage().add("Français");
			carreleurRepository.save(carreleur);
			Forum forum = new Forum("carreleurs");
			forum.getUsers().add(carreleur);
			forumRepository.save(forum);
		}

		Role user12Role = roleRepository.findByRole("client");
		if (user12Role == null) {
			Role newRole = new Role ("client") ;
			newRole = roleRepository.save(newRole);

			Client client = new Client();
			client.setEmail("client@gmail.com");
			client.setPassword(bCryptPasswordEncoder.encode("123456789"));
			client.setPasswordConfirmation(bCryptPasswordEncoder.encode("123456789"));
			client.setUsername("client");
			client.setActive(true);
			client.setCreatedAt(DateAttribute.getTime(new Date()));
			client.setOnline("offline");
			client.setRoles(new HashSet<Role>(Arrays.asList(newRole)));
			client.setFirstName("Client");
			client.setLastName("Client");
			Long phoneNumber = (long) 11111111;
			client.setPhoneNumber(phoneNumber);
			client.getLanguage().add("Français");
			clientRepository.save(client);
		}

	}
}
